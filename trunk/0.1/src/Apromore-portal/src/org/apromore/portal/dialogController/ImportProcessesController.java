package org.apromore.portal.dialogController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

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
	private String nativeType;
	private File fileUploaded;
	private String tmpPath;
	private String sessionId;
	
	public ImportProcessesController (MenuController menuC, MainController mainC) throws DialogException{

		this.mainC = mainC;
		this.menuC = menuC;
		this.tmpPath = this.mainC.getTmpPath();
		HttpSession session = (HttpSession)(Executions.getCurrent()).getDesktop().getSession().getNativeSession();
		this.sessionId = session.getId().toString();
		
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

		// delete folder associated with the session, if exists
		File folder = new File (this.tmpPath + this.sessionId);
		if (folder.exists()) {
			File[] content = folder.listFiles();
			for (int i=0; i<content.length;i++){
				content[i].delete();
			}
			folder.delete();
		}
	}

	/**
	 * Upload a file file: an archive or an xml file with the description of the model
	 * @param event
	 * @throws InterruptedException
	 */
	private void uploadProcess (UploadEvent event) throws InterruptedException {
		try {
			
			/*
			 * Create a folder for the session.
			 * if folder already exists for current session: empty and delete it first.
			 */
			File folder = new File (this.tmpPath + this.sessionId);
			if (folder.exists()) {
				File[] content = folder.listFiles();
				for (int i=0; i<content.length;i++){
					content[i].delete();
				}
				folder.delete();
			}
			Boolean ok= folder.mkdir();
			if (!ok) {
				throw new ExceptionImport ("Couldn't extract archive.");
			}
			
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
					this.nativeType = fileType;
				}
			}
			this.okButton.setDisabled(false);
			this.filenameLabel.setValue(this.filename + " (file type is " + fileType + ")");

			this.fileUploaded = new File(this.tmpPath + this.sessionId, this.filename);
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
	private void readFile() throws InterruptedException, IOException {
		String command = null;
		try {
			if (this.extension.compareTo("zip")==0 || this.extension.compareTo("tar")==0) {
				/*
				 *  Case of an archive: for each file in the session folder, check whether 
				 *  they all have the same extension. If yes, import each of which. If no, 
				 *  raise an exception.
				 */
				
				command = " cd " + this.sessionId;
				if(this.extension.compareTo("zip")==0) {
					command = " unzip ";
				} else {
					command = " tar xf ";
				}
				command += this.filename;
				System.out.println (command + "\n");
				
				
				//Process p = Runtime.getRuntime().exec(command + this.tmpPath + this.filename);
				Process p = Runtime.getRuntime().exec("pwd");
				String s = null;
				BufferedReader stdInput = new BufferedReader(new
						InputStreamReader(p.getInputStream()));
				BufferedReader stdError = new BufferedReader(new
						InputStreamReader(p.getErrorStream()));

				// log what happened
				while ((s = stdInput.readLine()) != null) {
					this.mainC.getLOG().info(s);
				}		
				while ((s = stdError.readLine()) != null) {
					this.mainC.getLOG().info(s);
				}
				
				// Get names of all files in folder
				
			} else {
				// Case of an single file: import it.
				File xml_file = new File (this.tmpPath + this.sessionId, this.filename);
				FileInputStream xml_process = new FileInputStream(xml_file);
				ImportOneProcess importProcess = new ImportOneProcess (this.mainC, this, xml_process, this.nativeType);
			}
			
		} catch (Exception e) {
			Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} finally {
			cancel();	
		}
	}
}
