/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.annotation.provider;

import java.util.Set;

import org.apromore.annotation.AnnotationProcessor;
import org.apromore.plugin.exception.PluginNotFoundException;

/**
 * Canoniser API used by Apromore, to access the Annotation Processor Plugins
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 */
public interface AnnotationProcessorProvider {

    /**
     * List all available Annotation Post Processors.
     * @return Set of Annotation
     */
    Set<AnnotationProcessor> listAll();

    /**
     * List all available Annotation Post Processors converting the specified source process type and target process type.
     * @param processType for example "EPML 2.0" or "YAWL 2.2"
     * @return Set of Annotations
     */
    Set<AnnotationProcessor> listBySourceAndTargetProcessType(String processType) throws PluginNotFoundException;

    /**
     * List all available Annotation Post Processors converting the specified native type with the exact name.
     * Please note there could be multiple versions installed, so a Collection will be returned.
     * @param processType for example "EPML 2.0" or "YAWL 2.2"
     * @param name usually the full class name of the Annotation
     * @return Set of Annotations
     */
    Set<AnnotationProcessor> listBySourceAndTargetProcessTypeAndName(String processType, String name) throws PluginNotFoundException;

    /**
     * Return the first Annotation Post Processors that is found with the given parameters.
     * @param processType for example "EPML 2.0" or "YAWL 2.2"
     * @return Annotation for given native type
     * @throws PluginNotFoundException in case there is no Annotation Post Processor found
     */
    AnnotationProcessor findBySourceAndTargetProcessType(String processType) throws PluginNotFoundException;

    /**
     * Return the first CanonAnnotation Post Processorsiser that is found with the given parameters.
     * @param processType for example "EPML 2.0" or "YAWL 2.2"
     * @param name usually the full class name of the Annotation
     * @param version usually the bundle version (X.Y.Z-CLASSIFIER)
     * @return Annotation for given native type and name and version
     * @throws PluginNotFoundException in case there is no Annotation Post Processors found
     */
    AnnotationProcessor findBySourceAndTargetProcessTypeAndNameAndVersion(String processType, String name, String version) throws PluginNotFoundException;

}
