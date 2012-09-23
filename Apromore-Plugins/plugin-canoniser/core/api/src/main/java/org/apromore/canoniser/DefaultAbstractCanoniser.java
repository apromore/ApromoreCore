/**
 *  Copyright 2012, Felix Mannhardt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.AbstractPropertyAwarePlugin;
import org.xml.sax.SAXException;

/**
 * Implements common functionality shared by all Canonisers
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public abstract class DefaultAbstractCanoniser extends AbstractPropertyAwarePlugin implements Canoniser {

    /**
     * Marshal the Canonical Process Format into the provided OutputStream with Validation.
     *
     * @param canonicalFormat
     * @param cpf
     * @throws JAXBException
     * @throws PropertyException
     * @throws SAXException
     */
    protected void marshalCanoncialFormat(final OutputStream canonicalFormat, final CanonicalProcessType cpf) throws JAXBException, SAXException {
        CPFSchema.marshalCanoncialFormat(canonicalFormat, cpf, true);
    }

    /**
     * Marshal the Annotations Format into the provided OutputStream with Validation.
     *
     * @param annotationFormat
     * @param anf
     * @throws JAXBException
     * @throws PropertyException
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
