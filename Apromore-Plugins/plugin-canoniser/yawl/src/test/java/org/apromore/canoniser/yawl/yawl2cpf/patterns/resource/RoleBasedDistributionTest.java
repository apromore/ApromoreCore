package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class RoleBasedDistributionTest extends BasePatternTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFTest#getYAWLFile()
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

        // Should contain the just the Role
        assertEquals(1, process.getResourceType().size());

        // Check Reference correct
        final TaskType taskA = (TaskType) nodeA;
        assertEquals(1, taskA.getResourceTypeRef().size());
        final ResourceTypeRefType resourceRef = taskA.getResourceTypeRef().get(0);
        assertEquals(null, resourceRef.getQualifier());

        ResourceTypeType resource = getResourceById(process, resourceRef.getResourceTypeId());
        assertEquals("RoleX", resource.getName());
        assertEquals(0, resource.getSpecializationIds().size());

    }



}
