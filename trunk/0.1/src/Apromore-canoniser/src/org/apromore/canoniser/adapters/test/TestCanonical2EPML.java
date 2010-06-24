package org.apromore.canoniser.adapters.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.Canonical2EPML;
import org.apromore.canoniser.exception.ExceptionStore;
import org.apromore.cpf.CanonicalProcessType;

import de.epml.TypeEPML;

public class TestCanonical2EPML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//File cpf_file = new File("/home/fauvet/models/epml_models/SAP_1.cpf");
		//File anf_file = new File("/home/fauvet/models/epml_models/SAP_1.anf");
		File cpf_file = new File("/home/fauvet/models/test/HKG4 International Depature Departure Check-in.xpdl.cpf");
		File anf_file = new File("/home/fauvet/models/test/HKG4 International Depature Departure Check-in.xpdl.anf");		
		try {
			JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_file);
			CanonicalProcessType cpf = rootElement.getValue();
			
			jc = JAXBContext.newInstance("org.apromore.anf");
			u = jc.createUnmarshaller();
			JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_file);
			AnnotationsType anf = anfRootElement.getValue();

			Canonical2EPML canonical2epml_1 = new Canonical2EPML(cpf);
			
			jc = JAXBContext.newInstance("de.epml");
			
			Marshaller m2 = jc.createMarshaller();
			m2.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<TypeEPML> cprocRootElem2 = 
				new de.epml.ObjectFactory().createEpml(canonical2epml_1.getEPML());
			m2.marshal(cprocRootElem2, new File("/home/fauvet/models/test/truc_without.epml"));
			
			Canonical2EPML canonical2epml_2 = new Canonical2EPML (cpf, anf);
			
			Marshaller m1 = jc.createMarshaller();
			m1.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<TypeEPML> cprocRootElem1 = 
				new de.epml.ObjectFactory().createEpml(canonical2epml_2.getEPML());
			m1.marshal(cprocRootElem1, new File("/home/fauvet/models/test/truc_with.epml"));
			
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
