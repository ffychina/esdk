package com.esdk.sql;

import java.sql.SQLException;
import java.util.Date;


public class Wheres implements IAssemble,IStatementSQL,IPrepareStatementSQL{
  private LogicTree conditionList;

  public Wheres(){
    conditionList=new LogicTree();
  }
  
  public void addCondition(String value){
    conditionList.addCondition(new Condition(value));
  }

  public void addCondition(ILogic value){
    conditionList.addCondition(value);
  }

  public void addCondition(ILogic[] value){
    for(int i=0;i<value.length;i++){
      addCondition(value[i]);
    }
  }

  public void addEqualColumn(Field field,Field anotherField){
    conditionList.addCondition(new FieldCondition(field,anotherField));
  }
  
  public void addNotEqualColumn(Field field,Field anotherField){
    conditionList.addCondition(new FieldCondition(field,false,anotherField));
  }
  
  public void addCondition(Field field,String exception,Object value){
    Where where=new Where(field,Where.OTHER,exception,value);
    conditionList.addCondition(where);
  }
  
  public void addCondition(Field field,int datatype,String exception,String value){
    Where where=new Where(field,datatype,exception,value);
    conditionList.addCondition(where);
  }
  
  public void addLikeCondition(Field field,String value){
    //Where where=new Where(field,Where.LIKE,value);
    ILogic where = WhereFactory.create(field,Where.LIKE, value);
    conditionList.addCondition(where);
  }
  
  public void addNotLikeCondition(Field field,String value){
    //Where where=new Where(field,Where.LIKE,value);
    ILogic where = WhereFactory.create(field,Where.NOTLIKE, value);
    conditionList.addCondition(where);
  }
  
  public void removeCondition(Field field){
  	conditionList.removeCondition(field);
  }  
  
  public void clearCondition(){
  	conditionList.clear();
  }
  
  public void addEqualString(Field field,Object value){
  	ILogic where = WhereFactory.create(field,Where.EQ, value);
    conditionList.addCondition(where);
  }
  
  public void addEqualCondition(Field field,Object value){
  	if(value==null)
  		conditionList.addCondition(new NullCondition(field));
  	else
  		conditionList.addCondition(WhereFactory.create(field,value));
  }
  
  public void addEqualCondition(Field field,Date value){
    //Where where=new Where(field,Where.DateTime,new java.sql.Timestamp(value.getTime()));
  	ILogic where = WhereFactory.create(field,Where.EQ, value.getTime());
    conditionList.addCondition(where);
  }
  
  public void addEqualCondition(Field field,Boolean value){
  	conditionList.addCondition(WhereFactory.create(field,Where.EQ, value));
  }
  
  public void addEqualCondition(Field field,String value){
  	conditionList.addCondition(WhereFactory.create(field,Where.EQ, value));
  }
  
  public void addEqualCondition(Field field,Number value){
    //Where where=new Where(field,Where.NUMERIC,value);
    ILogic where = WhereFactory.create(field,Where.EQ, value);
    conditionList.addCondition(where);
  }
  
  public void addNotEqualCondition(Field field,Object value){
    //Where where=new Where(field,Where.NOTEQUAL,value);
    ILogic where = WhereFactory.create(field,Where.NOTEQUAL, value);
    conditionList.addCondition(where);
  }
  
  public void addNotEqualCondition(Field field,String value){
    //Where where=new Where(field,Where.CHAR,"<>",value);
    ILogic where = WhereFactory.create(field,Where.NOTEQUAL, value);
    conditionList.addCondition(where);
  }
  
  public void addNotEqualCondition(Field field,Boolean value){
    //Where where=new Where(field,Where.BOOL,"<>",value);
  	ILogic where = WhereFactory.create(field,Where.NOTEQUAL, value);
    conditionList.addCondition(where);
  }
  
  public void addNotEqualCondition(Field field,Number value){
    //Where where=new Where(field,Where.NUMERIC,"<>",value);
  	ILogic where = WhereFactory.create(field,Where.NOTEQUAL, value);
    conditionList.addCondition(where);
  }
  
  public void addNotEqualString(Field field,String value){
    //Where where=new Where(field,Where.CHAR,"<>",value);
  	ILogic where = WhereFactory.create(field,Where.NOTEQUAL, value);
    conditionList.addCondition(where);
  }
  
