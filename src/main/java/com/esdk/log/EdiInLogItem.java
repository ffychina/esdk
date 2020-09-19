package com.esdk.log;

import java.math.BigDecimal;

public class EdiInLogItem extends LogItem implements IEdiInLogItem{
  private boolean isErr;
  private int msgID;
  private String msg;
  private String identification;
  private String msgCode;
  private String orderNumber;
  private String outerNumber;
  private BigDecimal clientID;
  private BigDecimal orderTypeID;
  
  public void setErr(boolean value){
    isErr=value;
  }

  public void setMsgID(int value){
    msgID=value;
  }

  public void setMsgCode(String value){
    msgCode=value;
  }

  public void setMsg(String value){
    msg=value;
  }

  public void setIdentification(String value){
    identification=value;
  }

  public void setOrderNumber(String value){
    orderNumber=value;
  }

  public void setOuterNumber(String value){
    outerNumber=value;
  }

  public void setClientID(BigDecimal value){
    clientID=value;
  }

  public void setOrderTypeID(BigDecimal value){
    orderTypeID=value;
  }

  public boolean isErr(){
    return isErr;
  }

  public int getMsgID(){
    return msgID;
  }

  public String getMsgCode(){
    return msgCode;
  }

  public String getMsg(){
    return msg;
  }

  public String getIdentification(){
    return identification;
  }

  public String getOrderNumber(){
    return orderNumber;
  }

  public String getOuterNumber(){
    return outerNumber;
  }

  public BigDecimal getClientID(){
    return clientID;
  }

  public BigDecimal getOrderTypeID(){
    return orderTypeID;
  }
  
  public static void main(String[] args){}
}
