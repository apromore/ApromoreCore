package org.apromore.portal.dialogController;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.apromore.portal.da.RequestToManager;
import org.apromore.portal.model_portal.ResultType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;


public class MenuController extends Menubar {

	private MainController mainC;
	private FileController modelC;
	private Menubar menuB;
	
	private Menuitem importMI;
	private InputStream nativeProcessFormat; // the uploaded file
	
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
		this.importMI.setAttribute("onUpload", "importModel(event)");
		// event listeners
		this.importMI.addEventListener("onUpload", new EventListener() {
			public void onEvent(Event event) throws Exception {
				importModel ((UploadEvent) event);	
			}
		});
	}

	protected void importModel (UploadEvent event) throws InterruptedException {
		//this.modelC = new FileController(this, this.mainC);
		this.nativeProcessFormat = event.getMedia().getStreamData();
		String fileName = event.getMedia().getName();
		String[] nativeType = fileName.split("\\.");
		RequestToManager request = new RequestToManager();
		ResultType result = request.ImportModel(this.nativeProcessFormat, nativeType[1], this.mainC.getCurrentUser());
		Messagebox.show("Process canonised " + result, "Attention", Messagebox.OK,
				Messagebox.INFORMATION);
	}

	public Menubar getMenuB() {
		return menuB;
	}

	public void setMenuB(Menubar menuB) {
		this.menuB = menuB;
	}


}
