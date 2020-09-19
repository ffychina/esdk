package com.esdk.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Franky.Fan
 * SQL的最底层的接口，是整个ORM的核心，定义了SQL对象的最基本的方法。
*/

public interface ISQL extends IConnectionable{
  String getSQL(); //获得可直接执行的SQL语句
  void clear();   //清除所有的查询参数，初始化对象
  void parse()throws Exception;   //解释SQL语句
  boolean perform() throws SQLException;   //执行SQL
  Connection getConnection();   
}
