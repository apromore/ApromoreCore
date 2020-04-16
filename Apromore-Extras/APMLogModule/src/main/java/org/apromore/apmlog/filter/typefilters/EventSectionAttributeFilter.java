/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.Choice;

import java.util.Set;

public class EventSectionAttributeFilter {

    public static boolean toKeep(AEvent event, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(event, logFilterRule);
            default: return !conformRule(event, logFilterRule);
        }
    }

    private static boolean conformRule(AEvent event, LogFilterRule logFilterRule) {
        String attributeKey = logFilterRule.getKey().toLowerCase();
        Set<String> values = logFilterRule.getPrimaryValuesInString();

        switch (attributeKey) {
            case "concept:name":
                if (values.contains(event.getName())) return true;
                break;
            case "org:resource":
                if (values.contains(event.getResource())) return true;
                break;
            case "lifecycle:transition":
                if (values.contains(event.getLifecycle())) return true;
                break;
            default:
                if (!event.getAttributeMap().keySet().contains(attributeKey)) return false;

                String val = event.getAttributeValue(attributeKey);
                if (values.contains(val)) return true;

                break;
        }

        return false;
    }
}
