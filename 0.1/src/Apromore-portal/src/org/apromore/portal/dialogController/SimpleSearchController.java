package org.apromore.portal.dialogController;


import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;

import org.apromore.portal.exception.ExceptionDao;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.ProcessSummariesType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Window;


public class SimpleSearchController extends Window {

	private MainController mainC;				// the main controller
	private Window simpleSearchW;				// the window that includes simple search features
	private Hbox previoussearchesH; 			// the box which includes previous search features
												// visible for connected user only
	private Button simplesearchesBu; 			// the button to trigger search
	private SearchHistoriesController previoussearchesCB; 	
												// the controller which manages search histories
	private EventListener searchEventListener;	// event listener associated with simplesearchesBu and previoussearchesCB
	
	public SimpleSearchController(MainController mainController) 
	throws UnsupportedEncodingException, ExceptionDao, JAXBException {

		/*
		 * <hbox>
			<combobox id="previoussearchescombobox" autodrop="true"
				tooltiptext="list of keywords separated by ',' (and semantic) or/and by ';' (or semantic). Brackets allowed." />

			<button id="simplesearchesbutton" height="22px"
				image="img/search.png" style="background:transparent" />
		</hbox>
		 */
		this.mainC = mainController;
		this.simpleSearchW = (Window) this.mainC.getFellow("simplesearchcomp").getFellow("simplesearchwindow");
		this.previoussearchesH = (Hbox) this.simpleSearchW.getFellow("previoussearcheshbox");
		
		// the combobox itself
		this.previoussearchesCB = new SearchHistoriesController(this.mainC);
		
		// its associated button
		this.simplesearchesBu = (Button) this.previoussearchesH.getFellow("previoussearchesbutton");
		
		/**
		 * sets components
		 */
		this.previoussearchesCB.setId("previoussearchescombobox");
		this.previoussearchesCB.setAutodrop(true);
		this.previoussearchesCB.setWidth("85%");
		this.previoussearchesCB.setHeight("100%");
		this.previoussearchesCB.setAttribute("hflex", "1");
		this.previoussearchesCB.setVisible(false);
		this.previoussearchesCB.setTooltiptext("list of keywords separated by ',' (and semantic) or/and by " +
				"';' (or semantic). Brackets allowed.");
		this.previoussearchesH.appendChild(previoussearchesCB);
		
		this.searchEventListener = new EventListener() {
			public void onEvent(Event event) throws Exception {
				processSearch ();				
			}
		};
		this.simplesearchesBu.addEventListener("onClick", this.searchEventListener);
		this.previoussearchesCB.addEventListener("onOK", this.searchEventListener);

	}

	/**
	 * process search specified previous search combobox: display processes satisfying the query
	 * and as a short message the number of those processes.
	 * @throws Exception
	 */
	protected void processSearch() throws Exception  {
		String query = this.previoussearchesCB.getValue();
		RequestToManager request = new RequestToManager();
		ProcessSummariesType processSummaries = request.ReadProcessSummariesType(query);
		int nbAnswers = processSummaries.getProcessSummary().size();
		String message = "Search returned " + nbAnswers ;
		if (nbAnswers > 1) {
			message += " processes.";
		} else {
			message += " process.";
		}
		this.mainC.displayMessage(message);
		this.mainC.displayProcessSummaries (processSummaries);
		/**
		 *Keeps search history up to date
		 */
		this.previoussearchesCB.addSearchHist(query);
	}

	/**
	 * Clears last search left by previous user
	 */
	public void clearSearches() {
		this.previoussearchesCB.setValue("");
	}
	public void Refresh() {
		this.previoussearchesCB.refresh("");	
	}

	public Hbox getPrevioussearchesH() {
		return previoussearchesH;
	}

	public void setPrevioussearchesH(Hbox previoussearchesH) {
		this.previoussearchesH = previoussearchesH;
	}

	public Button getSimplesearchesBu() {
		return simplesearchesBu;
	}

	public void setSimplesearchesBu(Button simplesearchesBu) {
		this.simplesearchesBu = simplesearchesBu;
	}

	public SearchHistoriesController getPrevioussearchesCB() {
		return previoussearchesCB;
	}

	public void setPrevioussearchesCB(SearchHistoriesController previoussearchesCB) {
		this.previoussearchesCB = previoussearchesCB;
	}
	
	
}
