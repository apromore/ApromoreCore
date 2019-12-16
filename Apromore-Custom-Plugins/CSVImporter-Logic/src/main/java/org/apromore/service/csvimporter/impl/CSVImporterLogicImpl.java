/*
 * Copyright © 2009-2019 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.csvimporter.impl;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apromore.service.csvimporter.Constants;
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.InvalidCSVException;
import org.apromore.service.csvimporter.LogEventModel;
import org.apromore.service.csvimporter.LogModel;
import org.apromore.service.csvimporter.LogSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.io.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
//import org.deckfour.xes.*;
// TODO: Auto-generated Javadoc

/**
 * The Class CsvToXes.
 */
public class CSVImporterLogicImpl implements CSVImporterLogic, Constants {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVImporterLogicImpl.class);
    private static final double errorAcceptance = 0.2;
    private static final String parsedCorrectly = "Format parsed! ";
    private static final String couldnotParse = "Could not parse!";
    private static final String parsedClass = "text-success";
    private static final String failedClass = "text-danger";
    private static final Parse parse = new Parse();

    private Div popUPBox;
    private String popupID;
    private String textboxID;
    private String labelID;
    /**
     * Prepare xes model.
     * <p>
     * //	 * @param media the media
     *
     * @return the list
     */
    @SuppressWarnings("resource")

    private static Integer AttribWidth = 150;


    public LogSample sampleCSV(CSVReader reader, int sampleSize) throws InvalidCSVException, IOException {

        // Obtain the header
        List<String> header = new ArrayList<>();
        Collections.addAll(header, reader.readNext());
        if (header.isEmpty()) {
            throw new InvalidCSVException("Could not parse file!");
        }

        // Obtain the sample of lines
        List<List<String>> lines = new ArrayList<>();
        for (String[] s = reader.readNext(); s != null && lines.size() < sampleSize; s = reader.readNext()) {
            lines.add(Arrays.asList(s));
        }

        // Construct the sample (no mutation expected after this point, although this isn't enforced by the code))
        LogSample sample = new LogSample(header, lines);


        // TODO: derived property calculations from the sample below this point should be migrated to the LogSample class

        List<String> line = lines.get(0);

        if (line.size() != header.size()) {
            reader.close();
            throw new InvalidCSVException("Number of columns in the header does not match number of columns in the data");
        }

        ListModelList<String[]> result = new ListModelList<>();
        setOtherTimestamps(result, line, sample);

        toLists(line.size(), AttribWidth - 20 + "px", line, sample);
        
        return sample;
    }

    public LogModel prepareXesModel(CSVReader reader, LogSample sample) throws InvalidCSVException, IOException {
        int errorCount = 0;
        int lineCount = 0;
        int finishCount = 0;
        boolean errorCheck = false;

        ArrayList<String> invalidRows = new ArrayList<>();
        try {

            // read first line from CSV as header
            String[] header = reader.readNext();

            // If any of the mandatory fields are missing show alert message to the user and return
            StringBuilder headNOTDefined = checkFields(sample.getHeads());
            if (headNOTDefined.length() != 0) {
                throw new InvalidCSVException(headNOTDefined.toString());
            }


            // create model "LogEventModel" of the log data
            // We set mandatory fields and other fields are set with hash map
            List<LogEventModel> logData = new ArrayList<>();
            HashMap<String, Timestamp> otherTimestamps;
            HashMap<String, String> others;
            Timestamp startTimestamp = null;
            String resourceCol = null;
            String errorMessage = null;

            for (Iterator<String[]> it = reader.iterator(); finishCount < 50; ) {
                String[] line = it.next();
                boolean rowGTG = true;
                if(line == null) {
                    // if line is empty, more to next iteration, until 50 lines are empty
                    finishCount++;
                    continue;
                }
                lineCount++;
                if (line != null && line.length > 2) {
                    try {
                        otherTimestamps = new HashMap<String, Timestamp>();
                        others = new HashMap<String, String>();

                        for (int p = 0; p <= line.length - 1; p++) {
                            if (sample.getOtherTimeStampsPos().get(p) != null) {
                                otherTimestamps.put(header[p], parse.parseTimestamp(line[p], sample.getOtherTimeStampsPos().get(p)));
                            } else if (p != sample.getHeads().get(caseid) && p != sample.getHeads().get(activity) && p != sample.getHeads().get(timestamp) && p != sample.getHeads().get(tsStart) && p != sample.getHeads().get(resource) && (sample.getIgnoredPos().isEmpty() || !sample.getIgnoredPos().contains(p))) {
                                others.put(header[p], line[p]);

                                if (header.length != line.length) {
                                    invalidRows.add("Row: " + (lineCount) + ", Error: number of columns does not match number of headers. "
                                            + "Number of headers: " + header.length + ", Number of columns: " + line.length + ".\n");
                                    errorCount++;
                                    rowGTG = false;
                                    break;

                                }

                            }
                        }
                        Timestamp tStamp = parse.parseTimestamp(line[sample.getHeads().get(timestamp)], sample.getTimestampFormat());

                        if (sample.getHeads().get(tsStart) != -1) {
                            startTimestamp = parse.parseTimestamp(line[sample.getHeads().get(tsStart)], sample.getStartTsFormat());
                            if (startTimestamp == null) {
                                if (tStamp != null) {
                                    startTimestamp = tStamp;
                                    invalidRows.add("Row: " + (lineCount) + ", Warning: Start time stamp field is invalid. Copying end timestamp field into start timestamp");
                                } else {
                                    invalidRows.add("Row: " + (lineCount) + ", Error: Start time stamp field is invalid. ");
                                    errorCount++;
                                }
                            }
                        }

                        /* Notify if resource field is empty */
                        if (sample.getHeads().get(resource) != -1) {
                            resourceCol = line[sample.getHeads().get(resource)];
                        }
                        /* check if end stimestamp field is null */
                        if (tStamp == null) {
                            if (startTimestamp != null) {
                                tStamp = startTimestamp;
                                invalidRows.add("Row: " + (lineCount) + ", Warning: End time stamp field is invalid. Copying start timestamp field into end timestamp");
                            } else {
                                invalidRows.add("Row: " + (lineCount) + ", Error: End time stamp field is empty.");
                                errorCount++;
                                continue;
                            }
                        }
                        if (otherTimestamps != null) {
                            for (Map.Entry<String, Timestamp> entry : otherTimestamps.entrySet()) {
                                if (entry.getKey() != null) {
                                    if (entry.getValue() == null) {
                                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date = dateFormat.parse("01/01/1900");
                                        long time = date.getTime();
                                        Timestamp tempTime = new Timestamp(time);
                                        entry.setValue(tempTime);
//                                        Messagebox.show("It is: "  + entry.getValue().toString());
                                        invalidRows.add("Row: " + (lineCount) + ", Error: " + entry.getKey() +
                                                " field is invalid timestamp.");
//                                        errorCount++;
                                        errorCheck = true;
                                    }
                                }
                            }
                        }
                        if (rowGTG) {
                            logData.add(new LogEventModel(line[sample.getHeads().get(caseid)], line[sample.getHeads().get(activity)], tStamp, startTimestamp, otherTimestamps, resourceCol, others));
                        }
                    } catch (Exception e) {
                        errorMessage = ExceptionUtils.getStackTrace(e);
                        e.printStackTrace();
                        errorCount++;
                        if (line.length > 4) {
                            invalidRows.add("Row: " + (lineCount) + ", Error: Something went wrong. Content: " + line[0] + "," +
                                    line[1] + "," + line[2] + "," + line[3] + " ...");
                            errorCount++;
                        } else {
                            invalidRows.add("Row: " + (lineCount ) + ", Error: Content: " + " Empty, or too short for display.");
                            errorCount++;
                        }
                    }
                }
            }

            if (errorCount > (lineCount * errorAcceptance)) {
                String notificationMessage = "Detected more than " + errorAcceptance * 100 + "% of the log with errors. Please make sure input file is a valid CSV file. \n" +
                        "\n Invalid rows: \n";

                for (int i = 0; i < 5; i++) {
                    notificationMessage = notificationMessage + invalidRows.get(i) + "\n";
                }
                LOGGER.error(errorMessage);
                throw new InvalidCSVException(notificationMessage, invalidRows);

            }

            return new LogModel(sortTraces(logData), lineCount, errorCount, invalidRows, errorCheck);

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void automaticFormat(ListModelList<String[]> result, List<String> myHeader, LogSample sample) {
        try {
            String currentFormat = null;
            String startFormat = null;

            // do multiple line
            int IncValue = 5;
            // skipping 5 lines is too much for small logs, go through every line when its less than 1000 lines in total.
            if (result.size() < 1000) {
                IncValue = 1;
            }
            outerloop:
            // naming the outer loop so we can break out from this loop within nested loops.
            for (int i = 0; i < Math.min(1000, result.size()); i += IncValue) {
                String[] newLine = result.get(i);

                for (int j = 0; j < newLine.length; j++) {
                    // Going row by row
                    if(newLine.length != myHeader.size()) {
                        continue;
                    }
                    if (getPos(timestampValues, myHeader.get(j).toLowerCase())) {
                        // if its timestamp field
                        String format = parse.determineDateFormat((newLine[j])); // dd.MM.yyyy //MM.dd.yyyy
                        Timestamp validTS = Parse.parseTimestamp(newLine[j], format);
                        if (validTS != null) {
                            try {
                                if (currentFormat != null) {
                                    // determine which one is right which one is wrong
                                    // hint: use sets to store all the possible formats, then parse them again.

                                    if (currentFormat != format) {
                                        Timestamp validTS2 = Parse.parseTimestamp(result.get(i - IncValue)[j], currentFormat);

                                        if (validTS.getYear() > 0) {
                                            currentFormat = format;
                                            break outerloop;
                                        } else {
                                            continue;
                                        }
                                    }
                                } else {
                                    currentFormat = format;
                                }
                            } catch (Exception e) {
                                // automatic parse might be inaccurate.
                                Messagebox.show("Automatic parse of End timestamp might be inaccurate. Please validate end timestamp field.");
                                break;
                            }

                        }

                    }

                    if (getPos(StartTsValues, myHeader.get(j))) {
                        // if its timestamp field
                        String format = parse.determineDateFormat((newLine[j]));
                        Timestamp validTS = Parse.parseTimestamp(newLine[j], format);


                        if (validTS != null) {
                            try {
                                if (startFormat != null) {

                                    // determine which one is right which one is wrong
                                    // hint: use sets to store all the possible formats, then parse them again.
                                    if (startFormat != format) {
                                        validTS = Parse.parseTimestamp(result.get(i - 1)[j], format);
                                        if (validTS != null) {
//                                            Messagebox.show("Current: " + startFormat + ", Changing to: " + format);
                                            startFormat = format;
                                            break outerloop;
                                        }
                                    }
                                } else {
                                    startFormat = format;
                                }
                            } catch (Exception e) {
                                // automatic parse might be inaccurate.
                                Messagebox.show("Automatic parse of start timestamp might be in accurate. Please validate start timestamp field.");
                                break;
                            }
                        }
                    }
                }
            }
            sample.setTimestampFormat(currentFormat);
            sample.setStartTsFormat(startFormat);
        } catch (Exception e) {
            // automatic detection failed.
            e.printStackTrace();
        }
    }

    /**
     * Header pos.
     * <p>
     * //     * @param line read line from CSV
     *
     * @return the hash map: including mandatory field as key and position in the array as the value.
     */

    public void setOtherAll(Window window, List<String> line, LogSample sample) {
        int otherIndex = 6;

        for (int i = 0; i < line.size(); i++) {
            Listbox lb = (Listbox) window.getFellow(String.valueOf(i));
            if (lb.getSelectedIndex() == 7) {
                removeColPos(i, sample);
                closePopUp(i);
                lb.setSelectedIndex(otherIndex);
                sample.getHeads().put("Event Attribute", i);
            }
        }

    }

    public void setIgnoreAll(Window window, List<String> sampleLine, LogSample sample) {
        int otherIndex = 7;

        for (int i = 0; i < sampleLine.size(); i++) {
            Listbox lb = (Listbox) window.getFellow(String.valueOf(i));
            if (lb.getSelectedIndex() == 6) {
                removeColPos(i, sample);
                closePopUp(i);
                lb.setSelectedIndex(otherIndex);
                sample.getHeads().put("ignore", i);
                sample.getIgnoredPos().add(i);
            }
        }
    }

    public void setOtherTimestamps(ListModelList<String[]> result, List<String> sampleLine, LogSample sample) {
        if (result == null || result.size() == 0) {
            sample.getOtherTimeStampsPos().clear();
            Integer timeStampPos = sample.getHeads().get(timestamp);
            Integer StartTimeStampPos = sample.getHeads().get(tsStart);

            for (int i = 0; i <= sampleLine.size() - 1; i++) {
                String detectedFormat = parse.determineDateFormat(sampleLine.get(i));
                if ((i != timeStampPos) && (i != StartTimeStampPos) && (detectedFormat != null)) {
                    sample.getOtherTimeStampsPos().put(i, detectedFormat);
                }
            }
        }  // do multiple line

    }

    /**
     * Gets the pos.
     *
     * @param col  the col: array which has possible names for each of the mandatory fields.
     * @param elem the elem: one item of the CSV line array
     * @return the pos: boolean value confirming if the elem is the required element.
     */
    private static boolean getPos(String[] col, String elem) {
        if (col == timestampValues || col == StartTsValues) {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::equals);
        } else {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::contains);
        }
    }

    /**
     * Check fields.
     * <p>
     * Check if all mandatory fields are found in the file, otherwise, construct a message based on the missed fields.
     *
     * @param posMap the pos map
     * @return the string builder
     */
    private StringBuilder checkFields(Map<String, Integer> posMap) {
        String[] fieldsToCheck = {caseid, activity, timestamp};
        StringBuilder importMessage = new StringBuilder();

        for (int f = 0; f <= fieldsToCheck.length - 1; f++) {
            if (posMap.get(fieldsToCheck[f]) == -1) {
                String mess = "No " + fieldsToCheck[f] + " defined!";
                importMessage = (importMessage.length() == 0 ? importMessage.append(mess) : importMessage.append(", " + mess));
            }
        }

        return importMessage;
    }


    private static List<LogEventModel> sortTraces(List<LogEventModel> traces) {
        Comparator<String> nameOrder = new NameComparator();
        traces.sort((o1, o2) -> nameOrder.compare(o1.getCaseID(), o2.getCaseID()));
        return traces;
    }


    private void toLists(int cols, String boxwidth, List<String> sampleLine, LogSample sample) {

        LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
        String other = "Event Attribute";
        String ignore = "ignore";

        menuItems.put(caseid, "Case ID");
        menuItems.put(activity, "Activity");
        menuItems.put(timestamp, "End timestamp");
        menuItems.put(tsStart, "Start timestamp");
        menuItems.put(tsValue, "Other timestamp");
        menuItems.put(resource, "Resource");
        menuItems.put(other, "Event Attribute");
        menuItems.put(ignore, "Ignore column");

        // get index of "other" item and select it.
        int otherIndex = new ArrayList<String>(menuItems.keySet()).indexOf(other);

        for (int cl = 0; cl <= cols - 1; cl++) {

            Listbox box = new Listbox();
            box.setMold("select"); // set listBox to select mode
            box.setId(String.valueOf(cl)); // set id of list as column position.
            box.setWidth(boxwidth);

            for (Map.Entry<String, String> dl : menuItems.entrySet()) {
                Listitem item = new Listitem();
                item.setValue(dl.getKey());
                item.setLabel(dl.getValue());

                if ((box.getSelectedItem() == null) && (
                        (dl.getKey().equals(caseid) && (cl == sample.getHeads().get(caseid))) ||
                                (dl.getKey().equals(activity) && (cl == sample.getHeads().get(activity))) ||
                                (dl.getKey().equals(timestamp) && (cl == sample.getHeads().get(timestamp))) ||
                                (dl.getKey().equals(tsStart) && (cl == sample.getHeads().get(tsStart))) ||
                                (dl.getKey().equals(resource) && (cl == sample.getHeads().get(resource))) ||
                                (dl.getKey().equals(tsValue) && (sample.getOtherTimeStampsPos().get(cl) != null)) ||
                                (dl.getKey().equals(other)))
                        ) {
                    item.setSelected(true);
                }

                box.appendChild(item);
            }


            box.addEventListener("onSelect", (Event event) -> {
                // get selected index, and check if it is caseid, activity or time stamp
                String selected = box.getSelectedItem().getValue();
                int colPos = Integer.parseInt(box.getId());
                removeColPos(colPos, sample);
                closePopUp(colPos);

                if (selected.equals(caseid) || selected.equals(activity) || selected.equals(timestamp) || selected.equals(tsStart) || new String(selected).equals(resource)) {

                    int oldColPos = sample.getHeads().get(selected);
                    if (oldColPos != -1) {
                        Listbox oldBox = sample.getLists().get(oldColPos);
                        oldBox.setSelectedIndex(otherIndex);
                        removeColPos(oldColPos, sample);
                        closePopUp(oldColPos);
                    }

                    if (selected.equals(timestamp) || selected.equals(tsStart)) {
                        tryParsing(parse.determineDateFormat(sampleLine.get(colPos)), colPos, sampleLine, sample);
                    } else {
                        sample.getHeads().put(selected, colPos);
                    }

                } else if (selected.equals(ignore)) {
                    sample.getIgnoredPos().add(colPos);
                } else if (selected.equals(tsValue)) {
                    tryParsing(parse.determineDateFormat(sampleLine.get(colPos)), colPos, sampleLine, sample);
                }
            });

            sample.getLists().add(box);
        }
    }

    private void removeColPos(int colPos, LogSample sample) {

        if (sample.getOtherTimeStampsPos().get(colPos) != null) {
            sample.getOtherTimeStampsPos().remove(colPos);

        } else if (sample.getIgnoredPos().contains(colPos)) {
            sample.getIgnoredPos().remove(Integer.valueOf(colPos));
        } else {

            for (Map.Entry<String, Integer> entry : sample.getHeads().entrySet()) {
                if (entry.getValue() == colPos) {
                    sample.getHeads().put(entry.getKey(), -1);
                    break;
                }
            }
        }
    }


    private void closePopUp(int colPos) {
        Window myPopUp = (Window) popUPBox.getFellow(popupID + colPos);
        myPopUp.setStyle(myPopUp.getStyle().replace("visible", "hidden"));
    }


    public void tryParsing(String format, int colPos, List<String> sampleLine, LogSample sample) {

        if (format != null && parse.parseTimestamp(sampleLine.get(colPos), format) != null) {
            Listbox box = sample.getLists().get(colPos);
            String selected = box.getSelectedItem().getValue();
            if (new String(selected).equals(timestamp)) {
                sample.getHeads().put(selected, colPos);
                sample.setTimestampFormat(format);
            } else if (new String(selected).equals(tsStart)) {
                sample.getHeads().put(selected, colPos);
                sample.setStartTsFormat(format);
            } else if (new String(selected).equals(tsValue)) {
                sample.getOtherTimeStampsPos().put(colPos, format);
            }
            openPopUpbox(colPos, format, parsedCorrectly, parsedClass);
        } else {
            openPopUpbox(colPos, format, couldnotParse, failedClass);
        }
    }


    public void openPopUp(LogSample sample) {
        Integer timeStampPos = sample.getHeads().get(timestamp);
        if (timeStampPos != -1) openPopUpbox(sample.getHeads().get(timestamp), sample.getTimestampFormat(), parsedCorrectly, parsedClass);

        Integer startTimeStampPos = sample.getHeads().get(tsStart);
        if (startTimeStampPos != -1) openPopUpbox(sample.getHeads().get(tsStart), sample.getStartTsFormat(), parsedCorrectly, parsedClass);

        for (Map.Entry<Integer, String> entry : sample.getOtherTimeStampsPos().entrySet()) {
            openPopUpbox(entry.getKey(), entry.getValue(), parsedCorrectly, parsedClass);
        }
    }

    public void setTimestampBox(Integer colPos, String format, String message, Window window) {
        Textbox txt = (Textbox) window.getFellow("newtextbox" + colPos);
        txt.setValue(format);

        txt.setStyle(txt.getStyle().replace("hidden", "visible"));
        if (message == parsedCorrectly) {
            txt.setStyle("background:green;");
        } else {
            txt.setStyle("background:red;");
        }

    }


    private void openPopUpbox(Integer colPos, String format, String message, String lblClass) {
        Window myPopUp = (Window) popUPBox.getFellow(popupID + colPos);
        myPopUp.setStyle(myPopUp.getStyle().replace("hidden", "visible"));
        Label check_lbl = (Label) myPopUp.getFellow(labelID + colPos);

        Textbox txt = (Textbox) myPopUp.getFellow(textboxID + colPos);
        txt.setValue(format);
        if (message == parsedCorrectly) {
            check_lbl.setZclass("greenLabel");
            check_lbl.setValue(message);
        } else {
            check_lbl.setZclass("redLabel");
            check_lbl.setValue(message);
        }
        check_lbl.setClass(lblClass);
    }

    public void setPopUPBox(Div popUPBox) {
        this.popUPBox = popUPBox;
    }

    public void setPopupID(String popupID) {
        this.popupID = popupID;
    }

    public void setTextboxID(String textboxID) {
        this.textboxID = textboxID;
    }

    public void setLabelID(String labelID) {
        this.labelID = labelID;
    }
}
