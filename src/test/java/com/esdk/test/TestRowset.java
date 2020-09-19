package com.esdk.test;

import java.util.Date;
import java.util.List;

import com.esdk.esdk;
import com.esdk.sql.Expression;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.test.orm.SampleRow;

public class TestRowset{
	public static void main(String[] args) throws Exception{
		ABRowSet<SampleRow> rs=new ABRowSet(SampleRow.class);
		Date date=esdk.time.getDate("2020-01-01 12:59:30");
		SampleRow row=rs.append();
		row.setDictId(1111111L);
		row.setCategory("订单类型");
		row.setName("支付订单");
		row.setSequence(10);
		row.setValid(true);
		row.setCreateTime(date);
		System.out.println(row.toXml());

		
		SampleRow row1=rs.append();
		row1.setDictId(222222L);
		row1.setCategory("订单类型");
		row1.setName("任务单");
		row1.setSequence(20);
		row1.setValid(true);
		row1.setCreateTime(date);

		SampleRow row2=rs.append();
		row2.setDictId(3333333L);
		row2.setCategory("订单类型");
		row2.setName("任务单");
		row2.setSequence(30);
		row2.setValid(true);
		row2.setCreateTime(date);

		SampleRow row3=rs.append();
		row3.setDictId(444444L);
		row3.setCategory("职业类型");
		row3.setName("程序员");
		row3.setSequence(10);
		row3.setValid(true);
		row3.setCreateTime(date);
		
		SampleRow row4=rs.append();
		row4.setDictId(555555555L);
		row4.setCategory("职业类型");
		row4.setName("业务员");
		row4.setSequence(20);
		row4.setValid(true);
		row4.setCreateTime(date);

		SampleRow row5=rs.append();
		row5.setDictId(66666666666L);
		row5.setCategory("职业类型");
		row5.setName("财务");
		row5.setSequence(30);
		row5.setValid(true);
		row5.setCreateTime(date);

		//输出json
		esdk.tool.assertEquals(rs.toJsonArray().toString(),"[{\"dictId\":\"1111111\",\"category\":\"订单类型\",\"name\":\"支付订单\",\"sequence\":10,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"},{\"dictId\":\"222222\",\"category\":\"订单类型\",\"name\":\"任务单\",\"sequence\":20,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"},{\"dictId\":\"3333333\",\"category\":\"订单类型\",\"name\":\"任务单\",\"sequence\":30,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"},{\"dictId\":\"444444\",\"category\":\"职业类型\",\"name\":\"程序员\",\"sequence\":10,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"},{\"dictId\":\"555555555\",\"category\":\"职业类型\",\"name\":\"业务员\",\"sequence\":20,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"},{\"dictId\":\"66666666666\",\"category\":\"职业类型\",\"name\":\"财务\",\"sequence\":30,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"}]");
		List<ABRowSet<SampleRow>> group=rs.group(row.md.Category);
		esdk.tool.assertEquals(group.size(),2);
		for(ABRowSet<SampleRow> subrs:group) {
			//输出csv
			System.out.println(subrs.toCsv(row.md.DictId,row.md.Category,row.md.Name,row.md.Valid,row.md.CreateTime));
			System.out.println("--------------------------------------------------------------------------------------");
		}

		//输出xml
		System.out.println(rs.toXml());
		
		//获取某字段的全部记录
		esdk.tool.assertEquals(esdk.str.valueOf(rs.getStrings(row.md.Name)),"支付订单,任务单,任务单,程序员,业务员,财务");
		
		//使用lambda表达式
		esdk.tool.assertEquals(esdk.str.valueOf(rs.getStrings(r->"%"+r.getName()+"%")),"%支付订单%,%任务单%,%任务单%,%程序员%,%业务员%,%财务%");
		
		esdk.tool.assertEquals(esdk.str.valueOf(esdk.array.unique(rs.getLongs(row.md.Sequence))),"10,20,30");
		
		//查找匹配字段的第一条记录
		esdk.tool.assertEquals(rs.findRow(row.md.PrimaryKey,1111111L).toString(),"{\"dictId\":\"1111111\",\"category\":\"订单类型\",\"name\":\"支付订单\",\"sequence\":10,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"}");
		
