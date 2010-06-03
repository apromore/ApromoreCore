package org.apromore.portal.dialogController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

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
	private String fileOrArchive;
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
			// build the list of supported extensions to display
			String supportedExtS = "zip, tar";
			Set<String> supportedExt = this.mainC.getNativeTypes().keySet();
			Iterator<String> it = supportedExt.iterator();
			while (it.hasNext()) {
				supportedExtS += ", " + it.next();
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
					readArchiveOrFile();
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
		recursiveDelete(folder);
	}

	private void recursiveDelete (File f) {
		if (f.isDirectory()) {
			File[] list = f.listFiles();
			for (int i=0;i<list.length;i++){
				recursiveDelete(list[i]);
			}
		}
		f.delete();
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

			// derive file type from its extension
			String fileType;
			this.fileOrArchive = event.getMedia().getName();
			String[] list_extensions = fileOrArchive.split("\\.");
			this.extension = list_extensions[list_extensions.length-1];
			if (this.extension.compareTo("zip")==0) {
				fileType = "zip archive";
			} else if (this.extension.compareTo("tar")==0) {
				fileType = "tar archive";
			} else {
				fileType = this.mainC.getNativeTypes().get(this.extension);
				if (fileType==null) {
					throw new ExceptionImport ("Unsupported extension.");
				}
				this.nativeType = fileType;
			}
			
			// now the file is uploaded, Ok button could be enabled
			
			this.okButton.setDisabled(false);
			this.filenameLabel.setValue(this.fileOrArchive + " (file type is " + fileType + ")");

			this.fileUploaded = new File(this.tmpPath + this.sessionId, this.fileOrArchive);
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
	private void readArchiveOrFile() throws InterruptedException, IOException {
		String command = null;
		try {
			if (this.extension.compareTo("zip")==0 || this.extension.compareTo("tar")==0) {
				/*
				 *  Case of an archive: extract files in session folder, 
				 *  for each file in the folder, check whether 
				 *  they all have the same extension. If yes, import each of which. If no, 
				 *  raise an exception.
				 */

				File archive = new File (this.tmpPath + this.sessionId, this.fileOrArchive);
				String separator = archive.separator;

				if(this.extension.compareTo("zip")==0) {
					command = " unzip -o " + this.tmpPath + this.sessionId + separator + this.fileOrArchive 
					+ " -d " + this.tmpPath + this.sessionId;
				} else {
					command = " tar -xf " + this.tmpPath + this.sessionId + separator + this.fileOrArchive 
					+ " -C " + this.tmpPath + this.sessionId;
				}

				System.out.println (command + "\n");


				Process p = Runtime.getRuntime().exec(command);
				// delete archive
				archive.delete();

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
				File folder = new File (this.tmpPath, this.sessionId);
				File[] folderFiles = folder.listFiles();
				if (folderFiles.length==0) {
					throw new ExceptionImport("Empty archive");
				}
				String ignoredFiles = "";
				String filename ;
				for (int j=0; j< folderFiles.length; j++){
					File xml_file = new File(folderFiles[j].getAbsolutePath());
					FileInputStream xml_process = new FileInputStream(xml_file);
					filename = folderFiles[j].getName();
					String processName = filename.split("\\.")[0];
					String nativeType = this.mainC.getNativeTypes().get(filename.split("\\.")[filename.split("\\.").length-1]);
					if (nativeType==null) {
						ignoredFiles += filename + ", ";
					} else {
						ImportOneProcess importProcess = 
							new ImportOneProcess (this.mainC, this, xml_process, processName, nativeType, filename);
					}
				}

			} else {
				// Case of a single file: import it.
				File xml_file = new File (this.tmpPath + this.sessionId, this.fileOrArchive);
				String processName = this.fileOrArchive.split("\\.")[0];
				FileInputStream xml_process = new FileInputStream(xml_file);
				ImportOneProcess importProcess =
					new ImportOneProcess (this.mainC, this, xml_process, processName, this.nativeType, fileOrArchive);
			}
			// clean folder and close window
			cancel();

		} catch (Exception e) {
			Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} 
	}
}
