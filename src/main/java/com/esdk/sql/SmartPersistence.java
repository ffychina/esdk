package com.esdk.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.esdk.exception.SdkRuntimeException;


public class SmartPersistence implements IPersistence{
  private Select select;
  private IPersistence persistenceStore;
  private Connection conn;
  
  public SmartPersistence(String tablename,Connection con){
    conn=con;
    select=new Select(tablename,con);
  }
  
  public SmartPersistence setPkFieldValue(String fieldname,Object pkid)throws SQLException{
    select.addEqualCondition(fieldname,pkid);
    select.addAllColumns();
    int count=select.count();
    if(count>1)
      throw new SQLException("found out record count more than one ,can not update or insert. sql:"+select.getSQL());
    else if(count==0)
      persistenceStore=new Insert(select.getTable().getTableName(),conn);
    else if(count==1) {
      persistenceStore=new Update(select.getTable().getTableName(),conn);
      ((Update)persistenceStore).addEqualCondition(fieldname,pkid);
    }
    else
      throw new SdkRuntimeException("unknown select error");
    this.addFieldValue(fieldname,pkid);
    return this;
  } 
  
  public SmartPersistence setUniqueCondition(String[] uniqueFieldNames,Object... values) throws SQLException {
  	if(uniqueFieldNames.length!=values.length)
  		throw new SQLException("parmeters not match fields");
  	Where[] wheres=new Where[uniqueFieldNames.length];
  	for(int i=0;i<uniqueFieldNames.length;i++) {
  		wheres[i]=new Where(select.createField(uniqueFieldNames[i]),values);
  	}
  	return setCondition(wheres);
  }
  
  public SmartPersistence setCondition(Where[] where)throws SQLException{
    select.addCondition(where);
    select.setAllColumns();
    int count=select.count();
    if(count>1)
      throw new SQLException("found out record count more than one ,can not update or insert. sql:"+select.getSQL());
    else if(count==0)
      persistenceStore=new Insert(select.getTable().getTableName(),conn);
    else if(count==1) {
      persistenceStore=new Update(select.getTable().getTableName(),conn);
      ((Update)persistenceStore).addCondition(where);
    }
    else
      throw new SQLException("unknown select error");
    return this;
  }

  public SmartPersistence addFieldValue(String fieldname,Object value){
    persistenceStore.addFieldValue(fieldname,value);
    return this;
  }

  public boolean hasFieldValues(){
    return persistenceStore.hasFieldValues();
  }

  public void clear(){
    persistenceStore.clear();
    Table t=select.getTable();
    select.clear();
    select.setTable(t);
  }

  public Connection getConnection(){
    return conn;
  }

  public String getSQL(){
    return persistenceStore.getSQL();
  }

  public void parse() throws Exception{
    persistenceStore.parse();
  }

  public boolean perform() throws SQLException{
    return persistenceStore.perform();
  }

  public void setConnection(Connection con){
    conn=con;
  }

	@Override public String getPreparedSql(){
		return persistenceStore.getPreparedSql();
	}

	@Override public Object[] getPreparedParameters(){
		return persistenceStore.getPreparedParameters();
	}
	public String[] getPreparedFields() {
		return persistenceStore.getPreparedFields();
	}
  public static void test() throws Exception {
      SmartPersistence sp=new SmartPersistence("TransportTrackTruck",null);
      sp.setPkFieldValue("TransportTrackTruckID",new BigDecimal("33"));
      sp.addFieldValue("TransportTrackTruckID",new BigDecimal("33"));
      sp.addFieldValue("TransportTrackID",new BigDecimal("508091651131251372"));
      sp.addFieldValue("TransportTrackIndex",new BigDecimal("2"));
      sp.addFieldValue("TruckIDNumber","粤A-00023");
      sp.addFieldValue("OperationToolID",new BigDecimal("307111447127785661"));
      sp.addFieldValue("OrderOperationToolID",new BigDecimal("0"));
      sp.addFieldValue("TransportTrackID",new BigDecimal("508091651131251372"));
      sp.addFieldValue("OrderID",new BigDecimal("0"));
      sp.addFieldValue("EventType","");
      sp.addFieldValue("TransType","");
      sp.addFieldValue("isModified",Boolean.TRUE);
      sp.addFieldValue("ModifyTime",new Date());
      sp.addFieldValue("Valid",Boolean.TRUE);
      System.out.println(sp.perform());
  }
  public static void main(String[] args) throws Exception{
    test();
  }
}
