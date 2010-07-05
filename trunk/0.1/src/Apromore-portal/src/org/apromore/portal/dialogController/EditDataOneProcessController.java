package org.apromore.portal.dialogController;

import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class EditDataOneProcessController {

	private Window editDataWindow;

	private MainController mainC ;
	private EditDataListProcessesController editDataListProcessesC;
	private Button okB;
	private Button cancelB;
	private Button cancelAllB;
	private Button resetB;
	private Radio r0;
	private Radio r1;
	private Radio r2;
	private Radio r3;
	private Radio r4;
	private Radio r5;
	private ProcessSummaryType process;
	private VersionSummaryType preVersion;
	private Textbox processNameT;
	private Textbox versionNameT;
	private Radiogroup rankingRG;
	private Row domainR;
	private Row ownerR;

	private SelectDynamicListController ownerCB;
	private SelectDynamicListController domainCB;

	public EditDataOneProcessController(MainController mainC,
			EditDataListProcessesController editDataListProcessesController,
			ProcessSummaryType process, VersionSummaryType version) throws SuspendNotAllowedException, InterruptedException {
		this.mainC = mainC;
		this.editDataListProcessesC = editDataListProcessesController;
		this.process = process;
		this.preVersion = version;

		Window win = (Window) Executions.createComponents("macros/editprocessdata.zul", null, null);

		this.editDataWindow = (Window) win.getFellow("editprocessdataW");
		this.editDataWindow.setId(this.editDataWindow.getId()+process.getId()+version.getName());
		this.editDataWindow.setTitle("Edit data process " + process.getName() + ", " + version.getName());
		this.okB = (Button) win.getFellow("editprocessdataOkB");
		this.okB.setId(this.okB.getId()+process.getId()+version.getName());
		this.cancelB = (Button) win.getFellow("editprocessdataCancelB");
		this.cancelB.setId(this.cancelB.getId()+process.getId()+version.getName());
		this.cancelAllB = (Button) win.getFellow("editprocessdataCancelAllB");
		this.cancelAllB.setId(this.cancelAllB.getId()+process.getId()+version.getName());
		this.resetB = (Button) win.getFellow("editprocessdataResetB");
		this.resetB.setId(this.resetB.getId()+process.getId()+version.getName());
		this.processNameT = (Textbox) win.getFellow("processname");
		this.processNameT.setId(this.processNameT.getId() +process.getId() + version.getName());
		this.versionNameT = (Textbox) win.getFellow("versionname");
		this.versionNameT.setId(this.versionNameT.getId() + process.getId() + version.getName());
		this.rankingRG = (Radiogroup) win.getFellow("ranking");
		this.rankingRG.setId(this.rankingRG.getId() + process.getId() + version.getName());
		this.r0 = (Radio) this.rankingRG.getFellow("r0");
		this.r1 = (Radio) this.rankingRG.getFellow("r1");
		this.r2 = (Radio) this.rankingRG.getFellow("r2");
		this.r3 = (Radio) this.rankingRG.getFellow("r3");
		this.r4 = (Radio) this.rankingRG.getFellow("r4");
		this.r5 = (Radio) this.rankingRG.getFellow("r5");
		this.domainR = (Row) win.getFellow("domain");
		this.domainR.setId(this.domainR.getId() + process.getId() + version.getName());
		this.domainCB = new SelectDynamicListController(this.mainC.getDomains());
		this.domainCB.setReference(this.mainC.getDomains());
		this.domainCB.setId(this.domainR.getId() + "domain");
		this.domainCB.setAutodrop(true);
		this.domainCB.setWidth("85%");
		this.domainCB.setHeight("100%");
		this.domainCB.setAttribute("hflex", "1");
		this.domainR.appendChild(domainCB);
		this.ownerR = (Row) win.getFellow("owner");
		this.ownerR.setId(this.ownerR.getId() + process.getId() + version.getName());
		this.ownerCB = new SelectDynamicListController(this.mainC.getUsers());
		this.ownerCB.setReference(this.mainC.getUsers());
		this.ownerCB.setId(this.ownerR.getId() + "owner");
		this.ownerCB.setAutodrop(true);
		this.ownerCB.setWidth("85%");
		this.ownerCB.setHeight("100%");
		this.ownerCB.setAttribute("hflex", "1");
		this.ownerR.appendChild(ownerCB);

		// allow change owner only for admin
		this.ownerCB.setDisabled(true);
		this.domainCB.setDisabled((process.getOwner().compareTo(this.mainC.getCurrentUser().getUsername())!=0));
		this.processNameT.setDisabled((process.getOwner().compareTo(this.mainC.getCurrentUser().getUsername())!=0));
		this.versionNameT.setDisabled((process.getOwner().compareTo(this.mainC.getCurrentUser().getUsername())!=0));
		
		reset();

		this.okB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editDataProcess();
			}
		});

		this.editDataWindow.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editDataProcess();
			}
		});

		this.cancelB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});	
		this.cancelAllB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancelAll();
			}
		});	
		this.resetB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				reset();
			}
		});	
		win.doModal();
	}

	protected void editDataProcess() throws Exception {
		Integer processId = this.process.getId();
		String processName = this.processNameT.getValue();
		String domain = this.domainCB.getValue();
		// TODO username should updatable by admin
		String username = this.process.getOwner();
		String preVersion = this.preVersion.getName();
		String newVersion = this.versionNameT.getValue();
		String rankingS = this.rankingRG.getSelectedItem().getLabel();
		Integer ranking = Integer.valueOf(rankingS);

		RequestToManager request = new RequestToManager();
		request.EditDataProcesses(processId, processName, domain, username, preVersion, newVersion, ranking);


		this.editDataListProcessesC.getEditedList().add(this);
		this.editDataListProcessesC.deleteFromToBeEdited(this);
		closePopup();
	}

	protected void cancel() throws Exception {
		// delete process from the list of processes still to be edited
		this.editDataListProcessesC.deleteFromToBeEdited(this);
		closePopup();
	}	

	private void closePopup() {
		this.editDataWindow.detach();
	}

	protected void cancelAll() {
		this.editDataListProcessesC.cancelAll();
	}	

	protected void reset() {
		this.processNameT.setValue(this.process.getName());
		this.versionNameT.setValue(this.preVersion.getName());
		this.domainCB.setValue(this.process.getDomain());
		r0.setChecked(this.preVersion.getRanking()==0);
		r1.setChecked(this.preVersion.getRanking()==1);
		r2.setChecked(this.preVersion.getRanking()==2);
		r3.setChecked(this.preVersion.getRanking()==3);
		r4.setChecked(this.preVersion.getRanking()==4);
		r5.setChecked(this.preVersion.getRanking()==5);
	}
	public Window getEditDataOneProcessWindow() {
		return editDataWindow;
	}

	public ProcessSummaryType getProcess() {
		return process;
	}

	public VersionSummaryType getPreVersion() {
		return preVersion;
	}

}
