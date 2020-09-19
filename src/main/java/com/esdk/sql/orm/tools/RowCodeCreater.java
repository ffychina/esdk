package com.esdk.sql.orm.tools;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.esdk.esdk;
import com.esdk.sql.JDBCTemplate;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.ABRow;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.sql.orm.IRow;
import com.esdk.sql.orm.IRowSet;
import com.esdk.sql.orm.ParentRow;
import com.esdk.sql.orm.RowFilter;
import com.esdk.utils.EasyStr;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
public class RowCodeCreater extends MetaDataCodeCreater{
  private static final String suffix="Row";
  private ABRowSet relationship=new ABRowSet("table","relationship","name","column","referenceTable","referenceColumn","hasValid");

	public void setRelationship(InputStream xml){
		if(xml!=null){
			JSONObject jsonObject=XML.toJSONObject(esdk.str.isToStr(xml));
//			System.out.println(jsonObject);
			JSONArray jsonArray=(JSONArray)jsonObject.getByPath("xml.record");
			for(Iterator iter=jsonArray.iterator();iter.hasNext();){
				IRow row=relationship.append();
				JSONObject record=(JSONObject)iter.next();
				row.set("table",record.get("table"));
				row.set("relationship",record.get("relationship"));
				row.set("name",record.get("name"));
				row.set("column",record.get("column"));
				row.set("referenceTable",record.get("referenceTable"));
				row.set("referenceColumn",record.get("referenceColumn"));
				row.set("referenceColumns",record.get("referenceColumns"));
				row.set("hasValid",esdk.obj.isTrue(record.get("hasValid")));
			}
		
		}
	}
	
