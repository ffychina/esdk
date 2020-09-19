package com.esdk.test.orm;
import com.esdk.sql.orm.ParentRow;

import java.sql.SQLException;

import java.sql.Connection;

import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

 @ApiModel("系統中心表")
public class TestCenterRow extends ParentRow<TestCenterRow>{
  public transient static final String PrimaryKey=TestCenterMetaData.PrimaryKey;
  public transient static final String tableName=TestCenterMetaData.TABLENAME;
  public transient static final TestCenterMetaData metaData=TestCenterSelect.metaData;
  public transient static final TestCenterMetaData md=metaData;
  public TestCenterRow(){
    super();
    setAutoIncrement(TestCenterMetaData.IsAutoIncrement);
  }

  public TestCenterRow(Connection conn){
    this();
    setConnection(conn);
  }

  public TestCenterRow(Connection conn,Number pkid)throws SQLException{
    this();
    setConnection(conn);
    refresh(pkid);
  }

  public TestCenterMetaData getMetaData(){
    return metaData;
  }

  public TestCenterRow newSelf(){
    return new TestCenterRow();
  }

  @ApiModelProperty(value="",example="0")
  public Integer getCenterId(){
    return (Integer)get(TestCenterMetaData.CenterId);
  }

  public void setCenterId(Integer value){
    setNewValue(TestCenterMetaData.CenterId,value);
  }

  @ApiModelProperty("中心编号")
  public String getCode(){
    return (String)get(TestCenterMetaData.Code);
  }

  public void setCode(String value){
    setNewValue(TestCenterMetaData.Code,value);
  }

  @ApiModelProperty("中心名称")
  public String getName(){
    return (String)get(TestCenterMetaData.Name);
  }

  public void setName(String value){
    setNewValue(TestCenterMetaData.Name,value);
  }

  @ApiModelProperty("英文名称")
  public String getEnglishName(){
    return (String)get(TestCenterMetaData.EnglishName);
  }

  public void setEnglishName(String value){
    setNewValue(TestCenterMetaData.EnglishName,value);
  }

  @ApiModelProperty("中心简称")
  public String getAbbr(){
    return (String)get(TestCenterMetaData.Abbr);
  }

  public void setAbbr(String value){
    setNewValue(TestCenterMetaData.Abbr,value);
  }

  @ApiModelProperty("中心电话")
  public String getTel(){
    return (String)get(TestCenterMetaData.Tel);
  }

  public void setTel(String value){
    setNewValue(TestCenterMetaData.Tel,value);
  }

  @ApiModelProperty("中心传真")
  public String getFax(){
    return (String)get(TestCenterMetaData.Fax);
  }

  public void setFax(String value){
    setNewValue(TestCenterMetaData.Fax,value);
  }

  @ApiModelProperty("中心地址")
  public String getAddress(){
    return (String)get(TestCenterMetaData.Address);
  }

  public void setAddress(String value){
    setNewValue(TestCenterMetaData.Address,value);
  }

  @ApiModelProperty("中心所属区域")
  public String getRegion(){
    return (String)get(TestCenterMetaData.Region);
  }

  public void setRegion(String value){
    setNewValue(TestCenterMetaData.Region,value);
  }

  @ApiModelProperty("电邮地址")
  public String getEmail(){
    return (String)get(TestCenterMetaData.Email);
  }

  public void setEmail(String value){
    setNewValue(TestCenterMetaData.Email,value);
  }

  @ApiModelProperty("联络人")
  public String getContact(){
    return (String)get(TestCenterMetaData.Contact);
  }

  public void setContact(String value){
    setNewValue(TestCenterMetaData.Contact,value);
  }

  @ApiModelProperty("类别")
  public String getType(){
    return (String)get(TestCenterMetaData.Type);
  }

  public void setType(String value){
    setNewValue(TestCenterMetaData.Type,value);
  }

  @ApiModelProperty("备注")
  public String getRemark(){
    return (String)get(TestCenterMetaData.Remark);
  }

  public void setRemark(String value){
    setNewValue(TestCenterMetaData.Remark,value);
  }

  @ApiModelProperty("网站LOGO")
  public String getLogoUrl(){
    return (String)get(TestCenterMetaData.LogoUrl);
  }

  public void setLogoUrl(String value){
    setNewValue(TestCenterMetaData.LogoUrl,value);
  }

  @ApiModelProperty("移动端应用LOGO")
  public String getAppLogoUrl(){
    return (String)get(TestCenterMetaData.AppLogoUrl);
  }

  public void setAppLogoUrl(String value){
    setNewValue(TestCenterMetaData.AppLogoUrl,value);
  }

  @ApiModelProperty("")
  public Boolean getValid(){
    return (Boolean)get(TestCenterMetaData.Valid);
  }

  public void setValid(Boolean value){
    setNewValue(TestCenterMetaData.Valid,value);
  }

  public Date getCreateTime(){
    return (Date)get(TestCenterMetaData.CreateTime);
  }

  public void setCreateTime(Date value){
    setNewValue(TestCenterMetaData.CreateTime,value);
  }

  public Integer getCreateUserId(){
    return (Integer)get(TestCenterMetaData.CreateUserId);
  }

  public void setCreateUserId(Integer value){
    setNewValue(TestCenterMetaData.CreateUserId,value);
  }

  public String getCreateUserName(){
    return (String)get(TestCenterMetaData.CreateUserName);
  }

  public void setCreateUserName(String value){
    setNewValue(TestCenterMetaData.CreateUserName,value);
  }

  public Date getModifyTime(){
    return (Date)get(TestCenterMetaData.ModifyTime);
  }

  public void setModifyTime(Date value){
    setNewValue(TestCenterMetaData.ModifyTime,value);
  }

  public Integer getModifyUserId(){
    return (Integer)get(TestCenterMetaData.ModifyUserId);
  }

  public void setModifyUserId(Integer value){
    setNewValue(TestCenterMetaData.ModifyUserId,value);
  }

  public String getModifyUserName(){
    return (String)get(TestCenterMetaData.ModifyUserName);
  }

  public void setModifyUserName(String value){
    setNewValue(TestCenterMetaData.ModifyUserName,value);
  }

  public Date getDeleteTime(){
    return (Date)get(TestCenterMetaData.DeleteTime);
  }

  public void setDeleteTime(Date value){
    setNewValue(TestCenterMetaData.DeleteTime,value);
  }

  public Integer getDeleteUserId(){
    return (Integer)get(TestCenterMetaData.DeleteUserId);
  }

  public void setDeleteUserId(Integer value){
    setNewValue(TestCenterMetaData.DeleteUserId,value);
  }

  public String getDeleteUserName(){
    return (String)get(TestCenterMetaData.DeleteUserName);
  }

  public void setDeleteUserName(String value){
    setNewValue(TestCenterMetaData.DeleteUserName,value);
  }

  public Integer getVersion(){
    return (Integer)get(TestCenterMetaData.Version);
  }

  public void setVersion(Integer value){
    setNewValue(TestCenterMetaData.Version,value);
  }

}
