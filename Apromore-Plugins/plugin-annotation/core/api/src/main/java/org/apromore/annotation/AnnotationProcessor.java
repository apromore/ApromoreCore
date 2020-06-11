/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.annotation;

import org.apromore.anf.AnnotationsType;
import org.apromore.annotation.exception.AnnotationProcessorException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.PluginResult;

/**
 * Interface to an Apromore Annotations post Processor. Each Annotation is build as a OSGi plugin and has to implement this interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface AnnotationProcessor extends ParameterAwarePlugin {

    /**
     * Type of the source native format which this annotation processor can handle. For example "EPML 2.0" or "YAWL 2.2"
     * @return the type of the source process format
     */
    String getProcessFormatProcessor();

    /**
     * Converts the data in native format to the canonical format and its annotation format.
     * @param canonisedFormat the source canonical format.
     * @param annotationFormat the the old annotation format.
     * @return a PluginResult with information about this operation
     * @throws AnnotationProcessorException in case of an Exception during conversion
     */
    PluginResult processAnnotation(CanonicalProcessType canonisedFormat, AnnotationsType annotationFormat)
            throws AnnotationProcessorException;

}
