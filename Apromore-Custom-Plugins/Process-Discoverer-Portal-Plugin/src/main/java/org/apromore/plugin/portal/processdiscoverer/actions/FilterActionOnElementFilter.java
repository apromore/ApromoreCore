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
import org.zkoss.zhtml.Messagebox;

/**
 * This abstract action is used for filtering on one single element (node or arc, one at a time).
 * It is implemented by different types of actions: retain or remove trace or events
 */
public abstract class FilterActionOnElementFilter extends FilterAction {
    protected String elementValue;
    protected String attributeKey;
    
    public FilterActionOnElementFilter(PDController appController, PDAnalyst analyst) {
        super(appController, analyst);
    }
    
    @Override
    public boolean execute() {
        try {
            if (elementValue == null || elementValue.isEmpty() || attributeKey == null || attributeKey.isEmpty()) {
                Messagebox.show("Invalid element filter action: element and attribute key were not set!");
            }
            setPreActionFilterCriteria(analyst.copyCurrentFilterCriteria());
            if (performFiltering(this.elementValue, this.attributeKey)) {
                return true;
            }
            else {
                Messagebox.show("The log is empty after applying all filter criteria! Please use different criteria.",
                        "Process Discoverer",
                        Messagebox.OK,
                        Messagebox.INFORMATION);
                return false;
            }
        } catch (Exception e) {
            // LOGGER.error("Error in filtering. Error message: " + e.getMessage());
            Messagebox.show("Error in filtering");
            return false;
        }
    }
    
    public void setElement(String elementValue, String attributeKey) {
        this.elementValue = elementValue;
        this.attributeKey = attributeKey;
    }
    
    public abstract boolean performFiltering(String elementValue, String attributeKey) throws Exception;
}
