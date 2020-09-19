 package com.esdk.sql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.esdk.esdk;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.ABRow;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.sql.orm.AbstractSelect;
import com.esdk.sql.orm.TableResultSet;
import com.esdk.utils.Constant;
import com.esdk.utils.EasyArray;
import com.esdk.utils.EasySql;
import com.esdk.utils.EasyStr;
import com.esdk.utils.RedisUtils;
import com.esdk.utils.TimeMeter;

/***
 * @author 范飞宇
 * @since 2005.?.? 
 */
public class Select implements ISelect,IPageSelect,ITableSelect{
  protected Columns columns;
  protected Froms froms;
  protected Wheres wheres;
  protected OrderBys orderbys;
  protected GroupBys groupbys;
  private int offset=-1,limit=-1;
	private Connection _conn;
  private StringBuilder querySQL;
  private boolean isDistinct;
  private boolean isFormatSql=true;
  
  private ResultSet _resultset;
  private Object[] parameters;
	private String primaryKeyFieldName;
	private boolean _showSql=SQLAssistant.IsShowSql;
	protected int useCacheSec=Constant.ResetUseCacheSec; 
	private boolean isCachable=esdk.prop.getBoolean("db_cachable",false);

  public Select() {
    froms=new Froms();
    columns=new Columns();
    wheres=new Wheres();
    orderbys=new OrderBys();
    groupbys=new GroupBys();
    querySQL=new StringBuilder();
  }
  
  public Select(Connection conn) {
  	this();
    setConnection(conn);
  }

  public Select(Table t,Connection conn) {
    this(conn);
    setTable(t);
  }

  public Select(Connection conn,boolean isTop) {
  	this(conn);
  	if(isTop)
  		setTop(SQLAssistant.DefaultSelectLimit);
  }
  
  public Select(String tablename){
  	this();
    froms.addTable(tablename);
  }

  public Select(String tablename,Connection conn){
    this(conn);
    froms.addTable(tablename);
  }

  public void setTableName(String tablename){
    froms.addTable(tablename);
  } 

  public void setTableName(String tablename,String alias){
    froms.clear();
    froms.addTable(tablename,alias);
  }

  public void setTable(Table table){
    froms.clear();
    froms.addTable(table);
  }
  
  public void setTableName(String tablename,String alias,String jointype){
    froms.clear();
    froms.addTable(tablename,alias,jointype);
  }

  public Table getTable(){
    return froms.toArray()[0];
  }

  public void clear(){
    columns.clear();
    froms.clear();
    wheres.clear();
    orderbys.clear();
    groupbys.clear();
    querySQL.delete(0,querySQL.length());
  }

  public boolean hasCondtion() {
    return wheres.size()>0;
  }
  
  public void parse(){
  	SqlDialect dialect=SqlDialect.getDialect();
  	HashMap otherArgs=new HashMap(1);
  	if(getPrimaryKeyFieldName()!=null) {
  		otherArgs.put("primaryKeyFieldName",getPrimaryKeyFieldName());
  	}
  	querySQL=dialect.parse(columns,froms,wheres,getDistinct(),offset,limit,orderbys,groupbys,otherArgs);
  	parameters=dialect.getParameters();
  }

  public Select setPrimaryKeyFieldName(String fieldName) {
  	this.primaryKeyFieldName=fieldName;
  	return this;
  }
  private String getPrimaryKeyFieldName(){
		return this.primaryKeyFieldName;
	}
  
	public String getSQL(){
  	SqlDialect dialect=SqlDialect.getDialect();
  	HashMap otherArgs=new HashMap(1);
  	if(getPrimaryKeyFieldName()!=null) {
  		otherArgs.put("primaryKeyFieldName",getPrimaryKeyFieldName());
  	}
  	StringBuilder stmtSql;
  	stmtSql=dialect.getSQL(columns,froms,wheres,getDistinct(),offset,limit,orderbys,groupbys,otherArgs);
		return isFormatSql?formatSQL(stmtSql.toString()):stmtSql.toString();
	}
  
  public Select setTop(int number){
    return setRowsLimit(number);
  }

	@Override public Select setRowsOffset(int startByZero){
		return setOffset(startByZero);
	}
	
	public Select setOffset(int offset){
		this.offset=offset;
		return this;
	}

