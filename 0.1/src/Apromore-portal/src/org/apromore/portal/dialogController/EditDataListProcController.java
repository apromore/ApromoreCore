package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.SuspendNotAllowedException;

public class EditDataListProcController {

	private MainController mainC ;	// the main controller 
	private MenuController menuC ; 	// the menu controller which called edit feature
	private HashMap<ProcessSummaryType,List<VersionSummaryType>> processVersions;
									// the selected process versions to be edited

	// list of controllers associated with editions still to be done
	private List<EditDataOneProcessController> toEditList; 
	//list of controllers associated with editions 
	private List<EditDataOneProcessController> editedList; 
		

	public EditDataListProcController(
			MainController mainC,
			MenuController menuController,
			HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions) 
	throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {	

		this.menuC = menuController;
		this.mainC = mainC;
		this.processVersions = selectedProcessVersions;
		this.toEditList = new ArrayList<EditDataOneProcessController>();
		this.editedList = new ArrayList<EditDataOneProcessController>();

		// process versions are edited one by one
		Set<ProcessSummaryType> keys = this.processVersions.keySet();
		Iterator<ProcessSummaryType> it = keys.iterator();
		while (it.hasNext()) {
			ProcessSummaryType process = it.next();
			for (Integer i=0; i<this.processVersions.get(process).size();i++) {
				VersionSummaryType version = this.processVersions.get(process).get(i);
				EditDataOneProcessController editDataOneProcess = 
					new EditDataOneProcessController(this.mainC, this, process, version);
				this.toEditList.add(editDataOneProcess);
			}
		}
	}

	/**
	 * Return list of controllers associated with process versions already edited
	 * @return List<EditDataOneProcessController>
	 */
	public List<EditDataOneProcessController> getEditedList() {
		if (editedList == null) {
			editedList = new ArrayList<EditDataOneProcessController>();
		}
		return this.editedList;
	}

	/**
	 * Return list of controllers associated with process versions still to be edited
	 * @return
	 */
	public List<EditDataOneProcessController> getToEditList() {
		if (toEditList == null) {
			toEditList = new ArrayList<EditDataOneProcessController>();
		}
		return toEditList;
	}

	/**
	 * Remove editOneProcess from list of controllers associated with process 
	 * versions still to be edited
	 * @param editOneProcess
	 * @throws Exception
	 */
	public void deleteFromToBeEdited(EditDataOneProcessController editOneProcess) throws Exception {
		this.toEditList.remove(editOneProcess);
		if (this.toEditList.size()==0) {
			reportEditData();
		}
	}

	/**
	 * Return a message which summarises edition work.
	 * If necessary, send request to main controller to refresh
	 * the table of process version summaries
	 * @throws Exception
	 */
	private void reportEditData() throws Exception {
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

	/**
	 * Cancel edition of remining process versions: empty the list of
	 * controllers associated to process versions still to be edited.
	 * @throws Exception
	 */
	public void cancelAll() throws Exception {
		for (int i=0;i<this.toEditList.size();i++) {
			if (this.toEditList.get(i).getEditDataOneProcessWindow()!=null){
				this.toEditList.get(i).getEditDataOneProcessWindow().detach();
			}
		}
		this.toEditList.clear();
		reportEditData();
	}
}
