package com.esdk.sql.orm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.lang.Lang;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;
import com.esdk.sql.Column;
import com.esdk.sql.ILogic;
import com.esdk.sql.IPageSelect;
import com.esdk.sql.ISQL;
import com.esdk.sql.ISelect;
import com.esdk.sql.NullCondition;
import com.esdk.sql.SQLRuntimeException;
import com.esdk.sql.SmartBetween;
import com.esdk.sql.Table;
import com.esdk.utils.EasyReflect;
import com.esdk.utils.EasyStr;
import com.esdk.utils.RedisClient;
/***
 * @author 范飞宇
 * @since 2006.?.? 
 */
abstract public class AbstractSelect<T extends AbstractSelect,K extends ParentRow> extends SelectWrapper<T,K> implements ISelect,IPageSelect{
  private boolean isOutputAllFields=true;
  private boolean isNullable=true; //high priority, notice that null is different with blank for db sql.
  private boolean isAllowBlank=true; //middle priority
  private boolean isCachable=esdk.prop.getBoolean("db_cachable",true);
  abstract public Object getMetaData();
  private String[] fieldNames;
  private Class[] fieldTypes;
  //private Columns columns=new Columns();
  protected LinkedHashMap fieldMap=new LinkedHashMap();
  ORMSession session;
  
  protected AbstractSelect(Table table) {
    super(table);
  }
  
  protected AbstractSelect(String tablename,boolean isTop,Connection conn) {
    super(tablename,conn);
    setTop(isTop?DefaultLimit:0);
  }

  protected AbstractSelect(String tablename,ORMSession ormsession) {
    super(tablename,ormsession.getConnection());
    this.setSession(ormsession);
  }

  protected AbstractSelect(String tablename,boolean isTop,ORMSession ormsession) {
    super(tablename,ormsession.getConnection());
    setTop(isTop?DefaultLimit:0);
    this.setSession(ormsession);
  }

  protected AbstractSelect(Table table,Connection conn) {
    super(table,conn);
  }

  protected AbstractSelect(String tablename,String alias,String joinType) {
    super(new Table(tablename,alias,joinType),null);
  }

  protected AbstractSelect(String tablename,String alias,boolean isJoin) {
    super(new Table(tablename,alias,isJoin),null);
  }

  protected AbstractSelect(String tablename,String joinType) {
    super(new Table(tablename,null,joinType),null);
  }

  protected AbstractSelect(String tablename,boolean isJoin) {
    super(new Table(tablename,isJoin),null);
  }
  
  protected AbstractSelect(String tablename,Connection conn) {
    super(tablename,conn);
  }

  public void setSession(ORMSession value){
    session=value;
  }
  
  public T setPrimaryKey(Long pkid) {
 	 fieldMap.put(getPrimaryKeyFieldName(),pkid);
 	 return (T)this;
 }
 
  public T setPrimaryKey(Long[] pkIds) {
  	super.addIn(getPrimaryKeyFieldName(),pkIds);
  	return (T)this;
 }
  
 @Override 
  public T addIn(String fieldName,Number...values){
  	fieldMap.remove(fieldName); // 清除field=value的条件
		super.removeCondition(fieldName);// 清除所有该字段的查询条件，以解决parse()后无法清除select查询条件的问题
		return super.addIn(fieldName,values);
  }
  
  @Override
  public T addIn(String fieldName,String...value){
  	fieldMap.remove(fieldName); // 清除field=value的条件
		super.removeCondition(fieldName);// 清除所有该字段的查询条件，以解决parse()后无法清除select查询条件的问题
  	return super.addIn(fieldName,value);
  }
  
  @Override
  public T addIn(String fieldName,AbstractSelect select){
  	fieldMap.remove(fieldName); // 清除field=value的条件
		super.removeCondition(fieldName);// 清除所有该字段的查询条件，以解决parse()后无法清除select查询条件的问题
  	return super.addIn(fieldName,select);
  }
  
