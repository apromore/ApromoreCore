/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.service.logimporter.services.legacy;

import static org.apromore.service.logimporter.utilities.ParquetUtilities.getHeaderFromParquet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Inject;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.model.Log;
import org.apromore.service.logimporter.common.EventLogImporter;
import org.apromore.service.logimporter.constants.Constants;
import org.apromore.service.logimporter.io.ParquetLocalFileReader;
import org.apromore.service.logimporter.model.LogErrorReport;
import org.apromore.service.logimporter.model.LogErrorReportImpl;
import org.apromore.service.logimporter.model.LogEventModel;
import org.apromore.service.logimporter.model.LogEventModelExt;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.model.LogModel;
import org.apromore.service.logimporter.model.LogModelImpl;
import org.apromore.service.logimporter.model.ParquetLogMetaData;
import org.apromore.service.logimporter.services.LogProcessor;
import org.apromore.service.logimporter.services.LogProcessorImpl;
import org.apromore.service.logimporter.utilities.XEventComparator;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service("parquetLogImporter")
public class LogImporterParquetImpl implements LogImporter, Constants {

  @Inject
  EventLogImporter eventLogImporter;
  @Inject
  ConfigBean config;
  private List<LogErrorReport> logErrorReport;
  private LogProcessor logProcessor;
  private ParquetReader<Group> reader;

  @Override
  public LogModel importLog(InputStream in, LogMetaData logMetaData, String charset,
      boolean skipInvalidRow, String username, Integer folderId, String logName) throws Exception {
    try {
      ParquetLogMetaData parquetLogSample = (ParquetLogMetaData) logMetaData;
      parquetLogSample.validateSample();

      File tempFile = parquetLogSample.getParquetTempFile();
      if (tempFile == null)
        throw new Exception("Imported file cant be found!");

      // Read Parquet file
      ParquetLocalFileReader parquetLocalFileReader =
          new ParquetLocalFileReader(new Configuration(true), tempFile);
      MessageType tempFileSchema = parquetLocalFileReader.getSchema();
      reader = parquetLocalFileReader.getParquetReader();

      if (reader == null)
        return null;

      String[] header = CollectionUtils.isEmpty(logMetaData.getHeader()) ?
              getHeaderFromParquet(tempFileSchema).toArray(new String[0]) :
              logMetaData.getHeader().toArray(new String[0]);

      logProcessor = new LogProcessorImpl();
      logErrorReport = new ArrayList<>();
      int lineIndex = 0;
      int numOfValidEvents = 0;
      String[] line;
      TreeMap<String, XTrace> tracesHistory = new TreeMap<String, XTrace>(); // Keep track of traces
      boolean rowLimitExceeded = false;
      LogEventModelExt logEventModelExt;

      // XES
      XFactory xFactory = new XFactoryNaiveImpl();
      XConceptExtension concept = XConceptExtension.instance();
      XLifecycleExtension lifecycle = XLifecycleExtension.instance();
      XTimeExtension timestamp = XTimeExtension.instance();
      XOrganizationalExtension resourceXes = XOrganizationalExtension.instance();

      XLog xLog;
      xLog = xFactory.createLog();
      xLog.getExtensions().add(concept);
      xLog.getExtensions().add(lifecycle);
      xLog.getExtensions().add(timestamp);
      xLog.getExtensions().add(resourceXes);
      lifecycle.assignModel(xLog, XLifecycleExtension.VALUE_MODEL_STANDARD);

      Log log = null;

      Group g;
      while ((g = reader.read()) != null) {

        // row in excess of the allowed limit
        if (!isValidLineCount(lineIndex + 1)) {
          rowLimitExceeded = true;
          break;
        }

        try {
          line = readGroup(g, tempFileSchema);
        } catch (Exception e) {
          logErrorReport
              .add(new LogErrorReportImpl(lineIndex, 0, null, "Cant read line. " + e.getMessage()));
          continue;
        }

        // new row, new event.
        lineIndex++;

        // empty row
        if (line.length == 0
            || (line.length == 1 && (line[0].trim().equals("") || line[0].trim().equals("\n"))))
          continue;

        // Validate num of column
        if (header.length != line.length) {
          logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null,
              "Number of columns does not match the number of headers. Number of headers: ("
                  + header.length + "). Number of columns: (" + line.length + ")"));
          continue;
        }

        // Construct an event
        logEventModelExt = logProcessor.processLog(Arrays.asList(line), Arrays.asList(header),
            logMetaData, lineIndex, logErrorReport);

        // If row is invalid, continue to next row.
        if (!logEventModelExt.isValid()) {
          continue;
        }

        // Construct a Trace if it's not exists
        if (tracesHistory.isEmpty() || !tracesHistory.containsKey(logEventModelExt.getCaseID())) {
          XTrace xT = xFactory.createTrace();
          concept.assignName(xT, logEventModelExt.getCaseID());
          assignEventsToTrace(logEventModelExt, xT);
          assignMyCaseAttributes(logEventModelExt.getCaseAttributes(), xT);
          tracesHistory.put(logEventModelExt.getCaseID(), xT);
          numOfValidEvents++;

        } else {
          XTrace xT = tracesHistory.get(logEventModelExt.getCaseID());
          assignEventsToTrace(logEventModelExt, xT);
          assignMyCaseAttributes(logEventModelExt.getCaseAttributes(), xT);
          numOfValidEvents++;
        }
      }

