package com.ren.ds.pool.proxy;

import java.sql.PreparedStatement;
import java.util.Map;


public interface PreparedStatementProxy extends PreparedStatement, StatementProxy , WrapperProxy{

    PreparedStatement getRawObject();
    
    // Map<Integer, JdbcParameter> getParameters();
    
}
