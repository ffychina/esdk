package com.esdk.sql;
import java.util.LinkedList;
public class OrderBys implements IAssemble{
  LinkedList orderbySet=new LinkedList();

  public void addOrderBy(OrderBy... orderbys){
    for(int i=0;i<orderbys.length;i++){
      addOrderBy(orderbys[i]);
    }
  }

  public void addOrderBy(OrderBy orderby){
    orderbySet.add(orderby);
  }

  public void addOrderBy(Field field){
    OrderBy orderby=new OrderBy(field);
    orderbySet.add(orderby);
  }

  public void addOrderBy(Field... fields){
    for(int i=0;i<fields.length;i++){
      addOrderBy(fields[i]);
    }
  }

  public void addOrderBy(Field field,boolean isDesc){
    OrderBy orderby=new OrderBy(field,isDesc);
    if(!orderbySet.contains(orderby))
    	orderbySet.add(orderby);
  }

  public void clear(){
    orderbySet.clear();
  }

  public OrderBy[] toArray(){
    return (OrderBy[])orderbySet.toArray(new OrderBy[0]);
  }

  public String assemble(){
    StringBuffer result=new StringBuffer();
    result.append(orderbySet.size()>0?"ORDER BY ":"");
    OrderBy[] array=toArray();
    for(int i=0;i<array.length;i++){
      result.append(array[i].toString());
      result.append(i<array.length-1?",":"");
    }
    return result.toString();
  }
  
  public OrderBys clone() {
  	OrderBys result=new OrderBys();
  	result.orderbySet=this.orderbySet;
  	return result;
  }
}
