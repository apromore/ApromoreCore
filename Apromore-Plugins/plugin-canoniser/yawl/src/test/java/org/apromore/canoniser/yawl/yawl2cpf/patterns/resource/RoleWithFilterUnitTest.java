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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ResourceDataFilterExpressionType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class RoleWithFilterUnitTest extends BasePatternUnitTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/RoleWithFilter.yawl");
    }

    @Test
    public void testRoleWithFilter() {
        CanonicalProcessType cpf = yawl2Canonical.getCpf();
        final NetType rootNet = cpf.getNet().get(0);

        TaskType task = (TaskType) getNodeByName(rootNet, "A");

        ResourceTypeType roleX = hasResourceType(task, cpf, "RoleX", "Primary");
        ResourceTypeType roleZ = hasResourceType(task, cpf, "RoleZ", "Primary");
        ResourceTypeType roleY = hasResourceType(task, cpf, "RoleY", "Primary");
        ResourceTypeType resourceA = hasResourceType(task, cpf, "ResourceA", "Secondary");

        assertNotNull(roleX);
        assertNotNull(roleY);
        assertNotNull(roleZ);
        assertNotNull(resourceA);

        assertNull(task.getAllocationStrategy());

        ResourceDataFilterExpressionType filter = task.getFilterByDataExpr();
        assertNotNull(filter);

        assertEquals(CPFSchema.EXPRESSION_LANGUAGE_XPATH, filter.getLanguage());
        assertTrue(filter.getDescription().contains("OrgGroupX") && filter.getDescription().contains("PositionX") && filter.getDescription().contains("CapabilityX"));
        assertEquals(
                "//ResourceType[attribute[@name='OrgGroup' AND @value='OrgGroupX'] AND " +
                "attribute[@name='Position' AND @value='PositionX'] AND " +
                "attribute[@name='Capability' AND @value='CapabilityX']]",
                filter.getExpression());

        assertNull(task.getFilterByRuntimeExpr());

    }

}
