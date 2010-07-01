package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class EditDataListProcessesController {

	private MainController mainC ;
	private MenuController menuC ;
	private HashMap<ProcessSummaryType,List<VersionSummaryType>> processVersions;

	private List<EditDataOneProcessController> toEditList; // list of edits to do
	private List<EditDataOneProcessController> editedList; //list of edits to be done


	public EditDataListProcessesController(
			MainController mainC,
			MenuController menuController,
			HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions) 
	throws SuspendNotAllowedException, InterruptedException {	

		this.menuC = menuController;
		this.mainC = mainC;
		this.processVersions = selectedProcessVersions;
		this.toEditList = new ArrayList<EditDataOneProcessController>();
		this.editedList = new ArrayList<EditDataOneProcessController>();

		Set<ProcessSummaryType> keys = this.processVersions.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			ProcessSummaryType process = (ProcessSummaryType) it.next();
			for (Integer i=0; i<this.processVersions.get(process).size();i++) {
				VersionSummaryType version = this.processVersions.get(process).get(i);
				EditDataOneProcessController editDataOneProcess = 
					new EditDataOneProcessController(this.mainC, this, process, version);
				this.toEditList.add(editDataOneProcess);
			}
		}
	}

	public List<EditDataOneProcessController> getEditedList() {
		if (editedList == null) {
			editedList = new ArrayList<EditDataOneProcessController>();
		}
		return this.editedList;
	}

	public void deleteFromToBeEdited(EditDataOneProcessController editOneProcess) throws InterruptedException {
		this.toEditList.remove(editOneProcess);
		if (this.toEditList.size()==0) {
			// For each modified process, build for each of its modified version
			// the mapping between the previous and the new version name
			HashMap <ProcessSummaryType, List<String>> preNewVersionMap = new HashMap<ProcessSummaryType, List<String>>();
			for (int i=0; i<this.editedList.size(); i++){
				ProcessSummaryType process = this.editedList.get(i).getProcess();
				if (!preNewVersionMap.containsKey(process)) {
					preNewVersionMap.put(process, new ArrayList<String>());
				}
				preNewVersionMap.get(process).add(this.editedList.get(i).getPreVersion().getName());
				preNewVersionMap.get(process).add(this.editedList.get(i).getNewVersion().getName());
			}
			RequestToManager request = new RequestToManager();
			request.WriteProcess (this.processVersions, preNewVersionMap);
			reportEditData();
		}
	}

	private void reportEditData() throws InterruptedException {
		String report = "Modification of " + this.editedList.size();
		if (this.editedList.size()==0) {
			report += " process.";
		}
		if (this.editedList.size()==1) {
			report +=  " process completed.";
		} else if (this.editedList.size()>1) {
			report +=  " processes completed.";
		};
		Messagebox.show(report, "", Messagebox.OK, Messagebox.INFORMATION);
	}

	public void cancelAll() {
		for (int i=0;i<this.toEditList.size();i++) {
			if (this.toEditList.get(i).getEditDataOneProcessWindow()!=null){
				this.toEditList.get(i).getEditDataOneProcessWindow().detach();
			}
		}
		this.toEditList.clear();
	}
}
