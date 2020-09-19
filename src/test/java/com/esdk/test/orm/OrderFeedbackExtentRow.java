package com.esdk.test.orm;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import com.esdk.sql.orm.ParentRow;
public class OrderFeedbackExtentRow extends ParentRow{
  public static final String PrimaryKey="OrderID";
  public static final String tableName="OrderFeedbackExtent";
  public ParentRow newSelf(){
    return new OrderFeedbackExtentRow();
  }
  public String getTableName(){
    return tableName;
  }
  public BigDecimal getOrderID()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.OrderID);
  }
  public void setOrderID(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.OrderID,value);
  }
  public BigDecimal getClientID()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.ClientID);
  }
  public void setClientID(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.ClientID,value);
  }
  public BigDecimal getOrderTypeID()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.OrderTypeID);
  }
  public void setOrderTypeID(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.OrderTypeID,value);
  }
  public String getOrderNumber()throws SQLException{
    return (String)get(OrderFeedbackExtentMetaData.OrderNumber);
  }
  public void setOrderNumber(String value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.OrderNumber,value);
  }
  public String getFeedbackResult()throws SQLException{
    return (String)get(OrderFeedbackExtentMetaData.FeedbackResult);
  }
  public void setFeedbackResult(String value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.FeedbackResult,value);
  }
  public String getCurrentStep()throws SQLException{
    return (String)get(OrderFeedbackExtentMetaData.CurrentStep);
  }
  public void setCurrentStep(String value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.CurrentStep,value);
  }
  public BigDecimal getWarehouseOutFeedbackTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.WarehouseOutFeedbackTime);
  }
  public void setWarehouseOutFeedbackTime(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.WarehouseOutFeedbackTime,value);
  }
  public BigDecimal getWarehouseInFeedbackTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.WarehouseInFeedbackTime);
  }
  public void setWarehouseInFeedbackTime(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.WarehouseInFeedbackTime,value);
  }
  public BigDecimal getFirstCarryFeedbackTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.FirstCarryFeedbackTime);
  }
  public void setFirstCarryFeedbackTime(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.FirstCarryFeedbackTime,value);
  }
  public BigDecimal getLastCarryFeedbackTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.LastCarryFeedbackTime);
  }
  public void setLastCarryFeedbackTime(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.LastCarryFeedbackTime,value);
  }
  public BigDecimal getFullCarryFeedbackTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.FullCarryFeedbackTime);
  }
  public void setFullCarryFeedbackTime(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.FullCarryFeedbackTime,value);
  }
  public BigDecimal getArrivedFeedbackTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.ArrivedFeedbackTime);
  }
  public void setArrivedFeedbackTime(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.ArrivedFeedbackTime,value);
  }
  public BigDecimal getSignFeedbackTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.SignFeedbackTime);
  }
  public void setSignFeedbackTime(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.SignFeedbackTime,value);
  }
  public BigDecimal getFinanceFeedbackTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.FinanceFeedbackTime);
  }
  public void setFinanceFeedbackTime(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.FinanceFeedbackTime,value);
  }
  public Boolean getIsFeedbackFinish()throws SQLException{
    return ((Boolean)get(OrderFeedbackExtentMetaData.IsFeedbackFinish));
  }
  public void setIsFeedbackFinish(boolean value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.IsFeedbackFinish,new Boolean(value));
  }
  public BigDecimal getHandleTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.HandleTime);
  }
  public void setHandleTime(Date value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.HandleTime,value);
  }
  public Boolean getValid()throws SQLException{
    return ((Boolean)get(OrderFeedbackExtentMetaData.Valid));
  }
  public void setValid(boolean value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.Valid,new Boolean(value));
  }
  public BigDecimal getReceiptTime()throws SQLException{
    return (BigDecimal)get(OrderFeedbackExtentMetaData.ReceiptTime);
  }
  public void setReceiptTime(BigDecimal value)throws SQLException{
    setNewValue(OrderFeedbackExtentMetaData.ReceiptTime,value);
  }
	@Override public Object getMetaData(){
		return new OrderFeedbackExtentMetaData();
	}
}
