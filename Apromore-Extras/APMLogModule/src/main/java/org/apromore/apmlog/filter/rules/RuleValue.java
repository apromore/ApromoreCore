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
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chii Chang
 * modified: 2022-06-16 by Chii Chang
 */
@Getter
@Setter
public class RuleValue implements Comparable<RuleValue>, Serializable {

    private final FilterType filterType;
    private final OperationType operationType;
    private String key;
    private String stringVal = "";
    private Number numericVal = 0;
    private Object objectVal;
    private BitSet bitSetValue;
    private final Set<String> stringSetValue = new UnifiedSet<>();

    private final Map<String, String> customAttributes = new UnifiedMap<>();

    public RuleValue(FilterType filterType, OperationType operationType, String key, Object objectVal) {
        this.filterType = filterType;
        this.operationType = operationType;
        this.key = key;
        setObjectVal(objectVal);
        this.stringVal = objectVal.toString();
    }

    public RuleValue(FilterType filterType, OperationType operationType, String key, BitSet bitSetValue) {
        this.filterType = filterType;
        this.operationType = operationType;
        this.key = key;
        this.objectVal = bitSetValue;
        this.stringVal = objectVal.toString();
        this.bitSetValue = bitSetValue;
    }

    public RuleValue(FilterType filterType, OperationType operationType, String key, Set<String> stringSetValue) {
        this.filterType = filterType;
        this.operationType = operationType;
        this.key = key;
        this.objectVal = stringSetValue;
        this.stringVal = objectVal.toString();
        setStringSetValue(stringSetValue);
    }

    public RuleValue(FilterType filterType, OperationType operationType, String key, String stringVal) {
        this.filterType = filterType;
        this.operationType = operationType;
        this.key = key;
        this.stringVal = stringVal;
    }

    public RuleValue(FilterType filterType, OperationType operationType, String key, Number numericVal) {
        if (numericVal == null) {
            numericVal = 0;
        }
        this.filterType = filterType;
        this.operationType = operationType;
        this.key = key;
        this.numericVal = numericVal;
        this.stringVal = numericVal + "";
    }

    private void setStringSetValue(Set<String> strings) {
        stringSetValue.clear();
        stringSetValue.addAll(strings);
    }

    public void putCustomAttribute(String key, String value) {
        customAttributes.put(key, value);
    }

    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes.clear();
        for (String k : customAttributes.keySet()) {
            this.customAttributes.put(k, customAttributes.get(k));
        }
    }

    public void setIntVal(int intVal) {
        this.numericVal = intVal;
    }

    public void setLongVal(long longVal) {
        this.numericVal = longVal;
    }

    public void setDoubleVal(double doubleVal) {
        this.numericVal = doubleVal;
    }

    public long getLongValue() {
        return numericVal.longValue();
    }

    public double getDoubleValue() {
        return numericVal.doubleValue();
    }

    public int getIntValue() {
        return numericVal.longValue() > Integer.MAX_VALUE ? Integer.MAX_VALUE : numericVal.intValue();
    }

    private Set<String> getSingleValSet() {
        return new HashSet<>(List.of(stringVal));
    }

    public Object getObjectVal() {
        // to be compliant to the old methods
        if (objectVal == null && stringVal != null) {
            return getSingleValSet();
        }
        return objectVal;
    }

    public Set<String> getStringSetValue() {
        if (!stringSetValue.isEmpty())
            return stringSetValue;

        if (objectVal != null && objectVal instanceof Set) {
            Set<Object> objSet = (Set<Object>) objectVal;
            setStringSetValue(objSet.stream().map(Object::toString).collect(Collectors.toSet()));
        }

        if (stringVal != null)
            setStringSetValue(getSingleValSet());

        return stringSetValue;
    }

    public String getStringValue() {
        return stringVal;
    }

    public void setObjectVal(Object obj) {
        this.objectVal = obj;

        if (obj instanceof Set)
            setStringSetValue(((Set<Object>) obj).stream().map(Object::toString).collect(Collectors.toSet()));

        if (obj instanceof BitSet)
            bitSetValue = (BitSet) obj;

        if (filterType == FilterType.CASE_SECTION_ATTRIBUTE_COMBINATION) {
            this.stringVal = stringSetValue.iterator().next();
        }
    }

    public RuleValue clone() {
        return deepClone();
    }

    public RuleValue deepClone() {

        Map<String, String> customAttrCopy = null;

        if (customAttributes.size() > 0) {
            customAttrCopy = new HashMap<>();
            for (String key : customAttributes.keySet()) {
                customAttrCopy.put(key, customAttributes.get(key));
            }
        }

        RuleValue rv;

        if (numericVal.doubleValue() != 0) {
            rv = new RuleValue(filterType, operationType, key, numericVal);
        } else if (!stringSetValue.isEmpty()) {
            rv = new RuleValue(filterType, operationType, key, stringSetValue);
        } else if (bitSetValue != null) {
            rv = new RuleValue(filterType, operationType, key, bitSetValue);
        } else if (objectVal != null) {
            rv = new RuleValue(filterType, operationType, key, objectVal);
        } else {
            rv = new  RuleValue(filterType, operationType, key, stringVal);
        }

        if (customAttrCopy != null) rv.setCustomAttributes(customAttrCopy);

        return rv;
    }

    @Override
    public int compareTo(@NotNull RuleValue ruleValue) {
        double d = getDoubleValue();
        if (d != 0 && ruleValue.getDoubleValue() != 0) {
            return Double.compare(d, ruleValue.getDoubleValue());
        } else {
            return this.stringVal.compareTo(ruleValue.getStringValue());
        }
    }

    public JSONObject toJSON() {
        JSONObject jsonRuleValue = new JSONObject();

        jsonRuleValue.put("filtertype", getFilterType().toString());
        jsonRuleValue.put("operationtype", getOperationType().toString());
        jsonRuleValue.put("key", getKey());
        jsonRuleValue.put("stringvalue", stringVal);
        jsonRuleValue.put("longvalue", getLongValue() + "");
        jsonRuleValue.put("doublevalue", getDoubleValue() + "");
        jsonRuleValue.put("intvalue", getIntValue() + "");

        // ================================================================
        // Case ID-based ruleValues were stored in BitSet
        // ================================================================
        if (getObjectVal() instanceof BitSet) {
            jsonRuleValue.put("objectvalue", getObjectVal().toString());
        }

        // ================================================================
        // Attribute-based ruleValues were stored in Set<String>
        // ================================================================
        switch (getFilterType()) {
            case CASE_EVENT_ATTRIBUTE:
            case CASE_CASE_ATTRIBUTE:
            case CASE_SECTION_ATTRIBUTE_COMBINATION:
            case EVENT_EVENT_ATTRIBUTE:
                Set<String> set = getStringSetValue();
                JSONArray jsonArray = new JSONArray();
                jsonArray.addAll(set);
                jsonRuleValue.put("objectvalue", jsonArray.toJSONString());
                break;
            default:
                break;
        }

        JSONObject jsonCustomAttributes = new JSONObject();
        if (getCustomAttributes().size() > 0) {
            for (String s : getCustomAttributes().keySet()) {
                jsonCustomAttributes.put(s, getCustomAttributes().get(s));
            }
        }
        jsonRuleValue.put("customAttributes", jsonCustomAttributes);

        return jsonRuleValue;
    }
}
