package com.esdk.sql.orm;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.esdk.esdk;
import com.esdk.utils.CharAppender;
import com.esdk.utils.Constant;
import com.esdk.utils.EasyCsv;
import com.esdk.utils.JsonUtils;
import com.esdk.utils.TString;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;

import com.esdk.utils.EasyObj;
/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
public class ABRow<T extends ABRow> implements IRow{
  private Map<String,Object> record;
  private transient LinkedHashMap mapChanged;
  protected transient boolean isChanged;
  
  public ABRow(Map<String,Object> map) {
    this.record=map;
  }
  
  public ABRow(ABRow abrow) {
    this.record=abrow.record;
  }
  
  public ABRow(IRow row) {
  	record=row.record();
  }
  
  public ABRow(String[] columns){
  	record=new LinkedHashMap();
  	for(int i=0;i<columns.length;i++){
			straightSet(columns[i],null);
		}
	}
  
	public ABRow(String[] columns,Object[] values){
		record=new LinkedHashMap();
		for(int i=0;i<columns.length;i++){
			Object value=(i>=values.length)?"":values[i];
			straightSet(columns[i],value);
		}
	}

	private Map getMapChanged() {
    if(mapChanged==null)
      mapChanged=new LinkedHashMap();
    return mapChanged;
  }
  
  @JSONField(serialize=false) 
  public String[] getNames(){
    return (String[])record.keySet().toArray(new String[record.size()]);
  }

  public boolean hasColumnName(String name){
    return record.containsKey(name);
  }

  public T load(Map datas){
    if(datas==null)
    	return (T)this;
    try{
      for(Iterator iter=record.keySet().iterator();iter.hasNext();){
        String key=(String)iter.next();
        if(datas.containsKey(key))
        	straightSet(key,datas.get(key));
      }
    }
    catch(Exception e){
      throw new RuntimeException(e);
    }
    return (T)this;
  }

  private void straightSet(String key,Object v){
    record.put(key,v);
  }

  private boolean isSameValue(String key,Object newvalue){
    Object oldvalue=record==null?null:get(key);
    if(newvalue==oldvalue)return true;//if(null==null) return true;
    if(newvalue instanceof Date && oldvalue instanceof Date)
    	return ((Date)newvalue).getTime()==((Date)oldvalue).getTime();
    return oldvalue!=null&&oldvalue.equals(newvalue);
  }
  
  public void set(String key,Object v){
    if(!isSameValue(key,v)) {
      setNewValue(key,v);
    }
  }
  
  private void setNewValue(String key,Object v){
    straightSet(key,v);
    isChanged=true;
    getMapChanged().put(key,v);
  }
  
  public Object get(String key){
    /*return record.get(key);*/
    return esdk.str.get(record,key);
  }

  @JSONField(serialize=false) 
  public boolean isChanged() {
    return isChanged;
  }
  
  @JSONField(serialize=false) 
  public Map getChanged() {
    return mapChanged;
  }

  public List toList() {
  	return RowUtils.toList(this);
  }

  public List toList(String...labels) {
  	return RowUtils.toList(this,labels);
  }
  
  @Override
  public String toXml() {
  	return XML.toXml(JSONUtil.parseObj(toMap(true)),Constant.RowXmlIdentifier);
  }
  
  @Override
  public String toXml(String... labels) {
  	return XML.toXml(JSONUtil.parseObj(toMap(true,labels)),Constant.RowXmlIdentifier);
  }
  
  public String toCsv(){
    return toCsv((String[])record.keySet().toArray(new String[0]));
  }

  public String toCsv(String... labels){
    TString result=new TString();
    result.append(new CharAppender(',').add(labels));
    CharAppender ca=new CharAppender(',');
    for(int j=0;j<labels.length;j++){
      ca.append(EasyCsv.csvEncode(esdk.str.getStringNoNull(this.get(labels[j]))));
    }
    result.appendLine(ca);
    return result.toString();    
  }

  /**默认输出驼峰格式*/
	@Override public JSONObject toJsonObject() {
  	return toJsonObject(true);
  }

  public String toString(){
  	return JsonUtils.toJSONString(toJsonObject(),JsonUtils.getSerializeConfig());
  }
  
