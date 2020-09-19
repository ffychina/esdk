package com.esdk.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esdk.esdk;
import com.esdk.sql.JDBCTemplate;
import com.esdk.sql.SQLAssistant;
import com.esdk.sql.SQLRuntimeException;
import com.esdk.sql.orm.SnowFlake;

public class EasySql{
  
  public static List<List> toLists(ResultSet rs) throws SQLException{
    ResultSetMetaData rsmd=rs.getMetaData();
    int FieldsCount=rsmd.getColumnCount();
    LinkedList result=new LinkedList();
    while(rs.next()){
      ArrayList subList=new ArrayList(FieldsCount);
      for(int i=0;i<FieldsCount;i++){
        subList.add(rs.getObject((i+1)));
      }
      result.add(subList);
    }
    rs.close();
    return result;
  }
  
  public static List<Object> toList(ResultSet rs,String columnName) throws SQLException{
    LinkedList result=new LinkedList();
    while(rs.next()){
      result.add(rs.getObject(columnName));
    }
    rs.close();
    return result;
  }
  
  public static List<Object[]> toList(ResultSet rs,String... columns) throws SQLException{
    LinkedList result=new LinkedList();
    while(rs.next()){
    	Object[] row=new Object[columns.length];
    	for(int i=0;i<columns.length;i++){
    		row[i]=rs.getObject(columns[i]);
			}
      result.add(row);
    }
    rs.close();
    return result;
  }
  
  public static String[][] ResultSetToArr(ResultSet rs) throws SQLException{
    ResultSetMetaData rsmd=rs.getMetaData();
    int FieldsCount=rsmd.getColumnCount();
    LinkedList list=new LinkedList();
    while(rs.next()){
      String[] arr=new String[FieldsCount];
      for(int i=0;i<arr.length;i++){
        arr[i]=rs.getString(i+1);// //下标从1开始,解决中文乱码问题
      }
      list.add(arr);
    }
    if(list.size()==0){
      return new String[0][0];
    }
    String[][] result=new String[list.size()][FieldsCount];
    for(int i=0;i<list.size();i++){
      System.arraycopy(list.get(i),0,result[i],0,FieldsCount);
    }
    list.clear();
    list=null;
    return result;
  }
  
  public static String[][] resultSetToArrWithHeader(ResultSet rs) throws SQLException {
    ResultSetMetaData rsmd=rs.getMetaData();
    int FieldsCount=rsmd.getColumnCount();
    LinkedList list=new LinkedList();
    String[] arr=new String[FieldsCount];
    for(int i=0;i<FieldsCount;i++){
      arr[i]=rsmd.getColumnLabel(i+1);
    }
    list.add(arr);
    while(rs.next()){
      arr=new String[FieldsCount];
      for(int i=0;i<arr.length;i++){
        arr[i]=rs.getString(i+1);
      }
      list.add(arr);
    }
    if(list.size()==0){
      return new String[0][0];
    }
    String[][] result=new String[list.size()][FieldsCount];
    for(int i=0;i<list.size();i++){
      System.arraycopy(list.get(i),0,result[i],0,FieldsCount);
    }
    list.clear();
    list=null;
    return result;
  }
  
  public static String[][] resultSetToArrWithHeader(ResultSet rs,String...columns) throws SQLException {
  	if(columns.length==0)
  		return resultSetToArrWithHeader(rs);
    LinkedList list=new LinkedList();
    list.add(columns);
    while(rs.next()){
      String[] record=new String[columns.length];
      for(int i=0;i<columns.length;i++){
        record[i]=rs.getString(columns[i]);
      }
      list.add(record);
    }
    return EasyStr.listToArr(list);
  }
  
	public static String[] getFieldsNames(Statement stmt,String tablename) throws SQLException{
    String[] result=null;
    int FieldsCount=-1;
//<获取结果集中的列名>
    ResultSet rs=null;
    rs=stmt.executeQuery("select top 1 * from "+tablename);
    ResultSetMetaData rsmd=rs.getMetaData();
    FieldsCount=rsmd.getColumnCount();
    result=new String[FieldsCount];
    for(int i=1;i<FieldsCount+1;i++){ //column indices start from 1
      result[i-1]=rsmd.getColumnLabel(i);
//      tableName=rsmd.getTableName(i); //get the name of the column's table name,but i can't find any avild datas
    }
//</获取结果集中的列名>
    return result;
  }

