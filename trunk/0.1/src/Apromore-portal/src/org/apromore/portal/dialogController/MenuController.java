package org.apromore.portal.dialogController;

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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;


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
	private String chosenNativeType;

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
		/* For the set of selected processes:
		 * - choose a native format for edition
		 * for each selected process version:
		 * 		- generate a mapping code whose life-span matches the connected user's session
		 * 		- open a new browser (get request sent to the editor)
		 */
		HashMap<Checkbox, VersionSummaryType> processVersionHM = this.mainC.getProcesstable().getProcessVersionsHM();
		HashMap<Checkbox, ProcessSummaryType> processHM = this.mainC.getProcesstable().getProcessHM();
		HashMap<Checkbox, List<Checkbox>> mapProcessVersions = this.mainC.getProcesstable().getMapProcessVersions();

		Set<Checkbox> keysCBproc = processHM.keySet(); // set of checkboxes for processes
		Iterator it = keysCBproc.iterator();
		// browse process checkboxes to find the first which is selected
		Checkbox cbProc;
		Checkbox cbVers;
		String processName, versionName,
		url, instruction = "";

		try {
			// Ask user to choose a native format
			ChooseNativeController chooseNativeC = new ChooseNativeController (this, this.nativeTypes);
			while (it.hasNext()) {
				cbProc = (Checkbox) it.next();
				if (cbProc.isChecked()){
					List<Checkbox> listCbVers = mapProcessVersions.get(cbProc);
					// if process selected, one version at least is selected too
					for(int i=0; i<listCbVers.size();i++) {
						cbVers = listCbVers.get(i);
						if (cbVers.isChecked()) {
							int processId = processHM.get(cbProc).getId();
							processName = processHM.get(cbProc).getName();
							versionName = processVersionHM.get(cbVers).getName();
							System.out.println(processName + ".." + versionName);

							url = "http://www.google.com/search?q="+processName;
							instruction += "window.open('" + url + "','','top=100,left=200,height=600,width=800,scrollbars=1,resizable=1'); ";
						}
					}
				}
			}
			if (instruction.compareTo("")!=0 && this.chosenNativeType!=null) {
				//Clients.evalJavaScript(instruction);
				System.out.println(instruction);
			}
		} catch (SuspendNotAllowedException e) {
			Messagebox.show("Repository not available ("+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (InterruptedException e) {
			Messagebox.show("Repository not available ("+e.getMessage()+")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
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
						try {
							exportNativeC = new ExportNativeController(this, processId, processName, versionName);
							this.exportNativeConts.add(exportNativeC);
						} catch (SuspendNotAllowedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

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

	public String getChosenNativeType() {
		return chosenNativeType;
	}

	public void setChosenNativeType(String chosenNativeType) {
		this.chosenNativeType = chosenNativeType;
	}

}
