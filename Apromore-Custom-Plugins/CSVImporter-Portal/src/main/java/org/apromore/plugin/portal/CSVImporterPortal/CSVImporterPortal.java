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
import java.sql.Timestamp;
import java.util.*;
import java.util.Calendar;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;

import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.lang.StringUtils;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.InvalidCSVException;
import org.apromore.service.csvimporter.LogModel;
import org.apromore.service.csvimporter.LogSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.*;
import org.zkoss.zk.ui.util.Clients;

import org.deckfour.xes.model.XLog;


@Component("csvImporterPortalPlugin")
public class CSVImporterPortal implements FileImporterPlugin {
    private static char[] supportedSeparators = {',','|',';','\t'};
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVImporterPortal.class);
    private static final double MAX_ERROR_FRACTION = 0.2;  // Accept up to 20% error rate

    @Inject private CSVImporterLogic csvImporterLogic;
    @Inject private EventLogService eventLogService;
    char separator = Character.UNASSIGNED;

    public void setCsvImporterLogic(CSVImporterLogic newCSVImporterLogic) {
        this.csvImporterLogic = newCSVImporterLogic;
    }

    public void setEventLogService(EventLogService newEventLogService) {
        this.eventLogService = newEventLogService;
    }

    private static Integer AttribWidth = 180;
    private static Integer IndexColumnWidth = 50;


    private LogSample sample = null;

    private boolean isPublic;

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
            isPublic  // public?
        );

        portalContext.refreshContent();
    }


    private static CSVReader newCSVReader(Media media, String charset) throws InvalidCSVException, IOException {

        // Guess at ethe separator character
        Reader reader = media.isBinary() ? new InputStreamReader(media.getStreamData(), charset) : media.getReaderData();
        BufferedReader brReader = new BufferedReader(reader);
        String firstLine = brReader.readLine();
        char separator = getMaxOccurringChar(firstLine);

        if(separator == Character.UNASSIGNED) {
            throw new InvalidCSVException("Separator is not supported.");
        }

        // Create the CSV reader
        reader = media.isBinary() ? new InputStreamReader(media.getStreamData(), charset) : media.getReaderData();
        return (new CSVReaderBuilder(reader))
                    .withSkipLines(0)
                    .withCSVParser((new RFC4180ParserBuilder()).withSeparator(separator).build())
                    .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                    .build();
    }


    private static char getMaxOccurringChar(String str) {

        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("input word must have non-empty value.");
        }
        char maxchar = ' ';
        int maxcnt = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = str.length() - 1; i >= 0; i--) {
            if(!Character.isLetter(str.charAt(i))) {
                for (char supportedSeparator : supportedSeparators) {
                    if (str.charAt(i) == supportedSeparator) {
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

    /**
     * Gets the Content.
     *
     * Read CSV content and create list model to be set as grid model.
     *
     * @param media the imported CSV file
     */
    @SuppressWarnings("null")
    private static LogSample displayCSVContent(CSVImporterLogic csvImporterLogic, Media media, Window window) {

        final int SAMPLE_SIZE = 100;

        Grid myGrid  = (Grid) window.getFellow("myGrid");
        Div popUPBox = (Div) window.getFellow("popUPBox");

        Combobox setEncoding = (Combobox) window.getFellow("setEncoding");
        String charset = setEncoding.getValue().contains(" ")
            ? setEncoding.getValue().substring(0, setEncoding.getValue().indexOf(' '))
            : setEncoding.getValue();

        try (CSVReader csvReader = newCSVReader(media, charset)) {

            // Sample the first SAMPLE_SIZE events from the log
            LogSample sample = csvImporterLogic.sampleCSV(csvReader, SAMPLE_SIZE);

            // Present the beginning of the log to the user so that they can confirm/add configuration

            myGrid.getChildren().clear();
            (new Columns()).setParent(myGrid);
            myGrid.getColumns().getChildren().clear();

            /// display first numberOfrows to user and display drop down lists to set attributes
            //Collections.addAll(header, csvReader.readNext());
            // Deal with UTF-8 with BOM file encoding
//                String BomC = new String(header.get(0).getBytes(), Charset.forName("UTF-8"));
//                header.set(0, BomC);

            //2019-11-12
            if(sample.getHeader().size() > 8) {
                window.setMaximizable(true);
                window.setMaximized(true);
            } else {
                window.setMaximizable(false);
                int size = IndexColumnWidth + sample.getHeader().size() * AttribWidth + 35;
                window.setWidth(size + "px");
            }
            if (popUPBox != null) {
                popUPBox.getChildren().clear();
            }

            Auxhead optionHead = new Auxhead();
            Auxheader index = new Auxheader();
            optionHead.appendChild(index);
            for (Listbox list : sample.getLists()) {
                Auxheader listHeader = new Auxheader();
                listHeader.appendChild(list);
                optionHead.appendChild(listHeader);
            }
            myGrid.appendChild(optionHead);

            Column indexCol = new Column();
            indexCol.setWidth(IndexColumnWidth + "px");
            indexCol.setValue("");
            indexCol.setLabel("");
            indexCol.setAlign("center");
            myGrid.getColumns().appendChild(indexCol);

            Button[] formatBtns = new Button[sample.getHeader().size()];

            for (int i = 0; i < sample.getHeader().size(); i++) {
                Column newColumn = new Column();
                newColumn.setValue(sample.getHeader().get(i));
                newColumn.setLabel(sample.getHeader().get(i));
                String label = (String) sample.getHeader().get(i);
                int labelLen = (label.length() * 14) + 20;
                if (labelLen < AttribWidth ) {
                    labelLen = AttribWidth;
                }
                newColumn.setWidth(labelLen + "px");
                newColumn.setAlign("center");
                Button formatBtn = new Button();
                formatBtn.setSclass("ap-csv-importer-format-icon ap-hidden");
                formatBtn.setIconSclass("z-icon-wrench");
                final int fi = i;
                formatBtn.addEventListener("onClick", event -> {
                    // Clients.evalJavaScript("openPop(" + fi + ")");
                    Window w = (Window) sample.getPopUPBox().getFellow("pop_" + fi);
                    w.setStyle(w.getStyle().replace("hidden", "visible"));
                    MouseEvent me = (MouseEvent)event;
                    int x = me.getPageX();
                    int y = me.getPageY();
                    w.setLeft((x - 180) + "px");
                    w.setTop((y + 120) + "px");
                    Clients.evalJavaScript("adjustPos(" + fi + ")");
                });
                formatBtns[i] = formatBtn;
                newColumn.appendChild(formatBtn);
                myGrid.getColumns().appendChild(newColumn);
                myGrid.getColumns().setSizable(true);  // TODO: this looks fishy
            }

            Popup helpP = (Popup) window.getFellow("popUpHelp");

            if(sample.getHeader() != null) {
//                System.out.println("Automatic formatting here! " + sample.getLines().get(0));
                List<String> errorMessages = sample.automaticFormat();
                for (String errorMessage: errorMessages) {
                    Messagebox.show(errorMessage);
                }
                sample.setOtherTimestamps();
            }

            createPopUpTextBox(sample.getHeader().size(), popUPBox, helpP, sample.getLines().get(0), sample, formatBtns);
            sample.openPopUp(false);

            Integer timeStampPos = sample.getHeads().get("timestamp");
            if (timeStampPos != -1) {
                formatBtns[timeStampPos].setSclass("ap-csv-importer-format-icon");
            }
            Integer startTimeStampPos = sample.getHeads().get("startTimestamp");
            if (startTimeStampPos != -1) {
                formatBtns[startTimeStampPos].setSclass("ap-csv-importer-format-icon");
            }

            for (Map.Entry<Integer, String> entry : sample.getOtherTimeStampsPos().entrySet()) {
                formatBtns[entry.getKey()].setSclass("ap-csv-importer-format-icon");
            }

            Button setOtherAll = (Button) window.getFellow("setOtherAll");
            setOtherAll.setTooltiptext("Change all Ignore columns to Other.");
            setOtherAll.addEventListener("onClick", event -> sample.setOtherAll(window));

            Button setIgnoreAll = (Button) window.getFellow("setIgnoreAll");
            setIgnoreAll.setTooltiptext("Change all Other columns to Ignore.");
            setIgnoreAll.addEventListener("onClick", event -> sample.setIgnoreAll(window));

            // set grid model; display the first SAMPLE_SIZE rows
            ListModelList<String[]> indexedResult = new ListModelList<>();
            for (int i = 0; i < sample.getLines().size(); i++) {
                List<String> withIndex = new ArrayList<>();
                withIndex.add(String.valueOf(i+1));
                withIndex.addAll(sample.getLines().get(i));
                String[] s = withIndex.toArray(new String[0]);
                indexedResult.add(s);
            }
            if (sample.getHeader().size() == SAMPLE_SIZE) {
                String[] continued = {"...",""};
                indexedResult.add(continued);
            }
            myGrid.setModel(indexedResult);

            //set grid row renderer
            GridRendererController rowRenderer = new GridRendererController();
            rowRenderer.setAttribWidth(AttribWidth);
            myGrid.setRowRenderer(rowRenderer);

            Button toXESButton = (Button) window.getFellow("toXESButton");
            toXESButton.setDisabled(false);

            window.setTitle("CSV Importer - " + media.getName());

            return sample;

        } catch (InvalidCSVException | IOException | NullPointerException e ) {
            e.printStackTrace();
            Messagebox.show("Failed to display the log. Try different encoding.",
                    "Error", Messagebox.OK,Messagebox.ERROR);
            window.detach();
            return null;
        }
    }

    private static void createPopUpTextBox(int colNum, Div popUPBox, Popup helpP, List<String> sampleLine, LogSample sample, Button[] formatBtns){
        for(int i = 0; i < colNum; i++){
            Window item = new Window();
            item.setId(LogSample.popupID + i);
            item.setWidth((AttribWidth) + "px");
            item.setMinheight(100);
            item.setClass("p-1");
            item.setBorder("normal");
//            item.setStyle("margin-left:" + (i==0? IndexColumnWidth: (i*AttribWidth) + IndexColumnWidth )  +
//                    "px; position: absolute; z-index: 10; visibility: hidden; top:3px;");
            item.setStyle("position: fixed; z-index: 10; visibility: hidden;");

            Button sp = new Button();
//            sp.setLabel("-");
//            sp.setImage("img/close-icon.png");
//            sp.setIconSclass("z-icon-compress");
            sp.setStyle("margin-right:3px; float: right; line-height: 10px; min-height: 5px; padding:3px;");
            sp.setIconSclass("z-icon-times");
//            sp.setZclass("min-height: 16px;");
            A hidelink = new A();
            hidelink.appendChild(sp);
            sp.addEventListener("onClick", (Event event) -> item.setStyle(item.getStyle().
                    replace("visible", "hidden")));

            Textbox textbox = new Textbox();
            textbox.setId(LogSample.textboxID + i);
            textbox.setWidth("98%");
            textbox.setPlaceholder("dd-MM-yyyy HH:mm:ss");
            textbox.setPopup(helpP);
            textbox.setClientAttribute("spellcheck", "false");
            textbox.setPopupAttributes(helpP, "after_start","","","toggle");

            textbox.addEventListener("onChanging", (InputEvent event) -> {
                if(StringUtils.isBlank(event.getValue()) || event.getValue().length() < 6) {
                    textbox.setPlaceholder("Specify timestamp format");
                }
                if(!event.getValue().isEmpty()){
                    sample.tryParsing(event.getValue(), Integer.parseInt(textbox.getId().replace(LogSample.textboxID,"")));
                }
            });
            Label check_lbl = new Label();
            check_lbl.setId(LogSample.labelID + i);
            item.appendChild(check_lbl);
            item.appendChild(hidelink);
            item.appendChild(textbox);

            popUPBox.appendChild(item);
        }
        popUPBox.clone();

        sample.setPopUPBox(popUPBox);
        sample.setFormatBtns(formatBtns);
    }



    // FileImporterPlugin implementation

    @Override
    public Set<String> getFileExtensions() {
        return new HashSet<>(Collections.singletonList("csv"));
    }

    @Override
    public void importFile(Media media, PortalContext portalContext, boolean isPublic) {
        LOGGER.info("Import file: " + media.getName());

        this.isPublic = isPublic;

        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(),
                    "zul/csvimporter.zul", null, null);

            if (window == null) {
                return;
            }

            // Initialize the character encoding drop-down menu
            Combobox setEncoding = (Combobox) window.getFellow("setEncoding");
            setEncoding.setModel(new ListModelList<>(new String[]{
                "UTF-8",
                "UTF-16",
                "windows-1250 (Eastern European)",
                "windows-1251 (Cyrillic)",
                "windows-1252 (Latin)",
                "windows-1253 (Greek)",
                "windows-1254 (Turkish)",
                "windows-1255 (Hebrew)",
                "windows-1256 (Arabic)",
                "windows-1258 (Vietnamese)",
                "windows-31j (Japanese)",
                "ISO-2022-CN (Chinese)"
            }));

            setEncoding.addEventListener("onSelect", event -> sample = displayCSVContent(csvImporterLogic, media, window));

            String[] allowedExtensions = {"csv", "xls", "xlsx"};
            if (!Arrays.asList(allowedExtensions).contains(media.getFormat())) {
                Messagebox.show("Please select CSV file!", "Error", Messagebox.OK, Messagebox.ERROR);
                return;
            }

            sample = displayCSVContent(csvImporterLogic, media, window);

            Button toXESButton = (Button) window.getFellow("toXESButton");
            toXESButton.addEventListener("onClick", event -> {

                String clearEncoding;

                if(setEncoding.getValue().contains(" ")) {
                    clearEncoding = setEncoding.getValue().substring(0, setEncoding.getValue().indexOf(' '));
                } else {
                    clearEncoding = setEncoding.getValue();
                }

                try (CSVReader reader = newCSVReader(media, clearEncoding)) {
                    LogModel xesModel = csvImporterLogic.prepareXesModel(reader, sample, MAX_ERROR_FRACTION);

                    if (xesModel.getErrorCount() > 0) {
                        String notificationMessage;
                        notificationMessage = "Imported: " + xesModel.getLineCount() + " row(s), with " + xesModel.getErrorCount() + " invalid row(s) being amended.  \n\n" +
                                "Invalid rows: \n";

                        for (int i = 0; i < Math.min(xesModel.getInvalidRows().size(), 5); i++) {
                            notificationMessage = notificationMessage + xesModel.getInvalidRows().get(i) + "\n";
                        }

                        if (xesModel.getInvalidRows().size() > 5) {
                            notificationMessage = notificationMessage + "\n ...";
                        }
                        Messagebox.show(notificationMessage
                                , "Invalid CSV File",
                                new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
                                new String[]{"Download Error Report", "Cancel"}, Messagebox.ERROR, null,
                                (EventListener) evt -> {
                                    if (evt.getName().equals("onOK")) {
                                        File tempFile = File.createTempFile("Error_Report", ".txt");
                                        FileWriter writer = new FileWriter(tempFile);
                                        for(String str: xesModel.getInvalidRows()) {
                                            writer.write(str + System.lineSeparator());
                                        }
                                        writer.close();
                                        Filedownload.save(new FileInputStream(tempFile),
                                                "text/plain; charset-UTF-8", "Error_Report_CSV.txt");
                                    }
                                });
                    }

                    if (xesModel.getErrorCheck()) {
                       Messagebox.show("Invalid fields detected. \nSelect Skip rows to upload" +
                                        " log by skipping all rows " + "containing invalid fields.\n Select Skip " +
                                        "columns upload log by skipping the entire columns " + "containing invalid fields.\n ",
                                "Confirm Dialog",
                                new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.IGNORE, Messagebox.Button.CANCEL},
                                new String[]{"Skip rows", "Skip columns", "Cancel"},
                                Messagebox.QUESTION, null, (EventListener) evt -> {
                                    if(evt.getName().equals("onOK")) {
                                        for (int i = 0; i < xesModel.getRows().size(); i++) {
                                            for (Map.Entry<String, Timestamp> entry : xesModel.getRows().get(i).getOtherTimestamps().entrySet()) {
                                                if (entry.getKey() == null) {
                                                    continue;
                                                }
                                                long tempLong = entry.getValue().getTime();
                                                Calendar cal = Calendar.getInstance();
                                                cal.setTimeInMillis(tempLong);
                                                if (cal.get(Calendar.YEAR) == 1900) {
                                                    System.out.println("Invalid timestamp. Entry Removed.");
                                                    xesModel.getRows().remove(i);
                                                }
                                            }
                                        }

                                        Messagebox.show("Hello Ok!");
                                        // create XES file
                                        XLog xlog = xesModel.getXLog();
                                        if (xlog != null) {
                                            saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""), portalContext);
                                        }

                                        window.invalidate();
                                        window.detach();

                                    } else if (evt.getName().equals("onIgnore")) {
                                        for (int i = 0; i < xesModel.getRows().size(); i++) {
                                            xesModel.getRows().get(i).setOtherTimestamps(null);
                                        }
                                        Messagebox.show("Hello Ignore!");
                                        // create XES file
                                        XLog xlog = xesModel.getXLog();
                                        if (xlog != null) {
                                            saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""), portalContext);
                                        }

                                        window.invalidate();
                                        window.detach();
                                    } else {
                                        //
                                    }
                                });
                    } else {
                        Messagebox.show("Total number of lines processed: " + xesModel.getLineCount() + "\n Your file " +
                                "has been imported.");
                        XLog xlog = xesModel.getXLog();
                        if (xlog != null) {
                            saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""), portalContext);
                        }

                        window.invalidate();
                        window.detach();
                    }

                } catch (InvalidCSVException e) {
                    if (e.getInvalidRows() == null) {
                        Messagebox.show(e.getMessage() , "Invalid CSV File", Messagebox.OK, Messagebox.ERROR);

                    } else {
                        Messagebox.show(e.getMessage() , "Invalid CSV File",
                            new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
                            new String[]{"Download Error Report", "Cancel"}, Messagebox.ERROR, null,
                                new EventListener() {
                                public void onEvent(Event evt) throws Exception {
                                    if (evt.getName().equals("onOK")) {
                                        File tempFile = File.createTempFile("Error_Report", ".txt");
                                        try (FileWriter writer = new FileWriter(tempFile)) {
                                            for(String str: e.getInvalidRows()) {
                                                writer.write(str + System.lineSeparator());
                                            }
                                            Filedownload.save(new FileInputStream(tempFile),
                                                    "text/plain; charset-UTF-8", "Error_Report_CSV.txt");

                                        } finally {
                                            tempFile.delete();
                                        }
                                    }
                                }
                            }
                        );
                    }
                } catch (IOException e) {
                    LOGGER.error("Failed to read");
                }
            });

            Button cancelButton = (Button) window.getFellow("cancelButton");
            cancelButton.addEventListener("onClick", event -> {
                window.invalidate();
                window.detach();
           });

            window.doModal();

        } catch (IOException e) {
            LOGGER.warn("Unable to execute sample method", e);
            Messagebox.show("Unable to import file : " + e, "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
