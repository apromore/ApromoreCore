package org.apromore.service.csvimporter.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateTimeStampsTemp {
    String dayRegex1 = "(0?[1-9]|[12][0-9]|3[01])"; // days from 1 to 31
    String monthRegex1 = "(0?[13578]|1[02])";      // Month with 31 days

    String dayRegex2 = "(0?[1-9]|[12][0-9]|30)"; // days from 1 to 30
    String monthRegex2 = "(0?[469]|11)";         // Month with 30 days

    String dayRegex3 = "(0?[1-9]|[12][0-9])"; // days from 1 to 29
    String monthRegex3 = "(0?2)";             // February


    String yearRegex = "([12][0-9]\\d{2})";
    String yearRegex2 = "[0-9][1-9]";


    String HMRegex = "(([01][0-9])|(2[0-3])):([0-5][0-9])"; // HH:mm
    String HMSRegex = "\\s(([01][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])"; // HH:mm:ss
    String HMSSRegex1 = "\\s(([01][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9]):([0-9][0-9][0-9])"; // HH:mm:ss:SSS
    String HMSSRegex2 = "\\s(([01][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9]).([0-9][0-9][0-9])"; // HH:mm:ss.SSS
    String HMSSRegex3 = "\\s(([01][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9]).([0-9][0-9][0-9])Z"; // HH:mm:ss.SSS
    String HMSSRegex4 = "T(([01][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9]).([0-9][0-9][0-9])Z"; // THH:mm:ss.SSSZ

    String AMPMRegex = "(AM|am|aM|Am|PM|pm|pM|Pm)";

    public void generate() {

        String[] separator = {"/", "-", ".", ""};
        String regextExp;
        String format;

        StringBuilder sb = new StringBuilder();
        sb.append("Regex" + ", Format" + '\n');

        for (String sep : separator) {

            ///////////////////////////////////////////////////////////////////////////////////////////////

            /// --- Month - Day - Year Combinations
            // MM/dd/yyyy ---- MM.dd.yyyy ---- MM-dd-yyyy ---- MMddyyyy
            regextExp = createEXP(monthDayFormat(sep), yearRegex, "", sep);
            format = createFormat("MM", "dd", "yyyy", "", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- MM/dd/yyyy HH:mm ---- MM.dd.yyyy HH:mm ---- MM-dd-yyyy HH:mm ---- MMddyyyy HH:mm
            regextExp = createEXP(monthDayFormat(sep), yearRegex, HMRegex, sep);
            format = createFormat("MM", "dd", "yyyy", " HH:mm", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- MM/dd/yyyy HH:mm:ss ---- MM.dd.yyyy HH:mm:ss---- MM-dd-yyyy HH:mm:ss ---- MMddyyyy HH:mm:ss
            regextExp = createEXP(monthDayFormat(sep), yearRegex, HMSRegex, sep);
            format = createFormat("MM", "dd", "yyyy", " HH:mm:ss", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- MM/dd/yyyy HHH:mm:ss:SSS ---- MM.dd.yyyy HH:mm:ss:SSS ---- MM-dd-yyyy HH:mm:ss:SSS ---- MMddyyyy HH:mm:ss:SSS
            regextExp = createEXP(monthDayFormat(sep), yearRegex, HMSSRegex1, sep);
            format = createFormat("MM", "dd", "yyyy", " HH:mm:ss:SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- MM/dd/yyyy HH:mm:ss.SSS ---- MM.dd.yyyy HH:mm:ss.SSS ---- MM-dd-yyyy HH:mm:ss.SSS ---- MMddyyyy HH:mm:ss.SSS
            regextExp = createEXP(monthDayFormat(sep), yearRegex, HMSSRegex2, sep);
            format = createFormat("MM", "dd", "yyyy", " HH:mm:ss.SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- MM/dd/yyyy HH:mm:ss.SSSZ ---- MM.dd.yyyy HH:mm:ss.SSSZ ---- MM-dd-yyyy HH:mm:ss.SSSZ ---- MMddyyyy HH:mm:ss.SSSZ
            regextExp = createEXP(monthDayFormat(sep), yearRegex, HMSSRegex3, sep);
            format = createFormat("MM", "dd", "yyyy", " HH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- MM/dd/yyyyTHH:mm:ss.SSSZ ---- MM.dd.yyyyTHH:mm:ss.SSSZ ---- MM-dd-yyyyTHH:mm:ss.SSSZ ---- MMddyyyyTHH:mm:ss.SSSZ
            regextExp = createEXP(monthDayFormat(sep), yearRegex, HMSSRegex4, sep);
            format = createFormat("MM", "dd", "yyyy", "THH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            /////////////////////////////////////
            /// Day - Month - Year Combination

            // dd/MM/yyyy ---- dd.MM.yyyy ---- dd-MM-yyyy
            regextExp = createEXP(dayMonthFormat(sep), yearRegex, "", sep);
            format = createFormat("dd", "MM", "yyyy", "", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- dd/MM/yyyy HH:mm ---- dd.MM.yyyy HH:mm ---- dd-MM-yyyy HH:mm
            regextExp = createEXP(dayMonthFormat(sep), yearRegex, HMRegex, sep);
            format = createFormat("dd", "MM", "yyyy", " HH:mm", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- dd/MM/yyyy HH:mm:ss ---- dd.MM.yyyy HH:mm:ss---- dd-MM-yyyy HH:mm:ss
            regextExp = createEXP(dayMonthFormat(sep), yearRegex, HMSRegex, sep);
            format = createFormat("dd", "MM", "yyyy", " HH:mm:ss", sep);
            sb.append(regextExp).append(",").append(format).append('\n');
            


            //--- dd/MM/yyyy HHH:mm:ss:SSS ---- dd.MM.yyyy HH:mm:ss:SSS ---- dd-MM-yyyy HH:mm:ss:SSS
            regextExp = createEXP(dayMonthFormat(sep), yearRegex, HMSSRegex1, sep);
            format = createFormat("dd", "MM", "yyyy", " HH:mm:ss:SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');
            


            //--- dd/MM/yyyy HH:mm:ss.SSS ---- dd.MM.yyyy HH:mm:ss.SSS ---- dd-MM-yyyy HH:mm:ss.SSS
            regextExp = createEXP(dayMonthFormat(sep), yearRegex, HMSSRegex2, sep);
            format = createFormat("dd", "MM", "yyyy", " HH:mm:ss.SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');
            

            //--- dd/MM/yyyy HH:mm:ss.SSSZ ---- dd.MM.yyyy HH:mm:ss.SSSZ ---- dd-MM-yyyy HH:mm:ss.SSSZ
            regextExp = createEXP(dayMonthFormat(sep), yearRegex, HMSSRegex3, sep);
            format = createFormat("dd", "MM", "yyyy", " HH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');
            

            //--- dd/MM/yyyyTHH:mm:ss.SSSZ ---- dd.MM.yyyyTHH:mm:ss.SSSZ ---- dd-MM-yyyyTHH:mm:ss.SSSZ
            regextExp = createEXP(dayMonthFormat(sep), yearRegex, HMSSRegex4, sep);
            format = createFormat("dd", "MM", "yyyy", "THH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            ///////////////////////////////////////////////////////////////////////////////////////////////

            /// --- Year - Month - Day  Combinations
            // yyyy/MM/dd ---- yyyy.MM.dd ---- yyyy-MM-dd
            regextExp = createEXP(yearRegex, monthDayFormat(sep), "", sep);
            format = createFormat("yyyy", "MM", "dd", "", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/MM/dd HH:mm ---- yyyy.MM.dd HH:mm ----  yyyy-MM-dd HH:mm
            regextExp = createEXP(yearRegex, monthDayFormat(sep), HMRegex, sep);
            format = createFormat("yyyy","MM", "dd",  " HH:mm", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //---yyyy/MM/dd HH:mm:ss ---- yyyy.MM.dd HH:mm:ss----  yyyy-MM-dd HH:mm:ss
            regextExp = createEXP(yearRegex, monthDayFormat(sep), HMSRegex, sep);
            format = createFormat("yyyy","MM", "dd", " HH:mm:ss", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/MM/dd HHH:mm:ss:SSS ---- yyyy.MM.dd HH:mm:ss:SSS ---- yyyy-MM-dd HH:mm:ss:SSS
            regextExp = createEXP(yearRegex, monthDayFormat(sep), HMSSRegex1, sep);
            format = createFormat("yyyy","MM", "dd", " HH:mm:ss:SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- yyyy/MM/dd HH:mm:ss.SSS ---- yyyy.MM.dd HH:mm:ss.SSS ---- yyyy-MM-dd HH:mm:ss.SSS
            regextExp = createEXP(yearRegex, monthDayFormat(sep), HMSSRegex2, sep);
            format = createFormat("yyyy","MM", "dd", " HH:mm:ss.SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/MM/dd HH:mm:ss.SSSZ ---- yyyy.MM.dd HH:mm:ss.SSSZ ---- yyyy-MM-dd HH:mm:ss.SSSZ
            regextExp = createEXP(yearRegex, monthDayFormat(sep), HMSSRegex3, sep);
            format = createFormat("yyyy","MM", "dd", " HH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/MM/ddTHH:mm:ss.SSSZ ---- yyyy.MM.ddyTHH:mm:ss.SSSZ ---- yyyy-MM-ddTHH:mm:ss.SSSZ
            regextExp = createEXP(yearRegex, monthDayFormat(sep), HMSSRegex4, sep);
            format = createFormat("yyyy","MM", "dd", "THH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            ///////////////////////////////////////////////////////////////////////////////////////////////

            /// --- Year - Day - Month Combinations
            // yyyy/dd/MM ---- yyyy.dd.MM ---- yyyy-dd-MM
            regextExp = createEXP(yearRegex, dayMonthFormat(sep), "", sep);
            format = createFormat("yyyy", "dd", "MM", "", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/dd/MM HH:mm ---- yyyy.dd.MM HH:mm ----  yyyy-dd-MM HH:mm
            regextExp = createEXP(yearRegex, dayMonthFormat(sep), HMRegex, sep);
            format = createFormat("yyyy","dd", "MM",  " HH:mm", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //---yyyy/dd/MM HH:mm:ss ---- yyyy.dd.MM HH:mm:ss----  yyyy-dd-MM HH:mm:ss
            regextExp = createEXP(yearRegex, dayMonthFormat(sep), HMSRegex, sep);
            format = createFormat("yyyy","dd", "MM", " HH:mm:ss", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/dd/MM HHH:mm:ss:SSS ---- yyyy.dd.MM HH:mm:ss:SSS ---- yyyy-dd-MM HH:mm:ss:SSS
            regextExp = createEXP(yearRegex, dayMonthFormat(sep), HMSSRegex1, sep);
            format = createFormat("yyyy","dd", "MM", " HH:mm:ss:SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- yyyy/dd/MM HH:mm:ss.SSS ---- yyyy.dd.MM HH:mm:ss.SSS ---- yyyy-dd-MM HH:mm:ss.SSS
            regextExp = createEXP(yearRegex, dayMonthFormat(sep), HMSSRegex2, sep);
            format = createFormat("yyyy","dd", "MM", " HH:mm:ss.SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/dd/MM HH:mm:ss.SSSZ ---- yyyy.dd.MM HH:mm:ss.SSSZ ---- yyyy-dd-MM HH:mm:ss.SSSZ
            regextExp = createEXP(yearRegex, dayMonthFormat(sep), HMSSRegex3, sep);
            format = createFormat("yyyy","dd", "MM", " HH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/dd/MMTHH:mm:ss.SSSZ ---- yyyy.dd.MMyTHH:mm:ss.SSSZ ---- yyyy-dd-MMTHH:mm:ss.SSSZ
            regextExp = createEXP(yearRegex, dayMonthFormat(sep), HMSSRegex4, sep);
            format = createFormat("yyyy","dd", "MM", "THH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');




            ////////////// When year is only 2 digits ------------------------------------------------------------------
            // ----------------------------------------------------------------------------------




            ///////////////////////////////////////////////////////////////////////////////////////////////

            /// --- Month - Day - Year Combinations
            // MM/dd/yy ---- MM.dd.yy ---- MM-dd-yy
            regextExp = createEXP(monthDayFormat(sep), yearRegex2, "", sep);
            format = createFormat("MM", "dd", "yy", "", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- MM/dd/yy HH:mm ---- MM.dd.yy HH:mm ---- MM-dd-yy HH:mm
            regextExp = createEXP(monthDayFormat(sep), yearRegex2, HMRegex, sep);
            format = createFormat("MM", "dd", "yy", " HH:mm", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- MM/dd/yy HH:mm:ss ---- MM.dd.yy HH:mm:ss---- MM-dd-yy HH:mm:ss
            regextExp = createEXP(monthDayFormat(sep), yearRegex2, HMSRegex, sep);
            format = createFormat("MM", "dd", "yy", " HH:mm:ss", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- MM/dd/yy HHH:mm:ss:SSS ---- MM.dd.yy HH:mm:ss:SSS ---- MM-dd-yy HH:mm:ss:SSS
            regextExp = createEXP(monthDayFormat(sep), yearRegex2, HMSSRegex1, sep);
            format = createFormat("MM", "dd", "yy", " HH:mm:ss:SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- MM/dd/yy HH:mm:ss.SSS ---- MM.dd.yy HH:mm:ss.SSS ---- MM-dd-yy HH:mm:ss.SSS
            regextExp = createEXP(monthDayFormat(sep), yearRegex2, HMSSRegex2, sep);
            format = createFormat("MM", "dd", "yy", " HH:mm:ss.SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- MM/dd/yy HH:mm:ss.SSSZ ---- MM.dd.yy HH:mm:ss.SSSZ ---- MM-dd-yy HH:mm:ss.SSSZ
            regextExp = createEXP(monthDayFormat(sep), yearRegex2, HMSSRegex3, sep);
            format = createFormat("MM", "dd", "yy", " HH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- MM/dd/yyTHH:mm:ss.SSSZ ---- MM.dd.yyTHH:mm:ss.SSSZ ---- MM-dd-yyTHH:mm:ss.SSSZ
            regextExp = createEXP(monthDayFormat(sep), yearRegex2, HMSSRegex4, sep);
            format = createFormat("MM", "dd", "yy", "THH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            /////////////////////////////////////
            /// Day - Month - Year Combination

            // dd/MM/yy ---- dd.MM.yy ---- dd-MM-yy
            regextExp = createEXP(dayMonthFormat(sep), yearRegex2, "", sep);
            format = createFormat("dd", "MM", "yy", "", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- dd/MM/yy HH:mm ---- dd.MM.yy HH:mm ---- dd-MM-yy HH:mm
            regextExp = createEXP(dayMonthFormat(sep), yearRegex2, HMRegex, sep);
            format = createFormat("dd", "MM", "yy", " HH:mm", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- dd/MM/yy HH:mm:ss ---- dd.MM.yy HH:mm:ss---- dd-MM-yy HH:mm:ss
            regextExp = createEXP(dayMonthFormat(sep), yearRegex2, HMSRegex, sep);
            format = createFormat("dd", "MM", "yy", " HH:mm:ss", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- dd/MM/yy HHH:mm:ss:SSS ---- dd.MM.yy HH:mm:ss:SSS ---- dd-MM-yy HH:mm:ss:SSS
            regextExp = createEXP(dayMonthFormat(sep), yearRegex2, HMSSRegex1, sep);
            format = createFormat("dd", "MM", "yy", " HH:mm:ss:SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- dd/MM/yy HH:mm:ss.SSS ---- dd.MM.yy HH:mm:ss.SSS ---- dd-MM-yy HH:mm:ss.SSS
            regextExp = createEXP(dayMonthFormat(sep), yearRegex2, HMSSRegex2, sep);
            format = createFormat("dd", "MM", "yy", " HH:mm:ss.SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- dd/MM/yy HH:mm:ss.SSSZ ---- dd.MM.yy HH:mm:ss.SSSZ ---- dd-MM-yy HH:mm:ss.SSSZ
            regextExp = createEXP(dayMonthFormat(sep), yearRegex2, HMSSRegex3, sep);
            format = createFormat("dd", "MM", "yy", " HH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- dd/MM/yyTHH:mm:ss.SSSZ ---- dd.MM.yyTHH:mm:ss.SSSZ ---- dd-MM-yyTHH:mm:ss.SSSZ
            regextExp = createEXP(dayMonthFormat(sep), yearRegex2, HMSSRegex4, sep);
            format = createFormat("dd", "MM", "yy", "THH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            ///////////////////////////////////////////////////////////////////////////////////////////////

            /// --- Year - Month - Day  Combinations
            // yyyy/MM/dd ---- yyyy.MM.dd ---- yyyy-MM-dd
            regextExp = createEXP(yearRegex2, monthDayFormat(sep), "", sep);
            format = createFormat("yy", "MM", "dd", "", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/MM/dd HH:mm ---- yyyy.MM.dd HH:mm ----  yyyy-MM-dd HH:mm
            regextExp = createEXP(yearRegex2, monthDayFormat(sep), HMRegex, sep);
            format = createFormat("yy", "MM", "dd",  " HH:mm", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //---yyyy/MM/dd HH:mm:ss ---- yyyy.MM.dd HH:mm:ss----  yyyy-MM-dd HH:mm:ss
            regextExp = createEXP(yearRegex2, monthDayFormat(sep), HMSRegex, sep);
            format = createFormat("yy", "MM", "dd", " HH:mm:ss", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/MM/dd HHH:mm:ss:SSS ---- yyyy.MM.dd HH:mm:ss:SSS ---- yyyy-MM-dd HH:mm:ss:SSS
            regextExp = createEXP(yearRegex2, monthDayFormat(sep), HMSSRegex1, sep);
            format = createFormat("yy", "MM", "dd", " HH:mm:ss:SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- yyyy/MM/dd HH:mm:ss.SSS ---- yyyy.MM.dd HH:mm:ss.SSS ---- yyyy-MM-dd HH:mm:ss.SSS
            regextExp = createEXP(yearRegex2, monthDayFormat(sep), HMSSRegex2, sep);
            format = createFormat("yy", "MM", "dd", " HH:mm:ss.SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/MM/dd HH:mm:ss.SSSZ ---- yyyy.MM.dd HH:mm:ss.SSSZ ---- yyyy-MM-dd HH:mm:ss.SSSZ
            regextExp = createEXP(yearRegex2, monthDayFormat(sep), HMSSRegex3, sep);
            format = createFormat("yy", "MM", "dd", " HH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/MM/ddTHH:mm:ss.SSSZ ---- yyyy.MM.ddyTHH:mm:ss.SSSZ ---- yyyy-MM-ddTHH:mm:ss.SSSZ
            regextExp = createEXP(yearRegex2, monthDayFormat(sep), HMSSRegex4, sep);
            format = createFormat("yy", "MM", "dd", "THH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            ///////////////////////////////////////////////////////////////////////////////////////////////

            /// --- Year - Day - Month Combinations
            // yyyy/dd/MM ---- yyyy.dd.MM ---- yyyy-dd-MM
            regextExp = createEXP(yearRegex2, dayMonthFormat(sep), "", sep);
            format = createFormat("yy","dd", "MM", "", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/dd/MM HH:mm ---- yyyy.dd.MM HH:mm ----  yyyy-dd-MM HH:mm
            regextExp = createEXP(yearRegex2, dayMonthFormat(sep), HMRegex, sep);
            format = createFormat("yy","dd", "MM",  " HH:mm", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //---yyyy/dd/MM HH:mm:ss ---- yyyy.dd.MM HH:mm:ss----  yyyy-dd-MM HH:mm:ss
            regextExp = createEXP(yearRegex2, dayMonthFormat(sep), HMSRegex, sep);
            format = createFormat("yy","dd", "MM", " HH:mm:ss", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/dd/MM HHH:mm:ss:SSS ---- yyyy.dd.MM HH:mm:ss:SSS ---- yyyy-dd-MM HH:mm:ss:SSS
            regextExp = createEXP(yearRegex2, dayMonthFormat(sep), HMSSRegex1, sep);
            format = createFormat("yy","dd", "MM", " HH:mm:ss:SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');



            //--- yyyy/dd/MM HH:mm:ss.SSS ---- yyyy.dd.MM HH:mm:ss.SSS ---- yyyy-dd-MM HH:mm:ss.SSS
            regextExp = createEXP(yearRegex2, dayMonthFormat(sep), HMSSRegex2, sep);
            format = createFormat("yy","dd", "MM", " HH:mm:ss.SSS", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/dd/MM HH:mm:ss.SSSZ ---- yyyy.dd.MM HH:mm:ss.SSSZ ---- yyyy-dd-MM HH:mm:ss.SSSZ
            regextExp = createEXP(yearRegex2, dayMonthFormat(sep), HMSSRegex3, sep);
            format = createFormat("yy","dd", "MM", " HH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');


            //--- yyyy/dd/MMTHH:mm:ss.SSSZ ---- yyyy.dd.MMyTHH:mm:ss.SSSZ ---- yyyy-dd-MMTHH:mm:ss.SSSZ
            regextExp = createEXP(yearRegex2, dayMonthFormat(sep), HMSSRegex4, sep);
            format = createFormat("yy","dd", "MM", "THH:mm:ss.SSSZ", sep);
            sb.append(regextExp).append(",").append(format).append('\n');

        }

        try (PrintWriter writer = new PrintWriter(new File("test.csv"))) {
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {

        }
    }


    private String dayMonthFormat(String sep){
        String inlineSep = sep.equals("")? "" : "\\";
        return  "(" + dayRegex1 + inlineSep + sep + monthRegex1 + "|" + dayRegex2 + inlineSep + sep + monthRegex2 + "|" + dayRegex3 + inlineSep + sep + monthRegex3 + ")";
    }

    private String monthDayFormat(String sep){
        String inlineSep = sep.equals("")? "" : "\\";
        return  "(" + monthRegex1  + inlineSep+ sep + dayRegex1 + "|" + monthRegex2  + inlineSep + sep + dayRegex2 + "|" + monthRegex3  + inlineSep + sep + dayRegex3 + ")";
    }
    private String createEXP(String firstSecond, String Third, String Fourth, String sep){
        String inlineSep = sep.equals("")? "" : "\\";
        return  "^(" + firstSecond + inlineSep + sep + Third + Fourth + ")$";
    }


    private String createFormat(String first, String Second, String Third, String Fourth, String sep){
        return first + sep + Second + sep + Third + Fourth;
    }
}
