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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YNet;

import de.hbrs.orxy.yawl.YAWLTestData;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandlerTest;
import de.hbrs.oryx.yawl.converter.layout.NetLayout;
import de.hbrs.oryx.yawl.util.YAWLUtils;

/**
 * Tests converting a YAWL AtomicTask
 */
public class AtomicTaskHandlerTest extends YAWLHandlerTest {

    @Test
    public void testConvert() throws JSONException {
        YNet net = (YNet) YAWLTestData.orderFulfillmentSpecification.getDecomposition("Ordering");
        // Adding stub Net
        orderFContext.addNet("Ordering", new BasicDiagram("Net"));

        YAtomicTask task = (YAtomicTask) net.getNetElement("Approve_Purchase_Order_1901");
        AtomicTaskHandler handler = new AtomicTaskHandler(orderFContext, task);
        assertEquals("AtomicTask", handler.getTaskType());
        handler.convert(net.getID());

        BasicShape shape = findShapeInOrderF(net, task);
        assertNotNull("Atomic Task not found", shape);

        // Can't check this here, as edges are connected in NetHandler after all
        // NetElements are converted. See code there for further checks.
        // assertEquals(2, shape.getIncomingsReadOnly().size());
        // assertEquals(2, shape.getOutgoingsReadOnly().size());

        NetLayout netLayout = orderFContext.getNetLayout(net.getID());
        assertTrue(netLayout.getFlowSet().containsAll(task.getPostsetFlows()));
        // Just PostsetFlows are remembered
        // assertTrue(netLayout.getFlowSet().containsAll(task.getPresetFlows()));

        // Basic Task Properties

        assertEquals(task.getName(), shape.getProperty("name"));
        assertEquals("Manual", shape.getProperty("icon"));
        assertEquals("xorT", shape.getProperty("join"));
        assertEquals("xorB", shape.getProperty("split"));

        JSONArray flowsInto = shape.getPropertyJsonObject("flowsinto").getJSONArray("items");
        assertEquals(2, flowsInto.length());
        JSONObject firstFlow = flowsInto.getJSONObject(0);
        assertEquals("null_156", firstFlow.getString("task"));
        assertEquals("/Ordering/POApproval/text()='true'", firstFlow.getString("predicate"));
        assertEquals(false, firstFlow.getBoolean("isdefault"));
        assertEquals(0, firstFlow.getInt("ordering"));

        JSONObject secondFlow = flowsInto.getJSONObject(1);
        assertEquals("OutputCondition_17", secondFlow.getString("task"));
        assertTrue(secondFlow.isNull("predicate"));
        assertEquals(true, secondFlow.getBoolean("isdefault"));
        assertTrue(secondFlow.isNull("ordering"));

        JSONArray inputParams = shape.getPropertyJsonObject("inputparameters").getJSONArray("items");
        assertEquals(1, inputParams.length());
        JSONObject inputParam1 = inputParams.getJSONObject(0);
        assertEquals("POrder", inputParam1.getString("taskvariable"));
        assertEquals("<POrder>{/Ordering/POrder/*}</POrder>", inputParam1.getString("expression"));

        JSONArray outputParams = shape.getPropertyJsonObject("outputparameters").getJSONArray("items");
        assertEquals(2, outputParams.length());
        JSONObject outputParam1 = outputParams.getJSONObject(0);
        assertEquals("PO_timedout", outputParam1.getString("taskvariable"));
        assertEquals("<PO_timedout>false</PO_timedout>", outputParam1.getString("expression"));
        JSONObject outputParam2 = outputParams.getJSONObject(1);
        assertEquals("POApproval", outputParam2.getString("taskvariable"));
        assertEquals("<POApproval>{/Approve_Purchase_Order/POApproval/text()}</POApproval>", outputParam2.getString("expression"));

        // Atomic Task only

        assertNull(shape.getProperty("configuration"));
        assertNull(shape.getProperty("customform"));
        assertEquals("{\"items\":[]}", shape.getProperty("cancelationset"));
        assertEquals("manual", shape.getProperty("decompositionexternalinteraction"));

        // Resourcing
        assertEquals("user", shape.getProperty("startinitiator"));
        assertEquals("", shape.getProperty("startinteraction"));
        assertEquals("user", shape.getProperty("allocateinitiator"));
        assertEquals("", shape.getProperty("allocateinteraction"));
        assertEquals("system", shape.getProperty("offerinitiator"));
        assertEquals("<distributionSet xmlns=\"http://www.yawlfoundation.org/yawlschema\">\r\n" + "  <initialSet>\r\n"
                + "    <role>RO-5dee0ac5-b65b-42e6-bef3-8e0446f74dca</role>\r\n" + "  </initialSet>\r\n" + "</distributionSet>",
                shape.getProperty("offerinteraction"));
        assertEquals("<privilege xmlns=\"http://www.yawlfoundation.org/yawlschema\">\r\n" + "  <name>canDelegate</name>\r\n"
                + "  <allowall>true</allowall>\r\n" + "</privilege>", shape.getProperty("privileges"));

        assertNotNull(shape.getPropertyJsonObject("decompositionvariables"));
        JSONArray varsJson = shape.getPropertyJsonObject("decompositionvariables").getJSONArray("items");
        assertNotNull(varsJson);
        JSONObject variable = varsJson.getJSONObject(0);
        assertEquals("POrder", variable.getString("name"));
        assertEquals("PurchaseOrderType", variable.getString("type"));
        assertEquals("input", variable.getString("usage"));
        assertFalse(variable.has("initalvalue"));
        assertEquals("http://www.w3.org/2001/XMLSchema", variable.getString("namespace"));
        assertEquals(false, variable.getBoolean("ismandatory"));
        assertEquals("", variable.getString("attributes"));

    }

