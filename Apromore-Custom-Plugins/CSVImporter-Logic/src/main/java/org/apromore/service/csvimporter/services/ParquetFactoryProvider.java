package org.apromore.service.csvimporter.services;

import static org.apromore.service.csvimporter.constants.Constants.CSV_FILE_EXTENTION;
import static org.apromore.service.csvimporter.constants.Constants.PARQUET_FILE_EXTENTION;

public class ParquetFactoryProvider {

    public ConvertToParquetFactory getParquetFactory(String fileExtention) {
        if (fileExtention.equalsIgnoreCase(CSV_FILE_EXTENTION)) {

            return new CsvFactory();

        } else if (fileExtention.equalsIgnoreCase(PARQUET_FILE_EXTENTION)) {
            return new ParquetFactory();
        } else {
            return null;
        }
    }

}
