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

import org.deckfour.xes.model.XAttributeContinuous;
import org.junit.Assert;
import org.junit.Test;

public class ContinuousAttributeTest extends AttributeTest {
	
	@Override
	protected ContinuousAttribute newEmptyAttribute(String key, AttributeLevel level) {
		ContinuousAttribute att = new ContinuousAttribute(key, level);
		return att; 
	}
	
	@Override
	protected ContinuousAttribute newWithInvalidKeyAttribute(String key, AttributeLevel level) {
		ContinuousAttribute att = new ContinuousAttribute(key, level);
		XAttributeContinuous xatt = xAttFactory.createAttributeContinuous(key, 100.1, null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected ContinuousAttribute newWithOneValue(String key, AttributeLevel level) {
		ContinuousAttribute att = new ContinuousAttribute(key, level);
		XAttributeContinuous xatt = xAttFactory.createAttributeContinuous(key, 100.1, null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected ContinuousAttribute newWithTwoValues(String key, AttributeLevel level) {
		ContinuousAttribute att = new ContinuousAttribute(key, level);
		att.registerXAttribute(xAttFactory.createAttributeContinuous(key, 100.1, null));
		att.registerXAttribute(xAttFactory.createAttributeContinuous(key, 200.1, null));
		return att; 
	}


	@Test
	public void testRegisterXAttributeWithInvalidType() {
		ContinuousAttribute att = new ContinuousAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeDiscrete("concept:name", 100, null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterXAttributeWithInvalidKey() {
		ContinuousAttribute att = new ContinuousAttribute("concept:name", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeContinuous("invalid_key", 100.1, null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterValidXAttribute() {
		ContinuousAttribute att = new ContinuousAttribute("concept:name", AttributeLevel.EVENT);
		XAttributeContinuous xatt = xAttFactory.createAttributeContinuous("concept:name", 100.1, null);
		int result = att.registerXAttribute(xatt);
		Assert.assertEquals(0,  result);
	}

	@Test 
	public void testGetMin() {
		ContinuousAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(null, att0.getMin());
		
		ContinuousAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(100.1, att1.getMin());
		
		ContinuousAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(100.1, att2.getMin());
	}
	
	@Test 
	public void testGetMax() {
		ContinuousAttribute att0 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(null, att0.getMax());
		
		ContinuousAttribute att1 = this.newWithOneValue("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(100.1, att1.getMax());
		
		ContinuousAttribute att2 = this.newWithTwoValues("concept:name", AttributeLevel.EVENT);
		Assert.assertEquals(200.1, att2.getMax());
	}

}
