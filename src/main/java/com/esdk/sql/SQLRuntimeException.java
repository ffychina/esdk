package com.esdk.sql;

public class SQLRuntimeException extends RuntimeException{
  private static final long serialVersionUID=1L;
  public SQLRuntimeException(String value) {
    super(value);
  }
  public SQLRuntimeException(Throwable value) {
    super(value);
  }
}
