/*
 * Copyright © 2009-2019 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package org.apromore.service.csvimporter.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.zkoss.zul.Messagebox;

public class Parse {

	
	
    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {
    	{
        put("^\\d{4}$\\d{2}\\d{2}", "yyyyMMdd");
        put("^\\d{4}$\\d{2}\\d{2}", "yyyyddMM");
        put("^\\d{2}\\d{2}\\d{4}$", "MMddyyyy");
        put("^\\d{2}\\d{2}\\d{4}$", "ddMMyyyy");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "dd/MM/yyyy");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
        put("^\\d{12}$", "yyyyMMddHHmm");
        put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        put("^\\d{1,2}-\\d[0-12]-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        put("^\\d{4}-\\d[0-12]-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "dd/MM/yyyy HH:mm");
//        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");

        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        put("^\\d{4}\\d{1,2}\\d[0-12]\\d{6}$", "yyyyMMddHHmmss");   // 20110625031548
        put("^\\d{4}\\d[0-12]\\d{1,2}\\d{6}$", "yyyyddMMHHmmss");   // 20112506031548
        put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");  //20110520 031548
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss"); //21-11-2011 11:11:!1
//        put("^\\d{1,2}/\\d{1,2}/\\d{4} \\s\\d{1,2}:\\d{2}:\\d{2}$.\\d{3}", "dd/MM/yyyy HH:mm:ss.SSS"); // 21/11/2011 11:11:11.111
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");    // 2011-11-21 11:11:11
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd/MM/yyyy HH:mm:ss");   //11/21/2011 11:11:11
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss"); // 2011/11/21 11:11:11
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy/MM/dd HH:mm:ss.SSS");  //2011/11/11 03:05:12.522
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");   // 11 Nov 2011 03:05:12
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss"); // 11 November 2011 03:05:12
        put("^\\d{1,2}.\\d{1,2}.\\d{1,2}\\s\\d{1,2}:\\d{1,2}$", "dd.MM.yy HH:mm"); //19.3.10 8:05
        put("^\\d{1,2}.\\d{1,2}.\\d{1,2}\\s\\d{1,2}:\\d{1,2}$", "MM.dd.yy HH:mm"); //9.13.10 8:05
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy-MM-dd HH:mm:ss.SSS");//2011-11-11 03:05:12.522
        put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy-MM-ddTHH:mm:ss.SSS");//2011-11-11T03:05:12.522
        put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-ddTHH:mm:ss");//2011-11-11T03:05:12.522
        put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy/MM/ddTHH:mm:ss.SSS");//2011/11/11T03:05:12.522
        put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/ddTHH:mm:ss");//2011/11/11T03:05:12.522





//            put("^(19|20)\\d\\d(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$", "yyyyMMdd"); //20191129
//            put("^(19|20)\\d\\d(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[012])$", "yyyyddMM"); //20192911
//            put("^(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])\\d\\d(19|20)$", "MMddyyyy"); //11292019
//            put("^(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[012])\\d\\d(19|20)$", "ddMMyyyy"); //29112019
//
//            put("^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-\\d\\d(19|20)$", "dd-MM-yyyy"); //29-11-2019
//            put("^(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])-\\d\\d(19|20)$", "MM-dd-yyyy"); //11-29-2019
//            put("^\\d\\d(19|20)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$", "yyyy/MM/dd"); //2019/11/29
//            put("^\\d\\d(19|20)-(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])$", "yyyy/dd/MM"); //2019/29/11
//
//            put("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d\\d(19|20)$", "dd/MM/yyyy"); //29/11/2019
//            put("^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])/\\d\\d(19|20)$", "MM/dd/yyyy"); //11/29/2019
//            put("^\\d\\d(19|20)/(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])$", "yyyy/MM/dd"); //2019/11/29
//            put("^\\d\\d(19|20)/(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])$", "yyyy/dd/MM"); //2019/29/11
//
//            put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
//            put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
//            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
//            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
//            put("^\\d{12}$", "yyyyMMddHHmm");
//            put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
//            put("^\\d{1,2}-\\d[0-12]-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
//            put("^\\d{4}-\\d[0-12]-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
//            put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "dd/MM/yyyy HH:mm");
////        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
//
//            put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
//            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
//            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
//            put("^\\d{4}\\d{1,2}\\d[0-12]\\d{6}$", "yyyyMMddHHmmss");   // 20110625031548
//            put("^\\d{4}\\d[0-12]\\d{1,2}\\d{6}$", "yyyyddMMHHmmss");   // 20112506031548
//            put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");  //20110520 031548
//            put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss"); //21-11-2011 11:11:!1
////        put("^\\d{1,2}/\\d{1,2}/\\d{4} \\s\\d{1,2}:\\d{2}:\\d{2}$.\\d{3}", "dd/MM/yyyy HH:mm:ss.SSS"); // 21/11/2011 11:11:11.111
//            put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");    // 2011-11-21 11:11:11
//            put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd/MM/yyyy HH:mm:ss");   //11/21/2011 11:11:11
//            put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss"); // 2011/11/21 11:11:11
//            put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy/MM/dd HH:mm:ss.SSS");  //2011/11/11 03:05:12.522
//            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");   // 11 Nov 2011 03:05:12
//            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss"); // 11 November 2011 03:05:12
//            put("^\\d{1,2}.\\d{1,2}.\\d{1,2}\\s\\d{1,2}:\\d{1,2}$", "dd.MM.yy HH:mm"); //9.3.10 8:05
//            put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy-MM-dd HH:mm:ss.SSS");//2011-11-11 03:05:12.522
//            put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy-MM-ddTHH:mm:ss.SSS");//2011-11-11T03:05:12.522
//            put("^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-ddTHH:mm:ss");//2011-11-11T03:05:12.522
//            put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}.\\d{3}$", "yyyy/MM/ddTHH:mm:ss.SSS");//2011/11/11T03:05:12.522
//            put("^\\d{4}/\\d{1,2}/\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/ddTHH:mm:ss");//2011/11/11T03:05:12.522
    }};
	
	public Timestamp parseTimestamp(String theDate, String theFormate) {
//	    Messagebox.show("Date: " + theDate + " Format: " + theFormate);
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(theFormate);
            formatter.setLenient(false);
		    Calendar cal = Calendar.getInstance();
		    Date d = formatter.parse(theDate);
			cal.setTime(d);
			return new Timestamp(cal.getTimeInMillis());
		} catch (Exception e) {
//            System.out.print(e.getStackTrace());
            return null;
		}
	}
	
	
    public String determineDateFormat(String dateString) {
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                try{
                    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_REGEXPS.get(regexp));
                    formatter.setLenient(false);
                    Calendar cal = Calendar.getInstance();
                    Date d = formatter.parse(dateString);
                    return DATE_FORMAT_REGEXPS.get(regexp);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }
        return null; // Unknown format.
    }
}
