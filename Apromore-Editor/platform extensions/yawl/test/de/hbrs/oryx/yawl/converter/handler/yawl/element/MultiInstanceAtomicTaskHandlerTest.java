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

import org.json.JSONException;
import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YNet;

import de.hbrs.orxy.yawl.YAWLTestData;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandlerTest;

public class MultiInstanceAtomicTaskHandlerTest extends YAWLHandlerTest {
    @Test
    public void testConvert() throws JSONException {
        YNet net = (YNet) YAWLTestData.orderFulfillmentSpecification.getDecomposition("Freight_in_Transit");
        // Adding stub Net
        orderFContext.addNet("Freight_in_Transit", new BasicDiagram("Net"));

        YAtomicTask task = (YAtomicTask) net.getNetElement("Log_Trackpoint_Order_Entry_4514");
        MultiInstanceAtomicTaskHandler handler = new MultiInstanceAtomicTaskHandler(orderFContext, task);
        assertEquals("AtomicMultipleTask", handler.getTaskType());
        handler.convert(net.getID());

        BasicShape shape = findShapeInOrderF(net, task);
        assertNotNull("Atomic Multiple Task not found", shape);

        assertEquals(new Integer(1), shape.getPropertyInteger("minimum"));
        assertEquals(new Integer(Integer.MAX_VALUE), shape.getPropertyInteger("maximum"));
        assertEquals(new Integer(Integer.MAX_VALUE), shape.getPropertyInteger("threshold"));

        assertEquals("static", shape.getProperty("creationmode"));
        assertEquals("/Freight_in_Transit/TrackpointNotices", shape.getProperty("miinputexpression"));
        assertEquals("for $i in /TrackpointNotices/* return $i", shape.getProperty("miinputsplittingexpression"));
        assertEquals("TrackpointNotice", shape.getProperty("miinputformalinputparam"));
        assertEquals(
                "<TrackpointOrderEntry> <TrackpointNotice><OrderNumber>{/Log_Trackpoint_Order_Entry/TrackpointNotice/OrderNumber/text()}</OrderNumber><ShipmentNumber>{/Log_Trackpoint_Order_Entry/TrackpointNotice/ShipmentNumber/text()}</ShipmentNumber> <Trackpoint>{/Log_Trackpoint_Order_Entry/TrackpointNotice/Trackpoint/text()}</Trackpoint><ArrivalTime>{/Log_Trackpoint_Order_Entry/TrackpointNotice/ArrivalTime/text()}</ArrivalTime> <DepartureTime>{/Log_Trackpoint_Order_Entry/TrackpointNotice/DepartureTime/text()}</DepartureTime><Notes>{/Log_Trackpoint_Order_Entry/TrackpointNotice/Notes/text()}</Notes> </TrackpointNotice> <Report>{/Log_Trackpoint_Order_Entry/Report/text()}</Report> </TrackpointOrderEntry>",
                shape.getProperty("mioutputformaloutputexpression"));
        assertEquals("<TrackpointOrderEntries>{for $i in /Log_Trackpoint_Order_Entry/TrackpointOrderEntry return $i}</TrackpointOrderEntries>",
                shape.getProperty("mioutputoutputjoiningexpression"));
        assertEquals("TrackpointOrderEntries", shape.getProperty("mioutputresultappliedtolocalvariable"));

    }

}
