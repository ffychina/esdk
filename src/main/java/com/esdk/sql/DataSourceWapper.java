package com.esdk.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceWapper implements DataSource {
    private DataSource _ds;

    public DataSourceWapper(DataSource datasource) {
        _ds = datasource;
    }

    public int getNumActive() {
        if (_ds instanceof DruidDataSource)
            return ((DruidDataSource) _ds).getActiveCount();
        return -1;
    }

    public int getNumIdle() {
        if (_ds instanceof DruidDataSource)
            return ((DruidDataSource) _ds).getActiveCount();
        return -1;
    }

    public int getMaxActive() {
        if (_ds instanceof DruidDataSource)
            return ((DruidDataSource) _ds).getMaxActive();
        return -1;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return _ds.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        _ds.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        _ds.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return _ds.getLoginTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return _ds.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return _ds.isWrapperFor(iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return _ds.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return _ds.getConnection(username, password);
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
