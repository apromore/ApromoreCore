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
package de.hbrs.oryx.yawl.converter.handler.yawl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.yawlfoundation.yawl.elements.YSpecification;

import de.hbrs.orxy.yawl.YAWLTestData;

public class SpecificationHandlerTest extends YAWLHandlerTest {

    @Test
    public void testConvert() {
        YSpecification spec = YAWLTestData.orderFulfillmentSpecification;
        SpecificationHandler handler = new SpecificationHandler(orderFContext, spec);
        handler.convert("");
        BasicDiagram s = orderFContext.getSpecificationDiagram();
        assertNotNull(s);
        assertFalse(orderFContext.getConversionError());
        assertEquals(spec.getName(), s.getProperty("specname"));
        assertEquals(spec.getMetaData().getUniqueID(), s.getProperty("specid"));
        assertEquals(spec.getMetaData().getTitle(), s.getProperty("spectitle"));
        // TODO
    }

}