	public Select setRowsLimit(int limit){
		this.limit=limit;
		return this;
	}

  public int getTop(){
    return this.limit;
  }

  public Connection getConnection(){
    return this._conn;
  }

  public void setConnection(Connection connection){
    this._conn=connection;
    SQLAssistant.setDatabaseProductName(this._conn);
  }

  private String getDistinct(){
    return isDistinct?"DISTINCT":"";
  }

  public void addEqualColumn(String fieldName,String anotherFieldname){
    this.wheres.addEqualColumn(this.createField(fieldName),this.createField(anotherFieldname));
  }

  public void addNotEqualColumn(String fieldName,String anotherFieldname){
    this.wheres.addNotEqualColumn(this.createField(fieldName),this.createField(anotherFieldname));
  }

  public void addEqualNull(String fieldName){
    this.wheres.addCondition(LogicFactory.nullCondition(this.createField(fieldName)));
  }
  
  public void addNotEqualNull(String fieldName){
    this.wheres.addCondition(LogicFactory.notNullCondition(this.createField(fieldName)));
  }
  
  public void addCondition(String value) {
    wheres.addCondition(value);
  }
  
  public void addCondition(ILogic value) {
    wheres.addCondition(value);
  }
  
  public void addCondition(ILogic[] value) {
    wheres.addCondition(value);
  }
  
  public void addEqualCondition(String fieldname,Object value){
    wheres.addEqualCondition(this.createField(fieldname),value);
  }
  
  public void addEqualCondition(String fieldName,boolean value){
    wheres.addEqualCondition(this.createField(fieldName),Boolean.valueOf(value));
  }

	public void eq(String fieldName,String value){
		wheres.addEqualCondition(this.createField(fieldName),value);
	}
	
	public void eq(String fieldName,Boolean value){
		wheres.addEqualCondition(this.createField(fieldName),value);
	}
	
	public void eq(String fieldName,Number value){
		wheres.addEqualCondition(this.createField(fieldName),value);
	}
	
	public void eq(String fieldName,Object value){
		wheres.addEqualCondition(this.createField(fieldName),value);
	}	
	
	/**大于*/
	public void gt(String fieldName,Object value){
		wheres.addCondition(this.createField(fieldName),Where.MORE,value);
	}
	
	/**大于等于*/
	public void gteq(String fieldName,Object value){
		wheres.addCondition(this.createField(fieldName),Where.MOREEQUAL,value);
	}
	
	/**小于*/
	public void lt(String fieldName,Object value){
		wheres.addCondition(this.createField(fieldName),Where.LESS,value);
	}
	
	/**小于等于*/
	public void lteq(String fieldName,Object value){
		wheres.addCondition(this.createField(fieldName),Where.LESSEQAL,value);
	}
	
	public void notEq(String fieldName,String value){
		wheres.addNotEqualCondition(this.createField(fieldName),value);
	}
	
	public void notEq(String fieldName,Object value){
		wheres.addNotEqualCondition(this.createField(fieldName),value);
	}

	public void notEq(String fieldName,Number value){
		wheres.addNotEqualCondition(this.createField(fieldName),value);
	}

	public void notEq(String fieldName,Boolean value){
		wheres.addNotEqualCondition(this.createField(fieldName),value);
	}


  public Select addNotBetween(String fieldName,Number minvalue,Number maxvalue){
    wheres.addCondition(new Between(this.createField(fieldName),minvalue,maxvalue,true));
    return this;
  }
  
  public Select addBetween(String fieldName,Number minvalue,Number maxvalue){
    wheres.addCondition(new Between(this.createField(fieldName),minvalue,maxvalue));
    return this;
  }
  
  public Select addNotBetween(String fieldName,Date minvalue,Date maxvalue){
    wheres.addCondition(new Between(this.createField(fieldName),minvalue,maxvalue,true));
    return this;
  }
  
  public Select addBetween(String fieldName,Date minvalue,Date maxvalue){
    wheres.addCondition(new Between(this.createField(fieldName),minvalue,maxvalue));
    return this;
  }
  
  public void addEqualCondition(String fieldName,String value){
    wheres.addEqualCondition(this.createField(fieldName),value);
  }
  
