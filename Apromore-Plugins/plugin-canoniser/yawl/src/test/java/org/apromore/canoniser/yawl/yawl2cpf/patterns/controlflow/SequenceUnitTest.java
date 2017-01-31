/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.junit.Test;

public class SequenceUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.patterns.controlflow.PatternTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC1Sequence.yawl");
    }

    @Test
    public void testAllNodesConnectedInSequence() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        assertEquals(3, rootNet.getEdge().size());
        assertEquals(4, rootNet.getNode().size());

        final Set<String> sourceSet = new HashSet<String>();
        final Set<String> targetSet = new HashSet<String>();

        for (final NodeType node : rootNet.getNode()) {
            sourceSet.add(node.getId());
            targetSet.add(node.getId());
        }

        // Test if all Nodes are connected just once
        for (final EdgeType edge : rootNet.getEdge()) {
            assertNotNull(edge.getSourceId());
            assertTrue("Edge referencing invalid Node", sourceSet.remove(edge.getSourceId()));
            assertNotNull(edge.getTargetId());
            assertTrue("Edge referencing invalid Node", targetSet.remove(edge.getTargetId()));
        }

        assertTrue("Not all Nodes connected", sourceSet.size() == 1);
        assertTrue("Not all Nodes connected", targetSet.size() == 1);
    }

}
