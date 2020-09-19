package com.esdk.convert;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.esdk.esdk;
import com.esdk.sql.ITableSelect;
import com.esdk.sql.SelectParser;
import com.esdk.sql.orm.TableResultSet;
import com.esdk.sql.orm.TableRow;
import com.esdk.utils.EasyFile;
import com.esdk.utils.EasyStr;

public class TraditionalConverter{
	private static String simpleStr="";
	private static String traditionalStr="";
	private boolean isReverse=false;
	static{
		try{
			simpleStr=esdk.file.loadFromFile(TraditionalConverter.class,"simplified.txt","utf-8");
			traditionalStr=esdk.file.loadFromFile(TraditionalConverter.class,"traditional.txt","utf-8");
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void setSimplifiedToTraditional(){
		setReverse(false);
	}

	public void setTraditionalToSimplified(){
		setReverse(true);
	}

	/** 为true时可以把繁体转为简体 */
	private void setReverse(boolean value){
		this.isReverse=value;
	}

	public void convert(Connection con) throws PropertyVetoException,SQLException{
		convert(con,null,null);
	}

	public void convert(Connection con,String includeTables,String excludeTables) throws PropertyVetoException,SQLException{
		con.setAutoCommit(false);
		String[] tables=esdk.str.split(includeTables);
		if(tables.length==0)
			tables=this.getTables(con.getMetaData());// EasySql.getTablesAndViews(con.getMetaData());//con.getMetaData(
		ITableSelect s=null;
		TableResultSet rs=null;
		String[] ignoreTables=esdk.str.split(excludeTables);
		for(int i=0;i<tables.length;i++){
			if(excludeTables!=null&&esdk.str.existOf(ignoreTables,tables[i])){
				System.out.println("table【"+tables[i]+"】 ignored.");
				continue;
			}
			System.out.println("table【"+tables[i]+"】 is doing...");
			s=SelectParser.create(tables[i],false,con);
			// s=SelectParser.create(tables[2],false,con);
			// s.setConnection(con);
			rs=s.toTableResultSet();
			for(Iterator iter=rs.iterator();iter.hasNext();){
				TableRow row=(TableRow)iter.next();
				for(int j=0,n=row.getNames().length;j<n;j++){
					String column=row.getNames()[j];
					if(row.get(column) instanceof String){
						row.set(column,convert(EasyStr.valueOf(row.get(column))));
					}
					row.update();
					row.flush();
				}
			}
		}
		con.commit();
	}

	public String convert(String str){
		String result=isReverse?this.traditionalToSimplified((String)str):this.simplifiedToTraditional((String)str);
		if(result!=null&&!result.equals(str))
			System.out.println(str+" --> "+result);
		return result;
	}

	private String[] getTables(DatabaseMetaData meta) throws SQLException{
		ArrayList tableList=new ArrayList();
		String[] t={"TABLE"};
		ResultSet rs=meta.getTables(null,"%","%",t);
		while(rs.next()){
			String tablename=rs.getString("TABLE_NAME");
			// System.out.println(tablename + " is creating...");
			tableList.add(tablename);
		}
		return (String[])tableList.toArray(new String[0]);
	}

	public void convert(File[] files) throws IOException{
		for(int i=0;i<files.length;i++){
			if(EasyStr.existOf("simplified.txt,traditional.txt",files[i].getName()))
				continue;
			String s=EasyFile.loadFromFile(files[i].getAbsolutePath(),"utf8");
			// System.out.println("checking "+ files[i].getCanonicalPath());
			String converted=convert(s);
			// EasyFile.SaveToFile(tempStr.toString(), "d:/t/"+f[i].getName(),true,
			// "utf-8");
			if(s.equals(converted)){
			}else{
				EasyFile.saveToFile(converted,files[i].getAbsolutePath(),true,"utf-8");
				System.out.println(files[i].getCanonicalPath()+" is updated");
			}
		}
	}

	public static String traditionalToSimplified(String s){
		StringBuffer result=new StringBuffer();
		char[] c=s.toCharArray();
		int index=-1;
		for(int i=0;i<c.length;i++){
			index=traditionalStr.indexOf(c[i]);
			if(index>=0){
				// System.out.println(c[i] + "-->" + simpleStr.charAt(index));
				result.append(simpleStr.charAt(index));
			}else{
				result.append(c[i]);
			}
		}
		return result.toString();
	}
	private static String SmplifiedExclude="范志琼涌岳";

	public static String simplifiedToTraditional(String s){
		StringBuffer result=new StringBuffer();
		char[] c=s.toCharArray();
		int index=-1;
		for(int i=0;i<c.length;i++){
			index=simpleStr.indexOf(c[i]);
			if(index>=0&&SmplifiedExclude.indexOf(c[i])<0){
				// System.out.println(c[i] + "-->" + traditionalStr.charAt(index));
				result.append(traditionalStr.charAt(index));
			}else{
				result.append(c[i]);
			}
		}
		return result.toString();
	}

	public static void main(String[] args) throws Exception{
		TraditionalConverter t=new TraditionalConverter();
		File[] files=EasyFile
				.find("../newlife/","\\.ftl|\\change\\.txt|\\.java|\\.js|\\.jsp|\\.jrxml|\\.html","CVS|SVN|zh-cn.js|zh_CN.js",true);
		System.out.println(files.length);
		t.setReverse(false);// truee for zss
		t.convert(files);
		// t.convert(getTestDatabase());
		System.out.println("finish");
	}
}

