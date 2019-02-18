package com.ren.ds.pool.proxy;

import java.sql.ResultSet;


public interface ResultSetProxy extends ResultSet, WrapperProxy{

    ResultSet getResultSetRaw();

    StatementProxy getStatementProxy();
    
     String getSql();
     /*
    JdbcSqlStat getSqlStat();

    int getCursorIndex();

    int getFetchRowCount();

    long getConstructNano();

    void setConstructNano(long constructNano);

    void setConstructNano();

    int getCloseCount();

    void addReadStringLength(int length);

    long getReadStringLength();

    void addReadBytesLength(int length);

    long getReadBytesLength();

    void incrementOpenInputStreamCount();

    int getOpenInputStreamCount();

    void incrementOpenReaderCount();

    int getOpenReaderCount();

    int getPhysicalColumn(int logicColumn);

    int getLogicColumn(int physicalColumn);

    List<Integer> getHiddenColumns();

    int getHiddenColumnCount();

    void setLogicColumnMap(Map<Integer, Integer> logicColumnMap);

    void setPhysicalColumnMap(Map<Integer, Integer> physicalColumnMap);

    void setHiddenColumns(List<Integer> hiddenColumns); 
    
     */
}
