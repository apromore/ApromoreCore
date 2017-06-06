package nl.rug.ds.bpm.event;

import nl.rug.ds.bpm.specification.jaxb.Specification;
import nl.rug.ds.bpm.event.listener.VerificationEventListener;
import nl.rug.ds.bpm.event.listener.VerificationLogListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 10-Apr-17.
 */
public class EventHandler {
	private static int logLevel = 1;
	
	private Set<VerificationEventListener> verificationEventListenerSet;
	private Set<VerificationLogListener> verificationLogListenerSet;
	
	public EventHandler() {
		verificationEventListenerSet = new HashSet<>();
		verificationLogListenerSet = new HashSet<>();
	}
	
	public void addEventListener(VerificationEventListener verificationEventListener) {
		verificationEventListenerSet.add(verificationEventListener);
	}
	
	public void removeEventListener(VerificationEventListener verificationEventListener) {
		verificationEventListenerSet.remove(verificationEventListener);
	}
	
	public void addLogListener(VerificationLogListener verificationLogListener) {
		verificationLogListenerSet.add(verificationLogListener);
	}
	
	public void removeLogListener(VerificationLogListener verificationLogListener) {
		verificationLogListenerSet.remove(verificationLogListener);
	}
	
	public void fireEvent(Specification specification, String formula, boolean eval) {
		VerificationEvent verificationEvent = new VerificationEvent(specification, formula, eval);
		
		for (VerificationEventListener listener: verificationEventListenerSet)
			listener.verificationEvent(verificationEvent);
	}
	
	public void logDebug(String message) {
		pushLog(new VerificationLogEvent(VerificationLogEvent.DEBUG, message));
	}
	
	public void logVerbose(String message) {
		pushLog(new VerificationLogEvent(VerificationLogEvent.VERBOSE, message));
	}
	
	public void logInfo(String message) {
		pushLog(new VerificationLogEvent(VerificationLogEvent.INFO, message));
	}
	
	public void logWarning(String message) {
		pushLog(new VerificationLogEvent(VerificationLogEvent.WARNING, message));
	}
	
	public void logError(String message) {
		pushLog(new VerificationLogEvent(VerificationLogEvent.ERROR, message));
	}
	
	public void logCritical(String message) {
		pushLog(new VerificationLogEvent(VerificationLogEvent.CRITICAL, message));
		
		System.exit(-1);
	}
	
	private void pushLog(VerificationLogEvent e) {
		if(e.getLogLevel() >= EventHandler.logLevel)
			for(VerificationLogListener listener: verificationLogListenerSet)
				listener.verificationLogEvent(e);
	}
	
	public static void setLogLevel(int logLevel) { EventHandler.logLevel = logLevel; }
	
	public static int getLogLevel() { return logLevel; }
}