  public void addEqualCondition(String fieldName,Number value){
    wheres.addEqualCondition(this.createField(fieldName),value);
  }
  
  public void addEqualCondition(String fieldName,Date value){
    wheres.addEqualCondition(this.createField(fieldName),value);
  }
  
  public void addEqualCondition(String fieldName,java.sql.Date value){
    wheres.addEqualCondition(this.createField(fieldName),value);
  }
  
  public void addEqualCondition(String fieldName,Boolean value){
    wheres.addEqualCondition(this.createField(fieldName),value);
  }
  
  public void addCondition(String fieldName,String expression,Object value){
    wheres.addCondition(this.createField(fieldName),expression,value);
  }

  public void addLikeCondition(String fieldName,String value){
    this.wheres.addLikeCondition(this.createField(fieldName),value);
  }
  
	/**支持多个or条件的条件表达式,注意输入时参数顺序和类型不要弄错*/
	public Select orExpress(String expression,Object...args){
		ILogic[] conditions=new ILogic[args.length/2];
		for(int i=0,n=conditions.length;i<n;i++)
			conditions[i]=WhereFactory.create(createField((String)args[i*2]),expression,args[i*2+1]);
		this.addCondition(LogicFactory.or(conditions));
		return this;
	}
	 
	/**支持多个or条件的条件表达式,自动判断参数是内容或表达式*/
	public Select or(Object...args){
		String fieldName=null,expression=Expression.EQ;
		Object rightValue=null;
		boolean isFieldValue=true;
		ArrayList<ILogic> conditionList=new ArrayList<ILogic>(args.length/2);
		for(Object arg:args) {
			if(isFieldValue) {
				fieldName=(String)arg;
				isFieldValue=false;
			}else if(EasyArray.contains(Expression.All,arg)) {
				expression=(String)arg;
			}else {
				rightValue=arg;
				if(rightValue.getClass().isArray())
					expression=Expression.IN;
				if(rightValue instanceof ISelect) {
					ISelect select=(ISelect)rightValue;
					if(rightValue instanceof AbstractSelect) {
						AbstractSelect as=(AbstractSelect)rightValue;
						if(as.getColumns().size()==0 || as.isOutputAllFields()) 
							as.setColumns(as.hasColumn(fieldName)?fieldName:as.getPrimaryKeyFieldName());
					}
					conditionList.add(LogicFactory.in(createField(fieldName),(ISelect)select));
				}
				else	
					conditionList.add(WhereFactory.create(createField(fieldName),expression,rightValue));
				isFieldValue=true;
				expression=Expression.EQ;
			}
		}
		this.addCondition(LogicFactory.or(esdk.array.toArray(conditionList)));
		return this;
}
		
  /**多个字段的or条件的模糊查询，例如:where name like abc% or code like abc% */
  public void addLikeOrConditions(String keyword,String... fieldNames){
		/*String keyword=fieldNames[fieldNames.length-1];
		String[] fieldNames=esdk.str.remove(fieldNames,keyword);*/
  	Field[] fields=new Field[fieldNames.length];
  	for(int i=0;i<fieldNames.length;i++) {
  		fields[i]=this.createField(fieldNames[i]);
  	}
    this.wheres.addCondition(LogicFactory.likeOr(keyword,fields));
  }
  
  public void addNotLikeCondition(String fieldName,String value){
    this.wheres.addNotLikeCondition(this.createField(fieldName),value);
  }
  
  public void removeCondition(String fieldName){
    wheres.removeCondition(this.createField(fieldName));
  }

  public void clearCondition(){
    wheres.clearCondition();
  }
  
  public void clearOn(){
    getTable().onCondition.clear();
  }
  
  public void addNotEqualCondition(String fieldName,Object value){
    wheres.addNotEqualCondition(this.createField(fieldName),value);
  }
  
  public void addNotEqualCondition(String fieldName,String value){
    wheres.addNotEqualCondition(this.createField(fieldName),value);
  }
  
  public void addNotEqualCondition(String fieldName,Boolean value){
    wheres.addNotEqualCondition(this.createField(fieldName),value);
  }
  
  public void addNotEqualCondition(String fieldName,Number value){
    wheres.addNotEqualCondition(this.createField(fieldName),value);
  }
  
