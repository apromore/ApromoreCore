package org.apromore.portal.dialogController;

import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
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
	
	public MenuController(MainController mainController) {
	
		this.mainC = mainController;
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
		
		this.importMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				importModel ();
			}
		});	
		
		this.editMI.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editModel ();
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

	protected void editModel() {
		Executions.getCurrent().sendRedirect("http://bach2.imag.fr:8080/projetPCarre/CDExportProjetHtml?idSujet=508","_blank");
	}
	
	public Menubar getMenuB() {
		return menuB;
	}

	public void setMenuB(Menubar menuB) {
		this.menuB = menuB;
	}


}
