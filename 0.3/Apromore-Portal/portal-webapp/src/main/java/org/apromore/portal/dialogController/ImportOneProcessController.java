package org.apromore.portal.dialogController;

import org.apromore.portal.common.Utils;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.exception.ExceptionImportAllMissing;
import org.apromore.model.ProcessSummaryType;
import org.wfmc._2008.xpdl2.PackageType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImportOneProcessController extends BaseController {

    private MainController mainC;
    private ImportListProcessesController importProcessesC;
    private Window importOneProcessWindow;
    private String fileName;
    private Label defaultOwner;
    private Textbox documentationTb;
    private Textbox lastUpdateTb;
    private Textbox creationDateTb;
    private Textbox processNameTb;
    private Textbox versionNameTb;
    private SelectDynamicListController domainCB;
    private SelectDynamicListController ownerCB;
    private Radio fakeEventsNoR;
    private Radio fakeEventsYesR;
    private InputStream nativeProcess; // the input stream read from uploaded file
    private String nativeType;
    private Button resetButton;
    private Button okButton;
    private Button okForAllButton;
    private Button cancelButton;
    private Button cancelAllButton;

    private String username;
    private String processName;
    private String readVersionName;
    private String readProcessName;
    private String readDocumentation;
    private String readCreated;
    private String readLastupdate;
    private String readAuthor;

    public ImportOneProcessController(MainController mainC, ImportListProcessesController importProcessesC, InputStream xml_is,
                                      String processName, String nativeType, String fileName)
            throws SuspendNotAllowedException, InterruptedException, JAXBException, IOException, ExceptionDomains, ExceptionAllUsers {

        this.importProcessesC = importProcessesC;
        this.mainC = mainC;
        this.username = this.mainC.getCurrentUser().getUsername();
        this.fileName = fileName;
        this.processName = processName;
        this.nativeProcess = xml_is;
        this.nativeType = nativeType;
        this.importOneProcessWindow = (Window) Executions.createComponents("macros/importOneProcess.zul", null, null);
        this.importOneProcessWindow.setTitle(this.importOneProcessWindow.getTitle() + " (file: " + this.fileName + ")");
        Rows rows = (Rows) this.importOneProcessWindow.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row processNameR = (Row) rows.getChildren().get(0);
        Row versionNameR = (Row) rows.getChildren().get(1);
        Row ownerR = (Row) rows.getChildren().get(2);
        Row creationDateR = (Row) rows.getChildren().get(3);
        Row lastUpdateR = (Row) rows.getChildren().get(4);
        Row documentationR = (Row) rows.getChildren().get(5);
        Row domainR = (Row) rows.getChildren().get(6);
        Row fakeEventsR = (Row) rows.getChildren().get(7);

        this.processNameTb = (Textbox) processNameR.getChildren().get(1);
        this.versionNameTb = (Textbox) versionNameR.getChildren().get(1);
        this.creationDateTb = (Textbox) creationDateR.getChildren().get(1);
        this.lastUpdateTb = (Textbox) lastUpdateR.getChildren().get(1);
        this.documentationTb = (Textbox) documentationR.getChildren().get(1);
        this.fakeEventsNoR = (Radio) fakeEventsR.getFirstChild().getNextSibling().getFirstChild();
        this.fakeEventsYesR = (Radio) this.fakeEventsNoR.getNextSibling();
        Div buttonsD = (Div) fakeEventsR.getNextSibling().getNextSibling().getFirstChild();
        this.okButton = (Button) buttonsD.getFirstChild();
        this.okForAllButton = (Button) buttonsD.getChildren().get(1);
        this.cancelButton = (Button) buttonsD.getChildren().get(2);
        this.cancelAllButton = (Button) buttonsD.getChildren().get(3);
        this.resetButton = (Button) buttonsD.getChildren().get(4);

        List<String> ownerNames = this.mainC.getUsers();
        this.defaultOwner = (Label) ownerR.getChildren().get(1);
        this.ownerCB = new SelectDynamicListController(ownerNames);
        this.ownerCB.setReference(ownerNames);
        this.ownerCB.setAutodrop(true);
        this.ownerCB.setWidth("85%");
        this.ownerCB.setHeight("100%");
        this.ownerCB.setAttribute("hflex", "1");
        ownerR.appendChild(ownerCB);

        List<String> domains = this.mainC.getDomains();
        this.domainCB = new SelectDynamicListController(domains);
        this.domainCB.setReference(domains);
        this.domainCB.setAutodrop(true);
        this.domainCB.setWidth("85%");
        this.domainCB.setHeight("100%");
        this.domainCB.setAttribute("hflex", "1");
        domainR.appendChild(domainCB);

        this.cancelAllButton.setVisible(this.importProcessesC.getToImportList().size() > 0);
        this.okForAllButton.setVisible(this.importProcessesC.getToImportList().size() > 0);
        reset();
        // check properties in npf_process: process name, version name, documentation, creation date,
        // last update, and author. Ask the user to give those which are missing
        // if native format is xpdl, these informations might exist in npf
        try {
            if (nativeType.compareTo("XPDL 2.1") == 0) {
                JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
                Unmarshaller u = jc.createUnmarshaller();
                JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(this.nativeProcess);
                PackageType pkg = rootElement.getValue();
                this.nativeProcess.reset();
                try {// get process author if defined
                    if (pkg.getRedefinableHeader().getAuthor().getValue().trim().compareTo("") != 0) {
                        readAuthor = pkg.getRedefinableHeader().getAuthor().getValue().trim();
                    }
                } catch (NullPointerException e) {
                    // do nothing
                }
                try {// get process name if defined
                    if (pkg.getName().trim().compareTo("") != 0) {
                        readProcessName = pkg.getName().trim();
                    }
                } catch (NullPointerException e) {
                    // do nothing
                }
                try {//get version name if defined
                    if (pkg.getRedefinableHeader().getVersion().getValue().trim().compareTo("") != 0) {
                        readVersionName = pkg.getRedefinableHeader().getVersion().getValue().trim();
                    }
                } catch (NullPointerException e) {
                    // do nothing
                }
                try {//get documentation if defined
                    if (pkg.getPackageHeader().getDocumentation().getValue().trim().compareTo("") != 0) {
                        readDocumentation = pkg.getPackageHeader().getDocumentation().getValue().trim();
                    }
                } catch (NullPointerException e) {
                    // do nothing
                }
                try {//get creation date if defined
                    if (pkg.getPackageHeader().getCreated().getValue().trim().compareTo("") != 0) {
                        readCreated = pkg.getPackageHeader().getCreated().getValue().trim();
                        //readCreated = Utils.xpdlDate2standardDate(readCreated);
                    }
                } catch (NullPointerException e) {
                    // do nothing
                }
                try {//get lastupdate date if defined
                    if (pkg.getPackageHeader().getModificationDate().getValue().trim().compareTo("") != 0) {
                        readLastupdate = pkg.getPackageHeader().getModificationDate().getValue().trim();
                        //readLastupdate = Utils.xpdlDate2standardDate(readLastupdate);
                    }
                } catch (NullPointerException e) {
                    // do nothing
                }
            } else if (nativeType.compareTo("EPML 2.0") == 0) {
                // as epml doesn't support process name, version, etc..
                // everything missing
                throw new ExceptionImportAllMissing();
            }
        } catch (ExceptionImportAllMissing e) {

        }
        this.processNameTb.setValue(readProcessName);
        this.versionNameTb.setValue(readVersionName);
        this.documentationTb.setValue(readDocumentation);
        this.creationDateTb.setValue(readCreated);
        this.lastUpdateTb.setValue(readLastupdate);
        if (ownerNames.contains(readAuthor)) {
            defaultOwner.setValue(readAuthor);
        }

        this.importOneProcessWindow.addEventListener("onLater", new EventListener() {
            public void onEvent(Event event) throws Exception {
                importAllProcess();
                Clients.clearBusy();
            }
        });

        this.ownerCB.addEventListener("onChange",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        SelectDynamicListController cb = (SelectDynamicListController) event.getTarget();
                        updateOwner(cb.getValue());
                    }
                });

        this.okButton.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        importProcess(domainCB.getValue(), username);
                    }
                });

        this.okForAllButton.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        Clients.showBusy("Processing...");
                        Events.echoEvent("onLater", importOneProcessWindow, null);
                    }
                });

        this.importOneProcessWindow.addEventListener("onOK",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        if (processNameTb.getValue().compareTo("") == 0
                                || versionNameTb.getValue().compareTo("") == 0) {
                            Messagebox.show("Please enter a value for each field.", "Attention", Messagebox.OK,
                                    Messagebox.EXCLAMATION);
                        } else {
                            importProcess(domainCB.getValue(), username);
                        }
                    }
                });
        this.cancelButton.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });
        this.cancelAllButton.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        cancelAll();
                    }
                });
        this.resetButton.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        reset();
                    }
                });
        this.importOneProcessWindow.doModal();
    }

    private void updateOwner(String owner) {
        this.defaultOwner.setValue(owner);
    }

    private void reset() {
        this.readVersionName = "0.1"; // default value for versionName if not found
        this.readProcessName = this.processName; // default value if not found
        this.readDocumentation = "";
        this.readCreated = Utils.getDateTime(); // default value for creationDate if not found
        this.readLastupdate = "";
        this.readAuthor = this.mainC.getCurrentUser().getUsername();
        this.fakeEventsNoR.setChecked(true);
        this.fakeEventsYesR.setChecked(false);
        this.processNameTb.setValue(readProcessName);
        this.versionNameTb.setValue(readVersionName);
        this.documentationTb.setValue(readDocumentation);
        this.creationDateTb.setValue(readCreated);
        this.lastUpdateTb.setValue(readLastupdate);
        this.defaultOwner.setValue(readAuthor);
    }

    private void cancel() throws InterruptedException, IOException {
        // delete process from the list of processes still to be imported
        this.importProcessesC.deleteFromToBeImported(this);
        closePopup();
    }

    private void closePopup() {
        this.importOneProcessWindow.detach();
    }

    /*
      * the user has clicked on cancel all button
      * cancelAll hosted by the DC which controls multiple file to import (importProcesses)
      */
    private void cancelAll() throws InterruptedException, IOException {
        this.importProcessesC.cancelAll();
    }

    public void importProcess(String domain, String owner) throws InterruptedException, IOException {
        try {
            ProcessSummaryType res =
                    getService().importProcess(owner, this.nativeType, this.processNameTb.getValue(),
                            this.versionNameTb.getValue(), this.nativeProcess, domain,
                            this.documentationTb.getValue(), this.creationDateTb.getValue().toString(),
                            this.lastUpdateTb.getValue().toString(), this.fakeEventsYesR.isChecked());
            // process successfully imported
            this.importProcessesC.getImportedList().add(this);
            this.mainC.displayNewProcess(res);
            /* keep list of domains update */
            this.domainCB.addItem(domain);
            // delete process from the list of processes still to be imported
            this.importProcessesC.deleteFromToBeImported(this);
        } catch (WrongValueException e) {
            e.printStackTrace();
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
                    Messagebox.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
                    Messagebox.ERROR);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
//                    Messagebox.ERROR);
        } finally {
            closePopup();
        }
    }

    /**
     * The user clicked "OK for all": the default values apply for all
     * process models still to import.
     *
     * @throws org.apromore.portal.exception.ExceptionImport
     *
     * @throws java.io.IOException
     * @throws InterruptedException
     * @throws WrongValueException
     */
    protected void importAllProcess() throws ExceptionImport, WrongValueException, InterruptedException, IOException {
        if (this.processNameTb.getValue().compareTo("") == 0
                || this.versionNameTb.getValue().compareTo("") == 0) {
            Messagebox.show("Please enter a value for each field.", "Attention", Messagebox.OK,
                    Messagebox.EXCLAMATION);
        } else {
            this.importProcessesC.importAllProcess(this.versionNameTb.getValue(), this.domainCB.getValue());
        }
    }

    public Window getImportOneProcessWindow() {
        return importOneProcessWindow;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDocumentation() {
        return documentationTb.getValue();
    }

    public String getNativeType() {
        return nativeType;
    }

    public String getLastUpdate() {
        return lastUpdateTb.getValue();
    }

    public String getCreated() {
        return creationDateTb.getValue();
    }

    public InputStream getNativeProcess() {
        return nativeProcess;
    }

    public Radio getFakeEventsYesR() {
        return fakeEventsYesR;
    }

}
