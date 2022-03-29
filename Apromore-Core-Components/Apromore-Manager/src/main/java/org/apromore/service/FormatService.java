/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;

/**
 * Interface for the Format Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface FormatService {

    /**
     * Finds all the native Types (formats) in the system that are supported.
     *
     * @return a List of formats in the system.
     */
    List<NativeType> findAllFormats();

    /**
     * Find a particular Native Type.
     *
     * @param nativeType the type to find
     * @return the nativeType record
     */
    NativeType findNativeType(String nativeType);
    
    /**
     * Store the Native XML in the DB.
     *
     * @param procName   the name of the process being imported.
     * @param processId  the id of the process we are importing
     * @param created    the time created
     * @param lastUpdate the time last updated
     * @param user       the user doing the updates
     * @param nativeType the native Type
     * @param annotationVersion the Annotations identifier name
     * @param cp the canonical process format, cpf and anf.
     * @throws IOException is resetting the input streams fails.
     */
    Native storeNative(String procName, String created, String lastUpdate, User user,
        NativeType nativeType, String annotationVersion, InputStream original) throws IOException;
    
    void updateNative(Native nativeData);
}
