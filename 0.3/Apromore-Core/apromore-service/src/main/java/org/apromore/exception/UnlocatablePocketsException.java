package org.apromore.exception;

/**
 * @author Chathura Ekanayake
 */
public class UnlocatablePocketsException extends Exception {

	public UnlocatablePocketsException(String msg) {
		super(msg);
	}
	
	public UnlocatablePocketsException(String msg, Throwable e) {
		super(msg, e);
	}
}
