package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class RoleWithDataDistributionSet extends BaseYAWL2CPFUnitTest {

    /* (non-Javadoc)
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/RoleWithDataDistributionSet.yawl");
    }

    @Test
    public void testDataExpression() {
        CanonicalProcessType cpf = yawl2Canonical.getCpf();
        final NetType rootNet = cpf.getNet().get(0);

        TaskType taskA = (TaskType) getNodeByName(rootNet, "A");
        TaskType taskB = (TaskType) getNodeByName(rootNet, "B");

        // Should have no resources and a "data" filter expression
        assertTrue(taskA.getResourceTypeRef().isEmpty());
        assertEquals("//ResourceType[type/text()='Participant' AND name/text()='cpf:getObjectValue(resource)']", taskA.getFilterByDataExpr().getExpression());

        // Should have no resources and a "data" filter expression
        assertTrue(taskB.getResourceTypeRef().isEmpty());
        assertEquals("//ResourceType[type/text()='Role' AND name/text()='cpf:getObjectValue(resource)']", taskB.getFilterByDataExpr().getExpression());

    }



}
