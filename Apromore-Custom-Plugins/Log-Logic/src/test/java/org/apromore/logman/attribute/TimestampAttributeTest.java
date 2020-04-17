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

import org.deckfour.xes.model.XAttributeTimestamp;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class TimestampAttributeTest extends AttributeTest {
	public static long datetime1 = DateTime.now().getMillis();
	public static long datetime2 = DateTime.now().getMillis() + 1000;
	
	@Override
	protected TimestampAttribute newEmptyAttribute(String key, AttributeLevel level) {
		TimestampAttribute att = new TimestampAttribute(key, level);
		return att; 
	}
	
	@Override
	protected TimestampAttribute newWithInvalidKeyAttribute(String key, AttributeLevel level) {
		TimestampAttribute att = new TimestampAttribute(key, level);
		XAttributeTimestamp xatt = xAttFactory.createAttributeTimestamp(key, 100, null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected TimestampAttribute newWithOneValue(String key, AttributeLevel level) {
		TimestampAttribute att = new TimestampAttribute(key, level);
		XAttributeTimestamp xatt = xAttFactory.createAttributeTimestamp(key, datetime1, null);
		att.registerXAttribute(xatt);
		return att; 
	}
	
	@Override
	protected TimestampAttribute newWithTwoValues(String key, AttributeLevel level) {
		TimestampAttribute att = new TimestampAttribute(key, level);
		att.registerXAttribute(xAttFactory.createAttributeTimestamp(key, datetime1, null));
		att.registerXAttribute(xAttFactory.createAttributeTimestamp(key, datetime2, null));
		return att; 
	}

	@Test
	public void testRegisterXAttributeWithInvalidType() {
		TimestampAttribute att = new TimestampAttribute("timestamp", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeContinuous("timestamp", 100, null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterXAttributeWithInvalidKey() {
		TimestampAttribute att = new TimestampAttribute("timestamp", AttributeLevel.EVENT);
		int result = att.registerXAttribute(xAttFactory.createAttributeTimestamp("invalid_key", datetime1, null));
		Assert.assertEquals(-1, result);
	}
	
	@Test
	public void testRegisterValidXAttribute() {
		TimestampAttribute att = new TimestampAttribute("timestamp", AttributeLevel.EVENT);
		XAttributeTimestamp xatt = xAttFactory.createAttributeTimestamp("timestamp", datetime1, null);
		int result = att.registerXAttribute(xatt);
		Assert.assertEquals(0,  result);
	}

	@Test
	public void testGetMinMax() {
		TimestampAttribute att0 = this.newEmptyAttribute("timestamp", AttributeLevel.EVENT);
		Assert.assertEquals(null, att0.getMin());
		Assert.assertEquals(null, att0.getMax());
		
		TimestampAttribute att1 = this.newWithOneValue("timestamp", AttributeLevel.EVENT);
		Assert.assertEquals(datetime1, att1.getMin());
		Assert.assertEquals(datetime1, att1.getMax());
		
		TimestampAttribute att2 = this.newWithTwoValues("timestamp", AttributeLevel.EVENT);
		Assert.assertEquals(datetime1, att2.getMin());
		Assert.assertEquals(datetime2, att2.getMax());
	}

}