  public JSONObject toJsonObject(boolean isFormatJavaBeanName) {
  	if(isFormatJavaBeanName) {
    	LinkedHashMap result=new LinkedHashMap();
    	String[] columns=esdk.str.toArray(this.record.keySet());
    	for(int i=0;i<columns.length;i++){
      	result.put(isFormatJavaBeanName?esdk.str.toCamelCase(columns[i]):columns[i],this.record.get(columns[i]));
  		}
  		return JsonUtils.toJsonObject(result);
  	}
  	else
  		return JsonUtils.toJsonObject(this.record);
  }

  public JSONObject toJsonObject(String... columns) {
  	LinkedHashMap result=new LinkedHashMap();
  	for(int i=0;i<columns.length;i++){
    	result.put(columns[i],this.record.get(columns[i]));
		}
  	return JsonUtils.toJsonObject(result);
  }

  public JSONObject toJsonObject(boolean isFormatJavaBeanName,String... columns) {
  	if(columns.length==0)
  		columns=esdk.str.toArray(this.record.keySet());
  	if(isFormatJavaBeanName) {
    	LinkedHashMap result=new LinkedHashMap();
    	for(int i=0;i<columns.length;i++){
      	result.put(isFormatJavaBeanName?esdk.str.toCamelCase(columns[i]):columns[i],this.record.get(columns[i]));
  		}
  		return JsonUtils.toJsonObject(result);
  	}
  	else
  		return toJsonObject(columns);
  }
  
  public Map toMap() {
  	return toMap(false);
  }
  
  public Map toMap(boolean isFormatJavaBeanName,String...columns) {
  	LinkedHashMap result=new LinkedHashMap(this.record.size());
  	for(Iterator iter=record.entrySet().iterator();iter.hasNext();) {
  		Entry entry=(Entry)iter.next();
  		String key=(String)entry.getKey();
  		if(columns.length==0||esdk.str.existOf(columns,key)) {
	  		Object value=entry.getValue();
	  		if(isFormatJavaBeanName)
	  			key=esdk.str.toCamelCase(key,false);
	  		result.put(key,value);
  		}
  	}
  	return result;
  }
  
  public Object clone() {
    ABRow result=new ABRow(this.record);
    result.load(this);
    return result;
  }
  
  @JSONField(serialize=false) 
  public Map record() {
    return record;
  }
  
  public T load(IRow row){
		if(row==null)
			return (T)this;
		for(Iterator iter=record.keySet().iterator();iter.hasNext();){
			String key=(String)iter.next();
			record.put(key,row.get(key));
		}
		return (T)this;
	}
  
  public void load(IRow row,boolean isNullable){
    if(row==null)return ;
    try{
      for(Iterator iter=record.keySet().iterator();iter.hasNext();){
        String key=(String)iter.next();
        Object newValue=row.get(key);
        if(isNullable||newValue!=null)
        	record.put(key,newValue);
      }
    }
    catch(Exception e){
      throw new RuntimeException(e);
    }
  }

  public T load(Object bean){
    if(bean!=null)
    	RowUtils.loadFrom(bean,this,false);
    return (T)this;
  }

  public T load(Object bean,boolean isNullable){
    if(bean!=null)
    	RowUtils.loadFrom(bean,this,isNullable);
    return (T)this;
  }

  public Object copy(Object bean) {
  	return RowUtils.copyTo(this,bean,false);
  }
  
  public Object copy(Object bean,boolean isNullable) {
  	return RowUtils.copyTo(this,bean,isNullable);
  }

	@Override public Boolean getBoolean(String key){
		return (Boolean)EasyObj.convert(get(key),Boolean.class);
	}

	@Override public Double getDouble(String key){
		return (Double)EasyObj.convert(get(key),Double.class);
	}

	@Override public Integer getInteger(String key){
		if(get(key)==null)
			 return null; //modify by franky at 2017-07-28 19:28, 之前一直是return 0;
		else
		   return (Integer)EasyObj.convert(get(key),Integer.class);
	}

	@Override public Long getLong(String key){
		return (Long)EasyObj.convert(get(key),Long.class);
	}

	@Override public Short getShort(String key){
		return (Short)EasyObj.convert(get(key),Short.class);
	}

	@Override public String getString(String key){
		return (String)EasyObj.convert(get(key),String.class);
	}
	@Override public Date getDate(String key){
		return (Date)EasyObj.convert(get(key),Date.class);
	}

}