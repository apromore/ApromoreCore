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
/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.apmlog.util;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Chii Chang (created: 2019)
 * Modified: Chii Chang (23/03/2020)
 * Modified: Chii Chang (01/02/2022)
 */
public class TimeUtil {

    private TimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final Number YEAR = 1000.0D * 60 * 60 * 24 * 365.25;
    public static final Number MONTH = 1000.0D * 60 * 60 * 24 * (365.25 / 12);
    public static final Number WEEK = 1000.0D * 60 * 60 * 24 * 7;
    public static final Number DAY = 1000.0D * 60 * 60 * 24;
    public static final Number HOUR = 1000.0D *  60 * 60;
    public static final Number MINUTE = 1000.0D *  60;
    public static final Number SECOND = 1000.0D;

    public static long epochMilliOf(ZonedDateTime zonedDateTime){
        return zonedDateTime.toInstant().toEpochMilli();
    }
    public static ZonedDateTime millisecondToZonedDateTime(long millisecond){
        Instant i = Instant.ofEpochMilli(millisecond);
        return ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
    }

    public static String millisecondToLocalDateTime(long millisecond) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return millisecondToZonedDateTime(millisecond).format(formatter);
    }

    public static ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        String timestamp = xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();
        return ZonedDateTime.parse(timestamp);
    }

    public static String convertTimestamp(long milliseconds) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS"));
    }

    public static String durationStringOf(long millis) {

        long yr = YEAR.longValue();
        long mth = MONTH.longValue();
        long wks = WEEK.longValue();
        long days = DAY.longValue();
        long hrs = HOUR.longValue();
        long mins = MINUTE.longValue();
        long secs = SECOND.longValue();

        if (millis >  yr) {
            return getYearDurationOutput(millis, yr, mth, days);
        }

        if (millis >  mth) {
            return getDurationOutput(millis, mth, days, "month", "day");
        }

        if (millis >  wks) {
            return getDurationOutput(millis, wks, days, "week", "day");
        }

        if (millis >  days) {
            return getDurationOutput(millis, days, hrs, "day", "hour");
        }

        if (millis >  hrs) {
            return getDurationOutput(millis, hrs, mins, "hour", "min");
        }

        if (millis >  mins) {
            return getDurationOutput(millis, mins, secs, "min", "sec");
        }

        if (millis > secs) {
            return getMillisDurationOutput(millis, secs);
        }

        if (millis > 0) {
            return millis + " millis";
        }

        return "instant";
    }

    private static String getYearDurationOutput(long millis, long yr, long mth, long days) {
        long yrPart =  millis / yr;
        long remainder = millis % yr;
        long mthPart = remainder / mth;
        long dPart = remainder / days;
        String output = "";
        if (yrPart < 2) output += yrPart + " year ";
        if (yrPart > 1) output += yrPart + " years ";
        if (mthPart == 1) output += mthPart + " month ";
        if (mthPart > 1) output += mthPart + " months ";
        if (mthPart == 0) {
            if (dPart == 1) output += dPart + " day";
            if (dPart > 1) output += dPart + " days";
        }
        return output;
    }

    private static String getDurationOutput(long millis, long refVal1, long refVal2, String unit1, String unit2) {
        long part1 =  millis / refVal1;
        long remainder = millis % refVal1;
        long part2 = remainder / refVal2;
        String output = "";
        if (part1  == 1) output += part1 + " "+unit1+" ";
        if (part1 > 1) output += part1 + " "+unit1+"s ";
        if (part2 == 1) output += part2 + " " + unit2;
        if (part2 > 1) output += part2 + " " + unit2 + "s";

        return output;
    }

    private static String getMillisDurationOutput(long millis, long secs) {
        long secPart =  millis / secs;
        long remainder = millis % secs;
        String output = "";
        if (secPart == 1) output += secPart + " sec ";
        if (secPart > 1) output += secPart + " secs ";
        if (remainder == 1) output += remainder + " milli";
        if (remainder > 1) output += remainder + " millis";

        return output;
    }

    public static String durationStringOf(double millis, String unit) {
        DecimalFormat df = Util.getDecimalFormat();

        double yr = YEAR.doubleValue();
        double mth = MONTH.doubleValue();
        double wks = WEEK.doubleValue();
        double days = DAY.doubleValue();
        double hrs = HOUR.doubleValue();
        double mins = MINUTE.doubleValue();
        double secs = SECOND.doubleValue();

        if (unit.toLowerCase().contains("year") || unit.toLowerCase().contains("yr")) {
            return df.format(millis / yr) + " years";
        } else if (unit.toLowerCase().contains("month") || unit.toLowerCase().contains("mth")) {
            return df.format(millis / mth) + " months";
        } else if (unit.toLowerCase().contains("week") || unit.toLowerCase().contains("wk")) {
            return df.format(millis / wks) + " weeks";
        } else if (unit.toLowerCase().contains("day")) {
            return df.format(millis / days) + " days";
        } else if (unit.toLowerCase().contains("hour") || unit.toLowerCase().contains("hr")) {
            return df.format(millis / hrs) + " hours";
        } else if (unit.toLowerCase().contains("minute") || unit.toLowerCase().contains("min")) {
            return df.format(millis / mins) + " minutes";
        } else if (unit.toLowerCase().contains("second") || unit.toLowerCase().contains("sec")) {
            return df.format(millis / secs) + " seconds";
        } else if (unit.toLowerCase().contains("milli")) {
            return df.format(millis ) + " milliseconds";
        }

        return "instant";
    }
}
