package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

public class EditOneProcessController extends Window {

	private Window chooseNativeW;
	private Button okB;
	private Button cancelB;
	private Button cancelAllB;
	private Row readOnlyR;
	private Listbox nativeTypesLB;
	private Listbox annotationsLB;
	private Checkbox annotationOnlyCB;
	private Listitem noAnnotationI;
	private MainController mainC ;
	private EditListProcessesController editListProcessesC;
	private ProcessSummaryType process;
	private VersionSummaryType version;

	public EditOneProcessController (MainController mainC, EditListProcessesController editListProcessesC,
			ProcessSummaryType process, VersionSummaryType version) 
	throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {

		this.mainC = mainC;
		this.editListProcessesC = editListProcessesC;
		this.process = process;
		this.version = version;


		this.chooseNativeW = (Window) Executions.createComponents("macros/choosenative.zul", null, null);
		this.chooseNativeW.setTitle("Edit process " + process.getName() + ", " + version.getName() + ", choose a native type.");
		Rows rows = (Rows) this.chooseNativeW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
		Row nativeTypesR = (Row) rows.getFirstChild();
		Row annotationR = (Row) nativeTypesR.getNextSibling();
		this.readOnlyR = (Row) annotationR.getNextSibling();
		Row buttonsR = (Row) readOnlyR.getNextSibling();
		this.nativeTypesLB = (Listbox) nativeTypesR.getFirstChild().getNextSibling();
		this.annotationsLB = (Listbox) annotationR.getFirstChild().getNextSibling();
		this.annotationOnlyCB = (Checkbox) readOnlyR.getFirstChild().getNextSibling();
		this.okB = (Button) buttonsR.getFirstChild().getFirstChild();
		this.cancelB = (Button) this.okB.getNextSibling();
		this.cancelAllB = (Button) this.cancelB.getNextSibling();

		// enable cancelAll button if at least 1 process versions left.
		this.cancelAllB.setVisible(this.editListProcessesC.getToEditList().size()>0);

		// build native format listbox
		HashMap<String,String> formats = this.mainC.getNativeTypes();
		Set<String> extensions = formats.keySet();
		Iterator<String> it = extensions.iterator();
		Listitem cbi;
		while (it.hasNext()){
			cbi = new Listitem();
			this.nativeTypesLB.appendChild(cbi);
			cbi.setLabel(formats.get(it.next()));
			if ("XPDL 2.1".compareTo(cbi.getLabel())==0) {
				cbi.setSelected(true);
			}
//			if ("EPML 2.0".compareTo(cbi.getLabel())==0) {
//				cbi.setDisabled(true);
//			}
		}
		//((Listitem) this.nativeTypesLB.getFirstChild()).setSelected(true);
		// Build list of annotations associated with the process version
		for (int i=0; i<this.version.getAnnotations().size(); i++){
			String native_type = this.version.getAnnotations().get(i).getNativeType();
			for (int k=0;k<this.version.getAnnotations().get(i).getAnnotationName().size();k++){
				cbi = new Listitem();
				this.annotationsLB.appendChild(cbi);
				cbi.setLabel(this.version.getAnnotations().get(i).getAnnotationName().get(k) 
						+ " (" + native_type + ")");
				cbi.setValue(this.version.getAnnotations().get(i).getAnnotationName().get(k));
				if (Constants.INITIAL_ANNOTATION.compareTo((String) cbi.getValue())==0) {
					cbi.setSelected(true);
				}
			}
		}
		this.noAnnotationI = new Listitem();
		this.noAnnotationI.setLabel(Constants.NO_ANNOTATIONS);
		this.annotationsLB.appendChild(this.noAnnotationI);

		this.annotationsLB.addEventListener("onSelect",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				syncReadOnlyR(event);
			}
		});
		this.annotationOnlyCB.addEventListener("onCheck",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				syncListboxe(event);
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
		this.chooseNativeW.doModal();
		//		} else {
		//			Messagebox.show("Not owner", "Attention", Messagebox.OK,
		//					Messagebox.ERROR);
		//		}
	}
	/**
	 * If "no annotation" has been selected in the list box, the row
	 * with checkbox "annotation only" has to be disabled.
	 * @param event
	 */
	protected void syncReadOnlyR(Event event) {
			this.annotationOnlyCB.setDisabled(this.noAnnotationI.isSelected());
	}
	/**
	 * If check box "Annotation only" is checked item "- no annotation" has to be disabled
	 * @param event
	 */
	protected void syncListboxe(Event event) {
		// if users tick "annotation only", disable "no annotation" in the list
			this.noAnnotationI.setDisabled(this.annotationOnlyCB.isChecked());
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
			String annotation = null;
			String readOnly = "false";
			if (this.annotationsLB.getSelectedItem() != null
					&& Constants.NO_ANNOTATIONS.compareTo(this.annotationsLB.getSelectedItem().getLabel())!=0) {
				annotation = (String) this.annotationsLB.getSelectedItem().getValue();
			}
			if (this.annotationOnlyCB.isChecked()) {
				readOnly = "true";
			} else {
				readOnly = "false";
			}
			// editProcess is hosted by main controller as it is called by others.
			this.mainC.editProcess(processId, processName, version, nativeType, domain, annotation, readOnly);
			// delete process from the list of processes still to be edited
			this.editListProcessesC.deleteFromToBeEdited(this);
			closePopup();
		}
	}


	public Window getEditOneProcessWindow() {
		return chooseNativeW;
	}
}
