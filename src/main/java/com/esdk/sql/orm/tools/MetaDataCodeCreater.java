package com.esdk.sql.orm.tools;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.esdk.esdk;
import com.esdk.sql.JDBCTemplate;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.ABRow;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.sql.orm.ForeignKey;
import com.esdk.sql.orm.IRow;
import com.esdk.sql.orm.IRowSet;
import com.esdk.utils.CharAppender;
import com.esdk.utils.EasyFile;
import com.esdk.utils.EasyMath;
import com.esdk.utils.EasyObj;
import com.esdk.utils.EasySql;
import com.esdk.utils.EasyRegex;
import com.esdk.utils.EasyObj;
public class MetaDataCodeCreater{
	protected static DatabaseMetaData dbmd;
	private static ABRowSet importedKeysRs=null;
	private static ABRowSet exportedKeysRs=null;
	public static String PackageName="com.esdk.orm";
	public static String parentPath=null;
	public boolean isOverwrite=false;
	private static final String suffix="MetaData";
	public static String schemaPattern=null,catelog=null;
	public static boolean isCreateSwaggerAnnotation=true;

	protected JavaCreater create(String tablename,ABResultSet columnRs,ABResultSet primaryRs,ABResultSet indexRs,
			IRowSet exportKeyRs,IRowSet importKeyRs) throws Exception{
		String javaFieldName=formatJavaBeanName(convertTableName(tablename),true);//modify by hrjie
		JavaCreater jc=new JavaCreater(getClassName(javaFieldName),PackageName);
		jc.addField("public",true,true,"String","TABLENAME",getStringValue((tablename)));
		ABRow tableStatusRow=new JDBCTemplate(dbmd.getConnection(),"show table status LIKE ?",tablename).getFirstRow();
		String tableComment=tableStatusRow.getString("Comment");
		if(tableComment.equalsIgnoreCase("VIEW")) {
			tableComment=(String)esdk.reflect.getFieldValue(esdk.reflect.safeNewInstance(PackageName+'.'+javaFieldName.replace("View","MetaData")),"TableComment")+"视图";
		}
		jc.addField("public",true,true,"String","TableComment","\""+tableComment+"\"");
		
		String primaryKeyName=findPrimaryKey(primaryRs,columnRs,jc);
		List<ABRowSet> uniIndexGroup=indexRs.filters("TYPE",3,"COLUMN_NAME","!=",primaryKeyName).group("INDEX_NAME");
		CharAppender uniqueIndexFields=new CharAppender(',');
		for(ABRowSet groupItem:uniIndexGroup){
			String columnNames=getStringArrayValue(groupItem.getStrings("COLUMN_NAME"));
			uniqueIndexFields.append(columnNames);
		}
		jc.addField("public",true,true,"String[][]","UniqueIndexFields","{"+uniqueIndexFields+"}");
		ArrayList columnList=new ArrayList(columnRs.size());
		CharAppender remarks=new CharAppender(',');
		CharAppender isNullables=new CharAppender(',');
		CharAppender columnDefs=new CharAppender(',');
		ArrayList columnDataTypeList=new ArrayList(columnRs.size());
		for(columnRs.beforeFirst();columnRs.next();){
			String fieldName=formatJavaBeanName(columnRs.getCurrentRow().getString("COLUMN_NAME"),true);
			String columnName=columnRs.getCurrentRow().getString("COLUMN_NAME");
			jc.addField("public",true,true,"String",fieldName,getStringValue(convertTableName(columnName)));//modify by hrjie
			columnList.add(getStringValue(columnName));
			remarks.append(getStringValue(columnRs.getCurrentRow().getString("REMARKS")));
			columnDefs.append(getColumnDef(columnRs.getCurrentRow().getString("COLUMN_DEF")));
			isNullables.append(getStringValue(columnRs.getCurrentRow().getString("IS_NULLABLE")).replaceAll("NO","false").replaceAll("YES","true").replaceAll("\"",""));
			/* String datatype=columnRs.getString("DATA_TYPE"); */
			jc.addImport(findJavaClass(columnRs.getCurrentRow()));
			columnDataTypeList.add(adjustsetParameterClass(getJavaDataType(columnRs.getCurrentRow()))+".class");
		}
		jc.addImport(ForeignKey.class);
		CharAppender exportKeyVariables=new CharAppender(',');
		for(Iterator iter=exportKeyRs.iterator();iter.hasNext();){
			IRow row=(IRow)iter.next();
			String className=getFKClassName(row.get("FK_NAME").toString());
			String variableName="PK"+className.substring(2);
			jc.addField("public",true,true,className,variableName,"new "+className+"()");
			exportKeyVariables.append(variableName);
		}
		jc.addField("public",true,true,"ForeignKey[]","ExportKeys","new ForeignKey[]{"+exportKeyVariables.toString()+"}");
		CharAppender importKeyVariables=new CharAppender(',');
		for(Iterator iter=importKeyRs.iterator();iter.hasNext();){
			IRow row=(IRow)iter.next();
			String className=getFKClassName(row.get("FK_NAME").toString());
			String variableName="FK"+className.substring(2);
			jc.addField("public",true,true,className,variableName,"new "+className+"()");
			importKeyVariables.append(variableName);
		}
		jc.addField("public",true,true,"ForeignKey[]","ImportKeys","new ForeignKey[]{"+importKeyVariables.toString()+"}");
		String columns=esdk.str.arrToStr((String[])columnList.toArray(new String[0]));
		String columndatatypes=esdk.str.arrToStr((String[])columnDataTypeList.toArray(new String[0]));
		jc.addField("public",true,true,"String[]","FieldNames","new String[]{"+columns+"}");
		jc.addField("public",true,true,"String[]","Remarks","new String[]{"+remarks+"}");
		jc.addField("public",true,true,"Class[]","FieldTypes","new Class[]{"+columndatatypes+"}");
		jc.addField("public",true,true,"boolean[]","isNullables","new boolean[]{"+isNullables+"}");
		jc.addField("public",true,true,"Object[]","ColumnDefs","new Object[]{"+columnDefs+"}");
		for(int i=0,n=columnRs.size();i<n;i++){
			IRow row=columnRs.getRow(i);
			String fieldName=formatJavaBeanName(row.getString("COLUMN_NAME"),true);
			String remark=row.getString("REMARKS");
			if(remark==null)
				remark=getCommonFieldRemark(fieldName);
			remark=getStringValue(remark);
			jc.addField("public",true,true,"String","R"+fieldName,remark);
		}
		return jc;
	}

