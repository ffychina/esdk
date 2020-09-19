package com.esdk.log;
import java.io.InputStream;
import java.util.Properties;

import com.esdk.utils.EasyFile;
public class FileLoggerConfiguration extends DefaultLoggerConfiguration{
  public static int LogLimitMBSize=20;
  public static void config(Logger logger){
    final String configFileName="log.config";
    config(logger,configFileName);
  }

  public static void config(Logger logger,String configFileName){
    InputStream is=EasyFile.getInputStream(configFileName);
    if(is==null)
      DefaultLoggerConfiguration.config(logger);
    else{
      Properties prop=new Properties();
      try{
        prop.load(is);
        LogLimitMBSize=Integer.valueOf(prop.getProperty("LogLimitMBSize",LogLimitMBSize+""));
        Class clsformater=Class.forName(prop.getProperty("ILogFormater"));
        logger.setLogFormater((ILogFormater)clsformater.newInstance());
        String[] appenderNames=prop.getProperty("ILogExporter").split(",");
        ILogExporter appender=null;
        if(logger.getLogFile()==null)
        	appender=AppenderFactory.createAppender(prop.getProperty("FileAppenderPath"),appenderNames);
        else
        	appender=AppenderFactory.createAppender(logger.getLogFile(),appenderNames);
        logger.setLogExporter(appender);
        for(int i=0;i<appenderNames.length;i++){
          LevelEffective levelEffective=null;
          levelEffective=new LevelEffective();
          Class clsLogLevel=Class.forName(prop.getProperty(appenderNames[i]+".LevelEffective.ILogLevel"));
          String[] levels=prop.getProperty(appenderNames[i]+".LevelEffective.setEffectiveLevels").split(",");
          int[] ilevel=new int[levels.length];
          for(int j=0;j<ilevel.length;j++){
            ilevel[j]=Integer.valueOf(levels[j]).intValue();
          }
          setEffectiveLevels(levelEffective,clsLogLevel,ilevel);
          appender.setLevelEffective(levelEffective);
          appender=appender.nextExporter();
        }
        is.close();
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }
}
