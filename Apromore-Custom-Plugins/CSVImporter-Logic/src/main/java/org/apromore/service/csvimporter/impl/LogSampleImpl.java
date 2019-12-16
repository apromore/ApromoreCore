package org.apromore.service.csvimporter.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apromore.service.csvimporter.InvalidCSVException;
import org.apromore.service.csvimporter.LogSample;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * A sample of a CSV log.
 */
class LogSampleImpl implements LogSample, Constants {

    private static final Integer AttribWidth = 150;
    private static final String parsedCorrectly = "Format parsed! ";
    private static final String couldnotParse = "Could not parse!";
    private static final String parsedClass = "text-success";
    private static final String failedClass = "text-danger";

    private static final Parse parse = new Parse();


    // Instance variables

    private List<String> header;
    private List<List<String>> lines;

    private Map<String, Integer> heads;
    private String timestampFormat;
    private String startTsFormat;
    private List<Listbox> lists;
    private List<Integer> ignoredPos;
    private HashMap<Integer, String> otherTimeStampsPos;
    private Div popUPBox;


    // Constructor

    LogSampleImpl(List<String> header, List<List<String>> lines) throws InvalidCSVException {
        this.header = header;
        this.lines = lines;

        this.heads = toHeads(header, lines.get(0));
        this.lists = new ArrayList<>();
        this.ignoredPos = new ArrayList<>();
        this.otherTimeStampsPos = new HashMap<>();

        List<String> line = lines.get(0);
        if (line.size() != header.size()) {
            throw new InvalidCSVException("Number of columns in the header does not match number of columns in the data");
        }
        setOtherTimestamps(new ArrayList<>(), this);
        toLists(line.size(), AttribWidth - 20 + "px", this);
    }


    // Accessors

    @Override
    public List<String> getHeader() { return header; }

    @Override
    public List<List<String>> getLines() { return lines; }

    @Override
    public Map<String, Integer> getHeads() { return heads; }

    @Override
    public String getTimestampFormat() { return timestampFormat; }

    @Override
    public void setTimestampFormat(String s) { timestampFormat = s; }

    @Override
    public String getStartTsFormat() { return startTsFormat; }

    @Override
    public void setStartTsFormat(String s) { startTsFormat = s; }

    @Override
    public List<Listbox> getLists() { return lists; }

    @Override
    public List<Integer> getIgnoredPos() { return ignoredPos; }

    @Override
    public HashMap<Integer, String> getOtherTimeStampsPos() { return otherTimeStampsPos; }

    @Override
    public Div getPopUPBox() { return popUPBox; }

    @Override
    public void setPopUPBox(Div popUPBox) { this.popUPBox = popUPBox; }


    // Public methods

