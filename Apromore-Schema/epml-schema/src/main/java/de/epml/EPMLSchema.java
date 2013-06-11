package de.epml;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import de.epml.cache.CachedJaxbContext;
import org.xml.sax.SAXException;

/**
 * Utilities for working with the YAWL Schema (Validation/Parsing)
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 *
 */
public class EPMLSchema {

    private static final String EXPORTED_SCHEMA_LOCATION = "http://www.epml.de EPML_2.0.xsd";
    private static final String EPML_SCHEMA_LOCATION = "/xsd/EPML_2.0.xsd";
    private static final String EPML_CONTEXT = "de.epml";

    /**
     * Schema of EPML
     *
     * @return
     * @throws SAXException
     */
    public static Schema getEPMLSchema() throws SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(EPMLSchema.class.getResource(EPML_SCHEMA_LOCATION));
    }

    /**
     * Validator for EPML
     *
     * @return
     * @throws SAXException
     */
    public static Validator getEPMLValidator() throws SAXException {
        return getEPMLSchema().newValidator();
    }

    /**
     * Marshal the EPML Format into the provided OutputStream.
     *
     * @param epmlFormat
     * @param epmlSpec
     * @throws JAXBException
     * @throws PropertyException
     * @throws SAXException
     */
    public static void marshalEPMLFormat(final OutputStream epmlFormat, final TypeEPML epmlSpec, final boolean isValidating)
            throws JAXBException, PropertyException, SAXException {
        final JAXBContext context = CachedJaxbContext.getJaxbContext(EPML_CONTEXT, ObjectFactory.class.getClassLoader());
                //JAXBContext.newInstance(EPML_CONTEXT, ObjectFactory.class.getClassLoader());
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, EXPORTED_SCHEMA_LOCATION);
        if (isValidating) {
            marshaller.setSchema(getEPMLSchema());
        }
        final JAXBElement<TypeEPML> epmlSpecElement = new ObjectFactory().createEpml(epmlSpec);
        marshaller.marshal(epmlSpecElement, epmlFormat);
    }

    /**
     * Un-marshal the EPML format from the provided InputStream.
     *
     * @param epmlFormat
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    @SuppressWarnings("unchecked")
    public static JAXBElement<TypeEPML> unmarshalEPMLFormat(final InputStream epmlFormat, final boolean isValidating)
            throws JAXBException, SAXException {
        final JAXBContext jc = CachedJaxbContext.getJaxbContext(EPML_CONTEXT, ObjectFactory.class.getClassLoader());
                //JAXBContext.newInstance(EPML_CONTEXT, ObjectFactory.class.getClassLoader());
        final Unmarshaller u = jc.createUnmarshaller();
        if (isValidating) {
            u.setSchema(getEPMLSchema());
        }
        return (JAXBElement<TypeEPML>) u.unmarshal(epmlFormat);
    }

}