		//按主键查找记录
		esdk.tool.assertEquals(rs.findById(1111111L).toJsonObject().toString(),"{\"dictId\":\"1111111\",\"category\":\"订单类型\",\"name\":\"支付订单\",\"sequence\":10,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"}");
		
		//过滤条件
		esdk.tool.assertEquals(rs.filter("category","订单类型").size(),3);
		esdk.tool.assertEquals(rs.filter("category","订单类型").size(),3);
		
		esdk.tool.assertEquals(rs.filter(2,"category","订单类型").size(),2);
		esdk.tool.assertEquals(rs.filter(2,"category","订单类型").filter(row.md.Sequence,Expression.MORE,10).size(),1);
		esdk.tool.assertEquals(rs.filter(2,"category","订单类型").filter(row.md.Sequence,Expression.MOREEQUAL,10).size(),2);
		esdk.tool.assertEquals(rs.filters(2,row.md.Category,"订单类型",row.md.Sequence,Expression.MOREEQUAL,10).size(),2);
		esdk.tool.assertEquals(rs.filter(2,"category=订单类型 && sequence>=10").size(),2);
		
		//lambda表达式支持filter
		esdk.tool.assertEquals(rs.filter(r->r.getCategory().equals("订单类型")).size(),3);
		esdk.tool.assertEquals(rs.filter(2,r->r.getCategory().equals("订单类型")).size(),2);
		
		//克隆数据集
		ABRowSet<SampleRow> cloneRs=(ABRowSet)rs.clone();
		esdk.tool.assertEquals(cloneRs.size(),6);
		
		//过滤重复值（distinct）
		ABRowSet distinctRs=rs.distinct(row.md.Category);
		esdk.tool.assertEquals(rs.size(),2);
		esdk.tool.assertEquals(distinctRs.size(),2);
		esdk.tool.assertEquals(distinctRs.toJsonArray().toString(),"[{\"dictId\":\"1111111\",\"category\":\"订单类型\",\"name\":\"支付订单\",\"sequence\":10,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"},{\"dictId\":\"444444\",\"category\":\"职业类型\",\"name\":\"程序员\",\"sequence\":10,\"valid\":true,\"createTime\":\"2020-01-01 12:59:30\"}]");

		esdk.tool.assertEquals(cloneRs.size(),6);
		
		//移除多条数据
		esdk.tool.assertEquals(cloneRs.remove(rs));
		esdk.tool.assertEquals(cloneRs.size(),4);

		//合计数
		esdk.tool.assertEquals(rs.sum(row.md.Sequence),20);
		esdk.tool.assertEquals(rs.first());
		esdk.tool.assertEquals(rs.last());

		//排序（正序、倒序）
		esdk.tool.assertEquals(cloneRs.sort(row.md.Sequence).getFirstRow().getSequence().intValue(),20);
		esdk.tool.assertEquals(cloneRs.sort(true,row.md.Sequence).getFirstRow().getSequence().intValue(),30);

		//获取子集
		esdk.tool.assertEquals(cloneRs.subRowSet(0,2).size(),2);

		//最小值、最大值（Number）
		esdk.tool.assertEquals(cloneRs.min(row.md.Sequence).intValue(),20);
		esdk.tool.assertEquals(cloneRs.max(row.md.Sequence).intValue(),30);
		
		//最大值（Comparable对象）
		esdk.tool.assertEquals(cloneRs.maxObj(row.md.CreateTime),date.clone());
		
		rs.getFirstRow().set("ext1","测试增加字段");
		esdk.tool.assertEquals(rs.toJsonArray(true,true).toString().contains("\"ext1\":\"测试增加字段\""));
		
		//使用lambda修改数据集
		rs.update(t->t.setName(t.getCategory()+"->"+t.getName()));
		esdk.tool.assertEquals(esdk.str.valueOf(rs.getStrings(r->r.getName())),"订单类型->支付订单,职业类型->程序员");
		
		//打印测试结果
		esdk.tool.printAssertInfo();
		
		
	}
}
