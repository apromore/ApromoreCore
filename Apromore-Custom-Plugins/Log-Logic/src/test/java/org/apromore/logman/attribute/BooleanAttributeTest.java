/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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


import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.junit.Assert;
import org.junit.Test;

public class BooleanAttributeTest extends AttributeTest {
	
	@Override
	protected BooleanAttribute newEmptyAttribute(String key, AttributeLevel level) {
		BooleanAttribute att = new BooleanAttribute(key, level);
		return att; 
	}
	
	@Override
	protected BooleanAttribute newWithInvalidKeyAttribute(String key, AttributeLevel level) {
		BooleanAttribute att = new BooleanAttribute(key, level);
		XAttributeDiscrete xatt = xAttFactory.createAttributeDiscrete(key, 100, null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected BooleanAttribute newWithOneValue(String key, AttributeLevel level) {
		BooleanAttribute att = new BooleanAttribute(key, level);
		XAttributeBoolean xatt = xAttFactory.createAttributeBoolean(key, true, null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected BooleanAttribute newWithTwoValues(String key, AttributeLevel level) {
		BooleanAttribute att = new BooleanAttribute(key, level);
		att.registerXAttribute(xAttFactory.createAttributeBoolean(key, true, null));
		att.registerXAttribute(xAttFactory.createAttributeBoolean(key, false, null));
		return att; 
	}

	@Test
	public void testGetValueIndex() {
		BooleanAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att.getValueIndex(true), 0);
		Assert.assertEquals(att.getValueIndex(false), 1);
		
		BooleanAttribute att2 = this.newWithInvalidKeyAttribute("concept:name", AttributeLevel.EVENT);
		Object result2 = att2.getValueIndex(true);
		Assert.assertEquals(-1, result2);
		
		BooleanAttribute att3 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Object result3 = att3.getValueIndex(true);
		Assert.assertEquals(-1, result3);
	}

	@Test
	public void testGetValueSize() {
		BooleanAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att.getValueSize(), 2);
		
		BooleanAttribute att2 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att2.getValueSize(), 0);
	}

	@Test
	public void testRegisterXAttributeWithInvalidType() {
		BooleanAttribute att = new BooleanAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeContinuous("concept:name", 100, null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterXAttributeWithInvalidKey() {
		BooleanAttribute att = new BooleanAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeBoolean("invalid_key", true, null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterValidXAttribute() {
		BooleanAttribute att = new BooleanAttribute("concept:name", AttributeLevel.EVENT);
		XAttributeBoolean xatt = xAttFactory.createAttributeBoolean("concept:name", true, null);
		int result = att.registerXAttribute(xatt);
		Assert.assertEquals(0,  result);
	}

	@Test
	public void testGetValues() {
		BooleanAttribute att = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att.getValue(0), true);
		
		BooleanAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att2.getValue(0), true);
		Assert.assertEquals(att2.getValue(1), false);
	}

	@Test
	public void testGetValue() {
		BooleanAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att1.getValue(0), true);
		
		BooleanAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att2.getValue(0), true);
		Assert.assertEquals(att2.getValue(1), false);
	}
	
	@Test
	public void testGetValueFromEmptyAttribute() {
		BooleanAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att0.getValue(0),null);
	}


}
