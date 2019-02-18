package com.ren.ds.stat;

public interface JdbcConnectionStatMBean {
    // 池外的连接个数
    long poolingCount();
    long notEmptyWaitThreadCount();
    long notEmptyWaitThreadPeak();
    long activeCount();
    long activePeakTime();
    long activePeak();
}
