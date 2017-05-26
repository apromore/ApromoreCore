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
	
	public void logVerbose(String message) {
		VerificationLogEvent verificationLogEvent = new VerificationLogEvent(VerificationLogEvent.VERBOSE, message);
		
		for(VerificationLogListener listener: verificationLogListenerSet)
			listener.verificationLogEvent(verificationLogEvent);
	}
	
	public void logInfo(String message) {
		VerificationLogEvent verificationLogEvent = new VerificationLogEvent(VerificationLogEvent.INFO, message);
		
		for(VerificationLogListener listener: verificationLogListenerSet)
			listener.verificationLogEvent(verificationLogEvent);
	}
	
	public void logWarning(String message) {
		VerificationLogEvent verificationLogEvent = new VerificationLogEvent(VerificationLogEvent.WARNING, message);
		
		for(VerificationLogListener listener: verificationLogListenerSet)
			listener.verificationLogEvent(verificationLogEvent);
	}
	
	public void logError(String message) {
		VerificationLogEvent verificationLogEvent = new VerificationLogEvent(VerificationLogEvent.ERROR, message);
		
		for(VerificationLogListener listener: verificationLogListenerSet)
			listener.verificationLogEvent(verificationLogEvent);
	}
	
	public void logCritical(String message) {
		VerificationLogEvent verificationLogEvent = new VerificationLogEvent(VerificationLogEvent.CRITICAL, message);
		
		for(VerificationLogListener listener: verificationLogListenerSet)
			listener.verificationLogEvent(verificationLogEvent);
		
		System.exit(-1);
	}
}
