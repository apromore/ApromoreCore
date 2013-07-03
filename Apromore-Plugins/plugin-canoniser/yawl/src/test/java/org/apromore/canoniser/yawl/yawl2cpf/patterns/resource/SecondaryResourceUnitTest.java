package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import java.io.File;

import org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class SecondaryResourceUnitTest extends BaseYAWL2CPFUnitTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/SecondaryResources.yawl");
    }

    @Test
    public void testSecondaryResources() {
        CanonicalProcessType cpf = yawl2Canonical.getCpf();
        final NetType rootNet = cpf.getNet().get(0);

        TaskType task = (TaskType) getNodeByName(rootNet, "A");

        ResourceTypeType roleZ = hasResourceType(task, cpf, "RoleZ", "Primary");
        ResourceTypeType roleY = hasResourceType(task, cpf, "RoleY" ,"Primary");
        ResourceTypeType resourceA = hasResourceType(task, cpf, "ResourceA", "Secondary");
        ResourceTypeType resourceB = hasResourceType(task, cpf, "ResourceB", "Secondary");
        ResourceTypeType roleX = hasResourceType(task, cpf, "RoleX", "Secondary");

    }

}
