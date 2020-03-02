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

package org.apromore.service;

import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.annotation.AnnotationProcessor;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.exception.PluginNotFoundException;

/**
 * Interface for the Annotation Pre Processor Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface AnnotationService {

    /**
     * Lists all installed Annotation Pre Processors for the given native process formats.
     * @param processType the source and target process format supports
     * @return Set of Annotation Pre Processors that support the native process formats
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    Set<AnnotationProcessor> listBySourceAndTargetProcessType(String processType) throws PluginNotFoundException;

    /**
     * Finds first Annotation Pre Processor with the given native types.
     * @param processType the source and target process format supports
     * @return Annotation Pre Processor that support the native process formats
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    AnnotationProcessor findBySourceAndTargetProcessType(String processType) throws PluginNotFoundException;

    /**
     * Finds first Annotation Pre Processor with given native type with given name and specified version.
     * @param processType the source and target process format supports
     * @param name the name of the processor
     * @param version the version of the processor
     * @return annotation that support the native process format
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    AnnotationProcessor findBySourceAndTargetProcessTypeAndNameAndVersion(String processType, String name, String version)
            throws PluginNotFoundException;

    /**
     * Run the Pre Processing for a canonical format and it's annotations. Updates only the ANF and returns the results.
     * @param sourceType the source process format
     * @param targetType the target process format.
     * @param canonicalFormat the canonical process
     * @param annotationFormat the canonical annotations
     * @return AnnotationsType updated with the correct layout info for the target format.
     */
    AnnotationsType preProcess(final String sourceType, final String targetType, final CanonicalProcessType canonicalFormat,
        final AnnotationsType annotationFormat);

}
