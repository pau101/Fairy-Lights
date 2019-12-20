package me.paulf.fairylights.util;

import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

public final class CalendarEvent {
	private final Month month;

	private final int dayStart;

	private final int dayEnd;

	public CalendarEvent(Month month, int dayStart, int dayEnd) {
		this.month = Objects.requireNonNull(month, "month");
		int length = month.maxLength();
		Preconditions.checkArgument(dayStart > 0 && dayStart <= length, "Illegal day for month");
		Preconditions.checkArgument(dayEnd > 0 && dayEnd <= length, "Illegal day for month");
		this.dayStart = dayStart;
		this.dayEnd = dayEnd;
	}

	public boolean isOccurringNow() {
		LocalDate now = LocalDate.now();
		if (now.getMonth() == month) {
			int day = now.getDayOfMonth();
			return day >= dayStart && day <= dayEnd;
		}
		return false;
	}
}
