/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORSplitType;
import org.junit.Test;

public class ExclusiveChoicePatternUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC4ExclusiveChoice.yawl");
    }

    @Test
    public void testIsExclusiveChoice() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        assertEquals(9, rootNet.getEdge().size());
        assertEquals(8, rootNet.getNode().size());

        final NodeType nodeA = checkNode(rootNet, "A", TaskType.class, 1, 1);

        final NodeType routingNode = getFirstSuccessor(rootNet, nodeA);
        assertTrue("Routing node should be XORSplitType", routingNode instanceof XORSplitType);
        assertEquals(3, countOutgoingEdges(rootNet, routingNode.getId()));

        final TaskType nodeB = checkNode(rootNet, "B", TaskType.class, 1, 1);
        final NodeType nodeC = checkNode(rootNet, "C", TaskType.class, 1, 1);
        final TaskType nodeD = checkNode(rootNet, "D", TaskType.class, 1, 1);


        final List<EdgeType> routingEdges = getOutgoingEdges(rootNet, routingNode.getId());
        for (final EdgeType edge : routingEdges) {
            if (edge.getTargetId().equals(nodeB.getId())) {
                assertTrue("Edge to Node B should be default", edge.getConditionExpr() != null);
            } else if (edge.getTargetId().equals(nodeC.getId())) {
                assertTrue("Edge to Node C should have a condition", edge.getConditionExpr() != null);
            } else if (edge.getTargetId().equals(nodeD.getId())) {
                assertTrue("Edge to Node D  should have a condition", edge.isDefault());
            }
        }

        final List<EdgeType> cEdges = getOutgoingEdges(rootNet, nodeC.getId());
        assertEquals(1, cEdges.size());
        final NodeType joiningNode = getNodeByID(rootNet, cEdges.get(0).getTargetId());
        assertTrue("Joining Node should be StateType", joiningNode instanceof StateType);
        assertEquals(3, countIncomingEdges(rootNet, joiningNode.getId()));
        assertEquals(1, countOutgoingEdges(rootNet, joiningNode.getId()));

    }

}
