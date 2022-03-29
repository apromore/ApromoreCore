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

import java.util.Arrays;
import java.util.List;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.plugin.portal.generic.PluginOutputResult;
import org.deckfour.xes.model.XLog;

/**
 * @author Bruce Hoang Nguyen (30/08/2019)
 */
public class LogFilterOutputResult extends PluginOutputResult {
    private XLog log;
    private List<LogFilterRule> filterCriteria;
    
    public LogFilterOutputResult(Object...objects) throws LogFilterWrongOutputException {
        super(objects);
        List<Object> objectList = Arrays.asList(objects);
        try {
            this.log = (XLog)objectList.get(0);
            this.filterCriteria = (List<LogFilterRule>)objectList.get(1);
            this.resultCode = SUCCESS_CODE;
            this.resultMessage = "Success";
        }
        catch (Exception ex) {
            this.resultMessage = "Wrong output parameters returned from LogFilter plugin";
            this.resultCode = -1;
            throw new LogFilterWrongOutputException(this.resultMessage);
        }
    }
    
    public LogFilterOutputResult(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
    
    public XLog getLog() {
        return this.log;
    }
    
    public List<LogFilterRule> getFilterCriteria() {
        return this.filterCriteria;
    }
}
