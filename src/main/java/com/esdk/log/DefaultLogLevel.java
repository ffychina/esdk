package com.esdk.log;

public class DefaultLogLevel extends AbstractLogLevel{
  public final static int _DEBUG=0,_INFO=1,_WARN=2,_ERROR=3,_FATAL=4;
  public final static String[] _LEVEL=new String[] {"debug","info","warn","error","fatal"};
  public final static String[] _levelChineseName= {"调试","信息","警告","错误","致命错误"};
  
  public DefaultLogLevel(int iLevel) throws IndexOutOfBoundsException{
    super(iLevel);
    _levelCode=_LEVEL;
  }
  public ILogLevel getInstance(int iLevel){
    return new DefaultLogLevel(iLevel);
  }
  
  
  protected void checkLevel(int iLevel)throws IndexOutOfBoundsException{
    if(iLevel<0&&iLevel>=getLevelArray().length)
      throw new IndexOutOfBoundsException("Index: "+iLevel+", Size: "+getLevelArray().length);
  }
  
  public static void test() {
    ILogLevel level=new DefaultLogLevel(_INFO);
    System.out.println(level.getProperty().equals(_LEVEL[_INFO]));
    System.out.println(level.getNextHigherLevel().getProperty().equals(_LEVEL[_WARN]));
    System.out.println(level.getNextlowerLevel().getProperty().equals(_LEVEL[_DEBUG]));
    System.out.println(level.getNextHigherLevel().equals(new DefaultLogLevel(_WARN)));
    System.out.println(level.getNextlowerLevel().getNextlowerLevel()==null);
  }
  
  public static void main(String[] args){
    test();
  }
}
