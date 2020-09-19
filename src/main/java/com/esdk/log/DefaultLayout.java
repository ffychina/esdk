package com.esdk.log;
public class DefaultLayout implements ILogFormater{
  public DefaultLayout(){}

  public ILogItem format(ILogItem item){
    item.setResult(item.getTime().concat(item.getDelimiter()).concat(item.getName()).concat(item.getDelimiter())
        .concat(item.getLevel().toString()).concat(item.getDelimiter()).concat(item.getContent()).concat(
            item.getEndChar()));
    return item;
  }
}
