/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.apromore.canoniser.provider.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.property.ParameterType;

public class TestCanoniser implements Canoniser {

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
    public String getNativeType() {
        return "testType";
    }

    @Override
    public PluginResult canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat,
            final PluginRequest request) throws CanoniserException {
        return null;
    }

    @Override
    public PluginResult deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput,
            final PluginRequest request) throws CanoniserException {
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
    public PluginResult createInitialNativeFormat(final OutputStream nativeOutput, final String processName, final String processVersion, final String processAuthor,
            final Date processCreated, final PluginRequest request) {
        return null;
    }

    @Override
    public CanoniserMetadataResult readMetaData(final InputStream nativeInput, final PluginRequest request) {
        return null;
    }

    @Override
    public String getEMail() {
        return "";
    }

}
