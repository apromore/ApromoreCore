/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.plugin.parquet.export.util;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.apromore.apmlog.util.NumberFormatStyle;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.zkoss.zk.ui.Executions;

public class Util {

    private Util() {
        throw new IllegalStateException("Utility class");
    }

    public static long SECOND = Long.parseLong("1000");
    public static long MINUTE = SECOND * 60;
    public static long HOUR = MINUTE * 60;
    public static long DAY = HOUR * 24;
    public static long WEEK = DAY * 7;
    public static long MONTH = 2629800000L; // value of 365.25 * DAY / 12
    public static long YEAR = 31557600000L; // value of 365.25 * DAY

    private static final String YEARS_TEXT = "years";
    private static final String MONTHS_TEXT = "months";
    private static final String WEEKS_TEXT = "weeks";
    private static final String DAYS_TEXT = "days";
    private static final String HOURS_TEXT = "hours";
    private static final String MINUTES_TEXT = "minutes";
    private static final String SECONDS_TEXT = "seconds";

    private static final String YRS_TEXT = "yrs";
    private static final String MTHS_TEXT = "mths";
    private static final String WKS_TEXT = "wks";
    private static final String HRS_TEXT = "hrs";
    private static final String MINS_TEXT = "mins";
    private static final String SECS_TEXT = "secs";
    private static final String D_TEXT = "d";

    public static long getDurationValueOf(String timeUnitText) {
        switch (timeUnitText) {
            case YEARS_TEXT:
            case YRS_TEXT:
                return YEAR;
            case MONTHS_TEXT:
            case MTHS_TEXT:
                return MONTH;
            case WEEKS_TEXT:
            case WKS_TEXT:
                return WEEK;
            case DAYS_TEXT:
            case D_TEXT:
                return DAY;
            case HOURS_TEXT:
            case HRS_TEXT:
                return HOUR;
            case SECONDS_TEXT:
            case SECS_TEXT:
                return SECOND;
            default:
                return 1;
        }
    }

    public static long getCommonTimeValue(String input) {
        if (input == null)
            return 0;

        switch (input.toLowerCase()) {
            case YEARS_TEXT:
            case "year":
            case YRS_TEXT:
            case "yr":
            case "31557600000":
            case "31536000000":
                return YEAR;
            case MONTHS_TEXT:
            case "month":
            case MTHS_TEXT:
            case "mth":
            case "2629800000":
            case "2678400000":
                return MONTH;
            case WEEKS_TEXT:
            case "week":
            case WKS_TEXT:
            case "wk":
            case "604800000":
                return WEEK;
            case DAYS_TEXT:
            case "day":
            case D_TEXT:
            case "86400000":
                return DAY;
            case HOURS_TEXT:
            case "hour":
            case HRS_TEXT:
            case "hr":
            case "3600000":
                return HOUR;
            case MINUTES_TEXT:
            case "minute":
            case MINS_TEXT:
            case "min":
            case "60000":
                return MINUTE;
            case SECONDS_TEXT:
            case "second":
            case SECS_TEXT:
            case "sec":
            case "1000":
                return SECOND;
            default:
                return MINUTE;
        }
    }

    public static final DecimalFormat ndf0 = new DecimalFormat("###############");
    public static final DecimalFormat ndf2 = new DecimalFormat("###############.##");
    public static final DecimalFormat ndf3 = new DecimalFormat("###############.###");

    public static NumberFormatStyle getNumberFormatStyle() {
        NumberFormatStyle style = null;
        try {
            style = (NumberFormatStyle) Executions.getCurrent().getDesktop().getAttribute("numberFormatStyle");
        } catch (Exception ignore) {
            // DO NOTHING
        }

        return style;
    }

    public static DecimalFormat getDecimalFormat() {
        NumberFormatStyle style = getNumberFormatStyle();
        return style != null ? style.getDecimalFormat() : ndf2;
    }

    public static DecimalFormat getDecimalFormatFor3Places() {
        NumberFormatStyle style = getNumberFormatStyle();
        return style != null ? style.getDecimalFormatForFixed(3) : ndf3;
    }

    public static DecimalFormat getDecimalFormatForInt() {
        NumberFormatStyle style = getNumberFormatStyle();
        return style != null ? style.getDecimalFormatForFixed(0) : ndf0;
    }

    public static int getDecimalPlace() {
        NumberFormatStyle nfs = getNumberFormatStyle();
        return nfs == null ? 2 : nfs.getDecimalPlace();
    }

    public static String getDecimalMark() {
        NumberFormatStyle nfs = getNumberFormatStyle();
        return nfs == null || nfs.isCommaDotStyle() ? "." : ",";
    }

