
package com.esdk.sql.orm;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.castor.Castors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.esdk.esdk;
import com.esdk.sql.Delete;
import com.esdk.sql.Function;
import com.esdk.sql.IConnectionable;
import com.esdk.sql.ISQL;
import com.esdk.sql.ISelect;
import com.esdk.sql.Insert;
import com.esdk.sql.SQLAssistant;
import com.esdk.sql.SQLRuntimeException;
import com.esdk.sql.Select;
import com.esdk.sql.Update;
import com.esdk.utils.CharAppender;
import com.esdk.utils.Constant;
import com.esdk.utils.EasyCsv;
import com.esdk.utils.EasyObj;
import com.esdk.utils.EasyReflect;
import com.esdk.utils.EasyStr;
import com.esdk.utils.RedisUtils;
import com.esdk.utils.Response;
import com.esdk.utils.TString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;
import io.swagger.annotations.ApiModelProperty;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
abstract public class ParentRow<T extends ParentRow> implements IRow,Cloneable,IConnectionable,ITable,IChangeable{
	private transient static final short INSERT=0,UPDATE=1,DELETE=2,SAVE=3;
	protected boolean isChanged;
	protected HashMap<String,Object> record=new LinkedHashMap();
	private transient HashMap<String,Object> changedValuesMap,originalMap,newVaulesMap;
	protected transient Connection conn;
	private transient boolean isInited; 

	protected transient ISQL sqlOperator;
	protected boolean isExistRecord; //是否数据库已存在该记录
	private transient short operatorFlag=SAVE;
	protected transient ORMSession session;
	protected transient boolean isAutoIncrement;
	protected transient boolean isCheckDirty,isCheckVersion;
	protected String[] columns;
	private boolean isForceUpdate=false;
	public transient static String VersionFieldName="version";
	private transient int useCacheSec;
	private boolean isCachable=esdk.prop.getBoolean("db_cachable",true);
	private boolean allowSetNullValue=true; //默认允许设置字段值为null，为false时，null值字段无法update

	/*
	 * private short uniqueOperatorFlag=-1; private short insertFlag=1,updateFlag=2,deleteFlag=3;
	 */
	@ApiModelProperty(hidden=true)
	public void setConnection(Connection con){
		conn=con;
	}
	
	public void initMaps() {
		if(!isInited) {
			if(changedValuesMap==null)
			 changedValuesMap=new LinkedHashMap();
			if(originalMap==null)
			 originalMap=new HashMap(2);
			if(newVaulesMap==null)
			 newVaulesMap=new LinkedHashMap();
		}
	}

	@ApiModelProperty(hidden=true)
	public void setSession(ORMSession value){
		session=value;
		conn=value.getConnection();
	}

	abstract public T newSelf();

