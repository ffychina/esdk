package com.esdk.sql;

import com.alibaba.fastjson.JSONArray;
import com.esdk.exception.SdkRuntimeException;
import com.esdk.sql.orm.*;
import com.esdk.utils.*;

import java.sql.*;
import java.util.Date;
import java.util.*;
/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
public class JDBCTemplate implements ISQL,ISelect{
  private String _sql;
  private Connection _conn;
  private ResultSet _rs;
  private Object[] _parameters=new Object[0];
  private String querySQL;
  private int updatedCount;
  private boolean _noParameters=false;
	private Number generatedKey;
	private boolean isAutoIncrement;

  public JDBCTemplate(String sql,Object[] parameters,Connection con){
    _sql=sql;
    setConnection(con);
    _parameters=parameters;
  }

  public JDBCTemplate(Connection conn,String sql) {
  	setConnection(conn);
  	setSql(sql);
   }

  public JDBCTemplate(Connection conn,boolean noParams,String sql) {
  	setConnection(conn);
  	this._noParameters=noParams;
  	setSql(sql);
   }

  /**参数能正确处理in条件，Object可以为数组、Collection和Literal对象，eg.new Object[new Integer[],int,String]*/
  public JDBCTemplate(Connection con,String sql,Object... parameters){
  	this(sql,parameters,con);
  }
  
  /**参数能正确处理in条件，Object可以为数组、Collection和Literal对象，eg.new Object[new Integer[],int,String]*/
  public JDBCTemplate(String sql,Object... parameters){
  	this(sql,parameters,null);
  }
  
  public JDBCTemplate(String sql,Collection parameters,Connection con){
  	this(sql,parameters.toArray(),con);
  }
  
  public JDBCTemplate setSql(String sql) {
  	_sql=sql;
  	if(!this._noParameters){
	  	Object[] newParams=new Object[EasyStr.GetSubStringCount(_sql,"?")];
	  	System.arraycopy(_parameters,0,newParams,0,_parameters.length);
	  	_parameters=newParams;
  	}
  	return this;
  }
  public JDBCTemplate clearParameters(){
  	_parameters=new Object[0]; 
  	return this;
  }

  /**能正确处理in条件，Object可以为数组、Collection和Literal*/
  public JDBCTemplate setParameter(int position,Object value) {
  	this._parameters[position]=value;
  	return this;
  }
  
  public JDBCTemplate setParameters(Object... values) {
  	for(int i=0;i<values.length;i++) {
  		this.setParameter(i,values[i]);
  	}
  	return this;
  }
  public int queryForInt() throws SQLException{
  	ResultSet rs=toResultSet();
  	int result=0;
  	if(rs.next())
  		result=rs.getInt(1);
  	else
  		throw new SQLException("Resultset is empty");
  	EasySql.close(rs);
  	return result;
  }

  public double queryForDouble() throws SQLException{
  	ResultSet rs=toResultSet();
  	double result=0;
  	if(rs.next())
  		result=rs.getDouble(1);
  	else
  		throw new SQLException("Resultset is empty");
  	EasySql.close(rs);
  	return result;
  }
  
  public String queryForString() throws SQLException{
  	ResultSet rs=toResultSet();
  	String result=null;
  	if(rs.next())
  		result=rs.getString(1);
  	else
  		throw new SQLException("Resultset is empty");
  	EasySql.close(rs);
  	return result;
  }
  
  public Object queryForObject() throws SQLException{
  	ResultSet rs=toResultSet();
  	Object result=null;
  	if(rs.next())
  		result=rs.getDouble(1);
  	else
  		throw new SQLException("Resultset is empty");
  	EasySql.close(rs);
  	return result;
  }

  public String[][] toArray(boolean isOutputColumn) throws SQLException{
    ResultSet rs=toResultSet();
    String [][] result=isOutputColumn?EasySql.resultSetToArrWithHeader(rs):EasySql.ResultSetToArr(rs);
    EasySql.close(rs);
    return result;
  }
  
  public String[][] toArray() throws SQLException{
  	return toArray(true);
  }

  public List<Object[]> toList() throws SQLException{
  	return EasySql.toList(toResultSet());
  }

  public List<List> toLists() throws SQLException{
  	return EasySql.toLists(toResultSet());
  }

