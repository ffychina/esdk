package com.esdk.utils;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class FileRenameBatch {

  public static void batchrename(String path)throws Exception{
    File file=new File(path);
    String extname="smi";
    ExtensionFileFilter ff=new ExtensionFileFilter(extname);
    File[] files=file.listFiles(ff);
    for(int i=0;i<files.length;i++){
      System.out.print(files[i].getAbsolutePath());
      DateFormat formatter=new java.text.SimpleDateFormat("dd.MM.yy-HH_mm_ss");
      Date date=new Date(files[i].lastModified());
      String d=formatter.format(date)+"-"+EasyStr.getRandom(3);
      String outputfilename=d+"."+extname;
      File outputfile=new File(EasyStr.getDirPath(files[i].getParent())+outputfilename);
      boolean flag=files[i].renameTo(outputfile);
      String message=flag?"success":"defeat";
      System.out.println(" ".concat(message));
    }
  }

  public static void main(String[] args) {
    try{
      batchrename("d:/ffy/sms");
    }
    catch(Exception ex){
    }

  }

}
