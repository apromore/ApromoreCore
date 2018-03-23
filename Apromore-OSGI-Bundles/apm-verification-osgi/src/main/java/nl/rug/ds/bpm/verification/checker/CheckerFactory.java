package nl.rug.ds.bpm.verification.checker;

import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.event.listener.VerificationEventListener;

import java.io.File;

/**
 * Created by Heerko Groefsema on 09-Jun-17.
 */
public abstract class CheckerFactory {
	protected File executable;
	protected EventHandler eventHandler;
	
	public CheckerFactory(File executable) {
		eventHandler = new EventHandler();
		this.executable = executable;
	}
	
	public abstract Checker getChecker();
	
	public void addEventListener(VerificationEventListener verificationEventListener) {
		eventHandler.addEventListener(verificationEventListener);
	}
	
	public void removeEventListener(VerificationEventListener verificationEventListener) {
		eventHandler.removeEventListener(verificationEventListener);
	}
}
