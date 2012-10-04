package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseYAWL2CPFTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.ResourceTypeType;
import org.junit.Test;

public class OrgDataConversionTest extends BaseYAWL2CPFTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/RoleWithFilter.yawl");
    }

    @Test
    public void test() {
        CanonicalProcessType cpf = yawl2Canonical.getCpf();

        ResourceTypeType roleX = getResourceByName(cpf, "RoleX");
        assertNotNull(roleX);

        ResourceTypeType roleY = getResourceByName(cpf, "RoleY");
        assertNotNull(roleY);

        ResourceTypeType roleZ = getResourceByName(cpf, "RoleZ");
        assertNotNull(roleZ);

        ResourceTypeType participantY = getResourceByName(cpf, "TestY TestY");
        assertNotNull(participantY);
        hasAttribute(participantY, "Capability", "CapabilityX");
        hasAttribute(participantY, "Capability", "CapabilityY");

        ResourceTypeType participantX = getResourceByName(cpf, "TestX TestX");
        assertNotNull(participantX);
        hasAttribute(participantX, "Capability", "CapabilityX");
        hasAttribute(participantX, "Position", "PositionX");
    }


}
