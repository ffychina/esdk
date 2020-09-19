package com.esdk.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esdk.esdk;
import com.esdk.log.DefaultLogLevel;
import com.esdk.log.Logger;
import com.esdk.utils.CharAppender;
import com.esdk.utils.EasyMath;
import com.esdk.utils.EasyStr;
import com.esdk.utils.EasyTime;
import com.esdk.utils.EasyRegex;
import com.esdk.utils.RegexReplace;
import com.esdk.utils.TimeMeter;
import com.esdk.utils.EasyObj;


public class SQLAssistant{
	public static int DefaultSelectLimit=esdk.prop.getInteger("DefaultSelectLimit",50);
	public static boolean IsShowSql=esdk.prop.getBoolean("IsShowSql",true);
	public static String[] ShowSqlExcludes=esdk.str.split(esdk.prop.get("ShowSqlExcludes"));
	public static boolean IsLogSql=esdk.prop.getBoolean("IsLogSql",true);
	public static boolean IsShowSpend=esdk.prop.getBoolean("IsShowSpend",true);
	public static String LogFilePath=esdk.prop.get("LogFilePath","/logs/sql.log");
	public static SqlDialect Dialect=new SqlDialect(null,esdk.prop.getString("DatabaseProductName",SqlDialect.DatabaseProductName_MySQL));
	public static int LongQueryTime=esdk.prop.getInteger("LongQueryTime",2000);
	public static String LogSlowQueries=esdk.prop.get("LogSlowQueries","/logs/slow_sql.log");
	public static String LogName=esdk.prop.getString("LogName","sql");
	public static boolean IsShowConnectionStatus=esdk.prop.getBoolean("IsShowConnectionStatus",false);
	public static Logger _log,_slowlog;

	public static void setDatabaseProductName(String value){
		Dialect.setDatabaseProductName(value);
	}
	
	public static void setDatabaseProductName(Connection conn){
		Dialect.setDatabaseProductName(conn);
	}
	
	private static Logger getLogger() {
		if(_log==null){
			_log=new Logger(new File(esdk.tool.getPropertiesValueWithSystemEnvirnment(LogFilePath)),LogName);
		}
		return _log;
	}
	
	private static Logger getSlowLogger() {
		if(_slowlog==null){
			_slowlog=new Logger(new File(esdk.tool.getPropertiesValueWithSystemEnvirnment(LogSlowQueries)),"slowsql");
			_slowlog.setSilence(true);
		}
		return _slowlog;
	}
	
	static void printSql(ISQL sql,long spendMs) {
		if (SQLAssistant.IsShowSpend){
			if(!checkExcludesSql(sql)) {
				String s = "spend:" + spendMs + "ms. ";
				printSql(s + sql.getSQL(), spendMs > LongQueryTime ? DefaultLogLevel._WARN : DefaultLogLevel._INFO);
			}
		}else{
			printSql(sql);
		}
	}
	
	public static void printSql(ISQL sql,TimeMeter meter) {
		printSql(sql,meter.getElapse());
	}
	
	public static void printSql(String sql) {
		printSql(sql,DefaultLogLevel._INFO);
	}

	public static void printSql(ISQL sql) {
		if(!checkExcludesSql(sql))
			printSql(sql.getSQL());
	}

	public static void printSql(BatchInsert bi,TimeMeter tm) {
		if (SQLAssistant.IsShowSpend){
			if(!checkExcludesSql(bi)) {
				String s = "spend:" + tm.getElapse() + "ms. ";
				printSql(s + getBatchShortSql(bi), tm.getElapse() > LongQueryTime ? DefaultLogLevel._WARN : DefaultLogLevel._INFO);
			}
		}else{
			printSql(getBatchShortSql(bi));
		}
	}

	private static String getBatchShortSql(BatchInsert bi) {
			if(bi.size()>10)
				return bi.getInsertFields()+"..."+"共插入"+bi.size()+"条记录";
			else
				return bi.getSQL();
	}

	public static String getPettySql(String sql) { //TODO 
		return sql; 
	}
	
	public static String getOneLineSql(String sql) {
		return sql.replaceAll("\\r?\\n"," ");
	}
	
