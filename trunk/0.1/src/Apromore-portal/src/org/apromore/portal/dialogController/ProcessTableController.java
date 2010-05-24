/**
 * Controller for view processtable.zul
 */

package org.apromore.portal.dialogController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apromore.portal.common.Constants;
import org.apromore.portal.common.ProcessIdColComparator;
import org.apromore.portal.common.ProcessNameColComparator;
import org.apromore.portal.exception.ExceptionDao;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.ProcessSummariesType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;



public class ProcessTableController extends Window {


	/**
	 * the structure of processSummariesGrid is:
	 * grid									processSummariesGrid
	 * 	columns
	 * 		column
	 * 			button 						revertSelectionB
	 * 		column
	 * 			checkbox					processSummaryCB
	 * 	rows								processSummariesRow
	 * --> on row for each process
	 * 		row
	 * 			detail
	 * 				grid
	 * 					columns
	 * 						column
	 * 						column
	 * 					/columns
	 * 					rows
	 * 					--> on row for each version
	 * 						row
	 * 							checkbox	<processVersionId>
	 * 							....
	 * 					/rows
	 * 				/grid
	 * 			detail
	 * 			label
	 * 			label 						<processId>
	 * 			toolbarbutton				<processId
	 * 			label
	 * 			label
	 * 			label
	 * 		row
	 * 	rows
	 * grid
	 */


	private MainController mainC; 							// the main controller
	private Grid processSummariesGrid; 						// the grid for process summaries
	private Rows processSummariesRows; 						// the rows for process summaries
	private Window processTableW;							// the window which the entry point
	private HashMap<Checkbox,ProcessSummaryType> processHM;	// Hasmap of checkboxes: one entry for each process 
	private HashMap<Checkbox,VersionSummaryType> processVersionsHM;// HashMap of checkboxes: one entry for each process version
	private HashMap<Checkbox, List<Checkbox>> mapProcessVersions; // <p, listV> in mapProcessVersions: checkboxes in listV are
															// associated with checkbox p
	private Integer latestVersionPos ;						// position of label latest version in row of process summary
	private Integer processTbPos;							// position of toolbarbuttons associated with process names in rows of process summary

	private Button revertSelectionB ;						// button which reverts the process selections
	private EventListener revertSelectionBlist;				// event listener associated with the revertSelectionB
	private Column columnId ;								// column to display processId
	private Column columnName;								// column to display process name
															
	public ProcessTableController(MainController mainController) throws Exception {

		/**
		 * get components of the process version table part 
		 */
		this.mainC = mainController;
		this.processTableW = (Window) this.mainC.getFellow("processtablecomp").getFellow("processTableWindow");
		this.processSummariesGrid = (Grid) this.processTableW.getFellow("processSummariesGrid");
		this.processSummariesRows = (Rows) this.processSummariesGrid.getFellow("processSummariesRows");
		this.revertSelectionB = (Button) this.processSummariesGrid.getFellow("revertSelectionB");

		this.columnId = (Column) this.processSummariesGrid.getFellow("columnId");
		this.columnName = (Column) this.processSummariesGrid.getFellow("columnName");

		ProcessNameColComparator asc1 = new ProcessNameColComparator(true),
				dsc1 = new ProcessNameColComparator(false);
		this.columnName.setSortAscending(asc1);
		this.columnName.setSortDescending(dsc1);
		ProcessIdColComparator asc2 = new ProcessIdColComparator(true),
				dsc2 = new ProcessIdColComparator(false);
		this.columnId.setSortAscending(asc2);
		this.columnId.setSortDescending(dsc2);
		
		// if change grid layouts modify value accordingly
		this.latestVersionPos = 7;
		this.processTbPos = 3 ;

		// initialize hashmaps
		this.processHM = new HashMap<Checkbox,ProcessSummaryType>();
		this.processVersionsHM = new HashMap<Checkbox,VersionSummaryType>();
		this.mapProcessVersions = new HashMap<Checkbox, List<Checkbox>>();

		/**
		 * At creation of the controller, get summaries of all processes.
		 * for each process: a row in the main grid with detail (grid inside)
		 * no keywords given
		 */
		RequestToManager request = new RequestToManager();
		ProcessSummariesType processSummaries = request.ReadProcessSummariesType("");
		this.mainC.displayMessage(processSummaries.getProcessSummary().size() + " processes.");
		displayProcessSummaries (processSummaries);
	}

