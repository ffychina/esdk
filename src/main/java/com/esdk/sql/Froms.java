package com.esdk.sql;
import java.util.LinkedHashMap;
public class Froms implements IAssemble{
  LinkedHashMap fromSet=new LinkedHashMap();

  public void clear(){
    fromSet.clear();
  }

  public Table getFirst() {
  	return (Table)fromSet.values().iterator().next();
  }
  public void addTable(String tablename){
    addTable(new Table(tablename));
  }

  public void addTable(String tablename,String alias){
    addTable(new Table(tablename,alias));
  }

  public void addTable(String tablename,String alias,String jointype){
    addTable(new Table(tablename,alias,jointype));
  }

  public void addTables(Table[] tables){
    for(int i=0;i<tables.length;i++){
      addTable(tables[i]);
    }
  }

  public void addTable(Table table){
    if(!fromSet.containsKey(table.getAliasName()))
      fromSet.put(table.getAliasName(),table);
    else
      System.out.println("!already has this TableName:".concat(table.getTableName()));
  }

  public int size(){
    return fromSet.size();
  }

  public Table findTable(String aliasname){
    return (Table)fromSet.get(aliasname);
  }

  public Table findTableByAlias(String aliasname){
    Table result=null;
    Table[] array=toArray();
    for(int i=0;i<array.length;i++){
      if(array[i].getAliasName().equalsIgnoreCase(aliasname)){
        result=array[i];
        break;
      }
    }
    return result;
  }

  public Table[] toArray(){
    return (Table[])fromSet.values().toArray(new Table[0]);
  }

  public String assemble(){
    StringBuffer result=new StringBuffer("FROM ");
    Table[] tablearray=toArray();
    for(int i=0;i<tablearray.length;i++){
      result.append(tablearray[i].toString());
    }
    return result.toString();
  }
  
  public Froms clone() {
  	Froms result=new Froms();
  	result.fromSet=(LinkedHashMap)this.fromSet.clone();
  	return result;
  }
}
