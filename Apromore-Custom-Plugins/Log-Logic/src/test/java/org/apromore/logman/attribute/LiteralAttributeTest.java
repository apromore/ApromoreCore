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

import org.deckfour.xes.model.XAttributeLiteral;
import org.junit.Assert;
import org.junit.Test;

public class LiteralAttributeTest extends AttributeTest {
	
	@Override
	protected LiteralAttribute newEmptyAttribute(String key, AttributeLevel level) {
		LiteralAttribute att = new LiteralAttribute(key, level);
		return att; 
	}
	
	@Override
	protected LiteralAttribute newWithInvalidKeyAttribute(String key, AttributeLevel level) {
		LiteralAttribute att = new LiteralAttribute(key, level);
		XAttributeLiteral xatt = xAttFactory.createAttributeLiteral("invalid_key", "test", null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected LiteralAttribute newWithOneValue(String key, AttributeLevel level) {
		LiteralAttribute att = new LiteralAttribute(key, level);
		XAttributeLiteral xatt = xAttFactory.createAttributeLiteral(key, "test", null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected LiteralAttribute newWithTwoValues(String key, AttributeLevel level) {
		LiteralAttribute att = new LiteralAttribute(key, level);
		att.registerXAttribute(xAttFactory.createAttributeLiteral(key, "test1", null));
		att.registerXAttribute(xAttFactory.createAttributeLiteral(key, "test2", null));
		return att; 
	}

	@Test
	public void testGetValueIndex() {
		LiteralAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att.getValueIndex("test1"), 0);
		Assert.assertEquals(att.getValueIndex("test2"), 1);
		
		LiteralAttribute att2 = this.newWithInvalidKeyAttribute("invalid key", AttributeLevel.EVENT);
		Object result2 = att2.getValueIndex("test1");
		Assert.assertEquals(-1, result2);
		
		LiteralAttribute att3 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Object result3 = att3.getValueIndex("test1");
		Assert.assertEquals(-1, result3);
	}

	@Test
	public void testGetValueSize() {
		LiteralAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att.getValueSize(), 2);
		
		LiteralAttribute att2 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att2.getValueSize(), 0);
	}

	@Test
	public void testRegisterXAttributeWithInvalidType() {
		LiteralAttribute att = new LiteralAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeDiscrete("concept:name", 100, null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterXAttributeWithInvalidKey() {
		LiteralAttribute att = new LiteralAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeLiteral("invalid_key", "test", null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterValidXAttribute() {
		LiteralAttribute att = new LiteralAttribute("concept:name", AttributeLevel.EVENT);
		XAttributeLiteral xatt = xAttFactory.createAttributeLiteral("concept:name", "test", null);
		int result = att.registerXAttribute(xatt);
		Assert.assertEquals(0,  result);
	}

	@Test
	public void testGetValues() {
		LiteralAttribute att = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att.getValue(0), "test");
		
		LiteralAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att2.getValue(0), "test1");
		Assert.assertEquals(att2.getValue(1), "test2");
	}

	@Test
	public void testGetValue() {
		LiteralAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att1.getValue(0), "test");
		Assert.assertEquals(att1.getValue(0), "test");
		
		LiteralAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att2.getValue(0), "test1");
		Assert.assertEquals(att2.getValue(1), "test2");
	}
	
	@Test// (expected = IndexOutOfBoundsException.class)
	public void testGetValueFromEmptyAttribute() {
		LiteralAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(att0.getValue(0),null);
	}


}
