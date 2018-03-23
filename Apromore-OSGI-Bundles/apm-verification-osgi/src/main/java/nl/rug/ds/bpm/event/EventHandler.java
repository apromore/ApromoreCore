package nl.rug.ds.bpm.event;

import nl.rug.ds.bpm.event.listener.VerificationEventListener;
import nl.rug.ds.bpm.specification.jaxb.Specification;
import nl.rug.ds.bpm.verification.checker.CheckerFormula;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 10-Apr-17.
 */
public class EventHandler {
	private Set<VerificationEventListener> verificationEventListenerSet;

	public EventHandler() {
		verificationEventListenerSet = new HashSet<>();
	}

	public void addEventListener(VerificationEventListener verificationEventListener) {
		verificationEventListenerSet.add(verificationEventListener);
	}

	public void removeEventListener(VerificationEventListener verificationEventListener) {
		verificationEventListenerSet.remove(verificationEventListener);
	}

	public void fireEvent(Specification specification, CheckerFormula formula, boolean eval) {
		VerificationEvent verificationEvent = new VerificationEvent(specification, formula, eval);

		for (VerificationEventListener listener: verificationEventListenerSet)
			listener.verificationEvent(verificationEvent);
	}
}
