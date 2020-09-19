package com.esdk.log;

public class DefaultLoggerConfiguration{
  public static void config(Logger logger){
    logger.setLogFormater(new DefaultLayout());
    
    ILogExporter appender=AppenderFactory.createAppender(logger.getLoggerName(),new String[]{AppenderFactory.CONSOLE,AppenderFactory.LIMITFILE});
    logger.setLogExporter(appender);
    
    ILogExporter duplicationAppender=appender;
    try{
      while(duplicationAppender!=null){
        LevelEffective levelEffective=null;
        if(duplicationAppender.getClass().equals(ConsoleAppender.class)){
          levelEffective=new LevelEffective();
          setEffectiveLevels(levelEffective,DefaultLogLevel.class,new int[]{DefaultLogLevel._DEBUG,DefaultLogLevel._INFO,DefaultLogLevel._WARN,DefaultLogLevel._ERROR,DefaultLogLevel._FATAL});
        }
        else if(duplicationAppender.getClass().equals(LimitCapacityFileAppend.class)){
          levelEffective=new LevelEffective();
          setEffectiveLevels(levelEffective,DefaultLogLevel.class,new int[]{1,2,3,4});
          duplicationAppender.setLevelEffective(levelEffective);
        }
        else if(duplicationAppender.getClass().equals(DailyFileAppender.class)){
          levelEffective=new LevelEffective();
          setEffectiveLevels(levelEffective,DefaultLogLevel.class,new int[]{1,2,3,4});
        }
        else if(duplicationAppender.getClass().equals(SocketAppender.class)){
          levelEffective=new LevelEffective();
          setEffectiveLevels(levelEffective,DefaultLogLevel.class,new int[]{2,3,4});
        }
        else if(duplicationAppender instanceof ILogExporter){
          levelEffective=new LevelEffective();
          setEffectiveLevels(levelEffective,DefaultLogLevel.class,new int[]{0,1,2,3,4});
        }
        duplicationAppender.setLevelEffective(levelEffective);
        duplicationAppender=duplicationAppender.nextExporter();
      }
    }
    catch(IndexOutOfBoundsException e){
    	throw new RuntimeException(e);
    }
    catch(Exception e){
    	throw new RuntimeException(e);
    }
  }
  
  protected static void setEffectiveLevels(ILevelEffective levelEffective,Class c,int[] levelCodes)throws Exception{
    ILogLevel[] levels= new ILogLevel[levelCodes.length];
    for(int i=0;i<levels.length;i++){
      java.lang.reflect.Constructor constructor = c.getConstructor(new Class[] {int.class});
      Object obj=constructor.newInstance(new Object[] {new Integer(levelCodes[i])});
      levels[i]=(ILogLevel)obj;
    }
    levelEffective.setEffectiveLevels(levels);
  }
  
}
