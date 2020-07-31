package org.apromore.service.csvimporter.services.impl;

import org.apromore.service.csvimporter.model.LogSample;

import java.io.File;

public class CSVToParqeutExporter implements ParquetExporter {
    @Override
    public void generateParqeuetFile(File importedFile, LogSample sample, File outputParquet) throws Exception {

    }
}
