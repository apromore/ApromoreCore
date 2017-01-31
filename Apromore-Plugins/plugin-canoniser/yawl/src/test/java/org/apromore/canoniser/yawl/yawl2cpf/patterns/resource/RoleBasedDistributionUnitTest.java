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

package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class RoleBasedDistributionUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/Resource/WPR2RoleBasedDistribution.yawl");
    }

    @Test
    public void testRoleBasedDistribution() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        final NodeType nodeA = getNodeByName(rootNet, "A");
        checkNode(rootNet, nodeA, TaskType.class, 1, 1);

        // Check Resources available in Process
        final CanonicalProcessType process = yawl2Canonical.getCpf();

        // Should contain the just the Role and the Participants
        assertEquals(5, process.getResourceType().size());

        // Check Reference correct
        final TaskType taskA = (TaskType) nodeA;
        assertEquals(1, taskA.getResourceTypeRef().size());
        final ResourceTypeRefType resourceRef = taskA.getResourceTypeRef().get(0);
        assertEquals("Primary", resourceRef.getQualifier());

        ResourceTypeType resource = getResourceById(process, resourceRef.getResourceTypeId());
        assertEquals("RoleX", resource.getName());
        assertEquals(2, resource.getSpecializationIds().size());

    }



}
