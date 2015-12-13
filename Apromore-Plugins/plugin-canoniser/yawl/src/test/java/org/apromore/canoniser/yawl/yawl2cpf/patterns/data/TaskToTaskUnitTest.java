/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
