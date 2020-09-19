package com.esdk.test.orm;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.esdk.sql.Select;

/**
 * function:
 * select * from ordermaster where ....
 */

public class SampleInsert{
  Select select;
  private BigDecimal _orderID;
  private String _orderNumber;
  private boolean _valid;
  
  public SampleInsert(Connection conn) {
   select=new Select("OrderMaster",conn);
  }
  
  public ResultSet toResultSet() throws SQLException {
    return select.toResultSet();
  }
  
  public void setOrderID(BigDecimal orderid) {
    _orderID=orderid;
  }
  
  public BigDecimal getOrderID() {
    return _orderID;
  }
  
  public void setOrderNumber(String ordernumber) {
    _orderNumber=ordernumber;
  }

  public String getOrderNumber() {
    return _orderNumber;
  }
  
  public void setValid(boolean valid) {
    _valid=valid;
  }
  
  public boolean getValid() {
    return _valid;
  }
}
