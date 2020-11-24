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
package org.apromore.apmlog.util;

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.PLog;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.*;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.io.File;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Chii Chang (11/2019)
 * Modified: Frank Ma (16/11/2019)
 * Modified: Chii Chang (28/01/2020)
 * Modified: Chii Chang (10/11/2020)
 */
public class Util {

    private static final double year = 1000.0D * 60 * 60 * 24 * 365;
    private static final double month = 1000.0D * 60 * 60 * 24 * 30.4166666667;
    private static final double week = 1000.0D * 60 * 60 * 24 * 7;
    private static final double day = 1000.0D * 60 * 60 * 24;
    private static final double hour = 1000.0D *  60 * 60;
    private static final double minute = 1000.0D *  60;
    private static final double second = 1000.0D;

    public static long epochMilliOf(ZonedDateTime zonedDateTime){

        long s = zonedDateTime.toInstant().toEpochMilli();
        return s;
    }

    public static ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        String timestamp = xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();

        timestamp = validateTimestamp(timestamp);

        return ZonedDateTime.parse(timestamp);
    }

    private static String validateTimestamp(String timestamp) {
        //0000-00-00T00:00:00.000+00:00
        String charAt10 = timestamp.substring(10, 11);
        String validTimestamp = timestamp;
        if (charAt10.equals(" ")) {
            validTimestamp = timestamp.substring(0, 10) + "T" + timestamp.substring(11);
        }
        return validTimestamp;
    }

    public static long epochMilliOf(String timestampString){
        return ZonedDateTime.parse(timestampString).toInstant().toEpochMilli();
    }

    public static ZonedDateTime millisecondToZonedDateTime(long millisecond){
        Instant i = Instant.ofEpochMilli(millisecond);
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return z;
    }

    public static String timestampStringOf(ZonedDateTime zdt){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return zdt.format(formatter);
    }

    public static String timestampStringOf(ZonedDateTime zdt, String format){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(format);
        return zdt.format(formatter);
    }


    public static String durationShortStringOf(long millis) {
        double secs = millis / second;
        double mins = millis / minute;
        double hrs = millis / hour;
        double days = millis / day;
        double wks = millis / week;
        double mths = millis / month;
        double yrs = millis / year;

        if (yrs > 1.0D) return df2.format(yrs) + " yrs";
        if (mths > 1.0D) return df2.format(mths) + " mths";
        if (wks > 1.0D) return df2.format(wks) + " wks";
        if (days > 1.0D) return df2.format(days) + " d";
        if (hrs > 1.0D) return df2.format(hrs) + " hrs";
        if (mins > 1.0D) return df2.format(mins) + " mins";
        if (secs > 1.0D) return df2.format(secs) + " secs";
        if (millis > 1.0D) return df2.format(millis) + " millis";
        return "instant";
    }

    public static String durationStringOf(double millis) {

        double secs = millis / second;
        double mins = millis / minute;
        double hrs = millis / hour;
        double days = millis / day;
        double wks = millis / week;
        double mths = millis / month;
        double yrs = millis / year;

        if (yrs > 1)  return df2.format(yrs) + " yrs";
        if (mths > 1) return df2.format(mths) + " mths";
        if (wks > 1) return df2.format(wks) + " wks";
        if (days > 1) return df2.format(days) + " d";
        if (hrs > 1) return df2.format(hrs) + " hrs";
        if (mins > 1) return df2.format(mins) + " mins";
        if (secs > 1) return df2.format(secs) + " secs";
        if (millis > 0) return df2.format(millis) + " millis";

        return "instant";
    }

    public static String durationShortStringOf(double millis) {
        double secs = millis / second;
        double mins = millis / minute;
        double hrs = millis / hour;
        double days = millis / day;
        double wks = millis / week;
        double mths = millis / month;
        double yrs = millis / year;

        if (yrs > 1.0D) return df2.format(yrs) + " yrs";
        if (mths > 1.0D) return df2.format(mths) + " mths";
        if (wks > 1.0D) return df2.format(wks) + " wks";
        if (days > 1.0D) return df2.format(days) + " d";
        if (hrs > 1.0D) return df2.format(hrs) + " hrs";
        if (mins > 1.0D) return df2.format(mins) + " mins";
        if (secs > 1.0D) return df2.format(secs) + " secs";
        if (millis > 1.0D) return df2.format(millis) + " millis";
        return "instant";
    }

    private static final DecimalFormat df2 = new DecimalFormat("###############.##");

    public static boolean isNumeric(String s) {

        if (s.equals("")) return false;

        UnifiedSet<Character> validChars = getValidCharactersOfNumbers();

        if (!String.valueOf(s.charAt(0)).equals("-")) {
            if (!validChars.contains(s.charAt(0))) return false;
        }


        boolean allNum = true;

        if (s.length() > 1) {
            for (int i = 0; i < s.length(); i++) {
                Character c = s.charAt(i);
                if (!validChars.contains(c)) {
                    allNum = false;
                    break;
                }
            }
        } else {
            if (!validChars.contains(s.charAt(0))) allNum = false;
        }

        return allNum;
    }

    public static UnifiedSet<Character> getValidCharactersOfNumbers() {

        UnifiedSet<Character> validChar = new UnifiedSet<>();
        validChar.put('0');
        validChar.put('1');
        validChar.put('2');
        validChar.put('3');
        validChar.put('4');
        validChar.put('5');
        validChar.put('6');
        validChar.put('7');
        validChar.put('8');
        validChar.put('9');
        validChar.put('.');

        return validChar;
    }



}
