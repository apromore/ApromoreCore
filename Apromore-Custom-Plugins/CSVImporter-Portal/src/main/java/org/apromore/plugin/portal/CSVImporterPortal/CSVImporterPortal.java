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
public class CSVImporterPortal implements FileImporterPlugin, Constants {

    @Inject
    private CSVImporterLogic csvImporterLogic;
    @Inject
    private EventLogService eventLogService;

    public void setCsvImporterLogic(CSVImporterLogic newCSVImporterLogic) {
        this.csvImporterLogic = newCSVImporterLogic;
    }

    public void setEventLogService(EventLogService newEventLogService) {
        this.eventLogService = newEventLogService;
    }

    private Media media;
    private PortalContext portalContext;
    private boolean isLogPublic;
    private Window window;
    private LogSample sample;

    private Integer attribWidth = 180;
    private Integer indexColumnWidth = 50;


    // FileImporterPlugin implementation
    @Override
    public Set<String> getFileExtensions() {
        return new HashSet<>(Collections.singletonList("csv"));
    }

    @Override
    public void importFile(Media media, PortalContext portalContext, boolean isLogPublic) {
        // IMPORTANT TO SET GLOBAL USED VARIABLES:
        this.media = media;
        this.portalContext = portalContext;
        this.isLogPublic = isLogPublic;
        this.window = null;

        if (!Arrays.asList(allowedExtensions).contains(media.getFormat())) {
            Messagebox.show("Please select CSV file!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        try {
            this.window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/csvimporter.zul", null, null);
        } catch (IOException e) {
            Messagebox.show("Unable to import file : " + e, "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        // Initialize the character encoding drop-down menu
        Combobox setEncoding = (Combobox) window.getFellow(setEncodingId);
        setEncoding.setModel(new ListModelList<>(fileEncoding));
        setEncoding.addEventListener("onSelect", event -> {
                    this.sample = getCSVSample();
                    if (sample != null) renderGridContent();
                }
        );

        this.sample = getCSVSample();
        if (sample != null) {
            setUpUI();
            window.doModal();
        }
    }


    private LogSample getCSVSample() {
        String charset = getFileEncoding();
        try (CSVReader csvReader = newCSVReader(charset)) {
            return csvImporterLogic.sampleCSV(csvReader, logSampleSize);
        } catch (InvalidCSVException e) {
            Messagebox.show(e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
            window.detach();
            return null;
        }
        catch (IOException e) {
            Messagebox.show("Failed to read the log. Try different encoding.", "Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
            window.detach();
            return null;
        }
    }

    private String getFileEncoding() {
        Combobox setEncoding = (Combobox) window.getFellow(setEncodingId);
        return setEncoding.getValue().contains(" ")
                ? setEncoding.getValue().substring(0, setEncoding.getValue().indexOf(' '))
                : setEncoding.getValue();
    }

    private CSVReader newCSVReader(String charset) throws InvalidCSVException, IOException {
        // Guess at ethe separator character
        Reader reader = media.isBinary() ? new InputStreamReader(media.getStreamData(), charset) : media.getReaderData();
        BufferedReader brReader = new BufferedReader(reader);
        String firstLine = brReader.readLine();
        char separator = getMaxOccurringChar(firstLine);

        if (separator == Character.UNASSIGNED || !(new String(supportedSeparators).contains(String.valueOf(separator)))) {
            throw new InvalidCSVException("Log is invalid, separator is not supported.");
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
            throw new IllegalArgumentException("Log is invalid, header must have non-empty value!");
        }
        char maxchar = ' ';
        int maxcnt = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isLetter(str.charAt(i))) {
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


    private void setUpUI(){
        if (sample.getHeader() != null) {
            // TODO: Review sample.automaticFormat()
            List<String> errorMessages = sample.automaticFormat();
            for (String errorMessage : errorMessages) {
                Messagebox.show(errorMessage);
            }
            sample.setOtherTimestamps();
        }

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

        setUpCSVGrid();
        renderGridContent();
        createPopUpTextBox();
        setUpButtons();
    }

    private void setUpCSVGrid() {
        // Present the beginning of the log to the user so that they can confirm/add configuration
        Grid myGrid = (Grid) window.getFellow(myGridId);

        myGrid.getChildren().clear();
        (new Columns()).setParent(myGrid);
        myGrid.getColumns().getChildren().clear();


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
        indexCol.setWidth(indexColumnWidth + "px");
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
            if (labelLen < attribWidth) {
                labelLen = attribWidth;
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
                MouseEvent me = (MouseEvent) event;
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
        // TODO: REVIEW sample.setFormatBtns
        sample.setFormatBtns(formatBtns);
    }

    private void renderGridContent(){
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
        if (sample.getHeader().size() == logSampleSize) {
            String[] continued = {"...", ""};
            indexedResult.add(continued);
        }
        myGrid.setModel(indexedResult);


        //set grid row renderer
        GridRendererController rowRenderer = new GridRendererController();
        rowRenderer.setAttribWidth(attribWidth);
        myGrid.setRowRenderer(rowRenderer);
    }

    private void createPopUpTextBox() {
        Div popUPBox = (Div) window.getFellow(popUPBoxId);
        if (popUPBox != null) {
            popUPBox.getChildren().clear();
        }
        Popup helpP = (Popup) window.getFellow("popUpHelp");
        int colNum = sample.getHeader().size();

        for (int i = 0; i < colNum; i++) {
            Window item = new Window();
            item.setId(LogSample.popupID + i);
            item.setWidth((attribWidth) + "px");
            item.setMinheight(100);
            item.setClass("p-1");
            item.setBorder("normal");
            item.setStyle("position: fixed; z-index: 10; visibility: hidden;");

            Button sp = new Button();
            sp.setStyle("margin-right:3px; float: right; line-height: 10px; min-height: 5px; padding:3px;");
            sp.setIconSclass("z-icon-times");

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
            textbox.setPopupAttributes(helpP, "after_start", "", "", "toggle");

            textbox.addEventListener("onChanging", (InputEvent event) -> {
                if (StringUtils.isBlank(event.getValue()) || event.getValue().length() < 6) {
                    textbox.setPlaceholder("Specify timestamp format");
                }
                if (!event.getValue().isEmpty()) {
                    sample.tryParsing(event.getValue(), Integer.parseInt(textbox.getId().replace(LogSample.textboxID, "")));
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
        // TODO: REVIEW sample.openPopUp
        sample.openPopUp(false);
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
        String charset = getFileEncoding();

        try (CSVReader reader = newCSVReader(charset)) {
            LogModel xesModel = csvImporterLogic.prepareXesModel(reader, sample, maxErrorFraction);

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
                                for (String str : xesModel.getInvalidRows()) {
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
                            if (evt.getName().equals("onOK")) {
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
                                    saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""));
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
                                    saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""));
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
                    saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""));
                }

                window.invalidate();
                window.detach();
            }

        } catch (InvalidCSVException e) {
            if (e.getInvalidRows() == null) {
                Messagebox.show(e.getMessage(), "Invalid CSV File", Messagebox.OK, Messagebox.ERROR);

            } else {
                Messagebox.show(e.getMessage(), "Invalid CSV File",
                        new Messagebox.Button[]{Messagebox.Button.OK, Messagebox.Button.CANCEL},
                        new String[]{"Download Error Report", "Cancel"}, Messagebox.ERROR, null,
                        new EventListener() {
                            public void onEvent(Event evt) throws Exception {
                                if (evt.getName().equals("onOK")) {
                                    File tempFile = File.createTempFile("Error_Report", ".txt");
                                    try (FileWriter writer = new FileWriter(tempFile)) {
                                        for (String str : e.getInvalidRows()) {
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
            Messagebox.show("Failed to read file: " + e, "Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            Messagebox.show("Failed to save file: " + e, "Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
    }

    private void saveLog(XLog xlog, String name) throws Exception {

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

        portalContext.refreshContent();
    }

//    private void handleCSVImporterLogicExceptions() {
//        StringBuilder errormessage = new StringBuilder();
//        for (String m : csvImporterLogic.getLogError().getErrorMessages()) {
//            errormessage.append(m);
//            errormessage.append(System.lineSeparator());
//        }
//        if (errormessage.length() != 0) {
//            Messagebox.show(errormessage.toString(), "Error", Messagebox.OK, Messagebox.ERROR);
//        }
//    }
}
