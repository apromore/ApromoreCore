package org.apromore.canoniser.adapters.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.XPDL2Canonical;
import org.apromore.canoniser.exception.ExceptionStore;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.rlf.RelationsType;
import org.wfmc._2008.xpdl2.PackageType;

public class TestXPDL2Canonical {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		///File file = new File("/home/fauvet/Diagram1.xpdl");
		File file = new File("/home/fauvet/models/model1.xpdl");
		try{
			JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(file);
			PackageType pkg = rootElement.getValue();

			XPDL2Canonical xpdl2canonical = new XPDL2Canonical(pkg);

			jc = JAXBContext.newInstance("org.apromore.cpf");
			Marshaller m = jc.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<CanonicalProcessType> cprocRootElem = 
				new org.apromore.cpf.ObjectFactory().createCanonicalProcess(xpdl2canonical.getCpf());
			//m.marshal(cprocRootElem, new File("/home/fauvet/Diagram1.cpf"));
			m.marshal(cprocRootElem, new File("/home/fauvet/models/model1.cpf"));

			
/*			jc = JAXBContext.newInstance("org.apromore.rlf");
			m = jc.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<RelationsType> relsRootElem = new org.apromore.rlf.ObjectFactory().createRelations(xpdl2canonical.getRlf());
			m.marshal(relsRootElem, new File ("/home/fauvet/model_m.rlf"));		
*/
			jc = JAXBContext.newInstance("org.apromore.anf");
			m = jc.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<AnnotationsType> annsRootElem = new org.apromore.anf.ObjectFactory().createAnnotations(xpdl2canonical.getAnf());
			//m.marshal(annsRootElem, new File ("/home/fauvet/Diagram1.anf"));
			m.marshal(annsRootElem, new File ("/home/fauvet/models/model1.anf"));

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExceptionStore e) {
			e.printStackTrace();
		}
	}

}
