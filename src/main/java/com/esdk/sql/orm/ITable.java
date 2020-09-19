package com.esdk.sql.orm;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModelProperty;

public interface ITable{
	@JSONField(serialize=false)
	public String getPrimaryKeyName();

	@JSONField(serialize=false)
	public String getTableName();
}