  public void addEqualNumeric(String fieldName,String value){
    wheres.addEqualNumeric(this.createField(fieldName),value);
  }

  public void addNotEqualNumeric(String fieldName,String value){
    wheres.addNotEqualNumeric(this.createField(fieldName),value);
  }

  public void addInNumeric(String fieldName,String[] value){
    wheres.addInNumeric(this.createField(fieldName),value);
  }
  
  public void addInCondition(String fieldName,Object[] value){
    if(value instanceof String[])
      wheres.addInCondition(this.createField(fieldName),(String[])value);
    else if(value instanceof Number[])
      wheres.addInCondition(this.createField(fieldName),(Number[])value);
    else
      throw new RuntimeException("不能识别的对象类型:"+value.getClass().getName());
  }

  public void addInCondition(String fieldName,Number... value){
    wheres.addInCondition(this.createField(fieldName),value);
  }

  public void addInCondition(String fieldName,String... value){
    wheres.addInCondition(this.createField(fieldName),value);
  }
  
  public void addInCondition(String fieldName,ISelect select){
    wheres.addInCondition(this.createField(fieldName),select);
  }
  
  public void addNotInCondition(String fieldName,ISelect select){
    wheres.addNotInCondition(this.createField(fieldName),select);
  }
  
  public void addNotInCondition(String fieldName,String... value){
    wheres.addNotInCondition(this.createField(fieldName),value);
  }
  
  public void addNotInCondition(String fieldName,Number... value){
    wheres.addNotInCondition(this.createField(fieldName),value);
  }
  
  public void addEqualEmplyValue(String fieldName){
    wheres.addEqualEmplyValue(this.createField(fieldName));
  }

  public void addEqualTrue(String fieldName){
    wheres.addEqualTrue(this.createField(fieldName));
  }

  public void addEqualEmplyNumeric(String fieldName){
    wheres.addEqualEmplyNumeric(this.createField(fieldName));
  }

  public void addNotEqualEmplyNumeric(String fieldName){
    wheres.addNotEqualEmplyNumeric(this.createField(fieldName));
  }

  public void addNotEqualEmplyValue(String fieldName){
    wheres.addNotEqualEmplyValue(this.createField(fieldName));
  }
  
  public boolean isDistinct(){
    return isDistinct;
  }

  public void setDistinct(boolean isdistinct){
    this.isDistinct=isdistinct;
  }

  public void addIsNullColumn(String columnname,Object replaceValue){
    ISNULLColumn isnull=new ISNULLColumn(createField(columnname),replaceValue);
    columns.addColumn(isnull);
  }
  
  public void addIsNullColumn(String columnname,Object replaceValue,String aliasname){
    ISNULLColumn isnull=new ISNULLColumn(createField(columnname),replaceValue,aliasname);
    columns.addColumn(isnull);
  }
  
  public void addColumn(Column column) {
    columns.addColumn(column);
  }
  
  public void addColumn(ISQL select,String aliasName) {
    columns.addColumn(new SelectColumn(select,aliasName));
  }
  
  public void addColumn(String columnname){
    columns.addColumn(getTable(),columnname);
  }

  public void addColumnWithAlias(String columnname,String aliasname){
    columns.addColumn(getTable(),columnname,null,aliasname);
  }

  public void addAllColumns(){
    columns.addColumn(getTable(),"*");
  }

  public void setAllColumns(){
    columns.clear();
    columns.addColumn(getTable(),"*");
  }

  public void setColumns(String... columnNames){
    columns.clear();
    for(int i=0;i<columnNames.length;i++){
      columns.addColumn(getTable(),columnNames[i]);
    }
  }

  public boolean isOutputAllColumns() {
  	return columns.columnSet.containsValue("*");
  }
  
  public Columns getColumns() {
  	return this.columns;
  }
  
  public void setColumns(String columnnames){
    setColumns(columnnames.split(","));
  }

  public Select setColumnsAsJavaBean(String...columnNames){
		Column[] columns=new Column[columnNames.length];
		for(int i=0;i<columnNames.length;i++){
			columns[i]=new Column(this.getTable().createField(columnNames[i]),null,EasyStr.toCamelCase(columnNames[i]));
		}
		setColumns(columns);
		return this;
	}

