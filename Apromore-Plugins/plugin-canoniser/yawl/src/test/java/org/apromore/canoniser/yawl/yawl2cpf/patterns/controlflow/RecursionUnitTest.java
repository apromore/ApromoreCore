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

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class RecursionUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC22Recursion.yawl");
    }

    @Test
    public void testIsRecursion() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        assertEquals(6, rootNet.getEdge().size());
        assertEquals(6, rootNet.getNode().size());

        final NodeType nodeNet = getNodeByName(rootNet, "Net");
        assertEquals(1, countOutgoingEdges(rootNet, nodeNet.getId()));
        assertEquals(1, countIncomingEdges(rootNet, nodeNet.getId()));
        assertTrue(nodeNet instanceof TaskType);
        final TaskType taskNet = (TaskType) nodeNet;
        assertEquals("Task linked to wrong subnet!", rootNet.getId(), taskNet.getSubnetId());
    }

}
