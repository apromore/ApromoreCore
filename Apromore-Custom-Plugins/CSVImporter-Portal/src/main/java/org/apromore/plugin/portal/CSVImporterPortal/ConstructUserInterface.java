package org.apromore.plugin.portal.CSVImporterPortal;

import com.opencsv.CSVReader;
import org.apache.commons.lang.StringUtils;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.csvimporter.*;
import org.deckfour.xes.model.XLog;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.xml.datatype.DatatypeFactory;
import java.io.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.*;


public class ConstructUserInterface implements Constants {

    private LogSample sample;
    private Window window;
    private Media media;

    private Div popUpBox;
    Button[] formatBtns;
    List<Listbox> dropDownLists;
    private CSVImporterLogic csvImporterLogic;
    PortalContext portalContext;
    EventLogService eventLogService;
    private boolean isLogPublic;
    public ConstructUserInterface(LogSample sample, Media media, Window window, CSVImporterLogic csvImporterLogic, EventLogService eventLogService, PortalContext portalContext, boolean isLogPublic) {
        this.sample = sample;
        this.window = window;
        this.media = media;
        this.csvImporterLogic = csvImporterLogic;
        this.portalContext = portalContext;
        this.eventLogService = eventLogService;
        this.isLogPublic= isLogPublic;
    }


    void setUpUI(){
        // Set up window size
        if (sample.getHeader().size() > 8) {
            window.setMaximizable(true);
            window.setMaximized(true);
        } else {
            window.setMaximizable(false);
            int size = indexColumnWidth + sample.getHeader().size() * columnWidth + 35;
            window.setWidth(size + "px");
        }
        window.setTitle("CSV Importer - " + media.getName());


        setDropDownLists();
        setCSVGrid();
        renderGridContent();
        setPopUpTextBox();
        setButtons();
    }

    private void setCSVGrid() {
        Grid myGrid = (Grid) window.getFellow(myGridId);

        myGrid.getChildren().clear();
        (new Columns()).setParent(myGrid);
        myGrid.getColumns().getChildren().clear();


        // dropdown lists
        Auxhead optionHead = new Auxhead();
        Auxheader index = new Auxheader();
        optionHead.appendChild(index);
        for (Listbox list : dropDownLists) {
            Auxheader listHeader = new Auxheader();
            listHeader.appendChild(list);
            optionHead.appendChild(listHeader);
        }
        myGrid.appendChild(optionHead);


        // index column
        Column indexCol = new Column();
        indexCol.setWidth(indexColumnWidth + "px");
        indexCol.setValue("");
        indexCol.setLabel("#");
        indexCol.setAlign("center");
        myGrid.getColumns().appendChild(indexCol);


        // set columns
        Button[] formatBtns = new Button[sample.getHeader().size()];
        for (int pos = 0; pos < sample.getHeader().size(); pos++) {
            Column newColumn = new Column();
            String label = sample.getHeader().get(pos);
            newColumn.setValue(label);
            newColumn.setLabel(label);
            int labelLen = (label.length() * 14) + 20;
            if (labelLen < columnWidth) {
                labelLen = columnWidth;
            }
            newColumn.setWidth(labelLen + "px");
            newColumn.setAlign("center");

            Button formatBtn = new Button();
            hideFormatBtn(formatBtn);
            formatBtn.setIconSclass("z-icon-wrench");
            final int fi = pos;
            formatBtn.addEventListener("onClick", event -> {
                openPopUpBox(fi);

//                MouseEvent me = (MouseEvent) event;
//                int x = me.getPageX();
//                int y = me.getPageY();
//                w.setLeft((x - 180) + "px");
//                w.setTop((y + 120) + "px");
            });
            formatBtns[pos] = formatBtn;

            newColumn.appendChild(formatBtn);
            myGrid.getColumns().appendChild(newColumn);
            myGrid.getColumns().setSizable(true);
        }

        this.formatBtns = formatBtns;
    }

    void renderGridContent(){
        Grid myGrid = (Grid) window.getFellow(myGridId);
        ListModelList<String[]> indexedResult = new ListModelList<>();

        int index = 1;
        for (List<String> myLine: sample.getLines()) {
            List<String> withIndex = new ArrayList<>();
            withIndex.add(String.valueOf(index));
            index++;
            withIndex.addAll(myLine);
            String[] s = withIndex.toArray(new String[0]);
            indexedResult.add(s);
        }
        if(logSampleSize <= sample.getLines().size()){
            indexedResult.add(new String[]{"..", "...", "...."});
        }
        myGrid.setModel(indexedResult);

        //set grid row renderer
        GridRendererController rowRenderer = new GridRendererController();
        rowRenderer.setAttribWidth(columnWidth);
        myGrid.setRowRenderer(rowRenderer);
    }

