package nl.rug.ds.bpm.verification;

import nl.rug.ds.bpm.specification.jaxb.BPMSpecification;
import nl.rug.ds.bpm.specification.jaxb.Specification;
import nl.rug.ds.bpm.specification.jaxb.SpecificationSet;
import nl.rug.ds.bpm.event.listener.VerificationEventListener;
import nl.rug.ds.bpm.event.listener.VerificationLogListener;
import nl.rug.ds.bpm.specification.jaxb.SpecificationType;
import nl.rug.ds.bpm.specification.marshaller.SpecificationUnmarshaller;
import nl.rug.ds.bpm.verification.converter.KripkeConverter;
import nl.rug.ds.bpm.verification.stepper.Marking;
import nl.rug.ds.bpm.verification.stepper.Stepper;
import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.specification.map.SpecificationTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by p256867 on 4-4-2017.
 */
public class Verifier {
	private EventHandler eventHandler;
	private Stepper stepper;
	private BPMSpecification bpmSpecification;
	private SpecificationTypeMap specificationTypeMap;
	
	private Set<SetVerifier> kripkeStructures;

    public Verifier(Stepper stepper) {
    	this.stepper = stepper;
    	eventHandler = new EventHandler();
    	specificationTypeMap = new SpecificationTypeMap();
		kripkeStructures = new HashSet<>();
    }
	
	public Verifier(Stepper stepper, EventHandler eventHandler) {
		this.stepper = stepper;
		this.eventHandler = eventHandler;
		specificationTypeMap = new SpecificationTypeMap();
		kripkeStructures = new HashSet<>();
	}

	public void verify(BPMSpecification bpmSpecification, File nusmv2) {
		this.bpmSpecification = bpmSpecification;

		verify(nusmv2);
	}
	
    public void verify(File specification, File nusmv2) {
		if(!(specification.exists() && specification.isFile()))
			eventHandler.logCritical("No such file " + specification.toString());

		SpecificationUnmarshaller unmarshaller = new SpecificationUnmarshaller(eventHandler, specification);
		bpmSpecification = unmarshaller.getSpecification();

		verify(nusmv2);
	}
    
	public void addEventListener(VerificationEventListener verificationEventListener) {
    	eventHandler.addEventListener(verificationEventListener);
	}
	
	public void addLogListener(VerificationLogListener verificationLogListener) {
    	eventHandler.addLogListener(verificationLogListener);
	}
	
	public void removeEventListener(VerificationEventListener verificationEventListener) {
		eventHandler.removeEventListener(verificationEventListener);
	}
	
	public void removeLogListener(VerificationLogListener verificationLogListener) {
		eventHandler.removeLogListener(verificationLogListener);
	}

	private void verify(File nusmv2) {
		if(!(nusmv2.exists() && nusmv2.isFile() && nusmv2.canExecute()))
			eventHandler.logCritical("Unable to call NuSMV2 binary at " + nusmv2.toString());

		eventHandler.logInfo("Loading configuration file");
		loadConfiguration();

		eventHandler.logInfo("Loading specification file");
		List<SetVerifier> verifiers = loadSpecification(bpmSpecification);
		
		eventHandler.logInfo("Verifying specification sets");
		int setid = 0;
		for (SetVerifier verifier: verifiers) {
			eventHandler.logInfo("Verifying set " + ++setid);
			verifier.buildKripke();
			verifier.verify(nusmv2);
		}
	}
		
	private void loadConfiguration() {
		SpecificationUnmarshaller unmarshaller = new SpecificationUnmarshaller(eventHandler, this.getClass().getResourceAsStream("/resources/specificationTypes.xml"));
		loadSpecificationTypes(unmarshaller.getSpecification(), specificationTypeMap);
	}
	
	private List<SetVerifier> loadSpecification(BPMSpecification specification) {
    	List<SetVerifier> verifiers = new ArrayList<>();

		loadSpecificationTypes(specification, specificationTypeMap);
		
		for(SpecificationSet specificationSet: specification.getSpecificationSets()) {
			SetVerifier setVerifier = new SetVerifier(eventHandler, stepper, specification, specificationSet);
			verifiers.add(setVerifier);
		}

		return verifiers;
    }

	private void loadSpecificationTypes(BPMSpecification specification, SpecificationTypeMap typeMap) {
		for (SpecificationType specificationType: specification.getSpecificationTypes()) {
			typeMap.addSpecificationType(specificationType);
			eventHandler.logVerbose("Adding specification type " + specificationType.getId());
		}

		for (SpecificationSet set: specification.getSpecificationSets())
			for (Specification spec: set.getSpecifications())
				if(typeMap.getSpecificationType(spec.getType()) != null)
					spec.setSpecificationType(typeMap.getSpecificationType(spec.getType()));
				else
					eventHandler.logWarning("No such specification type: " + spec.getType());
	}
	
	
	public static void setMaximumTokensAtPlaces(int maximum) {
		Marking.setMaximumTokensAtPlaces(maximum);
	}
	
	public static int getMaximumTokensAtPlaces() {
    	return Marking.getMaximumTokensAtPlaces();
	}
	
	public static void setMaximumStates(int max) {
    	KripkeConverter.setMaximumStates(max);
    }
	
	public static int getMaximumStates() {
    	return KripkeConverter.getMaximumStates();
    }
}
