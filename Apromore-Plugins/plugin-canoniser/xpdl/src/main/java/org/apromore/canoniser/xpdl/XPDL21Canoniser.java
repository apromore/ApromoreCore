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
	 * @see org.apromore.canoniser.Canoniser#canonise(java.io.InputStream, java.util.List, java.util.List)
	 */
	@Override
	public void canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat) throws CanoniserException {
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
	public void deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput) throws CanoniserException {

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
	private JAXBElement<PackageType> unmarshalNativeFormat(final InputStream nativeFormat)
			throws JAXBException {
		JAXBContext jc1 = JAXBContext.newInstance(XPDL2_CONTEXT);
		Unmarshaller u = jc1.createUnmarshaller();
		return (JAXBElement<PackageType>) u.unmarshal(nativeFormat);
	}

	private void marshalXPDLFormat(final PackageType xpdl, final OutputStream nativeFormat)
			throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(XPDL2_CONTEXT);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		JAXBElement<PackageType> rootepml = new org.wfmc._2008.xpdl2.ObjectFactory()
				.createPackage(xpdl);
		m.marshal(rootepml, nativeFormat);
	}

}
