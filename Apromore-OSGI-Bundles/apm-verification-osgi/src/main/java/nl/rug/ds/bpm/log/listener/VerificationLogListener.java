package nl.rug.ds.bpm.log.listener;

import nl.rug.ds.bpm.log.LogEvent;

/**
 * Created by Heerko Groefsema on 10-Apr-17.
 */
public interface VerificationLogListener {
	void verificationLogEvent(LogEvent event);
}
