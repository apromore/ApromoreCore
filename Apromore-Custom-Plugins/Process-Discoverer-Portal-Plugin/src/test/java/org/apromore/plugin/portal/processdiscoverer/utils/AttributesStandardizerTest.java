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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apromore.logman.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AttributesStandardizerTest {
    
    @Test
    void testStandardizeAttributeMap_standardAttributes() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry(Constants.ATT_KEY_RESOURCE, "Resource 1"),
                Map.entry(Constants.ATT_KEY_CONCEPT_NAME, "Activity 1"),
                Map.entry(Constants.ATT_KEY_ROLE, "Role 1"),
                Map.entry(Constants.ATT_KEY_END_TIME, "End timestamp 1"),
                Map.entry(Constants.ATT_KEY_START_TIME, "Start timestamp 1"),
                Map.entry(Constants.ATT_KEY_GROUP, "Group 1"),
                Map.entry("CustomKey2", "Custom Value2"),
                Map.entry("CustomKey1", "Custom Value1"),
                Map.entry(Constants.ATT_KEY_LIFECYCLE_TRANSITION, "complete")
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        
        assertEquals("Activity", keyIterator.next());
        assertEquals("Activity 1", valueIterator.next());
        
        assertEquals("Resource", keyIterator.next());
        assertEquals("Resource 1", valueIterator.next());
        
        assertEquals("Role", keyIterator.next());
        assertEquals("Role 1", valueIterator.next());
        
        assertEquals("Group", keyIterator.next());
        assertEquals("Group 1", valueIterator.next());
        
        assertEquals("Start timestamp", keyIterator.next());
        assertEquals("Start timestamp 1", valueIterator.next());
        
        assertEquals("End timestamp", keyIterator.next());
        assertEquals("End timestamp 1", valueIterator.next());
        
        assertEquals("CustomKey1", keyIterator.next());
        assertEquals("Custom Value1", valueIterator.next());
        
        assertEquals("CustomKey2", keyIterator.next());
        assertEquals("Custom Value2", valueIterator.next());
    }

    @Test
    void testStandardizeAttributeMap_emptyMap() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(new HashMap<>());
        assertEquals(new HashMap<>(), res);
    }
    
    @Test
    void testStandardizeAttributeMap_nonStandardAttributes() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("CustomKey2", "Custom Value 2"),
                Map.entry("CustomKey1", "Custom Value 1")
                ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        assertEquals(2, res.size());
        assertEquals("CustomKey1", keyIterator.next());
        assertEquals("Custom Value 1", valueIterator.next());
        assertEquals("CustomKey2", keyIterator.next());
        assertEquals("Custom Value 2", valueIterator.next());
    }
    
    @Test
    void testStandardizeAttributeMap_singleStandardAttribute() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry(Constants.ATT_KEY_CONCEPT_NAME, "Activity 1")));
        assertEquals(1, res.size());
        assertEquals("Activity", res.keySet().iterator().next());
        assertEquals("Activity 1", res.values().iterator().next());
    }
    
    @Test
    void testStandardizeAttributeMap_twoStandardAttributes() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry(Constants.ATT_KEY_RESOURCE, "Resource 1"),
                Map.entry(Constants.ATT_KEY_CONCEPT_NAME, "Activity 1")
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        assertEquals(2, res.size());
        assertEquals("Activity", keyIterator.next());
        assertEquals("Activity 1", valueIterator.next());
        assertEquals("Resource", keyIterator.next());
        assertEquals("Resource 1", valueIterator.next());
    }
    
    @Test
    void testStandardizeAttributeMap_oneStandardOneNonStandard() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("Random Key", "Random Value"),
                Map.entry(Constants.ATT_KEY_RESOURCE, "Resource 1")
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        assertEquals(2, res.size());
        assertEquals("Resource", keyIterator.next());
        assertEquals("Resource 1", valueIterator.next());
        assertEquals("Random Key", keyIterator.next());
        assertEquals("Random Value", valueIterator.next());
    }
    
    @Test
    void testStandardizeAttributeMap_duplicateAttributeKeys() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("Resource", "Resource Value 2"),
                Map.entry(Constants.ATT_KEY_RESOURCE, "Resource Value 1")
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        assertEquals(2, res.size());
        assertEquals("Resource", keyIterator.next());
        assertEquals("Resource Value 1", valueIterator.next());
        assertEquals("Resource(2)", keyIterator.next());
        assertEquals("Resource Value 2", valueIterator.next());
    }
    
    @Test
    void testStandardizeAttributeMap_sameKeywordButNonDuplicateAttributeKey() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("Activity", "Activity Value 1"), // use 'Activity' keyword
                Map.entry(Constants.ATT_KEY_RESOURCE, "Resource Value 1") // but actually no duplicate
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        assertEquals(2, res.size());
        assertEquals("Activity", keyIterator.next());
        assertEquals("Activity Value 1", valueIterator.next());
        assertEquals("Resource", keyIterator.next());
        assertEquals("Resource Value 1", valueIterator.next());
    }
    
    @Test
    void testStandardizeAttributeMap_excludedAttributeKeys() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("Resource", "Resource Value 1"),
                Map.entry(Constants.ATT_KEY_LIFECYCLE_TRANSITION, "complete")
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        assertEquals(1, res.size());
        assertEquals("Resource", keyIterator.next());
        assertEquals("Resource Value 1", valueIterator.next());
    }
    
    @Test
    void testStandardizeAttributeMap_excludedAttributeKeysTobeEmpty() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry(Constants.ATT_KEY_LIFECYCLE_TRANSITION, "complete")
        ));
        assertEquals(new HashMap<>(), res);
    }
    
}
