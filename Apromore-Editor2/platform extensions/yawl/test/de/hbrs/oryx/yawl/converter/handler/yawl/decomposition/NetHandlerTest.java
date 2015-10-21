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
package de.hbrs.oryx.yawl.converter.handler.yawl.decomposition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YDecomposition;

import de.hbrs.orxy.yawl.YAWLTestData;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandlerTest;

public class NetHandlerTest extends YAWLHandlerTest {

    @Test
    public void testConvert() throws JSONException {
        YDecomposition decomp = YAWLTestData.orderFulfillmentSpecification.getDecomposition("Carrier_Appointment");
        DecompositionHandler handler = new NetHandler(orderFContext, decomp);
        handler.convert(YAWLTestData.orderFulfillmentSpecification.getID());

        assertNotNull(orderFContext.getNet("Carrier_Appointment"));
        BasicShape net = orderFContext.getNet("Carrier_Appointment");

        assertEquals("Carrier_Appointment", net.getProperty("yawlid"));

        assertNotNull(net.getProperty("decompositionvariables"));
        JSONObject jsonProp = new JSONObject(net.getProperty("decompositionvariables"));
        assertEquals(13, jsonProp.getJSONArray("items").length());

        JSONObject rowZero = jsonProp.getJSONArray("items").getJSONObject(0);
        assertNotNull(rowZero);
        assertEquals("POrder", rowZero.getString("name"));
        assertEquals("input", rowZero.getString("usage"));
        assertEquals("PurchaseOrderType", rowZero.getString("type"));
        assertTrue(rowZero.isNull("initialvalue"));
        assertEquals("http://www.w3.org/2001/XMLSchema", rowZero.getString("namespace"));
        // TODO add additional attributes

        JSONObject rowSeven = jsonProp.getJSONArray("items").getJSONObject(7);
        assertNotNull(rowSeven);
        assertEquals("TrailerUsage", rowSeven.getString("name"));
        assertEquals("local", rowSeven.getString("usage"));
        assertEquals("TrailerUsageType", rowSeven.getString("type"));
        assertEquals("<OrderNumber/>\n" + "<Packages>\n" + "<Package>\n" + "<PackageID/>\n" + "<Volume>25</Volume>\n" + "</Package>\n"
                + "</Packages>", rowSeven.getString("initialvalue"));
        assertEquals("http://www.w3.org/2001/XMLSchema", rowSeven.getString("namespace"));
        // TODO add additional attributes

        assertEquals(96, net.getChildShapesReadOnly().size());
    }

    @Test
    public void testConvertWithFlows() {
        YDecomposition decomp = YAWLTestData.orderFulfillmentSpecification.getDecomposition("Ordering");
        DecompositionHandler handler = new NetHandler(orderFContext, decomp);
        handler.convert(YAWLTestData.orderFulfillmentSpecification.getID());

        assertNotNull(orderFContext.getNet("Ordering"));

        // Check if YFlows are correctly converted
        BasicShape shape = findAtomicTask(decomp, "Approve_Purchase_Order_1901");
        assertNotNull("Net should contain task: Approve_Purchase_Order_1901", shape);

        assertEquals(2, shape.getIncomingsReadOnly().size());
        assertEquals(2, shape.getOutgoingsReadOnly().size());
        assertTrue(flowsOutOfElement("Modify_Purchase_Order_2768", shape.getIncomingsReadOnly()));
        assertTrue(flowsOutOfElement("Create_Purchase_Order_104", shape.getIncomingsReadOnly()));
        assertTrue(flowsIntoElement("null_156", shape.getOutgoingsReadOnly()));
        assertTrue(flowsIntoElement("OutputCondition_17", shape.getOutgoingsReadOnly()));
    }

    private BasicShape findAtomicTask(final YDecomposition decomp, final String id) {
        Iterator<BasicShape> iterator = orderFContext.getNet(decomp.getID()).getChildShapesReadOnly().iterator();
        while (iterator.hasNext()) {
            BasicShape child = iterator.next();
            if (child.getProperty("yawlid").equals(id)) {
                return child;
            }
        }
        return null;
    }

    private boolean flowsIntoElement(final String id, final List<BasicShape> shapeList) {
        Iterator<BasicShape> iterator = shapeList.iterator();
        while (iterator.hasNext()) {
            BasicShape nextShape = iterator.next();
            if (nextShape instanceof BasicEdge) {
                if (((BasicEdge) nextShape).getTarget().getProperty("yawlid").equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean flowsOutOfElement(final String id, final List<BasicShape> shapeList) {
        Iterator<BasicShape> iterator = shapeList.iterator();
        while (iterator.hasNext()) {
            BasicShape nextShape = iterator.next();
            if (nextShape instanceof BasicEdge) {
                if (((BasicEdge) nextShape).getSource().getProperty("yawlid").equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

}
