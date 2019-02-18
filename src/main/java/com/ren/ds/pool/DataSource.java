package com.ren.ds.pool;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.ren.ds.config.FilterConfig;
import com.ren.ds.filter.Filter;
import com.ren.ds.filter.FilterChainImpl;
import com.ren.ds.filter.FilterManager;
import com.ren.ds.stat.JdbcConnectionStatMBean;
import com.ren.ds.stat.JdbcDataSourceStat;
import com.ren.ds.utils.Util;


public class DataSource extends AbstractDataSource implements JdbcConnectionStatMBean{
    private static final Logger logger = Logger.getLogger(DataSource.class);
    ////////////////state
    private volatile boolean inited = false;
    
    private CountDownLatch latch = new CountDownLatch(2);
    ////////////////store
    private PooledConnection[] connectionHolder;
    /**
     * 当前池内的连接数量
     */
    private int poolingCount;
    /**
     * 当前等待连接的线程数量
     */
    private long notEmptyWaitThreadCount = 0;
    /**
     * 最大等待的线程峰值
     */
    private long notEmptyWaitThreadPeak = 0;
    /**
     * 当前使用中的
     */
    private long activeCount = 0;
    /**
     * 使用峰值
     */
    private long activePeak = 0;
    /**
     * 时间
     */
    private long activePeakTime = 0;
    /**
     * 一个连接最少生存时间(毫秒)
     */
    private long minEvictableIdleTimeMillis = 20;
    /**
     * 回收一次连接的周期
     */
    private long timeBetweenEvictionRunsMillis = 1000;
    
