package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.SuspendNotAllowedException;

public class EditListProcessDataController {

	private MainController mainC ;	// the main controller 
	private MenuController menuC ; 	// the menu controller which called edit feature
	private Map<ProcessSummaryType,List<VersionSummaryType>> processVersions;
									// the selected process versions to be edited

	// list of controllers associated with editions still to be done
	private List<EditOneProcessDataController> toEditList; 
	//list of controllers associated with editions 
	private List<EditOneProcessDataController> editedList; 
		

	public EditListProcessDataController(
			MainController mainC,
			MenuController menuController,
			Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions) 
	throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {	

		this.menuC = menuController;
		this.mainC = mainC;
		this.processVersions = selectedProcessVersions;
		this.toEditList = new ArrayList<EditOneProcessDataController>();
		this.editedList = new ArrayList<EditOneProcessDataController>();

		// process versions are edited one by one
		Set<ProcessSummaryType> keys = this.processVersions.keySet();
		Iterator<ProcessSummaryType> it = keys.iterator();
		while (it.hasNext()) {
			ProcessSummaryType process = it.next();
			for (Integer i=0; i<this.processVersions.get(process).size();i++) {
				VersionSummaryType version = this.processVersions.get(process).get(i);
				EditOneProcessDataController editDataOneProcess = 
					new EditOneProcessDataController(this.mainC, this, process, version);
				this.toEditList.add(editDataOneProcess);
			}
		}
	}

	/**
	 * Return list of controllers associated with process versions already edited
	 * @return List<EditOneProcessDataController>
	 */
	public List<EditOneProcessDataController> getEditedList() {
		if (editedList == null) {
			editedList = new ArrayList<EditOneProcessDataController>();
		}
		return this.editedList;
	}

	/**
	 * Return list of controllers associated with process versions still to be edited
	 * @return
	 */
	public List<EditOneProcessDataController> getToEditList() {
		if (toEditList == null) {
			toEditList = new ArrayList<EditOneProcessDataController>();
		}
		return toEditList;
	}

	/**
	 * Remove editOneProcess from list of controllers associated with process 
	 * versions still to be edited
	 * @param editOneProcess
	 * @throws Exception
	 */
	public void deleteFromToBeEdited(EditOneProcessDataController editOneProcess) throws Exception {
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
			this.mainC.reloadProcessSummaries();
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
