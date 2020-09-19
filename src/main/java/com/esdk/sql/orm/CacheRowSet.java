package com.esdk.sql.orm;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.esdk.sql.IExpression;
import com.esdk.utils.CacheMap;

public class CacheRowSet<T extends IRow> implements IRowSet<T>{
	protected transient CacheMap<String,ABRowSet> cacheMap; 
	private ABRowSet abrs;

	public CacheRowSet(IRowSet rs){
		this.abrs=new ABRowSet(rs);
		cacheMap=new CacheMap(CacheMap.DEFAULT_TIMEOUT);
	}
	
	public CacheRowSet(String...columns){
		this(new ABRowSet(columns));
	}
	
	@Override
	public IRowSet filter(IRowFilter filter){
		String key=filter.toString();
		ABRowSet result=cacheMap.get(key);
		if(result==null||result.size()==0) {
			result=abrs.filter(filter);
			cacheMap.put(key,result);
		}
		return result;
	}

	@Override
	public ABRowSet filter(IExpression exp){
		String key=exp.toString();
		ABRowSet result=cacheMap.get(key);
		if(result==null||result.size()==0) {
			result=abrs.filter(exp);
			cacheMap.put(key,result);
		}
		return result;
	}

	@Override
	public ABRowSet filter(int top,IExpression exp){
		return filter(exp).subRowSet(0,top);
	}

	public  ABRowSet<T> filter(int top,String fieldName,Object equalValue) {
		String key=top+","+fieldName+"="+equalValue;
		ABRowSet result=cacheMap.get(key);
		if(result==null||result.size()==0) {
			result=abrs.filter(top,fieldName,equalValue);
			cacheMap.put(key,result);
		}
		return result;
	}
	
	public  ABRowSet<T> filter(String fieldName,Object equalValue) {
		String key=fieldName+"="+equalValue;
		ABRowSet result=cacheMap.get(key);
		if(result==null||result.size()==0) {
			result=abrs.filter(fieldName,equalValue);
			cacheMap.put(key,result);
		}
		return result;
	}
	
	public  ABRowSet<T> filter(String fieldName,String expression,Object equalValue) {
		String key=fieldName+expression+equalValue;
		ABRowSet result=cacheMap.get(key);
		if(result==null||result.size()==0) {
			result=abrs.filter(fieldName,expression,equalValue);
			cacheMap.put(key,result);
		}
		return result;
	}

	public  ABRowSet<T> filter(int top,String fieldName,String expression,Object equalValue) {
		String key=top+","+fieldName+expression+equalValue;
		ABRowSet result=cacheMap.get(key);
		if(result==null||result.size()==0) {
			result=abrs.filter(top,fieldName,expression,equalValue);
			cacheMap.put(key,result);
		}
		return result;
	}
	
	public  ABRowSet<T> filter(int top,String expr) {
		String key=top+","+expr;
		ABRowSet result=cacheMap.get(key);
		if(result==null||result.size()==0) {
			result=abrs.filter(top,expr);
			cacheMap.put(key,result);
		}
		return result;
	}
	
	public  ABRowSet<T> filter(String expr) {
		String key=expr;
		ABRowSet result=cacheMap.get(key);
		if(result==null||result.size()==0) {
			result=abrs.filter(expr);
			cacheMap.put(key,result);
		}
		return result;
	}
	 
  private void clearCachedFilterRowSet() {
  	if(cacheMap!=null)
    	cacheMap.clear();
  }
	
	public CacheRowSet<T> add(ABRowSet rs){
		this.abrs.add(rs.getRows());// 注意:多个对象共用一个IRow
		clearCachedFilterRowSet();
		return this;
	}
  
  public CacheRowSet<T> add(int position,IRow row){
    abrs.add(position,row);//注意:多个对象共用一个IRow
    clearCachedFilterRowSet();
    return this;
  }

   public CacheRowSet<T> add(IRow row){
    abrs.add(row);//注意:多个对象共用一个IRow
    clearCachedFilterRowSet();
    return this;
  }

  public CacheRowSet<T> add(List<T> rows){
    abrs.getRows().addAll(abrs.size(),rows);//注意:多个对象共用一个IRow
    clearCachedFilterRowSet();
    return this;
  }
  
