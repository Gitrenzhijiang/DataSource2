package com.ren.ds.pool.proxy;


import java.sql.Statement;
import java.util.List;



public interface StatementProxy extends Statement, WrapperProxy {

    ConnectionProxy getConnectionProxy();

    Statement getRawObject();
    
    String lastExecuteSql();
}