  public static String[] getFieldNames(ResultSet rs)throws SQLException{
    ResultSetMetaData rsmd=rs.getMetaData();
    int FieldsCount=rsmd.getColumnCount();
    String[] result=new String[FieldsCount];
    for(int i=0;i<FieldsCount;i++){
      result[i]=rsmd.getColumnLabel(i+1);
    }
    return result;
  }

  public static int getFieldsCount(Statement stmt,String sql) throws SQLException{ //得到字段数量
    int result=-1,FieldsCount=-1;
    ResultSet rs=null;
    rs=stmt.executeQuery(sql);
    ResultSetMetaData rsmd=rs.getMetaData();
    FieldsCount=rsmd.getColumnCount();
    result=FieldsCount;
    return result;
  }

  public static BigDecimal getTableID(Connection connection){
    boolean flag=true;
    String id=null;
    do{
      try{
        id=esdk.tool.occurID();
        PreparedStatement st=connection.prepareStatement("insert Register (RegisterID) values("+id.toString()+")");
        st.execute();
        flag=false;
      }
      catch(SQLException sql){
        if(sql.getErrorCode()!=2627)
          flag=false;
      }
    }
    while(flag);
    return new BigDecimal(id);
  }

	public static void close(ResultSet rs){
		try{
			if(rs!=null){
				Statement stmt=rs.getStatement();
				rs.close();
				if(stmt!=null)
					stmt.close();
			}
		}catch(SQLException e){
			System.err.println("Occur error when closing ResuletSet, please check the reason:"+esdk.tool.getExceptionStackTrace(e));
		}
	}
	
	public static boolean IsClosed(Connection conn){
		try{
			boolean result=conn.isClosed();
			if(!result)
				conn.setAutoCommit(conn.getAutoCommit());
			return result;
		}catch(SQLException e){
//			e.printStackTrace();
			EasySql.close(conn);
			System.err.println("the invalid connection has already been closed");
			return true;
		}
	}

  public static void close(Connection conn) {
  	try{
			if(conn!=null&&!conn.isClosed())
				conn.close();
		}
		catch(SQLException e){
			throw new SQLRuntimeException(e);
		}
  }

  public static void rollback(java.sql.Connection conn){
    try{
    	if(!conn.isClosed()) {
    		conn.rollback();
    	}
    }
    catch(Exception e){
      throw new SQLRuntimeException(e);
    }
  }

  public static String addSingleQuotesBySqlIn(String value){
    return  "'"+value.replaceAll(",","','")+"'";
  }
  
  public static int getColumnType(String tableName,String columnName,Connection conn) throws SQLException {
    tableName=tableName.toUpperCase();//hqlsdb需要表名大写,否则找不到记录
    ResultSet rs=conn.getMetaData().getColumns(null,null,tableName,columnName);
    int result=0;
    if(rs.next())
      result=rs.getInt("DATA_TYPE");
    return result;
  }
  public static String[] findUniqueColumnNames(String tableName,Connection conn) throws SQLException{
    tableName=tableName.toUpperCase();//hqlsdb需要表名大写,否则找不到记录
    LinkedList result=new LinkedList();
    ResultSet indexRs=conn.getMetaData().getIndexInfo(null,null,tableName,true,false);
/*    try{
      String[][] a=ffy.ffysql.ResultSetToArrWithColumn(indexRs);
      System.out.println(ffystr.ArrToCsv(a,","));
    }
    catch(Exception e){
      e.printStackTrace();
    }*/
    
    while(indexRs.next()){
      String columnName=indexRs.getString("COLUMN_NAME");
      if(columnName!=null){
        if(indexRs.getInt("Type")==DatabaseMetaData.tableIndexClustered){//TYPE=1 is PK, TYPE=3 is INDEX
          int datatype=getColumnType(tableName,columnName,conn);
          if(datatype==java.sql.Types.VARCHAR){
            result.add(columnName);
            break;
          }
        }
        else if(indexRs.getInt("TYPE")==DatabaseMetaData.tableIndexOther){
          boolean isPKColumn=columnName.equalsIgnoreCase(tableName.concat("ID"));
          if(!isPKColumn) //hsqldb的type都是3,不能以type判断,如果是主键值字段,不应该加入到结果中
            result.add(columnName);
        }
      }
    }
    indexRs.close();
    return (String[])result.toArray(new String[0]);
  }
  
