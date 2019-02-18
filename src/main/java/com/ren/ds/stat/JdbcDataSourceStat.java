package com.ren.ds.stat;

import java.sql.SQLException;
import java.util.Date;

import com.ren.ds.pool.DataSource;

public class JdbcDataSourceStat implements JdbcDataSourceStatMBean{
    
    private DataSource dataSource;
    private JdbcConnectionStatMBean connMB;
    public JdbcDataSourceStat(DataSource dataSource) {
        this.dataSource = dataSource;
        try {
            connMB = dataSource.unwrap(JdbcConnectionStatMBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String getDbName() {
        
        return dataSource.getName();
    }

    public JdbcConnectionStatMBean getConnMB() {
        return connMB;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("DBName:" + getDbName() + ", ");
        sb.append("池内连接数:" + connMB.poolingCount());
        sb.append(", 当前使用中连接:" + connMB.activeCount()
         + ", 活跃连接峰值数:" + connMB.activePeak() + ", 活跃连接峰值发生时间:" + new Date(connMB.activePeakTime()));
        sb.append(", 当前等待连接的线程:" + connMB.notEmptyWaitThreadCount() + ", 等待连接峰值数:" + connMB.notEmptyWaitThreadPeak());
        return sb.toString();
    }
    
}
