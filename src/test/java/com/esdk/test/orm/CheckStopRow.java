package com.esdk.test.orm;
import java.math.BigDecimal;
import java.sql.SQLException;

import com.esdk.sql.orm.ParentRow;
public class CheckStopRow extends ParentRow{
  public static final String PrimaryKey="pName";
  public static final String tableName="CheckStop";
  public ParentRow newSelf(){
    return new CheckStopRow();
  }
  public void setPrimaryKey(Object value){
    setNewValue(PrimaryKey,value);
  }
  public Object getPrimaryKey(){
    return get(PrimaryKey);
  }
  public void setPKID(Number value){
    setNewValue(PrimaryKey,value);
  }
  public Long getPKID(){
    return (Long)get(PrimaryKey);
  }
  public String getPrimaryKeyName(){
    return PrimaryKey;
  }
  public String getTableName(){
    return tableName;
  }
  public String getPname()throws SQLException{
    return (String)get(CheckStopMetaData.pName);
  }
  public void setPname(String value)throws SQLException{
    setNewValue(CheckStopMetaData.pName,value);
  }
  public BigDecimal getLastCheckTime()throws SQLException{
    return (BigDecimal)get(CheckStopMetaData.lastCheckTime);
  }
  public void setLastCheckTime(BigDecimal value)throws SQLException{
    setNewValue(CheckStopMetaData.lastCheckTime,value);
  }
  public BigDecimal getInterTime()throws SQLException{
    return (BigDecimal)get(CheckStopMetaData.interTime);
  }
  public void setInterTime(BigDecimal value)throws SQLException{
    setNewValue(CheckStopMetaData.interTime,value);
  }
  public String getAlertList()throws SQLException{
    return (String)get(CheckStopMetaData.alertList);
  }
  public void setAlertList(String value)throws SQLException{
    setNewValue(CheckStopMetaData.alertList,value);
  }
  public String getMailList()throws SQLException{
    return (String)get(CheckStopMetaData.mailList);
  }
  public void setMailList(String value)throws SQLException{
    setNewValue(CheckStopMetaData.mailList,value);
  }
  public boolean getValid()throws SQLException{
    return ((Boolean)get(CheckStopMetaData.Valid)).booleanValue();
  }
  public void setValid(boolean value)throws SQLException{
    setNewValue(CheckStopMetaData.Valid,new Boolean(value));
  }
	@Override public Object getMetaData(){
		return new CheckStopMetaData();
	}
}
