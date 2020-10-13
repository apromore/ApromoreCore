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
package org.apromore.apmlog.stats;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Set;

public class AAttribute {
    private String key;
    private UnifiedMap<String, AAttributeValue> attributeValues = new UnifiedMap<>();

    public AAttribute(String key) {
        this.key = key;
    }

    public AAttribute(String key, String stringValue) {
        this.key = key;
        attributeValues = new UnifiedMap<>();
        AAttributeValue aAttributeValue = new AAttributeValue(stringValue);
        attributeValues.put(stringValue, aAttributeValue);
    }

    public AAttribute(String key, UnifiedMap<String, AAttributeValue> attributeValues) {
        this.key = key;
        this.attributeValues = attributeValues;
    }

    public boolean add(AAttributeValue aAttributeValue) {
        String val = aAttributeValue.getStringValue();
        if (!attributeValues.containsKey(val)) {
            attributeValues.put(val, aAttributeValue);
            return true;
        }
        return false;
    }

    public String getKey() {
        return key;
    }

    public Set<String> getValues() {
        return attributeValues.keySet();
    }

    public boolean contains(String value) {
        return this.attributeValues.containsKey(value);
    }

    public AAttributeValue get(String value) {
        return this.attributeValues.get(value);
    }

    public UnifiedMap<String, AAttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public AAttribute clone() {
        AAttribute aat = new AAttribute(key);
        if (attributeValues.size() > 0) {
            UnifiedMap<String, AAttributeValue> attributeValuesClone = new UnifiedMap<>();

            for (String val : attributeValues.keySet()) {
                attributeValuesClone.put(val, attributeValues.get(val).clone());
            }
        }
        return aat;
    }
}
