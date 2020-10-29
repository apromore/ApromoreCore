/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.apmlog.filter;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.deckfour.xes.model.XLog;

import java.util.List;

/**
 * @author Chii Chang (Last modified: 28/01/2020)
 * Modified: Chii Chang (11/04/2020)
 */
public class APMLogFilterPackage { 

    private String filteredLogName;
    private APMLog filteredAPMLog;
    private PLog pLog;
    private XLog filteredXLog;
    private List<LogFilterRule> criteria;
    public APMLogFilterPackage(APMLog apmLog,
                               PLog pLog,
                               XLog filteredXLog,
                               List<LogFilterRule> criteria) {
        this.filteredAPMLog = filteredAPMLog;
        this.pLog = pLog;
        this.filteredXLog = filteredXLog;
        this.criteria = criteria;
    }
    public APMLogFilterPackage(String filteredLogName,
                               APMLog filteredAPMLog,
                               PLog pLog,
                               XLog filteredXLog,
                               List<LogFilterRule> criteria) {
        this.filteredLogName = filteredLogName;
        this.filteredAPMLog = filteredAPMLog;
        this.pLog = pLog;
        this.filteredXLog = filteredXLog;
        this.criteria = criteria;
    }

    public String getFilteredLogName() {
        return filteredLogName;
    }

    public APMLog getFilteredAPMLog() {
        return filteredAPMLog;
    }

    public PLog getPLog() {
        return pLog;
    }

    public XLog getFilteredXLog() {
        return filteredXLog;
    }

    public List<LogFilterRule> getCriteria() {
        return criteria;
    }

}
