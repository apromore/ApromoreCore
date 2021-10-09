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

package org.apromore.logman.attribute;

import org.apromore.logman.ALog;
import org.apromore.logman.Constants;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.factory.Lists;
import org.joda.time.DateTime;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AttributeStoreTest {
	public static long datetime1 = DateTime.now().getMillis();
	
	private AttributeStore createStoreFromEmptyLog() {
		XFactory factory = new XFactoryNaiveImpl();
		return new AttributeStore(new ALog(factory.createLog()));
	}
	
	private AttributeStore createStoreFromLogWithEmptyAttributes() {
		XFactory factory = new XFactoryNaiveImpl();
		XLog log = factory.createLog();
		log.add(factory.createTrace());
		log.get(0).add(factory.createEvent());
		return new AttributeStore(new ALog(log));
	}
	
	private AttributeStore createStoreFromLogOneTraceOneEventOneAttribute() {
		XFactory factory = new XFactoryNaiveImpl();
		XLog log = factory.createLog();
		log.add(factory.createTrace());
		log.get(0).add(factory.createEvent());
		log.get(0).get(0).getAttributes().put("literal1", factory.createAttributeLiteral("literal1", "Event1", null));
		return new AttributeStore(new ALog(log));
	}
	
	private XAttributeMap createSampleAttributeMap(int index) {
		XFactory factory = new XFactoryNaiveImpl();
		XAttributeMap attMap = factory.createAttributeMap();
		attMap.put("literal", factory.createAttributeLiteral("literal", "literalvalue" + index, null));
		attMap.put("discrete", factory.createAttributeDiscrete("discrete", 100, null));
		attMap.put("continuous", factory.createAttributeContinuous("continuous", 100.1, null));
		attMap.put("boolean", factory.createAttributeBoolean("boolean", true, null));
		attMap.put("timestamp", factory.createAttributeTimestamp("timestamp", datetime1, null));
		return attMap;
	}
	
	private XAttributeMap createStandardAttributeMap(int index) {
		XFactory factory = new XFactoryNaiveImpl();
		XAttributeMap attMap = factory.createAttributeMap();
		attMap.put(Constants.ATT_KEY_CONCEPT_NAME, factory.createAttributeLiteral(Constants.ATT_KEY_CONCEPT_NAME, "conceptname" + index, null));
		attMap.put(Constants.ATT_KEY_RESOURCE, factory.createAttributeLiteral(Constants.ATT_KEY_RESOURCE, "resource" + index, null));
		attMap.put(Constants.ATT_KEY_GROUP, factory.createAttributeLiteral(Constants.ATT_KEY_GROUP, "group" + index, null));
		attMap.put(Constants.ATT_KEY_ROLE, factory.createAttributeLiteral(Constants.ATT_KEY_ROLE, "role" + index, null));
		attMap.put(Constants.ATT_KEY_LIFECYCLE_TRANSITION, factory.createAttributeLiteral(Constants.ATT_KEY_LIFECYCLE_TRANSITION, "complete", null));
		attMap.put(Constants.ATT_KEY_TIMESTAMP, factory.createAttributeTimestamp(Constants.ATT_KEY_TIMESTAMP, datetime1, null));
		
		return attMap;
	}
	
	private AttributeStore createStoreFromLogWithTwoTracesTwoEventsFullSampleAttributes() {
		XFactory factory = new XFactoryNaiveImpl();
		XLog log = factory.createLog(this.createSampleAttributeMap(1));
		
		log.add(factory.createTrace(this.createSampleAttributeMap(1)));
		log.add(factory.createTrace(this.createSampleAttributeMap(2)));
		
		log.get(0).add(factory.createEvent(this.createSampleAttributeMap(1)));
		log.get(0).add(factory.createEvent(this.createSampleAttributeMap(2)));
		
		log.get(1).add(factory.createEvent(this.createSampleAttributeMap(1)));
		log.get(1).add(factory.createEvent(this.createSampleAttributeMap(2)));
		
//		log.get(1).get(0).getAttributes().put("discrete", factory.createAttributeDiscrete("discrete", 300, null));
		
		return new AttributeStore(new ALog(log));
	}
	
	private AttributeStore createStoreFromLogWithOneTracesTwoEventsStandardAttributes() {
		XFactory factory = new XFactoryNaiveImpl();
		XLog log = factory.createLog();
		log.getAttributes().put(Constants.ATT_KEY_CONCEPT_NAME, factory.createAttributeLiteral(Constants.ATT_KEY_CONCEPT_NAME, "log1", null));
		
		log.add(factory.createTrace());
		log.get(0).getAttributes().put(Constants.ATT_KEY_CONCEPT_NAME, factory.createAttributeLiteral(Constants.ATT_KEY_CONCEPT_NAME, "trace1", null));
		
		log.get(0).add(factory.createEvent(this.createStandardAttributeMap(1)));
		log.get(0).add(factory.createEvent(this.createStandardAttributeMap(2)));
		
		return new AttributeStore(new ALog(log));
	}
	
	@Test
	public void testGetAttribute() {
		AttributeStore store1 = this.createStoreFromEmptyLog();
		Assert.assertEquals(null, store1.getAttribute(0));
		
		AttributeStore store2 = this.createStoreFromLogWithEmptyAttributes();
		Assert.assertEquals(null, store2.getAttribute(0));
		
		AttributeStore store3 = this.createStoreFromLogOneTraceOneEventOneAttribute();
		Assert.assertEquals(null, store3.getAttribute(100));
		Assert.assertEquals("literal1", store3.getAttribute(0).getKey());
		Assert.assertEquals(AttributeLevel.EVENT, store3.getAttribute(0).getLevel());
		Assert.assertEquals(AttributeType.LITERAL, store3.getAttribute(0).getType());
	}

	@Test
	public void testGetAttributeIndexes() {
		AttributeStore store1 = this.createStoreFromEmptyLog();
		Assert.assertArrayEquals(new int[] {}, store1.getAttributeIndexes());
		
		AttributeStore store2 = this.createStoreFromLogWithEmptyAttributes();
		Assert.assertArrayEquals(new int[] {}, store2.getAttributeIndexes());
		
		AttributeStore store3 = this.createStoreFromLogOneTraceOneEventOneAttribute();
		Assert.assertArrayEquals(new int[] {0}, store3.getAttributeIndexes());
		
		AttributeStore store4 = this.createStoreFromLogWithTwoTracesTwoEventsFullSampleAttributes();
		Assert.assertArrayEquals(new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14}, store4.getAttributeIndexes());
	}

	@Test
	public void testGetAttributeWithKeyAndLevel() {
		AttributeStore store1 = this.createStoreFromEmptyLog();
		Assert.assertEquals(null, store1.getAttribute("literal1", AttributeLevel.LOG));
		
		AttributeStore store2 = this.createStoreFromLogWithEmptyAttributes();
		Assert.assertEquals(null, store2.getAttribute("literal1", AttributeLevel.LOG));
		
		AttributeStore store3 = this.createStoreFromLogOneTraceOneEventOneAttribute();
		Assert.assertEquals(null, store3.getAttribute("literal1", AttributeLevel.LOG));
		Assert.assertEquals("literal1", store3.getAttribute("literal1", AttributeLevel.EVENT).getKey());
		Assert.assertEquals(AttributeLevel.EVENT, store3.getAttribute("literal1", AttributeLevel.EVENT).getLevel());
	}

	@Test
	public void testGetAttributeWithXAttribute() {
		XFactory factory = new XFactoryNaiveImpl();
		XEvent event = factory.createEvent();
		event.getAttributes().put("literal1", factory.createAttributeLiteral("literal1", "literalvalue1", null));
		
		AttributeStore store3 = this.createStoreFromLogOneTraceOneEventOneAttribute();
		Assert.assertEquals("literal1", store3.getAttribute("literal1", AttributeLevel.EVENT).getKey());
		Assert.assertEquals(AttributeLevel.EVENT, store3.getAttribute("literal1", AttributeLevel.EVENT).getLevel());
	}

	@Test
	public void testGetAttributeIndex() {
		AttributeStore store1 = this.createStoreFromLogOneTraceOneEventOneAttribute();
		
		AbstractAttribute att = store1.getAttribute("literal1", AttributeLevel.EVENT);
		Assert.assertEquals(0, store1.getAttributeIndex(att));
		
		AttributeStore store2 = this.createStoreFromLogWithEmptyAttributes();
		Assert.assertEquals(-1, store2.getAttributeIndex(att));
	}

	@Test
	public void testGetAttributeIndexWithKeyLevel() {
		AttributeStore store1 = this.createStoreFromLogOneTraceOneEventOneAttribute();
		
		Assert.assertEquals(0, store1.getAttributeIndex("literal1", AttributeLevel.EVENT));
		
		Assert.assertEquals(-1, store1.getAttributeIndex("literal2", AttributeLevel.EVENT));
		
		Assert.assertEquals(-1, store1.getAttributeIndex("literal1", AttributeLevel.TRACE));
		
		AttributeStore store2 = this.createStoreFromLogWithEmptyAttributes();
		Assert.assertEquals(-1, store2.getAttributeIndex("literal1", AttributeLevel.EVENT));	
	}

	@Test
	public void testGetAttributeIndexWithXAttribute() {
		XFactory factory = new XFactoryNaiveImpl();
		XEvent event = factory.createEvent();
		event.getAttributes().put("literal1", factory.createAttributeLiteral("literal1", "literalvalue1", null));
		
		AttributeStore store1 = this.createStoreFromLogOneTraceOneEventOneAttribute();
		Assert.assertEquals(0, store1.getAttributeIndex(event.getAttributes().get("literal1"), event));
	}

	@Test
	public void testGetAttributes() {
		AttributeStore store1 = this.createStoreFromEmptyLog();
		Assert.assertEquals(Lists.immutable.empty(), store1.getLogAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store1.getTraceAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store1.getEventAttributes());
		
		Assert.assertEquals(Lists.immutable.empty(), store1.getLiteralAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store1.getBooleanAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store1.getDiscreteAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store1.getContinuousAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store1.getTimestampAttributes());
		
		AttributeStore store2 = this.createStoreFromLogWithEmptyAttributes();
		Assert.assertEquals(Lists.immutable.empty(), store2.getLogAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store2.getTraceAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store2.getEventAttributes());
		
		Assert.assertEquals(Lists.immutable.empty(), store2.getLiteralAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store2.getBooleanAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store2.getDiscreteAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store2.getContinuousAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store2.getTimestampAttributes());		
		
		AttributeStore store3 = this.createStoreFromLogOneTraceOneEventOneAttribute();
		Assert.assertEquals(Lists.immutable.empty(), store3.getLogAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store3.getTraceAttributes());
		ImmutableList<AbstractAttribute> atts = store3.getEventAttributes();
		Assert.assertEquals(1, atts.size());
		Assert.assertEquals("literal1", atts.get(0).getKey());
		Assert.assertEquals(AttributeLevel.EVENT, atts.get(0).getLevel());
		Assert.assertEquals(AttributeType.LITERAL, atts.get(0).getType());
		
		Assert.assertEquals(1, store3.getLiteralAttributes().size());
		Assert.assertEquals(Lists.immutable.empty(), store3.getBooleanAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store3.getDiscreteAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store3.getContinuousAttributes());
		Assert.assertEquals(Lists.immutable.empty(), store3.getTimestampAttributes());		
		
		AttributeStore store4 = this.createStoreFromLogWithTwoTracesTwoEventsFullSampleAttributes();
		ImmutableList<AbstractAttribute> store4logatts = store4.getLogAttributes();
		Assert.assertEquals(5, store4logatts.size());
		Assert.assertEquals(AttributeLevel.LOG, store4logatts.get(0).getLevel());
		
		ImmutableList<AbstractAttribute> store4traceatts = store4.getTraceAttributes();
		Assert.assertEquals(5, store4traceatts.size());
		Assert.assertEquals(AttributeLevel.TRACE, store4traceatts.get(0).getLevel());
		
		ImmutableList<AbstractAttribute> store4eventatts = store4.getEventAttributes();
		Assert.assertEquals(5, store4eventatts.size());
		Assert.assertEquals(AttributeLevel.EVENT, store4eventatts.get(0).getLevel());
		
		Assert.assertEquals(3, store4.getLiteralAttributes().size());
		Assert.assertEquals(3, store4.getBooleanAttributes().size());
		Assert.assertEquals(3, store4.getDiscreteAttributes().size());
		Assert.assertEquals(3, store4.getContinuousAttributes().size());
		Assert.assertEquals(3, store4.getTimestampAttributes().size());	
	}
	
	@Test
	public void testGetAttributesWithValue() {
		AttributeStore store1 = this.createStoreFromLogWithTwoTracesTwoEventsFullSampleAttributes();
		
		ImmutableList<AbstractAttribute> literalAtts = store1.getLiteralAttributesWithValues(new String[]{"noexist"});
		Assert.assertEquals(0, literalAtts.size());
		literalAtts = store1.getLiteralAttributesWithValues(new String[]{"literalvalue1"});
		Assert.assertEquals(3, literalAtts.size());
		
		ImmutableList<AbstractAttribute> booleanAtts = store1.getBooleanAttributesWithValues(new Boolean[]{false});
		Assert.assertEquals(0, booleanAtts.size());
		booleanAtts = store1.getBooleanAttributesWithValues(new Boolean[]{true});
		Assert.assertEquals(3, booleanAtts.size());
		
		ImmutableList<AbstractAttribute> longAtts = store1.getDiscreteAttributesWithValues(new Long[]{200L});
		Assert.assertEquals(0, longAtts.size());
		longAtts = store1.getDiscreteAttributesWithValues(new Long[]{100L});
		Assert.assertEquals(3, longAtts.size());
		
		ImmutableList<AbstractAttribute> doubleAtts = store1.getContinuousAttributesWithValue(1000d);
		Assert.assertEquals(0, doubleAtts.size());
		doubleAtts = store1.getContinuousAttributesWithValue(100.1d);
		Assert.assertEquals(3, doubleAtts.size());
		
		ImmutableList<AbstractAttribute> tmpAtts = store1.getTimestampAttributesWithValue(DateTime.now().getMillis());
		Assert.assertEquals(0, tmpAtts.size());
		tmpAtts = store1.getTimestampAttributesWithValue(datetime1);
		Assert.assertEquals(3, tmpAtts.size());
	}


	@Test
	public void testGetStandardAttributes() {
		AttributeStore store1 = this.createStoreFromEmptyLog();
		Assert.assertEquals(null, store1.getStandardEventConceptName());
		Assert.assertEquals(null, store1.getStandardEventGroup());
		Assert.assertEquals(null, store1.getStandardEventLifecycleTransition());
		Assert.assertEquals(null, store1.getStandardEventResource());
		Assert.assertEquals(null, store1.getStandardEventRole());
		Assert.assertEquals(null, store1.getStandardEventTimestamp());
		Assert.assertEquals(null, store1.getStandardLogConceptName());
		Assert.assertEquals(null, store1.getStandardTraceConceptName());
		
		AttributeStore store2 = this.createStoreFromLogWithEmptyAttributes();
		Assert.assertEquals(null, store2.getStandardEventConceptName());
		Assert.assertEquals(null, store2.getStandardEventGroup());
		Assert.assertEquals(null, store2.getStandardEventLifecycleTransition());
		Assert.assertEquals(null, store2.getStandardEventResource());
		Assert.assertEquals(null, store2.getStandardEventRole());
		Assert.assertEquals(null, store2.getStandardEventTimestamp());
		Assert.assertEquals(null, store2.getStandardLogConceptName());
		Assert.assertEquals(null, store2.getStandardTraceConceptName());
		
		AttributeStore store3 = this.createStoreFromLogWithOneTracesTwoEventsStandardAttributes();
		
		Assert.assertEquals(Constants.ATT_KEY_CONCEPT_NAME, store3.getStandardEventConceptName().getKey());
		Assert.assertEquals(AttributeLevel.EVENT, store3.getStandardEventConceptName().getLevel());
		Assert.assertEquals(AttributeType.LITERAL, store3.getStandardEventConceptName().getType());
		
		Assert.assertEquals(Constants.ATT_KEY_GROUP, store3.getStandardEventGroup().getKey());
		Assert.assertEquals(AttributeLevel.EVENT, store3.getStandardEventGroup().getLevel());
		Assert.assertEquals(AttributeType.LITERAL, store3.getStandardEventGroup().getType());
		
		Assert.assertEquals(Constants.ATT_KEY_RESOURCE, store3.getStandardEventResource().getKey());
		Assert.assertEquals(AttributeLevel.EVENT, store3.getStandardEventResource().getLevel());
		Assert.assertEquals(AttributeType.LITERAL, store3.getStandardEventResource().getType());
		
		Assert.assertEquals(Constants.ATT_KEY_ROLE, store3.getStandardEventRole().getKey());
		Assert.assertEquals(AttributeLevel.EVENT, store3.getStandardEventRole().getLevel());
		Assert.assertEquals(AttributeType.LITERAL, store3.getStandardEventRole().getType());
		
		Assert.assertEquals(Constants.ATT_KEY_LIFECYCLE_TRANSITION, store3.getStandardEventLifecycleTransition().getKey());
		Assert.assertEquals(AttributeLevel.EVENT, store3.getStandardEventLifecycleTransition().getLevel());
		Assert.assertEquals(AttributeType.LITERAL, store3.getStandardEventLifecycleTransition().getType());
		
		Assert.assertEquals(Constants.ATT_KEY_TIMESTAMP, store3.getStandardEventTimestamp().getKey());
		Assert.assertEquals(AttributeLevel.EVENT, store3.getStandardEventTimestamp().getLevel());
		Assert.assertEquals(AttributeType.TIMESTAMP, store3.getStandardEventTimestamp().getType());
		
		Assert.assertEquals(Constants.ATT_KEY_CONCEPT_NAME, store3.getStandardLogConceptName().getKey());
		Assert.assertEquals(AttributeLevel.LOG, store3.getStandardLogConceptName().getLevel());
		Assert.assertEquals(AttributeType.LITERAL, store3.getStandardLogConceptName().getType());
		
		Assert.assertEquals(Constants.ATT_KEY_CONCEPT_NAME, store3.getStandardTraceConceptName().getKey());
		Assert.assertEquals(AttributeLevel.TRACE, store3.getStandardTraceConceptName().getLevel());
		Assert.assertEquals(AttributeType.LITERAL, store3.getStandardTraceConceptName().getType());
		
		Assert.assertEquals(7, store3.getLiteralAttributes().size());
		Assert.assertEquals(0, store3.getBooleanAttributes().size());
		Assert.assertEquals(0, store3.getDiscreteAttributes().size());
		Assert.assertEquals(0, store3.getContinuousAttributes().size());
		Assert.assertEquals(1, store3.getTimestampAttributes().size());	
	}

	@Test
	public void testGetPerspectiveEventAttributes() {
		// Empty log
		AttributeStore store1 = this.createStoreFromEmptyLog();
		assertEquals(0, store1.getPerspectiveEventAttributes(100, Arrays.asList(new String[] {"concept:name"})).size());

		AttributeStore store2 = this.createStoreFromLogWithOneTracesTwoEventsStandardAttributes();
		assertEquals(0, store2.getPerspectiveEventAttributes(100, Arrays.asList(new String[] {"resource"})).size());

		// Single valid perspective
		ListIterable<AbstractAttribute> atts1 = store2.getPerspectiveEventAttributes(100, Arrays.asList(new String[] {"concept:name"}));
		assertEquals(1, atts1.size());
		assertEquals("concept:name", atts1.get(0).getKey());
		assertEquals(AttributeLevel.EVENT, atts1.get(0).getLevel());
		assertTrue(atts1.get(0) instanceof IndexableAttribute);
		assertEquals(Lists.immutable.of("conceptname1", "conceptname2"), ((IndexableAttribute)atts1.get(0)).getValues());

		// Two valid perspectives
		ListIterable<AbstractAttribute> atts2 = store2.getPerspectiveEventAttributes(100,
													Arrays.asList(new String[] {"concept:name", "org:resource"}));
		atts2 = atts2.toSortedListBy(AbstractAttribute::getKey);
		assertEquals(2, atts2.size());
		assertEquals("concept:name", atts2.get(0).getKey());
		assertEquals("org:resource", atts2.get(1).getKey());
		assertEquals(AttributeLevel.EVENT, atts2.get(1).getLevel());
		assertTrue(atts2.get(1) instanceof IndexableAttribute);
		assertEquals(Lists.immutable.of("resource1", "resource2"), ((IndexableAttribute)atts2.get(1)).getValues());

		// Max number of values
		ListIterable<AbstractAttribute> atts3 = store2.getPerspectiveEventAttributes(1,
													Arrays.asList(new String[] {"concept:name", "org:resource"}));
		assertEquals(0, atts3.size());
	}

}
