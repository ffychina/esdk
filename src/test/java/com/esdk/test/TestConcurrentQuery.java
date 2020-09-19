package com.esdk.test;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestConcurrentQuery {
	public static void main(String[] args) throws Exception {
//		test();
//		test1();
		test2();
	}

	private static void test() throws Exception {
		ConcurrentLinkedQueue<String> list=new ConcurrentLinkedQueue();
		list.add("a");
		list.add("b");
		while(!list.isEmpty()) {
			String item=list.remove();
			System.out.println(item);
		}
	}

	private static void test1() throws Exception {
		LinkedList<String> list=new LinkedList();
		list.add("a");
		list.add("b");
		for(Iterator<String> iter=list.iterator();iter.hasNext();) {
			String item=iter.next();
			System.out.println(item);
			iter.remove();
			System.out.println("list size:"+list.size());
			if(item.equals("a"))
				list.add("c");
		}
	}
	

	private static void test2() throws Exception {
		LinkedList<String> rowList=new LinkedList();
		rowList.add("a");
		rowList.add("b");
		for(int i=0;i<rowList.size();i++) {
			String item=rowList.get(i);
			System.out.println(item);
			rowList.remove(item);
			i--;
			System.out.println("list size:"+rowList.size());
			if(item.equals("a"))
				rowList.add("c");
		}
	}
}
