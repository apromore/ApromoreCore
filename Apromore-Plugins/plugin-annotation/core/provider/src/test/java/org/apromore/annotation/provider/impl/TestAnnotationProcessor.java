/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013, 2015 - 2017 Queensland University of Technology.
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

package org.apromore.annotation.provider.impl;

import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.annotation.AnnotationProcessor;
import org.apromore.annotation.exception.AnnotationProcessorException;
import org.apromore.annotation.result.AnnotationPluginResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.property.ParameterType;

public class TestAnnotationProcessor implements AnnotationProcessor {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getProcessFormatProcessor() {
        return "testType";
    }

    @Override
    public AnnotationPluginResult processAnnotation(CanonicalProcessType canonisedFormat, AnnotationsType annotationFormat)
            throws AnnotationProcessorException {
        return null;
    }

    @Override
    public Set<ParameterType<?>> getAvailableParameters() {
        return null;
    }

    @Override
    public Set<ParameterType<?>> getMandatoryParameters() {
        return null;
    }

    @Override
    public Set<ParameterType<?>> getOptionalParameters() {
        return null;
    }

    @Override
    public String getEMail() {
        return "";
    }

}
