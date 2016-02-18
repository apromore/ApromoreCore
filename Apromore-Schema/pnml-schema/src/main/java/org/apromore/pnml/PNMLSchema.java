/*
 * Copyright © 2009-2015 The Apromore Initiative.
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

package org.apromore.pnml;

import org.apromore.pnml.cache.CachedJaxbContext;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for working with the YAWL Schema (Validation/Parsing)
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 *
 */
public class PNMLSchema {

    private static final String EXPORTED_SCHEMA_LOCATION = "pnml.apromore.org pnml.xsd";
    private static final String PNML_SCHEMA_LOCATION = "/xsd/pnml.xsd";
    private static final String PNML_CONTEXT = "org.apromore.pnml";

    /**
     * Schema of PNML.
     *
     * @return the PNML Schema.
     * @throws org.xml.sax.SAXException
     */
    public static Schema getPNMLSchema() throws SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(PNMLSchema.class.getResource(PNML_SCHEMA_LOCATION));
    }

    /**
     * Validator for PNML.
     *
     * @return the PNML Validator.
     * @throws org.xml.sax.SAXException
     */
    public static Validator getPNMLValidator() throws SAXException {
        return getPNMLSchema().newValidator();
    }

    /**
     * Marshal the PNML. Format into the provided OutputStream.
     *
     * @param pnmlFormat the output stream.
     * @param pnmlSpec the pnml specification.
     * @throws javax.xml.bind.JAXBException
     * @throws javax.xml.bind.PropertyException
     * @throws org.xml.sax.SAXException
     */
    public static void marshalPNMLFormat(final OutputStream pnmlFormat, final PnmlType pnmlSpec, final boolean isValidating)
            throws JAXBException, PropertyException, SAXException {
        final JAXBContext context = CachedJaxbContext.getJaxbContext(PNML_CONTEXT, org.apromore.pnml.ObjectFactory.class.getClassLoader());
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        if (isValidating) {
            marshaller.setSchema(getPNMLSchema());
        }
        final JAXBElement<PnmlType> pnmlSpecElement = new org.apromore.pnml.ObjectFactory().createPnml(pnmlSpec);
        marshaller.marshal(pnmlSpecElement, pnmlFormat);
    }

    /**
     * Un-marshal the PNML format from the provided InputStream.
     *
     * @param pnmlFormat the pnml Input Stream.
     * @return the JAXB objects.
     * @throws javax.xml.bind.JAXBException
     * @throws org.xml.sax.SAXException
     */
    @SuppressWarnings("unchecked")
    public static JAXBElement<PnmlType> unmarshalPNMLFormat(final InputStream pnmlFormat, final boolean isValidating)
            throws JAXBException, SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        NamespaceFilter inFilter = new NamespaceFilter("pnml.apromore.org", true);
        inFilter.setParent(reader);
        SAXSource source = new SAXSource(inFilter, new org.xml.sax.InputSource(pnmlFormat));

        final JAXBContext jc = CachedJaxbContext.getJaxbContext(PNML_CONTEXT, org.apromore.pnml.ObjectFactory.class.getClassLoader());
        final Unmarshaller u = jc.createUnmarshaller();
        if (isValidating) {
            u.setSchema(getPNMLSchema());
        }
        return (JAXBElement<PnmlType>) u.unmarshal(source);
    }

}
