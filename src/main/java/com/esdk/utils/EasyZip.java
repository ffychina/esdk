/*
 * 2009-10-11 更改到压缩文件中的文件包含日期,加入解压程序
 */
package com.esdk.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import com.esdk.exception.SdkRuntimeException;

public class EasyZip{

	public static byte[] deflate(byte[] originalData){
		// create the compressor with highest level of compression
		Deflater compressor=new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);

		// Give the compressor the data to compress
		compressor.setInput(originalData);
		compressor.finish();

		// Create an expandable byte array to hold the compressed data.
		// you cannot use an array that's the same size as the original
		// because there is no guarantee that the compressed data will be
		// smaller than the uncompressed data
		ByteArrayOutputStream bos=new ByteArrayOutputStream(originalData.length);
		// Compress the data
		byte[] buf=new byte[1024];
		while(!compressor.finished()){
			int count=compressor.deflate(buf);
			bos.write(buf,0,count);
		}
		try{
			bos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		// get the compressed data
		byte[] compressedData=bos.toByteArray();
		return compressedData;
	}

	public static byte[] inflate(byte[] compressedData){
		// Create the decompressor and give it the data to compress
		Inflater decompressor=new Inflater();
		decompressor.setInput(compressedData);
		// create an expandable type array to hold the decompressed data
		ByteArrayOutputStream bos=new ByteArrayOutputStream(compressedData.length);
		// Decompress the data
		byte[] buf=new byte[1024];
		while(!decompressor.finished()){
			try{
				int count=decompressor.inflate(buf);
				bos.write(buf,0,count);
			}catch(DataFormatException e){
				e.printStackTrace();
			}
		}
		// get the decompressed data
		byte[] decompressedData=bos.toByteArray();
		return decompressedData;
	}

