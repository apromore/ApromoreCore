/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
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

    private Div popUpBox;
    private Button[] formatBtns;
    private Span[] parsedIcons;
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

        this.media = media;
        this.portalContext = portalContext;
        this.isLogPublic = isLogPublic;

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
                if (sample != null) {
                    setUpUI();
                    setButtons();
                }
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
        Span[] parsedIcons = new Span[sample.getHeader().size()];
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
            Span parsedIcon = new Span();

            if (pos == sample.getEndTimestampPos() || pos == sample.getStartTimestampPos() || sample.getOtherTimestamps().containsKey(pos)) {
                showFormatBtn(formatBtn);
                showAutoParsedGreenIcon(parsedIcon);
            } else {
                hideFormatBtn(formatBtn);
            }

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
            parsedIcons[pos] = parsedIcon;

            newColumn.appendChild(parsedIcon);
            newColumn.appendChild(formatBtn);
            myGrid.getColumns().appendChild(newColumn);
            myGrid.getColumns().setSizable(true);
        }

        this.formatBtns = formatBtns;
        this.parsedIcons = parsedIcons;
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
            sp.setStyle("margin-right:1px; float: right; line-height: 10px; min-height: 5px; padding:3px;");
            sp.setIconSclass("z-icon-times");

            A hidelink = new A();
            hidelink.appendChild(sp);
            sp.addEventListener("onClick", (Event event) -> item.setStyle(item.getStyle().replace("visible", "hidden")));

            Label popUpLabel = new Label();
            popUpLabel.setId(popUpLabelId + pos);
            if (pos == sample.getEndTimestampPos() || pos == sample.getStartTimestampPos() || sample.getOtherTimestamps().containsKey(pos)) {
                setPopUpLabel(pos, Parsed.AUTO, popUpLabel);
            }

            Textbox textbox = new Textbox();
            textbox.setId(popUpTextBoxId + pos);
            textbox.setWidth("98%");
            textbox.setPlaceholder("dd-MM-yyyy HH:mm:ss");
            textbox.setPopup(helpP);
            textbox.setClientAttribute("spellcheck", "false");
            textbox.setPopupAttributes(helpP, "after_start", "", "", "toggle");

            textbox.addEventListener("onChanging", (InputEvent event) -> {
                int colPos = Integer.parseInt(textbox.getId().replace(popUpTextBoxId, ""));
                Listbox box = dropDownLists.get(colPos);
                String selected = box.getSelectedItem().getValue();
                resetSelect(colPos);

                if (StringUtils.isBlank(event.getValue())) {
                    if (sample.isParsable(colPos)) {
                        parsedAuto(colPos, selected);
                    } else {
                        textbox.setPlaceholder("Specify timestamp format");
                        failedToParse(colPos);
                    }
                } else {
                    String format = event.getValue();
                    if (sample.isParsableWithFormat(colPos, format)) {
                        parsedManual(colPos, selected, format);
                    } else {
                        failedToParse(colPos);
                    }
                }
            });

            item.appendChild(hidelink);
            item.appendChild(popUpLabel);
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
                closePopUpBox(colPos);
                hideFormatBtn(formatBtns[colPos]);
                hideParsedIcon(parsedIcons[colPos]);

                switch (selected) {
                    case caseIdLabel:
                        resetUniqueAttribute(sample.getCaseIdPos());
                        sample.setCaseIdPos(colPos);
                        break;
                    case activityLabel:
                        resetUniqueAttribute(sample.getActivityPos());
                        sample.setActivityPos(colPos);
                        break;
                    case endTimestampLabel:
                        resetUniqueAttribute(sample.getEndTimestampPos());
                        timestampSelected(colPos, selected);
                        break;
                    case startTimestampLabel:
                        resetUniqueAttribute(sample.getStartTimestampPos());
                        timestampSelected(colPos, selected);
                        break;
                    case resourceLabel:
                        resetUniqueAttribute(sample.getResourcePos());
                        sample.setResourcePos(colPos);
                        break;
                    case otherTimestampLabel:
                        timestampSelected(colPos, selected);
                        break;
                    case caseAttributeLabel:
                        sample.getCaseAttributesPos().add(colPos);
                        break;
                    case ignoreLabel:
                        sample.getIgnoredPos().add(colPos);
                        break;
                    default:
                }
            });

            menuDropDownLists.add(box);
        }

        this.dropDownLists = menuDropDownLists;
    }

    private void resetUniqueAttribute(int oldColPos) {
        if (oldColPos != -1) {
            // reset value of the unique attribute
            resetSelect(oldColPos);
            closePopUpBox(oldColPos);
            hideFormatBtn(formatBtns[oldColPos]);
            hideParsedIcon(parsedIcons[oldColPos]);

            // set it as event attribute
            Listbox lb = (Listbox) window.getFellow(String.valueOf(0));
            int eventAttributeIndex = lb.getIndexOfItem((Listitem) lb.getFellow(eventAttributeLabel));
            Listbox oldBox = dropDownLists.get(oldColPos);
            oldBox.setSelectedIndex(eventAttributeIndex);
            sample.getEventAttributesPos().add(oldColPos);
        }
    }

    private void resetSelect(int pos) {
        // reset value for old select
        if (sample.getCaseIdPos() == pos) {
            sample.setCaseIdPos(-1);
        } else if (sample.getActivityPos() == pos) {
            sample.setActivityPos(-1);
        } else if (sample.getEndTimestampPos() == pos) {
            sample.setEndTimestampPos(-1);
        } else if (sample.getStartTimestampPos() == pos) {
            sample.setStartTimestampPos(-1);
        } else if (sample.getResourcePos() == pos) {
            sample.setResourcePos(-1);
        } else if (sample.getOtherTimestamps().containsKey(pos)) {
            sample.getOtherTimestamps().remove(pos);
        } else if (sample.getIgnoredPos().contains(pos)) {
            sample.getIgnoredPos().remove(Integer.valueOf(pos));
        } else if (sample.getCaseAttributesPos().contains(pos)) {
            sample.getCaseAttributesPos().remove(Integer.valueOf(pos));
        } else if (sample.getEventAttributesPos().contains(pos)) {
            sample.getEventAttributesPos().remove(Integer.valueOf(pos));
        }
    }

    private void timestampSelected(int colPos, String selected) {
        showFormatBtn(formatBtns[colPos]);
        String possibleFormat = getPopUpFormatText(colPos);
        if (possibleFormat != null && !possibleFormat.isEmpty() && sample.isParsableWithFormat(colPos, possibleFormat)) {
            parsedManual(colPos, selected, possibleFormat);
        } else if (sample.isParsable(colPos)) {
            parsedAuto(colPos, selected);
        } else {
            failedToParse(colPos);
        }
    }

    private void parsedManual(int colPos, String selected, String format) {
        updateTimestampPos(colPos, selected, format);
        setPopUpLabel(colPos, Parsed.MANUAL, null);
    }

    private void parsedAuto(int colPos, String selected) {
        updateTimestampPos(colPos, selected, null);
        setPopUpLabel(colPos, Parsed.AUTO, null);
        setPopUpFormatText(colPos, "");
    }

    private void failedToParse(int colPos) {
        sample.getEventAttributesPos().add(colPos);
        setPopUpLabel(colPos, Parsed.FAILED, null);
        openPopUpBox(colPos);
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

    private void setPopUpLabel(int pos, Enum type, Label check_lbl) {
        if (check_lbl == null) {
            Window myPopUp = (Window) popUpBox.getFellow(popUpFormatWindowId + pos);
            check_lbl = (Label) myPopUp.getFellow(popUpLabelId + pos);
        }

        if (type == Parsed.AUTO) {
            check_lbl.setZclass(greenLabelCSS);
            check_lbl.setValue(parsedAutoMessage);
            showAutoParsedGreenIcon(parsedIcons[pos]);
        } else if (type == Parsed.MANUAL) {
            check_lbl.setZclass(greenLabelCSS);
            check_lbl.setValue(parsedMessage);
            showManualParsedGreenIcon(parsedIcons[pos]);
        } else if (type == Parsed.FAILED) {
            check_lbl.setZclass(redLabelCSS);
            check_lbl.setValue(couldNotParseMessage);
            showParsedRedIcon(parsedIcons[pos]);
        }
    }

    private void showFormatBtn(Button myButton) {
        myButton.setSclass("ap-csv-importer-format-icon");
    }

    private void hideFormatBtn(Button myButton) {
        myButton.setSclass("ap-csv-importer-format-icon ap-hidden");
    }

    private void showAutoParsedGreenIcon(Span parsedIcon) {
        parsedIcon.setSclass("ap-csv-importer-parsed-icon z-icon-check-circle");
        parsedIcon.setTooltip(autoParsed);
    }

    private void showManualParsedGreenIcon(Span parsedIcon) {
        parsedIcon.setSclass("ap-csv-importer-parsed-icon z-icon-check-circle");
        parsedIcon.setTooltip(manualParsed);
    }

    private void showParsedRedIcon(Span parsedIcon) {
        parsedIcon.setSclass("ap-csv-importer-failedParse-icon z-icon-times-circle");
        parsedIcon.setTooltip(errorParsing);
    }

    private void hideParsedIcon(Span parsedIcon) {
        parsedIcon.setSclass("ap-hidden");
    }

    private String getPopUpFormatText(int pos) {
        Window myPopUp = (Window) popUpBox.getFellow(popUpFormatWindowId + pos);
        Textbox txt = (Textbox) myPopUp.getFellow(popUpTextBoxId + pos);
        return txt.getValue();
    }

    private void setPopUpFormatText(int pos, String text) {
        Window myPopUp = (Window) popUpBox.getFellow(popUpFormatWindowId + pos);
        Textbox txt = (Textbox) myPopUp.getFellow(popUpTextBoxId + pos);
        txt.setValue(text);
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
                CSVReader reader = new CSVFileReader().newCSVReader(media, getFileEncoding());
                if (reader != null) {
                    LogModel xesModel = csvImporterLogic.prepareXesModel(reader, sample);
                    List<LogErrorReport> errorReport = xesModel.getLogErrorReport();
                    if (errorReport.isEmpty()) {
                        saveXLog(xesModel);
                    } else {
                        handleInvalidData(xesModel);
                    }
                }
            } catch (Exception e) {
                Messagebox.show("Error! " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
                e.printStackTrace();
            }
        }
    }

    private StringBuilder validateUniqueAttributes() {
        StringBuilder importMessage = new StringBuilder();
        String mess = "- No attribute has been selected as ";

        if (sample.getCaseIdPos() == -1) {
            importMessage.append(mess).append("Case ID!");
        }
        if (sample.getActivityPos() == -1) {
            if (importMessage.length() == 0) {
                importMessage.append(mess).append("Activity!");
            } else {
                importMessage.append(System.lineSeparator()).append(System.lineSeparator()).append(mess).append("Activity!");
            }
        }
        if (sample.getEndTimestampPos() == -1) {
            if (importMessage.length() == 0) {
                importMessage.append(mess).append("End Timestamp!");
            } else {
                importMessage.append(System.lineSeparator()).append(System.lineSeparator()).append(mess).append("End Timestamp!");
            }
        }

        return importMessage;
    }

    private void handleInvalidData(LogModel xesModel) {
        try {
            Window errorPopUp = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/invalidData.zul", null, null);
            errorPopUp.doModal();

            List<LogErrorReport> errorReport = xesModel.getLogErrorReport();

            Label errorCount = (Label) errorPopUp.getFellow(errorCountLblId);
            errorCount.setValue(String.valueOf(errorReport.size()));

            Label columnList = (Label) errorPopUp.getFellow(invalidColumnsListLblId);

            Set<String> invColList = new HashSet<String>();
            Set<String> igColList = new HashSet<String>();
            Set<Integer> invTimestampPos = new HashSet<>();
            for (LogErrorReport error : errorReport) {
                if (error.getHeader() != null && !error.getHeader().isEmpty()) {
                    invColList.add(error.getHeader());
                    if (sample.getOtherTimestamps().containsKey(error.getColumnIndex())) {
                        igColList.add(error.getHeader());
                        invTimestampPos.add(error.getColumnIndex());
                    }
                }
            }
            if (!invColList.isEmpty()) {
                columnList.setValue("The following column(s) include(s) one or more errors: " + columnList(invColList));
            }

            if (!igColList.isEmpty()) {
                Label ignoredList = (Label) errorPopUp.getFellow(ignoredColumnsListLblId);
                Label ignoreLbl = (Label) errorPopUp.getFellow(ignoreColLblId);
                ignoreLbl.setVisible(true);
                ignoredList.setValue(columnList(igColList));

                Button skipColumns = (Button) errorPopUp.getFellow(skipColumnsBtnId);
                skipColumns.setVisible(true);
                skipColumns.addEventListener("onClick", event -> {
                            errorPopUp.invalidate();
                            errorPopUp.detach();

                            for (int pos : invTimestampPos) {
                                sample.getOtherTimestamps().remove(pos);
                                sample.getIgnoredPos().add(pos);
                            }

                            CSVReader reader = new CSVFileReader().newCSVReader(media, getFileEncoding());
                            saveXLog(csvImporterLogic.prepareXesModel(reader, sample));
                        }
                );
            }

            Button downloadBtn = (Button) errorPopUp.getFellow(downloadReportBtnId);
            downloadBtn.addEventListener("onClick", event -> {
                        downloadErrorLog(errorReport);
                    }
            );

            Button skipRows = (Button) errorPopUp.getFellow(skipRowsBtnId);
            skipRows.addEventListener("onClick", event -> {
                        errorPopUp.invalidate();
                        errorPopUp.detach();
                        saveXLog(xesModel);
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

            int counter = 1;
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

    private void saveXLog(LogModel xesModel) {
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