  public void addInCondition(Field field,String[] value){
    Where where=new Where(field,value);
    conditionList.addCondition(where);
  }
  
  public void addInCondition(Field field,Number[] value){
    Where where=new Where(field,Where.NUMERIC,value);
    conditionList.addCondition(where);
  }

  public void addInCondition(Field field,ISelect value){
    Condition subquery=new InCondition(field,value);
    conditionList.addCondition(subquery);
  }

  public void addEqualNumeric(Field field,String value){
    //Where where=new Where(field,Where.NUMERIC,value);
    ILogic where = WhereFactory.create(field,Where.EQ, value);
    conditionList.addCondition(where);
  }

  public void addNotEqualNumeric(Field field,String value){
    //Where where=new Where(field,Where.NUMERIC,Where.NOTEQUAL,value);
    ILogic where = WhereFactory.create(field,Where.NOTEQUAL, value);
    conditionList.addCondition(where);
  }

  public void addInNumeric(Field field,String[] value){
    Where where=new Where(field,Where.NUMERIC,value);
    conditionList.addCondition(where);
  }
  
  public void addNotInCondition(Field field,ISelect value){
    conditionList.addCondition(new InCondition(field,true,value));
  }
  
  public void addNotInCondition(Field field,String value[]){
  	if(value.length==0)
  		return;
    Where where=new Where();
    where.setField(field);
    where.setExpression(Where.NOTIN);
    where.setRightValue(value);
    conditionList.addCondition(where);
  }

  public void addNotInCondition(Field field,Number value[]){
  	if(value.length==0)
  		return;
    Where where=new Where();
    where.setField(field);
    where.setExpression(Where.NOTIN);
    where.setRightValue(value);
    conditionList.addCondition(where);
  }

  public void addEqualEmplyValue(Field field){
    Where where1=new Where(field,Where.CHAR,Where.EQUAL,"");
    Condition nullcondition=new NullCondition(field,true);
    nullcondition.setAnd(false);
    LogicTree tree=new LogicTree(new ILogic[]{where1,nullcondition});
    conditionList.addCondition(tree);
  }

  public void addNotEqualEmplyValue(Field field){
    Where where1=new Where(field,Where.CHAR,Where.NOTEQUAL,"");
    Condition nullcondition=new NullCondition(field,false);
    nullcondition.setAnd(true);
    LogicTree tree=new LogicTree(new ILogic[]{where1,nullcondition});
    conditionList.addCondition(tree);
  }

  public void addEqualEmplyNumeric(Field field){
    ILogic where1=new Where(field,Where.NUMERIC,Where.EQUAL,0);
    Condition nullcondition=new NullCondition(field,true);
    nullcondition.setAnd(false);
    LogicTree tree=new LogicTree(new ILogic[]{where1,nullcondition});
    conditionList.addCondition(tree);
  }

  public void addEqualTrue(Field field){
    ILogic where1=new Where(field,Where.BOOL,Where.EQUAL,Boolean.TRUE);
    Condition notNull=new NullCondition(field,false);
    notNull.setAnd(true);
    LogicTree tree=new LogicTree(new ILogic[]{where1,notNull});
    conditionList.addCondition(tree);
  }
  
  public void addNotEqualEmplyNumeric(Field field){
    Where where1=new Where(field,Where.NUMERIC,">",0);
    Condition nullcondition=new NullCondition(field,false);
    nullcondition.setAnd(true);
    LogicTree tree=new LogicTree(new ILogic[]{where1,nullcondition});
    conditionList.addCondition(tree);
  }

  public void clear(){
    conditionList.clear();
  }

  public int size(){
    return conditionList.size();
  }

  public String assemble(){
    String result=conditionList.getStmtSql();
    if(result.trim().length()>0)
      return "WHERE ".concat(result);
    return result;
  }

  public ILogic[] toArray(){
    return conditionList.toArray();
  }

  public String getStmtSql(){
    return assemble();
  }

  public Object[] getParameters(){
    return conditionList.getParameters();
  }

  public String getPstmtSql(){
    String result=conditionList.getPstmtSql();
    if(result.trim().length()>0)
      return "WHERE ".concat(result);
    return result;
  }
  
  public Wheres clone() {
  	Wheres result=new Wheres();
  	result.conditionList=this.conditionList.clone();
  	return result;
  }
}
