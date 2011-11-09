package org.apromore.service.impl;

import de.epml.TypeEPML;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.Canonical2EPML;
import org.apromore.canoniser.adapters.Canonical2XPDL;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.ExceptionAdapters;
import org.apromore.service.CanoniserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wfmc._2008.xpdl2.PackageType;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of the Canoniser Service Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CanoniserServiceImpl implements CanoniserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanoniserServiceImpl.class);

    /**
     * @see org.apromore.service.CanoniserService#deCanonise(long, String, String, javax.activation.DataSource, javax.activation.DataSource)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public DataSource deCanonise(final long processId, final String version, final String nativeType, final DataSource cpf, final DataSource anf) {
        AnnotationsType annType = null;
        DataSource native_ds = null;

        try {
            JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf.getInputStream());
            CanonicalProcessType canType = rootElement.getValue();
            if (anf != null) {
                jc = JAXBContext.newInstance("org.apromore.anf");
                u = jc.createUnmarshaller();
                JAXBElement<AnnotationsType> rootAnf = (JAXBElement<AnnotationsType>) u.unmarshal(anf.getInputStream());
                annType = rootAnf.getValue();
            }

            ByteArrayOutputStream native_xml = new ByteArrayOutputStream();
            if (nativeType.compareTo("XPDL 2.1") == 0) {
                Canonical2XPDL canonical2xpdl;
                if (anf == null) {
                    canonical2xpdl = new Canonical2XPDL(canType);
                } else {
                    canonical2xpdl = new Canonical2XPDL(canType, annType);
                }
                jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
                Marshaller m = jc.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(canonical2xpdl.getXpdl());
                m.marshal(rootxpdl, native_xml);
            } else if (nativeType.compareTo("EPML 2.0") == 0) {
                Canonical2EPML canonical2epml;
                if (anf == null) {
                    canonical2epml = new Canonical2EPML(canType);
                } else {
                    canonical2epml = new Canonical2EPML(canType, annType);
                }
                jc = JAXBContext.newInstance("de.epml");
                Marshaller m = jc.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(canonical2epml.getEPML());
                m.marshal(rootepml, native_xml);
            }

            InputStream native_xml_is = new ByteArrayInputStream(native_xml.toByteArray());
            native_ds = new ByteArrayDataSource(native_xml_is, "text/xml");
        } catch (JAXBException je) {
            LOGGER.error("DeCanonisation Failed: " + je.getMessage());
        } catch (IOException ioe) {
            LOGGER.error("DeCanonisation Failed: " + ioe.getMessage());
        } catch (ExceptionAdapters ae) {
            LOGGER.error("DeCanonisation Failed: " + ae.getMessage());
        }
        return native_ds;
    }

}
