package com.esdk.sql.orm;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.Map;

import com.esdk.utils.EasyStr;

public class TableRow extends ParentRow{
  private String PrimaryKey,tableName;
  private TableResultSet tableResultSet;
  private Map<String,Class> mapColumnClass;
  public TableRow(String tablename,String pkcolumn){
    tableName=tablename;
    PrimaryKey=pkcolumn;
  }
  
  public TableRow(String tablename,String pkcolumn,Object pkid,Connection con) throws SQLException{
    this(tablename,pkcolumn);
    refresh(pkid,con);
    if(isExistRecord())
      update();
  }
  
  public TableRow(String tablename,String pkcolumn,Connection con){
    tableName=tablename;
    PrimaryKey=pkcolumn;
    conn=con;
  }
  
  public TableRow(TableResultSet trs,String tablename,String pkcolumn){
    this(tablename,pkcolumn,trs.getConnection());
    tableResultSet=trs;
    super.isAutoIncrement=trs.isAutoIncrement();
    this.mapColumnClass=trs.mapColumnClass;
  }

  private void notifyTableResultSet(EventObject e) {
    if(tableResultSet!=null)
      tableResultSet.save((TableRow)e.getSource());
  }
  
  @Override public void delete(){
    super.delete();
    notifyTableResultSet(new EventObject(this));
  }
  
  @Override public void insert(){
    super.insert();
    notifyTableResultSet(new EventObject(this));
  }
  
  @Override protected void setNewValue(String key,Object newvalue){
    super.setNewValue(key,newvalue);
    if(isChanged())
      notifyTableResultSet(new EventObject(this));
  }

  public ParentRow newSelf(){
    return new TableRow(tableName,PrimaryKey);
  }
  public String getTableName(){
    return tableName;
  }

  @Override public String[] getNames(){
    if(columns==null)
      columns=(String[])record.keySet().toArray(new String[record.size()]);
    return columns;
  }

  @Override void initColumns(ResultSet rset) throws SQLException{
    ResultSetMetaData rsmd=rset.getMetaData();
    String[] labels=new String[rsmd.getColumnCount()]; 
    for(int i=1,n=rsmd.getColumnCount();i<=n;i++)
      labels[i-1]=rsmd.getColumnLabel(i);
    columns=labels;
  }
  
  @Override protected void loadFromResultSet(ResultSet rset) throws SQLException {
    ResultSetMetaData rsmd=rset.getMetaData();
    for(int i=1,n=rsmd.getColumnCount();i<=n;i++)
      record.put(rsmd.getColumnLabel(i),rset.getObject(i));
  }

	@Override public Object getMetaData(){
		return null;
	}
}
