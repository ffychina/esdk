package com.esdk.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Callback2;

import com.esdk.esdk;
import com.esdk.cipher.MD5Utils;
import com.esdk.exception.SdkRuntimeException;

import cn.hutool.core.io.FileUtil;
/***
 * @author 范飞宇
 * @since 2003.?.?
 */
public class EasyFile{
  public static final int _SortByKey=0;
  public static final int _SortByPath=1;
  public static final int _SortByName=2;
  public static final int _SortBySize=3;
  public static final int _SortByType=4;
  public static final int _SortByModifyTime=5;
  public static final int COLLECTIONBYDATE=0;
  public static final int COLLECTIONBYMONTH=1;
  public static final int COLLECTIONBYYEAR=2;
  public static final int _MB=1048576,_KB=1024;
  public static final int Sync_Type_Strict=1,Sync_Type_Last_Modified=2,Sync_Type_Length=3;
  public static final String UTF8="utf8",GB2312="gb2312",GBK="gbk",DEFAULTCHARTSET=GB2312;
  public static final String separator="/";
  public static final String wrongSeparator=separator.equals("/")?"\\":"/";
  
  public static boolean SaveStringArrayToFile(String[][] value,String breaksymbol,String filename){
    String str=EasyStr.arrToCsv(value,breaksymbol);
    try{
      FileWriter outputfile=new FileWriter(filename);
      outputfile.write(str);
      outputfile.close();
      return true;
    }
    catch(IOException ex){
      return false;
    }
  }

  /**删除目录包括子目录*/
	public static boolean deleteDir(File dir){
		if(dir.isDirectory()){
			String[] children=dir.list();
			for(int i=0;i<children.length;i++){
				boolean success=deleteDir(new File(dir,children[i]));
				if(!success){
					return false;
				}
			}
		}
		return dir.delete();
	}

  public static boolean mkdir(File folder){
  	return mkdir(folder,false);
  }
  
  public static boolean mkdir(String folderPath){
  	return mkdir(new File(folderPath),false);
  }
  
  /**isAncestorExist：是否已存在父目录，如果需要自动生成父目录，则该参数应为false*/
  public static boolean mkdir(File folder,boolean isAncestorExist){
    try{
      boolean success=false;
      if(folder.exists()){
        success=true;
        return true;
      }
      if(isAncestorExist){
        success=folder.mkdir();// create a directory;all ancestor directories must exist;
        if(!success){
          success=true;
        }
      }
      else{
        success=folder.mkdirs();// create a directory;all non-ancestor directories are
      }
      return success;
    }
    catch(Exception ex){
      ex.printStackTrace();
      return false;
    }
  }
  
  public static boolean createFolder(String folderPath,boolean isAncestorExist){
  	return mkdir(new File(folderPath),isAncestorExist);
  }

  public static boolean saveToFile(String data,String FileName,boolean isOverWrite,boolean isCreatePath) throws IOException{
    return saveToFile(data,FileName,isOverWrite,isCreatePath,"GB2312");
  }
  
  public static boolean saveToFile(String data,String FileName,boolean isOverWrite,boolean isCreatePath,String charset) throws IOException{
    boolean result=true;
    if(isCreatePath) {
      String path=getFileParentName(FileName);
      result=createFolder(path,false);
    }
    if(result)
      result=saveToFile(data,FileName,isOverWrite,charset);
    return result;
  }

  public static String saveToFile(boolean isCreateSerial,String FileName,String data) throws Exception{
    String result=FileName;
    if(!isCreateSerial) {
      SaveToFile(data,FileName,false);
    }
    else {
    File file=new File(FileName);
    if(!file.exists())
      SaveToFile(data,FileName,false);
    else
      result=saveFileBySerialNumber(FileName,3,data);
    }
    return result;
  }

  public static String saveToFile(boolean isCreateSerial,String FileName,String data,String charset) throws Exception{
    String result=FileName;
    if(!isCreateSerial){
      saveToFile(data,FileName,false,charset);
    }
    else{
      File file=new File(FileName);
      if(!file.exists())
        saveToFile(data,FileName,false,charset);
      else
        result=saveFileBySerialNumber(FileName,3,data,charset);
    }
    return result;
  }

  public static boolean moveFolders(String DestDir,String SourceDir){
    boolean result=false;
    createFolder(DestDir,false);
    File fDest=new File(DestDir);
    File fSource=new File(SourceDir);
    if(fSource.isDirectory()){
      File[] filelist=fSource.listFiles();
      for(int i=0;i<filelist.length;i++){
	if(filelist[i].isFile()){
	  result=filelist[i].renameTo(new File(fDest,filelist[i].getName()));
	}
	else if(filelist[i].isDirectory()){
	  result=moveFolders(getDirPath(DestDir+filelist[i].getName()),getDirPath(filelist[i].getAbsolutePath()));
//          filelist[i].delete();
	}
      }
    }
    result=fSource.delete();
    return result;
  }

  public static boolean rename(File sourcefile,File destfile,boolean isforceRename){
    boolean result;
    if(!sourcefile.exists())
      return false;
    result=sourcefile.renameTo(destfile);
    if(!result&&isforceRename)
      try{
        result=moveFile(sourcefile,destfile,true);
      }
      catch(Exception ex){
        result=false;
      }
    return result;
  }

  public static int delete(String path,final String regex) {
    File[] filterFiles=new File(path).listFiles(new FilenameFilter() {
      public boolean accept(File dir,String name){
        return EasyRegex.matches(name,regex);
      }
    });
    int result=0;
    for(int i=0;i<filterFiles.length;i++){
      filterFiles[i].delete();
      result++;
    }
    return result;
  }

