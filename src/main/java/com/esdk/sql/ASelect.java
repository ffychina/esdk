package com.esdk.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.sql.orm.IRowSet;
import com.esdk.sql.orm.TableResultSet;
import com.esdk.utils.EasySql;
import com.esdk.utils.EasyRegex;
import com.esdk.utils.EasyObj;

/**
 * 把自定义的select的sql语句包装成为Select对象，这样就可以实现count()的功能。
 * @author 范飞宇  ffyu@grgbanking.com
 * @version 1.00.00
 */
public class ASelect<T> implements ISelect,ITableSelect{
  private String sql,tableName;
  private Connection conn;
  private ResultSet _rs;
  
  public ASelect(String tablename,String sql0){
    this(tablename,sql0,null);
  }
  
  public ASelect(String tableName0,String sql0,Connection con){
    sql=sql0;
    tableName=tableName0;
    conn=con;
  }
  
  public String getTableName() {
    return tableName;
  }
  
  @Deprecated
  public String[][] toArray() throws SQLException{
    return null;
  }

  @Deprecated
  public int getTop(){
    return 0;
  }

  public ABResultSet toABResultSet() throws SQLException{
    perform();
    return new ABResultSet(_rs);
  }

  @Override public IRowSet toRowSet() throws SQLException{
		return new ABRowSet(toABResultSet());
	}

  public ResultSet toResultSet() throws SQLException{
    perform();
    return _rs;
  }

  public void clear(){
    sql=null;
  }

  public Connection getConnection(){
    return conn;
  }

  public String getSQL(){
    return sql;
  }

  @Deprecated
  public void parse() throws Exception{
    
  }

  public boolean perform() throws SQLException{
    Statement stmt=conn.createStatement();
    _rs=stmt.executeQuery(sql);
    return true;
  }

  public void setConnection(Connection con){
    conn=con;
  }

  public TableResultSet toTableResultSet() throws SQLException{
    return new TableResultSet(this,false);
  }
  
  @Override public String toString(){
    return getSQL();
  }

  /**
   * this is a simply method about count(*) for easy develop, but it is not strict and please use careful.
   */
  @Override public int count() throws SQLException { 
  	if(EasyObj.isBlank(this.sql))
  		throw new SQLRuntimeException("sql is null");
  	if(!EasyRegex.startWith(this.sql,"select"))
  		throw new SQLRuntimeException("count() is only for select:"+sql);
  	int selectIndex=EasyRegex.indexOf(sql,"select");
  	int fromIndex=EasyRegex.indexOf(sql,"from");
  	int orderByIndex=EasyRegex.indexOf(sql,"order by");
  	if(selectIndex>=fromIndex||selectIndex==-1)
  		throw new SQLRuntimeException("sql can not found select and from keywords:"+sql);
  	String temp=this.sql;
  	this.sql=this.sql.substring(0,selectIndex+6)+" count(*) as TOTALRECORDS "+this.sql.substring(fromIndex,orderByIndex>0?orderByIndex:sql.length());
  	ResultSet rs=this.toResultSet();
  	int result=rs.next()?rs.getInt("TOTALRECORDS"):0;
  	EasySql.close(rs);
  	sql=temp;
  	return result;
  }

}
