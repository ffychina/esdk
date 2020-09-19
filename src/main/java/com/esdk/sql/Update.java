package com.esdk.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.esdk.esdk;
import com.esdk.utils.EasyTime;
import com.esdk.utils.TestHelper;
import com.esdk.utils.TimeMeter;
/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
public class Update implements IPersistence{
	Table table;
	UpdateSetFields updateFields;

	public String[] getPreparedFields(){
		return updateFields.getFieldNames();
	}

	Wheres wheres;
	Connection _conn;
	int updatedCount;
	private String updateSql;

	public String getPreparedSql(){
		return updateSql;
	}

	Object[] parameters;

	public Object[] getPreparedParameters(){
		return parameters;
	}

	private Transaction _transaction;// TODO 未想清楚该如何实现

	public Update(String tablename,Connection conn){
		table=new Table(tablename);
		wheres=new Wheres();
		updateFields=new UpdateSetFields();
		setConnection(conn);
	}

	public Update(String tablename){
		table=new Table(tablename);
		wheres=new Wheres();
		updateFields=new UpdateSetFields();
	}

	public Update setTrancation(Transaction transaction){
		this._transaction=transaction;
		return this;
	}

	public Transaction getTrancation(){
		return this._transaction;
	}

	public Update addFieldValue(String fieldname,String value){
		updateFields.addFieldValue(fieldname,value);
		return this;
	}

	public Update addFieldValue(String fieldname,Number value){
		updateFields.addFieldValue(fieldname,value);
		return this;
	}

	public Update addFieldValue(String fieldname,boolean value){
		updateFields.addFieldValue(fieldname,value);
		return this;
	}

	public Update addFieldValue(String fieldname,Date value){
		updateFields.addFieldValue(fieldname,value);
		return this;
	}

	public Update addFieldValue(String fieldname,Boolean value){
		updateFields.addFieldValue(fieldname,value);
		return this;
	}

	public Update addFieldValue(String fieldname,Object value){
		updateFields.addFieldValue(fieldname,value);
		return this;
	}

	public Update addNumericValue(String fieldname,String value){
		updateFields.addNumericValue(fieldname,value);
		return this;
	}

	public Update addColumnValue(String fieldname,String column){
		addFieldValue(fieldname,column);
		return this;
	}

	@Override
	public String toString(){
		return getSQL();
	}

	public String getSQL(){
		StringBuffer sb=new StringBuffer();
		if(wheres.size()==0)
			throw new SQLRuntimeException("Where condition must not empty");
		sb.append("UPDATE ").append(table.getTableName()).append(" SET ").append(updateFields.assemble()).append(" ").append(wheres.assemble());
		return sb.toString();

	}

	public void clear(){
		table=null;
		updateFields.clear();
		wheres.clear();
	}

	public boolean hasFieldValues(){
		return updateFields.size()>0;
	}

	public void setTable(String tablename){
		table=new Table(tablename);
	}

	public Update addCondition(ILogic value){
		wheres.addCondition(value);
		return this;
	}

	public Update addCondition(ILogic[] value){
		wheres.addCondition(value);
		return this;
	}

	public Update addCondition(String value){
		wheres.addCondition(value);
		return this;
	}

	public Update addCondition(String fieldName,String expression,String value){
		wheres.addCondition(new Field(table.getAliasName(),fieldName),expression,value);
		return this;
	}
	public Update addCondition(String fieldName,String expression,Number value){
		wheres.addCondition(new Field(table.getAliasName(),fieldName),expression,value);
		return this;
	}
	public Update addEqualConditions(Map<String,Object> params){
		for(Iterator iter=params.entrySet().iterator();iter.hasNext();){
			Entry entry=(Entry)iter.next();
			addEqualCondition((String)entry.getKey(),entry.getValue());
		}
		return this;
	}

	public Update addEqualCondition(String fieldName,boolean value){
		wheres.addEqualNumeric(new Field(table.getAliasName(),fieldName),value?"1":"0");
		return this;
	}

	public Update addEqualCondition(String fieldName,Boolean value){
		wheres.addEqualNumeric(new Field(table.getAliasName(),fieldName),value?"1":"0");
		return this;
	}

	public Update addEqualColumn(String fieldName,String anotherFieldname){
		this.wheres.addEqualColumn(new Field(table.getAliasName(),fieldName),new Field(table.getAliasName(),anotherFieldname));
		return this;
	}

