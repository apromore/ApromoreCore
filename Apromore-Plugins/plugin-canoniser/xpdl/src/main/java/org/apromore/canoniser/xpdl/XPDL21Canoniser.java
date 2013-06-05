package org.apromore.canoniser.xpdl;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
import org.wfmc._2008.xpdl2.Author;
import org.wfmc._2008.xpdl2.Created;
import org.wfmc._2008.xpdl2.ObjectFactory;
import org.wfmc._2008.xpdl2.PackageHeader;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.RedefinableHeader;
import org.wfmc._2008.xpdl2.Version;

/**
 * XPDL 2.1 Canoniser Plugin
 *
 * @author Felix Mannhardt (University oaS Bonn-Rhein-Sieg)
 */
@Component("xpdlCanoniser")
public class XPDL21Canoniser extends DefaultAbstractCanoniser {

    private static final Logger LOGGER = LoggerFactory.getLogger(XPDL21Canoniser.class);

    public static final String XPDL2_CONTEXT = "org.wfmc._2008.xpdl2";

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
        JAXBElement<PackageType> rootepml = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(xpdl);
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
