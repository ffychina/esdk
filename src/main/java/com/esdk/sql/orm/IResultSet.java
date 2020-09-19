package com.esdk.sql.orm;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.esdk.sql.IExpression;

public interface IResultSet<T extends IRow> extends Iterable<T>,Serializable{
	int size();

	boolean hasColumnName(String name);

	String[] getColumnNames();

	Iterator<T> iterator();

	IRowSet filter(IRowFilter filter);

	IRowSet filter(IExpression exp);

	IRowSet filter(int top,IExpression exp);
	
	double sum(String...columns);

	<RS extends IResultSet>RS sort(boolean isDesc,String...columns);

	<RS extends IResultSet>RS sort(String...columns);

	ABRowSet subRowSet(int start,int limit);

	JSONArray toJsonArray();
	
	JSONArray toJsonArray(boolean isFormatJavaBean);
	
	JSONArray toJsonArray(boolean isFormatJavaBean,boolean isOutputExtraColumns);

	JSONArray toJsonArray(boolean isFormatJavaBean,String...columns);
	
	List<Map> toMapList();
	
	List<Map> toMapList(boolean isFormatJavaBean);
	
	String[] getStrings(String columnName);
	
	Integer[] getIntegers(String columnName);

	Object[] getObjects(String columnName);
	
	Double[] getDoubles(String columnName);
	
	/**返回第一条记录，长度为0时返回null*/
  T getFirstRow();
  
	/**返回第一条记录
	 * @param isNotNull 为true时自动生成一个row实例返回
	 * */
  T getFirstRow(boolean isNotNull);
  
  /**执行select.count返回总记录数*/
  int totalCount();
}
