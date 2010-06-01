package org.apromore.portal.dialogController;

import java.io.IOException;
import java.io.InputStream;

import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ImportOneProcess extends Window {

	private MainController mainC;
	private ImportProcessesController importProcessesC;
	private Window importOneProcessWindow;

	private Textbox processName;
	private Textbox domain;
	private InputStream nativeProcess; // the uploaded file
	private String nativeType;
	private Button okButton;
	private Button cancelButton;
	
	public ImportOneProcess (MainController mainC, ImportProcessesController importProcessesC, InputStream xml_process) {
		
		this.importProcessesC = importProcessesC;
		final Window win = (Window) Executions.createComponents("macros/importProcesses.zul", null, null);
		this.importOneProcessWindow = (Window) win.getFellow("importOneProcessWindow");
		this.processName = (Textbox) this.importOneProcessWindow.getFellow("processName");
		this.domain = (Textbox) this.importOneProcessWindow.getFellow("domain");
		this.okButton = (Button) this.importOneProcessWindow.getFellow("okButtonOneProcess");
		this.cancelButton = (Button) this.importOneProcessWindow.getFellow("cancelButtonOneProcess");
	}
	
	private void importProcess() throws InterruptedException {
		RequestToManager request = new RequestToManager();
		try {
			if (this.processName.getValue().compareTo("")==0
					|| this.domain.getValue().compareTo("")==0) {
				throw new ExceptionImport("Please enter a value for all fields.");
			} else {
				ProcessSummaryType res= 
					request.ImportModel(this.mainC.getCurrentUser().getUsername(), this.nativeType, this.processName.getValue(), 
							null, this.nativeProcess, this.domain.getValue());
				this.mainC.displayNewProcess(res);

				Messagebox.show("Import of " + this.processName.getValue() + " completed.", "", Messagebox.OK,
						Messagebox.INFORMATION);
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
		} 
	}
}
