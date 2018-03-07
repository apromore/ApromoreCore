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

package org.apromore.canoniser.xpdl;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.canoniser.xpdl.internal.Canonical2XPDL;
import org.apromore.canoniser.xpdl.internal.XPDL2Canonical;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wfmc._2009.xpdl2.*;

import javax.xml.bind.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * XPDL 2.2 Canoniser Plugin
 *
 * @author Pasquale Napoli
 */
@Component("xpdlCanoniser")
public class XPDL22Canoniser extends DefaultAbstractCanoniser {

    private static final Logger LOGGER = LoggerFactory.getLogger(XPDL22Canoniser.class);

    public static final String XPDL2_CONTEXT = "org.wfmc._2009.xpdl2";

    /* (non-Javadoc)
      * @see org.apromore.canoniser.Canoniser#canonise(java.io.InputStream, java.util.List, java.util.List)
      */
    @Override
    public PluginResult canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat,
            final List<CanonicalProcessType> canonicalFormat, final PluginRequest request) throws CanoniserException {
        try {
            JAXBElement<PackageType> nativeElement = unmarshalNativeFormat(nativeInput);
            XPDL2Canonical epml2canonical = new XPDL2Canonical(nativeElement.getValue());

            annotationFormat.add(epml2canonical.getAnf());
            canonicalFormat.add(epml2canonical.getCpf());

            return newPluginResult();
        } catch (JAXBException e) {
            throw new CanoniserException(e);
        }
    }


    /* (non-Javadoc)
      * @see org.apromore.canoniser.Canoniser#deCanonise(org.apromore.cpf.CanonicalProcessType, org.apromore.anf.AnnotationsType, java.io.OutputStream)
      */
    @Override
    public PluginResult deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat,
            final OutputStream nativeFormat, final PluginRequest request) throws CanoniserException {
        try {
            Canonical2XPDL canonical2epml;

            if (annotationFormat != null) {
                canonical2epml = new Canonical2XPDL(canonicalFormat, annotationFormat);
            } else {
                canonical2epml = new Canonical2XPDL(canonicalFormat);
            }
            marshalXPDLFormat(canonical2epml.getXpdl(), nativeFormat);

            return newPluginResult();
        } catch (JAXBException e) {
            throw new CanoniserException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private JAXBElement<PackageType> unmarshalNativeFormat(final InputStream nativeFormat) throws JAXBException {
        JAXBContext jc1 = JAXBContext.newInstance(XPDL2_CONTEXT, ObjectFactory.class.getClassLoader());
        Unmarshaller u = jc1.createUnmarshaller();
        return (JAXBElement<PackageType>) u.unmarshal(nativeFormat);
    }

    private void marshalXPDLFormat(final PackageType xpdl, final OutputStream nativeFormat) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(XPDL2_CONTEXT, ObjectFactory.class.getClassLoader());
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<PackageType> rootepml = new ObjectFactory().createPackage(xpdl);
        m.marshal(rootepml, nativeFormat);
    }

    /* (non-Javadoc)
     * @see org.apromore.canoniser.Canoniser#createInitialNativeFormat(java.io.OutputStream, java.lang.String, java.lang.String, java.lang.String, java.util.Date, org.apromore.plugin.PluginRequest)
     */
    @Override
    public PluginResult createInitialNativeFormat(final OutputStream nativeOutput, final String processName, final String processVersion,
            final String processAuthor, final Date processCreated, final PluginRequest request) {
        PackageType pkg = new PackageType();
        pkg.setName(processName);
        PackageHeader hder = new PackageHeader();
        pkg.setPackageHeader(hder);
        RedefinableHeader rhder = new RedefinableHeader();
        pkg.setRedefinableHeader(rhder);
        Author author = new Author();
        rhder.setAuthor(author);
        author.setValue(processAuthor);
        Version version = new Version();
        rhder.setVersion(version);
        version.setValue(processVersion);
        Created created = new Created();
        hder.setCreated(created);
        if (processCreated != null) {
            created.setValue(DateFormat.getDateTimeInstance().format(processCreated));
        }
        try {
            marshalXPDLFormat(pkg, nativeOutput);
        } catch (JAXBException e) {
            LOGGER.error("Could not create initial XPDL", e);
        }
        return newPluginResult();
    }

    /* (non-Javadoc)
     * @see org.apromore.canoniser.Canoniser#readMetaData(java.io.InputStream, org.apromore.plugin.PluginRequest)
     */
    @Override
    public CanoniserMetadataResult readMetaData(final InputStream nativeInput, final PluginRequest request) {
        CanoniserMetadataResult metadataResult = new CanoniserMetadataResult();

        try {
            JAXBElement<PackageType> rootElement = unmarshalNativeFormat(nativeInput);
            PackageType pkg = rootElement.getValue();

            try {// get process author if defined
                if (pkg.getRedefinableHeader().getAuthor().getValue().trim().compareTo("") != 0) {
                    metadataResult.setProcessAuthor(pkg.getRedefinableHeader().getAuthor().getValue().trim());
                }
            } catch (NullPointerException e) {
                // do nothing
            }

            try {// get process name if defined
                if (pkg.getName().trim().compareTo("") != 0) {
                    metadataResult.setProcessName(pkg.getName().trim());
                }
            } catch (NullPointerException e) {
                // do nothing
            }

            try {// get version name if defined
                if (pkg.getRedefinableHeader().getVersion().getValue().trim().compareTo("") != 0) {
                    metadataResult.setProcessVersion(pkg.getRedefinableHeader().getVersion().getValue().trim());
                }
            } catch (NullPointerException e) {
                // do nothing
            }

            try {// get documentation if defined
                if (pkg.getPackageHeader().getDocumentation().getValue().trim().compareTo("") != 0) {
                    metadataResult.setProcessDocumentation(pkg.getPackageHeader().getDocumentation().getValue().trim());
                }
            } catch (NullPointerException e) {
                // do nothing
            }

            try {// get creation date if defined
                if (pkg.getPackageHeader().getCreated().getValue().trim().compareTo("") != 0) {
                    //TODO parse XPDL date to java date
                    // metadataResult.setProcessCreated(pkg.getPackageHeader().getCreated().getValue().trim());
                }
            } catch (NullPointerException e) {
                // do nothing
            }

            try {// get lastupdate date if defined
                if (pkg.getPackageHeader().getModificationDate().getValue().trim().compareTo("") != 0) {
                    //TODO parse XPDL date to java date
                    //readLastupdate = pkg.getPackageHeader().getModificationDate().getValue().trim();
                }
            } catch (NullPointerException e) {
                // do nothing
            }
        } catch (JAXBException e1) {
            LOGGER.error("Could not read metadata from XPDL", e1);
            metadataResult.addPluginMessage("Could not read metadata from XPDL, reason: {0}", e1.getMessage());
        }

        return metadataResult;
    }


}
