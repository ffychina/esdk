package com.esdk.sql;

public class CaseColumn extends Column{
  private String content;
  private String aliasName;

  public CaseColumn(String content,String aliasName){
    this.content=content;
    this.aliasName=aliasName;
  }

  public String toString() {
    return content.concat(" as ").concat(aliasName);
  }
  
}
