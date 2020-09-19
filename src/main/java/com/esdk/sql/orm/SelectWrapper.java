package com.esdk.sql.orm;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.esdk.sql.Column;
import com.esdk.sql.Columns;
import com.esdk.sql.Field;
import com.esdk.sql.ILogic;
import com.esdk.sql.IRowMappper;
import com.esdk.sql.ISQL;
import com.esdk.sql.ISelect;
import com.esdk.sql.LogicFactory;
import com.esdk.sql.Select;
import com.esdk.sql.Table;
import com.esdk.sql.Where;
import com.esdk.sql.WhereFactory;
import com.esdk.utils.Constant;

abstract public class SelectWrapper<T extends ISelect,K extends IRow> implements ISelect{
	protected Select select;
	protected int useCacheSec;

	public Select getSelect(){
		parse();
		return select;
	}

	public SelectWrapper(Table table){
		select=new Select(table,null);
	}

	public SelectWrapper(Connection conn,boolean isTop){
		select=new Select(conn,isTop);
	}

	public SelectWrapper(Table table,Connection conn){
		select=new Select(table,conn);
	}

	public SelectWrapper(String tablename,Connection conn){
		select=new Select(tablename,conn);
	}

	public T addColumn(String columnname,String functionname){
		this.select.addColumn(columnname,functionname);
		return (T)this;
	}

	public T addColumnWithFunction(String columnname,String functionname){
		this.select.addColumn(columnname,functionname);
		return (T)this;
	}
	public T addColumn(String columnname,String functionname,String aliasName){
		this.select.addColumn(columnname,functionname,aliasName);
		return (T)this;
	}

	public T addColumn(String columnname){
		this.select.addColumn(columnname);
		return (T)this;
	}

	public T addColumn(ISQL select,String aliasName){
		this.select.addColumn(select,aliasName);
		return (T)this;
	}

	public T addColumn(Column column){
		this.select.addColumn(column);
		return (T)this;
	}

	public int queryForInt() throws SQLException{
		parse();
		return this.select.queryForInt();
	}

	public double queryForDouble() throws SQLException{
		parse();
		return this.select.queryForDouble();
	}

	public Object queryForObject() throws SQLException{
		parse();
		return this.select.queryForObject();
	}

	public T addColumns(Column...cols){
		select.addColumns(cols);
		return (T)this;
	}

	public T addColumnWithAlias(String columnname,String alias){
		this.select.addColumnWithAlias(columnname,alias);
		return (T)this;
	}

	public T addAllColumns(){
		this.select.addAllColumns();
		return (T)this;
	}

	public T addColumns(String...columnnames){
		this.select.addColumns(columnnames);
		return (T)this;
	}

	public T setColumns(String...columnnames){
		this.select.setColumns(columnnames);
		return (T)this;
	}

	public T setColumns(Column...cols){
		this.select.setColumns(cols);
		return (T)this;
	}

	public T addCondition(ILogic value){
		this.select.addCondition(value);
		return (T)this;
	}

	public T addConditions(ILogic... value){
		this.select.addCondition(LogicFactory.logicTree(value));
		return (T)this;
	}

	public T addCondition(String fieldName,String expression,Object value){
		this.select.addCondition(fieldName,expression,value);
		return (T)this;
	}

	public T addCondition(String value){
		this.select.addCondition(value);
		return (T)this;
	}

	public T removeCondition(String fieldName){
		this.select.removeCondition(fieldName);
		return (T)this;
	}
	
	public T clearCondition(){
		this.select.clearCondition();
		return (T)this;
	}
	
	public T clearOn(){
		this.select.clearOn();
		return (T)this;
	}
	
	public T addEq(String fieldName,Number value){
		this.select.eq(fieldName,value);
		return (T)this;
	}
	
	public T addEq(String fieldName,Object value){
		this.select.eq(fieldName,value);
		return (T)this;
	}

	public T addEq(String fieldName,boolean value){
		this.select.eq(fieldName,value);
		return (T)this;
	}

	public T addEq(String fieldName,String value){
		this.select.eq(fieldName,value);
		return (T)this;
	}

	public T eq(String fieldName,Object value){
		this.select.eq(fieldName,value);
		return (T)this;
	}

