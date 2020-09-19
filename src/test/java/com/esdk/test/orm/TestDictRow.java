package com.esdk.test.orm;
import com.esdk.sql.orm.ParentRow;

import java.sql.SQLException;

import java.sql.Connection;

import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

 @ApiModel("测试表")
public class TestDictRow extends ParentRow<TestDictRow>{
  public transient static final String PrimaryKey=TestDictMetaData.PrimaryKey;
  public transient static final String tableName=TestDictMetaData.TABLENAME;
  public transient static final TestDictMetaData metaData=TestDictSelect.metaData;
  public transient static final TestDictMetaData md=metaData;
  public TestDictRow(){
    super();
    setAutoIncrement(TestDictMetaData.IsAutoIncrement);
  }

  public TestDictRow(Connection conn){
    this();
    setConnection(conn);
  }

  public TestDictRow(Connection conn,Number pkid)throws SQLException{
    this();
    setConnection(conn);
    refresh(pkid);
  }

  public TestDictMetaData getMetaData(){
    return metaData;
  }

  public TestDictRow newSelf(){
    return new TestDictRow();
  }

  @ApiModelProperty(value="",example="0")
  public Long getDictId(){
    return (Long)get(TestDictMetaData.DictId);
  }

  public void setDictId(Long value){
    setNewValue(TestDictMetaData.DictId,value);
  }

  @ApiModelProperty("分类")
  public String getCategory(){
    return (String)get(TestDictMetaData.Category);
  }

  public void setCategory(String value){
    setNewValue(TestDictMetaData.Category,value);
  }

  @ApiModelProperty("名称或Key")
  public String getName(){
    return (String)get(TestDictMetaData.Name);
  }

  public void setName(String value){
    setNewValue(TestDictMetaData.Name,value);
  }

  @ApiModelProperty("内容或值")
  public String getContent(){
    return (String)get(TestDictMetaData.Content);
  }

  public void setContent(String value){
    setNewValue(TestDictMetaData.Content,value);
  }

  @ApiModelProperty(value="显示顺序",example="0")
  public Integer getSequence(){
    return (Integer)get(TestDictMetaData.Sequence);
  }

  public void setSequence(Integer value){
    setNewValue(TestDictMetaData.Sequence,value);
  }

  @ApiModelProperty("备注")
  public String getMemo(){
    return (String)get(TestDictMetaData.Memo);
  }

  public void setMemo(String value){
    setNewValue(TestDictMetaData.Memo,value);
  }

  @ApiModelProperty(value="中心ID",example="0")
  public Long getCenterId(){
    return (Long)get(TestDictMetaData.CenterId);
  }

  public void setCenterId(Long value){
    setNewValue(TestDictMetaData.CenterId,value);
  }

  @ApiModelProperty("")
  public Boolean getValid(){
    return (Boolean)get(TestDictMetaData.Valid);
  }

  public void setValid(Boolean value){
    setNewValue(TestDictMetaData.Valid,value);
  }

  public Date getCreateTime(){
    return (Date)get(TestDictMetaData.CreateTime);
  }

  public void setCreateTime(Date value){
    setNewValue(TestDictMetaData.CreateTime,value);
  }

  public Long getCreateUserId(){
    return (Long)get(TestDictMetaData.CreateUserId);
  }

  public void setCreateUserId(Long value){
    setNewValue(TestDictMetaData.CreateUserId,value);
  }

  public String getCreateUserName(){
    return (String)get(TestDictMetaData.CreateUserName);
  }

  public void setCreateUserName(String value){
    setNewValue(TestDictMetaData.CreateUserName,value);
  }

  public Date getModifyTime(){
    return (Date)get(TestDictMetaData.ModifyTime);
  }

  public void setModifyTime(Date value){
    setNewValue(TestDictMetaData.ModifyTime,value);
  }

  public Long getModifyUserId(){
    return (Long)get(TestDictMetaData.ModifyUserId);
  }

  public void setModifyUserId(Long value){
    setNewValue(TestDictMetaData.ModifyUserId,value);
  }

  public String getModifyUserName(){
    return (String)get(TestDictMetaData.ModifyUserName);
  }

  public void setModifyUserName(String value){
    setNewValue(TestDictMetaData.ModifyUserName,value);
  }

  public Date getDeleteTime(){
    return (Date)get(TestDictMetaData.DeleteTime);
  }

  public void setDeleteTime(Date value){
    setNewValue(TestDictMetaData.DeleteTime,value);
  }

  public Long getDeleteUserId(){
    return (Long)get(TestDictMetaData.DeleteUserId);
  }

  public void setDeleteUserId(Long value){
    setNewValue(TestDictMetaData.DeleteUserId,value);
  }

  public String getDeleteUserName(){
    return (String)get(TestDictMetaData.DeleteUserName);
  }

  public void setDeleteUserName(String value){
    setNewValue(TestDictMetaData.DeleteUserName,value);
  }

  public Integer getVersion(){
    return (Integer)get(TestDictMetaData.Version);
  }

  public void setVersion(Integer value){
    setNewValue(TestDictMetaData.Version,value);
  }

}
