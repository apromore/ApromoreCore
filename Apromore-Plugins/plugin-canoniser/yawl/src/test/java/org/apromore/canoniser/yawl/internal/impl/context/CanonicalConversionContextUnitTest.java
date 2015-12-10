/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.yawl.internal.impl.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.yawl.utils.NoOpMessageManager;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeType;
import org.junit.Before;
import org.junit.Test;

public class CanonicalConversionContextUnitTest {

    private CanonicalConversionContext context;
    private CanonicalConversionContext context2;
    private CanonicalConversionContext context3;
    private CanonicalConversionContext context4;

    @Before
    public void setUp() throws Exception {
        final File file = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC4ExclusiveChoice.yawl.cpf");
        final CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file)), true).getValue();
        context = new CanonicalConversionContext(cpf, new AnnotationsType(), new NoOpMessageManager());

        final File file2 = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPR2RoleBasedDistribution.yawl.cpf");
        final CanonicalProcessType cpf2 = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file2)), true).getValue();
        context2 = new CanonicalConversionContext(cpf2, new AnnotationsType(), new NoOpMessageManager());

        final File file3 = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/orderfulfillment.yawl.cpf");
        final CanonicalProcessType cpf3 = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file3)), true).getValue();
        context3 = new CanonicalConversionContext(cpf3, new AnnotationsType(), new NoOpMessageManager());

        final File file4 = new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/External/PNML/13_AndSplitJoin.cpf");
        final CanonicalProcessType cpf4 = CPFSchema.unmarshalCanonicalFormat(new BufferedInputStream(new FileInputStream(file4)), false).getValue();
        context4 = new CanonicalConversionContext(cpf4, new AnnotationsType(), new NoOpMessageManager());
    }

    @Test
    public void testGetNetById() {
        doTestNetById(context);
        doTestNetById(context2);
        doTestNetById(context3);
        doTestNetById(context4);
    }

    private void doTestNetById(final CanonicalConversionContext c) {
        for (final NetType net : c.getCanonicalProcess().getNet()) {
            assertNotNull(c.getNetById(net.getId()));
        }
    }

    @Test
    public void testGetNodeById() {
        doTestNodeById(context);
        doTestNodeById(context2);
        doTestNodeById(context3);
        doTestNodeById(context4);
    }

    private void doTestNodeById(final CanonicalConversionContext c) {
        for (final NetType net : c.getCanonicalProcess().getNet()) {
            for (final NodeType node : net.getNode()) {
                assertNotNull(c.getNodeById(node.getId()));
            }
        }
    }

    @Test
    public void testGetResourceTypeById() {
        for (final ResourceTypeType rt : context2.getCanonicalProcess().getResourceType()) {
            assertNotNull(context2.getResourceTypeById(rt.getId()));
        }
        for (final ResourceTypeType rt : context3.getCanonicalProcess().getResourceType()) {
            assertNotNull(context3.getResourceTypeById(rt.getId()));
        }
    }

    @Test
    public void testGetObjectTypeById() {
        doTestObjectById(context);
        doTestObjectById(context3);
    }

    private void doTestObjectById(final CanonicalConversionContext c) {
        for (final NetType net : c.getCanonicalProcess().getNet()) {
            for (final ObjectType o : net.getObject()) {
                assertNotNull(c.getObjectTypeById(o.getId()));
            }
        }
    }

    @Test
    public void testGetEdgeById() {
        doTestEdgeById(context);
        doTestEdgeById(context2);
        doTestEdgeById(context3);
        doTestEdgeById(context4);
    }

    private void doTestEdgeById(final CanonicalConversionContext c) {
        for (final NetType net : c.getCanonicalProcess().getNet()) {
            for (final EdgeType e : net.getEdge()) {
                final EdgeType edge = c.getEdgeById(e.getId());
                assertNotNull(edge);
                assertNotNull("Edge is missing Source", edge.getSourceId());
                assertNotNull("Edge is missing Target", edge.getTargetId());
            }
        }
    }

    @Test
    public void testGetPreSet() {
        assertEquals(1, context.getPreSet("C-OutputCondition").size());
        assertEquals(0, context.getPreSet("C-InputCondition").size());
        // State before Output Condition
        final NodeType state = context.getFirstPredecessor("C-OutputCondition");
        assertNotNull(state);
        assertEquals(3, context.getPreSet(state.getId()).size());
        assertEquals(1, context.getPreSet("C-A").size());
        assertEquals(1, context.getPreSet("C-C").size());
        assertEquals(1, context.getPreSet("C-D").size());

    }

    @Test
    public void testGetPostSet() {
        assertEquals(0, context.getPostSet("C-OutputCondition").size());
        assertEquals(1, context.getPostSet("C-InputCondition").size());
        final NodeType split = context.getFirstSuccessor("C-A");
        assertNotNull("Missing successor!", split);
        assertEquals(3, context.getPostSet(split.getId()).size());
        assertEquals(1, context.getPostSet("C-C").size());
        assertEquals(1, context.getPostSet("C-D").size());

        // Orderfulfillment
        assertEquals(1, context3.getPostSet("C-Freight-Delivered-7").size());
        assertEquals(1, context3.getPostSet(context3.getFirstSuccessor("C-Freight-Delivered-7").getId()).size());
        assertEquals(0, context3.getPostSet("C-OutputCondition-2").size());
    }

    @Test
    public void testGetFirstSuccessor() {
        assertNotNull(context.getFirstSuccessor("C-InputCondition"));
        assertNotNull(context.getFirstSuccessor("C-A"));
        assertNull(context.getFirstSuccessor("C-OutputCondition"));
    }

    @Test
    public void testGetFirstPredecessor() {
        assertNotNull(context.getFirstPredecessor("C-OutputCondition"));
        assertNotNull(context.getFirstPredecessor("C-A"));
        assertNull(context.getFirstPredecessor("C-InputCondition"));
    }

    @Test
    public void testHasMultipleExits() {
        assertFalse(context.hasMultipleExits(context.getNetById("N-Net")));
        assertFalse(context3.hasMultipleExits(context3.getNetById("N-Overall")));
        assertTrue(context4.hasMultipleExits(context4.getNetById("6121993")));
    }

    @Test
    public void testHasMultipleEntries() {
        assertFalse(context.hasMultipleEntries(context.getNetById("N-Net")));
        assertFalse(context3.hasMultipleEntries(context3.getNetById("N-Overall")));
        assertTrue(context4.hasMultipleEntries(context4.getNetById("6121993")));
    }

    @Test
    public void testInvalidateCPFCaches() {
        final NodeType node = context.getNodeById("C-A");
        context.getNetById("N-Net").getNode().remove(node);
        context.invalidateCPFCaches();
        assertNull(context.getNodeById("C-A"));
    }

}