  public Select addColumnsAsJavaBean(String...columnNames){
		Column[] columns=new Column[columnNames.length];
		for(int i=0;i<columnNames.length;i++){
			columns[i]=new Column(this.getTable().createField(columnNames[i]),null,EasyStr.toCamelCase(columnNames[i]));
		}
		addColumns(columns);
		return this;
	}

  public void addColumns(Column... cols){
  	columns.addColumns(cols);
  }
  
  public void setColumns(Column... cols){
  	columns.clear();
  	columns.addColumns(cols);
  }
  
  public void addColumns(String... columnnames){
    Column[] columnArray=new Column[columnnames.length];
    for(int i=0;i<columnnames.length;i++){
      columnArray[i]=new Column(this.createField(columnnames[i]));
    }
    columns.addColumns(columnArray);
  }
  
  public void addColumn(String columnname,String functionname){
    columns.addColumn(getTable(),columnname,functionname);
  }

  public void addColumn(String columnname,String functionname,String aliasname) {
    columns.addColumn(getTable(),columnname,functionname,aliasname);
  }

  public void addOrderBy(String... fieldname){
    for(int i=0;i<fieldname.length;i++){
      addOrderBy(fieldname[i]);
    }
  }
  
  public void addOrderBy(String fieldname){
    this.orderbys.addOrderBy(this.createField(fieldname));
  }
  
  public void setOrderBy(String... fieldname){
  	clearOrderBy();
    for(int i=0;i<fieldname.length;i++){
      addOrderBy(fieldname[i]);
    }
  }

  public void addOrderBy(String fieldname,boolean isDesc){
    this.orderbys.addOrderBy(this.createField(fieldname),isDesc);
  }

  /**清除order by语句*/
  public void clearOrderBy(){
  	this.orderbys.clear();
  }
  
  public void setOrderBy(String fieldname,boolean isDesc){
  	clearOrderBy();
    this.orderbys.addOrderBy(this.createField(fieldname),isDesc);
  }
  public void addGroupBy(String fieldname){
    groupbys.add(this.createField(fieldname));
  }

  public void setGroupBy(String... fieldnames){
    groupbys.clear();
    for(int i=0;i<fieldnames.length;i++){
    	addGroupBy(fieldnames[i]);
    }
  }

  public void setHaving(String having) {
  	groupbys.setHaving(having);
  }
  
  public String getTableAliasName() {
    return getTable().getAliasName();
  }
  
  public Select setTableAliasName(String alias) {
    getTable().setAliasName(alias);
    return this;
  }
  
  public Field createField(String fieldname) {
    return getTable().createField(fieldname);
  }
  
  public Column createColumn(String fieldname,String aliasName) {
    return new Column(getTable().createField(fieldname),null,aliasName);
  }
  
  public Column createColumn(String columnName) {
    return new Column(getTable().createField(columnName));
  }
  
  public void addOnCondition(Field leftfield,Field rightfield){
    getTable().addOnCondition(new OnCondition(leftfield,rightfield));
  }
  
  public void addOnCondition(Field field,String value){
    getTable().addOnCondition(new Where(field,Where.EQUAL,value));
  }
  
  public void addOnCondition(Field field,Object value){
    getTable().addOnCondition(new Where(field,Where.EQUAL,value));
  }
  
  public void addOnCondition(ILogic condition){
    getTable().addOnCondition(condition);
  }

  public boolean executeQuery() throws SQLException{
    String sql=getSQL();
    try{
      Statement stmt=_conn.createStatement();
      _resultset=stmt.executeQuery(sql);
      return _resultset!=null;
    }
    catch(SQLException e){
      throw new SQLException(e.toString()+"SQL语句："+sql);
    }
  }

  public boolean perform() throws SQLException{
    if(_conn==null)
      throw new SQLException("Connection is null,please setConnectcion first");
    parse();
    try{
      PreparedStatement pstmt=_conn.prepareStatement(querySQL.toString());
      for(int i=0;i<parameters.length;i++){
        pstmt.setObject(i+1,parameters[i]);
      }
      if(_showSql) {
        TimeMeter tm=TimeMeter.newInstanceOf();
        _resultset=pstmt.executeQuery();
        SQLAssistant.printSql(this,tm);
      }
      else {
        _resultset=pstmt.executeQuery();
      }
      /*System.out.println(pstmt.isClosed());//will occur AbstractMethodError
      _conn.close();
      pstmt.close();
      System.out.println(pstmt.isPoolable());*/
      return _resultset!=null;
    }
    catch(SQLException e){
    	System.err.println(LocalDateTime.now()+"打印异常错误："+esdk.tool.getExceptionStackTrace(e));
      throw new SQLException(e.toString()+"，SQL语句:"+getSQL());
    }
  }
  
