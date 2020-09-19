package com.esdk.log;
import java.util.ArrayList;
public class LevelEffective implements ILevelEffective{
  ArrayList _includeLevel;

  public LevelEffective(){
    _includeLevel=new ArrayList();
  }

  public void addEffectiveLevel(ILogLevel level){
    if(!_includeLevel.contains(level))
      _includeLevel.add(level);
  }

  public void setEffectiveLevels(ILogLevel[] levels){
    for(int i=0;i<levels.length;i++){
      addEffectiveLevel(levels[i]);
    }
  }

  public boolean containLevel(ILogLevel level){
    return _includeLevel.contains(level)?true:false;
  }

  public static void test(){
    LevelEffective le=new LevelEffective();
    le.setEffectiveLevels(new DefaultLogLevel[]{
      new DefaultLogLevel(DefaultLogLevel._INFO),
      new DefaultLogLevel(DefaultLogLevel._INFO),
      new DefaultLogLevel(DefaultLogLevel._WARN),
      new DefaultLogLevel(DefaultLogLevel._ERROR),
      new DefaultLogLevel(DefaultLogLevel._FATAL)     
      });
    System.out.println(le.containLevel(new DefaultLogLevel(DefaultLogLevel._INFO))); 
    System.out.println(!le.containLevel(new DefaultLogLevel(DefaultLogLevel._DEBUG))); 
  }
  
  public static void main(String[] args){
    test();
  }
}
