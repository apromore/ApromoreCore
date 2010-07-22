package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
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

		//if (this.process.getOwner().compareTo(this.mainC.getCurrentUser().getUsername())==0) {
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
			// TODO temporary so the user cannot choose to edit in epml format
			if ("EPML 2.0".compareTo(cbi.getLabel())==0) {
				cbi.setDisabled(true);
			}
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
		//		} else {
		//			Messagebox.show("Not owner", "Attention", Messagebox.OK,
		//					Messagebox.ERROR);
		//		}
	}

	protected void activateOkButton() {
		this.okB.setDisabled(false);
		//this.nativeTypesLB.removeChild(this.emptynative);
	}

	protected void cancel() throws Exception {
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

	protected void editProcess() throws Exception {
		if (this.nativeTypesLB.getSelectedItem() == null
				||	this.nativeTypesLB.getSelectedItem() != null
				&& this.nativeTypesLB.getSelectedItem().getLabel().compareTo("")==0){
			Messagebox.show("Please select a native type.", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} else {
			Listitem cbi = this.nativeTypesLB.getSelectedItem();		
			Integer processId = this.process.getId();
			String processName = this.process.getName();
			// normally, only one version...
			String version = this.version.getName();
			String nativeType = cbi.getLabel();
			String domain = this.process.getDomain();
			// editProcess is hosted by main controller as it is called by others.
			this.mainC.editProcess(processId, processName, version, nativeType, domain);
			cancel();
		}
	}


	public Window getEditOneProcessWindow() {
		return chooseNativeW;
	}
}