    public static String getGroupingMark() {
        NumberFormatStyle nfs = getNumberFormatStyle();
        return nfs == null || nfs.isCommaDotStyle() ? "," : ".";
    }

    public static String getLocale() {
        NumberFormatStyle nfs = getNumberFormatStyle();
        return nfs != null && nfs.isDotCommaStyle() ? "de" : "us";
    }

    public static String upperCaseFirstChar(@NotNull @NotEmpty String text) {
        if (text.isEmpty())
            return "";

        String mod = text.substring(0, 1).toUpperCase();
        mod += text.substring(1);
        return mod;
    }

    public static String lowerCaseFirstChar(String text) {
        String mod = text.substring(0, 1).toLowerCase();
        mod += text.substring(1);
        return mod;
    }

    public static String getAttributeDisplayNameForMultiple(String code) {
        switch(code) {
            case "concept:name": return LabelUtil.getLabel("dash_activities");
            case "org:resource": return LabelUtil.getLabel("dash_resources");
            case "org:group": return LabelUtil.getLabel("dash_groups");
            default: return code;
        }
    }

    public static String getAttributeKeyDisplayName(String code) {
        switch(code) {
            case "concept:name": return LabelUtil.getLabel("dash_activity");
            case "org:resource": return LabelUtil.getLabel("dash_resource");
            case "org:group": return LabelUtil.getLabel("dash_group");
            default: return code;
        }
    }

    public static void reorderAttributeKeys(List<String> keyList) {
        Collections.sort(keyList);

        if (keyList.contains("org:group")) {
            keyList.remove("org:group");
            keyList.add(0, "org:group");
        }

        swapToFirst("org:group", keyList);
        swapToFirst("org:resource", keyList);
        swapToFirst("concept:name", keyList);
    }

    private static void swapToFirst(String targetText, List<String> textList) {
        if (textList.contains(targetText)) {
            textList.remove(targetText);
            textList.add(0, targetText);
        }
    }

    public static long epochMilliOf(ZonedDateTime zonedDateTime){
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        String timestamp = xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();
        return ZonedDateTime.parse(timestamp);
    }

    public static List<XLog> parseXLogFile(File xLogFile) throws Exception {
        String fileName = xLogFile.getName();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        XesXmlParser parser  = extension.equals(".gz") ? new XesXmlGZIPParser() : new XesXmlParser();
        return parser.parse(xLogFile);
    }

