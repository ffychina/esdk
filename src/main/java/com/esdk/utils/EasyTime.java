package com.esdk.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;

import com.esdk.esdk;
/***
 * @author 范飞宇
 * @since 2003.?.?
 */
public class EasyTime {
	public final static String COMPACTDATE_FORMAT = "yyyyMMdd";
	public final static String COMPACTDATETIME_FORMAT = "yyyyMMddHHmmss";
	public final static String COMPACTDATETIME_FULL_FORMAT = "yyyyMMddHHmmssSSS";
	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public final static String FULL_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public final static String HK_DATE_FORMAT = "dd/MM/yyyy";

	public static final int DateMillSecond=1000*60*60*24;
	
	public static String calucateDurationDate(Date begin,Date end) {
		return calucateDurationDate(begin,end,null);
	}

	/**计算两个日期相隔的时间段*/
	public static String calucateDurationDate(Date begin,Date end, String format) {
		if(format==null)
			format="{}年{}月{}日";
		if(begin.compareTo(end)>0)
			return intervalTime(begin,end,Calendar.DATE)+"日";
		Integer[] params=new Integer[3];
		params[0]=intervalTime(begin,end,Calendar.YEAR);
		if(params[0]>0) {
			begin=addDate(begin,Calendar.YEAR,params[0]);
		}
		params[1]=intervalTime(begin,end,Calendar.MONTH);
		if(params[1]>0)
			begin=addDate(begin,Calendar.MONTH,params[1]);
		params[2]=intervalTime(begin,end,Calendar.DATE);
		return EasyStr.format(format,(Object[])params);
	}
	
	/**比较两个日期的相隔天数*/
	public static int dateDiff(Date begin,Date end) {
		return dateDiff(begin,end,'d');
	}

	/**比较两个日期的相隔天数(d)|月数(M)|年数(y)*/
	@SuppressWarnings("deprecation")
	public static int dateDiff(Date begin,Date end, char unit) {
		long sec =0;
		int result=-1;
		switch (unit){
		case Calendar.SECOND:	
		case 's':	
			sec = 1000; break;		
		case Calendar.MINUTE:	
		case 'm':	
			sec = 60000; break;		
		case Calendar.HOUR:		
		case 'h':	
		case 'H':	
			sec = 3600000; break;	
		case Calendar.DATE:		
		case 'd':		
		case Calendar.DAY_OF_WEEK:
		case Calendar.DAY_OF_YEAR:
			sec = 86400000; break;   //取得日期间隔,按时间单位算/24/60/60/1000	
		case Calendar.WEEK_OF_MONTH:
		case Calendar.WEEK_OF_YEAR:
		case 'w':
			sec = 604800000; break;   //按时间差取星期的间隔,每7天为一个星期/7/24/60/60/1000;
		case Calendar.YEAR:
		case 'y':
			result=end.getYear()-begin.getYear();
			if(end.getMonth()*100+end.getDate()<begin.getMonth()*100+begin.getDate())
				result-=1;
			return result;
		case Calendar.MONTH:
		case 'M':
			result=end.getMonth()-begin.getMonth();
			int v=end.getYear()-begin.getYear();
			if(v>0)
				result=v*12+result;
			if(end.getDate()<begin.getDate())
				result-=1;
			return result;
		default : sec =-1;
		}
		if (sec == -1) return -1;
		result=(int)((end.getTime() - begin.getTime())/sec);
		return result;
	}

	public static int networkDays(Date begin,Date end) {
		return networkDays(begin,end,new Date[] {});
	}
	
	/**比较两个日期的相隔的工作日天数（排除指定的假期）*/
	@SuppressWarnings("deprecation")
	public static int networkDays(Date begin,Date end, Date[] holidays) {
		int result=0;
		for(Date s=begin;s.getTime()<=end.getTime();s.setDate(s.getDate()+1)) {
			if(s.getDay()!=6&&s.getDay()!=0&&!EasyArray.contains(holidays,s))
				result++;
		}
		return result;
	}
	
	/**日期转指定格式字符串*/
	public static String toString(Date date, String format) {
		return formatDate(date,format);
	}
	
	/**日期转指定格式字符串*/
	public static String toString(Date date) {
		return formatDate(date);
	}
	
	/**字符串转为日期类型*/
	public static Date toDate(String datestr){
		return valueOf(datestr);
	}

	public static Date getDate(String dateString){
		return valueOf(dateString);
	}
	
