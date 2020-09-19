package com.esdk.log;

public interface ILevelEffective{
  void addEffectiveLevel(ILogLevel level) ;
  void setEffectiveLevels(ILogLevel[] levels);
  boolean containLevel(ILogLevel level);
}
