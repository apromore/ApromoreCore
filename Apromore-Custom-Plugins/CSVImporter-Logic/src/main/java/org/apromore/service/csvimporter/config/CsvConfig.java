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
