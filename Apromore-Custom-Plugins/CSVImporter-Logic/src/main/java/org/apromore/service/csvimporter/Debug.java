package org.apromore.service.csvimporter;

import org.apromore.service.csvimporter.dateparser.DateParserUtils;

import java.sql.Timestamp;

public class Debug {

    public static void main(String[] arg) {

        System.out.println(DateParserUtils.parseCalendar("1100000123"));
        System.out.println(new Timestamp(DateParserUtils.parseCalendar(" 20.05.2011 10:20:30.922").getTimeInMillis()));
    }
}