	private String getColumnDef(String defVal){
		if(defVal==null)
			return null;
		else if(esdk.math.isNumeric(defVal))
			return defVal+"";
		else if(defVal.contains("b'"))
			return esdk.regex.findSub(defVal,"b'(\\d+)'",1);
		else 
			return getStringValue(defVal);
	}

	private static Map<String,String> map=esdk.map.createMap("remark","备注","version","版本号","create_time","创建时间","create_user_id","创建人ID","create_user_name","创建人","modify_time","修改时间","modify_user_id","修改人ID","modify_user_name","修改人","delete_time","删除时间","delete_user_id","删除人ID","delete_user_name","删除人","valid","有效标识");
	private String getCommonFieldRemark(String fieldName){
		return esdk.obj.or(map.get(fieldName),"");
	}

	protected String createJavaContent(String tablename,ABResultSet columnRs,ABResultSet primaryRs,ABResultSet indexRs,
			IRowSet exportKeyRs,IRowSet importKeyRs) throws Exception{
		JavaCreater jc=create(tablename,columnRs,primaryRs,indexRs,exportKeyRs,importKeyRs);
		jc.parse();
		return jc.getJavaContent();
	}

	protected String findPrimaryKey(ABResultSet primaryRs,ABResultSet columnRs) throws SQLException{
		String primaryKeyFieldName;
		if(primaryRs.next()){
			primaryKeyFieldName=primaryRs.getCurrentRow().getString("COLUMN_NAME");
		}else{
			primaryKeyFieldName=columnRs.getFirstRow().getString("COLUMN_NAME");
		}
		if(!dbmd.getDatabaseProductName().equals("Oracle")){
			for(columnRs.beforeFirst();columnRs.isAfterLast();columnRs.next()){
				if(columnRs.getCurrentRow().getString("COLUMN_NAME").equals(primaryKeyFieldName)){
					if(columnRs.getCurrentRow().getString("TYPE_NAME").indexOf("identity")>0
							||EasyObj.equal(columnRs.getCurrentRow().getString("IS_AUTOINCREMENT"),"YES")){
						break;
					}
				}
			}
		}
		return primaryKeyFieldName;
	}

