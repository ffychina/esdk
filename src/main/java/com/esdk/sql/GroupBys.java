package com.esdk.sql;

import java.util.LinkedHashMap;

import com.esdk.esdk;

public class GroupBys implements IAssemble{
  LinkedHashMap groupbySet=new LinkedHashMap();
  private String having;

  public void add(Field field) {
    GroupBy groupby=new GroupBy(field);
    add(groupby);
  }

  public void add(GroupBy[] groupby) {
    for(int i=0;i<groupby.length;i++){
      add(groupby[i]);
    }
  }
  
  public void add(GroupBy groupby) {
    groupbySet.put(groupby.toString(),groupby);
  }
  
  public int size() {
    return groupbySet.size();
  }
  
  public void clear() {
    groupbySet.clear();
    having=null;
  }
  
  public GroupBy[] toArray(){
    return (GroupBy[])groupbySet.values().toArray(new GroupBy[0]);
  }

  public void setHaving(String having) {
  	this.having="HAVING "+having;
  }
  
  public String assemble(){
    StringBuffer result=new StringBuffer();
    result.append(groupbySet.size()>0?"\r\nGroup By ":"");
    GroupBy[] array=toArray();
    for(int i=0;i<array.length;i++){
      result.append(array[i].toString());
      result.append(i<array.length-1?",":"");

    }
    if(esdk.str.isValid(having)) {
    	result.append(" ").append(having);
    }
    return result.toString();
  }
  
  public GroupBys clone() {
  	GroupBys result=new GroupBys();
  	result.groupbySet=this.groupbySet;
  	result.having=this.having;
  	return result;
  }
}
