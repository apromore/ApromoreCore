package nl.rug.ds.bpm.pnml.verifier;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import nl.rug.ds.bpm.event.VerificationEvent;
import nl.rug.ds.bpm.event.VerificationLogEvent;
import nl.rug.ds.bpm.event.listener.VerificationEventListener;
import nl.rug.ds.bpm.event.listener.VerificationLogListener;
import nl.rug.ds.bpm.verification.Verifier;

/**
 * Created by Heerko Groefsema on 07-Apr-17.
 */
public class PnmlVerifier implements VerificationEventListener, VerificationLogListener {
	
	public static void main(String [ ] args) {
		if(args.length > 2) {
			PnmlVerifier pnmlVerifier = new PnmlVerifier(args[0], args[1], args[2]);
		} 
		else {
			System.out.println("Usage: PNMLVerifier PNML_file Specification_file NuSMV2_binary_path");
		}
	}
	
	public PnmlVerifier(String pnml, String specification, String nusmv2) {
		File pnmlFile = new File(pnml);
		File specificationFile = new File(specification);
		File nusmv2Binary = new File(nusmv2);

		//Set maximum amount of tokens at a single place
		//Safety feature, prevents infinite models
		//Standard value of 3
		Verifier.setMaximumTokensAtPlaces(3);
		
		//Set maximum size of state space
		//Safety feature, prevents memory issues
		//Standard value of 7 million
		//(equals models of 4 parallel branches with each 50 activities)
		//Lower if on machine with limited memory
		Verifier.setMaximumStates(7000000);

		//Make step class for specific Petri net type
		ExtPnmlStepper stepper;
		try {
			stepper = new ExtPnmlStepper(pnmlFile);
			
			//Make a verifier which uses that step class
			Verifier verifier = new Verifier(stepper);

			//Implement listeners and
			//Add listeners to receive log and result notifications
			verifier.addLogListener(this);
			verifier.addEventListener(this);
			
			//Start verification
			verifier.verify(specificationFile, nusmv2Binary);
			
			//Remove listeners
			verifier.removeLogListener(this);
			verifier.removeEventListener(this);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	//Listener implementations
	@Override
	public void verificationEvent(VerificationEvent event) {
		//Use for user feedback
		//Event returns, specification id, formula, type, result, and specification itself
		System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] FEEDBACK\t: " + event.toString());
	}
	
	@Override
	public void verificationLogEvent(VerificationLogEvent event) {
		//Use for log and textual user feedback
		if(event.getLogLevel() > VerificationLogEvent.VERBOSE)
			System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + event.toString());
	}
}
