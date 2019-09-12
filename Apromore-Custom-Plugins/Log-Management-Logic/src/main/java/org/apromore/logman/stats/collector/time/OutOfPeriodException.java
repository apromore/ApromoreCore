package org.apromore.logman.stats.collector.time;

public class OutOfPeriodException extends Exception {
	public OutOfPeriodException(String errorMessage) {
		super(errorMessage);
	}
}
