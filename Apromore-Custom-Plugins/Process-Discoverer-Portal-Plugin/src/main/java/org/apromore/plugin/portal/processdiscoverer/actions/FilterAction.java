/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
import org.apromore.zk.label.LabelSupplier;
import org.zkoss.zul.Messagebox;

/**
 * FilterAction bundles different type of filtering requests to filter log data.
 * Each type of request is bundled as a subclass of FilterAction
 * <p>
 * To be able to do undo/redo, FilterAction keeps track of the filter criteria used by {@link}PDAnalyst
 * before and after the filter action is executed. These criteria are deep cloned from the original one.
 * <p>
 * For undo, PDAnalyst is called with pre-action filter criteria ({@link PDAnalyst#filter}).
 * <p>
 * For redo, PDAnalyst is called with either post-action filter criteria or additive criteria ({@link PDAnalyst#filterAdditive).
 */
public abstract class FilterAction implements Action, LabelSupplier {
    protected PDController appController;
    protected PDAnalyst analyst;
    protected List<LogFilterRule> preActionFilterCriteria;
    protected List<LogFilterRule> postActionFilterCriteria;

    @Override
    public String getBundleName() {
        return "pd";
    }

    public FilterAction(PDController appController, PDAnalyst analyst) {
        this.appController = appController;
        this.analyst = analyst;
    }
    
    // Call before filtering action is done
    public void setPreActionFilterCriteria(List<LogFilterRule> filterCriteria) {
        this.preActionFilterCriteria = filterCriteria;
    }
    
    // Call after filtering is done
    public void setPostActionFilterCriteria(List<LogFilterRule> filterCriteria) {
        postActionFilterCriteria = filterCriteria;
    }
    
    @Override
    public abstract boolean execute();

    
    @Override
    public void undo() throws Exception {
        analyst.filter(this.preActionFilterCriteria);
    }
}
