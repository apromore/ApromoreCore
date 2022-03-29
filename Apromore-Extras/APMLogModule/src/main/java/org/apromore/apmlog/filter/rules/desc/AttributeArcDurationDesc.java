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
package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AttributeArcDurationDesc {
    public static String getDescription(LogFilterRule logFilterRule) {

        StringBuilder sb = new StringBuilder();

        String fromVal = "", toVal = "";
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            if (ruleValue.getOperationType() == OperationType.FROM) {
                fromVal = ruleValue.getStringValue();
            }
            if (ruleValue.getOperationType() == OperationType.TO) {
                toVal = ruleValue.getStringValue();
            }
        }

        sb.append(logFilterRule.getChoice() == Choice.RETAIN ? "Retain " : "Remove ");
        sb.append(" all cases where the directly-follows relation '");
        sb.append(fromVal).append("' -> '").append(toVal).append("'");
        sb.append(" has duration between [");

        Set<RuleValue> secRuleValues = logFilterRule.getSecondaryValues();
        List<RuleValue> secRuleValueList = new ArrayList<RuleValue>(secRuleValues);
        Collections.sort(secRuleValueList);

        for (int i = 0; i < secRuleValueList.size(); i++) {
            RuleValue rv = secRuleValueList.get(i);
            String unit = rv.getCustomAttributes().get("unit");
            sb.append(TimeUtil.durationStringOf(secRuleValueList.get(i).getDoubleValue(), unit));
            if (i < secRuleValueList.size() -1) {
                sb.append(" AND ");
            }
        }

        sb.append("]");

        return sb.toString();
    }

    private static String getDisplayAttributeKey(String attributeKey) {
        switch (attributeKey) {
            case "concept:name": return "Activity";
            case "org:resource": return "Resource";
            case "org:group": return "Resource group";
            case "org:role": return "Role";
            case "lifecycle:transition": return "Status";
            default: return attributeKey;
        }
    }
}
