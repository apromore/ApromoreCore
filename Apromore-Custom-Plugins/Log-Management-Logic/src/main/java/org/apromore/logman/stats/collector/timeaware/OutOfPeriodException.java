package org.apromore.logman.stats.collector.timeaware;

public class OutOfPeriodException extends Exception {
	public OutOfPeriodException(String errorMessage) {
		super(errorMessage);
	}
}
