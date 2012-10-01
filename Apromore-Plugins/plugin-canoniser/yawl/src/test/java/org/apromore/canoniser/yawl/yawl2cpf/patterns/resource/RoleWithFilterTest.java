package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.junit.Ignore;
import org.junit.Test;

// TODO implement test
@Ignore
public class RoleWithFilterTest extends BasePatternTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/RoleWithFilter.yawl");
    }

    @Test
    public void testRoleWithFilter() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        TaskType task = (TaskType) getNodeByName(rootNet, "A");

        ResourceTypeType distributionSet = hasResourceType(task, yawl2Canonical.getCpf(), "Distribution Set for A");
        assertNotNull(distributionSet);


    }




}
