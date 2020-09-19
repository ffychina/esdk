package com.esdk.sql;
import java.sql.Connection;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.esdk.utils.TimeMeter;
import com.esdk.esdk;
import com.esdk.utils.EasyObj;

/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
public class Delete implements ISQL{
  Table table;
  Wheres wheres;
  Connection connection;
  String deleteQuery;
  private int deleteCount;

  public Delete(String tablename,Connection conn){
    table=new Table(tablename);
    wheres=new Wheres();
    setConnection(conn);
  }

  public String getSQL(){
    parse();
    return deleteQuery;
  }

  public void clear(){
    table=null;
  }

  public void setTable(String tablename) {
    table=new Table(tablename);
  }

  public void addCondition(ILogic value){
    wheres.addCondition(value);
  }

  public void addEqualConditions(Map<String,Object> params){
  	for(Iterator iter=params.entrySet().iterator();iter.hasNext();){
  		Entry entry=(Entry)iter.next();
  		eq((String)entry.getKey(),entry.getValue());
  	}
  }
  
  public void addCondition(ILogic[] value){
    wheres.addCondition(value);
  }

  public void addCondition(String value){
    wheres.addCondition(value);
  }
  
  public void addCondition(String fieldName,String expression,String value){
    wheres.addCondition(new Field(table.getAliasName(),fieldName),expression,value);
  }
  
  public Delete eq(String fieldName,boolean value){
    wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
    return this;
  }

  public Delete eq(String fieldname,Object value){
    wheres.addEqualCondition(new Field(table.getAliasName(),fieldname),value);
    return this;
  }
  
  public void addEqualCondition(String fieldName,String value){
    wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
  }

	public void eq(String fieldName,String value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
	}
	
	public void eq(String fieldName,Boolean value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
	}
	
	public void eq(String fieldName,Number value){
		wheres.addEqualCondition(new Field(table.getAliasName(),fieldName),value);
	}
	
	public void notEq(String fieldName,String value){
		wheres.addNotEqualCondition(new Field(table.getAliasName(),fieldName),value);
	}

	public void notEq(String fieldName,Number value){
		wheres.addNotEqualCondition(new Field(table.getAliasName(),fieldName),value);
	}

	public void notEq(String fieldName,Boolean value){
		wheres.addNotEqualCondition(new Field(table.getAliasName(),fieldName),value);
	}

  public void addEqualNumeric(String fieldName,String value){
    wheres.addEqualNumeric(new Field(table.getAliasName(),fieldName),value);
  }

  public Delete in(String fieldName,String... value){
    wheres.addInCondition(new Field(table.getAliasName(),fieldName),value);
    return this;
  }

  public Delete in(String fieldName,Number... value){
    wheres.addInCondition(new Field(table.getAliasName(),fieldName),value);
    return this;
  }

  public Delete in(String fieldName,ISelect select){
    wheres.addInCondition(new Field(table.getAliasName(),fieldName),select);
    return this;
  }
  
  public void addInNumeric(String fieldName,String... value){
    wheres.addInNumeric(new Field(table.getAliasName(),fieldName),value);
  }
  
  public Delete notIn(String fieldName,ISelect select)throws Exception{
    wheres.addNotInCondition(new Field(table.getAliasName(),fieldName),select);
    return this;
  }
  
  public void addEqualEmplyNumeric(String fieldName){
    wheres.addEqualEmplyNumeric(new Field(table.getAliasName(),fieldName));
  }

  public void addEqualEmplyValue(String fieldName){
    wheres.addEqualEmplyValue(new Field(table.getAliasName(),fieldName));
  }

  public void addNotEqualEmplyNumeric(String fieldName){
    wheres.addNotEqualEmplyNumeric(new Field(table.getAliasName(),fieldName));
  }

  public void addNotEqualEmplyValue(String fieldName){
    wheres.addNotEqualEmplyValue(new Field(table.getAliasName(),fieldName));
  }
  
  public Field instanceField(String fieldName) {
    return new Field(table.getAliasName(),fieldName);
  }
  
  public void parse(){
    StringBuffer sb=new StringBuffer();
    if(wheres.size()==0)
      throw new SQLRuntimeException("Where condition must not empty");
    sb.append("DELETE FROM ").append(table.getTableName()).append(" ").append(wheres.assemble());
    deleteQuery=sb.toString();
  }

  public boolean perform(){
    if(connection==null)
      throw new SQLRuntimeException("Connection is null,please setConnectcion first");
    parse();
    try{
    	Statement stmt=connection.createStatement();
      TimeMeter tm=TimeMeter.newInstanceOf();
    	boolean success=stmt.execute(getSQL());//why success is false?
    	deleteCount=stmt.getUpdateCount();
      SQLAssistant.printSql(this,tm);
    	stmt.close();
/*      System.out.println("update success quantity is "+i);*/
      return true;
    }
    catch(SQLException e){
      throw new SQLRuntimeException("Delete失败："+e.toString()+"SQL语句："+getSQL());
    }
  }

  public Connection getConnection(){
    return connection;
  }

  public void setConnection(Connection conn){
    this.connection=conn;
  }

  public static void test() {
    try{
      Delete delete=new Delete("OrderMaster",null);
      delete.addEqualCondition("OrderNumber","test002");
      delete.addEqualNumeric("OrderID","1234567890");
      delete.parse();
      esdk.tool.assertEquals(delete.getSQL(),"DELETE FROM OrderMaster WHERE OrderNumber='test002' AND OrderID='1234567890'");
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args){
    test();
  }

	public int getDeleteCount(){
		return deleteCount;
	}

}
