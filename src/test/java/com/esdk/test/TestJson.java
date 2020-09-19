package com.esdk.test;


import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esdk.esdk;
import com.esdk.test.bean.Contact;
import com.esdk.utils.EasyTime;
import com.esdk.utils.JsonUtils;
import com.esdk.utils.ParseException;
import com.esdk.utils.EasyObj;

public class TestJson {
	public static void main(String[] args) throws ParseException {
		test();
	}

	private static void test() throws ParseException {
		Contact c=new Contact();
		c.setId(111);
		c.setCreateTime(EasyTime.valueOf("2013-12-31 12:00:33"));
		c.setName("张三");
		c.setMobile("13560002233");
		c.setModifyTime(EasyTime.valueOf("2013-12-31 12:44:33"));
		JSONObject jo=esdk.json.toJsonObject(c,JsonUtils.getPropertyPreFilter("name","modifyTime"));
		esdk.tool.assertEquals(jo.get("id"),111);
		esdk.tool.assertEquals(jo.toJSONString(),"{\"createTime\":\"2013-12-31 12:00:33\",\"mobile\":\"13560002233\",\"id\":111}");
		ArrayList list=new ArrayList();
		list.add(c);
		JSONArray ja=esdk.json.toJsonArray(list,JsonUtils.getPropertyPreFilter("name","modifyTime"));
		esdk.tool.assertEquals(ja.toString(),"[{\"createTime\":\"2013-12-31 12:00:33\",\"mobile\":\"13560002233\",\"id\":111}]");
		Contact c1=esdk.json.toBean(jo,Contact.class);
		esdk.tool.assertEquals(c1.getCreateTime(),c.getCreateTime());
		esdk.tool.assertEquals(c1.getId(),c.getId());
		List<Contact> listc1=esdk.json.toBeanList(ja,Contact.class);
		esdk.tool.assertEquals(listc1.size(),1);
		esdk.tool.assertEquals(EasyObj.equals(listc1.get(0),c1));
		esdk.tool.printAssertInfo();
	}
}
