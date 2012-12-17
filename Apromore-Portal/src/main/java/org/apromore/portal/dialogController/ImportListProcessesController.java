package org.apromore.portal.dialogController;

import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionImport;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.bind.JAXBException;

public class ImportListProcessesController extends BaseController {

    private MenuController menuC;
    private MainController mainC;
    private Window importProcessesWindow;
    private Button okButton;
    private Button uploadButton;
    private Button cancelButton;
    private Label filenameLabel;
    private Label supportedExtL;
    private String extension;
    private String fileOrArchive;
    private String nativeType;
    private String ignoredFiles;
    private Media media;
    private List<ImportOneProcessController> toImportList; // List of imports to be done
    private List<ImportOneProcessController> importedList; // List of imports successfully completed

    public ImportListProcessesController(MenuController menuC, MainController mainC) throws DialogException {

        this.ignoredFiles = "";
        this.mainC = mainC;
        this.menuC = menuC;
        this.toImportList = new ArrayList<ImportOneProcessController>();
        this.importedList = new ArrayList<ImportOneProcessController>();

        try {
            final Window win = (Window) Executions.createComponents("macros/importProcesses.zul", null, null);
            this.importProcessesWindow = (Window) win.getFellow("importProcessesWindow");
            this.okButton = (Button) this.importProcessesWindow.getFellow("okButtonImportProcesses");
            this.uploadButton = (Button) this.importProcessesWindow.getFellow("uploadButton");
            this.cancelButton = (Button) this.importProcessesWindow.getFellow("cancelButtonImportProcesses");
            this.filenameLabel = (Label) this.importProcessesWindow.getFellow("filenameLabel");
            this.supportedExtL = (Label) this.importProcessesWindow.getFellow("supportedExt");
            // build the list of supported extensions to display
            String supportedExtS = "zip";
            Set<String> supportedExt = this.mainC.getNativeTypes().keySet();
            Iterator<String> it = supportedExt.iterator();
            while (it.hasNext()) {
                supportedExtS += ", " + it.next();
            }
            this.supportedExtL.setValue(supportedExtS);
            // event listeners
            importProcessesWindow.addEventListener("onLater", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    extractArchiveOrFile();
                    Clients.clearBusy();
                }
            });
            uploadButton.addEventListener("onUpload", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    uploadFile((UploadEvent) event);
                }
            });
            okButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    Clients.showBusy("Processing...");
                    Events.echoEvent("onLater", importProcessesWindow, null);
                }
            });
            cancelButton.addEventListener("onClick", new EventListener() {
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
        this.importProcessesWindow.detach();
    }

    /**
     * Upload file: an archive or an xml file
     * @param event
     * @throws InterruptedException
     */
    private void uploadFile(UploadEvent event) throws InterruptedException {
        try {
            // derive file type from its extension
            String fileType;
            this.media = event.getMedia();
            this.fileOrArchive = event.getMedia().getName();
            String[] list_extensions = this.fileOrArchive.split("\\.");
            this.extension = list_extensions[list_extensions.length - 1];
            if (this.extension.compareTo("zip") == 0) {
                fileType = "zip archive";
            } else {
                fileType = this.mainC.getNativeTypes().get(this.extension);
                if (fileType == null) {
                    throw new ExceptionImport("Unsupported extension.");
                }
                this.nativeType = fileType;
            }

            // now the file is uploaded, Ok button could be enabled
            this.okButton.setDisabled(false);
            this.filenameLabel.setValue(this.fileOrArchive + " (file/model type is " + fileType + ")");
        } catch (ExceptionImport e) {
            Messagebox.show("Upload failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (Exception e) {
            Messagebox.show("Repository not available (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    /**
     * Read uploaded file: zip archive or file which contains native description
     * in one of the supported native format.
     * zip or tar: extract files and import each if possible
     * file: import
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    private void extractArchiveOrFile() throws InterruptedException {
        try {
            if (this.extension.compareTo("zip") == 0) {
                String extension = null;
                String entryName = null;
                String nativeType = null;
                this.ignoredFiles = "";
                String defaultProcessName = null;
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
                String defaultProcessName = this.fileOrArchive.split("\\.")[0];
                importProcess(this.mainC, this, this.media.getStreamData(), defaultProcessName, this.nativeType, this.fileOrArchive);
            }

        } catch (JAXBException e) {
            Messagebox.show("Import failed (File doesn't conform Xschema specification: " + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (Exception e) {
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private void importProcess(MainController mainC, ImportListProcessesController importC, InputStream xml_is, String processName, String nativeType, String filename) throws SuspendNotAllowedException, InterruptedException, JAXBException, IOException, ExceptionDomains, ExceptionAllUsers {
        ImportOneProcessController oneImport = new ImportOneProcessController(mainC, importC, xml_is, processName, nativeType, filename);
        this.toImportList.add(oneImport);
    }

    /*
      * cancel all remaining imports
      */
    public void cancelAll() throws InterruptedException, IOException {
        for (int i = 0; i < this.toImportList.size(); i++) {
            if (this.toImportList.get(i).getImportOneProcessWindow() != null) {
                this.ignoredFiles += ", " + this.toImportList.get(i).getFileName();
                this.toImportList.get(i).getImportOneProcessWindow().detach();
            }
        }
        this.toImportList.clear();
        reportImport();
        cancel();
    }

    public List<ImportOneProcessController> getImportedList() {
        if (importedList == null) {
            importedList = new ArrayList<ImportOneProcessController>();
        }
        return this.importedList;
    }

    public List<ImportOneProcessController> getToImportList() {
        if (toImportList == null) {
            toImportList = new ArrayList<ImportOneProcessController>();
        }
        return this.toImportList;
    }

    // remove from the list of processes to be imported
    // if the list exhausted, display a message and terminate import
    public void deleteFromToBeImported(ImportOneProcessController importOneProcess) throws IOException, InterruptedException {
        this.toImportList.remove(importOneProcess);

        if (this.toImportList.size() == 0) {
            reportImport();
            // clean folder and close window
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
    public void importAllProcess(String version, String domain) throws InterruptedException, IOException {
        List<ImportOneProcessController> importAll = new ArrayList<ImportOneProcessController>();
        importAll.addAll(this.toImportList);
        for (int i = 0; i < importAll.size(); i++) {
            ImportOneProcessController importOneProcess = importAll.get(i);
            try {
                importOneProcess.importProcess(domain, UserSessionManager.getCurrentUser().getUsername());
                // process successfully imported
            } catch (IOException e) {
                e.printStackTrace();
                Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        }
        this.cancelAll();
    }
}
