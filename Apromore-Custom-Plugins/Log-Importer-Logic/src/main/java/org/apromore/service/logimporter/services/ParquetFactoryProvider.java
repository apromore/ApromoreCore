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

import static org.apromore.service.logimporter.constants.Constants.CSV_FILE_EXTENSION;
import static org.apromore.service.logimporter.constants.Constants.PARQUET_FILE_EXTENSION;
import static org.apromore.service.logimporter.constants.Constants.PARQ_FILE_EXTENSION;
import static org.apromore.service.logimporter.constants.Constants.XLSX_FILE_EXTENSION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("parquetFactoryProvider")
public class ParquetFactoryProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParquetFactoryProvider.class);

    @Value("${maxEventCount:#{null}}")
    private Integer maxEventCount;

    /**
     * Get the parquet importer factory provided the file extension.
     *
     * @param fileExtension the file extension, which will be treated case-insensitively
     * @return <code>null</code> if the <var>fileExtension</var> is not recognized
     */
    public ParquetImporterFactory getParquetFactory(String fileExtension) {

        LOGGER.info("File Format: " + fileExtension);
        if (fileExtension == null) {
            return null;
        }

        switch (fileExtension.toLowerCase()) {
            case CSV_FILE_EXTENSION:
                return new ParquetImporterFactoryCSVImpl(maxEventCount);

            case PARQUET_FILE_EXTENSION:
            case PARQ_FILE_EXTENSION:
                return new ParquetImporterFactoryParquetImpl(maxEventCount);

            case XLSX_FILE_EXTENSION:
                return new ParquetImporterFactoryXLSXImpl(maxEventCount);

            default:
                return null;
        }
    }
}