  public List getFirstList() throws SQLException{
  	ResultSet rs=toResultSet();
    ResultSetMetaData rsmd=rs.getMetaData();
    int FieldsCount=rsmd.getColumnCount();
    LinkedList result=new LinkedList();
    if(rs.next()){
      for(int i=0;i<FieldsCount;i++){
      	result.add(rs.getObject((i+1)));
      }
    }
    EasySql.close(rs);
    return result;
  }

  public ABRow getFirstRow() throws SQLException{
  	ABResultSet abrs=toABResultSet();
  	if(abrs.next()) {
  		abrs.close();
  		return (ABRow)abrs.getCurrentRow();
  	}
  	else
  		return null;
  }
  
  public List<Object[]> toList(String...columns) throws SQLException{
  	return EasySql.toList(toResultSet(),columns);
  }

  public List<Object> toList(String column) throws SQLException{
  	return EasySql.toList(toResultSet(),column);
  }

  public ABResultSet<IRow> toABResultSet() throws SQLException{
    perform();
    return new ABResultSet(_rs);
  }
  
	@Override public IRowSet toRowSet() throws SQLException{
		return toABRowSet();
	}

  public <RS extends ParentResultSet> RS toParentResultSet(Class<RS> resultsetClass) throws SQLException{
  	parse();
		try{
			java.lang.reflect.Constructor constructor=resultsetClass.getConstructor(new Class[]{ResultSet.class});
			ParentResultSet result=(ParentResultSet)constructor.newInstance(new Object[]{toResultSet()});
			return (RS)result;
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
  }

  public <R extends ParentRow> ABRowSet<R> toRowSet(Class<R> parentRowCls) throws Exception{
  	Class resultsetCls=Class.forName(parentRowCls.getName().replaceAll("Row$","ResultSet"));
  	return (ABRowSet<R>)new ABRowSet(toParentResultSet(resultsetCls));
  }
  
	public ABRowSet toABRowSet() throws SQLException{
  	return new ABRowSet(toABResultSet());
  }

  public ResultSet toResultSet() throws SQLException{
    perform();
    return _rs;
  }
  public List toList(IRowMappper mapper) throws SQLException{
  	perform();
  	List result=new ArrayList();
  	for(int i=0;_rs.next();i++) {
  		result.add(mapper.mapRow(_rs, i));
  	}
  	EasySql.close(_rs);
  	return result;
  }
  
  public List toList(IRowMappper mapper,int start,int limit) throws SQLException{
  	perform();
  	List result=new ArrayList();
  	limit=limit<0?Integer.MAX_VALUE:limit;
  	for(int i=0,n=start+limit;i<n&&_rs.next();i++) {
  		if(i>=start)
  			result.add(mapper.mapRow(_rs, i));
  	}
  	EasySql.close(_rs);
  	return result;
  }
  
  public List toList(final Class pojoClass,int start,int limit) throws SQLException{
  	return toList(new ReflectRowMapper(pojoClass),start,limit);
  }
  
  public List toList(final Class pojoClass) throws SQLException{
  	return toList(pojoClass,0,-1);
  }
  
  public JSONArray toJsonArray() throws SQLException{
		return toABResultSet().toJsonArray();
	}

  public JSONArray toJsonArray(boolean isFormatJavaBeanName) throws SQLException{
		return toABResultSet().toJsonArray(isFormatJavaBeanName);
	}
  
  public void clear(){
    _sql=null;
  }

  public Connection getConnection(){
    return _conn;
  }

  public String getSQL(){
  	parse();
    return querySQL;
  }

  public void parse(){
  	querySQL=adjustDialect(_sql);
  	for(int i=0;_parameters!=null&&i<_parameters.length;i++){
			String param=SQLAssistant.getStmtSqlValue(_parameters[i]);
			if(param.startsWith("(")&&param.endsWith(")"))
				param=param.substring(1,param.length()-1);
			querySQL=querySQL.replaceFirst("\\?",param);
		}
  }
	
  private String adjustDialect(String sql){
  	return new SqlDialect(sql,_conn).convert();
  }
  

	public boolean perform() throws SQLException{
    if(_conn==null)
      throw new SQLException("Connection is null,please setConnectcion first");
    if(_conn.isClosed())
      throw new SQLException("Connection is closed");
  	adjustLiteralParams();
    String sql=adjustDialect(_sql);
    PreparedStatement pstmt=_conn.prepareStatement(sql);
    for(int i=0;_parameters!=null&&i<_parameters.length;i++){
    	if(_parameters[i]!=null&&_parameters[i].getClass().equals(Date.class))
    		_parameters[i]=new Timestamp(((Date)_parameters[i]).getTime());
      pstmt.setObject(i+1,_parameters[i]);
    }
    boolean result=false;
    String temp=sql.toLowerCase();
		TimeMeter tm=TimeMeter.newInstanceOf();
    if(temp.startsWith("insert into")||temp.startsWith("delete from")) {
    	result=pstmt.execute();
    	result=true;//TODO insert 总是返回false,原因未明
    	getGeneratedKeys(pstmt);
    }
    else if(temp.startsWith("update")){
    	updatedCount=pstmt.executeUpdate();
    	result=updatedCount>=0;
    }
    else if(temp.startsWith("select")) {
    	_rs=pstmt.executeQuery();
    	result=true;
    }
    else {
    	result=pstmt.execute();
    	_rs=pstmt.getResultSet();
    }
		SQLAssistant.printSql(this, tm.getElapse());
    return result;
  }

	private boolean getGeneratedKeys(PreparedStatement pstmt) throws SQLException{
		if(isAutoIncrement){
			if(SQLAssistant.isPostgreSQL()){
				ResultSet rs=pstmt.getResultSet();
				if(rs.next()){
					generatedKey=rs.getBigDecimal(1);
				}
			}else{
				ResultSet gkrs=pstmt.getGeneratedKeys();
				if(gkrs.next()){
					generatedKey=(Number)gkrs.getObject(1);
				}
				gkrs.close();
			}
		}
		return generatedKey!=null;
	}
	
	public Number insert() throws SQLException {
		boolean result=perform();
		if(result)
			return generatedKey;
		return null;
	}
	
	public int update() throws SQLException {
		perform();
		return updatedCount;
	}
	
  private void adjustLiteralParams(){
  	if(_parameters.length==0) return;
  	boolean flag=false;
  	for(int i=0;i<_parameters.length;i++){
  		if(_parameters[i] instanceof Literal||_parameters[i] instanceof Collection||_parameters[i].getClass().isArray()) {
  			flag=true;
  			break;
  		}
		}
		if(flag){
			String _sql1=_sql;
			int start=0;
			ArrayList paraList=new ArrayList(_parameters.length);
			for(int i=0;i<_parameters.length;i++){
				start=_sql1.indexOf('?',start);
				if(start>=0){
					String param=SQLAssistant.getJDBCTemplateParams(_parameters[i]);
					_sql1=_sql1.substring(0,start)+param+_sql1.substring(start+1);
					start+=param.length()+1;
				}
			}
			_sql=_sql1;
			_parameters=paraList.toArray();
		}
	}

  public int getUpdatedCount() {
  	return updatedCount;
  }
  
	public void setConnection(Connection con){
    _conn=con;
    SQLAssistant.setDatabaseProductName(con);
  }
  
  @Override public String toString(){
    return getSQL();
  }

  /**
   * this is a simply method about count(*) for easy develop, but it is not strict and please use careful.
   */
  @Override public int count() throws SQLException { 
  	if(EasyObj.isBlank(this._sql))
  		throw new SQLRuntimeException("sql is null");
  	if(!EasyRegex.startWith(this._sql,"select"))
  		throw new SQLRuntimeException("count() is only for select:"+_sql);
  	int selectIndex=EasyRegex.indexOf(_sql,"select");
  	int fromIndex=EasyRegex.indexOf(_sql,"from");
  	int orderByIndex=EasyRegex.indexOf(_sql,"order by");
    //特定sql不以order by 来截取
  	if(SQLAssistant.isMySQL())
  		 orderByIndex = -1;
  	if(selectIndex>=fromIndex||selectIndex==-1)
  		throw new SQLRuntimeException("sql can not found select and from keywords:"+_sql);
  	String temp=this._sql;
  	if(EasyStr.existOf(this._sql," union ",true))
  		this._sql="select count(*) as TOTALRECORDS from (\n"+this._sql+"\n) a";
  	else	
  		this._sql=this._sql.substring(0,selectIndex+6)+" count(*) as TOTALRECORDS "+this._sql.substring(fromIndex,orderByIndex>0?orderByIndex:_sql.length());
  	ResultSet rs=this.toResultSet();
  	int result=rs.next()?rs.getInt("TOTALRECORDS"):0;
  	EasySql.close(rs);
  	_sql=temp;
  	return result;
  }


}