  public static String findPKColumn(String tableName,Connection conn) throws SQLException{
   // tableName=tableName.toUpperCase();// hqlsdb需要表名大写,否则找不到记录
    ResultSet pkRs=conn.getMetaData().getPrimaryKeys(null,null,tableName);
    String columnName=null;
    while(pkRs.next()){//未考虑多主键值的情况,请注意,以后解决
      columnName=pkRs.getString("COLUMN_NAME");
      break;
    }
    pkRs.close();
    return columnName;
  }
  
  private static final SnowFlake snowFlake=SnowFlake.getInstance();
  /**使用雪花算法得到全局唯一*/
  public static long genNextPrimaryId() {
  	return snowFlake.nextId();
  }

  public static String getCreateTableScript(String tableName,DatabaseMetaData dbmd) throws Exception {
    ResultSet columnRs=dbmd.getColumns(null,"%",tableName,"%");
    ResultSet primaryRs=dbmd.getPrimaryKeys(null,null,tableName);
    ResultSet indexRs=dbmd.getIndexInfo(null,null,tableName,true,false);
    StringBuffer result=new StringBuffer();
    result.append("Create Table ").append(tableName).append("(");
/*    String csv=ffystr.ArrToCsv(ffysql.ResultSetToArrWithColumn(columnRs),",");
    String csv1=ffystr.ArrToCsv(ffysql.ResultSetToArrWithColumn(primaryRs),",");
    String csv2=ffystr.ArrToCsv(ffysql.ResultSetToArrWithColumn(indexRs),",");
    new TString(new String[] {csv,csv1,csv2}).save(new File("c:/dddb.txt"));*/
    HashSet primaryKey=new HashSet();
    while(primaryRs.next()) {
      primaryKey.add(primaryRs.getString("COLUMN_NAME"));
    }
    HashMap map=new HashMap();
    while(indexRs.next()) {
      if(!indexRs.getBoolean("NON_UNIQUE")&&indexRs.getInt("TYPE")==3){
        String indexname=indexRs.getString("INDEX_NAME");
        if(map.get(indexname)==null){
          map.put(indexname,new CharAppender(','));
        }
        ((CharAppender)map.get(indexname)).append(indexRs.getString("COLUMN_NAME"));
      }
    }
    CharAppender uniqueIndexConstraints=new CharAppender(',');
    for(Iterator iter=map.keySet().iterator();iter.hasNext();){
      String indexname=(String)iter.next();
      uniqueIndexConstraints.append("constraint "+indexname+" unique("+map.get(indexname).toString()+")");
    }
    CharAppender ca=new CharAppender(',');
    for(;columnRs.next();){
      String columnType=columnRs.getString("TYPE_NAME");
      int columnsize=columnRs.getInt("COLUMN_SIZE");
      if(columnsize>100)
        columnsize=18;
      if(columnType.toLowerCase().startsWith("numeric"))
        columnType+="("+columnsize+","+columnRs.getString("DECIMAL_DIGITS")+")";
      else if(columnType.toLowerCase().indexOf("char")>=0)      
        columnType+="("+columnsize+")";
      String primaryFlag=(primaryKey.contains(columnRs.getString("COLUMN_NAME"))?" primary key":"");
      String nullableFlag=columnRs.getBoolean("NULLABLE")?"":" not null";
      String defaultvalue=EasyStr.isBlank(columnRs.getString("COLUMN_DEF"))?"":" default "+columnRs.getString("COLUMN_DEF");
      ca.append(columnRs.getString("COLUMN_NAME")+" "+columnType+nullableFlag+defaultvalue+primaryFlag);
    }
    result.append(ca);
    if(uniqueIndexConstraints.length()>0)
      result.append(",").append(uniqueIndexConstraints);
    result.append(")");
    return result.toString();
  }

  public static String[] getTablesAndViews(Connection conn) throws SQLException {
  	return getTablesAndViews(conn.getMetaData());
  }

  public static String[] getTablesAndViews(Connection conn,String... key) throws SQLException {
  	return getTablesAndViews(conn.getMetaData(),key);
  }
  
