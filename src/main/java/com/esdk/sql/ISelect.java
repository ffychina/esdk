/**
 * @author Franky.Fan
 * SQL的Select类的接口，所有的Select对象都需要实现该接口，用于执行select，获得记录集，并要求实现count方法
 * ，可直接执行select count(*)得到记录集总数。
*/
package com.esdk.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.IRowSet;

public interface ISelect<T> extends ISQL{
	public int DefaultLimit=50;
  ResultSet toResultSet() throws SQLException;       //返回JDBC默认的数据集
  IRowSet toRowSet() throws SQLException;            //返回数据集，数据已获得到JVM本地
  ABResultSet toABResultSet() throws SQLException;   //返回数据集（数据还留在数据集
  String[][] toArray() throws SQLException;          //返回二维数组
  int count()throws SQLException;                   //所有的select语句都用Select Count(*)的方式得到数据集的数量。
}
