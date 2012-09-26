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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YCompositeTask;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.util.JDOMUtil;

/**
 * Despite its name it is testing the conversion of a whole decomposition, so it also tests most of the other YAWL elements.
 *
 */
public class OryxCompositeTaskHandlerTest extends OryxHandlerTest {

    @Test
    public void testConvert() {
        doTestCompositeTaskById("Ordering_3", "Overall");
        doTestCompositeTaskById("Carrier_Appointment_4", "Overall");
        doTestCompositeTaskById("Freight_in_Transit_6", "Overall");
        doTestCompositeTaskById("Payment_5", "Overall");
        doTestCompositeTaskById("Freight_Delivered_7", "Overall");
    }

    /**
     * Test if the Task is converted correct
     *
     * @param taskId
     * @param netId
     */
    private void doTestCompositeTaskById(final String taskId, final String netId) {
        BasicShape taskShape = findShapeById(taskId, netId);

        BasicShape parentShape = taskShape.getParent();

        mockParentNet(parentShape);
        mockSubnets();

        OryxCompositeTaskHandler handler = new OryxCompositeTaskHandler(context, taskShape);
        handler.convert();

        YExternalNetElement netElement = context.getNet(parentShape).getNetElement(netId+"-"+taskId);

        assertNotNull(netElement);
        assertTrue(netElement instanceof YCompositeTask);

        YCompositeTask task = (YCompositeTask) netElement;
        YCompositeTask originalTask = (YCompositeTask) findOriginalNetElementById(taskId, netId);

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
        YDecomposition decomp = task.getDecompositionPrototype();
        assertNotNull(decomp);
        // Can't compare XML as YAWL does not sort Tasks and Conditions for
        // serialization
        // assertEquals(originalTask.getDecompositionPrototype().toXML(),
        // task.getDecompositionPrototype().toXML());
        YDecomposition originalDecomp = originalTask.getDecompositionPrototype();
        assertEquals(originalDecomp.getAttributes(), decomp.getAttributes());
        assertEquals(originalDecomp.getCodelet(), decomp.getCodelet());
        assertEquals(originalDecomp.getDocumentation(), decomp.getDocumentation());
        assertEquals(originalDecomp.getID(), decomp.getID());
        compareParameters(originalDecomp.getInputParameters(), decomp.getInputParameters());
        compareParameters(originalDecomp.getOutputParameters(), decomp.getOutputParameters());
        assertEquals(originalDecomp.getLogPredicate(), decomp.getLogPredicate());
        assertEquals(originalDecomp.getName(), decomp.getName());

        if (decomp instanceof YNet) {
            YNet net = (YNet) decomp;
            assertTrue(originalDecomp instanceof YNet);
            YNet originalNet = (YNet) originalDecomp;
            assertEquals(originalNet.getExternalDataGateway(), net.getExternalDataGateway());
            compareNetElements(originalNet.getNetElements(), net.getNetElements());
        }
    }

    private void compareNetElements(final Map<String, YExternalNetElement> elements1, final Map<String, YExternalNetElement> elements2) {

        Comparator<YExternalNetElement> elementComparator = new Comparator<YExternalNetElement>() {

            @Override
            public int compare(final YExternalNetElement o1, final YExternalNetElement o2) {
                return o1.getID().compareTo(o2.getID());
            }
        };

        List<YExternalNetElement> sorted1 = new ArrayList<YExternalNetElement>(elements1.values());
        List<YExternalNetElement> sorted2 = new ArrayList<YExternalNetElement>(elements2.values());

        // Remove all implicit conditions as they are not converted to XML
        Iterator<YExternalNetElement> iterator = sorted1.iterator();
        while (iterator.hasNext()) {
            YExternalNetElement element = iterator.next();
            if (element instanceof YCondition && ((YCondition) element).isImplicit()) {
                iterator.remove();
            }
        }

        Collections.sort(sorted1, elementComparator);
        Collections.sort(sorted2, elementComparator);

        StringBuilder result1 = new StringBuilder();
        for (YExternalNetElement netElement : sorted1) {
            result1.append(netElement.toXML().replaceAll("<flowsInto>.*?<\\/flowsInto>", "").replaceAll(netElement.getNet().getID()+"-", ""));
        }

        StringBuilder result2 = new StringBuilder();
        for (YExternalNetElement param : sorted2) {
            result2.append(param.toXML().replaceAll("<flowsInto>.*?<\\/flowsInto>", "").replaceAll(param.getNet().getID()+"-", ""));
        }

        assertEquals(result1.toString(), result2.toString());
        compareFlowsInto(sorted1, sorted2);
    }

    @SuppressWarnings("unchecked")
    private void compareFlowsInto(final List<YExternalNetElement> sorted1, final List<YExternalNetElement> sorted2) {

        Iterator<YExternalNetElement> iterator1 = sorted1.iterator();
        Iterator<YExternalNetElement> iterator2 = sorted2.iterator();

        while (iterator1.hasNext()) {
            YExternalNetElement element1 = iterator1.next();
            assertTrue("Element " + element1.getID() + " not present in converted data", iterator2.hasNext()); // Same
                                                                                                               // amount
                                                                                                               // of
                                                                                                               // elements
            YExternalNetElement element2 = iterator2.next();

            List<YFlow> sorted1Flows = new ArrayList<YFlow>(element1.getPostsetFlows());
            List<YFlow> sorted2Flows = new ArrayList<YFlow>(element2.getPostsetFlows());
            Collections.sort(sorted1Flows);
            Collections.sort(sorted2Flows);

            Iterator<YFlow> flowIterator1 = sorted1Flows.iterator();
            Iterator<YFlow> flowIterator2 = sorted2Flows.iterator();

            while (flowIterator1.hasNext()) {
                assertTrue(flowIterator2.hasNext());
                assertTrue(flowIterator1.next().compareTo(flowIterator2.next()) == 0);
            }
        }

    }

    private void compareParameters(final Map<String, YParameter> param1, final Map<String, YParameter> param2) {

        List<YParameter> sorted1 = new ArrayList<YParameter>(param1.values());
        Collections.sort(sorted1);

        List<YParameter> sorted2 = new ArrayList<YParameter>(param2.values());
        Collections.sort(sorted2);

        StringBuilder result1 = new StringBuilder();
        for (YParameter param : sorted1) {
            result1.append(param.toXML());
        }

        StringBuilder result2 = new StringBuilder();
        for (YParameter param : sorted2) {
            result2.append(param.toXML());
        }

        assertEquals(result1.toString(), result2.toString());
    }

}
