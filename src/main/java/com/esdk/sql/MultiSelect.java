package com.esdk.sql;

import java.sql.Connection;

public class MultiSelect<T> extends Select{
  public MultiSelect(Connection conn){
    super(conn);
  }

  public Table getTable(){
    Table[] fromarray=froms.toArray();
    return fromarray[fromarray.length-1];
  }

  public void addTable(Table table){
    froms.addTable(table);
  }
  
  public void addTableName(String tablename){
    froms.addTable(tablename);
  }

  public void addTableName(String tablename,String alias){
    froms.addTable(tablename,alias);
  }

  public void addTableName(String tablename,String alias,String jointype){
    froms.addTable(tablename,alias,jointype);
  }

  public Table findTable(String aliasname) {
    Table result=froms.findTable(aliasname);
    return result!=null?result:froms.findTableByAlias(aliasname);
  }

  public Field createField(String tableAliasName,String fieldname) {
    return new Field(findTable(tableAliasName).getAliasName(),fieldname); 
  }
  
  public static void test(){
    try{
//      Select select=new Select("ordermaster");
      MultiSelect select=new MultiSelect(null);
      select.addTableName("OrderMaster","o");
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
      
      select.addTableName("EdiDataIn","ei",Table.INNERJOIN);
      select.addOnCondition(select.createField("ei","OrderNumber"), select.createField("o","OrderNumber"));
      select.addOnCondition(new Field(select.findTable("o").getAliasName(),"OrderTypeID"), new Field(select.getTableAliasName(),"OrderTypeID"));
      select.setTop(10);
      select.setDistinct(true);
      select.addColumn("OrderID");
      select.addGroupBy("ClientID");
      select.addEqualEmplyValue("Remark");
      select.addInCondition("OrderNumber",new String[]{"a","b"});
      Condition condition=new Condition("3=4");
      condition.setAnd(false);
      select.addCondition(condition);
      
      select.addTableName("OrderFeedbackExtent","ofe",Table.LEFTJOIN);
      select.addColumn("IsFeedbackFinish");
      select.addOnCondition(new Field(select.findTable("ei").getAliasName(),"OrderNumber"), new Field(select.findTable("ofe").getAliasName(),"OrderNumber"));
      select.addOnCondition(new Field(select.findTable("o").getAliasName(),"OrderID"), new Field(select.findTable("ofe").getAliasName(),"OrderID"));
      select.parse();
      System.out.println(select.getSQL());
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public static void main(String[] args){
    test();
  }
}
