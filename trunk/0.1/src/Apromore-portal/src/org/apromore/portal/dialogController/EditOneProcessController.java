package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.portal.exception.ExceptionWriteEditSession;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.EditSessionType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class EditOneProcessController extends Window {

	private Window chooseNativeW;
	private Button okB;
	private Button cancelB;
	private Button cancelAllB;
	private Listbox nativeTypesLB;
	private MainController mainC ;
	private EditListProcessesController editListProcessesC;
	private ProcessSummaryType process;
	private VersionSummaryType version;

	public EditOneProcessController (MainController mainC, EditListProcessesController editListProcessesC,
			ProcessSummaryType process, VersionSummaryType version) throws SuspendNotAllowedException, InterruptedException {

		this.mainC = mainC;
		this.editListProcessesC = editListProcessesC;
		this.process = process;
		this.version = version;

		Window win = (Window) Executions.createComponents("macros/choosenative.zul", null, null);

		this.chooseNativeW = (Window) win.getFellow("choosenativeW");
		this.chooseNativeW.setId(this.chooseNativeW.getId()+process.getId()+version.getName());
		this.chooseNativeW.setTitle("Edit process " + process.getName() + ", " + version.getName() + ", choose a native type.");
		this.okB = (Button) win.getFellow("choosenativeOkB");
		this.okB.setId(this.okB.getId()+process.getId()+version.getName());
		this.cancelB = (Button) win.getFellow("choosenativeCancelB");
		this.cancelB.setId(this.cancelB.getId()+process.getId()+version.getName());
		this.cancelAllB = (Button) win.getFellow("choosenativeCancelAllB");
		this.cancelAllB.setId(this.cancelAllB.getId()+process.getId()+version.getName());
		this.nativeTypesLB = (Listbox) win.getFellow("choosenativeLB");
		this.nativeTypesLB.setId(this.nativeTypesLB.getId()+process.getId()+version.getName());

		HashMap<String,String> formats = this.mainC.getNativeTypes();
		Set<String> extensions = formats.keySet();
		Iterator<String> it = extensions.iterator();
		while (it.hasNext()){
			Listitem cbi = new Listitem();
			this.nativeTypesLB.appendChild(cbi);
			cbi.setLabel(formats.get(it.next()));
		}

		this.nativeTypesLB.addEventListener("onSelect",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				activateOkButton();
			}
		});

		this.okB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editProcess();
			}
		});
		this.chooseNativeW.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editProcess();
			}
		});
		this.cancelB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});	
		this.cancelAllB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancelAll();
			}
		});	
		win.doModal();
	}

	protected void activateOkButton() {
		this.okB.setDisabled(false);
		//this.nativeTypesLB.removeChild(this.emptynative);
	}

	protected void cancel() {
		// delete process from the list of processes still to be edited
		this.editListProcessesC.deleteFromToBeEdited(this);
		closePopup();
	}	
	
	private void closePopup() {
		this.chooseNativeW.detach();
	}
	
	protected void cancelAll() {
		this.editListProcessesC.cancelAll();
	}	

	protected void editProcess() throws InterruptedException {

		String instruction="", url=this.mainC.getHost();
		int offsetH = 100, offsetV=200;
		int editSessionCode ;

		Listitem cbi = nativeTypesLB.getSelectedItem();
		EditSessionType editSession = new EditSessionType();
		editSession.setDomain(process.getDomain());
		editSession.setNativeType(cbi.getLabel());
		editSession.setProcessId(process.getId());
		editSession.setProcessName(process.getName());
		editSession.setUsername(this.mainC.getCurrentUser().getUsername());
		editSession.setVersionName(version.getName());

		try {
			RequestToManager request = new  RequestToManager();
			editSessionCode = request.WriteEditSession(editSession);
			if (cbi.getLabel().compareTo("XPDL 2.1")==0) {
				url += this.mainC.getOryxEndPoint_xpdl()+"sessionCode=";
			} else if (cbi.getLabel().compareTo("EPML 2.0")==0) {
				url += this.mainC.getOryxEndPoint_epml()+"sessionCode=";
			} else {
				throw new ExceptionWriteEditSession("Native format not supported.");
			}
			url += editSessionCode;
			instruction += "window.open('" + url + "','','top=" + offsetH + ",left=" + offsetV 
			+ ",height=600,width=800,scrollbars=1,resizable=1'); ";
			Clients.evalJavaScript(instruction);	
			cancel();
		} catch (ExceptionWriteEditSession e) {
			Messagebox.show("Cannot edit " + process.getName() + " (" 
					+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}
	}
	

	public Window getEditOneProcessWindow() {
		return chooseNativeW;
	}
}
