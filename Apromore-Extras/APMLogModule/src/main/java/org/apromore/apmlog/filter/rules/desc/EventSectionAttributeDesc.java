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
package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EventSectionAttributeDesc extends AttributeDesc{

    public static String getDescription(LogFilterRule logFilterRule) {

        StringBuilder sb = new StringBuilder();
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        sb.append(choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all activity instances where attribute ");

        String attributeKey = logFilterRule.getKey();

        sb.append(getKeyLabel(attributeKey) + " is equal to [");

        Set<RuleValue> ruleValues = logFilterRule.getPrimaryValues();
        Set<String> valSet = (Set<String>) ruleValues.iterator().next().getObjectVal();
        List<String> valList = new ArrayList<>(valSet);
        Collections.sort(valList);

        for (int i = 0; i < valList.size(); i++) {
            sb.append(valList.get(i));
            if (i < valList.size() -1) {
                sb.append(" OR ");
            }
        }

        sb.append("]");

        return sb.toString();
    }
}
