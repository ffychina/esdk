package com.esdk.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.sql.orm.AbstractSelect;
import com.esdk.sql.orm.ParentResultSet;
import com.esdk.sql.orm.ParentRow;
import com.esdk.utils.EasyStr;
import com.esdk.utils.EasyObj;
/***
 * 多表查询对象
 * @author 范飞宇
 * @since 2006.?.? 
 */
public class JoinSelect implements ISelect,IPageSelect{
  private MultiSelect multiSelect;
  private JoinConfig joinConfig;
  ArrayList<Select> _selects=new ArrayList(4);
  private HashSet aliasSet=new HashSet(8);
  private String _joinType=LEFTJOIN;
  
  public static final String INNERJOIN=JoinConfig.INNERJOIN,LEFTJOIN=JoinConfig.LEFTJOIN,RIGHTJOIN=JoinConfig.RIGHTJOIN,FULLJOIN=JoinConfig.FULLJOIN;

  public JoinSelect(Connection conn) {
    multiSelect=new MultiSelect(conn);
  }
  
  public JoinSelect(Select[] selects,Connection conn) {
    this(conn);
   this._selects.addAll(Arrays.asList(selects));
  }
  
  public JoinSelect(Connection conn,boolean isLimitRows,Select... selects) {
    this(selects,conn);
  	setTop(isLimitRows?DefaultLimit:0);
  }
  
  public JoinSelect(Connection conn,AbstractSelect... selects) {
    this(conn);
    setTop(DefaultLimit);
    for(int i=0;i<selects.length;i++){
    	selects[i].parse();
    	_selects.add(selects[i].getSelect());
		}
  }
  
  public JoinSelect(Connection conn,boolean isLimitRows,AbstractSelect... selects) {
    this(conn,selects);
    setTop(isLimitRows?DefaultLimit:0);
  }
  
  public JoinSelect(Connection conn,boolean isLimitRows,String joinType,AbstractSelect... selects) {
    this(conn,selects);
    setTop(isLimitRows?DefaultLimit:0);
    if(esdk.str.isValid(joinType))
    	setJoinType(joinType);
  }
  
  public JoinSelect setJoinConfig(JoinConfig jc) {
  	this.joinConfig=jc;
  	return this;
  }
  
  public JoinConfig getJoinConfig() {
  	if(joinConfig==null)
  		joinConfig=new JoinConfig();
  	return joinConfig;
  }
  
  public JoinSelect setJoinType(String jointype) {
  	this._joinType=jointype;
  	return this;
  }
  
  public JoinSelect on(AbstractSelect joinSelect,AbstractSelect masterSelect) {
  	getJoinConfig().on(joinSelect,masterSelect);
  	return this;
  }
  
  public JoinSelect on(AbstractSelect joinSelect,AbstractSelect masterSelect,String joinType) {
  	getJoinConfig().on(joinSelect,masterSelect,joinType);
  	return this;
  }
  
  public JoinSelect on(Select joinSelect, Field f1, Field f2) {
  	getJoinConfig().on(joinSelect, f1, f2);
		return this;
	}
  public JoinSelect on(Select joinSelect, Field f1, Field f2,String joinType) {
  	getJoinConfig().on(joinSelect, f1, f2,joinType);
		return this;
	}
  
  public JoinSelect on(AbstractSelect joinSelect, Field f1, Field f2) {
  	getJoinConfig().on(joinSelect, f1, f2);
		return this;
	}
  
  public JoinSelect on(AbstractSelect joinSelect, Field f1, Field f2,String joinType) {
  	getJoinConfig().on(joinSelect, f1, f2,joinType);
		return this;
	}

	public JoinSelect on(Select joinSelect, ILogic logic) {
		getJoinConfig().on(joinSelect, logic);
		return this;
	}

	public JoinSelect on(AbstractSelect joinSelect, ILogic logic) {
		joinSelect.addOnCondition(logic);
		return this;
	}

	public JoinSelect on(Select joinSelect, ILogic logic,String joinType) {
		getJoinConfig().on(joinSelect, logic,joinType);
		return this;
	}
	
	public JoinSelect onPrimaryKey(AbstractSelect as1, AbstractSelect as2) {
		getJoinConfig().onPrimaryKey(as1, as2);
		return this;
	}

	public void addSelects(Select... selects) {
		this._selects.addAll(Arrays.asList(selects));
  }
  
  public void addSelect(Select select) {
    multiSelect.columns.addColumns(select.columns.toArray());
    multiSelect.froms.addTables(select.froms.toArray());
    multiSelect.groupbys.add(select.groupbys.toArray());
    multiSelect.orderbys.addOrderBy(select.orderbys.toArray());
    multiSelect.wheres.addCondition(select.wheres.toArray());
    if(getTop()>0&&select.getTop()>0)
    	multiSelect.setTop(EasyObj.min(multiSelect.getTop(),select.getTop()));
  }