    private void setPopUpTextBox() {
        Div popUPBox = (Div) window.getFellow(popUPBoxId);
        if (popUPBox != null) {
            popUPBox.getChildren().clear();
        }
        Popup helpP = (Popup) window.getFellow(popUpHelpId);

        for (int pos = 0; pos < sample.getHeader().size(); pos++) {
            Window item = new Window();
            item.setId(popupID + pos);
            item.setWidth((columnWidth) + "px");
            item.setMinheight(100);
            item.setClass("p-1");
            item.setBorder("normal");
            item.setStyle("position: fixed; z-index: 10; visibility: hidden;");

            Button sp = new Button();
            sp.setStyle("margin-right:3px; float: right; line-height: 10px; min-height: 5px; padding:3px;");
            sp.setIconSclass("z-icon-times");

            A hidelink = new A();
            hidelink.appendChild(sp);
            sp.addEventListener("onClick", (Event event) -> item.setStyle(item.getStyle().replace("visible", "hidden")));

            Label check_lbl = new Label();
            check_lbl.setId(labelID + pos);
            String redLabelCSS = "redLabel";
            String greenLabelCSS = "greenLabel";
            String couldNotParse = "Could not parse timestamp!";
            String parsedMessage = "Parsed correctly!";

            check_lbl.setZclass(redLabelCSS);
            check_lbl.setValue(couldNotParse);

            Textbox textbox = new Textbox();
            textbox.setId(textboxID + pos);
            textbox.setWidth("98%");
            textbox.setPlaceholder("dd-MM-yyyy HH:mm:ss");
            textbox.setPopup(helpP);
            textbox.setClientAttribute("spellcheck", "false");
            textbox.setPopupAttributes(helpP, "after_start", "", "", "toggle");

            textbox.addEventListener("onChanging", (InputEvent event) -> {
                if (StringUtils.isBlank(event.getValue()) || event.getValue().length() < 6) {
                    textbox.setPlaceholder("Specify timestamp format");
                }
                else {
                    int colPos = Integer.parseInt(textbox.getId().replace(textboxID, ""));
                    String format = event.getValue();

                    if(sample.isParsableWithFormat(colPos, format)){

                        check_lbl.setZclass(greenLabelCSS);
                        check_lbl.setValue(parsedMessage);

                        Listbox box = dropDownLists.get(colPos);
                        String timestampLabel = box.getSelectedItem().getValue();
                        updateTimestampPos(colPos, timestampLabel, format);
                   }else{
                        check_lbl.setZclass(redLabelCSS);
                        check_lbl.setValue(couldNotParse);
                   }
                }
            });

            item.appendChild(check_lbl);
            item.appendChild(hidelink);
            item.appendChild(textbox);

            popUPBox.appendChild(item);
        }
        popUPBox.clone();

        this.popUpBox = popUPBox;
    }

