/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
