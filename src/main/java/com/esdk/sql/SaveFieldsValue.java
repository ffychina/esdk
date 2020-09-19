package com.esdk.sql;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

abstract class SaveFieldsValue implements IPrepareStatementSQL,IAssemble,IStatementSQL{
  LinkedHashMap linkedMap=new LinkedHashMap();
  public void clear() {
    linkedMap.clear();
  }
  public String[] getFieldNames(){
  	return (String[])linkedMap.keySet().toArray(new String[0]);
  }
  public int size() {
    return linkedMap.size();
  }

  public void addFieldValue(String fieldname,String value){
    linkedMap.put(fieldname,value);
  }

  public void addFieldValue(String fieldname,Number value){
    linkedMap.put(fieldname,value);
  }
  
  public void addFieldValue(String fieldname,boolean value){
    linkedMap.put(fieldname,value?Boolean.TRUE:Boolean.FALSE);
  } 
  
  public void addFieldValue(String fieldname,Date value){
  	if(value!=null&&value.getClass().equals(Date.class))
  		value=new Timestamp(((Date)value).getTime());
    linkedMap.put(fieldname,value);
  } 
  
  public void addFieldValue(String fieldname,Boolean value){
    linkedMap.put(fieldname,value);
  }
  
  public void addFieldValue(String fieldname,Object value){
    if(value!=null){
      if(value.getClass().equals(Boolean.class))
        addFieldValue(fieldname,(Boolean)value);
      else if(value.getClass().equals(Time.class))
        addFieldValue(fieldname,(Time)value);
      else if(value instanceof java.util.Date)
        addFieldValue(fieldname,(Date)SQLAssistant.getPrepredSatementValue(value));
      else if(value instanceof Number)
        addFieldValue(fieldname,(Number)value);
      else if(value instanceof byte[])
        linkedMap.put(fieldname,(byte[])value);
      else if(value instanceof Function)
        linkedMap.put(fieldname,value);
      else
        addFieldValue(fieldname,value.toString());
    }
    else
      linkedMap.put(fieldname,null);
  }
  
  public void addNumericValue(String fieldname,String value){
    linkedMap.put(fieldname,value);
  }
  
  public void addColumnValue(String fieldname,String column){
    addFieldValue(fieldname,column);
  }
  
  public Object[] getParameters(){
  	Collection result=new ArrayList();
  	for(Iterator iter=linkedMap.values().iterator();iter.hasNext();){
  		Object obj=iter.next();
  		if(obj!=null&&obj instanceof Function) {
  			//result.add(obj.toString());
  		}
  		else if(obj==null||!(obj instanceof Expression))
  			result.add(obj);
  	}
    return result.toArray();
  }

  public String assemble(){
    return getStmtSql();
  }

}


