package org.apromore.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.provider.CanoniserProvider;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.SerializationException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.impl.PluginRequestImpl;
import org.apromore.plugin.property.ParameterType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.helper.CPFtoGraphHelper;
import org.apromore.service.helper.GraphToCPFHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.DecanonisedProcess;
import org.apromore.util.StreamUtil;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("CanoniserProvider")
    private CanoniserProvider canoniserProvider;


    /* (non-Javadoc)
     * @see org.apromore.service.CanoniserService#findByNativeType(java.lang.String)
     */
    @Override
    public Set<Canoniser> listByNativeType(final String nativeType) {
        return canoniserProvider.listByNativeType(nativeType);
    }
    
    /* (non-Javadoc)
     * @see org.apromore.service.CanoniserService#findByNativeType(java.lang.String)
     */
    @Override
    public Canoniser findByNativeType(String nativeType) throws PluginNotFoundException {
        return canoniserProvider.findByNativeType(nativeType);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.CanoniserService#findByNativeTypeAndNameAndVersion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Canoniser findByNativeTypeAndNameAndVersion(String nativeType, String name, String version) throws PluginNotFoundException {
        return canoniserProvider.findByNativeTypeAndNameAndVersion(nativeType, name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.service.CanoniserService#canonise(java.lang.String, java.lang.String, java.io.InputStream, java.util.Set)
     */
    @Override
    @Transactional(readOnly = true)
    public CanonisedProcess canonise(final String nativeType, final InputStream processXml, final Set<RequestParameterType<?>> canoniserProperties)
            throws CanoniserException {

        LOGGER.info("Canonising process with native type {}", nativeType);

        List<CanonicalProcessType> cpfList = new ArrayList<CanonicalProcessType>();
        List<AnnotationsType> anfList = new ArrayList<AnnotationsType>();
        CanonisedProcess cp = new CanonisedProcess();

        try {
            Canoniser c = getCanoniserProvider().findByNativeType(nativeType);
            PluginRequestImpl canoniserRequest = new PluginRequestImpl();
            canoniserRequest.addRequestProperty(canoniserProperties);
            PluginResult canoniserResult = c.canonise(processXml, anfList, cpfList, canoniserRequest);
            cp.setMessages(canoniserResult.getPluginMessage());

        } catch (CanoniserException | PluginNotFoundException e) {
            throw new CanoniserException("Could not canonise " + nativeType, e);
        }

        ByteArrayOutputStream anfXml = new ByteArrayOutputStream();
        ByteArrayOutputStream cpfXml = new ByteArrayOutputStream();

        if (cpfList.size() > 1 || anfList.size() > 1) {
            throw new NotYetImplementedException("Canonising to multiple CPF, ANF files is not yet supported!");
        } else {
            try {
                // TODO turn validation on as schema is stable
                ANFSchema.marshalAnnotationFormat(anfXml, anfList.get(0), false);
                cp.setAnf(new ByteArrayInputStream(anfXml.toByteArray()));
                cp.setAnt(anfList.get(0));

                // TODO turn validation on as schema is stable
                CPFSchema.marshalCanoncialFormat(cpfXml, cpfList.get(0), false);
                cp.setCpf(new ByteArrayInputStream(cpfXml.toByteArray()));
                cp.setCpt(cpfList.get(0));

            } catch (JAXBException | SAXException e) {
                throw new CanoniserException("Error trying to marshal ANF or CPF. This is probably an internal error in a Canoniser.", e);
            }
        }

        return cp;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.service.CanoniserService#deCanonise(java.lang.Integer, java.lang.String, java.lang.String,
     * org.apromore.cpf.CanonicalProcessType, javax.activation.DataSource, java.util.Set)
     */
    @Override
    @Transactional(readOnly = true)
    public DecanonisedProcess deCanonise(final Integer processId, final String version, final String nativeType,
            final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final Set<RequestParameterType<?>> canoniserProperties)
            throws CanoniserException {

        LOGGER.info("DeCanonising process with native type {}", nativeType);

        ByteArrayOutputStream nativeXml = new ByteArrayOutputStream();
        DecanonisedProcess decanonisedProces = new DecanonisedProcess();

        try {
            Canoniser c = getCanoniserProvider().findByNativeType(nativeType);
            PluginRequestImpl canoniserRequest = new PluginRequestImpl();
            canoniserRequest.addRequestProperty(canoniserProperties);            
            PluginResult pluginResult = c.deCanonise(canonicalFormat, annotationFormat, nativeXml, canoniserRequest);
            InputStream nativeXmlIs = new ByteArrayInputStream(nativeXml.toByteArray());
            decanonisedProces.setNativeFormat(nativeXmlIs);
            decanonisedProces.setMessages(pluginResult.getPluginMessage());
            return decanonisedProces;
        } catch (PluginNotFoundException e) {
            throw new CanoniserException("Could not deCanonise " + nativeType, e);
        } catch (PluginException e) {
            throw new CanoniserException("Could not deCanonise " + nativeType, e);
        }

    }

    // TODO all the following methods do not really belong here

    /**
     * @see org.apromore.service.CanoniserService#CPFtoString(org.apromore.cpf.CanonicalProcessType) {@inheritDoc}
     */
    @Override
    public String CPFtoString(final CanonicalProcessType cpt) throws JAXBException {
        ByteArrayOutputStream xml = new ByteArrayOutputStream();
        try {
            CPFSchema.marshalCanoncialFormat(xml, cpt, false);
        } catch (SAXException e) {
            // TODO add to list of thrown exception
            throw new JAXBException(e);
        }
        return StreamUtil.convertStreamToString(new ByteArrayInputStream(xml.toByteArray()));
    }

    /**
     * @see org.apromore.service.CanoniserService#serializeCPF(org.apromore.graph.JBPT.CPF) {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CanonicalProcessType serializeCPF(final CPF graph) throws SerializationException {
        return GraphToCPFHelper.createCanonicalProcess(graph);
    }

    /**
     * @see org.apromore.service.CanoniserService#deserializeCPF(org.apromore.cpf.CanonicalProcessType) {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CPF deserializeCPF(final CanonicalProcessType cpf) throws SerializationException {
        return CPFtoGraphHelper.createGraph(cpf);
    }

    /**
     * Getter for unit testing
     *
     * @return
     */
    CanoniserProvider getCanoniserProvider() {
        return canoniserProvider;
    }

    /**
     * Setter for unit testing
     *
     * @param canoniserProvider
     */
    void setCanoniserProvider(final CanoniserProvider canoniserProvider) {
        this.canoniserProvider = canoniserProvider;
    }


}
