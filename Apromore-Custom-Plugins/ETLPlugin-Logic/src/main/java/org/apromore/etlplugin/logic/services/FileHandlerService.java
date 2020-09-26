/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.etlplugin.logic.services;

import org.apromore.etlplugin.logic.services.impl.IllegalFileTypeException;
import org.zkoss.util.media.Media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Interface for creating file services.
 */
public interface FileHandlerService {
    /**
     * Writes the input files to an output stream.
     *
     * @param medias the input files.
     * @return return the message to show on client side.
     * @throws IllegalFileTypeException if the file type is unsupported
     */
    String writeFiles(Media[] medias)
                    throws IOException, IllegalFileTypeException;

    /**
     * Output a file.
     *
     * @return returns a file.
     */
    File outputFile();

    /**
     * Outputs all files.
     *
     * @return returns a list of files.
     */
    ArrayList<File> outputFiles();

    /**
     * Find the newest parquet file in directory.
     *
     * @param directoryFilePath path to directory
     * @return returns a file
     */
    File getLastParquet(String directoryFilePath);
}
