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

package org.apromore.plugin.portal.processdiscoverer.impl.apmlog;

import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.plugin.portal.logfilter.generic.*;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.controllers.LogFilterController;
import org.apromore.plugin.portal.processdiscoverer.data.InvalidDataException;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;

/**
 * LogFilterControllerWithAPMLog is {@link LogFilterController} but uses APMLog to do filtering.
 *
 * @author Bruce Nguyen
 *
 */
public class LogFilterControllerWithAPMLog extends LogFilterController implements LogFilterClient {
    private LogDataWithAPMLog logData;
    public LogFilterControllerWithAPMLog(PDController controller) throws Exception {
        super(controller);
        if (!(parent.getLogData() instanceof LogDataWithAPMLog)) {
            throw new InvalidDataException("Expect LogDataWithAPMLog data but receiving different data!");
        }
        else {
            logData = (LogDataWithAPMLog)controller.getLogData();
        }
    }

    @Override
    // Open LogFilter window
    public void onEvent(Event event) throws Exception {
        if (event.getData() == null) {
            LogFilterRequest lfr = logData.getCurrentFilterCriteria() == null ||
                    ((List<LogFilterRule>) logData.getCurrentFilterCriteria()).isEmpty() ?
                    getRequestWithOption(new EditorOption(FilterType.CASE_VARIANT)) : getDefaultRequest();
            parent.getLogFilterPlugin().execute(lfr);
        } else if (event.getData() instanceof JSONObject) {
            onInvokeExtEvent((JSONObject) event.getData());
        } else {
            onInvokeEvent(event.getData().toString());
        }
    }

    private void onInvokeEvent(String payload) {
        FilterType filterType = getFilterType(payload);
        LogFilterRule rule = getLastMatchedRule(filterType, (List<LogFilterRule>) logData.getCurrentFilterCriteria());
        EditorOption option = rule != null ? new EditorOption(filterType, rule) : new EditorOption(filterType);
        LogFilterRequest lfr = getRequestWithOption(option);
        parent.getLogFilterPlugin().execute(lfr);
    }

    private void onInvokeExtEvent(JSONObject param) {
        String type = (String) param.get("type");
        FilterType filterType = getFilterType(type);
        String data, source, target;
        Map<String, Object> parameters = new UnifiedMap<>();
        String mainAttribute = parent.getUserOptions().getMainAttributeKey();

        switch (filterType) {
            case CASE_SECTION_ATTRIBUTE_COMBINATION:
            case EVENT_ATTRIBUTE_DURATION:
                data = (String) param.get("data");
                if (filterType == FilterType.EVENT_ATTRIBUTE_DURATION &&
                        !logData.hasSufficientDurationVariant(mainAttribute, data)) {
                    Messagebox.show("Unable to filter on node duration as there's only one value.",
                            "Filter error", Messagebox.OK, Messagebox.ERROR);
                    return;
                }
                parameters.put("key", mainAttribute);
                parameters.put("value", data);
                break;
            case ATTRIBUTE_ARC_DURATION:
                source = (String) param.get("source");
                target = (String) param.get("target");
                if (!logData.hasSufficientDurationVariant(mainAttribute, source, target)) {
                    Messagebox.show("Unable to filter on arc duration as there's only one value.",
                            "Filter error", Messagebox.OK, Messagebox.ERROR);
                    return;
                }
                parameters.put("key", mainAttribute);
                parameters.put("from", source);
                parameters.put("to", target);
                break;
            default:
                return;
        }

        Clients.showBusy("Launch Filter Dialog ...");

        LogFilterRule rule = getLastMatchedRuleWithValues(filterType, parameters,
                (List<LogFilterRule>) logData.getCurrentFilterCriteria());

        EditorOption option = rule != null ? new EditorOption(filterType, rule) :
                new EditorOption(filterType, parameters);

        LogFilterRequest lfr = getRequestWithOption(option);
        parent.getLogFilterPlugin().execute(lfr);
        Clients.clearBusy();
    }

    private boolean isValidEventAttributeDuration(String mainAttribute, String data) {
        if (!logData.hasSufficientDurationVariant(mainAttribute, data)) {
            Messagebox.show("Unable to filter on node duration as there's only one value.",
                    "Filter error", Messagebox.OK, Messagebox.ERROR);
            return false;
        }

        return true;
    }

    private LogFilterRequest getRequestWithOption(EditorOption option) {
        return new LogFilterRequest(this, parent.getSourceLogId(), parent.getTitle(),
                logData.getOriginalAPMLog(), (List<LogFilterRule>) logData.getCurrentFilterCriteria(), option);
    }

