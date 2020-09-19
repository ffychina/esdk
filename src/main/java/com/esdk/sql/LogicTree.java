package com.esdk.sql;

import java.util.LinkedList;

public class LogicTree implements ILogic,IPrepareStatementSQL,IStatementSQL{
  private LinkedList<ILogic> conditionList=new LinkedList<ILogic>();
  private boolean isAnd=true;

  public LogicTree() {}
  
  public LogicTree(ILogic value) {
    addCondition(value);
  }
  
  public LogicTree(ILogic... value) {
    addCondition(value);
  }
  
  public LogicTree addCondition(ILogic... value) {
    addConditionAnd(value);
    return this;
  }
  
  public LogicTree addConditionOr(ILogic... value) {
    LogicTree logictree=new LogicTree(value);
    logictree.setAnd(false);
    addCondition(logictree);
    return this;
  }
  
  public LogicTree addConditionAnd(ILogic... value) {
    for(int i=0;i<value.length;i++){
      addCondition(value[i]);
    }
    return this;
  }
  
  public LogicTree addCondition(ILogic value) {
  	if(!conditionList.contains(value)) {
  		if(value instanceof LogicTree)
  			addCondition((LogicTree)value);
  		else
  			conditionList.add(value);
  	}
  	return this;
  }
  
  public LogicTree removeCondition(Field field) {
  	for(int i=0;i<conditionList.size();i++) {
  		ILogic condition=conditionList.get(i);
  		if(condition instanceof Where) {
  			Where where=(Where)condition;
  			if(where.getField().toString().equals(field.toString()))
  				conditionList.remove(where);
  		}
  	}
  	return this;
  }

  
  public LogicTree addCondition(LogicTree tree) {
    if(tree.size()>0&&!conditionList.contains(tree))
      conditionList.add(tree);
    return this;
  }
  
  public String toString(){
    return getStmtSql();
  }

  private String getLogicSymbol(boolean isand) {
    return isand?" AND ":" OR ";
  }

  
  public boolean isAnd(){
    return isAnd;
  }

  public void setAnd(boolean isand){
    this.isAnd=isand;
  }

  public LogicTree setOr(){
    this.isAnd=false;
    return this;
  }
  
  public void clear() {
    conditionList.clear();
  }

  public LogicTree clone() {
  	LogicTree result=new LogicTree();
  	result.conditionList=(LinkedList<ILogic>)this.conditionList.clone();
  	result.isAnd=this.isAnd;
  	return result;
  }
  
  public int size() {
    return conditionList.size();
  }
  
  public ILogic[] toArray() {
    return (ILogic[])conditionList.toArray(new ILogic[0]);
  }

  public Object[] getParameters(){
    LinkedList paramValues=new LinkedList();
    for(int i=0;i<conditionList.size();i++){
      ILogic logic=(ILogic)conditionList.get(i);
      Object[] array=logic.getParameters();
      for(int j=0;logic.getParameters()!=null&&j<array.length;j++)
        paramValues.add(array[j]);
    }
    return paramValues.toArray();
  }

  public String getPstmtSql(){
    StringBuffer result=new StringBuffer();
    for(int i=0;i<conditionList.size();i++){
      ILogic logic=(ILogic)conditionList.get(i);
      result.append(i==0?"":getLogicSymbol(logic.isAnd()));
      if(logic.getClass().equals(LogicTree.class))
        result.append("(").append(logic.getPstmtSql()).append(")");
      else
        result.append(logic.getPstmtSql());
    }
    return result.toString();
  }

  public String getStmtSql(){
    StringBuffer result=new StringBuffer();
    for(int i=0;i<conditionList.size();i++){
      ILogic logic=(ILogic)conditionList.get(i);
      result.append(i==0?"":getLogicSymbol(logic.isAnd()));
      if(logic.getClass().equals(LogicTree.class))
        result.append("(").append(logic.getStmtSql()).append(")");
      else
        result.append(logic.getStmtSql());
    }
    return result.toString();
  }
  
  @Override public int hashCode() {
  	return toString().hashCode();
  }
  
  @Override public boolean equals(Object obj) {
  	if(obj==this)
  		return true;
  	if(obj==null)
  		return false;
  	if(this.getClass().equals(obj.getClass()))
  		return toString().equals(obj.toString());
  	return false;
  }
}

