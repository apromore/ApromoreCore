package org.apromore.service.csvimporter.impl;

import org.apromore.service.csvimporter.InvalidCSVException;
import org.apromore.service.csvimporter.LogSample;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.*;

import java.sql.Timestamp;
import java.util.*;

import org.zkoss.zk.ui.util.Clients;

/**
 * A sample of a CSV log.
 */
class LogSampleImpl implements LogSample, Constants {

    private static final Integer AttribWidth = 180;
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
    private Button[] formatBtns;
    private List<Integer> caseAttributesPos;


    // Constructor

    LogSampleImpl(List<String> header, List<List<String>> lines) throws InvalidCSVException {
        this.header = header;
        this.lines = lines;

        // Choose the first non-header line to use for all our format-sniffing
        List<String> sampleLine = lines.get(0);

//        if (sampleLine.size() != header.size()) {
//            throw new InvalidCSVException("Number of columns in the header does not match number of columns in the data");
//        }

        // Empty header permutation map (heads)
        heads = new HashMap<>();
        heads.put(caseid, -1);
        heads.put(activity, -1);
        heads.put(timestamp, -1);
        heads.put(tsStart, -1);
        heads.put(resource, -1);

        // Populate heads, timestampFormat, startTsFormat
        for (int i = 0; i < header.size(); i++) {
            if (sampleLine.get(i) != null) {
                if ((heads.get(caseid) == -1) && getPos(caseIdValues, header.get(i))) {
                    heads.put(caseid, i);
                } else if ((heads.get(activity) == -1) && getPos(activityValues, header.get(i))) {
                    heads.put(activity, i);
                } else if ((heads.get(timestamp) == -1) && getPos(timestampValues, header.get(i).toLowerCase())) {
                    String format = parse.determineDateFormat(sampleLine.get(i));
                    if (format != null) {
                        heads.put(timestamp, i);
                        timestampFormat = format;
                    }
                } else if ((heads.get(tsStart) == -1) && getPos(StartTsValues, header.get(i))) {
                    String format = parse.determineDateFormat(sampleLine.get(i));
                    if (format != null) {
                        heads.put(tsStart, i);
                        startTsFormat = format;
                    }
                } else if ((heads.get(resource) == -1) && getPos(resourceValues, header.get(i))) {
                    heads.put(resource, i);
                }
            }
        }

        this.lists = new ArrayList<>();
        this.ignoredPos = new ArrayList<>();
        this.otherTimeStampsPos = new HashMap<>();
        this.caseAttributesPos = new ArrayList<>();

        setOtherTimestamps();
        toLists(this);
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

    @Override
    public Button[] getFormatBtns() { return this.formatBtns; }

    @Override
    public void setFormatBtns(Button[] formatBtns) { this.formatBtns = formatBtns; }

    @Override
    public List<Integer> getCaseAttributesPos() { return caseAttributesPos; }

    // Public methods

    @Override
    public List<String> automaticFormat() {
        List<String> errorMessages = new ArrayList<>();

        try {
            String currentFormat = null;
            String startFormat = null;

            // skipping 5 lines is too much for small logs, go through every line when it's less than 1000 lines in total.
            int IncValue = (this.lines.size() < 1000) ? 1 : 5;

            outerloop:
            // naming the outer loop so we can break out from this loop within nested loops.
            for (int i = 0; i < Math.min(1000, this.lines.size()); i += IncValue) {
                List<String> newLine = this.lines.get(i);

                for (int j = 0; j < newLine.size(); j++) {
                    // Going row by row
                    if(newLine.size() != getHeader().size()) {
                        continue;
                    }
                    if (getPos(timestampValues, getHeader().get(j).toLowerCase())) {
                        // if its timestamp field


                        //TODO this needs to use determineFormatForArray method from Parse.java
                        String format = parse.determineDateFormat((newLine.get(j))); // dd.MM.yyyy //MM.dd.yyyy
                        Timestamp validTS = Parse.parseTimestamp(newLine.get(j), format);
                        if (validTS != null) {
                            try {
                                if (currentFormat != null) {
                                    // determine which one is right which one is wrong
                                    // hint: use sets to store all the possible formats, then parse them again.

                                    if (currentFormat != format) {
                                        Timestamp validTS2 = Parse.parseTimestamp(this.lines.get(i - IncValue).get(j), currentFormat);

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
                                errorMessages.add("Automatic parse of End timestamp might be inaccurate. Please validate end timestamp field.");
                                break;
                            }

                        }

                    }

                    if (getPos(StartTsValues, getHeader().get(j))) {
                        // if its timestamp field
                        String format = parse.determineDateFormat((newLine.get(j)));
                        Timestamp validTS = Parse.parseTimestamp(newLine.get(j), format);


                        if (validTS != null) {
                            try {
                                if (startFormat != null) {

                                    // determine which one is right which one is wrong
                                    // hint: use sets to store all the possible formats, then parse them again.
                                    if (startFormat != format) {
                                        validTS = Parse.parseTimestamp(this.lines.get(i - 1).get(j), format);
                                        if (validTS != null) {
                                            startFormat = format;
                                            break outerloop;
                                        }
                                    }
                                } else {
                                    startFormat = format;
                                }
                            } catch (Exception e) {
                                // automatic parse might be inaccurate.
                                errorMessages.add("Automatic parse of start timestamp might be inaccurate. Please validate start timestamp field.");
                                break;
                            }
                        }
                    }
                }
            }
            setTimestampFormat(currentFormat);
            setStartTsFormat(startFormat);
        } catch (Exception e) {
            // automatic detection failed.
            e.printStackTrace();
            errorMessages.add("Automatic detection failed.");
        }

        return errorMessages;
    }

    @Override
    public void openPopUp(boolean show) {
        Integer timeStampPos = this.getHeads().get(timestamp);
        if (timeStampPos != -1) openPopUpbox(this.getHeads().get(timestamp), this.getTimestampFormat(), parsedCorrectly, parsedClass, this, show);

        Integer startTimeStampPos = this.getHeads().get(tsStart);
        if (startTimeStampPos != -1) openPopUpbox(this.getHeads().get(tsStart), this.getStartTsFormat(), parsedCorrectly, parsedClass, this, show);

        for (Map.Entry<Integer, String> entry : this.getOtherTimeStampsPos().entrySet()) {
            openPopUpbox(entry.getKey(), entry.getValue(), parsedCorrectly, parsedClass, this, show);
        }
    }

    @Override
    public void setOtherAll(Window window) {
        int otherIndex = 6;

        for (int i = 0; i < this.getLines().get(0).size(); i++) {
            Listbox lb = (Listbox) window.getFellow(String.valueOf(i));
            if (lb.getSelectedIndex() == 8) {
                removeColPos(i, this);
                closePopUp(i, this);
                lb.setSelectedIndex(otherIndex);
                this.getHeads().put("Event Attribute", i);
            }
        }

    }

    @Override
    public void setIgnoreAll(Window window) {
        int ignoreIndex = 8;

        for (int i = 0; i < this.getLines().get(0).size(); i++) {
            Listbox lb = (Listbox) window.getFellow(String.valueOf(i));
            if (lb.getSelectedIndex() == 6) {
                removeColPos(i, this);
                closePopUp(i, this);
                lb.setSelectedIndex(ignoreIndex);
                this.getHeads().put("ignore", i);
                this.getIgnoredPos().add(i);
            }
        }
    }

    @Override
    public void setOtherTimestamps() {
        this.getOtherTimeStampsPos().clear();
        Integer timeStampPos = this.getHeads().get(timestamp);
        Integer StartTimeStampPos = this.getHeads().get(tsStart);

        for (int i = 0; i < this.getLines().get(0).size(); i++) {
            String detectedFormat = parse.determineDateFormat(this.getLines().get(0).get(i));
            if ((i != timeStampPos) && (i != StartTimeStampPos) && (detectedFormat != null)) {
                this.getOtherTimeStampsPos().put(i, detectedFormat);
            }
        }
    }

    @Override
    public void tryParsing(String format, int colPos) {

        if (format == null || parse.parseTimestamp(this.getLines().get(0).get(colPos), format) == null) {
            openPopUpbox(colPos, format, couldnotParse, failedClass, this, true);
            return;
        }

        Listbox box = this.getLists().get(colPos);
        String selected = box.getSelectedItem().getValue();
        if (new String(selected).equals(timestamp)) {
            this.getHeads().put(selected, colPos);
            this.setTimestampFormat(format);
        } else if (new String(selected).equals(tsStart)) {
            this.getHeads().put(selected, colPos);
            this.setStartTsFormat(format);
        } else if (new String(selected).equals(tsValue)) {
            this.getOtherTimeStampsPos().put(colPos, format);
        }
        openPopUpbox(colPos, format, parsedCorrectly, parsedClass, this, true);
    }


    // Internal methods

    private static void toLists(LogSample sample) {

        LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
        String other = "Event Attribute";
        String caseAttribute = "Case Attribute";
        String ignore = "ignore";

        menuItems.put(caseid, "Case ID");
        menuItems.put(activity, "Activity");
        menuItems.put(timestamp, "End timestamp");
        menuItems.put(tsStart, "Start timestamp");
        menuItems.put(tsValue, "Other timestamp");
        menuItems.put(resource, "Resource");
        menuItems.put(other, "Event Attribute");
        menuItems.put(caseAttribute, "Case Attribute");
        menuItems.put(ignore, "Ignore column");

        // get index of "other" item and select it.
        int otherIndex = new ArrayList<String>(menuItems.keySet()).indexOf(other);

        for (int cl = 0; cl < sample.getLines().get(0).size(); cl++) {

            Listbox box = new Listbox();
            box.setMold("select"); // set listBox to select mode
            box.setId(String.valueOf(cl)); // set id of list as column position.
            box.setWidth(AttribWidth - 20 + "px");

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
                        sample.tryParsing(parse.determineDateFormat(sample.getLines().get(0).get(colPos)), colPos);
                    } else {
                        sample.getHeads().put(selected, colPos);
                    }

                } else if (selected.equals(ignore)) {
                    sample.getIgnoredPos().add(colPos);
                } else if (selected.equals(tsValue)) {
                    sample.tryParsing(parse.determineDateFormat(sample.getLines().get(0).get(colPos)), colPos);
                } else if(selected.equals(caseAttribute)){
                    sample.getCaseAttributesPos().add(colPos);
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

    private static void openPopUpbox(Integer colPos, String format, String message, String lblClass, LogSample sample, boolean show) {
        Window myPopUp = (Window) sample.getPopUPBox().getFellow(popupID + colPos);
        Label check_lbl = (Label) myPopUp.getFellow(labelID + colPos);
        Button[] formatBtns = (Button[]) sample.getFormatBtns();
        formatBtns[colPos].setSclass("ap-csv-importer-format-icon");

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
        if (show) {
            myPopUp.setStyle(myPopUp.getStyle().replace("hidden", "visible"));
            Clients.evalJavaScript("adjustPos(" + colPos + ")");
        }
    }

    private static void closePopUp(int colPos, LogSample sample) {
        Window myPopUp = (Window) sample.getPopUPBox().getFellow(popupID + colPos);
        myPopUp.setStyle(myPopUp.getStyle().replace("visible", "hidden"));
    }
}
