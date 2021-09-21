/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
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
package org.apromore.apmlog.filter.validation.typevalidation;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CaseIdValidator extends AbstractLogFilterRuleValidator {

    private CaseIdValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static LogFilterRule validateCaseId(LogFilterRule logFilterRule, APMLog apmLog) {

        if (logFilterRule.getPrimaryValues().iterator().next().getCustomAttributes().isEmpty()) return null;

        List<ATrace> traceList = apmLog.getTraces();

        RuleValue priVal = logFilterRule.getPrimaryValues().iterator().next();

        Set<String> ruleCaseIds = priVal.getCustomAttributes().keySet();

        Map<String, Integer> validCustomAttrs = apmLog.getTraces().stream()
                .filter(x -> ruleCaseIds.contains(x.getCaseId()))
                .collect(Collectors.toMap(ATrace::getCaseId, ATrace::getImmutableIndex));

        if (validCustomAttrs.isEmpty())
            return null;

        LogFilterRule cloned = logFilterRule.clone();

        RuleValue clonedPriVal = cloned.getPrimaryValues().iterator().next();

        clonedPriVal.getCustomAttributes().clear();
        BitSet bs = new BitSet(traceList.size());

        for (Map.Entry<String, Integer> entry : validCustomAttrs.entrySet()) {
            bs.set(entry.getValue());
            clonedPriVal.getCustomAttributes().put(entry.getKey(), entry.getValue().toString());
        }

        clonedPriVal.setObjectVal(bs);

        return cloned;
    }
}
