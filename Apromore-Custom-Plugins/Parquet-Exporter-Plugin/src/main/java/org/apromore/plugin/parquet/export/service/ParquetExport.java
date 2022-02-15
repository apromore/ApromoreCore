/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apromore.apmlog.xes.XESAttributeCodes;
import org.apromore.plugin.parquet.export.util.LoggerUtil;
import org.apromore.plugin.parquet.export.util.ZKMessageCtrl;
import org.apromore.plugin.parquet.export.core.data.LogItem;
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

    // ================================================================================================
    // public methods
    // ================================================================================================

    public static void downloadEventLog(LogItem logItem, Charset charset) {
        Schema schema = EventLogParquet.getEventLogSchema(logItem, charset);
        List<GenericData.Record> data = EventLogParquet.getEventLogRecords(logItem, schema);
        downloadParquet("General", logItem, data, schema);
    }

    public static boolean exportParquetFile(LogItem logItem, String directoryPath) {
        Schema schema = EventLogParquet.getEventLogSchema(logItem, StandardCharsets.UTF_8);
        List<GenericData.Record> data = EventLogParquet.getEventLogRecords(logItem, schema);
        String filename = logItem.getSourceLogName() + ".parquet";
        deleteDir(new File(directoryPath));
        String outPath = directoryPath + "/" + filename;

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

    public static void downloadCasesTable(LogItem logItem, Charset charset) {
        Schema schema = CasesTable.getSchema(charset);
        List<GenericData.Record> data = CasesTable.getRecords(logItem, schema);
        downloadParquet("Cases", logItem, data, schema);
    }

    public static void downloadCaseVariantsTable(LogItem logItem, Charset charset) {
        Schema schema = CaseVariantsTable.getSchema(charset);
        List<GenericData.Record> data = CaseVariantsTable.getRecords(logItem, schema);
        downloadParquet("Case variants", logItem, data, schema);
    }

    public static void downloadCaseAttributeTable(String key, LogItem logItem, Charset charset) {
        Schema schema = CaseAttributeTable.getSchema(charset);
        List<GenericData.Record> data = CaseAttributeTable.getRecords(key, logItem, schema);
        downloadParquet(key, logItem, data, schema);
    }

    public static void downloadEventAttributeTable(List<EventAttributeContentItem> items,
                                                   String key,
                                                   String setType,
                                                   LogItem logItem,
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
                                          LogItem logItem,
                                          List<GenericData.Record> data,
                                          Schema schema) {
        String filename = String.format("%s.%s", FileNameGenerator.getName(logItem.getLabel(), dataName), "parquet");

        downloadParquet(filename, data, schema);
    }

    public static void downloadParquet(String filename,
                                       List<GenericData.Record> data,
                                       Schema schema) {
        Path OUT_PATH = new Path(filename);

        // delete if exist
        try {
            Files.delete(java.nio.file.Paths.get(filename));
        } catch (Exception ignored) {}

        try {
            writeToParquet(data, OUT_PATH, schema);
            byte[] finalbytes = Files.readAllBytes(java.nio.file.Paths.get(filename));
            Filedownload.save(finalbytes, "application/parquet", filename);
            Files.delete(java.nio.file.Paths.get(filename));
        } catch (Exception e) {
            LoggerUtil.getLogger(ParquetExport.class).error("Failed to write parquet file", e);
        }
    }

    protected static void writeToParquet(List<GenericData.Record> recordsToWrite,
                                       Path writeFilePath,
                                       Schema schema) throws IOException {

        try (ParquetWriter<GenericData.Record> pqWriter = AvroParquetWriter
                .<GenericData.Record>builder(writeFilePath)
                .withSchema(schema)
                .withConf(new org.apache.hadoop.conf.Configuration())
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .build()) {

            for (GenericData.Record record : recordsToWrite) {
                pqWriter.write(record);
            }
        }
    }
}
