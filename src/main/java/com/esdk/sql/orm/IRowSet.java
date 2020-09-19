package com.esdk.sql.orm;

import java.util.Collection;
import java.util.List;

public interface IRowSet<T extends IRow> extends IResultSet<T>{
  String[] getColumnNames();
  IRowSet setColumnNames(String[] names);
  IRowSet setColumnNames(Collection names);
  IRowSet add(int position,IRow row);
  IRowSet add(IRow row);
  IRowSet add(List<T> rows);
  IRowSet add(IRowSet<T> rows);
  boolean remove(IRow row);
  IRow remove(int index);
  boolean remove(IRowSet<T> rs);
  boolean remove(Collection<IRow> list);
  List<IRow> getRows();
  /*List<List> toList();*/
  void removeAll();
  boolean isEmpty();
  IRow getRow(int i);
  IRow getRow();
  IRow setRow(IRow row);
  IRow setRow(int index,IRow row);
	Collection group(String...fields);
	String toCsv();
	String toCsv(String...labels);
  public T getFirstRow();
}
