package org.apromore.canoniser.epml;

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
import org.apromore.canoniser.epml.internal.Canonical2EPML;
import org.apromore.canoniser.epml.internal.EPML2Canonical;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.property.BooleanProperty;
import org.springframework.stereotype.Component;

import de.epml.TypeEPML;

/**
 * EPML 2.0 Canoniser Plugin
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
@Component("epmlCanoniser")
public class EPML20Canoniser extends DefaultAbstractCanoniser {

	public static final String EPML_CONTEXT = "de.epml";

	private BooleanProperty fakeEventsProperty;

	public EPML20Canoniser() {
		super();
		this.fakeEventsProperty = new BooleanProperty("addFakeProperties", "", false, true);
		addProperty(fakeEventsProperty);
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
	 * @see org.apromore.plugin.Plugin#getName()
	 */
	@Override
	public String getName() {
		return EPML20Canoniser.class.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.Plugin#getVersion()
	 */
	@Override
	public String getVersion() {
		// TODO how to insert bundle version
		return "1.0.0.SNAPSHOT";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.plugin.Plugin#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Default canoniser for EPML";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.canoniser.Canoniser#getNativeType()
	 */
	@Override
	public String getNativeType() {
		return "EPML 2.0";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apromore.canoniser.Canoniser#canonise(org.apromore.canoniser.NativeInput, java.io.OutputStream, java.io.OutputStream)
	 */
	@Override
	public void canonise(InputStream nativeInput, List<AnnotationsType> annotationFormat, List<CanonicalProcessType> canonicalFormat) throws CanoniserException {

		try {
			JAXBElement<TypeEPML> nativeElement = unmarshalNativeFormat(nativeInput);
			EPML2Canonical epml2canonical = new EPML2Canonical(nativeElement.getValue());

			annotationFormat.add(epml2canonical.getANF());
			canonicalFormat.add(epml2canonical.getCPF());

		} catch (JAXBException e) {
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
			Canonical2EPML canonical2epml;

			if (annotationFormat != null) {
				canonical2epml = new Canonical2EPML(canonicalFormat, annotationFormat,
						fakeEventsProperty.getValueAsBoolean());
			} else {
				canonical2epml = new Canonical2EPML(canonicalFormat, fakeEventsProperty.getValueAsBoolean());
			}

			marshalEPMLFormat(canonical2epml.getEPML(), nativeOutput);
		} catch (JAXBException e) {
			throw new CanoniserException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private JAXBElement<TypeEPML> unmarshalNativeFormat(InputStream nativeFormat) throws JAXBException {
		JAXBContext jc1 = JAXBContext.newInstance(EPML_CONTEXT);
		Unmarshaller u = jc1.createUnmarshaller();
		return (JAXBElement<TypeEPML>) u.unmarshal(nativeFormat);
	}

	private void marshalEPMLFormat(TypeEPML epml, OutputStream nativeFormat) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(EPML_CONTEXT);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(epml);
		m.marshal(rootepml, nativeFormat);
	}

}
