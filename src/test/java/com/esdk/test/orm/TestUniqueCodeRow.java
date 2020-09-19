package com.esdk.test.orm;
import com.esdk.sql.orm.ParentRow;

import java.sql.SQLException;

import java.sql.Connection;

import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

 @ApiModel("唯一码写入测试")
public class TestUniqueCodeRow extends ParentRow<TestUniqueCodeRow>{
  public transient static final String PrimaryKey=TestUniqueCodeMetaData.PrimaryKey;
  public transient static final String tableName=TestUniqueCodeMetaData.TABLENAME;
  public transient static final TestUniqueCodeMetaData metaData=TestUniqueCodeSelect.metaData;
  public transient static final TestUniqueCodeMetaData md=metaData;
  public TestUniqueCodeRow(){
    super();
    setAutoIncrement(TestUniqueCodeMetaData.IsAutoIncrement);
  }

  public TestUniqueCodeRow(Connection conn){
    this();
    setConnection(conn);
  }

  public TestUniqueCodeRow(Connection conn,Number pkid)throws SQLException{
    this();
    setConnection(conn);
    refresh(pkid);
  }

  public TestUniqueCodeMetaData getMetaData(){
    return metaData;
  }

  public TestUniqueCodeRow newSelf(){
    return new TestUniqueCodeRow();
  }

  @ApiModelProperty(value="",example="0")
  public Long getUid(){
    return (Long)get(TestUniqueCodeMetaData.Uid);
  }

  public void setUid(Long value){
    setNewValue(TestUniqueCodeMetaData.Uid,value);
  }

  public Long getMachineId(){
    return (Long)get(TestUniqueCodeMetaData.MachineId);
  }

  public void setMachineId(Long value){
    setNewValue(TestUniqueCodeMetaData.MachineId,value);
  }

  public Long getProductId(){
    return (Long)get(TestUniqueCodeMetaData.ProductId);
  }

  public void setProductId(Long value){
    setNewValue(TestUniqueCodeMetaData.ProductId,value);
  }

  @ApiModelProperty("")
  public Boolean getValid(){
    return (Boolean)get(TestUniqueCodeMetaData.Valid);
  }

  public void setValid(Boolean value){
    setNewValue(TestUniqueCodeMetaData.Valid,value);
  }

  public Date getCreateTime(){
    return (Date)get(TestUniqueCodeMetaData.CreateTime);
  }

  public void setCreateTime(Date value){
    setNewValue(TestUniqueCodeMetaData.CreateTime,value);
  }

}
