package com.esdk.sql;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esdk.esdk;
import com.esdk.esdk;
import com.esdk.sql.orm.IRow;
import com.esdk.sql.orm.ParentRow;
import com.esdk.sql.orm.RowUtils;
import com.esdk.utils.*;

/**批量insert into（注意oracle不支持该功能）*/
public class BatchInsert implements ISQL{
  protected String tableName;
  private Connection _conn;
  protected String insertSQL;
  protected String[] fields;
  protected LinkedList<Object[]> _values=new LinkedList();
  private boolean isAutoIncrement=true;
  private List generatedKeys;

  public BatchInsert(String tablename,Connection conn){
    this.tableName=tablename;
    setConnection(conn);
  }

  public BatchInsert(String tablename,String[] fields,Connection conn){
    this(tablename,conn);
    setFields(fields);
  }

  public String getSQL(){
  	parse();
    return insertSQL;
  }

  public void clear(){
    tableName=null;
    fields=null;
    _values.clear();
  }
  
  public void clearValues() {
  	_values.clear();
  }
  
  public int size() {
  	return _values.size();
  }
  
  public BatchInsert setFields(String... fields){
  	this.fields=fields;
  	return this;
  }
  
  public BatchInsert addValue(Object[] values){
  	_values.add(values);
  	return this;
  }
  
  public BatchInsert addValue(Map values){
  	ArrayList list=new ArrayList(fields.length);
  	for(int i=0;i<fields.length;i++){
  		list.add(values.get(fields[i]));
  	}
  	return addValue(list.toArray());
  }
  
  public BatchInsert addValues(List<Object[]> list){
		_values.addAll(list);
		return this;
  }
  
  public BatchInsert addRows(Collection<IRow> collection){
		for(IRow row:collection) {
			addValue(row);
		}
		return this;
  }
  
  public BatchInsert addValue(ParentRow row) {
  	List result=esdk.obj.isValid(fields)?row.toList(fields):row.toList();
  	if(row.isAutoIncrement()) {
  		result.remove(0);
  	}else if(row.getPKID()==null){
  		row.setPKID(RowUtils.genNextPrimaryId());
  		result=row.toList();
  	}
  	if(fields==null) {
  		if(row.isAutoIncrement())
  			setFields(esdk.str.remove(row.getNames(),row.getPrimaryKeyName()));
  		else
  			setFields(row.getNames());
  	}
  	_values.add(result.toArray());
  	return this;
  }
  
  public BatchInsert addValue(IRow row) {
  	if(row instanceof ParentRow)
  		return addValue((ParentRow)row);
  	else
  		return addValue(row.toList().toArray());
  }
  
  String getInsertFields() {
  	StringBuffer result=new StringBuffer().append("Insert Into ").append(tableName).append(" (").append(EasyStr.arrToStr(fields)).append(")").append(" values ");
  	return result.toString(); 
  }
  
  public void parse(){
    StringBuffer sb=new StringBuffer().append("Insert Into ").append(tableName).append(" (").append(EasyStr.arrToStr(fields)).append(")").append(" values ");
    for(Iterator<Object[]> iter=_values.iterator();iter.hasNext();){
    	Object[] array=iter.next();
    	sb.append("(");
    	for(int i=0,n=array.length;i<n;i++){
    		sb.append(SQLAssistant.getStmtSqlValue(array[i]));
    		if(i<n-1)
    			sb.append(",");
    	}
    	sb.append(")");
    	if(iter.hasNext())
    		sb.append(",");
    }
    insertSQL=sb.toString();
  }

  public boolean perform(){
    if(_conn==null)
      throw new SQLRuntimeException("Connection is null,please invoke setConnectcion before perform");
    if(_values.size()==0)
    	return true;
    try{
			parse();
			Statement st=_conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			TimeMeter tm=TimeMeter.newInstanceOf();
			boolean flag=SQLAssistant.isPostgreSQL()?st.execute(insertSQL):st.execute(insertSQL,Statement.RETURN_GENERATED_KEYS);
			getGeneratedKeys(st);
			SQLAssistant.printSql(this,tm);
			st.close();
			//return flag;; //待分析，目前测试在Mysql环境下，flag永远都是返回false.
			return true;
		}catch(SQLException e){
			throw new SQLRuntimeException(e);
		}
  }

  public List getGeneratedKeys() {
  	return generatedKeys;
  }
  
	private boolean getGeneratedKeys(Statement st) throws SQLException{
		if(isAutoIncrement()){
			generatedKeys=new LinkedList();
			if(SQLAssistant.isPostgreSQL()){
				ResultSet rs=st.getResultSet();
				while(rs!=null&&rs.next()) {
					generatedKeys.add(rs.getObject(1));
				}
			}else{
				ResultSet gkrs=st.getGeneratedKeys();
				while(gkrs!=null&&gkrs.next()){
					generatedKeys.add(gkrs.getObject(1));
				}
				EasySql.close(gkrs);
			}
		}
		return generatedKeys!=null;
	}

  public Connection getConnection(){
    return _conn;
  }

  public void setConnection(Connection conn){
    this._conn=conn;
    SQLAssistant.setDatabaseProductName(this._conn);
  }

	public void setAutoIncrement(boolean isAutoIncrement){
		this.isAutoIncrement = isAutoIncrement;
	}

	public boolean isAutoIncrement(){
		return isAutoIncrement;
	}

	public static void test() {
    try{
    	BatchInsert bi=new BatchInsert("member",new String[]{"code","name","remark","sex","valid","create_time"},null);
    	bi.addValue(new Object[]{"CN001","测试名称1","who's your name?",2,true,EasyTime.valueOf("2010-01-02 18:01:59")});
    	bi.addValue(new Object[]{"CN002","测试名称2",null,2,true,EasyTime.valueOf("2010-03-02 23:01:01")});
    	esdk.tool.assertEquals(bi.getSQL(),"Insert Into member (code,name,remark,sex,valid,create_time) values ('CN001','测试名称1','who''s your name?',2,1,'2010-01-02 18:01:59'),('CN002','测试名称2',null,2,1,'2010-03-02 23:01:01')");
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args){
    test();
  }

}
