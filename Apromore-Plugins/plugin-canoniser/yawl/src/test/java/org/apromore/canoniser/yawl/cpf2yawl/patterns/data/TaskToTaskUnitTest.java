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

package org.apromore.canoniser.yawl.cpf2yawl.patterns.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

public class TaskToTaskUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPD9TaskToTask.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPD9TaskToTask.yawl.anf");
    }

    @Test
    public void testNetVariables() {
        NetFactsType net = findRootNet();
        checkLocalVariable("x", "boolean", net);
        assertTrue(net.getOutputParam().isEmpty());
        assertTrue(net.getInputParam().isEmpty());
        assertEquals(1, net.getLocalVariable().size());
    }

    @Test
    public void testTaskMappings() {
        ExternalTaskFactsType taskA = findTaskByName("A", findRootNet());
        ExternalTaskFactsType taskB = findTaskByName("B", findRootNet());

        assertNotNull(taskA);
        assertNotNull(taskB);

        assertNull(taskA.getStartingMappings());
        checkOutputMapping("x", "<x>Boolean({/C-A/x/text()})</x>", taskA);

        assertNull(taskB.getCompletedMappings());
        checkInputMapping("y", "<y>{/N-Net/x}</y>", taskB);
    }

    @Test
    public void testTaskVariables() {
        WebServiceGatewayFactsType dA = findDecomposition(findTaskByName("A", findRootNet()));
        WebServiceGatewayFactsType dB = findDecomposition(findTaskByName("B", findRootNet()));

        assertNotNull(dA);
        assertNotNull(dB);

        assertEquals(1, dA.getOutputParam().size());
        assertTrue(dA.getInputParam().isEmpty());
        checkOutputParameter("x", "string", dA);

        assertEquals(1, dB.getInputParam().size());
        assertTrue(dB.getOutputParam().isEmpty());
        checkInputParameter("y", "boolean", dB);
    }


}
