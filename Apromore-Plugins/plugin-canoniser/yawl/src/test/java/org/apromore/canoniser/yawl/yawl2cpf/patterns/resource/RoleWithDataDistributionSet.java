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

package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class RoleWithDataDistributionSet extends BaseYAWL2CPFUnitTest {

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/RoleWithDataDistributionSet.yawl");
    }

    @Test
    public void testDataExpression() {
        CanonicalProcessType cpf = yawl2Canonical.getCpf();
        final NetType rootNet = cpf.getNet().get(0);

        TaskType taskA = (TaskType) getNodeByName(rootNet, "A");
        TaskType taskB = (TaskType) getNodeByName(rootNet, "B");

        // Should have no resources and a "data" filter expression
        assertTrue(taskA.getResourceTypeRef().isEmpty());
        assertEquals("//ResourceType[type/text()='Participant' AND name/text()='cpf:getObjectValue(resource)']", taskA.getFilterByDataExpr().getExpression());

        // Should have no resources and a "data" filter expression
        assertTrue(taskB.getResourceTypeRef().isEmpty());
        assertEquals("//ResourceType[type/text()='Role' AND name/text()='cpf:getObjectValue(resource)']", taskB.getFilterByDataExpr().getExpression());

    }



}
