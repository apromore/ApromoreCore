package org.oryxeditor.server.diagram.exception;

/**
 * A node has more than one docker, although it may only have one
 * 
 * @author Philipp Maschke
 *
 */
@SuppressWarnings("serial")
public class TooManyDockersException extends RuntimeException {

	public TooManyDockersException(String message) {
		super(message);
	}
	
}