    private void setDropDownLists() {

        List<Listbox> menuDropDownLists = new ArrayList<>();
        LinkedHashMap<String, String> menuItems = new LinkedHashMap<>();

        menuItems.put(sample.getCaseIdLabel(), "Case ID");
        menuItems.put(sample.getActivityLabel(), "Activity");
        menuItems.put(sample.getTimestampLabel(), "End timestamp");
        menuItems.put(sample.getStartTimestampLabel(), "Start timestamp");
        menuItems.put(sample.getOtherTimestampLabel(), "Other timestamp");
        menuItems.put(sample.getResourceLabel(), "Resource");
        menuItems.put(caseAttributeLabel, "Case Attribute");
        menuItems.put(eventAttributeLabel, "Event Attribute");
        menuItems.put(ignoreLabel, "Ignore Attribute");

        // get index of "eventAttribute" item and select it.
        int eventAttributeIndex = new ArrayList<String>(menuItems.keySet()).indexOf(eventAttributeLabel);

        for (int pos = 0; pos < sample.getHeader().size(); pos++) {
            Listbox box = new Listbox();
            box.setMold("select"); // set listBox to select mode
            box.setId(String.valueOf(pos)); // set id of list as column position.
            box.setWidth(columnWidth - 20 + "px");

            for (Map.Entry<String, String> myItem : menuItems.entrySet()) {
                Listitem item = new Listitem();
                item.setValue(myItem.getKey());
                item.setLabel(myItem.getValue());
                item.setId(myItem.getKey());

                if ((box.getSelectedItem() == null) && (
                        (myItem.getKey().equals(sample.getCaseIdLabel()) && (pos == sample.getUniqueAttributes().get(sample.getCaseIdLabel()))) ||
                                (myItem.getKey().equals(sample.getActivityLabel()) && (pos == sample.getUniqueAttributes().get(sample.getActivityLabel()))) ||
                                (myItem.getKey().equals(sample.getTimestampLabel()) && (pos == sample.getUniqueAttributes().get(sample.getTimestampLabel()))) ||
                                (myItem.getKey().equals(sample.getStartTimestampLabel()) && (pos == sample.getUniqueAttributes().get(sample.getStartTimestampLabel()))) ||
                                (myItem.getKey().equals(sample.getResourceLabel()) && (pos == sample.getUniqueAttributes().get(sample.getResourceLabel()))) ||
                                (myItem.getKey().equals(sample.getOtherTimestampLabel()) && (sample.getOtherTimestamps().get(pos) != null)) ||
                                (myItem.getKey().equals(caseAttributeLabel) && (sample.getCaseAttributesPos().contains(pos))) ||
                                (myItem.getKey().equals(eventAttributeLabel) && (sample.getEventAttributesPos().contains(pos))))
                ) {
                    item.setSelected(true);
                }

                box.appendChild(item);
            }


            box.addEventListener("onSelect", (Event event) -> {
                // get selected index, and check if it is one of main attributes
                String selected = box.getSelectedItem().getValue();
                int colPos = Integer.parseInt(box.getId());
                resetColPos(colPos);
                closePopUpBox(colPos);
                hideFormatBtn(formatBtns[colPos]);

                if (selected.equals(sample.getCaseIdLabel()) || selected.equals(sample.getActivityLabel()) || selected.equals(sample.getTimestampLabel()) || selected.equals(sample.getStartTimestampLabel()) || new String(selected).equals(sample.getResourceLabel())) {

                    int oldColPos = sample.getUniqueAttributes().get(selected);
                    if (oldColPos != -1) {
                        Listbox oldBox = menuDropDownLists.get(oldColPos);
                        oldBox.setSelectedIndex(eventAttributeIndex);
                        resetColPos(oldColPos);
                        closePopUpBox(oldColPos);
                        hideFormatBtn(formatBtns[oldColPos]);
                    }

                    if (selected.equals(sample.getTimestampLabel()) || selected.equals(sample.getStartTimestampLabel())) {
                        if(sample.isParsable(colPos)){
                            updateTimestampPos(colPos, selected, null);
                        }else{
                            showFormatBtn(formatBtns[colPos]);
                            openPopUpBox(colPos);
                        }
                    } else {
                        sample.getUniqueAttributes().put(selected, colPos);
                    }

                } else if (selected.equals(ignoreLabel)) {
                    sample.getIgnoredPos().add(colPos);
                } else if (selected.equals(sample.getOtherTimestampLabel())) {
                    if(sample.isParsable(colPos)){
                        sample.getOtherTimestamps().put(colPos, null);
                    }else{
                        showFormatBtn(formatBtns[colPos]);
                        openPopUpBox(colPos);
                    }
                } else if(selected.equals(caseAttributeLabel)){
                    sample.getCaseAttributesPos().add(colPos);
                }
            });

            menuDropDownLists.add(box);
        }

        this.dropDownLists = menuDropDownLists;
    }

    private void updateTimestampPos(int pos, String timestampLabel, String format){
        if (timestampLabel.equals(sample.getTimestampLabel())) {
            sample.getUniqueAttributes().put(timestampLabel, pos);
            sample.setEndTimestampFormat(format);
        } else if (timestampLabel.equals(sample.getStartTimestampLabel())) {
            sample.getUniqueAttributes().put(timestampLabel, pos);
            sample.setStartTimestampFormat(format);
        } else if (timestampLabel.equals(sample.getOtherTimestampLabel())) {
            sample.getOtherTimestamps().put(pos, format);
        }
    }

