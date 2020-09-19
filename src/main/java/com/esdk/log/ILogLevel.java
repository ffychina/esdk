package com.esdk.log;

public interface ILogLevel{
  String getProperty();
  ILogLevel getNextlowerLevel();
  ILogLevel getNextHigherLevel();
  String[] getLevelArray();
  int getLevel();
}
