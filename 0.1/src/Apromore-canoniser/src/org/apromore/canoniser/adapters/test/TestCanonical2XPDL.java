package org.apromore.canoniser.adapters.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.Canonical2XPDL;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.rlf.RelationsType;
import org.wfmc._2008.xpdl2.PackageType;

public class TestCanonical2XPDL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File cpf_file = new File("/home/fauvet/models/model1.cpf");
		File anf_file = new File("/home/fauvet/models/model1.anf");
		File rlf_file = new File("/home/fauvet/models/model1.rlf");
		
		try {
			JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_file);
			CanonicalProcessType cpf = rootElement.getValue();
			
			jc = JAXBContext.newInstance("org.apromore.rlf");
			u = jc.createUnmarshaller();
			JAXBElement<RelationsType> relsRootElement = (JAXBElement<RelationsType>) u.unmarshal(rlf_file);
			RelationsType rlf = relsRootElement.getValue();

			jc = JAXBContext.newInstance("org.apromore.anf");
			u = jc.createUnmarshaller();
			JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_file);
			AnnotationsType anf = anfRootElement.getValue();

			Canonical2XPDL canonical2xpdl_2 = new Canonical2XPDL (cpf, rlf, anf);
			
			jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
			
			Marshaller m2 = jc.createMarshaller();
			m2.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<PackageType> cprocRootElem2 = 
				new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(canonical2xpdl_2.getXpdl());
			m2.marshal(cprocRootElem2, new File("/tmp/model1_2.xpdl"));
			
			RelationsType empty_rlf = new RelationsType();
			empty_rlf.getRelation().clear();
			Canonical2XPDL canonical2xpdl_1 = new Canonical2XPDL (cpf, empty_rlf, anf);
			
			Marshaller m1 = jc.createMarshaller();
			m1.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<PackageType> cprocRootElem1 = 
				new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(canonical2xpdl_1.getXpdl());
			m1.marshal(cprocRootElem1, new File("/tmp/model1_1.xpdl"));
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
