/**
 * Copyright (c) 2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
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

import static org.junit.Assert.*;

import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import de.hbrs.orxy.yawl.YAWLTestData;
import de.hbrs.oryx.yawl.converter.YAWLConverter;
import de.hbrs.oryx.yawl.converter.YAWLConverter.OryxResult;

/**
 * Testing Conversion from YAWL to Oryx
 */
public class YAWL2OryxConversionTest {

	@Test
	public void testBasicConversion() throws YSyntaxException, JDOMException, IOException {
		YAWLConverter converter = new YAWLConverter();
		OryxResult result = converter.convertYAWLToOryx(YAWLTestData.orderFulfillmentSource);
		assertNotNull(result);
		assertEquals("There should be 9 nets in Orderfulfilment", result.getDiagrams().size(), 9);
		assertNotNull(result.getRootDiagram());
		assertNotNull(result.getWarnings());
		assertNotNull(result.getRootNetId());
		assertEquals("Wrong RootNetId", "Overall", result.getRootNetId());
	}

	@Test
	public void testBasicDiagramConversion() throws YSyntaxException, JDOMException, IOException {
		YAWLConverter converter = new YAWLConverter();
		OryxResult c = converter.convertYAWLToOryx(YAWLTestData.orderFulfillmentSource);
		BasicDiagram d = c.getRootDiagram();
		assertEquals("http://b3mn.org/stencilset/yawl2.2#", d.getStencilsetRef().getNamespace());
		assertEquals("Wrong amount of Shapes", 16, d.getChildShapesReadOnly().size());
		assertEquals("Overall", d.getProperty("yawlid"));
	}

}
