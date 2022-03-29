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

import org.deckfour.xes.model.XAttributeLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LiteralAttributeTest extends AttributeTest {
	
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
	void testGetValueIndex() {
		LiteralAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals(0, att.getValueIndex("test1"));
		assertEquals(1, att.getValueIndex("test2"));
		
		LiteralAttribute att2 = this.newWithInvalidKeyAttribute("invalid key", AttributeLevel.EVENT);
		Object result2 = att2.getValueIndex("test1");
		assertEquals(-1, result2);
		
		LiteralAttribute att3 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Object result3 = att3.getValueIndex("test1");
		assertEquals(-1, result3);
	}

	@Test
	void testGetValueSize() {
		LiteralAttribute att = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals(2, att.getValueSize());
		
		LiteralAttribute att2 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		assertEquals(0, att2.getValueSize());
	}

	@Test
	void testRegisterXAttributeWithInvalidType() {
		LiteralAttribute att = new LiteralAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeDiscrete("concept:name", 100, null));
		assertEquals(-1, result);
	}
	
	@Test
	void testRegisterXAttributeWithInvalidKey() {
		LiteralAttribute att = new LiteralAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeLiteral("invalid_key", "test", null));
		assertEquals(-1, result);
	}
	
	@Test
	void testRegisterValidXAttribute() {
		LiteralAttribute att = new LiteralAttribute("concept:name", AttributeLevel.EVENT);
		XAttributeLiteral xatt = xAttFactory.createAttributeLiteral("concept:name", "test", null);
		int result = att.registerXAttribute(xatt);
		assertEquals(0,  result);
	}

	@Test
	void testGetValues() {
		LiteralAttribute att = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		assertEquals("test", att.getValue(0));
		
		LiteralAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals("test1", att2.getValue(0) );
		assertEquals("test2", att2.getValue(1));
	}

	@Test
	void testGetValue() {
		LiteralAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		assertEquals("test", att1.getValue(0));
		assertEquals("test", att1.getValue(0));
		
		LiteralAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		assertEquals("test1", att2.getValue(0) );
		assertEquals("test2", att2.getValue(1) );
	}
	
	@Test// (expected = IndexOutOfBoundsException.class)
	void testGetValueFromEmptyAttribute() {
		LiteralAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		assertNull(att0.getValue(0));
	}


}
