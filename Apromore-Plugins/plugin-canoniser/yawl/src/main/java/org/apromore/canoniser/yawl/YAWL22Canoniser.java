/**
 * Copyright 2012, Felix Mannhardt
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.Canonical2YAWL;
import org.apromore.canoniser.yawl.internal.YAWL2Canonical;
import org.apromore.canoniser.yawl.internal.impl.Canonical2YAWLImpl;
import org.apromore.canoniser.yawl.internal.impl.YAWL2CanonicalImpl;
import org.apromore.canoniser.yawl.internal.utils.NamespaceFilter;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.property.DefaultProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSchema;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;
import org.yawlfoundation.yawlschema.orgdata.YAWLOrgDataSchema;

/**
 * Canoniser plugin for YAWL 2.2/2.3
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
@Component("yawlCanoniser")
public class YAWL22Canoniser extends DefaultAbstractCanoniser {

    private static final Logger LOGGER = LoggerFactory.getLogger(YAWL22Canoniser.class);

    private static final String YAWL_ORGDATA_URI = "http://www.yawlfoundation.org/yawlschema/orgdata";

    private final DefaultProperty resourceDataInput;
    private final DefaultProperty resourceDataOutput;

    /**
     * Default Constructor of YAWL Canoniser
     */
    public YAWL22Canoniser() {
        super();
        resourceDataInput = new DefaultProperty("Read Organisational Data", InputStream.class,
                "Reads a .ybkp file containing the organisational data used in this YAWL workflow.", false);
        resourceDataOutput = new DefaultProperty("Write Organisational Data", OutputStream.class,
                "Writes a .ybkp file containing the organisational data used in this YAWL workflow.", false);
        addProperty(resourceDataInput);
        addProperty(resourceDataOutput);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.Canoniser#canonise(java.io.InputStream, java.util.List, java.util.List)
     */
    @Override
    public void canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat)
            throws CanoniserException {

        LOGGER.info("Start canonising %s", getNativeType());

        try {
            final JAXBElement<SpecificationSetFactsType> nativeElement = unmarshalNativeFormat(nativeInput);

            final YAWL2Canonical yawl2canonical = new YAWL2CanonicalImpl();

            if (resourceDataInput.hasValue()) {

                LOGGER.debug("Using provided organisational data in Canoniser");

                final InputStream organisationalData = (InputStream) resourceDataInput.getValue();
                final JAXBElement<OrgDataType> orgDataElement = unmarshalOrgDataFormat(organisationalData);
                yawl2canonical.convertToCanonical(nativeElement.getValue(), orgDataElement.getValue());

            } else {
                yawl2canonical.convertToCanonical(nativeElement.getValue());
            }

            annotationFormat.add(yawl2canonical.getAnf());
            canonicalFormat.add(yawl2canonical.getCpf());

        } catch (final JAXBException e) {
            throw new CanoniserException(e);
        } catch (final SAXException e) {
            throw new CanoniserException(e);
        }

        LOGGER.info("Finished canonising %s", getNativeType());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.Canoniser#deCanonise(org.apromore.cpf.CanonicalProcessType, org.apromore.anf.AnnotationsType, java.io.OutputStream)
     */
    @Override
    public void deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput)
            throws CanoniserException {

        LOGGER.info("Start decanonising %s", getNativeType());

        try {
            final Canonical2YAWL canonical2yawl = new Canonical2YAWLImpl();

            if (annotationFormat != null) {
                LOGGER.debug("Decanonising with Annotation");
                canonical2yawl.convertToYAWL(canonicalFormat, annotationFormat);
            } else {
                LOGGER.debug("Decanonising without Annotation");
                canonical2yawl.convertToYAWL(canonicalFormat);
            }

            marshalYAWLFormat(canonical2yawl.getYAWL(), nativeOutput);
            // TODO
            // YAWLOrgDataSchema.marshalYAWLOrgDataFormat((OutputStream) resourceDataOutput.getValue(), canonical2yawl.getOrgData(), true);

        } catch (final JAXBException e) {
            throw new CanoniserException(e);
        } catch (final SAXException e) {
            throw new CanoniserException(e);
        }

        LOGGER.info("Finished decanonising %s", getNativeType());

    }

    private JAXBElement<OrgDataType> unmarshalOrgDataFormat(final InputStream organisationalData) throws JAXBException, SAXException {
        final NamespaceFilter namespaceFilter = new NamespaceFilter(YAWL_ORGDATA_URI, true);
        // Create an XMLReader to use with our filter
        final XMLReader reader = XMLReaderFactory.createXMLReader();
        namespaceFilter.setParent(reader);
        // Prepare the input, in this case a java.io.File (output)
        final InputSource is = new InputSource(organisationalData);

        // Create a SAXSource specifying the filter
        final SAXSource source = new SAXSource(namespaceFilter, is);

        return YAWLOrgDataSchema.unmarshalYAWLOrgDataFormat(source, false);
    }

    private JAXBElement<SpecificationSetFactsType> unmarshalNativeFormat(final InputStream nativeFormat) throws JAXBException, SAXException {
        // Also try to parse non-valid YAWL XML
        return YAWLSchema.unmarshalYAWLFormat(nativeFormat, false);
    }

    private void marshalYAWLFormat(final SpecificationSetFactsType yawlSpecification, final OutputStream nativeFormat) throws JAXBException,
            SAXException {
        // Always validate our own output
        YAWLSchema.marshalYAWLFormat(nativeFormat, yawlSpecification, true);
    }

}
