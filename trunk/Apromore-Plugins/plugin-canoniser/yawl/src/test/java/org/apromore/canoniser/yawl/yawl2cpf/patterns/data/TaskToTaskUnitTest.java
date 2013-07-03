package org.apromore.canoniser.yawl.yawl2cpf.patterns.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class TaskToTaskUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/Data/WPD9TaskToTask.yawl");
    }


    @Test
    public void testTaskToTask() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);

        assertEquals(1, rootNet.getObject().size());

        ObjectType x = getObjectByName(rootNet, "x");
        assertNotNull(x);
        assertEquals("boolean", ((SoftType) x).getType());

        final TaskType taskA = (TaskType) getNodeByName(rootNet, "A");
        assertNotNull(taskA);

        assertNotNull(getObjectOutputRef(taskA, x));

        OutputExpressionType xExpr = findExpression("x", taskA.getOutputExpr());
        assertNotNull(xExpr);
        assertEquals("x = Boolean({cpf:getTaskObjectValue('x')/text()})",xExpr.getExpression());

        TaskType taskB = (TaskType) getNodeByName(rootNet, "B");
        assertNotNull(taskB);

        assertNotNull(getObjectInputRef(taskB, x));

        InputExpressionType yExpr = findExpression("y", taskB.getInputExpr());
        assertNotNull(yExpr);
        assertEquals("y = {cpf:getObjectValue('x')}", yExpr.getExpression());
    }
}
