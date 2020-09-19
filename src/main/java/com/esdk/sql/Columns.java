package com.esdk.sql;

import java.util.LinkedHashMap;

public class Columns implements IAssemble{
  LinkedHashMap columnSet=new LinkedHashMap();

  public void clear(){columnSet.clear();}
  
  public int size(){return columnSet.size();}
  
  public Column[] toArray(){
    return (Column[])columnSet.values().toArray(new Column[0]);
  }
  
  public void addColumn(Table table,String columnname) {
    addColumn(table,columnname,null);
  }
  
  public void addColumn(Table table,String columnname,String functionname) {
  	 addColumn(new Column(table.createField(columnname),functionname));
  }

  public void addColumn(Table table,String columnname,String functionname,String aliasname) {
    Column column=new Column(table.createField(columnname),functionname,aliasname);
    addColumn(column);
  }
  
  public void addColumn(String prefix,String columnname) {
    addColumn(prefix,columnname,null);
  }
  
  public void addColumn(String prefix,String columnname,String functionname) {
    Column column=new Column(prefix,columnname,functionname);
    addColumn(column);
  }

  public void addColumn(String prefix,String columnname,String functionname,String aliasname) {
    Column column=new Column(prefix,columnname,functionname,aliasname);
    addColumn(column);
  }
  
  public void addColumn(Column column) {
    columnSet.put(column.toString(),column);
  }
  
  public void addColumns(Column[] columns) {
    for(int i=0;i<columns.length;i++){
      columnSet.put(columns[i].toString(),columns[i]);  
    }
  }
  
  public String assemble(){
    StringBuffer result=new StringBuffer();
    Column[] array=toArray();
    for(int i=0;i<array.length;i++){
      if(array[i].isExport())
        result.append(array[i].toString()).append(",");
    }
    if(result.length()>0&&result.lastIndexOf(",")==result.length()-1)
      result.delete(result.length()-1,result.length());
    if(result.length()==0)
      result.append("*");
    return result.toString();
  }
  
  @Override
  public Columns clone() {
  	Columns result=new Columns();
  	result.columnSet=(LinkedHashMap)this.columnSet.clone();
  	return result;
  }
}

