package nl.rug.ds.bpm.specification.marshaller;

import nl.rug.ds.bpm.event.EventHandler;
import nl.rug.ds.bpm.specification.jaxb.BPMSpecification;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.OutputStream;

/**
 * Created by Heerko Groefsema on 24-Apr-17.
 */
public class SpecificationMarshaller {
	
	public SpecificationMarshaller(EventHandler eventHandler, BPMSpecification bpmSpecification, File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(BPMSpecification.class);
			Marshaller marshaller = context.createMarshaller();
			
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(bpmSpecification, file);
			
		} catch (JAXBException e) {
			eventHandler.logError("Failed to write" + file.toString());
		}
	}
	
	public SpecificationMarshaller(EventHandler eventHandler, BPMSpecification bpmSpecification, OutputStream stream) {
		try {
			JAXBContext context = JAXBContext.newInstance(BPMSpecification.class);
			Marshaller marshaller = context.createMarshaller();
			
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(bpmSpecification, stream);
			
		} catch (JAXBException e) {
			eventHandler.logError("Failed to write to output stream");
		}
	}
}
