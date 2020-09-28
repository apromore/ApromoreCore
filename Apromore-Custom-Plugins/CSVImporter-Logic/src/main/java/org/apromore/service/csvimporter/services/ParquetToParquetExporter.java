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
package org.apromore.service.csvimporter.services;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.io.ParquetFileWriter;
import org.apromore.service.csvimporter.io.ParquetLocalFileReader;
import org.apromore.service.csvimporter.io.ParquetLocalFileWriter;
import org.apromore.service.csvimporter.model.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apromore.service.csvimporter.utilities.ParquetUtilities.createParquetSchema;
import static org.apromore.service.csvimporter.utilities.ParquetUtilities.getHeaderFromParquet;

class ParquetToParquetExporter implements ParquetExporter {

    private List<LogErrorReport> logErrorReport;
    private LogProcessor logProcessor;

    @Override
    public LogModel generateParqeuetFile(InputStream in, LogSample sample, String charset, File outputParquet, boolean skipInvalidRow) throws Exception {

        ParquetLogSampleImpl parquetLogSample = (ParquetLogSampleImpl) sample;
        parquetLogSample.validateSample();

        //If file exist, delete it
        if (outputParquet.exists())
            outputParquet.delete();

        File tempFile = parquetLogSample.getParquetTempFile();
        if (tempFile == null)
            throw new Exception("Imported file cant be found!");

        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempFile);
        MessageType tempFileSchema = parquetLocalFileReader.getSchema();
        ParquetReader<Group> reader = parquetLocalFileReader.getParquetReader();

        if (reader == null)
            return null;

        String[] header = getHeaderFromParquet(tempFileSchema).toArray(new String[0]);
        MessageType sampleSchema = createParquetSchema(header, sample);
        ParquetFileWriter writer = new ParquetLocalFileWriter().getParquetWriter(outputParquet, sampleSchema);

        logProcessor = new LogProcessorImpl();
        logErrorReport = new ArrayList<>();
        int lineIndex = 0;
        int numOfValidEvents = 0;
        String[] line;
        boolean rowLimitExceeded = false;
        LogEventModelExt logEventModelExt;

        Group g;
        while ((g = reader.read()) != null && isValidLineCount(lineIndex)) {

            try {
                line = readGroup(g, tempFileSchema);
            } catch (Exception e) {
                logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null, "Cant read line. " + e.getMessage()));
                continue;
            }

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
            logEventModelExt = logProcessor.processLog(Arrays.asList(line), Arrays.asList(header), sample, lineIndex, logErrorReport);

            // If row is invalid, continue to next row.
            if (!logEventModelExt.isValid()) {
                if (skipInvalidRow) {
                    continue;
                } else {
                    //Upon migrating to parquet, xlog need to be removed and LogModelXLogImpl need to be renamed
                    return new LogModelXLogImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents);
                }
            }

            writer.write(logEventModelExt);
            numOfValidEvents++;
        }
        writer.close();

        if (!isValidLineCount(lineIndex))
            rowLimitExceeded = true;

        //Upon migrating to parquet, xlog need to be removed and LogModelXLogImpl need to be renamed
        return new LogModelXLogImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents);
    }

    private boolean isValidLineCount(int lineCount) {
        return true;
    }

    private String[] readGroup(Group g, MessageType schema) {

        String[] line = new String[schema.getColumns().size()];
        for (int j = 0; j < schema.getFieldCount(); j++) {

            String valueToString = g.getValueToString(j, 0);
            line[j] = valueToString;
        }
        return line;
    }
}