    private void openPopUpBox(int pos) {
        Window myPopUp = (Window) popUpBox.getFellow(popupID + pos);
        myPopUp.setStyle(myPopUp.getStyle().replace("hidden", "visible"));
        Clients.evalJavaScript("adjustPos(" + pos + ")");
    }

    private void closePopUpBox(int pos) {
        Window myPopUp = (Window) popUpBox.getFellow(popupID + pos);
        myPopUp.setStyle(myPopUp.getStyle().replace("visible", "hidden"));
    }

    private void showFormatBtn(Button myButton){
        myButton.setSclass("ap-csv-importer-format-icon");
    }

    private void hideFormatBtn(Button myButton){
        myButton.setSclass("ap-csv-importer-format-icon ap-hidden");
    }

    private void resetColPos(int pos) {
        if (sample.getOtherTimestamps().get(pos) != null) {
            sample.getOtherTimestamps().remove(pos);
        } else if (sample.getIgnoredPos().contains(pos)) {
            sample.getIgnoredPos().remove(Integer.valueOf(pos));
        } else {
            for (Map.Entry<String, Integer> entry : sample.getUniqueAttributes().entrySet()) {
                if (entry.getValue() == pos) {
                    sample.getUniqueAttributes().put(entry.getKey(), -1);
                    break;
                }
            }
        }
    }

    private void setButtons(){

        Button toEventAttributes = (Button) window.getFellow(ignoreToEventBtnId);
        toEventAttributes.setTooltiptext("Change all Ignored Attributes to Event Attributes");
        toEventAttributes.addEventListener("onClick", event ->
                ignoreToEvent()
        );

        Button ignoreEventAttributes = (Button) window.getFellow(eventToIgnoreBtnId);
        ignoreEventAttributes.setTooltiptext("Ignore all Event Attributes");
        ignoreEventAttributes.addEventListener("onClick", event ->
                eventToIgnore()
        );

        Button toXESButton = (Button) window.getFellow(toXESBtnId);
        toXESButton.setDisabled(false);
        toXESButton.addEventListener("onClick", event -> {
            convertToXes();
        });

        Button cancelButton = (Button) window.getFellow(cancelBtnId);
        cancelButton.addEventListener("onClick", event -> {
            window.invalidate();
            window.detach();
        });
    }

