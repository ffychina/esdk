package com.esdk.sql;

public interface IPrepareStatementSQL{
  String getPstmtSql();
  Object[] getParameters();
}
