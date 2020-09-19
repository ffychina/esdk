package com.esdk.sql;

import com.esdk.utils.EasyObj;

public class OrderBy{
  Field field;
  boolean isDescent=false;
  
  public OrderBy(String prefix,String columnname){
    field=new Field(prefix,columnname);
  }

  public OrderBy(Field field0){
    field=field0;
  }

  public OrderBy(Field field0,boolean isdescent){
    field=field0;
    isDescent=isdescent;
  }

  public boolean isDescent(){
    return this.isDescent;
  }

  public void setDescent(boolean isdescent){
    this.isDescent=isdescent;
  }
  
  public Field getField() {
  	return this.field;
  }
  
  public String toString() {
    return isDescent?field.toString().concat(" desc"):field.toString();
  }
  
  @Override public boolean equals(Object obj){
  	return EasyObj.equalsByJson(this,obj);
  }
  
  @Override
  public int hashCode(){
  	return EasyObj.hashCode(this);
  }
}
