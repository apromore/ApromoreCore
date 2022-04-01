/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.commons.datetime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZoneId;

import org.apromore.commons.datetime.Constants;

/**
 * Various duration utility for newer java.time.*
 */
public final class TimeUtils {

    static final ZoneOffset DEFAULT_ZONE_OFFSET = OffsetDateTime.now().getOffset();

    public static Date localDateAndOffsetTimeToDate(LocalDate localDate, OffsetTime offsetTime) {
        OffsetDateTime offsetDateTime = localDate.atTime(offsetTime);
        long epochMilliseconds = offsetDateTime.toInstant().toEpochMilli();
        return new Date(epochMilliseconds);
    }

    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(
                localDate
                        .atStartOfDay()
                        .atZone(ZoneId.of("UTC"))
                        .toInstant()
        );
    }

    /**
     * Convert Date (assumed in UTC) to LocalDate
     *
     * @param date Date in UTC
     * @return LocalDate
     */
    public static LocalDate utcDateToLocalDate(Date date) {
        return date
                .toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDate();
    }

    /**
     * Get year month and day of Date and plonk them into LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return LocalDate.of(year, month, dayOfMonth);
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date
                .toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
    }

    public static Date localDateAndTimeToDate(LocalDate localDate, int hour, int min) {
        OffsetTime offsetTime = OffsetTime.of(LocalTime.of(hour, min), ZoneOffset.UTC);
        return TimeUtils.localDateAndOffsetTimeToDate(localDate, offsetTime);
    }

}
