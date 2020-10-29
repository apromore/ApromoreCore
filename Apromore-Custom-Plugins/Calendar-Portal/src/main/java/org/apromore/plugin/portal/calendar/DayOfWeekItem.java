/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.plugin.portal.calendar;

import java.util.Date;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.DayOfWeek;
import java.text.SimpleDateFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import org.apromore.commons.datetime.TimeUtils;
import org.apromore.plugin.portal.calendar.TimeRange;
import org.apromore.plugin.portal.calendar.Constants;

/**
 * UI model for calendar weekday
 *
 * Note: We need to use Date for ZK
 */
public class DayOfWeekItem {

    static final String WEEKDAY_FORMAT = "EEEE";
    static final DateTimeFormatter WEEKDAY_FORMATTER = DateTimeFormatter.ofPattern(WEEKDAY_FORMAT);

    @Getter @Setter private String label;
    @Getter @Setter private Date date;
    @Getter @Setter private List<TimeRange> ranges;
    @Getter @Setter private DayOfWeek dayOfWeek;
    @Getter @Setter private Boolean workday;

    public DayOfWeekItem(DayOfWeek dayOfWeek, Boolean workday, List<TimeRange> ranges) {
        this.dayOfWeek = dayOfWeek;
        this.workday = workday;
        this.ranges = ranges;

        LocalDate localDate = Constants.LOCAL_DATE_REF.with(TemporalAdjusters.next(dayOfWeek));
        this.label = localDate.format(WEEKDAY_FORMATTER);
        this.date = TimeUtils.localDateToDate(localDate);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DayOfWeekItem)){
            return false;
        }
        DayOfWeekItem dow = (DayOfWeekItem) obj;

        return (this.dayOfWeek.equals(dow.dayOfWeek));
    }

    @Override
    public int hashCode() {
        if (dayOfWeek == null) {
            return 0;
        }
        return dayOfWeek.getValue();
    }

    public void updateRange(
            TimeRange range,
            int startHour,
            int startMin,
            int endHour,
            int endMin
    ) {
        if (startHour >= 0) {
            range.updateStartTime(startHour, startMin);
        }
        if (endHour  >= 0) {
            range.updateEndTime(endHour, endMin);
        }
    }

    public void updateRange(TimeRange range, Date start, Date end) {
        int startHour = start.getHours();
        int startMin = start.getMinutes();
        int endHour = end.getHours();
        int endMin = end.getMinutes();
        updateRange(range, startHour, startMin, endHour, endMin);
    }

}
