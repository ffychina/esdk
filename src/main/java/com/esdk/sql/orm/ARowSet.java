package com.esdk.sql.orm;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.nutz.lang.util.LinkedArray;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;
import com.esdk.sql.Expression;
import com.esdk.sql.IExpression;
import com.esdk.sql.RowExpressions;
import com.esdk.sql.SQLRuntimeException;
import com.esdk.utils.Constant;
import com.esdk.utils.EasyArray;
import com.esdk.utils.EasyFile;
import com.esdk.utils.EasyMath;
import com.esdk.utils.EasyObj;
import com.esdk.utils.EasyQuery;
import com.esdk.utils.EasyStr;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;
/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
public class ARowSet<T extends IRow> implements IRowSet<T>,Cloneable,ICursor{
	protected transient IResultSet _abrs;
  protected transient int cursor=-1;
  protected LinkedHashSet columns;
  protected String _rowClsName;
  protected List<IRow> rowList;
  private String primaryKeyFieldName;
  
  public ARowSet(){
    rowList=new ArrayList();
    columns=new LinkedHashSet();
  }

  public ARowSet(Class<T> rowCls){
    this();
    this._rowClsName=rowCls.getName();
    try {
      setColumnNames(((T)esdk.reflect.safeNewInstance(rowCls)).getNames());
    } catch (Exception e) {
      throw new SdkRuntimeException(e);
    }
  }

  public ARowSet(String[] columns){
    this();
    setColumnNames(columns);
  }

  public ARowSet(Object[][] array){
    this();
    columns.addAll(Arrays.asList(array[0]));
    for(int i=1;i<array.length;i++){
      add(new ABRow((String[])array[0],array[i]));
    }
  }

  public  <R extends ParentRow>ARowSet (JSONArray jsarray,Class<R> parentRowCls){
    this();
    if(jsarray.size()>0) {
    	JSONObject jo=jsarray.getJSONObject(0);
    	columns.addAll(jo.keySet());
    	 try{
				for(int i=0;i<jsarray.size();i++){
					 R parentRow=parentRowCls.newInstance();
					 parentRow.load(jsarray.getJSONObject(i));
				   add(parentRow);
				 }
			}catch(Exception e){
				throw new SdkRuntimeException(e);
			}
    }
  }

