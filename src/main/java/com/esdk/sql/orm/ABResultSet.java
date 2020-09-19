
package com.esdk.sql.orm;

import java.io.Closeable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.alibaba.fastjson.JSONArray;
import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;
import com.esdk.sql.Expression;
import com.esdk.sql.IExpression;
import com.esdk.sql.ISelect;
import com.esdk.sql.OrderBy;
import com.esdk.sql.RowExpressions;
import com.esdk.sql.SQLRuntimeException;
import com.esdk.utils.Constant;
import com.esdk.utils.EasyObj;
import com.esdk.utils.EasyQuery;
import com.esdk.utils.EasySql;
import com.esdk.utils.EasyStr;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;
/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
public class ABResultSet<T extends IRow> implements Iterable<T>,IResultSetCursor,Closeable,IResultSet<T>,Serializable{
	protected transient ResultSet rs;
	protected LinkedList<T> rowList;
	protected transient ResultSetMetaData rsmd;
	protected int cursorPos=-1;
	private boolean isCachedAllRow=false;
	protected Map mapColumnClass;
	protected String[] columns;
	private boolean _isClosed;
	transient private ISelect select;
	private transient int totalCount;

	public ABResultSet(ResultSet resultset){
		rs=resultset;
		rowList=new LinkedList();
	}

	public ABResultSet(JSONArray ja){
		if(ja.size()>0)
			columns=ja.getJSONObject(0).keySet().toArray(new String[0]);
		rowList=new LinkedList();
		for(int i=0;i<ja.size();i++){
			new ABRow(ja.getJSONObject(i));
		}
	}

  public List<ABRowSet<IRow>> group(String... fields) {
  	return RowSetUtils.group(new ABRowSet(this),fields);
  }
  
	protected ResultSet resultSet(){
		return rs;
	}

	public void disConnect() throws SQLException{
		if(!rs.isClosed())
			last();
	}

	protected List<T> getSubList(int start,int limit) throws SQLException{
		int begin=start-1;
		if(cursorPos>begin)
			cursorPos=begin;
		else
			while(cursorPos<begin&&next()){}
		if(cursorPos==begin){
			for(int i=start,n=start+limit;i<n&&next();i++){}
			List<T> subRowList=rowList.subList(start,Math.min(start+limit,rowList.size()));
			close();
			return subRowList;
		}
		return rowList.subList(0,0);
	}

	public ABRowSet<T> subRowSet(int start,int limit){
		try{
			List<T> subList=getSubList(start,limit);
			return subList==null?null:new ABRowSet(subList);
		}catch(SQLException e){
			throw new SQLRuntimeException(e);
		}
	}

	/**使用lambda表达式过滤*/
	public ABRowSet<T> filter(Predicate<T> fn){
		return filter(0,fn);
	}
	
	public ABRowSet<T> filter(int top,Predicate<T> fn){
		ABRowSet result=new ABRowSet(this.getColumnNames());
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

	public ABRowSet<T> filter(IRowFilter filter){
		return RowSetUtils.filter(new ABRowSet(this),filter);
	}

	@Override
	public ABRowSet<T> filter(IExpression exp){
		return RowSetUtils.filter(new ABRowSet(this),new RowFilter(exp));
	}

	@Override
	public ABRowSet<T> filter(int top,IExpression exp){
		return RowSetUtils.filter(new ABRowSet(this),new RowFilter(exp),top);
	}

	public ABRowSet<T> filter(String expr){
		return filter(0,expr);
	}

	public ABRowSet<T> filter(int top,String expr){
		try{
			ABRowSet<T> result=new ABRowSet(this,new EasyQuery(this.getAllRows()).filter(expr,top).toList());
			return result;
		}catch(Exception e){
			throw new SQLRuntimeException(e);
		}
	}

	public ABRowSet<T> filter(String fieldName,Object equalValue){
		return filter(RowFilter.create(fieldName,equalValue));
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

	public ABRowSet<T> filter(int top,String fieldName,Object equalValue){
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
			}else if(esdk.array.contains(Expression.All,arg)) {
				expression=(String)arg;
			}else {
				rightValue=arg;
				isFieldValue=true;
				expressions.add(fieldValue,expression,rightValue);
				expression=Expression.EQ;
			}
		}
		return filter(expressions);
	}
	
