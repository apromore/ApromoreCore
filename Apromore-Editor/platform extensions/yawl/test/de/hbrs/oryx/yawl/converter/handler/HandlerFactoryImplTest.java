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
package de.hbrs.oryx.yawl.converter.handler;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YCompositeTask;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YInputCondition;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YOutputCondition;
import org.yawlfoundation.yawl.elements.YSpecification;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;

public class HandlerFactoryImplTest {

    private static HandlerFactoryImpl factory;

    @BeforeClass
    public static void setUpClass() throws Exception {
        factory = new HandlerFactoryImpl(new YAWLConversionContext(), new OryxConversionContext());
    }

    @Test
    public void testCreateYAWLConverterYSpecification() {
        assertNotNull(factory.createYAWLConverter(new YSpecification()));
    }

    @Test
    public void testCreateYAWLConverterYDecomposition() {
        YNet net = new YNet("test", new YSpecification());
        assertNotNull(factory.createYAWLConverter(net));
        net.setAttribute("isRootNet", "true");
        assertNotNull(factory.createYAWLConverter(net));
    }

    @Test
    public void testCreateYAWLConverterYNetElement() {
        YNet net = new YNet("test", new YSpecification());
        assertNotNull(factory.createYAWLConverter(new YAtomicTask("test", 0, 0, net)));
        assertNotNull(factory.createYAWLConverter(new YCompositeTask("test", 0, 0, net)));
        assertNotNull(factory.createYAWLConverter(new YCondition("test", net)));
        assertNotNull(factory.createYAWLConverter(new YInputCondition("test", net)));
        assertNotNull(factory.createYAWLConverter(new YOutputCondition("test", net)));
    }

    @Test
    public void testCreateYAWLConverterYFlow() {
        YNet net = new YNet("test", new YSpecification());
        assertNotNull(factory.createYAWLConverter(new YFlow(new YCondition("test", net), new YCondition("test2", net))));
    }

    @Test
    public void testCreateOryxConverterBasicDiagram() {
        assertNotNull(factory.createOryxConverter(new BasicDiagram("test", "Diagram")));
    }

    @Test
    public void testCreateOryxConverterBasicShape() {
        assertNotNull(factory.createOryxConverter(new BasicNode("test", "AtomicTask")));
        assertNotNull(factory.createOryxConverter(new BasicNode("test", "CompositeTask")));
        assertNotNull(factory.createOryxConverter(new BasicNode("test", "AtomicMultipleTask")));
        assertNotNull(factory.createOryxConverter(new BasicNode("test", "CompositeMultipleTask")));
        assertNotNull(factory.createOryxConverter(new BasicNode("test", "Condition")));
        assertNotNull(factory.createOryxConverter(new BasicNode("test", "InputCondition")));
        assertNotNull(factory.createOryxConverter(new BasicNode("test", "OutputCondition")));
    }

    @Test
    public void testCreateOryxConverterBasicEdgeBasicShape() {
        assertNotNull(factory.createOryxConverter(new BasicEdge("test", "Flow")));
    }

}
