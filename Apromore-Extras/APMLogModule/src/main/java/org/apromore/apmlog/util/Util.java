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
package org.apromore.apmlog.util;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.zkoss.zk.ui.Executions;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Chii Chang (11/2019)
 * Modified: Frank Ma (16/11/2019)
 * Modified: Chii Chang (28/01/2020)
 * Modified: Chii Chang (10/11/2020)
 * Modified: Chii Chang (01/02/2022)
 */
public class Util {

    private Util() {
        throw new IllegalStateException("Utility class");
    }

    public static final DecimalFormat df3 = new DecimalFormat("###############.###");
    public static final DecimalFormat df2 = new DecimalFormat("###############.##");
    public static final DecimalFormat df0 = new DecimalFormat("###############");

    public static long epochMilliOf(ZonedDateTime zonedDateTime){
        return zonedDateTime.toInstant().toEpochMilli();
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
        return ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
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

    public static String durationStringOf(double millis) {

        double secs = millis / TimeUtil.SECOND.doubleValue();
        double mins = millis / TimeUtil.MINUTE.doubleValue();
        double hrs = millis / TimeUtil.HOUR.doubleValue();
        double days = millis / TimeUtil.DAY.doubleValue();
        double wks = millis / TimeUtil.WEEK.doubleValue();
        double mths = millis / TimeUtil.MONTH.doubleValue();
        double yrs = millis / TimeUtil.YEAR.doubleValue();

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

    public static boolean isNumeric(String s) {

        if (s == null) return false;
        if (s.equals("")) return false;

        UnifiedSet<Character> validChars = getValidCharactersOfNumbers();

        if (s.toLowerCase().contains("e+")) {
            String replaced = s.toLowerCase().replaceAll("e\\+", "");
            for (int i = 0; i < replaced.length(); i++) {
                if (!validChars.contains(replaced.charAt(i))) return false;
            }
            return true;
        }

        if (s.toLowerCase().contains("e-")) {
            String replaced = s.toLowerCase().replaceAll("e-", "");
            for (int i = 0; i < replaced.length(); i++) {
                if (!validChars.contains(replaced.charAt(i))) return false;
            }
            return true;
        }

        if (!String.valueOf(s.charAt(0)).equals("-")) {
            if (!validChars.contains(s.charAt(0))) return false;
        }


        boolean allNum = true;

        if (s.length() > 1) {
            for (int i = 1; i < s.length(); i++) {
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

    public static String timestampRangeStringOf(long fromTime, long toTime) {
        return "From " + timestampStringOf(fromTime) + " to " + timestampStringOf(toTime);
    }

    public static String timestampStringOf(long millisecond){
        ZonedDateTime zdt = millisecondToZonedDateTime(millisecond);
        return zdt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    public static DecimalFormat getDecimalFormat() {
        DecimalFormat decimalFormat;

        try {
            NumberFormatStyle nfs =
                    (NumberFormatStyle) Executions.getCurrent().getDesktop().getAttribute("numberFormatStyle");
            decimalFormat = nfs.getDecimalFormat();
        } catch (Exception e) {
            decimalFormat = new DecimalFormat("##############0.##");
        }

        return decimalFormat;
    }

    public static String getCurrencySymbol(String currency) {
        switch (currency.toLowerCase()) {
            case "eur":
            case "euro":
                return "\u20ac";
            case "gbp":
                return "\u00a3";
            case "jpy":
                return "\u00A5";
            default:
                return "$";
        }
    }

    public static String getDisplayAttributeKey(String attributeKey) {
        switch (attributeKey) {
            case AttributeCodes.CONCEPT_NAME: return "Activity";
            case AttributeCodes.ORG_RESOURCE: return "Resource";
            case AttributeCodes.ORG_GROUP: return "Resource group";
            case AttributeCodes.ORG_ROLE: return "Role";
            case AttributeCodes.LIFECYCLE_TRANSITION: return "Status";
            default: return attributeKey;
        }
    }
}
