package com.esdk.sql.orm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.esdk.sql.ITableSelect;
import com.esdk.sql.SQLRuntimeException;
import com.esdk.utils.EasySql;

public class TableResultSet<T extends TableRow> extends ABResultSet<IRow> implements ITable,IChangeable,IRowSet<IRow>{
  private String tableName,pkColumnName;
  private String[] uniqueKeyColumn;
  private ArrayList<TableRow> listChangedRow;
  private Connection conn;
  private boolean isAutoIncrement;
  public TableResultSet(ITableSelect select,boolean isFindUniqueColmnNames) throws SQLException {
    super(select.toResultSet());
    conn=rs.getStatement().getConnection();
    setTableName(select.getTableName());
    pkColumnName=findPKColumn();
    if(isFindUniqueColmnNames)
    	uniqueKeyColumn=EasySql.findUniqueColumnNames(tableName,conn);
    else
    	uniqueKeyColumn=new String[0];
  }
  
  public TableResultSet(ITableSelect select) throws SQLException {
  	this(select,true);
  }
  public ABResultSet setTableName(String table) {
    tableName=table;
    return this;
  }

  private String findPKColumn() throws SQLException {
    String result=EasySql.findPKColumn(tableName,conn);
    if(result==null)
      result=this.getColumns()[0];
    return result;
  }
  
  public TableResultSet(ResultSet rset) {
    super(rset);
  }

  public Connection getConnection() {
    return conn;
  }
  
	public void setAutoIncrement(boolean isAutoIncrement){
		this.isAutoIncrement = isAutoIncrement;
	}
	
	public boolean isAutoIncrement(){
		return isAutoIncrement;
	}
	
  public TableRow createTableRow() {
    return new TableRow(this,getTableName(),getPrimaryKeyName());
  }
  
  @Override public List<IRow> getRowList() throws SQLException{
    return super.gainAllRow();
  }
  
  @Override protected IRow getCurrent() throws SQLException{
    TableRow result=createTableRow();
    /*result.setConnection(conn);*/
    result.record=super.getCurrentMap();
    result.isExistRecord=true;
    return result;
  }
  
  @Override Map getMapColumnClass() throws SQLException{
    if(mapColumnClass==null){
      ResultSetMetaData rsmdata=gainMetaData();
      mapColumnClass=new LinkedHashMap(rsmdata.getColumnCount());
      try{
        for(int i=1,n=rsmdata.getColumnCount()+i;i<n;i++){
          if(rsmdata.getColumnTypeName(i).equals("bit"))//改动部份，针对HSQLDB的BIT数据类型的兼容问题作出的调整
            mapColumnClass.put(rsmdata.getColumnLabel(i),int.class);
          else
            mapColumnClass.put(rsmdata.getColumnLabel(i),Class.forName(rsmdata.getColumnClassName(i)));
        }
      }
      catch(ClassNotFoundException e){
        throw new SQLException(e);
      }
    }
    return mapColumnClass;
  }
  
  @Override public String getTableName() {
    return tableName;
  }
  public String getPkColumn(){
    return pkColumnName;
  }
  
  public String[] getUniqueColumns(){
    return uniqueKeyColumn;
  }

  @Override public String getPrimaryKeyName(){
    return getPkColumn();
  }

  private List<TableRow> getChangedRowList() {
    if(listChangedRow==null)
      listChangedRow=new ArrayList();
    return listChangedRow;
  }
  
  private void addChangedRow(TableRow tableRow) {
    if(!getChangedRowList().contains(tableRow))
      getChangedRowList().add(tableRow);
  }
  
  public void save(TableRow row) {
    addChangedRow(row);
  }
  
  public TableRow insertRow() throws SQLException {
    TableRow insertRow=createTableRow();
    insertRow.setAutoIncrement(isAutoIncrement());
    insertRow.insert();
    add(insertRow);
    last();
    return insertRow;
  }
  
  public TableRow updateRow() throws SQLException {
    TableRow updateRow=(TableRow)getCurrentRow();
    updateRow.update();
    addChangedRow(updateRow);
    return updateRow;
  }
  
  public TableRow deleteRow() throws SQLException {
    TableRow deletedRow=(TableRow)this.rowList.remove(cursorPos);
    deletedRow.delete();
    addChangedRow(deletedRow);
    previous();
    return deletedRow;
  }
  
  public boolean contains(IRow row) {
    return rowList.contains(row);  
  }
  
  public boolean flush() throws SQLException{
    int errCount=0;
    List<TableRow> changedList=getChangedRowList();
    for(int i=0;i<changedList.size();i++){
      boolean result=changedList.get(i).flush();
      if(result)
        changedList.remove(i--);
      else
        errCount+=1;
    }
    return errCount==0;
  }
  
