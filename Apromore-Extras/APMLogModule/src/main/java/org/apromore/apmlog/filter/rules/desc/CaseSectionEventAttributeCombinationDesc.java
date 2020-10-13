/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
import org.apromore.apmlog.filter.types.Inclusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CaseSectionEventAttributeCombinationDesc {
    public static String getDescription(LogFilterRule logFilterRule) {

        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all cases where ";

        String attributeKey = getDisplayName(logFilterRule.getPrimaryValues().iterator().next().getKey());

        String firstVal = logFilterRule.getPrimaryValuesInString().iterator().next();

        desc += "'" + attributeKey + "' is equal to [" + firstVal + "] and ";

        String secondKey = getDisplayName(logFilterRule.getSecondaryValues().iterator().next().getKey());

        desc += "'" + secondKey + "' contains [";

        Set<String> secondValues = logFilterRule.getSecondaryValuesInString();
        List<String> secondValuesList = new ArrayList<>(secondValues);
        Collections.sort(secondValuesList);

        for (int i = 0; i < secondValuesList.size(); i++) {
            desc += secondValuesList.get(i);
            if (i < secondValuesList.size() -1) {
                desc += logFilterRule.getInclusion() == Inclusion.ANY_VALUE ? " OR " : " AND ";
            }
        }

        desc += "]";

        return desc;
    }

    private static String getDisplayName(String attributeKey) {
        switch (attributeKey) {
            case "concept:name": return "Activity";
            case "org:resource": return "Resource";
            case "org:group": return "Resource Group";
            default: return attributeKey;
        }
    }
}
