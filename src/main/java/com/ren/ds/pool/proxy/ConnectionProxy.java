package com.ren.ds.pool.proxy;

import java.sql.Connection;
import java.util.Date;
import java.util.Properties;


public interface ConnectionProxy extends Connection, WrapperProxy {

    Connection getRawObject();

    Properties getProperties();

    DataSourceProxy getDirectDataSource();

    Date getConnectedTime();

    TransactionInfo getTransactionInfo();
    
    int getCloseCount();
}