	protected String findPrimaryKey(ABResultSet primaryRs,ABResultSet columnRs,JavaCreater javaContentCreater)
			throws NumberFormatException,SQLException{
		String primaryKeyFieldName;
		if(primaryRs.first()){
			primaryKeyFieldName=primaryRs.getCurrentRow().getString("COLUMN_NAME");
			javaContentCreater.addField("public",true,true,"String","PrimaryKey",getStringValue(primaryKeyFieldName));
			javaContentCreater.addField("public",true,true,"int","IPrimaryKey",
					String.valueOf(Integer.valueOf(primaryRs.getCurrentRow().getString("KEY_SEQ")).intValue()-1));
		}else{
			columnRs.next();
			primaryKeyFieldName=columnRs.getCurrentRow().getString("COLUMN_NAME");
			// columnRs.afterLast();
			columnRs.first();
			javaContentCreater.addField("public",true,true,"String","PrimaryKey",getStringValue(primaryKeyFieldName));
			javaContentCreater.addField("public",true,true,"int","IPrimaryKey","0");
			columnRs.beforeFirst();
		}
		columnRs.beforeFirst();
		boolean isAutoIncrement=false;
		if(!dbmd.getDatabaseProductName().equals("Oracle")){
			while(columnRs.next()){
				if(columnRs.getCurrentRow().getString("COLUMN_NAME").equals(primaryKeyFieldName)){
					if(columnRs.getCurrentRow().getString("TYPE_NAME").indexOf("identity")>0
							||EasyObj.equal(columnRs.getCurrentRow().getString("IS_AUTOINCREMENT"),"YES")){
						isAutoIncrement=true;
						break;
					}
				}
			}
		}else{
			String sequenceName="SEQ_"+columnRs.getRow(0).getString("TABLE_NAME");
			isAutoIncrement=new JDBCTemplate(dbmd.getConnection(),"SELECT COUNT(*) FROM user_sequences WHERE sequence_name='"
					+sequenceName+"'").count()==1;
		}
		javaContentCreater.addField("public",true,true,"boolean","IsAutoIncrement",Boolean.valueOf(isAutoIncrement)
				.toString());
		return primaryKeyFieldName;
	}

	public static String formatJavaBeanName(String columnName,boolean isCaptial){
		return esdk.str.toCamelCase(columnName,isCaptial);
	}

	public static String toCamelCase(String columnName){
		return esdk.str.toCamelCase(columnName);
	}

	public static String adjustsetParameterClass(String javaclass){
		javaclass=javaclass.replaceFirst("Time14","BigDecimal");
		if(javaclass.equals("boolean"))
			return javaclass.replaceFirst("boolean","Boolean");
		else if(javaclass.equals("float"))
			return javaclass.replaceFirst("float","Double");
		else if(javaclass.equals("long"))
			return javaclass.replaceFirst("long","Long");
		else if(javaclass.equals("short"))
			return javaclass.replaceFirst("short","Short");
		else if(javaclass.equals("int"))
			return javaclass.replaceFirst("int","Integer");
		else if(javaclass.equals("double"))
			return javaclass.replaceFirst("double","Double");
		return javaclass;
	}

	public static String adjustResultSetMethod(String classname){
		String result=classname.replaceFirst("Time14","BigDecimal");
		if(classname.equals("short"))
			result=classname.replaceFirst("short","Short");
		else if(classname.equals("int"))
			result=classname.replaceFirst("int","Int");
		else if(classname.equals("long"))
			result=classname.replaceFirst("long","Long");
		else if(classname.equals("float"))
			result=classname.replaceFirst("float","Double");
		else if(classname.equals("double"))
			result=classname.replaceFirst("double","Double");
		else if(classname.equals("boolean"))
			result=classname.replaceFirst("boolean","Boolean");
		return result;
	}

	protected String getJavaDataType(IRow columnMetaDataRow){
		String columnType=columnMetaDataRow.getString("TYPE_NAME").toLowerCase();
		int columnSize=columnType.equals("bit")?0:columnMetaDataRow.getInteger("COLUMN_SIZE");
		int digits=EasyMath.toInt(columnMetaDataRow.getInteger("DECIMAL_DIGITS"));
		return getJavaDataType(columnType,columnSize,digits);
	}
	
