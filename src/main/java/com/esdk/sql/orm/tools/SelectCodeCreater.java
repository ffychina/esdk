package com.esdk.sql.orm.tools;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.esdk.sql.Field;
import com.esdk.sql.ISQL;
import com.esdk.sql.SmartBetween;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.AbstractSelect;
import com.esdk.sql.orm.IRowSet;
import com.esdk.utils.EasyObj;
public class SelectCodeCreater extends MetaDataCodeCreater{
  private static String suffix="Select";
  @Override protected String createJavaContent(String tablename,ABResultSet columnRs,ABResultSet primaryRs,ABResultSet indexRs,IRowSet exportKeyRs,IRowSet importKeyRs) throws Exception{
   	tablename=formatJavaBeanName(convertTableName(tablename),true);//add by hrjie
    JavaCreater javaContentCreater=new JavaCreater(getClassName(tablename),PackageName);
    javaContentCreater.addImport(Connection.class.getName());
    javaContentCreater.addImport(AbstractSelect.class.getName());
    javaContentCreater.addImport(SQLException.class.getName());
    javaContentCreater.addImport(ISQL.class.getName());
    javaContentCreater.addImport(Field.class.getName());
    javaContentCreater.setExtentClassName("AbstractSelect<"+tablename+"Select,"+tablename+"Row>");
    javaContentCreater.setImplementClassName("ISQL");
    javaContentCreater.addConstructor("","    super("+tablename+"MetaData.TABLENAME,false);");
    javaContentCreater.addConstructor("Connection conn,boolean isTop","    super("+tablename+"MetaData.TABLENAME,isTop,conn);");
    javaContentCreater.addConstructor("Connection conn","    super("+tablename+"MetaData.TABLENAME,conn);");
    javaContentCreater.addConstructor("boolean isJoin","    super("+tablename+"MetaData.TABLENAME,isJoin);");
    javaContentCreater.addConstructor("String joinType","    super("+tablename+"MetaData.TABLENAME,joinType);");
    javaContentCreater.addConstructor("com.esdk.sql.orm.ORMSession ormsession,boolean isTop","    super("+tablename+"MetaData.TABLENAME,isTop,ormsession);");
    javaContentCreater.addConstructor("com.esdk.sql.orm.ORMSession ormsession","    super("+tablename+"MetaData.TABLENAME,ormsession);");

    javaContentCreater.addField("public",true,true,tablename+"MetaData","metaData","new "+tablename+"MetaData()");
    javaContentCreater.addField("public",true,true,tablename+"MetaData","md","metaData");
    javaContentCreater.addMethod("public",tablename+"MetaData","getMetaData","","","    return metaData;");
    javaContentCreater.addMethod("public",""+tablename+"Row[]","to"+tablename+"RowArray","","throws Exception","    return ("+tablename+"Row[])list().toArray(new "+tablename+"Row[0]);");
    javaContentCreater.addMethod("public",""+tablename+"Row","getFirst"+tablename+"Row","","","    return ("+tablename+"Row)getFirstRow();");
    javaContentCreater.addMethod("public",""+tablename+"Row","getFirst"+tablename+"Row","boolean isCreateInstance","","    return ("+tablename+"Row)getFirstRow(isCreateInstance);");
    
    javaContentCreater.addMethod("public",""+tablename+"ResultSet","to"+tablename+"ResultSet","","throws SQLException","    return ("+tablename+"ResultSet)toParentResultSet();");
    javaContentCreater.addMethod("public","void","setPrimaryKey","Object value","","    fieldMap.put("
        +tablename+"MetaData.PrimaryKey"+",value);");
    ArrayList columnList=new ArrayList();
    for(columnRs.beforeFirst();columnRs.next();){
      String columnname=formatJavaBeanName(columnRs.getCurrentRow().getString("COLUMN_NAME"),true);
      columnList.add(getStringValue(columnname));
      
      String columnJavaClass=adjustsetParameterClass(getShortClassName(getJavaDataType(columnRs.getCurrentRow())));
      String getcontent="    return ("+columnJavaClass+")fieldMap.get("+tablename+"MetaData."+columnname+");";
      //getcontent=adjustgetMethodContent(columnJavaClass,getcontent) ;
      javaContentCreater.addMethod("public",columnJavaClass,"get"+columnname,"","",getcontent);
      String setcontent="    fieldMap.put("+tablename+"MetaData."+columnname+",value);\r\n    return this;";
      //setcontent=adjustsetMethodContent(columnJavaClass,setcontent);
      javaContentCreater.addMethod("public",tablename+"Select","set"+columnname,columnJavaClass+" value","",setcontent);
      if(!columnJavaClass.equals("Date")&&!columnJavaClass.equals("Boolean")) {
	      String setInContent="    super.addIn("+tablename+"MetaData."+columnname+",values);\r\n    return this;";
	      javaContentCreater.addMethod("public",tablename+"Select","set"+columnname,columnJavaClass+"[] values","",setInContent);

	      String setInSelectContent="    super.addIn("+tablename+"MetaData."+columnname+",select);\r\n    return this;";
	      javaContentCreater.addMethod("public",tablename+"Select","set"+columnname,"AbstractSelect select","",setInSelectContent);
      }
      if(columnJavaClass.equals("String")){
      	 String likecontent="    addLikeCondition(metaData."+columnname+",value);\r\n    return this;";
      	 javaContentCreater.addMethod("public",tablename+"Select","addLike"+columnname,columnJavaClass+" value","",likecontent);
      }
      if(!columnname.endsWith("Id")&&(columnJavaClass.equals("Date")||columnJavaClass.equals("BigDecimal")||columnJavaClass.equals("Short")
      		||columnJavaClass.equals("Integer")||columnJavaClass.equals("Double")||columnJavaClass.equals("Long"))) {
      	String betweenContent="    fieldMap.put("+tablename+"MetaData."+columnname+",new SmartBetween(this.createField("+tablename+"MetaData."+columnname+"),start,end));\r\n    return this;";
      	javaContentCreater.addMethod("public",tablename+"Select","set"+columnname,columnJavaClass+" start,"+columnJavaClass+" end","",betweenContent);
      	javaContentCreater.addImport(SmartBetween.class);
      }
      String methodcontent="    return this.createField(metaData."+columnname+");";
      javaContentCreater.addMethod("public",Field.class.getSimpleName(),"create"+columnname+"Field","","",methodcontent);
    }
    javaContentCreater.parse();
    return javaContentCreater.getJavaContent();
  }

  protected String getClassName(String tablename){
    return tablename+suffix;
  }
}
