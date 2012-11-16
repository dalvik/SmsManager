package com.sky.sms.adv.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *<p>日期时间格式化类</p>
 *<p>Description:将日期时间按照需求进行格式化</p>
 *<p>Copyright: Copyright Love999(c) 2010</p>
 *<p>Company:HangZhou Love999 Technology Development Co.Ltd</p>
 *@author 桑开洋
 *@version 1.0
 */
public class DateUtil {
	
	/*****    有毫秒转换成时间戳   ****/
	public static String formatyyMMDDHHmmss(long time){
		Date date  = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	/*****    有毫秒转换成时间戳   ****/
	public static String formatyyMMDDHHmm(long time){
		Date date  = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}
	
	/*****    有毫秒转换成时间戳   ****/
	public static String formatyyMMDDHHmmss2(long time){
		Date date  = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return sdf.format(date);
	}
	
	/*****    有毫秒转换成时间戳   ****/
	public static String formatYYYYMMDD(long time){
		Date date  = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	/*****    有毫秒转换成时间戳   ****/
	public static String formatHHmmss(long time){
		Date date  = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(date);
	}
	
	/*****    字符串型日期转换为Date型日期   ****/
	public static Date formateStringToDate(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			return new Date();
		}
		return date;
	}

	/*****    HH:mm 型日期转换为当前日期的毫秒数   ****/
	public static long formateTimeToDate(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		String str = getDateToString(date);
		try {
			date = format.parse(str + " " + dateStr);
		} catch (ParseException e) {
			return new Date().getTime();
		}
		return date.getTime();
	}

	/*****   yyyy-MM-dd HH:mm 型日期转换为当前日期的毫秒数   ****/
	public static long formateTimeToDate2(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			return new Date().getTime();
		}
		return date.getTime();
	}
	
	/*****    Date型日期转换为字符串型日期   ****/
	public static String getDateToString(Date datetime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(datetime);
	}
	
	/*****    Date型日期转换为 年-月-日 字符串型   ****/
	public static String parseDateToString(Date datetime){
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
		return sdf.format(datetime);
	}
	
	
	/***    将毫秒数转换成日期类型   **/
	public static Date formatTime(long time){
		Date date  = new Date(time);
		return date;
	}
	
	// 得到今天是周几
	public static int getDayOfWeek() {
		Date date = new Date();
		return date.getDay();
	}
	
}
