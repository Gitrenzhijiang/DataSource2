package com.ren.ds.pool.proxy;

import java.sql.ResultSetMetaData;


public interface ResultSetMetaDataProxy extends ResultSetMetaData, WrapperProxy{
    
    ResultSetMetaData getResultSetMetaDataRaw();

    ResultSetProxy getResultSetProxy();
}
