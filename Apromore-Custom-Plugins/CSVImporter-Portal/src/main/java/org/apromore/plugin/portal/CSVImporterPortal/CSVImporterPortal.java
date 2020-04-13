/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 * Copyright (C) 2019 - 2020 The University of Tartu.
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

package org.apromore.plugin.portal.CSVImporterPortal;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.commons.lang.StringUtils;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.csvimporter.*;
import org.deckfour.xes.model.XLog;
import org.springframework.stereotype.Component;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@Component("csvImporterPortalPlugin")
public class CSVImporterPortal implements FileImporterPlugin, Constants {

    @Inject
    private CSVImporterLogic csvImporterLogic;
    @Inject
    private EventLogService eventLogService;


    private Window window;
    private Media media;
    private PortalContext portalContext;

    private LogSample sample;
    private LogModel xesModel;

    private Div popUpBox;
    private Button[] formatBtns;
    private List<Listbox> dropDownLists;

    private boolean isLogPublic;

    public void setCsvImporterLogic(CSVImporterLogic newCSVImporterLogic) {
        this.csvImporterLogic = newCSVImporterLogic;
    }

    public void setEventLogService(EventLogService newEventLogService) {
        this.eventLogService = newEventLogService;
    }

    @Override
    public Set<String> getFileExtensions() {
        return new HashSet<>(Collections.singletonList("csv"));
    }

