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
package de.hbrs.oryx.yawl.converter.handler.yawl.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNet;

import de.hbrs.orxy.yawl.YAWLTestData;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandlerTest;
import de.hbrs.oryx.yawl.converter.handler.yawl.element.AtomicTaskHandler;

public class FlowHandlerTest extends YAWLHandlerTest {

    @Test
    public void testConvert() {

        YNet net = (YNet) YAWLTestData.orderFulfillmentSpecification.getDecomposition("Ordering");
        // Adding stub Net
        orderFContext.addNet("Ordering", new BasicDiagram("Net"));

        YAtomicTask sourceTask = (YAtomicTask) net.getNetElement("Create_Purchase_Order_104");
        YAtomicTask targetTask = (YAtomicTask) net.getNetElement("Approve_Purchase_Order_1901");

        // Convert source and target
        new AtomicTaskHandler(orderFContext, sourceTask).convert(net.getID());
        new AtomicTaskHandler(orderFContext, targetTask).convert(net.getID());

        // Convert flow between
        FlowHandler handler = new FlowHandler(orderFContext, new YFlow(sourceTask, targetTask));
        handler.convert(net.getID());

        // Check for Edge
        BasicShape sourceShape = findShapeInOrderF(net, sourceTask);
        BasicShape targetShape = findShapeInOrderF(net, targetTask);
        assertNotNull("Source not found", sourceShape);
        assertNotNull("Target not found", targetShape);

        BasicShape edge = sourceShape.getOutgoingsReadOnly().get(0);
        assertNotNull(edge);
        assertTrue(edge instanceof BasicEdge);

        assertEquals(targetShape, ((BasicEdge) edge).getTarget());

    }

}
