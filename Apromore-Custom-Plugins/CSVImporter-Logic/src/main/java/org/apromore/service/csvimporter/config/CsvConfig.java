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
package org.apromore.service.csvimporter.config;

import java.util.Map;

import org.apromore.service.csvimporter.services.legacy.LogImporter;
import org.apromore.service.csvimporter.services.legacy.LogImporterCSVImpl;
import org.apromore.service.csvimporter.services.legacy.LogImporterParquetImpl;
import org.apromore.service.csvimporter.services.legacy.LogImporterXLSXImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvConfig {

	@Autowired
	LogImporterCSVImpl logImporterCSVImpl;

	@Autowired
	LogImporterParquetImpl logImporterParquetImpl;

	@Autowired
	LogImporterXLSXImpl logImporterXLSXImpl;

	@Bean
	@Qualifier("logImporterMap")
	public Map<String, LogImporter> logImporterMap() {
		return Map.of("csv", logImporterCSVImpl, "parquet", logImporterParquetImpl, "parq", logImporterParquetImpl,
				"xlsx", logImporterXLSXImpl);

	}

}
