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
package org.apromore.plugin.portal.processdiscoverer.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apromore.logman.Constants;
import org.eclipse.collections.api.bimap.BiMap;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.impl.factory.BiMaps;


/**
 * Attribute map (key->value pairs) can have arbitrary keys and they can be of any sort order.
 * For example, the keys can be "concept:name" or "org:role", "concept:name" should be standardized
 * as "Activity" and "org:role" should be standardized as "Role". "org:role" can occur before "concept:name"
 * or after or they can be missing.
 * 
 * This class defines rules to sort attributes and standardize attribute keys for use on the UI.
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributesStandardizer {
    private List<String> orderedAttributeKeys = new ArrayList<>();
    private BiMap<String, String> attributeKeyMapping = BiMaps.mutable.empty(); // old key label => new key label
    private Set<String> excludedAttributeKeys = new HashSet<>();
    
    public static AttributesStandardizer SIMPLE = createSimpleStandardizer();
    
    private static AttributesStandardizer createSimpleStandardizer() {
        AttributesStandardizer standard = new AttributesStandardizer();
        
        MutableBiMap<String,String> attKeyMapping = BiMaps.mutable.empty();
        attKeyMapping.put(Constants.ATT_KEY_CONCEPT_NAME, "Activity");
        attKeyMapping.put(Constants.ATT_KEY_RESOURCE, "Resource");
        attKeyMapping.put(Constants.ATT_KEY_ROLE, "Role");
        attKeyMapping.put(Constants.ATT_KEY_GROUP, "Group");
        attKeyMapping.put(Constants.ATT_KEY_TIMESTAMP, "Timestamp");
        attKeyMapping.put(Constants.ATT_KEY_START_TIME, "Start timestamp");
        attKeyMapping.put(Constants.ATT_KEY_END_TIME, "End timestamp");
        standard.setAttributeKeyMapping(attKeyMapping);
        
        standard.setAttributeOrdering(Arrays.asList(
                "Activity",
                "Resource",
                "Role",
                "Group",
                "Timestamp",
                "Start timestamp",
                "End timestamp"
                ));
        
       standard.setExcludedAttributeKeys(new HashSet<>(Arrays.asList("lifecycle:transition")));
       
       return standard;
    }
    
    /**
     * @param attributeKeyMap: key is attribute key, value is the standard attribute key
     */
    public void setAttributeKeyMapping(BiMap<String, String> attributeKeyMapping) {
        if (attributeKeyMapping == null) return;
        this.attributeKeyMapping = attributeKeyMapping;
    }
    
    /**
     * @param orderedAttributeKeys: list of standardized attribute keys in expected order
     */
    public void setAttributeOrdering(List<String> orderedAttributeKeys) {
        if (orderedAttributeKeys == null) return;
        this.orderedAttributeKeys = orderedAttributeKeys;
    }
    
    public void setExcludedAttributeKeys(Set<String> excludedAttKeys) {
        if (excludedAttKeys == null) return;
        this.excludedAttributeKeys = excludedAttKeys;
    }
    
    /**
     * Get attribute map where attributes have the right order and standard names applied
     * Attributes without a standard name keep their existing key names
     * Attributes without a specified order number are arranged in the natural order of their keys.
     */
    public SortedMap<String,String> standardizedAttributeMap(Map<String, String> attributeMap) {
        // Copy to a new attribute map with new attribute key -> value
        // Those without a standardized key are taken as-is
        // Duplicate keys are copied to a new key with a "(2)" as the added suffix
        Map<String, String> standardizedAttributeMap = new HashMap<>();
        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
            if (excludedAttributeKeys.contains(entry.getKey())) continue;
            String replaceKey = attributeKeyMapping.containsValue(entry.getKey()) &&
                                    attributeMap.containsKey(attributeKeyMapping.inverse().get(entry.getKey()))
                                    ? entry.getKey()+"(2)"
                                    : entry.getKey();
            standardizedAttributeMap.put(attributeKeyMapping.containsKey(replaceKey) ? attributeKeyMapping.get(replaceKey) : replaceKey, entry.getValue());
        }
        
        // Create a mapping from attribute key to its right order number starting from 0.
        // This mapping is for use as a comparator
        Map<String, Integer> comparatorKeyMap = new HashMap<>(); //attribute key => ordinal number
        int orderNumber = 0;
        for (String key : this.orderedAttributeKeys) {
            if (standardizedAttributeMap.containsKey(key)) {
                comparatorKeyMap.put(key, orderNumber);
                orderNumber++;
            }
        }
        
        // Get other keys not in the prioritized order into the comparator, they are taken by the natural ordering of the key labels
        List<String> orderedKeys = new ArrayList<>(new TreeSet<>(standardizedAttributeMap.keySet()));
        for (String key : orderedKeys) {
            if (!comparatorKeyMap.containsKey(key)) {
                comparatorKeyMap.put(key, orderNumber);
                orderNumber++;
            }
        }
        
        // Created a sorted map using the comparator map above
        SortedMap<String, String> sortedAttributeMap = new TreeMap<String,String> (new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(comparatorKeyMap.get(o1), comparatorKeyMap.get(o2));
            }
        });
        sortedAttributeMap.putAll(standardizedAttributeMap);
        
        return sortedAttributeMap;
    }
    
    
}
