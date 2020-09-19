package com.esdk.test.orm;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import com.esdk.sql.ISQL;
import com.esdk.sql.orm.AbstractSelect;
public class OrderFeedbackExtentSelect extends AbstractSelect implements ISQL{
  public OrderFeedbackExtentSelect(Connection conn){
    super(OrderFeedbackExtentMetaData.TABLENAME,conn);
  }
  public OrderFeedbackExtentRow[] toOrderFeedbackExtentRowArray()throws Exception{
    return (OrderFeedbackExtentRow[])list().toArray(new OrderFeedbackExtentRow[0]);
  }
  public OrderFeedbackExtentRow getFirstOrderFeedbackExtentRow()throws Exception{
    return (OrderFeedbackExtentRow)getFirstRow();
  }
  public OrderFeedbackExtentResultSet toOrderFeedbackExtentResultSet()throws SQLException{
    return new OrderFeedbackExtentResultSet(toResultSet());
  }
  public void setPrimaryKey(Object value){
    fieldMap.put("OrderID",value);
  }
  public BigDecimal getOrderID(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.OrderID);
  }
  public void setOrderID(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.OrderID,value);
  }
  public BigDecimal getClientID(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.ClientID);
  }
  public void setClientID(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.ClientID,value);
  }
  public BigDecimal getOrderTypeID(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.OrderTypeID);
  }
  public void setOrderTypeID(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.OrderTypeID,value);
  }
  public String getOrderNumber(){
    return (String)fieldMap.get(OrderFeedbackExtentMetaData.OrderNumber);
  }
  public void setOrderNumber(String value){
    fieldMap.put(OrderFeedbackExtentMetaData.OrderNumber,value);
  }
  public String getFeedbackResult(){
    return (String)fieldMap.get(OrderFeedbackExtentMetaData.FeedbackResult);
  }
  public void setFeedbackResult(String value){
    fieldMap.put(OrderFeedbackExtentMetaData.FeedbackResult,value);
  }
  public String getCurrentStep(){
    return (String)fieldMap.get(OrderFeedbackExtentMetaData.CurrentStep);
  }
  public void setCurrentStep(String value){
    fieldMap.put(OrderFeedbackExtentMetaData.CurrentStep,value);
  }
  public BigDecimal getWarehouseOutFeedbackTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.WarehouseOutFeedbackTime);
  }
  public void setWarehouseOutFeedbackTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.WarehouseOutFeedbackTime,value);
  }
  public BigDecimal getWarehouseInFeedbackTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.WarehouseInFeedbackTime);
  }
  public void setWarehouseInFeedbackTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.WarehouseInFeedbackTime,value);
  }
  public BigDecimal getFirstCarryFeedbackTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.FirstCarryFeedbackTime);
  }
  public void setFirstCarryFeedbackTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.FirstCarryFeedbackTime,value);
  }
  public BigDecimal getLastCarryFeedbackTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.LastCarryFeedbackTime);
  }
  public void setLastCarryFeedbackTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.LastCarryFeedbackTime,value);
  }
  public BigDecimal getFullCarryFeedbackTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.FullCarryFeedbackTime);
  }
  public void setFullCarryFeedbackTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.FullCarryFeedbackTime,value);
  }
  public BigDecimal getArrivedFeedbackTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.ArrivedFeedbackTime);
  }
  public void setArrivedFeedbackTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.ArrivedFeedbackTime,value);
  }
  public BigDecimal getSignFeedbackTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.SignFeedbackTime);
  }
  public void setSignFeedbackTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.SignFeedbackTime,value);
  }
  public BigDecimal getFinanceFeedbackTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.FinanceFeedbackTime);
  }
  public void setFinanceFeedbackTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.FinanceFeedbackTime,value);
  }
  public boolean getIsFeedbackFinish(){
    return ((Boolean)fieldMap.get(OrderFeedbackExtentMetaData.IsFeedbackFinish)).booleanValue();
  }
  public void setIsFeedbackFinish(boolean value){
    fieldMap.put(OrderFeedbackExtentMetaData.IsFeedbackFinish,new Boolean(value));
  }
  public BigDecimal getHandleTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.HandleTime);
  }
  public void setHandleTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.HandleTime,value);
  }
  public boolean getValid(){
    return ((Boolean)fieldMap.get(OrderFeedbackExtentMetaData.Valid)).booleanValue();
  }
  public void setValid(boolean value){
    fieldMap.put(OrderFeedbackExtentMetaData.Valid,new Boolean(value));
  }
  public BigDecimal getReceiptTime(){
    return (BigDecimal)fieldMap.get(OrderFeedbackExtentMetaData.ReceiptTime);
  }
  public void setReceiptTime(BigDecimal value){
    fieldMap.put(OrderFeedbackExtentMetaData.ReceiptTime,value);
  }
	@Override public Object getMetaData() {
		return new OrderFeedbackExtentMetaData();
	}
}
