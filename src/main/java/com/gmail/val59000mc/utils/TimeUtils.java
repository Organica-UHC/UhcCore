package com.gmail.val59000mc.utils;

public class TimeUtils{

	public static final long SECOND_TICKS = 20L;

	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND*60;
	public static final long HOUR = MINUTE*60;
	public static final long HOUR_2 = HOUR*2;

	public static String getFormattedTime(long time){
		int h,m;
		h = (int) time / (60 * 60);
		time -= h * (60 * 60);
		m = (int) time / 60;
		time -= m * 60;

		String hString = h < 10 ? "0" + h : String.valueOf(h);
		String mString = m < 10 ? "0" + m : String.valueOf(m);
		String sString = time < 10 ? "0" + time : String.valueOf(time);

		return hString + ":" + mString + ":" + sString;
	}

}