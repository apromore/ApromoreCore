package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.portal.exception.ExceptionWriteEditSession;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.EditSessionType;
import org.apromore.portal.model_manager.FormatsType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class EditListNativesController extends Window {

	private MainController mainC ;
	private MenuController menuC ;
	private Window chooseNativeW;
	private Button okB;
	private Button cancelB;
	private Listbox nativeTypesLB;
	//	private Listitem emptynative;
	private HashMap<ProcessSummaryType,List<VersionSummaryType>> processVersions;

	public EditListNativesController (MainController mainC, MenuController menuC, HashMap<String,String> formats, 
			HashMap<ProcessSummaryType,List<VersionSummaryType>> processVersions) {

		Window win = (Window) Executions.createComponents("macros/choosenative.zul", null, null);

		this.chooseNativeW = (Window) win.getFellow("choosenativeW");
		this.okB = (Button) win.getFellow("choosenativeOkB");
		this.cancelB = (Button) win.getFellow("choosenativeCancelB");
		this.nativeTypesLB = (Listbox) win.getFellow("choosenativeLB");
		//this.emptynative = (Listitem) win.getFellow("emptynative");
		this.menuC = menuC;
		this.mainC = mainC;
		this.processVersions = processVersions;

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
				EditListNatives();
			}
		});
		this.chooseNativeW.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				EditListNatives();
			}
		});
		this.cancelB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});	

	}

	protected void activateOkButton() {
		this.okB.setDisabled(false);
		//this.nativeTypesLB.removeChild(this.emptynative);
	}

	protected void EditListNatives () throws InterruptedException {
		Listitem cbi = nativeTypesLB.getSelectedItem();
		Set<ProcessSummaryType> keys = processVersions.keySet();
		Iterator it = keys.iterator();
		String instruction="", url=""; 
		int offsetH = 100, offsetV=200;

		RequestToManager request = new  RequestToManager();
		int editSessionCode ;
		while (it.hasNext()) {
			ProcessSummaryType process = (ProcessSummaryType) it.next();
			for (Integer i=0; i<processVersions.get(process).size();i++) {
				VersionSummaryType version = processVersions.get(process).get(i);
				offsetH-=100; offsetV-=100;
				EditSessionType editSession = new EditSessionType();
				editSession.setDomain(process.getDomain());
				editSession.setNativeType(cbi.getLabel());
				editSession.setProcessId(process.getId());
				editSession.setProcessName(process.getName());
				editSession.setUsername(this.mainC.getCurrentUser().getUsername());
				editSession.setVersionName(version.getName());
				try {
					editSessionCode = request.WriteEditSession(editSession);

					if (cbi.getLabel().compareTo("XPDL 2.1")==0) {
						url = this.mainC.getOryxEndPoint_xpdl()+"&sessionCode=";
					} else if (cbi.getLabel().compareTo("EPML 2.0")==0) {
						url = this.mainC.getOryxEndPoint_epml()+"&sessionCode=";
					} else {
						throw new ExceptionWriteEditSession("Native format not supported.");
					}
					url += editSessionCode;
					instruction += "window.open('" + url + "','','top=" + offsetH + ",left=" + offsetV 
					+ ",height=600,width=800,scrollbars=1,resizable=1'); ";
				} catch (ExceptionWriteEditSession e) {
					Messagebox.show("Cannot edit " + process.getName() + " (" 
							+e.getMessage()+")", "Attention", Messagebox.OK,
							Messagebox.ERROR);
				}
			}
		}
		Clients.evalJavaScript(instruction);
		cancel();
	}

	protected void cancel() {
		this.getChildren().clear();
		this.chooseNativeW.detach();
	}

}
