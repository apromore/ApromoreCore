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
