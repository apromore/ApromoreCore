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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;
import org.apromore.plugin.portal.DefaultPortalPlugin;
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
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.*;

import org.deckfour.xes.model.XLog;
import com.opencsv.CSVReader;
@Component("csvImporterPortalPlugin")
public class CSVImporterPortal extends DefaultPortalPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVImporterPortal.class);

    @Inject private CSVImporterLogic csvImporterLogic;
    @Inject private EventLogService eventLogService;

    private String label = "CSV Importer";
    private String groupLabel = "Discover";
    private Media media;

    public void setCsvImporterLogic(CSVImporterLogic newCSVImporterLogic) {
        this.csvImporterLogic = newCSVImporterLogic;
    }

    public void setEventLogService(EventLogService newEventLogService) {
        this.eventLogService = newEventLogService;
    }

    // PortalPlugin overrides

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }
    private static String popupID = "pop_";
    private static String textboxID = "txt_";
    private static String labelID = "lbl_";

    private static Integer AttribWidth = 180;

//    /**
//     * Upload file.
//     *
//     * @param event the event: upload event
//     *              allows importing CSV file, if imported correctly, it sets the grid model and row renderer.
//     */



    @Override
    public void execute(PortalContext portalContext) {
        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/csvimporter.zul", null, null);
            Button uploadButton = (Button) window.getFellow("uploadButton");
            Button cancelButton = (Button) window.getFellow("cancelButton");
//            Button topCancelButton = (Button) window.getFellow("topCancelButton");
            Label fileNameLabel = (Label) window.getFellow("fileNameLabel");
            ListModelList<String[]> result = new ListModelList<String[]>();
            Grid myGrid  = (Grid) window.getFellow("myGrid");
            Div attrBox = (Div) window.getFellow("attrBox");
            Div popUPBox = (Div) window.getFellow("popUPBox");
            Button toXESButton = (Button) window.getFellow("toXESButton");
            Div gridBox = (Div) window.getFellow("gridBox");

            uploadButton.addEventListener("onUpload", new EventListener<UploadEvent>() {
                public void onEvent(UploadEvent event) throws Exception {
                    try {

                        media = event.getMedia();

                        myGrid.setHeight("95%");

                        csvImporterLogic.resetLine();
                        csvImporterLogic.resetHead();
                        csvImporterLogic.resetList();

                        if(attrBox != null) {
                            attrBox.getChildren().clear();
                        }
                        String[] allowedExtensions = {"csv", "xls", "xlsx"};
                        if (Arrays.asList(allowedExtensions).contains(media.getFormat())) {

                            displayCSVContent(media, result, myGrid, attrBox, popUPBox);
                            gridBox.setWidth(attrBox.getWidth());
                            // set grid model
                            if(result != null) {
                                myGrid.setModel(result);
                            } else{
                                Messagebox.show("Result is NULL!", "Attention", Messagebox.OK, Messagebox.ERROR);
                            }
                            //set grid row renderer
                            gridRendererController rowRenderer = new gridRendererController();
                            rowRenderer.setAttribWidth(AttribWidth);

                            myGrid.setRowRenderer(rowRenderer);
                            toXESButton.setDisabled(false);

                            fileNameLabel.setValue("Current File: " + media.getName());
                            window.setPosition("top,left");
                        } else {
                            Messagebox.show("Please select CSV file!", "Error", Messagebox.OK, Messagebox.ERROR);
                        }

                    } catch (Exception e) {
                        LOGGER.info("Unable to import file", e);
                        Messagebox.show("Unable to import file: " + e, "Attention", Messagebox.OK, Messagebox.ERROR);
                    }
                }

            });


            toXESButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    // on clicking the button: CONVERT TO XES

                    if (media != null){
                        Reader reader = media.isBinary() ? new InputStreamReader(media.getStreamData())
                                : media.getReaderData();
                        List<LogModel> xesModel = csvImporterLogic.prepareXesModel(reader);
                        if (xesModel != null) {
                            // create XES file
                            XLog xlog = csvImporterLogic.createXLog(xesModel);
                            saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""), portalContext);
                            Messagebox.show("Your file has been created!");
                            window.detach();
                        }
                    }else{
                        Messagebox.show("Upload file first!");
                    }
                }

            });

            cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    window.detach();
                }
            });

