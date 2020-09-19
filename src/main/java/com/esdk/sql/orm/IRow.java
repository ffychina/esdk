package com.esdk.sql.orm;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
public interface IRow<T> extends IChangeable{
	Object get(String key);
	Short getShort(String key);
	Integer getInteger(String key);
	Long getLong(String key);
	Double getDouble(String key);
	String getString(String key);
	Boolean getBoolean(String key);
	void set(String key,Object v);
	Date getDate(String key);
	
	@JSONField(serialize=false) 
	String[] getNames();
	boolean hasColumnName(String name);
	List toList();
	List toList(String...labels);
	String toCsv();
	String toXml();
	String toXml(String...labels);
	String toCsv(String...labels);
	JSONObject toJsonObject();
	JSONObject toJsonObject(String... labels);
	JSONObject toJsonObject(boolean formatJavaBeanName);
	JSONObject toJsonObject(boolean formatJavaBeanName,String... labels);
	T load(IRow row);
	T load(Map rowMap);
	T clone() ;
	/**getMap会导致swagger在@requestBody下无法输出参数*/
	@JSONField(serialize=false) 
	Map record();
	@JSONField(serialize=false) 
	Map toMap();
	Map toMap(boolean isFormatJavaBeanName,String...columns);
	@JSONField(serialize=false) 
	Map getChanged();
}
