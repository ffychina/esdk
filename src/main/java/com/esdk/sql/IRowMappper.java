package com.esdk.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IRowMappper {
	public Object mapRow(ResultSet rs,int index)throws SQLException;
}
