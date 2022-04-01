/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.service.logimporter.services;

import static org.apromore.service.logimporter.utilities.ParquetUtilities.createParquetSchema;
import static org.apromore.service.logimporter.utilities.ParquetUtilities.getHeaderFromParquet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.logimporter.io.ParquetFileWriter;
import org.apromore.service.logimporter.io.ParquetLocalFileReader;
import org.apromore.service.logimporter.io.ParquetLocalFileWriter;
import org.apromore.service.logimporter.model.LogErrorReport;
import org.apromore.service.logimporter.model.LogErrorReportImpl;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.model.LogModel;
import org.apromore.service.logimporter.model.LogModelImpl;
import org.apromore.service.logimporter.model.ParquetEventLogModel;
import org.apromore.service.logimporter.model.ParquetLogMetaData;
import org.apromore.service.logimporter.utilities.FileUtils;

class ParquetImporterParquetImpl extends AbstractParquetImporter {

    private List<LogErrorReport> logErrorReport;
    private LogProcessorParquet logProcessorParquet;
    private ParquetReader<Group> reader;
    private ParquetFileWriter writer;

    ParquetImporterParquetImpl(final Integer maxEventCount) {
        super(maxEventCount);
    }

    @Override
    public LogModel importParquetFile(InputStream in, LogMetaData logMetaData, String charset, File outputParquet,
                                      boolean skipInvalidRow) throws Exception {

        try {
            ParquetLogMetaData parquetLogSample = (ParquetLogMetaData) logMetaData;
            parquetLogSample.validateSample();

            //If file exist, delete it
            if (outputParquet.exists()) {
                FileUtils.deleteFile(outputParquet);
            }

            File tempFile = parquetLogSample.getParquetTempFile();
            if (tempFile == null) {
                throw new Exception("Imported file cant be found!");
            }

            //Read Parquet file
            ParquetLocalFileReader parquetLocalFileReader =
                new ParquetLocalFileReader(new Configuration(true), tempFile);
            MessageType tempFileSchema = parquetLocalFileReader.getSchema();
            reader = parquetLocalFileReader.getParquetReader();

            if (reader == null) {
                return null;
            }

            String[] header = getHeaderFromParquet(tempFileSchema).toArray(new String[0]);
            MessageType sampleSchema = createParquetSchema(header);
            writer = new ParquetLocalFileWriter().getParquetWriter(outputParquet, sampleSchema);

            logProcessorParquet = new LogProcessorParquetImpl();
            logErrorReport = new ArrayList<>();
            int lineIndex = 0;
            int numOfValidEvents = 0;
            String[] line;
            boolean rowLimitExceeded = false;
            ParquetEventLogModel parquetEventLogModel;

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
                if (line.length == 0
                    || (line.length == 1 && (line[0].trim().equals("") || line[0].trim().equals("\n")))) {
                    continue;
                }

                //Validate num of column
                if (header.length != line.length) {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null,
                        "Number of columns does not match the number of headers. Number of headers: ("
                        + header.length + "). Number of columns: (" + line.length + ")"));
                    continue;
                }

                //Construct an event
                parquetEventLogModel =
                    logProcessorParquet.processLog(line, header, logMetaData, lineIndex, logErrorReport);

                // If row is invalid, continue to next row.
                if (!parquetEventLogModel.isValid()) {
                    if (skipInvalidRow) {
                        continue;
                    } else {
                        //Upon migrating to parquet, xlog need to be removed and LogModelImpl need to be renamed
                        return new LogModelImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents, null);
                    }
                }

                writer.write(parquetEventLogModel.getEvent());
                numOfValidEvents++;
            }

            //If file empty, delete it
            if (numOfValidEvents == 0) {
                FileUtils.deleteFile(outputParquet);
            }

            if (!isValidLineCount(lineIndex)) {
                rowLimitExceeded = true;
            }

            //Upon migrating to parquet, xlog need to be removed and LogModelImpl need to be renamed
            return new LogModelImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents, null);
        } finally {
            closeQuietly(in);
        }
    }

    private String[] readGroup(Group g, MessageType schema) {

        String[] line = new String[schema.getColumns().size()];
        for (int j = 0; j < schema.getFieldCount(); j++) {

            String valueToString = g.getValueToString(j, 0);
            line[j] = valueToString;
        }
        return line;
    }

    private void closeQuietly(InputStream in) throws IOException {
        if (in != null) {
            in.close();
        }
        if (this.writer != null) {
            this.writer.close();
        }
        if (this.reader != null) {
            this.reader.close();
        }
    }
}