    public void ignoreToEvent() {
        Listbox lb = (Listbox) window.getFellow(String.valueOf(0));
        int eventAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(eventAttributeLabel));
        int ignoreAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(ignoreLabel));

        for (int pos = 0; pos < sample.getHeader().size(); pos++) {
            lb = (Listbox) window.getFellow(String.valueOf(pos));

            if (lb.getSelectedIndex() == ignoreAttributeIndex) {
                sample.getIgnoredPos().remove(Integer.valueOf(pos));
                sample.getEventAttributesPos().add(pos);
                lb.setSelectedIndex(eventAttributeIndex);
            }
        }
    }

    public void eventToIgnore() {
        Listbox lb = (Listbox) window.getFellow(String.valueOf(0));
        int eventAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(eventAttributeLabel));
        int ignoreAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(ignoreLabel));

        for (int pos = 0; pos < sample.getHeader().size(); pos++) {
            lb = (Listbox) window.getFellow(String.valueOf(pos));
            if (lb.getSelectedIndex() == eventAttributeIndex) {
                sample.getEventAttributesPos().remove(Integer.valueOf(pos));
                sample.getIgnoredPos().add(pos);
                lb.setSelectedIndex(ignoreAttributeIndex);
            }
        }
    }

    public Window getWindow() {
        return window;
    }

    public void setSample(LogSample sample) {
        this.sample = sample;
    }


    private void convertToXes() {

        // If any of the mandatory fields are missing show alert message to the user and return
        StringBuilder headNOTDefined = validateUniqueAttributes(sample.getUniqueAttributes());
        if (headNOTDefined.length() != 0) {
            Messagebox.show(headNOTDefined.toString(), "Missing fields!", Messagebox.OK, Messagebox.ERROR);
        } else {
            String charset = "UTF-8";
            ConstructCSVSample CSVSample = new ConstructCSVSample();

            try (CSVReader reader = CSVSample.newCSVReader(charset)) {
                LogModel xesModel = csvImporterLogic.prepareXesModel(reader, sample);
                List<LogErrorReport> errorReport = xesModel.getLogErrorReport();

                if(errorReport.isEmpty()){
                    saveLog(xesModel);
                } else {

                    // Show pop up





                }






//                if (xesModel.getErrorCount() > 0) {
//                    String notificationMessage;
//                    notificationMessage = "Imported: " + xesModel.getLineCount() + " row(s), with " + xesModel.getErrorCount() + " invalid row(s) being amended.  \n\n" +
//                            "Invalid rows: \n";
//
//                    for (int i = 0; i < Math.min(xesModel.getInvalidRows().size(), 5); i++) {
//                        notificationMessage = notificationMessage + xesModel.getInvalidRows().get(i) + "\n";
//                    }
//
//                    if (xesModel.getInvalidRows().size() > 5) {
//                        notificationMessage = notificationMessage + "\n ...";
//                    }
//                    Messagebox.show(notificationMessage
//                            , "Invalid CSV File",
//                            new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
//                            new String[]{"Download Error Report", "Cancel"}, Messagebox.ERROR, null,
//                            (EventListener) evt -> {
//                                if (evt.getName().equals("onOK")) {
//                                    File tempFile = File.createTempFile("Error_Report", ".txt");
//                                    FileWriter writer = new FileWriter(tempFile);
//                                    for (String str : xesModel.getInvalidRows()) {
//                                        writer.write(str + System.lineSeparator());
//                                    }
//                                    writer.close();
//                                    Filedownload.save(new FileInputStream(tempFile),
//                                            "text/plain; charset-UTF-8", "Error_Report_CSV.txt");
//                                }
//                            });
//                }
//
//                if (xesModel.getErrorCheck()) {
//                    Messagebox.show("Invalid fields detected. \nSelect Skip rows to upload" +
//                                    " log by skipping all rows " + "containing invalid fields.\n Select Skip " +
//                                    "columns upload log by skipping the entire columns " + "containing invalid fields.\n ",
//                            "Confirm Dialog",
//                            new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.IGNORE, Messagebox.Button.CANCEL},
//                            new String[]{"Skip rows", "Skip columns", "Cancel"},
//                            Messagebox.QUESTION, null, (EventListener) evt -> {
//                                if (evt.getName().equals("onOK")) {
//                                    for (int i = 0; i < xesModel.getRows().size(); i++) {
//                                        for (Map.Entry<String, Timestamp> entry : xesModel.getRows().get(i).getOtherTimestamps().entrySet()) {
//                                            if (entry.getKey() == null) {
//                                                continue;
//                                            }
//                                            long tempLong = entry.getValue().getTime();
//                                            java.util.Calendar cal = java.util.Calendar.getInstance();
//                                            cal.setTimeInMillis(tempLong);
//                                            if (cal.get(Calendar.YEAR) == 1900) {
//                                                System.out.println("Invalid timestamp. Entry Removed.");
//                                                xesModel.getRows().remove(i);
//                                            }
//                                        }
//                                    }
//
//                                    Messagebox.show("Hello Ok!");
//                                    // create XES file
//                                    XLog xlog = xesModel.getXLog();
//                                    if (xlog != null) {
//                                        saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""));
//                                    }
//
//                                    window.invalidate();
//                                    window.detach();
//
//                                } else if (evt.getName().equals("onIgnore")) {
//                                    for (int i = 0; i < xesModel.getRows().size(); i++) {
//                                        xesModel.getRows().get(i).setOtherTimestamps(null);
//                                    }
//                                    Messagebox.show("Hello Ignore!");
//                                    // create XES file
//                                    XLog xlog = xesModel.getXLog();
//                                    if (xlog != null) {
//                                        saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""));
//                                    }
//
//                                    window.invalidate();
//                                    window.detach();
//                                } else {
//                                    //
//                                }
//                            });
//                } else {
//
//                }

            } catch (InvalidCSVException e) {
//                if (e.getInvalidRows() == null) {
//                    Messagebox.show(e.getMessage(), "Invalid CSV File", Messagebox.OK, Messagebox.ERROR);
//
//                } else {
//                    Messagebox.show(e.getMessage(), "Invalid CSV File",
//                            new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
//                            new String[]{"Download Error Report", "Cancel"}, Messagebox.ERROR, null,
//                            new EventListener() {
//                                public void onEvent(Event evt) throws Exception {
//                                    if (evt.getName().equals("onOK")) {
//                                        File tempFile = File.createTempFile("Error_Report", ".txt");
//                                        try (FileWriter writer = new FileWriter(tempFile)) {
//                                            for (String str : e.getInvalidRows()) {
//                                                writer.write(str + System.lineSeparator());
//                                            }
//                                            Filedownload.save(new FileInputStream(tempFile),
//                                                    "text/plain; charset-UTF-8", "Error_Report_CSV.txt");
//
//                                        } finally {
//                                            tempFile.delete();
//                                        }
//                                    }
//                                }
//                            }
//                    );
//                }



            } catch (IOException e) {
                Messagebox.show("Failed to read file: " + e, "Error", Messagebox.OK, Messagebox.ERROR);
                e.printStackTrace();
            } catch (Exception e) {
                Messagebox.show("Failed to save file: " + Arrays.toString(e.getStackTrace()), "Error", Messagebox.OK, Messagebox.ERROR);
                e.printStackTrace();
            }
        }
    }


    private StringBuilder validateUniqueAttributes(Map<String, Integer> mainAttributes) {
        String[] attributesToCheck = {sample.getCaseIdLabel(), sample.getActivityLabel(), sample.getTimestampLabel()};
        StringBuilder importMessage = new StringBuilder();
        String messingField;
        String mess;
        for (String attribute : attributesToCheck) {
            if (mainAttributes.get(attribute) == -1) {
                if (attribute.equals(sample.getCaseIdLabel())) {
                    messingField = "Case ID";
                } else if (attribute.equals(sample.getActivityLabel())) {
                    messingField = "Activity";
                } else {
                    messingField = "End Timestamp";
                }
                mess = "No attribute has been selected as " + messingField + "!";
                if (importMessage.length() == 0) {
                    importMessage.append(mess);
                } else {
                    importMessage.append(System.lineSeparator()).append(mess);
                }
            }
        }
        return importMessage;
    }


    private void saveLog(LogModel logModel) throws Exception {
        XLog xlog = logModel.getXLog();
        String name = media.getName().replaceFirst("[.][^.]+$", "");
        if (xlog != null) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            eventLogService.exportToStream(outputStream, xlog);

            int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

            eventLogService.importLog(
                    portalContext.getCurrentUser().getUsername(),
                    folderId,
                    name,
                    new ByteArrayInputStream(outputStream.toByteArray()),
                    "xes.gz",
                    "",  // domain
                    DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                    isLogPublic  // public?
            );

            window.invalidate();
            window.detach();

            Messagebox.show("Total number of lines processed: " + logModel.getRows().size() + "\n Your file has been imported successfully!");

            portalContext.refreshContent();
        }
    }



    private void errorEncountered(LogModel xesModel) throws IOException {

        Window errorPopUp = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/error.zul", null, null);


        Button toEventAttributes = (Button) errorPopUp.getFellow("downloadErrorLog");
        toEventAttributes.addEventListener("onClick", event ->
                downloadErrorLog()
        );

//        List<Integer> invalidOthertimestamp = new ArrayList<>();
//        List<LogErrorReport> errorReport = xesModel.getLogErrorReport();
//        for (int othertimestampPos : sample.getOtherTimestamps().keySet()) {
//            if(errorReport.stream().anyMatch(p -> p.getColumnIndex() == othertimestampPos)){
//                invalidOthertimestamp.add(othertimestampPos);
//            }
//        }
//        Messagebox.Button[] buttons;
//        String [] buttonsLable;
//
//        if(!invalidOthertimestamp.isEmpty()){
//            buttons =  new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.IGNORE, Messagebox.Button.RETRY, Messagebox.Button.CANCEL};
//            buttonsLable = new String[]{"Download Error Report", "Cancel"};
//        }else{
//            buttons=  new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.IGNORE, Messagebox.Button.CANCEL};
//
//        }

    }

    private void downloadErrorLog() throws IOException {

        File tempFile = File.createTempFile("Error_Report", ".txt");
        FileWriter writer = new FileWriter(tempFile);
//        for (String str : xesModel.getInvalidRows()) {
//            writer.write(str + System.lineSeparator());
//        }
        writer.close();
        Filedownload.save(new FileInputStream(tempFile),
                "text/plain; charset-UTF-8", "Error_Report_CSV.txt");

    }

}
