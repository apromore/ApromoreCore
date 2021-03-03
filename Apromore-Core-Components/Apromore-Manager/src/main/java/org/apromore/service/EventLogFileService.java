/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import java.io.InputStream;
import java.io.OutputStream;

public interface EventLogFileService {

    /**
     * Copy XES log from specified source name to target name
     * Only work for "xes.gz" extension
     *
     * @param sourceFileName source name
     * @param targetFileName target name
     * @throws Exception IOException
     */
    void copyFile(String sourceFileName, String targetFileName) throws Exception;

    /**
     * Copy XES log from specified source file InputStream to target file's OutputStream
     *
     * @param sourceFile source file's InputStream
     * @param targetFile target file's OutputStream
     * @throws Exception IOException
     */
    void copyFile(InputStream sourceFile, OutputStream targetFile) throws Exception;

    /**
     * Delete specified file if it exist
     *
     * @param fileFullName File name
     * @throws Exception IOException
     */
    void deleteFileIfExist(String fileFullName) throws Exception;
}
