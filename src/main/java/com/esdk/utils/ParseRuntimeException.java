package com.esdk.utils;

public class ParseRuntimeException extends RuntimeException{
  public ParseRuntimeException(String s){
    super(s);
  }
  public ParseRuntimeException(Throwable value) {
    super(value);
  }
}
