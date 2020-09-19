package com.esdk.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.esdk.esdk;
import com.esdk.utils.*;
/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
public class Insert implements IPersistence{
	private Table table;
	private Connection _conn;
	private InsertSetFields insertFields;

	public String[] getPreparedFields(){
		return insertFields.getFieldNames();
	}

	private String insertSql;

	public String getPreparedSql(){
		return insertSql;
	}

	private Object[] parameters;

	public Object[] getPreparedParameters(){
		return parameters;
	}

	private boolean isAutoIncrement;
	private Number generatedKey;
	private String primaryKeyName;

	public Insert(String tablename,Connection conn){
		table=new Table(tablename);
		setConnection(conn);
		insertFields=new InsertSetFields();
	}

	public void setPrimaryKeyName(String primaryKeyName){
		this.primaryKeyName=primaryKeyName;
	}

	public String getPrimaryKeyName(){
		return primaryKeyName;
	}

	public boolean hasFieldValues(){
		return insertFields.size()>0;
	}

	@Override
	public String toString(){
		return getSQL();
	}

	public String getSQL(){
		return new StringBuffer().append("INSERT INTO ").append(table.getTableName()).append(insertFields.getStmtSql()).toString();
	}

	/** 打印insert的參數,方便調試 */
	public void printParams(){
		CharAppender result=new CharAppender(',');
		String[] fields=getPreparedFields();
		Object[] values=insertFields.getParameters();
		for(int i=0;i<fields.length;i++){
			result.append(fields[i]+"="+esdk.str.valueOf(values[i]));
		}
		System.out.println("insert sql params: "+result.toString());
	}

	public void clear(){
		table=null;
		insertFields.clear();
	}

	public void addFieldValues(Map<String,Object> params){
		for(Iterator iter=params.entrySet().iterator();iter.hasNext();){
			Entry entry=(Entry)iter.next();
			addFieldValue((String)entry.getKey(),entry.getValue());
		}
	}

	public Insert addFieldValue(String fieldname,boolean value){
		insertFields.addFieldValue(fieldname,value);
		return this;
	}

	public Insert addFieldValue(String fieldname,String value){
		insertFields.addFieldValue(fieldname,value);
		return this;
	}

	public Insert addFieldValue(String fieldname,Number value){
		insertFields.addFieldValue(fieldname,value);
		return this;
	}

	public Insert addFieldValue(String fieldname,Date value){
		insertFields.addFieldValue(fieldname,value);
		return this;
	}

	public Insert addFieldValue(String fieldname,Boolean value){
		insertFields.addFieldValue(fieldname,value);
		return this;
	}

	public Insert addFieldValue(String fieldname,Object value){
		insertFields.addFieldValue(fieldname,value);
		return this;
	}

	public Insert addNumericValue(String fieldname,String value){
		insertFields.addNumericValue(fieldname,value);
		return this;
	}

	public void parse(){
		StringBuffer sb=new StringBuffer().append("INSERT INTO ").append(table.getTableName()).append(insertFields.getPstmtSql());

		insertSql=sb.toString();
		if(SQLAssistant.isPostgreSQL())
			insertSql+=getPrimaryKeyName()==null?" returning * ":"returning ".concat(getPrimaryKeyName());
		parameters=insertFields.getParameters();
	}

	public boolean perform(){
		if(_conn==null)
			throw new SQLRuntimeException("Connection is null,please invoke setConnectcion before perform");
		parse();
		PreparedStatement pstmt=null;
		try{
			pstmt=getPreparedStatement();
			for(int i=0;i<parameters.length;i++){
				pstmt.setObject(i+1,parameters[i]);
			}
			TimeMeter tm=TimeMeter.newInstanceOf();
			pstmt.execute();
			getGeneratedKeys(pstmt);
			SQLAssistant.printSql(this,tm);
			pstmt.close();
			return true;
		}catch(SQLException e){
			throw new SQLRuntimeException("Insert失败："+e.toString()+",SQL语句："+getSQL());
		}
	}

