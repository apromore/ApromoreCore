package org.apromore.exception;

/**
 * @author Chathura Ekanayake
 */
public class NonEditableVersionException extends Exception {

	public NonEditableVersionException() {
	}

	public NonEditableVersionException(String arg0) {
		super(arg0);
	}

	public NonEditableVersionException(Throwable arg0) {
		super(arg0);
	}

	public NonEditableVersionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
