package org.apromore.portal.dialogController;

import org.apromore.portal.model_manager.FormatsType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

public class ChooseNativeController extends Window {
	
	private MenuController menuC ;
	private Window chooseNativeW;
	private Button okB;
	private Button cancelB;
	private Listbox nativeTypesLB;
	private Listitem emptynative;
	
	public ChooseNativeController (MenuController menuC, FormatsType formats) 
	throws SuspendNotAllowedException, InterruptedException {
		
		Window win = (Window) Executions.createComponents("macros/choosenative.zul", null, null);
		
		this.chooseNativeW = (Window) win.getFellow("choosenativeW");
		this.okB = (Button) win.getFellow("choosenativeOkB");
		this.cancelB = (Button) win.getFellow("choosenativeCancelB");
		this.nativeTypesLB = (Listbox) win.getFellow("choosenativeLB");
		this.emptynative = (Listitem) win.getFellow("emptynative");
		this.menuC = menuC;
		
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
				choiceOk();
			}
		});
		this.chooseNativeW.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				choiceOk();
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
		this.nativeTypesLB.removeChild(this.emptynative);
	}

	protected void choiceOk () {
		Listitem cbi = nativeTypesLB.getSelectedItem();
		this.menuC.setChosenNativeType(cbi.getLabel());
		cancel();
	}
	
	protected void cancel() {
		this.getChildren().clear();
		this.chooseNativeW.detach();
	}
	
}