  public boolean isExistRecord() throws SQLException{
    perform();
    boolean result=_resultset.next();
    EasySql.close(_resultset);
    return result;
  }
  
  public ABRow getFirstRow() throws SQLException{
    Map map=getFirstRecord();
    if(map==null)
      return null;
    else 
      return new ABRow(map);
  }

  /**获取第一条记录的第一个字段值*/
  public Integer getFirstInteger() {
  	return getFirstValue(Integer.class);
  }
  
  /**获取第一条记录的第一个字段值，并指定默认值*/
  public Integer getFirstInteger(Integer defVal) {
  	return getFirstValue(Integer.class,defVal);
  }

  /**获取第一条记录的第一个字段值*/
  public String getFirstString() {
  	return getFirstValue(String.class);
  }
  
  /**获取第一条记录的第一个字段值，并指定默认值*/
  public Double getFirstDouble(Double defVal) {
  	return getFirstValue(Double.class,defVal);
  }

  /**获取第一条记录的第一个字段值*/
  public Double getFirstDouble() {
  	return getFirstValue(Double.class);
  }
  
  /**获取第一条记录的第一个字段值，并指定默认值*/
  public String getFirstLong(String defVal) {
  	return getFirstValue(String.class,defVal);
  }

  /**获取第一条记录的第一个字段值*/
  public Long getFirstLong() {
  	return getFirstValue(Long.class);
  }
  
  /**获取第一条记录的第一个字段值，并指定默认值*/
  public Long getFirstLong(Long defVal) {
  	return getFirstValue(Long.class,defVal);
  }

  /**获取第一条记录的第一个字段值，并指定默认值
   * @throws SQLException */
  public <V> V getFirstValue(Class<V> cls) {
  	ABRow row;
		try{
			row=getFirstRow();
			if(row==null)
	  		return null;
	  	else {
	  		String field=(String)row.record().keySet().iterator().next();
	  		return esdk.obj.valueOf(cls,row.get(field));
	  	}
		}catch(SQLException e){
			throw esdk.tool.wrapThrowble(e);
			
		}
  }
  
  /**获取第一条记录的第一个字段值*/
  public <V> V getFirstValue(Class<V> cls,V defVal) {
  	return esdk.obj.or(getFirstValue(cls),defVal);
  }

  private Map getFirstRecord() throws SQLException{
    perform();
    ABResultSet arset=new ABResultSet(_resultset);
    Map result=null;
    if(arset.next()){
      result=arset.getFirstRow().record();
    }
    arset.close();
    return result;
  }
  
  public ResultSet toResultSet() throws SQLException{
    perform();
    return _resultset;
  }

  public ABResultSet toABResultSet() throws SQLException{
    if(useCacheSec<=0||!isCachable)
  		return new ABResultSet(toResultSet());
  	else {
  		if(RedisUtils.existsObj(this.getSQL()))
  			return (ABResultSet)RedisUtils.getObj(this.getSQL());
  		else {
  			ABResultSet result=new ABResultSet(toResultSet());
  			RedisUtils.setObj(this.getSQL(),result,useCacheSec);
  			return result;
  		}
  	}
  }

  public List toList(IRowMappper mapper) throws SQLException{
  	perform();
  	List result=new ArrayList();
  	for(int i=0;_resultset.next();i++) {
  		result.add(mapper.mapRow(_resultset, i));
  	}
  	EasySql.close(_resultset);
  	return result;
  }
  
  public List toList(IRowMappper mapper,int start,int limit) throws SQLException{
  	perform();
  	List result=new ArrayList();
  	limit=limit<0?Integer.MAX_VALUE:limit;
  	for(int i=0,n=start+limit;i<n&&_resultset.next();i++) {
  		if(i>=start)
  			result.add(mapper.mapRow(_resultset, i));
  	}
  	EasySql.close(_resultset);
  	return result;
  }
  
