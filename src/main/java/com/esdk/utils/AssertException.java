package com.esdk.utils;

class AssertException extends java.lang.Exception{

  private static final long serialVersionUID=-2882494483529852436L;

  public AssertException(){
    super();
  }

  public AssertException(String s){
    super(s);
  }

  public String toString() {
    return this.getMessage();
  }
}