	private static void printSql(String sql,int level) {
		if(!sql.endsWith(";"))
			sql+=";";
/*		sql = "\r\n\t"+sql;*/
		if(IsLogSql) {
			switch(level){
			case DefaultLogLevel._DEBUG:
				getLogger().debug(sql);
				break;
			case DefaultLogLevel._INFO:
				getLogger().info(sql);
				break;
			case DefaultLogLevel._WARN:
				getLogger().warn(sql);
				if(LogSlowQueries!=null)
					getSlowLogger().warn(sql);
				break;
			case DefaultLogLevel._ERROR:
				getLogger().error(sql);
				break;
			default:
				getLogger().info(sql);
			}
		}
		else if(IsShowSql) {
			if(level>DefaultLogLevel._INFO)
				System.err.println(sql);
			else
				System.out.println(sql);
		}
	}

	private static boolean checkExcludesSql(ISQL sql) {
		String s= sql.getSQL().replaceAll("\\r?\\n"," ");
		return EasyRegex.findOr(s,ShowSqlExcludes);
	}
	
	static public Object getPrepredSatementValue(Object value) {
		if(value==null)
			return null;
		else if(value instanceof Date)
			return new Timestamp(((Date)value).getTime());
		else return value;
	}
	
	static public String getStmtSqlValue(Object value) {
    String s=null;
    if(value==null)
      s="null";
    else if(value instanceof String) {
      s=((String)value).replace("'","''").replace("$","\\$"); //Mysql support \', but mssql just support ''.
      s=SQLAssistant.getDialect().adjustStmtSQLValue(s);
      s="'"+(String)s+"'";
    }
    else if(value instanceof Literal) {
      s=value.toString();
    }
    else if(value instanceof Boolean) {
    	if(Dialect.isPostgreSQL())
    		s=((Boolean)value).booleanValue()?"true":"false";
    	else
    		s=((Boolean)value).booleanValue()?"1":"0";
    }
    else if(value instanceof Number) {
      s=((Number)value).toString();
    }
    else if(value.getClass().equals(java.sql.Date.class)) {
      s="'".concat(value.toString()).concat("'");
      if(isOracle())
      	s="to_date("+s+",'YYYY-MM-DD HH24:MI:SS')";
    }
    else if(value.getClass().equals(java.sql.Timestamp.class)) {
      s="'".concat(EasyTime.formatDate((java.sql.Timestamp)value)).concat("'");
      if(isOracle())
      	s="to_date("+s+",'YYYY-MM-DD HH24:MI:SS')";
    }
    else if(value.getClass().equals(java.util.Date.class)) {
      s="'".concat(EasyTime.formatDate((java.util.Date)value,EasyTime.DATETIME_FORMAT).concat("'"));
      if(isOracle())
      	s="to_date("+s+",'YYYY-MM-DD HH24:MI:SS')";
    }
/*    else if(value instanceof Object[]) {
      String[] strarr=EasyStr.toStringArray((Object[])value);
      if(strarr.length>0) {
        int dataType=(((Object[])value)[0] instanceof String)?Where.CHAR:Where.OTHER;
        s=dataType==Where.CHAR?EasySql.addSingleQuotesBySqlIn(EasyStr.ArrToStr(strarr)):EasyStr.ArrToStr(strarr);
        s="(".concat(s)+")";
      }else {
      	s="(null)";
      }
    }*/
    else if(value.getClass().isArray()) {
    	s="("+getArrayValue(value)+")";
    }
    else if(value instanceof Iterable) {
    	s="("+getCollectionValue((Iterable)value)+")";
    }
    else if(value instanceof Function) {
      s=((Function)value).toString();
    }
    else {
      s=value.toString();
    }
    return s;
  }
  
  static String getCollectionValue(Iterable value) {
  	CharAppender result=new CharAppender(',');
  	for(Iterator iter=value.iterator();iter.hasNext();) {
  		result.append(getStmtSqlValue(iter.next()));
  	}
  	return result.toString();
  }
  
  public static Map getJDBCTemplateParams(Map params){
  	for(Iterator iter=params.entrySet().iterator();iter.hasNext();) {
  		Entry entry=(Entry)iter.next();
  		entry.setValue(SQLAssistant.getJDBCTemplateParams(entry.getValue()));
  	}
  	return params;
  }
  
  public static String getJDBCTemplateParams(Object value){
  	if(value==null)
  		return (String)value;
		if(value.getClass().isArray())
			return SQLAssistant.getArrayValue(value);
		else if(value instanceof Collection)
			return SQLAssistant.getCollectionValue((Collection)value);
		else
			return SQLAssistant.getStmtSqlValue(value);
  }
  
