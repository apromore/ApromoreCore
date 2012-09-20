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
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
public abstract class DefaultAbstractCanoniser extends AbstractPropertyAwarePlugin implements Canoniser {

	protected void marshalCanoncialFormat(final OutputStream canonicalFormat, final CanonicalProcessType cpf) throws JAXBException,
			PropertyException, SAXException {
		CPFSchema.marshalCanoncialFormat(canonicalFormat, cpf, true);
	}

	protected void marshalAnnotationFormat(final OutputStream annotationFormat, final AnnotationsType anf) throws JAXBException,
			PropertyException, SAXException {
		ANFSchema.marshalAnnotationFormat(annotationFormat, anf, true);
	}

	protected JAXBElement<CanonicalProcessType> unmarshalCanonicalFormat(final InputStream canonicalFormat) throws JAXBException, SAXException {
		return CPFSchema.unmarshalCanonicalFormat(canonicalFormat, true);
	}

	protected JAXBElement<AnnotationsType> unmarshalAnnotationFormat(final InputStream annotationFormat) throws JAXBException, SAXException {
		if (annotationFormat != null) {

			return ANFSchema.unmarshalAnnotationFormat(annotationFormat, false);

		} else {
			return null;
		}
	}

}
