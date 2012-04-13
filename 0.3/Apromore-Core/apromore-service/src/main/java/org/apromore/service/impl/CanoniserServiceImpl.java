package org.apromore.service.impl;

import java.io.*;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import de.epml.TypeEPML;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.*;
import org.apromore.canoniser.adapters.pnml2canonical.NamespaceFilter;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.CanoniserException;
import org.apromore.pnml.PnmlType;
import org.apromore.service.CanoniserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wfmc._2008.xpdl2.PackageType;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

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
    @SuppressWarnings("unchecked")
    public DataSource deCanonise(final long processId, final String version, final String nativeType, final DataSource cpf, final DataSource anf) {
        AnnotationsType annType = null;
        DataSource native_ds = null;

        try {
            JAXBContext jc = JAXBContext.newInstance(CPF_CONTEXT);
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf.getInputStream());
            CanonicalProcessType canType = rootElement.getValue();
            if (anf != null) {
                jc = JAXBContext.newInstance(ANF_CONTEXT);
                u = jc.createUnmarshaller();
                JAXBElement<AnnotationsType> rootAnf = (JAXBElement<AnnotationsType>) u.unmarshal(anf.getInputStream());
                annType = rootAnf.getValue();
            }

            ByteArrayOutputStream native_xml = new ByteArrayOutputStream();
            if (nativeType.compareTo("XPDL 2.1") == 0) {
                deCanoniseXPDL(anf, annType, canType, native_xml);
            } else if (nativeType.compareTo("EPML 2.0") == 0) {
                deCanoniseEPML(anf, annType, canType, native_xml);
            }  else if (nativeType.compareTo("PNML 1.3.2") == 0) {
                deCanonisePNML(anf, annType, canType, native_xml);
            }

            InputStream native_xml_is = new ByteArrayInputStream(native_xml.toByteArray());
            native_ds = new ByteArrayDataSource(native_xml_is, "text/xml");
        } catch (JAXBException je) {
            LOGGER.error("DeCanonisation Failed: " + je.getMessage());
        } catch (IOException ioe) {
            LOGGER.error("DeCanonisation Failed: " + ioe.getMessage());
        } catch (CanoniserException ae) {
            LOGGER.error("DeCanonisation Failed: " + ae.getMessage());
        }
        return native_ds;
    }


    /**
     * @see org.apromore.service.CanoniserService#canonise(String, InputStream, String, ByteArrayOutputStream, ByteArrayOutputStream)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void canonise(String cpf_uri, InputStream process_xml, String nativeType, ByteArrayOutputStream anf_xml,
            ByteArrayOutputStream cpf_xml) throws CanoniserException, IOException, JAXBException, SAXException {
        if (nativeType.compareTo("XPDL 2.1") == 0) {
            canoniseXPDL(cpf_uri, process_xml, anf_xml, cpf_xml);
        } else if (nativeType.compareTo("EPML 2.0") == 0) {
            canoniseEPML(cpf_uri, process_xml, anf_xml, cpf_xml);
        } else if (nativeType.compareTo("PNML 1.3.2") == 0) {
            canonisePNML(cpf_uri, process_xml, anf_xml, cpf_xml);
        } else {
            throw new CanoniserException("Native type not supported.");
        }
    }





    /* Canonise the epml to CPF format */
    @SuppressWarnings("unchecked")
    private void canoniseEPML(String cpf_uri, InputStream process_xml, ByteArrayOutputStream anf_xml, ByteArrayOutputStream cpf_xml)
            throws JAXBException, CanoniserException {
        JAXBContext jc1 = JAXBContext.newInstance(EPML_CONTEXT);
        Unmarshaller u = jc1.createUnmarshaller();
        JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(process_xml);
        TypeEPML epml = rootElement.getValue();
        EPML2Canonical epml2canonical = new EPML2Canonical(epml, Long.parseLong(cpf_uri));

        jc1 = JAXBContext.newInstance(ANF_CONTEXT);
        Marshaller m_anf = jc1.createMarshaller();
        m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<AnnotationsType> cproc_anf = new org.apromore.anf.ObjectFactory().createAnnotations(epml2canonical.getANF());
        m_anf.marshal(cproc_anf, anf_xml);

        jc1 = JAXBContext.newInstance(CPF_CONTEXT);
        Marshaller m_cpf = jc1.createMarshaller();
        m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<CanonicalProcessType> cproc_cpf = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(epml2canonical.getCPF());
        m_cpf.marshal(cproc_cpf, cpf_xml);
    }

    /* DeCanonise the CPF to EPML format */
    private void deCanoniseEPML(DataSource anf, AnnotationsType annType, CanonicalProcessType canType, ByteArrayOutputStream native_xml)
            throws JAXBException {
        JAXBContext jc;Canonical2EPML canonical2epml;
        if (anf == null) {
            canonical2epml = new Canonical2EPML(canType);
        } else {
            canonical2epml = new Canonical2EPML(canType, annType);
        }
        jc = JAXBContext.newInstance(EPML_CONTEXT);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(canonical2epml.getEPML());
        m.marshal(rootepml, native_xml);
    }


    /* Canonise the XPDL to CPF format */
    @SuppressWarnings("unchecked")
    private void canoniseXPDL(String cpf_uri, InputStream process_xml, ByteArrayOutputStream anf_xml, ByteArrayOutputStream cpf_xml)
            throws JAXBException, CanoniserException {
        JAXBContext jc1 = JAXBContext.newInstance(Constants.JAXB_CONTEXT_XPDL);
        Unmarshaller u = jc1.createUnmarshaller();
        JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
        PackageType pkg = rootElement.getValue();
        XPDL2Canonical xpdl2canonical = new XPDL2Canonical(pkg, Long.parseLong(cpf_uri));

        jc1 = JAXBContext.newInstance(ANF_CONTEXT);
        Marshaller m_anf = jc1.createMarshaller();
        m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<AnnotationsType> cproc_anf = new org.apromore.anf.ObjectFactory().createAnnotations(xpdl2canonical.getAnf());
        m_anf.marshal(cproc_anf, anf_xml);

        jc1 = JAXBContext.newInstance(CPF_CONTEXT);
        Marshaller m_cpf = jc1.createMarshaller();
        m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<CanonicalProcessType> cproc_cpf = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(xpdl2canonical.getCpf());
        m_cpf.marshal(cproc_cpf, cpf_xml);
    }

    /* DeCanonise the CPF to XPDL format */
    private void deCanoniseXPDL(DataSource anf, AnnotationsType annType, CanonicalProcessType canType, ByteArrayOutputStream native_xml)
            throws JAXBException, CanoniserException {
        JAXBContext jc;Canonical2XPDL canonical2xpdl;
        if (anf == null) {
            canonical2xpdl = new Canonical2XPDL(canType);
        } else {
            canonical2xpdl = new Canonical2XPDL(canType, annType);
        }
        jc = JAXBContext.newInstance(XPDL2_CONTEXT);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(canonical2xpdl.getXpdl());
        m.marshal(rootxpdl, native_xml);
    }

    /* Canonise the PNML to CPF format */
    @SuppressWarnings("unchecked")
    private void canonisePNML(String cpf_uri, InputStream process_xml, ByteArrayOutputStream anf_xml, ByteArrayOutputStream cpf_xml)
            throws JAXBException, CanoniserException, SAXException {
        JAXBContext jc1 = JAXBContext.newInstance(PNML_CONTEXT);
        Unmarshaller u = jc1.createUnmarshaller();
        XMLReader reader = XMLReaderFactory.createXMLReader();
        NamespaceFilter inFilter = new NamespaceFilter("pnml.apromore.org", true);
        inFilter.setParent(reader);
        SAXSource source = new SAXSource(inFilter, new org.xml.sax.InputSource(process_xml));
        JAXBElement<PnmlType> rootElement = (JAXBElement<PnmlType>) u.unmarshal(source);
        PnmlType pkg = rootElement.getValue();
        PNML2Canonical pnml2canonical = new PNML2Canonical(pkg, Long.parseLong(cpf_uri));

        jc1 = JAXBContext.newInstance(CPF_CONTEXT);
        Marshaller m_cpf = jc1.createMarshaller();
        m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<CanonicalProcessType> cproc_cpf = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(pnml2canonical.getCPF());
        m_cpf.marshal(cproc_cpf, cpf_xml);

        jc1 = JAXBContext.newInstance(ANF_CONTEXT);
        Marshaller m_anf = jc1.createMarshaller();
        m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<AnnotationsType> anf = new org.apromore.anf.ObjectFactory().createAnnotations(pnml2canonical.getANF());
        m_anf.marshal(anf, anf_xml);
    }

    /* DeCanonise the CPF to PNML format */
    private void deCanonisePNML(DataSource anf, AnnotationsType annType, CanonicalProcessType canType, ByteArrayOutputStream native_xml)
            throws JAXBException, CanoniserException {
        JAXBContext jc;
        Canonical2PNML canonical2pnml;
        if (anf == null) {
            canonical2pnml = new Canonical2PNML(canType);
        } else {
            canonical2pnml = new Canonical2PNML(canType, annType);
        }
        jc = JAXBContext.newInstance(PNML_CONTEXT);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<PnmlType> rootPNML = new org.apromore.pnml.ObjectFactory().createPnml(canonical2pnml.getPNML());
        m.marshal(rootPNML, native_xml);
    }


}
