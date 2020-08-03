package org.apromore.service.csvimporter.services;

class ParquetFactory implements ConvertToParquetFactory{
    @Override
    public SampleLogGenerator createSampleLogGenerator() {
        return new ParquetSampleLogGenerator();
    }

    @Override
    public ParquetExporter createParquetExporter() {
        return new ParquetToParquetExporter();
    }
}
