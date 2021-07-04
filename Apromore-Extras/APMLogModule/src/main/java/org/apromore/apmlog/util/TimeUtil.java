/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
 */
public class TimeUtil {

    public static long epochMilliOf(ZonedDateTime zonedDateTime){
        long s = zonedDateTime.toInstant().toEpochMilli();
        return s;
    }
    public static ZonedDateTime millisecondToZonedDateTime(long millisecond){
        Instant i = Instant.ofEpochMilli(millisecond);
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return z;
    }
    public static ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        String timestamp = xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();
        ZonedDateTime z = ZonedDateTime.parse(timestamp);
        return z;
    }

    public static String convertTimestamp(long milliseconds) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS"));
    }

    public static String durationStringOf(long millis) {

        long yr = Long.valueOf("31536000000");
        long mth = Long.valueOf("2678400000");
        long wks = Long.valueOf("604800000");
        long days = Long.valueOf("86400000");
        long hrs = Long.valueOf("3600000");
        long mins = Long.valueOf("60000");
        long secs = Long.valueOf("1000");

        if (millis >  yr) {
            long yrPart =  millis / (long) yr;
            long remainder = millis % (long) yr;
            long mthPart = remainder / (long) mth;
            long dPart = remainder / (long) days;
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

        if (millis >  mth) {
            long mthPart =  millis / (long) mth;
            long remainder = millis % (long) mth;
            long dPart = remainder / (long) days;
            String output = "";
            if (mthPart == 1) output += mthPart + " month ";
            if (mthPart > 1) output += mthPart + " months ";
            if (dPart == 1) output += dPart + " day";
            if (dPart > 1) output += dPart + " days";

            return output;
        }

        if (millis >  wks) {
            long wksPart =  millis / (long) wks;
            long remainder = millis % (long) wks;
            long dPart = remainder / (long) days;
            String output = "";
            if (wksPart == 1) output += wksPart + " week ";
            if (wksPart > 1) output += wksPart + " weeks ";
            if (dPart == 1) output += dPart + " day";
            if (dPart > 1) output += dPart + " days";

            return output;
        }

        if (millis >  days) {
            long dPart =  millis / (long) days;
            long remainder = millis % (long) days;
            long hrPart = remainder / (long) hrs;
            String output = "";
            if (dPart == 1) output += dPart + " day ";
            if (dPart > 1) output += dPart + " days ";
            if (hrPart == 1) output += hrPart + " hour";
            if (hrPart > 1) output += hrPart + " hours";

            return output;
        }

        if (millis >  hrs) {
            long hrPart =  millis / (long) hrs;
            long remainder = millis % (long) hrs;
            long mPart = remainder / (long) mins;
            String output = "";
            if (hrPart  == 1) output += hrPart + " hour ";
            if (hrPart > 1) output += hrPart + " hours ";
            if (mPart == 1) output += mPart + " min";
            if (mPart > 1) output += mPart + " mins";

            return output;
        }

        if (millis >  mins) {
            long minPart =  millis / (long) mins;
            long remainder = millis % (long) mins;
            long secPart = remainder / (long) secs;
            String output = "";
            if (minPart  == 1) output += minPart + " min ";
            if (minPart > 1) output += minPart + " mins ";
            if (secPart == 1) output += secPart + " sec";
            if (secPart > 1) output += secPart + " secs";

            return output;
        }

        if (millis > secs) {
            long secPart =  millis / (long) secs;
            long remainder = millis % (long) secs;
            String output = "";
            if (secPart == 1) output += secPart + " sec ";
            if (secPart > 1) output += secPart + " secs ";
            if (remainder == 1) output += remainder + " milli";
            if (remainder > 1) output += remainder + " millis";

            return output;
        }

        if (millis > 0) {
            return millis + " millis";
        }

        return "instant";
    }

    public static String durationStringOf(double millis, String unit) {
        DecimalFormat df2 = new DecimalFormat("###############.##");

        double yr = Double.valueOf("31536000000");
        double mth = Double.valueOf("2678400000");
        double wks = Double.valueOf("604800000");
        double days = Double.valueOf("86400000");
        double hrs = Double.valueOf("3600000");
        double mins = Double.valueOf("60000");
        double secs = Double.valueOf("1000");

        if (unit.toLowerCase().contains("year") || unit.toLowerCase().contains("yr")) {
            return df2.format(millis / yr) + " years";
        } else if (unit.toLowerCase().contains("month") || unit.toLowerCase().contains("mth")) {
            return df2.format(millis / mth) + " months";
        } else if (unit.toLowerCase().contains("week") || unit.toLowerCase().contains("wk")) {
            return df2.format(millis / wks) + " weeks";
        } else if (unit.toLowerCase().contains("day")) {
            return df2.format(millis / days) + " days";
        } else if (unit.toLowerCase().contains("hour") || unit.toLowerCase().contains("hr")) {
            return df2.format(millis / hrs) + " hours";
        } else if (unit.toLowerCase().contains("minute") || unit.toLowerCase().contains("min")) {
            return df2.format(millis / mins) + " minutes";
        } else if (unit.toLowerCase().contains("second") || unit.toLowerCase().contains("sec")) {
            return df2.format(millis / secs) + " seconds";
        } else if (unit.toLowerCase().contains("milli")) {
            return df2.format(millis ) + " milliseconds";
        }

        return "instant";
    }
}
