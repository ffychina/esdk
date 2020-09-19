package com.esdk.utils;

public class LoginAccount{
  private String serverName,loginName,password;
  
  public LoginAccount(String user,String password) {
  	this.setLoginName(user);
    this.setPassword(password);
  }
  
  public LoginAccount(String severName,String user,String password) {
  	this.setServerName(severName);
  	this.setLoginName(user);
  	this.setPassword(password);
  }

  public String getPassword(){
    return this.password;
  }

  public String getServerName(){
    return this.serverName;
  }

	public void setServerName(String serverName){
		this.serverName = serverName;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getLoginName(){
		return loginName;
	}

	public void setLoginName(String loginName){
		this.loginName = loginName;
	}
}
