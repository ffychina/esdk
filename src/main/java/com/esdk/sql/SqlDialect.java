package com.esdk.sql;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esdk.esdk;
import com.esdk.sql.datasource.FileConnectionPool;
import com.esdk.utils.EasyMath;
import com.esdk.utils.EasyStr;
import com.esdk.utils.MString;
import com.esdk.utils.EasyRegex;
import com.esdk.utils.RegexReplace;
import com.esdk.utils.EasyObj;

public class SqlDialect{
	private String _sql;
	private static String specificFunctionsRegex="(getAge|getDateDiff)";
	private String _databaseProductName,_databaseMajorVersion;
	public static final String DatabaseProductName_PostgreSQL="PostgreSQL",DatabaseProductName_Oracle="Oracle",
			DatabaseProductName_MsSQL="Microsoft SQL Server",DatabaseProductName_MySQL="MySQL",DatabaseProductName_HSQL="HSQL Database Engine";

	public SqlDialect(){
	}
	
	public SqlDialect(String sql){
		this._sql=sql;
	}

	public SqlDialect(String sql,Connection conn){
		this(sql);
		setDatabaseProductName(conn);
	}
	
	public SqlDialect(String sql,String databaseProductName){
		this(sql);
		setDatabaseProductName(databaseProductName);
	}

	public void setDatabaseProductName(String value){
		_databaseProductName=value;
	}

	
	public void setDatabaseProductName(Connection conn){
		if(conn==null||_databaseProductName!=null)
			return;
		try{
			setDatabaseProductName(conn.getMetaData().getDatabaseProductName());
			setDatabaseMajorVersion(conn.getMetaData().getDatabaseMajorVersion()+"");
		}catch(SQLException e){
			throw new SQLRuntimeException(e);
		}
	}

	private String adjustFunction(String sql){
		if(isMsSQL()){
			String result=EasyRegex.replaceAll(sql,"(dbo\\.)?"+specificFunctionsRegex,"dbo.$2",true);
			result=EasyRegex.replaceAll(result,"ifnull","isnull");
			result=EasyRegex.replaceAll(result,"now\\(\\)","getDate()");
			return result;
		}else if(!isMsSQL()){
			String result=EasyRegex.replaceAll(sql,"(dbo\\.)"+specificFunctionsRegex,"$2",true);
			result=EasyRegex.replaceAll(result,"isnull","ifnull");
			result=EasyRegex.replaceAll(result,"getDate\\(\\)","now()");
			return result;
		}
		return sql;
	}

	private String adjustTop(String sql){
		if(EasyStr.existOf(sql," top ",true)&&!isMsSQL()){
			return top2Limit(sql);
		}else if(EasyStr.existOf(sql," limit ",true)&&isMsSQL()){
			return limit2Top(sql);
		}
		return sql;
	}

	public static String top2Limit(String sql){
		if(!Pattern.compile(" top ",Pattern.CASE_INSENSITIVE).matcher(sql).find()){
			return sql;
		}
		 return new TopToLimitMString(sql).convert();
	}
	public static String limit2Top(String sql){
		if(!Pattern.compile(" limit ",Pattern.CASE_INSENSITIVE).matcher(sql).find()){
			return sql;
		}
		 return new LimitToTopMString(sql).convert();
	
	}


	public String convert(){
		return adjustFunction(adjustTop(_sql));
	}
	static protected class TopToLimitMString extends MString{
		public TopToLimitMString(CharSequence cs){
			super(cs);
		}

		@Override protected CharSequence replace(String regex,String replacement,final CharSequence item,final MString scope){
			try{
				return new RegexReplace(item.toString(),"(select)( +top +)(\\d+)(.*)",Pattern.CASE_INSENSITIVE){
					@Override public String getReplacement(Matcher matcher){
						scope.add(" limit "+matcher.group(3));
						return matcher.group(1)+matcher.group(4);
					}
				}.replaceAll();
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}

		public String convert(){
			return this.replaceAll(null,null).toString();
		}
	}

	public boolean isPostgreSQL(){
		return DatabaseProductName_PostgreSQL.equals(_databaseProductName);
	}

	public boolean isMySQL(){
		return DatabaseProductName_MySQL.equals(_databaseProductName);
	}

	public boolean isMsSQL(Connection conn){
		if(conn==null)
			return false;
		try{
			String dbproductName=conn.getMetaData().getDatabaseProductName();
			return DatabaseProductName_MsSQL.equals(dbproductName)||DatabaseProductName_HSQL.equals(dbproductName);
		}catch(SQLException e){
			return false;
		}
	}

	public boolean isMsSQL(){
		return DatabaseProductName_MsSQL.equals(_databaseProductName)||isHSQL();
	}

	public boolean isMsSQL2000(){
		return (DatabaseProductName_MsSQL.equals(_databaseProductName)&&getDatabaseMajorVersion().equals("8"))||isHSQL();
	}
	
	public boolean isHSQL() {
		return DatabaseProductName_HSQL.equals(_databaseProductName);
	}
	public boolean isOracle(){
		return DatabaseProductName_Oracle.equalsIgnoreCase(_databaseProductName);
	}
	
	static protected class LimitToTopMString extends TopToLimitMString{
		public LimitToTopMString(CharSequence cs){
			super(cs);
		}

