/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.oryx.yawl.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.Test;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import de.hbrs.orxy.yawl.OryxTestData;
import de.hbrs.oryx.yawl.converter.YAWLConverter;
import de.hbrs.oryx.yawl.converter.YAWLConverter.YAWLResult;

/**
 * Testing conversion from Oryx to YAWL
 * 
 */
public class Oryx2YAWLConversionTest {

	@Test
	public void testBasicConversion() throws YSyntaxException, JDOMException, IOException {
		YAWLConverter converter = new YAWLConverter();
		YAWLResult result = converter.convertOryxToYAWL(OryxTestData.orderFulfillment);
		assertNotNull(result);
		assertEquals("orderfulfillment", result.getFilename());
		assertNotNull(result.getWarnings());
		assertNotNull(result.getYAWLAsXML());

		// Can not directly compare source
		// assertEquals(result.getYAWLAsXML(),
		// YAWLTestData.orderFulfillmentSource);
	}

}
