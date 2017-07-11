package nl.rug.ds.bpm.event.listener;

import nl.rug.ds.bpm.event.VerificationLogEvent;

/**
 * Created by Heerko Groefsema on 10-Apr-17.
 */
public interface VerificationLogListener {
	void verificationLogEvent(VerificationLogEvent event);
}