	public void emptyProcessSummaries () {
		this.processHM.clear();
		this.processVersionsHM.clear();
		this.mapProcessVersions.clear();
		this.revertSelectionB.removeEventListener("onClick", this.revertSelectionBlist);
		while (this.processSummariesRows.getChildren().size()>0){
			this.processSummariesRows.removeChild(this.processSummariesRows.getFirstChild());
		}
	}

	public void displayProcessSummaries(ProcessSummariesType processSummaries) {
		
		for (int i=0;i<processSummaries.getProcessSummary().size();i++){
			ProcessSummaryType process = processSummaries.getProcessSummary().get(i);
			displayOneProcess (process);		
		}
		
		this.revertSelectionBlist = new EventListener() {
			public void onEvent(Event event) throws Exception {
				revertSelection();				
			}
		};
		// click on revert selection button
		this.revertSelectionB.addEventListener("onClick", this.revertSelectionBlist);
	}


	public void displayOneProcess (ProcessSummaryType process) {
		// one row for each process
		Row processSummaryR = new Row();
		Detail processSummaryD = new Detail();
		processSummaryD.setId(process.getId().toString());
		processSummaryD.setOpen(false);

		this.processSummariesRows.appendChild(processSummaryR);

		/** 
		 * assign process summary values to labels
		 */
		Checkbox processCB = new Checkbox();
		Label processId = new Label(process.getId().toString());

		// update hashmaps
		this.processHM.put(processCB, process);
		List<Checkbox> listV = new ArrayList<Checkbox>();
		this.mapProcessVersions.put(processCB, listV);
		Toolbarbutton processName = new Toolbarbutton(process.getName());
		processName.setStyle(Constants.TOOLBARBUTTON_STYLE);
		
		//	processName.setId(process.getId().toString());

		Label processOriginalLanguage = new Label(process.getOriginalNativeType());
		Label processDomain = new Label(process.getDomain());
		Label processProcessRanking = new Label(process.getRanking().toString());
		Label processLatestVersion = new Label(process.getLastVersion());

		processSummaryR.appendChild(processSummaryD);
		processSummaryR.appendChild(processCB);
		processSummaryR.appendChild(processId);
		processSummaryR.appendChild(processName);
		processSummaryR.appendChild(processOriginalLanguage);
		processSummaryR.appendChild(processDomain);
		processSummaryR.appendChild(processProcessRanking);
		processSummaryR.appendChild(processLatestVersion);

		// click on process name to select it
		processName.addEventListener("onClick", new EventListener() {
			public void onEvent(Event event) throws Exception {
				maintainSelectedProcesses (event);
			}
		});
		// click on "+" to get process details
		processSummaryD.addEventListener("onOpen", new EventListener() {
			public void onEvent(Event event) throws Exception {
				Detail processSummaryD = (Detail) event.getTarget();
				displayVersionsSummaries (processSummaryD);
			}
		});

	}


