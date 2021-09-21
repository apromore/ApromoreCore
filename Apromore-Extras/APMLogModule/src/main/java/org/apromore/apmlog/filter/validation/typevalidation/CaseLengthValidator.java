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
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

public class CaseLengthValidator extends AbstractLogFilterRuleValidator {

    private CaseLengthValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static LogFilterRule validateCaseLength(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule logFilterRule = originalRule.clone();

        long[] caseLengthArray = apmLog.getTraces().stream()
                .mapToLong(x -> x.getActivityInstances().size())
                .toArray();

        LongArrayList lal = new LongArrayList(caseLengthArray);

        return replaceLongValues(logFilterRule, lal);
    }
}
