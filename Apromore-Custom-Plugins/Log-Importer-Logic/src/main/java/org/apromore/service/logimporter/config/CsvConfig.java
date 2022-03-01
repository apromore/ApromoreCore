/**
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
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not @see <a href="http://www.gnu.org/licenses/lgpl-3.0.html"></a>
 * #L%
 */

package org.apromore.service.logimporter.config;

import java.util.Map;
import org.apromore.service.logimporter.services.legacy.LogImporter;
import org.apromore.service.logimporter.services.legacy.LogImporterCSVImpl;
import org.apromore.service.logimporter.services.legacy.LogImporterParquetImpl;
import org.apromore.service.logimporter.services.legacy.LogImporterXLSXImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvConfig {

    @Autowired
    LogImporterCSVImpl logImporterCsvImpl;

    @Autowired
    LogImporterParquetImpl logImporterParquetImpl;

    @Autowired
    LogImporterXLSXImpl logImporterXlsxImpl;

    @Bean
    @Qualifier("logImporterMap")
    public Map<String, LogImporter> logImporterMap() {
        return Map.of("csv", logImporterCsvImpl, "parquet", logImporterParquetImpl, "parq", logImporterParquetImpl,
            "xlsx", logImporterXlsxImpl);

    }

}
