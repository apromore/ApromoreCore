package org.apromore.canoniser.pnml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.pnml.internal.Canonical2PNML;
import org.apromore.canoniser.pnml.internal.PNML2Canonical;
import org.apromore.canoniser.pnml.internal.pnml2canonical.NamespaceFilter;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.pnml.PnmlType;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * PNML 1.3.2 Canoniser Plugin
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
@Component("pnmlCanoniser")
public class PNML132Canoniser extends DefaultAbstractCanoniser {

	private static final String PNML_CONTEXT = "org.apromore.pnml";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.Plugin#getName()
	 */
	@Override
	public String getName() {
		return PNML132Canoniser.class.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.Plugin#getVersion()
	 */
	@Override
	public String getVersion() {
		return "1.0.0-SNAPSHOT";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.Plugin#getType()
	 */
	@Override
	public String getType() {
		return Canoniser.class.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.Plugin#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Default PNML 1.3.2 Canoniser";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.canoniser.Canoniser#getNativeType()
	 */
	@Override
	public String getNativeType() {
		return "PNML 1.3.2";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.canoniser.Canoniser#canonise(org.apromore.canoniser.NativeInput, java.io.OutputStream, java.io.OutputStream)
	 */
	@Override
	public void canonise(InputStream nativeInput, List<AnnotationsType> annotationFormat, List<CanonicalProcessType> canonicalFormat) throws CanoniserException {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			NamespaceFilter inFilter = new NamespaceFilter("pnml.apromore.org", true);
			inFilter.setParent(reader);
			SAXSource source = new SAXSource(inFilter, new org.xml.sax.InputSource(nativeInput));

			JAXBElement<PnmlType> nativeElement = unmarshalNativeFormat(source);
			PNML2Canonical pnml2canonical = new PNML2Canonical(nativeElement.getValue());

			annotationFormat.add(pnml2canonical.getANF());
			canonicalFormat.add(pnml2canonical.getCPF());

		} catch (JAXBException e) {
			throw new CanoniserException(e);
		} catch (SAXException e) {
			throw new CanoniserException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.canoniser.Canoniser#deCanonise(java.io.InputStream, java.io.InputStream, org.apromore.canoniser.NativeOutput)
	 */
	@Override
	public void deCanonise(CanonicalProcessType canonicalFormat, AnnotationsType annotationFormat, OutputStream nativeOutput) throws CanoniserException {
		try {
			Canonical2PNML canonical2pnml;

			if (annotationFormat != null) {
				canonical2pnml = new Canonical2PNML(canonicalFormat, annotationFormat);
			} else {
				canonical2pnml = new Canonical2PNML(canonicalFormat);
			}

			marshalNativeFormat(canonical2pnml.getPNML(), nativeOutput);
		} catch (JAXBException e) {
			throw new CanoniserException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private JAXBElement<PnmlType> unmarshalNativeFormat(SAXSource nativeFormat) throws JAXBException {
		JAXBContext jc1 = JAXBContext.newInstance(PNML_CONTEXT);
		Unmarshaller u = jc1.createUnmarshaller();
		return (JAXBElement<PnmlType>) u.unmarshal(nativeFormat);
	}

	private void marshalNativeFormat(PnmlType pnml, OutputStream nativeFormat) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(PNML_CONTEXT);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		JAXBElement<PnmlType> rootepml = new org.apromore.pnml.ObjectFactory().createPnml(pnml);
		m.marshal(rootepml, nativeFormat);
	}

}