	public static Date getDate(String dateString, String dateFormat) {
		if(esdk.str.isBlank(dateString))
			return null;
		SimpleDateFormat dd = new SimpleDateFormat(dateFormat);
		//String d = dd.format(new Date());
			try{
				Date date = dd.parse(dateString);
				return date;
			}
			catch(java.text.ParseException e){
				return null;
			}
	}

/**计算相隔时间数*/
	public static int intervalTime(Date begin,Date end,int calendarType){
		return dateDiff(begin,end,(char)calendarType);
	}
	
	/**计算相隔时间数*/
	public static long intervalTime(Date begin,Date end,int calendarType,boolean calculateTime){
		long result = 0;
		if (calculateTime || (calendarType == Calendar.SECOND)	|| (calendarType == Calendar.MINUTE)
				|| (calendarType == Calendar.HOUR)) {
			result = intervalTime(end,begin,calendarType);
			if (result >=0) return result; 
		}
		//不根据时间来
		switch (calendarType){
		case Calendar.DATE:		
		case Calendar.DAY_OF_WEEK:
		case Calendar.DAY_OF_YEAR:
			result = intervalDayNoTime(end,begin);
			break;
		case Calendar.WEEK_OF_MONTH:
		case Calendar.WEEK_OF_YEAR:
			result = intervalWeekNoTime(end,begin);
			break;
		case Calendar.MONTH:
			result = intervalMonth(end,begin);
			break;
		default : 
		
		}
		
		return result;
	}

	public static int intervalDayNoTime(Date d1,Date d2){
		//取日期,不计时间
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		Calendar c3 = Calendar.getInstance();
		c1.clear(); c2.clear();c3.clear();
		
		if (d1.after(d2)){
			c1.setTime(d2); c2.setTime(d1);
		}else{
			c1.setTime(d1); c2.setTime(d2);
		}
		int day =0;
		while (c1.get(Calendar.YEAR) < c2.get(Calendar.YEAR)){
			//设置到年底12月31号,Calendar.set中的月份参数从0开始,0为1月,11为12月
			c3.set(c1.get(Calendar.YEAR), 11, 31);  
			day = day + c3.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
			c1.set(c1.get(Calendar.YEAR)+1, 0, 1);
		}
		day = day + c2.get(Calendar.DAY_OF_YEAR)- c1.get(Calendar.DAY_OF_YEAR);
		return day;
	}

	public static int intervalWeekNoTime(Date d1, Date d2){
		//按每周日为一周的开始计算周的间隔
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		Calendar c3 = Calendar.getInstance();
		c1.clear(); c2.clear(); c3.clear();
		if (d1.after(d2)){
			c1.setTime(d2); c2.setTime(d1);
		}else{
			c1.setTime(d1); c2.setTime(d2);
		}
		int week = 0;
		while (c1.get(Calendar.YEAR) < c2.get(Calendar.YEAR)){
			//设置到年底12月31号,Calendar.set中的月份从0开始,0为1月,11为12月
			c3.set(c1.get(Calendar.YEAR), 11, 31);  
			while (c3.get(Calendar.WEEK_OF_YEAR) == 1){  //与下年的1号在同一个星期内
				c3.set(Calendar.DATE, -4);
			}
			week = week + c3.get(Calendar.WEEK_OF_YEAR)- c1.get(Calendar.WEEK_OF_YEAR);
			c1.set(c1.get(Calendar.YEAR)+1, 0, 1);
		}
		week = week + c2.get(Calendar.WEEK_OF_YEAR)- c1.get(Calendar.WEEK_OF_YEAR);
		return week;
	}

