/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.canoniser;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.xml.sax.SAXException;

/**
 * Implements common functionality shared by all Canonisers and reads the supported native type from the Canonisers 'plugin.config' file. The key used
 * is: 'canoniser.nativeType'.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class DefaultAbstractCanoniser extends DefaultParameterAwarePlugin implements Canoniser {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.Canoniser#getNativeType()
     */
    @Override
    public String getNativeType() {
        return getConfigurationByName("canoniser.nativeType");
    }

    /**
     * Marshal the Canonical Process Format into the provided OutputStream with Validation.
     *
     * @param canonicalFormat
     * @param cpf
     * @throws JAXBException
     * @throws SAXException
     */
    protected void marshalCanonicalFormat(final OutputStream canonicalFormat, final CanonicalProcessType cpf) throws JAXBException, SAXException {
        CPFSchema.marshalCanonicalFormat(canonicalFormat, cpf, true);
    }

    /**
     * Marshal the Annotations Format into the provided OutputStream with Validation.
     *
     * @param annotationFormat
     * @param anf
     * @throws JAXBException
     * @throws SAXException
     */
    protected void marshalAnnotationFormat(final OutputStream annotationFormat, final AnnotationsType anf) throws JAXBException, SAXException {
        ANFSchema.marshalAnnotationFormat(annotationFormat, anf, true);
    }

    /**
     * Unmarshals the Canonical Process Format from the provided InputStream with Validation.
     *
     * @param canonicalFormat
     * @return JAXBElement holding CanonicalProcessType
     * @throws JAXBException
     * @throws SAXException
     */
    protected JAXBElement<CanonicalProcessType> unmarshalCanonicalFormat(final InputStream canonicalFormat) throws JAXBException, SAXException {
        return CPFSchema.unmarshalCanonicalFormat(canonicalFormat, true);
    }

    /**
     * Unmarshal the Annotations Format from the provided InputStream without Validation.
     *
     * @param annotationsFormat
     * @return
     * @throws JAXBException
     * @throws SAXException
     */
    protected JAXBElement<AnnotationsType> unmarshalAnnotationFormat(final InputStream annotationFormat) throws JAXBException, SAXException {
        if (annotationFormat != null) {
            return ANFSchema.unmarshalAnnotationFormat(annotationFormat, false);
        } else {
            return null;
        }
    }

}
