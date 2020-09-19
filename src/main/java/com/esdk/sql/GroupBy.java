package com.esdk.sql;

public class GroupBy{
  private Field field;
  
  public GroupBy(Field field0){
    field=field0;
  }
  
  public GroupBy(String prefix,String columnname){
    field=new Field(prefix,columnname);
  }
  
  public String toString() {
    return field.toString();
  }
}