	public <R extends ParentRow> ARowSet(List<Object> beanList,Class<R> parentRowCls){
		this();
		try{
			R parentRow=parentRowCls.newInstance();
			columns.addAll(Arrays.asList(parentRow.getNames()));
			for(int i=0;i<beanList.size();i++){
				parentRow=parentRowCls.newInstance();
				parentRow.load(beanList.get(i));
				add(parentRow);
			}
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
	}
	
  public ARowSet(JSONArray jsarray){
    this();
    if(jsarray.size()>0) {
    	JSONObject jo=jsarray.getJSONObject(0);
    	columns.addAll(jo.keySet());
    	 for(int i=0;i<jsarray.size();i++){
         add(new ABRow(jsarray.getJSONObject(i)));
       }
    }
  }
  
  public ARowSet(IResultSet rs,List<IRow> list) {
  	this(list);
  	if(rs instanceof ARowSet)
  		_abrs=((ARowSet)rs)._abrs;
  	else
  		_abrs=rs;
  	setColumnNames(rs.getColumnNames());
  }
  
  public ARowSet(File csvFile) throws IOException{
  	this(EasyStr.fromCsv(EasyFile.loadFromFile(csvFile)));
  }
  public ARowSet(File csvFile,String charset) throws IOException{
  	this(EasyStr.fromCsv(EasyFile.loadFromFile(csvFile,charset,EasyFile._KB)));
  }
  public ARowSet(File csvFile,String charset,boolean forceText) throws IOException{
  	this(EasyStr.fromCsv(EasyFile.loadFromFile(csvFile,charset,EasyFile._KB),forceText));
  }
  
  public ARowSet(ABResultSet rs) {
    this();
    this._abrs=rs;
    if(rs!=null) {
    	if(rs instanceof ParentResultSet)
    		this._rowClsName=((ParentResultSet)_abrs)._rowClsName;
	    try{
	      columns.addAll(Arrays.asList(rs.getColumns()));
	      for(Iterator iter=rs.iterator();iter.hasNext();){
	        Object obj=iter.next();
	        if(obj instanceof IRow)
	          rowList.add((IRow)obj);
	        else if(obj instanceof Map)
	          rowList.add(new ABRow((Map)obj));
	        else
	          throw new SQLRuntimeException("无法识别的类型:"+obj.getClass().toString());
	      }
	    }
	    catch(SQLException e){
	      throw new RuntimeException(e);
	    }
    }
  }
  
  public ARowSet(IRow[] rows){
    this();
    if(rows.length>0)
      columns.addAll(Arrays.asList(rows[0].getNames()));
      
    rowList.addAll(Arrays.asList(rows));
  }

  public ARowSet(List<IRow> list){
    this();
    if(list.size()>0)
      columns.addAll(Arrays.asList(((IRow)list.get(0)).getNames()));
    rowList.addAll(list);
  }

  public ARowSet(Collection<Map> listMap){
    this();
    if(listMap.size()>0) {
    	List<Map> list=new ArrayList(listMap);
    	Map jo=list.get(0);
    	columns.addAll(jo.keySet());
    	 for(int i=0;i<list.size();i++){
         add(new ABRow(list.get(i)));
       }
    }
  }
  
  protected final boolean isRange(int cursorPos){
    return ABResultSet.isRange(rowList,cursorPos);
  }
  
  @Override public ARowSet<T> add(int position,IRow row){
    rowList.add(position,row);//注意:多个对象共用一个IRow
    return this;
  }

  private int warningSize=10000;
  private void checkSize() {
    if(rowList.size()>warningSize) {
    	System.out.println("请注意：数据集数据大于"+warningSize);
    	warningSize+=5000;
    }
  }
  
  @Override public ARowSet<T> add(IRow row){
    rowList.add(row);//注意:多个对象共用一个IRow
    checkSize();
    return this;
  }

  @Override public ARowSet<T> add(List<T> rows){
    rowList.addAll(rowList.size(),rows);//注意:多个对象共用一个IRow
    checkSize();
    return this;
  }
  
  @Override public ARowSet<T> add(IRowSet rows){
    rowList.addAll(rowList.size(),rows.getRows());//注意:多个对象共用一个IRow
    checkSize();
    return this;
  }

  @Override  public String[] getColumnNames(){
    return (String[])columns.toArray(new String[columns.size()]);
  }

  /*按主键查询记录**/
	public T findById(Object pkid){
		boolean isParentRow=this.size()>0&&this.getRow(0) instanceof ParentRow;
		String pkfieldName=isParentRow?((ParentRow)this.getFirstRow()).getPrimaryKeyName():getColumnNames()[0];
		for(IRow row:this){
			if(esdk.obj.equal(pkid,row.get(pkfieldName)))
				return (T)row;
		}
		return null;
	}
	
  /*按字段内容查找匹配的第一条记录**/
	public T findRow(String column,Object find){
		for(Iterator<T> iter=iterator();iter.hasNext();){
			T row=iter.next();
			if(EasyObj.equal(row.get(column),find))
				return row;
		}
		return null;
	}

  @Override public T getRow(int i){
    checkRange(i);
    return (T)rowList.get(i);
  }

  protected final void checkRange(int i) {
    if(!isRange(i))
      throw new IndexOutOfBoundsException("index is "+i+" but size is "+rowList.size());
  }
  
  @Override public T getRow(){
    return getRow(cursor);
  }

  @Override  public int size(){
    return rowList.size();
  }

  public List<IRow> getRows(){
    return rowList;
  }
  
  public List<Map> toMapList(){
  	return toMapList(false);
  }
  
  public List<Map> toMapList(boolean isCamelCase){
  	ArrayList result=new ArrayList(this.size());
    for(int i=0;i<rowList.size();i++){
			IRow row=(IRow)rowList.get(i);
			result.add(row.toMap(isCamelCase));
		}
		return result;
  }
  
  public List<Map> toMapList(boolean isCamelCase,String...columns){
  	ArrayList result=new ArrayList(this.size());
    for(int i=0;i<rowList.size();i++){
			IRow row=(IRow)rowList.get(i);
			result.add(row.toMap(isCamelCase,columns));
		}
		return result;
  }
  
  /**默认输出驼峰格式，不输出扩展字段*/
	@Override
  public JSONArray toJsonArray(){
  	return toJsonArray(true);
  }

	@Override
	public JSONArray toJsonArray(boolean isCamelCase){
		return toJsonArray(isCamelCase,getColumnNames());
	}

	@Override
	public JSONArray toJsonArray(boolean isCamelCase,boolean isOutputExtraColumns){
		return toJsonArray(isCamelCase,isOutputExtraColumns?Constant.EmptyStrArr:getColumnNames());
	}
	
  public JSONArray toJsonArray(String... columns){
		return RowSetUtils.toJsonArray(this.toMapList(true,columns),false);
  }
  
  public JSONArray toJsonArray(boolean isCamelCase,String... columns){
  	if(columns.length>0)
  		return RowSetUtils.toJsonArray(this.toMapList(isCamelCase,columns),isCamelCase);
  	else
  		return RowSetUtils.toJsonArray(this.toMapList(isCamelCase),isCamelCase);
  }

	public <B> List<B> toBeanList(Class<B> beanCls){
		ArrayList result=new ArrayList(this.size());
		for(int i=0;i<rowList.size();i++){
			IRow row=(IRow)rowList.get(i);
			result.add(RowUtils.copyTo(row,esdk.reflect.safeNewInstance(beanCls),true));
		}
		return result;
	}
  
	/**为了方便调试，输出的格式不是csv也不是json，所以不能用作为业务数据内容输出*/
  @Override public String toString(){
    StringBuilder result=new StringBuilder();
    int[] index=new int[]{0};
    this.rowList.forEach(e-> {
      result.append((index[0]++)+"_"+e.getClass().getSimpleName()+e.toString()+"\n");
    });
    if(result.length()>0)
      result.delete(result.length()-1,result.length());
    return result.toString();
  }

  @Override public boolean hasColumnName(String name){
    return columns.contains(name);
  }

  @Override public boolean isEmpty(){
    return rowList.size()==0;
  }

  @Override public boolean remove(IRow row){
    return rowList.remove(row);
  }

  @Override public boolean remove(IRowSet rs){
    return remove(rs.getRows());
  }
  
  public boolean remove(ABRowSet rs){
    return remove(rs.getRows());
  }

  @Override public boolean remove(Collection<IRow> coll){
  	boolean result=true;
  	for(Iterator iter=coll.iterator();iter.hasNext();) {
  		IRow row=(IRow)iter.next();
  		result=remove(row)&&result;
  	}
  	return result;
  }

  @Override public IRow remove(int index){
    checkRange(index);
    return rowList.remove(index);
  }

  public void clear() {
  	removeAll();
  }

  @Override  public void removeAll(){
    rowList.clear();
  }
  
  public String toXml() {
  	ArrayList list=new ArrayList(rowList.size());
  	for(IRow row:rowList) {
  		list.add(row.toMap());
  	}
  	return XML.toXml(JSONUtil.parseArray(list),Constant.RowsetXmlIdentifier);
  }
  
  public String toXml(String[] labels) {
  	ArrayList list=new ArrayList(rowList.size());
  	for(IRow row:rowList) {
  		list.add(row.toMap(true,labels));
  	}
  	return XML.toXml(JSONUtil.parseArray(list),Constant.RowsetXmlIdentifier);
  }
  
  @Override  public String toCsv(){
  	return toCsv(getColumnNames());
  }

  public boolean toCsvFile(File file) throws IOException {
  	return esdk.file.saveToFile(toCsv(),file,true);
  }

  @Override  public String toCsv(String... labels){
    List<String[]> result=new ArrayList<String[]>(rowList.size()+1);
    result.add(labels);
    try{
      for(Iterator iter=rowList.iterator();iter.hasNext();){
        IRow row=(IRow)iter.next();
        ArrayList<String> list=new ArrayList(labels.length);
        for(Iterator iterator=Arrays.asList(labels).iterator();iterator.hasNext();){
          String column=(String)iterator.next();
          Object content=row.get(column);
          list.add(EasyStr.getStringNoNull(content));
        }
        result.add(list.toArray((String[])new String[0]));
      }
      return EasyStr.toCsv(result);
    }
    catch(Exception e){
      throw new RuntimeException(e);
    }
  }
  
  public List<T> toList(){
  	return (List<T>)rowList;
  }
  
  public String[][] toArray2(){
  	List result=new ArrayList(this.size()+1);
  	String[] labels=this.getColumnNames();
  	result.add(labels);
  	 ArrayList<String> list=new ArrayList(labels.length);
  	for(Iterator iter=rowList.iterator();iter.hasNext();) {
  		IRow row=(IRow)iter.next();
      for(Iterator iterator=Arrays.asList(labels).iterator();iterator.hasNext();){
        String column=(String)iterator.next();
        Object content=row.get(column);
        list.add(EasyStr.getStringNoNull(content));
      }
      result.add(list.toArray((String[])new String[0]));
      list.clear();
  	}
  	return EasyStr.listToArr(result);
	}
  
  public List<List> toList2(){
  	return toList2(getColumnNames());
  }

	private List<List> toList2(String... labels){
		List<List> result=new ArrayList<List>(rowList.size()+1);
		for(Iterator iter=rowList.iterator();iter.hasNext();){
			IRow row=(IRow)iter.next();
			ArrayList list=new ArrayList(labels.length);
			for(Iterator iterator=Arrays.asList(labels).iterator();iterator.hasNext();){
				String column=(String)iterator.next();
				Object content=row.get(column);
				list.add(content);
			}
			result.add(list);
		}
		return result;
	}

	@Override  public IRow setRow(IRow row){
    checkRange(cursor);
    return rowList.set(cursor,row);
  }

	@Override  public IRow setRow(int index,IRow row){
    checkRange(index);
    return rowList.set(index,row);
  }

	@Override public ARowSet setColumnNames(String[] names){
    if(!EasyObj.isBlank(names))
      columns.addAll(Arrays.asList(names));
    return this;
  }

	public ARowSet addColumn(String name) {
		if(!EasyObj.isBlank(name)) {
			columns.add(name);
		}
		return this;
	}
	
	public ARowSet addColumns(String... names) {
		if(!EasyObj.isBlank(names)) {
			for(String item:names) {
				columns.add(item);
			}
		}
		return this;
	}
  @Override public IRowSet setColumnNames(Collection names){
    if(names!=null)
      columns.addAll(Arrays.asList(names));
    return this;
  }
  
	/*  @Override public ABRowSet<T> clone(){
	  ARowSet result;
			try{
				result=(ARowSet)super.clone();
				result.setColumnNames(this.getColumnNames());
		    for(IRow row:rowList){
		      result.add((IRow)row.clone());
		    }
		    return (ABRowSet<T>)result;
			}catch(CloneNotSupportedException e){
				throw new SdkRuntimeException(e);
			}
	}*/
  
  @Override public ARowSet<T> clone(){
	  try{
			ARowSet<T> result=(ARowSet<T>)super.clone();
			result.rowList=new ArrayList(this.size());
			result.rowList.addAll(this.rowList);
			result._rowClsName=this._rowClsName;
			return result;
		}catch(CloneNotSupportedException e){
			throw new SdkRuntimeException(e);
		}
	}
  
  @Override public Iterator<T> iterator() {
    return (Iterator<T>)rowList.iterator();
  }

  public T createRow() {
  	if(_abrs!=null&&_abrs instanceof ParentResultSet)
  		return (T)((ParentResultSet)_abrs).createRowInstance();
    else if(this._rowClsName!=null) {
    	ParentRow result=esdk.reflect.safeNewInstance(_rowClsName);
    	RowUtils.fillDefaultVals(result);
    	return (T)result;
    }else {
    	return (T)new ABRow(getColumnNames());
    }
  }

	/**使用lambda表达式过滤*/
	public ABRowSet<T> filter(Predicate<T> fn){
		return filter(0,fn);
	}

	/**使用lambda表达式过滤，只获取第1条件记录*/
	public T filterFirst(Predicate<T> fn){
		return filter(1,fn).getFirstRow();
	}
	
	/**使用lambda表达式过滤，只获取第1条件记录
	 * 参数autoCreateInstance:是否自动创建一个新实例
	 * */
	public T filterFirst(Predicate<T> fn,boolean autoCreateInstance){
		return filter(1,fn).getFirstRow(autoCreateInstance);
	}
	
	/**使用lambda表达式过滤*/
	public ABRowSet<T> filter(int top,Predicate<T> fn){
		ABRowSet<T> result=new ABRowSet();
		result._abrs=this._abrs;
		result._rowClsName=this._rowClsName;
		if(top<=0)
			top=Integer.MAX_VALUE;
		for(T row:this) {
			if(fn.test(row)) {
				if(top-->0)
					result.add(row);
			}
		}
		return result;
	}
	
	/**使用lambda表达式过滤，把找到的记录移除并返回*/
	public ABRowSet<T> remove(Predicate<T> fn){
		ABRowSet<T> result=filter(fn);
		remove(result);
		return result;
	}
	
  @Override public ABRowSet<T> filter(IRowFilter filter) {
  	return RowSetUtils.filter(this,filter);
  }
  
  @Override public ABRowSet<T> filter(IExpression exp) {
  	return RowSetUtils.filter(this,new RowFilter(exp));
  }
  
	@Override
	public ABRowSet<T> filter(int top,IExpression exp){
		return RowSetUtils.filter(new ABRowSet(this),new RowFilter(exp),top);
	}

  public T filterFirst(String fieldName,Object equalValue) {
  	return filter(RowFilter.create(fieldName,equalValue)).getFirstRow();
  }
  
  public ABRowSet<T> filter(String fieldName,Object equalValue) {
  	return filter(RowFilter.create(fieldName,equalValue));
  }
  
  public ABRowSet<T> filter(String fieldName,String expression,Object equalValue) {
  	return filter(RowFilter.create(fieldName,expression,equalValue));
  }
  
  public ABRowSet<T> filter(int top,String fieldName,String expression,Object equalValue) {
  	return RowSetUtils.filter(this,RowFilter.create(fieldName,expression,equalValue),top);
  }
  
  public ABRowSet<T> filter(String expr) {
  	synchronized(this){
	  	ABRowSet<T> result=new ABRowSet<T>(this.getColumnNames());
	  	result.rowList=(new EasyQuery(getRows()).filter(expr).toList());
	  	return result;
  	}
  }
  
  public ABRowSet<T> filter(int top,String expr){
  	synchronized(this){
	  	ABRowSet result=new ABRowSet(this.getColumnNames());
	  	try {
				result.load(new EasyQuery(this.getRows()).filter(expr,top).toList());
			} catch (Exception e) {
				throw new SQLRuntimeException(e);
			}
			return result;
		}
  }

  public ABRowSet<T> filter(int top,String fieldName,Object equalValue) {
  	return filter(RowFilter.create(fieldName,equalValue).setTop(top));
  }

	/**可以一次输入多个and的条件，并支持所有的表达式。eg.: filter("name","张三",code,<>,test,"age","<",18)*/
	public ABRowSet<T> filters(String firstField,Object... args){
		args=esdk.array.concat(Object.class,firstField,args);
		RowExpressions expressions=new RowExpressions();
		String fieldValue=null,expression=Expression.EQ;
		Object rightValue=null;
		boolean isFieldValue=true;
		for(Object arg:args) {
			if(isFieldValue) {
				fieldValue=(String)arg;
				isFieldValue=false;
			}else if(EasyArray.contains(Expression.All,arg)) {
				expression=(String)arg;
			}else {
				rightValue=arg;
				expressions.add(fieldValue,expression,rightValue);
				isFieldValue=true;
				expression=Expression.EQ;
			}
		}
		return filter(expressions);
	}
	
	/**可以一次输入多个and的条件，并支持所有的表达式。eg.: filter("name","张三",code,<>,test,"age","<",18)*/
	public ABRowSet<T> filters(int top,String firstField,Object... args){
		args=esdk.array.concat(Object.class,firstField,args);
		RowFilter result=null;
		String fieldValue=null,expression=Expression.EQ;
		Object rightValue=null;
		boolean isFieldValue=true;
		for(Object arg:args) {
			if(isFieldValue) {
				fieldValue=(String)arg;
				isFieldValue=false;
			}else if(esdk.array.contains(Expression.All,arg)) {
				expression=(String)arg;
			}else {
				rightValue=arg;
				isFieldValue=true;
				if(result==null)
					result=RowFilter.create(fieldValue,expression,rightValue).setTop(top);
				else
					result.add(fieldValue,expression,rightValue);
				expression=Expression.EQ;
			}
		}
		return filter(result);
	}
  
	/**对多个字段进行分组，以字段内容作为唯一值，把多条记录分配到多个记录集中*/
	@Override public List<ABRowSet<T>> group(String... fields) {
  	return RowSetUtils.group(this,fields);
  }
  
	/**使用lambda表达式修改记录内容*/
	public ABRowSet<T> update(Consumer<T> fn) {
		for(T row:this) {
			fn.accept(row);
		}
		return (ABRowSet<T>)this;
	}
	
	/**使用lambda表达式过滤*/
  public String[] getStrings(Function<T,String> fn) {
  	String[] result=new String[rowList.size()];
  	int i=0;
  	for(T row:this){
			result[i++]=fn.apply(row);
		}
  	return result;
  }
	
	/**使用lambda表达式过滤*/
  public Long[] getLongs(Function<T,Long> fn) {
  	Long[] result=new Long[rowList.size()];
  	int i=0;
  	for(T row:this){
			result[i++]=fn.apply(row);
		}
  	return result;
  }
	
	/**使用lambda合计数量，可以用sum()代替*/
  public int reduceInt(ToIntFunction<T> fn) {
		int result=0;
		for(T row:this){
			result+=fn.applyAsInt(row);
		}
		return result;
	}
	
	/**使用lambda合计数量，可以用sum()代替*/
	public long reduceLong(ToLongFunction<T> fn) {
		long result=0;
		for(T row:this){
			result+=fn.applyAsLong(row);
		}
		return result;
	}
	
	/**使用lambda合计数量，可以用sum()代替*/
	public double reduce(ToDoubleFunction<T> fn) {
		Double result=0D;
		for(T row:this){
			result+=fn.applyAsDouble(row);
		}
		return result;
	}
	
	/**得到某个字段的所有记录的内容*/
	public String[] getStrings(String columnName) {
  	String[] result=new String[rowList.size()];
  	int i=0;
  	for(Iterator iterator=rowList.iterator();iterator.hasNext();){
  		IRow row=(IRow)iterator.next();
			result[i++]=(String)row.get(columnName);
		}
  	return result;
  }
  
	/**得到某个字段的所有记录的内容*/
  public Integer[] getIntegers(String columnName) {
  	Integer[] result=new Integer[rowList.size()];
  	int i=0;
  	for(Iterator iterator=rowList.iterator();iterator.hasNext();){
  		IRow row=(IRow)iterator.next();
			result[i++]=esdk.obj.castTo(row.get(columnName),Integer.class);
		}
  	return result;
  }
  
	/**得到某个字段的所有记录的内容*/
  public Long[] getLongs(String columnName) {
  	Long[] result=new Long[rowList.size()];
  	int i=0;
  	for(Iterator iterator=rowList.iterator();iterator.hasNext();){
  		IRow row=(IRow)iterator.next();
			result[i++]=esdk.obj.castTo(row.get(columnName),Long.class);
		}
  	return result;
  }
  
	/**得到某个字段的所有记录的内容*/
  public Double[] getDoubles(String columnName) {
  	Double[] result=new Double[rowList.size()];
  	int i=0;
  	for(Iterator iterator=rowList.iterator();iterator.hasNext();){
  		IRow row=(IRow)iterator.next();
			result[i++]=esdk.obj.castTo(row.get(columnName),Double.class);
		}
  	return result;
  }
  
	/**得到某个字段的所有记录的内容*/
  public Number[] getNumbers(String columnName) throws Exception{
  	Number[] result=new Number[size()];
    for(int i=0,n=rowList.size();i<n;i++){
      result[i]=(Number)((IRow)rowList.get(i)).get(columnName);
    }
    return result;
  }
  
  /**合计总数量*/
  @Override public double sum(String...columns) {
  	double result=0;
    for(int i=0,n=rowList.size();i<n;i++){
    	IRow row=(IRow)rowList.get(i);
    	for(int j=0;j<columns.length;j++){
    		if((row).get(columns[j])!=null)
    		  result+=((Number)(row).get(columns[j])).doubleValue();
    	}
    }
    return result;
  }  
  
  /**取平均值*/
  public double average(String...columns) throws Exception {
  	return sum(columns)/size();
  }
  
  /**取最大值*/
  public Number max(String...columns) throws Exception {
  	Number result=null;
  	for(int i=0;i<columns.length;i++) {
  		Number max=esdk.math.max(getNumbers(columns[i]));
  		if(result==null)
  			result=max;
  		else
  			result=esdk.math.max(max,result);
  	}
    return result;
  }
  
  /**取最小值*/
  public Number min(String...columns) throws Exception {
  	Number result=null;
  	for(int i=0;i<columns.length;i++) {
  		Number min=esdk.math.min(getNumbers(columns[i]));
  		if(result==null)
  			result=min;
  		else
  			result=esdk.math.min(min,result);
  	}
    return result;
  }
  
  /**取最大值*/
  public Object maxObj(String...columns) throws Exception {
  	Comparable result=null;
  	for(int i=0;i<columns.length;i++) {
  		Comparable max=esdk.obj.max((Comparable[])getValues(columns[i]).toArray(new Comparable[0]));
  		if(result==null)
  			result=max;
  		else
  			result=esdk.obj.max(max,result);
  	}
    return result;
  }
  
  /**取最小值*/
  public Comparable minObj(String...columns) throws Exception {
  	Comparable result=null;
  	for(int i=0;i<columns.length;i++) {
  		Comparable min=esdk.obj.min((Comparable[])getValues(columns[i]).toArray(new Comparable[0]));
  		if(result==null)
  			result=min;
  		else
  			result=esdk.obj.min(min,result);
  	}
    return result;
  }
  
  public Object[] getObjects(String columnName) {
  	Object[] result=new Object[rowList.size()];
  	int i=0;
  	for(Iterator iterator=rowList.iterator();iterator.hasNext();){
  		IRow row=(IRow)iterator.next();
			result[i++]=row.get(columnName);
		}
  	return result;
  }
  
  public List getValues(String columnName) {
  	ArrayList result=new ArrayList(rowList.size());
  	for(Iterator iterator=rowList.iterator();iterator.hasNext();){
  		IRow row=(IRow)iterator.next();
			result.add(row.get(columnName));
		}
  	return result;
  }
  
  public Object[] getBigDecimals(String columnName) {
  	BigDecimal[] result=new BigDecimal[rowList.size()];
  	int i=0;
  	for(Iterator iterator=rowList.iterator();iterator.hasNext();){
  		IRow row=(IRow)iterator.next();
			result[i++]=(BigDecimal)row.get(columnName);
		}
  	return result;
  }
  
  public T getFirstRow() {
  	return size()>0?getRow(0):null;
  }
  
  public T getFirstRow(boolean autoCreateInstance) {
  	return size()>0?getRow(0):autoCreateInstance?createRow():null;
  }
  
  public T getLastRow(){
  	return size()>0?getRow(this.size()-1):null;
  }
  
  @Override public ABRowSet<T> sort(String... columnKeys) {
    return sort(false,columnKeys);
  }
  
	@Override public ABRowSet<T> sort(final boolean isDesc,final String...cols){
		if(size()>0){
			Collections.sort(this.rowList,new Comparator(){
				@Override public int compare(Object o1,Object o2){
					IRow i1=(IRow)o1;
					IRow i2=(IRow)o2;
					int result=0;
					if(i2==null && i1==null)
						result=0;
					else {
					for(int i=0;i<cols.length&&result==0;i++){
						if(isDesc){
							if(i2 == null)
								result=-1;
							else if(i1==null)
								result=1;
							else 
								result=EasyObj.compareTo(i2.get(cols[i]),i1.get(cols[i]));
							if(result>0)
								break;
						}else{
							if(i1 == null)
								result=-1;
							else if(i2 ==null )
								result=1;
							else
								result=EasyObj.compareTo(i1.get(cols[i]),i2.get(cols[i]));
							if(result<0)
								break;
						}
					}
					}
					return result;
					
				}
			});
		}
		return (ABRowSet<T>)this;
	}
	
  public ABRowSet<T> subRowSet(int start,int limit) {
  	int adjustStart=EasyMath.max(EasyMath.min(start,rowList.size()),0);
  	int adjustLimit=adjustStart+EasyMath.min(esdk.math.max(limit,0),EasyMath.max(rowList.size()-adjustStart,0));
  	List<IRow> subList=this.rowList.subList(adjustStart,adjustLimit);
  	return subList==null?null:new ABRowSet(this.getColumnNames()).load(subList);
  }

	public void setPrimaryKeyFieldName(String primaryKeyFieldName) {
		this.primaryKeyFieldName = primaryKeyFieldName;
	}

	public String getPrimaryKeyFieldName() {
		return primaryKeyFieldName;
	}
	
	public int indexOf(IRow row) {
		if (EasyObj.isValid(getPrimaryKeyFieldName())) {
			for (int i = 0, n = this.size(); i < n; i++) {
				if (this.getRow(i).get(getPrimaryKeyFieldName()).equals(row.get(getPrimaryKeyFieldName())))
					return i;
			}
			return -1;
		} else {
			return getRows().indexOf(row);
		}
	}
  @Override  public void afterLast(){
    cursor=rowList.size();
  }

  public void beforeFirst(){
    cursor=-1;
  }

  @Override public boolean first(){
  	cursor=0;
    return(isRange((cursor))); 
  }

  @Override public int getCursor(){
    if(isRange(cursor))
      return cursor;
    else
      return -1;
  }

  public boolean last(){
  	cursor=rowList.size()-1;
    return isRange(cursor);
  }

  @Override public boolean next(){
    return isRange(++cursor);
  }

  @Override public boolean absolute(int i){
  	cursor=i;
    return isRange(cursor);
  }

  @Override public IRow getCurrentRow(){
    return getRow(cursor);
  }

  @Override public boolean isAfterLast(){
    return cursor>=rowList.size();
  }

  @Override public boolean isBeforeFirst(){
    return cursor<0;
  }

  @Override public boolean isFirst(){
    return cursor==0;
  }

  @Override public boolean isLast(){
    return cursor==rowList.size()-1;
  }

  @Override public boolean previous(){
    return isRange(--cursor);
  }

  @Override public boolean relative(int offset){
    boolean result=isRange(cursor+offset);
    if(result) cursor+=offset;
    return result;
  }
  
  public int totalCount() {
  	return ((ABResultSet)_abrs).totalCount();
  }
  
}