      // Sort and feed xLog
      tracesHistory.forEach(
          /*
           * Java 8 (k, v) -> { v.sort(new XEventComparator()); xLog.add(v); }
           */
          new java.util.function.BiConsumer<String, XTrace>() {
            @Override
            public void accept(String k, XTrace v) {
              v.sort(new XEventComparator());
              xLog.add(v);
            }
          });

      // Import XES when there is no invalid row
      if (username != null
              && !username.isEmpty()
              && folderId != null
              && logName != null
              && !logName.isEmpty()
              && (logErrorReport.isEmpty() || skipInvalidRow)) {
        log = eventLogImporter.importXesLog(xLog, username, folderId, logName);
      }

      return new LogModelImpl(xLog, logErrorReport, rowLimitExceeded, numOfValidEvents, log);

    } finally {
      closeQuietly(in);
    }
  }

  private boolean isValidLineCount(int lineCount) {
    return config == null || config.getMaxEventCount() == null || lineCount <= config.getMaxEventCount();
  }

  private String[] readGroup(Group g, MessageType schema) {

    String[] line = new String[schema.getColumns().size()];
    for (int j = 0; j < schema.getFieldCount(); j++) {

      String valueToString = g.getValueToString(j, 0);
      line[j] = valueToString;
    }
    return line;
  }

  private void assignEventsToTrace(LogEventModel logEventModel, XTrace xTrace) {
    XEvent xEvent;

    if (logEventModel.getStartTimestamp() != null) {
      xEvent = createEvent(logEventModel, false);
      xTrace.add(xEvent);
    }
    xEvent = createEvent(logEventModel, true);
    xTrace.add(xEvent);
  }

  private void assignMyCaseAttributes(Map<String, String> caseAttributes, XTrace xTrace) {

    XAttributeMap xAttributeMap = xTrace.getAttributes();

    if (caseAttributes != null && !caseAttributes.isEmpty()) {

      XAttribute attribute;
      for (Map.Entry<String, String> entry : caseAttributes.entrySet()) {
        if (entry.getValue() != null && entry.getValue().trim().length() != 0
            && !xAttributeMap.containsKey(entry.getKey())) {
          attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
          xTrace.getAttributes().put(entry.getKey(), attribute);
        }
      }
    }
  }

  private XEvent createEvent(LogEventModel myEvent, Boolean isEndTimestamp) {

    XFactory xFactory = new XFactoryNaiveImpl();
    XEvent xEvent = xFactory.createEvent();

    XConceptExtension concept = XConceptExtension.instance();
    concept.assignName(xEvent, myEvent.getActivity());

    if (myEvent.getResource() != null) {
      XOrganizationalExtension resource = XOrganizationalExtension.instance();
      resource.assignResource(xEvent, myEvent.getResource());
    }

    XLifecycleExtension lifecycle = XLifecycleExtension.instance();
    XTimeExtension timestamp = XTimeExtension.instance();
    if (isEndTimestamp) {
      lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.COMPLETE);
      timestamp.assignTimestamp(xEvent, myEvent.getEndTimestamp());
    } else {
      lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.START);
      timestamp.assignTimestamp(xEvent, myEvent.getStartTimestamp());
    }


    XAttribute attribute;
    if (myEvent.getOtherTimestamps() != null) {
      Map<String, Timestamp> otherTimestamps = myEvent.getOtherTimestamps();
      for (Map.Entry<String, Timestamp> entry : otherTimestamps.entrySet()) {
        attribute = new XAttributeTimestampImpl(entry.getKey(), entry.getValue());
        xEvent.getAttributes().put(entry.getKey(), attribute);
      }
    }

    Map<String, String> eventAttributes = myEvent.getEventAttributes();
    for (Map.Entry<String, String> entry : eventAttributes.entrySet()) {
      if (entry.getValue() != null && entry.getValue().trim().length() != 0) {
        attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
        xEvent.getAttributes().put(entry.getKey(), attribute);
      }
    }
    return xEvent;
  }

  private void closeQuietly(InputStream in) throws IOException {
    if (in != null)
      in.close();
    if (this.reader != null)
      this.reader.close();
  }
}
