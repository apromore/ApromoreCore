package org.apromore.service.csvimporter.services;

public class ParquetFactoryProvider {

    public static ConvertToParquetFactory getParquetFactory(String fileExtention) {
        if (fileExtention.equalsIgnoreCase("csv")) {

            return new CsvFactory();

        } else if (fileExtention.equalsIgnoreCase("parquet")) {
            return new ParquetFactory();
        } else {
            return null;
        }
    }

}
