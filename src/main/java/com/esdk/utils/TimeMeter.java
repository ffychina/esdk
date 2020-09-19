/**
 * @author franky.Fan
 * 秒表计时
 * */
package com.esdk.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

import com.esdk.esdk;

public class TimeMeter {
	private Date begin;
	private Date end;
	private OutputStream pw;
	public TimeMeter() {
		pw=System.out;
		begin();
	}
	public TimeMeter(PrintStream printstream) {
		pw=printstream;
		begin();
	}
	public TimeMeter(Date begin) {
		this.begin=begin;
	}
	
	public TimeMeter begin() {
		setBegin(new Date());
		return this;
	}
	
	public TimeMeter setBegin(Date begin) {
		this.begin=begin;
		return this;
	}
	
	public Date getBegin() {
		return this.begin;
	}
	
	public TimeMeter setEnd(Date end) {
		this.end=end;
		return this;
	}
	
	public Date getEnd() {
		return this.end;
	}
	
	public TimeMeter end() {
		this.end=new Date();
		return this;
	}
	
	public long getElapse() {
		return (end==null?new Date():end).getTime()-begin.getTime();
	}

	public TimeMeter printElapse() {
		printElapse("耗费时间:{0}");
		return this;
	}
	
	public TimeMeter printElapse(String pattern) {
		try{
			pw.write(EasyStr.format(pattern,EasyTime.formatElapse(getElapse())).getBytes());
		}catch(IOException e){
			e.printStackTrace();
		}
		return this;
	}

	public static TimeMeter newInstanceOf(){
		return new TimeMeter();
	}

 @Override public String toString() {
		return getElapse("耗费时间:{}");
	}
 
 	public String getElapse(String pattern) {
 		return EasyStr.format(pattern,EasyTime.formatElapse(getElapse()));
 	}
 	
	public static void main(String[] args) {
		esdk.tool.assertEquals(new TimeMeter().begin().end().printElapse().getElapse(),0);
	}
}