    @Override
    public void importFile(Media media, PortalContext portalContext, boolean isLogPublic) {
        if (!Arrays.asList(allowedExtensions).contains(media.getFormat())) {
            Messagebox.show("Please select CSV file!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        this.media = media;
        this.portalContext = portalContext;
        this.isLogPublic = isLogPublic;
        this.xesModel = null;

        CSVFileReader CSVReader = new CSVFileReader();
        try {
            this.window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/csvimporter.zul", null, null);
            Combobox setEncoding = (Combobox) window.getFellow(setEncodingId);
            setEncoding.setModel(new ListModelList<>(fileEncoding));
            setEncoding.addEventListener("onSelect", event -> {
                        CSVReader csvReader = CSVReader.newCSVReader(media, getFileEncoding());
                        if (csvReader != null) {
                            this.sample = csvImporterLogic.sampleCSV(csvReader, logSampleSize);
                            if (sample != null) setUpUI();
                        }
                    }
            );

            CSVReader csvReader = CSVReader.newCSVReader(media, getFileEncoding());
            if (csvReader != null) {
                this.sample = csvImporterLogic.sampleCSV(csvReader, logSampleSize);
                if (sample != null) setUpUI();
            }

            window.doModal();
        } catch (Exception e) {
            Messagebox.show("Failed to read the log!" + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
            window.detach();
            window.invalidate();
        }
    }

    private String getFileEncoding() {
        Combobox setEncoding = (Combobox) window.getFellow(setEncodingId);
        return setEncoding.getValue().contains(" ")
                ? setEncoding.getValue().substring(0, setEncoding.getValue().indexOf(' '))
                : setEncoding.getValue();
    }

    private void setUpUI() {
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
        setPopUpFormatBox();
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

    private void renderGridContent() {
        Grid myGrid = (Grid) window.getFellow(myGridId);
        ListModelList<String[]> indexedResult = new ListModelList<>();

        int index = 1;
        for (List<String> myLine : sample.getLines()) {
            List<String> withIndex = new ArrayList<>();
            withIndex.add(String.valueOf(index));
            index++;
            withIndex.addAll(myLine);
            String[] s = withIndex.toArray(new String[0]);
            indexedResult.add(s);
        }
        if (logSampleSize <= sample.getLines().size()) {
            indexedResult.add(new String[]{"..", "...", "...."});
        }
        myGrid.setModel(indexedResult);

        //set grid row renderer
        GridRendererController rowRenderer = new GridRendererController();
        rowRenderer.setAttribWidth(columnWidth);
        myGrid.setRowRenderer(rowRenderer);
    }

    private void setPopUpFormatBox() {
        Div popUPBox = (Div) window.getFellow(popUpDivId);
        if (popUPBox != null) {
            popUPBox.getChildren().clear();
        }
        Popup helpP = (Popup) window.getFellow(popUpHelpId);

        for (int pos = 0; pos < sample.getHeader().size(); pos++) {
            Window item = new Window();
            item.setId(popUpFormatWindowId + pos);
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
            check_lbl.setId(popUpLabelId + pos);
            String redLabelCSS = "redLabel";
            String greenLabelCSS = "greenLabel";
            String couldNotParse = "Could not parse timestamp!";
            String parsedMessage = "Parsed correctly!";

            check_lbl.setZclass(redLabelCSS);
            check_lbl.setValue(couldNotParse);

            Textbox textbox = new Textbox();
            textbox.setId(popUpTextBoxId + pos);
            textbox.setWidth("98%");
            textbox.setPlaceholder("dd-MM-yyyy HH:mm:ss");
            textbox.setPopup(helpP);
            textbox.setClientAttribute("spellcheck", "false");
            textbox.setPopupAttributes(helpP, "after_start", "", "", "toggle");

            textbox.addEventListener("onChanging", (InputEvent event) -> {
                if (StringUtils.isBlank(event.getValue()) || event.getValue().length() < 6) {
                    textbox.setPlaceholder("Specify timestamp format");
                } else {
                    int colPos = Integer.parseInt(textbox.getId().replace(popUpTextBoxId, ""));
                    String format = event.getValue();

                    if (sample.isParsableWithFormat(colPos, format)) {

                        check_lbl.setZclass(greenLabelCSS);
                        check_lbl.setValue(parsedMessage);

                        Listbox box = dropDownLists.get(colPos);
                        String timestampLabel = box.getSelectedItem().getValue();
                        resetSelect(colPos);
                        updateTimestampPos(colPos, timestampLabel, format);
                    } else {
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

        menuItems.put(caseIdLabel, "Case ID");
        menuItems.put(activityLabel, "Activity");
        menuItems.put(endTimestampLabel, "End timestamp");
        menuItems.put(startTimestampLabel, "Start timestamp");
        menuItems.put(otherTimestampLabel, "Other timestamp");
        menuItems.put(resourceLabel, "Resource");
        menuItems.put(caseAttributeLabel, "Case Attribute");
        menuItems.put(eventAttributeLabel, "Event Attribute");
        menuItems.put(ignoreLabel, "Ignore Attribute");


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
                        (myItem.getKey().equals(caseIdLabel) && sample.getCaseIdPos() == pos) ||
                                (myItem.getKey().equals(activityLabel) && sample.getActivityPos() == pos) ||
                                (myItem.getKey().equals(endTimestampLabel) && sample.getEndTimestampPos() == pos)) ||
                        (myItem.getKey().equals(startTimestampLabel) && sample.getStartTimestampPos() == pos) ||
                        (myItem.getKey().equals(otherTimestampLabel) && sample.getOtherTimestamps().containsKey(pos)) ||
                        (myItem.getKey().equals(resourceLabel) && sample.getResourcePos() == pos) ||
                        (myItem.getKey().equals(caseAttributeLabel) && sample.getCaseAttributesPos().contains(pos)) ||
                        (myItem.getKey().equals(eventAttributeLabel) && sample.getEventAttributesPos().contains(pos)) ||
                        (myItem.getKey().equals(ignoreLabel) && sample.getIgnoredPos().contains(pos))
                ) {
                    item.setSelected(true);
                }
                box.appendChild(item);
            }


            box.addEventListener("onSelect", (Event event) -> {
                String selected = box.getSelectedItem().getValue();
                int colPos = Integer.parseInt(box.getId());
                resetSelect(colPos);

                if (selected.equals(caseIdLabel)) {
                    resetUniqueAttribute(sample.getCaseIdPos());
                    sample.setCaseIdPos(colPos);
                } else if (selected.equals(activityLabel)) {
                    resetUniqueAttribute(sample.getActivityPos());
                    sample.setActivityPos(colPos);
                } else if (selected.equals(endTimestampLabel)) {
                    resetUniqueAttribute(sample.getEndTimestampPos());
                    if (sample.isParsable(colPos)) {
                        updateTimestampPos(colPos, selected, null);
                    } else {
                        sample.getEventAttributesPos().add(colPos);
                        showFormatBtn(formatBtns[colPos]);
                        openPopUpBox(colPos);
                    }

                } else if (selected.equals(startTimestampLabel)) {
                    resetUniqueAttribute(sample.getStartTimestampPos());
                    if (sample.isParsable(colPos)) {
                        updateTimestampPos(colPos, selected, null);
                    } else {
                        sample.getEventAttributesPos().add(colPos);
                        showFormatBtn(formatBtns[colPos]);
                        openPopUpBox(colPos);
                    }
                } else if (selected.equals(resourceLabel)) {
                    resetUniqueAttribute(sample.getResourcePos());
                    sample.setResourcePos(colPos);
                } else if (selected.equals(ignoreLabel)) {
                    sample.getIgnoredPos().add(colPos);
                } else if (selected.equals(otherTimestampLabel)) {
                    if (sample.isParsable(colPos)) {
                        sample.getOtherTimestamps().put(colPos, null);
                    } else {
                        sample.getEventAttributesPos().add(colPos);
                        showFormatBtn(formatBtns[colPos]);
                        openPopUpBox(colPos);
                    }
                } else if (selected.equals(caseAttributeLabel)) {
                    sample.getCaseAttributesPos().add(colPos);
                }
            });

            menuDropDownLists.add(box);
        }

        this.dropDownLists = menuDropDownLists;
    }

    private void resetUniqueAttribute(int oldColPos) {
        if (oldColPos != -1) {
            resetSelect(oldColPos);

            Listbox lb = (Listbox) window.getFellow(String.valueOf(0));
            int eventAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(eventAttributeLabel));
            Listbox oldBox = dropDownLists.get(oldColPos);
            oldBox.setSelectedIndex(eventAttributeIndex);
            sample.getEventAttributesPos().add(oldColPos);
        }
    }

    private void resetSelect(int pos) {
        if (sample.getOtherTimestamps().containsKey(pos)) {
            sample.getOtherTimestamps().remove(pos);
            closePopUpBox(pos);
            hideFormatBtn(formatBtns[pos]);

        } else if (sample.getIgnoredPos().contains(pos)) {
            sample.getIgnoredPos().remove(Integer.valueOf(pos));

        } else if (sample.getCaseAttributesPos().contains(pos)) {
            sample.getCaseAttributesPos().remove(Integer.valueOf(pos));

        } else if (sample.getEventAttributesPos().contains(pos)) {
            sample.getEventAttributesPos().remove(Integer.valueOf(pos));

        } else if (sample.getCaseIdPos() == pos) {
            sample.setCaseIdPos(-1);

        } else if (sample.getActivityPos() == pos) {
            sample.setActivityPos(-1);

        } else if (sample.getEndTimestampPos() == pos) {
            sample.setEndTimestampPos(-1);
            closePopUpBox(pos);
            hideFormatBtn(formatBtns[pos]);

        } else if (sample.getStartTimestampPos() == pos) {
            sample.setStartTimestampPos(-1);
            closePopUpBox(pos);
            hideFormatBtn(formatBtns[pos]);

        } else if (sample.getResourcePos() == pos) {
            sample.setResourcePos(-1);
        }
    }

    private void updateTimestampPos(int pos, String timestampLabel, String format) {
        if (timestampLabel.equals(endTimestampLabel)) {
            sample.setEndTimestampPos(pos);
            sample.setEndTimestampFormat(format);
        } else if (timestampLabel.equals(startTimestampLabel)) {
            sample.setStartTimestampPos(pos);
            sample.setStartTimestampFormat(format);
        } else if (timestampLabel.equals(otherTimestampLabel)) {
            sample.getOtherTimestamps().put(pos, format);
        }
    }

    private void openPopUpBox(int pos) {
        Window myPopUp = (Window) popUpBox.getFellow(popUpFormatWindowId + pos);
        myPopUp.setStyle(myPopUp.getStyle().replace("hidden", "visible"));
        Clients.evalJavaScript("adjustPos(" + pos + ")");
    }

    private void closePopUpBox(int pos) {
        Window myPopUp = (Window) popUpBox.getFellow(popUpFormatWindowId + pos);
        myPopUp.setStyle(myPopUp.getStyle().replace("visible", "hidden"));
    }

    private void showFormatBtn(Button myButton) {
        myButton.setSclass("ap-csv-importer-format-icon");
    }

    private void hideFormatBtn(Button myButton) {
        myButton.setSclass("ap-csv-importer-format-icon ap-hidden");
    }

    private void setButtons() {

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

    private void ignoreToEvent() {
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

    private void eventToIgnore() {
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

    private void convertToXes() {
        StringBuilder headNOTDefined = validateUniqueAttributes();
        if (headNOTDefined.length() != 0) {
            Messagebox.show(headNOTDefined.toString(), "Missing fields!", Messagebox.OK, Messagebox.ERROR);
        } else {
            try {
                if (xesModel == null) {
                    CSVReader reader = new CSVFileReader().newCSVReader(media, getFileEncoding());
                    if (reader != null) {
                        xesModel = csvImporterLogic.prepareXesModel(reader, sample);
                    }
                }

                List<LogErrorReport> errorReport = xesModel.getLogErrorReport();
                if (errorReport.isEmpty()) {
                    saveXLog();
                } else {
                    handleInvalidData();
                }
            } catch (Exception e) {
                Messagebox.show("Error! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
                e.printStackTrace();
            }
        }
    }

    private StringBuilder validateUniqueAttributes() {
        StringBuilder importMessage = new StringBuilder();
        String mess = "No attribute has been selected as ";

        if (sample.getCaseIdPos() == -1) {
            importMessage.append(mess + "Case ID!");
        }
        if (sample.getActivityPos() == -1) {
            if (importMessage.length() == 0) {
                importMessage.append(mess + "Activity!");
            } else {
                importMessage.append(System.lineSeparator()).append(mess + "Activity!");
            }
        }
        if (sample.getEndTimestampPos() == -1) {
            if (importMessage.length() == 0) {
                importMessage.append(mess + "End Timestamp!");
            } else {
                importMessage.append(System.lineSeparator()).append(mess + "End Timestamp!");
            }
        }

        return importMessage;
    }

    private void handleInvalidData() {
        try {
            Window errorPopUp = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/invalidData.zul", null, null);
            errorPopUp.doModal();

            List<LogErrorReport> errorReport = xesModel.getLogErrorReport();

            Label errorCount = (Label) errorPopUp.getFellow(handleErrorCount);
            errorCount.setValue(String.valueOf(errorReport.size()));

            Label columnList = (Label) errorPopUp.getFellow(handleInvalidColumnsList);

            Set<String> invColList = new HashSet<String>();
            Set<String> igColList = new HashSet<String>();
            for (LogErrorReport error : errorReport) {
                if(error.getHeader() != null && !error.getHeader().isEmpty()){
                    invColList.add(error.getHeader());
                    if (sample.getOtherTimestamps().containsKey(error.getColumnIndex())) {
                        igColList.add(error.getHeader());
                    }
                }
            }
            if(!invColList.isEmpty()){
                columnList.setValue("Following column(s) include(s) one or more errors: " + columnList(invColList));
            }

            if (!igColList.isEmpty()) {
                Label ignoredList = (Label) errorPopUp.getFellow(handleIgnoredColumnsList);
                Label ignoreLbl = (Label) errorPopUp.getFellow(handleIgnoreColLbl);
                ignoreLbl.setVisible(true);
                ignoredList.setValue("Following column(s) will be ignored: " + columnList(igColList));

                List<Integer> columnIndex = errorReport.stream()
                        .map(LogErrorReport::getColumnIndex)
                        .collect(Collectors.toList());

                Button skipColumns = (Button) errorPopUp.getFellow(handleSkipColumnsBtnId);
                skipColumns.setVisible(true);
                skipColumns.addEventListener("onClick", event -> {
                            errorPopUp.invalidate();
                            errorPopUp.detach();

                            for (int pos : columnIndex) {
                                sample.getOtherTimestamps().remove(pos);
                                sample.getIgnoredPos().add(pos);
                            }

                            CSVReader reader = new CSVFileReader().newCSVReader(media, getFileEncoding());
                            xesModel = csvImporterLogic.prepareXesModel(reader, sample);
                            saveXLog();
                        }
                );
            }

            Button downloadBtn = (Button) errorPopUp.getFellow(handleDownloadBtnId);
            downloadBtn.addEventListener("onClick", event -> {
                        downloadErrorLog(errorReport);
                    }
            );

            Button skipRows = (Button) errorPopUp.getFellow(handleSkipRowsBtnId);
            skipRows.addEventListener("onClick", event -> {
                        errorPopUp.invalidate();
                        errorPopUp.detach();
                        saveXLog();
                    }
            );

            Button cancelButton = (Button) errorPopUp.getFellow(handleCancelBtnId);
            cancelButton.addEventListener("onClick", event -> {
                errorPopUp.invalidate();
                errorPopUp.detach();
            });

        } catch (IOException e) {
            Messagebox.show("Error! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private String columnList(Set<String> list) {
        StringBuilder colList = new StringBuilder();
        for (String col : list) {
            colList.append(col).append(" | ");
        }
        if (colList.length() > 0) {
            return colList.toString().substring(0, colList.toString().length() - 2);
        }
        return null;
    }

    private void downloadErrorLog(List<LogErrorReport> errorReport) throws Exception {

        File tempFile = File.createTempFile("ErrorReport", ".csv");
        try (FileWriter writer = new FileWriter(tempFile);

             CSVWriter csvWriter = new CSVWriter(writer,
                     CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END);) {

            String[] headerRecord = {"#", "Row Index", "Column", "Error message"};
            csvWriter.writeNext(headerRecord);

            int counter = 0;
            for (LogErrorReport error : errorReport) {
                csvWriter.writeNext(new String[]{String.valueOf(counter++), String.valueOf(error.getRowIndex()), error.getHeader(), error.getError()});
            }

            InputStream csvLogStream = new FileInputStream(tempFile);
            Filedownload.save(csvLogStream, "text/csv; charset-UTF-8", "ErrorReport.csv");
        } catch (Exception e) {
            Messagebox.show("Failed to download error log: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        } finally {
            tempFile.delete();

        }
    }

    private void saveXLog() {
        try {
            XLog xlog = xesModel.getXLog();
            if (xlog == null) {
                throw new InvalidCSVException("Failed to create XES log!");
            }

            String name = media.getName().replaceFirst("[.][^.]+$", "");
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

            Messagebox.show("Total number of lines processed: " + xesModel.getRowsCount() + "\n Your file has been imported successfully!");

            portalContext.refreshContent();
        } catch (InvalidCSVException e) {
            Messagebox.show(e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        } catch (Exception e) {
            Messagebox.show("Failed to write and save log! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
