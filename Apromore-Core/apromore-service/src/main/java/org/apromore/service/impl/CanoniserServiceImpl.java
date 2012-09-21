package org.apromore.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.bpmn.BPMN20Canoniser;
import org.apromore.canoniser.epml.EPML20Canoniser;
import org.apromore.canoniser.pnml.PNML132Canoniser;
import org.apromore.canoniser.provider.impl.CanoniserProviderImpl;
import org.apromore.canoniser.xpdl.XPDL21Canoniser;
import org.apromore.canoniser.yawl.YAWL22Canoniser;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.CanoniserException;
import org.apromore.exception.SerializationException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.plugin.exception.PluginNotFoundException;
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
import org.xml.sax.SAXException;

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
    public String CPFtoString(final CanonicalProcessType cpt) throws JAXBException {
        ByteArrayOutputStream xml = new ByteArrayOutputStream();
        try {
            CPFSchema.marshalCanoncialFormat(xml, cpt, false);
        } catch (SAXException e) {
            //TODO add to list of thrown exception
            throw new JAXBException(e);
        }
        return StreamUtil.convertStreamToString(new ByteArrayInputStream(xml.toByteArray()));
    }

    /**
     * @see org.apromore.service.CanoniserService#canonise(String, String, java.io.InputStream)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CanonisedProcess canonise(final String nativeType, final String cpf_uri, final InputStream process_xml) throws CanoniserException {

        LOGGER.info("Canonising process with native type {}", nativeType);

        CanoniserProviderImpl canoniserProvider = new CanoniserProviderImpl();
        //TODO replace by OSGi DI
        // Workaround until OSGi is there
        ArrayList<Canoniser> canoniserList = new ArrayList<Canoniser>();
        //TODO What about Properties like addFakeEvents??
        canoniserList.add(new EPML20Canoniser());
        canoniserList.add(new XPDL21Canoniser());
        canoniserList.add(new PNML132Canoniser());
        canoniserList.add(new YAWL22Canoniser());
        canoniserList.add(new BPMN20Canoniser());
        canoniserProvider.setCanoniserList(canoniserList);

        List<CanonicalProcessType> cpfList = new ArrayList<CanonicalProcessType>();
        List<AnnotationsType> anfList = new ArrayList<AnnotationsType>();

        try {
            canoniserProvider.canonise(nativeType, process_xml, anfList, cpfList);
        } catch (org.apromore.canoniser.exception.CanoniserException | PluginNotFoundException e) {
            throw new CanoniserException("Could not canonise "+nativeType, e);
        }

        CanonisedProcess cp = new CanonisedProcess();
        ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
        ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();

        if (cpfList.size() > 1 || anfList.size() > 1) {
            throw new CanoniserException("Can only process single CPF/ANF pair!");
        } else {
            try {
                //TODO turn validation on as schema is stable
                ANFSchema.marshalAnnotationFormat(anf_xml, anfList.get(0), false);
                cp.setAnf(new ByteArrayInputStream(anf_xml.toByteArray()));
                cp.setAnt(anfList.get(0));

                //TODO turn validation on as schema is stable
                CPFSchema.marshalCanoncialFormat(cpf_xml, cpfList.get(0), false);
                cp.setCpf(new ByteArrayInputStream(cpf_xml.toByteArray()));
                cp.setCpt(cpfList.get(0));

            } catch (PropertyException e) {
                throw new CanoniserException(e);
            } catch (JAXBException e) {
                throw new CanoniserException(e);
            } catch (SAXException e) {
                throw new CanoniserException("Error trying to marshal ANF or CPF. This is probably an internal error in a Canoniser.",e);
            }
        }

        return cp;
    }

    /**
     * @throws JAXBException
     * @throws IOException
     * @see org.apromore.service.CanoniserService#deCanonise
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public DataSource deCanonise(final Integer processId, final String version, final String nativeType,
            final CanonicalProcessType canType, final DataSource anf) throws CanoniserException {

        LOGGER.info("DeCanonising process with native type {}", nativeType);

        CanoniserProviderImpl canoniserProvider = new CanoniserProviderImpl();
        //TODO replace by OSGi DI
        // Workaround until OSGi is there
        ArrayList<Canoniser> canoniserList = new ArrayList<Canoniser>();
        canoniserList.add(new EPML20Canoniser());
        canoniserList.add(new XPDL21Canoniser());
        canoniserList.add(new PNML132Canoniser());
        canoniserList.add(new YAWL22Canoniser());
        canoniserList.add(new BPMN20Canoniser());
        canoniserProvider.setCanoniserList(canoniserList);

        try {
            AnnotationsType annType = null;
            DataSource native_ds = null;

            if (anf != null && anf.getInputStream().available() > 0) {
                annType = ANFSchema.unmarshalAnnotationFormat(anf.getInputStream(), false).getValue();
            }

            ByteArrayOutputStream native_xml = new ByteArrayOutputStream();

            try {
                canoniserProvider.deCanonise(nativeType, annType, canType, native_xml);
            } catch (org.apromore.canoniser.exception.CanoniserException | PluginNotFoundException e) {
                throw new CanoniserException("Could not deCanonise "+nativeType+" Exception was: "+e.getMessage(), e.getCause());
            }

            InputStream native_xml_is = new ByteArrayInputStream(native_xml.toByteArray());
            native_ds = new ByteArrayDataSource(native_xml_is, "text/xml");

            return native_ds;
        } catch (IOException e) {
            throw new CanoniserException("IOException during decanonisation",e);
        } catch (JAXBException e) {
            throw new CanoniserException("JAXBException during decanonisation",e);
        } catch (SAXException e) {
            throw new CanoniserException("Error trying to unmarshal ANF or CPF. This is probably an internal error in a Canoniser.",e);
        }

    }


    /**
     * @see org.apromore.service.CanoniserService#serializeCPF(org.apromore.graph.JBPT.CPF)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CanonicalProcessType serializeCPF(final CPF graph) throws SerializationException {
        return GraphToCPFHelper.createCanonicalProcess(graph);
    }

    /**
     * @see org.apromore.service.CanoniserService#deserializeCPF(org.apromore.cpf.CanonicalProcessType)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CPF deserializeCPF(final CanonicalProcessType cpf) throws SerializationException {
        return CPFtoGraphHelper.createGraph(cpf);
    }


}
