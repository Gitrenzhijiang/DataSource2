package com.ren.ds.pool.proxy;

import java.sql.CallableStatement;


public interface CallableStatementProxy extends CallableStatement, PreparedStatementProxy, WrapperProxy{

    CallableStatement getRawObject();
}
