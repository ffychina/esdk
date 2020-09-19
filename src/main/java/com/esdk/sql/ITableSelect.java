package com.esdk.sql;

import java.sql.SQLException;

import com.esdk.sql.orm.TableResultSet;
import com.esdk.sql.orm.TableRow;

public interface ITableSelect<T extends ISelect,K extends TableRow> extends ISelect<T>{
  String getTableName();
  TableResultSet toTableResultSet() throws SQLException;
}
