/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2019 The University of Tartu.
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

package org.apromore.service.csvexporter.impl;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.csv.FullLogCSV;
import org.apromore.apmlog.filter.PLog;
import org.apromore.service.csvexporter.CSVExporterLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

@Service
public class CSVExporterLogicImpl implements CSVExporterLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVExporterLogicImpl.class);

    @Override
    public Path exportCSV(APMLog apmLog, String encoding) {
        try {
            Path tempCSV = FullLogCSV.getCSVFile(new PLog(apmLog), StandardCharsets.UTF_8.toString());
            if (tempCSV == null || Files.notExists(tempCSV)) {
                LOGGER.debug("The temp CSV path \"{}\" doesn't exist!", tempCSV);
                return null;
            }

            Path tempGZIP = Files.createTempFile(null, ".csv.gz");
            tempCSV.toFile().deleteOnExit();
            tempGZIP.toFile().deleteOnExit();
            LOGGER.debug("Get temp CSV path \"{}\"", tempCSV);

            compressGzip(tempCSV, tempGZIP);
            Files.delete(tempCSV);
            return tempGZIP;

        } catch (IOException e) {
            LOGGER.error("Error occurred while creating temp CSV file: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Copy file (FileInputStream) to GZIPOutputStream
     *
     * @param source source path
     * @param target target path
     * @throws IOException IOException
     */
    private void compressGzip(Path source, Path target) throws IOException {

        try (GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(target.toFile()));
             FileInputStream fis = new FileInputStream(source.toFile())) {

            // copy file
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gos.write(buffer, 0, len);
            }
        }
    }
    
    @Override
    public Path generateCSV(APMLog apmLog) {
        Path tempCSV = FullLogCSV.getCSVFile(new PLog(apmLog), StandardCharsets.UTF_8.toString());
        if (tempCSV == null || Files.notExists(tempCSV)) {
            LOGGER.debug("The temp CSV path \"{}\" doesn't exist!", tempCSV);
            return null;
        }
        tempCSV.toFile().deleteOnExit();
        LOGGER.debug("Get temp CSV path \"{}\"", tempCSV);
        return tempCSV;
    }

}
