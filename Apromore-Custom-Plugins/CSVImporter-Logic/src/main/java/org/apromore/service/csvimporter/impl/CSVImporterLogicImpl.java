package org.apromore.service.csvimporter.impl;

import org.apromore.service.csvimporter.CSVImporterLogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.*;

import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.zkoss.zul.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.*;


import com.opencsv.CSVReader;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.*;
import org.deckfour.xes.out.XesXmlSerializer;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
//import org.deckfour.xes.*;
// TODO: Auto-generated Javadoc
    /**
     * The Class CsvToXes.
     */
    public class CSVImporterLogicImpl implements CSVImporterLogic {


        /** The case id values. */
        private String[] caseIdValues = {"case", "case id", "case-id", "service id", "event id"};

        /** The activity values. */
        private String[] activityValues = {"activity", "activity id", "activity-id", "operation", "event"};

        /** The timestamp Values. */
        private String[] timestampValues = {"timestamp", "end date", "complete timestamp", "time:timestamp"};


        private String[] StartTsValues = {"start date", "start timestamp"};

        private String[] resourceValues = {"resource", "agent", "employee", "group"};

        /** The Constant caseid. */
        private static final String caseid = "caseid";

        /** The Constant activity. */
        private static final String activity = "activity";

        /** The Constant timestamp. */
        private static final String timestamp = "timestamp";

        private static final String tsStart = "startTimestamp";
        private static final String resource = "resource";
        private static final String tsValue = "otherTimestamp";



        private List<Listbox> lists;
        private HashMap<String, Integer> heads;
        private List<Integer> ignoredPos;
        private HashMap<Integer, String> otherTimeStampsPos;
        private String[] line;
        private String timestampFormat;
        private String startTsFormat;

        private Div popUPBox;
        private String popupID;
        private String textboxID;
        private String labelID;
        private static final String parsedCorrectly = "Format parsed! ";
        private static final String couldnotParse = "Could not parse!";
        private static final String parsedClass = "text-success";
        private static final String failedClass = "text-danger";

        private static Parse parse = new Parse();
        /**
         * Prepare xes model.
         *
         //	 * @param media the media
         * @return the list
         */
        @SuppressWarnings("resource")
        public List<LogModel> prepareXesModel(Reader r) {

            try (CSVReader reader = new CSVReader(r)) {

                // read first line from CSV as header
                String[] header = reader.readNext();

                // If any of the mandatory fields are missing show alert message to the user and return
                StringBuilder headNOTDefined = checkFields(heads);
                if(headNOTDefined.length() !=0) {
                    Messagebox.show(headNOTDefined.toString());
                    return null;
                }


                // create model "LogModel" of the log data
                // We set mandatory fields and other fields are set with hash map
                String[] line;
                List<LogModel> logData = new ArrayList<LogModel>();
                HashMap<String, Timestamp> otherTimestamps;
                HashMap<String, String> others;
                Timestamp startTimestamp = null;
                String resourceCol = null;

                while ((line = reader.readNext()) != null) {

                    otherTimestamps = new HashMap<String, Timestamp>();
                    others = new HashMap<String, String>();

                    for(int p=0; p<= line.length-1; p++) {
                        if(otherTimeStampsPos.get(p) !=null){
                            otherTimestamps.put(header[p], parse.parseTimestamp(line[p], otherTimeStampsPos.get(p)));
                        }
                        else if (p!= heads.get(caseid) && p!= heads.get(activity) && p!= heads.get(timestamp)&& p!= heads.get(tsStart)&& p!= heads.get(resource) && (ignoredPos.isEmpty() || !ignoredPos.contains(p)) ) {
                            others.put(header[p], line[p]);
                        }
                    }
                    Timestamp tStamp =  parse.parseTimestamp(line[heads.get(timestamp)], timestampFormat);

                    if (heads.get(tsStart)!=  -1){
                        startTimestamp =  parse.parseTimestamp(line[heads.get(tsStart)], startTsFormat);
                    }
                    if (heads.get(resource)!=  -1){
                        resourceCol = line[heads.get(resource)];
                    }

                    logData.add(new LogModel(line[heads.get(caseid)], line[heads.get(activity)], tStamp ,startTimestamp, otherTimestamps,resourceCol, others));

                }
                return sortTraces(logData);

            } catch (IOException e) {
                e.printStackTrace();
                Messagebox.show(e.getMessage());
            }
            return null;
        }


        /**
         * Header pos.
         *
         * @param line read line from CSV
         * @return the hash map: including mandatory field as key and position in the array as the value.
         */

        public void setHeads(String[] line) {
            // initialize map
            heads = new HashMap<String, Integer>();
            heads.put(caseid, -1);
            heads.put(activity, -1);
            heads.put(timestamp, -1);
            heads.put(tsStart, -1);
            heads.put(resource, -1);

            for(int i=0; i<= line.length -1; i++) {
                if((heads.get(caseid) == -1) && getPos(caseIdValues, line[i])){
                    heads.put(caseid, i);
                }
                else if((heads.get(activity) == -1) && getPos(activityValues, line[i])) {
                    heads.put(activity, i);
                }
                else if((heads.get(timestamp) == -1) && getPos(timestampValues, line[i])) {
                    String format = parse.determineDateFormat(this.line[i]);
                    if(format!= null){
                        heads.put(timestamp, i);
                        timestampFormat = format;
                    }
                }
                else if((heads.get(tsStart) == -1) && getPos(StartTsValues, line[i])) {
                    String format = parse.determineDateFormat(this.line[i]);
                    if(format!= null){
                        heads.put(tsStart, i);
                        startTsFormat = format;
                    }
                }
                else if((heads.get(resource) == -1) && getPos(resourceValues, line[i])) {
                    heads.put(resource, i);
                }
            }
        }

        public HashMap<String, Integer> getHeads() {
            return this.heads;
        }

        public void setLine(String[] line) {
            this.line = line;
        }

        public void resetLine() { this.line = null; };
        public void resetHead() { this.heads = null; };
        public void resetList() { this.lists = null; };
        public void setOtherTimestamps(){
            otherTimeStampsPos = new HashMap<Integer, String>();
            Integer timeStampPos = heads.get(timestamp);
            Integer StartTimeStampPos = heads.get(tsStart);

            for(int i=0; i<= this.line.length -1; i++) {
                String detectedFormat = parse.determineDateFormat(this.line[i]);
                if((i != timeStampPos) && (i != StartTimeStampPos) && (detectedFormat != null)){
                    otherTimeStampsPos.put(i, detectedFormat);
                }
            }
        }


        /**
         * Gets the pos.
         *
         * @param col the col: array which has possible names for each of the mandatory fields.
         * @param elem the elem: one item of the CSV line array
         * @return the pos: boolean value confirming if the elem is the required element.
         */
        private Boolean getPos(String[] col, String elem) {
            return	Arrays.stream(col).anyMatch(elem.toLowerCase()::equals);
        }

        /**
         * Check fields.
         *
         * Check if all mandatory fields are found in the file, otherwise, construct a message based on the missed fields.
         *
         * @param posMap the pos map
         * @return the string builder
         */
        private StringBuilder checkFields(HashMap<String, Integer> posMap) {
            String[] fieldsToCheck = {caseid,activity,timestamp};
            StringBuilder importMessage = new StringBuilder();

            for(int f=0; f<=fieldsToCheck.length -1; f++) {
                if(posMap.get(fieldsToCheck[f]) == -1) {
                    String mess = "No " + fieldsToCheck[f] + " defined!";
                    importMessage = (importMessage.length() == 0?  importMessage.append(mess) :  importMessage.append(", " + mess));
                }
            }

            return importMessage;
        }


        private List<LogModel> sortTraces(List<LogModel> traces){
            Comparator<LogModel> compareCaseID = (LogModel o1, LogModel o2) -> {
                try {
                    String c1 = o1.getCaseID().replaceAll("[^0-9]", "");
                    String c2 = o2.getCaseID().replaceAll("[^0-9]", "");
                    Integer case1 = Integer.parseInt(c1);
                    Integer case2 = Integer.parseInt(c2);
                    return case1.compareTo(case2);
                }
                catch (NumberFormatException e)
                {
                    return o1.getCaseID().compareTo( o2.getCaseID());
                }
            };
            Collections.sort(traces, compareCaseID);
            return traces;
        }



        public void setLists(int cols, HashMap<String, Integer> heads, String boxwidth) {

            lists = new ArrayList<Listbox>();
            ignoredPos = new ArrayList<Integer>();


            LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
            String other = "other";
            String ignore = "ignore";

            menuItems.put(caseid, "Case ID");
            menuItems.put(activity, "Activity");
            menuItems.put(timestamp, "End timestamp");
            menuItems.put(tsStart, "Start timestamp");
            menuItems.put(tsValue, "Other timestamp");
            menuItems.put(resource, "Resource");
            menuItems.put(other, "Other");
            menuItems.put(ignore, "Ignore column");

            // get index of "other" item and select it.
            int otherIndex = new ArrayList<String>(menuItems.keySet()).indexOf(other);

            for(int cl =0; cl<=cols-1; cl++) {

                Listbox box = new Listbox();
                box.setMold("select"); // set listBox to select mode
                box.setId(String.valueOf(cl)); // set id of list as column position.
                box.setWidth(boxwidth);

                for (Map.Entry<String, String> dl : menuItems.entrySet()){
                    Listitem item = new Listitem();
                    item.setValue(dl.getKey());
                    item.setLabel(dl.getValue());

                    if((box.getSelectedItem() == null) && (
                            ( new String(dl.getKey()).equals(caseid) && cl == heads.get(caseid))||
                                    ( new String(dl.getKey()).equals(activity) && cl == heads.get(activity))||
                                    ( new String(dl.getKey()).equals(timestamp) && cl == heads.get(timestamp))||
                                    ( new String(dl.getKey()).equals(tsStart) && cl == heads.get(tsStart))||
                                    ( new String(dl.getKey()).equals(resource) && cl == heads.get(resource))||
                                    ( new String(dl.getKey()).equals(tsValue) && otherTimeStampsPos.get(cl) != null)||
                                    ( new String(dl.getKey()).equals(other)))
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

                    if(new String(selected).equals(caseid) || new String(selected).equals(activity)|| new String(selected).equals(timestamp) || new String(selected).equals(tsStart) || new String(selected).equals(resource)){

                        int oldColPos = heads.get(selected);
                        if(oldColPos!= -1){
                            Listbox oldBox = lists.get(oldColPos);
                            oldBox.setSelectedIndex(otherIndex);
                            removeColPos(oldColPos);
                            closePopUp(oldColPos);
                        }

                        if(new String(selected).equals(timestamp)|| new String(selected).equals(tsStart)){
                            tryParsing(parse.determineDateFormat(this.line[colPos]), colPos);
                        }else{
                            heads.put(selected,colPos);
                        }

                    }else if(new String(selected).equals(ignore)) {
                        ignoredPos.add(colPos);
                    }else if(new String(selected).equals(tsValue)){
                        tryParsing(parse.determineDateFormat(this.line[colPos]), colPos);
                    }
                });

                lists.add(box);
            }
        }

        public List<Listbox> getLists() {
            return lists;
        }

        private void removeColPos(int colPos){

            if(otherTimeStampsPos.get(colPos) != null){
                otherTimeStampsPos.remove(Integer.valueOf(colPos));

            }else if(ignoredPos.contains(colPos)){
                ignoredPos.remove(Integer.valueOf(colPos));
            }else{

                for (Map.Entry<String, Integer> entry : heads.entrySet()) {
                    if(entry.getValue() == colPos){
                        heads.put(entry.getKey(), -1);
                        break;
                    }
                }
            }
        }


        private void closePopUp(int colPos){
            Window myPopUp = (Window)popUPBox.getFellow(popupID + colPos);
            myPopUp.setStyle(myPopUp.getStyle().replace("visible", "hidden"));
        }


        public void tryParsing(String format, int colPos){

            if(format!= null && parse.parseTimestamp(this.line[colPos],format) !=null){
                Listbox box = lists.get(colPos);
                String selected = box.getSelectedItem().getValue();
                if(new String(selected).equals(timestamp)){
                    heads.put(selected,colPos);
                    timestampFormat = format;
                }
                else if(new String(selected).equals(tsStart)) {
                    heads.put(selected, colPos);
                    startTsFormat = format;
                }
                else if(new String(selected).equals(tsValue)){
                    otherTimeStampsPos.put(colPos, format);
                }
                openPopUpbox(colPos, format, parsedCorrectly,parsedClass);
            }else{
                openPopUpbox(colPos, format, couldnotParse,failedClass);
            }
        }


        public void openPopUp(){
            Integer timeStampPos = heads.get(timestamp);
            if(timeStampPos != -1) openPopUpbox(heads.get(timestamp), timestampFormat, parsedCorrectly, parsedClass);

            Integer startTimeStampPos = heads.get(tsStart);
            if(startTimeStampPos != -1) openPopUpbox(heads.get(tsStart), startTsFormat, parsedCorrectly, parsedClass);

            for (Map.Entry<Integer, String> entry : otherTimeStampsPos.entrySet()) {
                openPopUpbox(entry.getKey(), entry.getValue(), parsedCorrectly, parsedClass);
            }
        }


        private void openPopUpbox(Integer colPos, String format, String message, String lblClass){
            Window myPopUp = (Window)popUPBox.getFellow(popupID + colPos);
            myPopUp.setStyle(myPopUp.getStyle().replace("hidden", "visible"));
//		myPopUp.setHeight("12%");
            Label check_lbl = (Label)myPopUp.getFellow(labelID + colPos);

            Textbox txt = (Textbox)myPopUp.getFellow(textboxID+ colPos);
            txt.setValue(format);
//            check_lbl.setSclass("redLabel");
//            check_lbl.setZclass("redLabel");
            if(message == parsedCorrectly) {
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
         *
         *	create xlog element, assign respective extensions and attributes for each event and trace
         * @param traces the traces
         * @return the x log
         */
        public XLog createXLog(List<LogModel> traces){
            if(traces == null) return null;

            XFactory xFactory = new XFactoryBufferedImpl();
            XLog xLog = xFactory.createLog();
            XTrace xTrace = null;
            XEvent xEvent = null;
            List<XEvent> allEvents = new ArrayList<XEvent>();

            // declare standard extensions of the log
            XConceptExtension concept = XConceptExtension.instance();
            XLifecycleExtension lifecycle = XLifecycleExtension.instance();
            XTimeExtension timestamp = XTimeExtension.instance();
            XOrganizationalExtension resource = XOrganizationalExtension.instance();

            xLog.getExtensions().add(concept);
            xLog.getExtensions().add(lifecycle);
            xLog.getExtensions().add(timestamp);
            xLog.getExtensions().add(resource);

            lifecycle.assignModel(xLog, XLifecycleExtension.VALUE_MODEL_STANDARD);

            String newTraceID = null;	// to keep track of traces, when a new trace is created we assign its value and add the respective events for the trace.

            Comparator<XEvent> compareTimestamp= (XEvent o1, XEvent o2) -> ((XAttributeTimestampImpl) o1.getAttributes().get("time:timestamp")).getValue().compareTo(((XAttributeTimestampImpl) o2.getAttributes().get("time:timestamp")).getValue());

            for (LogModel trace : traces) {
                String caseID = trace.getCaseID();

                if(newTraceID == null || !newTraceID.equals(caseID)){	// This could be new trace

                    if(!allEvents.isEmpty()){
                        Collections.sort(allEvents, compareTimestamp);
                        xTrace.addAll(allEvents);
                        allEvents = new ArrayList<XEvent>();
                    }

                    xTrace = xFactory.createTrace();
                    concept.assignName(xTrace, caseID);
                    xLog.add(xTrace);
                    newTraceID = caseID;
                }

                if(trace.getStartTimestamp() != null){
                    xEvent = createEvent(trace, xFactory, concept, lifecycle, timestamp, resource, false);
                    allEvents.add(xEvent);
                }

                xEvent = createEvent(trace, xFactory, concept, lifecycle, timestamp, resource, true);
                allEvents.add(xEvent);
            }

            if(!allEvents.isEmpty()){
                Collections.sort(allEvents, compareTimestamp);
                xTrace.addAll(allEvents);
            }

            return xLog;
        }



        private XEvent createEvent(LogModel theTrace,XFactory xFactory, XConceptExtension concept, XLifecycleExtension lifecycle,XTimeExtension timestamp, XOrganizationalExtension resource, Boolean isEndTimestamp){

            XEvent xEvent = xFactory.createEvent();
            concept.assignName(xEvent, theTrace.getConcept());

            if(theTrace.getResource() !=null){
                resource.assignResource(xEvent, theTrace.getResource() );
            }

            XAttribute attribute;
            HashMap<String, Timestamp> otherTimestamps = theTrace.getOtherTimestamps();
            for (Map.Entry<String, Timestamp> entry : otherTimestamps.entrySet()) {
                attribute = new XAttributeTimestampImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }

            HashMap<String, String> others = theTrace.getOthers();
            for (Map.Entry<String, String> entry : others.entrySet()) {
                if(entry.getValue() != null && entry.getValue().trim().length() != 0){
                    attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                    xEvent.getAttributes().put(entry.getKey(), attribute);
                }
            }

            if(!isEndTimestamp){
                lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.START);
                timestamp.assignTimestamp(xEvent, theTrace.getStartTimestamp());

            }else{
                lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.COMPLETE);
                timestamp.assignTimestamp(xEvent, theTrace.getTimestamp());
            }

            return  xEvent;
        }


        /**
         * To XES file.
         *
         *	Serialize xLog to XES file.
         *
         * @param xLog the x log
         * @throws FileNotFoundException the file not found exception
         * @throws IOException Signals that an I/O exception has occurred.
         */
        public void toXESfile(XLog xLog, String FileName) throws FileNotFoundException, IOException {
            if(xLog == null) return;


            String FileNameWithoutExtention = FileName.replaceFirst("[.][^.]+$", "");
            XesXmlSerializer serializer = new XesXmlSerializer();
            serializer.serialize(xLog, new FileOutputStream(new File(FileNameWithoutExtention + ".xes")));
            Messagebox.show("Your file has been created!");

        }
    }
