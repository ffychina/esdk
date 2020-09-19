package com.esdk.test.orm;
import java.sql.Connection;

import com.esdk.sql.orm.AbstractSelect;

import java.sql.SQLException;

import com.esdk.sql.ISQL;

import com.esdk.sql.Field;

import com.esdk.sql.SmartBetween;

import java.util.Date;

public class SampleSelect extends AbstractSelect<SampleSelect,SampleRow> implements ISQL{
  public static final SampleMetaData metaData=new SampleMetaData();
  public static final SampleMetaData md=metaData;
  public SampleSelect(){
    super(SampleMetaData.TABLENAME,false);
  }

  public SampleSelect(Connection conn,boolean isTop){
    super(SampleMetaData.TABLENAME,isTop,conn);
  }

  public SampleSelect(Connection conn){
    super(SampleMetaData.TABLENAME,conn);
  }

  public SampleSelect(boolean isJoin){
    super(SampleMetaData.TABLENAME,isJoin);
  }

  public SampleSelect(String joinType){
    super(SampleMetaData.TABLENAME,joinType);
  }

  public SampleSelect(com.esdk.sql.orm.ORMSession ormsession,boolean isTop){
    super(SampleMetaData.TABLENAME,isTop,ormsession);
  }

  public SampleSelect(com.esdk.sql.orm.ORMSession ormsession){
    super(SampleMetaData.TABLENAME,ormsession);
  }

  public SampleMetaData getMetaData(){
    return metaData;
  }

  public SampleRow[] toSampleRowArray()throws Exception{
    return (SampleRow[])list().toArray(new SampleRow[0]);
  }

  public SampleRow getFirstSampleRow(){
    return (SampleRow)getFirstRow();
  }

  public SampleRow getFirstSampleRow(boolean isCreateInstance){
    return (SampleRow)getFirstRow(isCreateInstance);
  }

  public SampleResultSet toSampleResultSet()throws SQLException{
    return (SampleResultSet)toParentResultSet();
  }

  public void setPrimaryKey(Object value){
    fieldMap.put(SampleMetaData.PrimaryKey,value);
  }

  public Long getDictId(){
    return (Long)fieldMap.get(SampleMetaData.DictId);
  }

  public SampleSelect setDictId(Long value){
    fieldMap.put(SampleMetaData.DictId,value);
    return this;
  }

  public SampleSelect setDictId(Long[] values){
    super.addIn(SampleMetaData.DictId,values);
    return this;
  }

  public SampleSelect setDictId(AbstractSelect select){
    super.addIn(SampleMetaData.DictId,select);
    return this;
  }

  public Field createDictIdField(){
    return this.createField(metaData.DictId);
  }

  public String getCategory(){
    return (String)fieldMap.get(SampleMetaData.Category);
  }

  public SampleSelect setCategory(String value){
    fieldMap.put(SampleMetaData.Category,value);
    return this;
  }

  public SampleSelect setCategory(String[] values){
    super.addIn(SampleMetaData.Category,values);
    return this;
  }

  public SampleSelect setCategory(AbstractSelect select){
    super.addIn(SampleMetaData.Category,select);
    return this;
  }

  public SampleSelect addLikeCategory(String value){
    addLikeCondition(metaData.Category,value);
    return this;
  }

  public Field createCategoryField(){
    return this.createField(metaData.Category);
  }

  public String getName(){
    return (String)fieldMap.get(SampleMetaData.Name);
  }

  public SampleSelect setName(String value){
    fieldMap.put(SampleMetaData.Name,value);
    return this;
  }

  public SampleSelect setName(String[] values){
    super.addIn(SampleMetaData.Name,values);
    return this;
  }

  public SampleSelect setName(AbstractSelect select){
    super.addIn(SampleMetaData.Name,select);
    return this;
  }

  public SampleSelect addLikeName(String value){
    addLikeCondition(metaData.Name,value);
    return this;
  }

  public Field createNameField(){
    return this.createField(metaData.Name);
  }

  public String getContent(){
    return (String)fieldMap.get(SampleMetaData.Content);
  }

  public SampleSelect setContent(String value){
    fieldMap.put(SampleMetaData.Content,value);
    return this;
  }

  public SampleSelect setContent(String[] values){
    super.addIn(SampleMetaData.Content,values);
    return this;
  }

  public SampleSelect setContent(AbstractSelect select){
    super.addIn(SampleMetaData.Content,select);
    return this;
  }

  public SampleSelect addLikeContent(String value){
    addLikeCondition(metaData.Content,value);
    return this;
  }

  public Field createContentField(){
    return this.createField(metaData.Content);
  }

