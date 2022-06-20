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

import lombok.Getter;
import lombok.Setter;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.RuleLevel;
import org.apromore.apmlog.filter.types.Section;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Chii Chang
 * modified: 2022-05-30 by Chii Chang
 * modified: 2022-06-16 by Chii Chang
 */
@Setter
@Getter
public class LogFilterRuleImpl implements LogFilterRule, Serializable {

    protected final FilterType filterType;
    protected final Choice choice;
    protected Inclusion inclusion;
    protected Section section;
    protected String key;
    protected Set<RuleValue> primaryValues;
    protected Set<RuleValue> secondaryValues;
    protected Set<RuleValue> thirdlyValues;
    protected Set<String> primaryStringValues;
    protected Set<String> secondaryStringValues;
    protected RuleLevel ruleLevel = RuleLevel.CONTENT;
    protected String currency = "AUD";
    protected String costPerspective;
    protected Map<String, Double> costRates = new HashMap<>();

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

    public static LogFilterRuleImpl init(FilterType filterType,
                                         boolean retain,
                                         Set<RuleValue> primaryValues) {
        Choice choice = retain ? Choice.RETAIN : Choice.REMOVE;
        Section section = FilterType.isCaseFilter(filterType) ? Section.CASE : Section.EVENT;
        return new LogFilterRuleImpl(choice, Inclusion.ALL_VALUES, section, filterType,
                "", primaryValues, null);
    }

    public LogFilterRuleImpl withCostRates(String currency, String costPerspective, Map<String, Double> costRates) {
        this.currency = currency;
        this.costPerspective = costPerspective;
        this.costRates.clear();
        this.costRates.putAll(costRates);
        return this;
    }

    public LogFilterRuleImpl includeAll(boolean includeAll) {
        this.inclusion = includeAll ? Inclusion.ALL_VALUES : Inclusion.ANY_VALUE;
        return this;
    }

    public LogFilterRuleImpl withKey(String key) {
        this.key = key;
        return this;
    }

    public LogFilterRuleImpl withInclusion(Inclusion inclusion) {
        this.inclusion = inclusion;
        return this;
    }

    public LogFilterRuleImpl withSection(Section section) {
        this.section = section;
        return this;
    }

    public LogFilterRuleImpl withPrimaryValues(Set<RuleValue> primaryValues) {
        this.primaryValues = primaryValues;
        updatePrimaryStringValues();
        return this;
    }

    public LogFilterRuleImpl withSecondaryValues(Set<RuleValue> secondaryValues) {
        this.secondaryValues = secondaryValues;
        updateSecondaryStringValues();
        return this;
    }

    private void updatePrimaryStringValues() {
        primaryStringValues = new HashSet<>();

        for (RuleValue ruleValue : primaryValues) {
            primaryStringValues.add(ruleValue.getStringValue());
        }
    }

    private void updateSecondaryStringValues() {
        if (secondaryValues != null) {
            secondaryStringValues = new HashSet<>();
            for (RuleValue ruleValue : secondaryValues) {
                secondaryStringValues.add(ruleValue.getStringValue());
            }
        }
    }

    public LogFilterRuleImpl withThirdlyValues(Set<RuleValue> thirdlyValues) {
        this.thirdlyValues = thirdlyValues;
        return this;
    }

    public Set<String> getPrimaryValuesInString() {
        return primaryStringValues;
    }

    public Set<String> getSecondaryValuesInString() {
        return secondaryStringValues;
    }

    @Override
    public Number getPrimaryNumericValueByOperationType(OperationType operationType) {
        if (primaryValues == null) {
            return 0;
        }

        return primaryValues.stream()
                .filter(x -> x.getOperationType() == operationType)
                .map(RuleValue::getDoubleValue)
                .findFirst()
                .orElse(0.0);
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

        Set<RuleValue> thiValCopy = null;
        if (thirdlyValues != null) {
            thiValCopy = new HashSet<>();
            for (RuleValue ruleValue : thirdlyValues) {
                thiValCopy.add(ruleValue.clone());
            }
        }

        return LogFilterRuleImpl.init(filterType, choice == Choice.RETAIN, priValCopy)
                .withKey(key)
                .withInclusion(inclusion)
                .withSection(section)
                .withSecondaryValues(secValCopy)
                .withThirdlyValues(thiValCopy)
                .withCostRates(currency, costPerspective, new HashMap<>(costRates));
    }

    @Override
    public String toString() { return DescriptionProducer.getDescription(this); }

    public String getFilterTypeDesc() {
        return filterType.toDisplay();
    }

    public String getFilterRuleDesc() {
        return toString();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonCriterion = new JSONObject();

        jsonCriterion.put("filtertype", filterType.toString());
        jsonCriterion.put("choice", choice.toString());
        jsonCriterion.put("inclusion", inclusion.toString());
        jsonCriterion.put("section", section.toString());
        jsonCriterion.put("key", key);

        appendRuleValues(jsonCriterion, primaryValues, "primaryvalues");
        appendRuleValues(jsonCriterion, secondaryValues, "secondaryvalues");
        appendRuleValues(jsonCriterion, thirdlyValues, "thirdlyvalues");

        if (costPerspective != null && !costPerspective.isEmpty()) {
            jsonCriterion.put("costperspective", costPerspective);
        }

        // ================================================
        // Cost rate-related values are runtime parameters.
        // They will not be stored in the JSON.
        // ================================================

        return jsonCriterion;
    }

    private void appendRuleValues(JSONObject jsonCriterion, Set<RuleValue> ruleValueSet, String jsonKey) {
        if (ruleValueSet != null && !ruleValueSet.isEmpty()) {
            JSONArray jsonArray = new JSONArray();
            for (RuleValue rv : ruleValueSet) {
                jsonArray.add(rv.toJSON());
            }
            jsonCriterion.put(jsonKey, jsonArray);
        }
    }
}
