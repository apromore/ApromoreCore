package org.apromore.portal.dialogController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ImportOneProcessController extends Window {

	private MainController mainC;
	private ImportListProcessesController importProcessesC;
	private Window importOneProcessWindow;

	private Textbox processName;
	private Textbox versionName;
	private Textbox domain;
	private File xml_file; 			   // the actual file
	private InputStream nativeProcess; // the input stream read from uploaded file
	private String nativeType;
	private Button okButton;
	private Button cancelButton;
	private Button cancelAllButton;
	
	public ImportOneProcessController (MainController mainC, ImportListProcessesController importProcessesC, File xml_file, 
			String processName, String nativeType, String fileName) 
	throws SuspendNotAllowedException, InterruptedException, FileNotFoundException, JAXBException {
		
		this.importProcessesC = importProcessesC;
		this.mainC = mainC;
		this.xml_file= xml_file;
		this.nativeProcess = new FileInputStream(this.xml_file);
		this.nativeType = nativeType;
		final Window win = (Window) Executions.createComponents("macros/importOneProcess.zul", null, null);
		this.importOneProcessWindow = (Window) win.getFellow("importOneProcessWindow");
		this.importOneProcessWindow.setId(this.importOneProcessWindow.getId()+fileName);
		this.processName = (Textbox) this.importOneProcessWindow.getFellow("processName");
		this.processName.setId(this.processName.getId()+fileName);
		this.versionName = (Textbox) this.importOneProcessWindow.getFellow("versionName");
		this.versionName.setId(this.versionName.getId()+fileName);
		this.domain = (Textbox) this.importOneProcessWindow.getFellow("domain");
		this.domain.setId(this.domain.getId()+fileName);
		this.okButton = (Button) this.importOneProcessWindow.getFellow("okButtonOneProcess");
		this.okButton.setId(this.okButton.getId()+fileName);
		this.cancelButton = (Button) this.importOneProcessWindow.getFellow("cancelButtonOneProcess");
		this.cancelButton.setId(this.cancelButton.getId()+fileName);
		this.cancelAllButton = (Button) this.importOneProcessWindow.getFellow("cancelAllButtonOneProcess");
		this.cancelAllButton.setId(this.cancelAllButton.getId()+fileName);
		
		String readVersionName = "0.1"; // default value for versionName if not found
		String readProcessName = processName ; // default value if not found

		// check properties in xml_process: version, documentation, created, modificationDate
		// if native format is xpdl, extract information from xml file
		if (nativeType.compareTo("XPDL 2.1")==0) {
			JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(new FileInputStream(this.xml_file));
			PackageType pkg = rootElement.getValue();

			try {
				if (pkg.getName().compareTo("")!=0) {
					readProcessName = pkg.getName();
				}
			} catch (NullPointerException e) {
				// default value
			}
			try {
				if (pkg.getRedefinableHeader().getVersion().getValue().compareTo("")!=0) {
					readVersionName = pkg.getRedefinableHeader().getVersion().getValue();
				}
			} catch (NullPointerException e) {
				// default value
			}
		}
		this.processName.setValue(readProcessName);
		this.versionName.setValue(readVersionName);
		
		this.okButton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				importProcess();
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
		win.doModal();
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
	 * @throws JAXBException 
	 * @throws InterruptedException 
	 * Import process whose details are given in the class variable:
	 * username, nativeType, processName, versionName, domain and xml process
	 * @return name of imported process
	 * @exception
	 **/
	private void  importProcess() throws InterruptedException, IOException {
		RequestToManager request = new RequestToManager();
		try {
			if (this.processName.getValue().compareTo("")==0
					|| this.versionName.getValue().compareTo("")==0) {
				throw new ExceptionImport("Please enter a value for each field.");
			} else {
				ProcessSummaryType res= 
					request.ImportModel(this.mainC.getCurrentUser().getUsername(), this.nativeType, this.processName.getValue(), 
							this.versionName.getValue(), this.nativeProcess, this.domain.getValue());
				// process successfully imported
				this.importProcessesC.getImportedList().add(this);
				this.mainC.displayNewProcess(res);
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
			// delete process from the list of processes still to be imported
			this.importProcessesC.deleteFromToBeImported(this);
			closePopup();
		}
	}

	public Window getImportOneProcessWindow() {
		return importOneProcessWindow;
	}
	
}
