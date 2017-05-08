/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.portal.dialogController;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apromore.model.ImportLogResultType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

public class ImportController extends BaseController {

    private MainController mainC;
    private Window importWindow;

    private String nativeType;
    private String ignoredFiles;

    private Media media = null;
    private byte[] logByteArray = null;
    private String fileName = null;
    private String fileType = null;
    private String extension;
    private Label fileNameLabel;
    private boolean isPublic;

    private Button okButton;

    private List<ImportOneProcessController> toImportList = new ArrayList<>();
    private List<ImportOneProcessController> importedList = new ArrayList<>();

    public ImportController(MainController mainC) throws DialogException {
        this.ignoredFiles = "";
        this.mainC = mainC;

        try {
            final Window win = (Window) Executions.createComponents("macros/import.zul", null, null);
            this.importWindow = (Window) win.getFellow("importWindow");
            Button uploadButton = (Button) this.importWindow.getFellow("uploadButton");
            Button cancelButton = (Button) this.importWindow.getFellow("cancelButtonImport");
            okButton = (Button) this.importWindow.getFellow("okButtonImport");
            this.fileNameLabel = (Label) this.importWindow.getFellow("fileNameLabel");
            Label supportedExtL = (Label) this.importWindow.getFellow("supportedExt");
            isPublic = ((Checkbox) this.importWindow.getFellow("public")).isChecked();

            // build the list of supported extensions to display
            String supportedExtS = "xes, xes.gz, mxml, mxml.gz, zip";
            Set<String> supportedExt = this.mainC.getNativeTypes().keySet();
            for (String aSupportedExt : supportedExt) {
                supportedExtS += ", " + aSupportedExt;
            }

            supportedExtL.setValue(supportedExtS);

            uploadButton.addEventListener("onUpload", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    uploadFile((UploadEvent) event);
                }
            });
            okButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    importFile();
                }
            });
            cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });

            win.doModal();
        } catch (Exception e) {
            throw new DialogException("Error in importProcesses controller: " + e.getMessage());
        }
    }

    private void cancel() throws IOException {
        this.importWindow.detach();
    }

    /**
     * Upload file: an archive or an xml file
     * @param event the event to process.
     * @throws InterruptedException
     */
