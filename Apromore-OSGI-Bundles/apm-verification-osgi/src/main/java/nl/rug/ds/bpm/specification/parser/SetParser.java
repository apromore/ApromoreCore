package nl.rug.ds.bpm.specification.parser;

import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.event.listener.VerificationLogListener;
import nl.rug.ds.bpm.specification.jaxb.*;
import nl.rug.ds.bpm.specification.map.SpecificationTypeMap;
import nl.rug.ds.bpm.specification.marshaller.SpecificationUnmarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;


/**
 * Created by Heerko Groefsema on 29-May-17.
 */
public class SetParser {
	private EventHandler eventHandler;
	private SpecificationTypeMap specificationTypeMap;
	private BPMSpecification bpmSpecification;
	private SpecificationSet specificationSet;
	private Parser parser;
	private int id = 0;
	
	public SetParser() {
		eventHandler = new EventHandler();
		specificationTypeMap = new SpecificationTypeMap();
		
		parser = new Parser(eventHandler, specificationTypeMap);
		bpmSpecification = new BPMSpecification();
		specificationSet = new SpecificationSet();
		bpmSpecification.addSpecificationSet(specificationSet);
		
		loadConfiguration();
	}

	public SetParser(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
		specificationTypeMap = new SpecificationTypeMap();

		parser = new Parser(eventHandler, specificationTypeMap);
		bpmSpecification = new BPMSpecification();
		specificationSet = new SpecificationSet();
		bpmSpecification.addSpecificationSet(specificationSet);

		loadConfiguration();
	}
	
	public void parse(String string) {
		if(string.toLowerCase().startsWith("group"))
			addGroup(string);
		else
			addSpecification(string);
	}
	
	private void addSpecification(String specification) {
		Specification spec = parser.parseSpecification(specification);
		if(spec != null) {
			spec.setId("parsed" + id++);
			specificationSet.addSpecification(spec);
		}
	}
	
	private void addGroup(String group) {
		Group g = parser.parseGroup(group);
		if(group != null)
			bpmSpecification.addGroup(g);
	}
	
	public BPMSpecification getSpecification() {
		return bpmSpecification;
	}
	
	public void addLogListener(VerificationLogListener verificationLogListener) {
		eventHandler.addLogListener(verificationLogListener);
	}
	
	public void removeLogListener(VerificationLogListener verificationLogListener) {
		eventHandler.removeLogListener(verificationLogListener);
	}
	
	private void loadConfiguration() {
		try {
//            File initialFile = new File("/Users/armascer/Work/ApromoreCode/ApromoreCode/Apromore-OSGI-Bundles/apm-verification-osgi/resources/specificationTypes.xml");
//            InputStream targetStream = new FileInputStream(initialFile);
            InputStream targetStream = new java.io.BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("specificationTypes.xml"));

			SpecificationUnmarshaller unmarshaller = new SpecificationUnmarshaller(eventHandler, targetStream);
			loadSpecificationTypes(unmarshaller.getSpecification(), specificationTypeMap);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadSpecificationTypes(BPMSpecification specification, SpecificationTypeMap typeMap) {
		for (SpecificationType specificationType: specification.getSpecificationTypes())
			typeMap.addSpecificationType(specificationType);
	}
}
