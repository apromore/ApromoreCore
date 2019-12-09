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
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.InvalidCSVException;
import org.apromore.service.csvimporter.LogEventModel;
import org.apromore.service.csvimporter.LogModel;
import org.apromore.service.csvimporter.LogSample;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.out.XesXmlSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.*;

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
public class CSVImporterLogicImpl implements CSVImporterLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVImporterLogicImpl.class);
    /**
     * The Constant caseid.
     */
    private static final String caseid = "caseid";
    /**
     * The Constant activity.
     */
    private static final String activity = "activity";
    /**
     * The Constant timestamp.
     */
    private static final String timestamp = "timestamp";
    private static final double errorAcceptance = 0.2;
    private static final String tsStart = "startTimestamp";
    private static final String resource = "resource";
    private static final String tsValue = "otherTimestamp";
    private static final String parsedCorrectly = "Format parsed! ";
    private static final String couldnotParse = "Could not parse!";
    private static final String parsedClass = "text-success";
    private static final String failedClass = "text-danger";
    private static Parse parse = new Parse();
    /**
     * The case id values.
     */
    private String[] caseIdValues = {"case", "case id", "case-id", "service id", "event id", "caseid", "serviceid"};
    /**
     * The activity values.
     */
    private String[] activityValues = {"activity", "activity id", "activity-id", "operation", "event"};
    /**
     * The timestamp Values.
     */
    private String[] timestampValues = {"timestamp", "end date", "complete timestamp", "time:timestamp", "completion time"};
    private String[] StartTsValues = {"start date", "start timestamp", "start time"};
    private String[] resourceValues = {"resource", "agent", "employee", "group"};
    private List<Listbox> lists;
    private HashMap<String, Integer> heads;
    private List<Integer> ignoredPos;
    private HashMap<Integer, String> otherTimeStampsPos;
    private List<String> line;
    private String timestampFormat;
    private String startTsFormat;
    private Div popUPBox;
    private String popupID;
    private String textboxID;
    private String labelID;
    private boolean errorCheck = false;
    /**
     * Prepare xes model.
     * <p>
     * //	 * @param media the media
     *
     * @return the list
     */
    @SuppressWarnings("resource")

    public Boolean getErrorCheck() {
        return errorCheck;
    }

    private static Integer AttribWidth = 150;

    public LogSample sampleCSV(CSVReader reader) throws InvalidCSVException, IOException {
        List<String> header = new ArrayList<String>();
        List<String> line = new ArrayList<String>();
        ListModelList<String[]> result = new ListModelList<>();

        Collections.addAll(header, reader.readNext());

        line = Arrays.asList(reader.readNext());
        if (line.size() < 2 && line != null) {
            while (line.size() < 2 && line != null) {
                line = Arrays.asList(reader.readNext());
            }
        }

        if (line != null && header != null && !line.isEmpty() && !header.isEmpty() && line.size() > 1) {
            setLine(line);
            setHeads(header);
            setOtherTimestamps(result);
        } else {
            throw new InvalidCSVException("Could not parse file!");
        }

        if (line.size() != header.size()) {
            reader.close();
            throw new InvalidCSVException("Number of columns in the header does not match number of columns in the data");
        } else {
            setLists(line.size(), getHeads(), AttribWidth - 20 + "px");
        }

        return new LogSample();
    }

    public LogModel prepareXesModel(CSVReader reader) throws InvalidCSVException {
        int errorCount = 0;
        int lineCount = 0;
        int finishCount = 0;
        errorCheck = false;

        ArrayList<String> invalidRows = new ArrayList<String>();
        try {

            // read first line from CSV as header
            String[] header = reader.readNext();

            // If any of the mandatory fields are missing show alert message to the user and return
            StringBuilder headNOTDefined = checkFields(heads);
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
                            if (otherTimeStampsPos.get(p) != null) {
                                otherTimestamps.put(header[p], parse.parseTimestamp(line[p], otherTimeStampsPos.get(p)));
                            } else if (p != heads.get(caseid) && p != heads.get(activity) && p != heads.get(timestamp) && p != heads.get(tsStart) && p != heads.get(resource) && (ignoredPos.isEmpty() || !ignoredPos.contains(p))) {
                                others.put(header[p], line[p]);

                                if (header.length != line.length) {
                                    invalidRows.add("Row: " + (lineCount) + ", Error: number of columns does not match number of headers. "
                                            + "Number of headers: " + header.length + ", Number of columns: " + line.length + ".\n");
                                    errorCount++;
                                    continue;

                                }

                            }
                        }
                        Timestamp tStamp = parse.parseTimestamp(line[heads.get(timestamp)], timestampFormat);

                        if (heads.get(tsStart) != -1) {
                            startTimestamp = parse.parseTimestamp(line[heads.get(tsStart)], startTsFormat);
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
                        if (heads.get(resource) != -1) {
                            resourceCol = line[heads.get(resource)];
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
                                        continue;
                                    }
                                }
                            }
                        }
                            logData.add(new LogEventModel(line[heads.get(caseid)], line[heads.get(activity)], tStamp, startTimestamp, otherTimestamps, resourceCol, others));

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

            } else {
                if (errorCount > 0) {
                    String notificationMessage;
                    notificationMessage = "Imported: " + lineCount + " row(s), with " + errorCount + " invalid row(s) being amended.  \n\n" +
                            "Invalid rows: \n";

                    for (int i = 0; i < Math.min(invalidRows.size(), 5); i++) {
                        notificationMessage = notificationMessage + invalidRows.get(i) + "\n";
                    }

                    if (invalidRows.size() > 5) {
                        notificationMessage = notificationMessage + "\n ...";
                    }
//                    Messagebox.show(notificationMessage
//                            , "Invalid CSV File",
//                            new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
//                            new String[]{"Download Error Report", "Cancel"}, Messagebox.ERROR, null, new org.zkoss.zk.ui.event.EventListener() {
//                                public void onEvent(Event evt) throws Exception {
//                                    if (evt.getName().equals("onOK")) {
//                                        File tempFile = File.createTempFile("Error_Report", ".txt");
//                                        FileWriter writer = new FileWriter(tempFile);
//                                        for(String str: invalidRows) {
//                                            writer.write(str + System.lineSeparator());
//                                        }
//                                        writer.close();
//                                        Filedownload.save(new FileInputStream(tempFile), "text/plain; charset-UTF-8", "Error_Report_CSV.txt");
//                                    }
//                                }
//                            });
                    return new LogModel(sortTraces(logData), lineCount, errorCount, invalidRows);
                }

                return new LogModel(sortTraces(logData), lineCount, errorCount, invalidRows);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isValidLineCount(int lineCount) {
        return true;
    }
    public void automaticFormat(ListModelList<String[]> result, List<String> myHeader) {
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

                                        if (validTS != null && validTS.getYear() > 0) {
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
                                // automatic parse might be in accurate.
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
                                        } else {
                                            continue;
                                        }
                                    }
                                } else {
                                    startFormat = format;
                                }
                            } catch (Exception e) {
                                // automatic parse might be in accurate.
                                Messagebox.show("Automatic parse of start timestamp might be in accurate. Please validate end timestamp field.");
                                break;
                            }
                        }
                    }
                }
            }
            timestampFormat = currentFormat;
            startTsFormat = startFormat;
        } catch (Exception e) {
            // automatic detection failed.
            e.printStackTrace();
        }
    }

    public HashMap<String, Integer> getHeads() {
        return this.heads;
    }

    /**
     * Header pos.
     * <p>
     * //     * @param line read line from CSV
     *
     * @return the hash map: including mandatory field as key and position in the array as the value.
     */

    public void setOtherAll(Window window) {
        int otherIndex = 6;

        for (int i = 0; i < line.size(); i++) {
            Listbox lb = (Listbox) window.getFellow(String.valueOf(i));
            if (lb.getSelectedIndex() == 7) {
                removeColPos(i);
                closePopUp(i);
                lb.setSelectedIndex(otherIndex);
                heads.put("Event Attribute", i);
            }
        }

    }

    public void setIgnoreAll(Window window) {
        int otherIndex = 7;

        for (int i = 0; i < line.size(); i++) {
            Listbox lb = (Listbox) window.getFellow(String.valueOf(i));
            if (lb.getSelectedIndex() == 6) {
                removeColPos(i);
                closePopUp(i);
                lb.setSelectedIndex(otherIndex);
                heads.put("ignore", i);
                ignoredPos.add(i);
            }
        }
    }

    public void setHeads(List<String> line) {
        // initialize map
        heads = new HashMap<String, Integer>();
        heads.put(caseid, -1);
        heads.put(activity, -1);
        heads.put(timestamp, -1);
        heads.put(tsStart, -1);
        heads.put(resource, -1);

        for (int i = 0; i <= line.size() - 1; i++) {
            if (this.line.get(i) != null) {
                if ((heads.get(caseid) == -1) && getPos(caseIdValues, line.get(i))) {
                    heads.put(caseid, i);
                } else if ((heads.get(activity) == -1) && getPos(activityValues, line.get(i))) {
                    heads.put(activity, i);
                } else if ((heads.get(timestamp) == -1) && getPos(timestampValues, line.get(i).toLowerCase())) {
                    String format = parse.determineDateFormat(this.line.get(i));
                    if (format != null) {
                        heads.put(timestamp, i);
                        timestampFormat = format;
                    }
                } else if ((heads.get(tsStart) == -1) && getPos(StartTsValues, line.get(i))) {
                    String format = parse.determineDateFormat(this.line.get(i));
                    if (format != null) {
                        heads.put(tsStart, i);
                        startTsFormat = format;
                    }
                } else if ((heads.get(resource) == -1) && getPos(resourceValues, line.get(i))) {
                    heads.put(resource, i);
                }
            }
        }
    }

    public void setLine(List<String> line) {
        this.line = line;
    }

    public void resetLine() {
        this.line = null;
    }

    ;

    public void resetHead() {
        this.heads = null;
    }

    ;

    public void resetList() {
        this.lists = null;
    }

    ;

    public void setOtherTimestamps(ListModelList<String[]> result) {
        if (result == null || result.size() == 0) {
            otherTimeStampsPos = new HashMap<Integer, String>();
            Integer timeStampPos = heads.get(timestamp);
            Integer StartTimeStampPos = heads.get(tsStart);

            for (int i = 0; i <= this.line.size() - 1; i++) {
                String detectedFormat = parse.determineDateFormat(this.line.get(i));
                if ((i != timeStampPos) && (i != StartTimeStampPos) && (detectedFormat != null)) {
                    otherTimeStampsPos.put(i, detectedFormat);
                }
            }
        } else {
            // do multiple line
        }
    }

    /**
     * Gets the pos.
     *
     * @param col  the col: array which has possible names for each of the mandatory fields.
     * @param elem the elem: one item of the CSV line array
     * @return the pos: boolean value confirming if the elem is the required element.
     */
    private Boolean getPos(String[] col, String elem) {
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
    private StringBuilder checkFields(HashMap<String, Integer> posMap) {
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


    private List<LogEventModel> sortTraces(List<LogEventModel> traces) {
        Comparator<String> nameOrder = new NameComparator();
        Collections.sort(traces, (o1, o2) -> nameOrder.compare(o1.getCaseID(), o2.getCaseID()));
        return traces;
    }


    public void setLists(int cols, HashMap<String, Integer> heads, String boxwidth) {

        lists = new ArrayList<Listbox>();
        ignoredPos = new ArrayList<Integer>();


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
                        (new String(dl.getKey()).equals(caseid) && cl == heads.get(caseid)) ||
                                (new String(dl.getKey()).equals(activity) && cl == heads.get(activity)) ||
                                (new String(dl.getKey()).equals(timestamp) && cl == heads.get(timestamp)) ||
                                (new String(dl.getKey()).equals(tsStart) && cl == heads.get(tsStart)) ||
                                (new String(dl.getKey()).equals(resource) && cl == heads.get(resource)) ||
                                (new String(dl.getKey()).equals(tsValue) && otherTimeStampsPos.get(cl) != null) ||
                                (new String(dl.getKey()).equals(other)))
                        ) {
                    item.setSelected(true);
                }

                box.appendChild(item);
            }


            box.addEventListener("onSelect", (Event event) -> {
                // get selected index, and check if it is caseid, activity or time stamp
                String selected = box.getSelectedItem().getValue();
                int colPos = Integer.parseInt(box.getId());
                removeColPos(colPos);
                closePopUp(colPos);

                if (new String(selected).equals(caseid) || new String(selected).equals(activity) || new String(selected).equals(timestamp) || new String(selected).equals(tsStart) || new String(selected).equals(resource)) {

                    int oldColPos = heads.get(selected);
                    if (oldColPos != -1) {
                        Listbox oldBox = lists.get(oldColPos);
                        oldBox.setSelectedIndex(otherIndex);
                        removeColPos(oldColPos);
                        closePopUp(oldColPos);
                    }

                    if (new String(selected).equals(timestamp) || new String(selected).equals(tsStart)) {
                        tryParsing(parse.determineDateFormat(this.line.get(colPos)), colPos);
                    } else {
                        heads.put(selected, colPos);
                    }

                } else if (new String(selected).equals(ignore)) {
                    ignoredPos.add(colPos);
                } else if (new String(selected).equals(tsValue)) {
                    tryParsing(parse.determineDateFormat(this.line.get(colPos)), colPos);
                }
            });

            lists.add(box);
        }
    }

    public List<Listbox> getLists() {
        return lists;
    }

    private void removeColPos(int colPos) {

        if (otherTimeStampsPos.get(colPos) != null) {
            otherTimeStampsPos.remove(Integer.valueOf(colPos));

        } else if (ignoredPos.contains(colPos)) {
            ignoredPos.remove(Integer.valueOf(colPos));
        } else {

            for (Map.Entry<String, Integer> entry : heads.entrySet()) {
                if (entry.getValue() == colPos) {
                    heads.put(entry.getKey(), -1);
                    break;
                }
            }
        }
    }


    private void closePopUp(int colPos) {
        Window myPopUp = (Window) popUPBox.getFellow(popupID + colPos);
        myPopUp.setStyle(myPopUp.getStyle().replace("visible", "hidden"));
    }


    public void tryParsing(String format, int colPos) {

        if (format != null && parse.parseTimestamp(this.line.get(colPos), format) != null) {
            Listbox box = lists.get(colPos);
            String selected = box.getSelectedItem().getValue();
            if (new String(selected).equals(timestamp)) {
                heads.put(selected, colPos);
                timestampFormat = format;
            } else if (new String(selected).equals(tsStart)) {
                heads.put(selected, colPos);
                startTsFormat = format;
            } else if (new String(selected).equals(tsValue)) {
                otherTimeStampsPos.put(colPos, format);
            }
            openPopUpbox(colPos, format, parsedCorrectly, parsedClass);
        } else {
            openPopUpbox(colPos, format, couldnotParse, failedClass);
        }
    }


    public void openPopUp() {
        Integer timeStampPos = heads.get(timestamp);
        if (timeStampPos != -1) openPopUpbox(heads.get(timestamp), timestampFormat, parsedCorrectly, parsedClass);

        Integer startTimeStampPos = heads.get(tsStart);
        if (startTimeStampPos != -1) openPopUpbox(heads.get(tsStart), startTsFormat, parsedCorrectly, parsedClass);

        for (Map.Entry<Integer, String> entry : otherTimeStampsPos.entrySet()) {
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


    /**
     * Creates the X log.
     * <p>
     * create xlog element, assign respective extensions and attributes for each event and trace
     *
     * @param traces the traces
     * @return the x log
     */
    public XLog createXLog(List<LogEventModel> traces) {
        if (traces == null) return null;

        XFactory xFactory = new XFactoryNaiveImpl();
        XLog xLog = xFactory.createLog();
        XTrace xTrace = null;
        XEvent xEvent = null;
        List<XEvent> allEvents = new ArrayList<XEvent>();

        // declare standard extensions of thEe log
        XConceptExtension concept = XConceptExtension.instance();
        XLifecycleExtension lifecycle = XLifecycleExtension.instance();
        XTimeExtension timestamp = XTimeExtension.instance();
        XOrganizationalExtension resource = XOrganizationalExtension.instance();

        xLog.getExtensions().add(concept);
        xLog.getExtensions().add(lifecycle);
        xLog.getExtensions().add(timestamp);
        xLog.getExtensions().add(resource);

        lifecycle.assignModel(xLog, XLifecycleExtension.VALUE_MODEL_STANDARD);

        String newTraceID = null;    // to keep track of traces, when a new trace is created we assign its value and add the respective events for the trace.

//        Comparator<XEvent> compareTimestamp = (XEvent o1, XEvent o2) -> ((XAttributeTimestampImpl) o1.getAttributes().get("time:timestamp")).getValue().compareTo(((XAttributeTimestampImpl) o2.getAttributes().get("time:timestamp")).getValue());
        Comparator<XEvent> compareTimestamp = (XEvent o1, XEvent o2) -> {
            Date o1Date;
            Date o2Date;
            if (o1.getAttributes().get("time:timestamp") != null) {
                XAttribute o1da = o1.getAttributes().get("time:timestamp");
                if (((XAttributeTimestamp) o1da).getValue() != null) {
                    o1Date = ((XAttributeTimestamp) o1da).getValue();
                } else {
                    return -1;
                }
            } else {
                return -1;
            }

            if (o2.getAttributes().get("time:timestamp") != null) {
                XAttribute o2da = o2.getAttributes().get("time:timestamp");
                if (((XAttributeTimestamp) o2da).getValue() != null) {
                    o2Date = ((XAttributeTimestamp) o2da).getValue();
                } else {
                    return 1;
                }
            } else {
                return 1;
            }

            if (o1Date == null || o1Date.toString().isEmpty()) {
                Messagebox.show("o1Date is null!");
                return 1;
            } else if (o2Date == null || o2Date.toString().isEmpty()) {
                Messagebox.show("o2Date is null!");
                return -1;
            } else {
                return o1Date.compareTo(o2Date);
            }

        };
//        Comparator<XEvent> compareTimestamp = Comparator.comparing((XEvent o) -> ((XAttributeTimestampImpl) o.getAttributes().get("time:timestamp")).getValue());

        for (LogEventModel trace : traces) {
            String caseID = trace.getCaseID();

            if (newTraceID == null || !newTraceID.equals(caseID)) {    // This could be new trace

                if (!allEvents.isEmpty()) {
                    Collections.sort(allEvents, compareTimestamp);
                    xTrace.addAll(allEvents);
                    allEvents = new ArrayList<XEvent>();
                }

                xTrace = xFactory.createTrace();
                concept.assignName(xTrace, caseID);
                xLog.add(xTrace);
                newTraceID = caseID;
            }

            if (trace.getStartTimestamp() != null) {
                xEvent = createEvent(trace, xFactory, concept, lifecycle, timestamp, resource, false);
                allEvents.add(xEvent);
            }
            if (timestamp != null) {
                xEvent = createEvent(trace, xFactory, concept, lifecycle, timestamp, resource, true);
            }
            allEvents.add(xEvent);
        }

        if (!allEvents.isEmpty()) {
            Collections.sort(allEvents, compareTimestamp);
            xTrace.addAll(allEvents);
        }


        if (xEvent == null) {
            return null;
        } else {
            return xLog;
        }
    }


    private XEvent createEvent(LogEventModel theTrace, XFactory xFactory, XConceptExtension concept, XLifecycleExtension lifecycle, XTimeExtension timestamp, XOrganizationalExtension resource, Boolean isEndTimestamp) {

        XEvent xEvent = xFactory.createEvent();
        concept.assignName(xEvent, theTrace.getConcept());

        if (theTrace.getResource() != null) {
            resource.assignResource(xEvent, theTrace.getResource());
        }

        XAttribute attribute;
        if(theTrace.getOtherTimestamps() != null) {
            HashMap<String, Timestamp> otherTimestamps = theTrace.getOtherTimestamps();
            for (Map.Entry<String, Timestamp> entry : otherTimestamps.entrySet()) {
                attribute = new XAttributeTimestampImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }
        HashMap<String, String> others = theTrace.getOthers();
        for (Map.Entry<String, String> entry : others.entrySet()) {
            if (entry.getValue() != null && entry.getValue().trim().length() != 0) {
                attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }
        if (theTrace.getTimestamp() != null) {
            if (!isEndTimestamp) {
                lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.START);
                timestamp.assignTimestamp(xEvent, theTrace.getStartTimestamp());

            } else {
                lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.COMPLETE);
                timestamp.assignTimestamp(xEvent, theTrace.getTimestamp());
            }
        }

        return xEvent;
    }


    /**
     * To XES file.
     * <p>
     * Serialize xLog to XES file.
     *
     * @param xLog the x log
     * @throws FileNotFoundException the file not found exception
     * @throws IOException           Signals that an I/O exception has occurred.
     */
    public void toXESfile(XLog xLog, String FileName) throws FileNotFoundException, IOException {
        if (xLog == null) return;


        String FileNameWithoutExtention = FileName.replaceFirst("[.][^.]+$", "");
        XesXmlSerializer serializer = new XesXmlSerializer();
        serializer.serialize(xLog, new FileOutputStream(new File(FileNameWithoutExtention + ".xes")));
//        Messagebox.show("Your file has been created!");

    }

    public List<String> getEncoding() {
        return Arrays.asList(new String[]{"UTF-8", "windows-1250 (Eastern European)", "windows-1251 (Cyrillic)",
                "windows-1252 (Latin)", "windows-1253 (Greek)", "windows-1254 (Turkish)",
                "windows-1255 (Hebrew)", "windows-1256 (Arabic)", "windows-1258 (Vietnamese)", "windows-31j (Japanese)",
                "ISO-2022-CN (Chinese)"});
    }

}
