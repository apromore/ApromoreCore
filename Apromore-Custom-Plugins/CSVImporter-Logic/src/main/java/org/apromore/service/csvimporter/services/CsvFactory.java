package org.apromore.service.csvimporter.services;

import org.apromore.service.csvimporter.services.impl.CSVSampleLogGenerator;
import org.apromore.service.csvimporter.services.impl.CSVToParqeutExporter;
import org.apromore.service.csvimporter.services.impl.ParquetExporter;
import org.apromore.service.csvimporter.services.impl.SampleLogGenerator;

public class CsvFactory implements ConvertToParquetFactory{
    @Override
    public SampleLogGenerator createSampleLogGenerator() {
        return new CSVSampleLogGenerator();
    }

    @Override
    public ParquetExporter createParquetExporter() {
        return new CSVToParqeutExporter();
    }
}
