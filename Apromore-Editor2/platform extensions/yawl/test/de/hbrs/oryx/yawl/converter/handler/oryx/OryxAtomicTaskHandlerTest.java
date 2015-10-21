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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jdom2.Element;
import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.editor.core.layout.YTaskLayout;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.util.JDOMUtil;

public class OryxAtomicTaskHandlerTest extends OryxHandlerTest {

    @Test
    public void testConvert() {
        doTestAtomicTaskById("Authorize_Loss_or_Damage_Claim_257", "Freight_Delivered");
        doTestAtomicTaskById("Claims_Timeout_254", "Freight_Delivered");
        doTestAtomicTaskById("Approve_Purchase_Order_1901", "Ordering");
        doTestAtomicTaskById("Approve_Shipment_Payment_Order_593", "Payment");
        doTestAtomicTaskById("Prepare_Transportation_Quote_390", "Carrier_Appointment");
        doTestAtomicTaskById("null_389", "Carrier_Appointment");
    }

    @Test
    public void testBasicLayoutConversion() {
        doTestBasicLayout("Authorize_Loss_or_Damage_Claim_257", "Freight_Delivered");
        doTestBasicLayout("Claims_Timeout_254", "Freight_Delivered");
        doTestBasicLayout("Approve_Purchase_Order_1901", "Ordering");
        doTestBasicLayout("Approve_Shipment_Payment_Order_593", "Payment");
        doTestBasicLayout("Prepare_Transportation_Quote_390", "Carrier_Appointment");
        doTestBasicLayout("null_389", "Carrier_Appointment");
    }

    /**
     * Test if the Task Layout is converted correctly
     *
     * @param taskId
     * @param netId
     */
    private void doTestBasicLayout(final String taskId, final String netId) {
        BasicShape taskShape = findShapeById(taskId, netId);

        BasicShape parentShape = taskShape.getParent();

        mockParentNet(parentShape);

        OryxShapeHandler handler = new OryxAtomicTaskHandler(context, taskShape);
        handler.convert();

        YTaskLayout taskLayout = context.getLayout().getTaskLayout(netId, netId+"-"+taskId);
        assertNotNull(taskLayout);
        assertNotNull(taskLayout.getBounds());

        if (taskShape.hasProperty("join") && !taskShape.getProperty("join").equals("none")) {
            assertNotNull(taskLayout.getJoinLayout());
        }

        if (taskShape.hasProperty("split") && !taskShape.getProperty("split").equals("none")) {
            assertNotNull(taskLayout.getSplitLayout());
        }

    }

    /**
     * Test if the Task is converted correct
     *
     * @param taskId
     * @param netId
     */
    private void doTestAtomicTaskById(final String taskId, final String netId) {
        BasicShape taskShape = findShapeById(taskId, netId);

        BasicShape parentShape = taskShape.getParent();

        mockParentNet(parentShape);

        OryxShapeHandler handler = new OryxAtomicTaskHandler(context, taskShape);
        handler.convert();

        YExternalNetElement netElement = context.getNet(parentShape).getNetElement(netId+"-"+taskId);

        assertNotNull(netElement);
        assertTrue(netElement instanceof YAtomicTask);
        // TODO check properties

        YAtomicTask task = (YAtomicTask) netElement;
        YAtomicTask originalTask = (YAtomicTask) findOriginalNetElementById(taskId, netId);

        if (originalTask.getCancelledBySet().size() > 0) {
            assertEquals("Task ID " + taskId, originalTask.getRemoveSet().size(), context.getCancellationSet(context.getNet(parentShape), task)
                    .size());
        }
        for (YExternalNetElement element : originalTask.getRemoveSet()) {
            assertTrue(context.getCancellationSet(context.getNet(parentShape), task).contains(element.getID()));
        }

        Element element = originalTask.getConfigurationElement();
        if (element != null) {
            Element originalConfigurationElement = element.getChild("configuration", element.getNamespace());

            Element newElement = task.getConfigurationElement();
            assertNotNull(task.getConfigurationElement());
            Element newConfigurationElement = task.getConfigurationElement().getChild("configuration", newElement.getNamespace());
            assertNotNull(newConfigurationElement);

            assertEquals("Task ID " + taskId, originalConfigurationElement, newConfigurationElement);
        }

        assertEquals("Task ID " + taskId, originalTask.getCustomFormURL(), task.getCustomFormURL());
        assertEquals("Task ID " + taskId, originalTask.getJoinType(), task.getJoinType());
        assertEquals("Task ID " + taskId, originalTask.getSplitType(), task.getSplitType());
        assertEquals("Task ID " + taskId, originalTask.getName(), task.getName());
        assertEquals("Task ID " + taskId, originalTask.getDocumentation(), task.getDocumentation());
        assertEquals("Task ID " + taskId, JDOMUtil.elementToString(originalTask.getResourcingSpecs()),
                JDOMUtil.elementToString(task.getResourcingSpecs()));
        assertEquals(originalTask.getDecompositionPrototype().toXML(), task.getDecompositionPrototype().toXML());
    }

}