	public T eq(String fieldName,Number value){
		return addEq(fieldName,value);
	}

	public T eq(String fieldName,String value){
		this.select.eq(fieldName,value);
		return (T)this;
	}
	
	public T eq(String fieldName,Boolean value){
		this.select.eq(fieldName,value);
		return (T)this;
	}

	/**大于*/
	public T gt(String fieldName,Number value){
		this.select.gt(fieldName,value);
		return (T)this;
	}
	
	/**小于*/
	public T lt(String fieldName,Number value){
		this.select.lt(fieldName,value);
		return (T)this;
	}
	
	
	/**多个or的等于条件，注意输入时参数顺序和类型不要弄错，sql效果相当于"where name=abc or code=dog"*/
	public T orEq(Object...args){
		return orExpress(Where.EQ,args);
	}
	
	/**多个or的不等于条件，注意输入时参数顺序和类型不要弄错，sql效果相当于"where name!=abc or code!=dog"*/
	public T orNotEq(Object...args){
		return orExpress(Where.NOTEQUAL,args);
	}

	/**多个or的like条件，注意输入时参数顺序和类型不要弄错，sql效果相当于"where name like %abc% or code like dog%"*/
	public T orLike(Object...args){
		return orExpress(Where.LIKE,args);
	}

	/**支持多个or条件的条件表达式,注意输入时参数顺序和类型不要弄错*/
	public T orExpress(String expression,Object...args){
		select.orExpress(expression,args);
		return (T)this;
	}
	
	/**支持多个or条件的条件表达式,自动判断参数是内容或表达式*/
	public T or(Object...args){
		select.or(args);
		return (T)this;
	}
	
	public T notEq(String fieldName,String value){
		this.select.notEq(fieldName,value);
		return (T)this;
	}

	public T notEq(String fieldName,Number value){
		this.select.notEq(fieldName,value);
		return (T)this;
	}

	public T notEq(String fieldName,Boolean value){
		this.select.notEq(fieldName,value);
		return (T)this;
	}

	public T addNotEqualCondition(String fieldName,Object value){
		return (T)addNotEq(fieldName,value);
	}
	
	public T addNotEq(String fieldName,Object value){
		this.select.addNotEqualCondition(fieldName,value);
		return (T)this;
	}

	public T addNotEqualNull(String fieldName){
		this.select.addNotEqualNull(fieldName);
		return (T)this;
	}

	public T addEqualEmplyNumeric(String fieldName){
		this.select.addEqualEmplyNumeric(fieldName);
		return (T)this;
	}

	public T addEqualEmplyValue(String fieldName){
		this.select.addEqualEmplyValue(fieldName);
		return (T)this;
	}

	public T addEqualNumeric(String fieldName,String value){
		this.select.addEqualNumeric(fieldName,value);
		return (T)this;
	}

	public void addGroupBy(String fieldname){
		this.select.addGroupBy(fieldname);
	}

	public T setGroupBy(String...fieldNames){
		this.select.setGroupBy(fieldNames);
		return (T)this;
	}

	public T setHaving(String having) {
		this.select.setHaving(having);
		return (T)this;
	}

	public T addInCondition(String fieldName,ISelect value){
		return addIn(fieldName,value);
	}

	public T addInCondition(String fieldName,Number... values){
		return addIn(fieldName,values);
	}
	
	public T addIn(String fieldName,ISelect value){
		this.select.addInCondition(fieldName,value);
		return (T)this;
	}

	/**自动寻找select的输出字段，先找有没有这个字段，找不到就拿主键*/
	public T addIn(String fieldName,AbstractSelect select){
		if(select.getColumns().size()==0 || select.isOutputAllFields()) {
			select.setColumns(select.hasColumn(fieldName)?fieldName:select.getPrimaryKeyFieldName());
		}
		this.select.addInCondition(fieldName,select);
		return (T)this;
	}

	public T addIn(String fieldName,Number...value){
		this.select.addInCondition(fieldName,value);
		return (T)this;
	}
	
	public T addInCondition(String fieldName,String...value){
		return addIn(fieldName,value);
	}