  public List toList(final Class pojoClass,int start,int limit) throws SQLException{
  	return toList(new ReflectRowMapper(pojoClass),start,limit);
  }
  
  public List toList(final Class pojoClass) throws SQLException{
  	return toList(pojoClass,0,-1);
  }
  
  public TableResultSet toTableResultSet() throws SQLException{
    return new TableResultSet(this);
  }

  public ABRowSet toRowSet() throws SQLException {
  	return new ABRowSet(toABResultSet());
	}
  
  public String[][] toArray()throws SQLException{
  	return toArray(true);
  }
  
  public String[][] toArray(boolean isOutputColumn) throws SQLException{
    ResultSet rs=toResultSet();
    String [][] result=isOutputColumn?EasySql.resultSetToArrWithHeader(rs):EasySql.ResultSetToArr(rs);
    EasySql.close(rs);
    return result;
  }
  
  public JSONArray toJsonArray() throws SQLException{
  	return toJsonArray(true);
  }

  public JSONArray toJsonArray(boolean isFormatJavaBeanName) throws SQLException{
		return toABResultSet().toJsonArray(isFormatJavaBeanName);
	}

  public void finalize() {
    this.columns.clear();
    this.columns=null;
    this.froms.clear();
    this.froms=null;
    this.groupbys.clear();
    this.groupbys=null;
    this.orderbys.clear();
    this.orderbys=null;
    this.wheres.clear();
    this.wheres=null;
  }

  public String getTableName(){
    return getTable().getTableName();
  }
  
  @Override public String toString(){
    return getSQL();
  }

  @Override public int count() throws SQLException {
  	int result;
  	int top=getTop();
  	setTop(0);
  	if(this.groupbys.size()==0&&this.getDistinct().trim().length()==0) {
	  	Columns cols=this.columns;
	  	this.columns=new Columns();
	  	OrderBys orders=this.orderbys;
	  	this.orderbys=new OrderBys();
	  	addColumns(new Column("","*",Column.COUNT,"TOTAL"));
	  	ABResultSet abrs=toABResultSet();
	  	result=abrs.absolute(0)?abrs.getFirstRow().getInteger("TOTAL"):0;
	  	abrs.close();
	  	this.columns=cols;
	  	this.orderbys=orders;
  	}
  	else {
  		result=new WrapSelect(this,"a").useCache(useCacheSec).count();
  	}
  	setTop(top);
  	return result;
  }
  
	public Select setJoinType(String join){
		Table table=froms.getFirst();
		table.setRelationShip(join);
		if(EasyStr.isBlank(table.getAliasName()))
			table.createAliasName();
		return this;
	}
	
  public int queryForInt() throws SQLException{
  	ResultSet rs=toResultSet();
  	int result=0;
  	if(rs.next())
  		result=rs.getInt(1);
  	else
  		result=0;
  	EasySql.close(rs);
  	return result;
  }

  public double queryForDouble() throws SQLException{
  	ResultSet rs=toResultSet();
  	double result=0;
  	if(rs.next())
  		result=rs.getDouble(1);
  	else
  		result=0;
  	EasySql.close(rs);
  	return result;
  }
  
  public Object queryForObject() throws SQLException{
  	ResultSet rs=toResultSet();
  	Object result=null;
  	if(rs.next())
  		result=rs.getDouble(1);
  	EasySql.close(rs);
  	return result;
  }
  	
  public void setFormatSQL(boolean isformatsql) {
  	this.isFormatSql=isformatsql;
  }
  
  public String formatSQL(String sql) {
  	return sql.replaceAll("\\r?\\n"," ");
  }

	public Select showSql(boolean b){
		_showSql=b;
		return this;
	}
	
	public Select useCache(int sec) {
		this.useCacheSec=sec;
		return this;
	}
	
	public Select clone() {
		Select result=new Select();
		result._conn=this._conn;
	  result.columns=this.columns.clone();
    result.froms=this.froms.clone();
    result.wheres=this.wheres.clone();
    result.orderbys=this.orderbys.clone();
    result.groupbys=this.groupbys.clone();
    return result;
	}

	
}
