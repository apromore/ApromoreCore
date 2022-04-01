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
package org.apromore.apmlog.filter.typefilters.between;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.FIRST_OCCURRENCE;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.INCLUDE_SELECTION;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;

public class BetweenFilterRuleAlt {

    private final Choice choice;
    private final RuleValue sourceRuleValue;
    private final RuleValue targetRuleValue;
    private final String attribute;
    private boolean sourceFirstOccur;
    private boolean targetFirstOccur;
    private boolean includeFrom;
    private boolean includeTo;

    private String source;
    private String target;

    public BetweenFilterRuleAlt(LogFilterRule logFilterRule) {
        choice = logFilterRule.getChoice();

        attribute = logFilterRule.getKey();
        sourceRuleValue = BetweenFilterSupport.findValue(OperationType.FROM, logFilterRule);
        targetRuleValue = BetweenFilterSupport.findValue(OperationType.TO, logFilterRule);

        if (sourceRuleValue == null || targetRuleValue == null)
            return;

        sourceFirstOccur = Boolean.parseBoolean(sourceRuleValue.getCustomAttributes().get(FIRST_OCCURRENCE));
        targetFirstOccur = Boolean.parseBoolean(targetRuleValue.getCustomAttributes().get(FIRST_OCCURRENCE));
        includeFrom = Boolean.parseBoolean(sourceRuleValue.getCustomAttributes().get(INCLUDE_SELECTION));
        includeTo = Boolean.parseBoolean(targetRuleValue.getCustomAttributes().get(INCLUDE_SELECTION));
        source = sourceRuleValue.getStringValue();
        target = targetRuleValue.getStringValue();
    }

    public Choice getChoice() {
        return choice;
    }

    public String getAttribute() {
        return attribute;
    }

    public RuleValue getSourceRuleValue() {
        return sourceRuleValue;
    }

    public RuleValue getTargetRuleValue() {
        return targetRuleValue;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public boolean isSourceFirstOccur() {
        return sourceFirstOccur;
    }

    public boolean isTargetFirstOccur() {
        return targetFirstOccur;
    }

    public boolean isIncludeFrom() {
        return includeFrom;
    }

    public boolean isIncludeTo() {
        return includeTo;
    }
}
