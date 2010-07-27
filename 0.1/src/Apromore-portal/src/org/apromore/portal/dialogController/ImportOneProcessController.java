package org.apromore.portal.dialogController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.wfmc._2008.xpdl2.PackageType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ImportOneProcessController extends Window {

	private MainController mainC;
	private ImportListProcessesController importProcessesC;
	private Window importOneProcessWindow;
	private String fileName;
	private String documentation;
	private String lastUpdate;
	private String created;
	private Textbox processName;
	private Textbox versionName;
	private Row domainR;
	private SelectDynamicListController domainCB;
	private InputStream nativeProcess; // the input stream read from uploaded file
	private String nativeType;
	private Button okButton;
	private Button okForAllButton;
	private Button cancelButton;
	private Button cancelAllButton;

	public ImportOneProcessController (MainController mainC, ImportListProcessesController importProcessesC, InputStream xml_is, 
			String processName, String nativeType, String fileName) 
	throws SuspendNotAllowedException, InterruptedException, JAXBException, IOException, ExceptionDomains {

		this.importProcessesC = importProcessesC;
		this.mainC = mainC;
		this.fileName = fileName;
		this.nativeProcess = xml_is;
		this.nativeType = nativeType;
		this.importOneProcessWindow = (Window) Executions.createComponents("macros/importOneProcess.zul", null, null);
		this.importOneProcessWindow.setTitle(this.importOneProcessWindow.getTitle() + " (file: " + this.fileName + ")");
		Rows rows = (Rows) this.importOneProcessWindow.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
		Row processNameR = (Row) rows.getFirstChild();
		Row versionNameR = (Row) processNameR.getNextSibling();
		this.domainR = (Row) versionNameR.getNextSibling();
		Div buttonsD = (Div) this.domainR.getNextSibling().getNextSibling().getFirstChild();
		this.processName = (Textbox) processNameR.getFirstChild().getNextSibling();
		this.versionName = (Textbox) versionNameR.getFirstChild().getNextSibling();
		
		this.okButton = (Button) buttonsD.getFirstChild();
		this.okForAllButton = (Button) buttonsD.getFirstChild().getNextSibling();
		this.cancelButton = (Button) buttonsD.getFirstChild().getNextSibling().getNextSibling();
		this.cancelAllButton = (Button) buttonsD.getFirstChild().getNextSibling().getNextSibling().getNextSibling();

		List<String> domains = this.mainC.getDomains();
		this.domainCB = new SelectDynamicListController(domains);
		this.domainCB.setReference(domains);
		this.domainCB.setId(fileName);
		this.domainCB.setAutodrop(true);
		this.domainCB.setWidth("85%");
		this.domainCB.setHeight("100%");
		this.domainCB.setAttribute("hflex", "1");
		this.domainR.appendChild(domainCB);

		this.cancelAllButton.setVisible(this.importProcessesC.getToImportList().size()>0);
		this.okForAllButton.setVisible(this.importProcessesC.getToImportList().size()>0);
		String readVersionName = "0.1"; // default value for versionName if not found
		String readProcessName = processName ; // default value if not found
		String readDocumentation = "" ; 
		String readCreated = "";
		String readLastupdate = "";
		// check properties in xml_process: process name, version name, documentation, creation date, last update
		// if native format is xpdl, extract information from xml file
		if (nativeType.compareTo("XPDL 2.1")==0) {

			JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
			Unmarshaller u = jc.createUnmarshaller();
			// as the InputStream nativeProcess needs to be read multiple times, place a
			// mark for future reset
			this.nativeProcess.mark(0);
			JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(this.nativeProcess);
			PackageType pkg = rootElement.getValue();

			// reset InputStream to previous mark
			this.nativeProcess.reset();

			try {// get process name if defined
				if (pkg.getName().trim().compareTo("")!=0) {
					readProcessName = pkg.getName().trim();
				}
			} catch (NullPointerException e) {
				// default value
			}
			try {//get version name if defined
				if (pkg.getRedefinableHeader().getVersion().getValue().trim().compareTo("")!=0) {
					readVersionName = pkg.getRedefinableHeader().getVersion().getValue().trim();
				}
			} catch (NullPointerException e) {
				// default value
			}
			try {//get documentation if defined
				if (pkg.getPackageHeader().getDocumentation().getValue().trim().compareTo("")!=0) {
					readDocumentation = pkg.getPackageHeader().getDocumentation().getValue().trim();
				}
			} catch (NullPointerException e) {
				// default value
			}
			try {//get creation date if defined
				if (pkg.getPackageHeader().getCreated().getValue().trim().compareTo("")!=0) {
					readCreated = pkg.getPackageHeader().getCreated().getValue().trim();
					//readCreated = Utils.xpdlDate2standardDate(readCreated);
				}
			} catch (NullPointerException e) {
				// default value
			}
			try {//get lastupdate date if defined
				if (pkg.getPackageHeader().getModificationDate().getValue().trim().compareTo("")!=0) {
					readLastupdate = pkg.getPackageHeader().getModificationDate().getValue().trim();
					//readLastupdate = Utils.xpdlDate2standardDate(readLastupdate);
				}
			} catch (NullPointerException e) {
				// default value
			}
		} else if (nativeType.compareTo("EPML 2.0")==0) {

		}
		this.processName.setValue(readProcessName);
		this.versionName.setValue(readVersionName);
		this.documentation = readDocumentation;
		this.created = readCreated ;
		this.lastUpdate = readLastupdate;

		this.okButton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				importProcess();
			}
		});			

		this.okForAllButton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				importAllProcess();
			}
		});	

		this.importOneProcessWindow.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				importProcess();
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
		this.importOneProcessWindow.doModal();
	}

	private void cancel() throws InterruptedException, IOException{
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

	/**
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws JAXBException 
	 * @throws InterruptedException 
	 * Import process whose details are given in the class variable:
	 * username, nativeType, processName, versionName, domain and xml process
	 * @return name of imported process
	 * @exception
	 **/
	private void  importProcess() throws InterruptedException, IOException  {
		RequestToManager request = new RequestToManager();
		try {
			if (this.processName.getValue().compareTo("")==0
					|| this.versionName.getValue().compareTo("")==0) {
				Messagebox.show("Please enter a value for each field.", "Attention", Messagebox.OK,
						Messagebox.EXCLAMATION);
			} else {
				String domain = this.domainCB.getValue();
				ProcessSummaryType res= 
					request.importProcess(this.mainC.getCurrentUser().getUsername(), this.nativeType, this.processName.getValue(), 
							this.versionName.getValue(), this.nativeProcess, domain,
							this.documentation, this.created, this.lastUpdate);
				// process successfully imported
				this.importProcessesC.getImportedList().add(this);
				this.mainC.displayNewProcess(res);
				/* keep list of domains update */
				this.domainCB.addItem(domain);
				// delete process from the list of processes still to be imported
				this.importProcessesC.deleteFromToBeImported(this);
			}
		} catch (WrongValueException e) {
			e.printStackTrace();
			Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (ExceptionImport e) {
			e.printStackTrace();
			Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} finally {
			closePopup();
		}
	}

	/**
	 * The user clicked "OK for all": the default values apply for all
	 * process models still to import.
	 * @throws ExceptionImport 
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws WrongValueException 
	 */
	protected void importAllProcess() throws ExceptionImport, WrongValueException, InterruptedException, IOException {
		if (this.processName.getValue().compareTo("")==0
				|| this.versionName.getValue().compareTo("")==0) {
			Messagebox.show("Please enter a value for each field.", "Attention", Messagebox.OK,
					Messagebox.EXCLAMATION);
		} else {
			this.importProcessesC.importAllProcess(this.versionName.getValue(), this.domainCB.getValue());
		}
	}
	public Window getImportOneProcessWindow() {
		return importOneProcessWindow;
	}

	public String getFileName() {
		return fileName;
	}

	public String getDocumentation() {
		return documentation;
	}

	public String getNativeType() {
		return nativeType;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public String getCreated() {
		return created;
	}

	public InputStream getNativeProcess() {
		return nativeProcess;
	}


}
