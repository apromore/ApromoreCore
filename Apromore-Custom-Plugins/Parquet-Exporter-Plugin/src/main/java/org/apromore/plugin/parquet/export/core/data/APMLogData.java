/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.plugin.parquet.export.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public class APMLogData {

    private static final UnifiedSet<String> invalidCaseAttributeKeys = UnifiedSet.newSetWith("case:variant");
    private static final UnifiedSet<String> invalidEventAttributeKeys = UnifiedSet.newSetWith("activity", "resource");

    private APMLogData() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, List<ATrace>> getCaseAttributeValueGroups(List<ATrace> traceList, String key) {
        return traceList.stream()
            .filter(x -> x.getAttributes().containsKey(key))
            .collect(Collectors.groupingBy(x -> x.getAttributes().get(key)));
    }

    public static Set<String> getUniqueCaseAttributeKeys(PLog pLog) {
        return getUniqueCaseAttributeKeys(new ArrayList<>(pLog.getPTraces()));
    }

    public static Set<String> getUniqueCaseAttributeKeys(List<ATrace> traceList) {
        return traceList.stream().flatMap(x -> x.getAttributes().keySet().stream()
            .filter(n -> !invalidCaseAttributeKeys.contains(n.toLowerCase()))
        ).collect(Collectors.toSet());
    }

    public static Set<String> getUniqueEventAttributeKeys(List<ActivityInstance> activityInstanceList) {
        return activityInstanceList.stream().flatMap(x -> x.getAttributes().keySet().stream()
            .filter(n -> !invalidEventAttributeKeys.contains(n.toLowerCase()))
        ).collect(Collectors.toSet());
    }


    public static Set<String> getUniqueCaseAttributeValues(PLog pLog, String attribute) {
        return getUniqueCaseAttributeValues(new ArrayList<>(pLog.getPTraces()), attribute);
    }

    public static Set<String> getUniqueCaseAttributeValues(List<ATrace> traceList, String attribute) {
        return traceList.stream().filter(x -> x.getAttributes().containsKey(attribute))
            .map(x -> x.getAttributes().get(attribute)).collect(Collectors.toSet());
    }


    public static Set<String> getUniqueEventAttributeValues(List<ActivityInstance> activityInstanceList,
                                                            String attribute) {
        return activityInstanceList.stream().filter(x -> x.getAttributes().containsKey(attribute))
            .map(x -> x.getAttributes().get(attribute)).collect(Collectors.toSet());
    }

    public static Map<String, Integer> getCaseAttributeUniqueValues(PLog pLog) {
        return pLog.getPTraces().stream()
            .flatMap(trace -> trace.getAttributes().entrySet().stream()
                .filter(n -> !invalidCaseAttributeKeys.contains(n.getKey().toLowerCase())))
            .collect(Collectors.groupingBy(Map.Entry::getKey)).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue()
                .stream().collect(Collectors.groupingBy(n -> n)).keySet().size()));
    }

    public static Map<String, Integer> getEventAttributeUniqueValues(PLog pLog) {
        return pLog.getActivityInstances().stream()
            .flatMap(act -> act.getAttributes().entrySet().stream()
                .filter(n -> !invalidEventAttributeKeys.contains(n.getKey().toLowerCase())))
            .collect(Collectors.groupingBy(Map.Entry::getKey)).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue()
                .stream().collect(Collectors.groupingBy(n -> n)).keySet().size()));
    }


}
