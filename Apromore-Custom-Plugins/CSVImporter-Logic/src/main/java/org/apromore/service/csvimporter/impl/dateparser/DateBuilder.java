/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
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
package org.apromore.service.csvimporter.impl.dateparser;

import lombok.Getter;
import lombok.Setter;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * This DateTime used for caching the properties of parser.
 *
 * @author sulin
 * @since 2019-09-12 14:58:15
 */
@Getter
@Setter
final class DateBuilder {

    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    private static final ZoneOffset DEFAULT_OFFSET = OffsetDateTime.now().getOffset();

    int week;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
    int ns;
    long unixsecond;

    boolean zoneOffsetSetted;
    int zoneOffset;
    TimeZone zone;

    boolean am;
    boolean pm;

    /**
     * Reset this instance, clear all fields to be default value.
     */
    void reset() {
        this.week = 1;
        this.year = 0;
        this.month = 1;
        this.day = 1;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        this.ns = 0;
        this.unixsecond = 0;
        this.am = false;
        this.pm = false;
        this.zoneOffsetSetted = false;
        this.zoneOffset = 0;
        this.zone = null;
    }

    /**
     * Convert this instance into Date
     */
    Date toDate() {
        if (!zoneOffsetSetted) {
            return toCalendar().getTime();
        }
        LocalDateTime dateTime = toLocalDateTime();
        long second = dateTime.toEpochSecond(DEFAULT_OFFSET);
        long ns = dateTime.getNano();
        return new Date(second * 1000 + ns / 1000000);
    }

    /**
     * Convert this instance into Calendar
     */
    Calendar toCalendar() {
        this.prepare();
        Calendar calendar = Calendar.getInstance();
        if (unixsecond != 0) {
            calendar.setTimeInMillis(unixsecond * 1000 + ns / 1000000);
            return calendar;
        }
        if (zone != null) {
            calendar.setTimeZone(zone);
        }
        if (zoneOffsetSetted) {
            String[] ids = TimeZone.getAvailableIDs(zoneOffset * 60000);
            if (ids.length == 0) {
                throw new DateTimeException("Can't build Calendar, because the zoneOffset[" + zoneOffset
                        + "] can't be converted to an valid TimeZone.");
            }
            calendar.setTimeZone(TimeZone.getTimeZone(ids[0]));
        }
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, ns / 1000000);
        return calendar;
    }

    /**
     * Convert this instance into LocalDateTime
     */
    LocalDateTime toLocalDateTime() {
        this.prepare();
        if (unixsecond > 0) {
            return LocalDateTime.ofEpochSecond(unixsecond, ns, ZoneOffset.UTC);
        }
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second, ns);
        int zoneSecond = 0;
        // with TimeZone
        if (zone != null) {
            zoneSecond = (TimeZone.getDefault().getRawOffset() - zone.getRawOffset()) / 1000;
        }
        // with ZoneOffset
        if (zoneOffsetSetted) {
            zoneSecond = TimeZone.getDefault().getRawOffset() / 1000 - zoneOffset * 60;
        }
        return zoneSecond == 0 ? dateTime : dateTime.plusSeconds(zoneSecond);
    }

    /**
     * Convert this instance into OffsetDateTime
     */
    OffsetDateTime toOffsetDateTime() {
        this.prepare();
        if (unixsecond > 0) {
            return OffsetDateTime.ofInstant(Instant.ofEpochSecond(unixsecond, ns), UTC_ZONE_ID);
        }
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second, ns);
        // with ZoneOffset
        if (zoneOffsetSetted) {
            ZoneOffset offset = ZoneOffset.ofHoursMinutes(zoneOffset / 60, Math.abs(zoneOffset % 60));
            return dateTime.atOffset(offset);
        }
        // with TimeZone
        if (zone != null) {
            return dateTime.atZone(zone.toZoneId()).toOffsetDateTime();
        }
        // with default
        return dateTime.atZone(ZoneOffset.ofHoursMinutes(0, 0)).toOffsetDateTime();
    }

    /**
     * Prepare this builder
     */
    private void prepare() {
        if (am && hour == 12) {
            this.hour = 0;
        }
        if (pm && hour != 12) {
            this.hour += 12;
        }
    }

}
