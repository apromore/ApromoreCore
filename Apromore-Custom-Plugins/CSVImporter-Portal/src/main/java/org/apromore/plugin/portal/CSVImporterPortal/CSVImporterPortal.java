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

package org.apromore.plugin.portal.CSVImporterPortal;

import java.io.*;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.impl.LogModel;
import org.apromore.service.csvimporter.impl.gridRendererController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.*;

import org.deckfour.xes.model.XLog;
import com.opencsv.CSVReader;


@Component("csvImporterPortalPlugin")
public class CSVImporterPortal implements FileImporterPlugin {
    private char[] supportedSeparators = {',','|',';','\t'};
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVImporterPortal.class);

    @Inject private CSVImporterLogic csvImporterLogic;
    @Inject private EventLogService eventLogService;
    char separator = Character.UNASSIGNED;

    public void setCsvImporterLogic(CSVImporterLogic newCSVImporterLogic) {
        this.csvImporterLogic = newCSVImporterLogic;
    }

    public void setEventLogService(EventLogService newEventLogService) {
        this.eventLogService = newEventLogService;
    }

    private static String popupID = "pop_";
    private static String textboxID = "txt_";
    private static String labelID = "lbl_";

    private static Integer AttribWidth = 150;

    private void saveLog(XLog xlog, String name, PortalContext portalContext) throws Exception {

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
                false  // public?
        );

        portalContext.refreshContent();
    }


    /**
     * Gets the Content.
     *
     * @param media the imported CSV file
     * @return the model data
     * <p>
     * read CSV content and create list model to be set as grid model.
     */
    @SuppressWarnings("null")
    private void displayCSVContent(Media media, ListModelList<String[]> result, Grid myGrid, Div attrBox, Div popUPBox, Window window) {
        String firstLine = null;

        BufferedReader brReader = new BufferedReader(new InputStreamReader(media.getStreamData()));

        try {
            firstLine = brReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            Messagebox.show("Can not read this file", "Error", Messagebox.OK, Messagebox.ERROR);
        }

        separator = getMaxOccuringChar(firstLine);
        CSVReader reader = null;
        if(separator == Character.UNASSIGNED) {
            Messagebox.show("Separator is not supported.", "Error", Messagebox.OK, Messagebox.ERROR);
        } else {
            try {

                CSVParser parser = new CSVParserBuilder().withSeparator(separator).withIgnoreQuotations(true).build();
                // check file format to choose correct file reader.
                if (media.isBinary()) {
                    reader = new CSVReaderBuilder(new InputStreamReader(media.getStreamData())).withSkipLines(0).withCSVParser(parser).build();
                } else {
                    reader = new CSVReaderBuilder(media.getReaderData()).withSkipLines(0).withCSVParser(parser).build();
                }
                String[] header;
                String[] line;

                if (myGrid.getColumns() == null) {
                    new Columns().setParent(myGrid);
                } else {
                    myGrid.getColumns().getChildren().clear();
                }

                /// display first numberOfrows to user and display drop down lists to set attributes
                header = reader.readNext();   // read first line

//                window.setHeight("100%");
                if(header.length > 9) {
                    window.setWidth("98%");
                } else {
                    Double DynamicWidth = null;
                    DynamicWidth = 10.88 * header.length;
                    window.setWidth(DynamicWidth + "%");
                }

                for (int i = 0; i < header.length; i++) {
                    Column newColumn = new Column();
                    newColumn.setWidth(AttribWidth + "px");
                    newColumn.setValue(header[i]);
                    newColumn.setLabel(header[i]);
                    newColumn.setAlign("center");
                    myGrid.getColumns().appendChild(newColumn);
                }
                // add dropdown lists
                if (attrBox != null) {
                    attrBox.getChildren().clear();
                }
                if (popUPBox != null) {
                    popUPBox.getChildren().clear();
                }
                if (result != null) {
                    result.clear();
                }

                line = reader.readNext();
                if (line == null || header == null) {
                    Messagebox.show("Could not parse file!");
                }

                    csvImporterLogic.setLine(line);
                    csvImporterLogic.setHeads(header);
                    csvImporterLogic.setOtherTimestamps();

                    if (line.length != header.length) {
                        Messagebox.show("Number of columns in the header does not match number of columns in the data", "Invalid CSV file", Messagebox.OK, Messagebox.ERROR);
                        window.detach();
                        reader.close();
                    } else {

                        attrBox.setWidth(line.length * AttribWidth + "px");

                        csvImporterLogic.setLists(line.length, csvImporterLogic.getHeads(), AttribWidth + "px");

                        List<Listbox> lists = csvImporterLogic.getLists();
                        for (Listbox list : lists) {
                            attrBox.appendChild(list);
                        }

                        createPopUpTextBox(line.length, popUPBox);
                        csvImporterLogic.openPopUp();


                        // display first 1000 rows
                        int numberOfrows = 1000 - 1;
                        while (line != null && numberOfrows >= 0) {
                            result.add(line);
                            numberOfrows--;
                            line = reader.readNext();
                        }
                        reader.close();
                    }
            } catch (IOException e) {
                e.printStackTrace();
                Messagebox.show(e.getMessage());
            }
        }
    }


    private void createPopUpTextBox(int colNum, Div popUPBox){
        for(int i =0; i<= colNum -1; i++){
            Window item = new Window();
            item.setId(popupID+ i);
            item.setWidth((AttribWidth) + "px");
            item.setMinheight(100);
            item.setClass("p-1");
            item.setBorder("normal");
            item.setStyle("margin-left:" + (i==0? 10: (i*AttribWidth) )  + "px; position: absolute; z-index: 10; visibility: hidden; top:1px;");

            Button sp = new Button();
//            sp.setLabel("-");
            sp.setIconSclass("z-icon-compress");
            sp.setStyle("margin-left:20px;");
//            sp.setZclass("min-height: 16px;");
            A hidelink = new A();
            hidelink.appendChild(sp);
            sp.addEventListener("onClick", (Event event) -> {
                item.setStyle(item.getStyle().replace("visible", "hidden"));
            });

            Textbox textbox = new Textbox();
            textbox.setId(textboxID + i);
            textbox.setWidth("100%");
            textbox.setPlaceholder("Specify timestamp format");
            textbox.addEventListener("onChanging", (InputEvent event) -> {
                if(!(event.getValue().isEmpty() || event.getValue().equals(""))){
                    csvImporterLogic.tryParsing(event.getValue(), Integer.parseInt(textbox.getId().replace(textboxID,"")));
                }
            });
            item.appendChild(textbox);

            Label check_lbl = new Label();
            check_lbl.setId(labelID + i);
            item.appendChild(check_lbl);
            item.appendChild(hidelink);
            popUPBox.appendChild(item);
        }
        popUPBox.clone();

        csvImporterLogic.setPopUPBox(popUPBox);
        csvImporterLogic.setPopupID(popupID);
        csvImporterLogic.setTextboxID(textboxID);
        csvImporterLogic.setLabelID(labelID);
    }


    // FileImporterPlugin implementation

    @Override
    public Set<String> getFileExtensions() {
        return new HashSet<>(Arrays.asList("csv"));
    }

    @Override
    public void importFile(Media media, PortalContext portalContext, boolean isPublic) {
        LOGGER.info("Import file: " + media.getName());

        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/csvimporter.zul", null, null);
//            Label fileNameLabel = (Label) window.getFellow("fileNameLabel");
            ListModelList<String[]> result = new ListModelList<String[]>();
            Grid myGrid  = (Grid) window.getFellow("myGrid");
            Div attrBox = (Div) window.getFellow("attrBox");
            Div popUPBox = (Div) window.getFellow("popUPBox");
            Button toXESButton = (Button) window.getFellow("toXESButton");

            if(media != null) {
                window.setWidth("95%");
//                window.setVflex("min");
                myGrid.setHeight("100%");


                csvImporterLogic.resetLine();
                csvImporterLogic.resetHead();
                csvImporterLogic.resetList();

                if (attrBox != null) {
                    attrBox.getChildren().clear();
                }
                String[] allowedExtensions = {"csv", "xls", "xlsx"};
                if (Arrays.asList(allowedExtensions).contains(media.getFormat())) {

                    displayCSVContent(media, result, myGrid, attrBox, popUPBox, window);
                    if (window != null) {
                        // set grid model
                        if (result != null) {
                            myGrid.setModel(result);
                        } else {
                            Messagebox.show("Result is NULL!", "Attention", Messagebox.OK, Messagebox.ERROR);
                        }
                        //set grid row renderer
                        gridRendererController rowRenderer = new gridRendererController();
                        rowRenderer.setAttribWidth(AttribWidth);

                        myGrid.setRowRenderer(rowRenderer);
                        toXESButton.setDisabled(false);
                        window.setTitle("CSV Importer - " + media.getName());
//                        fileNameLabel.setValue("Current File: " + media.getName());
                        window.setPosition("top,left");
                    }
                } else {
                    Messagebox.show("Please select CSV file!", "Error", Messagebox.OK, Messagebox.ERROR);
                }
            }
            toXESButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    CSVReader reader = null;
                    if(window != null) {
                        // on clicking the button: CONVERT TO XES
                        if (media != null) {
                            try {
                                CSVParser parser = new CSVParserBuilder().withSeparator(separator).withIgnoreQuotations(true).build();
                                // check file format to choose correct file reader.
                                if (media.isBinary()) {
                                    reader = new CSVReaderBuilder(new InputStreamReader(media.getStreamData())).withSkipLines(0).withCSVParser(parser).build();
                                } else {
                                    reader = new CSVReaderBuilder(media.getReaderData()).withSkipLines(0).withCSVParser(parser).build();
                                }
                            }catch (Exception e) {
                                LOGGER.error("Failed to read");
                            }
                            List<LogModel> xesModel = csvImporterLogic.prepareXesModel(reader);
                            if (xesModel != null) {
                                // create XES file
                                XLog xlog = csvImporterLogic.createXLog(xesModel);
                                if (xlog != null) {
                                    saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""), portalContext);
//                                    Messagebox.show("Your file has been created!");
                                }
                                window.detach();
                            }
                        } else {
                            Messagebox.show("Upload file first!");
                        }
                    }
                }

            });
                window.doModal();

        } catch (IOException e) {
            LOGGER.warn("Unable to execute sample method", e);
            Messagebox.show("Unable to import file : " + e, "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
    private char getMaxOccuringChar(String str)
    {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("input word must have non-empty value.");
        }
        char maxchar = ' ';
        int maxcnt = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = str.length() - 1; i >= 0; i--) {
            if(!Character.isLetter(str.charAt(i))) {
                for(int j =0; j < supportedSeparators.length; j++) {
                    if(str.charAt(i) == supportedSeparators[j]) {
                        char ch = str.charAt(i);
                        if (++charcnt[ch] >= maxcnt) {
                            maxcnt = charcnt[ch];
                            maxchar = ch;
                        }
                    }
                }
            }
        }
        return maxchar;
    }
}
