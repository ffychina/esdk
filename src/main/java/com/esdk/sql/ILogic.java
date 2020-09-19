package com.esdk.sql;

public interface ILogic extends IPrepareStatementSQL,IStatementSQL{
  boolean isAnd();
  ILogic setOr();
}
