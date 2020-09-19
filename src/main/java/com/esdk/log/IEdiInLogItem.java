package com.esdk.log;

import java.math.BigDecimal;

public interface IEdiInLogItem extends ILogItem{
  void setErr(boolean value);
  void setMsgID(int value);
  void setMsgCode(String value);
  void setMsg(String value);
  void setIdentification(String value);
  void setOrderNumber(String value);
  void setOuterNumber(String value);
  void setClientID(BigDecimal value);
  void setOrderTypeID(BigDecimal value);
//  void setLastTime(BigDecimal value);
  boolean isErr();
  int getMsgID();
  String getMsgCode();
  String getMsg();
  String getIdentification();
  String getOrderNumber();
  String getOuterNumber();
  BigDecimal getClientID();
  BigDecimal getOrderTypeID();
}
