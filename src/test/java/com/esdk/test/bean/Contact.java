package com.esdk.test.bean;


import java.io.Serializable;
import java.util.Date;

import org.nutz.json.Json;

import com.esdk.utils.EasyObj;

import cn.hutool.core.util.ObjectUtil;

public class Contact implements Serializable{
	private Integer id;
	private String memberId;
	private String name;
	private String mobile;
	private Boolean married;
	private String email;
	private String remark;
	private Date createTime;
	private Date modifyTime;
	private static final long serialVersionUID=1L;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id=id==null?null:id;
	}

	public String getMemberId(){
		return memberId;
	}

	public void setMemberId(String memberId){
		this.memberId=memberId==null?null:memberId.trim();
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name=name==null?null:name.trim();
	}

	public String getMobile(){
		return mobile;
	}

	public void setMobile(String mobile){
		this.mobile=mobile==null?null:mobile.trim();
	}

	public String getEmail(){
		return email;
	}

	public void setEmail(String email){
		this.email=email==null?null:email.trim();
	}

	public String getRemark(){
		return remark;
	}

	public void setRemark(String remark){
		this.remark=remark==null?null:remark.trim();
	}

	public Date getCreateTime(){
		return createTime;
	}

	public void setCreateTime(Date createTime){
		this.createTime=createTime;
	}

	public Date getModifyTime(){
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime){
		this.modifyTime=modifyTime;
	}

  @Override
  public String toString(){
  	return Json.toJson(this);
  }
  
  @Override
  public int hashCode(){
  	return toString().hashCode();
  }

  @Override
  public boolean equals(Object obj){
  	return EasyObj.eq(this,obj);
  }
  
	public Boolean getMarried(){
		return married;
	}

	public void setMarried(Boolean married){
		this.married = married;
	}
}