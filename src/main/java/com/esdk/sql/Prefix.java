package com.esdk.sql;

public class Prefix{
  private String prefix="";

  public Prefix() {}

  public Prefix(String prefixname) {
    setPrefix(prefixname);
  }
  
  public String getPrefix(){
    return this.prefix;
  }

  public void setPrefix(String arg){
    if(arg!=null&&arg.trim().length()>0)
    this.prefix=arg.concat(".");
  }
}
