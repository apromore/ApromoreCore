/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
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
package org.apromore.service.csvimporter.services;

import com.opencsv.CSVReader;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.io.CSVFileReader;
import org.apromore.service.csvimporter.io.ParquetFileWriter;
import org.apromore.service.csvimporter.model.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.apromore.service.csvimporter.utilities.CSVUtilities.getMaxOccurringChar;
import static org.apromore.service.csvimporter.utilities.ParquetUtilities.createParquetSchema;

class CSVToParquetExporter implements ParquetExporter {

    private List<LogErrorReport> logErrorReport;
    private LogProcessorParquet logProcessorParquet;
    private Reader readerin;
    private BufferedReader brReader;
    private InputStream in2;
    private CSVReader reader;
    private ParquetFileWriter writer;


    @Override
    public LogModel generateParqeuetFile(InputStream in, LogSample sample, String charset, File outputParquet, boolean skipInvalidRow) throws Exception {

        try {
            sample.validateSample();
            //If file exist, delete it
            if (outputParquet.exists())
                outputParquet.delete();

            readerin = new InputStreamReader(in, Charset.forName(charset));
            brReader = new BufferedReader(readerin);
            String firstLine = brReader.readLine();
            char separator = getMaxOccurringChar(firstLine);
            String[] header = firstLine.split("\\s*" + separator + "\\s*");

            in2 = new ReaderInputStream(brReader, charset);
            reader = new CSVFileReader().newCSVReader(in2, charset, separator);
            if (reader == null)
                return null;

            MessageType parquetSchema = createParquetSchema(header);

            // Classpath manipulation so that ServiceLoader in parquet-osgi reads its own META-INF/services rather than the servlet context bundle's (i.e. the portal)
            Thread thread = Thread.currentThread();
            synchronized (thread) {
                ClassLoader originalContextClassLoader = thread.getContextClassLoader();
                try {
                    thread.setContextClassLoader(Path.class.getClassLoader());
                    writer = new ParquetFileWriter(new Path(outputParquet.toURI()), parquetSchema, true);
                } finally {
                    thread.setContextClassLoader(originalContextClassLoader);
                }
            }

            logProcessorParquet = new LogProcessorParquetImpl();
            logErrorReport = new ArrayList<>();
            int lineIndex = 1; // set to 1 since first line is the header
            int numOfValidEvents = 0;
            String[] line;
            boolean rowLimitExceeded = false;
            ParquetEventLogModel parquetEventLogModel;

            while ((line = reader.readNext()) != null && isValidLineCount(lineIndex - 1)) {

                // new row, new event.
                lineIndex++;

                //empty row
                if (line.length == 0 || (line.length == 1 && (line[0].trim().equals("") || line[0].trim().equals("\n"))))
                    continue;

                //Validate num of column
                if (header.length != line.length) {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null, "Number of columns does not match the number of headers. Number of headers: (" + header.length + "). Number of columns: (" + line.length + ")"));
                    continue;
                }

                //Construct an event
                parquetEventLogModel = logProcessorParquet.processLog(line, header, sample, lineIndex, logErrorReport);

                // If row is invalid, continue to next row.
                if (!parquetEventLogModel.isValid()) {
                    if (skipInvalidRow) {
                        continue;
                    } else {
                        return new LogModelXLogImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents);
                    }
                }

                writer.write(parquetEventLogModel.getEvent());
                numOfValidEvents++;
            }

            //If file empty, delete it
            if (numOfValidEvents == 0)
                outputParquet.delete();

            if (!isValidLineCount(lineIndex - 1))
                rowLimitExceeded = true;

            return new LogModelXLogImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents);

        } finally {
            closeQuietly(in);
        }
    }

    private boolean isValidLineCount(int lineCount) {
        return true;
    }

    private void closeQuietly(InputStream in) throws IOException {
        if (in != null)
            in.close();
        if (this.writer != null)
            this.writer.close();
        if (this.readerin != null)
            this.readerin.close();
        if (this.brReader != null)
            this.brReader.close();
        if (this.reader != null)
            this.reader.close();
        if (this.in2 != null)
            this.in2.close();
    }
}