  public Integer getSequence(){
    return (Integer)fieldMap.get(SampleMetaData.Sequence);
  }

  public SampleSelect setSequence(Integer value){
    fieldMap.put(SampleMetaData.Sequence,value);
    return this;
  }

  public SampleSelect setSequence(Integer[] values){
    super.addIn(SampleMetaData.Sequence,values);
    return this;
  }

  public SampleSelect setSequence(AbstractSelect select){
    super.addIn(SampleMetaData.Sequence,select);
    return this;
  }

  public SampleSelect setSequence(Integer start,Integer end){
    fieldMap.put(SampleMetaData.Sequence,new SmartBetween(this.createField(SampleMetaData.Sequence),start,end));
    return this;
  }

  public Field createSequenceField(){
    return this.createField(metaData.Sequence);
  }

  public String getMemo(){
    return (String)fieldMap.get(SampleMetaData.Memo);
  }

  public SampleSelect setMemo(String value){
    fieldMap.put(SampleMetaData.Memo,value);
    return this;
  }

  public SampleSelect setMemo(String[] values){
    super.addIn(SampleMetaData.Memo,values);
    return this;
  }

  public SampleSelect setMemo(AbstractSelect select){
    super.addIn(SampleMetaData.Memo,select);
    return this;
  }

  public SampleSelect addLikeMemo(String value){
    addLikeCondition(metaData.Memo,value);
    return this;
  }

  public Field createMemoField(){
    return this.createField(metaData.Memo);
  }

  public Long getCenterId(){
    return (Long)fieldMap.get(SampleMetaData.CenterId);
  }

  public SampleSelect setCenterId(Long value){
    fieldMap.put(SampleMetaData.CenterId,value);
    return this;
  }

  public SampleSelect setCenterId(Long[] values){
    super.addIn(SampleMetaData.CenterId,values);
    return this;
  }

  public SampleSelect setCenterId(AbstractSelect select){
    super.addIn(SampleMetaData.CenterId,select);
    return this;
  }

  public Field createCenterIdField(){
    return this.createField(metaData.CenterId);
  }

  public Boolean getValid(){
    return (Boolean)fieldMap.get(SampleMetaData.Valid);
  }

  public SampleSelect setValid(Boolean value){
    fieldMap.put(SampleMetaData.Valid,value);
    return this;
  }

  public Field createValidField(){
    return this.createField(metaData.Valid);
  }

  public Date getCreateTime(){
    return (Date)fieldMap.get(SampleMetaData.CreateTime);
  }

  public SampleSelect setCreateTime(Date value){
    fieldMap.put(SampleMetaData.CreateTime,value);
    return this;
  }

  public SampleSelect setCreateTime(Date start,Date end){
    fieldMap.put(SampleMetaData.CreateTime,new SmartBetween(this.createField(SampleMetaData.CreateTime),start,end));
    return this;
  }

  public Field createCreateTimeField(){
    return this.createField(metaData.CreateTime);
  }

  public Long getCreateUserId(){
    return (Long)fieldMap.get(SampleMetaData.CreateUserId);
  }

  public SampleSelect setCreateUserId(Long value){
    fieldMap.put(SampleMetaData.CreateUserId,value);
    return this;
  }

  public SampleSelect setCreateUserId(Long[] values){
    super.addIn(SampleMetaData.CreateUserId,values);
    return this;
  }

  public SampleSelect setCreateUserId(AbstractSelect select){
    super.addIn(SampleMetaData.CreateUserId,select);
    return this;
  }

  public Field createCreateUserIdField(){
    return this.createField(metaData.CreateUserId);
  }

  public String getCreateUserName(){
    return (String)fieldMap.get(SampleMetaData.CreateUserName);
  }

  public SampleSelect setCreateUserName(String value){
    fieldMap.put(SampleMetaData.CreateUserName,value);
    return this;
  }

  public SampleSelect setCreateUserName(String[] values){
    super.addIn(SampleMetaData.CreateUserName,values);
    return this;
  }

  public SampleSelect setCreateUserName(AbstractSelect select){
    super.addIn(SampleMetaData.CreateUserName,select);
    return this;
  }

  public SampleSelect addLikeCreateUserName(String value){
    addLikeCondition(metaData.CreateUserName,value);
    return this;
  }

  public Field createCreateUserNameField(){
    return this.createField(metaData.CreateUserName);
  }

  public Date getModifyTime(){
    return (Date)fieldMap.get(SampleMetaData.ModifyTime);
  }

  public SampleSelect setModifyTime(Date value){
    fieldMap.put(SampleMetaData.ModifyTime,value);
    return this;
  }

