package org.apromore.canoniser.yawl.cpf2yawl.patterns.resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.yawlfoundation.yawlschema.orgdata.CapabilityType;
import org.yawlfoundation.yawlschema.orgdata.OrgDataType;
import org.yawlfoundation.yawlschema.orgdata.ParticipantType;
import org.yawlfoundation.yawlschema.orgdata.PositionType;
import org.yawlfoundation.yawlschema.orgdata.RoleType;

public class OrgDataConversionTest extends BaseCPF2YAWLTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/RoleWithFilter.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/RoleWithFilter.yawl.anf");
    }

    @Ignore //TODO
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

        checkPosition(yawlOrgData, "PositionX");
    }

    private static PositionType checkPosition(final OrgDataType yawlOrgData, final String title) {
        for (PositionType position: yawlOrgData.getPositions().getPosition()) {
            if (title.equals(position.getTitle())) {
                return position;
            }
        }
        fail("Missing position "+title);
        return null;
    }

    private static CapabilityType checkCapability(final OrgDataType yawlOrgData, final String name) {
        for (CapabilityType c: yawlOrgData.getCapabilities().getCapability()) {
            if (name.equals(c.getName())) {
                return c;
            }
        }
        fail("Missing capability "+name);
        return null;
    }

    private static ParticipantType checkParticipant(final OrgDataType yawlOrgData, final String firstName) {
        for (ParticipantType p: yawlOrgData.getParticipants().getParticipant()) {
            if (firstName.equals(p.getFirstname())) {
                return p;
            }
        }
        fail("Missing participant "+firstName);
        return null;
    }

    private static RoleType checkRole(final OrgDataType yawlOrgData, final String name) {
        for (RoleType role: yawlOrgData.getRoles().getRole()) {
            if (name.equals(role.getName())) {
                return role;
            }
        }
        fail("Missing role "+name);
        return null;
    }

}