  public static String[] getTablesAndViews(DatabaseMetaData meta) throws SQLException{
  	return getTablesAndViews(meta,new String[]{"TABLE","VIEW"});
	}
  
  public static String[] getTablesAndViews(DatabaseMetaData meta,String... key) throws SQLException{
		ArrayList tableList=new ArrayList();
		String catelog=meta.getConnection().getCatalog();
		ResultSet rs=meta.getTables(catelog,"%","%",key);
		while(rs.next()){
			String tablename=rs.getString("TABLE_NAME");
			tableList.add(tablename);
		}
		return (String[])tableList.toArray(new String[0]);
	}
  
  /**
   * 如果出现BIN$开头的表,请执行:purge recyclebin;
   * */
  public static String[] getOracleTablesAndViews(Connection conn,String category,String schema) throws SQLException{
		ArrayList tableList=new ArrayList();
		ResultSet rs=conn.getMetaData().getTables(category,schema,"%",new String[]{"TABLE","VIEW"});
		while(rs.next()){
			String tablename=rs.getString("TABLE_NAME");
			tableList.add(tablename);
		}
		return (String[])tableList.toArray(new String[0]);
	}
	
	public static Timestamp getOracleTimestamp(Object value){
		try{
			Class clz=value.getClass();
			Method m=clz.getMethod("timestampValue",(Class[])null);
			// m = clz.getMethod("timeValue", null); 时间类型
			// m = clz.getMethod("dateValue", null); 日期类型
			return (Timestamp)m.invoke(value,(Object[])null);
		}catch(Exception e){
			return null;
		}
	}

	public static String[] getMssql2008TablesAndViews(Connection conn) throws Exception{
  	JDBCTemplate jt = new JDBCTemplate(conn,"SELECT name FROM sysobjects WHERE xtype='U'  or xtype = 'V'");
  	return jt.toABResultSet().getStrings("name");
  }
  
	public static String format(String sql,Map<String,Object> params){
		HashMap newParams=new HashMap(params.size());
		for(Iterator iter=params.keySet().iterator();iter.hasNext();){
			String key=(String)iter.next();
			newParams.put(key,SQLAssistant.getJDBCTemplateParams(params.get(key)));
		}
		return EasyStr.format(sql,newParams);
	}
	
	public static String format(String sql,Object... params){
		String[] newParams=new String[params.length];
		for(int i=0;i<params.length;i++){
			newParams[i]=SQLAssistant.getJDBCTemplateParams(params[i]);
		}
		return EasyStr.format(sql,newParams);
	}
	
	/** 如果有*号换成%，否则前后加%符号，并且把空格换成% */
	public static String wildcard(String keyword){
		String result=keyword.trim();
		if(keyword.contains("*"))
			result=result.replace("*","%");
		else
			result=("%"+result.replaceAll(" ","%")+"%").replaceAll("%%","%");
		return result.trim();
	}
	
	public static String[] wildcard(String[] keywords){
		for(int i=0;i<keywords.length;i++) {
			keywords[i]=wildcard(keywords[i]);
		}
		return keywords;
	}

	private static void test() throws ParseException{
		esdk.tool.assertEquals(EasySql.format("select * from member where member_id={0} and code={1} and valid={2} or create_time={3} or name in ({4})"
				,12,"M0002",true,EasyTime.valueOf("2010-10-23"),new Object[]{"张三","李四"}),
			"select * from member where member_id=12 and code='M0002' and valid=1 or create_time='2010-10-23 00:00:00' or name in ('张三','李四')"	);
		HashMap params=new HashMap();
		params.put("memberId",12);
		params.put("code","M0002");
		params.put("valid",true);
		params.put("createTime",EasyTime.valueOf("2010-10-23"));
		params.put("names",new Object[]{"张三","李四"});
		esdk.tool.assertEquals(EasySql.format("select * from member where member_id=${memberId} and code=${code} and valid=${valid} or create_time=${createTime} or name in (${names})",params)
				,"select * from member where member_id=12 and code='M0002' and valid=1 or create_time='2010-10-23 00:00:00' or name in ('张三','李四')"	);
	}
	
	public static void main(String[] args) throws Exception{
		test();
		//testOld();
	}

}