  public int indexOf(IRow row){
    return rowList.indexOf(row);  
  }
  
  public void moveTo(IRow obj,int newIndex){
    int oldIndex=rowList.indexOf(obj);
    if(oldIndex!=newIndex){
      rowList.add(newIndex,obj);
      if(oldIndex<newIndex&&oldIndex>=0){
        rowList.remove(oldIndex);
      }
      else{
        rowList.remove(oldIndex+1);
      }
    }
  }
  
  public void sort(int... posKeys) {
    sort(false,posKeys);
  }
  
  public void sort(boolean isDescend,int... posKeys) {
    try{
      String[] columns=getColumns();
      String[] columnKeys=new String[posKeys.length];
      for(int i=0;i<columnKeys.length;i++){
        columnKeys[i]=columns[i];
      }
      sort(isDescend,columnKeys);
    }
    catch(SQLException e){
      throw new RuntimeException(e);
    }
  }
  
  @Override public TableRow getRow(int index){
    return (TableRow)rowList.get(index);
  }
  
  @Override public TableRow getRow(){
    return (TableRow)rowList.get(cursorPos);
  }
  
  public void sort(final String column) {
    sort(false,column);
  }
  
  @Override public boolean isChanged(){
    return listChangedRow!=null&&listChangedRow.size()>0;
  }

  @Override public TableResultSet add(IRow tableRow){
    TableRow insertRow=(TableRow)tableRow;
    addChangedRow(insertRow);
    this.rowList.add(insertRow);
    return this;
  }
  
  @Override public TableResultSet add(int position,IRow tableRow){
    TableRow insertRow=(TableRow)tableRow;
    addChangedRow(insertRow);
    this.rowList.add(position,insertRow);
    return this;
  }
  
  @Override public TableResultSet add(List<IRow> rows){
    for(Iterator iter=rows.iterator();iter.hasNext();){
      TableRow tableRow=(TableRow)iter.next();
      add(tableRow);
    }
    return this;
  }
  
  @Override public TableResultSet add(IRowSet rows){
  	return this.add(rows.getRows());
  }
  
  @Override public String[] getColumnNames(){
    String[] result=null;
    try{
      result=getColumns();
    }
    catch(SQLException e){
      throw new SQLRuntimeException(e);
    }
    return result;
  }
  
  public List<IRow> getRows(){
    return rowList;
  }
  
  @Override  public boolean isEmpty(){
    return rowList.size()>0;
  }
  
  @Override public boolean remove(IRow row){
    if(rowList.remove(row)){
      TableRow deleteRow=(TableRow)row;
      deleteRow.delete();
      addChangedRow(deleteRow);
      return true;
    }
    return false;
  }
  
  @Override  public IRow remove(int index){
    TableRow result=getRow(index);
    remove(result);
    return result;
  }
  
  @Override public boolean remove(IRowSet iRowSet){
  	boolean result=true;
    for(int i=0,n=iRowSet.size();i<n;i++) {
    	result=result&&remove(iRowSet.getRow(i));
    }
    return result;
  }

  @Override public boolean remove(Collection<IRow> coll){
  	boolean result=true;
    for(Iterator iter=coll.iterator();iter.hasNext();) {
    	result=result&&remove((IRow)iter.next());
    }
    return result;
  }
  

  @Override public void removeAll(){
    for(Iterator iter=rowList.iterator();iter.hasNext();){
       remove((TableRow)iter.next());
    }
  }
  
  @Deprecated /**columns of TableResultSet is fixed ,invalid for this method*/
  @Override public IRowSet setColumnNames(String[] names){
    return null;
  }
  
  @Deprecated /**columns of TableResultSet is fixed, invalid for this method*/
  @Override public IRowSet setColumnNames(Collection names){
    return null;
  }
  
  @Override public IRow setRow(IRow row){
    checkRange(cursorPos);
    return rowList.set(cursorPos,row);
  }
  
  @Override public IRow setRow(int index,IRow row){
    checkRange(index);
    return rowList.set(cursorPos,row);
  }

  @Override public String toCsv() {
    try{
      return super.toCsv();
    }
    catch(SQLException e){
      throw new SQLRuntimeException(e);
    }
  }
  
  @Override public String toCsv(String... columns) {
    try{
      return super.toCsv(columns);
    }
    catch(SQLException e){
      throw new SQLRuntimeException(e);
    }
  }

	@Override public List<ABRowSet<IRow>> group(String...fields){
		return RowSetUtils.group(this,fields);
	}

}
