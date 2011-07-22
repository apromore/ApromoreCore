package org.apromore.toolbox.similaritySearch.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IdGeneratorHelper_apromore {
	private long current;

	public IdGeneratorHelper_apromore() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
		Date date = new Date();
		String time = dateFormat.format(date);
		current = Long.parseLong(time);
	}
	
	public long getNextId() {
		current = current + 1;
		return current;
	}
	
}