  public SampleSelect setModifyTime(Date start,Date end){
    fieldMap.put(SampleMetaData.ModifyTime,new SmartBetween(this.createField(SampleMetaData.ModifyTime),start,end));
    return this;
  }

  public Field createModifyTimeField(){
    return this.createField(metaData.ModifyTime);
  }

  public Long getModifyUserId(){
    return (Long)fieldMap.get(SampleMetaData.ModifyUserId);
  }

  public SampleSelect setModifyUserId(Long value){
    fieldMap.put(SampleMetaData.ModifyUserId,value);
    return this;
  }

  public SampleSelect setModifyUserId(Long[] values){
    super.addIn(SampleMetaData.ModifyUserId,values);
    return this;
  }

  public SampleSelect setModifyUserId(AbstractSelect select){
    super.addIn(SampleMetaData.ModifyUserId,select);
    return this;
  }

  public Field createModifyUserIdField(){
    return this.createField(metaData.ModifyUserId);
  }

  public String getModifyUserName(){
    return (String)fieldMap.get(SampleMetaData.ModifyUserName);
  }

  public SampleSelect setModifyUserName(String value){
    fieldMap.put(SampleMetaData.ModifyUserName,value);
    return this;
  }

  public SampleSelect setModifyUserName(String[] values){
    super.addIn(SampleMetaData.ModifyUserName,values);
    return this;
  }

  public SampleSelect setModifyUserName(AbstractSelect select){
    super.addIn(SampleMetaData.ModifyUserName,select);
    return this;
  }

  public SampleSelect addLikeModifyUserName(String value){
    addLikeCondition(metaData.ModifyUserName,value);
    return this;
  }

  public Field createModifyUserNameField(){
    return this.createField(metaData.ModifyUserName);
  }

  public Date getDeleteTime(){
    return (Date)fieldMap.get(SampleMetaData.DeleteTime);
  }

  public SampleSelect setDeleteTime(Date value){
    fieldMap.put(SampleMetaData.DeleteTime,value);
    return this;
  }

  public SampleSelect setDeleteTime(Date start,Date end){
    fieldMap.put(SampleMetaData.DeleteTime,new SmartBetween(this.createField(SampleMetaData.DeleteTime),start,end));
    return this;
  }

  public Field createDeleteTimeField(){
    return this.createField(metaData.DeleteTime);
  }

  public Long getDeleteUserId(){
    return (Long)fieldMap.get(SampleMetaData.DeleteUserId);
  }

  public SampleSelect setDeleteUserId(Long value){
    fieldMap.put(SampleMetaData.DeleteUserId,value);
    return this;
  }

  public SampleSelect setDeleteUserId(Long[] values){
    super.addIn(SampleMetaData.DeleteUserId,values);
    return this;
  }

  public SampleSelect setDeleteUserId(AbstractSelect select){
    super.addIn(SampleMetaData.DeleteUserId,select);
    return this;
  }

  public Field createDeleteUserIdField(){
    return this.createField(metaData.DeleteUserId);
  }

  public String getDeleteUserName(){
    return (String)fieldMap.get(SampleMetaData.DeleteUserName);
  }

  public SampleSelect setDeleteUserName(String value){
    fieldMap.put(SampleMetaData.DeleteUserName,value);
    return this;
  }

  public SampleSelect setDeleteUserName(String[] values){
    super.addIn(SampleMetaData.DeleteUserName,values);
    return this;
  }

  public SampleSelect setDeleteUserName(AbstractSelect select){
    super.addIn(SampleMetaData.DeleteUserName,select);
    return this;
  }

  public SampleSelect addLikeDeleteUserName(String value){
    addLikeCondition(metaData.DeleteUserName,value);
    return this;
  }

  public Field createDeleteUserNameField(){
    return this.createField(metaData.DeleteUserName);
  }

  public Integer getVersion(){
    return (Integer)fieldMap.get(SampleMetaData.Version);
  }

  public SampleSelect setVersion(Integer value){
    fieldMap.put(SampleMetaData.Version,value);
    return this;
  }

  public SampleSelect setVersion(Integer[] values){
    super.addIn(SampleMetaData.Version,values);
    return this;
  }

  public SampleSelect setVersion(AbstractSelect select){
    super.addIn(SampleMetaData.Version,select);
    return this;
  }

  public SampleSelect setVersion(Integer start,Integer end){
    fieldMap.put(SampleMetaData.Version,new SmartBetween(this.createField(SampleMetaData.Version),start,end));
    return this;
  }

  public Field createVersionField(){
    return this.createField(metaData.Version);
  }

}
