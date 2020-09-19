package com.esdk.test;


import java.sql.Connection;

import com.esdk.esdk;
import com.esdk.sql.Expression;
import com.esdk.sql.JoinSelect;
import com.esdk.sql.Table;
import com.esdk.sql.Where;
import com.esdk.sql.datasource.FileConnectionPool;
import com.esdk.sql.orm.ORMSession;
import com.esdk.sql.orm.ORMSessionFactory;
import com.esdk.sql.orm.SelectFactory;
import com.esdk.sql.orm.tools.ORMCreater;
import com.esdk.test.orm.TestCenterSelect;
import com.esdk.test.orm.TestDictRow;
import com.esdk.test.orm.TestDictSelect;
import com.esdk.test.orm.TestUserResultSet;
import com.esdk.test.orm.TestUserSelect;
import com.esdk.utils.EasyObj;

public class TestSelect {

  public static void test() throws Exception{
      Connection conn=FileConnectionPool.getConnection();
			/*Connection con1=ConnectionPoolBuilder.createPool(EasyFile.getProperties("db.properties")).createConnection();*/
			/*Connection conn=ConnectionBuilder.createConnection(EasyFile.getInputStream("db.properties"));*/
      //最简单的sql查询
      TestUserSelect tus=new TestUserSelect(conn).setUserCode("admin").setValid(true);
      esdk.tool.assertEquals(tus.toString(),"SELECT * FROM test_user WHERE user_code='admin' AND valid=1");
      TestUserResultSet turs=tus.toTestUserResultSet();
      //输出jsonarray
      esdk.tool.assertEquals(turs.toJsonArray().toString(),"[{\"centerNames\":\"康远运输系统\",\"createUserId\":1,\"modifyUserId\":666,\"createUserName\":\"超级管理员\",\"remark\":\"\",\"orgId\":11108447192010752,\"userCode\":\"admin\",\"valid\":true,\"modifyUserName\":\"超级管理员\",\"photoUrl\":\"\",\"centerIds\":\"108\",\"password\":\"e10adc3949ba59abbe56e057f20f883e\",\"modifyTime\":\"2016-09-18 09:21:50\",\"userAlias\":\"\",\"rank\":\"\",\"tel\":\"\",\"email\":\"ffychina@163.com\",\"centerId\":108,\"roleId\":4,\"deptId\":13,\"mobile\":\"13560003355\",\"frozen\":false,\"userName\":\"超级管理员\",\"userId\":1,\"version\":183,\"roleIds\":\"4\",\"createTime\":\"2016-09-18 09:21:50\",\"userAccount\":\"\",\"loginCenterId\":108,\"roleNames\":\"系统管理员\"}]");
      //输出csv
      esdk.tool.assertEquals(turs.toCsv(),"user_id,user_code,user_name,user_account,user_alias,tel,mobile,email,password,remark,photo_url,center_id,login_center_id,center_ids,center_names,org_id,dept_id,role_id,role_ids,role_names,rank,frozen,valid,create_time,create_user_id,create_user_name,modify_time,modify_user_id,modify_user_name,delete_time,delete_user_id,delete_user_name,version\r\n" + 
      		"1,admin,超级管理员,,,,13560003355,ffychina@163.com,e10adc3949ba59abbe56e057f20f883e,,,108,108,108,康远运输系统,11108447192010752,13,4,4,系统管理员,,false,true,2016-09-18 09:21:50.0,1,超级管理员,2016-09-18 09:21:50.0,666,超级管理员,,,,183");
      //"like"条件会自动清删"="条件 
      tus.addLikeUserCode("ad%");
      esdk.tool.assertEquals(tus.getSQL(),"SELECT * FROM test_user WHERE valid=1 AND user_code LIKE 'ad%'");
      esdk.tool.assertEquals(tus.toJsonArray().toString(),"[{\"userId\":\"1\",\"userCode\":\"admin\",\"userName\":\"超级管理员\",\"userAccount\":\"\",\"userAlias\":\"\",\"tel\":\"\",\"mobile\":\"13560003355\",\"email\":\"ffychina@163.com\",\"password\":\"e10adc3949ba59abbe56e057f20f883e\",\"remark\":\"\",\"photoUrl\":\"\",\"centerId\":\"108\",\"loginCenterId\":\"108\",\"centerIds\":\"108\",\"centerNames\":\"康远运输系统\",\"orgId\":\"11108447192010752\",\"deptId\":\"13\",\"roleId\":\"4\",\"roleIds\":\"4\",\"roleNames\":\"系统管理员\",\"rank\":\"\",\"frozen\":false,\"valid\":true,\"createTime\":\"2016-09-18 09:21:50\",\"createUserId\":\"1\",\"createUserName\":\"超级管理员\",\"modifyTime\":\"2016-09-18 09:21:50\",\"modifyUserId\":\"666\",\"modifyUserName\":\"超级管理员\",\"version\":183}]");
      //时间范围条件,自动判断最大值和最小值是否为空,智能使用">=,<=,between"
      tus.setCreateTime(esdk.time.valueOf("2018-01-01"),esdk.time.valueOf("2020-01-01"));
      esdk.tool.assertEquals(tus.getSQL(),"SELECT * FROM test_user WHERE valid=1 AND user_code LIKE 'ad%' AND create_time between '2018-01-01 00:00:00' and '2020-01-01 00:00:00'");
      //清除查询条件
      tus.clearCondition();
      //sql获取记录数
      esdk.tool.assertEquals(tus.count(),3);
      //时间范围最大值为空,智能使用>=条件
      tus.setCreateTime(esdk.time.valueOf("2018-01-01"),null);
      esdk.tool.assertEquals(tus.getSQL(),"SELECT * FROM test_user WHERE create_time>='2018-01-01 00:00:00'");
      //按创建时间倒序
      tus.setOrderBy(tus.md.CreateTime,true);
      esdk.tool.assertEquals(tus.getSQL(),"SELECT * FROM test_user WHERE create_time>='2018-01-01 00:00:00' ORDER BY create_time desc");
      //对多个字段排序.注意不支持一次性对多个字段倒序,因为没有这个场景.
      tus.setOrderBy(tus.md.UserCode,tus.md.UserName);
      esdk.tool.assertEquals(tus.getSQL(),"SELECT * FROM test_user WHERE create_time>='2018-01-01 00:00:00' ORDER BY user_code,user_name");
      //追加倒序
      tus.addOrderBy(tus.md.CenterId,true);
      esdk.tool.assertEquals(tus.getSQL(),"SELECT * FROM test_user WHERE create_time>='2018-01-01 00:00:00' ORDER BY user_code,user_name,center_id desc");
      //设置输出字段
      tus.setColumns(tus.md.UserId,tus.md.UserCode,tus.md.UserName,tus.md.CenterId);
      //清除排序字段
      tus.clearOrderBy();
      tus.clearCondition();
      //追加输出字段,并设置别名
      tus.addColumnWithAlias(tus.md.UserCode,"login_code");
      esdk.tool.assertEquals(tus.getSQL(),"SELECT user_id,user_code,user_name,center_id,user_code as login_code FROM test_user");
      //设置top 50
      tus.setTop(50);
      esdk.tool.assertEquals(tus.getSQL(),"SELECT user_id,user_code,user_name,center_id,user_code as login_code FROM test_user LIMIT 50");
      //设置分页
      tus.setRowsOffset(10);
      tus.setRowsLimit(20);
      esdk.tool.assertEquals(tus.getSQL(),"SELECT user_id,user_code,user_name,center_id,user_code as login_code FROM test_user LIMIT 20 OFFSET 10");
      //in条件
      tus.setUserCode(new String[] {"admin","tms_user"});
      esdk.tool.assertEquals(tus.getSQL(),"SELECT user_id,user_code,user_name,center_id,user_code as login_code FROM test_user WHERE user_code IN ('admin','tms_user') LIMIT 20 OFFSET 10");
      //or条件
      tus.clearCondition();
      tus.setTop(0).setRowsOffset(0);
      //不打印sql日志
      tus.showSql(false);
      //输出list<parentRow>
      esdk.tool.assertEquals(tus.toList().get(0).getUserCode(),"admin");
      //清理一级和二级缓存
			tus.clearCache();
			//使用二级缓存(需要redis服务支持)
			tus.useCache(true);
			tus.showSql(true);
			//第一次打印日志
			esdk.tool.assertEquals(tus.toTestUserResultSet().size(),3);
			//第二次从缓存获取,没有执行sql,所以不会打印sql日志
			esdk.tool.assertEquals(tus.toRowSet().size(),3);
			//从在线数据集获取只第一条记录,参数true是指如果没有查到数据,则自动创建,避免返回null.
			esdk.tool.assertEquals(tus.getFirstRow(true).getUserCode(),"admin");
			//创建一级缓存
			ORMSession ormsession=ORMSessionFactory.getORMSession(conn);
			//创建select工厂类
			SelectFactory factory=new SelectFactory(ormsession);
			//使用一级缓存
			TestCenterSelect tcs=factory.createSelect(TestCenterSelect.class);
			tcs.setCenterId(108);
			//第一次有sql日志
			esdk.tool.assertEquals(tcs.toRowSet().size(),1);
			//第二次从缓存获取没有sql日志,要注意的是只能缓存Rowset,不能缓存count()和resultset
			esdk.tool.assertEquals(tcs.toRowSet().size(),1);
			//子查询,字段名以手工指定为最优先,没指定的话自动找出匹配的字段名,如果匹配不上则找主键值字段
			tus.setCenterId(tcs);
			esdk.tool.assertEquals(tus.getSQL(),"SELECT user_id,user_code,user_name,center_id,user_code as login_code FROM test_user WHERE center_id IN(SELECT center_id FROM test_center WHERE center_id=108)");
			//使用top50
			TestDictSelect tds=new TestDictSelect(conn,true);
			tds.setValid(true).setOrderBy(tds.md.Category);
			//多表查询,使用top50,使用 left join(默认inner join),查询条件和输出字段都是参考各个select
			tcs.addColumnWithAlias(tcs.md.Name,"center_name");
			
			//默认left join方式，并默认限制50条记录
			JoinSelect cs=new JoinSelect(conn,tds,tcs).on(tcs,tds);
			esdk.tool.assertEquals(cs.getSQL(),"SELECT td.*,tc.center_id,tc.name as center_name FROM test_dict td LEFT JOIN test_center tc on tc.center_id=td.center_id WHERE td.valid=1 AND tc.center_id=108 ORDER BY td.category LIMIT 50");
			
			//指定left join方式和限制输出50条记录
			cs=new JoinSelect(conn,true,Table.LEFTJOIN,tds,tcs).on(tcs,tds);
			esdk.tool.assertEquals(cs.getSQL(),"SELECT td.*,tc.center_id,tc.name as center_name FROM test_dict td LEFT JOIN test_center tc on tc.center_id=td.center_id WHERE td.valid=1 AND tc.center_id=108 ORDER BY td.category LIMIT 50");
			
			//输出Rowset<IRow>对象
			esdk.tool.assertEquals(cs.toRowSet().toJsonArray().toString(),"[{\"dictId\":\"1\",\"category\":\"支付类型\",\"name\":\"alipay\",\"content\":\"支付宝\",\"sequence\":10,\"centerId\":108,\"valid\":true,\"centerName\":\"华师继教\"},{\"dictId\":\"2\",\"category\":\"支付类型\",\"name\":\"wepay\",\"content\":\"微信支付\",\"sequence\":20,\"centerId\":108,\"valid\":true,\"centerName\":\"华师继教\"},{\"dictId\":\"3\",\"category\":\"支付类型\",\"name\":\"unipay\",\"content\":\"银联支付\",\"sequence\":30,\"centerId\":108,\"valid\":true,\"centerName\":\"华师继教\"}]");
			//输出Rowset<ParentRow>对象
			TestDictRow tdr=cs.toRowSet(TestDictRow.class).getFirstRow();
			esdk.tool.assertEquals(tdr.toJsonObject(true,false).toString(),"{\"dictId\":\"1\",\"category\":\"支付类型\",\"name\":\"alipay\",\"content\":\"支付宝\",\"sequence\":10,\"centerId\":108,\"valid\":true,\"centerName\":\"华师继教\"}");
			esdk.tool.assertEquals(tdr.getContent(),"支付宝");
			//多个like的or条件
			tds.orLike(tds.md.Content,"%支付%",tds.md.Content,"%微信%");
			esdk.tool.assertEquals(tds.getSQL(),"SELECT td.* FROM test_dict td WHERE td.valid=1 AND (td.content LIKE '%支付%' OR td.content LIKE '%微信%') ORDER BY td.category LIMIT 50");
			tds.clearCondition();
			//多个等于的or条件
			tds.orEq(tds.md.Category,"支付类型",tds.md.Name,"pay");
			esdk.tool.assertEquals(tds.getSQL(),"SELECT td.* FROM test_dict td WHERE (td.category='支付类型' OR td.name='pay') ORDER BY td.category LIMIT 50");
			//多个自定义表达式的or条件
			tds.orExpress(Where.LIKE,tds.md.Category,"支付%",tds.md.Name,"%pay");
			esdk.tool.assertEquals(tds.getSQL(),"SELECT td.* FROM test_dict td WHERE (td.category='支付类型' OR td.name='pay') AND (td.category LIKE '支付%' OR td.name LIKE '%pay') ORDER BY td.category LIMIT 50");
			tds.clearCondition();
			tds.or(tds.md.Category,"支付宝",tds.md.Name,Expression.LIKE,"%pay");
			esdk.tool.assertEquals(tds.getSQL(),"SELECT td.* FROM test_dict td WHERE (td.category='支付宝' OR td.name LIKE '%pay') ORDER BY td.category LIMIT 50");
			tds.clearCondition();
			//设置is null和is not null条件
			tds.addEqualNull(tds.md.DeleteTime).addNotEqualNull(tds.md.CreateTime);
			esdk.tool.assertEquals(tds.getSQL(),"SELECT td.* FROM test_dict td WHERE td.delete_time IS NULL AND td.create_time IS NOT NULL ORDER BY td.category LIMIT 50");
			//清理where条件、on条件、排序
			tds.clearCondition();
			tds.clearOrderBy();
			tcs.clearOn();
			//设置字段子查询
			tcs.setColumns(tcs.md.Name);
			tds.addColumn(tcs,"center_name");
			esdk.tool.assertEquals(tds.getSQL(),"SELECT td.*,(SELECT tc.name FROM test_center tc WHERE tc.center_id=108) as center_name FROM test_dict td LIMIT 50");
      esdk.tool.printAssertInfo();
      System.exit(0);
  }

  public static void main(String[] args) throws Exception{
    test();
  }}
