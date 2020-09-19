package com.esdk.test.orm;
import java.sql.Connection;

import com.esdk.sql.orm.AbstractSelect;

import java.sql.SQLException;

import com.esdk.sql.ISQL;

import com.esdk.sql.Field;

import com.esdk.sql.SmartBetween;

import java.util.Date;

public class TestDictSelect extends AbstractSelect<TestDictSelect,TestDictRow> implements ISQL{
  public static final TestDictMetaData metaData=new TestDictMetaData();
  public static final TestDictMetaData md=metaData;
  public TestDictSelect(){
    super(TestDictMetaData.TABLENAME,false);
  }

  public TestDictSelect(Connection conn,boolean isTop){
    super(TestDictMetaData.TABLENAME,isTop,conn);
  }

  public TestDictSelect(Connection conn){
    super(TestDictMetaData.TABLENAME,conn);
  }

  public TestDictSelect(boolean isJoin){
    super(TestDictMetaData.TABLENAME,isJoin);
  }

  public TestDictSelect(String joinType){
    super(TestDictMetaData.TABLENAME,joinType);
  }

  public TestDictSelect(com.esdk.sql.orm.ORMSession ormsession,boolean isTop){
    super(TestDictMetaData.TABLENAME,isTop,ormsession);
  }

  public TestDictSelect(com.esdk.sql.orm.ORMSession ormsession){
    super(TestDictMetaData.TABLENAME,ormsession);
  }

  public TestDictMetaData getMetaData(){
    return metaData;
  }

  public TestDictRow[] toTestDictRowArray()throws Exception{
    return (TestDictRow[])list().toArray(new TestDictRow[0]);
  }

  public TestDictRow getFirstTestDictRow(){
    return (TestDictRow)getFirstRow();
  }

  public TestDictRow getFirstTestDictRow(boolean isCreateInstance){
    return (TestDictRow)getFirstRow(isCreateInstance);
  }

  public TestDictResultSet toTestDictResultSet()throws SQLException{
    return (TestDictResultSet)toParentResultSet();
  }

  public void setPrimaryKey(Object value){
    fieldMap.put(TestDictMetaData.PrimaryKey,value);
  }

  public Long getDictId(){
    return (Long)fieldMap.get(TestDictMetaData.DictId);
  }

  public TestDictSelect setDictId(Long value){
    fieldMap.put(TestDictMetaData.DictId,value);
    return this;
  }

  public TestDictSelect setDictId(Long[] values){
    super.addIn(TestDictMetaData.DictId,values);
    return this;
  }

  public TestDictSelect setDictId(AbstractSelect select){
    super.addIn(TestDictMetaData.DictId,select);
    return this;
  }

  public Field createDictIdField(){
    return this.createField(metaData.DictId);
  }

  public String getCategory(){
    return (String)fieldMap.get(TestDictMetaData.Category);
  }

  public TestDictSelect setCategory(String value){
    fieldMap.put(TestDictMetaData.Category,value);
    return this;
  }

  public TestDictSelect setCategory(String[] values){
    super.addIn(TestDictMetaData.Category,values);
    return this;
  }

  public TestDictSelect setCategory(AbstractSelect select){
    super.addIn(TestDictMetaData.Category,select);
    return this;
  }

  public TestDictSelect addLikeCategory(String value){
    addLikeCondition(metaData.Category,value);
    return this;
  }

  public Field createCategoryField(){
    return this.createField(metaData.Category);
  }

  public String getName(){
    return (String)fieldMap.get(TestDictMetaData.Name);
  }

  public TestDictSelect setName(String value){
    fieldMap.put(TestDictMetaData.Name,value);
    return this;
  }

  public TestDictSelect setName(String[] values){
    super.addIn(TestDictMetaData.Name,values);
    return this;
  }

  public TestDictSelect setName(AbstractSelect select){
    super.addIn(TestDictMetaData.Name,select);
    return this;
  }

  public TestDictSelect addLikeName(String value){
    addLikeCondition(metaData.Name,value);
    return this;
  }

  public Field createNameField(){
    return this.createField(metaData.Name);
  }

  public String getContent(){
    return (String)fieldMap.get(TestDictMetaData.Content);
  }

  public TestDictSelect setContent(String value){
    fieldMap.put(TestDictMetaData.Content,value);
    return this;
  }

  public TestDictSelect setContent(String[] values){
    super.addIn(TestDictMetaData.Content,values);
    return this;
  }

  public TestDictSelect setContent(AbstractSelect select){
    super.addIn(TestDictMetaData.Content,select);
    return this;
  }

  public TestDictSelect addLikeContent(String value){
    addLikeCondition(metaData.Content,value);
    return this;
  }

  public Field createContentField(){
    return this.createField(metaData.Content);
  }

