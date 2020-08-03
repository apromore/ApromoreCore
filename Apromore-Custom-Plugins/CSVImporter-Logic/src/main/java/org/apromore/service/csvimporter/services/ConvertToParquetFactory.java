package org.apromore.service.csvimporter.services;

public interface ConvertToParquetFactory {

    SampleLogGenerator createSampleLogGenerator();

    ParquetExporter createParquetExporter();

}