    @Override
    public void automaticFormat(List<String[]> result, LogSample sample) {
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
                    if(newLine.length != sample.getHeader().size()) {
                        continue;
                    }
                    if (getPos(timestampValues, sample.getHeader().get(j).toLowerCase())) {
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

                    if (getPos(StartTsValues, sample.getHeader().get(j))) {
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

    @Override
    public void openPopUp(LogSample sample) {
        Integer timeStampPos = sample.getHeads().get(timestamp);
        if (timeStampPos != -1) openPopUpbox(sample.getHeads().get(timestamp), sample.getTimestampFormat(), parsedCorrectly, parsedClass, sample);

        Integer startTimeStampPos = sample.getHeads().get(tsStart);
        if (startTimeStampPos != -1) openPopUpbox(sample.getHeads().get(tsStart), sample.getStartTsFormat(), parsedCorrectly, parsedClass, sample);

        for (Map.Entry<Integer, String> entry : sample.getOtherTimeStampsPos().entrySet()) {
            openPopUpbox(entry.getKey(), entry.getValue(), parsedCorrectly, parsedClass, sample);
        }
    }

    @Override
    public void setOtherAll(Window window, LogSample sample) {
        int otherIndex = 6;

        for (int i = 0; i < sample.getLines().get(0).size(); i++) {
            Listbox lb = (Listbox) window.getFellow(String.valueOf(i));
            if (lb.getSelectedIndex() == 7) {
                removeColPos(i, sample);
                closePopUp(i, sample);
                lb.setSelectedIndex(otherIndex);
                sample.getHeads().put("Event Attribute", i);
            }
        }

    }

    @Override
    public void setIgnoreAll(Window window, LogSample sample) {
        int otherIndex = 7;

        for (int i = 0; i < sample.getLines().get(0).size(); i++) {
            Listbox lb = (Listbox) window.getFellow(String.valueOf(i));
            if (lb.getSelectedIndex() == 6) {
                removeColPos(i, sample);
                closePopUp(i, sample);
                lb.setSelectedIndex(otherIndex);
                sample.getHeads().put("ignore", i);
                sample.getIgnoredPos().add(i);
            }
        }
    }

    @Override
    public void setOtherTimestamps(List<String[]> result, LogSample sample) {
        if (result == null || result.size() == 0) {
            sample.getOtherTimeStampsPos().clear();
            Integer timeStampPos = sample.getHeads().get(timestamp);
            Integer StartTimeStampPos = sample.getHeads().get(tsStart);

            for (int i = 0; i < sample.getLines().get(0).size(); i++) {
                String detectedFormat = parse.determineDateFormat(sample.getLines().get(0).get(i));
                if ((i != timeStampPos) && (i != StartTimeStampPos) && (detectedFormat != null)) {
                    sample.getOtherTimeStampsPos().put(i, detectedFormat);
                }
            }
        }  // do multiple line

    }

    @Override
    public void tryParsing(String format, int colPos, LogSample sample) {

        if (format == null || parse.parseTimestamp(sample.getLines().get(0).get(colPos), format) == null) {
            openPopUpbox(colPos, format, couldnotParse, failedClass, sample);
            return;
        }

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
        openPopUpbox(colPos, format, parsedCorrectly, parsedClass, sample);
    }


    // Internal methods

    private Map<String, Integer> toHeads(List<String> line, List<String> sampleLine) {

        final Parse parse = new Parse();

        // initialize map
        Map<String, Integer> heads = new HashMap<>();
        heads.put(caseid, -1);
        heads.put(activity, -1);
        heads.put(timestamp, -1);
        heads.put(tsStart, -1);
        heads.put(resource, -1);

        for (int i = 0; i <= line.size() - 1; i++) {
            if (sampleLine.get(i) != null) {
                if ((heads.get(caseid) == -1) && getPos(caseIdValues, line.get(i))) {
                    heads.put(caseid, i);
                } else if ((heads.get(activity) == -1) && getPos(activityValues, line.get(i))) {
                    heads.put(activity, i);
                } else if ((heads.get(timestamp) == -1) && getPos(timestampValues, line.get(i).toLowerCase())) {
                    String format = parse.determineDateFormat(sampleLine.get(i));
                    if (format != null) {
                        heads.put(timestamp, i);
                        timestampFormat = format;
                    }
                } else if ((heads.get(tsStart) == -1) && getPos(StartTsValues, line.get(i))) {
                    String format = parse.determineDateFormat(sampleLine.get(i));
                    if (format != null) {
                        heads.put(tsStart, i);
                        startTsFormat = format;
                    }
                } else if ((heads.get(resource) == -1) && getPos(resourceValues, line.get(i))) {
                    heads.put(resource, i);
                }
            }
        }

        return heads;
    }

    public static void toLists(int cols, String boxwidth, LogSample sample) {

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
                closePopUp(colPos, sample);

                if (selected.equals(caseid) || selected.equals(activity) || selected.equals(timestamp) || selected.equals(tsStart) || new String(selected).equals(resource)) {

                    int oldColPos = sample.getHeads().get(selected);
                    if (oldColPos != -1) {
                        Listbox oldBox = sample.getLists().get(oldColPos);
                        oldBox.setSelectedIndex(otherIndex);
                        removeColPos(oldColPos, sample);
                        closePopUp(oldColPos, sample);
                    }

                    if (selected.equals(timestamp) || selected.equals(tsStart)) {
                        sample.tryParsing(parse.determineDateFormat(sample.getLines().get(0).get(colPos)), colPos, sample);
                    } else {
                        sample.getHeads().put(selected, colPos);
                    }

                } else if (selected.equals(ignore)) {
                    sample.getIgnoredPos().add(colPos);
                } else if (selected.equals(tsValue)) {
                    sample.tryParsing(parse.determineDateFormat(sample.getLines().get(0).get(colPos)), colPos, sample);
                }
            });

            sample.getLists().add(box);
        }
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

    private static void removeColPos(int colPos, LogSample sample) {

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

    private static void openPopUpbox(Integer colPos, String format, String message, String lblClass, LogSample sample) {
        Window myPopUp = (Window) sample.getPopUPBox().getFellow(popupID + colPos);
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

    private static void closePopUp(int colPos, LogSample sample) {
        Window myPopUp = (Window) sample.getPopUPBox().getFellow(popupID + colPos);
        myPopUp.setStyle(myPopUp.getStyle().replace("visible", "hidden"));
    }
}
