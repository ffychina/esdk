package com.esdk.sql.orm.tools;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.esdk.esdk;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.IRowSet;
import com.esdk.sql.orm.ParentResultSet;
import com.esdk.utils.EasyObj;
public class ResultSetCodeCreater extends MetaDataCodeCreater{
  private static final String suffix="ResultSet";

  @Override protected String createJavaContent(String tablename,ABResultSet columnRs,ABResultSet primaryRs,ABResultSet indexRs,IRowSet exportKeyRs,IRowSet importKeyRs) throws Exception{
  	tablename=convertTableName(tablename);
  	tablename=formatJavaBeanName(tablename,true);
    JavaCreater javaContentCreater=new JavaCreater(getClassName(tablename),PackageName);
    javaContentCreater.addImport(ResultSet.class.getName());
    javaContentCreater.addImport(SQLException.class.getName());
    javaContentCreater.addImport(ParentResultSet.class.getName());
    
    javaContentCreater.setExtentClassName("ParentResultSet<"+tablename+"Row>");
    esdk.tool.read(primaryRs);
    /*javaContentCreater.setImplementClassName("ISQL");*/
    javaContentCreater.addConstructor("ResultSet value","    super(value);");
    javaContentCreater.addField("public",true,true,tablename+"MetaData","metaData",tablename+"Select.metaData");
    javaContentCreater.addField("public",true,true,tablename+"MetaData","md","metaData");
    javaContentCreater.addMethod("public",tablename+"Row","createRowInstance","","","    return new "+tablename+"Row();");
    javaContentCreater.addMethod("public",tablename+"Row","getCurrentRow","","throws SQLException","    return ("+tablename+"Row)super.gainCurrentRow(rs);");
    javaContentCreater.addMethod("public",tablename+"Row[]","getAllRow","","throws SQLException","    return ("+tablename+"Row[])gainAllRow().toArray(new "+tablename+"Row[0]);");

    
    ArrayList columnList=new ArrayList();
    for(columnRs.beforeFirst();columnRs.next();){
      String columnname=formatJavaBeanName(columnRs.getCurrentRow().getString("COLUMN_NAME"),true);
      columnList.add(getStringValue(columnname));
      String columnJavaClass=getJavaDataType(columnRs.getCurrentRow());
      
      String getcontent="    return rs.get"+adjustResultSetMethod(getShortClassName(columnJavaClass))+"("+tablename+"MetaData."+columnname+");";
      javaContentCreater.addMethod("public",adjustsetParameterClass(getShortClassName(columnJavaClass)),"get"+columnname,"","throws SQLException",getcontent);
      
/*      String setcontent="    fieldMap.put("+getStringValue(columnname)+",value);";
      javaContentCreater.addMethold("public","void","set"+columnname,columnJavaClass+" value","throws SQLException",setcontent);*/
    }
    javaContentCreater.parse();
    return javaContentCreater.getJavaContent();
  }
  
  protected String getClassName(String tablename){
    return tablename+suffix;
  }
}