  static String getArrayValue(Object value) {
  	CharAppender result=new CharAppender(',');
  	if(value.getClass().isArray()&&!(value instanceof Object[])) {
  		Number[] temp=EasyMath.toNumberArray(value);
  		for(int i=0;i<temp.length;i++)
  			result.append(temp[i].toString());
  	}
  	else {
  		Object[] temp=(Object[])value;
    	for(int i=0;i<temp.length;i++) {
    		result.append(getStmtSqlValue(temp[i]));
    	}
  	}
  	return result.toString();
  }

  private static String[] SQLKeyWords=new String[] {"as","date","select","from","where","count","sum","min","max","group","top","update","insert"}; 
  static String getAbbreviation(String tableName) {
		if (tableName != null && tableName.length() > 0) {
			String beanName = EasyStr.toCamelCase(tableName, true);
			StringBuffer sb=new StringBuffer();
			for (int i = 0, n = beanName.length(); i < n; i++) {
				if(EasyStr.isCapital(beanName.charAt(i)))
					sb.append(beanName.charAt(i));
			}
			String result=sb.toString().toLowerCase();
			if(EasyStr.existOf(SQLKeyWords,result)) {
				result+="_";
			}
			return result;
		} else
			return tableName;
	}
  

	static public String getPrettyHibernateSQL(String sql) {
		String result=sql;
		Pattern p=Pattern.compile("(select )(.*?)( from )(.*)",Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
		Matcher m=p.matcher(sql);
		final HashSet aliasSet=new HashSet();
		try{
			while(m.find()) {
				String fields=(new RegexReplace(m.group(2),"(\\w*)(\\..*?)( as \\w*)(,? ?)"){
					@Override public String getReplacement(Matcher m){
						aliasSet.add(m.group(1));
						return m.group(1)+m.group(2)+m.group(4);
					}
				}).replaceAll();
				result=(m.group(1)+fields+m.group(3)+m.group(4));
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		String newAlias=null;
		HashSet newAliasSet=new HashSet();
		for(Iterator iter=aliasSet.iterator();iter.hasNext();) {
			String alias=(String)iter.next();
			newAlias=SQLAssistant.getAbbreviation(alias);
			if(!newAliasSet.contains(newAlias)) {
				newAliasSet.add(newAlias);
				result=result.replaceAll(alias,newAlias);
			}
			
		}
		return result;
	}

	static public String showSQL(String sql,Object[] params) {
		String result=getPrettyHibernateSQL(sql);
		for(int i=0;result.indexOf('?')>=0;i++) {
			//result=result.replaceFirst("\\?",EasyStr.ReplaceAll(SQLAssistant.getStmtSqlValue(params[i]),"$","\\$",false));
			result=result.replaceFirst("\\?",(params[i] instanceof Map)?"null":SQLAssistant.getStmtSqlValue(params[i]));
		}
		return result;
	}
	
	public static void main(String[] args){
		String sql="select processdef0_.ID_ as ID1_0_, processdef0_.NAME_ as NAME3_0_, processdef0_.DESCRIPTION_ as DESCRIPT4_0_, processdef0_.VERSION_ as VERSION5_0_, processdef0_.ISTERMINATIONIMPLICIT_ as ISTERMIN6_0_, processdef0_.STARTSTATE_ as STARTSTATE7_0_ from JBPM_PROCESSDEFINITION processdef0_ where processdef0_.NAME_=? order by processdef0_.VERSION_ desc limit ?";
		esdk.tool.assertEquals(getPrettyHibernateSQL(sql),"select p.ID_, p.NAME_, p.DESCRIPTION_, p.VERSION_, p.ISTERMINATIONIMPLICIT_, p.STARTSTATE_ from JBPM_PROCESSDEFINITION p where p.NAME_=? order by p.VERSION_ desc limit ?");
	}
	public static boolean isPostgreSQL(){
		return Dialect.isPostgreSQL();
	}
	public static boolean isMySQL(){
		return Dialect.isMySQL();
	}
	public static boolean isOracle(){
		return Dialect.isOracle();
	}

	public static boolean isMsSQL(){
		return Dialect.isMsSQL();
	}

	public static boolean isMSSQL2000(){
		return Dialect.isMsSQL2000();
	}
	
	public static SqlDialect getDialect(){
		return Dialect;
	}
	
	public static String getCharset(){
		if(isMsSQL())
			return "gbk";
		else if(isMySQL())
			return "utf8";
		else if(isPostgreSQL())
			return "utf8";
		else
			return "iso-8859-1";
	}
}
