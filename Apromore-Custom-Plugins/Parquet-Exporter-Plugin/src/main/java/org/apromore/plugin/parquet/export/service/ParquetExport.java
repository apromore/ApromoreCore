/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.plugin.parquet.export.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apromore.apmlog.xes.XESAttributeCodes;
import org.apromore.plugin.parquet.export.util.LoggerUtil;
import org.apromore.plugin.parquet.export.util.ZKMessageCtrl;
import org.apromore.plugin.parquet.export.core.data.LogExportItem;
import org.apromore.plugin.parquet.export.tables.CaseAttributeTable;
import org.apromore.plugin.parquet.export.tables.CaseVariantsTable;
import org.apromore.plugin.parquet.export.tables.CasesTable;
import org.apromore.plugin.parquet.export.tables.EventAttributeTable;
import org.apromore.plugin.parquet.export.tables.tablecontents.EventAttributeContentItem;
import org.apromore.plugin.parquet.export.util.FileNameGenerator;
import org.apromore.plugin.parquet.export.util.LabelUtil;
import org.apromore.plugin.parquet.export.types.TablesSetTypes;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.zkoss.zul.Filedownload;

public class ParquetExport extends AbstractParquetProducer {
    public static final int BUFFER_SIZE = 16384;
    public static final String FAILED_TO_WRITE_PARQUET_FILE = "Failed to write parquet file";

    // ================================================================================================
    // public methods
    // ================================================================================================

    public static void downloadEventLog(LogExportItem logItem, Charset charset) {
        Schema schema = EventLogParquet.getEventLogSchema(logItem, charset);
        List<GenericData.Record> data = EventLogParquet.getEventLogRecords(logItem, schema);
        downloadParquet("General", logItem, data, schema);
    }

