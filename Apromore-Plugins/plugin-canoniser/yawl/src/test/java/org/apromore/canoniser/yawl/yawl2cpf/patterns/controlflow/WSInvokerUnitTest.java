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

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.DirectionEnum;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.junit.Test;

public class WSInvokerUnitTest extends BasePatternUnitTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/WSInvokerTest.yawl");
    }

    @Test
    public void testWSInvoker() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        final TaskType nodeA = (TaskType) checkNode(rootNet, "A", TaskType.class, 1, 1);
        assertNotNull(nodeA);

        final TypeAttribute yawlService = findExtensionByName(nodeA, "http://www.yawlfoundation.org/yawlschema/yawlService");
        assertNotNull(yawlService);
        assertNotNull(yawlService.getAny());

        final String incMessageID = getOutgoingEdges(rootNet, nodeA.getId()).get(0).getTargetId();
        assertNotNull(incMessageID);
        final MessageType incMessage = (MessageType) checkNodeById(rootNet, incMessageID, MessageType.class, 1, 1);
        assertNotNull(incMessage);
        assertEquals(DirectionEnum.INCOMING, incMessage.getDirection());

        final String outMessageID = getIncomingEdges(rootNet, nodeA.getId()).get(0).getSourceId();
        assertNotNull(outMessageID);
        final MessageType outMessage = (MessageType) checkNodeById(rootNet, outMessageID, MessageType.class, 1, 1);
        assertNotNull(outMessage);
        assertEquals(DirectionEnum.OUTGOING, outMessage.getDirection());
    }

}
