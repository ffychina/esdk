package com.esdk.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;

import com.esdk.utils.EasyFile;
import com.esdk.utils.EasyObj;

public class AppenderFactory{
  final static String CONSOLE="CONSOLE",SIMPLEFILE="SIMPLEFILE",LIMITFILE="LIMITFILE",DAILYFILE="DAILYFILE",MAIL="MAIL",SOCKET="SOCKET";

  public static ILogExporter createAppender(){
    return createAppender(CONSOLE);
  }

  public static ILogExporter createAppender(String appenderName){
    return createAppender(new String[]{appenderName});
  }
  
  public static ILogExporter createAppender(String[] appenderNames){
    return createAppender("worker",appenderNames);
  }
  
  public static ILogExporter createAppender(String logName,String[] appenderNames){
  	return createAppender(logName,null,appenderNames);
  }

  public static ILogExporter createAppender(String logName,String logPath,String[] appenderNames){
    String path=EasyObj.isBlank(logPath)?"./log/":EasyFile.getDirPath(logPath);
    String extName=".log";
  	File logFile=new File(path+logName+extName);
  	return createAppender(logFile,appenderNames);
  }

  public static ILogExporter createAppender(File logFile,String[] appenderNames){
   Vector appenders=new Vector();
    if(appenderNames==null)
      return null;
    for(int i=0;i<appenderNames.length;i++){
      try{
        if(appenderNames[i].equalsIgnoreCase(CONSOLE)){
          appenders.add(new ConsoleAppender());
        }
        else if(appenderNames[i].equalsIgnoreCase(SIMPLEFILE)){
          SimpleFileAppender tempExporter=new SimpleFileAppender();
          tempExporter.setLogName(null);
          appenders.add(tempExporter);
        }
        else if(appenderNames[i].equalsIgnoreCase(LIMITFILE)){
            LimitCapacityFileAppend tempExporter=new LimitCapacityFileAppend();
            tempExporter.setLogFile(logFile,FileLoggerConfiguration.LogLimitMBSize);
            appenders.add(tempExporter);
        }
        else if(appenderNames[i].equalsIgnoreCase(DAILYFILE)){
            DailyFileAppender tempExporter=new DailyFileAppender();
            tempExporter.setLogFile(logFile);
            appenders.add(tempExporter);
        }
        else if(appenderNames[i].equalsIgnoreCase(SOCKET)){
          SocketAppender tempExporter=new SocketAppender();
          appenders.add(tempExporter);
        }
      }
      catch(FileNotFoundException e){
      	throw new RuntimeException(e);
      }
      catch(Exception e){
      	throw new RuntimeException(e);
      }
    }
    return addAppender(appenders);
  }
  
  private static ILogExporter addAppender(Vector appenders) {
    ILogExporter firstAppender=null;
    Iterator iter=appenders.iterator();
    if(iter.hasNext())
      firstAppender=(ILogExporter)iter.next();
    ILogExporter appender=firstAppender;
    while(iter.hasNext()) {
      appender.addNextExporter((ILogExporter)iter.next());
      appender=appender.nextExporter();
    }
    return firstAppender;
  }
  
  public static void main(String[] args){
    AppenderFactory.createAppender();
    AppenderFactory.createAppender(CONSOLE);
    AppenderFactory.createAppender(new String[]{CONSOLE,LIMITFILE,DAILYFILE});
  }
}
