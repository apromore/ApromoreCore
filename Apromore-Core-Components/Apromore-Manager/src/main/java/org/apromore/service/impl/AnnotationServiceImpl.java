/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
import java.util.Arrays;
import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.annotation.AnnotationProcessor;
import org.apromore.annotation.exception.AnnotationProcessorException;
import org.apromore.annotation.provider.AnnotationProcessorProvider;
import org.apromore.annotation.result.AnnotationPluginResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.service.AnnotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the Canoniser Service Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class AnnotationServiceImpl implements AnnotationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationServiceImpl.class);

    private AnnotationProcessorProvider annProvider;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationProcessorProvider Annotation Processor Provider.
     */
    @Inject
    public AnnotationServiceImpl(final @Qualifier("annotationProcessorProvider") AnnotationProcessorProvider annotationProcessorProvider) {
        annProvider = annotationProcessorProvider;
    }



    /* (non-Javadoc)
     * @see org.apromore.service.AnnotationService#listBySourceAndTargetProcessType(java.lang.String)
     */
    @Override
    public Set<AnnotationProcessor> listBySourceAndTargetProcessType(String processType) throws PluginNotFoundException {
        return annProvider.listBySourceAndTargetProcessType(processType);
    }
    
    /* (non-Javadoc)
     * @see org.apromore.service.AnnotationService#findBySourceAndTargetProcessType(java.lang.String)
     */
    @Override
    public AnnotationProcessor findBySourceAndTargetProcessType(String processType) throws PluginNotFoundException {
        return annProvider.findBySourceAndTargetProcessType(processType);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.AnnotationService#findBySourceAndTargetProcessTypeAndNameAndVersion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public AnnotationProcessor findBySourceAndTargetProcessTypeAndNameAndVersion(String processType, String name, String version)
            throws PluginNotFoundException {
        return annProvider.findBySourceAndTargetProcessTypeAndNameAndVersion(processType, name, version);
    }

    /* (non-Javadoc)
     * @see org.apromore.service.AnnotationService#preProcess(java.lang.String, java.lang.String, CanonicalProcessType, AnnotationsType)
     */
    @Override
    public AnnotationsType preProcess(final String sourceType, final String targetType, final CanonicalProcessType canonicalFormat,
            final AnnotationsType annotationFormat) {
        AnnotationPluginResult result = null;

        try {
            if (canonicalFormat != null) {
                if (targetType != null) {
                    AnnotationProcessor preProcessor = findBySourceAndTargetProcessType(sourceType + " " + targetType);
                    result = (AnnotationPluginResult) preProcessor.processAnnotation(canonicalFormat, annotationFormat);
                }
            }
        } catch (PluginNotFoundException | AnnotationProcessorException e) {
            LOGGER.warn("Plugin not found for '" + sourceType + "' and '" + targetType + "'.");
            LOGGER.debug("Plugin not found because of exception",e);

        }

        if (result == null || result.getAnnotationsType() == null) {
            return annotationFormat;
        } else {
            return result.getAnnotationsType();
        }
    }

}
