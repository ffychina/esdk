package com.esdk.sql;

public class ISNULLColumn extends Column{
  private Field field;
  private Object replaceObj;
  private String labelName;

  public ISNULLColumn(Field f,Object replaceValue){
    field=f;
    replaceObj=replaceValue;
    labelName=f.getName();
  }

  public ISNULLColumn(Field f,Object replaceValue,String aliasName){
    field=f;
    replaceObj=replaceValue;
    labelName=aliasName;
  }
  
  public String toString() {
    StringBuffer result=new StringBuffer();
    result.append("ISNULL(").append(field).append(",").append(replaceObj).append(") AS ").append(labelName);
    return result.toString();
  }
  
}
