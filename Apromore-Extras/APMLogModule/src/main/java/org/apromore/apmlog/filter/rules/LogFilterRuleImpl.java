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
package org.apromore.apmlog.filter.rules;

import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.Section;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class LogFilterRuleImpl implements LogFilterRule, Serializable {

    private final Choice choice;
    private final Inclusion inclusion;
    private final Section section;
    private final FilterType filterType;
    private final String key;
    private Set<RuleValue> primaryValues;
    private Set<RuleValue> secondaryValues;
    private final Set<String> primaryStringValues;
    private Set<String> secondaryStringValues;

    public LogFilterRuleImpl(Choice choice, Inclusion inclusion, Section section, FilterType filterType,
                             String key,
                             Set<RuleValue> primaryValues, Set<RuleValue> secondaryValues) {
        this.choice = choice;
        this.inclusion = inclusion;
        this.section = section;
        this.filterType = filterType;
        this.primaryValues = primaryValues;
        this.secondaryValues = secondaryValues;
        this.key = key;

        primaryStringValues = new HashSet<>();

        for (RuleValue ruleValue : primaryValues) {
            primaryStringValues.add(ruleValue.getStringValue());
        }

        if (secondaryValues != null) {
            secondaryStringValues = new HashSet<>();
            for (RuleValue ruleValue : secondaryValues) {
                secondaryStringValues.add(ruleValue.getStringValue());
            }
        }
    }

    public void setPrimaryValues(Set<RuleValue> primaryValues) {
        this.primaryValues = primaryValues;
    }

    public void setSecondaryValues(Set<RuleValue> secondaryValues) {
        this.secondaryValues = secondaryValues;
    }

    public Choice getChoice() {
        return choice;
    }


    public Inclusion getInclusion() {
        return inclusion;
    }


    public Section getSection() {
        return section;
    }


    public FilterType getFilterType() {
        return filterType;
    }

    public String getKey() {
        return key;
    }

    public Set<RuleValue> getPrimaryValues() {
        return primaryValues;
    }

    public Set<String> getPrimaryValuesInString() {
        return primaryStringValues;
    }

    public Set<String> getSecondaryValuesInString() {
        return secondaryStringValues;
    }

    public Set<RuleValue> getSecondaryValues() {
        return secondaryValues;
    }

    // ====================================================================================
    // DO NOT USED!! TO BE REMOVED!!
    // ====================================================================================
    public LogFilterRule clone() {
        return deepClone();
    }

    public LogFilterRule deepClone() {

        Set<RuleValue> priValCopy = new HashSet<>();
        for (RuleValue ruleValue : primaryValues) {
            priValCopy.add(ruleValue.clone());
        }

        Set<RuleValue> secValCopy = null;
        if (secondaryValues != null) {
            secValCopy = new HashSet<>();
            for (RuleValue ruleValue : secondaryValues) {
                secValCopy.add(ruleValue.clone());
            }
        }

        return new LogFilterRuleImpl(
                choice, inclusion, section, filterType, key, priValCopy, secValCopy);
    }

    @Override
    public String toString() { return DescriptionProducer.getDescription(this); }

    public String getFilterTypeDesc() {
        return filterType.toDisplay();
    }

    public String getFilterRuleDesc() {
        return toString();
    }
}
