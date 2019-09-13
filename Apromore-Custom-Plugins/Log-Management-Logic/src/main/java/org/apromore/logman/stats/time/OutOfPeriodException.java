package org.apromore.logman.stats.time;

public class OutOfPeriodException extends Exception {
	public OutOfPeriodException(String errorMessage) {
		super(errorMessage);
	}
}
