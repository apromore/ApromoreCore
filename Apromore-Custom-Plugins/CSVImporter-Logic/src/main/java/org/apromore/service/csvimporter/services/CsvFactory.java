package org.apromore.service.csvimporter.services;

class CsvFactory implements ConvertToParquetFactory{
    @Override
    public SampleLogGenerator createSampleLogGenerator() {
        return new CSVSampleLogGenerator();
    }

    @Override
    public ParquetExporter createParquetExporter() {
        return new CSVToParqeutExporter();
    }
}
