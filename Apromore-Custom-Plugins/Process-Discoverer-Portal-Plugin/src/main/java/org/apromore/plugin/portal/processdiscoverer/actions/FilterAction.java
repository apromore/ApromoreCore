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

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zul.Messagebox;

/**
 * FilterAction bundles different type of filtering requests to filter log data.
 * Each type of request is implemented by a subclass of FilterAction
 * To maintain the right context, FilterAction keeps track of the filter criteria used
 * before and after the filter action is executed. These criteria are deep cloned
 * from the original criteria kept by <{@link }PDAnalyst>.
 * They are used to support undo/redo and ensures the consistency. For example, the previous
 * criteria are used for undo (the original log is filtered again).
 */
public abstract class FilterAction implements Action {
    protected PDController appController;
    protected PDAnalyst analyst;
    protected List<LogFilterRule> previousFilterCriteria;
    protected List<LogFilterRule> actionFilterCriteria;

    public FilterAction(PDController appController, PDAnalyst analyst) {
        this.appController = appController;
        this.analyst = analyst;
    }
    
    // Call before filtering action is done
    public void setPreviousFilterCriteria(List<LogFilterRule> filterCriteria) {
        this.previousFilterCriteria = filterCriteria;
    }
    
    // Call after filtering is done
    public void setActionFilterCriteria(List<LogFilterRule> filterCriteria) {
        actionFilterCriteria = filterCriteria;
    }
    
    @Override
    public abstract boolean execute();

    
    @Override
    public void undo() {
        try {
            appController.getProcessAnalyst().filter(this.previousFilterCriteria);
            appController.updateUI(false);
        } catch (Exception e) {
            Messagebox.show("Error when undoing filter action. Error message: " + e.getMessage());
        }
    }
}
