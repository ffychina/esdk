package com.esdk.sql.orm;

import java.sql.SQLException;

public interface IResultSetCursor {
  int getCursor() ;
  boolean isEmpty() throws SQLException;
  boolean next() throws SQLException;
  boolean previous() throws SQLException;
  boolean first() throws SQLException;
  boolean last() throws SQLException;
  boolean isFirst() throws SQLException;
  boolean isLast() throws SQLException;
  boolean absolute(int row) throws SQLException;
  boolean relative(int offset) throws SQLException;
  void beforeFirst() throws SQLException;
  void afterLast() throws SQLException;
  boolean isBeforeFirst() throws SQLException;
  boolean isAfterLast() throws SQLException;
  int size();
  IRow getCurrentRow() throws SQLException;
}