//            topCancelButton.addEventListener("onClick", new EventListener<Event>() {
//                public void onEvent(Event event) throws Exception {
//                    window.detach();
//                }
//            });

            window.doModal();

        } catch (IOException e) {
//            LOGGER.warn("Unable to execute sample method", e);
            Messagebox.show("Unable to import file : " + e, "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

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

//    private static org.apromore.service.csvimporter.impl.CSVImporterLogicImpl CsvToXes = new CsvToXes();
    /**
     * Gets the Content.
     *
     * @param media the imported CSV file
     * @return the model data
     * <p>
     * read CSV content and create list model to be set as grid model.
     */
    @SuppressWarnings("null")
    private void displayCSVContent(Media media, ListModelList<String[]> result, Grid myGrid, Div attrBox, Div popUPBox) {
        CSVReader reader = null;

        try {

            // check file format to choose correct file reader.
            if(media.isBinary()){
                reader = new CSVReader(new InputStreamReader(media.getStreamData()));
            } else {
                reader = new CSVReader(media.getReaderData());
            }
//            ListModelList<String[]> result = new ListModelList<String[]>();
            String[] header;
            String[] line;

            if(myGrid.getColumns() == null) {
                new Columns().setParent(myGrid);
            } else {
                myGrid.getColumns().getChildren().clear();
            }


            /// display first numberOfrows to user and display drop down lists to set attributes
            header = reader.readNext();   // read first line



            for(int i=0; i<header.length ; i++) {
                Column newColumn = new Column();
                newColumn.setWidth(AttribWidth + "px");
                newColumn.setValue(header[i]);
                newColumn.setLabel(header[i]);
                newColumn.setAlign("center");
                myGrid.getColumns().appendChild(newColumn);
//                myGrid.getColumns().appendChild(newColumn);
            }
            // add dropdown lists
            if(attrBox != null) {
                attrBox.getChildren().clear();
            }
            if(popUPBox != null) {
                popUPBox.getChildren().clear();
            }
            if(result != null) {
                result.clear();
            }

//            result.add(header);

            line = reader.readNext();
            if(line == null || header == null) {
                Messagebox.show("Could not parse file!");
            }

            csvImporterLogic.setLine(line);
            csvImporterLogic.setHeads(header);
            csvImporterLogic.setOtherTimestamps();

            myGrid.setStyle("width:80%;height:80%;");
//            myGrid.setWidth(line.length * AttribWidth + "px");
            attrBox.setWidth(line.length * AttribWidth + "px");

            csvImporterLogic.setLists(line.length, csvImporterLogic.getHeads(), AttribWidth + "px");

            List<Listbox> lists = csvImporterLogic.getLists();
            for (Listbox list : lists) {
                attrBox.appendChild(list);
//                attrBox.appendChild(new Space());
            }
//            attrBox.clone();


//            System.out.println("attrBox is done. Line length:" + line.length);
            createPopUpTextBox(line.length, popUPBox);
            csvImporterLogic.openPopUp();

//            System.out.println("createPopUpTxtBox is done.");

            // display first 1000 rows
            int numberOfrows = 1000;
            while (line != null && numberOfrows >= 0) {
                result.add(line);
                numberOfrows--;
                line = reader.readNext();
            }
            reader.close();

//            System.out.println("reader is closed, result is set. Ended displayContent.");
//            return result;

        } catch (IOException e) {
            e.printStackTrace();
            Messagebox.show(e.getMessage());
//            return null;
        }
    }


    private void createPopUpTextBox(int colNum, Div popUPBox){
        popUPBox.setWidth(colNum * (AttribWidth + 30) + "px");
        for(int i =0; i<= colNum -1; i++){
            Window item = new Window();
            item.setId(popupID+ i);
            item.setWidth((AttribWidth) + "px");
            item.setMinheight(100);
            item.setClass("p-1");
            item.setBorder("normal");
            item.setStyle("margin-left:" + (i==0? 10: (i*AttribWidth) + 5)  + "px; position: absolute; z-index: 10; visibility: hidden; top:50px;");

            Button sp = new Button();
            sp.setLabel("Hide");
//            sp.setClass("fas fa-angle-double-up text-secondary float-right mb-1");
            A hidelink = new A();
            hidelink.appendChild(sp);
            sp.addEventListener("onClick", (Event event) -> {
                item.setStyle(item.getStyle().replace("visible", "hidden"));
//                popUPBox.getChildren().clear();
            });

            Textbox textbox = new Textbox();
            textbox.setId(textboxID + i);
            textbox.setWidth("100%");
            textbox.setPlaceholder("Specify timestamp format");

            textbox.addEventListener("onBlur", (Event event) -> {
                if(!(textbox.getValue().isEmpty() || textbox.getValue().equals(""))){
                    csvImporterLogic.tryParsing(textbox.getValue(), Integer.parseInt(textbox.getId().replace(textboxID,"")));
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


}
