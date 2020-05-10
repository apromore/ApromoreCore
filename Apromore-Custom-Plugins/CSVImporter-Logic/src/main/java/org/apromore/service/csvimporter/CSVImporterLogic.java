/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.service.csvimporter;

import com.opencsv.CSVReader;
import java.io.IOException;

/**
 * Service which converts event logs in CSV format to XES format.
 *
 * Conversion is performed by first calling {@link #sampleCSV} to sample the beginning of the
 * CSV document and make a best guess at the meanings of the headers.
 * The sample may be corrected by hand, and then passed to the {@link #prepareXesModel} method
 * to convert the entire document.
 * The converted XES model is obtained using {@link LogModel#getXLog}.
 */
public interface CSVImporterLogic {

    /**
     * Sample the beginning of a CSV document and try to automatically guess a header configuration.
     *
     * @param reader  a source of CSV data; this must be open to the beginning of the data so that the header may be read
     * @param sampleSize  how many lines of CSV data to sample from the <var>reader</var>; this may be truncated if the file is short
     * @return a sample object containing up to <var>sampleSize</var> lines of data and a guess at the meanings of the headers; this
     *     object can be visualized by a user, corrected, and then used to configure the {@link #prepareXesModel} method
     * @throws InvalidCSVException if the CSV is too malformed to work with
     * @throws IOException if <var>reader</var> cannot read the CSV data
     */
    LogSample sampleCSV(CSVReader reader, int sampleSize) throws InvalidCSVException, IOException;

    /**
     * Process an entire CSV document using a given header configuration.
     *
     * @param reader  a source of CSV data; this must be open to the beginning of the data so that the header may be read
     * @param sample  header configuration
     * @param errorAcceptance  maximum acceptable error fraction, e.g. 0.2 to set the error rate at 20%
     * @throws InvalidCSVException if more than <var>errorAcceptance</var> of the data has errors
     * @throws IOException if <var>reader</var> cannot read the CSV data
     */
    LogModel prepareXesModel(CSVReader reader, LogSample sample, double errorAcceptance) throws InvalidCSVException, IOException;

}
