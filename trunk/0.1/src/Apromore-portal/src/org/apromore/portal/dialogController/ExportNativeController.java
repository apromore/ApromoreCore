package org.apromore.portal.dialogController;

import org.apromore.portal.model_manager.FormatsType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;
import org.zkoss.zul.api.Row;

public class ExportNativeController extends Window {

	private Window exportNativeW;
	private Grid exportNatG;
	private Rows exportNatRs;
	private Label processNameL;
	private Label versionNameL;
	private Button okB;
	private Button cancelB;
	private Listbox nativeTypesLB;
	
	public ExportNativeController (MenuController menuC, int processId, 
				String processName, String versionName, FormatsType formats)   {

		this.exportNativeW = (Window) Executions.createComponents("macros/exportnative.zul", null, null);
		

		String id = processId + " " + versionName;
		this.exportNativeW.setId(id);
		this.exportNativeW.setTitle("Export Native (" + processName + ", " + versionName +")");
		this.exportNatG = (Grid) this.exportNativeW.getFirstChild().getFirstChild();
		this.exportNatRs = (Rows) exportNatG.getFirstChild().getNextSibling();
		Row processNameR = (Row) exportNatRs.getFirstChild();
		Row versionNameR = (Row) processNameR.getNextSibling();
		Row nativeTypeR = (Row) versionNameR.getNextSibling();
		Row buttonsR = (Row) nativeTypeR.getNextSibling();

		this.processNameL = (Label) processNameR.getFirstChild().getNextSibling();
		this.processNameL.setValue(processName + " (" + processId + ")");
		this.versionNameL = (Label) versionNameR.getFirstChild().getNextSibling();
		this.versionNameL.setValue(versionName);
		this.nativeTypesLB = (Listbox) nativeTypeR.getFirstChild().getNextSibling();
		this.okB = (Button) buttonsR.getFirstChild().getFirstChild();
		this.cancelB = (Button) buttonsR.getFirstChild().getFirstChild().getNextSibling();
		
		
		for (int i=0; i<formats.getFormat().size(); i++) {
			Listitem cbi = new Listitem();
			this.nativeTypesLB.appendChild(cbi);
			cbi.setLabel(formats.getFormat().get(i));
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
				export();
			}
		});
		this.exportNativeW.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				export();
			}
		});
		this.cancelB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});	
		
		//this.exportNativeW.doOverlapped();
	}
	
	protected void activateOkButton() {
		this.okB.setDisabled(false);
		//this.nativeTypesLB.removeChild(this.emptynative);
	}
	
	private void cancel() {
		this.exportNativeW.detach();
		//inform the menuC
		
	}
	
	private void export () {
		
	}
}