	protected String getJavaDataType(String columntype,int columnsize,int digits){
		if(columntype.matches("number|int|int4|serial|tinyint")&&digits==0&&(columnsize>=2&&columnsize<=11)) 
			return "int";
		else if(columntype.equalsIgnoreCase("int identity"))
			return "int";
		else if(columntype.matches("int8|bigint|long|int unsigned"))
			return "long";
		else if(columntype.matches("number|int")&&columnsize>=8&&columnsize!=10&&digits==0)
			return "long";
		else if(columntype.matches("smallint|short"))
			return "short";
		else if(columntype.matches("bit|bool|boolean"))
			return "boolean";
		else if(columntype.matches("number|tinyint")&&(columnsize==1||columnsize==3))// mysql view用到union时，tinyint(1)会变为tinyint(3)，应调整为boolean类型。
			return "boolean";
		else if(columntype.matches("money|double|float|float8|real")||(columntype.matches("number|decimal")&&columnsize==8&&digits>0))
			return "double";
		else if(columntype.matches("number")&&(columnsize>10||columnsize==16)||columntype.equals("numeric() identity")||columntype.equals("tonytime"))
			return "BigDecimal";
		else if(EasyRegex.find(columntype,"varchar|varchar2|nvarchar|text|longtext|char|uniqueidentifier"))
			return "String";
		else if(columntype.equals("binary"))
			return InputStream.class.getName();
		else if(columntype.equalsIgnoreCase("smalldatetime"))
			return Date.class.getName();
		else if(columntype.equalsIgnoreCase("datetime")||columntype.equalsIgnoreCase("timestamp")
				||columntype.equalsIgnoreCase("date"))
			return Date.class.getName();
		return Object.class.getName();
	}

	protected Class findJavaClass(IRow columnMetaDataRow){
		String columnType=columnMetaDataRow.getString("TYPE_NAME");
		int columnSize=columnType.equalsIgnoreCase("BIT")?0:columnMetaDataRow.getInteger("COLUMN_SIZE");
		int digits=EasyMath.toInt(columnMetaDataRow.getInteger("DECIMAL_DIGITS"));
		return findJavaClass(columnType,columnSize,digits);
	}
	
	protected Class findJavaClass(String columntype,int columnsize,int digits){
		if(columntype.equalsIgnoreCase("number")&&columnsize>=2&&columnsize<=4||columnsize<=11)
			return Integer.class;
		if(columntype.equalsIgnoreCase("number")&&columnsize==1)
			return Boolean.class;
		if(columntype.equalsIgnoreCase("number")&&(columnsize>10||columnsize==16))
			return BigDecimal.class;
		if(columntype.equalsIgnoreCase("number")&&columnsize==8&&digits>0)
			return Double.class;
		if(columntype.equalsIgnoreCase("number")&&columnsize==8&&digits==0)
			return Long.class;
		if(columntype.equalsIgnoreCase("numeric")||columntype.equalsIgnoreCase("number"))
			return BigDecimal.class;
		else if(columntype.equalsIgnoreCase("numeric() identity"))
			return BigDecimal.class;
		else if(columntype.equalsIgnoreCase("decimal"))
			return Double.class;
		else if(columntype.equalsIgnoreCase("tonytime"))
			return BigDecimal.class;
		else if(columntype.equalsIgnoreCase("money"))
			return Double.class;
		else if(EasyRegex.find(columntype,"varchar|varchar2|nvarchar|text|longtext|char|uniqueidentifier"))
			return String.class;
		else if(columntype.equalsIgnoreCase("binary"))
			return InputStream.class;
		else if(columntype.equalsIgnoreCase("bit")&&columnsize==1)
			return Boolean.class;
		else if(columntype.equalsIgnoreCase("smallint"))
			return Short.class;
		else if(columntype.equalsIgnoreCase("short"))
			return Short.class;
		else if(columntype.equalsIgnoreCase("int"))
			return Integer.class;
		else if(columntype.equalsIgnoreCase("long"))
			return Long.class;
		else if(columntype.equalsIgnoreCase("float"))
			return Double.class;
		else if(columntype.equalsIgnoreCase("double"))
			return Double.class;
		else if(columntype.equalsIgnoreCase("int identity"))
			return Integer.class;
		else if(columntype.equalsIgnoreCase("smalldatetime"))
			return Date.class;
		else if(columntype.equalsIgnoreCase("datetime"))
			return Date.class;
		else if(columntype.equalsIgnoreCase("date"))
			return Date.class;
		return Object.class;
	}

	protected String getJavaFileName(String tablename){
		String path=parentPath+esdk.str.ReplaceAll(PackageName,".","/",false)+"/";
		return path.concat(getClassName(convertTableName(tablename))).concat(".java");//modify by hrjie
	}

