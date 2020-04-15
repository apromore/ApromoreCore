/*
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2018-2020 The University of Melbourne.
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttributeDiscrete;
import org.junit.Assert;
import org.junit.Test;

public class DiscreteAttributeTest extends AttributeTest {
	
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
	public void testGetValueIndex() {
		DiscreteAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(0, att.getValueIndex(100));
		Assert.assertEquals(1, att.getValueIndex(200));
		
		DiscreteAttribute att2 = this.newWithInvalidKeyAttribute("concept:name", AttributeLevel.EVENT);
		Object result2 = att2.getValueIndex(100);
		Assert.assertEquals(-1, result2);
		
		DiscreteAttribute att3 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Object result3 = att3.getValueIndex(100);
		Assert.assertEquals(-1, result3);
	}

	@Test
	public void testGetValueSize() {
		DiscreteAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att.getValueSize(), 2);
		
		DiscreteAttribute att2 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att2.getValueSize(), 0);
	}

	@Test
	public void testRegisterXAttributeWithInvalidType() {
		DiscreteAttribute att = new DiscreteAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeContinuous("concept:name", 100, null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterXAttributeWithInvalidKey() {
		DiscreteAttribute att = new DiscreteAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeDiscrete("invalid_key", 100, null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterValidXAttribute() {
		DiscreteAttribute att = new DiscreteAttribute("concept:name", AttributeLevel.EVENT);
		XAttributeDiscrete xatt = xAttFactory.createAttributeDiscrete("concept:name", 100, null);
		int result = att.registerXAttribute(xatt);
		Assert.assertEquals(0,  result);
	}

	@Test
	public void testGetValues() {
		DiscreteAttribute att = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals((long)100, att.getValue(0));
		
		DiscreteAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals((long)100, att2.getValue(0));
		Assert.assertEquals((long)200, att2.getValue(1));
	}

	@Test
	public void testGetValue() {
		DiscreteAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals((long)100, att1.getValue(0));
		
		DiscreteAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals((long)100, att2.getValue(0));
		Assert.assertEquals((long)200, att2.getValue(1));
	}
	
	@Test //(expected = IndexOutOfBoundsException.class)
	public void testGetValueFromEmptyAttribute() {
		DiscreteAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att0.getValue(0),null);
	}
	
	@Test 
	public void testGetMin() {
		DiscreteAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(null, att0.getMin());
		
		DiscreteAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals((long)100, att1.getMin());
		
		DiscreteAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals((long)100, att2.getMin());
	}
	
	@Test 
	public void testGetMax() {
		DiscreteAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(null, att0.getMax());
		
		DiscreteAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals((long)100, att1.getMax());
		
		DiscreteAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals((long)200, att2.getMax());
	}


}
