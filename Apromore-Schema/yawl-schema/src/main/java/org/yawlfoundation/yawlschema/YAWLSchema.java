/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.yawlfoundation.yawlschema;

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
 * Utilities for working with the YAWL Schema (Validation/Parsing)
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 *
 */
public class YAWLSchema {
    
        private static final String EXPORTED_SCHEMA_LOCATION = "http://www.yawlfoundation.org/yawlschema http://www.yawlfoundation.org/yawlschema/YAWL_Schema2.1.xsd";
        private static final String YAWL_SCHEMA_LOCATION = "/xsd/YAWL_Schema2.2.xsd";
        private static final String YAWL_CONTEXT = "org.yawlfoundation.yawlschema";

        /**
         * Schema of YAWL
         * 
         * @return
         * @throws SAXException
         */
        public static Schema getYAWLSchema() throws SAXException {
            SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(YAWLSchema.class.getResource(YAWL_SCHEMA_LOCATION));
            return schema;
        }    
        
        /**
         * Validator for YAWL
         * 
         * @return
         * @throws SAXException
         */
        public static Validator getYAWLValidator() throws SAXException {
            return getYAWLSchema().newValidator();
        }
        
        /**
         * Marshal the YAWL Format into the provided OutputStream.
         * 
         * @param yawlFormat
         * @param yawlSpec
         * @throws JAXBException
         * @throws PropertyException
         * @throws SAXException
         */
        public static void marshalYAWLFormat(final OutputStream yawlFormat, final SpecificationSetFactsType yawlSpec, final boolean isValidating) throws JAXBException, PropertyException, SAXException {
        	// For JAXB to work with OSGi we have to specify a ClassLoader that 'sees' all classes in YAWL_CONTEXT
            final JAXBContext context = JAXBContext.newInstance(YAWL_CONTEXT, ObjectFactory.class.getClassLoader());
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, EXPORTED_SCHEMA_LOCATION);
            if (isValidating) {
                marshaller.setSchema(getYAWLSchema());   
            }            
            final JAXBElement<SpecificationSetFactsType> yawlSpecElement = new ObjectFactory().createSpecificationSet(yawlSpec);
            marshaller.marshal(yawlSpecElement, yawlFormat);
        }

        /**
         * Un-marshal the YAWL format from the provided InputStream.
         * 
         * @param yawlFormat
         * @return
         * @throws JAXBException
         * @throws SAXException
         */
        @SuppressWarnings("unchecked")
        public static JAXBElement<SpecificationSetFactsType> unmarshalYAWLFormat(final InputStream yawlFormat, final boolean isValidating) throws JAXBException, SAXException {
        	// For JAXB to work with OSGi we have to specify a ClassLoader that 'sees' all classes in YAWL_CONTEXT 
            final JAXBContext jc = JAXBContext.newInstance(YAWL_CONTEXT, ObjectFactory.class.getClassLoader());
            final Unmarshaller u = jc.createUnmarshaller();
            if (isValidating) {
                u.setSchema(getYAWLSchema());   
            }            
            return (JAXBElement<SpecificationSetFactsType>) u.unmarshal(yawlFormat);
        }    

}