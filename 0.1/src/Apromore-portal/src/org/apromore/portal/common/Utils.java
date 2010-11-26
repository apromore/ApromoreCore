package org.apromore.portal.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	public static String xpdlDate2standardDate (String xpdlDate) {
		// example of xpdl date: 2010-02-16T20:18:32.1102462+10:0
		// example of standard date: 2010-02-16 20:18:32
		
		String[] comp = xpdlDate.split("T");
		String day = xpdlDate.split("T")[0];
		String time = (xpdlDate.split("T")[1]).split("\\+")[0];
		time = time.split("\\.")[0];
		
		return day + " " + time;
	}
	/**
	 * Generate a cpf uri for version of processId
	 * @param processId
	 * @param version
	 * @return
	 */
	public static String newCpfURI() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
		Date date = new Date();
		String time = dateFormat.format(date);
		return time;
	}
}
