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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RuleValue implements Comparable<RuleValue>, Serializable {

    private FilterType filterType;
    private OperationType operationType;
    private String key;
    private String stringVal = "";
    private long longVal = 0;
    private double doubleVal = 0;
    private int intVal = 0;
    private Object objectVal;

    private final Map<String, String> customAttributes = new UnifiedMap<>();

    public RuleValue(FilterType filterType, OperationType operationType, String key, Object objectVal) {
        this.filterType = filterType;
        this.operationType = operationType;
        this.key = key;
        this.objectVal = objectVal;
        this.stringVal = objectVal.toString();
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
        this.stringVal = intVal + "";
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

    public Object getObjectVal() {
        /** to be compliant to the old methods **/
        if (objectVal == null && stringVal != null) {
            return new HashSet<>(Arrays.asList(stringVal));
        }
        return objectVal;
    }

    public RuleValue clone() {

        Map<String, String> customAttrCopy = null;

        if (customAttributes != null) {
            if (customAttributes.size() > 0) {
                customAttrCopy = new HashMap<>();
                for (String key : customAttributes.keySet()) {
                    customAttrCopy.put(key, customAttributes.get(key));
                }
            }
        }

        RuleValue rv = null;

        if (objectVal != null) rv = new RuleValue(filterType, operationType, key, objectVal);
        else if (longVal != 0) rv = new RuleValue(filterType, operationType, key, longVal );
        else if (doubleVal != 0) rv = new RuleValue(filterType, operationType, key, doubleVal );
        else if (intVal != 0) rv = new RuleValue(filterType, operationType, key, intVal );
        else rv = new  RuleValue(filterType, operationType, key, stringVal );

        if (customAttrCopy != null) rv.setCustomAttributes(customAttrCopy);

        return rv;
    }


    @Override
    public int compareTo(RuleValue o) {
        if (this.intVal != 0 && o.getIntValue() != 0) {
            if (this.intVal > o.getIntValue()) return 1;
            else if (this.intVal < o.getIntValue()) return -1;
            else return 0;
        } else if (this.doubleVal != 0 && o.getDoubleValue() != 0) {
            if (this.doubleVal > o.getDoubleValue()) return 1;
            else if (this.doubleVal < o.getDoubleValue()) return -1;
            else return 0;
        } else if (this.longVal != 0 && o.getLongValue() != 0) {
            if (this.longVal > o.getLongValue()) return 1;
            else if (this.longVal < o.getLongValue()) return -1;
            else return 0;
        } else {
            return this.stringVal.compareTo(o.getStringValue());
        }
    }

    public void setObjectVal(Object obj) {
        this.objectVal = obj;
        if (filterType == FilterType.CASE_SECTION_ATTRIBUTE_COMBINATION) {
            this.stringVal = ((Set<String>) obj).iterator().next();
        }
    }
}
