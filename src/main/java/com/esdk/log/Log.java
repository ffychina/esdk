package com.esdk.log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.esdk.utils.EasyFile;
import com.esdk.utils.EasyTime;

public class Log{
	private StringBuffer _AllMessage;
  private StringBuffer _AllErrMessage;
  private StringBuffer _AllNotificationMessage;
  private StringBuffer _noTimeErrMessage = new StringBuffer();

  private int lastgetindex=-1;
  private int lasterrgetindex=-1;
  private int lastnotificationindex=-1;
  private java.io.BufferedWriter outLog;
  private java.io.BufferedWriter outErr;
  private long MaxFileLength=0;
  private long MaxErrFileLength=0;
  private long MaxMemoryLength=1024*1024;
  private boolean isRealTimeDisplay=false;
  public static final char flagErr='!',flagOnlydisp='#',flagOnlyLog='$',flagNotification='*';//according to 1,2,4,8
  public static final int iNormal=0,iErr=1,iDisplay=2,iLog=4,iNotify=8;
  public static boolean debugMode = false;

  public Log(String LogFileName,int LogMaxMBSize,String ErrLogFileName,int ErrMaxMBSize){
    _AllMessage=new StringBuffer();
    _AllErrMessage=new StringBuffer();
    _AllNotificationMessage=new StringBuffer();
    lastgetindex=0;
    lasterrgetindex=0;
    lastnotificationindex=0;
    setMaxFileLength(LogMaxMBSize);
    setErrMaxFileLength(ErrMaxMBSize);
   try{
      setLogFileName(LogFileName);
      setErrFileName(ErrLogFileName);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
  }

  public void setMaxFileLength(int MBSize){
    if(MBSize<=0)
      MBSize=100;
    MaxFileLength=MBSize*1024*1024;

  }

  public void setErrMaxFileLength(int MBSize){
    if(MBSize<=0)
      MBSize=100;
    MaxErrFileLength=MBSize*1024*1024;
  }

  public void setLogFileName(String filename)throws IOException{
    File file=new File(filename);
    if(file.length()>MaxFileLength)
      if(!renameFileName(file))
	throw new IOException("文件".concat(file.getName()).concat("改名失败"));
    outLog=new BufferedWriter(new FileWriter(file,true));
  }

  public void setErrFileName(String filename)throws IOException{
    File file=new File(filename);
    if(file.length()>MaxErrFileLength)
      if(!renameFileName(file))
	throw new IOException("文件".concat(file.getName()).concat("改名失败"));
    outErr=new BufferedWriter(new FileWriter(file,true));
  }

  private boolean renameFileName(File sourcefile){
    boolean result=false;
    if(!EasyFile.isFileLock(sourcefile)){
      String descfilename=sourcefile.getAbsolutePath();
      descfilename=sourcefile.getParent()+"/"+EasyFile.getFileNameNotExt(descfilename)+EasyTime.getNowTime("yyMMddHHmmss")+"."+EasyFile.getFileExtName(descfilename);
      if(sourcefile.renameTo(new File(descfilename)))
	result=true;
    }
    return result;
  }

  public void flush()throws IOException{
    outErr.flush();
    outLog.flush();
  }

  public void addMessage(String msg){
    int flag=0;
    if(msg.indexOf(flagErr)>=0){
      flag+=1;
    }
    else if(msg.indexOf(flagOnlydisp)>=0){
      flag+=2;
    }
    else if(msg.indexOf(flagOnlyLog)>=0){
      flag+=4;
    }
    else if(msg.indexOf(flagNotification)>=0){
      flag+=8;
    }
    addMessage(msg,flag);
  }

  private void addMessage(String msg,int flag){
    switch(flag){
      case 0:{
	addMessage(msg,false,true,true);break;
      }
      case 1:{
	addMessage(msg,true,true,true);break;
      }
      case 2:{
	addMessage(msg,true,true,false);break;
      }
      case 3:{
	addMessage(msg,false,true,false);break;
      }
      case 4:{
	addMessage(msg,false,false,true);break;
      }
      case 5:{
	addMessage(msg,true,false,true);break;
      }
      case 6:{
	addMessage(msg,false,true,true);break;
      }
      case 7:{
	addMessage(msg,true,true,true);
      }
      case 8:{
	addMessage(msg,false,true,true,true);
      }
    }
  }

  public void addMessage(String msg,boolean isErr,boolean isDisplay,boolean isSaveLog){
    addMessage(msg,isErr,isDisplay,isSaveLog,false);
  }

  public void addMessage(String msg,boolean isErr,boolean isDisplay,boolean isSaveLog,boolean isNotify){
    try{
      if(isErr){
	_noTimeErrMessage.append(msg);
      }
      msg=EasyTime.getNowTime("y-M-d HH:mm:ss").concat(" ").concat(msg);
      if(isDisplay || debugMode)
	_AllMessage.append(msg);
      if(isSaveLog){
	outLog.write(msg);
	outLog.flush();
      }
      if(isNotify){
	_AllNotificationMessage.append(msg);
      }
      if(isErr){
	_AllErrMessage.append(msg);
	outErr.write(msg);
	outErr.flush();
      }
      if(isRealTimeDisplay || debugMode)
	if(isErr)
	  System.err.print(getNewMessage());
	else
	  System.out.print(getNewMessage());
      else
	getNewMessage();
    }
    catch(IOException ex){
      ex.printStackTrace();
    }
  }

  public String getAllMessage(){
    return _AllMessage.toString();
  }

  public String getAllErrMessage(){
    return _AllErrMessage.toString();
  }

  public String getAllNotificationMessage(){
    return _AllNotificationMessage.toString();
  }

  public String getNewMessage(){
    String result=_AllMessage.substring(lastgetindex);
    lastgetindex=_AllMessage.length();
    if(_AllMessage.length()>MaxMemoryLength)
      clearMessage();
    return result;
  }

  public String getNewErrMessage(){
    String result=_AllErrMessage.substring(lasterrgetindex);
    lasterrgetindex=_AllErrMessage.length();
    if(_AllErrMessage.length()>MaxMemoryLength)
      clearErrMessage();
    return result;
  }

  public String getNewNotificationMessage(){
    String result=_AllNotificationMessage.substring(lastnotificationindex);
    lastnotificationindex=_AllNotificationMessage.length();
    if(_AllNotificationMessage.length()>MaxMemoryLength)
      clearErrMessage();
    return result;
  }

  public String getNoTimeErrMessage(){
	  String eMsg = _noTimeErrMessage.toString();
	   _noTimeErrMessage.delete( 0 , _noTimeErrMessage.length());
	  return eMsg;
  }
  public boolean hasNewMessage(){
    if(lastgetindex==_AllMessage.length())
      return false;
    return true;
  }

  public boolean hasNewErrMessage(){
    if(lasterrgetindex==_AllErrMessage.length())
      return false;
    return true;
  }

  public boolean hasNewNotificationMessage(){
    if(lastnotificationindex==_AllNotificationMessage.length())
      return false;
    return true;
  }

  public void clearMessage(){
    if(lastgetindex==_AllMessage.length()){
      _AllMessage.delete(0,_AllMessage.length());
      lastgetindex=0;
    }
  }

  public void clearErrMessage(){
    if(lasterrgetindex==_AllErrMessage.length()){
      _AllErrMessage.delete(0,_AllErrMessage.length());
      _noTimeErrMessage.delete(0,_noTimeErrMessage.length());
      lasterrgetindex=0;
    }
  }

  public void clearNotificationMessage(){
    if(lastnotificationindex==_AllNotificationMessage.length()){
      _AllNotificationMessage.delete(0,_AllNotificationMessage.length());
      lasterrgetindex=0;
    }
  }

  public void setisRealTimeDisplay(boolean value){
    isRealTimeDisplay=value;
  }
}
