package org.apromore.service.csvimporter.services;

import org.apromore.service.csvimporter.model.LogSample;

import java.io.File;
import java.io.InputStream;

public interface ParquetExporter {

    void generateParqeuetFile(InputStream in, LogSample sample, File outputParquet) throws Exception;

}
