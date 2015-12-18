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

package org.apromore.canoniser.yawl.cpf2yawl.patterns.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;

public class OrgDataConversionUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/RoleWithFilter.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/RoleWithFilter.yawl.anf");
    }

    @Test
    public void testDirectDistribution() {

        OrgDataType yawlOrgData = canonical2Yawl.getOrgData();

        assertNotNull(yawlOrgData.getRoles());
        assertNotNull(yawlOrgData.getParticipants());
        assertNotNull(yawlOrgData.getCapabilities());
        assertNotNull(yawlOrgData.getPositions());

        checkRole(yawlOrgData, "RoleX");
        checkRole(yawlOrgData, "RoleY");
        checkRole(yawlOrgData, "RoleZ");

        checkParticipant(yawlOrgData, "TestY TestY");
        checkParticipant(yawlOrgData, "TestX TestX");

        checkCapability(yawlOrgData, "CapabilityX");
        checkCapability(yawlOrgData, "CapabilityY");
        assertEquals(2, yawlOrgData.getCapabilities().getCapability().size());

        checkPosition(yawlOrgData, "PositionX");
        assertEquals(1, yawlOrgData.getPositions().getPosition().size());
    }

}