	public T addIn(String fieldName,String...value){
		this.select.addInCondition(fieldName,value);
		return (T)this;
	}
	
	public T addInNumeric(String fieldName,String...value){
		this.select.addInNumeric(fieldName,value);
		return (T)this;
	}

	public T addLikeCondition(String fieldName,String value){
		return addLike(fieldName,value);
	}

	public T addLike(String fieldName,String value){
		this.select.addLikeCondition(fieldName,value);
		return (T)this;
	}
	
  /**多个字段、相同关键字的or条件的模糊查询，例如:where name like abc% or code like abc% */
	public T addLikeColumnsOr(String keyword,String... columnNames){
		this.select.addLikeOrConditions(keyword,columnNames);
		return (T)this;
	}

  /**一个字段、多个关键字的or条件的模糊查询，例如:where name like abc% or name like god% */
	public T addLikeValuesOr(String columnName,String... keywords){
		Object[] args=new Object[keywords.length*2];
		for(int i=0;i<keywords.length;i++) {
			args[i*2]=columnName;
			args[i*2+1]=keywords[i];
		}
		return orLike(args);
	}

  /**一个字段、多个关键字的and条件的模糊查询，例如:where name like %dog% and name like %cat% */
	public T addLikeValuesAnd(String columnName,String... keywords){
		for(String keyword:keywords) {
			this.addLikeCondition(columnName,keyword);
		}
		return (T)this;
	}

	public T addNotLikeCondition(String fieldName,String value){
		return addNotLike(fieldName,value);
	}

	public T addNotLike(String fieldName,String value){
		this.select.addNotLikeCondition(fieldName,value);
		return (T)this;
	}
	
	public T addNotEqualEmplyNumeric(String fieldName){
		this.select.addNotEqualEmplyNumeric(fieldName);
		return (T)this;
	}

	public T addNotEqualEmplyValue(String fieldName){
		this.select.addNotEqualEmplyValue(fieldName);
		return (T)this;
	}

	public T addNotInCondition(String fieldName,ISelect value){
		return (T)addNotIn(fieldName,value);
	}
	
	public T addNotIn(String fieldName,ISelect value){
		this.select.addNotInCondition(fieldName,value);
		return (T)this;
	}

	public T addNotIn(String fieldName,AbstractSelect select){
		if(select.getColumns().size()==0 || select.isOutputAllFields()) {
			select.setColumns(select.hasColumn(fieldName)?fieldName:select.getPrimaryKeyFieldName());
		}
		this.select.addNotInCondition(fieldName,select);
		return (T)this;
	}

	public T addNotInCondition(String fieldName,String...value){
		return (T)addNotIn(fieldName,value);
	}

	public T addNotIn(String fieldName,String... value){
		this.select.addNotInCondition(fieldName,value);
		return (T)this;
	}
	
	public T addNotInCondition(String fieldName,Number...value){
		return (T)addNotIn(fieldName,value);
	}	
	public T addNotIn(String fieldName,Number...value){
		this.select.addNotInCondition(fieldName,value);
		return (T)this;
	}

	public T addOnCondition(Field leftfield,Field rightfield){
		this.select.addOnCondition(leftfield,rightfield);
		return (T)this;
	}

	public T addOnCondition(Field field,Object value){
		this.select.addOnCondition(field,value);
		return (T)this;
	}

	public T addOnCondition(ILogic condition){
		this.select.addOnCondition(condition);
		return (T)this;
	}

	public T addOrderBy(String fieldname,boolean isDesc){
		this.select.addOrderBy(fieldname,isDesc);
		return (T)this;
	}

	public T addOrderBy(String fieldname){
		this.select.addOrderBy(fieldname);
		return (T)this;
	}

	public T addOrderBy(String... fieldname){
		this.select.addOrderBy(fieldname);
		return (T)this;
	}

	public T setOrderBy(String fieldname,boolean isDesc){
		this.select.setOrderBy(fieldname,isDesc);
		return (T)this;
	}
	
	public T setOrderBy(String... fieldname){
		this.select.setOrderBy(fieldname);
		return (T)this;
	}
	
	public T setOrderBy(String fieldname){
		this.select.setOrderBy(fieldname);
		return (T)this;
	}
	
