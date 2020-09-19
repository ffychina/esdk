/**
' * 与ARowSet不同在于ABRowSet实现了ICursor接口
 */
package com.esdk.sql.orm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.esdk.esdk;
import com.esdk.sql.RowExpression;
import com.esdk.sql.RowExpressions;
import com.esdk.utils.Constant;
/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
public class ABRowSet<T extends IRow> extends ARowSet<T> implements Cloneable{
	public ABRowSet(){
		this(16);
	}
	public ABRowSet(Class<T> rowCls){
		super(rowCls);
	}

	public ABRowSet(IRowSet rs){
		this(rs.getColumnNames());
		this.load(rs);
	}

	public ABRowSet(int initialCapacity){
		rowList=new ArrayList(initialCapacity);
		columns=new LinkedHashSet();
		cursor=-1;
	}

	public ABRowSet(String...columns){
		super(columns);
	}

	public ABRowSet(String[][] array){
		super(array);
	}

	public ABRowSet(Object[][] array){
		this();
		columns.addAll(Arrays.asList(array[0]));
		String[] cols=esdk.str.toArray(columns);
		for(int i=1;i<array.length;i++){
			add(new ABRow(cols,array[i]));
		}
	}

	public ABRowSet(JSONArray jsarray){
		super(jsarray);
	}

	public  <R extends ParentRow>ABRowSet (JSONArray jsarray,Class<R> parentRowCls){
		super(jsarray,parentRowCls);
	}

	public  <R extends ParentRow>ABRowSet (List beanList,Class<R> parentRowCls){
		super(beanList,parentRowCls);
	}

	public ABRowSet(Collection<Map> collMap){
		super(collMap);
	}

	public ABRowSet(File csvFile) throws IOException{
		super(csvFile);
	}

	public ABRowSet(InputStream is){
		super(esdk.str.fromCsv(esdk.str.isToStr(is)));
	}
	
	public ABRowSet(File csvFile,String charset) throws IOException{
		super(csvFile,charset);
	}

	public ABRowSet(File csvFile,String charset,boolean forceText) throws IOException{
		super(csvFile,charset,forceText);
	}

	public ABRowSet(ABResultSet rs){
		super(rs);
	}

	public ABRowSet(IRow[] rows){
		super(rows);
	}

	public ABRowSet(IResultSet rs,List<IRow> list){
		super(rs,list);
	}

	public ABRowSet(List<IRow> list){
		super(list);
	}

	public ABRowSet load(IRowSet rs){
		this.removeAll();
		return (ABRowSet)this.add(rs);
	}

	public ABRowSet load(List<IRow> list){
		rowList.addAll(list);
		return this;
	}

	public T append(){
		T result=createRow();
		add(result);
		last();
		return result;
	}

	public T insertAfter(int pos){
		T result=createRow();
		add(++pos,result);
		next();
		return result;
	}

	public T insert(int pos){
		T result=createRow();
		add(pos,result);
		next();
		return result;
	}

	@Override
	public ABRowSet<T> add(IRowSet rs){
		super.add(rs.getRows());// 注意:多个对象共用一个IRow
		return this;
	}
	
	public ABRowSet<T> add(ABRowSet rs){
		super.add(rs.getRows());// 注意:多个对象共用一个IRow
		return this;
	}
	

	public ABRowSet<T> distinct(String...fields){
		return (ABRowSet)RowSetUtils.distinct(this,fields);
	}

	/**把重复的记录删除。注意，返回的是已删除的记录集*/
	public ABRowSet<T> removeRepeat(String...fields){
		return (ABRowSet)RowSetUtils.removeRepeat(this,fields);
	}
	
	/**left join操作，类似于select的left join处理逻辑，适合处理1对1或1对N
	 * appendColumns：增加字段如果为空则获取记录集的所有字段。注意会排除重复字段，并且重复字段以原数据集为准，不会被新数据集更新。
	 * onFieldsMap：字段匹配条件，只能支持字段值匹配，不能指定常量值匹配，如有常量值条件应先执行过滤条件处理。
	 * 							key为原记录集的字段名，value为join记录集的字段名。
	 *              为null时会以两个记录集的重叠字段（去掉公用字段和主键）做为匹配条件。
	 * */
	public ABRowSet<T> join(IRowSet rs,String[] appendColumns,Map<String,String> onFieldsMap){
		if(rs==null || rs.size()==0)
			return this;
		if(esdk.obj.isBlank(appendColumns)) {
			appendColumns=rs.getColumnNames();
		}
		appendColumns=esdk.str.remove(appendColumns,this.getColumnNames());
		for(String column:appendColumns) {
			addColumn(column);
		}
		if(onFieldsMap==null || onFieldsMap.keySet().size()==0) {
			String[] joinFields=esdk.array.overlap(this.getColumnNames(),rs.getColumnNames());
			joinFields=esdk.str.remove(esdk.str.remove(joinFields,Constant.SystemFields),"id");
			onFieldsMap=new LinkedHashMap();
			for(String field:joinFields) {
				onFieldsMap.put(field,field);
			}
		}
		for(IRow row:this) {
			RowExpressions exps=new RowExpressions();
			for(Entry<String,String> entry:onFieldsMap.entrySet()) {
				exps.add(RowExpression.create(null,entry.getValue(),row.get(entry.getKey())));
			}
			IRow findRow=rs.filter(1,exps).getFirstRow();
			if(findRow!=null)
				if(row instanceof ParentRow)
					((ParentRow)row).load(findRow.toMap(false,appendColumns),false,true);
				else
					row.load(findRow.toMap(false,appendColumns));
		}
		return this;
	}
	
	/**
	 * @param appendColumns:多个增加的字段用逗号分隔。
	 * @param appendColumns:多个on字段用逗号分隔，支持相同的字段名，也可以支持用冒号分隔的字段名匹配（eg.: userName,userId:createUserId）
	 * */
	public ABRowSet<T> join(IRowSet rs,String appendColumns,String onColumns){
		String[] onColumnsArray=onColumns.split(",|;");
		String[] appendColumnsArray=esdk.str.split(appendColumns,",|;");
		LinkedHashMap<String,String> onFieldsMap=new LinkedHashMap<>(onColumnsArray.length);
		for(String column:onColumnsArray){
			if(!column.contains(":")) {
				onFieldsMap.put(column,column);
			}else {
				String[] keyVal=column.split(":");
				onFieldsMap.put(keyVal[0],keyVal[1]);
			}
		}
		return join(rs,appendColumnsArray,onFieldsMap);
	}
	
	public ABRowSet<T> join(IRowSet rs,String[] appendColumns,String... onColumns){
		LinkedHashMap<String,String> onFieldsMap=new LinkedHashMap<>(onColumns.length);
		for(String column:onColumns){
			onFieldsMap.put(column,column);
		}
		return join(rs,appendColumns,onFieldsMap);
	}
}
