package com.ren.ds.pool.proxy;

import java.sql.Driver;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import com.ren.ds.filter.Filter;
import com.ren.ds.stat.JdbcDataSourceStat;


public interface DataSourceProxy extends DataSource {
    JdbcDataSourceStat getDataSourceStat();

    String getName();

    String getDbType();

    Driver getRawDriver();

    String getUrl();

    String getRawJdbcUrl();

    List<Filter> getProxyFilters();

    long createConnectionId();

    long createStatementId();

    long createResultSetId();

    long createMetaDataId();

    long createTransactionId();

    Properties getConnectProperties();
}
