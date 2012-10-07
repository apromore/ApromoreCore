package org.apromore.canoniser.epml;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.canoniser.epml.internal.Canonical2EPML;
import org.apromore.canoniser.epml.internal.EPML2Canonical;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.impl.DefaultPluginResult;
import org.apromore.plugin.property.PluginPropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.epml.TypeCoordinates;
import de.epml.TypeDirectory;
import de.epml.TypeEPC;
import de.epml.TypeEPML;

/**
 * EPML 2.0 Canoniser Plugin
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 *
 */
@Component("epmlCanoniser")
public class EPML20Canoniser extends DefaultAbstractCanoniser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EPML20Canoniser.class);

    public static final String EPML_CONTEXT = "de.epml";

    public static final String ADD_FAKE_PROPERTY_ID = "addFakeProperties";

    private final PluginPropertyType<Boolean> fakeEventsProperty;

	public EPML20Canoniser() {
		super();
		this.fakeEventsProperty = new PluginPropertyType<Boolean>(ADD_FAKE_PROPERTY_ID,"Add Fake Events?", "", false, true);
		registerProperty(fakeEventsProperty);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apromore.canoniser.Canoniser#canonise(org.apromore.canoniser.NativeInput, java.io.OutputStream, java.io.OutputStream)
	 */
	@Override
	public PluginResult canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat, final PluginRequest request) throws CanoniserException {

		try {
			JAXBElement<TypeEPML> nativeElement = unmarshalNativeFormat(nativeInput);
			EPML2Canonical epml2canonical = new EPML2Canonical(nativeElement.getValue());

			annotationFormat.add(epml2canonical.getANF());
			canonicalFormat.add(epml2canonical.getCPF());

			return newPluginResult();

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
	public PluginResult deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput, final PluginRequest request) throws CanoniserException {

		try {
			Canonical2EPML canonical2epml;

			if (annotationFormat != null) {
				canonical2epml = new Canonical2EPML(canonicalFormat, annotationFormat,
						request.getRequestProperty(fakeEventsProperty).getValue());
			} else {
				canonical2epml = new Canonical2EPML(canonicalFormat, request.getRequestProperty(fakeEventsProperty).getValue());
			}

			marshalEPMLFormat(canonical2epml.getEPML(), nativeOutput);

			return newPluginResult();

		} catch (JAXBException e) {
			throw new CanoniserException(e);
		} catch (PluginPropertyNotFoundException e) {
		    throw new CanoniserException(e);
        }

	}

	@SuppressWarnings("unchecked")
	private JAXBElement<TypeEPML> unmarshalNativeFormat(final InputStream nativeFormat) throws JAXBException {
		JAXBContext jc1 = JAXBContext.newInstance(EPML_CONTEXT);
		Unmarshaller u = jc1.createUnmarshaller();
		return (JAXBElement<TypeEPML>) u.unmarshal(nativeFormat);
	}

	private void marshalEPMLFormat(final TypeEPML epml, final OutputStream nativeFormat) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(EPML_CONTEXT);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(epml);
		m.marshal(rootepml, nativeFormat);
	}

    /* (non-Javadoc)
     * @see org.apromore.canoniser.Canoniser#createInitialNativeFormat(java.io.OutputStream, org.apromore.plugin.PluginRequest)
     */
    @Override
    public PluginResult createInitialNativeFormat(final OutputStream nativeOutput, final String processName, final String processVersion, final String processAuthor,
            final Date processCreated, final PluginRequest request) {
        // create an empty epml process (see issue 129)
        // then just creation of an empty process.
        TypeEPML epml = new TypeEPML();
        TypeCoordinates coordinates = new TypeCoordinates();
        coordinates.setXOrigin("leftToRight");
        coordinates.setYOrigin("topToBottom");
        epml.setCoordinates(coordinates);
        TypeDirectory directory = new TypeDirectory();
        directory.setName("Root");
        epml.getDirectory().add(directory);
        TypeEPC epc = new TypeEPC();
        epc.setEpcId(new BigInteger("1"));
        if (processName != null) {
            epc.setName(processName);   
        } else {
            epc.setName("");
        }
        directory.getEpcOrDirectory().add(epc);
        
        DefaultPluginResult newPluginResult = newPluginResult();

        try {
            marshalEPMLFormat(epml, nativeOutput);
        } catch (JAXBException e) {
            LOGGER.error("Could not create initial EPML", e);
            newPluginResult.addPluginMessage("Could not create initial EPML, reason: {0}", e.getMessage());
        }
        
        return newPluginResult;
    }

    /* (non-Javadoc)
     * @see org.apromore.canoniser.Canoniser#readMetaData(java.io.InputStream, org.apromore.plugin.PluginRequest)
     */
    @Override
    public CanoniserMetadataResult readMetaData(final InputStream nativeInput, final PluginRequest request) {
        //TODO read metadata from EPML
        return new CanoniserMetadataResult();
    }

}