	public Update addNotEqualColumn(String fieldName,String anotherFieldname){
		this.wheres.addNotEqualColumn(new Field(table.getAliasName(),fieldName),new Field(table.getAliasName(),anotherFieldname));
		return this;
	}

	public Update addEqualCondition(String fieldname,Object value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldname),value);
		return this;
	}

	public Update addEqualCondition(String fieldName,Number value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}

	public Update addEqualCondition(String fieldName,String value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}

	public Update eq(String fieldName,String value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}

	public Update eq(String fieldName,Boolean value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}
	
	public Update eq(String fieldName,Number value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}
	
	public Update eq(String fieldName,Object value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}
	
	public Update addNotEqualCondition(String fieldName,String value){
		wheres.addNotEqualString(new Field(table.getAliasName(),fieldName),value);
		return this;
	}

	public Update notEq(String fieldName,String value){
		wheres.addNotEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}

	public Update notEq(String fieldName,Number value){
		wheres.addNotEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}

	public Update notEq(String fieldName,Boolean value){
		wheres.addNotEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}
	public Update notEq(String fieldName,Object value){
		wheres.addNotEqualCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}
	public Update addInCondition(String fieldName,Object[] value){
		if(value instanceof String[])
			wheres.addInCondition(new Field(table.getAliasName(),fieldName),(String[])value);
		else if(value instanceof Number[])
			wheres.addInCondition(new Field(table.getAliasName(),fieldName),(Number[])value);
		else
			throw new RuntimeException("不能识别的对象类型:"+value.getClass().getName());
		return this;
	}

	public Update addInCondition(String fieldName,String[] value){
		wheres.addInCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}

	public Update addInCondition(String fieldName,Number[] value){
		wheres.addInCondition(new Field(table.getAliasName(),fieldName),value);
		return this;
	}

	public Update addInCondition(String fieldName,ISelect select){
		wheres.addInCondition(new Field(table.getAliasName(),fieldName),select);
		return this;
	}

	public Update addInNumeric(String fieldName,String[] value){
		wheres.addInNumeric(new Field(table.getAliasName(),fieldName),value);
		return this;
	}

	public Update addEqualEmplyNumeric(String fieldName){
		wheres.addEqualEmplyNumeric(new Field(table.getAliasName(),fieldName));
		return this;
	}

	public Update addEqualEmplyValue(String fieldName){
		wheres.addEqualEmplyValue(new Field(table.getAliasName(),fieldName));
		return this;
	}

	public Update addNotEqualEmplyNumeric(String fieldName){
		wheres.addNotEqualEmplyNumeric(new Field(table.getAliasName(),fieldName));
		return this;
	}

	public Update addNotEqualEmplyValue(String fieldName){
		wheres.addNotEqualEmplyValue(new Field(table.getAliasName(),fieldName));
		return this;
	}

	public Field instanceField(String fieldName){
		return new Field(table.getAliasName(),fieldName);
	}

	public Update addNotInCondition(String fieldName,ISelect select) throws Exception{
		wheres.addNotInCondition(new Field(table.getAliasName(),fieldName),select);
		return this;
	}

	public void parse(){
		updateSql=new StringBuffer().append("UPDATE ").append(table.getTableName()).append(" SET ").append(updateFields.getPstmtSql()).append(" ").append(wheres.getPstmtSql()).toString();
		Object[] updatefieldParameters=updateFields.getParameters();
		Object[] whereParameters=wheres.getParameters();
		parameters=new Object[updatefieldParameters.length+whereParameters.length];
		for(int i=0;i<updatefieldParameters.length;i++)
			parameters[i]=updatefieldParameters[i];
		for(int i=0;i<whereParameters.length;i++)
			parameters[i+updatefieldParameters.length]=whereParameters[i];
	}

	public boolean perform(){
		if(_conn==null)
			throw new SQLRuntimeException("Connection is null,please setConnectcion first");
		PreparedStatement pstmt=null;
		try{
			parse();
			pstmt=_conn.prepareStatement(updateSql);
			for(int i=0;i<parameters.length;i++){
				pstmt.setObject(i+1,parameters[i]);
			}
			TimeMeter tm=TimeMeter.newInstanceOf();
			updatedCount=pstmt.executeUpdate();
			SQLAssistant.printSql(this,tm);
			pstmt.close();
			return true;
		}catch(SQLException e){
			throw new SQLRuntimeException("Update失败："+e.toString()+"SQL语句："+getSQL());
		}
	}

	public int performUpdate() {
		perform();
		return updatedCount;
	}
	
	/*
	 * public boolean executeSQL() throws SQLException{ Statement
	 * stmt=_conn.createStatement(); try{
	 * updatedCount=stmt.executeUpdate(getSQL()); stmt.close(); return true; }
	 * catch(SQLException e){ stmt.close(); throw new
	 * SQLException("Update失败:"+e.toString()+"SQL语句:"+getSQL()); } }
	 */

	public Connection getConnection(){
		return _conn;
	}

	public void setConnection(Connection conn){
		this._conn=conn;
		SQLAssistant.setDatabaseProductName(this._conn);
	}

	public void addFieldExpression(String fieldName,String literal){
		updateFields.addFieldExpression(fieldName,literal);
	}

	class UpdateSetFields extends SaveFieldsValue{

		public String getPstmtSql(){
			StringBuffer result=new StringBuffer();
			Iterator iter=linkedMap.entrySet().iterator();
			while(iter.hasNext()){
				Entry entry=(Entry)iter.next();
				String fieldname=(String)entry.getKey();
				if(entry.getValue() instanceof Expression){
					result.append(entry.getValue().toString());
				}else{
					String v=(entry.getValue() instanceof Function)?entry.getValue().toString():"?";
					result.append(fieldname).append("=").append(v);
				}
				result.append(iter.hasNext()?",":"");
			}
			return result.toString();
		}

		public void addFieldExpression(String fieldName,String literal){
			linkedMap.put(fieldName,new Expression(fieldName,literal));
		}

		public String getStmtSql(){
			StringBuffer result=new StringBuffer();
			Iterator iter=linkedMap.keySet().iterator();
			while(iter.hasNext()){
				String fieldname=(String)iter.next();
				Object value=linkedMap.get(fieldname);
				if(value instanceof Expression){
					result.append(value.toString());
				}else{
					result.append(fieldname).append("=").append(SQLAssistant.getStmtSqlValue(value));
				}
				result.append(iter.hasNext()?",":"");
			}
			return result.toString();
		}
	}

	public int getUpdatedCount(){
		return this.updatedCount;
	}

	private static void test(){
		try{
			Update update=new Update("OrderMaster",(Connection)null);
			update.addEqualCondition("OrderNumber","test002");
			update.eq("OrderID","1234567890");
			update.addEqualEmplyNumeric("ModifyTime");
			update.addFieldValue("Remark","ok");
			update.addFieldExpression("PrintTime","CreateTime");
			update.addFieldExpression("OrderNumber","OrdreNumber+'-01'");
			update.addFieldExpression("TotalQuantity","TotalQuantity+1");
			update.addFieldValue("FeedbackTime",new BigDecimal("20060907"));
			update.addNumericValue("StartPointID","54621354");
			update.addEqualColumn("FirstOrgID","FinallyOrgID");
			update.addFieldValue("HandleTime",(BigDecimal)null);
			update.addFieldValue("UpdateTime",EasyTime.getDate("2008-10-01 23:59:59",EasyTime.DATETIME_FORMAT));
			update.parse();
			TestHelper.assertEquals(update.getSQL(),"Update OrderMaster set Remark='ok',PrintTime=CreateTime,OrderNumber=OrdreNumber+'-01',TotalQuantity=TotalQuantity+1,FeedbackTime=20060907,StartPointID='54621354',HandleTime=null,UpdateTime='2008-10-01 23:59:59'\r\n"+"Where OrderNumber = 'test002' and OrderID = '1234567890' and (ModifyTime = 0 or ModifyTime is null) and FirstOrgID=FinallyOrgID");
			TestHelper.assertEquals(update.updateSql,"Update OrderMaster set Remark=?,PrintTime=CreateTime,OrderNumber=OrdreNumber+'-01',TotalQuantity=TotalQuantity+1,FeedbackTime=?,StartPointID=?,HandleTime=?,UpdateTime=?\r\n"+"Where OrderNumber = ? and OrderID = ? and (ModifyTime = ? or ModifyTime is null) and FirstOrgID=FinallyOrgID");
			TestHelper.assertEquals(Arrays.asList(update.parameters).toString(),"[ok, 20060907, 54621354, null, 2008-10-01 23:59:59.0, test002, 1234567890, 0]");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		test();
	}

}
