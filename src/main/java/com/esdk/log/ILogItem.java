package com.esdk.log;

public interface ILogItem{
  void setTimeFormat(String format);
  void setName(String value);
  void setDelimiter(String delimit);
  void setContent(String value);
  void setEndChar(String value);
  void setLevel(ILogLevel level);
  void setResult(String value);
  
  long getID();
  String getTime();
  String getName();
  String getContent();
  String getEndChar();
  String getDelimiter();
  String getResult();

  int size();

//  String getResultNoTime();
//  String getResultNoID();
  ILogLevel getLevel();
}
