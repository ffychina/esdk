package com.esdk.test.orm;
import com.esdk.sql.orm.ParentRow;

import java.sql.SQLException;

import java.sql.Connection;

import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

 @ApiModel("系统用户表")
public class TestUserRow extends ParentRow<TestUserRow>{
  public transient static final String PrimaryKey=TestUserMetaData.PrimaryKey;
  public transient static final String tableName=TestUserMetaData.TABLENAME;
  public transient static final TestUserMetaData metaData=TestUserSelect.metaData;
  public transient static final TestUserMetaData md=metaData;
  public TestUserRow(){
    super();
    setAutoIncrement(TestUserMetaData.IsAutoIncrement);
  }

  public TestUserRow(Connection conn){
    this();
    setConnection(conn);
  }

  public TestUserRow(Connection conn,Number pkid)throws SQLException{
    this();
    setConnection(conn);
    refresh(pkid);
  }

  public TestUserMetaData getMetaData(){
    return metaData;
  }

  public TestUserRow newSelf(){
    return new TestUserRow();
  }

  @ApiModelProperty(value="",example="0")
  public Long getUserId(){
    return (Long)get(TestUserMetaData.UserId);
  }

  public void setUserId(Long value){
    setNewValue(TestUserMetaData.UserId,value);
  }

  @ApiModelProperty("用户编号")
  public String getUserCode(){
    return (String)get(TestUserMetaData.UserCode);
  }

  public void setUserCode(String value){
    setNewValue(TestUserMetaData.UserCode,value);
  }

  @ApiModelProperty("用户姓名")
  public String getUserName(){
    return (String)get(TestUserMetaData.UserName);
  }

  public void setUserName(String value){
    setNewValue(TestUserMetaData.UserName,value);
  }

  @ApiModelProperty("用户账号")
  public String getUserAccount(){
    return (String)get(TestUserMetaData.UserAccount);
  }

  public void setUserAccount(String value){
    setNewValue(TestUserMetaData.UserAccount,value);
  }

  @ApiModelProperty("用户别名")
  public String getUserAlias(){
    return (String)get(TestUserMetaData.UserAlias);
  }

  public void setUserAlias(String value){
    setNewValue(TestUserMetaData.UserAlias,value);
  }

  @ApiModelProperty("联系电话")
  public String getTel(){
    return (String)get(TestUserMetaData.Tel);
  }

  public void setTel(String value){
    setNewValue(TestUserMetaData.Tel,value);
  }

  @ApiModelProperty("移动电话")
  public String getMobile(){
    return (String)get(TestUserMetaData.Mobile);
  }

  public void setMobile(String value){
    setNewValue(TestUserMetaData.Mobile,value);
  }

  @ApiModelProperty("电子邮箱")
  public String getEmail(){
    return (String)get(TestUserMetaData.Email);
  }

  public void setEmail(String value){
    setNewValue(TestUserMetaData.Email,value);
  }

  @ApiModelProperty("密码")
  public String getPassword(){
    return (String)get(TestUserMetaData.Password);
  }

  public void setPassword(String value){
    setNewValue(TestUserMetaData.Password,value);
  }

  @ApiModelProperty("备注")
  public String getRemark(){
    return (String)get(TestUserMetaData.Remark);
  }

  public void setRemark(String value){
    setNewValue(TestUserMetaData.Remark,value);
  }

  @ApiModelProperty("照片URL")
  public String getPhotoUrl(){
    return (String)get(TestUserMetaData.PhotoUrl);
  }

  public void setPhotoUrl(String value){
    setNewValue(TestUserMetaData.PhotoUrl,value);
  }

  @ApiModelProperty(value="所属中心",example="0")
  public Long getCenterId(){
    return (Long)get(TestUserMetaData.CenterId);
  }

  public void setCenterId(Long value){
    setNewValue(TestUserMetaData.CenterId,value);
  }

  @ApiModelProperty(value="登录中心",example="0")
  public Long getLoginCenterId(){
    return (Long)get(TestUserMetaData.LoginCenterId);
  }

  public void setLoginCenterId(Long value){
    setNewValue(TestUserMetaData.LoginCenterId,value);
  }

  @ApiModelProperty("可登录中心集")
  public String getCenterIds(){
    return (String)get(TestUserMetaData.CenterIds);
  }

  public void setCenterIds(String value){
    setNewValue(TestUserMetaData.CenterIds,value);
  }