	public static int intervalMonth(Date d1, Date d2) {
		// 取得月份间隔
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.clear(); c2.clear();
		c1.setTime(d1); c2.setTime(d2);
		
		int Year = Math.abs(c1.get(Calendar.YEAR)-c2.get(Calendar.YEAR));
		int Month = Math.abs(c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH));
		return Year * 12 + Month;
	}

	@SuppressWarnings("deprecation") 
	public static Date createDate(int year,int month,int date) {
		if(year>1900)
			year=year-1900;
		return new Date(year,month-1,date);
	}
	
	@SuppressWarnings("deprecation") 
	public static java.sql.Date createSqlDate(int year,int month,int date) {
		if(year>1900)
			year=year-1900;
		return new java.sql.Date(year,month-1,date);
	}
	
	@SuppressWarnings("deprecation")
	public static Date createDate(int year,int month,int date,int hour,int minute,int second) {
		if(year>1900)
			year=year-1900;
		return new Date(year,month-1,date,hour,minute,second);
	}
	
	public static String getNowTime() {
		return getNowTime(COMPACTDATETIME_FORMAT);
	}

	public static int getToday() {
		return getDayOfWeek(new Date());
	}
	
	public static String getNowTime(String dateformat) {
		SimpleDateFormat formatter = new SimpleDateFormat(dateformat); // value="yyMMdd HHmmss"
		String result = formatter.format(new java.util.Date());
		formatter = null;
		return result;
	}

	@SuppressWarnings("deprecation") 
	public static Date setTime(Date date,int hour,int minute,int second) {
		date.setHours(hour);
		date.setMinutes(minute);
		date.setSeconds(second);
		return date;
	}
	
	/**清除date的时间，即00:00:00,000*/
	public static Date getStartDate(Date date) {
		if(date==null)
			return date;
		else {
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);
			return cal.getTime();
		}
	}
	
	/**获取date的最后一毫秒，即把时间写死为23:59:59,999*/
	public static Date getEndDate(Date d){
		if(d==null)
			return d;
		else {
			Calendar cal=Calendar.getInstance();
			cal.setTime(d);
			cal.set(Calendar.HOUR_OF_DAY,23);
			cal.set(Calendar.MINUTE,59);
			cal.set(Calendar.SECOND,59);
			cal.set(Calendar.MILLISECOND,999);
			return cal.getTime();
		}
	}

  public static String getStandardTimeFormat(Date date) {
  	return formatDate(date);
 }
  
  private static String getFourYearTime(String value){
		if(value.matches("\\d{2}-.*")){
				return "20".concat(value);
		}else
			return value;
	}
  
  public static boolean isDate(String value){
		return valueOf(value)!=null;
		
  }
  @SuppressWarnings("deprecation") public static Date valueOf(Object value){
  	if(value==null)
  		return null;
  	else if(value instanceof Date)
  		return (Date)value;
  	else if(value instanceof LocalDate)
  		return Date.from(((LocalDate)value).atStartOfDay(ZoneId.systemDefault()).toInstant());
		else if(value instanceof String){
			String datestr=(String)value;
			if(EasyObj.isBlank(datestr))
				return null;
			String standardDateTime=null;
			if(datestr.indexOf("CST")>=0||datestr.indexOf("GMT")>=0)
				return new Date(datestr);
			else if(datestr.indexOf('/')>0)
				standardDateTime=convertHKDateTime(datestr);
			else
				standardDateTime=convertPRCDateTime(datestr);
			return getDate(standardDateTime,DATETIME_FORMAT);
		}
  	return null;
  }
  
  public static String getStandardTimeFormat(String value) throws ParseException{
    if(value.trim().length()==0)
      throw new ParseException("日期不能为空");
    String originalvalue=value;
    //value=EasyStr.ReplaceAll(value,"/","-",false).trim();
    value=EasyStr.ReplaceAll(value,"  "," ",false,true);
    if((value==null||value.length()<8||value.length()>19)
        ||(value.length()==8&&value.indexOf("-")>=0&&value.indexOf("-0")>0)){
      throw new ParseException("日期:"+value+"格式有误,正确的格式:2004-09-22 00:00:00 ");
      // return "1900-01-01 00:00:00";
    }
    value=getFourYearTime(value);
    String result="1900-01-01 00:00:00";
    if(value.length()==8&&value.indexOf("-")<0)
      value=value.substring(0,4)+"-"+value.substring(4,6)+"-"+value.substring(6,8)+" 00:00:00";
    if(value.length()==19)
      return value;
    int ipos=0;
    String year=null;
    String month=null;
    String date=null;
    String hour=null;
    String minute=null;
    String second=null;
    try{
      ipos=value.indexOf("-");
      if(ipos>0){
        year=value.substring(0,ipos);
        value=value.substring(ipos+1,value.length());
        ipos=value.indexOf("-");
        month=EasyStr.insertZero(value.substring(0,ipos),2);
        value=value.substring(ipos+1,value.length());
        if(value.indexOf(" ")<0)
          value=value.concat(" ");
        ipos=value.indexOf(" ");
        date=EasyStr.insertZero(value.substring(0,ipos),2);
        value=value.substring(ipos+1,value.length());
      }
      else{
        year=value.substring(0,4);
        month=value.substring(4,6);
        date=value.substring(6,8);
        value=value.substring(8);
      }
      if(EasyMath.isNumeric(value)){
        hour=value.substring(0,2);
        minute=value.substring(2,4);
        second=value.substring(4,6);
      }
      else{
        ipos=value.indexOf(":");
        if(ipos>=0)
          hour=EasyStr.insertZero(value.substring(0,value.indexOf(":")),2);
        else
          hour="00";
        value=value.substring(value.indexOf(":")+1,value.length());
        ipos=value.indexOf(":");
        if(ipos>=0)
          minute=EasyStr.insertZero(value.substring(0,value.indexOf(":")),2);
        else if(value.length()>0)
          minute=EasyStr.insertZero(value.trim(),2);
        else
          minute="00";
        value=value.indexOf(":")>=0?value.substring(value.indexOf(":")+1,value.length()):"00";
        if(value.matches("[0-9,\\s]*"))//numeric and whitespace
          second=EasyStr.insertZero(value.trim(),2);
      }
      result=year.concat("-")+month.concat("-")+date.concat(" ")+hour.concat(":")+minute.concat(":")+second;
    }
    catch(Exception ex){
      throw new ParseException("日期格式有误"+originalvalue+":"+ex.toString()+".");
      //      return "1900-01-01 00:00:00";
    }
    return result;
  }
  
  public static String convertPRCDateTime(String value){
  	String result;//"2009-03-01 01:02:03"
  	String regex=value.indexOf("-")>=0?"(\\d{4})-?(\\d{1,2})-?(\\d{1,2}) ?(\\d{0,2}):?(\\d{0,2}):?(\\d{0,2})":"(\\d{4})(\\d{0,2})(\\d{0,2})(\\d{0,2})(\\d{0,2})(\\d{0,2})";
		try{
			result=(new RegexReplace(value,regex){
				@Override public String getReplacement(Matcher m){
					//System.out.println(EasyStr.concat(m.group(1),m.group(2),m.group(3),m.group(4),m.group(5),m.group(6)));
					return EasyStr.concat(m.group(1),"-"
							,m.group(2).length()>0?EasyStr.insertZero(m.group(2),2):"01","-"
							,m.group(3).length()>0?EasyStr.insertZero(m.group(3),2):"01"," "
							,m.group(4)!=null?EasyStr.insertZero(m.group(4),2):"00",":"
							,m.group(5)!=null?EasyStr.insertZero(m.group(5),2):"00",":"
							,m.group(6)!=null?EasyStr.insertZero(m.group(6),2):"00");
				}
			}).replaceAll();
	  	return result;
		}catch(Exception e){
			throw new ParseRuntimeException(e.toString());
		}
  }
	
  public static String convertHKDateTime(String value){
  	String result;//"01/03/2009 01:02:03"
		try{
			result=(new RegexReplace(value,"(\\d{1,2})/(\\d{1,2})/(\\d{4}) ?(\\d{0,2}):?(\\d{0,2}):?(\\d{0,2})"){
				@Override public String getReplacement(Matcher m){
					//System.out.println(EasyStr.concat(m.group(1),m.group(2),m.group(3),m.group(4),m.group(5),m.group(6)));
					return EasyStr.concat(m.group(3),"-",m.group(2),"-",m.group(1)," "
							,m.group(4)!=null?EasyStr.insertZero(m.group(4),2):"00",":"
							,m.group(5)!=null?EasyStr.insertZero(m.group(5),2):"00",":"
							,m.group(6)!=null?EasyStr.insertZero(m.group(6),2):"00");
				}
			}).replaceAll();
	  	return result;
		}catch(Exception e){
			throw new ParseRuntimeException(e.toString());
		}
  }
  
	/**日期转指定格式字符串*/
  public static String formatDate(Date date) {
  	if(date==null)return null;
 	 	return new SimpleDateFormat(DATETIME_FORMAT).format(date);
  }
  
  /**手工指定日期模板，格式化日期*/
  public static String formatDate(Date date,String pattern) {
  	if(date==null)return null;
 	 	return new SimpleDateFormat(pattern).format(date);
  }
  /**判断时分秒是否为空，只是日期类型*/
  public static boolean isEmptyTime(Date date) {
  	return formatDate(date,"HH:mm:ss,SSS").equals("00:00:00,000");
  }
  
  /**日期增加天数，也可以用负数表示减少日期*/
	public static Date addDate(Date date, int value) {
		return addDate(date,Calendar.DATE,value);
	}

	/**手工指定是时间计量单位（天数、月份、日期、时分秒）的增加或减少*/
	public static Date addDate(Date date, int field, int value) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(field, value);
		if(date.getClass().equals(java.util.Date.class))
			return new java.util.Date(c.getTimeInMillis());
		else if(date.getClass().equals(java.sql.Date.class))
			return new java.sql.Date(c.getTimeInMillis());
		else if(date.getClass().equals(java.sql.Timestamp.class))
			return new java.sql.Timestamp(c.getTimeInMillis());
		else
			return c.getTime();
	}
  
	static public Date addDate(Date date,int iYear,int iMonth,int iDate){
		GregorianCalendar newDate=new GregorianCalendar();
		newDate.setTime(date);
		newDate.add(GregorianCalendar.YEAR,iYear);
		newDate.add(GregorianCalendar.MONTH,iMonth);
		newDate.add(GregorianCalendar.DATE,iDate);
		return newDate.getTime();
	}
	
	static public Date addDate(Date date,int iYear,int iMonth,int iDate,int iHour,int iMinute,int iSecond){
		GregorianCalendar newDate=new GregorianCalendar();
		newDate.setTime(date);
		newDate.add(GregorianCalendar.YEAR,iYear);
		newDate.add(GregorianCalendar.MONTH,iMonth);
		newDate.add(GregorianCalendar.DATE,iDate);
		newDate.add(GregorianCalendar.HOUR,iHour);
		newDate.add(GregorianCalendar.MINUTE,iMinute);
		newDate.add(GregorianCalendar.SECOND,iSecond);
		return newDate.getTime();
	}

	/**计算年龄（以当前日期为准）*/
	static public int computeAge(Date birthday){
		 return computeAge(new Date(), birthday);
	}
	
	/**计算年龄*/
	@SuppressWarnings("deprecation")
	static public int computeAge(Date now,Date birthday) {
		int age = now.getYear()-birthday.getYear();
		if(birthday.getMonth()>now.getMonth()||(birthday.getMonth()==now.getMonth()&&birthday.getDate()>now.getDate()))
			age--;
		return age;
	}
	
	/**格式化耗费时间*/
	public static String formatElapse(long ms) {
		long elapse=ms;
		String result="0毫秒";
		int SSS=(int)(elapse % 1000);
		if(SSS>0) {
			result=SSS+"毫秒";
		}
		if(SSS<elapse) {
			elapse=elapse/1000;
			int ss=(int)(elapse % 60);
			result=ss+"秒"+result;
			if(ss<elapse) {
				elapse=elapse/60;
				int mm=(int)(elapse % 60);
				result=mm+"分"+result;
				if(mm<elapse){
					elapse=elapse/60;
					int HH=(int)(elapse % 60);
					result=HH+"小时"+result;
					if(HH<elapse) {
						elapse=elapse/24;
						int dd=(int)(elapse % 24);
						result=dd+"天"+result;
					}
				}
			}
		}
		return result;
	}

	/**格式化耗费时间, hideTime 顺序为由小到大，即：毫秒->秒->分->时*/
	public static String formatElapse(long ms,boolean... showTime) {
		long elapse=ms;
		String result="";
		int SSS=(int)(elapse % 1000);
		if(showTime.length<1||showTime[0]) {
			if(SSS>0) 
				result=SSS+"毫秒";
		}
		if(SSS<elapse) {
			elapse=elapse/1000;
			int ss=(int)(elapse % 60);
			if(showTime.length<2||showTime[1]) {
				if(ss>0)
					result=ss+"秒"+result;
			}
			if(ss<elapse) {
				elapse=elapse/60;
				int mm=(int)(elapse % 60);
				if(showTime.length<3||showTime[2]) {
					result=mm+"分"+result;
				}
				if(mm<elapse){
					elapse=elapse/60;
					int HH=(int)(elapse % 60);
					if(showTime.length<4||showTime[4]) {
						result=HH+"小时"+result;
					}
					if(HH<elapse) {
						elapse=elapse/24;
						int dd=(int)(elapse % 24);
						result=dd+"天"+result;
					}
				}
			}
		}
		return result;
	}
	
	public static long time2sec(String time) {
		long result=(long)esdk.time.addDate(esdk.time.getDate(time,"HH:mm:ss"),Calendar.HOUR,8).getTime()/1000; //23:59:59
		return result;
	}
	
	public static long time2sec(String time,String pattern) {
		int result=(int)esdk.time.addDate(esdk.time.getDate(time,pattern),Calendar.HOUR,8).getTime()/1000; //23:59:59
		return result;
	}

	public static String sec2time(long second) {
		return formatDate(esdk.time.addDate(new Date(second*1000),Calendar.HOUR,-8),"HH:mm:ss");
	}

	public static String sec2time(long second,String pattern) {
		return formatDate(esdk.time.addDate(new Date(second*1000),Calendar.HOUR,-8),pattern);
	}
	
	@SuppressWarnings("deprecation") public static int getYear(Date date) {
		return date.getYear()+1900;
	}
	
	@SuppressWarnings("deprecation") public static int getMonth(Date date) {
		return date.getMonth()+1;
	}
	
	@SuppressWarnings("deprecation") public static int getYear() {
		return new Date().getYear()+1900;
	}
	
	@SuppressWarnings("deprecation") public static int getMonth() {
		return new Date().getMonth()+1;
	}
	
	@SuppressWarnings("deprecation") public static int getDate(Date date) {
		return date.getDate();
	}
	
	@SuppressWarnings("deprecation") public static int getDate() {
		return new Date().getDate();
	}
	
	/**获得星期几*/
	public static int getDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(cal.DAY_OF_WEEK);
	}
	
	/**
	  * 得到某年某月的第一天
	  * 
	  * @param year
	  * @param month
	  * @return
	  */
	 public static String getFirstDayOfMonth(int year, int month,String format) {
	  Calendar cal = Calendar.getInstance();
	  cal.set(Calendar.YEAR, year);
	  cal.set(Calendar.MONTH, month-1);
	  cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
	  return new SimpleDateFormat(format).format(cal.getTime());
	 }
	 
	 /**
	  * 得到某年某月的最后一天
	  * 
	  * @param year
	  * @param month
	  * @return
	  */ 
	 public static String getLastDayOfMonth(int year, int month,String format) {
	  Calendar cal = Calendar.getInstance();
	  cal.set(Calendar.YEAR, year);
	  cal.set(Calendar.MONTH, month-1);
	  cal.set(Calendar.DAY_OF_MONTH, 1);
	  int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	  cal.set(Calendar.DAY_OF_MONTH, value);
	  return new SimpleDateFormat(format).format(cal.getTime());
	 }
	 
	 /**
	  * 得到某年某月的最后一天
	  * 
	  * @param year
	  * @param month
	  * @return
	  */ 
	 public static Date getLastDateOfMonth(int year, int month) {
	  Calendar cal = Calendar.getInstance();
	  cal.set(Calendar.YEAR, year);
	  cal.set(Calendar.MONTH, month-1);
	  cal.set(Calendar.DAY_OF_MONTH, 1);
	  cal.set(Calendar.HOUR_OF_DAY, 23);
	  cal.set(Calendar.MINUTE, 59);
	  cal.set(Calendar.SECOND, 59);
	  int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	  cal.set(Calendar.DAY_OF_MONTH, value);
	  return cal.getTime();
	 }

		public static String getTime(String dateformat,long date){
			return formatDate(new Date(date),dateformat);
		}

		public static String getBeforeMonth(String dateformat,int value){
			return getAfterMonth(dateformat,-value);
		}

		public static String getAfterMonth(String dateformat,int value){
			return getAfterTime(dateformat,Calendar.MONTH,value);
		}

		public static String getBeforeTime(String dateformat,int date){
			return getAfterTime(dateformat,-date);
		}

		public static String getAfterTime(String dateformat,int date){
			return getAfterTime(dateformat,Calendar.DATE,date);
		}

		public static String getAfterTime(String dateformat,int field,int value){
			String result;
			java.text.SimpleDateFormat formatter=new java.text.SimpleDateFormat(dateformat); // value="yyMMdd HHmmss"
			Calendar cal=Calendar.getInstance();
			cal.add(field,value);
			result=formatter.format(cal.getTime());
			return result;
		}

		public static String getIntervalTime(String dateformat,String formatTime,int field,int value) throws java.text.ParseException{
			java.text.SimpleDateFormat formatter=new java.text.SimpleDateFormat(dateformat); // value="yyMMdd HHmmss"
			Date date=formatter.parse(formatTime);
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			cal.add(field,value);
			String result=formatter.format(cal.getTime());
			return result;
		}
}
