/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
package org.apromore.service.impl;

import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.provider.CanoniserProvider;
import org.apromore.common.ConfigBean;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.DecanonisedProcess;
import org.apromore.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

/**
 * Implementation of the Canoniser Service Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class CanoniserServiceImpl implements CanoniserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanoniserServiceImpl.class);

    private CanoniserProvider canProvider;
    private boolean enableCPF;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param canoniserProvider Canoniser Provider.
     */
    @Inject
    public CanoniserServiceImpl(final @Qualifier("canoniserProvider") CanoniserProvider canoniserProvider,
                                final ConfigBean config) {
        canProvider = canoniserProvider;
        enableCPF = config.getEnableCPF();
    }


    /* (non-Javadoc)
     * @see org.apromore.service.CanoniserService#findByNativeType(java.lang.String)
     */
    @Override
    public Set<Canoniser> listByNativeType(final String nativeType) {
        return canProvider.listByNativeType(nativeType);
    }
    
    /* (non-Javadoc)
     * @see org.apromore.service.CanoniserService#findByNativeType(java.lang.String)
     */
    @Override
    public Canoniser findByNativeType(String nativeType) throws PluginNotFoundException {
        return canProvider.findByNativeType(nativeType);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.CanoniserService#findByNativeTypeAndNameAndVersion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Canoniser findByNativeTypeAndNameAndVersion(String nativeType, String name, String version) throws PluginNotFoundException {
        return canProvider.findByNativeTypeAndNameAndVersion(nativeType, name, version);
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.service.CanoniserService#canonise(java.lang.String, java.io.InputStream, java.util.Set)
     */
    @Override
    public CanonisedProcess canonise(final String nativeType, final InputStream processXml, final Set<RequestParameterType<?>> canoniserProperties)
            throws CanoniserException {
        LOGGER.debug("Canonising process with native type {}", nativeType);

        CanonisedProcess cp = new CanonisedProcess();
        cp.setOriginal(processXml);

        if (enableCPF) {
            List<CanonicalProcessType> cpfList = new ArrayList<>();
            List<AnnotationsType> anfList = new ArrayList<>();

            ByteArrayOutputStream anfXml = new ByteArrayOutputStream();
            ByteArrayOutputStream cpfXml = new ByteArrayOutputStream();

            try {
                Canoniser c = canProvider.findByNativeType(nativeType);
                PluginRequestImpl canoniserRequest = new PluginRequestImpl();
                canoniserRequest.addRequestProperty(canoniserProperties);
                PluginResult canoniserResult = c.canonise(processXml, anfList, cpfList, canoniserRequest);
                cp.setMessages(canoniserResult.getPluginMessage());

                if (cpfList.size() > 1 || anfList.size() > 1) {
                    throw new CanoniserException("Canonising to multiple CPF, ANF files is not yet supported!");
                } else {
                    try {
                        ANFSchema.marshalAnnotationFormat(anfXml, anfList.get(0), false);
                        cp.setAnf(new ByteArrayInputStream(anfXml.toByteArray()));
                        cp.setAnt(anfList.get(0));

                        CPFSchema.marshalCanonicalFormat(cpfXml, cpfList.get(0), false);
                        cp.setCpf(new ByteArrayInputStream(cpfXml.toByteArray()));
                        cp.setCpt(cpfList.get(0));
                    } catch (JAXBException | SAXException e) {
                        throw new CanoniserException("Error trying to marshal ANF or CPF. This is probably an internal error in a Canoniser.", e);
                    }
                }
            } catch (CanoniserException | PluginNotFoundException e) {
                throw new CanoniserException("Could not canonise " + nativeType, e);
            }
        }

        return cp;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.service.CanoniserService#deCanonise(java.lang.String, org.apromore.cpf.CanonicalProcessType, javax.activation.DataSource, java.util.Set)
     */
    @Override
    public DecanonisedProcess deCanonise(final String nativeType,
            final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat,
            final Set<RequestParameterType<?>> canoniserProperties) throws CanoniserException {
        LOGGER.debug("DeCanonising process with native type {}", nativeType);

        ByteArrayOutputStream nativeXml = new ByteArrayOutputStream();
        DecanonisedProcess decanonisedProcess = new DecanonisedProcess();

        try {
            Canoniser c = canProvider.findByNativeType(nativeType);
            PluginRequestImpl canoniserRequest = new PluginRequestImpl();
            canoniserRequest.addRequestProperty(canoniserProperties);            
            PluginResult pluginResult = c.deCanonise(canonicalFormat, annotationFormat, nativeXml, canoniserRequest);
            InputStream nativeXmlIs = new ByteArrayInputStream(nativeXml.toByteArray());
            decanonisedProcess.setNativeFormat(nativeXmlIs);
            decanonisedProcess.setMessages(pluginResult.getPluginMessage());
        } catch (PluginException e) {
            throw new CanoniserException("Could not deCanonise " + nativeType, e);
        }

        return decanonisedProcess;
    }



    // TODO all the following methods do not really belong here

    /**
     * @see org.apromore.service.CanoniserService#CPFtoString(org.apromore.cpf.CanonicalProcessType)
     * {@inheritDoc}
     */
    @Override
    public String CPFtoString(final CanonicalProcessType cpt) throws JAXBException {
        ByteArrayOutputStream xml = new ByteArrayOutputStream();
        try {
            CPFSchema.marshalCanonicalFormat(xml, cpt, false);
        } catch (SAXException e) {
            throw new JAXBException(e);
        }
        return StreamUtil.convertStreamToString(new ByteArrayInputStream(xml.toByteArray()));
    }

    /**
     * @see org.apromore.service.CanoniserService#XMLtoCPF(String)
     * {@inheritDoc}
     */
    @Override
    public CanonicalProcessType XMLtoCPF(final String xml) throws JAXBException {
        CanonicalProcessType cpf;
        try {
            JAXBElement<CanonicalProcessType> jaxb = CPFSchema.unmarshalCanonicalFormat(new ByteArrayInputStream(xml.getBytes()) ,false);
            cpf = jaxb.getValue();
        } catch (SAXException e) {
            throw new JAXBException(e);
        }
        return cpf;
    }

}
