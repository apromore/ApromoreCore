package nl.rug.ds.bpm.log;

import nl.rug.ds.bpm.log.listener.VerificationLogListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 01-Mar-18.
 */
public class Logger {
	private static int logLevel = 1;
	private static Set<VerificationLogListener> verificationLogListenerSet = new HashSet<>();
	
	public Logger() {
	}
	
	public static void addLogListener(VerificationLogListener verificationLogListener) {
		verificationLogListenerSet.add(verificationLogListener);
	}
	
	public static void removeLogListener(VerificationLogListener verificationLogListener) {
		verificationLogListenerSet.remove(verificationLogListener);
	}
	
	public static void log(String message, int logLevel) {
		if (logLevel >= Logger.logLevel) {
			LogEvent e = new LogEvent(logLevel, message);
			for (VerificationLogListener listener : verificationLogListenerSet)
				listener.verificationLogEvent(e);
		}
	}
	
	public static int getLogLevel() {
		return logLevel;
	}
	
	public static void setLogLevel(int logLevel) {
		Logger.logLevel = logLevel;
	}
}
