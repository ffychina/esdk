package com.esdk.sql.datasource;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.esdk.interfaces.IRequestClose;

public interface IConnectionPool extends IRequestClose{
  Connection getConnection();
  DataSource getDataSource();
}
