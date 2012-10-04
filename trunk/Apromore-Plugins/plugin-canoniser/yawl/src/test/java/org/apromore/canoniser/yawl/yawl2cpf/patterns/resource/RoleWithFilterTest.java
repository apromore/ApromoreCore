package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternTest;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ResourceDataFilterExpressionType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class RoleWithFilterTest extends BasePatternTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/RoleWithFilter.yawl");
    }

    @Test
    public void testRoleWithFilter() {
        CanonicalProcessType cpf = yawl2Canonical.getCpf();
        final NetType rootNet = cpf.getNet().get(0);

        TaskType task = (TaskType) getNodeByName(rootNet, "A");

        ResourceTypeType roleX = hasResourceType(task, cpf, "RoleX");
        ResourceTypeType roleZ = hasResourceType(task, cpf, "RoleZ");
        ResourceTypeType roleY = hasResourceType(task, cpf, "RoleY");

        assertNotNull(roleX);
        assertNotNull(roleY);
        assertNotNull(roleZ);

        assertNull(task.getAllocationStrategy());

        ResourceDataFilterExpressionType filter = task.getFilterByDataExpr();
        assertNotNull(filter);

        assertEquals(CPFSchema.EXPRESSION_LANGUAGE_XPATH, filter.getLanguage());
        assertEquals("In organisational group 'OrgGroupX' and In organisational group 'PositionX' and With capability 'CapabilityX'",
                filter.getDescription());
        assertEquals(
                "//ResourceType[attribute[@name='OrgGroup' and @value='OrgGroupX'] and " +
                "attribute[@name='Position' and @value='PositionX'] and " +
                "attribute[@name='Capability' and @value='CapabilityX']]",
                filter.getExpression());

        assertNull(task.getFilterByRuntimeExpr());
    }

}
