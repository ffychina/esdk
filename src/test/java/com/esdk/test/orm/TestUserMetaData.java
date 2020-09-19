package com.esdk.test.orm;
import java.util.Date;

import com.esdk.sql.orm.ForeignKey;

public class TestUserMetaData{
  public static final String TABLENAME="test_user";
  public static final String TableComment="系统用户表";
  public static final String PrimaryKey="user_id";
  public static final int IPrimaryKey=0;
  public static final boolean IsAutoIncrement=false;
  public static final String[] UniqueIndexFields=new String[]{"user_code","center_id","user_name","center_id"};
  public static final String UserId="user_id";
  public static final String UserCode="user_code";
  public static final String UserName="user_name";
  public static final String UserAccount="user_account";
  public static final String UserAlias="user_alias";
  public static final String Tel="tel";
  public static final String Mobile="mobile";
  public static final String Email="email";
  public static final String Password="password";
  public static final String Remark="remark";
  public static final String PhotoUrl="photo_url";
  public static final String CenterId="center_id";
  public static final String LoginCenterId="login_center_id";
  public static final String CenterIds="center_ids";
  public static final String CenterNames="center_names";
  public static final String OrgId="org_id";
  public static final String DeptId="dept_id";
  public static final String RoleId="role_id";
  public static final String RoleIds="role_ids";
  public static final String RoleNames="role_names";
  public static final String Rank="rank";
  public static final String Frozen="frozen";
  public static final String Valid="valid";
  public static final String CreateTime="create_time";
  public static final String CreateUserId="create_user_id";
  public static final String CreateUserName="create_user_name";
  public static final String ModifyTime="modify_time";
  public static final String ModifyUserId="modify_user_id";
  public static final String ModifyUserName="modify_user_name";
  public static final String DeleteTime="delete_time";
  public static final String DeleteUserId="delete_user_id";
  public static final String DeleteUserName="delete_user_name";
  public static final String Version="version";
  public static final ForeignKey[] ExportKeys=new ForeignKey[]{};
  public static final ForeignKey[] ImportKeys=new ForeignKey[]{};
  public static final String[] FieldNames=new String[]{"user_id","user_code","user_name","user_account","user_alias","tel","mobile","email","password","remark","photo_url","center_id","login_center_id","center_ids","center_names","org_id","dept_id","role_id","role_ids","role_names","rank","frozen","valid","create_time","create_user_id","create_user_name","modify_time","modify_user_id","modify_user_name","delete_time","delete_user_id","delete_user_name","version"};
  public static final String[] Remarks=new String[]{"","用户编号","用户姓名","用户账号","用户别名","联系电话","移动电话","电子邮箱","密码","备注","照片URL","所属中心","登录中心","可登录中心集","可登录中心名称集","所属机构","所属部门","角色","所拥有角色","所拥有角色名称","职务级别","是否已冻结","","","","","","","","","","",""};
  public static final Class[] FieldTypes=new Class[]{Long.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,Long.class,Long.class,String.class,String.class,Long.class,Long.class,Long.class,String.class,String.class,String.class,Boolean.class,Boolean.class,java.util.Date.class,Long.class,String.class,java.util.Date.class,Long.class,String.class,java.util.Date.class,Long.class,String.class,Integer.class};
  public static final boolean[] isNullables=new boolean[]{false,false,false,false,false,true,true,true,true,true,true,false,false,false,false,true,true,false,false,false,true,false,true,true,true,true,true,true,true,true,true,true,true};
  public static final String RUserId="";
  public static final String RUserCode="用户编号";
  public static final String RUserName="用户姓名";
  public static final String RUserAccount="用户账号";
  public static final String RUserAlias="用户别名";
  public static final String RTel="联系电话";
  public static final String RMobile="移动电话";
  public static final String REmail="电子邮箱";
  public static final String RPassword="密码";
  public static final String RRemark="备注";
  public static final String RPhotoUrl="照片URL";
  public static final String RCenterId="所属中心";
  public static final String RLoginCenterId="登录中心";
  public static final String RCenterIds="可登录中心集";
  public static final String RCenterNames="可登录中心名称集";
  public static final String ROrgId="所属机构";
  public static final String RDeptId="所属部门";
  public static final String RRoleId="角色";
  public static final String RRoleIds="所拥有角色";
  public static final String RRoleNames="所拥有角色名称";
  public static final String RRank="职务级别";
  public static final String RFrozen="是否已冻结";
  public static final String RValid="";
  public static final String RCreateTime="";
  public static final String RCreateUserId="";
  public static final String RCreateUserName="";
  public static final String RModifyTime="";
  public static final String RModifyUserId="";
  public static final String RModifyUserName="";
  public static final String RDeleteTime="";
  public static final String RDeleteUserId="";
  public static final String RDeleteUserName="";
  public static final String RVersion="";
}
