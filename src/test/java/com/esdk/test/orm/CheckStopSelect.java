package com.esdk.test.orm;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import com.esdk.sql.ISQL;
import com.esdk.sql.orm.AbstractSelect;
public class CheckStopSelect extends AbstractSelect implements ISQL{
  public CheckStopSelect(Connection conn){
    super(CheckStopMetaData.TABLENAME,conn);
  }
  public CheckStopRow[] toCheckStopRowArray()throws Exception{
    return (CheckStopRow[])list().toArray(new CheckStopRow[0]);
  }
  public CheckStopRow getFirstCheckStopRow()throws Exception{
    return (CheckStopRow)getFirstRow();
  }
  public CheckStopResultSet toCheckStopResultSet()throws SQLException{
    return new CheckStopResultSet(toResultSet());
  }
  public void setPrimaryKey(Object value){
    fieldMap.put("pname",value);
  }
  public String getPname(){
    return (String)fieldMap.get(CheckStopMetaData.pName);
  }
  public void setPname(String value){
    fieldMap.put(CheckStopMetaData.pName,value);
  }
  public BigDecimal getLastCheckTime(){
    return (BigDecimal)fieldMap.get(CheckStopMetaData.lastCheckTime);
  }
  public void setLastCheckTime(BigDecimal value){
    fieldMap.put(CheckStopMetaData.lastCheckTime,value);
  }
  public BigDecimal getInterTime(){
    return (BigDecimal)fieldMap.get(CheckStopMetaData.interTime);
  }
  public void setInterTime(BigDecimal value){
    fieldMap.put(CheckStopMetaData.interTime,value);
  }
  public String getAlertList(){
    return (String)fieldMap.get(CheckStopMetaData.alertList);
  }
  public void setAlertList(String value){
    fieldMap.put(CheckStopMetaData.alertList,value);
  }
  public String getMailList(){
    return (String)fieldMap.get(CheckStopMetaData.mailList);
  }
  public void setMailList(String value){
    fieldMap.put(CheckStopMetaData.mailList,value);
  }
  public boolean getValid(){
    return ((Boolean)fieldMap.get(CheckStopMetaData.Valid)).booleanValue();
  }
  public void setValid(boolean value){
    fieldMap.put(CheckStopMetaData.Valid,new Boolean(value));
  }
	@Override public Object getMetaData() {
		return new CheckStopMetaData();
	}
}