  @ApiModelProperty("可登录中心名称集")
  public String getCenterNames(){
    return (String)get(TestUserMetaData.CenterNames);
  }

  public void setCenterNames(String value){
    setNewValue(TestUserMetaData.CenterNames,value);
  }

  @ApiModelProperty(value="所属机构",example="0")
  public Long getOrgId(){
    return (Long)get(TestUserMetaData.OrgId);
  }

  public void setOrgId(Long value){
    setNewValue(TestUserMetaData.OrgId,value);
  }

  @ApiModelProperty(value="所属部门",example="0")
  public Long getDeptId(){
    return (Long)get(TestUserMetaData.DeptId);
  }

  public void setDeptId(Long value){
    setNewValue(TestUserMetaData.DeptId,value);
  }

  @ApiModelProperty(value="角色",example="0")
  public Long getRoleId(){
    return (Long)get(TestUserMetaData.RoleId);
  }

  public void setRoleId(Long value){
    setNewValue(TestUserMetaData.RoleId,value);
  }

  @ApiModelProperty("所拥有角色")
  public String getRoleIds(){
    return (String)get(TestUserMetaData.RoleIds);
  }

  public void setRoleIds(String value){
    setNewValue(TestUserMetaData.RoleIds,value);
  }

  @ApiModelProperty("所拥有角色名称")
  public String getRoleNames(){
    return (String)get(TestUserMetaData.RoleNames);
  }

  public void setRoleNames(String value){
    setNewValue(TestUserMetaData.RoleNames,value);
  }

  @ApiModelProperty("职务级别")
  public String getRank(){
    return (String)get(TestUserMetaData.Rank);
  }

  public void setRank(String value){
    setNewValue(TestUserMetaData.Rank,value);
  }

  @ApiModelProperty("是否已冻结")
  public Boolean getFrozen(){
    return (Boolean)get(TestUserMetaData.Frozen);
  }

  public void setFrozen(Boolean value){
    setNewValue(TestUserMetaData.Frozen,value);
  }

  @ApiModelProperty("")
  public Boolean getValid(){
    return (Boolean)get(TestUserMetaData.Valid);
  }

  public void setValid(Boolean value){
    setNewValue(TestUserMetaData.Valid,value);
  }

  public Date getCreateTime(){
    return (Date)get(TestUserMetaData.CreateTime);
  }

  public void setCreateTime(Date value){
    setNewValue(TestUserMetaData.CreateTime,value);
  }

  public Long getCreateUserId(){
    return (Long)get(TestUserMetaData.CreateUserId);
  }

  public void setCreateUserId(Long value){
    setNewValue(TestUserMetaData.CreateUserId,value);
  }

  public String getCreateUserName(){
    return (String)get(TestUserMetaData.CreateUserName);
  }

  public void setCreateUserName(String value){
    setNewValue(TestUserMetaData.CreateUserName,value);
  }

  public Date getModifyTime(){
    return (Date)get(TestUserMetaData.ModifyTime);
  }

  public void setModifyTime(Date value){
    setNewValue(TestUserMetaData.ModifyTime,value);
  }

  public Long getModifyUserId(){
    return (Long)get(TestUserMetaData.ModifyUserId);
  }

  public void setModifyUserId(Long value){
    setNewValue(TestUserMetaData.ModifyUserId,value);
  }

  public String getModifyUserName(){
    return (String)get(TestUserMetaData.ModifyUserName);
  }

  public void setModifyUserName(String value){
    setNewValue(TestUserMetaData.ModifyUserName,value);
  }

  public Date getDeleteTime(){
    return (Date)get(TestUserMetaData.DeleteTime);
  }

  public void setDeleteTime(Date value){
    setNewValue(TestUserMetaData.DeleteTime,value);
  }

  public Long getDeleteUserId(){
    return (Long)get(TestUserMetaData.DeleteUserId);
  }

  public void setDeleteUserId(Long value){
    setNewValue(TestUserMetaData.DeleteUserId,value);
  }

  public String getDeleteUserName(){
    return (String)get(TestUserMetaData.DeleteUserName);
  }

  public void setDeleteUserName(String value){
    setNewValue(TestUserMetaData.DeleteUserName,value);
  }

  public Integer getVersion(){
    return (Integer)get(TestUserMetaData.Version);
  }

  public void setVersion(Integer value){
    setNewValue(TestUserMetaData.Version,value);
  }

}