	private String getFKJavaFileName(String className){
		String path=parentPath+esdk.str.ReplaceAll(PackageName,".","/",false)+"/";
		return path.concat(className).concat(".java");
	}

	protected String getClassName(String tablename){
		return tablename+suffix;
	}
//add by hrjie
	protected String convertTableName(String tablename){
		if(esdk.str.indexOf(tablename,"T_",true)==0){
			tablename=tablename.substring(2);
		}else if(esdk.str.indexOf(tablename,"V_",true)==0){
			tablename=tablename.substring(2);
			tablename+="_View";
		}
		return tablename;
	}

	public void action(String tablename){
		try{
			catelog=dbmd.getConnection().getCatalog();
			ABResultSet columnRs=new ABResultSet(dbmd.getColumns(catelog,schemaPattern,tablename,"%"));
			ABResultSet primaryRs=new ABResultSet(dbmd.getPrimaryKeys(catelog,schemaPattern,tablename));
			ABResultSet indexRs=getIndexRsForSafy(tablename);
			if(columnRs.isEmpty()) {
				System.err.println(tablename+"没有找到任何字段，请检查数据表是否存在");
				return;
			}
			// must transfer exportKeyRs and importedKeyRs to ABRowset,because just
			// ABResultSet will throw ResultSet already close exception in sqlserver
			// jdbc
			ABRowSet exportKeyRs=getExportedKeysRs(tablename);
			ABRowSet importedKeyRs=getImportedKeysRs(tablename);
			if(this.getClass().equals(MetaDataCodeCreater.class)){
				creteFKJavaFile(exportKeyRs);
				creteFKJavaFile(importedKeyRs);
			}
			String javaFileName=getJavaFileName(formatJavaBeanName(convertTableName(tablename),true));
			if(new File(javaFileName).exists()&&!isOverwrite)
				System.err.println("file exist:"+javaFileName);
			else{
				/*
				 * String xmlfilename=getXmlFileName(tablename); boolean
				 * xmlsaveresult=ffyfile
				 * .SaveToFile(ffyxml.DocToStr(createXml(tablename,columnRs
				 * ,primaryRs)),xmlfilename,true,true);
				 * System.out.println(xmlfilename+" "+xmlsaveresult);
				 */
				String content=createJavaContent((tablename),columnRs,primaryRs,indexRs,exportKeyRs,importedKeyRs);
				if(!new File(javaFileName).exists()||!EasyFile.loadFromFile(new File(javaFileName)).equals(content)){
					boolean javasaveresult=EasyFile.saveToFile(content,javaFileName,true,true,"utf8");
					System.out.println(javaFileName+" "+javasaveresult);
				}else
					System.out.println(javaFileName+" is not any changed.");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private ABRowSet getImportedKeysRs(String tablename) throws SQLException{
		if(importedKeysRs==null)
			importedKeysRs=new ABRowSet(new ABResultSet(dbmd.getImportedKeys(catelog,schemaPattern,"%")));
		return importedKeysRs.filter("FKTABLE_NAME",tablename);
	}

	private ABRowSet getExportedKeysRs(String tablename) throws SQLException{
		if(exportedKeysRs==null)
			exportedKeysRs=new ABRowSet(new ABResultSet(dbmd.getExportedKeys(catelog,schemaPattern,"%")));
		return exportedKeysRs.filter("PKTABLE_NAME",tablename);
	}

	private ABResultSet getIndexRsForSafy(String tablename) throws SQLException{
		if(!dbmd.getDatabaseProductName().equals("Oracle"))
			return new ABResultSet(dbmd.getIndexInfo(catelog,schemaPattern,tablename,true,false));
		else{
			try{
				return new ABResultSet(dbmd.getIndexInfo(catelog,schemaPattern,tablename,false,false));
			}catch(SQLException e){
				if(e.getMessage().trim().indexOf("ORA-01702")>=0) // "ORA-01702: 视图不适用于此处"
																													// or
																													// "ORA-01702: a view is not appropriate here"
					return new ABResultSet(dbmd.getPrimaryKeys(catelog,schemaPattern,tablename)); // 如果是view就返回一个空的ResultSet
			}
			return null;
		}
	}

	private String getFKClassName(String fkname){
		return esdk.str.toCamelCase(fkname.toString(),true).replaceFirst("Fk","FK");
	}

	protected String getStringValue(String strVariant){
		return strVariant==null?null:"\"".concat(esdk.str.getStringNoNull(strVariant)).trim().replace("\\","\\\\").concat("\"");
	}

	protected String getStringArrayValue(String[] array){
		CharAppender ca=new CharAppender(',');
		for(int i=0;i<array.length;i++){
			ca.append(getStringValue(array[i]));
		}
		return "{"+ca+"}";
	}
		private void creteFKJavaFile(IRowSet exportKeyRs) throws IOException{
		for(Iterator iter=exportKeyRs.iterator();iter.hasNext();){
			IRow row=(IRow)iter.next();
			String className=getFKClassName(row.get("FK_NAME").toString());
			JavaCreater jc=new JavaCreater(className,PackageName);
			jc.setExtentClassName(ForeignKey.class.getSimpleName());
			jc.addImport(ForeignKey.class);
			jc.addField("public",true,true,"String","PKTABLE_CAT",getStringValue(row.getString("PKTABLE_CAT")));
			jc.addField("public",true,true,"String","PKTABLE_SCHEM",getStringValue(row.getString("PKTABLE_SCHEM")));
			jc.addField("public",true,true,"String","PKTABLE_NAME",getStringValue(row.getString("PKTABLE_NAME")));
			jc.addField("public",true,true,"String","PKCOLUMN_NAME",getStringValue(row.getString("PKCOLUMN_NAME")));
			jc.addField("public",true,true,"String","FKTABLE_SCHEM",getStringValue(row.getString("FKTABLE_SCHEM")));
			jc.addField("public",true,true,"String","FKTABLE_NAME",getStringValue(row.getString("FKTABLE_NAME")));
			jc.addField("public",true,true,"String","FKCOLUMN_NAME",getStringValue(row.getString("FKCOLUMN_NAME")));
			jc.addField("public",true,true,"int","KEY_SEQ",row.getString("KEY_SEQ"));
			jc.addField("public",true,true,"int","UPDATE_RULE",row.getString("UPDATE_RULE")==null?"0":row.getString("UPDATE_RULE"));
			jc.addField("public",true,true,"int","DELETE_RULE",row.getString("DELETE_RULE"));
			jc.addField("public",true,true,"String","FK_NAME",getStringValue(row.getString("FK_NAME")));
			jc.addField("public",true,true,"String","PK_NAME",getStringValue(row.getString("PK_NAME")));
			jc.addField("public",true,true,"int","DEFERRABILITY",row.getString("DEFERRABILITY"));
			String value="\tsetPkTableCat(PKTABLE_CAT);\n";
			value+="\tsetPkTableSchem(PKTABLE_SCHEM);\n";
			value+="\tsetPkTableName(PKTABLE_NAME);\n";
			value+="\tsetPkColumnName(PKCOLUMN_NAME);\n";
			value+="\tsetFkTableSchem(FKTABLE_SCHEM);\n";
			value+="\tsetFkTableName(FKTABLE_NAME);\n";
			value+="\tsetFkColumnName(FKCOLUMN_NAME);\n";
			value+="\tsetKeySeq(KEY_SEQ);\n";
			value+="\tsetUpdateRule(UPDATE_RULE);\n";
			value+="\tsetDeleteRule(DELETE_RULE);\n";
			value+="\tsetFkName(FK_NAME);\n";
			value+="\tsetPkName(PK_NAME);\n";
			value+="\tsetDeferrability(DEFERRABILITY);\n";
			jc.addConstructor("",value);
			jc.parse();
			String content=jc.getJavaContent();
			String javaFileName=getFKJavaFileName(className);
			if(!new File(javaFileName).exists()||!EasyFile.loadFromFile(new File(javaFileName)).equals(content)){
				boolean javasaveresult=EasyFile.saveToFile(content,javaFileName,true,true,"utf8");
				System.out.println(javaFileName+" "+javasaveresult);
			}else
				System.out.println(javaFileName+" is not any changed.");
		}
	}

	public void action(String[] tables){
		if(!new File(parentPath).exists()){
			System.err.println(esdk.str.format("!找不到该文件夹{0}，不能自动创建，请设置正确的文件夹路径",parentPath));
			return;
		}
		try{
			for(int i=0;i<tables.length;i++){
				action(tables[i]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void action(){
		if(!new File(parentPath).exists()){
			System.err.println(esdk.str.format("!找不到该文件夹{0}，不能自动创建，请设置正确的文件夹路径",parentPath));
			return;
		}
		try{
			action(EasySql.getTablesAndViews(dbmd));
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	protected String getShortClassName(String clspath){
		return clspath.substring(clspath.lastIndexOf(".")+1);
	}


}
