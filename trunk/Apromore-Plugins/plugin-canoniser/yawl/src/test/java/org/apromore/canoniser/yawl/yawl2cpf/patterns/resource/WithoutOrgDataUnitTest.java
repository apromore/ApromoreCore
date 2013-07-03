package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Test;

public class WithoutOrgDataUnitTest extends BaseYAWL2CPFUnitTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/RoleWithFilter.yawl");
    }

    @Override
    protected File getYAWLOrgDataFile() {
        return null;
    }

    @Test
    public void test() {
        CanonicalProcessType cpf = yawl2Canonical.getCpf();
        assertTrue(cpf.getResourceType().isEmpty());
    }

}
