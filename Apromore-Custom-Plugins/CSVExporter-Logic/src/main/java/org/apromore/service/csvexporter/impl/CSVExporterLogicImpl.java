/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2019 The University of Tartu.
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

package org.apromore.service.csvexporter.impl;

import org.apromore.service.csvexporter.CSVExporterLogic;
import org.deckfour.xes.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

@Service
public class CSVExporterLogicImpl implements CSVExporterLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVExporterLogicImpl.class);

    private static String CASEID = "Case ID";
    private static String ACTIVITY = "Activity";

    @Override
    public File exportCSV(XLog xLog) {
        return writeCSVFile(xLog, generateColumnNames(xLog));
    }

    /**
     * Generate List of column names based on specified XLog
     *
     * @param traces XLog (List of XTraces)
     * @return
     */
    private List<String> generateColumnNames(List<XTrace> traces) {

        List<String> columnNames;

        Set<String> listOfAttributes = new LinkedHashSet<String>();
        columnNames = new ArrayList<String>();
        columnNames.add(CASEID);
        columnNames.add(ACTIVITY);

        for (XTrace myTrace : traces) {
            listOfAttributes.addAll(myTrace.getAttributes().keySet());


            for (XEvent myEvent : myTrace) {

                listOfAttributes.addAll(myEvent.getAttributes().keySet());

            }
        }

        listOfAttributes.remove("concept:name");
        columnNames.addAll(new ArrayList<String>(listOfAttributes));

        return columnNames;
    }

    /**
     * Get AttributeValue from XAttribute based on different XAttribute types
     *
     * @param xAttribute XAttribute
     * @return
     */
    private String getAttributeValue(XAttribute xAttribute) {

        if (xAttribute instanceof XAttributeLiteral) {
            String theValue = ((XAttributeLiteral) xAttribute).getValue();
            if (theValue.contains(",")) {
                return "\"" + theValue + "\"";
            }
            return theValue;
        } else if (xAttribute instanceof XAttributeTimestamp) {

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return df.format(((XAttributeTimestamp) xAttribute).getValue());
        } else if (xAttribute instanceof XAttributeBoolean) {
            return String.valueOf(((XAttributeBoolean) xAttribute).getValue());
        } else if (xAttribute instanceof XAttributeDiscrete) {
            return String.valueOf(((XAttributeDiscrete) xAttribute).getValue());
        } else if (xAttribute instanceof XAttributeContinuous) {
            return String.valueOf(((XAttributeContinuous) xAttribute).getValue());
        }
        return "";

    }

    /**
     * Write Log to temp CSV file row by row, and then compress to GZ file.
     *
     * @param xLog XLog
     * @param columnNames List of column names
     * @return File
     */
    private File writeCSVFile(XLog xLog, List<String> columnNames) {

        try {

            // create a temporary file
            Path tempCSV = Files.createTempFile(null, ".csv");
            Path tempGZIP = Files.createTempFile(null, ".csv.gz");
            tempCSV.toFile().deleteOnExit();
            tempGZIP.toFile().deleteOnExit();
            System.out.println(tempCSV);
            LOGGER.debug("Create temp CSV path \"{}\"", tempCSV);


            StringBuilder sb = new StringBuilder();

            String prefix = "";
            for (String one : columnNames) {
                sb.append(prefix);
                prefix = ",";
                sb.append(one);
            }
            sb.append('\n');

            // write headers
            Files.write(tempCSV, sb.toString().getBytes(StandardCharsets.UTF_8));
            //empty StringBuilder
            sb.setLength(0);

            // write rest columns
            String columnValue;
            HashMap<String, String> attributeList;
            HashMap<String, String> eventAttributes;
            String attributeValue;

            for (XTrace myTrace : xLog) {

                attributeList = new HashMap<String, String>();

                for (Map.Entry<String, XAttribute> tAtt : myTrace.getAttributes().entrySet()) {

                    attributeValue = getAttributeValue(tAtt.getValue());
                    if ("concept:name".equals(tAtt.getKey())) {
                        attributeList.put(CASEID, attributeValue);
                    } else {
                        attributeList.put(tAtt.getKey(), attributeValue);
                    }
                }

                for (XEvent myEvent : myTrace) {
                    eventAttributes = new HashMap<String, String>(attributeList);

                    for (Map.Entry<String, XAttribute> eAtt : myEvent.getAttributes().entrySet()) {

                        attributeValue = getAttributeValue(eAtt.getValue());
                        if ("concept:name".equals(eAtt.getKey())) {
                            eventAttributes.put(ACTIVITY, attributeValue);
                        } else {
                            eventAttributes.put(eAtt.getKey(), attributeValue);
                        }
                    }

                    LogModel row = new LogModel(eventAttributes);

                    // start to write row
                    prefix = "";
                    for (String one : columnNames) {

                        sb.append(prefix);
                        prefix = ",";

                        columnValue = row.getAttributeList().get(one);
                        if (columnValue != null && columnValue.trim().length() != 0) {
                            sb.append(columnValue);
                        }
                    }
                    sb.append('\n');
                    Files.write(tempCSV, sb.toString().getBytes(StandardCharsets.UTF_8),
                            StandardOpenOption.APPEND);
                    //empty StringBuilder
                    sb.setLength(0);

                }
            }

            if (Files.notExists(tempCSV)) {
                LOGGER.debug("The temp CSV path \"{}\" doesn't exist!", tempCSV);
                return null;
            }

            compressGzip(tempCSV, tempGZIP);
            Files.delete(tempCSV);
            return tempGZIP.toFile();

        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error occurred while creating temp CSV file: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * copy file (FileInputStream) to GZIPOutputStream
     *
     * @param source source path
     * @param target target path
     * @throws IOException IOException
     */
    private void compressGzip(Path source, Path target) throws IOException {

        try (GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(target.toFile()));
             FileInputStream fis = new FileInputStream(source.toFile())) {

            // copy file
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gos.write(buffer, 0, len);
            }

        }

    }

}
