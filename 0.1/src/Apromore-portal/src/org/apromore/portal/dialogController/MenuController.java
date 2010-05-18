package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.FormatsType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;


public class MenuController extends Menubar {

	private MainController mainC;
	private ImportProcessController importC;
	private Menubar menuB;


	private Menu processM;
	private Menuitem importMI;
	private Menuitem exportMI;
	private Menuitem editMI;
	private Menuitem copyMI;
	private Menuitem pasteMI;
	private Menuitem deleteMI;
	private Menuitem cutMI;

	private Menu evaluationM;
	private Menu comparisonM;
	private Menu managementM;
	private Menu presentationM;
	private Menuitem evalQualityMI;
	private Menuitem evalCorrectnessMI;
	private Menuitem evalPerformanceMI;
	private FormatsType nativeTypes;			// choice of native formats

	private Vector<ExportNativeController> exportNativeConts ;

	public MenuController(MainController mainController) throws ExceptionFormats {

		this.mainC = mainController;
		this.exportNativeConts = new Vector<ExportNativeController>();
		/**
		 * get components
		 */
		this.menuB = (Menubar) this.mainC.getFellow("menucomp").getFellow("operationMenu");
		this.importMI = (Menuitem) this.menuB.getFellow("fileImport");
		this.exportMI = (Menuitem) this.menuB.getFellow("fileExport");
		this.editMI = (Menuitem) this.menuB.getFellow("processEdit");
		this.evaluationM = (Menu) this.menuB.getFellow("evaluation");
		this.comparisonM = (Menu) this.menuB.getFellow("comparison");
		this.managementM = (Menu) this.menuB.getFellow("management");
		this.presentationM = (Menu) this.menuB.getFellow("presentation");

		/**
		 * get list of formats
		 */
		RequestToManager request = new RequestToManager();
		this.nativeTypes = request.ReadFormats();

		this.importMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				importModel ();
			}
		});	

		this.editMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editNative ();
			}
		});	

		this.exportMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				exportNative ();
			}
		});	
	}

	protected void importModel (){
		try {
			this.importC = new ImportProcessController(this, mainC);
		} catch (DialogException e) {
			e.printStackTrace();
		}
	}

	protected void editNative() throws InterruptedException {
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
		if (processVersions.size()!=0) {
			EditListNativesController editList = 
				new EditListNativesController (this, this.nativeTypes,processVersions);
		}
	}


	protected void exportNative() {
		/* for each selected process version:
		 * 			- open a window described in exportnative.zul
		 * 			- ask the user to choose a native format
		 */
		HashMap<Checkbox, VersionSummaryType> processVersionHM = this.mainC.getProcesstable().getProcessVersionsHM();
		HashMap<Checkbox, ProcessSummaryType> processHM = this.mainC.getProcesstable().getProcessHM();
		HashMap<Checkbox, List<Checkbox>> mapProcessVersions = this.mainC.getProcesstable().getMapProcessVersions();
		Set<Checkbox> keysCBproc = processHM.keySet(); // set of checkboxes for processes
		Iterator it = keysCBproc.iterator();
		Checkbox cbProc;
		Checkbox cbVers;
		String processName, versionName;
		while (it.hasNext()) {
			cbProc = (Checkbox) it.next();
			if (cbProc.isChecked()){
				List<Checkbox> listCbVers = mapProcessVersions.get(cbProc);
				for(int i=0; i<listCbVers.size();i++) {
					cbVers = listCbVers.get(i);
					if (cbVers.isChecked()) {
						int processId = processHM.get(cbProc).getId();
						processName = processHM.get(cbProc).getName();
						versionName = processVersionHM.get(cbVers).getName();

						System.out.println(processName + ".." + versionName);

						ExportNativeController exportNativeC;
							exportNativeC = new ExportNativeController(this, processId, processName, 
									versionName, this.nativeTypes);
							this.exportNativeConts.add(exportNativeC);
					}
				}
			}
		}
	}

	public Menubar getMenuB() {
		return menuB;
	}

	public void setMenuB(Menubar menuB) {
		this.menuB = menuB;
	}

	public FormatsType getNativeTypes() {
		return nativeTypes;
	}


}