  public Integer getSequence(){
    return (Integer)fieldMap.get(TestDictMetaData.Sequence);
  }

  public TestDictSelect setSequence(Integer value){
    fieldMap.put(TestDictMetaData.Sequence,value);
    return this;
  }

  public TestDictSelect setSequence(Integer[] values){
    super.addIn(TestDictMetaData.Sequence,values);
    return this;
  }

  public TestDictSelect setSequence(AbstractSelect select){
    super.addIn(TestDictMetaData.Sequence,select);
    return this;
  }

  public TestDictSelect setSequence(Integer start,Integer end){
    fieldMap.put(TestDictMetaData.Sequence,new SmartBetween(this.createField(TestDictMetaData.Sequence),start,end));
    return this;
  }

  public Field createSequenceField(){
    return this.createField(metaData.Sequence);
  }

  public String getMemo(){
    return (String)fieldMap.get(TestDictMetaData.Memo);
  }

  public TestDictSelect setMemo(String value){
    fieldMap.put(TestDictMetaData.Memo,value);
    return this;
  }

  public TestDictSelect setMemo(String[] values){
    super.addIn(TestDictMetaData.Memo,values);
    return this;
  }

  public TestDictSelect setMemo(AbstractSelect select){
    super.addIn(TestDictMetaData.Memo,select);
    return this;
  }

  public TestDictSelect addLikeMemo(String value){
    addLikeCondition(metaData.Memo,value);
    return this;
  }

  public Field createMemoField(){
    return this.createField(metaData.Memo);
  }

  public Long getCenterId(){
    return (Long)fieldMap.get(TestDictMetaData.CenterId);
  }

  public TestDictSelect setCenterId(Long value){
    fieldMap.put(TestDictMetaData.CenterId,value);
    return this;
  }

  public TestDictSelect setCenterId(Long[] values){
    super.addIn(TestDictMetaData.CenterId,values);
    return this;
  }

  public TestDictSelect setCenterId(AbstractSelect select){
    super.addIn(TestDictMetaData.CenterId,select);
    return this;
  }

  public Field createCenterIdField(){
    return this.createField(metaData.CenterId);
  }

  public Boolean getValid(){
    return (Boolean)fieldMap.get(TestDictMetaData.Valid);
  }

  public TestDictSelect setValid(Boolean value){
    fieldMap.put(TestDictMetaData.Valid,value);
    return this;
  }

  public Field createValidField(){
    return this.createField(metaData.Valid);
  }

  public Date getCreateTime(){
    return (Date)fieldMap.get(TestDictMetaData.CreateTime);
  }

  public TestDictSelect setCreateTime(Date value){
    fieldMap.put(TestDictMetaData.CreateTime,value);
    return this;
  }

  public TestDictSelect setCreateTime(Date start,Date end){
    fieldMap.put(TestDictMetaData.CreateTime,new SmartBetween(this.createField(TestDictMetaData.CreateTime),start,end));
    return this;
  }

  public Field createCreateTimeField(){
    return this.createField(metaData.CreateTime);
  }

  public Long getCreateUserId(){
    return (Long)fieldMap.get(TestDictMetaData.CreateUserId);
  }

  public TestDictSelect setCreateUserId(Long value){
    fieldMap.put(TestDictMetaData.CreateUserId,value);
    return this;
  }

  public TestDictSelect setCreateUserId(Long[] values){
    super.addIn(TestDictMetaData.CreateUserId,values);
    return this;
  }

  public TestDictSelect setCreateUserId(AbstractSelect select){
    super.addIn(TestDictMetaData.CreateUserId,select);
    return this;
  }

  public Field createCreateUserIdField(){
    return this.createField(metaData.CreateUserId);
  }

  public String getCreateUserName(){
    return (String)fieldMap.get(TestDictMetaData.CreateUserName);
  }

  public TestDictSelect setCreateUserName(String value){
    fieldMap.put(TestDictMetaData.CreateUserName,value);
    return this;
  }

  public TestDictSelect setCreateUserName(String[] values){
    super.addIn(TestDictMetaData.CreateUserName,values);
    return this;
  }

  public TestDictSelect setCreateUserName(AbstractSelect select){
    super.addIn(TestDictMetaData.CreateUserName,select);
    return this;
  }

  public TestDictSelect addLikeCreateUserName(String value){
    addLikeCondition(metaData.CreateUserName,value);
    return this;
  }

  public Field createCreateUserNameField(){
    return this.createField(metaData.CreateUserName);
  }

  public Date getModifyTime(){
    return (Date)fieldMap.get(TestDictMetaData.ModifyTime);
  }

  public TestDictSelect setModifyTime(Date value){
    fieldMap.put(TestDictMetaData.ModifyTime,value);
    return this;
  }

