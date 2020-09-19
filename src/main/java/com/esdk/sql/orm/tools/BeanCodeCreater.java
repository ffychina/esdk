package com.esdk.sql.orm.tools;
import java.sql.Connection;
import java.sql.SQLException;

import com.esdk.esdk;
import com.esdk.sql.JDBCTemplate;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.ABRow;
import com.esdk.sql.orm.IRowSet;
import com.esdk.utils.EasyStr;

public class BeanCodeCreater extends MetaDataCodeCreater{
	private static final String suffix="Bean";

	@Override	protected String createJavaContent(String tablename,ABResultSet columnRs,ABResultSet primaryRs,ABResultSet indexRs,IRowSet exportKeyRs,IRowSet importKeyRs) throws Exception{
		JavaCreater jc=create(tablename,columnRs,primaryRs,indexRs,exportKeyRs,importKeyRs);
		if(isCreateSwaggerAnnotation) {
			jc.addImport("io.swagger.annotations.ApiModel");
			jc.addImport("io.swagger.annotations.ApiModelProperty");
			jc.addImport("javax.validation.constraints.NotNull");
		}
		jc.setExtentClassName("com.esdk.core.bean.BaseBean");
		if(isCreateSwaggerAnnotation) {
			ABRow tableStatusRow=new JDBCTemplate(dbmd.getConnection(),"show table status LIKE ?",tablename).getFirstRow();
			jc.addClassAnnotation(" @ApiModel(\""+tableStatusRow.getString("Comment")+"\")");
		}
		String primaryFieldName=esdk.str.toCamelCase(findPrimaryKey(primaryRs,columnRs));
		for(columnRs.beforeFirst();columnRs.next();){
			String columnname=formatJavaBeanName(columnRs.getCurrentRow().getString("COLUMN_NAME"),false);
			String columnJavaClass=adjustsetParameterClass(getShortClassName(getJavaDataType(columnRs.getCurrentRow())));
			jc.addField("private",false,false,getShortClassName(columnJavaClass),columnname);
			if(isCreateSwaggerAnnotation) {
				String swaggerAnnotation="  @ApiModelProperty(\""+columnRs.getCurrentRow().getString("REMARKS")+"\")";
				/*				if(swaggerAnnotation.matches("Long|Integer|Double|Short"))
									swaggerAnnotation="  @ApiModelProperty(value=\""+columnRs.getString("REMARKS")+"\",example=\"0\")";*/
				jc.addFieldAnnotation(swaggerAnnotation);
				if(columnRs.getCurrentRow().getString("IS_NULLABLE").equals("NO") && !primaryFieldName.equals(columnname)) {
					String notNullAnnotation="  @NotNull(message=\""+getNotNullMessage(columnRs.getCurrentRow().getString("REMARKS"))+"\")";
					jc.addFieldAnnotation(notNullAnnotation);
				}
			}
		}
		for(columnRs.beforeFirst();columnRs.next();){
			String columnname=formatJavaBeanName(columnRs.getCurrentRow().getString("COLUMN_NAME"),false);
			String columnJavaClass=adjustsetParameterClass(getShortClassName(getJavaDataType(columnRs.getCurrentRow())));
			String getcontent=EasyStr.format("    return {0};",columnname);
			jc.addMethod("public",getShortClassName(columnJavaClass),"get"+formatJavaBeanName(columnname,true),"","",getcontent);
			String setcontent=EasyStr.format("    {0}=value;",columnname);
			jc.addMethod("public","void","set"+formatJavaBeanName(columnname,true),columnJavaClass+" value","",setcontent);
		}
		jc.addMethod("public","String","toString","","","\t\treturn super.toString();");
		jc.parse();
		return jc.getJavaContent();
	}

	@Override protected JavaCreater create(String tablename,ABResultSet columnRs,ABResultSet primaryRs,ABResultSet indexRs,IRowSet exportKeyRs,IRowSet importKeyRs) throws Exception {
  	String javaFieldName=formatJavaBeanName(tablename,true);
    JavaCreater javaContentCreater=new JavaCreater(getClassName(formatJavaBeanName(convertTableName(tablename),true)),PackageName);//modify by hrjie
    javaContentCreater.addField("public",true,true,"String","TABLENAME",getStringValue(javaFieldName));
    return javaContentCreater;
  }

  @Override protected String findPrimaryKey(ABResultSet primaryRs,ABResultSet columnRs,JavaCreater javaContentCreater) throws NumberFormatException, SQLException{
  	String primaryKeyFieldName;
  	if(primaryRs.next()) {
  		primaryKeyFieldName=primaryRs.getCurrentRow().getString("COLUMN_NAME");
      javaContentCreater.addField("public",true,true,"String","PrimaryKey",getStringValue(primaryKeyFieldName));
      javaContentCreater.addField("public",true,true,"int","IPrimaryKey",String.valueOf(Integer.valueOf(primaryRs.getCurrentRow().getString("KEY_SEQ")).intValue()-1));
    }
    else {
    	columnRs.next();
    	primaryKeyFieldName=columnRs.getCurrentRow().getString("COLUMN_NAME");
    	//columnRs.afterLast();
    	columnRs.first();
      javaContentCreater.addField("public",true,true,"String","PrimaryKey",getStringValue(primaryKeyFieldName));
      javaContentCreater.addField("public",true,true,"int","IPrimaryKey","0");
      columnRs.beforeFirst();
    }
  	columnRs.beforeFirst();
  	boolean isAutoIncrement=false;
  	while(columnRs.next()) {
  		if(columnRs.getCurrentRow().getString("COLUMN_NAME").equals(primaryKeyFieldName)) {
  			if(columnRs.getCurrentRow().getString("TYPE_NAME").indexOf("identity")>0) {
  				isAutoIncrement=true;
  				break;
  			}
  		}
  	}
  	javaContentCreater.addField("public",true,true,"boolean","IsAutoIncrement",Boolean.valueOf(isAutoIncrement).toString());
  	return primaryKeyFieldName;
  }

  @Override protected String getClassName(String tablename){
		return tablename+suffix;
	}
	private String getNotNullMessage(String remark){
		if(remark.contains("(")||remark.contains("（"))
			remark=esdk.regex.findSub(remark,"(.*?)[\\(,，（：]",1);
		if(remark.endsWith("ID"))
			remark=remark.replace("ID","名称");
		return remark.trim()+"不能为空";
	}


	public static void make(Connection conn,String packageName,String[] tables,String targetPath,boolean isOverwrite) throws SQLException, ClassNotFoundException {
		System.out.println("starting...");
		BeanCodeCreater.parentPath=targetPath;
		BeanCodeCreater beanCodeCreater=new BeanCodeCreater();
		beanCodeCreater.dbmd=conn.getMetaData();
		beanCodeCreater.isOverwrite=isOverwrite;
		beanCodeCreater.PackageName=packageName;
		beanCodeCreater.action(tables);
	}
}