	private boolean getGeneratedKeys(PreparedStatement pstmt) throws SQLException{
		if(isAutoIncrement()){
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

	private PreparedStatement getPreparedStatement() throws SQLException{
		if(SQLAssistant.isPostgreSQL())
			return _conn.prepareStatement(insertSql);
		else if(isAutoIncrement()){
			if(SQLAssistant.isOracle())
				return _conn.prepareStatement(insertSql,new String[]{getPrimaryKeyName()});
			else
				return _conn.prepareStatement(insertSql,PreparedStatement.RETURN_GENERATED_KEYS);
		}else
			return _conn.prepareStatement(insertSql);
	}

	public Object getGeneratedKey(){
		return generatedKey;
	}

	public boolean executeSQL() throws SQLException{
		Statement stmt=_conn.createStatement();
		try{
			stmt.execute(getSQL());
			stmt.close();
			return true;
		}catch(SQLException e){
			stmt.close();
			throw new SQLException("Insert失败："+e.toString()+",SQL语句："+getSQL());
		}
	}

	public Connection getConnection(){
		return _conn;
	}

	public void setConnection(Connection conn){
		this._conn=conn;
		SQLAssistant.setDatabaseProductName(this._conn);
	}

	class InsertSetFields extends SaveFieldsValue{
		private String getInsertFields(){
			String[] fields=(String[])linkedMap.keySet().toArray(new String[0]);
			return EasyStr.arrToStr(fields);
		}

		private String getInsertValues(){
			Object[] values=linkedMap.values().toArray();
			StringBuffer result=new StringBuffer();
			for(int i=0;i<values.length;i++){
				result.append(SQLAssistant.getStmtSqlValue(values[i]));
				if(i<values.length-1)
					result.append(",");
			}
			return result.toString();
		}

		public String getStmtSql(){
			return new StringBuffer().append(" (").append(getInsertFields()).append(")").append(" VALUES ").append("(").append(getInsertValues()).append(")").toString();
		}

		public String getPstmtSql(){
			StringBuffer result=new StringBuffer().append(" (").append(getInsertFields()).append(")").append("VALUES ").append("(");
			for(Iterator iter=linkedMap.entrySet().iterator();iter.hasNext();){
				Entry entry=(Entry)iter.next();
				Object value=entry.getValue();
				if(value!=null&&value instanceof Function){
					result.append(value.toString());
				}else{
					result.append("?");
				}
				if(iter.hasNext())
					result.append(",");
			}
			result.append(")");
			return result.toString();
		}
	}

	public void setAutoIncrement(boolean isAutoIncrement){
		this.isAutoIncrement=isAutoIncrement;
	}

	public boolean isAutoIncrement(){
		return isAutoIncrement;
	}

	public String getTableName(){
		return table.getTableName();
	}

	public static void test(){
		try{
			Insert insert=new Insert("EdiInLog",null);
			insert.addFieldValue("OrderID",new BigDecimal(111));
			insert.addFieldValue("OrderNumber","test002");
			insert.addNumericValue("OrderTypeID","54621354");
			insert.addNumericValue("MsgID","12");
			insert.addFieldValue("MsgCode","OrderInsertSuccess");
			insert.addFieldValue("Msg","'订单新增成功'");
			insert.addFieldValue("HandleTime",(Object)null);
			insert.addFieldValue("UpdateTime",EasyTime.getDate("2008-10-01 23:59:59",EasyTime.DATETIME_FORMAT));
			insert.addNumericValue("Valid","1");
			TestHelper.assertEquals(insert.getSQL(),"Insert Into EdiInLog (OrderID,OrderNumber,OrderTypeID,MsgID,MsgCode,Msg,HandleTime,UpdateTime,Valid)\r\n"+"Values (111,'test002','54621354','12','OrderInsertSuccess','''订单新增成功''',null,'2008-10-01 23:59:59','1')");
			insert.parse();
			TestHelper.assertEquals(insert.insertSql,"Insert Into EdiInLog (OrderID,OrderNumber,OrderTypeID,MsgID,MsgCode,Msg,HandleTime,UpdateTime,Valid)\r\n"+"Values (?,?,?,?,?,?,?,?,?)");
			TestHelper.assertEquals(Arrays.asList(insert.parameters).toString(),"[111, test002, 54621354, 12, OrderInsertSuccess, '订单新增成功', null, 2008-10-01 23:59:59.0, 1]");
			insert.printParams();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		test();
	}
}
