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
package org.apromore.apmlog.filter.validation.typevalidation;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.validation.ValidatedFilterRule;
import org.eclipse.collections.api.tuple.primitive.LongLongPair;

public class TimeframeValidator extends AbstractLogFilterRuleValidator {

    private TimeframeValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static ValidatedFilterRule validateTimeframe(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule logFilterRule = originalRule.deepClone();

        LongLongPair valPair = getFromAndToLongValues(logFilterRule);

        if (valPair.getOne() > apmLog.getEndTime() || valPair.getTwo() < apmLog.getStartTime())
            return createInvalidFilterRuleResult(originalRule);

        long validFrom = Math.max(valPair.getOne(), apmLog.getStartTime());
        long validTo = Math.min(valPair.getTwo(), apmLog.getEndTime());

        if (validFrom == apmLog.getStartTime() && validTo == apmLog.getEndTime())
            return createInvalidFilterRuleResult(originalRule);

        return replaceLongValues(logFilterRule, validFrom, validTo);
    }
}
