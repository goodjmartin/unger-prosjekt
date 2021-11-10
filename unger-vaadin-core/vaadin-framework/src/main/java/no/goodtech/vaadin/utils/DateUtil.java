/*
 * Copyright (c) Cronus Engineering AS
 * Created on       : 16.nov.2004
 * Created by       : Knut Erik Ballestad
 */
package no.goodtech.vaadin.utils;

import java.util.Calendar;
import java.util.Date;

import javax.management.timer.Timer;

/**
 * @deprecated use DateUtil from utils 1.106 when it's available
 */
public class DateUtil {

	/**
	 * @return difference from current system time and GMT in hours 
	 * If daylight savings is active, it will be included in the offset
	 */
	public static long getGmtOffset() {
		return DateUtil.getGmtOffset(new Date());
	}

	/**
	 * @return difference from current time zone and GMT in hours for a specific local timestamp. 
	 * If daylight savings is active, it will be included in the offset
	 */
	public static long getGmtOffset(Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		final int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
		final int dstOffset = calendar.get(Calendar.DST_OFFSET);
		final long gmtOffset = (zoneOffset + dstOffset) / Timer.ONE_HOUR;
		return gmtOffset;
	}
	
	/**
	 * Transforms a timestamp in local timezone to GMT
	 * @param time local timestamp
	 * @return GMT
	 */
	public static Date toGmt(Date time) {
		final long offset = DateUtil.getGmtOffset();
		return new Date(time.getTime() - offset * Timer.ONE_HOUR);
	}
	
	/**
	 * Transforms a timestamp in GMT to local timezone
	 * @param gmtTime timestamp in GMT
	 * @return local time for given timestamp
	 */
	public static Date toLocal(Date gmtTime) {
		return toLocal(gmtTime.getTime());
	}
	
	/**
	 * Transforms a timestamp in GMT to local timezone
	 * @param gmtTimeMillis timestamp in GMT
	 * @return local time for given timestamp
	 */
	public static Date toLocal(long gmtTimeMillis) {
		final long offset = DateUtil.getGmtOffset();
		return new Date(gmtTimeMillis + offset * Timer.ONE_HOUR);
	}
}
