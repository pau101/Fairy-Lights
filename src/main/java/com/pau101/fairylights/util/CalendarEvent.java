package com.pau101.fairylights.util;

import java.util.Calendar;

public class CalendarEvent {
	private long date;

	private long lengthMillis;

	public CalendarEvent(Calendar calendar) {
		date = calendar.getTimeInMillis();
	}

	public boolean isOcurringNow() {
		long now = System.currentTimeMillis();
		return now >= date && now < date + lengthMillis;
	}

	public void setLengthDays(float lengthDays) {
		setLengthMillis((long) (lengthDays * 86400000.0));
	}

	public void setLengthHours(float lengthHours) {
		setLengthMillis((long) (lengthHours * 3600000.0));
	}

	public void setLengthMillis(long lengthMillis) {
		this.lengthMillis = lengthMillis;
	}

	public long timeUntil() {
		return date - System.currentTimeMillis();
	}
}
