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

import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Inclusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class AttributeDesc {

    protected static String getDescriptionFromSetValue(Set<RuleValue> ruleValues, Inclusion inclusion) {

        Set<String> valSet = ruleValues != null && !ruleValues.isEmpty() ?
                (Set<String>) ruleValues.iterator().next().getObjectVal() : null;

        if (valSet == null) return "";

        List<String> valList = new ArrayList<>(valSet);
        Collections.sort(valList);

        StringBuilder sb = new StringBuilder();

        if (valList.size() > 1) sb.append("[");

        int count = 0;
        for (String s : valList) {
            sb.append("'" + s + "'");
            if (count < valList.size() -1) {
                sb.append(inclusion == Inclusion.ANY_VALUE ? " OR " : " AND ");
            }
            count += 1;
        }

        if (valList.size() > 1) sb.append("]");

        return sb.toString();
    }

    protected static String getKeyLabel(String attributeKey) {
        switch (attributeKey) {
            case "concept:name": return "Activity";
            case "org:resource": return "Resource";
            case "org:group": return "Group";
            case "org:role": return "Role";
            case "lifecycle:transition": return "Status";
            default: return attributeKey;
        }
    }
}
