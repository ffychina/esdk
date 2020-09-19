package com.esdk.test;

import java.lang.reflect.Method;

import com.esdk.sql.ConnectionBuilder;
import com.esdk.sql.Insert;
import com.esdk.sql.JDBCTemplate;
import com.esdk.sql.PostgresWhere;
import com.esdk.sql.SelectParser;
import com.esdk.sql.Update;
import com.esdk.sql.WhereFactory;
import com.esdk.sql.orm.TableResultSet;
import com.esdk.utils.CharAppender;
import com.esdk.utils.EasyFile;
import com.esdk.utils.EasyLogger;
import com.esdk.utils.EasyObj;
import com.esdk.utils.MString;
import com.esdk.utils.Mutex;
import com.esdk.utils.TString;
import com.esdk.utils.TestHelper;
import com.esdk.utils.VString;

public class SDKTest{

	public static void test(){
		try{
			Class[] clss=new Class[]{TestEasyStr.class,ORMTest.class,
					TestSelect.class,Insert.class,Update.class,ConnectionBuilder.class,CharAppender.class,TString.class,EasyFile.class,
					TableResultSet.class,EasyObj.class,Mutex.class,VString.class,JDBCTemplate.class,WhereFactory.class,PostgresWhere.class,
					EasyLogger.class,MString.class,SelectParser.class};
			for(int i=0;i<clss.length;i++){
				System.out.println("testing "+clss[i].getName()+":");
				Method method=clss[i].getDeclaredMethod("test",new Class[]{});
				method.setAccessible(true);
				method.invoke(null,new Object[]{});
			}
			TestHelper.printAssertInfo();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		test();
	}
}
