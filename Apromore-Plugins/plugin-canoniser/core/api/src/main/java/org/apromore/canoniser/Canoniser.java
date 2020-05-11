/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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
package org.apromore.canoniser;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;

/**
 * Interface to an Apromore canoniser. Each canoniser is build as a OSGi plugin and has to implement this interface.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 */
public interface Canoniser extends ParameterAwarePlugin {

    static final String CANONISE_PARAMETER = "org.apromore.canoniser.canonise";
    static final String DECANONISE_PARAMETER = "org.apromore.canoniser.decanonise";

    /**
     * Type of the native format which this canoniser can handle. For example "EPML 2.0" or "YAWL 2.2"
     *
     * @return the type of the native format
     */
    String getNativeType();

    /**
     * Converts the data in native format to the canonical format and its annotation format.
     *
     * @param nativeInput
     *            stream of the native format
     * @param annotationFormat
     *            list to which the canonized Annotations are added
     * @param canonicalFormat
     *            list to which the canonized Canonical Processes are added
     * @param request
     *            containing additional parameters
     * @return a PluginResult with information about this operation
     * @throws CanoniserException
     *             in case of an Exception during conversion
     */
    PluginResult canonise(InputStream nativeInput, List<AnnotationsType> annotationFormat, List<CanonicalProcessType> canonicalFormat,
            PluginRequest request) throws CanoniserException;

    /**
     * Converts the data in annotation format and canonical format to the native format.
     *
     * @param canonicalFormat
     *            Canonical Process Type to deCanonise
     * @param annotationFormat
     *            Annotations to deCanonise
     * @param nativeOutput
     *            stream of the native format
     * @param request
     *            containing additional parameters
     * @return a PluginResult with information about this operation
     * @throws CanoniserException
     *             in case of an Exception during conversion
     */
    PluginResult deCanonise(CanonicalProcessType canonicalFormat, AnnotationsType annotationFormat, OutputStream nativeOutput, PluginRequest request)
            throws CanoniserException;

    /**
     * Creates an initial (empty) document of the supported native process format.
     *
     * @param nativeOutput
     * @param processName
     * @param processVersion
     * @param processAuthor
     * @param processCreated
     * @param request
     *            containing additional parameters
     * @return a PluginResult with information about this operation
     */
    PluginResult createInitialNativeFormat(OutputStream nativeOutput, String processName, String processVersion, String processAuthor,
            Date processCreated, PluginRequest request);

    /**
     * Reads just the meta data from the native process. Please do not close the InputStream as it may be used later on.
     *
     * @param nativeInput
     *            stream of the native format
     * @param request
     *            containing additional parameters
     * @return a CanoniserMetadataResult
     */
    CanoniserMetadataResult readMetaData(InputStream nativeInput, PluginRequest request);

}