//    private void uploadFile(UploadEvent event) throws InterruptedException {
//        try {
//            // derive file type from its extension
//            String fileType;
//            this.media = event.getMedia();
//            this.fileOrArchive = event.getMedia().getName();
//            String[] list_extensions = this.fileOrArchive.split("\\.");
//            this.extension = list_extensions[list_extensions.length - 1];
//            if (this.extension.compareTo("zip") == 0) {
//                fileType = "zip archive";
//            } else {
//                fileType = this.mainC.getNativeTypes().get(this.extension);
//                if (fileType == null) {
//                    throw new ExceptionImport("Unsupported extension.");
//                }
//                this.nativeType = fileType;
//            }
//
//            this.fileNameLabel.setValue(this.fileOrArchive + " (file/model type is " + fileType + ")");
//
//            // Simulate the ok button ?? I don't think this is correct.
//            Clients.showBusy("Processing...");
//            Events.echoEvent("onLater", importWindow, null);
//        } catch (ExceptionImport e) {
//            Messagebox.show("Upload failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
//        } catch (Exception e) {
//            Messagebox.show("Repository not available (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
//        }
//    }

    private void uploadFile(UploadEvent event) throws ExceptionFormats, ExceptionImport {
        media = event.getMedia();
        fileNameLabel.setStyle("color: blue");
        fileNameLabel.setValue(media.getName());
        logByteArray = media.getByteData();
        fileName = media.getName();
        String[] list_extensions = fileName.split("\\.");
        extension = list_extensions[list_extensions.length - 1];
        if(!extension.equalsIgnoreCase("zip") && !extension.equalsIgnoreCase("gz") && !extension.equalsIgnoreCase("xes") && !extension.equalsIgnoreCase("mxml")) {
            fileType = this.mainC.getNativeTypes().get(extension);
            if (fileType == null) {
                throw new ExceptionImport("Unsupported extension.");
            }
            nativeType = fileType;
        }
        okButton.setDisabled(false);
    }

    private void importFile() throws InterruptedException, IOException, ExceptionDomains, ExceptionAllUsers, JAXBException {
        if(extension.equals("zip")) {
            extractArchiveOrFile();
        }else if(fileName.toLowerCase().endsWith("xes") || fileName.toLowerCase().endsWith("xes.gz") || fileName.toLowerCase().endsWith("mxml") || fileName.toLowerCase().endsWith("mxml.gz")) {
            importLog();
        }else {
            importProcess(this.mainC, this, this.media.getStreamData(), this.fileName.split("\\.")[0], this.nativeType, this.fileName);
        }
    }

    private void importLog() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(logByteArray, 0, logByteArray.length);
            InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());

            Integer folderId = 0;
            if (UserSessionManager.getCurrentFolder() != null) {
                folderId = UserSessionManager.getCurrentFolder().getId();
            }

            extension = discoverExtension(fileName);
            System.out.println(fileName);
            System.out.println(extension);
            String logFileName = fileName.substring(0, fileName.indexOf(extension) - 1);

            getService().importLog(UserSessionManager.getCurrentUser().getUsername(), folderId, logFileName, inputStream,
                    extension, "", DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(), isPublic);

            mainC.refresh();
        } catch (Exception e) {
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }finally {
            closePopup();
        }
    }

    private void closePopup() {
        importWindow.detach();
    }

    private String discoverExtension(String logFileName) {
        if(logFileName.endsWith("mxml")) {
            return "mxml";
        }else if(logFileName.endsWith("mxml.gz")) {
            return "mxml.gz";
        }else if(logFileName.endsWith("xes")) {
            return "xes";
        }else if(logFileName.endsWith("xes.gz")) {
            return "xes.gz";
        }
        return null;
    }

    /**
     * Read uploaded file: zip archive or file which contains native description
     * in one of the supported native format.
     * zip or tar: extract files and import each if possible
     * file: import
     * @throws InterruptedException
     */
    private void extractArchiveOrFile() throws InterruptedException {
        try {
            if (this.extension.compareTo("zip") == 0) {
                String extension;
                String entryName;
                String nativeType;
                this.ignoredFiles = "";
                String defaultProcessName;
                ZipInputStream zipIS = new ZipInputStream(this.media.getStreamData());
                ZipEntry zipEntry;
                while ((zipEntry = zipIS.getNextEntry()) != null) {
                    entryName = zipEntry.getName();
                    if (!zipEntry.isDirectory()) {
                        extension = entryName.split("\\.")[entryName.split("\\.").length - 1];
                        defaultProcessName = entryName.split("\\.")[0];
                        nativeType = this.mainC.getNativeTypes().get(extension);
                        if (nativeType == null) {
                            this.ignoredFiles += entryName + ", ";
                        } else {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            int size;
                            byte[] buffer = new byte[2048];
                            while ((size = zipIS.read(buffer, 0, buffer.length)) != -1) {
                                bos.write(buffer, 0, size);
                            }
                            InputStream zipEntryIS = new ByteArrayInputStream(bos.toByteArray());
                            importProcess(this.mainC, this, zipEntryIS, defaultProcessName, nativeType, entryName);
                            bos.flush();
                            bos.close();
                        }
                    } else {
                        this.ignoredFiles += entryName + ", ";
                    }
                }
            } else {
                // Case of a single file: import it.
                String defaultProcessName = this.fileName.split("\\.")[0];
                importProcess(this.mainC, this, this.media.getStreamData(), defaultProcessName, this.nativeType, this.fileName);
            }

        } catch (JAXBException e) {
            Messagebox.show("Import failed (File doesn't conform Xschema specification: " + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (Exception e) {
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private void importProcess(MainController mainC, ImportController importC, InputStream xml_is, String processName, String nativeType, String filename) throws SuspendNotAllowedException, InterruptedException, JAXBException, IOException, ExceptionDomains, ExceptionAllUsers {
        ImportOneProcessController oneImport = new ImportOneProcessController(mainC, importC, xml_is, processName, nativeType, filename, isPublic);
        this.toImportList.add(oneImport);
    }

    /*
      * cancel all remaining imports
      */
    public void cancelAll() throws InterruptedException, IOException {
        for (ImportOneProcessController aToImportList : this.toImportList) {
            if (aToImportList.getImportOneProcessWindow() != null) {
                this.ignoredFiles += ", " + aToImportList.getFileName();
                aToImportList.getImportOneProcessWindow().detach();
            }
        }
        this.toImportList.clear();
        reportImport();
        cancel();
    }

    public List<ImportOneProcessController> getImportedList() {
        if (importedList == null) {
            importedList = new ArrayList<>();
        }
        return this.importedList;
    }

    public List<ImportOneProcessController> getToImportList() {
        if (toImportList == null) {
            toImportList = new ArrayList<>();
        }
        return this.toImportList;
    }

    // remove from the list of processes to be imported
    // if the list exhausted, display a message and terminate import
    public void deleteFromToBeImported(ImportOneProcessController importOneProcess) throws IOException, InterruptedException {
        this.toImportList.remove(importOneProcess);

        if (this.toImportList.size() == 0) {
            reportImport();
            cancel();
        }
    }

    public void reportImport() throws InterruptedException {
        String report = "Import of " + this.importedList.size();
        if (this.importedList.size() == 0) {
            report += " process.";
        }
        if (this.importedList.size() == 1) {
            report += " process completed.";
        } else if (this.importedList.size() > 1) {
            report += " processes completed.";
        }
        if (this.ignoredFiles.compareTo("") != 0) {
            report += "\n (" + this.ignoredFiles + " ignored).";
        }
        this.mainC.displayMessage(report);
    }

    /* Import all remaining files. Called from ImportOneProcessController after user clicked "OK all"
      * Apply default values to all file still to be imported:
      * - version name
      * - domain
      */
    public void importAllProcess(String domain) throws InterruptedException, IOException {
        List<ImportOneProcessController> importAll = new ArrayList<>();
        importAll.addAll(this.toImportList);
        for (ImportOneProcessController importOneProcess : importAll) {
            try {
                importOneProcess.importProcess(domain, UserSessionManager.getCurrentUser().getUsername());
            } catch (IOException e) {
                e.printStackTrace();
                Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        }
        this.cancelAll();
    }
}
