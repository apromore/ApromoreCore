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
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.RuleLevel;
import org.apromore.apmlog.filter.types.Section;
import org.zkoss.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * @author Chii Chang
 * modified: 2022-05-30 by Chii Chang
 * modified: 2022-06-16 by Chii Chang
 */
public interface LogFilterRule {

    Choice getChoice();
    Inclusion getInclusion();
    Section getSection();
    String getKey();
    FilterType getFilterType();
    Set<RuleValue> getPrimaryValues();
    Set<RuleValue> getSecondaryValues();
    Set<RuleValue> getThirdlyValues();
    Set<String> getPrimaryValuesInString();
    Set<String> getSecondaryValuesInString();
    LogFilterRule deepClone();
    void setRuleLevel(RuleLevel ruleLevel);
    RuleLevel getRuleLevel();
    void setKey(String key);

    void setCostPerspective(String costPerspective);
    String getCostPerspective();
    void setCurrency(String currency);
    String getCurrency();
    void setCostRates(Map<String, Double> costRates);
    Map<String, Double> getCostRates();

    JSONObject toJSON();
    Number getPrimaryNumericValueByOperationType(OperationType operationType);

    // ====================================================================================
    // DO NOT USED!! TO BE REMOVED!!
    // ====================================================================================
    LogFilterRule clone();

    void setPrimaryValues(Set<RuleValue> primaryValues);
    void setSecondaryValues(Set<RuleValue> secondaryValues);
    void setThirdlyValues(Set<RuleValue> thirdlyValues);
    String getFilterRuleDesc();
    String getFilterTypeDesc();
}
