/**
 * @author franky.ffy
 */
package com.esdk.utils;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.ArrayUtil;

public class EasyScheduler{
	private Timer timer=new Timer();

	public EasyScheduler(final Runnable task,final String schedule){
		String[] s=fill(schedule.split(" "));
		final String second=s[0];
		final String minute=s[1];
		final String hour=s[2];
		final String date=s[3];
		final String month=s[4];
		final String day=s[5];
		final String year=s[6];
		timer.schedule(new TimerTask(){
			@Override public void run(){
				Calendar c=Calendar.getInstance();
				c.setTime(new Date());
				if(isRange(year,c.get(Calendar.YEAR))
						&&isRange(month,c.get(Calendar.MONTH)+1) // 月份要加1，如7月1日用c.get(Calendar.MONTH)等于6
						&&isRange(date,c.get(Calendar.DATE))
						&&isRange(hour,c.get(Calendar.HOUR_OF_DAY))
						&&isRange(day,c.get(Calendar.DAY_OF_WEEK)) //周日=1，周一=2，...周六=7
						&&isRange(minute,c.get(Calendar.MINUTE))
						&&isRange(second,c.get(Calendar.SECOND))){
					task.run();
				}
			}
		},0,1000);
	}

	public void cancel(){
		timer.cancel();
	}

	private String[] fill(String[] s){
		if(s.length<7){
			String[] result=new String[7];
			System.arraycopy(s,0,result,0,s.length);
			for(int i=0;i<result.length;i++){
				if(result[i]==null)
					result[i]="*";
			}
			return result;
		}
		return s;
	}

	
	/** 
	 * 秒 分 时 日 月 星期 年。与Quarz的时间表达式是一致的。 
	 * x/y表达一个等步长序列，x为起始值，y为增量步长值。如在分钟字段中使用0/15，则表示为0,15,30和45秒，而5/15在分钟字段中表示5,20,35,50
	 * */
	private boolean isRange(String range,int value){
		if(range.equals("*")||range.equals("?"))
			return true;
		else if(range.matches("\\d+"))
			return Integer.valueOf(range).intValue()==value;
		else if(range.indexOf(",")>=0){
			return ArrayUtil.contains(EasyMath.toIntArray(range.split(",")),value);
		}else if(range.matches("\\d+\\-\\d+")){
			Matcher m=Pattern.compile("(\\d+)\\-(\\d+)").matcher(range);
			if(m.matches()){
				return Integer.valueOf(m.group(1))<=value&&Integer.valueOf(m.group(2))>=value;
			}else
				return false;
		}else if(range.indexOf("/")>=0){
			String[] array=range.split("/");
			Integer start=EasyMath.toInt(array[0]),step=EasyMath.toInt(array[1]);
			boolean result=(value==start)||(value>start&&(value-start)%step==0);
			return result;
		}
		return false;
	}

	public static void main(String[] args){
		System.out.println("schedule is start");
		Runnable runable=new Runnable(){
			@Override public void run(){
				System.out.println(new java.sql.Timestamp(new Date().getTime()));
			}
		};
//		new EasyScheduler(runable,"0 0/5 0-23 * * 1-7 2009,2010,2011,2012,2013"); // 秒 分 时 日 月 星期 年。与Quarz的时间表达式是一致的,即表示2009年至2013年，周一至日，每小时每隔5分钟的第0秒，会触发任务执行。
		new EasyScheduler(runable,"* 12 10 * * 2"); // 每周一执行
	}
}