  @Override protected String createJavaContent(String tablename,ABResultSet columnRs,ABResultSet primaryRs,ABResultSet indexRs,IRowSet exportKeyRs,IRowSet importKeyRs) throws Exception{
  	ABRowSet tableRelationship=relationship.filter(RowFilter.create("table",tablename));
  	String tableName=formatJavaBeanName(convertTableName(tablename),true);
    JavaCreater jc=new JavaCreater(getClassName(tableName),PackageName);
    jc.addImport(ParentRow.class.getName());
    jc.addImport(SQLException.class.getName());
    jc.addImport(Connection.class.getName());
		if(isCreateSwaggerAnnotation) {
			jc.addImport("io.swagger.annotations.ApiModel");
			jc.addImport("io.swagger.annotations.ApiModelProperty");
		}
		if(isCreateSwaggerAnnotation) {
			ABRow tableStatusRow=new JDBCTemplate(dbmd.getConnection(),"show table status LIKE ?",tablename).getFirstRow();
			jc.addClassAnnotation(" @ApiModel(\""+tableStatusRow.getString("Comment")+"\")");
		}
    jc.setExtentClassName("ParentRow<"+tableName+"Row>");
    /*javaContentCreater.setImplementClassName("ISQL");
    javaContentCreater.addConstructor("ResultSet value","    super(value);");*/
    jc.addConstructor("","    super();\r\n    setAutoIncrement("+tableName+"MetaData.IsAutoIncrement);");
    jc.addConstructor("Connection conn","    this();\r\n    setConnection(conn);");
    jc.addConstructor("Connection conn,Number pkid","throws SQLException","    this();\r\n    setConnection(conn);\r\n    refresh(pkid);");
    jc.addField("public transient",true,true,"String","PrimaryKey",tableName+"MetaData.PrimaryKey");
    jc.addField("public transient",true,true,"String","tableName",tableName+"MetaData.TABLENAME");
    jc.addField("public transient",true,true,tableName+"MetaData","metaData",tableName+"Select.metaData");
    jc.addField("public transient",true,true,tableName+"MetaData","md","metaData");
    jc.addMethod("public",tableName+"MetaData","getMetaData","","","    return metaData;");
    jc.addMethod("public",tableName+"Row","newSelf","","","    return new "+tableName+"Row();");
//    jc.addMethod("public","String","getPrimaryKeyName","","","    return PrimaryKey;");
//    jc.addMethod("public","String","getTableName","","","    return tableName;");
    
    for(columnRs.beforeFirst();columnRs.next();){
      String columnname=formatJavaBeanName(columnRs.getCurrentRow().getString("COLUMN_NAME"),true);
      String columnJavaClass=adjustsetParameterClass(getShortClassName(getJavaDataType(columnRs.getCurrentRow())));
      
			if(isCreateSwaggerAnnotation) {
				String swaggerAnnotation="  @ApiModelProperty(\""+columnRs.getCurrentRow().getString("REMARKS")+"\")";
//				if(columnJavaClass.matches("Long|Integer|Double|Short")) //不需要提供example也不会报错了
//					swaggerAnnotation="  @ApiModelProperty(value=\""+columnRs.getCurrentRow().getString("REMARKS")+"\",example=\"0\")";
				jc.addMethodAnnotation(swaggerAnnotation);
			}

			String getcontent="    return ("+columnJavaClass+")get("+tableName+"MetaData."+columnname+");";
      getcontent=adjustgetMethodContent(columnJavaClass,getcontent);
      jc.addMethod("public",getShortClassName(columnJavaClass),"get"+columnname,"","",getcontent);
      
      String setcontent="    setNewValue("+tableName+"MetaData."+columnname+",value);";
      setcontent=adjustsetMethodContent(columnJavaClass,setcontent);
      jc.addMethod("public","void","set"+columnname,columnJavaClass+" value","",setcontent);
    }
    for(Iterator iter=exportKeyRs.iterator();iter.hasNext();){
  		IRow row=(IRow)iter.next();
  		String fkTableName=EasyStr.toCamelCase(row.getString("FKTABLE_NAME"),true);
  		String className=fkTableName+"ResultSet";
  		String fkcolName=EasyStr.toCamelCase(row.getString("FKCOLUMN_NAME"),true);
  		String pkcolName=EasyStr.toCamelCase(row.getString("PKCOLUMN_NAME"),true);
  		String fieldName=fkTableName+fkcolName.replaceFirst("Id$","")+"ResultSet";
    	jc.addField("public",false,false,className,fieldName);
    	String content=EasyStr.format("\t\tif({2}==null){\r\n" + 
    			"\t\t\t{0}Select {3}Select=new {0}Select(conn,false);\r\n" + 
    			"\t\t\t{3}Select.set{4}(get{5}());\r\n" + 
    			"\t\t\t{2}={3}Select.to{1}();\r\n" + 
    			"\t\t}\r\n" + 
    			"\t\treturn {2};",
    			fkTableName,className,fieldName,EasyStr.format(fkTableName),fkcolName,pkcolName);
    	jc.addMethod("public",className,"get"+EasyStr.upperFirst(fieldName),"","throws SQLException",content);
    }

    for(Iterator iter=importKeyRs.iterator();iter.hasNext();){
  		IRow row=(IRow)iter.next();
  		String pkTableName=EasyStr.toCamelCase(row.getString("PKTABLE_NAME"),true);
  		String className=pkTableName+"Row";
  		String fkcolName=EasyStr.toCamelCase(row.getString("FKCOLUMN_NAME"),true);
  		String fieldName=EasyStr.toCamelCase(fkcolName).replaceFirst("Id$","")+"Row";
    	jc.addField("public",false,false,className,fieldName);
    	String content=EasyStr.format("  	if({2}==null)\r\n" + 
    			"  		{2}=new {1}(conn,get{3}());\r\n" + 
    			"  	return {2};",
    			pkTableName,className,fieldName,fkcolName);
    	jc.addMethod("public",className,"get"+EasyStr.upperFirst(fieldName),"","throws SQLException",content);
    }
    //relationship
    for(Iterator iter=tableRelationship.iterator();iter.hasNext();){
  		IRow row=(IRow)iter.next();
  		if(row.getString("relationship").equalsIgnoreCase("one_to_many")) {
	  		String referenceTableName=EasyStr.toCamelCase(row.getString("referenceTable"),true);
	  		String className=referenceTableName+"ResultSet";
	  		String fkcolName=EasyStr.toCamelCase(row.getString("referenceColumn"),true);
	  		String referenceColumns=row.getString("referenceColumns");
	  		String pkcolName=EasyStr.toCamelCase(findPrimaryKey(primaryRs,columnRs),true);
	  		String fieldName=row.getString("name");
	    	jc.addField("public",false,false,true,className,fieldName);
	    	String content=EasyStr.format("\t\tif({2}==null){\r\n" + 
	    			"\t\t\t{0}Select {3}Select=new {0}Select(conn,false);\r\n" + 
	    			"\t\t\t{3}Select.set{4}(get{5}());\r\n" +
	    			getReferenceColumnsMethods(referenceColumns,referenceTableName)+
	    			(row.getBoolean("hasValid")?"\t\t\t{3}Select.setValid(true);\r\n":"")+
	    			"\t\t\t{2}={3}Select.to{1}();\r\n" + 
	    			"\t\t}\r\n" + 
	    			"\t\treturn {2};",
	    			referenceTableName,className,fieldName,EasyStr.format(referenceTableName),fkcolName,pkcolName);
	    	jc.addMethod("public",className,"get"+EasyStr.upperFirst(fieldName),"","throws SQLException",content);
  		}
  		else if(row.getString("relationship").equalsIgnoreCase("many_to_one")
  				||row.getString("relationship").equalsIgnoreCase("one_to_one")) {
    		String referenceTableCamel=EasyStr.toCamelCase(row.getString("referenceTable"),true);
    		String className=referenceTableCamel+"Row";
    		String fkcolName=EasyStr.toCamelCase(row.getString("referenceColumn"),true);
    		String fieldName=row.getString("name");
      	jc.addField("public",false,false,true,className,fieldName);
      	String content=EasyStr.format("  	if({2}==null)\r\n" + 
      			"  		{2}=new {1}(conn,get{3}());\r\n" + 
      			"  	return {2};",
      			referenceTableCamel,className,fieldName,fkcolName);
      	jc.addMethod("public",className,"get"+EasyStr.upperFirst(fieldName),"","throws SQLException",content);
	  		}
    }
    jc.parse();
    return jc.getJavaContent();
  }

