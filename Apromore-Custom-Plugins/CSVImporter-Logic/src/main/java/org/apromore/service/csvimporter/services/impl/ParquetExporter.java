package org.apromore.service.csvimporter.services.impl;

import org.apromore.service.csvimporter.model.LogSample;

import java.io.File;

public interface ParquetExporter {

    void generateParqeuetFile(File importedFile, LogSample sample, File outputParquet) throws Exception;

}
