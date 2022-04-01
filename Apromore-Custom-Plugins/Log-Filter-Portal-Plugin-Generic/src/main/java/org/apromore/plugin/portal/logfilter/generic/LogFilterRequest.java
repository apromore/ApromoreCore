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
import org.apromore.apmlog.filter.rules.LogFilterRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author Chii Chang (2021-03-15)
 */
public class LogFilterRequest {
    private LogFilterClient client;
    private int logId;
    private String logName;
    private APMLog originalLog;
    private List<LogFilterRule> criteria;
    private Map<String, Object> otherParameters;
    private EditorOption editorOption;

    /**
     *
     * @param client the client of LogFilterEE who has to handles LogFilterResponse
     * @param logId the ID of the original EventLog. It is used to find the related Filter Usermetadata from Database
     * @param logName the name of the original EventLog. It is used to display the log name on the window title
     * @param originalLog the APMLog converted from the original EventLog.
     *                    FilterEE will update the statistics of the log for UI
     *                    based on the original log and the criteria at runtime
     * @param criteria the list of LogFilterRule
     * @param otherParameters any optional values
     * @param editorOption for directly open the Filter Editor bypass the default criteria list view.
     */
    public LogFilterRequest(LogFilterClient client,
                            int logId,
                            String logName,
                            APMLog originalLog,
                            List<LogFilterRule> criteria,
                            Map<String, Object> otherParameters,
                            EditorOption editorOption) {
        this.client = client;
        this.logId = logId;
        this.logName = logName;
        this.originalLog = originalLog;
        this.criteria = criteria;
        this.otherParameters = otherParameters != null ? otherParameters : new HashMap<>();
        this.editorOption = editorOption;
    }

    /** Init without options **/
    public LogFilterRequest(LogFilterClient client,
                            int logId,
                            String logName,
                            APMLog originalLog,
                            List<LogFilterRule> criteria) {
        this.client = client;
        this.logId = logId;
        this.logName = logName;
        this.originalLog = originalLog;
        this.criteria = criteria;
    }

    /** Init without EditorOption **/
    public LogFilterRequest(LogFilterClient client,
                            int logId,
                            String logName,
                            APMLog originalLog,
                            List<LogFilterRule> criteria,
                            Map<String, Object> otherParameters) {
        this.client = client;
        this.logId = logId;
        this.logName = logName;
        this.originalLog = originalLog;
        this.criteria = criteria;
        this.otherParameters = otherParameters != null ? otherParameters : new HashMap<>();
    }

    /** Init without otherParameters **/
    public LogFilterRequest(LogFilterClient client,
                            int logId,
                            String logName,
                            APMLog originalLog,
                            List<LogFilterRule> criteria,
                            EditorOption editorOption) {
        this.client = client;
        this.logId = logId;
        this.logName = logName;
        this.originalLog = originalLog;
        this.criteria = criteria;
        this.editorOption = editorOption;
    }

    public LogFilterClient getClient() {
        return client;
    }

    public int getLogId() {
        return logId;
    }

    public String getLogName() {
        return logName;
    }

    public APMLog getOriginalLog() {
        return originalLog;
    }

    public List<LogFilterRule> getCriteria() {
        return criteria;
    }

    public Map<String, Object> getOtherParameters() {
        return otherParameters;
    }

    public EditorOption getEditorOption() {
        return editorOption;
    }
}