  private String getReferenceColumnsMethods(String referenceColumns,String tableName) {
  	if(referenceColumns==null)
  		return "";
  	Map<String,String>columnsMap=null;
  	if(referenceColumns.startsWith("{")) {
  		columnsMap=esdk.map.strToMap(referenceColumns);
  	}else {
  		columnsMap=new LinkedHashMap();
  		String[] columns=esdk.str.split(referenceColumns);
  		for(String col:columns) {
  			columnsMap.put(col,col);
  		}
  	}
  	String result="";
  	for(Iterator iter=columnsMap.entrySet().iterator();iter.hasNext();) {
  		Entry<String,String> entry=(Entry)iter.next();
  		result+=esdk.str.format("\t\t\t{}Select.set{}(get{}());\r\n",tableName,esdk.str.toCamelCase(entry.getKey(),true),esdk.str.toCamelCase(entry.getKey(),true));
  	}
  	return result;
  }
  
  private String adjustgetMethodContent(String classname,String content) {
    if(classname.indexOf("boolean")>=0) {
      content=content.replaceFirst("boolean","(Boolean");
      content=EasyStr.ReplaceFirst(content,");",")).booleanValue();",true);
    }
    else if(classname.indexOf("int")>=0){
      content=content.replaceFirst("(int)","Integer");
    }
    return content;
  }
  
  private String adjustsetMethodContent(String classname,String content) {
    if(classname.indexOf("boolean")>=0) {
      return EasyStr.ReplaceFirst(content,"value","new Boolean(value)",false);
    }
    return content;
  }

  @Override protected String getClassName(String tablename){
    return tablename+suffix;
  }
}
