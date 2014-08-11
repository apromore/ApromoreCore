package org.apromore.canoniser.xpdl;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.xpdl.internal.Canonical2XPDL;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Ignore;
import org.junit.Test;
import org.wfmc._2009.xpdl2.PackageType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import static org.junit.Assert.assertTrue;

@Ignore
public class Canonical2XPDLUnitTest {

    @Test
    public void testNothing() {
        assertTrue(true);
    }


	/**
	 * @param args
	 * @throws CanoniserException
	 */
	public void main(String[] args) throws CanoniserException {
		File cpf_file = new File("/powderfinger/home/fauvet/models/eeeee.cpf");
		File anf_initial_file = new File("/powderfinger/home/fauvet/models/test/test-blue-initial.anf");
		File anf_green_file = new File("/powderfinger/home/fauvet/models/test/test-blue-green.anf");
		//File cpf_file = new File("/home/fauvet/models/model1.cpf");
		//File anf_file = new File("/home/fauvet/models/model1.anf");
		try {
			JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_file);
			CanonicalProcessType cpf = rootElement.getValue();

//			jc = JAXBContext.newInstance("org.apromore.anf");
//			u = jc.createUnmarshaller();
//			JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_initial_file);
//			AnnotationsType anf = anfRootElement.getValue();
			
//			Canonical2XPDL canonical2xpdl_with_anf = new Canonical2XPDL (cpf, anf);
			Canonical2XPDL canonical2xpdl_no_anf = new Canonical2XPDL(cpf);
			
			jc = JAXBContext.newInstance("org.wfmc._2009.xpdl2");
			Marshaller m1 = jc.createMarshaller();
			m1.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			
			JAXBElement<PackageType> cprocRootElem1 = 
				new org.wfmc._2009.xpdl2.ObjectFactory().createPackage(canonical2xpdl_no_anf.getXpdl());
			m1.marshal(cprocRootElem1, new File("/coldplay/home/fauvet/models/eeeee.xpdl"));
			
//			jc = JAXBContext.newInstance("org.apromore.anf");
//			u = jc.createUnmarshaller();
//			anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_green_file);
//			anf = anfRootElement.getValue();
			
//			canonical2xpdl_with_anf = new Canonical2XPDL (cpf, anf);
//			jc = JAXBContext.newInstance("org.wfmc._2009.xpdl2");
//			Marshaller m2 = jc.createMarshaller();
//			m2.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
//			
//			JAXBElement<PackageType> cprocRootElem2 = 
//				new org.wfmc._2009.xpdl2.ObjectFactory().createPackage(canonical2xpdl_with_anf.getXpdl());
//			m1.marshal(cprocRootElem2, new File("/coldplay/home/fauvet/models/test/test-blue-green.xpdl"));
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