  /**转移文件到备份目录中，如果重名则自动改名*/
  public static File backupFile(File srcfile,String destPath) throws IOException{
    File destfile=new File(destPath,srcfile.getName());
    if(destfile.exists()) {
      destfile=figureSerialFileName(destfile,3);
    }
    moveFile(srcfile,destfile,true);
    return destfile;
  }

  public static boolean moveFile(String srcFilePath,String destFilePath) throws IOException{
  	return moveFile(new File(srcFilePath),new File(destFilePath),true);
  }
  
  public static boolean moveFile(File srcfile,File destfile) throws IOException{
  	return moveFile(srcfile,destfile,true);
  }
  
	public static boolean moveFile(File srcfile,File destfile,boolean overwrite) throws IOException{
    boolean result=false;
    if(destfile.exists()&&destfile.isDirectory()) {
    	destfile=new File(destfile,srcfile.getName());
    }
    result=srcfile.renameTo(destfile);
    if(result)
      return true;
    if(!destfile.getParentFile().exists())
      createFolder(destfile.getParentFile().getAbsolutePath(),false);
    if(srcfile.compareTo(destfile)==0)
      return true;
    if(overwrite || !destfile.exists()){
      int filelen=new Long(srcfile.length()).intValue();
      byte[] b=new byte[filelen];
      try( FileInputStream fis=new FileInputStream(srcfile);
      		FileOutputStream fos=new FileOutputStream(destfile);){
        fis.read(b);
        fos.write(b);
      }
      catch(FileNotFoundException e){
      	//throw new RuntimeException(e);
      	return false;
      }
      // 保持原文件的生成时间
      destfile.setLastModified(srcfile.lastModified());
      result=srcfile.delete();
    }
    return result;
  }

  public static boolean copyFolder(String desPath,String SourcePath) throws Exception{
    if(!new File(desPath).exists()){
      createFolder(desPath,false);
    }
    File[] filelist=new File(SourcePath).listFiles();
    for(int i=0;i<filelist.length;i++){
      if(filelist[i].isDirectory()){
	copyFolder(desPath+filelist[i].getName()+"\\",SourcePath+filelist[i].getName()+"\\");
      }
      else{
	copyFile(filelist[i].getAbsolutePath(),desPath+filelist[i].getName());
      }
    }
    return true;
  }

  public static boolean syncFolder(File srcFolder,File targetFolder) {
  	return syncFolder(srcFolder,targetFolder,null,null);
  }
  public static boolean syncFolder(File srcFolder,File targetFolder,String includesRegex,String excludeRegex) {
  	return syncFolder(srcFolder,targetFolder,includesRegex,excludeRegex,null);
  }
	public static boolean syncFolder(File srcFolder,File targetFolder,String includesRegex,String excludeRegex,Callback2<File,File> fn){
		return syncFolder(srcFolder,targetFolder,includesRegex,excludeRegex,fn,Sync_Type_Last_Modified);
	}
 