	/**可以一次输入多个and的条件，并支持所有的表达式。eg.: filter("name","张三",code,<>,test,"age","<",18)*/
	public ABRowSet<T> filters(int top,String firstField,Object... args){
		args=esdk.array.concat(firstField,args);
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
	
	public ABRowSet<T> filter(String fieldName,String expression,Object equalValue){
		return filter(RowFilter.create(fieldName,expression,equalValue));
	}

	public ABRowSet<T> filter(int top,String fieldName,String expression,Object equalValue){
		return filter(RowFilter.create(fieldName,expression,equalValue).setTop(top));
	}

	public List<Map> toMapList(){
		ArrayList result=new ArrayList(this.size());
		for(int i=0;i<rowList.size();i++){
			IRow row=(IRow)rowList.get(i);
			result.add(row.record());
		}
		return result;
	}

	public List<T> toRowList(){
		this.size();
		return (List<T>)rowList;
	}

	public List<Map> toMapList(boolean isFormatJavaBeanName){
		if(!isFormatJavaBeanName)
			return toMapList();
		ArrayList result=new ArrayList(this.size());
		for(int i=0;i<rowList.size();i++){
			IRow row=(IRow)rowList.get(i);
			result.add(esdk.map.toCamelCaseMap(row.record()));
		}
		return result;
	}

	public List<Map> toMapList(boolean isFormatJavaBeanName,String...columns){
		if(!isFormatJavaBeanName)
			return toMapList();
		ArrayList result=new ArrayList(this.size());
		for(int i=0;i<rowList.size();i++){
			IRow row=(IRow)rowList.get(i);
			result.add(row.toMap(isFormatJavaBeanName,columns));
		}
		return result;
	}

  /**默认输出驼峰格式，不输出扩展字段*/
	@Override
	public JSONArray toJsonArray(){
		return toJsonArray(true);
	}

	@Override
	public JSONArray toJsonArray(boolean isFormatJavaBeanName){
		return RowSetUtils.toJsonArray(this.toMapList(isFormatJavaBeanName),false);
	}
	
	@Override
  public JSONArray toJsonArray(boolean isFormatJavaBeanName,boolean isOutputExtraColumns){
		return RowSetUtils.toJsonArray(this.toMapList(isFormatJavaBeanName,isOutputExtraColumns?Constant.EmptyStrArr:columns),false);
  }

	@Override
  public JSONArray toJsonArray(boolean isFormatJavaBeanName,String... columns){
		return RowSetUtils.toJsonArray(this.toMapList(isFormatJavaBeanName,columns),false);
  }

	@Override
	public String toString(){
    StringBuilder result=new StringBuilder();
    int[] index=new int[]{0};
    this.rowList.forEach(e-> {
      result.append((index[0]++)+"_"+e.getClass().getSimpleName()+e.toString()+"\n");
    });
    if(result.length()>0)
      result.delete(result.length()-1,result.length());
    return result.toString();
	}

	public JSONArray toJsonArray(int start,int limit){
		try{
			List<T> sub=this.getSubList(start,limit);
			JSONArray result=new JSONArray(sub.size());
			for(int i=0;i<sub.size();i++){
				result.add(sub.get(i).toJsonObject());
			}
			return result;
		}catch(SQLException e){
			throw new SQLRuntimeException(e);
		}
	}

	void addRowList(IRow currentRow) throws SQLException{
		rowList.add((T)currentRow);
	}

	public boolean next() throws SQLException{
		if(!isCachedAllRow&&rowList.size()==cursorPos+1){
			if(rs.next()){
				cursorPos++;
				if(cursorPos==rowList.size()){
					addRowList(getCurrent());
				}
			}else{
				isCachedAllRow=true;
				close();
				return false;
			}
		}else{
			if(cursorPos<rowList.size())
				cursorPos++;
		}
		return isRange();
	}

	protected IRow getCurrent() throws SQLException{
		return new ABRow(getCurrentMap());
	}

	public boolean first() throws SQLException{
		if(rowList.size()==0)
			next();
		if(rowList.size()>0){
			cursorPos=0;
		}
		return isRange();
	}

	public boolean last() throws SQLException{
		while(next()){
		}
		if(rowList.size()>0){
			cursorPos=rowList.size()-1;
			return true;
		}
		return false;
	}

	public int size(){
		return getRowSize();
	}

	private int getRowSize(){
		int mem=cursorPos;
		int result=0;
		try{
			if(last())
				result=rowList.size();
			cursorPos=mem;
			return result;
		}catch(SQLException e){
			e.printStackTrace();
			cursorPos=mem;
			return result;
		}
	}

	public boolean previous(){
		if(cursorPos>=0)
			cursorPos--;
		return isRange();
	}

	private boolean isRange(){
		return isRange(cursorPos);
	}

	private boolean isRange(int row){
		return isRange(rowList,row);
	}

	static boolean isRange(List list,int row){
		return row>=0&&row<list.size();
	}

	protected void checkRange(int i){
		if(!isRange(i))
			throw new IndexOutOfBoundsException("index is"+i+" but size is "+rowList.size());
	}

	public boolean absolute(int position) throws SQLException{
		if(cursorPos>=position) {
			cursorPos=position;
			return true;
		}else {
			while(cursorPos < position && !isCachedAllRow) {
				next();
			}
		}
		if(isRange(position)){
			cursorPos=position;
			return true;
		}else
			return false;
	}

	public void beforeFirst(){
		cursorPos=-1;
	}

	public void afterLast() throws SQLException{
		if(last()){
			cursorPos++;
		}
	}

	public boolean isAfterLast() throws SQLException{
		return cursorPos>=rowList.size();
	}

	public boolean isBeforeFirst() throws SQLException{
		return cursorPos<0;
	}

	public int getCursor(){
		return cursorPos;
	}

	public Object gainCurrentRow() throws SQLException{
		return rowList.get(cursorPos);
	}

	public IRow getCurrentRow() throws SQLException{
		return (IRow)gainCurrentRow();
	}

	protected LinkedHashMap getCurrentMap() throws SQLException{
		rsmd=gainMetaData();
		LinkedHashMap result=new LinkedHashMap(rsmd.getColumnCount());
		for(int i=1;i<=rsmd.getColumnCount();i++){
			result.put(rsmd.getColumnLabel(i),RowUtils.getCurrentValue(rs,rsmd,i));
		}
		return result;
	}

	protected Object[] getCurrentArray() throws SQLException{
		rsmd=gainMetaData();
		Object[] result=new Object[rsmd.getColumnCount()];
		for(int i=1,n=rsmd.getColumnCount()+i;i<n;i++){
			result[i-1]=RowUtils.getCurrentValue(rs,rsmd,i);
		}
		return result;
	}

	public T getFirstRow(){
		return (T)getRow(0);
	}

	public T getFirstRow(boolean isCreateInstance){
		T result=(T)getRow(0);
		if(result==null&&isCreateInstance){
			result=(T)new ABRow(this.columns);
		}
		return result;
	}

	public T getLastRow(){
		return (T)getRow(this.size()-1);
	}

	public T getRow(){
		return (T)getRow(getCursor());
	}
	
	public IRow getRow(int index){
		try{
			for(int i=-1;next()&&i<index;i++){
				esdk.tool.emptyFn();
			}
			return new ABRow(this.rowList.get(index));
		}catch(SQLException e){
			throw new SQLRuntimeException(e);
		}
	}

	protected ResultSetMetaData gainMetaData() throws SQLException{
		if(rsmd==null){
			rsmd=rs.getMetaData();
			mapColumnClass=getMapColumnClass();
		}
		return rsmd;
	}

	Map getMapColumnClass() throws SQLException{
		if(mapColumnClass==null){
			ResultSetMetaData rsmdata=gainMetaData();
			mapColumnClass=new LinkedHashMap(rsmdata.getColumnCount());
			try{
				for(int i=1,n=rsmdata.getColumnCount()+i;i<n;i++){
					mapColumnClass.put(rsmdata.getColumnLabel(i),Class.forName(rsmdata.getColumnClassName(i)));
				}
			}catch(ClassNotFoundException e){
				throw new SQLException(e);
			}
		}
		return mapColumnClass;
	}

	protected boolean findAutoIncrement(String column) throws SQLException{
		ResultSetMetaData md=gainMetaData();
		for(int i=1,n=md.getColumnCount()+i;i<n;i++){
			if(md.getColumnLabel(i).equalsIgnoreCase(column))
				return md.isAutoIncrement(i);
		}
		throw new SQLException("can not find Column:"+column);
	}

	public String[] getColumns() throws SQLException{
		if(columns==null){
			gainMetaData();
			columns=new String[rsmd.getColumnCount()];
			/* gainMetaData(); */
			for(int i=0,n=rsmd.getColumnCount();i<n;i++){
				columns[i]=rsmd.getColumnLabel(i+1);
			}
		}
		return columns;
	}

	protected List<T> gainAllRow() throws SQLException{
		int currentpos=cursorPos;
		columns=getColumns();
		last();
		cursorPos=currentpos;
		return rowList;
	}

	public boolean hasColumnName(String name){
		return EasyStr.existOf(columns,name);
	}

	public List<T> getAllRows() throws SQLException{
		return gainAllRow();
	}

	public void close(){
		try{
			if(_isClosed && rs.isClosed())
				return;
			else {
				getColumns();
				EasySql.close(rs);
				_isClosed=true;
			}
		}catch(SQLException e){
			throw new SdkRuntimeException(e);
		}
	}

	@Override
	public void finalize(){
		if(_isClosed)
			return;
		try{
			close();
		}catch(Exception e){
			throw new SQLRuntimeException(e);
		}
	}

	public List<T> getRowList() throws SQLException{
		last();
		return (List)rowList;
	}

	public int findColumn(String columnLabel) throws SQLException{
		return rs.findColumn(columnLabel);
	}

	public Statement getStatement() throws SQLException{
		return rs.getStatement();
	}

	public int getResultSetType() throws SQLException{
		return rs.getType();
	}

	public boolean isEmpty() throws SQLException{
		int temp=getCursor();
		boolean res=!first();
		cursorPos=temp;
		return res;
	}
	
	public boolean isFirst() throws SQLException{
		return cursorPos==0;
	}

	public boolean isLast() throws SQLException{
		return cursorPos==rowList.size()-1;
	}

	public void refreshRow() throws SQLException{
		rs.refreshRow();
	}

	public boolean relative(int offset) throws SQLException{
		if(offset==0)
			return isRange();
		if(isCachedAllRow){
			if(isRange(cursorPos+offset)){
				cursorPos+=offset;
				return true;
			}else
				return false;
		}else{
			if(offset>=0){
				for(int i=cursorPos;i<cursorPos+offset;i++){
					if(!next()){
						return false;
					}
				}
			}else{
				for(int i=cursorPos;i>cursorPos+offset;i--){
					if(!previous()){
						return false;
					}
				}
			}
		}
		return true;
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
  
	public String toCsv() throws SQLException{
		return toCsv(getColumns());
	}

	public String toCsv(boolean trim) throws SQLException{
		return toCsv(trim,getColumns());
	}

	public String toCsv(String...colums) throws SQLException{
		return toCsv(false,colums);
	}

	public String toCsv(boolean trim,String...colums) throws SQLException{
		return EasyStr.toCsv(toArray(trim,colums));
	}

	public List<List> toList(String...cols) throws SQLException{
		if(cols.length==0)
			cols=this.columns;
		List<List> result=new ArrayList<List>();
		List rowList=gainAllRow();
		for(Iterator iter=rowList.iterator();iter.hasNext();){
			IRow row=(IRow)iter.next();
			result.add(row.toList(cols));
		}
		return result;
	}
	
	public String[][] toArray(String...cols) throws SQLException{
		return toArray(false,cols);
	}

	public String[][] toArray(boolean trim,String...cols) throws SQLException{
		if(cols.length==0)
			cols=this.columns;
		List rowList=gainAllRow();
		List<String[]> result=new ArrayList<String[]>(rowList.size()+1);
		result.add(cols);
		int size=rowList.size();
		for(int i=0;i<size;i++){
			IRow row=(IRow)rowList.get(i);
			ArrayList<String> list=new ArrayList(cols.length);
			Map map=row.record();
			for(int j=0;j<cols.length;j++){
				if(trim)
					list.add(EasyStr.getStringNoNull(map.get(cols[j])).trim());
				else
					list.add(EasyStr.getStringNoNull(map.get(cols[j])));
			}
			result.add((String[])list.toArray(new String[0]));
		}
		return EasyStr.listToArr(result);
	}
	
	public Iterator<T> iterator(){
		/*getRowSize();
		return (Iterator<T>)rowList.iterator();*/
		return new ResultsetIterator();
	}

	public Object[] getObjects(String columnName){
		Object[] result=new Object[size()];
		for(int i=0,n=rowList.size();i<n;i++){
			result[i]=((IRow)rowList.get(i)).get(columnName);
		}
		return result;
	}

	public Integer[] getIntegers(String columnName){
		Integer[] result=new Integer[size()];
		for(int i=0,n=rowList.size();i<n;i++){
			result[i]=(Integer)((IRow)rowList.get(i)).get(columnName);
		}
		return result;
	}

	public Long[] getLongs(String columnName){
		Long[] result=new Long[size()];
		for(int i=0,n=rowList.size();i<n;i++){
			result[i]=(Long)((IRow)rowList.get(i)).get(columnName);
		}
		return result;
	}
		
	/**使用lambda计算数量*/
  public int reduceInt(ToIntFunction<T> fn) {
		int result=0;
		for(T row:this){
			result+=fn.applyAsInt(row);
		}
		return result;
	}
	
	/**使用lambda计算数量*/
	public long reduceLong(ToLongFunction<T> fn) {
		long result=0;
		for(T row:this){
			result+=fn.applyAsLong(row);
		}
		return result;
	}
	
  /**使用lambda计算数量*/
	public double reduce(ToDoubleFunction<T> fn) {
		Double result=0D;
		for(T row:this){
			result+=fn.applyAsDouble(row);
		}
		return result;
	}

	/**使用lambda表达式过滤*/
  public Long[] getLongs(Function<T,Long> fn) {
  	Long[] result=new Long[this.size()];
  	int i=0;
  	for(T row:this){
			result[i++]=fn.apply(row);
		}
  	return result;
  }
  
	public Double[] getDoubles(String columnName){
		Double[] result=new Double[size()];
		for(int i=0,n=rowList.size();i<n;i++){
			result[i]=(Double)((IRow)rowList.get(i)).get(columnName);
		}
		return result;
	}

	public Number[] getNumbers(String columnName){
		Number[] result=new Number[size()];
		for(int i=0,n=rowList.size();i<n;i++){
			result[i]=(Number)((IRow)rowList.get(i)).get(columnName);
		}
		return result;
	}
	
	/**使用lambda表达式修改记录内容*/
	public ABResultSet<T> update(Consumer<T> fn) throws Exception {
		for(T row:this) {
			fn.accept(row);
		}
		return this;
	}
	
	/**使用lambda表达式加工记录内容返回数组*/
	public String[] getStrings(String columnName){
		String[] result=new String[size()];
		for(int i=0,n=rowList.size();i<n;i++){
			result[i]=EasyStr.getStringNoNull(((IRow)rowList.get(i)).get(columnName));
		}
		return result;
	}
	
  public String[] getStrings(Function<T,String> fn) {
  	String[] result=new String[size()];
  	int i=0;
  	for(T row:this){
			result[i++]=fn.apply(row);
		}
  	return result;
  }
  
	@Override
	public double sum(String...columns){
		double result=0;
		for(int i=0,n=size();i<n;i++){
			IRow row=(IRow)rowList.get(i);
			for(int j=0;j<columns.length;j++){
				Number num=(Number)(row).get(columns[j]);
				if(num!=null)
					result+=num.doubleValue();
			}
		}
		return result;
	}

	@Override
	public ABResultSet sort(String...columnKeys){
		return sort(false,columnKeys);
	}

	@Override
	public ABResultSet sort(final boolean isDesc,final String...cols){
		if(size()>0){
			IRow currentRow=null;
			if(isRange(cursorPos))
				currentRow=(IRow)rowList.get(cursorPos);
			Collections.sort(this.rowList,new Comparator(){
				@Override
				public int compare(Object o1,Object o2){
					IRow i1=(IRow)o1;
					IRow i2=(IRow)o2;
					int result=0;
					for(int i=0;i<cols.length&&result==0;i++){
						if(isDesc){
							result=EasyObj.compareTo(i2.get(cols[i]),i1.get(cols[i]));
							if(result>0)
								break;
						}else{
							result=EasyObj.compareTo(i1.get(cols[i]),i2.get(cols[i]));
							if(result<0)
								break;
						}
					}
					return result;
				}
			});
			cursorPos=rowList.indexOf(currentRow);
		}
		return this;
	}

	public ABResultSet sort(final OrderBy...cols){
		size();
		IRow currentRow=null;
		if(isRange(cursorPos))
			currentRow=(IRow)rowList.get(cursorPos);
		Collections.sort(this.rowList,new Comparator(){
			@Override
			public int compare(Object o1,Object o2){
				IRow i1=(IRow)o1;
				IRow i2=(IRow)o2;
				int result=0;
				for(int i=0;i<cols.length&&result==0;i++){
					if(cols[i].isDescent())
						result=EasyObj.compareTo(i2.get(cols[i].getField().getName()),i1.get(cols[i].getField().getName()));
					else
						result=EasyObj.compareTo(i1.get(cols[i].getField().getName()),i2.get(cols[i].getField().getName()));
				}
				return result;
			}
		});
		cursorPos=rowList.indexOf(currentRow);
		return this;
	}

	@Override
	public String[] getColumnNames(){
		try{
			return getColumns();
		}catch(SQLException e){
			throw new SQLRuntimeException(e);
		}
	}

	public ABRowSet distinct(String...fields){
		return (ABRowSet)RowSetUtils.distinct(new ABRowSet(this),fields);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException{
		ABResultSet result=new ABResultSet(this.rs);
		result.rowList=this.rowList;
		result.rsmd=this.rsmd;
		result.isCachedAllRow=this.isCachedAllRow;
		result.mapColumnClass=this.mapColumnClass;
		result.columns=this.columns;
		result._isClosed=this._isClosed;
		return result;
	}

	private class ResultsetIterator implements Iterator<T>{
		int cursor=-1;

		public boolean hasNext(){
			if(!isCachedAllRow && rowList.size()<=cursor+1)
				try{
					ABResultSet.this.next();
				}catch(SQLException e){
					throw new SQLRuntimeException(e);
				}
			return isRange(cursor+1);
		}

		public T next(){
			if(!isCachedAllRow&&rowList.size()==cursor+1){
				try{
					if(rs.next()){
						cursor++;
						if(cursor==rowList.size()){
							addRowList(getRow(cursor));
						}
					}else{
						isCachedAllRow=true;
						close();
						return (T)getRow(cursor);
					}
				}catch(Exception e){
					throw new SQLRuntimeException(e);
				}
			}else{
				if(cursor<rowList.size())
					cursor++;
			}
			return (T)getRow(cursor);
		}

		public void remove(){
			System.err.println("暂不实现Remove功能");
		}
	}

	protected void setSelect(ISelect select){
		this.select = select;
	}
	
	/**执行select.count返回总记录数*/
	public int totalCount(){
		if(totalCount>0)
			return totalCount;
		try{
			return select.count();
		}catch(SQLException e){
			throw new SQLRuntimeException(e);
		}
	}

}
