package com.esdk.sql.orm.tools;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import com.esdk.esdk;
import com.esdk.sql.SQLAssistant;
import com.esdk.sql.datasource.FileConnectionPool;
import com.esdk.utils.Constant;
import com.esdk.utils.TimeMeter;

public class ORMCreater{
	private static boolean isCreateBean=false;//是否需要生成bean，默认为false

	public static void make(Connection conn,String packageName,String[] tables,String targetPath,boolean isOverwrite) throws SQLException, ClassNotFoundException {
		make(conn,packageName,tables,targetPath,isOverwrite,null);
	}

	public static void make(Connection conn,String packageName,String[] tables,String targetPath,boolean isOverwrite,InputStream relationshipXml) throws SQLException, ClassNotFoundException {
    System.out.println("starting...");
    SQLAssistant.setDatabaseProductName(conn);
    TimeMeter tm=new TimeMeter();
    MetaDataCodeCreater.parentPath=targetPath;
    MetaDataCodeCreater.PackageName=packageName;
    MetaDataCodeCreater metadataCreator=new MetaDataCodeCreater();
    metadataCreator.dbmd=conn.getMetaData();
    metadataCreator.isOverwrite=isOverwrite;
   /* metadataCreator.action();查找所有表*/
    metadataCreator.action(tables);
    
    RowCodeCreater rowCreator=new RowCodeCreater();
    rowCreator.setRelationship(relationshipXml);
    rowCreator.isOverwrite=isOverwrite;
    rowCreator.dbmd=conn.getMetaData();
    rowCreator.action(tables);
    tm.printElapse();
    
    ResultSetCodeCreater resultsetCreator=new ResultSetCodeCreater();
    resultsetCreator.isOverwrite=isOverwrite;
    resultsetCreator.dbmd=conn.getMetaData();
    resultsetCreator.action(tables);
    
    SelectCodeCreater selectCreator=new SelectCodeCreater();
    selectCreator.isOverwrite=isOverwrite;
    selectCreator.dbmd=conn.getMetaData();
    selectCreator.action(tables);
    
    if(isCreateBean) {
	    BeanCodeCreater beanCreator=new BeanCodeCreater();  //bean对象很少用到，需要用到才生成。
	    beanCreator.isOverwrite=isOverwrite;
	    beanCreator.dbmd=conn.getMetaData();
	    beanCreator.action(tables);
    }
    tm.printElapse();
    System.out.println("ending.");
	}
	
	public static void main(String[] args) throws Exception{
		Connection conn=FileConnectionPool.getConnection();
		String[] tables=esdk.sql.getTablesAndViews(conn.getMetaData());
//	String[] tables=new JDBCTemplate(con,"SELECT table_name FROM user_tables union SELECT view_name as table_name FROM User_Views").toABResultSet().getStrings("TABLE_NAME"); //for oracle
		String targetPath="src/test/java/";
		String ormPackagePath="com.esdk.test.orm";
		ORMCreater.make(conn,ormPackagePath,tables,targetPath,true);
	}
}
