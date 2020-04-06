package org.apromore.plugin.portal.CSVImporterPortal;

import org.apache.commons.lang.StringUtils;
import org.apromore.service.csvimporter.LogSample;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ConstructUserInterface implements Constants {

    private LogSample sample;
    private Window window;
    private Media media;

    private Integer attribWidth = 180;
    private Integer indexColumnWidth = 50;

    private Div popUpBox;
    Button[] formatBtns;

    public ConstructUserInterface(LogSample sample, Media media, Window window) {
        this.sample = sample;
        this.window = window;
        this.media = media;
    }

    public Window getWindow() {
        return window;
    }

    public void setSample(LogSample sample) {
        this.sample = sample;
    }

    void setUpUI(){
        // Set up window size
        if (sample.getHeader().size() > 8) {
            window.setMaximizable(true);
            window.setMaximized(true);
        } else {
            window.setMaximizable(false);
            int size = indexColumnWidth + sample.getHeader().size() * attribWidth + 35;
            window.setWidth(size + "px");
        }
        window.setTitle("CSV Importer - " + media.getName());


        popUpBox = createPopUpTextBox();
        setUpCSVGrid();
        renderGridContent();
        setUpButtons();
    }

    private void setUpCSVGrid() {
        Grid myGrid = (Grid) window.getFellow(myGridId);

        myGrid.getChildren().clear();
        (new Columns()).setParent(myGrid);
        myGrid.getColumns().getChildren().clear();


        // dropdown lists
        Auxhead optionHead = new Auxhead();
        Auxheader index = new Auxheader();
        optionHead.appendChild(index);
        for (Listbox list : createDropDownLists()) {
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


        // set header
        Button[] formatBtns = new Button[sample.getHeader().size()];
        for (int i = 0; i < sample.getHeader().size(); i++) {
            Column newColumn = new Column();
            String label = sample.getHeader().get(i);
            newColumn.setValue(label);
            newColumn.setLabel(label);
            int labelLen = (label.length() * 14) + 20;
            if (labelLen < attribWidth) {
                labelLen = attribWidth;
            }
            newColumn.setWidth(labelLen + "px");
            newColumn.setAlign("center");

            Button formatBtn = new Button();
            hideFormatBtn(formatBtn);
            formatBtn.setIconSclass("z-icon-wrench");
            final int fi = i;
            formatBtn.addEventListener("onClick", event -> {
                Window w = (Window) popUpBox.getFellow("pop_" + fi);
                w.setStyle(w.getStyle().replace("hidden", "visible"));
//                MouseEvent me = (MouseEvent) event;
//                int x = me.getPageX();
//                int y = me.getPageY();
//                w.setLeft((x - 180) + "px");
//                w.setTop((y + 120) + "px");
//                Clients.evalJavaScript("adjustPos(" + fi + ")");
            });

            formatBtns[i] = formatBtn;
            newColumn.appendChild(formatBtn);
            myGrid.getColumns().appendChild(newColumn);
            myGrid.getColumns().setSizable(true);
        }

        int timeStampPos = sample.getMainAttributes().get(sample.getTimestampLabel());
        if (timeStampPos != -1) {
            showFormatBtn(formatBtns[timeStampPos]);
        }
        int startTimeStampPos = sample.getMainAttributes().get(sample.getStartTimestampLabel());
        if (startTimeStampPos != -1) {
            showFormatBtn(formatBtns[startTimeStampPos]);
        }

        for (Map.Entry<Integer, String> entry : sample.getOtherTimeStampsPos().entrySet()) {
            showFormatBtn(formatBtns[entry.getKey()]);
        }

        this.formatBtns = formatBtns;
    }


    private void showFormatBtn(Button myButton){
        myButton.setSclass("ap-csv-importer-format-icon");
    }

    private void hideFormatBtn(Button myButton){
        myButton.setSclass("ap-csv-importer-format-icon ap-hidden");
    }


    void renderGridContent(){
        Grid myGrid = (Grid) window.getFellow(myGridId);
        // set grid model; display the first logSampleSize rows
        ListModelList<String[]> indexedResult = new ListModelList<>();
        for (int i = 0; i < sample.getLines().size(); i++) {
            List<String> withIndex = new ArrayList<>();
            withIndex.add(String.valueOf(i + 1));
            withIndex.addAll(sample.getLines().get(i));
            String[] s = withIndex.toArray(new String[0]);
            indexedResult.add(s);
        }
        if (sample.getLines().size() == logSampleSize) {
            String[] continued = {"...", ""};
            indexedResult.add(continued);
        }
        myGrid.setModel(indexedResult);

        //set grid row renderer
        GridRendererController rowRenderer = new GridRendererController();
        rowRenderer.setAttribWidth(attribWidth);
        myGrid.setRowRenderer(rowRenderer);
    }


    private List<Listbox> createDropDownLists() {

        List<Listbox> menuDropDownLists = new ArrayList<>();
        LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
        String eventAttribute = "Event Attribute";
        String caseAttribute = "Case Attribute";
        String ignore = "ignore";

        menuItems.put(sample.getCaseIdLabel(), "Case ID");
        menuItems.put(sample.getActivityLabel(), "Activity");
        menuItems.put(sample.getTimestampLabel(), "End timestamp");
        menuItems.put(sample.getStartTimestampLabel(), "Start timestamp");
        menuItems.put(sample.getOtherTimestampLabel(), "Other timestamp");
        menuItems.put(sample.getResourceLabel(), "Resource");
        menuItems.put(eventAttribute, "Event Attribute");
        menuItems.put(caseAttribute, "Case Attribute");
        menuItems.put(ignore, "Ignore Attribute");

        // get index of "eventAttribute" item and select it.
        int eventAttributeIndex = new ArrayList<String>(menuItems.keySet()).indexOf(eventAttribute);

        for (int pos = 0; pos < sample.getHeader().size(); pos++) {
            Listbox box = new Listbox();
            box.setMold("select"); // set listBox to select mode
            box.setId(String.valueOf(pos)); // set id of list as column position.
            box.setWidth(AttribWidth - 20 + "px");

            for (Map.Entry<String, String> myItem : menuItems.entrySet()) {
                Listitem item = new Listitem();
                item.setValue(myItem.getKey());
                item.setLabel(myItem.getValue());

                if ((box.getSelectedItem() == null) && (
                        (myItem.getKey().equals(sample.getCaseIdLabel()) && (pos == sample.getMainAttributes().get(sample.getCaseIdLabel()))) ||
                                (myItem.getKey().equals(sample.getActivityLabel()) && (pos == sample.getMainAttributes().get(sample.getActivityLabel()))) ||
                                (myItem.getKey().equals(sample.getTimestampLabel()) && (pos == sample.getMainAttributes().get(sample.getTimestampLabel()))) ||
                                (myItem.getKey().equals(sample.getStartTimestampLabel()) && (pos == sample.getMainAttributes().get(sample.getStartTimestampLabel()))) ||
                                (myItem.getKey().equals(sample.getResourceLabel()) && (pos == sample.getMainAttributes().get(sample.getResourceLabel()))) ||
                                (myItem.getKey().equals(sample.getStartTimestampLabel()) && (sample.getOtherTimeStampsPos().get(pos) != null)) ||
                                (myItem.getKey().equals(caseAttribute) && (sample.getCaseAttributesPos().contains(pos))) ||
                                (myItem.getKey().equals(eventAttribute)))
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
                closePopUpBox(colPos);

                if (selected.equals(sample.getCaseIdLabel()) || selected.equals(sample.getActivityLabel()) || selected.equals(sample.getTimestampLabel()) || selected.equals(sample.getStartTimestampLabel()) || new String(selected).equals(sample.getResourceLabel())) {

                    int oldColPos = sample.getMainAttributes().get(selected);
                    if (oldColPos != -1) {
                        Listbox oldBox = menuDropDownLists.get(oldColPos);
                        oldBox.setSelectedIndex(eventAttributeIndex);
                        removeColPos(oldColPos);
                        closePopUpBox(oldColPos);
                    }

                    if (selected.equals(sample.getTimestampLabel()) || selected.equals(sample.getStartTimestampLabel())) {
                        boolean parsed = sample.isParsable(colPos);
                        if(parsed){
                            sample.getMainAttributes().put(selected, colPos);
                            if (selected.equals(sample.getTimestampLabel())) {
                                sample.setTimestampFormat(null);
                            } else {
                                sample.setStartTsFormat(null);
                            }
                        }
                        openPopUpBox(colPos, parsed);
                    } else {
                        sample.getMainAttributes().put(selected, colPos);
                    }

                } else if (selected.equals(ignore)) {
                    sample.getIgnoredPos().add(colPos);
                } else if (selected.equals(sample.getOtherTimestampLabel())) {
                    boolean parsed = sample.isParsable(colPos);
                    if(parsed){
                        sample.getOtherTimeStampsPos().put(colPos, null);
                    }
                    openPopUpBox(colPos, parsed);
                } else if(selected.equals(caseAttribute)){
                    sample.getCaseAttributesPos().add(colPos);
                }
            });

            menuDropDownLists.add(box);
        }

        return menuDropDownLists;
    }

    private Div createPopUpTextBox() {
        Div popUPBox = (Div) window.getFellow(popUPBoxId);
        if (popUPBox != null) {
            popUPBox.getChildren().clear();
        }
        Popup helpP = (Popup) window.getFellow(popUpHelpId);
        int colNum = sample.getHeader().size();

        for (int pos = 0; pos < colNum; pos++) {
            Window item = new Window();
            item.setId(popupID + pos);
            item.setWidth((attribWidth) + "px");
            item.setMinheight(100);
            item.setClass("p-1");
            item.setBorder("normal");

            item.setStyle("margin-left:" + (pos==0? 15: (pos*AttribWidth) + 15)  + "px; position: absolute; z-index: 10; visibility: hidden;");

            Button sp = new Button();
            sp.setStyle("margin-right:3px; float: right; line-height: 10px; min-height: 5px; padding:3px;");
            sp.setIconSclass("z-icon-times");

            A hidelink = new A();
            hidelink.appendChild(sp);
            sp.addEventListener("onClick", (Event event) -> item.setStyle(item.getStyle().
                    replace("visible", "hidden")));

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
                if (!event.getValue().isEmpty()) {
                    sample.isParsableWithFormat(Integer.parseInt(textbox.getId().replace(textboxID, "")), event.getValue());
                }
            });

            Label check_lbl = new Label();
            check_lbl.setId(labelID + pos);
            item.appendChild(check_lbl);
            item.appendChild(hidelink);
            item.appendChild(textbox);

            popUPBox.appendChild(item);
        }
        popUPBox.clone();
        return popUPBox;
    }


    private void openPopUpBox(Integer pos, boolean parsed) {
        showFormatBtn(formatBtns[pos]);
        Window myPopUp = (Window) popUpBox.getFellow(popupID + pos);
        myPopUp.setStyle(myPopUp.getStyle().replace("hidden", "visible"));

        Label check_lbl = (Label) myPopUp.getFellow(labelID + pos);

        if (parsed) {
            check_lbl.setZclass("greenLabel");
            check_lbl.setValue("Timestamp parsed correctly!");

             Textbox txt = (Textbox) myPopUp.getFellow(textboxID + pos);
             if(txt.getText() == null || txt.getText().isEmpty()){
                 txt.setSclass("ap-hidden");
             }
        } else {
            check_lbl.setZclass("redLabel");
            check_lbl.setValue("Could not parse timestamp!");
        }
    }

    private void closePopUpBox(int pos) {
        Window myPopUp = (Window) popUpBox.getFellow(popupID + pos);
        myPopUp.setStyle(myPopUp.getStyle().replace("visible", "hidden"));
    }

    private void removeColPos(int pos) {
        if (sample.getOtherTimeStampsPos().get(pos) != null) {
            sample.getOtherTimeStampsPos().remove(pos);
        } else if (sample.getIgnoredPos().contains(pos)) {
            sample.getIgnoredPos().remove(Integer.valueOf(pos));
        } else {
            for (Map.Entry<String, Integer> entry : sample.getMainAttributes().entrySet()) {
                if (entry.getValue() == pos) {
                    sample.getMainAttributes().put(entry.getKey(), -1);
                    break;
                }
            }
        }
    }


    private void setUpButtons(){
        // Set up buttons
        Button setOtherAll = (Button) window.getFellow(setOtherAllBtnId);
        setOtherAll.setTooltiptext("Change all Ignore columns to Other.");
        setOtherAll.addEventListener("onClick", event -> sample.setOtherAll(window));

        Button setIgnoreAll = (Button) window.getFellow(setIgnoreAllBtnId);
        setIgnoreAll.setTooltiptext("Change all Other columns to Ignore.");
        setIgnoreAll.addEventListener("onClick", event -> sample.setIgnoreAll(window));

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





    // TODO: Needs careful review
    private void convertToXes() {
//        String charset = getFileEncoding();
//
//        try (CSVReader reader = newCSVReader(charset)) {
//            LogModel xesModel = csvImporterLogic.prepareXesModel(reader, sample, maxErrorFraction);
//
//            if (xesModel.getErrorCount() > 0) {
//                String notificationMessage;
//                notificationMessage = "Imported: " + xesModel.getLineCount() + " row(s), with " + xesModel.getErrorCount() + " invalid row(s) being amended.  \n\n" +
//                        "Invalid rows: \n";
//
//                for (int i = 0; i < Math.min(xesModel.getInvalidRows().size(), 5); i++) {
//                    notificationMessage = notificationMessage + xesModel.getInvalidRows().get(i) + "\n";
//                }
//
//                if (xesModel.getInvalidRows().size() > 5) {
//                    notificationMessage = notificationMessage + "\n ...";
//                }
//                Messagebox.show(notificationMessage
//                        , "Invalid CSV File",
//                        new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
//                        new String[]{"Download Error Report", "Cancel"}, Messagebox.ERROR, null,
//                        (EventListener) evt -> {
//                            if (evt.getName().equals("onOK")) {
//                                File tempFile = File.createTempFile("Error_Report", ".txt");
//                                FileWriter writer = new FileWriter(tempFile);
//                                for (String str : xesModel.getInvalidRows()) {
//                                    writer.write(str + System.lineSeparator());
//                                }
//                                writer.close();
//                                Filedownload.save(new FileInputStream(tempFile),
//                                        "text/plain; charset-UTF-8", "Error_Report_CSV.txt");
//                            }
//                        });
//            }
//
//            if (xesModel.getErrorCheck()) {
//                Messagebox.show("Invalid fields detected. \nSelect Skip rows to upload" +
//                                " log by skipping all rows " + "containing invalid fields.\n Select Skip " +
//                                "columns upload log by skipping the entire columns " + "containing invalid fields.\n ",
//                        "Confirm Dialog",
//                        new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.IGNORE, Messagebox.Button.CANCEL},
//                        new String[]{"Skip rows", "Skip columns", "Cancel"},
//                        Messagebox.QUESTION, null, (EventListener) evt -> {
//                            if (evt.getName().equals("onOK")) {
//                                for (int i = 0; i < xesModel.getRows().size(); i++) {
//                                    for (Map.Entry<String, Timestamp> entry : xesModel.getRows().get(i).getOtherTimestamps().entrySet()) {
//                                        if (entry.getKey() == null) {
//                                            continue;
//                                        }
//                                        long tempLong = entry.getValue().getTime();
//                                        java.util.Calendar cal = java.util.Calendar.getInstance();
//                                        cal.setTimeInMillis(tempLong);
//                                        if (cal.get(Calendar.YEAR) == 1900) {
//                                            System.out.println("Invalid timestamp. Entry Removed.");
//                                            xesModel.getRows().remove(i);
//                                        }
//                                    }
//                                }
//
//                                Messagebox.show("Hello Ok!");
//                                // create XES file
//                                XLog xlog = xesModel.getXLog();
//                                if (xlog != null) {
//                                    saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""));
//                                }
//
//                                window.invalidate();
//                                window.detach();
//
//                            } else if (evt.getName().equals("onIgnore")) {
//                                for (int i = 0; i < xesModel.getRows().size(); i++) {
//                                    xesModel.getRows().get(i).setOtherTimestamps(null);
//                                }
//                                Messagebox.show("Hello Ignore!");
//                                // create XES file
//                                XLog xlog = xesModel.getXLog();
//                                if (xlog != null) {
//                                    saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""));
//                                }
//
//                                window.invalidate();
//                                window.detach();
//                            } else {
//                                //
//                            }
//                        });
//            } else {
//                Messagebox.show("Total number of lines processed: " + xesModel.getLineCount() + "\n Your file " +
//                        "has been imported.");
//                XLog xlog = xesModel.getXLog();
//                if (xlog != null) {
//                    saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""));
//                }
//
//                window.invalidate();
//                window.detach();
//            }
//
//        } catch (InvalidCSVException e) {
//            if (e.getInvalidRows() == null) {
//                Messagebox.show(e.getMessage(), "Invalid CSV File", Messagebox.OK, Messagebox.ERROR);
//
//            } else {
//                Messagebox.show(e.getMessage(), "Invalid CSV File",
//                        new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
//                        new String[]{"Download Error Report", "Cancel"}, Messagebox.ERROR, null,
//                        new EventListener() {
//                            public void onEvent(Event evt) throws Exception {
//                                if (evt.getName().equals("onOK")) {
//                                    File tempFile = File.createTempFile("Error_Report", ".txt");
//                                    try (FileWriter writer = new FileWriter(tempFile)) {
//                                        for (String str : e.getInvalidRows()) {
//                                            writer.write(str + System.lineSeparator());
//                                        }
//                                        Filedownload.save(new FileInputStream(tempFile),
//                                                "text/plain; charset-UTF-8", "Error_Report_CSV.txt");
//
//                                    } finally {
//                                        tempFile.delete();
//                                    }
//                                }
//                            }
//                        }
//                );
//            }
//        } catch (IOException e) {
//            Messagebox.show("Failed to read file: " + e, "Error", Messagebox.OK, Messagebox.ERROR);
//            e.printStackTrace();
//        } catch (Exception e) {
//            Messagebox.show("Failed to save file: " + e, "Error", Messagebox.OK, Messagebox.ERROR);
//            e.printStackTrace();
//        }
    }

//    private void saveLog(XLog xlog, String name) throws Exception {
//
//        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        eventLogService.exportToStream(outputStream, xlog);
//
//        int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();
//
//        eventLogService.importLog(
//                portalContext.getCurrentUser().getUsername(),
//                folderId,
//                name,
//                new ByteArrayInputStream(outputStream.toByteArray()),
//                "xes.gz",
//                "",  // domain
//                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
//                isLogPublic  // public?
//        );
//
//        portalContext.refreshContent();
//    }

}
