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

import java.util.List;
import java.util.stream.Collectors;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zul.Messagebox;

public abstract class FilterAction implements Action {
    protected PDController appController;
    protected PDAnalyst analyst;
    protected List<LogFilterRule> previousFilterCriteria;
    protected String filterValue;
    protected String filterAttributeKey;

    public FilterAction(PDController appController, PDAnalyst analyst) {
        this.appController = appController;
        this.analyst = analyst;
    }
    
    public void setExecutionParams(String filterValue, String filterAttributeKey) {
        this.filterValue = filterValue;
        this.filterAttributeKey = filterAttributeKey;
    }
    
    protected List<LogFilterRule> copyCurrentFilterCriteria() {
        return ((List<LogFilterRule>)analyst.getCurrentFilterCriteria())
            .stream()
            .map((c) -> c.clone())
            .collect(Collectors.toList());
    }
    
    protected void showEmptyLogMessageBox() {
        Messagebox.show("The log is empty after applying all filter criteria! Please use different criteria.",
              "Process Discoverer",
              Messagebox.OK,
              Messagebox.INFORMATION);
    }
    
    @Override
    public abstract boolean execute();

    
    @Override
    public void undo() {
        PDAnalyst analyst = appController.getProcessAnalyst();
        analyst.filter(this.previousFilterCriteria);
        appController.updateUI(false);
        appController.updateUndoRedoButtons();
    }
}