	public void clear(){
		Table t=getTable();
		this.select.clear();
		this.select.setTable(t);
	}

	public Field createField(String fieldname){
		return this.select.createField(fieldname);
	}

	public Column createColumn(String fieldname,String aliasName){
		return this.select.createColumn(fieldname,aliasName);
	}

	public Column createColumn(String columnName){
		return this.select.createColumn(columnName);
	}
	
	public Connection getConnection(){
		return this.select.getConnection();
	}

	public Columns getColumns(){
		return this.select.getColumns();
	}
	
	public String getSQL(){
		parse();
		return select.getSQL();
	}

	public Table getTable(){
		return this.select.getTable();
	}

	public int getTop(){
		return this.select.getTop();
	}

	public boolean isDistinct(){
		return this.select.isDistinct();
	}

	abstract public void parse();

	public boolean perform() throws SQLException{
		return this.select.perform();
	}

	public void setConnection(Connection connection){
		this.select.setConnection(connection);
	}

	public T setDistinct(boolean isdistinct){
		this.select.setDistinct(isdistinct);
		return (T)this;
	}

	public T setTableName(String tablename,String alias,String jointype){
		this.select.setTableName(tablename,alias,jointype);
		return (T)this;
	}

	public T setTableName(String tablename,String alias){
		this.select.setTableName(tablename,alias);
		return (T)this;
	}

	public T setTableName(String tablename){
		this.select.setTableName(tablename);
		return (T)this;
	}

	public T setTop(int number){
		this.select.setTop(number);
		return (T)this;
	}

	public T setOffset(int value) {
		this.select.setOffset(value);
		return (T)this;
	}

	public T setRowsLimit(int value) {
		this.select.setRowsLimit(value);
		return (T)this;
	}
	
	public T setRowsOffset(int value) {
		this.select.setRowsOffset(value);
		return (T)this;
	}

	public String[][] toArray() throws SQLException{
		parse();
		return this.select.toArray();
	}

	public ResultSet toResultSet() throws SQLException{
		parse();
		return this.select.toResultSet();
	}

	public ABResultSet toABResultSet() throws SQLException{
		return new ABResultSet(toResultSet());
	}

	public List toList(IRowMappper mapper) throws SQLException{
		parse();
		return select.toList(mapper);
	}

	public List toList(IRowMappper mapper,int start,int limit) throws SQLException{
		parse();
		return select.toList(mapper,start,limit);
	}

	public List toList(Class cls) throws SQLException{
		parse();
		return select.toList(cls);
	}

	public List toList(Class cls,int start,int limit) throws SQLException{
		parse();
		return select.toList(cls,start,limit);
	}

	public JSONArray toJsonArray() throws SQLException{
		parse();
		return select.toABResultSet().toJsonArray();
	}

	public JSONArray toJsonArray(boolean isFormatJavaBeanName) throws SQLException{
		parse();
		return select.toABResultSet().toJsonArray(isFormatJavaBeanName);
	}

	public String toString(){
		return getSQL();
	}

	public String getTableAliasName(){
		return select.getTableAliasName();
	}

	public int count() throws SQLException{
		parse();
		return select.count();
	}

	public T setTableAliasName(String alias){
		select.setTableAliasName(alias);
		return (T)this;
	}

	public T addBetween(String fieldName,Number minvalue,Number maxvalue){
		select.addBetween(fieldName,minvalue,maxvalue);
		return (T)this;
	}

	public T addBetween(String fieldName,Date minvalue,Date maxvalue){
		select.addBetween(fieldName,minvalue,maxvalue);
		return (T)this;
	}

	public T setJoinType(String join){
		select.setJoinType(join);
		return (T)this;
	}
	
	public T addEqualNull(String fieldName){
		this.select.addEqualNull(fieldName);
		return (T)this;
	}
	
	public T showSql(boolean b){
		this.select.showSql(b);
		return (T)this;
	}

	public T useCache(int sec) {
		this.select.useCache(sec);
		useCacheSec=sec;
		return (T)this;
	}
	
	public T useCache(boolean ifCachiable) {
		return useCache(ifCachiable?Constant.DefaultCacheSec:Constant.ResetUseCacheSec);
	}
	
}
