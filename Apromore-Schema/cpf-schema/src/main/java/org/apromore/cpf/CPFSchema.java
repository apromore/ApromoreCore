package org.apromore.cpf;

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

import org.xml.sax.SAXException;

/**
 * Utilities for working with the CPF (Validation/Parsing)
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 *
 */
public class CPFSchema {

    private static final String CPF_SCHEMA_LOCATION = "/xsd/cpf_0.6.xsd";
    private static final String CPF_CONTEXT = "org.apromore.cpf:org.yawlfoundation.yawlschema";

    /**
     * Schema of CPF
     * 
     * @return
     * @throws SAXException
     */
    public static Schema getCPFSchema() throws SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(CPFSchema.class.getResource(CPF_SCHEMA_LOCATION));
        return schema;
    }    
    
    /**
     * Validator for CPF
     * 
     * @return
     * @throws SAXException
     */
    public static Validator getCPFValidator() throws SAXException {
        return getCPFSchema().newValidator();
    }
    
    /**
     * Marshal the Canonical Process Format into the provided OutputStream.
     * 
     * @param canonicalFormat
     * @param cpf
     * @throws JAXBException
     * @throws PropertyException
     * @throws SAXException
     */
    public static void marshalCanoncialFormat(final OutputStream canonicalFormat, final CanonicalProcessType cpf, final boolean isValidating) throws JAXBException, PropertyException, SAXException {
        final JAXBContext context = JAXBContext.newInstance(CPF_CONTEXT);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        if (isValidating) {
            marshaller.setSchema(getCPFSchema());   
        }
        final JAXBElement<CanonicalProcessType> cproc_cpf = new ObjectFactory().createCanonicalProcess(cpf);
        marshaller.marshal(cproc_cpf, canonicalFormat);
    }


    /**
     * Unmarshal the Canonical Process Format from the provided InputStream.
     * 
     * @param canonicalFormat
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    @SuppressWarnings("unchecked")
    public static JAXBElement<CanonicalProcessType> unmarshalCanonicalFormat(final InputStream canonicalFormat, final boolean isValidating) throws JAXBException, SAXException {
        final JAXBContext jc = JAXBContext.newInstance(CPF_CONTEXT);
        final Unmarshaller u = jc.createUnmarshaller();
        if (isValidating) {
            u.setSchema(getCPFSchema());   
        }
        return (JAXBElement<CanonicalProcessType>) u.unmarshal(canonicalFormat);
    }    

}
