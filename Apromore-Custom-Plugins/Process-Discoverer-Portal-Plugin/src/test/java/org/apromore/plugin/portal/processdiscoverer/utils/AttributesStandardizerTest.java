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
package org.apromore.plugin.portal.processdiscoverer.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apromore.logman.Constants;
import org.junit.Assert;
import org.junit.Test;

public class AttributesStandardizerTest {
    
    @Test
    public void testStandardizeAttributeMap_standardAttributes() {
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
        
        Assert.assertEquals("Activity", keyIterator.next());
        Assert.assertEquals("Activity 1", valueIterator.next());
        
        Assert.assertEquals("Resource", keyIterator.next());
        Assert.assertEquals("Resource 1", valueIterator.next());
        
        Assert.assertEquals("Role", keyIterator.next());
        Assert.assertEquals("Role 1", valueIterator.next());
        
        Assert.assertEquals("Group", keyIterator.next());
        Assert.assertEquals("Group 1", valueIterator.next());
        
        Assert.assertEquals("Start timestamp", keyIterator.next());
        Assert.assertEquals("Start timestamp 1", valueIterator.next());
        
        Assert.assertEquals("End timestamp", keyIterator.next());
        Assert.assertEquals("End timestamp 1", valueIterator.next());
        
        Assert.assertEquals("CustomKey1", keyIterator.next());
        Assert.assertEquals("Custom Value1", valueIterator.next());
        
        Assert.assertEquals("CustomKey2", keyIterator.next());
        Assert.assertEquals("Custom Value2", valueIterator.next());
    }

    @Test
    public void testStandardizeAttributeMap_emptyMap() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(new HashMap<>());
        Assert.assertEquals(new HashMap<>(), res);
    }
    
    @Test
    public void testStandardizeAttributeMap_nonStandardAttributes() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("CustomKey2", "Custom Value 2"),
                Map.entry("CustomKey1", "Custom Value 1")
                ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        Assert.assertEquals(2, res.size());
        Assert.assertEquals("CustomKey1", keyIterator.next());
        Assert.assertEquals("Custom Value 1", valueIterator.next());
        Assert.assertEquals("CustomKey2", keyIterator.next());
        Assert.assertEquals("Custom Value 2", valueIterator.next());
    }
    
    @Test
    public void testStandardizeAttributeMap_singleStandardAttribute() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry(Constants.ATT_KEY_CONCEPT_NAME, "Activity 1")));
        Assert.assertEquals(1, res.size());
        Assert.assertEquals("Activity", res.keySet().iterator().next());
        Assert.assertEquals("Activity 1", res.values().iterator().next());
    }
    
    @Test
    public void testStandardizeAttributeMap_twoStandardAttributes() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry(Constants.ATT_KEY_RESOURCE, "Resource 1"),
                Map.entry(Constants.ATT_KEY_CONCEPT_NAME, "Activity 1")
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        Assert.assertEquals(2, res.size());
        Assert.assertEquals("Activity", keyIterator.next());
        Assert.assertEquals("Activity 1", valueIterator.next());
        Assert.assertEquals("Resource", keyIterator.next());
        Assert.assertEquals("Resource 1", valueIterator.next());
    }
    
    @Test
    public void testStandardizeAttributeMap_oneStandardOneNonStandard() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("Random Key", "Random Value"),
                Map.entry(Constants.ATT_KEY_RESOURCE, "Resource 1")
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        Assert.assertEquals(2, res.size());
        Assert.assertEquals("Resource", keyIterator.next());
        Assert.assertEquals("Resource 1", valueIterator.next());
        Assert.assertEquals("Random Key", keyIterator.next());
        Assert.assertEquals("Random Value", valueIterator.next());
    }
    
    @Test
    public void testStandardizeAttributeMap_duplicateAttributeKeys() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("Resource", "Resource Value 2"),
                Map.entry(Constants.ATT_KEY_RESOURCE, "Resource Value 1")
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        Assert.assertEquals(2, res.size());
        Assert.assertEquals("Resource", keyIterator.next());
        Assert.assertEquals("Resource Value 1", valueIterator.next());
        Assert.assertEquals("Resource(2)", keyIterator.next());
        Assert.assertEquals("Resource Value 2", valueIterator.next());
    }
    
    @Test
    public void testStandardizeAttributeMap_sameKeywordButNonDuplicateAttributeKey() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("Activity", "Activity Value 1"), // use 'Activity' keyword
                Map.entry(Constants.ATT_KEY_RESOURCE, "Resource Value 1") // but actually no duplicate
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        Assert.assertEquals(2, res.size());
        Assert.assertEquals("Activity", keyIterator.next());
        Assert.assertEquals("Activity Value 1", valueIterator.next());
        Assert.assertEquals("Resource", keyIterator.next());
        Assert.assertEquals("Resource Value 1", valueIterator.next());
    }
    
    @Test
    public void testStandardizeAttributeMap_excludedAttributeKeys() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry("Resource", "Resource Value 1"),
                Map.entry(Constants.ATT_KEY_LIFECYCLE_TRANSITION, "complete")
        ));
        Iterator<String> keyIterator = res.keySet().iterator();
        Iterator<String> valueIterator = res.values().iterator();
        Assert.assertEquals(1, res.size());
        Assert.assertEquals("Resource", keyIterator.next());
        Assert.assertEquals("Resource Value 1", valueIterator.next());
    }
    
    @Test
    public void testStandardizeAttributeMap_excludedAttributeKeysTobeEmpty() {
        AttributesStandardizer standardizer = AttributesStandardizer.SIMPLE;
        Map<String,String> res = standardizer.standardizedAttributeMap(Map.ofEntries(
                Map.entry(Constants.ATT_KEY_LIFECYCLE_TRANSITION, "complete")
        ));
        Assert.assertEquals(new HashMap<>(), res);
    }
    
}