	public static boolean syncFolder(File srcFolder,File targetFolder,String includesRegex,String excludeRegex,Callback2<File,File> fn,int synctype){
		try{
			if(!targetFolder.exists()){
				targetFolder.mkdirs();
			}
			File[] srcSubFileList=srcFolder.listFiles();
			for(int i=0;i<srcSubFileList.length;i++){
				File srcSubFile=srcSubFileList[i];
				if(EasyObj.isValid(excludeRegex)&&srcSubFile.getName().matches(excludeRegex))
					continue;
				else if(EasyObj.isBlank(includesRegex)||srcSubFile.getName().matches(includesRegex)){
					File targetSubFile=new File(targetFolder,srcSubFileList[i].getName());
					if(srcSubFile.isDirectory()){
						syncFolder(srcSubFile,targetSubFile,includesRegex,excludeRegex,fn);
					}else {
						boolean rtn=false;
						switch(synctype){
						case Sync_Type_Last_Modified:
							if(srcSubFile.lastModified()>targetSubFile.lastModified())
								rtn=copyFile(srcSubFile,targetSubFile);
							break;
						case Sync_Type_Strict:
							if(srcSubFile.lastModified()!=targetSubFile.lastModified()||srcSubFile.length()!=targetSubFile.length())
								rtn=copyFile(srcSubFile,targetSubFile);
							break;
						case Sync_Type_Length:
							if(srcSubFile.length()!=targetSubFile.length())
								rtn=copyFile(srcSubFile,targetSubFile);
							break;
						default:
							break;
						}
						if(fn!=null)
							fn.invoke(srcSubFile,targetSubFile);
					}
				}
			}
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
		return true;
	}
	
  public static boolean save(InputStream is,String destfilename) throws IOException {
    return save(is,new File(destfilename));
  }

  public static boolean save(InputStream is,File destfile) throws IOException {
    byte[] buf=new byte[_KB];
    FileOutputStream fos=new FileOutputStream(destfile,false);
    for(int len=0;(len=is.read(buf))>0;){
      fos.write(buf,0,len);
    }
    close(fos);
    close(is);
    return true;
  }
  
  public static String getMD5(File file) throws FileNotFoundException, IOException {
  	return MD5Utils.encrypt(getBytes(file));
  }
  
  public static byte[] getBytes(File file) throws FileNotFoundException, IOException {
  	try(FileInputStream fis=new FileInputStream(file)){
			byte[] result=new byte[(int)file.length()];
			fis.read(result);
			return result;
		}
  }
  
  public static String loadFromFile(Class cls,String name) throws IOException {
    return loadFromFile(cls.getResourceAsStream(name),_KB);
  }
  
  public static String loadFromFile(Class cls,String name,String charset) throws IOException {
    return loadFromFile(cls.getResourceAsStream(name),charset,_KB);
  }
  
  public static String loadFromFile(String filename) throws IOException{
    int bufferSize=_MB;
    return loadFromFile(filename,bufferSize);
  }
  
  public static String loadFromFile(File file) throws IOException{
  	return loadFromFile(file,_KB);
  }
  
  public static String loadFromFile(File file,String charset) throws IOException{
  	return loadFromFile(new InputStreamReader(new FileInputStream(file),charset),_KB*10);
  }
  
  public static String loadFromFile(File file,String charset,int bufferSize) throws IOException{
  	return loadFromFile(new InputStreamReader(new FileInputStream(file),charset),bufferSize);
  }
  
  public static String loadFromFile(File file,int bufferSize) throws IOException{
    if(!file.exists()||file.isDirectory()){
      throw new IOException("文件不存在"+file.getCanonicalPath());
    }
    FileInputStream fis=new FileInputStream(file);
    String result=loadFromFile(fis,bufferSize);
    fis.close();
    return result;
  }

  public static String loadFromFile(InputStream inputStream,String charset,int bufferSize)throws IOException{
  	return loadFromFile(new InputStreamReader(inputStream,charset),bufferSize);
  }
  
  public static String loadFromFile(InputStream inputStream,int bufferSize)throws IOException{
    ByteArrayOutputStream bos=new ByteArrayOutputStream();
  	byte[] buff=new byte[bufferSize];
  	//bos.write(inputStream);
  	int len=0;
  	while((len=inputStream.read(buff))>0) {
  		bos.write(buff,0,len);
  	}
  	bos.close();
  	inputStream.close();
  	return bos.toString();
  }
  
  public static String loadFromFile(InputStream inputStream,String charset)throws IOException{
  	return loadFromFile(new InputStreamReader(inputStream,charset));
  }
  public static String loadFromFile(String filename,int buffersize) throws IOException{
    return loadFromFile(new File(filename),buffersize);
  }

  public static String loadFromFile(String filename,String charset) throws IOException{
    return loadFromFile(filename,charset,1024*1024);
  }

  public static String loadFromFile(InputStreamReader isr) throws IOException{
  	return loadFromFile(isr,_KB);
  }
  
  public static String loadFromFile(InputStreamReader isr,int bufferSize) throws IOException{
  	return EasyStr.readerToStr(isr,bufferSize,true);
  }
  public static String loadFromFile(String filename,String charset,int bufferSize) throws IOException{
  	return loadFromFile(new File(filename),charset,bufferSize);
  }

  /**把\改为/，尾部确保为/*/
  public static String getDirPath(String path){
  	if(esdk.str.isBlank(path))
  		return path;
  	if(path.contains(wrongSeparator))
  		path=path.replace(wrongSeparator,separator);
    if(!path.endsWith(separator)){
      path+=separator;
    }
    return path;
  }

  /**
   * 支持传入多个路径参数，自动删除多余的路径分隔符，自动转为/号，并确保尾部为/
   * */
  public static String getDirPath(String rootPath,String... subPaths){
  	StringBuilder result=new StringBuilder(getDirPath(rootPath));
   for(String path:subPaths) {
  	 path=getDirPath(path);
  	 if(path.startsWith(separator))
  		 path=path.substring(1,path.length());
  	 result.append(path);
   }
   return result.toString();
  }
  
  /**获取文件或目录的父目录路径*/
  public static String getParentPath(String fileWithPath) {
  	if(fileWithPath.endsWith("/") || fileWithPath.endsWith("\\"))
  		fileWithPath=fileWithPath.substring(0,fileWithPath.length()-1);
  	return EasyRegex.findSub(fileWithPath,"(.*[/\\\\])[\\w\\.]*",1);
  }

  /**获取文件路径，自动判断和添加folder尾部的路径分隔符*/
  public static String getFilePath(String folder,String filename) {
  	return getDirPath(folder)+filename;
  }
  
  /**
   *delete all files and subdirectories under dir;
   *return true if all deletions were successful.
   *if a deletion fails,the method stops attempting to delete and returns false;
   * */
  public static boolean DeleteFolder(String dirPath){
  	boolean result=Files.deleteDir(new File(dirPath));
    return result;
  }

  public static boolean copyFile(File infile,String distPath,boolean isCreateDir) throws IOException{
    if(isCreateDir)
      createFolder(distPath,false);
    return copyFile(infile,new File(getDirPath(distPath).concat(infile.getName())),_MB);
  }

  public static boolean copyFile(String sourceFileName,String destFileName,boolean isOverWrite) throws IOException{
    if(new File(destFileName).exists()&&!isOverWrite){
      return false;
    }
    copyFile(sourceFileName,destFileName);
    return true;
  }

  public static boolean copyFile(String sourceFileName,String destFileName) throws IOException{
    return copyFile(sourceFileName,destFileName,_MB);
  }

  public static boolean copyFile(String sourceFileName,String destFileName,int BufferSize) throws IOException{
    return copyFile(new File(sourceFileName),new File(destFileName),BufferSize);
  }
  
  public static boolean copyFile(File SourceFile,File DestinationFile,int BufferSize) throws IOException{
    boolean result=false;
    try(FileInputStream fis=new FileInputStream(SourceFile);FileOutputStream fos=new FileOutputStream(DestinationFile);){
      byte[] buffer=new byte[BufferSize];
      int readlen=0;
      while((readlen=fis.read(buffer))>0){
        fos.write(buffer,0,readlen);
      }
    }
    DestinationFile.setLastModified(SourceFile.lastModified());
    result=true;
    return result;
  }

	public static boolean copyFile(File src,File target) throws IOException{
		try(FileInputStream fis=new FileInputStream(src);
				FileOutputStream fos=new FileOutputStream(target);
				FileChannel sourcefc=fis.getChannel();
				FileChannel targetfc=fos.getChannel();){
			sourcefc.transferTo(0,sourcefc.size(),targetfc);
			target.setLastModified(src.lastModified());
		}
		return true;
	}
  
	public static boolean copyFile(File src,File target,boolean isCreateDir) throws IOException{
		 if(isCreateDir)
	      createFolder(target.getParent(),false);
		 return copyFile(src,target);
	}
	
  public static boolean saveToFile(File file,String content,boolean isOverWrite) throws IOException{
    if(!file.exists()||isOverWrite){
      FileWriter fw=new FileWriter(file,false);
      fw.write(content);
      fw.close();
    }
    else{
      throw new IOException("!文件"+file.getAbsolutePath()+"已存在");
    }
    return true;
  }
  
  public static boolean saveToFile(String content,File file,boolean isOverWrite) throws IOException{
    if(!file.exists()||isOverWrite){
      FileWriter fw=new FileWriter(file,false);
      fw.write(content);
      fw.close();
    }
    else{
      throw new IOException("!文件"+file.getAbsolutePath()+"已存在");
    }
    return true;
  }
  
  public static boolean SaveToFile(File file,String content,boolean isOverWrite,boolean isCreatPath) throws IOException{
  	boolean result=true;
  	if(isCreatPath)
  		result=createFolder(file.getParent(),false);
  	if(result)
  		result=result&&saveToFile(file,content,isOverWrite);
  	return result;
  }
  public static boolean SaveToFile(String str,String filename,boolean isOverWrite) throws IOException{
    return saveToFile(new File(filename),str,isOverWrite);
  }
  
  public static boolean saveToFile(byte[] content,File file,boolean isOverWrite) throws IOException,FileNotFoundException{
    if(!file.exists()||isOverWrite){
      try(FileOutputStream fos=new FileOutputStream(file,false);){
      	fos.write(content);
      }
    }
    else{
      throw new IOException("!文件"+file.getAbsolutePath()+"已存在");
    }
    return true;
  }
  
  public static boolean saveToFile(String content,File file,boolean isOverWrite,String charset) throws IOException,FileNotFoundException{
    if(!file.exists()||isOverWrite){
      try(FileOutputStream fos=new FileOutputStream(file,false);OutputStreamWriter osw=new OutputStreamWriter(fos,charset);){
        osw.write(content);
      }
    }
    else{
      throw new IOException("!文件"+file.getAbsolutePath()+"已存在");
    }
    return true;
  }
  
  public static boolean saveToFile(String content,String filename,boolean isOverWrite,String charset) throws IOException,FileNotFoundException{
  	return saveToFile(content,new File(filename),isOverWrite,charset);
  }
  
  public static String[][] getStringArrayFromPrnFile(String filename,int[] sa) throws Exception{
    int arrlen=sa.length-1;
    ArrayList ArrayOfList=new ArrayList();
    File FHandle=new File(filename);
    FileReader fr=new FileReader(FHandle);
    BufferedReader br=new BufferedReader(fr);
    String strline="";
    while((strline=br.readLine())!=null){
      if(strline.length()==0)continue; //过滤空行
      if(strline.getBytes().length<sa[sa.length-1]){
	br.close();
	fr.close();
	throw new Exception("文件"+filename+",定义行长度超出文本行长度");
      }
      String[] arstr=new String[arrlen];
      for(int i=0;i<arrlen;i++){
	arstr[i]=EasyStr.getStringByByteLength(strline,sa[i],sa[i+1]-sa[i]).trim();
	if(strline.indexOf("&#38;#38;")<0){
	  arstr[i]=arstr[i].replaceAll("&","&#38;#38;"); //由于XML把"&"作为转义符号,为了输入&,因此要增加这行语句,也可以用"&x#26;"替换"&#38;"
	}
      }
      ArrayOfList.add(arstr);
    }
    String[][] result=new String[ArrayOfList.size()][arrlen];
    for(int i=0;i<result.length;i++)
      for(int j=0;j<result[i].length;j++)
	result[i][j]=((String[])ArrayOfList.get(i))[j];
    br.close();
    fr.close();
    return result;
  }
  
  public static String[][] GetStringArrayFromFile(String filename, int[] sa) throws FileNotFoundException, IOException{
		String[][] result = null;
		try(FileReader fr=new FileReader(new File(filename));BufferedReader br=new BufferedReader(fr)){
			int arrlen = sa.length - 1;
			ArrayList ArrayOfList = new ArrayList();
			String strline = "";
			while ((strline = br.readLine()) != null) {
				String[] arstr = new String[arrlen];
				for (int i = 0; i < arrlen; i++) {
					arstr[i] = EasyStr.getStringByByteLength(strline, sa[i], sa[i + 1] - sa[i]).trim();
					if (strline.indexOf("&#38;#38;") < 0) {
						arstr[i] = arstr[i].replaceAll("&", "&#38;#38;"); // 由于XML把"&"作为转义符号,为了输入&,因此要增加这行语句,也可以用"&x#26;"替换"&#38;"
					}
				}
				ArrayOfList.add(arstr);
			}
			result = new String[ArrayOfList.size()][arrlen];
			for (int i = 0; i < result.length; i++) {
				for (int j = 0; j < result[i].length; j++) {
					result[i][j] = ((String[]) ArrayOfList.get(i))[j];
				}
			}
		} 
		return result;
	}

  public static String[][] getStringArray(File file,String breaksymbol) throws Exception{
    ArrayList ArrayOfList=new ArrayList();
    String[][] result=null;
    try{
      FileReader fr=new FileReader(file);
      BufferedReader br=new BufferedReader(fr);
      String strtemp="",strLine="";
      while((strLine=br.readLine())!=null){
        int beginPosition=0;
        int endPosition=0;
        if(strLine.indexOf("&#38;#38;")<0){
          strLine=strLine.replaceAll("&","&#38;#38;"); // 由于XML把"&"作为转义符号,为了输入&,因此要增加这行语句,也可以用"&x#26;",&amp;替换"&#38;"
        }
        ArrayList ArraryOfString=new ArrayList();
        while(endPosition>=0){
          if(strLine.indexOf("\"",beginPosition)==beginPosition){
            endPosition=EasyStr.instr(strLine,"\"",beginPosition+1);
            endPosition=EasyStr.instr(strLine,breaksymbol,endPosition+1);
          }
          else{
            endPosition=strLine.indexOf(breaksymbol,beginPosition);
          }
          if(endPosition<0){
            strtemp=strLine.substring(beginPosition,strLine.length());
          }
          else{
            strtemp=strLine.substring(beginPosition,endPosition);
            endPosition+=1;
          }
          if(strtemp.length()!=0&&(strtemp.substring(0,1).equals("\""))
              &&(strtemp.substring(strtemp.length()-1,strtemp.length()).equals("\""))){ // 如果有双引号则去掉
            strtemp=strtemp.substring(1,strtemp.length()-1);
          }
          beginPosition=endPosition;
          ArraryOfString.add(strtemp);
        }
        ArrayOfList.add(ArraryOfString);
      }
      br.close();
      fr.close();
      result=EasyStr.GetStringArray(ArrayOfList);
    }
    catch(IOException e){
      throw e;
    }
    return result; //返回字符串数组
  }

  public static String[][] GetStringArrayFromFile(String FileName,String breaksymbol) throws Exception{ // 把文本文件的内容换成二维字符串数组
    return getStringArray(new File(FileName),breaksymbol);
    
  }

  public static String getFileExtName(String filename){ //注意:后缀名可能不止3个字节
  	return esdk.regex.findSub(filename,".*\\.(\\w*)",1);
  }

  public static String getFileParentName(String filename){
    File file=new File(filename);
    return file.getParent();
  }

  public static String getFileNameNoPath(String filename){
    int maxpos=0;
    int posi=filename.lastIndexOf("/");
    int posj=filename.lastIndexOf("\\");
    maxpos=posi>posj?posi:posj;
    if(maxpos>=0)
      if(maxpos>0)filename=filename.substring(maxpos+1);
    return filename;
  }

  public static String getFileNameNotExt(String filename){
    filename=getStandardFileName(filename);
    int posindex=filename.lastIndexOf("/");
    if(posindex>=0)
      filename=filename.substring(posindex+1);
    for(int i=filename.length()-1;i>=0;i--){
      if(filename.substring(i,i+1).equals(".")){
        filename=filename.substring(0,i);
        return filename;
      }
    }
    return null;
  }

  public static String getStandardFileName(String filename) {
    return filename.replaceAll("\\\\","/");
  }
  
  public static String saveFileBySerialNumber(String filename,int SerialLength,String data) throws Exception{
    return saveFileBySerialNumber(filename,SerialLength,data,"GB2312");
  }
      
  public static String saveFileBySerialNumber(String filename,int serialLength,String data,String charset) throws Exception{
    File serialFile=figureSerialFileName(new File(filename),serialLength);
    createFolder(new File(filename).getParent(),false);
    saveToFile(data,serialFile,false,charset);
    return serialFile.getCanonicalPath();
  }

  public static File figureSerialFileName(File file,int serialLength)throws IOException{
  	String folder=file.getParent();
    String filename=file.getAbsolutePath();
    String ext=EasyStr.getFileExtName(filename);
    String fileNameNoExt=EasyFile.getFileNameNotExt(filename);
    String serialFileName=fileNameNoExt;
    File distFile=file;
    do {
      serialFileName=esdk.str.serial(serialFileName,serialLength);
      distFile=new File(folder+"/"+serialFileName+"."+ext);
    }while(distFile.exists());
    return distFile;
  }
  
  public static File[] SortFiles(File[] files,String SortBy,boolean isAscending){
    return SortFiles(files,new String[]{SortBy},isAscending);
  }

  public static File[] SortFiles(File[] files,String[] SortBy,boolean isAscending){
    int[] iSortBy=new int[SortBy.length];
    for(int i=0;i<SortBy.length;i++){
      if(EasyStr.existOf(SortBy[i],"key",true))
	iSortBy[i]=_SortByKey;
      else if(EasyStr.existOf(SortBy[i],"path",true))
	iSortBy[i]=_SortByPath;
      else if(EasyStr.existOf(SortBy[i],"name",true))
	iSortBy[i]=_SortByName;
      else if(EasyStr.existOf(SortBy[i],"size",true))
	iSortBy[i]=_SortBySize;
      else if(EasyStr.existOf(SortBy[i],"type",true))
	iSortBy[i]=_SortByType;
      else if(EasyStr.existOf(SortBy[i],"modify",true))
	iSortBy[i]=_SortByModifyTime;
      else
	iSortBy[i]=_SortByKey;
    }
    return SortFiles(files,iSortBy,isAscending);
  }

  public static File[] SortFiles(File[] files,int iSortBy,boolean isAscending){
    return SortFiles(files,new int[]{iSortBy},isAscending);
  }

  public static File[] SortFiles(File[] files,int[] iSortBy,boolean isAscending){
    int iposAbsolutePath=6;
    String[][] filesAttribute=new String[files.length][7];
    for(int i=0;i<files.length;i++){
      filesAttribute[i][_SortByKey]=String.valueOf(i);
      filesAttribute[i][_SortByPath]=files[i].getParent();
      filesAttribute[i][_SortByName]=files[i].getName().toLowerCase();
      filesAttribute[i][_SortBySize]=String.valueOf(files[i].length());
      filesAttribute[i][_SortByType]=getFileExtName(files[i].getName()).toLowerCase();
      filesAttribute[i][_SortByModifyTime]=String.valueOf(files[i].lastModified());
      filesAttribute[i][iposAbsolutePath]=files[i].getAbsolutePath();
    }
    EasyStr.bubbleSortStringArray(filesAttribute,iSortBy,isAscending);
    for(int i=0;i<filesAttribute.length;i++){
      int iSortKey=Integer.valueOf(filesAttribute[i][_SortByKey]).intValue();
      if(i!=iSortKey){
        files[i]=new File(filesAttribute[i][iposAbsolutePath]);
      }
    }
    return files;
  }

  public static long getFileSize(String filename){
    File file=new File(filename);
    return file.length();
  }

  public static boolean isFileLock(String filename){
    File file=new File(filename);
    return isFileLock(file);
  }

  public static boolean isFileLock(File file){
		boolean result=true;
		FileLock flock=null;
		try{
			RandomAccessFile fis=new RandomAccessFile(file,"rw");
			FileChannel lockfc=fis.getChannel();
			flock=lockfc.tryLock();
			if(flock!=null&&flock.isValid()) {
				result=false;
				flock.release();
			}
			fis.close();
		}catch(FileNotFoundException ex){
		}catch(IOException ex){
		}
		return result;
	}

  public static void SaveAsByMonth(String SourcePath,String Destpath,String findkey,String ExcludeKey) throws Exception{
    File file=new File(SourcePath);
    ExtensionFileFilter ff=new ExtensionFileFilter(findkey);
    File[] files=file.listFiles(ff);
    ArrayList al=new ArrayList();
    for(int i=0;i<files.length;i++){
      if(files[i].getPath().indexOf(ExcludeKey)<0)
        al.add(files[i]);
    }
    files=(File[])al.toArray(new File[0]);
    DateFormat formatter=new java.text.SimpleDateFormat("yyyyMM");
    for(int i=0;i<files.length;i++){
      Date date=new Date(files[i].lastModified());
      String d=formatter.format(date);
      String path=EasyFile.getDirPath(Destpath).concat(d).concat("/");
      createFolder(path,false);
      if(files[i].renameTo(new File(path.concat(files[i].getName()))))
        System.out.println(path.concat(files[i].getName()).concat(" ok"));
      else
        System.out.println(path.concat(files[i].getName()).concat(" fail"));
    }
  }

  public static File[] FindFiles(String[] Paths,String Key,String ExcludeKey,boolean isFindSubFolder){
    ArrayList result=new ArrayList();
    for(int i=0;i<Paths.length;i++){
      File[] files=FindFiles(Paths[i],Key,ExcludeKey,isFindSubFolder);
      for(int j=0;j<files.length;j++){
        result.add(files[j]);
      }
    }
    return (File[])result.toArray(new File[0]);
  }
  
  public static File[] find(String Path,String regex,String excludeRegex,boolean isFindSubFolder){
  	regex=regex==null?"":regex;
  	Pattern p=Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
  	Pattern pe=EasyObj.isValid(excludeRegex)?Pattern.compile(excludeRegex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL):null;
    ArrayList result=new ArrayList();
    File path=new File(Path);
    if(path.exists()){
      File[] files=path.listFiles();
      for(int i=0;i<files.length;i++){
        if(files[i].isFile()){
          if(regex.equals("*")||regex.length()==0&&(pe==null||!pe.matcher(files[i].getName()).find())){// *号为通配符,跳过查找关键值
            result.add(files[i]);
          }
          else{
            if(p.matcher(files[i].getName()).find()&&(pe==null||!pe.matcher(files[i].getName()).find()))
              result.add(files[i]);
          }
        }
        else{
          if(isFindSubFolder){
            File[] filelist=find(files[i].getAbsolutePath(),regex,excludeRegex,true);
            result.addAll(java.util.Arrays.asList(filelist));
          }
        }
      }
    }
    return (File[])result.toArray(new File[0]);
  }  
  
  public static File[] FindFiles(String Path,String andKey,String ExcludeKey,boolean isFindSubFolder){
  	String[] keywords=EasyStr.split(andKey);
  	String[] excludeKeys=esdk.str.isValid(ExcludeKey)?EasyStr.split(ExcludeKey):new String[] {};
    ArrayList result=new ArrayList();
    File path=new File(Path);
    if(path.exists()){
      File[] files=path.listFiles();
      for(int i=0;i<files.length;i++){
        if(files[i].isFile()){
          if(andKey.equals("*")||andKey.length()==0){ // *号为通配符,跳过查找关键值
            if(!EasyStr.existAnd(files[i].getName(),excludeKeys,true))
              result.add(files[i]);
          }
          else{
            if(EasyRegex.findOr(files[i].getName(), keywords))
              if(!EasyRegex.findOr(files[i].getName(),excludeKeys))
                result.add(files[i]);
          }
        }
        else{
          if(isFindSubFolder){
            File[] filelist=FindFiles(files[i].getAbsolutePath(),andKey,ExcludeKey,true);
            result.addAll(java.util.Arrays.asList(filelist));
          }
        }
      }
    }
    return (File[])result.toArray(new File[0]);
  }

  public static File[] FindFilesMax(String Path,String Key,String ExcludeKey,boolean isFindSubFolder,int maxList){
    File[] bufferlist=EasyFile.FindFiles(Path,Key,ExcludeKey,isFindSubFolder);
    if(bufferlist.length<maxList)
      maxList=bufferlist.length;
    File[] filelist=new File[maxList];
    System.arraycopy(bufferlist,0,filelist,0,maxList);
    return filelist;
  }
  
  public static File[] FindFiles(String paths[],String keys[],String excludeKey,boolean isFindSubFolder) {
    ArrayList list=new ArrayList();
    for(int i=0;i<paths.length;i++){
      for(int j=0;j<keys.length;j++){
        File[] filelist=FindFiles(paths[i],keys[j],excludeKey,isFindSubFolder);
        for(int k=0;k<filelist.length;k++){
          list.add(filelist[k]);
        }
      }
    }
    return (File[])list.toArray(new File[0]);
  }
  
  public static boolean isExistDir(String path) {
    File dir=new File(path);
    return dir.isDirectory();
  }
  
  public static String getSavePathByCollectionMothed(String path,int flag) {
    String SavePath="";
    String nowdate=EasyTime.getNowTime("yyyyMMdd");
    String month=nowdate.substring(0,6);
    String year=nowdate.substring(0,4);
    path=EasyFile.getDirPath(path);
    switch(flag){
      case COLLECTIONBYDATE:
        SavePath=path.concat(month).concat("/").concat(nowdate);
        break;
      case COLLECTIONBYMONTH:
        SavePath=path.concat(month);
        break;
      case COLLECTIONBYYEAR:
        SavePath=path.concat(year);
        break;
      default:
        SavePath=path;
        break;
    }
    EasyFile.createFolder(SavePath,false);
    return EasyFile.getDirPath(SavePath);
  }
  
  public static String replaceExtName(String fileName,String replaceExtName) {
    return getFileParentName(fileName).concat(File.separator).concat(getFileNameNotExt(getFileNameNoPath(fileName))).concat(".").concat(replaceExtName);
  }
  
  public static void close(Closeable closeable){
    if(closeable!=null){
      try{
        closeable.close();
      }
      catch(IOException e){
        throw new SdkRuntimeException(e);
      }
    }
  }
  
  public static InputStream getInputStreamFromResources(String fileName) {
		InputStream inputStream = null;
		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		return inputStream;
	}
  
  public static InputStream getInputStream(String fileName) {
		InputStream inputStream = getInputStreamFromResources(fileName);
		if(inputStream==null) 
			inputStream=Streams.fileIn(new File(fileName));
		return inputStream;
	}
  
  public static File findFile(Class cls,String name) {
    if(!esdk.str.isIncludeChinese(name))
      return new File(cls.getResource(name).getPath());
    else {
      return new File(new File(cls.getResource(name).getPath()).getParent()+"\\"+name);
    }
  }
  
	public static Properties getProperties(InputStream is){
		Properties props=new Properties();
		try{
			props.load(is);
			is.close();
			return props;
		}catch(IOException e){
			throw new SdkRuntimeException(e);
		}
	}
  
	public static Properties getProperties(Reader reader){
		Properties props=new Properties();
		try{
			props.load(reader);
			reader.close();
			return props;
		}catch(IOException e){
			throw new SdkRuntimeException(e);
		}
	}
	
	public static Properties getProperties(String fileName){
		return getProperties(getInputStream(fileName));
	}
	
	public static Properties getProperties(String fileName,String charset){
		try(InputStreamReader reader=new InputStreamReader(getInputStream(fileName),charset)){
			return getProperties(reader);
		}catch(Exception e) {
			throw new SdkRuntimeException(e);
		}
	}
	
	public static Properties getProperties(Class cls,String fileName){
		return getProperties(cls.getResourceAsStream(fileName));
	}

	public static Properties getPropertiesFile(String fileName){
		return getProperties(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
	}
	
	public static String caluateFileSize(long fileLength){// 转换文件大小
		if(fileLength==0)
			return "0K";
		DecimalFormat df=new DecimalFormat("#.00");
		String fileSizeString="";
		if(fileLength<1024){
			fileSizeString=df.format((double)fileLength)+"B";
		}else if(fileLength<1048576){
			fileSizeString=df.format((double)fileLength/1024)+"K";
		}else if(fileLength<1073741824){
			fileSizeString=df.format((double)fileLength/1048576)+"M";
		}else{
			fileSizeString=df.format((double)fileLength/1073741824)+"G";
		}
		return fileSizeString;
	}
	
	private static HashMap monitoringFileMap=new HashMap<File,Long>();
	public static boolean checkModified(File... files) {
		for(int i=0;i<files.length;i++) {
			if(files[i]!=null) {
				if(!monitoringFileMap.containsKey(files[i])||!monitoringFileMap.get(files[i]).equals(files[i].lastModified())) {
					monitoringFileMap.put(files[i],files[i].lastModified());
					return true;
				}
			}
		}
		if(monitoringFileMap.size()>1000)
			System.err.println("notice: monitoring files total more than 1000, find the reason or change the max value");
		return false;
	}
	
	public static boolean checkModified(String configFile){
		if(configFile==null)
			return false;
			String[] configFiles=esdk.str.split(configFile,",");
			File[] configfiles=new File[configFiles.length];
			for(int i=0;i<configfiles.length;i++){
				configfiles[i]=new File(configFiles[i]);
			}
		return checkModified(configfiles);
	}
	
	public static String caluateFormetFileSize(long fileLength){// 转换文件大小
		DecimalFormat df=new DecimalFormat("#.00");
		String fileSizeString="";
		if(fileLength<1024){
			fileSizeString=df.format((double)fileLength)+"B";
		}else if(fileLength<1048576){
			fileSizeString=df.format((double)fileLength/1024)+"KB";
		}else if(fileLength<1073741824){
			fileSizeString=df.format((double)fileLength/1048576)+"MB";
		}else{
			fileSizeString=df.format((double)fileLength/1073741824)+"GB";
		}
		return fileSizeString;
	}

	public static boolean equals(File file1,File file2) throws IOException {
		boolean result=file1.equals(file2);
		if(result)
			return result;
		return file1.getName().equals(file2.getName())&&file1.length()==file2.length() && file1.lastModified()==file2.lastModified();
	}
	
	public static boolean contentEquals(File file1,File file2) {
		return FileUtil.contentEquals(file1,file2);
	}

	/**从资源路径中获取文件*/
	public static File getFileByResource(String filePath){
		if(filePath.startsWith("/"))
			filePath=filePath.substring(1);
		URL url=Thread.currentThread().getContextClassLoader().getResource(filePath);
		return url!=null? new File(url.getPath()):null;
	}
	
  private static void test()throws Exception{
  	File testfileFolder=new File("./src/test/resources/testfiles");
  	esdk.tool.assertEquals(syncFolder(testfileFolder,new File("/temp/testfolder"),".+\\.(xml|xls|txt)",".+\\.rar"));
  	esdk.tool.assertEquals(getMD5(new File(testfileFolder,"test.txt")),"a112b152af7e904334410e35d0e73fbd");
  	String deleteDir=getDirPath("/temp/","testdeleteforlder","/subfolder");
  	mkdir(deleteDir);
  	deleteDir(new File(deleteDir));
  	esdk.tool.assertEquals(new File(deleteDir).exists(),false);
  	
    String content=EasyFile.loadFromFile(getFilePath(testfileFolder.getPath(),"test.txt"));
    esdk.tool.assertEquals(content.length(),768);
    esdk.tool.assertEquals(getFileExtName("c:/abc.txt.dat"),"dat");
    esdk.tool.assertEquals(replaceExtName("c:/b2b/data/ava/hello.csv.test","dat").equals("c:\\b2b\\data\\ava\\hello.csv.dat"));
    esdk.tool.assertEquals(!isExistDir("v:/feedback/backup"));
    String destPath=getDirPath(testfileFolder.getPath(),"temp/");
    esdk.tool.assertEquals(createFolder(destPath,false));
    esdk.tool.assertEquals(copyFile(getFilePath(testfileFolder.getPath(),"web.xml"),getDirPath(destPath)+"test.xml"));
    esdk.tool.assertEquals(copyFile(getFilePath(testfileFolder.getPath(),"web.xml"),getFilePath(testfileFolder.getPath(),"test.xml")));
    esdk.tool.assertEquals(backupFile(new File(testfileFolder,"test.xml"),destPath),new File(testfileFolder,"/temp/test001.xml"));
    esdk.tool.assertEquals(new File(getDirPath(destPath)+"test001.xml").delete());
    esdk.tool.assertEquals(DeleteFolder(new File(destPath).getAbsolutePath()));
    String filename=saveFileBySerialNumber(getFilePath(testfileFolder.getPath(),"/temp/test.txt"),2,"testcontent大家好");
    esdk.tool.aeic(new File(filename).getCanonicalPath(),new File(testfileFolder,"temp/test01.txt").getCanonicalPath());
    esdk.tool.assertEquals(save(new FileInputStream(getFilePath(testfileFolder.getPath(),"temp/test01.txt")),getFilePath(testfileFolder.getPath(),"/temp/temp.txt")));
    esdk.tool.assertEquals(deleteDir(new File(testfileFolder,"/temp/")));
    esdk.tool.assertEquals(caluateFormetFileSize(new File(testfileFolder,"test.yml").length()),"2.98KB");
  	esdk.tool.assertEquals(getParentPath("/gzvolunteer/report/"),"/gzvolunteer/");
  	esdk.tool.assertEquals(getParentPath("/gzvolunteer/report/volunteer_stat.do"),"/gzvolunteer/report/");
  	esdk.tool.assertEquals(getParentPath("\\gzvolunteer\\report\\volunteer_stat.do"),"\\gzvolunteer\\report\\");
  }

  public static void main(String[] args) throws Exception{
    test();
  }

}
