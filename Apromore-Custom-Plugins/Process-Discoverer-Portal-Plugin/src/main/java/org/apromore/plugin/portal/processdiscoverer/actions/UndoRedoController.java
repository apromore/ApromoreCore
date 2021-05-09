/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.plugin.portal.processdiscoverer.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UndoRedoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UndoRedoController.class);

    private PDController parent;
    private FilterActionHistory filterActionHistory;

    public UndoRedoController(
        PDController parent) {
        this.parent = parent;
        List<LogFilterRule> initialFilterCriteria = new ArrayList<LogFilterRule>();
        FilterAction initialFilterAction = new FilterAction("NO_FILTER", initialFilterCriteria);
        filterActionHistory = new FilterActionHistory(initialFilterAction);
    }

    public void add(String actionName) {
        List<LogFilterRule> currentFilterCriteria = (List<LogFilterRule>)parent.getProcessAnalyst().getCurrentFilterCriteria();
        // deep copy
        List<LogFilterRule> filterCriteria = currentFilterCriteria
            .stream()
            .map((c) -> c.clone())
            .collect(Collectors.toList());
        /*
        // shallow copy
        List<LogFilterRule> filterCriteria = new ArrayList<LogFilterRule>(
            (List<LogFilterRule>)parent.getLogData().getCurrentFilterCriteria()
        );
        */
        FilterAction filterAction = new FilterAction(actionName, filterCriteria);
        filterActionHistory.add(filterAction);
        parent.updateUndoRedoButtons(filterActionHistory.canUndo(), filterActionHistory.canRedo());
    }

    /**
     * Common logic for undo and redo
     *
     * @param action
     * @throws Exception
     */
    public void execute (FilterAction action) throws Exception {
        List<LogFilterRule> filterCriteria = action.getFilterCriteria();
        PDAnalyst analyst = parent.getProcessAnalyst();
        // logData.setCurrentFilterCriteria(filterCriteria); // done by the next method
        analyst.filter(filterCriteria);
        parent.updateUI(false);
        parent.updateUndoRedoButtons(filterActionHistory.canUndo(), filterActionHistory.canRedo());
    }

    public void undo() {
        try {
            FilterAction action = (FilterAction) filterActionHistory.undo();
            execute(action);
        } catch (Exception e) {
            LOGGER.error("Errors occured while attempting undo: " + e.getMessage());
        }
    }

    public void redo() {
        try {
            FilterAction action = (FilterAction) filterActionHistory.redo();
            execute(action);
        } catch (Exception e) {
            LOGGER.error("Errors occured while attempting redo: " + e.getMessage());
        }
    }
}