    private LogFilterRequest getDefaultRequest() {
        return new LogFilterRequest(this, parent.getSourceLogId(), parent.getTitle(),
                logData.getOriginalAPMLog(), (List<LogFilterRule>) logData.getCurrentFilterCriteria());
    }

    private FilterType getFilterType(String payload) {
        switch (payload) {
            case "CaseTabID": return FilterType.CASE_ID;
            case "CaseTabVariant": return FilterType.CASE_VARIANT;
            case "EventTabAttribute": return FilterType.EVENT_EVENT_ATTRIBUTE;
            case "CaseTabAttribute": return FilterType.CASE_EVENT_ATTRIBUTE;
            case "CaseTabPerformance": return FilterType.DURATION;
            case "CaseTabTimeframe": return FilterType.CASE_TIME;
            default:
                return FilterType.valueOf(payload);
        }
    }

    private LogFilterRule getLastMatchedRule(FilterType filterType, List<LogFilterRule> criteria) {
        if (criteria == null || criteria.isEmpty()) return null;

        List<LogFilterRule> criteriaCopy = new ArrayList<>(criteria);
        Collections.reverse(criteriaCopy);

        return criteriaCopy.stream()
                .filter(x -> asSameFilterGroup(x.getFilterType(), filterType))
                .findFirst()
                .orElse(null);
    }

    private boolean asSameFilterGroup(FilterType ruleFilterType, FilterType targetFilterType) {
        switch (ruleFilterType) {
            case STARTTIME:
            case ENDTIME:
            case CASE_TIME:
                switch (targetFilterType) {
                    case STARTTIME:
                    case ENDTIME:
                    case CASE_TIME:
                        return true;
                    default:
                        return false;
                }
            default:
                return ruleFilterType == targetFilterType;
        }
    }

    private LogFilterRule getLastMatchedRuleWithValues(FilterType filterType,
                                                       Map<String, Object> parameters,
                                                       List<LogFilterRule> criteria) {

        if (criteria == null || criteria.isEmpty()) return null;

        List<LogFilterRule> criteriaCopy = new ArrayList<>(criteria);
        Collections.reverse(criteriaCopy);

        return criteriaCopy.stream()
                .filter(x -> matchRuleValues(x, filterType, parameters))
                .findFirst()
                .orElse(null);
    }

    private boolean matchRuleValues(LogFilterRule logFilterRule,
                                    FilterType filterType,
                                    Map<String, Object> parameters) {
        if (logFilterRule.getFilterType() != filterType) return false;
        if (!logFilterRule.getKey().equals(parameters.get("key"))) return false;

        Set<RuleValue> primVals = logFilterRule.getPrimaryValues();

        if (primVals == null || primVals.isEmpty()) return false;

        switch (filterType) {
            case ATTRIBUTE_ARC_DURATION:
                RuleValue rvFrom = findRuleValueByOpeType(OperationType.FROM, primVals);
                RuleValue rvTo = findRuleValueByOpeType(OperationType.TO, primVals);
                if (rvFrom == null || rvTo == null) return false;

                return rvFrom.getStringValue().equals(parameters.get("from")) &&
                        rvTo.getStringValue().equals(parameters.get("to"));

            case EVENT_ATTRIBUTE_DURATION:
                return primVals.iterator().next().getKey().equals(parameters.get("value"));
            default:
                return false;
        }
    }

    private RuleValue findRuleValueByOpeType(OperationType operationType, Set<RuleValue> ruleValues) {
        return ruleValues.stream()
                .filter(e -> e.getOperationType() == operationType)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void onPluginExecutionFinished(LogFilterOutputResult outputParams) throws Exception {
        // This has been replaced with ZK Event Queue in onEvent().
    }

    @Override
    public void clearFilter() throws Exception {
        logData.clearFilter();
        parent.updateUI(true);
    }

    @Override
    public void processResponse(LogFilterResponse logFilterResponse) {
        PLog pLog = logFilterResponse.getPLog();
        if (!pLog.getPTraceList().isEmpty()) {
            parent.getLogData().setCurrentFilterCriteria(logFilterResponse.getCriteria());
            try {
                logData.updateLog(pLog, logFilterResponse.getApmLog());
                parent.updateUI(true);
            } catch (Exception e) {
                Messagebox.show(e.toString(), "Filter Response Error. " + e.getMessage(),
                        Messagebox.OK,
                        Messagebox.ERROR);
            }

        }
    }
}
