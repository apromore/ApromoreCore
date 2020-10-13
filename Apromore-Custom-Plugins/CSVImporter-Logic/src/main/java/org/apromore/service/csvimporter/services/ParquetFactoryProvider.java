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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apromore.service.csvimporter.constants.Constants.*;

public class ParquetFactoryProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParquetFactoryProvider.class);

    public ConvertToParquetFactory getParquetFactory(String fileExtension) {

        LOGGER.info("File Format: " + fileExtension);
        if (fileExtension.equalsIgnoreCase(CSV_FILE_EXTENSION)) {
            return new CsvFactory();
        } else if (fileExtension.equalsIgnoreCase(PARQUET_FILE_EXTENSION)) {
            return new ParquetFactory();
        } else if (fileExtension.equalsIgnoreCase(XLSX_FILE_EXTENSION)) {
            return new XLSFactory();
        } else {
            return null;
        }
    }

}