  @Override
  public T addIn(String fieldName,ISelect value){
  	fieldMap.remove(fieldName); // 清除field=value的条件
		super.removeCondition(fieldName);// 清除所有该字段的查询条件，以解决parse()后无法清除select查询条件的问题
  	return super.addIn(fieldName,value);
  }
  
  public ParentResultSet toParentResultSet() throws SQLException {
  	ParentResultSet parentResultSet=null;
  	if(useCacheSec<=0||!isCachable)
  		parentResultSet=createParentResultSet(toResultSet());
  	else {
  		RedisClient redis=new RedisClient();
  		if(redis.existsObj(this.getSQL())) {
  			parentResultSet=(ParentResultSet)redis.getObj(this.getSQL());
  		}
  		if(parentResultSet==null) {
  			redis.del(redis.keys(this.getSQL()+"*"));//注意一定要有*号结尾
				parentResultSet=createParentResultSet(toResultSet());
				redis.setObj(this.getSQL(),parentResultSet,useCacheSec);
			}
  		redis.close();
  	}
  	parentResultSet.setSelect(this);
  	return parentResultSet;
  }
  
  @Override public ABRowSet<K> toRowSet() throws SQLException{
  	if(session==null){
  		return new ABRowSet(toParentResultSet());
  	}
  	else 
  		return (ABRowSet<K>)session.toRowSet(this);
  }
  
  public List<K> toList(int start,int limit) throws Exception {
    return session==null?toParentResultSet().getSubList(start,limit):session.list(this).subList(start,Math.min(start+limit,session.list(this).size()));
  }
  
  public List<K> toList() throws Exception {
    return list();
  }
  
  public List<K> list() throws Exception {
    return session==null?toParentResultSet().gainAllRow():session.list(this);
  }
  
	public K getFirstRow(){
		return getFirstRow(false);
	}
  
