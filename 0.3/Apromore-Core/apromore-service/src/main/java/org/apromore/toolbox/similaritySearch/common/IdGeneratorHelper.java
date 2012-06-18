package org.apromore.toolbox.similaritySearch.common;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IdGeneratorHelper {

	private BigInteger nextId;
	
	public IdGeneratorHelper() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
		Date date = new Date();
		String time = dateFormat.format(date);
		this.nextId = new BigInteger(time);
	}
	public String getNextId() {
		nextId = nextId.add(BigInteger.ONE);
	    return nextId.toString();
	}

}