		@Override protected CharSequence replace(String regex,String replacement,final CharSequence item,final MString scope){
			try{
				final String str=item.toString();
				Pattern p=Pattern.compile("(select )?(.*?)( +limit +)(\\d+)( +offset +)?(\\d+)?",Pattern.CASE_INSENSITIVE);
				Matcher m=p.matcher(str);
				if(m.find()){
					return new RegexReplace(str,"(select )?(.*?)( +limit +)(\\d+)( +offset +)?(\\d+)?",Pattern.CASE_INSENSITIVE){
						@Override public String getReplacement(Matcher matcher){
							int top=EasyMath.toInt(matcher.group(4))+EasyMath.toInt(matcher.group(6));
							if(str.toLowerCase().startsWith("select "))
								return str.replaceAll("(select)( )","$1$2top "+top+" ").replaceAll("( +limit +)(\\d+)( +offset +)?(\\d+)?","");
							else{
								String select=scope.remove(0).toString();
								scope.add(0,select.replaceAll("(select)( )","$1$2top "+top+" "));
								return EasyStr.concat(matcher.group(1),matcher.group(2));
							}
						}
					}.replaceAll();
				}else {
					p=Pattern.compile("(select )?(.*?)( +limit +)(\\d+)",Pattern.CASE_INSENSITIVE);
					m=p.matcher(str);
					return new RegexReplace(str,"(select )?(.*?)( +limit +)(\\d+)",Pattern.CASE_INSENSITIVE){
						@Override public String getReplacement(Matcher matcher){
							int top=EasyMath.toInt(matcher.group(4));
							if(str.toLowerCase().startsWith("select "))
								return str.replaceAll("(select)( )","$1$2top "+top+" ").replaceAll("( +limit +)(\\d+)","");
							else{
								String select=scope.remove(0).toString();
								scope.add(0,select.replaceAll("(select)( )","$1$2top "+top+" "));
								return EasyStr.concat(matcher.group(1),matcher.group(2));
							}
						}
					}.replaceAll();
				}
				
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	public String adjustStmtSQLValue(String sql){
		if(isMySQL()){
			return sql.replace("\\","\\\\"); 
		}
		return sql;
	}

	public static void main(String[] args) throws Exception{
		Connection msconn=FileConnectionPool.getConnection();
 		esdk.tool.assertEquals(new JDBCTemplate(msconn,"select * from member where member_id in (select member_id from member limit 1) where (member_type='abcd') and valid=1 limit 100 offset  5").getSQL(),"select top 105 * from member where member_id in (select top 1 member_id from member) where (member_type='abcd') and valid=1");
 		esdk.tool.assertEquals(new JDBCTemplate(msconn,"select * from member where member_id in (select member_id from member limit 3 offset 5) where (member_type='abcd') and valid=1  limit 100 offset  5").getSQL(),"select top 105 * from member where member_id in (select top 8 member_id from member) where (member_type='abcd') and valid=1");
	}

	public void setDatabaseMajorVersion(String databaseMajorVersion){
		this._databaseMajorVersion = databaseMajorVersion;
	}

	public String getDatabaseMajorVersion(){
		return _databaseMajorVersion;
	}
  
  protected static StringBuilder join(Serializable... values) {
  	StringBuilder result=new StringBuilder();
  	for(Object item:values) {
  		String part=EasyObj.or(item.toString(),"");
  		if(part.length()>0) {
    		char c=part.charAt(0);
    		if(result.length()>0&&result.charAt(result.length()-1)!=' '&&c!='\r'&&c!='\n'&&c!='('&&c!=')')
    			result.append(" ");
    		result.append(part);
  		}
  	}
  	return result;
  }

	protected StringBuilder pstmtSql;
  protected Object[] parameters;
	
  @SuppressWarnings("unused")
	public StringBuilder getSQL(Columns columns,Froms froms,Wheres wheres,String distinct,int offset
			,int limit,OrderBys orderbys,GroupBys groupbys,Map<String,Serializable> otherArgs){
		return join("SELECT",distinct,columns.assemble(),froms.assemble(),wheres.assemble(),groupbys.assemble(),orderbys.assemble());
	}
	
  @SuppressWarnings("unused")
	public StringBuilder parse(Columns columns,Froms froms,Wheres wheres,String distinct,int offset
			,int limit,OrderBys orderbys,GroupBys groupbys,Map<String,Serializable> otherArgs){
		pstmtSql=join("SELECT",distinct,columns.assemble(),froms.assemble(),wheres.getPstmtSql(),groupbys.assemble(),orderbys.assemble());
		parameters=wheres.getParameters();
		return pstmtSql;
	}
	
	public StringBuilder getPstmtSql() {
		return pstmtSql;
	}
	public Object[] getParameters(){
		return parameters;
	}
	
  public static SqlDialect getDialect() {
  	if(SQLAssistant.isMSSQL2000())
  		return new Mssql2000Dialect();
  	if(SQLAssistant.isMySQL()||SQLAssistant.isPostgreSQL())
  		return new MysqlDalect();
  	if(SQLAssistant.isOracle())
  		return new OracleDialect();
  	else
  		return new SqlDialect();
  }
}