  public K getFirstRow(boolean isCreateInstance){
		K result=null;
		ParentResultSet prs=null;
		try{
			if(session!=null){
				List list=session.list(this);
				result=(K)(list.size()>0?list.get(0):null);
			}
			if(result==null){
				int oldTop=select.getTop();
				if(oldTop<=0)
					select.setTop(1);
				prs=toParentResultSet();
				result=(K)prs.getFirstRow();
				select.setTop(oldTop);
			}
			if(!isCreateInstance)
				return result;
		}catch(Exception e){
			throw new SQLRuntimeException(e);
		}
  	if(isCreateInstance&&result==null) {
  		result=(K)prs.createRowInstance();
			result.setConnection(getConnection());
			RowUtils.fillDefaultVals(result);
			result.load(this);
			return (K)result;
  	}
  	return result;
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

  /**获取第一条记录的第一个字段值，并指定默认值*/
  public <V> V getFirstValue(Class<V> cls) {
  	K row=getFirstRow();
  	if(row==null)
  		return null;
  	else {
  		String field=(String)row.record.keySet().iterator().next();
  		return esdk.obj.valueOf(cls,row.get(field));
  	}
  }
  
  /**获取第一条记录的第一个字段值*/
  public <V> V getFirstValue(Class<V> cls,V defVal) {
  	return esdk.obj.or(getFirstValue(cls),defVal);
  }
  
  protected ParentRow getFirstRowNoSession(){
  	return (ParentRow)getFirstRowNoSession(false);
  }

  protected ParentRow getFirstRowNoSession(boolean isCreateInstance) {
  	ParentRow result=null;
  	int oldTop=getTop();
  	try{
  		setTop(1);
			ParentResultSet prs=this.toParentResultSet();
			setTop(oldTop);
			if(prs.first())
				result=(ParentRow)prs.getCurrent();
			else if(isCreateInstance) {
				result=prs.createRowInstance();
				result.setConnection(getConnection());
				result.load(this);
			}
			prs.close();
			return (ParentRow)result;
		}
		catch(Exception e){
			throw new SQLRuntimeException(e);
		}
  }
  
  public boolean isExistRecord()throws SQLException{
    return count()>0;
  }
  
	protected ParentResultSet createParentResultSet(ResultSet rs){
		try{
			Class resultsetClass=Thread.currentThread().getContextClassLoader().loadClass(this.getClass().getPackage().getName().concat(".").concat(getTableName().concat("ResultSet")));
			java.lang.reflect.Constructor constructor=resultsetClass.getConstructor(new Class[]{ResultSet.class});
			ParentResultSet result=(ParentResultSet)constructor.newInstance(new Object[]{rs});
			return result;
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
	}

  public String getTableName() {
    String clsname=this.getClass().getSimpleName();
    return clsname.substring(0,clsname.indexOf("Select"));
    /*return getTable().getTableName();*/
  }
  
  
  public boolean isOutputAllFields() {
    return isOutputAllFields||select.isOutputAllColumns();
  }
  
  public T setOutputAllFields(boolean value) {
    isOutputAllFields=value;
    return (T)this;
  }

	public AbstractSelect setCamelCaseColumns(String...columnNames){
		Column[] columns=new Column[columnNames.length];
		for(int i=0;i<columnNames.length;i++){
			columns[i]=new Column(this.getTableAliasName(),columnNames[i],null,EasyStr.toCamelCase(columnNames[i]));
		}
		select.setColumns(columns);
		setOutputAllFields(false);
		return this;
	}

	@Override public T addColumns(String... columnnames){
    super.addColumns(columnnames);
    return (T)this;
  }

  public T setColumns(String columnnames) {
    setColumns(EasyStr.split(columnnames));
    return (T)this;
  }
  
  @Override public T setColumns(Column... cols){
  	super.setColumns(cols);
  	setOutputAllFields(false);
  	return (T)this;
  }
  
  @Override public T addColumnWithAlias(String columnname,String alias){
    super.addColumnWithAlias(columnname,alias);
    return (T)this;
  }
  
  @Override public T addColumn(Column column){
    super.addColumn(column);
    return (T)this;
  }

  @Override public T addColumn(ISQL select,String aliasName){
  	super.addColumn(select,aliasName);
    return (T)this;
  }
  
  @Override public T setColumns(String... columnNames){
    super.setColumns(columnNames);
    setOutputAllFields(false);
    return (T)this;
  }
  
  /**
   * 实现多个别名字段的输出：select name as c1,code as c2 ... 
   * @param columnsMap:可输出多个字段的别名
   * */
  public T setColumns(Map<String,String> columnsMap){
  	for(String columnName:columnsMap.keySet()) {
  		String aliasName=esdk.str.or(columnsMap.get(columnName),columnName);
  		super.addColumnWithAlias(columnName,aliasName);
  	}
    setOutputAllFields(false);
    return (T)this;
  }


  @Override public void clear() {
  	super.clear();
  	this.fieldMap.clear();
  }

  /**清除session、redis缓存*/
	public void clearCache(){
    if(session!=null) {
      session.remove(this);
    }
    if(useCacheSec>0||isCachable) {
    	RedisClient redis=new RedisClient();
  		if(redis.existsObj(this.getSQL())) {
  			redis.del(redis.keys(this.getSQL()+"*"));//注意一定要有*号结尾
  		}
  		redis.close();
    }
  }
	
  /**清除order by语句*/
	public void clearOrderBy() {
		this.select.clearOrderBy();
	}
	
	@Override
	public T clearCondition(){
		this.fieldMap.clear();
		this.select.clearCondition();
		return (T)this;
	}
	
  public void parse(){
		if(isOutputAllFields) {
			Column[] cols=this.select.getColumns().toArray();
			this.select.getColumns().clear();
			addColumns("*");
			addColumns(cols);
		}
		for(Iterator iter=fieldMap.keySet().iterator();iter.hasNext();){
			String fieldname=(String)iter.next();
			Object obj=fieldMap.get(fieldname);
			if(obj==null&&isNullable())
				this.addCondition(new NullCondition(this.createField(fieldname)));
			else if(obj instanceof SmartBetween) {
				SmartBetween smartBetweeen=(SmartBetween)obj;
				if(!smartBetweeen.getStmtSql().equals("1=1"))
					this.addCondition(smartBetweeen);
			}
			else if((obj!=null && !"".equals(obj.toString())) || isAllowBlank()){
				if(obj instanceof ILogic)
					this.addCondition((ILogic)obj);
				else	
					this.eq(fieldname,obj);
			}
			else if(isAllowBlank()&&"".equals(obj))
				this.eq(fieldname,obj);
		}
		select.setPrimaryKeyFieldName(getPrimaryKeyFieldName());
	}

  @Override
  public T addLikeValuesOr(String fieldName,String...keywords){
  	fieldMap.remove(fieldName); // 清除field=value的条件
		super.removeCondition(fieldName);// 清除所有该字段的查询条件，以解决parse()后无法清除select查询条件的问题
  	return super.addLikeValuesOr(fieldName,keywords);
  }
  
  @Override
  public T addLikeValuesAnd(String columnName,String...keywords){
  	return super.addLikeValuesAnd(columnName,keywords);
  }
  
  @Override
  public T addLike(String fieldName,String value){
  	return super.addLike(fieldName,value);
  }
  
  public String getPrimaryKeyFieldName() {
  	return (String)EasyReflect.getFieldValue(getMetaData(),"PrimaryKey");
  }

	@Override public T setTableAliasName(String alias){
  	return (T)super.setTableAliasName(alias);
  }

	@Override public T orLike(Object...args){
		return super.orLike(args);
	}
	
	public T setNullable(boolean isNullable){
		this.isNullable = isNullable;
		return (T)this;
	}

	public boolean isNullable(){
		return isNullable;
	}
	
	public T setAllowBlank(boolean isAllowBlank){
		this.isAllowBlank = isAllowBlank;
		return (T)this;
	}

	public boolean isAllowBlank(){
		return isAllowBlank;
	}
  
	/**
	 * 只匹配已存在的字段，忽略null字段
	 * 支持between条件，但必须是Begin|Min|End|Max结尾的Key名称
	 * 日期范围会自动把日期最大值的时分秒改为当天最后一秒，注意前提是时间值为00:00:00才会自动修改。
	 * */
  public T load(Map map) {
  	HashSet<String> skipFieldSet=new HashSet();
  	for(Iterator<Entry> iter=map.entrySet().iterator();iter.hasNext();) {
  		Entry<String,Object> item=iter.next();
  		String field=esdk.str.toUnderlineCase(item.getKey());
  		int index=esdk.array.indexOf(getNames(),field);
  		if(index>=0) {
  			if(item.getValue()==null && !isNullable)
  				continue;
  			else if(item.getValue().toString().length()==0 && !this.isAllowBlank)
  				continue;
  			else if(item.getValue() instanceof String && ((String)item.getValue()).contains("*")) { //把*号变成%号，改为like方式模糊查询。
  				addLikeCondition(field,((String)item.getValue()).replaceAll("\\*","%"));
  			}else {
  				this.fieldMap.put(field,esdk.obj.valueOf(getFieldTypes()[index],item.getValue()));
  			}
  		}
  		else if(item.getKey().matches(".*?(Begin|Min|End|Max)$")) {
  			field=esdk.str.toUnderlineCase(item.getKey().replaceAll("(Begin|Min|End|Max)$",""));
  			if(skipFieldSet.contains(field))
  				continue;
  			index=esdk.array.indexOf(getNames(),field);
  			String minFieldName=item.getKey().endsWith("End")?item.getKey().replaceFirst("End$","Begin"):item.getKey().replaceFirst("Max$","Min");
  			String maxFieldName=item.getKey().endsWith("Begin")?item.getKey().replaceFirst("Begin$","End"):item.getKey().replaceFirst("Min$","Max");
  			Object minValue=esdk.obj.valueOf(getFieldTypes()[index],map.get(minFieldName));
  			Object maxValue=esdk.obj.valueOf(getFieldTypes()[index],map.get(maxFieldName));
  			if((minValue!=null && minValue instanceof Number)||(maxValue !=null && maxValue instanceof Number)) {
  				fieldMap.put(field,new SmartBetween(this.createField(field),(Number)minValue,(Number)maxValue));
  			}
  			else {
  				Date maxDateTime=(Date)maxValue;
  				if(maxValue!=null && esdk.time.formatDate(maxDateTime,"HH:mm:ss").equals("00:00:00"))
  					maxDateTime=esdk.time.getEndDate(maxDateTime);
  				fieldMap.put(field,new SmartBetween(this.createField(field),(Date)minValue,maxDateTime));
  			}
  			skipFieldSet.add(field);
  		}
  	}
  	return (T)this;
  }
  
  public T load(IRow row) {
  	this.load(row.toMap(false));
//  	EasyReflect.copyBeanProperties(obj,this,false,true,esdk.str.distinct(RowFacility.Keywords,"TableAliasName","Session","OutputAllFields","ColumnsAsJavaBean"));
  	return (T)this;
  }
  
  public T load(Object obj) {
  	this.load(Lang.obj2map(obj));
//  	EasyReflect.copyBeanProperties(obj,this,false,true,esdk.str.distinct(RowFacility.Keywords,"TableAliasName","Session","OutputAllFields","ColumnsAsJavaBean"));
  	return (T)this;
  }
  
  public T load(AbstractSelect abstractSelect) {
  	this.fieldMap=(LinkedHashMap)abstractSelect.fieldMap.clone();
  	this.isOutputAllFields=abstractSelect.isOutputAllFields;
  	this.isNullable=abstractSelect.isNullable;
  	this.isAllowBlank=abstractSelect.isAllowBlank;
  	this.select=abstractSelect.select.clone();
  	return (T)this;
  }

	@Override
	/** 会自动把字段等于某个值的条件清空，因为不可能出现又等于又like的情况，这样是毫无意义的 */
	public T addLikeCondition(String fieldName,String value){
		fieldMap.remove(fieldName); // 清除field=value的条件
		super.removeCondition(fieldName);// 清除所有该字段的查询条件，以解决parse()后无法清除select查询条件的问题
		return super.addLikeCondition(fieldName,value);
	}

	@Override
	/** 会自动把字段等于某个值的条件清空 */
	public T addNotLikeCondition(String fieldName,String value){
		fieldMap.remove(fieldName); // 清除field=value的条件
		super.removeCondition(fieldName);// 清除所有该字段的查询条件，以解决parse()后无法清除select查询条件的问题
		return super.addNotLikeCondition(fieldName,value);
	}

	@Override
	public T removeCondition(String fieldName){
		fieldMap.remove(fieldName);
		return super.removeCondition(fieldName);
	}

	@Override
	public T useCache(int sec){
		return super.useCache(sec);
	}

	@Override
	public T useCache(boolean ifCachiable){
		return super.useCache(ifCachiable);
	}

	public boolean hasColumn(String column){
		return EasyReflect.hasPropertyName(this.getMetaData().getClass(),esdk.str.toCamelCase(column,true));
	}
	
	public String[] getNames() {
		if(fieldNames==null)
			fieldNames=(String[])EasyReflect.getDeclaredFieldValue(getMetaData(),"FieldNames");
		return fieldNames;
	}
	
	private Class[] getFieldTypes() {
		if(fieldTypes==null)
			fieldTypes=(Class[])EasyReflect.getDeclaredFieldValue(getMetaData(),"FieldTypes");
		return fieldTypes;
	}
}
