/**
 * @author Franky.Fan
 * 定义SQL查询分页的接口，早期只是针对sqlserver，已过时，现在应使用setRowsOffset(int nums)和setRowsLimit(int nums)定义分页。
*/
package com.esdk.sql;

public interface IPageSelect<T> extends ISelect<T>{
  T setTop(int number);   //设置TOP，早期针对sqlserver而设。后期应使用setRowsLimit(int nums)。
  int getTop();           
	T setRowsOffset(int startByZero);
  T setRowsLimit(int nums);
  //int getRowsOffset();
  //int getRowsLimit();
  //T setPrimaryKeyFieldName(String fieldName);
}