    public static boolean exportParquetFile(LogExportItem logItem, String directoryPath) {
        Schema schema = EventLogParquet.getEventLogSchema(logItem, StandardCharsets.UTF_8);
        List<GenericData.Record> data = EventLogParquet.getEventLogRecords(logItem, schema);
        String filename = logItem.getSourceLogName() + ".parquet";
        deleteDir(new File(directoryPath));
        String outPath = directoryPath + File.separator + filename;

        try {
            writeToParquet(data, new Path(outPath), schema);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static boolean deleteDir(File dir) {
        File[] innerFiles = dir.listFiles();
        if (innerFiles != null) {
            for (File file : innerFiles) {
                deleteDir(file);
            }
        }
        return dir.delete();
    }

    public static void downloadCasesTable(LogExportItem logItem, Charset charset) {
        Schema schema = CasesTable.getSchema(charset);
        List<GenericData.Record> data = CasesTable.getRecords(logItem, schema);
        downloadParquet("Cases", logItem, data, schema);
    }

    public static void downloadCaseVariantsTable(LogExportItem logItem, Charset charset) {
        Schema schema = CaseVariantsTable.getSchema(charset);
        List<GenericData.Record> data = CaseVariantsTable.getRecords(logItem, schema);
        downloadParquet("Case variants", logItem, data, schema);
    }

    public static void downloadCaseAttributeTable(String key, LogExportItem logItem, Charset charset) {
        Schema schema = CaseAttributeTable.getSchema(charset);
        List<GenericData.Record> data = CaseAttributeTable.getRecords(key, logItem, schema);
        downloadParquet(key, logItem, data, schema);
    }

    public static void downloadEventAttributeTable(List<EventAttributeContentItem> items,
                                                   String key,
                                                   String setType,
                                                   LogExportItem logItem,
                                                   Charset charset) {

        Schema schema = EventAttributeTable.getSchema(charset);
        List<GenericData.Record> data = EventAttributeTable.getRecords(items, schema);
        String keyForDisplay = XESAttributeCodes.getDisplayLabelForSingle(key);

        String label = !setType.equals(TablesSetTypes.ALL) ?
                String.format("%s (%s)", keyForDisplay, setType)
                :
                keyForDisplay;

        downloadParquet(label, logItem, data, schema);
    }

    // ================================================================================================
    // private methods
    // ================================================================================================

    protected static ByteArrayOutputStream getZIPByteArrayOutputStream(UnifiedSet<String> fileNameSet,
                                                                       Charset charset) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(baos, charset);

        for (String filename : fileNameSet) {
            try {
                ZipEntry zipEntry = new ZipEntry(filename);
                zipOutputStream.putNextEntry(zipEntry);

                byte[] finalbytes = Files.readAllBytes(java.nio.file.Paths.get(filename));
                zipOutputStream.write(finalbytes);
                zipOutputStream.closeEntry();

                Files.delete(java.nio.file.Paths.get(filename));

            } catch (Exception e) {
                ZKMessageCtrl.showError(LabelUtil.getLabel("dash_parquet_data_create_fail"));
            }
        }

        try {
            zipOutputStream.close(); // perform after all entries are closed
            return baos;
        } catch (Exception e) {
            ZKMessageCtrl.showError("Failed to create Parquet data.");
            return null;
        }
    }

    protected static void downloadZIP(String archiveName,
                                      UnifiedSet<String> fileNameSet,
                                      Charset charset) {

        ByteArrayOutputStream baos = getZIPByteArrayOutputStream(fileNameSet, charset);

        if (baos == null) return;

        try {
            Filedownload.save(baos.toByteArray(), "application/x-compressed", archiveName + ".zip");
        } catch (Exception e) {
            ZKMessageCtrl.showError(LabelUtil.getLabel("dash_parquet_data_create_fail"));
        }
    }

    protected static void downloadParquet(String dataName,
                                          LogExportItem logItem,
                                          List<GenericData.Record> data,
                                          Schema schema) {
        String filename = String.format("%s.%s", FileNameGenerator.getName(logItem.getLabel(), dataName), "parquet");

        downloadParquet(filename, data, schema);
    }

    public static boolean writeParquetToOutputStream(String filename, List<GenericData.Record> data,
                                                     Schema schema, OutputStream outputStream) {
        Path outPath = new Path(filename);
        // delete if exist
        try {
            Files.delete(java.nio.file.Paths.get(filename));
        } catch (Exception ignored) {
            LoggerUtil.getLogger(ParquetExport.class).info("File not found", ignored);
        }

        try {
            writeToParquet(data, outPath, schema);
        } catch (IOException e) {
            return false;
        }

        // Replace with the
        try (InputStream fIn = Files.newInputStream(java.nio.file.Paths.get(filename));
             BufferedInputStream in = new BufferedInputStream(fIn)) {
            BufferedOutputStream out = new BufferedOutputStream(outputStream);
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.close();
            Files.delete(java.nio.file.Paths.get(filename));
        } catch (Exception e) {
            LoggerUtil.getLogger(ParquetExport.class).error(FAILED_TO_WRITE_PARQUET_FILE, e);
            return false;
        }
        return true;
    }

    public static void downloadParquet(String filename,
                                       List<GenericData.Record> data,
                                       Schema schema) {
        Path outPath = new Path(filename);
        // delete if exist
        try {
            Files.delete(java.nio.file.Paths.get(filename));
        } catch (Exception ignored) {}

        try {
            writeToParquet(data, outPath, schema);
            byte[] finalbytes = Files.readAllBytes(java.nio.file.Paths.get(filename));

            Filedownload.save(finalbytes, "application/parquet", filename);
            Files.delete(java.nio.file.Paths.get(filename));
        } catch (Exception e) {
            LoggerUtil.getLogger(ParquetExport.class).error(FAILED_TO_WRITE_PARQUET_FILE, e);
        }
    }

    public static java.nio.file.Path writeAndReturnParquetFilePath(String filename,
                                                                   List<GenericData.Record> data,
                                                                   Schema schema) {
        Path outPath = new Path(filename);
        try {
            Files.delete(java.nio.file.Paths.get(filename));
        } catch (Exception ignored) {
            //Do nothing
        }
        try {
            writeToParquet(data, outPath, schema);
            return java.nio.file.Paths.get(filename); //Temp File will be deleted after ZIP
        } catch (Exception e) {
            LoggerUtil.getLogger(ParquetExport.class).error(FAILED_TO_WRITE_PARQUET_FILE, e);
        }
        return null;
    }

    protected static void writeToParquet(List<GenericData.Record> recordsToWrite,
                                       Path writeFilePath,
                                       Schema schema) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        try (ParquetWriter<GenericData.Record> pqWriter = AvroParquetWriter
            .<GenericData.Record>builder(writeFilePath)
            .withSchema(schema)
            .withConf(conf)
            .withCompressionCodec(CompressionCodecName.SNAPPY)
            .build()) {

            for (GenericData.Record recordGen : recordsToWrite) {
                pqWriter.write(recordGen);
            }
        }
    }
}
