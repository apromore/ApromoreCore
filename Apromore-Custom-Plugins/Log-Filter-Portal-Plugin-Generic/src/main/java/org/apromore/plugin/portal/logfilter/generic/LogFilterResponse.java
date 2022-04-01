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
package org.apromore.plugin.portal.logfilter.generic;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;

import java.util.List;
import java.util.Map;

/**
 * @author Chii Chang (2021-03-15)
 */
public class LogFilterResponse {
    private int logId;
    private String logName;
    private APMLog apmLog;
    private PLog pLog;
    private List<LogFilterRule> criteria;
    private Map<String, Object> otherParameters;

    public LogFilterResponse(int logId, String logName, APMLog apmLog, PLog pLog, List<LogFilterRule> criteria,
                             Map<String, Object> otherParameters) {
        this.logId = logId;
        this.logName = logName;
        this.apmLog = apmLog;
        this.pLog = pLog;
        this.criteria = criteria;
        this.otherParameters = otherParameters;
    }

    public int getLogId() {
        return logId;
    }

    public String getLogName() {
        return logName;
    }

    public APMLog getApmLog() {
        return apmLog;
    }

    public PLog getPLog() {
        return pLog;
    }

    public List<LogFilterRule> getCriteria() {
        return criteria;
    }

    public Map<String, Object> getOtherParameters() {
        return otherParameters;
    }
}
