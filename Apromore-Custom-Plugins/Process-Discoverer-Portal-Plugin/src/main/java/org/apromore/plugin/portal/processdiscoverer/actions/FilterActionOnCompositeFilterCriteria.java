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

import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.zkoss.zul.Messagebox;

/*
 * This filter action is used with the LogFilter window.
 * It is a special action because the filtering is actually executed inside the window (when click on the OK button)
 * instead of this action. Therefore, this action's execute() only does some data preparation to support undo and redo.
 * It should be used with PDController.storeAction rather than PDController.executeAction.
 */
public class FilterActionOnCompositeFilterCriteria extends FilterAction {
    public FilterActionOnCompositeFilterCriteria(PDController appController, PDAnalyst analyst) {
        super(appController, analyst);
    }
    
    /**
     * This is only called via redo while the first execution is done by the LogFilter window
     */
    @Override
    public boolean execute() {
        try {
            analyst.filter(postActionFilterCriteria);
            return true;
        } catch (Exception e) {
            // LOGGER.error("Error in filtering. Error message: " + e.getMessage());
            Messagebox.show("Error in filtering");
            return false;
        }
    }

}
