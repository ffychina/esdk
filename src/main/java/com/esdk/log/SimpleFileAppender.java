package com.esdk.log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.esdk.exception.SdkRuntimeException;
import com.esdk.interfaces.IRequestClose;
import com.esdk.utils.EasyFile;
public class SimpleFileAppender extends AbstractAppender implements IRequestClose{
  protected OutputStream _outStream;
  protected File _logFile;

  public SimpleFileAppender(){}

  public SimpleFileAppender(String logName) throws FileNotFoundException{
    setLogName(logName);
  }

  public void close()throws IOException{
    if(_outStream!=null)
      _outStream.close();
  }
  
  public void setLogName(String logName) throws FileNotFoundException{
    if(logName==null)
      logName="/logs/untitled.log";
    setLogFile(new File(logName));
  }

  public void setLogFile(File logfile) throws FileNotFoundException{
  	this._logFile=logfile;
    AutoCreateFileAndPath(_logFile);
    _outStream=new FileOutputStream(_logFile,true);
  }

  public void append(ILogItem item){
    try{
      checkOutStream(_outStream);
      _outStream.write(item.getResult().getBytes(EasyFile.UTF8));
    }
    catch(Exception e){
      throw new SdkRuntimeException(e);
    }
  }

  private void checkOutStream(OutputStream outStream) throws Exception{
    if(outStream==null)
      throw new Exception("outputStream can't not null.");
  }

  protected boolean AutoCreateFileAndPath(File file){
    try{
      if(!file.exists()){
        file.getParentFile().mkdirs();
        return file.createNewFile();
      }
      return true;
    }
    catch(IOException e){
      e.printStackTrace();
      return false;
    }
  }
}