    public static ZonedDateTime millisecondToZonedDateTime(long millisecond){
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millisecond), ZoneId.systemDefault());
    }

    public static String timestampStringOf(ZonedDateTime zdt){
        return zdt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS"));
    }

    public static String durationStringOf(Number millis, String unit, boolean allowInstant, boolean useCustomStyle) {

        double secs = millis.doubleValue() / 1000L;
        double mins = millis.doubleValue() / 1000 / 60;
        double hrs = millis.doubleValue() / 1000 / 60 / 60d;
        double days = millis.doubleValue() / 1000 / 60 / 60 / 24d;
        double wks = millis.doubleValue() / 1000 / 60 / 60 / 24 / 7d;
        double mths = millis.doubleValue() / 1000 / 60 / 60 / 24 / (365.25 / 12);
        double yrs = millis.doubleValue() / 1000 / 60 / 60 / 24 / 365.25d;

        NumberFormatStyle style = getNumberFormatStyle();

        DecimalFormat sdf = style != null && useCustomStyle ? style.getDecimalFormat() : ndf2;

        if (unit != null) {
            switch (unit.toLowerCase()) {
                case YEARS_TEXT:
                case YRS_TEXT:
                    return sdf.format(yrs) + " yrs";
                case MONTHS_TEXT:
                case MTHS_TEXT:
                    return sdf.format(mths) + " mths";
                case WEEKS_TEXT:
                case WKS_TEXT:
                    return sdf.format(wks) + " wks";
                case DAYS_TEXT:
                case D_TEXT:
                    return sdf.format(days) + " d";
                case HOURS_TEXT:
                case HRS_TEXT:
                    return sdf.format(hrs) + " hrs";
                case MINUTES_TEXT:
                case MINS_TEXT:
                    return sdf.format(mins) + " mins";
                case SECONDS_TEXT:
                case SECS_TEXT:
                    return sdf.format(secs) + " secs";
                default:
                    return millis + " millis";
            }
        }

        if (yrs > 1)  return sdf.format(yrs) + " yrs";
        if (mths > 1) return sdf.format(mths) + " mths";
        if (wks > 1) return sdf.format(wks) + " wks";
        if (days > 1) return sdf.format(days) + " days";
        if (hrs > 1) return sdf.format(hrs) + " hrs";
        if (mins > 1) return sdf.format(mins) + " mins";
        if (secs > 1) return sdf.format(secs) + " secs";
        if (millis.doubleValue() > 0) return sdf.format(millis) + " millis";

        return allowInstant ? "instant" : "0 s";
    }

    public static String durationStringOf(Number millis, boolean useCustomStyle) {
        return durationStringOf(millis, null, false, useCustomStyle);
    }

    public static String durationStringOf(Number millis) {
        return durationStringOf(millis, null, false, true);
    }

    public static String durationStringOf(Number millis, String unit) {
        return durationStringOf(millis, unit, false, true);
    }

    public static String durationStringOf(Number millis, String unit, boolean useCustomStyle) {
        return durationStringOf(millis, unit, false, useCustomStyle);
    }

    public static String getDurationStringLabelScript(String unit) {
        String s = "";
        if (unit.equals(YRS_TEXT)) {
            s = "function() { " +
                    "var yrs = 1000 * 60 * 60 * 24 * 365.25; " +
                    "var ds = 1000 * 60 * 60 * 24; " +
                    "var yrPart = parseInt(this.value / yrs); " +
                    "var remainder = this.value % yrs;" +
                    "var days = Math.round(remainder / ds);" +
                    "var output = ''; " +
                    "if (yrPart < 2) output += yrPart + ' yr ';" +
                    "if (yrPart > 1) output += yrPart + ' yrs ';" +
                    "if (days === 1) { output += ' 1 day'; }" +
                    "if (days > 1) { output += days + ' days'; }" +
                    "if (days < 1) {" +
                    "   var hrs = 1000 * 60 * 60; " +
                    "   var hrPart = Math.round(remainder / hrs);" +
                    "   if (hrPart === 1) { output += hrPart + ' hr'; }" +
                    "   if (hrPart > 1) { output += hrPart + ' hrs'; }" +
                    "}" +
                    "return output;" +
                    "}";
        } else if (unit.equals(MTHS_TEXT)) {
            s = "function() { " +
                    "var mths = 1000 * 60 * 60 * 24 * (365.25 / 12); " +
                    "var ds = 1000 * 60 * 60 * 24; " +
                    "var mthPart = parseInt(this.value / mths); " +
                    "var remainder = this.value % mths;" +
                    "var days = Math.round(remainder / ds);" +
                    "var output = ''; " +
                    "if (mthPart < 2) output += mthPart + ' mth ';" +
                    "if (mthPart > 1) output += mthPart + ' mths ';" +
                    "if (days === 1) { output += ' 1 day'; }" +
                    "if (days > 1) { output += days + ' days'; }" +
                    "if (days < 1) {" +
                    "   var hrs = 1000 * 60 * 60; " +
                    "   var hrPart = Math.round(remainder / hrs);" +
                    "   if (hrPart === 1) { output += hrPart + ' hr'; }" +
                    "   if (hrPart > 1) { output += hrPart + ' hrs'; }" +
                    "}" +
                    "return output;" +
                    "}";
        } else if (unit.equals(WKS_TEXT)) {
            s = "function() { " +
                    "var wks = 1000 * 60 * 60 * 24 * 7; " +
                    "var ds = 1000 * 60 * 60 * 24; " +
                    "var wkPart = parseInt(this.value / wks); " +
                    "var remainder = this.value % wks;" +
                    "var days = Math.round(remainder / ds);" +
                    "var output = ''; " +
                    "if (wkPart < 2) output += wkPart + ' wk ';" +
                    "if (wkPart > 1) output += wkPart + ' wks ';" +
                    "if (days === 1) { output += ' 1 day'; }" +
                    "if (days > 1) { output += days + ' days'; }" +
                    "if (days < 1) {" +
                    "   var hrs = 1000 * 60 * 60; " +
                    "   var hrPart = Math.round(remainder / hrs);" +
                    "   if (hrPart === 1) { output += hrPart + ' hr'; }" +
                    "   if (hrPart > 1) { output += hrPart + ' hrs'; }" +
                    "}" +
                    "return output;" +
                    "}";
        } else if (unit.equals(D_TEXT)) {
            s = "function() { " +
                    "var ds = 1000 * 60 * 60 * 24; " +
                    "var hrs = 1000 * 60 * 60; " +
                    "var dPart = parseInt(this.value / ds); " +
                    "var remainder = this.value % ds;" +

                    "var output = ''; " +
                    "if (dPart < 2) output += dPart + ' day ';" +
                    "if (dPart > 1) output += dPart + ' days ';" +

                    "var hrPart = Math.round(remainder / hrs);" +
                    "if (hrPart === 1) { output += hrPart + ' hr'; }" +
                    "if (hrPart > 1) { output += hrPart + ' hrs'; }" +
                    "return output;" +
                    "}";
        } else if (unit.equals(HRS_TEXT)) {
            s = "function() { " +
                    "var hrs = 1000 * 60 * 60; " +
                    "var ms = 1000 * 60; " +
                    "var hrPart = parseInt(this.value / hrs); " +
                    "var remainder = this.value % hrs;" +
                    "var mPart = Math.round(remainder / ms);" +
                    "var output = ''; " +
                    "if (hrPart < 2) output += hrPart + ' hr ';" +
                    "if (hrPart > 1) output += hrPart + ' hrs ';" +
                    "if (mPart === 1) { output += ' 1 min'; }" +
                    "if (mPart > 1) { output += mPart + ' mins'; }" +
                    "return output;" +
                    "}";
        } else if (unit.equals("mins")) {
            s = "function() { " +
                    "var ms = 1000 * 60; " +
                    "var secs = 1000 * 60; " +
                    "var mPart = parseInt(this.value / ms); " +
                    "var remainder = this.value % ms;" +
                    "var secPart = Math.round(remainder / secs);" +
                    "var output = ''; " +
                    "if (mPart < 2) output += mPart + ' min ';" +
                    "if (mPart > 1) output += mPart + ' mins ';" +
                    "if (secPart === 1) { output += ' 1 sec'; }" +
                    "if (secPart > 1) { output += secPart + ' secs'; }" +
                    "return output;" +
                    "}";
        } else if (unit.equals(SECS_TEXT)) {
            s = "function() { " +
                    "var secs = 1000; " +
                    "var mms = 1; " +
                    "var secPart = parseInt(this.value / secs); " +
                    "var remainder = this.value % secs;" +
                    "var mmPart = Math.round(remainder / mms);" +
                    "var output = ''; " +
                    "if (secPart < 2) output += secPart + ' sec ';" +
                    "if (secPart > 1) output += secPart + ' secs ';" +
                    "if (mmPart === 1) { output += ' 1 milli '; }" +
                    "if (mmPart > 1) { output += secPart + ' millis '; }" +
                    "return output;" +
                    "}";
        } else if (unit.equals("millis")) {
            s = "function() { " +
                    "var millis = this.value; " +
                    "if (millis < 2) return this.value + ' milli';" +
                    "else return this.value + ' millis';" +
                    "}";
        } else {
            s = "function() { " +
                    "return 'instant';" +
                    "}";
        }
        return s;
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

    static final String PRESTO_LOG_DIR = "presto_log_dir";

    public static String getParquetEventLogFilePath(String logName, Properties dashProperties) {
        if (dashProperties == null || !dashProperties.containsKey(PRESTO_LOG_DIR))
            return "";

        String prestoLogDir = dashProperties.get(PRESTO_LOG_DIR).toString();
        return prestoLogDir + "/" + logName + "/" + logName + ".parquet";
    }

    public static String getParquetEventLogDir(String logName, Properties dashProperties) {
        if (dashProperties == null || !dashProperties.containsKey(PRESTO_LOG_DIR))
            return "";

        String prestoLogDir = dashProperties.get(PRESTO_LOG_DIR).toString();
        return prestoLogDir + "/" + logName;
    }

    public static String durationStringOf(Number millis, long unitValue) {
        return durationStringOf(millis, getUnitLabel(unitValue), false);
    }

    private static String getUnitLabel(long unitValue) {
        if (unitValue == Util.YEAR) {
            return YEARS_TEXT;
        } else if (unitValue == Util.MONTH) {
            return MONTHS_TEXT;
        } else if (unitValue == Util.WEEK) {
            return WEEKS_TEXT;
        } else if (unitValue == Util.DAY) {
            return DAYS_TEXT;
        } else if (unitValue == Util.HOUR) {
            return HOURS_TEXT;
        } else if (unitValue == Util.MINUTE) {
            return MINUTES_TEXT;
        } else if (unitValue == Util.SECOND) {
            return SECONDS_TEXT;
        } else {
            return "milliseconds";
        }
    }

    public static String timestampRangeStringOf(long fromTime, long toTime) {
        return "From " + timestampStringOf(fromTime) + " to " + timestampStringOf(toTime);
    }

    public static String timestampStringOf(long millisecond){
        ZonedDateTime zdt = millisecondToZonedDateTime(millisecond);
        return zdt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

}