    @Test
    public void testConvertWithTimer() {
        YNet net = (YNet) YAWLTestData.orderFulfillmentSpecification.getDecomposition("Freight_Delivered");
        // Adding stub Net
        orderFContext.addNet("Freight_Delivered", new BasicDiagram("Net"));

        YAtomicTask task = (YAtomicTask) net.getNetElement("Claims_Timeout_254");
        AtomicTaskHandler handler = new AtomicTaskHandler(orderFContext, task);
        assertEquals("AtomicTask", handler.getTaskType());
        handler.convert(net.getID());

        BasicShape shape = findShapeInOrderF(net, task);
        assertNotNull("Atomic Task not found", shape);

        assertEquals("<timer><netparam>ClaimsDeadlineTimer</netparam></timer>", shape.getProperty("timer"));

        assertEquals("automated", shape.getProperty("decompositionexternalinteraction"));
        assertEquals("Timer", shape.getProperty("icon"));
    }

    @Test
    public void testConfigureableWorkflow() {
        YNet net = (YNet) YAWLTestData.testSpecification.getDecomposition("TestNet");
        // Adding stub Net
        testContext.addNet("TestNet", new BasicDiagram("Net"));

        YAtomicTask task = (YAtomicTask) net.getNetElement("Test");
        AtomicTaskHandler handler = new AtomicTaskHandler(testContext, task);
        handler.convert(net.getID());

        BasicShape shape = findShapeInTest(net, task);
        assertNotNull("Atomic Task not found", shape);

        assertNotNull(shape.getProperty("configuration"));
        Element confElement = task.getConfigurationElement();
        assertEquals(YAWLUtils.elementToString(confElement.getChild("configuration", confElement.getNamespace())), shape.getProperty("configuration"));
    }

    @Test
    public void testExtendedAttributesAndLogPredicates() throws JSONException {
        YNet net = (YNet) YAWLTestData.testSpecification.getDecomposition("TestNet");
        // Adding stub Net
        testContext.addNet("TestNet", new BasicDiagram("Net"));

        YAtomicTask task = (YAtomicTask) net.getNetElement("Test");
        AtomicTaskHandler handler = new AtomicTaskHandler(testContext, task);
        handler.convert(net.getID());

        BasicShape shape = findShapeInTest(net, task);
        assertNotNull("Atomic Task not found", shape);

        assertEquals(task.getDecompositionPrototype().getID(), shape.getProperty("decompositionid"));

        assertNotNull(shape.getPropertyJsonObject("decompositionvariables"));
        JSONArray varsJson = shape.getPropertyJsonObject("decompositionvariables").getJSONArray("items");
        assertNotNull(varsJson);
        JSONObject variable = varsJson.getJSONObject(0);
        assertEquals("Test", variable.getString("name"));
        assertEquals("string", variable.getString("type"));
        assertEquals("output", variable.getString("usage"));
        assertEquals("Test", variable.getString("initialvalue"));
        assertEquals("http://www.w3.org/2001/XMLSchema", variable.getString("namespace"));
        assertEquals(false, variable.getBoolean("ismandatory"));
        assertEquals(
                " <text-above>Test</text-above> <maxLength>23</maxLength> <optional>true</optional> <background-color>#FF6699</background-color> <length>23</length> <tooltip>Test</tooltip> <text-below>Test</text-below>",
                variable.getString("attributes"));
        assertEquals("<completion>OnOutput</completion>", variable.getString("logpredicate"));

        // Test Task Log Predicate
        assertEquals("<start>OnStart</start><completion>OnCompletion</completion>", shape.getProperty("decompositionlogpredicate"));

        assertEquals("http://test", shape.getProperty("customform"));
        assertEquals("Test", shape.getProperty("documentation"));
    }

}