	/**
	 * Build grid to display version details of the corresponding process
	 * If process selected, highlight latest version
	 * @param Event
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws JAXBException 
	 * @throws ExceptionDao 
	 */
	protected void displayVersionsSummaries (Detail processSummaryD) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException, ExceptionDao, JAXBException {

		/* details might have been already build, in this case the Detail processSummaryD has at least
		 * on children.
		 */
		if (processSummaryD.getChildren().size()==0) {
			Checkbox processCB = (Checkbox) processSummaryD.getNextSibling();

			ProcessSummaryType process = this.processHM.get(processCB);

			// the grid for process versions
			Grid processVersionG = new Grid();

			Column checkboxes = new Column();
			checkboxes.setWidth("0px");		

			Columns versionHeads = new Columns();
			versionHeads.setSizable(true);
			Column headVersionName = new Column("Version name");
			headVersionName.setSort("auto");
			Column headCreationDate = new Column("Creation date");
			headCreationDate.setSort("auto");
			Column headLastUpdate = new Column("Last update");
			headLastUpdate.setSort("auto");
			Column headDocumentation = new Column("Documentation");
			headDocumentation.setSort("auto");
			Column headRanking  = new Column("Ranking");
			headRanking.setSort("auto");
			
			headVersionName.setWidth("15%");
			headCreationDate.setWidth("25%");
			headLastUpdate.setWidth("25%");
			headDocumentation.setWidth("30%");
			headRanking.setWidth("15%");
			
			processVersionG.appendChild(versionHeads);
			versionHeads.appendChild(checkboxes);
			versionHeads.appendChild(headVersionName);
			versionHeads.appendChild(headCreationDate);
			versionHeads.appendChild(headLastUpdate);
			versionHeads.appendChild(headDocumentation);
			versionHeads.appendChild(headRanking);

			processSummaryD.appendChild(processVersionG);
			Rows processVersionsR = new Rows();
			processVersionG.appendChild(processVersionsR);

			for (int j=0;j<process.getVersionSummaries().size();j++){
				VersionSummaryType version = process.getVersionSummaries().get(j);
				/**
				 * for each version a row, with a checkbox identified by processId/versionName
				 */
				Row versionR = new Row();
				versionR.setStyle(Constants.UNSELECTED_VERSION);
				Checkbox versionCB = new Checkbox();
				this.processVersionsHM.put(versionCB,version);
				this.mapProcessVersions.get(processCB).add(versionCB);
				versionCB.setVisible(false);
				versionCB.setId(process.getId().toString() + "/" + version.getName());

				Toolbarbutton versionName = new Toolbarbutton (version.getName());
				versionName.setStyle(Constants.TOOLBARBUTTON_STYLE);
				Label versionCreationDate = new Label (version.getCreationDate().toString());
				Label versionLastUpdate = new Label (version.getLastUpdate().toString());
				Label versionRanking = new Label (version.getRanking().toString());
				Label versionDocumentation = new Label ("to be completed");
				
				processVersionsR.appendChild(versionR);

				versionR.appendChild(versionCB);
				versionR.appendChild(versionName);
				versionR.appendChild(versionCreationDate);
				versionR.appendChild(versionLastUpdate);
				versionR.appendChild(versionDocumentation);
				versionR.appendChild(versionRanking);

				/* the process might has been already selected, thus its latest version has to be marked as 
				 * selected too.
				 */
				if (processCB.isChecked()) {
					if (version.getName().compareTo(process.getLastVersion())==0) {
						versionCB.setChecked(true);
					}
				}
				/*
				 * click on version name
				 */
				versionName.addEventListener("onClick", new EventListener() {
					public void onEvent(Event event) throws Exception {
						revertProcessVersion (event);
					}
				});
			}
		}
	}

	/**
	 * revert selection of version corresponding to event
	 * @param Event event
	 * @throws InterruptedException 
	 * @throws JAXBException 
	 * @throws ExceptionDao 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	protected void revertProcessVersion(Event event) 
	throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, 
			ExceptionDao, JAXBException, NumberFormatException, ParseException {

		// click on a version might have selected/unselected the corresponding process too
		/* for the process:
		 * - if the version is the only one selected => un select the process
		 * - if no versions are selected => select the process
		 */

		Toolbarbutton versionNameB = (Toolbarbutton) event.getTarget();

		Row versionR = (Row) versionNameB.getParent(); 						// selected version
		Rows versionsR = (Rows) versionR.getParent(); 						// rows (versions)
		List<Row> versions = versionsR.getChildren();						// one row for each versions of the same process

		Detail versionD = (Detail) versionsR.getParent().getParent(); 		// detail (related to the process)
		Row processR = (Row) versionD.getParent(); 							// process 

		Checkbox versionCB = (Checkbox) versionNameB.getPreviousSibling();	// checkbox associated with the version
		Checkbox processCB = (Checkbox) processR.getChildren().get(1);		// checkbox associated with the process