  public void setDistinct(boolean isdistinct){
    multiSelect.setDistinct(isdistinct);
  }

  public boolean perform() throws SQLException{
  	parse();
    return multiSelect.perform();
  }

  public ResultSet toResultSet()throws SQLException{
  	parse();
    return multiSelect.toResultSet();
  }

  public ABResultSet toABResultSet() throws SQLException{
  	parse();
    return multiSelect.toABResultSet();
  }
  
  public JSONArray toJsonArray() throws SQLException {
  	parse();
  	return multiSelect.toJsonArray();
  }
  public JSONArray toJsonArray(boolean isFomatJavaBeanName) throws SQLException {
  	parse();
  	return multiSelect.toJsonArray(isFomatJavaBeanName);
  }
  public ABRowSet toRowSet() throws SQLException{
  	parse();
    return multiSelect.toRowSet();
  }
  
  public <T extends ParentResultSet> T toParentResultSet(Class<T> resultsetClass) throws SQLException{
  	parse();
  	ResultSet rs=multiSelect.toResultSet();
		try{
			java.lang.reflect.Constructor constructor=resultsetClass.getConstructor(new Class[]{ResultSet.class});
			ParentResultSet result=(ParentResultSet)constructor.newInstance(new Object[]{rs});
			return (T)result;
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
  }
  
  public <T extends ParentRow> ABRowSet<T> toRowSet(Class<T> parentRowCls) throws Exception{
  	Class resultsetCls=Class.forName(parentRowCls.getName().replaceAll("Row$","ResultSet"));
  	return (ABRowSet<T>)new ABRowSet(toParentResultSet(resultsetCls));
  }
  
  public List toList(IRowMappper mapper) throws SQLException{
  	parse();
    return multiSelect.toList(mapper);
  }
  public List toList(IRowMappper mapper,int start,int limit) throws SQLException{
  	parse();
    return multiSelect.toList(mapper,start,limit);
  }
  
  public List toList(final Class pojoClass) throws SQLException{
  	parse();
  	return multiSelect.toList(pojoClass);
  }
  public List toList(final Class pojoClass,int start,int limit) throws SQLException{
  	parse();
  	return multiSelect.toList(pojoClass,start,limit);
  }
  
  public String[][] toArray()throws SQLException{
  	parse();
    return multiSelect.toArray();
  }

  public int count() throws SQLException {
  	parse();
  	return multiSelect.count();
  }
  
  public JoinSelect setTop(int number){
    multiSelect.setTop(number);
    return this;
  }
  
  public int getTop(){
    return multiSelect.getTop();
  }

  public Connection getConnection(){
    return multiSelect.getConnection();
  }

  public void setConnection(Connection connection){
    multiSelect.setConnection(connection);
  }

  public String getSQL(){
  	parse();
    return multiSelect.getSQL();
  }

  
  public void clear(){
    multiSelect.clear();
  }

  public void parse(){
  	for(int i=0;i<_selects.size();i++){
  		Select s=_selects.get(i);
    	//1.找出没有on条件的select,把它放在第一位.
  		if(s.getTable().onCondition.toString().length()==0) {
  			if(i>0) {
  				_selects.add(0,_selects.remove(i));
  			}
  		}
		}
  	
  	for(int i=0;i<_selects.size();i++){
  		Select s=_selects.get(i);
    	//2.配置别名,如果別名有重复,就增加别名的序列号.
  		String alias=s.getTable().getAliasName();
			if(EasyStr.isBlank(alias))
				alias=SQLAssistant.getAbbreviation(s.getTableName());
			while(aliasSet.contains(alias)) {
				alias=EasyStr.serial(alias);
			}
			s.setTableAliasName(alias);
			aliasSet.add(alias);
		}
  	for(int i=0;i<_selects.size();i++) {
  		if(i==0)
  			_selects.get(i).setJoinType(null);
  		else if(i>0 && esdk.str.isBlank(_selects.get(i).getTable().getJoinType()))
  			_selects.get(i).setJoinType(_joinType);
  		addSelect(_selects.get(i));
  	}
  	_selects.clear();
    multiSelect.parse();
  } 

	public JoinSelect addOrderBy(Field... fields){
    this.multiSelect.orderbys.addOrderBy(fields);
    return this;
  }

	public JoinSelect addOrderBy(Field fields,boolean isDesc){
    this.multiSelect.orderbys.addOrderBy(fields,isDesc);
    return this;
  }
	
	public JoinSelect addOrderBy(OrderBy... orderBy){
    this.multiSelect.orderbys.addOrderBy(orderBy);
    return this;
  }

  @Override  public String toString(){
  	return getSQL();
  }
  
	public JoinSelect setRowsOffset(int startByZero){
		multiSelect.setOffset(startByZero);
		return this;
	}
	public JoinSelect setRowsLimit(int limit) {
		multiSelect.setRowsLimit(limit);
		return this;
	}
	
  public static void test(){
    try{
//      Select select=new Select("ordermaster");
      Select select=new Select((Connection)null);
      select.setTableName("OrderMaster","o");
      select.addColumn("TotalQuantity",Column.COUNT);
      select.addColumn("OrderNumber");
      select.addOrderBy("OrderNumber");
      select.addOrderBy("EndPointID");
      select.addOrderBy("StartPointID",true);
      select.addNotEqualEmplyValue("UserValue1");
      select.addEqualEmplyNumeric("WarehouseFinishTime");
      select.addNotEqualEmplyNumeric("FeedbackTime");
      select.addEqualNumeric("EndPointID","213");
      Where where=new Where();
      where.setFunctionName("len");
      where.setField(select.createField("FeedbackTime"));
      where.setDataType(Where.TIME14);
      where.setExpression("=");
      where.setRightValue("0");
      select.addCondition(where);
      select.addEqualCondition("Remark","txt");
      select.addCondition("1=2");
      select.addGroupBy("OrderTypeID");
      
      Select select1=new Select((Connection)null);
      select1.setTableName("EdiDataIn","ei",Table.FULLJOIN);
      select1.setJoinType(Table.INNERJOIN);
      select1.setTop(10);
      select1.addColumn("OrderID");
      select1.addGroupBy("ClientID");
      select1.addEqualEmplyValue("Remark");
      select1.addInCondition("OrderNumber",new String[]{"a","b"});
      Condition condition=new Condition("3=4");
      condition.setAnd(false);
      select1.addCondition(condition);
/*      select1.addOnCondition(select1.createField("OrderNumber"), select.createField("OrderNumber"));
      select1.addOnCondition(new Field(select.getTable().getAliasName(),"OrderTypeID"), new Field(select1.getTableAliasName(),"OrderTypeID"));
*/
      Select select2=new Select((Connection)null);
      select2.setTableName("OrderFeedbackExtent","ofe",Table.LEFTJOIN);
      select2.addColumn("IsFeedbackFinish");
      /*select2.addOnCondition(select1.createField("OrderNumber"), select2.createField("OrderNumber"));
      select2.addOnCondition(select.createField("OrderID"),select2.createField("OrderID"));*/

      JoinSelect combo=new JoinSelect(null);
      combo.setDistinct(true);
      combo.addSelects(new Select[] {select,select1,select2});
      combo.on(select1,select1.createField("OrderNumber"), select.createField("OrderNumber"));
      combo.on(select1,new Field(select.getTable().getAliasName(),"OrderTypeID"), new Field(select1.getTableAliasName(),"OrderTypeID"));
      combo.on(select2,select1.createField("OrderNumber"), select2.createField("OrderNumber"));
      combo.on(select2,select1.createField("OrderID"),select2.createField("OrderID"));
      combo.addOrderBy(select2.createField("OrderId"),true);
      combo.parse();
      esdk.tool.assertEquals(combo.getSQL(),
      		"Select Distinct Top 10 Count(o.TotalQuantity) as CountTotalQuantity,o.OrderNumber,ei.OrderID,ofe.IsFeedbackFinish\r\n" + 
      		"From OrderMaster o join EdiDataIn ei on ei.OrderNumber=o.OrderNumber and o.OrderTypeID=ei.OrderTypeID left join OrderFeedbackExtent ofe on ei.OrderNumber=ofe.OrderNumber and ei.OrderID=ofe.OrderID\r\n" + 
      		"Where (o.UserValue1 <> '' and o.UserValue1 is not null) and (o.WarehouseFinishTime = 0 or o.WarehouseFinishTime is null) and (o.FeedbackTime > 0 and o.FeedbackTime is not null) and o.EndPointID = '213' and len(o.FeedbackTime) = '0' and o.Remark = 'txt' and 1=2 and (ei.Remark = '' or ei.Remark is null) and ei.OrderNumber in ('a','b') or 3=4\r\n" + 
      		"Order By ofe.OrderId desc,o.OrderNumber,o.EndPointID,o.StartPointID desc\r\n" + 
      		"Group By o.OrderTypeID,ei.ClientID");
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args){
    test();
  }

}
