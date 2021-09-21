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
package org.apromore.apmlog.filter.rules;

import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleValue implements Comparable<RuleValue>, Serializable {

    private final FilterType filterType;
    private final OperationType operationType;
    private final String key;
    private String stringVal = "";
    private long longVal = 0;
    private double doubleVal = 0;
    private int intVal = 0;
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

    public RuleValue(FilterType filterType, OperationType operationType, String key, long longVal) {
        this.filterType = filterType;
        this.operationType = operationType;
        this.key = key;
        this.longVal = longVal;
        this.intVal = Long.valueOf(longVal).intValue();
        this.stringVal = longVal + "";
    }

    public RuleValue(FilterType filterType, OperationType operationType, String key, double doubleVal) {
        this.filterType = filterType;
        this.operationType = operationType;
        this.key = key;
        this.doubleVal = doubleVal;
        this.stringVal = doubleVal + "";
    }

    public RuleValue(FilterType filterType, OperationType operationType, String key, int intVal) {
        this.filterType = filterType;
        this.operationType = operationType;
        this.key = key;
        this.intVal = intVal;
        this.longVal = intVal;
        this.stringVal = intVal + "";
    }

    private void setStringSetValue(Set<String> strings) {
        stringSetValue.clear();
        stringSetValue.addAll(strings);
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
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

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }

    public void setLongVal(long longVal) {
        this.longVal = longVal;
    }

    public void setDoubleVal(double doubleVal) {
        this.doubleVal = doubleVal;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public String getKey() {
        return this.key;
    }

    public String getStringValue() {
        return this.stringVal;
    }

    public long getLongValue() {
        if (longVal == 0 && doubleVal > 0) return Double.valueOf(doubleVal).longValue();
        return longVal;
    }

    public double getDoubleValue() {
        if (doubleVal == 0 && longVal > 0) return longVal;
        return doubleVal;
    }

    public int getIntValue() {
        return intVal;
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

    public BitSet getBitSetValue() {
        return bitSetValue;
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

        Map<String, String> customAttrCopy = null;

        if (customAttributes.size() > 0) {
            customAttrCopy = new HashMap<>();
            for (String key : customAttributes.keySet()) {
                customAttrCopy.put(key, customAttributes.get(key));
            }
        }

        RuleValue rv = null;

        if (longVal != 0) rv = new RuleValue(filterType, operationType, key, longVal );
        else if (doubleVal != 0) rv = new RuleValue(filterType, operationType, key, doubleVal );
        else if (intVal != 0) rv = new RuleValue(filterType, operationType, key, intVal );
        else if (!stringSetValue.isEmpty()) rv = new RuleValue(filterType, operationType, key, stringSetValue);
        else if (bitSetValue != null) rv = new RuleValue(filterType, operationType, key, bitSetValue);
        else if (objectVal != null) rv = new RuleValue(filterType, operationType, key, objectVal);
        else rv = new  RuleValue(filterType, operationType, key, stringVal );

        if (customAttrCopy != null) rv.setCustomAttributes(customAttrCopy);

        return rv;
    }


    @Override
    public int compareTo(@NotNull RuleValue ruleValue) {
        if (ruleValue == null)
            return 0;

        if (this.intVal != 0 && ruleValue.getIntValue() != 0) {
            return Integer.compare(this.intVal, ruleValue.getIntValue());
        } else if (this.doubleVal != 0 && ruleValue.getDoubleValue() != 0) {
            return Double.compare(this.doubleVal, ruleValue.getDoubleValue());
        } else if (this.longVal != 0 && ruleValue.getLongValue() != 0) {
            return Long.compare(this.longVal, ruleValue.getLongValue());
        } else {
            return this.stringVal.compareTo(ruleValue.getStringValue());
        }
    }

}
