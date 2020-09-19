/**
 * Estimate object is mutex at this JVM at this time,in despite of any situation.
 * @author franky.Fan
 */
package com.esdk.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.esdk.esdk;
public class Mutex{
  private static ArrayList unique;
  private static FileOutputStream outputFile;
  private static FileInputStream inputFile;
  private static int limitSize=100;
  private static final File mutexFileName=new File("mutex.dat");
  
/**
 * estimate the obj if unique.
 * @param obj 
 * @return if obj is exist ,then return false,vice versa
 */
  public static void deleteMutexFile() {
    if(mutexFileName.exists())
      mutexFileName.delete();
  }
  
  public synchronized static boolean isMutex(Object obj){
    initializeUnique();
    if(unique.contains(obj))
      return true;
    return false;
  }

  private synchronized static void initializeUnique(){
    if(unique==null){
      loadunique();
    }
    if(unique==null)
      unique=new ArrayList();

  }

  private synchronized static void saveunique(){
    try{
      if(unique!=null){
        if(unique.size()>limitSize)
          System.err.println("too much workers please decrease performance...");
        outputFile=new FileOutputStream(mutexFileName);
        ObjectOutputStream serializaStream=new ObjectOutputStream(outputFile);
        serializaStream.writeObject(unique);
        serializaStream.close();
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  private synchronized static void loadunique(){
    try{
      if(unique==null){
        if(mutexFileName.exists()){
          inputFile=new FileInputStream(mutexFileName);
          ObjectInputStream serializestream=new ObjectInputStream(inputFile);
          unique=(ArrayList)serializestream.readObject();
          inputFile.close();
        }
      }
    }
    catch(Exception e){
      throw new RuntimeException(e);
    }
  }

  public synchronized static void addUnique(String uniqueName){
    initializeUnique();
    if(!unique.contains(uniqueName)){
      unique.add(uniqueName);
      saveunique();
    }
  }

  public synchronized static void removeUnique(String uniqueName){
    initializeUnique();
    unique.remove(uniqueName);
    saveunique();
  }
  
  public static void test(){
    String obj="aib";
    esdk.tool.assertEquals(isMutex(obj));
    for(int i=0;i<limitSize+1;i++){
      addUnique(new String(obj+i));
    }
    for(int i=0;i<limitSize+1;i++){
      removeUnique(new String(obj+i));
    }
    addUnique(new String(obj));
    esdk.tool.assertEquals(isMutex(obj));   
  }
  
  public static void main(String[] args){
    test();
  }

}
