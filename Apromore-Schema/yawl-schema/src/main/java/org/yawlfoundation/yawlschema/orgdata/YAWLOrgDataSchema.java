package org.yawlfoundation.yawlschema.orgdata;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Utilities for working with the YAWL OrgData Schema (Validation/Parsing)
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 *
 */
public class YAWLOrgDataSchema {

    private static final String YAWL_ORGDATA_SCHEMA_LOCATION = "/xsd/OrgDataBackup_Schema2.2.xsd";
    private static final String YAWL_ORGDATA_CONTEXT = "org.yawlfoundation.yawlschema.orgdata";

    /**
     * Schema of YAWL
     * 
     * @return
     * @throws SAXException
     */
    public static Schema getYAWLOrgDataSchema() throws SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(YAWLOrgDataSchema.class.getResource(YAWL_ORGDATA_SCHEMA_LOCATION));
        return schema;
    }    
    
    /**
     * Validator for YAWL
     * 
     * @return
     * @throws SAXException
     */
    public static Validator getYAWLOrgDataValidator() throws SAXException {
        return getYAWLOrgDataSchema().newValidator();
    }
    
    /**
     * Marshal the YAWL OrgData Format into the provided OutputStream.
     * 
     * @param yawlOrgDataFormat
     * @param yawlOrgData
     * @throws JAXBException
     * @throws PropertyException
     * @throws SAXException
     */
    public static void marshalYAWLOrgDataFormat(final OutputStream yawlOrgDataFormat, final OrgDataType yawlOrgData, final boolean isValidating) throws JAXBException, PropertyException, SAXException {
        final JAXBContext context = JAXBContext.newInstance(YAWL_ORGDATA_CONTEXT, ObjectFactory.class.getClassLoader());
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        if (isValidating) {
            marshaller.setSchema(getYAWLOrgDataSchema());   
        }            
        final JAXBElement<OrgDataType> yawlOrgDataElement = new ObjectFactory().createOrgdata(yawlOrgData);
        marshaller.marshal(yawlOrgDataElement, yawlOrgDataFormat);
    }


    /**
     * Un-marshal the YAWL OrgData format from the provided InputStream.
     * 
     * @param yawlOrgDataFormat
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    @SuppressWarnings("unchecked")
    public static JAXBElement<OrgDataType> unmarshalYAWLOrgDataFormat(final InputStream yawlOrgDataFormat, final boolean isValidating) throws JAXBException, SAXException {
    	// For JAXB to work with OSGi we have to specify a ClassLoader that 'sees' all classes in YAWL_ORGDATA_CONTEXT
        final JAXBContext jc = JAXBContext.newInstance(YAWL_ORGDATA_CONTEXT, ObjectFactory.class.getClassLoader());
        final Unmarshaller u = jc.createUnmarshaller();
        if (isValidating) {
            u.setSchema(getYAWLOrgDataSchema());   
        }            
        return (JAXBElement<OrgDataType>) u.unmarshal(yawlOrgDataFormat);
    }
    
    /**
     * Un-marshal the YAWL OrgData format from the provided Source.
     * 
     * @param yawlOrgDataFormat
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    @SuppressWarnings("unchecked")
    public static JAXBElement<OrgDataType> unmarshalYAWLOrgDataFormat(final Source yawlOrgDataFormat, final boolean isValidating) throws JAXBException, SAXException {
    	// For JAXB to work with OSGi we have to specify a ClassLoader that 'sees' all classes in YAWL_ORGDATA_CONTEXT
        final JAXBContext jc = JAXBContext.newInstance(YAWL_ORGDATA_CONTEXT, ObjectFactory.class.getClassLoader());
        final Unmarshaller u = jc.createUnmarshaller();
        if (isValidating) {
            u.setSchema(getYAWLOrgDataSchema());   
        }            
        return (JAXBElement<OrgDataType>) u.unmarshal(yawlOrgDataFormat);
    }        

    
}
