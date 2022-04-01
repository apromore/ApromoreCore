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

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class AttributeTest {
	protected XFactory xAttFactory = new XFactoryNaiveImpl();
	
	protected abstract AbstractAttribute newEmptyAttribute(String key, AttributeLevel level);
	protected abstract AbstractAttribute newWithInvalidKeyAttribute(String key, AttributeLevel level);
	protected abstract AbstractAttribute newWithOneValue(String key, AttributeLevel level);
	protected abstract AbstractAttribute newWithTwoValues(String key, AttributeLevel level);

	@Test
	void testGetKeyWithLevel() {
		AbstractAttribute att1 = this.newEmptyAttribute("concept:name", AttributeLevel.EVENT);
		assertEquals("concept:name", att1.getKeyWithLevel());
		
		AbstractAttribute att2 = this.newEmptyAttribute("concept:name", AttributeLevel.TRACE);
		assertEquals("(case)concept:name", att2.getKeyWithLevel());
		
		AbstractAttribute att3 = this.newEmptyAttribute("concept:name", AttributeLevel.LOG);
		assertEquals("(log)concept:name", att3.getKeyWithLevel());
	}
}
