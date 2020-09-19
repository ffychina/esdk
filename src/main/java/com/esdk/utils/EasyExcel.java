package com.esdk.utils;
import java.io.File;
import java.io.IOException;

import org.nutz.lang.Files;
import org.nutz.lang.Streams;

import cn.hutool.core.io.FileUtil;

public class EasyExcel{
	public static byte[] toXls(String[][] csv){
		String templet=null;
		try{
			templet=EasyFile.loadFromFile(EasyFile.getInputStreamFromResources("com/esdk/utils/export.html"),Constant.UTF8);
			StringBuffer content=new StringBuffer();
			if(csv==null)
				return new byte[0];
			for(int i=0;i<csv.length;i++){
				content.append("<tr>");
				for(int j=0;j<csv[i].length;j++){
					content.append("<td>").append(EasyStr.getStringNoNull(csv[i][j])).append("</td>");
				}
				content.append("</tr>\n");
			}
			return templet.replace("${content}",content).getBytes("utf8");
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	public static boolean toXls(String[][] csv,File out) throws IOException{
		return EasyFile.saveToFile(toXls(csv),out,true);
	}

	public static void main(String[] args) throws IOException{
		System.out.println(toXls(new String[][]{{"code","name"},{"A111","张三丰"}},new File("/output.xls")));
	}
}
