package org.apromore.portal.dialogController;

import java.io.IOException;
import java.io.InputStream;

import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.manager.RequestToManager;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ImportProcessController extends Window {

	private MenuController menuC;
	private MainController mainC;
	private Window importProcessWindow;
	private Button okButton;
	private Button uploadButton;
	private Button cancelButton;
	private Label filenameLabel ;
	private Textbox processName;
	private InputStream nativeProcess; // the uploaded file
	private String nativeType;
	
	public ImportProcessController (MenuController menuC, MainController mainC) throws DialogException{

		this.mainC = mainC;
		this.menuC = menuC;
		try {
			final Window win = (Window) Executions.createComponents("macros/importprocess.zul", null, null);
			this.importProcessWindow = (Window) win.getFellow("importWindow");
			this.processName = (Textbox) this.importProcessWindow.getFellow("processName");
			this.okButton = (Button) this.importProcessWindow.getFellow("okButton");
			this.uploadButton = (Button) this.importProcessWindow.getFellow("uploadButton");
			this.cancelButton = (Button) this.importProcessWindow.getFellow("cancelButton");
			this.filenameLabel = (Label) this.importProcessWindow.getFellow("filenameLabel");
			this.uploadButton.setAttribute("onUpload", "importModel(event)");
			
			// event listeners
			uploadButton.addEventListener("onUpload", new EventListener() {
				public void onEvent(Event event) throws Exception {
					uploadProcess ((UploadEvent) event);	
				}
			});

			okButton.addEventListener("onClick",
					new EventListener() {
				public void onEvent(Event event) throws Exception {
					importProcess();
				}
			});	
			cancelButton.addEventListener("onClick",
					new EventListener() {
				public void onEvent(Event event) throws Exception {
					cancel();
				}
			});	
			importProcessWindow.addEventListener("onOK",
					new EventListener() {
				public void onEvent(Event event) throws Exception {
					importProcess();
				}
			});	
			win.doModal();
		} catch (Exception e) {
			throw new DialogException("Error in importProcess controller: " + e.getMessage());
		}
	}

	private void cancel() {
		this.importProcessWindow.detach();
	}

	private void uploadProcess (UploadEvent event) throws InterruptedException {
		try {
			this.nativeProcess = event.getMedia().getStreamData();
			String fileName = event.getMedia().getName();
			String[] nativeType = fileName.split("\\.");
			this.nativeType = nativeType[1];
			this.okButton.setDisabled(false);
			this.filenameLabel.setValue(fileName);
		} catch (Exception e) {
			Messagebox.show("Repository not available ("+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} 
	}
	
	private void importProcess() throws InterruptedException {
		RequestToManager request = new RequestToManager();
		try {
			request.ImportModel(this.nativeType, this.processName.getValue(), this.nativeProcess);
			Messagebox.show("Process " + this.processName.getValue() + " canonised and stored.", "", Messagebox.OK,
					Messagebox.INFORMATION);
		} catch (WrongValueException e) {
			e.printStackTrace();
			Messagebox.show("Canonisation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (ExceptionImport e) {
			e.printStackTrace();
			Messagebox.show("Canonisation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			Messagebox.show("Canonisation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} finally {
			cancel();
		}
	}
}
