package org.apromore.portal.dialogController;

import org.apromore.model.ProcessSummariesType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class APQLFilterController extends BaseFilterController {

    private static final long serialVersionUID = -7879730951994569217L;

    public APQLFilterController(MainController mainController) {
        super(mainController);

        Grid filterGrid = ((Grid) Executions.createComponents("macros/filter/apqlFilter.zul", getMainController(), null));

        final Textbox apqlQuery = (Textbox) filterGrid.getFellow("apqlQuery");
        Button submitButton = (Button) filterGrid.getFellow("submitButton");

        submitButton.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                runAPQL(apqlQuery.getValue());
            }
        });

        appendChild(filterGrid);
    }


    private void runAPQL(String query) {
        try {
            ProcessSummariesType processSummaries = getService().runAPQLExpression(query);
            int nbAnswers = processSummaries.getProcessSummary().size();
            String message = "Search returned " + nbAnswers;
            if (nbAnswers > 1) {
                message += " processes.";
            } else {
                message += " process.";
            }
            getMainController().displayMessage(message);
            getMainController().displayProcessSummaries(processSummaries, true);
        } catch (Exception e)  {
            Messagebox.show(e.getMessage(), "Error", Messagebox.OK, Messagebox.EXCLAMATION);
        }
    }

}
