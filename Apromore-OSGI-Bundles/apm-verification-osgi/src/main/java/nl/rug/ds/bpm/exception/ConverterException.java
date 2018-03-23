package nl.rug.ds.bpm.exception;

import nl.rug.ds.bpm.log.LogEvent;
import nl.rug.ds.bpm.log.Logger;

/**
 * Created by Heerko Groefsema on 01-Mar-18.
 */
public class ConverterException extends Exception {
	public ConverterException(String message) {
		super(message);
		Logger.log(message, LogEvent.ERROR);
	}
}
