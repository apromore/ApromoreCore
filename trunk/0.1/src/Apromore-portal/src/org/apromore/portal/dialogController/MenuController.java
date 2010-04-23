package org.apromore.portal.dialogController;

import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;


public class MenuController extends Menubar {

	private MainController mainC;
	private ImportProcessController importC;
	private Menubar menuB;
	
	private Menuitem importMI;
	
	private Menu editM;
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
		this.editM = (Menu) this.menuB.getFellow("edit");
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
	}

	protected void importModel (){
		try {
			this.importC = new ImportProcessController(this, mainC);
		} catch (DialogException e) {
			e.printStackTrace();
		}
	}

	public Menubar getMenuB() {
		return menuB;
	}

	public void setMenuB(Menubar menuB) {
		this.menuB = menuB;
	}


}
