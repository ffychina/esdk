package com.esdk.log;

public class CustomizeLogLevel extends AbstractLogLevel{
  final static int _WAREHOUSE=0,_TRANSPORT=1,_MANAGER=2,_DEVELOPER=3,_OTHER=5;
  final static String[] _LEVEL=new String[] {"warehouse","transport","manager","developer","other"};
  public CustomizeLogLevel(int iLevel) throws IndexOutOfBoundsException{
    super(iLevel);
    _levelCode=_LEVEL;
  }
  public ILogLevel getInstance(int iLevel){
    return new CustomizeLogLevel(iLevel);
  }
  
  public static void test() {
    ILogLevel level=new CustomizeLogLevel(_TRANSPORT);
    System.out.println(level.getProperty().equals(_LEVEL[_TRANSPORT]));
    System.out.println(level.getNextHigherLevel().getProperty().equals(_LEVEL[_MANAGER]));
    System.out.println(level.getNextlowerLevel().getProperty().equals(_LEVEL[_WAREHOUSE]));
    System.out.println(level.getNextHigherLevel().equals(new CustomizeLogLevel(_MANAGER)));
    System.out.println(level.getNextlowerLevel().getNextlowerLevel()==null);
  }
  
  public static void main(String[] args){
    test();
  }
}
