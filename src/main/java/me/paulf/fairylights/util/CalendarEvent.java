package me.paulf.fairylights.util;

import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

public final class CalendarEvent {
    private final Month month;

    private final int dayStart;

    private final int dayEnd;

    public CalendarEvent(final Month month, final int dayStart, final int dayEnd) {
        this.month = Objects.requireNonNull(month, "month");
        final int length = month.maxLength();
        Preconditions.checkArgument(dayStart > 0 && dayStart <= length, "Illegal day for month");
        Preconditions.checkArgument(dayEnd > 0 && dayEnd <= length, "Illegal day for month");
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
    }

    public boolean isOccurringNow() {
        final LocalDate now = LocalDate.now();
        if (now.getMonth() == this.month) {
            final int day = now.getDayOfMonth();
            return day >= this.dayStart && day <= this.dayEnd;
        }
        return false;
    }
}
