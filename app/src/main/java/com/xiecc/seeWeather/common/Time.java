package com.xiecc.seeWeather.common;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowYMDHMSTime(){
		
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String date = mDateFormat.format(new Date());
		return date;
	}
	/**
	 * MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowMDHMSTime(){
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"MM-dd HH:mm:ss");
		String date = mDateFormat.format(new Date());
		return date;
	}
	/**
	 * MM-dd
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowYMD(){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		String date = mDateFormat.format(new Date());
		return date;
	}

	/**
	 * yyyy-MM-dd
	 * @param date
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getYMD(Date date){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		String dateS = mDateFormat.format(date);
		return dateS;
	}
}
