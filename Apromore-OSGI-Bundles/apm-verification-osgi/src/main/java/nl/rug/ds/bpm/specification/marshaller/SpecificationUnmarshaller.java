package nl.rug.ds.bpm.specification.marshaller;

import nl.rug.ds.bpm.exception.SpecificationException;
import nl.rug.ds.bpm.specification.jaxb.BPMSpecification;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;

/**
 * Created by Heerko Groefsema on 07-Apr-17.
 */
public class SpecificationUnmarshaller {
	private BPMSpecification specification;

	public SpecificationUnmarshaller(File file) throws SpecificationException {
		try {
			JAXBContext context = JAXBContext.newInstance(BPMSpecification.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			specification = (BPMSpecification) unmarshaller.unmarshal(file);
		}
		catch (Exception e) {
			throw new SpecificationException("Failed to load " + file.toString());
		}
	}

	public SpecificationUnmarshaller(InputStream is) throws SpecificationException {
		try {
			JAXBContext context = JAXBContext.newInstance(BPMSpecification.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			specification = (BPMSpecification) unmarshaller.unmarshal(is);
		}
		catch (Exception e) {
			throw new SpecificationException("Failed to read input stream");
		}
	}

	public BPMSpecification getSpecification() { return specification; }
}