		VersionSummaryType version = this.processVersionsHM.get(versionCB);
		ProcessSummaryType process = this.processHM.get(processCB);
		/*
		 * Was the version selected? 
		 * if no, select it and its process
		 */
		if (!versionCB.isChecked()) {
			versionCB.setChecked(true);
			processCB.setChecked(true);
			highlightP (processR, true);
			highlightV (versionR, true);

		} else {
			// the version was selected => unselect it
			versionCB.setChecked(false);
			highlightV (versionR, false);
			// for the same process, if no versions remain selected, unselect the process
			// search whether one is checked 
			int j = 0;
			while (j<versions.size() && !((Checkbox) versions.get(j).getFirstChild()).isChecked()) {
				j++;
			}
			// none is checked
			if (j==versions.size()) {
				processCB.setChecked(false);
				highlightP (processR, false);
			}
		}
	}

	private void highlightV(Row versionR, Boolean highlighted) {

		String selected = Constants.SELECTED_VERSION ;
		String unselected = Constants.UNSELECTED_VERSION;

		if (highlighted) {
			versionR.setStyle(selected);	// highlight version
			ColorFont (versionR, "#FFFFFF");
		} else {
			versionR.setStyle(unselected);
			ColorFont (versionR, "#000000");
		}


	}


	private void highlightP(Row processR, Boolean highlighted) {

		// #E8C2C1 is pink
		// #598DCA is blue
		// #FFFFFF is white
		// #000000 is black

		String selected = Constants.SELECTED_PROCESS ;
		String unselectedEven = Constants.UNSELECTED_EVEN;
		String unselectedOdd = Constants.UNSELECTED_ODD;
		Integer index = processR.getParent().getChildren().indexOf(processR);
		Detail processD = (Detail) processR.getFirstChild();
		if (highlighted) {
			processR.setStyle(selected);
			processD.setStyle(selected);
			ColorFont (processR, "#FFFFFF");
		} else {
			if (index % 2 == 0) {
				//index is even
				processR.setStyle(unselectedEven);
				processD.setStyle(unselectedEven);
			} else {
				//index is odd
				processR.setStyle(unselectedOdd);
				processD.setStyle(unselectedOdd);
			}
			ColorFont (processR, "#000000");
		}
	}

	/** 
	 * control the selected processes: 
	 * - simple click reverse selection of the process
	 * - click + shift selects all processes from the previous selected (if any) and the current one
	 * @param event corresponding to the click
	 * @throws InterruptedException
	 * @throws JAXBException 
	 * @throws ExceptionDao 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	protected void maintainSelectedProcesses(Event event) 
	throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, ExceptionDao, JAXBException, NumberFormatException, ParseException {
		/*
		 * selectedProcess contains selected processes (ordered by id)
		 * 1 - click on a process: reverts selection of it
		 * 2 - click+shift on a process: select all processes whose index
		 * is between the last in the list and the one previously selected (if any)
		 */
		MouseEvent e = (MouseEvent) event;
		Toolbarbutton tb = (Toolbarbutton) e.getTarget(); 	// toolbarbutton whose lable is a processName
		Row processSummaryR = (Row) tb.getParent(); 		// row associated with a process process
		Rows processSummariesR = (Rows) processSummaryR.getParent();	
		Integer index = processSummariesR.getChildren().indexOf(processSummaryR);	// index of the selected row

		// find the process which is the last selected (if any) in the list of displayed processes
		// because there is no warranty on the order of objects returned by getChildren()
		// need to find the max index among those selected.
		Iterator<Row> itRow = processSummariesR.getChildren().iterator();
		int maxIndex = -1,
		curIndex = 0;
		while (itRow.hasNext()){
			Row currentRow = itRow.next();
			curIndex = currentRow.getParent().getChildren().indexOf(currentRow);
			Checkbox currentCB = (Checkbox) currentRow.getChildren().get(1);
			if (currentCB.isChecked()){
				if (curIndex > maxIndex) maxIndex = curIndex;
			}
		}
		// if no selected process, whatever is the click, select the process
		if (maxIndex == -1) {
			reverseProcessSelection(index);
		} else {
			// if click+shift: select all processes between the one click+shift selected
			// and the last one in the list.
			if (e.getKeys()==260){
				Integer lower, upper ;
				if (maxIndex>index) {
					// selection before the last previously selected
					upper = maxIndex - 1;
					lower = index;
				} else {
					// selection after the last previously selected
					upper = index;
					lower = maxIndex + 1;
				}
				// between lower and lower: for those selected do nothing
				// for others: select them, 
				for (int j=lower;j<=upper;j++){
					Row currentRow = (Row) processSummariesR.getChildren().get(j);
					Checkbox currentCB = (Checkbox) currentRow.getChildren().get(1);
					if (!currentCB.isChecked()) reverseProcessSelection(j);
				}
			} else {
				// if simple click: revert the selection
				reverseProcessSelection(index);
			}
		}
	}

	/**
	 * Revert processes selections: perform minus 
	 * 		this.selectedProcess = all processes - this.selectedProcess
	 * @param event
	 * @throws ParseException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws JAXBException 
	 * @throws ExceptionDao 
	 * @throws InterruptedException 
	 * @throws NumberFormatException 
	 * @throws InterruptedException 
	 * @throws JAXBException 
	 * @throws ExceptionDao 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	protected void revertSelection () 
	throws NumberFormatException, InterruptedException, ExceptionDao, JAXBException, ClassNotFoundException, 
		InstantiationException, IllegalAccessException, ParseException {
		Iterator<Row> itRow = this.processSummariesRows.getChildren().iterator();
		int curIndex = 0;
		while (itRow.hasNext()) {
			Row currentRow = itRow.next();
			curIndex = currentRow.getParent().getChildren().indexOf(currentRow);
			reverseProcessSelection (curIndex);
		}
	}


	/**
	 * Reverse process selection.
	 * - if selected -> unselected: unselect associated versions
	 *          if process details open: no version highlighted
	 * - if unselected -> selected: select latest version
	 *          if process details open: highlight latest version
	 * @param Integer is the process index in the list of displayed processed
	 * @throws JAXBException 
	 * @throws ExceptionDao 
	 * @throws InterruptedException 
	 * @throws NumberFormatException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws ParseException 
	 */
	protected void reverseProcessSelection (Integer index) 
	throws NumberFormatException, InterruptedException, ExceptionDao, JAXBException, 
	ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException {	

		Row processSummaryR = (Row) this.processSummariesRows.getChildren().get(index); // row for process
		Label latestVersionL = (Label) processSummaryR.getChildren().get(this.latestVersionPos); // process latest version
		String latestVersion = latestVersionL.getValue();
		Detail processSummaryD = (Detail) processSummaryR.getFirstChild();	// detail for process

		Checkbox cbP = (Checkbox) processSummaryR.getChildren().get(1); // checkbox for process
		ProcessSummaryType processSummaryT = this.processHM.get(cbP);

		if (cbP.isChecked()) {
			// process was selected
			// unselect all selected version(s)
			//unselectProcessVersions (index);
			// no version highlighted
			Grid versionsG = (Grid) processSummaryD.getFirstChild(); // grid for process versions
			Rows versionsR = (Rows) versionsG.getChildren().get(1); // rows for process versions
			List<Row> versionR = versionsR.getChildren();
			for (int i=0;i<versionR.size();i++) {
				Checkbox cbV = (Checkbox) versionR.get(i).getFirstChild();
				cbV.setChecked(false);
				highlightV(versionR.get(i), false);	
				VersionSummaryType versionSummaryT = this.processVersionsHM.get(cbV);
			}

		} else {
			// process was not selected

			if (processSummaryD.getChildren().size() == 0){
				// detail needs to be built
				displayVersionsSummaries (processSummaryD);
			} 
			// highlight latest version
			// find latest version
			Grid versionsG = (Grid) processSummaryD.getFirstChild(); // grid for process versions
			Rows versionsR = (Rows) versionsG.getChildren().get(1); // rows for process versions
			List<Row> versionR = versionsR.getChildren();
			int i = 0;
			while (i < versionR.size()) { 
				Row version = (Row) versionR.get(i);
				Checkbox cbV = (Checkbox) version.getFirstChild();
				if (cbV.getId().split("/")[1].compareTo(latestVersion)==0){
					break;
				}
				i++;
			}
			if (i < versionR.size()) {
				// must be true!
				Checkbox cbV = (Checkbox) versionR.get(i).getFirstChild();
				cbV.setChecked(true);
				highlightV(versionR.get(i), true);	
				VersionSummaryType versionSummaryT = this.processVersionsHM.get(cbV);
			}

		}
		cbP.setChecked(!cbP.isChecked());
		highlightP (processSummaryR,cbP.isChecked());
	}

	private void ColorFont(HtmlBasedComponent v, String color) {
		Iterator<HtmlBasedComponent> itV = v.getChildren().iterator();
		while (itV.hasNext()) {
			((HtmlBasedComponent) itV.next()).setStyle("color:"+color);
		}
	}

	public HashMap<Checkbox, VersionSummaryType> getProcessVersionsHM() {
		return processVersionsHM;
	}

	public HashMap<Checkbox, ProcessSummaryType> getProcessHM() {
		return processHM;
	}

	public HashMap<Checkbox, List<Checkbox>> getMapProcessVersions() {
		return mapProcessVersions;
	}

	/**
	 * Add the process to the table
	 * @param returnedProcess
	 */
	public void displayNewProcess(ProcessSummaryType process) {
		this.displayOneProcess(process);
	}
}