    public DataSource() {
        this.connProperties = FilterConfig.initConfig();
        loadConfigFormProperties(connProperties);
    }
    private void loadConfigFormProperties(Properties properties) {
        /**
         * initialSize
         * maxActive
         * minIdle
         * maxIdle
         * maxWait
         */
        Optional<Integer> o1 = Optional.ofNullable(Util.getInteger(properties, "initialSize"));
        this.initialSize = o1.orElse(DEFAULT_INITIAL_SIZE);
        this.maxActive = Optional.ofNullable(Util.getInteger(properties, "maxActive")).orElse(DEFAULT_MAX_ACTIVE_SIZE);
        this.minIdle = Optional.ofNullable(Util.getInteger(properties, "minIdle")).orElse(DEFAULT_MIN_IDLE);
        this.maxIdle = Optional.ofNullable(Util.getInteger(properties, "maxIdle")).orElse(DEFAULT_MAX_IDLE);
        this.maxWait = Optional.ofNullable(Util.getLong(properties, "maxWait")).orElse(DEFAULT_MAX_WAIT);
        /**
         * jdbcUrl=
           username=
           password=
           driverClass= 
         */
        this.jdbcUrl = properties.getProperty("jdbcUrl");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        this.driverClass = properties.getProperty("driverClass");
    }
    /**
     * 初始化
     */
    public void init() {
        if (inited) {
            return;
        }
        try {
            lock.lockInterruptibly();
            if (inited) {
                return;
            }
            Class clazz =  Class.forName(driverClass);
            // 加载driver
            this.driver = (Driver) clazz.newInstance();
            // 加载配置的filter
            this.filters = FilterManager.getFilters();
            
            // 初始化
            for (int i = 0;i < filters.size();i++) {
                filters.get(i).init(this);
            }
            // 初始化conn
            connectionHolder = new PooledConnection[maxActive];
            
            for (poolingCount = 0;poolingCount < initialSize;poolingCount++) {
                connectionHolder[poolingCount] = getPhysicalConnect();
            }
            // 启动两个线程
            new CreateConnectRunner().start();
            new DestoryConnectRunner().start();
            latch.await();
            inited = true;
            logger.info("filters:" + filters);
            logger.info("DataSource inited");
        } catch (Exception e) {
            // ignore
        } finally {
            lock.unlock();
        }
    }
    
    
    // 物理的拿到
    private PooledConnection getPhysicalConnect() throws SQLException {
        return new PooledConnection(this, createRawConnection());
    }
    // 丢弃
    private void discard(PooledConnection conn) {
        if (conn != null ) {
            try {
                conn.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 从池内拿到连接;如果maxWait<0,无限等待直到拿到连接
     * @return
     */
    public PooledConnection getConnectionDirect(long maxWait) {
        lock.lock();
        try {
            PooledConnection conn = null;
            if (maxWait < 0) {
                conn = take();
            }else {
                final long nanos = TimeUnit.MILLISECONDS.toNanos(maxWait);
                conn = poll(nanos);
            }
            if (conn != null) {
                activeCount++;
                if (activeCount > activePeak) {
                    activePeak = activeCount;
                    activePeakTime = System.currentTimeMillis();
                }
            } else {
                throw new RuntimeException("maxWait time may be is too small");
            }
            return conn;
        } finally {
            lock.unlock();
        }
    }
    /**
     * 从池中拿连接，最多等待nanos
     * @param nanos
     * @return
     */
    private PooledConnection poll(long nanos) {
        long estimate = nanos;
        for (;;) {
            if (poolingCount == 0) {
                empty.signal();
                // 等待
                notEmptyWaitThreadCount++;
                // 更新峰值
                if (notEmptyWaitThreadCount > notEmptyWaitThreadPeak) {
                    notEmptyWaitThreadPeak = notEmptyWaitThreadCount;
                }
                try {
                    estimate = notEmpty.awaitNanos(estimate);
                } catch (Exception e) {
                    // ignore
                } finally {
                    notEmptyWaitThreadCount--;
                }
                // 等待estimate时间后，再次检测
                if (poolingCount == 0) {
                    if (estimate <= 0) {
                        // 时间消耗完
                        return null;
                    }
                }
            }
            PooledConnection conn = connectionHolder[--poolingCount];
            connectionHolder[poolingCount] = null;
            return conn;
        }
    }

// 取出
    private PooledConnection take() {
        while (poolingCount == 0) {
            empty.signal();
            notEmptyWaitThreadCount++;
            // 更新峰值
            if (notEmptyWaitThreadCount > notEmptyWaitThreadPeak) {
                notEmptyWaitThreadPeak = notEmptyWaitThreadCount;
            }
            try {
                notEmpty.await();
            } catch (InterruptedException e) {
                // ignore
            } finally {
                notEmptyWaitThreadCount--;
            }
        }
        PooledConnection conn = connectionHolder[--poolingCount];
        connectionHolder[poolingCount] = null;
        return conn;
    }
    
    
    //// 
    private class CreateConnectRunner extends Thread {
        public CreateConnectRunner() {
            this.setDaemon(true);
            this.setName("CreateConnectRunner");
        }
        @Override
        public void run() {
            latch.countDown();
            for (;;) {
                lock.lock();
                try {
                    // 当有线程在等待时才可能创建连接
                    if (notEmptyWaitThreadCount <= 0) {
                        empty.await();
                    }
                    // 不能超过最大活跃连接的数量
                    if (poolingCount + activeCount >= maxActive) {
                        empty.await();
                        continue;
                    }
                    
                    
                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    lock.unlock();
                }
                // 需要创建一个连接
                try {
                    PooledConnection conn = getPhysicalConnect();
                    // 放入连接池
                    put(conn);
                } catch (SQLException e) {
                    logger.error("创建数据库连接时超时!", e);
                }
                
                
            }
            
        }

        
    }
    // 销毁过多的空闲连接
    private class DestoryConnectRunner extends Thread {
        public DestoryConnectRunner() {
            this.setDaemon(true);
            this.setName("DestoryConnectThread");
        }
        @Override
        public void run() {
            latch.countDown();
            List<PooledConnection> evitList = new ArrayList<>();
            for (;;) {
                try {
                    Thread.sleep(timeBetweenEvictionRunsMillis);
                    lock.lock();
                    // 是否有必要缩减
                    if (poolingCount <= maxIdle) {
                        notEmpty.await();
                        continue;
                    }
                    // 批量的抛弃这些连接
                    int checkCount = poolingCount - minIdle;
                    int destoryCount = 0;
                    for (int i = 0;i < checkCount;i++) {
                        PooledConnection conn = connectionHolder[i];
                        if (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-conn.getConnectedTimeNano())
                                <= minEvictableIdleTimeMillis) {
                            continue;
                        }
                        destoryCount++;
                    }
                    // 从后往前抛弃
                    for (int i = 0;i < destoryCount;i++) {
                        PooledConnection conn = connectionHolder[--poolingCount];
                        evitList.add(conn);
                    }
                } catch (Exception e) {
                    logger.error("缩减连接时出错");
                } finally {
                    lock.unlock();
                }
                // 释放连接
                while (!evitList.isEmpty()) {
                    discard(evitList.remove(0));
                }
            }
        }
        
    }
    
    // 放入,并非回收连接
    private void put(PooledConnection conn) {
        lock.lock();
        try {
            while (poolingCount == maxActive) {
                empty.await();
            }
            connectionHolder[poolingCount++] = conn;
            notEmpty.signal();
        } catch (Exception e) {
            logger.error("回收连接时出错");
        } finally {
            lock.unlock();
        }
    }
    /**
     * 回收
     * @param conn
     */
    public void recycle(PooledConnection conn) {
        lock.lock();
        conn.reset();
        try {
            while (poolingCount == maxActive) {
                empty.await();
            }
            connectionHolder[poolingCount++] = conn;
            activeCount--;
            notEmpty.signal();
        } catch (Exception e) {
            logger.error("回收连接时出错");
        } finally {
            lock.unlock();
        }
    }
    
    //////接口实现/////////////
    @Override
    public PooledConnection getConnection(String username, String password) throws SQLException {
        init();
        if (getProxyFilters().size() > 0) {
            FilterChainImpl filterChain = new FilterChainImpl(this);
            return filterChain.dataSource_connect(this, maxWait);
        } else {
            return getConnectionDirect(maxWait);
        }
    }
    // // // // databaseSourceProxy
    @Override
    public JdbcDataSourceStat getDataSourceStat() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return this.driverClass;
    }

    @Override
    public String getDbType() {
        return null;
    }

    @Override
    public Driver getRawDriver() {
        return driver;
    }

    @Override
    public String getUrl() {
        return jdbcUrl;
    }

    @Override
    public String getRawJdbcUrl() {
        return this.jdbcUrl;
    }

    @Override
    public List<Filter> getProxyFilters() {
        return super.filters;
    }

    private AtomicLong ids = new AtomicLong(0);
    private AtomicLong sids = new AtomicLong(0);
    private AtomicLong rsids = new AtomicLong(0);
    private AtomicLong mdids = new AtomicLong(0);
    private AtomicLong tids = new AtomicLong(0);
    @Override
    public long createConnectionId() {
        return ids.getAndIncrement();
    }

    @Override
    public long createStatementId() {
        return sids.getAndIncrement();
    }

    @Override
    public long createResultSetId() {
        return rsids.getAndIncrement();
    }

    @Override
    public long createMetaDataId() {
        return mdids.getAndIncrement();
    }

    @Override
    public long createTransactionId() {
        return tids.getAndIncrement();
    }

    @Override
    public Properties getConnectProperties() {
        return connProperties;
    }
    
    /////////////////////////// for conn
    @Override
    public long poolingCount() {
        return this.poolingCount;
    }
    @Override
    public long notEmptyWaitThreadCount() {
        return this.notEmptyWaitThreadCount;
    }
    @Override
    public long notEmptyWaitThreadPeak() {
        return this.notEmptyWaitThreadPeak;
    }
    @Override
    public long activeCount() {
        return this.activeCount;
    }
    @Override
    public long activePeak() {
        return this.activePeak;
    }
    @Override
    public long activePeakTime() {
        return activePeakTime;
    }

   

    
}
