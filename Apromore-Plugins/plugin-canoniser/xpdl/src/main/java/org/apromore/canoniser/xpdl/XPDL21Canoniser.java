package org.apromore.canoniser.xpdl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.xpdl.internal.Canonical2XPDL;
import org.apromore.canoniser.xpdl.internal.XPDL2Canonical;
import org.apromore.cpf.CanonicalProcessType;
import org.springframework.stereotype.Component;
import org.wfmc._2008.xpdl2.PackageType;


/**
 * XPDL 2.1 Canoniser Plugin
 * 
 * @author Felix Mannhardt (University oaS Bonn-Rhein-Sieg)
 *
 */
@Component("xpdlCanoniser")
public class XPDL21Canoniser extends DefaultAbstractCanoniser {

	public static final String XPDL2_CONTEXT = "org.wfmc._2008.xpdl2";


	/* (non-Javadoc)
	 * @see org.apromore.plugin.Plugin#getType()
	 */
	@Override
	public String getType() {
		return Canoniser.class.getName();
	}


	/* (non-Javadoc)
	 * @see org.apromore.plugin.Plugin#getName()
	 */
	@Override
	public String getName() {
		return XPDL21Canoniser.class.getName();
	}


	/* (non-Javadoc)
	 * @see org.apromore.plugin.Plugin#getVersion()
	 */
	@Override
	public String getVersion() {
		//TODO how to insert bundle version
		return "1.0.0.SNAPSHOT";
	}

	/* (non-Javadoc)
	 * @see org.apromore.plugin.Plugin#getDescription()
	 */
	@Override
	public String getDescription() {		
		return "Default canoniser for XPDL 2.1";
	}

	/* (non-Javadoc)
	 * @see org.apromore.canoniser.Canoniser#getNativeType()
	 */
	@Override
	public String getNativeType() {		
		return "XPDL 2.1";
	}	
	

	/* (non-Javadoc)
	 * @see org.apromore.canoniser.Canoniser#canonise(java.io.InputStream, java.util.List, java.util.List)
	 */
	@Override
	public void canonise(InputStream nativeInput, List<AnnotationsType> annotationFormat, List<CanonicalProcessType> canonicalFormat) throws CanoniserException {
		try {
			JAXBElement<PackageType> nativeElement = unmarshalNativeFormat(nativeInput);
			XPDL2Canonical epml2canonical = new XPDL2Canonical(nativeElement.getValue());
					
			annotationFormat.add(epml2canonical.getAnf());
			canonicalFormat.add(epml2canonical.getCpf());
	        
		} catch (JAXBException e) {
			throw new CanoniserException(e);
		}
	}


	/* (non-Javadoc)
	 * @see org.apromore.canoniser.Canoniser#deCanonise(org.apromore.cpf.CanonicalProcessType, org.apromore.anf.AnnotationsType, java.io.OutputStream)
	 */
	@Override
	public void deCanonise(CanonicalProcessType canonicalFormat, AnnotationsType annotationFormat, OutputStream nativeOutput) throws CanoniserException {

		try {
			Canonical2XPDL canonical2epml;

			if (annotationFormat != null) {
				canonical2epml = new Canonical2XPDL(
				        canonicalFormat,
				        annotationFormat);
			} else {
				canonical2epml = new Canonical2XPDL(canonicalFormat);
			}

			marshalXPDLFormat(canonical2epml.getXpdl(), nativeOutput);
		} catch (JAXBException e) {
			throw new CanoniserException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private JAXBElement<PackageType> unmarshalNativeFormat(InputStream nativeFormat)
			throws JAXBException {
		JAXBContext jc1 = JAXBContext.newInstance(XPDL2_CONTEXT);
		Unmarshaller u = jc1.createUnmarshaller();
		return (JAXBElement<PackageType>) u.unmarshal(nativeFormat);
	}	

	private void marshalXPDLFormat(PackageType xpdl, OutputStream nativeFormat)
			throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(XPDL2_CONTEXT);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		JAXBElement<PackageType> rootepml = new org.wfmc._2008.xpdl2.ObjectFactory()
				.createPackage(xpdl);
		m.marshal(rootepml, nativeFormat);
	}

}
