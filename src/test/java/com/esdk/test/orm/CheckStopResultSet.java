package com.esdk.test.orm;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.esdk.sql.orm.ParentResultSet;
import com.esdk.sql.orm.ParentRow;
public class CheckStopResultSet extends ParentResultSet{
  public CheckStopResultSet(ResultSet value){
    super(value);
  }
  public ParentRow createRowInstance(){
    return new CheckStopRow();
  }
  public CheckStopRow getCurrentRow()throws SQLException{
    return (CheckStopRow)super.gainCurrentRow(rs);
  }
  public CheckStopRow[] getAllRow()throws SQLException{
    return (CheckStopRow[])gainAllRow().toArray(new CheckStopRow[0]);
  }
  public String getPname()throws SQLException{
    return rs.getString(CheckStopMetaData.pName);
  }
  public BigDecimal getLastCheckTime()throws SQLException{
    return rs.getBigDecimal(CheckStopMetaData.lastCheckTime);
  }
  public BigDecimal getInterTime()throws SQLException{
    return rs.getBigDecimal(CheckStopMetaData.interTime);
  }
  public String getAlertList()throws SQLException{
    return rs.getString(CheckStopMetaData.alertList);
  }
  public String getMailList()throws SQLException{
    return rs.getString(CheckStopMetaData.mailList);
  }
  public boolean getValid()throws SQLException{
    return rs.getBoolean(CheckStopMetaData.Valid);
  }
}
