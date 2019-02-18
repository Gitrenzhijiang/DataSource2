package com.ren.ds.filter.stat;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.ren.ds.filter.FilterAdapter;
import com.ren.ds.filter.FilterChain;
import com.ren.ds.pool.DataSource;
import com.ren.ds.pool.PooledConnection;
import com.ren.ds.pool.proxy.DataSourceProxy;
import com.ren.ds.pool.proxy.WrapperProxy;
import com.ren.ds.stat.JdbcDataSourceStat;
import com.ren.ds.stat.JdbcDataSourceStatMBean;

public class StatFilter extends FilterAdapter {
    private static final Logger logger = Logger.getLogger(StatFilter.class);
    private volatile JdbcDataSourceStatMBean jdbcDataSourceStatMBean;
    
    @Override
    public void init(DataSourceProxy dataSource) {
        super.init(dataSource);
        try {
            jdbcDataSourceStatMBean = new JdbcDataSourceStat(dataSource.unwrap(DataSource.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    @Override
    public void dataSource_releaseConnection(FilterChain chain, PooledConnection connection) throws SQLException {
        super.dataSource_releaseConnection(chain, connection);
        logger.info("回收连接:" + connection);
        logger.info(jdbcDataSourceStatMBean.toString());
    }



    @Override
    public PooledConnection dataSource_getConnection(FilterChain chain, DataSource dataSource, long maxWaitMillis)
            throws SQLException {
        PooledConnection pc = super.dataSource_getConnection(chain, dataSource, maxWaitMillis);
        logger.info("获取连接:" + pc);
        logger.info(jdbcDataSourceStatMBean.toString());
        return pc;
    }



    public JdbcDataSourceStatMBean getJdbcDataSourceStatMBean() {
        return jdbcDataSourceStatMBean;
    }

    public void setJdbcDataSourceStatMBean(JdbcDataSourceStatMBean jdbcDataSourceStatMBean) {
        this.jdbcDataSourceStatMBean = jdbcDataSourceStatMBean;
    }
    
}
