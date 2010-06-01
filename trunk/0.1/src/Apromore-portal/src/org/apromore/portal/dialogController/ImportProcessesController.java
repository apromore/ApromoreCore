package org.apromore.portal.dialogController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.http.HttpSession;

import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.model_manager.FormatsType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class ImportProcessesController extends Window {

	private MenuController menuC;
	private MainController mainC;
	private Window importProcessesWindow;
	private Button okButton;
	private Button uploadButton;
	private Button cancelButton;
	private Label filenameLabel;
	private Label supportedExtL;
	private String extension;
	private String filename;
	private File fileUploaded;
	private String tmpPath;

	public ImportProcessesController (MenuController menuC, MainController mainC) throws DialogException{

		this.mainC = mainC;
		this.menuC = menuC;
		this.tmpPath = this.mainC.getTmpPath();

		try {
			final Window win = (Window) Executions.createComponents("macros/importProcesses.zul", null, null);
			this.importProcessesWindow = (Window) win.getFellow("importProcessesWindow");
			this.okButton = (Button) this.importProcessesWindow.getFellow("okButtonImportProcesses");
			this.uploadButton = (Button) this.importProcessesWindow.getFellow("uploadButton");
			this.cancelButton = (Button) this.importProcessesWindow.getFellow("cancelButtonImportProcesses");
			this.filenameLabel = (Label) this.importProcessesWindow.getFellow("filenameLabel");
			this.uploadButton.setAttribute("onUpload", "importModel(event)");
			this.supportedExtL = (Label) this.importProcessesWindow.getFellow("supportedExt");
			String supportedExtS = "zip, tar";
			for (int i=0; i<this.mainC.getNativeTypes().getFormat().size(); i++) {
				supportedExtS += ", " + this.mainC.getNativeTypes().getFormat().get(i).getExtension();
			}
			this.supportedExtL.setValue(supportedExtS);
			// event listeners
			uploadButton.addEventListener("onUpload", new EventListener() {
				public void onEvent(Event event) throws Exception {
					uploadProcess ((UploadEvent) event);	
				}
			});

			okButton.addEventListener("onClick",
					new EventListener() {
				public void onEvent(Event event) throws Exception {
					readFile();
				}
			});	
			cancelButton.addEventListener("onClick",
					new EventListener() {
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

		// delete the file, if exists
		if (this.filename != null) {
			Process p = Runtime.getRuntime().exec("rm " + this.tmpPath + this.filename);
			String s = null;
			BufferedReader stdInput = new BufferedReader(new
					InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new
					InputStreamReader(p.getErrorStream()));

			// log the result of the command
			this.mainC.getLOG().info("File deleted: " + this.tmpPath + this.filename);
			while ((s = stdInput.readLine()) != null) {
				this.mainC.getLOG().info(s);
			}		
			while ((s = stdError.readLine()) != null) {
				this.mainC.getLOG().info(s);
			}							
		}
	}

	/**
	 * Upload a file file: an archive or an xml file with the description of the model
	 * @param event
	 * @throws InterruptedException
	 */
	private void uploadProcess (UploadEvent event) throws InterruptedException {
		try {
			String fileType;
			FormatsType formats = this.mainC.getNativeTypes();
			this.filename = event.getMedia().getName();
			String[] list_extensions = filename.split("\\.");
			this.extension = list_extensions[list_extensions.length-1];
			if (this.extension.compareTo("zip")==0) {
				fileType = "zip archive";
			} else if (this.extension.compareTo("tar")==0) {
				fileType = "tar archive";
			} else {
				int i = 0;
				while (i < formats.getFormat().size() 
						&& formats.getFormat().get(i).getExtension().compareTo(this.extension)!=0) {
					i++;
				}
				if (i == formats.getFormat().size()) {
					throw new ExceptionImport("Extension not recognised.");
				} else {
					fileType = formats.getFormat().get(i).getFormat();
				}
			}
			this.okButton.setDisabled(false);
			this.filenameLabel.setValue(this.filename + " (file type is " + fileType + ")");

			this.fileUploaded = new File(this.tmpPath + this.filename);
			InputStream is = event.getMedia().getStreamData();

			//write is to the file
			OutputStream out = new FileOutputStream(this.fileUploaded);

			int read=0;
			byte[] bytes = new byte[1024];

			while((read = is.read(bytes))!= -1){
				out.write(bytes, 0, read);
			}
			is.close();
			out.flush();
			out.close();	


		} catch (ExceptionImport e) {
			Messagebox.show("Upload failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (Exception e) {
			Messagebox.show("Repository not available ("+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} 
	}

	/**
	 * Read uploaded file: zip, tar archives or file which contains native description
	 * in one of the supported native format.
	 * zip or tar: extract files and import each if possible
	 * file: import
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void readFile() throws InterruptedException {
		String command = null;
		try {
			HttpSession session = (HttpSession)(Executions.getCurrent()).getDesktop().getSession().getNativeSession();
			System.out.println( "Wrote to session:" + session.getId() ) ;
			
			/*
			if (this.extension.compareTo("zip")==0 || this.extension.compareTo("tar")==0) {
				if (this.extension.compareTo("zip")==0) {
					command = " unzip ";
				} else if (this.extension.compareTo("tar")==0) {
					command = " tar xvf ";
				} 
				Process p = Runtime.getRuntime().exec(command + this.tmpPath + this.filename);
				String s = null;
				BufferedReader stdInput = new BufferedReader(new
						InputStreamReader(p.getInputStream()));
				BufferedReader stdError = new BufferedReader(new
						InputStreamReader(p.getErrorStream()));

				// log the result of the command
				this.mainC.getLOG().info("File deleted: " + this.tmpPath + this.filename);
				while ((s = stdInput.readLine()) != null) {
					this.mainC.getLOG().info(s);
				}		
				while ((s = stdError.readLine()) != null) {
					this.mainC.getLOG().info(s);
				}		
			} else {

			}
			*/
		} catch (Exception e) {
			Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}
	}
}
