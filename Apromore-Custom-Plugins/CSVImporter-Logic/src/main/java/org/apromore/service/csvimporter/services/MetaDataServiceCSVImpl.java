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
package org.apromore.service.csvimporter.services;

import com.opencsv.CSVReader;
import org.apache.commons.io.input.ReaderInputStream;
import org.apromore.commons.utils.Delimiter;
import org.apromore.service.csvimporter.io.CSVFileReader;
import org.apromore.service.csvimporter.model.LogMetaData;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class MetaDataServiceCSVImpl implements MetaDataService {

	private static final int MAX_ROW_COUNT = 2;

    private Reader reader;
    private BufferedReader brReader;
    private InputStream in2;
    private CSVReader csvReader;
    private List<String> headers = new ArrayList<String>();
    private String separator = "";

    @Override
    public void validateLog(InputStream in, String charset) throws Exception {

	try {
	    reader = new InputStreamReader(in, Charset.forName(charset));
	    brReader = new BufferedReader(reader);

		List<String> sampleRows = getSampleRows(brReader);

	    String firstLine = sampleRows.get(0);
	    if (firstLine == null || firstLine.isEmpty()) {
		throw new Exception("header must have non-empty value!");
	    }

		String separator = Delimiter.findDelimiter(sampleRows);
	    this.separator = separator;
	    this.headers = Arrays.asList(firstLine.split("\\s*" + separator + "\\s*"));

	} catch (IOException e) {
	    throw new Exception("Unable to import file", e);
	} finally {
	    closeQuietly(in);
	}
    }

    @Override
    public LogMetaData extractMetadata(InputStream in, String charset) throws Exception {

	if (!headers.isEmpty()) {
	    return new LogMetaData(this.headers);
	}
	try {
	    reader = new InputStreamReader(in, Charset.forName(charset));
	    brReader = new BufferedReader(reader);

		List<String> sampleRows = getSampleRows(brReader);

	    String firstLine = sampleRows.get(0);
	    firstLine = firstLine.replaceAll("\"", "");

	    String separator = Delimiter.findDelimiter(sampleRows);

	    List<String> header = Arrays.asList(firstLine.split("\\s*" + separator + "\\s*"));

	    return new LogMetaData(header);

	} finally {
	    closeQuietly(in);
	}
    }

    @Override
    public List<List<String>> generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {

	try {
	    reader = new InputStreamReader(in, Charset.forName(charset));
	    brReader = new BufferedReader(reader);

		List<String> sampleRows = getSampleRows(brReader);

		String firstLine = sampleRows.get(0);
	    firstLine = firstLine.replaceAll("\"", "");

	    String separator = !Objects.equals(this.separator, "") ? this.separator : Delimiter.findDelimiter(sampleRows);

	    List<String> header = !headers.isEmpty() ? headers
		    : Arrays.asList(firstLine.split("\\s*" + separator + "\\s*"));

	    in2 = new ReaderInputStream(brReader, charset);
	    csvReader = new CSVFileReader().newCSVReader(in2, charset, !Objects.equals(separator, "") ?
				separator.charAt(0) : '\u0000');

	    if (csvReader == null)
		return null;

	    List<List<String>> lines = new ArrayList<>();
	    String[] myLine;

	    while ((myLine = csvReader.readNext()) != null && lines.size() < sampleSize) {
		if (myLine.length != header.size())
		    continue;
		lines.add(Arrays.asList(myLine));
	    }

	    return lines;

	} finally {
	    closeQuietly(in);
	}
    }

    private List<String> getSampleRows(BufferedReader brReader) throws IOException {
		int rowCount = MAX_ROW_COUNT;
		List<String> rows = new ArrayList<>();
		String rowLine = brReader.readLine();
		while (--rowCount > 0 && rowLine != null) {
			rows.add(rowLine);
			rowLine = brReader.readLine();
		}
		return rows;
	}

    private void closeQuietly(InputStream in) throws IOException {
	if (in != null)
	    in.close();
	if (this.csvReader != null)
	    this.csvReader.close();
	if (this.brReader != null)
	    this.brReader.close();
	if (this.reader != null)
	    this.reader.close();
	if (this.in2 != null)
	    this.in2.close();
    }
}