	public static boolean addtozip(OutputStream outputstream,File[] originalFiles){
		try{
			ZipEntry zipEntry=null;
			// create a buffer for reading the files
			byte[] buf=new byte[1024];
			// Create the zip file
			ZipOutputStream out=new ZipOutputStream(outputstream);
//			out.setEncoding("UTF8");
			out.setEncoding(System.getProperty("sun.jnu.encoding"));
			out.setMethod(ZipOutputStream.DEFLATED);
			// Compress this files
			for(int i=0;i<originalFiles.length;i++){
				// ZipOutputStream.XEntry不支持中文,要先把中文名称转成UTF8
				String filename=originalFiles[i].getName();
				// Add Zip entry to output stream
				if(originalFiles[i].isDirectory())
					out.putNextEntry(new ZipEntry(filename+"/"));
				else{
					zipEntry=new ZipEntry(filename);
					zipEntry.setTime(originalFiles[i].lastModified());
					out.putNextEntry(zipEntry);
					FileInputStream in=new FileInputStream(originalFiles[i].getAbsolutePath());
					// Transfer bytes from the file to the zip file
					int len;
					while((len=in.read(buf))>0){
						out.write(buf,0,len);
					}
					// Complete the zip file
					out.closeEntry();
					in.close();
				}
			}
			out.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			return false;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean zip(File sfile) throws IOException{
		return zip(sfile,new File(sfile.getCanonicalPath()+".zip"));
	}

	public static boolean CompressAndMove(File sfile,File zipFile) throws IOException{
		if(zip(sfile,zipFile))
			return EasyFile.deleteDir(sfile);
		return false;
	}

	public static boolean zip(File sfile,File zipFile) throws IOException{
		zipFile.getParentFile().mkdirs();
		// Create the zip file
		ZipOutputStream zipos=new ZipOutputStream(zipFile);
		zipos.setEncoding(System.getProperty("sun.jnu.encoding"));//设置文件名编码方式
		boolean result=false;
		try{
			result=zip("",sfile,zipos);
		}finally{
			zipos.closeEntry();
			EasyFile.close(zipos);
		}
		return result;
	}

	/**
	 * 主要的压缩工作函数
	 * 
	 * @param parent
	 * @param sfile
	 * @param zipos
	 * @return
	 * @throws IOException
	 */
	public static boolean zip(String parent,File sfile,ZipOutputStream zipos) throws IOException{
		ZipEntry zipEntry=null;
		boolean result=false;
		// create a buffer for reading the files
		byte[] buf=new byte[1024];
		// Compress this file
		if(sfile.isDirectory()){
			String path=parent+sfile.getName()+"/";
			zipos.putNextEntry(new ZipEntry(path));
			File[] subfiles=sfile.listFiles();
			for(int i=0;i<subfiles.length;i++){
				result=zip(path,subfiles[i],zipos);
				if(!result)
					break;
			}
			result=true;
		}else{
			// Add Zip entry to output stream
			zipEntry=new ZipEntry(parent+sfile.getName());
			zipEntry.setTime(sfile.lastModified());
			zipos.putNextEntry(zipEntry);
			FileInputStream in=null;
			try{
				in=new FileInputStream(sfile);
				// Transfer bytes from the file to the zip file
				int len;
				// Complete the zip file
				zipos.setEncoding(System.getProperty("sun.jnu.encoding"));//设置文件名编码方式
				while((len=in.read(buf))>0){
					zipos.write(buf,0,len);
				}
				result=true;
			}finally{
				EasyFile.close(in);
			}
		}
		return result;
	}

	public static String[] getFileNames(String zipFileName) throws Exception{
		// Open the zip file
		ZipFile zipFile=new ZipFile(zipFileName);
		// Enumerate each entry
		ArrayList result=new ArrayList();
		for(Enumeration entries=zipFile.getEntries();entries.hasMoreElements();){
			// Get the entry name
			String zipEntryName=((java.util.zip.ZipEntry)entries.nextElement()).getName();
			/* System.out.println(new String(Character.toString(zipEntryName.charAt
			 * (1)).getBytes("UTF-16"))); */
			result.add(zipEntryName);
		}
		return (String[])result.toArray(new String[0]);
	}

	public static boolean compressFiles(File[] fileList,File zipFile,boolean isDelete) throws IOException{
		return compressFiles(fileList,zipFile,true,isDelete);
	}

	public static boolean compressFiles(File[] fileList,File zipFile,boolean isIncludeDir,boolean isDelete) throws IOException{
		boolean result=false;
		if(zipFile.getParentFile()!=null)
			zipFile.getParentFile().mkdirs();
		ZipOutputStream zipos=new ZipOutputStream(zipFile);
		zipos.setEncoding(System.getProperty("sun.jnu.encoding"));//设置文件名编码方式
		try{
			for(int i=0;i<fileList.length;i++){
				if(fileList[i].isDirectory()){
					if(isIncludeDir)
						result=zip("",fileList[i],zipos);
				}else{
					result=zip("",fileList[i],zipos);
				}
				if(!result)
					break;
			}

			if(isDelete&&result)
				for(int i=0;i<fileList.length;i++){
					if(fileList[i].isDirectory()){
						if(isIncludeDir)
							EasyFile.deleteDir(fileList[i]);
					}else
						fileList[i].delete();
				}
		}finally{
			zipos.closeEntry();
			EasyFile.close(zipos);
		}

		return result;
	}

	// 解压指定zip文件
	public static void unzip(File[] zipFileList,String outputPath,boolean allFileToOneDir){
		for(int i=0;i<zipFileList.length;i++){
			unzip(zipFileList[i].getAbsolutePath(),outputPath,allFileToOneDir);
		}
	}

	public static void unzip(String zipFileName,String outputPath){
		unzip(zipFileName,outputPath,false);
	}

	public static void unZip(File zipFile,String outputPath){
		unzip(zipFile.getAbsolutePath(),outputPath,false);
	}

	public static void unzip(String zipFileName,String outputPath,boolean allFileToOneDir){
		try{
			ZipFile zipFile=new ZipFile(zipFileName, System.getProperty("sun.jnu.encoding"));
			Enumeration<?> e=zipFile.getEntries();
			ZipEntry zipEntry=null;
			File f=null;
			String fn="";
			if(EasyObj.isBlank(outputPath))
				outputPath=new File(zipFileName).getParent();
			EasyFile.createFolder(outputPath,false);
			while(e.hasMoreElements()){
				zipEntry=(ZipEntry)e.nextElement();
//				System.out.println("unziping "+zipEntry.getName());
				if(zipEntry.isDirectory()){
					if(allFileToOneDir)
						continue;
					f=new File(outputPath+File.separator+zipEntry.getName());
					if(!f.exists()){
						f.mkdirs();
						System.out.println("创建目录："+f.getAbsolutePath());
					}
				}else{
					fn=zipEntry.getName();
					if(allFileToOneDir)
						fn=EasyFile.getFileNameNoPath(fn);
					f=new File(outputPath+File.separator+fn);
					if(!f.getParentFile().exists())
						f.getParentFile().mkdirs();
					f.createNewFile();
					InputStream in=zipFile.getInputStream(zipEntry);
					FileOutputStream out=new FileOutputStream(f);

					byte[] by=new byte[1024];
					int c;
					while((c=in.read(by))!=-1){
						out.write(by,0,c);
					}
					out.close();
					in.close();
					f.setLastModified(zipEntry.getTime());
//					System.out.println("解压文件:"+f.getAbsolutePath());
				}
			}
			zipFile.close();
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
	}

	public static void test(){
		try{
			zip(new File("./testfiles/测试中文压缩"),new File("./testfiles/测试中文压缩.zip"));
			unzip("./testfiles/测试中文压缩.zip","./testfiles/");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		test();
	}

}
