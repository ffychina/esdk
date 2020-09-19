package com.esdk.test.orm;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.esdk.sql.orm.ParentResultSet;
import com.esdk.sql.orm.ParentRow;
public class OrderFeedbackExtentResultSet extends ParentResultSet{
  public OrderFeedbackExtentResultSet(ResultSet value){
    super(value);
  }
  public ParentRow createRowInstance(){
    return new OrderFeedbackExtentRow();
  }
  public OrderFeedbackExtentRow getCurrentRow()throws SQLException{
    return (OrderFeedbackExtentRow)super.gainCurrentRow(rs);
  }
  public OrderFeedbackExtentRow[] getAllRow()throws SQLException{
    return (OrderFeedbackExtentRow[])gainAllRow().toArray(new OrderFeedbackExtentRow[0]);
  }
  public BigDecimal getOrderID()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.OrderID);
  }
  public BigDecimal getClientID()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.ClientID);
  }
  public BigDecimal getOrderTypeID()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.OrderTypeID);
  }
  public String getOrderNumber()throws SQLException{
    return rs.getString(OrderFeedbackExtentMetaData.OrderNumber);
  }
  public String getFeedbackResult()throws SQLException{
    return rs.getString(OrderFeedbackExtentMetaData.FeedbackResult);
  }
  public String getCurrentStep()throws SQLException{
    return rs.getString(OrderFeedbackExtentMetaData.CurrentStep);
  }
  public BigDecimal getWarehouseOutFeedbackTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.WarehouseOutFeedbackTime);
  }
  public BigDecimal getWarehouseInFeedbackTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.WarehouseInFeedbackTime);
  }
  public BigDecimal getFirstCarryFeedbackTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.FirstCarryFeedbackTime);
  }
  public BigDecimal getLastCarryFeedbackTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.LastCarryFeedbackTime);
  }
  public BigDecimal getFullCarryFeedbackTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.FullCarryFeedbackTime);
  }
  public BigDecimal getArrivedFeedbackTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.ArrivedFeedbackTime);
  }
  public BigDecimal getSignFeedbackTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.SignFeedbackTime);
  }
  public BigDecimal getFinanceFeedbackTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.FinanceFeedbackTime);
  }
  public boolean getIsFeedbackFinish()throws SQLException{
    return rs.getBoolean(OrderFeedbackExtentMetaData.IsFeedbackFinish);
  }
  public BigDecimal getHandleTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.HandleTime);
  }
  public boolean getValid()throws SQLException{
    return rs.getBoolean(OrderFeedbackExtentMetaData.Valid);
  }
  public BigDecimal getReceiptTime()throws SQLException{
    return rs.getBigDecimal(OrderFeedbackExtentMetaData.ReceiptTime);
  }
}
