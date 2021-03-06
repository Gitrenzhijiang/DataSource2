package com.ren.ds.pool.proxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;

import com.ren.ds.filter.FilterChainImpl;


public class StatementProxyImpl extends WrapperProxyImpl implements StatementProxy {

    private final ConnectionProxy  connection;
    private final Statement        statement;
    private String           lastExecuteSql ;

    private FilterChainImpl        filterChain = null;

    public StatementProxyImpl(ConnectionProxy connection, Statement statement, long id){
        super(statement, id);
        this.connection = connection;
        this.statement = statement;
    }

    public ConnectionProxy getConnectionProxy() {
        return connection;
    }

    public Statement getRawObject() {
        return this.statement;
    }

    public FilterChainImpl createChain() {
        FilterChainImpl chain = this.filterChain;
        if (chain == null) {
            chain = new FilterChainImpl(this.getConnectionProxy().getDirectDataSource());
        } else {
            this.filterChain = null;
        }

        return chain;
    }

    public void recycleFilterChain(FilterChainImpl chain) {
        chain.reset();
        this.filterChain = chain;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        createChain().statement_addBatch(this, sql);
    }

    @Override
    public void cancel() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_cancel(this);
        recycleFilterChain(chain);
    }

    @Override
    public void clearBatch() throws SQLException {

        FilterChainImpl chain = createChain();
        chain.statement_clearBatch(this);
        recycleFilterChain(chain);
    }

    @Override
    public void clearWarnings() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_clearWarnings(this);
        recycleFilterChain(chain);
    }

    @Override
    public void close() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_close(this);
        recycleFilterChain(chain);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        
        FilterChainImpl chain = createChain();
        boolean ret = chain.statement_execute(this, sql);
        this.lastExecuteSql = sql;
        recycleFilterChain(chain);
        return ret;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {

        FilterChainImpl chain = createChain();
        boolean ret = chain.statement_execute(this, sql, autoGeneratedKeys);
        this.lastExecuteSql = sql;
        recycleFilterChain(chain);
        return ret;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {

        FilterChainImpl chain = createChain();
        boolean ret = chain.statement_execute(this, sql, columnIndexes);
        this.lastExecuteSql = sql;
        recycleFilterChain(chain);
        return ret;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {

        FilterChainImpl chain = createChain();
        boolean ret = chain.statement_execute(this, sql, columnNames);
        this.lastExecuteSql = sql;
        recycleFilterChain(chain);
        return ret;
    }

    @Override
    public int[] executeBatch() throws SQLException {

        FilterChainImpl chain = createChain();
        int[] updateCounts = chain.statement_executeBatch(this);
        recycleFilterChain(chain);

        return updateCounts;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {

        FilterChainImpl chain = createChain();
        ResultSet resultSet = chain.statement_executeQuery(this, sql);
        this.lastExecuteSql = sql;
        recycleFilterChain(chain);
        return resultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {

        FilterChainImpl chain = createChain();
        int updateCount = chain.statement_executeUpdate(this, sql);
        this.lastExecuteSql = sql;
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {

        FilterChainImpl chain = createChain();
        int updateCount = chain.statement_executeUpdate(this, sql, autoGeneratedKeys);
        this.lastExecuteSql = sql;
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {

        FilterChainImpl chain = createChain();
        int updateCount = chain.statement_executeUpdate(this, sql, columnIndexes);
        this.lastExecuteSql = sql;
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {

        FilterChainImpl chain = createChain();
        int updateCount = chain.statement_executeUpdate(this, sql, columnNames);
        this.lastExecuteSql = sql;
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public Connection getConnection() throws SQLException {
        FilterChainImpl chain = createChain();
        Connection conn = chain.statement_getConnection(this);
        recycleFilterChain(chain);
        return conn;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getFetchDirection(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getFetchSize() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getFetchSize(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        FilterChainImpl chain = createChain();
        ResultSet value = chain.statement_getGeneratedKeys(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getMaxFieldSize(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getMaxRows() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getMaxRows(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.statement_getMoreResults(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.statement_getMoreResults(this, current);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getQueryTimeout(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        FilterChainImpl chain = createChain();
        ResultSet value = chain.statement_getResultSet(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getResultSetConcurrency(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getResultSetHoldability(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getResultSetType() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getResultSetType(this);
        recycleFilterChain(chain);
        return value;
    }

    // bug fixed for oracle
    @Override
    public int getUpdateCount() throws SQLException {
        FilterChainImpl chain = createChain();
        int updateCount = chain.statement_getUpdateCount(this);
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        FilterChainImpl chain = createChain();
        SQLWarning value = chain.statement_getWarnings(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isClosed() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.statement_isClosed(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.statement_isPoolable(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setCursorName(this, name);
        recycleFilterChain(chain);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setEscapeProcessing(this, enable);
        recycleFilterChain(chain);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setFetchDirection(this, direction);
        recycleFilterChain(chain);
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setFetchSize(this, rows);
        recycleFilterChain(chain);
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setMaxFieldSize(this, max);
        recycleFilterChain(chain);
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setMaxRows(this, max);
        recycleFilterChain(chain);
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setPoolable(this, poolable);
        recycleFilterChain(chain);
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setQueryTimeout(this, seconds);
        recycleFilterChain(chain);
    }



    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == StatementProxy.class) {
            return (T) this;
        }

        return super.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == StatementProxy.class) {
            return true;
        }

        return super.isWrapperFor(iface);
    }

    public void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public String lastExecuteSql() {
        return this.lastExecuteSql;
    }

}
