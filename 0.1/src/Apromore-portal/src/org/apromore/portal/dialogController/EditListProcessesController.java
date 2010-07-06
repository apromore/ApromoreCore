package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Window;

public class EditListProcessesController extends Window {

	private MainController mainC ;
	private MenuController menuC ;
	//	private Listitem emptynative;
	private HashMap<ProcessSummaryType,List<VersionSummaryType>> processVersions;
	private List<EditOneProcessController> toEditList; // list of edits to do
	private List<EditOneProcessController> editedList; //list of edits sent to editor
	
	public EditListProcessesController (MainController mainC, MenuController menuC, 
			HashMap<ProcessSummaryType,List<VersionSummaryType>> processVersions) 
	throws SuspendNotAllowedException, InterruptedException {

		//this.emptynative = (Listitem) win.getFellow("emptynative");
		this.menuC = menuC;
		this.mainC = mainC;
		this.processVersions = processVersions;
		this.toEditList = new ArrayList<EditOneProcessController>();
		this.editedList = new ArrayList<EditOneProcessController>();
		Set<ProcessSummaryType> keys = processVersions.keySet();
		Iterator it = keys.iterator();

		while (it.hasNext()) {
			ProcessSummaryType process = (ProcessSummaryType) it.next();
			for (Integer i=0; i<processVersions.get(process).size();i++) {
				VersionSummaryType version = processVersions.get(process).get(i);
				EditOneProcessController editOneProcess = new EditOneProcessController(this.mainC, this, process, version);
				this.toEditList.add(editOneProcess);
			}
		}
	}
	
	public List<EditOneProcessController> getEditedLis() {
		if (editedList == null) {
			editedList = new ArrayList<EditOneProcessController>();
		}
		return this.editedList;
	}
	
	public void deleteFromToBeEdited(EditOneProcessController editOneProcess) throws Exception {
		this.toEditList.remove(editOneProcess);
		if (this.toEditList.size()==0){
			reportEditProcess();
		}
	}
	
	private void reportEditProcess() throws Exception {
		String report = "Modification of " + this.editedList.size();
		if (this.editedList.size()==0) {
			report += " process.";
		} else {
			if (this.editedList.size()==1) {
				report +=  " process completed.";
			} else if (this.editedList.size()>1) {
				report +=  " processes completed.";
			};
			this.mainC.refreshProcessSummaries();
		}
		this.mainC.displayMessage(report);
	}

	public void cancelAll() {
		for (int i=0;i<this.toEditList.size();i++) {
			if (this.toEditList.get(i).getEditOneProcessWindow()!=null){
				this.toEditList.get(i).getEditOneProcessWindow().detach();
			}
		}
		this.toEditList.clear();
	}
}
