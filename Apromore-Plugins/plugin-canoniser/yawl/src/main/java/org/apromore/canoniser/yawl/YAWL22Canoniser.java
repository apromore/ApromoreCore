/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.io.IOUtils;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.canoniser.yawl.internal.Canonical2YAWL;
import org.apromore.canoniser.yawl.internal.YAWL2Canonical;
import org.apromore.canoniser.yawl.internal.impl.Canonical2YAWLImpl;
import org.apromore.canoniser.yawl.internal.impl.MessageManagerImpl;
import org.apromore.canoniser.yawl.internal.impl.YAWL2CanonicalImpl;
import org.apromore.canoniser.yawl.internal.utils.NamespaceFilter;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.PluginResultImpl;
import org.apromore.plugin.exception.PluginPropertyNotFoundException;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.PluginParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.yawlfoundation.yawlschema.MetaDataType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSchema;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;
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

    private final PluginParameterType<InputStream> resourceDataInput;

    /**
     * Default Constructor of YAWL Canoniser
     */
    public YAWL22Canoniser() {
        super();
        resourceDataInput = new PluginParameterType<>("readOrgData", "YAWL Organisational Data", InputStream.class,
                "File (.ybkp) containing the organisational data used during import of the YAWL workflow.", false, Canoniser.CANONISE_PARAMETER);
        registerParameter(resourceDataInput);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.Canoniser#canonise(java.io.InputStream, java.util.List, java.util.List)
     */
    @Override
    public PluginResult canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat, final PluginRequest request)
            throws CanoniserException {

        LOGGER.info("Start canonising {}", getNativeType());

        try {
            final JAXBElement<SpecificationSetFactsType> nativeElement = YAWLSchema.unmarshalYAWLFormat(nativeInput, false);

            PluginResultImpl canoniserResult = newPluginResult();
            final YAWL2Canonical yawl2canonical = new YAWL2CanonicalImpl(new MessageManagerImpl(canoniserResult));

            ParameterType<InputStream> orgDataProperty = request.getRequestParameter(resourceDataInput);

            if (orgDataProperty.hasValue()) {
                LOGGER.debug("Using provided organisational data in YAWL Canoniser");
                try (InputStream organisationalData = orgDataProperty.getValue()) {
                    final JAXBElement<OrgDataType> orgDataElement = unmarshalOrgDataFormat(organisationalData);
                    yawl2canonical.convertToCanonical(nativeElement.getValue(), orgDataElement.getValue());
                } catch (IOException e) {
                    canoniserResult.addPluginMessage("Could not read provided organisational data for YAWL Canoniser. Canonise without organisational model.");
                    yawl2canonical.convertToCanonical(nativeElement.getValue());
                }
            } else {
                yawl2canonical.convertToCanonical(nativeElement.getValue());
            }

            annotationFormat.add(yawl2canonical.getAnf());
            canonicalFormat.add(yawl2canonical.getCpf());

            LOGGER.info("Finished canonising {}", getNativeType());
            return canoniserResult;

        } catch (final JAXBException | PluginPropertyNotFoundException | SAXException e) {
            throw new CanoniserException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.Canoniser#deCanonise(org.apromore.cpf.CanonicalProcessType, org.apromore.anf.AnnotationsType, java.io.OutputStream)
     */
    @Override
    public PluginResult deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput, final PluginRequest request)
            throws CanoniserException {

        LOGGER.info("Start decanonising {}", getNativeType());

        try {
            YAWLCanoniserResult canoniserResult = new YAWLCanoniserResult();
            final Canonical2YAWL canonical2yawl = new Canonical2YAWLImpl(new MessageManagerImpl(canoniserResult));

            if (annotationFormat != null) {
                LOGGER.debug("Decanonising with Annotation");
                canonical2yawl.convertToYAWL(canonicalFormat, annotationFormat);
            } else {
                LOGGER.debug("Decanonising without Annotation");
                canonical2yawl.convertToYAWL(canonicalFormat);
            }
            YAWLSchema.marshalYAWLFormat(nativeOutput, canonical2yawl.getYAWL(), false);

            OutputStream orgDataOutput = new ByteArrayOutputStream();
            YAWLOrgDataSchema.marshalYAWLOrgDataFormat(orgDataOutput, canonical2yawl.getOrgData(), false);
            canoniserResult.setYawlOrgData(orgDataOutput);

            LOGGER.info("Finished decanonising {}", getNativeType());
            return canoniserResult;

        } catch (final JAXBException | SAXException e) {
            throw new CanoniserException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.canoniser.Canoniser#createInitialNativeFormat(java.io.OutputStream, org.apromore.plugin.PluginRequest)
     */
    @Override
    public PluginResult createInitialNativeFormat(final OutputStream nativeOutput, final String processName, final String processVersion, final String processAuthor,
            final Date processCreated, final PluginRequest request) {
        PluginResultImpl result = newPluginResult();
        try {
            IOUtils.copy(getClass().getResourceAsStream("Initial.yawl"), nativeOutput);
        } catch (IOException e) {
            LOGGER.error("Could not create initial YAWL process", e);
            result.addPluginMessage("Could not create initial YAWL process, reson: {0}",  e.getMessage());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.apromore.canoniser.Canoniser#readMetaData(java.io.InputStream, org.apromore.plugin.PluginRequest)
     */
    @Override
    public CanoniserMetadataResult readMetaData(final InputStream nativeInput, final PluginRequest request) {
        CanoniserMetadataResult result = new CanoniserMetadataResult();
        try {
            SpecificationSetFactsType yawlSpecs = YAWLSchema.unmarshalYAWLFormat(nativeInput, false).getValue();
            if (!yawlSpecs.getSpecification().isEmpty()) {
                YAWLSpecificationFactsType spec = yawlSpecs.getSpecification().get(0);
                result.setProcessName(spec.getName() != null ? spec.getName() : spec.getUri());
                if (spec.getMetaData() != null) {
                    MetaDataType metaData = spec.getMetaData();
                    result.setProcessAuthor(convertCreator(metaData.getCreator()));
                    if (metaData.getVersion() != null) {
                        result.setProcessVersion(metaData.getVersion().toPlainString());
                    }
                    result.setProcessDocumentation(metaData.getDescription());
                    if (metaData.getCreated() !=null) {
                        result.setProcessCreated(metaData.getCreated().toGregorianCalendar().getTime());
                    }
                }
            }
        } catch (JAXBException | SAXException e) {
            LOGGER.error("Could not create initial YAWL process", e);
            result.addPluginMessage("Could not read YAWL metadata, reson: {0}",  e.getMessage());
        }
        return result;
    }

    private String convertCreator(final List<String> creator) {
        if (creator != null) {
            StringBuilder authorSb = new StringBuilder();
            Iterator<String> iter = creator.iterator();
            while (iter.hasNext()) {
                authorSb.append(iter.next());
                if (iter.hasNext()) {
                    authorSb.append(", ");
                }
            }
            return authorSb.toString();
        }
        return "";
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

}
