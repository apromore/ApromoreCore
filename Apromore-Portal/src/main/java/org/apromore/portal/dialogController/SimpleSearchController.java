package org.apromore.portal.dialogController;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;

import org.apromore.model.ProcessSummariesType;
import org.apromore.portal.exception.ExceptionDao;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Window;

public class SimpleSearchController extends BaseController {

    private MainController mainC;
    private Hbox previoussearchesH;
    private Button simplesearchesBu;
    private SearchHistoriesController previoussearchesCB;

    public SimpleSearchController(MainController mainController) throws UnsupportedEncodingException, ExceptionDao, JAXBException {
        /*
           * <hbox>
              <combobox id="previoussearchescombobox" autodrop="true"
                  tooltiptext="list of keywords separated by ',' (and semantic) or/and by ';' (or semantic). Brackets allowed." />

              <button id="simplesearchesbutton" height="22px"
                  image="img/search.png" style="background:transparent" />
          </hbox>
           */
        this.mainC = mainController;
        Window simpleSearchW = (Window) this.mainC.getFellow("simplesearchcomp").getFellow("simplesearchwindow");
        this.previoussearchesH = (Hbox) simpleSearchW.getFellow("previoussearcheshbox");
        this.previoussearchesCB = new SearchHistoriesController(this.mainC, this);
        this.simplesearchesBu = (Button) this.previoussearchesH.getFellow("previoussearchesbutton");
        this.previoussearchesCB.setId("previoussearchescombobox");
        this.previoussearchesCB.setAutodrop(true);
        this.previoussearchesCB.setWidth("95%");
        this.previoussearchesCB.setHeight("100%");
        this.previoussearchesCB.setAttribute("hflex", "1");
        this.previoussearchesCB.setTooltiptext("list of keywords separated by ',' (and semantic) or/and by ';' (or semantic). Brackets allowed.");
        this.previoussearchesH.appendChild(previoussearchesCB);

        EventListener searchEventListener = new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                processSearch();
            }
        };
        this.simplesearchesBu.addEventListener("onClick", searchEventListener);
        this.previoussearchesCB.addEventListener("onOK", searchEventListener);
    }

    /**
     * process search specified previous search combobox: display processes satisfying the query
     * and as a short message the number of those processes.
     *
     * @throws Exception
     */
    protected void processSearch() throws Exception {
        String query = this.previoussearchesCB.getValue();
        ProcessSummariesType processSummaries = getService().readProcessSummaries(query);
        int nbAnswers = processSummaries.getProcessSummary().size();
        String message = "Search returned " + nbAnswers;
        if (nbAnswers > 1) {
            message += " processes.";
        } else {
            message += " process.";
        }
        this.mainC.displayMessage(message);
        this.mainC.displayProcessSummaries(processSummaries, true);
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