	@ApiModelProperty(hidden=true)
	public void setPrimaryKey(Object value){
		set(getPrimaryKeyName(),value);
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	public Object getPrimaryKey(){
		return get(getPrimaryKeyName());
	}

	@ApiModelProperty(hidden=true)
	@JsonIgnore
	public void setPKID(Long value){
		set(getPrimaryKeyName(),value);
	}

	@ApiModelProperty(hidden=true)
	@JsonIgnore
	public Long getPKID(){
		return Castors.me().castTo(get(getPrimaryKeyName()),Long.class);
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	abstract public Object getMetaData();

	public HashMap record(){
		return record;
	}

	protected void setNewValue(String key,Object newvalue){
		if(allowSetNullValue||newvalue!=null){
			initMaps();
			newVaulesMap.put(key,newvalue);
			if(!isSameValue(key,newvalue)){
				isChanged=true;
				Object oldvalue=record.put(key,newvalue);
				addOriginalMap(key,oldvalue);
				changedValuesMap.put(key,newvalue);
			}
		}
	}

	private void addOriginalMap(String key,Object oldValue){
		if(!originalMap.containsKey(key))
			originalMap.put(key,oldValue);
	}

	public Object get(String key){
		return EasyStr.get(record,key);
	}

	/**不会根据字段类型自动做格式的转换，因此处理效率比较高*/
	public void set(int pos,String value){
		set(getNames()[pos],value);
	}

	/**会根据表的字段类型自动转换，如果是自定义字段则不做类型转换*/
	public void put(String key,Object newvalue){
		try{
			Object obj=valueOf(key,newvalue);
			setNewValue(key,obj);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**支持基础数据类型、JSON、ResultSet对象*/
	private Object valueOf(String columnname,Object value) throws Exception{
		if(value instanceof JSON)
			return value;
		if(value instanceof IResultSet)
			return ((IResultSet)value).toJsonArray(true,true);
		Class columnClass=getColumnClass(columnname);
		if(columnClass!=null&&columnClass.equals(Boolean.class))
			return esdk.obj.toBoolean(value);
		else if(columnClass!=null&&!columnClass.equals(String.class)&&(value!=null&&value.toString().length()==0))
			return value;
		else
			return EasyObj.valueOf(columnClass,value);
	}

	private Class getColumnClass(String columnname){
		ColumnMetaData obj=RowUtils.findColumnMetaData(this.getClass(),columnname);
		if(obj==null)
			return null;
		return obj.ColumnClass;
	}

	private boolean isSameValue(String key,Object newvalue){
		Object oldvalue=record==null?null:get(key);
		if(newvalue==oldvalue)
			return true;
		if(newvalue==null||oldvalue==null)
			return false;
		if(newvalue.getClass().equals(oldvalue.getClass()))
			return ((Comparable)newvalue).compareTo(oldvalue)==0;
		else {
			if(newvalue instanceof Number && oldvalue instanceof Number)
				return ((Number)newvalue).equals(oldvalue);
			else if(newvalue instanceof Date && oldvalue instanceof Date)
				return ((Date)newvalue).equals(oldvalue);
			else
				return newvalue.equals(oldvalue);
		}
	}

	private void updateBuffer(){
		if((isExistRecord||isForceUpdate)&&isChanged){
			sqlOperator=new Update(getTableName(),conn);
			Update sqler=(Update)sqlOperator;
			sqler.eq(getPrimaryKeyName(),getPrimaryKey());
			for(Iterator iterator=changedValuesMap.keySet().iterator();iterator.hasNext();){
				String key=(String)iterator.next();
				if(!(isForceUpdate&&isAutoIncrement&&key.equals(getPrimaryKeyName())))
					sqler.addFieldValue(key,changedValuesMap.get(key));
			}
		}
	}

	private void insertBuffer(){
		if(isChanged){
			sqlOperator=new Insert(getTableName(),conn);
			Insert insert=(Insert)sqlOperator;
			insert.setAutoIncrement(isAutoIncrement);
			if(SQLAssistant.isPostgreSQL())
				insert.setPrimaryKeyName(getPrimaryKeyName());
			if(isAutoIncrement&&SQLAssistant.isOracle()){ //eg. SEND_LOG_SEQ_ID
				insert.addFieldValue(getPrimaryKeyName(),new Function("SEQ_"+getTableName()+".NEXTVAL"));
				insert.setPrimaryKeyName(getPrimaryKeyName());
			}
			for(int i=0,n=this.getNames().length;i<n;i++){
				String key=this.getNames()[i];
				if(record.get(key)!=null)
					insert.addFieldValue(key,record.get(key));
			}
			if(!isAutoIncrement&&this.getPrimaryKey()==null){
				this.setPrimaryKey(RowUtils.genNextPrimaryId());
				insert.addFieldValue(this.getPrimaryKeyName(),this.getPrimaryKey());
			}
			if(session!=null)
				session.put(getSelect(),this);
		}
	}

	private void saveBuffer(){
		if(isExistRecord||isForceUpdate)
			updateBuffer();
		else
			insertBuffer();
	}

	public void insert(){
		operatorFlag=INSERT;
	}

	public void save(boolean ifForceUpdate){
		if(ifForceUpdate){
			if(getPrimaryKey()==null)
				insert();
			else if(!isExistRecord())
				update(ifForceUpdate);
		}else
			save();
	}

	public void save(){
		operatorFlag=SAVE;
	}

	public void update(){
		operatorFlag=UPDATE;
	}

	public void update(boolean isForce){
		operatorFlag=UPDATE;
		isForceUpdate=isForce;
	}

	public void delete(){
		operatorFlag=DELETE;
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	private ISelect getSelect(){
		Select sqler=new Select(getTableName(),conn);
		sqler.setTop(0);
		sqler.addEqualCondition(getPrimaryKeyName(),getPrimaryKey());
		sqler.addAllColumns();
		return sqler;
	}

	public T refresh() throws SQLException{
		ISelect sqler=getSelect();
		if(session!=null){
			T row=(T)session.getFirstRow(sqler);
			if(row==null)
				refreshRecord(sqler);
			else
				this.record=row.record;
		}else
			refreshRecord(sqler);
		return (T)this;
	}

	private void refreshRecord(ISelect sqler) throws SQLException{
		if(useCacheSec<=0||!isCachable){
			sqler.setConnection(conn);
			ResultSet rset=sqler.toResultSet();
			clear();
			isExistRecord=rset.next();
			initColumns(rset);
			if(isExistRecord){
				loadFromResultSet(rset);
			}
			//	    rset.getStatement().close(); 要注释掉，因为有可能会引起SQLException：Operation not allowed after ResultSet closed
			rset.close();
			isChanged=false;
			if(session!=null){
				session.put(sqler,this);
			}
		}else{
			String key=sqler.getSQL();
			if(!RedisUtils.existsObj(key))
				RedisUtils.setObj(key,((Select)sqler).getFirstRow(),useCacheSec);
			this.load((IRow)RedisUtils.getObj(key));
		}
	}

	void initColumns(ResultSet rset) throws SQLException{
		esdk.tool.read(rset);
	}

	protected void loadFromResultSet(ResultSet rset) throws SQLException{
		String fieldname=null;
		try{
			ResultSetMetaData rsmd=rset.getMetaData();
			for(int i=1,n=rsmd.getColumnCount()+i;i<n;i++){
				record.put(rsmd.getColumnLabel(i),RowUtils.getCurrentValue(rset,rsmd,i));
			}
		}catch(SQLException e){
			throw new SQLException("Field["+fieldname+"] Error: "+e.toString());
		}
	}

	public void clearDirty(){
		initMaps();
		changedValuesMap.clear();
		newVaulesMap.clear();
		isChanged=false;
	}

	private void clear(){
		Object pkid=getPrimaryKey();
		record.clear();
		record.put(getPrimaryKeyName(),pkid);
		initMaps();
		changedValuesMap.clear();
		originalMap.clear();
	}

	public T refresh(Object pk,Connection con) throws SQLException{
		setConnection(con);
		refresh(pk);
		return (T)this;
	}

	public void refresh(Object pk) throws SQLException{
		if(pk==null)
			return;
		record.put(getPrimaryKeyName(),pk);
		refresh();
	}

	public T refresh(Object pk,ORMSession session0) throws SQLException{
		setSession(session0);
		setPrimaryKey((Serializable)pk);
		return refresh();
	}

	private void deleteBuffer(){
		if(getPrimaryKey()!=null){
			sqlOperator=new Delete(getTableName(),conn);
			Delete sqler=(Delete)sqlOperator;
			sqler.eq(getPrimaryKeyName(),getPrimaryKey());
			isChanged=true;// isChange or isAllowCommit?
		}
	}

	@ApiModelProperty(hidden=true)
	public boolean isChanged(){
		return isChanged;
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	public Map<String,Object> getChanged(){
		return changedValuesMap;
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	public Map<String,Object> getNewValues(){
		return this.newVaulesMap;
	}

	public Object findOriginalValue(String fieldName){
		initMaps();
		return originalMap.get(fieldName);
	}

	public boolean checkUnique(String...uniqueFields) throws Exception{
		if(uniqueFields.length>0){
			Select s=new Select(this.getTableName(),conn);
			s.setTop(0);
			for(int i=0;i<uniqueFields.length;i++){
				s.addEqualCondition(uniqueFields[i],this.get(uniqueFields[i]));
			}
			if(getPrimaryKey()!=null)
				s.notEq(getPrimaryKeyName(),getPrimaryKey());
			return s.count()==0;
		}
		return true;
	}

	public boolean checkUniqueRecord() throws Exception{
		Field field=EasyReflect.findField(getMetaData().getClass(),"UniqueIndexFields",false);
		String[] uniqueIndexFields=(String[])field.get(getMetaData());
		return checkUnique(uniqueIndexFields);
	}

	public T saveUniqueRecord() throws Exception{
		Field field=EasyReflect.findField(getMetaData().getClass(),"UniqueIndexFields",false);
		String[] uniqueIndexFields=(String[])field.get(getMetaData());
		if(uniqueIndexFields.length>0){
			Select s=new Select(this.getTableName(),conn);
			s.setTop(0);
			for(int i=0;i<uniqueIndexFields.length;i++){
				s.addEqualCondition(uniqueIndexFields[i],this.get(uniqueIndexFields[i]));
			}
			if(getPrimaryKey()!=null)
				s.addNotEqualCondition(getPrimaryKeyName(),getPrimaryKey());
			IRow dbRow=s.getFirstRow();
			if(dbRow!=null){
				ParentRow originalParentRow=((ParentRow)this.clone());
				originalParentRow.clear();
				originalParentRow.record=(HashMap)this.record;
				this.record=(HashMap)dbRow.record();
				this.clearDirty();
				this.load(originalParentRow);
				this.isExistRecord=true;
			}
		}
		return (T)this;
	}

	public Response validate(){
		return RowUtils.validate(this);
	}

	public boolean checkDirty() throws SQLException{
		boolean result=false;
		initMaps();
		if(isCheckDirty&&isChanged()&&(sqlOperator instanceof Update)){
			Select s=new Select(getTableName(),this.conn);
			s.addEqualCondition(getPrimaryKeyName(),getPrimaryKey());
			String[] changedColumns=getChangedColumns();
			s.setColumns(changedColumns);
			for(int i=0;i<changedColumns.length;i++){
				s.addEqualCondition(changedColumns[i],originalMap.get(changedColumns[i]));
			}
			result=!s.isExistRecord();
			s.clear();
			if(result)
				throw new SQLDirtyException(
						"table:"+getTableName()+" pkid:"+getPrimaryKey()+" can not perform update because columns["+EasyStr.arrToStr(getChangedColumns(),'|')+"] is dirty");
		}
		return result;
	}

	public boolean performWithCheckVersion() throws SQLException{
		boolean result=false;
		String version=VersionFieldName;
		if(isCheckVersion&&isChanged()&&hasColumnName(version)&&(sqlOperator instanceof Update)){
			Update update=(Update)sqlOperator;
			int ver=(Integer)get(version);
			update.addEqualCondition(version,ver);
			update.addFieldValue(version,ver+1);
			update.perform();
			result=update.getUpdatedCount()==1;
			if(!result)
				throw new SQLDirtyException(
						"table:"+getTableName()+" pkid:"+getPrimaryKey()+" can not perform update because columns["+EasyStr.arrToStr(getChangedColumns(),'|')+"] is dirty");
		}else{
			result=sqlOperator.perform();
		}
		return result;
	}

	public boolean performWithCheckDirty() throws SQLException{
		initMaps();
		boolean result=false;
		if(isCheckDirty&&isChanged()&&(sqlOperator instanceof Update)){
			String[] changedColumns=getChangedColumns();
			Update update=(Update)sqlOperator;
			for(int i=0;i<changedColumns.length;i++){
				update.addEqualCondition(changedColumns[i],originalMap.get(changedColumns[i]));
			}
			update.perform();
			result=update.getUpdatedCount()==1;
			if(!result)
				throw new SQLDirtyException(
						"table:"+getTableName()+" pkid:"+getPrimaryKey()+" can not perform update because columns["+EasyStr.arrToStr(getChangedColumns(),'|')+"] is dirty");
		}else{
			result=sqlOperator.perform();
		}
		return result;
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	private String[] getChangedColumns(){
		CharAppender result=new CharAppender(',');
		for(Iterator iter=getChanged().keySet().iterator();iter.hasNext();){
			String column=(String)iter.next();
			if(hasColumnName(column)){
				result.append(column);
			}
		}
		return EasyStr.split(result.toString());
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	public void setCheckDirty(boolean value){
		isCheckDirty=value;
		if(isCheckDirty)
			isCheckVersion=false;
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	public void setCheckVersion(boolean value){
		isCheckVersion=value;
		if(isCheckVersion)
			isCheckDirty=false;
	}

	public boolean flush() throws SQLException{
		boolean result=false;
		result=preCommit();// defaul value is true,return true when not changed and not any
		if(result){
			if(isChanged&&sqlOperator!=null){
				if(sqlOperator instanceof Update){
					if(isCheckDirty)
						/* checkDirty(); */
						result=performWithCheckDirty();
					else if(isCheckVersion)
						result=performWithCheckVersion();
					else
						result=sqlOperator.perform()&&((Update)sqlOperator).getUpdatedCount()==1;
				}else
					result=sqlOperator.perform();
			}
			if(result){
				switch(operatorFlag){
				case INSERT:
				case SAVE:
					isExistRecord=true;
					if(isAutoIncrement&&sqlOperator instanceof Insert){
						Object generatedKey=((Insert)sqlOperator).getGeneratedKey();
						setPrimaryKey(generatedKey);
					}
					break;
				case DELETE:
					isExistRecord=false;
				default:
					break;
				}
				isChanged=false;
				changedValuesMap.clear();
				originalMap.clear();
				newVaulesMap.clear();
			}
		}
		return result;
	}

	public String showSQL(){
		if(sqlOperator==null)
			pretreat();
		return sqlOperator.getSQL();
	}

	public boolean preCommit(){
		if(conn==null)
			throw new SQLRuntimeException("please invoke setConnection() before commit()");
		pretreat();
		return true;
	}

	private void pretreat(){
		initMaps();
		switch(operatorFlag){
		case INSERT:
			insertBuffer();
			break;
		case UPDATE:
			updateBuffer();
			break;
		case DELETE:
			deleteBuffer();
			break;
		case SAVE:
			saveBuffer();
			break;
		default:
			saveBuffer();
		}
	}

	public void testinsert(){
		changedValuesMap=record;
		isChanged=true;
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	ISQL getISQL(){// specialize to Transaction
		pretreat();
		return sqlOperator;
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	public boolean isSaved(){
		return !isChanged;
	}

	public Object clone(){
		ParentRow result=newSelf();
		result.isChanged=this.isChanged;
		result.record=(HashMap)this.record.clone();
		result.changedValuesMap=(HashMap)this.changedValuesMap.clone();
		result.conn=this.conn;
		result.sqlOperator=this.sqlOperator;
		return result;
	}

	@ApiModelProperty(hidden=true)
	public boolean isExistRecord(){
		return this.isExistRecord;
	}

	/**手工修改是否存在记录标记，避免再去查数据库，保持isExistRecord的业务一致性*/
	public T markDeleted(){
		this.isExistRecord=false;
		return (T)this;
	}

	public List toList(){
		return RowUtils.toList(this);
	}

	public List toList(String...labels){
		return RowUtils.toList(this,labels);
	}

	@Override
	public String toXml(){
		return XML.toXml(JSONUtil.parseObj(toMap(true)),Constant.RowXmlIdentifier);
	}

	@Override
	public String toXml(String...labels){
		return XML.toXml(JSONUtil.parseObj(toMap(true,labels)),Constant.RowXmlIdentifier);
	}

	public String toCsv(){
		TString result=new TString();
		result.append(new CharAppender(',').add(getNames()));
		CharAppender ca=new CharAppender(',');
		for(int j=0;j<getNames().length;j++){
			ca.append(EasyCsv.csvEncode(esdk.str.getText(getString(getNames()[j]))));
		}
		result.appendLine(ca);
		return result.toString();
	}

	@Override
	public String toCsv(String...labels){
		TString result=new TString();
		result.append(new CharAppender(',').add(labels));
		CharAppender ca=new CharAppender(',');
		for(int j=0;j<labels.length;j++){
			ca.append(EasyCsv.csvEncode(esdk.str.getText(getString(labels[j]))));
		}
		result.appendLine(ca);
		return result.toString();
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	public String[] getNames(){
		if(columns==null){
			String classname=this.getClass().getName();
			classname=classname.substring(0,classname.length()-3).concat("MetaData");
			try{
				columns=(String[])Class.forName(classname).getField("FieldNames").get(this);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		return columns;
	}

	public void load(Object[] csv) throws Exception{
		int i=0;
		for(Iterator iter=record.keySet().iterator();iter.hasNext();){
			String key=(String)iter.next();
			put(key,csv[i++]);
		}
	}

	/**加载时可过滤通用字段*/
	public T load(ParentRow row,boolean excludeCommonFields){
		return load(row,null,excludeCommonFields?Constant.SystemFields:null);
	}

	/**加载时可指定白名单和黑名单*/
	public T load(IRow row,String[] includesFields,String[] excludeFields){
		if(row==null||row==this)
			return (T)this;
		String key=null;
		for(Iterator iter=Arrays.asList(getNames()).iterator();iter.hasNext();){
			key=(String)iter.next();
			if(row.hasColumnName(key)
					&& (esdk.array.isBlank(includesFields)||esdk.array.contains(includesFields,key)) 
					&& (esdk.array.isBlank(excludeFields)||!esdk.array.contains(excludeFields,key)))
				put(key,row.get(key));
		}
		return (T)this;
	}

	public T load(IRow row){
		if(row==null||row==this)
			return (T)this;
		String key=null;
		try{
			for(Iterator iter=Arrays.asList(getNames()).iterator();iter.hasNext();){
				key=(String)iter.next();
				if(row.hasColumnName(key))
					put(key,row.get(key));
			}
			return (T)this;
		}catch(Exception e){
			System.err.println("字段【"+key+"】载入错误...");
			throw new RuntimeException(e);
		}
	}

	public T load(IRow row,Map<String,String> fieldsMapping){
		return load(row,false,fieldsMapping);
	}

	public T load(IRow row,boolean acceptNull){
		return load(row,acceptNull,(Map<String,String>)Constant.EmptyMap);
	}

	public T load(IRow srcRow,boolean acceptNull,Map<String,String> fieldsMap){
		if(srcRow==null)
			return (T)this;
		try{
			for(Iterator iter=Arrays.asList(getNames()).iterator();iter.hasNext();){
				String key=(String)iter.next();
				Object newValue=srcRow.get(esdk.str.or(fieldsMap.get(key),key));
				if(acceptNull||newValue!=null)
					put(key,newValue);
			}
			return (T)this;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param acceptNull:
	 * true：允许加载null值
	 * false：忽略null值
	 * */
	public T load(IRow row,boolean acceptNull,String[] includes){
		if(row==null)
			return (T)this;
		try{
			for(int i=0;i<includes.length;i++){
				String key=includes[i];
				Object newValue=row.get(key);
				if(acceptNull||newValue!=null)
					put(key,newValue);
			}
			return (T)this;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public boolean hasColumnName(String name){
		return record.containsKey(name);
	}

	public boolean hasColumn(String column){
		return EasyReflect.hasPropertyName(this.getMetaData().getClass(),esdk.str.toCamelCase(column,true));
	}

	public void set(String key,Object v){
		setNewValue(key,v);
	}

	public Map toMap(){
		return (Map)record.clone();
	}

	public void setAutoIncrement(boolean isAutoIncrement){
		this.isAutoIncrement=isAutoIncrement;
	}

	/**默认输出驼峰格式，且输出扩展字段*/
	public JSONObject toJsonObject(){
		return toJsonObject(true,true);
	}

	public <B> B toBean(Class<B> cls){
		try{
			return (B)esdk.reflect.copyBeanProperties(this,cls.newInstance());
		}catch(InstantiationException|IllegalAccessException e){
			throw esdk.tool.wrapThrowble(e);
		}
	}

	public JSONObject toJsonObject(String...columns){
		LinkedHashMap result=new LinkedHashMap();
		for(int i=0;i<columns.length;i++){
			result.put(columns[i],this.record.get(columns[i]));
		}
		return RowUtils.toJsonObject(result,false);
	}

	public JSONObject toJsonObject(boolean isFormatJavaBeanName){
		return toJsonObject(isFormatJavaBeanName,this.getNames());
	}

	public JSONObject toJsonObject(boolean isFormatJavaBeanName,boolean includeExtendFields){
		if(isFormatJavaBeanName){
			String[] columns=includeExtendFields?esdk.str.toArray(this.record.keySet()):this.getNames();
			return toJsonObject(true,columns);
		}
		return RowUtils.toJsonObject(this.record,isFormatJavaBeanName);
	}

	public JSONObject toJsonObject(boolean isFormatJavaBeanName,String...columns){
		if(columns.length==0)
			columns=getNames();
		return RowUtils.toJsonObject(this.record,isFormatJavaBeanName);
	}

	public Map toMap(String...columns){
		return toMap(false,columns);
	}

	public Map toMap(boolean isFormatJavaBeanName,String...columns){
		LinkedHashMap result=new LinkedHashMap();
		if(columns.length==0)
			columns=esdk.str.toArray(record.keySet());
		for(int i=0;i<columns.length;i++){
			result.put(isFormatJavaBeanName?EasyStr.toCamelCase(columns[i]):columns[i],this.record.get(columns[i]));
		}
		return result;
	}

	public T load(AbstractSelect aselect){
		this.load(aselect.fieldMap);
		return (T)this;
	}

	public T load(Map rowMap){
		Collection cols=esdk.array.overlap(rowMap.keySet(),Arrays.asList(getNames()));
		for(Iterator iter=cols.iterator();iter.hasNext();){
			String key=(String)iter.next();
			this.set(key,rowMap.get(key));
		}
		return (T)this;
	}

	/**
	 *isFormatJavaBeanProperty：为true可以自适应下划线或大小写的columnName写值到Row。
	 *checkExistColumn：为true则限制必须存在这个columnName才允许写值到Row
	 */
	public T load(Map map,boolean checkExistColumn,boolean isFormatJavaBeanProperty){
		for(Iterator iter=map.entrySet().iterator();iter.hasNext();){
			Entry entry=(Entry)iter.next();
			String columnName=(String)entry.getKey();
			boolean foundColumn=false;
			if(this.hasColumnName(columnName))
				foundColumn=true;
			else if(isFormatJavaBeanProperty){
				columnName=esdk.str.toUnderlineCase((String)entry.getKey());
				if(this.hasColumnName(columnName))
					foundColumn=true;
				else{
					columnName=(String)entry.getKey();
				}
			}
			if(!checkExistColumn||foundColumn)
				this.set(columnName,entry.getValue());

		}
		return (T)this;
	}

	public T load(Object bean){
		return (T)load(bean,false);
	}

	public T load(Object bean,boolean acceptNull){
		return load(bean,null,acceptNull);
	}

	public T load(Object bean,Map map){
		return (T)load(bean,map,false);
	}

	public T load(Object bean,Map map,boolean acceptNull){
		EasyReflect.copyBeanProperties(bean,this,false,acceptNull,map,RowUtils.Keywords);
		return (T)this;
	}

	@Override
	public String toString(){
		return esdk.json.toJSONString(toJsonObject(),esdk.json.getSerializeConfig());
	}

	@Override
	public int hashCode(){
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object o){
		if(o==null||!o.getClass().equals(this.getClass()))
			return false;
		return JSONUtil.toJsonStr(record).equals(JSONUtil.toJsonStr(((IRow)o).record()));
	}

	@Override
	public Boolean getBoolean(String key){
		return (Boolean)EasyObj.convert(get(key),Boolean.class);
	}

	@Override
	public Double getDouble(String key){
		return (Double)EasyObj.convert(get(key),Double.class);
	}

	@Override
	public Integer getInteger(String key){
		return (Integer)EasyObj.convert(get(key),Integer.class);
	}

	@Override
	public Long getLong(String key){
		return (Long)EasyObj.convert(get(key),Long.class);
	}

	@Override
	public Short getShort(String key){
		return (Short)EasyObj.convert(get(key),Short.class);
	}

	@Override
	public String getString(String key){
		return (String)EasyObj.convert(get(key),String.class);
	}

	public Date getDate(String key){
		return (Date)EasyObj.convert(get(key),Date.class);
	}

	@ApiModelProperty(hidden=true)
	@JSONField(serialize=false)
	public boolean isAutoIncrement(){
		return this.isAutoIncrement;
	}

	public T useCache(int cacheSec){
		this.useCacheSec=cacheSec;
		return (T)this;
	}

	public void allowSetNullValue(boolean value){
		this.allowSetNullValue=value;
	}

	@ApiModelProperty(hidden=true)
	@Override
	public String getPrimaryKeyName(){
		return (String)esdk.reflect.getFieldValue(getMetaData(),"PrimaryKey");
	}

	@ApiModelProperty(hidden=true)
	@Override
	public String getTableName(){
		return (String)esdk.reflect.getFieldValue(getMetaData(),"TABLENAME");
	}

}
