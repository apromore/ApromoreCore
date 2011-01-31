package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;


public class MenuController extends Menubar {

	private MainController mainC;
	private ImportListProcessesController importC;
	private CreateProcessController createC;
	private Menubar menuB;


	private Menu processM;
	private Menuitem createMI;
	private Menuitem importMI;
	private Menuitem exportMI;
	private Menuitem editModelMI;
	private Menuitem editDataMI;
	private Menuitem deleteMI;
	private Menuitem copyMI;
	private Menuitem pasteMI;

	private Menu evaluationM;
	private Menu filteringM;
	private Menuitem similaritySearchMI;
	private Menuitem exactMatchingMI;

	private Menu designM;
	private Menuitem mergeMI;

	private Menu presentationM;
	private Menuitem evalQualityMI;
	private Menuitem evalCorrectnessMI;
	private Menuitem evalPerformanceMI;

	public MenuController(MainController mainController) throws ExceptionFormats {

		this.mainC = mainController;
		/**
		 * get components
		 */
		this.menuB = (Menubar) this.mainC.getFellow("menucomp").getFellow("operationMenu");
		this.createMI = (Menuitem) this.menuB.getFellow("createProcess");
		this.importMI = (Menuitem) this.menuB.getFellow("fileImport");
		this.exportMI = (Menuitem) this.menuB.getFellow("fileExport");
		this.editModelMI = (Menuitem) this.menuB.getFellow("processEdit");
		this.editDataMI = (Menuitem) this.menuB.getFellow("dataEdit");
		this.deleteMI = (Menuitem) this.menuB.getFellow("processDelete");
		this.copyMI = (Menuitem) this.menuB.getFellow("processCopy");
		this.pasteMI = (Menuitem) this.menuB.getFellow("processPaste");

		this.evaluationM = (Menu) this.menuB.getFellow("evaluation");

		this.filteringM = (Menu) this.menuB.getFellow("filtering");
		this.similaritySearchMI = (Menuitem) this.menuB.getFellow("similaritySearch");
		this.exactMatchingMI = (Menuitem) this.menuB.getFellow("exactMatching");

		this.designM = (Menu) this.menuB.getFellow("design");
		this.mergeMI = (Menuitem) this.menuB.getFellow("designMerging");
		this.presentationM = (Menu) this.menuB.getFellow("presentation");

		this.createMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				createModel ();
			}
		});	 

		this.importMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				importModel ();
			}
		});	

		this.editModelMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editNative ();
			}
		});	

		this.editDataMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editData ();
			}
		});	

		this.exportMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				exportNative ();
			}
		});	
		this.deleteMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				deleteSelectedProcessVersions ();
			}
		});	
		this.similaritySearchMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				searchSimilarProcesses ();
			}
		});	
		this.mergeMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				mergeSelectedProcessVersions ();
			}
		});	
	}

	/**
	 * Search for similar processes to the one currently selected
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	protected void searchSimilarProcesses() throws SuspendNotAllowedException, InterruptedException {
		HashMap<ProcessSummaryType,List<VersionSummaryType>> selectedProcessVersions =
			getSelectedProcessVersions();
		this.mainC.eraseMessage();
		int processId;
		String versionName;
		if (selectedProcessVersions.size()==1
				&& selectedProcessVersions.get(selectedProcessVersions.keySet().iterator().next()).size()==1) {
			ProcessSummaryType process = selectedProcessVersions.keySet().iterator().next();
			VersionSummaryType version = selectedProcessVersions.get(selectedProcessVersions.keySet().iterator().next()).get(0);	
			SimilaritySearchController similaritySearchC = 
				new SimilaritySearchController(this. mainC, this, process, version);
		} else if (selectedProcessVersions.size()==0) {
			this.mainC.displayMessage("No process version selected, should be exactly one.");
		} else {
			this.mainC.displayMessage("Too many versions selected (should be exactly one).");
		}

	}

	protected void mergeSelectedProcessVersions() throws InterruptedException, ExceptionDomains {
		HashMap<ProcessSummaryType,List<VersionSummaryType>> selectedProcessVersions =
			getSelectedProcessVersions();
		this.mainC.eraseMessage();

		if (selectedProcessVersions.size() > 1) {
			try {
				new ProcessMergeController(this.mainC, this,
						selectedProcessVersions);
			} catch (SuspendNotAllowedException e) {
				Messagebox.show(e.getMessage(), "Attention", Messagebox.OK,
						Messagebox.ERROR);
			} catch (ExceptionAllUsers e) {
				String message;
				if (e.getMessage() == null) {
					message = "Couldn't retrieve users reference list.";
				} else {
					message = e.getMessage();
				}
				Messagebox.show(message, "Attention", Messagebox.OK,
						Messagebox.ERROR);
			}
		} else {
			this.mainC
			.displayMessage("Select at least 2 process models for merge.");
		}
	}

	protected void createModel() throws InterruptedException {
		this.mainC.eraseMessage();
		try {
			this.createC = new CreateProcessController (this.mainC, this.mainC.getNativeTypes());
		} catch (SuspendNotAllowedException e) {
			Messagebox.show(e.getMessage(), "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (InterruptedException e) {
			Messagebox.show(e.getMessage(), "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (ExceptionDomains e) {
			String message ;
			if (e.getMessage()==null) {
				message = "Couldn't retrieve domains reference list.";
			} else {
				message = e.getMessage();
			}
			Messagebox.show(message, "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (ExceptionFormats e) {
			String message ;
			if (e.getMessage()==null) {
				message = "Couldn't retrieve formats reference list.";
			} else {
				message = e.getMessage();
			}
			Messagebox.show(message, "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (ExceptionAllUsers e) {
			String message ;
			if (e.getMessage()==null) {
				message = "Couldn't retrieve users reference list.";
			} else {
				message = e.getMessage();
			}
			Messagebox.show(message, "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}

	}

	/**
	 * Edit all selected process versions
	 * @throws InterruptedException
	 * @throws ExceptionFormats 
	 * @throws SuspendNotAllowedException 
	 */
	protected void editNative() throws InterruptedException, SuspendNotAllowedException, ExceptionFormats {
		this.mainC.eraseMessage();
		HashMap<ProcessSummaryType,List<VersionSummaryType>> selectedProcessVersions =
			getSelectedProcessVersions();
		if (selectedProcessVersions.size()!=0) {
			EditListProcessesController editList = 
				new EditListProcessesController (this.mainC, this, selectedProcessVersions);
		} else {
			this.mainC.displayMessage("No process version selected.");
		}
	}


	/**
	 * Delete all selected process versions.
	 * @throws Exception 
	 */
	protected void deleteSelectedProcessVersions() throws Exception {
		this.mainC.eraseMessage();
		HashMap<ProcessSummaryType,List<VersionSummaryType>> selectedProcessVersions =
			getSelectedProcessVersions();
		if (selectedProcessVersions.size()!=0) {
			this.mainC.deleteProcessVersions(selectedProcessVersions);
		} else {
			this.mainC.displayMessage("No process version selected.");
		}
	}


	/**
	 * Export all selected process versions, each of which in a native format to be chosen by the user
	 * @throws InterruptedException 
	 * @throws SuspendNotAllowedException 
	 * @throws ExceptionFormats 
	 */
	protected void exportNative() throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {

		this.mainC.eraseMessage();
		HashMap<ProcessSummaryType,List<VersionSummaryType>> selectedProcessVersions =
			getSelectedProcessVersions();

		if (selectedProcessVersions.size()!=0) {
			ExportListNativeController exportList =
				new ExportListNativeController (this.mainC, this, selectedProcessVersions);
		} else {
			this.mainC.displayMessage("No process version selected.");
		}
	}

	protected void importModel () throws InterruptedException{
		this.mainC.eraseMessage();
		try {
			this.importC = new ImportListProcessesController(this, mainC);
		} catch (DialogException e) {
			Messagebox.show(e.getMessage(), "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	/**
	 * Return all selected process versions structured in an Hash map:
	 * <p, l> belongs to the result <=> for the process whose id is p, all versions whose
	 * name belong to l are selected.
	 * 
	 * @return HashMap<ProcessSummaryType,List<VersionSummaryType>>
	 */
	private HashMap<ProcessSummaryType,List<VersionSummaryType>> getSelectedProcessVersions() {
		this.mainC.eraseMessage();
		/* Build a list of the selected process versions <p, v>
		 */
		HashMap<Checkbox, VersionSummaryType> processVersionHM = this.mainC.getProcesstable().getProcessVersionsHM();
		HashMap<Checkbox, ProcessSummaryType> processHM = this.mainC.getProcesstable().getProcessHM();
		HashMap<Checkbox, List<Checkbox>> mapProcessVersions = this.mainC.getProcesstable().getMapProcessVersions();

		Set<Checkbox> keysCBproc = processHM.keySet(); // set of checkboxes for processes
		Iterator it = keysCBproc.iterator();
		// browse process checkboxes to find the first which is selected
		Checkbox cbProc;
		Checkbox cbVers;

		HashMap<ProcessSummaryType,List<VersionSummaryType>> processVersions = 
			new HashMap<ProcessSummaryType,List<VersionSummaryType>>();
		while (it.hasNext()) {
			cbProc = (Checkbox) it.next();
			if (cbProc.isChecked()){
				List<Checkbox> listCbVers = mapProcessVersions.get(cbProc);
				List<VersionSummaryType> listVersion = new ArrayList<VersionSummaryType>();
				// if process selected, one version at least is selected too
				for(int i=0; i<listCbVers.size();i++) {
					cbVers = listCbVers.get(i);
					if (cbVers.isChecked()) {
						listVersion.add(processVersionHM.get(cbVers));
					}
				}
				processVersions.put(processHM.get(cbProc), listVersion);
			}
		}
		return processVersions;
	}

	/**
	 * Edit meta data of selected process versions:
	 * 	- Process name (will be propagated to all versions of the process)
	 *  - Version name
	 * 	- Domain (will be propagated to all versions of the process)
	 *  - Ranking
	 * @throws InterruptedException 
	 * @throws SuspendNotAllowedException 
	 * @throws ExceptionAllUsers 
	 * @throws ExceptionDomains 
	 */
	private void editData() throws SuspendNotAllowedException, InterruptedException, ExceptionDomains, ExceptionAllUsers {
		this.mainC.eraseMessage();
		HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions =
			getSelectedProcessVersions();

		if (selectedProcessVersions.size()!=0) {
			EditListProcessDataController editList = 
				new EditListProcessDataController (this.mainC, this, selectedProcessVersions);
		} else {
			this.mainC.displayMessage("No process version selected.");
		}
	}

	public Menubar getMenuB() {
		return menuB;
	}

	public void setMenuB(Menubar menuB) {
		this.menuB = menuB;
	}

	public Menuitem getMergeMI() {
		return mergeMI;
	}

}
