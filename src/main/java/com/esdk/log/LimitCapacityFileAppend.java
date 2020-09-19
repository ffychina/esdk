package com.esdk.log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.esdk.utils.EasyFile;
import com.esdk.utils.EasyTime;
public class LimitCapacityFileAppend extends SimpleFileAppender{
  private int _limitLogByteSize;
  private long _filesize;
  public LimitCapacityFileAppend(){
  	_filesize=0;
  }

  public void setLogName(String infoLogName,int limitInfoLogMBSize)throws Exception{
  	setLogFile(new File(infoLogName),limitInfoLogMBSize);
  }
  
  public void setLogFile(File logfile,int limitInfoLogMBSize)throws Exception{
    _logFile=logfile;
    _limitLogByteSize=limitInfoLogMBSize*1024*1024;
    initializeLimitSizeStream(_logFile,_limitLogByteSize);
  }

  protected void initializeLimitSizeStream(File file,long limitbyteSize)throws FileNotFoundException,IOException{
    AutoCreateFileAndPath(file);
    _outStream=new FileOutputStream(file,true);
    if(isExceedLimitSize((FileOutputStream)_outStream,limitbyteSize))
      truncateCurrentFile(file,getBackupName(file));
  }

  private boolean isExceedLimitSize(FileOutputStream stream,long limitSize) throws IOException{
    FileChannel fc=stream.getChannel();
//    System.out.println(fc.size());
    _filesize=fc.size();
    return _filesize>=limitSize?true:false;
  }
  
  private void truncateCurrentFile(File file,String backupFileName){
    try{
      _outStream.close();
      if(file.renameTo(new File(backupFileName))) {
        _outStream=new FileOutputStream(file,true);
        _filesize=0;
      }
      else {
      	_filesize=0;
        throw new IOException("can't rename "+file.getPath()+" to "+backupFileName);
      }
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
  
  private String getBackupName(File sourcefile){
    String descfilename=sourcefile.getPath();
    descfilename=sourcefile.getParent()+"/"+EasyFile.getFileNameNotExt(descfilename)+"_"+EasyTime.getNowTime("yyMMddHHmmss")+"."+EasyFile.getFileExtName(descfilename);
    return descfilename;
    
  }
  
  public void append(ILogItem item){
    if(isExceedFileSize())
      truncateCurrentFile(_logFile,getBackupName(_logFile));
    super.append(item);
    _filesize+=item.size();
  }
  
  private boolean isExceedFileSize(){
  	return _filesize>=_limitLogByteSize?true:false;
  }

}
