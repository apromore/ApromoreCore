package org.apromore.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import de.epml.TypeEPML;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.Canonical2EPML;
import org.apromore.canoniser.adapters.Canonical2PNML;
import org.apromore.canoniser.adapters.Canonical2XPDL;
import org.apromore.canoniser.adapters.EPML2Canonical;
import org.apromore.canoniser.adapters.PNML2Canonical;
import org.apromore.canoniser.adapters.XPDL2Canonical;
import org.apromore.canoniser.adapters.pnml2canonical.NamespaceFilter;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.CanoniserException;
import org.apromore.exception.SerializationException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.pnml.PnmlType;
import org.apromore.service.CanoniserService;
import org.apromore.service.helper.CPFtoGraphHelper;
import org.apromore.service.helper.GraphToCPFHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.util.StreamUtil;
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
@Service("CanoniserService")
@Transactional(propagation = Propagation.REQUIRED)
public class CanoniserServiceImpl implements CanoniserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanoniserServiceImpl.class);


    /**
     * @see org.apromore.service.CanoniserService#CPFtoString(org.apromore.cpf.CanonicalProcessType)
     * {@inheritDoc}
     */
    @Override
    public String CPFtoString(CanonicalProcessType cpt) throws JAXBException {
        ByteArrayOutputStream xml = new ByteArrayOutputStream();

        JAXBContext jc = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Marshaller cpf = jc.createMarshaller();
        cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<CanonicalProcessType> canTyp = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(cpt);
        cpf.marshal(canTyp, xml);

        return StreamUtil.convertStreamToString(new ByteArrayInputStream(xml.toByteArray()));
    }

    /**
     * @see org.apromore.service.CanoniserService#canonise(String, String, java.io.InputStream)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CanonisedProcess canonise(String nativeType, String cpf_uri, InputStream process_xml) throws CanoniserException, IOException,
            JAXBException, SAXException {
        CanonisedProcess cp;
        if (nativeType.compareTo(Constants.XPDL_2_1) == 0) {
            cp = canoniseXPDL(cpf_uri, process_xml);
        } else if (nativeType.compareTo(Constants.EPML_2_0) == 0) {
            cp = canoniseEPML(cpf_uri, process_xml);
        } else if (nativeType.compareTo(Constants.PNML_1_3_2) == 0) {
            cp = canonisePNML(cpf_uri, process_xml);
        } else {
            throw new CanoniserException("Native type not supported.");
        }
        return cp;
    }

    /**
     * @see org.apromore.service.CanoniserService#deCanonise
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public DataSource deCanonise(final Integer processId, final String version, final String nativeType,
            final CanonicalProcessType canType, final DataSource anf) {
        AnnotationsType annType = null;
        DataSource native_ds = null;

        try {
            if (anf != null) {
                JAXBContext jc = JAXBContext.newInstance(Constants.ANF_CONTEXT);
                Unmarshaller u = jc.createUnmarshaller();
                JAXBElement<AnnotationsType> rootAnf = (JAXBElement<AnnotationsType>) u.unmarshal(anf.getInputStream());
                annType = rootAnf.getValue();
            }

            ByteArrayOutputStream native_xml = new ByteArrayOutputStream();
            if (nativeType.compareTo(Constants.XPDL_2_1) == 0) {
                native_xml = deCanoniseXPDL(anf, annType, canType);
            } else if (nativeType.compareTo(Constants.EPML_2_0) == 0) {
                native_xml = deCanoniseEPML(anf, annType, canType);
            } else if (nativeType.compareTo(Constants.PNML_1_3_2) == 0) {
                native_xml = deCanonisePNML(anf, annType, canType);
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
     * @see org.apromore.service.CanoniserService#serializeCPF(org.apromore.graph.JBPT.CPF)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CanonicalProcessType serializeCPF(CPF graph) throws SerializationException {
        return GraphToCPFHelper.createCanonicalProcess(graph);
    }

    /**
     * @see org.apromore.service.CanoniserService#deserializeCPF(org.apromore.cpf.CanonicalProcessType)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CPF deserializeCPF(CanonicalProcessType cpf) throws SerializationException {
        return CPFtoGraphHelper.createGraph(cpf);
    }


    /* Canonise the epml to CPF format */
    @SuppressWarnings("unchecked")
    private CanonisedProcess canoniseEPML(String cpf_uri, InputStream process_xml) throws JAXBException, CanoniserException {
        CanonisedProcess cp = new CanonisedProcess();

        JAXBContext jc1 = JAXBContext.newInstance(Constants.EPML_CONTEXT);
        Unmarshaller u = jc1.createUnmarshaller();
        JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(process_xml);
        TypeEPML epml = rootElement.getValue();
        EPML2Canonical epml2canonical = new EPML2Canonical(epml, Long.parseLong(cpf_uri));

        jc1 = JAXBContext.newInstance(Constants.ANF_CONTEXT);
        Marshaller m_anf = jc1.createMarshaller();
        m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<AnnotationsType> cproc_anf = new org.apromore.anf.ObjectFactory().createAnnotations(epml2canonical.getANF());
        cp.setAnt(cproc_anf.getValue());

        jc1 = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Marshaller m_cpf = jc1.createMarshaller();
        m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<CanonicalProcessType> cproc_cpf = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(epml2canonical.getCPF());
        cp.setCpt(cproc_cpf.getValue());

        return cp;
    }

    /* DeCanonise the CPF to EPML format */
    private ByteArrayOutputStream deCanoniseEPML(DataSource anf, AnnotationsType annType, CanonicalProcessType canType)
            throws JAXBException {
        ByteArrayOutputStream native_xml = new ByteArrayOutputStream();
        JAXBContext jc;
        Canonical2EPML canonical2epml;
        if (anf == null) {
            canonical2epml = new Canonical2EPML(canType);
        } else {
            canonical2epml = new Canonical2EPML(canType, annType);
        }
        jc = JAXBContext.newInstance(Constants.EPML_CONTEXT);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(canonical2epml.getEPML());
        m.marshal(rootepml, native_xml);

        return native_xml;
    }


    /* Canonise the XPDL to CPF format */
    @SuppressWarnings("unchecked")
    private CanonisedProcess canoniseXPDL(String cpf_uri, InputStream process_xml) throws JAXBException, CanoniserException {
        ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
        ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();
        CanonisedProcess cp = new CanonisedProcess();

        JAXBContext jc1 = JAXBContext.newInstance(Constants.XPDL2_CONTEXT);
        Unmarshaller u = jc1.createUnmarshaller();
        JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
        PackageType pkg = rootElement.getValue();
        XPDL2Canonical xpdl2canonical = new XPDL2Canonical(pkg, Long.parseLong(cpf_uri));

        jc1 = JAXBContext.newInstance(Constants.ANF_CONTEXT);
        Marshaller m_anf = jc1.createMarshaller();
        m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<AnnotationsType> cproc_anf = new org.apromore.anf.ObjectFactory().createAnnotations(xpdl2canonical.getAnf());
        m_anf.marshal(cproc_anf, anf_xml);
        cp.setAnt(cproc_anf.getValue());
        cp.setAnf(new ByteArrayInputStream(anf_xml.toByteArray()));

        jc1 = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Marshaller m_cpf = jc1.createMarshaller();
        m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<CanonicalProcessType> cproc_cpf = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(xpdl2canonical.getCpf());
        m_cpf.marshal(cproc_cpf, cpf_xml);
        cp.setCpt(cproc_cpf.getValue());
        cp.setCpf(new ByteArrayInputStream(cpf_xml.toByteArray()));

        return cp;
    }

    /* DeCanonise the CPF to XPDL format */
    private ByteArrayOutputStream deCanoniseXPDL(DataSource anf, AnnotationsType annType, CanonicalProcessType canType)
            throws JAXBException, CanoniserException {
        ByteArrayOutputStream native_xml = new ByteArrayOutputStream();
        JAXBContext jc;
        Canonical2XPDL canonical2xpdl;
        if (anf == null) {
            canonical2xpdl = new Canonical2XPDL(canType);
        } else {
            canonical2xpdl = new Canonical2XPDL(canType, annType);
        }
        jc = JAXBContext.newInstance(Constants.XPDL2_CONTEXT);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(canonical2xpdl.getXpdl());
        m.marshal(rootxpdl, native_xml);

        return native_xml;
    }

    /* Canonise the PNML to CPF format */
    @SuppressWarnings("unchecked")
    private CanonisedProcess canonisePNML(String cpf_uri, InputStream process_xml) throws JAXBException,
            CanoniserException, SAXException {
        CanonisedProcess cp = new CanonisedProcess();

        JAXBContext jc1 = JAXBContext.newInstance(Constants.PNML_CONTEXT);
        Unmarshaller u = jc1.createUnmarshaller();
        XMLReader reader = XMLReaderFactory.createXMLReader();
        NamespaceFilter inFilter = new NamespaceFilter("pnml.apromore.org", true);
        inFilter.setParent(reader);
        SAXSource source = new SAXSource(inFilter, new org.xml.sax.InputSource(process_xml));
        JAXBElement<PnmlType> rootElement = (JAXBElement<PnmlType>) u.unmarshal(source);
        PnmlType pkg = rootElement.getValue();
        PNML2Canonical pnml2canonical = new PNML2Canonical(pkg, Long.parseLong(cpf_uri));

        jc1 = JAXBContext.newInstance(Constants.CPF_CONTEXT);
        Marshaller m_cpf = jc1.createMarshaller();
        m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<CanonicalProcessType> cproc_cpf = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(pnml2canonical.getCPF());
        cp.setCpt(cproc_cpf.getValue());

        jc1 = JAXBContext.newInstance(Constants.ANF_CONTEXT);
        Marshaller m_anf = jc1.createMarshaller();
        m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<AnnotationsType> anf = new org.apromore.anf.ObjectFactory().createAnnotations(pnml2canonical.getANF());
        cp.setAnt(anf.getValue());
        return cp;
    }

    /* DeCanonise the CPF to PNML format */
    private ByteArrayOutputStream deCanonisePNML(DataSource anf, AnnotationsType annType, CanonicalProcessType canType)
            throws JAXBException, CanoniserException {
        ByteArrayOutputStream native_xml = new ByteArrayOutputStream();
        JAXBContext jc;
        Canonical2PNML canonical2pnml;
        if (anf == null) {
            canonical2pnml = new Canonical2PNML(canType);
        } else {
            canonical2pnml = new Canonical2PNML(canType, annType);
        }
        jc = JAXBContext.newInstance(Constants.PNML_CONTEXT);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        JAXBElement<PnmlType> rootPNML = new org.apromore.pnml.ObjectFactory().createPnml(canonical2pnml.getPNML());
        m.marshal(rootPNML, native_xml);

        return native_xml;
    }


}
