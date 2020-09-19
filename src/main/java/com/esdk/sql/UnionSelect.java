package com.esdk.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.fastjson.JSONArray;
import com.esdk.esdk;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.utils.EasyObj;

public class UnionSelect<T> implements ISelect{
  private StringBuffer _querySQL=new StringBuffer();
  private Connection _conn;
  private JDBCTemplate _jt;
  private boolean isChanged=false;
  
  public UnionSelect(ISelect... selects) {
  	this.addSelects(selects);
  }
  
  public UnionSelect(Connection conn) {
  	this._conn=conn;
  }
  
  public UnionSelect(ISelect[] selects,Connection conn) {
    this(conn);
    addSelects(selects);
  }
  
  public UnionSelect(Connection conn,ISelect... selects) {
    this(conn);
    for(int i=0;i<selects.length;i++){
      addSelect(selects[i]);
		}
  }
  
  public void addSelects(ISelect[] selects) {
    for(int i=0;i<selects.length;i++){
      addSelect(selects[i]);
    }
  }
  
  public void addSelect(ISelect select) {
  	if(_querySQL.length()==0)
  		_querySQL.append(select.getSQL());
  	else
  		_querySQL.append("\r\nunion\r\n").append(select.getSQL());
  	isChanged=true;
  }

  public boolean perform() throws SQLException{
  	if(isChanged) {
  		_jt=new JDBCTemplate(this._conn,_querySQL.toString());
  		isChanged=false;
  	}
    return _jt.perform();
  }

  public ResultSet toResultSet()throws SQLException{
    perform();
    return _jt.toResultSet();
  }

  public ABResultSet toABResultSet() throws SQLException{
    perform();
    return _jt.toABResultSet();
  }
  public JSONArray toJsonArray(boolean isFomatJavaBeanName) throws SQLException {
    perform();
    return _jt.toJsonArray(isFomatJavaBeanName);
  }
  
  public ABRowSet toRowSet() throws SQLException{
    perform();
    return _jt.toABRowSet();
  }
  
  public String[][] toArray()throws SQLException{
    perform();
    return _jt.toArray();
  }

  public int count() throws SQLException {
	JDBCTemplate  jt=new JDBCTemplate(getConnection(),"select count(*) as TOTALRECORDS from ("+this.getSQL()+") as a");
    return jt.queryForInt();
  }
  
  /**
   * deprecate, it is not neccessary for union  
   */
  public int getTop(){
    return 0;
  }

  public Connection getConnection(){
    return this._conn;
  }

  public void setConnection(Connection connection){
    _conn=connection;
  }

  public String getSQL(){
    return _querySQL.toString();
  }

  
  public void clear(){
    _querySQL.delete(0,_querySQL.length());
  }

  /**
   * deprecate, it is not neccessary for union  
   */
  public void parse() throws Exception{
  } 

  @Override  public String toString(){
  	return getSQL();
  }
  
  public static void test(){
  	Select s=new Select("checkstop",null);
  	s.setColumns("id","name");
  	s.addEqualCondition("id",1);
  	
  	Select s1=new Select("checkstart");
  	s1.setColumns("id","name");
  	s1.addEqualCondition("name","sec");
  	s1.addOrderBy("name",true);
  	UnionSelect us=new UnionSelect((Connection)null,s,s1);
  	esdk.tool.assertEquals(us.getSQL()
  		 ,"Select Top 50 id,name\r\n" + 
  			"From checkstop \r\n" + 
  			"Where id = 1\r\n" + 
  			"union\r\n" + 
  			"Select Top 50 id,name\r\n" + 
  			"From checkstart \r\n" + 
  			"Where name = 'sec'\r\n" + 
  			"Order By name desc");
  }
  
  public static void main(String[] args){
    test();
  }

}
