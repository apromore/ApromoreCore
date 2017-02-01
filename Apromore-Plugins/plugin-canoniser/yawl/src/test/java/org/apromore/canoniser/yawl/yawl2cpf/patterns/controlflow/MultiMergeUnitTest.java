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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.XORJoinType;
import org.junit.Test;

public class MultiMergeUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC8MultiMerge.yawl");
    }

    @Test
    public void testMultiMerge() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        assertEquals(10, rootNet.getEdge().size());
        assertEquals(9, rootNet.getNode().size());

        final NodeType nodeA = getNodeByName(rootNet, "A");
        assertEquals(1, countOutgoingEdges(rootNet, nodeA.getId()));
        assertEquals(1, countIncomingEdges(rootNet, nodeA.getId()));

        final List<EdgeType> edges = getOutgoingEdges(rootNet, nodeA.getId());
        assertEquals(1, edges.size());
        final NodeType routingNode = getNodeByID(rootNet, edges.get(0).getTargetId());
        assertTrue("Routing node should be ANDSplitType", routingNode instanceof ANDSplitType);
        assertEquals(3, countOutgoingEdges(rootNet, routingNode.getId()));

        final NodeType nodeB = getNodeByName(rootNet, "B");
        assertEquals(1, countOutgoingEdges(rootNet, nodeB.getId()));
        assertEquals(1, countIncomingEdges(rootNet, nodeB.getId()));

        final NodeType nodeC = getNodeByName(rootNet, "C");
        assertEquals(1, countOutgoingEdges(rootNet, nodeC.getId()));
        assertEquals(1, countIncomingEdges(rootNet, nodeC.getId()));

        final NodeType nodeD = getNodeByName(rootNet, "D");
        assertEquals(1, countOutgoingEdges(rootNet, nodeD.getId()));
        assertEquals(1, countIncomingEdges(rootNet, nodeD.getId()));

        final List<EdgeType> cEdges = getOutgoingEdges(rootNet, nodeC.getId());
        assertEquals(1, cEdges.size());
        final NodeType joiningNode = getNodeByID(rootNet, cEdges.get(0).getTargetId());
        assertTrue("Joining Node should be XORJoinType", joiningNode instanceof XORJoinType);
        assertEquals(3, countIncomingEdges(rootNet, joiningNode.getId()));
        assertEquals(1, countOutgoingEdges(rootNet, joiningNode.getId()));

        final NodeType nodeE = getNodeByName(rootNet, "E");
        assertEquals(1, countOutgoingEdges(rootNet, nodeE.getId()));
        assertEquals(1, countIncomingEdges(rootNet, nodeE.getId()));
    }

}
