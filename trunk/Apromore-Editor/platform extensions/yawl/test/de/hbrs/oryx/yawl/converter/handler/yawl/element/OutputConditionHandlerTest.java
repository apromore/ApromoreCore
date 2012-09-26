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
package de.hbrs.oryx.yawl.converter.handler.yawl.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YOutputCondition;

import de.hbrs.orxy.yawl.YAWLTestData;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandlerTest;

public class OutputConditionHandlerTest extends YAWLHandlerTest {

    @Test
    public void testConvert() {
        YNet net = (YNet) YAWLTestData.orderFulfillmentSpecification.getDecomposition("Freight_in_Transit");
        // Adding stub Net
        orderFContext.addNet("Freight_in_Transit", new BasicDiagram("Net"));

        YOutputCondition condition = (YOutputCondition) net.getNetElement("OutputCondition_47");
        ConditionHandler handler = new OutputConditionHandler(orderFContext, condition);
        handler.convert(net.getID());

        BasicShape shape = findShapeInOrderF(net, condition);
        assertNotNull("OutputCondition not found", shape);
        assertEquals(shape.getStencilId(), "OutputCondition");
    }

    @Test
    public void testConvertWithName() {
        YNet net = (YNet) YAWLTestData.orderFulfillmentSpecification.getDecomposition("Ordering");
        // Adding stub Net
        orderFContext.addNet("Ordering", new BasicDiagram("Net"));

        YOutputCondition condition = (YOutputCondition) net.getNetElement("OutputCondition_17");
        ConditionHandler handler = new OutputConditionHandler(orderFContext, condition);
        handler.convert(net.getID());

        BasicShape shape = findShapeInOrderF(net, condition);
        assertNotNull("OutputCondition not found", shape);
        assertEquals(condition.getName(), shape.getProperty("name"));
        assertEquals(condition.getDocumentation(), shape.getProperty("documentation"));
    }

}
