package org.apromore.portal.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String xpdlDate2standardDate(String xpdlDate) {
        // example of xpdl date: 2010-02-16T20:18:32.1102462+10:0
        // example of standard date: 2010-02-16 20:18:32

        String[] comp = xpdlDate.split("T");
        String day = xpdlDate.split("T")[0];
        String time = (xpdlDate.split("T")[1]).split("\\+")[0];
        time = time.split("\\.")[0];

        return day + " " + time;
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
