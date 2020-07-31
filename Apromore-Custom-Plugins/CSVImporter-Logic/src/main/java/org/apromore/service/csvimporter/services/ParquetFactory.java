package org.apromore.service.csvimporter.services;

import org.apromore.service.csvimporter.services.impl.ParquetExporter;
import org.apromore.service.csvimporter.services.impl.ParquetSampleLogGenerator;
import org.apromore.service.csvimporter.services.impl.ParquetToParquetExporter;
import org.apromore.service.csvimporter.services.impl.SampleLogGenerator;

public class ParquetFactory implements ConvertToParquetFactory{
    @Override
    public SampleLogGenerator createSampleLogGenerator() {
        return new ParquetSampleLogGenerator();
    }

    @Override
    public ParquetExporter createParquetExporter() {
        return new ParquetToParquetExporter();
    }
}
