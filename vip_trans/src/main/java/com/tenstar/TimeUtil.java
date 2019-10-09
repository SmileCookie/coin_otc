package com.tenstar;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtil
{
	private final static Logger log = Logger.getLogger(TimeUtil.class);

  public static Timestamp getNow()
  {
    return new Timestamp(System.currentTimeMillis());
  }
  /***
	 * get now time
	 * @return Timestamp
	 */
	@Deprecated
	public static Timestamp getTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	public static Timestamp getToday0Show(Timestamp time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+" 00:00:00");
	}
	/**
	 * 返回一个日期，只用来做显示
	 * 
	 * @param nowDate
	 * @return
	 */
	public static Date getStringToDate(String nowDate) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return sdf.parse(nowDate);
		} catch (Exception e) {
			log.error(e.toString(), e);
			return null;
		}
	}

	// 获得本周一的日期
	public static String getMondayOFWeek() {
		GregorianCalendar cal = new GregorianCalendar();
		Date now = new Date();
		cal.setTime(now);
		cal.setFirstDayOfWeek(GregorianCalendar.MONDAY); // 设置一个星期的第一天为星期1，默认是星期日
		cal.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);

		SimpleDateFormat dateutil = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return dateutil.format(cal.getTime()).toString(); // 本周1
	}
	
	// 获取当月第一天
	public static String getFirstDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-01 00:00:00";
	}

	/**
	 * 得到当前时间的前多少天
	 */
	public static Timestamp getBeforeTime(int day){
		Calendar objCal = Calendar.getInstance();
		objCal.add(Calendar.DATE, day);
		Timestamp date = new Timestamp(objCal.getTimeInMillis());
		return date;
	}

	
	public static Timestamp getAfterDayTime(Timestamp date, int day){
		Calendar objCal = Calendar.getInstance();
		objCal.setTime(date);
		objCal.add(Calendar.DATE, day);
		return new Timestamp(objCal.getTimeInMillis());
	}
	
	public static Timestamp getZero(){
		return Timestamp.valueOf("1970-01-01 00:00:00");
	}
	// 获取今天开始时间
		public static String getToday() {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

			return sdf.format(cal.getTime());
		}

		// 获取昨天开始时间
		public static String getyesToday() {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			return sf2.format(cal.getTime());
		}
	/**
	 * 获取这个月的第一天起始位置
	 * @return Timestamp 比如：2012-05-01 00:00:00
	 */
	public static Timestamp getMouthFirst(){
		Timestamp time=new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+"-01 00:00:00");
	}
	/**
	 * 获取这天的开始时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static Timestamp getTodayFirst(){
		Timestamp time=new Timestamp(System.currentTimeMillis());
		return  getTodayFirst(time);
	}
	/**
	 * 获取这天的开始时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static Timestamp getTodayFirst(Timestamp time){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+" 00:00:00");
	}
	/**
	 * 获取这天的开始时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static long getTodayFirst(long time){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+" 00:00:00").getTime();
	}
	/**
	 * 获取这天的结束时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static Timestamp getTodayLast(Timestamp time){
		time=getAfterDayDate(time,1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+" 00:00:00");
	}
	/**
	 * 获取这天的结束时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static long getTodayLast(long time){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+" 23:59:59").getTime();
	}
	public static String jsDate(Timestamp time){
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM dd,yyyy 00:mm:ss");
		return sdf.format(time);
		
	}
	/**
	 * 获取这小时的开始时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static Timestamp getHourFirst(){
		Timestamp time=new Timestamp(System.currentTimeMillis());
		return getHourFirst(time);
	}
	/**
	 * 获取这小时的开始时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static long getHourFirstLong(){
		
		return getHourFirst(System.currentTimeMillis());
	}
	/**
	 * 获取这小时的开始时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static Timestamp getHourFirst(Timestamp time){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+":00:00");
	}
	/**
	 * 获取这小时的开始时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static long getHourFirst(long time){
		Timestamp date = new Timestamp(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
		String dayStr=sdf.format(date);
		return Timestamp.valueOf(dayStr+":00:00").getTime();
	}
	/**
	 * 获取这小时的开始时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static Timestamp getHourLast(){
		Timestamp time=new Timestamp(System.currentTimeMillis());
		return getHourLast(time);
	}
	/**
	 * 获取这小时的开始时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static Timestamp getHourLast(Timestamp time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+":59:59");
	}
	/**
	 * 获取这小时的结束时间
	 * @return Timestamp 比如：2012-05-01 13:00:00
	 */
	public static long getHourLast(long time){
		//Timestamp date=new Timestamp(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+":59:59").getTime();
	}
	/**
	 * 获取这分钟的开始时间
	 * @return Timestamp 比如：2012-05-01 13:21:00
	 */
	public static Timestamp getMinuteFirst(){
		Timestamp time=new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dayStr=sdf.format(time);
		return Timestamp.valueOf(dayStr+":00");
	}
	/**
	 * 获取这分钟的开始时间
	 * @return Timestamp 比如：2012-05-01 13:21:00
	 */
	public static Timestamp getMinuteFirst(long times){
		//Timestamp time=new Timestamp(times);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dayStr=sdf.format(times);
		return Timestamp.valueOf(dayStr+":00");
	}
	/**
	 * 获取0-1440的一个分钟数
	 * @return Timestamp 比如：2012-05-01 13:21:00
	 */
	public static int getMinuteFirstInDay(long times){
		Timestamp time=new Timestamp(times);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String dayStr=sdf.format(time);
		String sp[]=dayStr.split(":");
		return Integer.parseInt(sp[0])*60+Integer.parseInt(sp[1]);
	}
	
	/**
	 * 获取0-1440*60的一个秒数，在一天当中的秒数
	 * @return Timestamp 比如：2012-05-01 13:21:00
	 */
	public static int getSecondInDay(long times){
		Timestamp time=new Timestamp(times);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String dayStr=sdf.format(time);
		String sp[]=dayStr.split(":");
		return Integer.parseInt(sp[0])*60*60+Integer.parseInt(sp[1])*60+Integer.parseInt(sp[2]);
	}
	
	/**
	 * 获取与2013年1月1日之间的天数差
	 * @return Timestamp 比如：2012-05-01 13:21:00
	 */
	public static long getDayInYear(long times){
		try{
		Timestamp time=new Timestamp(times);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd");
		String date2="2013-01-01";
		 Date d2=sdf.parse(date2);
		 
		 long daysBetween=(time.getTime()-d2.getTime()+1000000)/(3600*24*1000);
		 
		 return  daysBetween;
		
		}catch(Exception ex){
			log.error(ex.toString(), ex);
			return -1;
		}
	}
	
  public static Timestamp getAfterSecDate(Timestamp date, int second)
  {
    Calendar objCal = Calendar.getInstance();
    objCal.setTime(date);
    objCal.add(13, second);
    Timestamp ndate = new Timestamp(objCal.getTimeInMillis());
    return ndate;
  }

	/**
	 * 得到当前时间的后n天
	 * 可以传递负值
	 */
	public static Timestamp getAfterDayTime(Date d,int n){
		Calendar objCal = Calendar.getInstance();
		objCal.setTime(d);
		objCal.add(Calendar.DATE, n);
		Timestamp date = new Timestamp(objCal.getTimeInMillis());
		return date;
	}
  public static Timestamp getAfterDayDate(Timestamp date, int day) {
    Calendar objCal = Calendar.getInstance();
    objCal.setTime(date);
    objCal.add(5, day);
    Timestamp ndate = new Timestamp(objCal.getTimeInMillis());
    return ndate;
  }

  public static Timestamp getAfterWeekDate(Timestamp date, int day) {
    Calendar objCal = Calendar.getInstance();
    objCal.setTime(date);
    objCal.add(4, day);
    Timestamp ndate = new Timestamp(objCal.getTimeInMillis());
    return ndate;
  }

  public static Timestamp getAfterMonthDate(Timestamp date, int day) {
    Calendar objCal = Calendar.getInstance();
    objCal.setTime(date);
    objCal.add(2, day);
    Timestamp ndate = new Timestamp(objCal.getTimeInMillis());
    return ndate;
  }

	/***
	 * before 到 to 还差的  天 时 分 秒
	 * @param before
	 * @param to
	 * @return
	 */
	public static String getDhms(Timestamp before , Timestamp to){
		long total_time = to.getTime() - before.getTime();
		return getShowByCha(total_time);
	}

	public static String getShowByCha(long cha){
		
		long day=cha/(24*60*60*1000); 
		long hour=(cha/(60*60*1000)-day*24); 
		long min=((cha/(60*1000))-day*24*60-hour*60); 
		long s=(cha/1000-day*24*60*60-hour*60*60-min*60); 

		String strHour=hour<10?("0"+hour).toString():String.valueOf(hour);
		String strMin=min<10?("0"+min).toString():String.valueOf(min);
		String strS=s<10?("0"+s).toString():String.valueOf(s);
		
		if(day < 0){
			return null;
		}
		
		return ""+day+"天"+strHour+"小时"+strMin+"分"+strS+"秒";
	}
	
	public static String getDifferenceTime(Date date) {
		if(date == null)
			return null;

		Date currentDate = new Date();
		long total_time = currentDate.getTime() - date.getTime();
	    // 计算出相差天数
		long days = total_time / (24 * 3600 * 1000);
		// 计算出小时数
		long leave1 = total_time % (24 * 3600 * 1000); // 计算天数后剩余的毫秒数
		long hours = leave1 / (3600 * 1000);
		// 计算相差分钟数
		long leave2 = leave1 % (3600 * 1000); // 计算小时数后剩余的毫秒数
		long minutes = leave2 / (60 * 1000);
		// 计算相差秒数
		long leave3 = leave2 % (60 * 1000); // 计算分钟数后剩余的毫秒数
		long seconds = leave3 / 1000;
		
		if(days > 0){
			if(days > 2){
				return getDateToString(date);
			}else{
				String hm = new SimpleDateFormat("HH:mm").format(date);
				return days == 1 ? "昨天 " + hm : days == 2 ? "前天 " + hm : days + " 天 " + hm;
			}
		}else if(hours > 0){
			return hours + " 小时前";
		}else if(minutes > 0){
			return minutes + " 分钟前";
		}else if(seconds > 0){
			return seconds + " 秒前";
		}
		return null;
	}

	public static String getDateToString(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return sdf.format(date);
		} catch (Exception e) {
			log.error(e.toString(), e);
			return null;
		}
	}
	public static int getIntervalDays(Timestamp startday,Timestamp endday){
		//分别得到两个时间的毫秒数
		long sl=startday.getTime();
		long el=endday.getTime();
		long ei=el-sl;
		//根据毫秒数计算间隔天数
		return (int)(ei/(1000*60*60*24));
	} 
	public static Timestamp getAfterDate(int second) {
	    Calendar objCal = Calendar.getInstance();
	    objCal.setTime(getNow());
	    objCal.add(Calendar.SECOND, second);
	    Timestamp ndate = new Timestamp(objCal.getTimeInMillis());
	    return ndate;
	  }

	/**
	 * 获取昨天某分钟的时间戳
	 * @param h
	 * @param m
	 * @return
	 */
	public static Long getYestodayTime(int h,int m){
		Calendar yesterdayEnd=Calendar.getInstance();
		yesterdayEnd.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		yesterdayEnd.add(Calendar.DATE,0);
		yesterdayEnd.set(Calendar.HOUR_OF_DAY, 00);
		yesterdayEnd.set(Calendar.MINUTE, 00);
		yesterdayEnd.set(Calendar.SECOND, 00);
		yesterdayEnd.set(Calendar.MILLISECOND, 000);
		Date time=yesterdayEnd.getTime();
		return time.getTime();
	}

	/**
	 * 今天开始时间
	 * @return
	 */
	public static Long getStartTime(){
		Calendar todayStart = new GregorianCalendar();
		TimeZone zone = TimeZone.getTimeZone("GMT+8:00");
		todayStart.setTimeZone(zone);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		System.out.println("今天起始时间： "+todayStart.getTime());
		return todayStart.getTime().getTime();
	}

	/**
	 * 判断此时间戳是哪一时刻
	 * @return
	 */
	public static Boolean getNewTime(Long time){
        boolean flag = false;
		for(int h=23;h>0;h--){
			for(int m=59;m>0;m--){
				Long newTime = getYestodayTime(h,m);
                if(time.equals(newTime)){
                    flag = true;
                    break;
                }
			}
            if(flag){
                break;
            }
		}
		return flag;
	}
	public static void main(String[] args) {
//		Timestamp ts = Timestamp.valueOf("2013-11-18 00:00:00");
//		log.info(getAfterDayDate(ts, -1));
//
//		log.info(getAfterDate(60));
//		Long time = 1544496780000L;
//		while (true){
//			Boolean flag = getNewTime(time);
//			if(flag){
//				break;
//			}
//		}
		Long time = getStartTime();
		System.out.println("args = [" + time + "]");
	}
}