package com.ren.ds.pool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ren.ds.filter.Filter;
import com.ren.ds.filter.FilterChainImpl;
import com.ren.ds.pool.proxy.DataSourceProxy;

public abstract class AbstractDataSource extends WrapAdapter implements DataSource, DataSourceProxy{

    private static final Logger logger = Logger.getLogger(AbstractDataSource.class);
    /**
     * 默认初始化连接大小
     */
    public final static int DEFAULT_INITIAL_SIZE = 0;
    /**
     * 默认最大活跃连接大小
     */
    public final static int DEFAULT_MAX_ACTIVE_SIZE = 8;
    /**
     * 默认最大空闲连接大小
     */
    public final static int DEFAULT_MAX_IDLE = 6;
    /**
     * 默认最小空闲连接大小
     */
    public final static int DEFAULT_MIN_IDLE = 3;
    /**
     * 默认最大等待时间(毫秒)
     */
    public final static long DEFAULT_MAX_WAIT = -1;
    
    /**
     * 默认回收超过运行时长连接
     */
//    public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 60 * 1000L;
    /**
     * 默认连接超时
     */
//    public static final long DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS = 30 * 1000;

    //////////////////////////////////////////////////////////////////////////////
    protected volatile String username;
    protected volatile String password;
    protected volatile String jdbcUrl;
    protected volatile String driverClass;
    
    protected List<Filter> filters;
    protected Driver driver;
    protected Properties connProperties;
    
    /**
     * 初始化连接大小
     */
    protected volatile int initialSize = DEFAULT_INITIAL_SIZE;
    /**
     * 最大活跃连接
     */
    protected volatile int maxActive = DEFAULT_MAX_ACTIVE_SIZE;
    /**
     * 最小空闲连接
     */
    protected volatile int minIdle = DEFAULT_MIN_IDLE;
    /**
     * 最大空闲连接
     */
    protected volatile int maxIdle = DEFAULT_MAX_IDLE;
    /**
     * 最大等待时间
     */
    protected volatile long maxWait = DEFAULT_MAX_WAIT;
    /**
     * 输出流  java.sql.DataSource 
     */
    protected volatile PrintWriter printWriter = new PrintWriter(System.out);
    /**
     * 默认使用不公平锁
     */
    protected final Lock lock = new ReentrantLock(false);
    protected final Condition notEmpty = lock.newCondition();
    protected final Condition empty = lock.newCondition();
    
    
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        
        return printWriter;
    }
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        printWriter = out;
    }
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }
    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }
    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        
        throw new SQLFeatureNotSupportedException();
    }
    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(username, password);
    }
    /**
     * 创建真正的连接
     * @return
     */
    protected Connection createRawConnection() {
        // dont 
        Connection conn = null;
        Properties pyproperties = new Properties();
        pyproperties.putAll(this.getConnectProperties());
        if (this.username != null && username.length() > 0)
            pyproperties.setProperty("user", this.username);
        if (this.password != null)
            pyproperties.setProperty("password", this.password);
        try {
            conn = createPhysicalConnection(jdbcUrl, pyproperties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    private Connection createPhysicalConnection(String url, Properties info) throws SQLException {
        Connection conn;
        if (getProxyFilters().size() == 0) {
            conn = this.getRawDriver().connect(url, info);
        } else {
            conn = new FilterChainImpl(this).connection_connect(info);
        }
        return conn;
    }
    
}
