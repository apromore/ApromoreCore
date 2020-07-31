package org.apromore.service.csvimporter.services;

import org.apromore.service.csvimporter.services.impl.ParquetExporter;
import org.apromore.service.csvimporter.services.impl.SampleLogGenerator;

public interface ConvertToParquetFactory {

    SampleLogGenerator createSampleLogGenerator();

    ParquetExporter createParquetExporter();

}
