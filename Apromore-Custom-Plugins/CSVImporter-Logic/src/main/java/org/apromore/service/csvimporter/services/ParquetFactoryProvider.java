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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static org.apromore.service.csvimporter.constants.Constants.*;

@Service("parquetFactoryProvider")
public class ParquetFactoryProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParquetFactoryProvider.class);

    /**
     * @param fileExtension  the file extension, which will be treated case-insensitively
     * @return <code>null</code> if the <var>fileExtension</var> is not recognized
     */
    public ParquetImporterFactory getParquetFactory(String fileExtension) {

        LOGGER.info("File Format: " + fileExtension);
        if (fileExtension == null) {
            return null;
        }

        switch (fileExtension.toLowerCase()) {
        case CSV_FILE_EXTENSION:
            return new ParquetImporterFactoryCSVImpl();

        case PARQUET_FILE_EXTENSION:
        case PARQ_FILE_EXTENSION:
            return new ParquetImporterFactoryParquetImpl();

        case XLSX_FILE_EXTENSION:
            return new ParquetImporterFactoryXLSXImpl();

        default:
            return null;
        }
    }

}
