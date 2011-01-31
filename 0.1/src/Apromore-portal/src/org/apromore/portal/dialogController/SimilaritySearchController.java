package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.List;

import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.ProcessSummariesType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

public class SimilaritySearchController extends Window {

	private MainController mainC;
	private MenuController menuC;
	private Window similaritySearchW;
	private Row algoChoiceR;
	private Row buttonsR;
	private Listbox algosLB;
	private Row modelthreshold;
	private Row labelthreshold;
	private Row contextthreshold;
	private Row skipeweight;
	private Row subeweight;
	private Row skipnweight;
	private Row subnweight;
	private Button OKbutton;
	private Button CancelButton;
	private ProcessSummaryType process;
	private VersionSummaryType version;
	
	public SimilaritySearchController (MainController mainC, MenuController menuC, 
			ProcessSummaryType process, VersionSummaryType version) 
	throws SuspendNotAllowedException, InterruptedException {
		this.mainC = mainC;
		this.menuC = menuC;
		this.version = version;
		this.process = process;
		this.similaritySearchW = (Window) Executions.createComponents("macros/similaritysearch.zul", null, null);
		
		this.algoChoiceR = (Row) this.similaritySearchW.getFellow("similaritySearchAlgoChoice");
		this.buttonsR = (Row) this.similaritySearchW.getFellow("similaritySearchButtons");

		this.OKbutton = (Button) this.similaritySearchW.getFellow("similaritySearchOKbutton");
		this.CancelButton = (Button) this.similaritySearchW.getFellow("similaritySearchCancelbutton");
		
		// get parameter rows
		this.modelthreshold = (Row) this.similaritySearchW.getFellow("modelthreshold");
		this.labelthreshold = (Row) this.similaritySearchW.getFellow("labelthreshold");
		this.contextthreshold = (Row) this.similaritySearchW.getFellow("contextthreshold");
		this.skipeweight = (Row) this.similaritySearchW.getFellow("skipeweight");
		this.subeweight = (Row) this.similaritySearchW.getFellow("subeweight");
		this.skipnweight = (Row) this.similaritySearchW.getFellow("skipnweight");
		this.subnweight = (Row) this.similaritySearchW.getFellow("subnweight");
		
		this.algosLB = (Listbox) this.algoChoiceR.getFirstChild().getNextSibling();
		// build the listbox to choose algo
		Listitem listItem = new Listitem();
		listItem.setLabel("Greedy");
		this.algosLB.appendChild(listItem);
		listItem.setSelected(true);
		
		listItem = new Listitem();
		listItem.setLabel("Hungarian");
		this.algosLB.appendChild(listItem);
		
		updateActions();
		
		this.algosLB.addEventListener("onSelect",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				updateActions();
			}
		});
		this.OKbutton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				searchSimilarProcesses();
			}
		});
		this.OKbutton.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				searchSimilarProcesses();
			}
		});
		this.CancelButton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});
		
		this.similaritySearchW.doModal();
	}
	
	protected void cancel()   {
		this.similaritySearchW.detach();
	}

	protected void searchSimilarProcesses() {
		String message = null;
		try {
			RequestToManager request = new RequestToManager();
			ProcessSummariesType result = request.searchForSimilarProcesses(
					process.getId(), version.getName(),
					this.algosLB.getSelectedItem().getLabel(),
					((Doublebox) this.modelthreshold.getFirstChild().getNextSibling()).getValue(),
					((Doublebox) this.labelthreshold.getFirstChild().getNextSibling()).getValue(),
					((Doublebox)  this.contextthreshold.getFirstChild().getNextSibling()).getValue(),
					((Doublebox)  this.skipnweight.getFirstChild().getNextSibling()).getValue(),
					((Doublebox) this.subnweight.getFirstChild().getNextSibling()).getValue(),
					((Doublebox) this.skipeweight.getFirstChild().getNextSibling()).getValue());

			message = "Search returned " + result.getProcessSummary().size() ;
			if (result.getProcessSummary().size() > 1) {
				message += " processes.";
			} else {
				message += " process.";
			}
			mainC.displayProcessSummaries(result, true, process, version);
		} catch (Exception e) {
			message = "Search failed (" + e.getMessage() + ")";
		}
		this.similaritySearchW.detach();
		mainC.displayMessage(message);
	}

	protected void updateActions() {
		this.OKbutton.setDisabled(false);
		String algo = this.algosLB.getSelectedItem().getLabel();
		this.modelthreshold.setVisible(algo.compareTo("Hungarian")==0 || algo.compareTo("Greedy")==0);
		this.labelthreshold.setVisible(algo.compareTo("Hungarian")==0 || algo.compareTo("Greedy")==0);
		this.contextthreshold.setVisible(algo.compareTo("Hungarian")==0 || algo.compareTo("Greedy")==0);
		this.skipeweight.setVisible(algo.compareTo("Greedy")==0);
		this.subeweight.setVisible(algo.compareTo("Greedy")==0);
		this.skipnweight.setVisible(algo.compareTo("Greedy")==0);
		this.subnweight.setVisible(algo.compareTo("Greedy")==0);
	}
}
