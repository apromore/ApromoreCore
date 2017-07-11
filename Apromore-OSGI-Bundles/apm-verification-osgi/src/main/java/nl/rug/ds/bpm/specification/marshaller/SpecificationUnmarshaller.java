package nl.rug.ds.bpm.specification.marshaller;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.specification.jaxb.BPMSpecification;

/**
 * Created by Heerko Groefsema on 07-Apr-17.
 */
public class SpecificationUnmarshaller {
	private EventHandler eventHandler;
	private BPMSpecification specification;
	
	public SpecificationUnmarshaller(EventHandler eventHandler, File file) {
		this.eventHandler = eventHandler;
		try {
			JAXBContext context = JAXBContext.newInstance(BPMSpecification.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			specification = (BPMSpecification) unmarshaller.unmarshal(file);
		} 
		catch (Exception e) {
			eventHandler.logCritical("Failed to load " + file.toString());
		}
	}
	
	public SpecificationUnmarshaller(EventHandler eventHandler, InputStream is) {
		this.eventHandler = eventHandler;
		try {
			JAXBContext context = JAXBContext.newInstance(BPMSpecification.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			specification = (BPMSpecification) unmarshaller.unmarshal(is);
		} 
		catch (Exception e) {
			e.printStackTrace();
			eventHandler.logCritical("Failed to read input stream");
		}
	}
	
	public BPMSpecification getSpecification() { return specification; }
}