  public CacheRowSet<T> add(IRowSet rows){
    abrs.rowList.addAll(abrs.rowList.size(),rows.getRows());//注意:多个对象共用一个IRow
    clearCachedFilterRowSet();
    return this;
  }
  @Override public boolean remove(IRow row){
    clearCachedFilterRowSet();
    return abrs.remove(row);
  }
  
  @Override public boolean remove(IRowSet rs){
    clearCachedFilterRowSet();
    return remove(rs.getRows());
  }

  public boolean remove(ABRowSet rs){
    clearCachedFilterRowSet();
    return remove(rs.getRows());
  }

  @Override public boolean remove(Collection<IRow> coll){
  	boolean result=true;
  	for(Iterator iter=coll.iterator();iter.hasNext();) {
  		IRow row=(IRow)iter.next();
  		result=remove(row)&&result;
  	}
    clearCachedFilterRowSet();
  	return result;
  }
  
  @Override public IRow remove(int index){
    clearCachedFilterRowSet();
    return abrs.remove(index);
  }

	@Override
	public int size(){
		return abrs.size();
	}

	@Override
	public boolean hasColumnName(String name){
		return abrs.hasColumnName(name);
	}

	@Override
	public Iterator<T> iterator(){
		return abrs.iterator();
	}

	@Override
	public double sum(String...columns){
		return abrs.sum(columns);
	}

	@Override
	public <RS extends IResultSet> RS sort(boolean isDesc,String...columns){
		return (RS)abrs.sort(isDesc,columns);
	}

	@Override
	public <RS extends IResultSet> RS sort(String...columns){
		return (RS)abrs.sort(columns);
	}

	@Override
	public ABRowSet subRowSet(int start,int limit){
		return abrs.subRowSet(start,limit);
	}

	@Override
	public JSONArray toJsonArray(){
		return abrs.toJsonArray();
	}

	@Override
	public JSONArray toJsonArray(boolean isOutputExtraColumns){
		return abrs.toJsonArray(isOutputExtraColumns);
	}

	@Override
  public JSONArray toJsonArray(boolean isFormatJavaBeanName,String... columns){
  	return abrs.toJsonArray(isFormatJavaBeanName,columns);
  }
	
  public JSONArray toJsonArray(boolean isFormatJavaBeanName,boolean isOutputExtraColumns){
  	return abrs.toJsonArray(isFormatJavaBeanName,isOutputExtraColumns);
  }

	@Override
	public List<Map> toMapList(){
		return toMapList();
	}

	@Override
	public List<Map> toMapList(boolean isFormatJavaBean){
		return toMapList(isFormatJavaBean);
	}

	@Override
	public String[] getStrings(String columnName){
		return abrs.getStrings(columnName);
	}

	@Override
	public Integer[] getIntegers(String columnName){
		return abrs.getIntegers(columnName);
	}

	@Override
	public String[] getColumnNames(){
		return abrs.getColumnNames();
	}

	@Override
	public IRowSet setColumnNames(String[] names){
		return abrs.setColumnNames(names);
	}

	@Override
	public IRowSet setColumnNames(Collection names){
		return abrs.setColumnNames(names);
	}

	@Override
	public List<IRow> getRows(){
		return abrs.getRows();
	}

	@Override
	public void removeAll(){
		abrs.removeAll();
		clearCachedFilterRowSet();
	}

	@Override
	public boolean isEmpty(){
		return abrs.isEmpty();
	}

	@Override
	public IRow getRow(int i){
		return abrs.getRow(i);
	}

	@Override
	public IRow getRow(){
		return abrs.getRow();
	}

	@Override
	public IRow setRow(IRow row){
		return abrs.setRow(row);
	}

	@Override
	public IRow setRow(int index,IRow row){
		return abrs.setRow(index,row);
	}

	@Override
	public List<ABRowSet<IRow>> group(String...fields){
		return abrs.group(fields);
	}

	@Override
	public String toCsv(){
		return abrs.toCsv();
	}

	@Override
	public String toCsv(String...labels){
		return abrs.toCsv(labels);
	}

	@Override
	public T getFirstRow(){
		return (T)abrs.getFirstRow();
	}
	
	@Override
	public T getFirstRow(boolean isNotNull){
		return (T)abrs.getFirstRow(isNotNull);
	}

	@Override
	public Object[] getObjects(String columnName){
		return abrs.getObjects(columnName);
	}

	@Override
	public Double[] getDoubles(String columnName){
		return abrs.getDoubles(columnName);
	}

	@Override
	public int totalCount(){
		return abrs.totalCount();
	}

}
