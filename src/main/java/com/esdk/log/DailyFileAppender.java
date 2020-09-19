package com.esdk.log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.esdk.utils.EasyFile;
import com.esdk.utils.EasyTime;
import com.esdk.utils.EasyObj;
public class DailyFileAppender extends SimpleFileAppender{
  private String oldDate,newDate;
  public DailyFileAppender(){
  }

  private void truncateCurrentFile(File file,String backupFileName){
    try{
      _outStream.close();
      if(new File(backupFileName).exists())
        throw new IOException("file already exist: "+backupFileName);
      if(file.renameTo(new File(backupFileName))){
        _outStream=new FileOutputStream(file,true);
      }
      else{
        throw new IOException("can't rename "+file.getPath()+" to "+backupFileName);
      }
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
  
  private String getBackupName(File sourcefile){
    String descfilename=sourcefile.getPath();
    descfilename=sourcefile.getParent().concat("/").concat(EasyFile.getFileNameNotExt(descfilename)).concat(EasyTime.getBeforeTime("yyMMdd",1)).concat(".").concat(EasyFile.getFileExtName(descfilename));
    return descfilename;
  }

  public void append(ILogItem item){
    if(isTimeCanTruncateCurrentFile())
      truncateCurrentFile(_logFile,getBackupName(_logFile));
    super.append(item);
  }
  
  private boolean isTimeCanTruncateCurrentFile(){
    newDate=EasyTime.getNowTime("yyMMdd");
    if(oldDate==null)
      oldDate=EasyTime.getNowTime("yyMMdd");
    if(!newDate.equals(oldDate)){
      oldDate=newDate;
      return true;
    }
    return false;
//    return true; //debug
  }

}
