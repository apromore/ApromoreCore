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

package org.apromore.logman.attribute;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.deckfour.xes.model.XAttributeDiscrete;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DiscreteAttributeTest extends AttributeTest {
	
	@Override
	protected DiscreteAttribute newEmptyAttribute(String key, AttributeLevel level) {
		DiscreteAttribute att = new DiscreteAttribute(key, level);
		return att; 
	}
	
	@Override
	protected DiscreteAttribute newWithInvalidKeyAttribute(String key, AttributeLevel level) {
		DiscreteAttribute att = new DiscreteAttribute(key, level);
		XAttributeDiscrete xatt = xAttFactory.createAttributeDiscrete("invalid_key", 100, null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected DiscreteAttribute newWithOneValue(String key, AttributeLevel level) {
		DiscreteAttribute att = new DiscreteAttribute(key, level);
		XAttributeDiscrete xatt = xAttFactory.createAttributeDiscrete(key, 100, null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected DiscreteAttribute newWithTwoValues(String key, AttributeLevel level) {
		DiscreteAttribute att = new DiscreteAttribute(key, level);
		att.registerXAttribute(xAttFactory.createAttributeDiscrete(key, 100, null));
		att.registerXAttribute(xAttFactory.createAttributeDiscrete(key, 200, null));
		return att; 
	}

	@Test
	void testGetValueIndex() {
		DiscreteAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals(0, att.getValueIndex(100));
		assertEquals(1, att.getValueIndex(200));
		
		DiscreteAttribute att2 = this.newWithInvalidKeyAttribute("concept:name", AttributeLevel.EVENT);
		Object result2 = att2.getValueIndex(100);
		assertEquals(-1, result2);
		
		DiscreteAttribute att3 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Object result3 = att3.getValueIndex(100);
		assertEquals(-1, result3);
	}

	@Test
	void testGetValueSize() {
		DiscreteAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals(2, att.getValueSize());
		
		DiscreteAttribute att2 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		assertEquals(0, att2.getValueSize());
	}

	@Test
	void testRegisterXAttributeWithInvalidType() {
		DiscreteAttribute att = new DiscreteAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeContinuous("concept:name", 100, null));
		assertEquals(-1, result);
	}
	
	@Test
	void testRegisterXAttributeWithInvalidKey() {
		DiscreteAttribute att = new DiscreteAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeDiscrete("invalid_key", 100, null));
		assertEquals(-1, result);
	}
	
	@Test
	void testRegisterValidXAttribute() {
		DiscreteAttribute att = new DiscreteAttribute("concept:name", AttributeLevel.EVENT);
		XAttributeDiscrete xatt = xAttFactory.createAttributeDiscrete("concept:name", 100, null);
		int result = att.registerXAttribute(xatt);
		assertEquals(0,  result);
	}

	@Test
	void testGetValues() {
		DiscreteAttribute att = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		assertEquals((long)100, att.getValue(0));
		
		DiscreteAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals((long)100, att2.getValue(0));
		assertEquals((long)200, att2.getValue(1));
	}

	@Test
	void testGetValue() {
		DiscreteAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		assertEquals((long)100, att1.getValue(0));
		
		DiscreteAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals((long)100, att2.getValue(0));
		assertEquals((long)200, att2.getValue(1));
	}
	
	@Test //(expected = IndexOutOfBoundsException.class)
	void testGetValueFromEmptyAttribute() {
		DiscreteAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		assertNull(att0.getValue(0));
	}
	
	@Test 
	void testGetMin() {
		DiscreteAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		assertEquals(null, att0.getMin());
		
		DiscreteAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		assertEquals((long)100, att1.getMin());
		
		DiscreteAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals((long)100, att2.getMin());
	}
	
	@Test 
	void testGetMax() {
		DiscreteAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		assertEquals(null, att0.getMax());
		
		DiscreteAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		assertEquals((long)100, att1.getMax());
		
		DiscreteAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals((long)200, att2.getMax());
	}


}
