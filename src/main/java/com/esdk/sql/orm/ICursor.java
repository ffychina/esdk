package com.esdk.sql.orm;


public interface ICursor extends IResultSetCursor{
  boolean next();
  boolean previous();
  boolean first();
  boolean last();
  boolean isFirst();
  boolean isLast();
  boolean absolute(int cursor);
  boolean relative(int offset);
  void beforeFirst();
  void afterLast();
  boolean isBeforeFirst();
  boolean isAfterLast();
  IRow getCurrentRow();
}
