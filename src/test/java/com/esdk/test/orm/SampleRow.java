package com.esdk.test.orm;
import com.esdk.sql.orm.ParentRow;

import java.sql.SQLException;

import java.sql.Connection;

import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

 @ApiModel("测试表")
public class SampleRow extends ParentRow<SampleRow>{
  public transient static final String PrimaryKey=SampleMetaData.PrimaryKey;
  public transient static final String tableName=SampleMetaData.TABLENAME;
  public transient static final SampleMetaData metaData=SampleSelect.metaData;
  public transient static final SampleMetaData md=metaData;
  public SampleRow(){
    super();
    setAutoIncrement(SampleMetaData.IsAutoIncrement);
  }

  public SampleRow(Connection conn){
    this();
    setConnection(conn);
  }

  public SampleRow(Connection conn,Number pkid)throws SQLException{
    this();
    setConnection(conn);
    refresh(pkid);
  }

  public SampleMetaData getMetaData(){
    return metaData;
  }

  public SampleRow newSelf(){
    return new SampleRow();
  }

  @ApiModelProperty(value="",example="0")
  public Long getDictId(){
    return (Long)get(SampleMetaData.DictId);
  }

  public void setDictId(Long value){
    setNewValue(SampleMetaData.DictId,value);
  }

  @ApiModelProperty("分类")
  public String getCategory(){
    return (String)get(SampleMetaData.Category);
  }

  public void setCategory(String value){
    setNewValue(SampleMetaData.Category,value);
  }

  @ApiModelProperty("名称或Key")
  public String getName(){
    return (String)get(SampleMetaData.Name);
  }

  public void setName(String value){
    setNewValue(SampleMetaData.Name,value);
  }

  @ApiModelProperty("内容或值")
  public String getContent(){
    return (String)get(SampleMetaData.Content);
  }

  public void setContent(String value){
    setNewValue(SampleMetaData.Content,value);
  }

  @ApiModelProperty(value="显示顺序",example="0")
  public Integer getSequence(){
    return (Integer)get(SampleMetaData.Sequence);
  }

  public void setSequence(Integer value){
    setNewValue(SampleMetaData.Sequence,value);
  }

  @ApiModelProperty("备注")
  public String getMemo(){
    return (String)get(SampleMetaData.Memo);
  }

  public void setMemo(String value){
    setNewValue(SampleMetaData.Memo,value);
  }

  @ApiModelProperty(value="中心ID",example="0")
  public Long getCenterId(){
    return (Long)get(SampleMetaData.CenterId);
  }

  public void setCenterId(Long value){
    setNewValue(SampleMetaData.CenterId,value);
  }

  @ApiModelProperty("")
  public Boolean getValid(){
    return (Boolean)get(SampleMetaData.Valid);
  }

  public void setValid(Boolean value){
    setNewValue(SampleMetaData.Valid,value);
  }

  public Date getCreateTime(){
    return (Date)get(SampleMetaData.CreateTime);
  }

  public void setCreateTime(Date value){
    setNewValue(SampleMetaData.CreateTime,value);
  }

  public Long getCreateUserId(){
    return (Long)get(SampleMetaData.CreateUserId);
  }

  public void setCreateUserId(Long value){
    setNewValue(SampleMetaData.CreateUserId,value);
  }

  public String getCreateUserName(){
    return (String)get(SampleMetaData.CreateUserName);
  }

  public void setCreateUserName(String value){
    setNewValue(SampleMetaData.CreateUserName,value);
  }

  public Date getModifyTime(){
    return (Date)get(SampleMetaData.ModifyTime);
  }

  public void setModifyTime(Date value){
    setNewValue(SampleMetaData.ModifyTime,value);
  }

  public Long getModifyUserId(){
    return (Long)get(SampleMetaData.ModifyUserId);
  }

  public void setModifyUserId(Long value){
    setNewValue(SampleMetaData.ModifyUserId,value);
  }

  public String getModifyUserName(){
    return (String)get(SampleMetaData.ModifyUserName);
  }

  public void setModifyUserName(String value){
    setNewValue(SampleMetaData.ModifyUserName,value);
  }

  public Date getDeleteTime(){
    return (Date)get(SampleMetaData.DeleteTime);
  }

  public void setDeleteTime(Date value){
    setNewValue(SampleMetaData.DeleteTime,value);
  }

  public Long getDeleteUserId(){
    return (Long)get(SampleMetaData.DeleteUserId);
  }

  public void setDeleteUserId(Long value){
    setNewValue(SampleMetaData.DeleteUserId,value);
  }

  public String getDeleteUserName(){
    return (String)get(SampleMetaData.DeleteUserName);
  }

  public void setDeleteUserName(String value){
    setNewValue(SampleMetaData.DeleteUserName,value);
  }

  public Integer getVersion(){
    return (Integer)get(SampleMetaData.Version);
  }

  public void setVersion(Integer value){
    setNewValue(SampleMetaData.Version,value);
  }

}