  public TestDictSelect setModifyTime(Date start,Date end){
    fieldMap.put(TestDictMetaData.ModifyTime,new SmartBetween(this.createField(TestDictMetaData.ModifyTime),start,end));
    return this;
  }

  public Field createModifyTimeField(){
    return this.createField(metaData.ModifyTime);
  }

  public Long getModifyUserId(){
    return (Long)fieldMap.get(TestDictMetaData.ModifyUserId);
  }

  public TestDictSelect setModifyUserId(Long value){
    fieldMap.put(TestDictMetaData.ModifyUserId,value);
    return this;
  }

  public TestDictSelect setModifyUserId(Long[] values){
    super.addIn(TestDictMetaData.ModifyUserId,values);
    return this;
  }

  public TestDictSelect setModifyUserId(AbstractSelect select){
    super.addIn(TestDictMetaData.ModifyUserId,select);
    return this;
  }

  public Field createModifyUserIdField(){
    return this.createField(metaData.ModifyUserId);
  }

  public String getModifyUserName(){
    return (String)fieldMap.get(TestDictMetaData.ModifyUserName);
  }

  public TestDictSelect setModifyUserName(String value){
    fieldMap.put(TestDictMetaData.ModifyUserName,value);
    return this;
  }

  public TestDictSelect setModifyUserName(String[] values){
    super.addIn(TestDictMetaData.ModifyUserName,values);
    return this;
  }

  public TestDictSelect setModifyUserName(AbstractSelect select){
    super.addIn(TestDictMetaData.ModifyUserName,select);
    return this;
  }

  public TestDictSelect addLikeModifyUserName(String value){
    addLikeCondition(metaData.ModifyUserName,value);
    return this;
  }

  public Field createModifyUserNameField(){
    return this.createField(metaData.ModifyUserName);
  }

  public Date getDeleteTime(){
    return (Date)fieldMap.get(TestDictMetaData.DeleteTime);
  }

  public TestDictSelect setDeleteTime(Date value){
    fieldMap.put(TestDictMetaData.DeleteTime,value);
    return this;
  }

  public TestDictSelect setDeleteTime(Date start,Date end){
    fieldMap.put(TestDictMetaData.DeleteTime,new SmartBetween(this.createField(TestDictMetaData.DeleteTime),start,end));
    return this;
  }

  public Field createDeleteTimeField(){
    return this.createField(metaData.DeleteTime);
  }

  public Long getDeleteUserId(){
    return (Long)fieldMap.get(TestDictMetaData.DeleteUserId);
  }

  public TestDictSelect setDeleteUserId(Long value){
    fieldMap.put(TestDictMetaData.DeleteUserId,value);
    return this;
  }

  public TestDictSelect setDeleteUserId(Long[] values){
    super.addIn(TestDictMetaData.DeleteUserId,values);
    return this;
  }

  public TestDictSelect setDeleteUserId(AbstractSelect select){
    super.addIn(TestDictMetaData.DeleteUserId,select);
    return this;
  }

  public Field createDeleteUserIdField(){
    return this.createField(metaData.DeleteUserId);
  }

  public String getDeleteUserName(){
    return (String)fieldMap.get(TestDictMetaData.DeleteUserName);
  }

  public TestDictSelect setDeleteUserName(String value){
    fieldMap.put(TestDictMetaData.DeleteUserName,value);
    return this;
  }

  public TestDictSelect setDeleteUserName(String[] values){
    super.addIn(TestDictMetaData.DeleteUserName,values);
    return this;
  }

  public TestDictSelect setDeleteUserName(AbstractSelect select){
    super.addIn(TestDictMetaData.DeleteUserName,select);
    return this;
  }

  public TestDictSelect addLikeDeleteUserName(String value){
    addLikeCondition(metaData.DeleteUserName,value);
    return this;
  }

  public Field createDeleteUserNameField(){
    return this.createField(metaData.DeleteUserName);
  }

  public Integer getVersion(){
    return (Integer)fieldMap.get(TestDictMetaData.Version);
  }

  public TestDictSelect setVersion(Integer value){
    fieldMap.put(TestDictMetaData.Version,value);
    return this;
  }

  public TestDictSelect setVersion(Integer[] values){
    super.addIn(TestDictMetaData.Version,values);
    return this;
  }

  public TestDictSelect setVersion(AbstractSelect select){
    super.addIn(TestDictMetaData.Version,select);
    return this;
  }

  public TestDictSelect setVersion(Integer start,Integer end){
    fieldMap.put(TestDictMetaData.Version,new SmartBetween(this.createField(TestDictMetaData.Version),start,end));
    return this;
  }

  public Field createVersionField(){
    return this.createField(metaData.Version);
  }

}
