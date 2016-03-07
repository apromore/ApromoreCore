/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class TaskDataUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPD1TaskData.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPD1TaskData.yawl.anf");
    }

    @Test
    public void testNetVariables() {
        NetFactsType net = findRootNet();
        checkInputParameter("n1", "boolean", net);
        checkOutputParameter("n2", "string", net);
        checkInputParameter("n3", "byte", net);
        checkOutputParameter("n3", "byte", net);
        checkLocalVariable("n4", "string", net);
        checkLocalVariable("n5", "YDocumentType", net);
    }

    @Test
    public void testTaskMappings() {
        ExternalTaskFactsType taskA = findTaskByName("A", findRootNet());
        assertNotNull(taskA);

        checkInputMapping("t1", "<t1>{/N-Net/n1/text()}</t1>", taskA);
        checkInputMapping("t2", "<t2>{/N-Net/n1/text()}</t2>", taskA);
        checkInputMapping("t3", "<t3>{/N-Net/n3/text()}</t3>", taskA);

        checkOutputMapping("n3", "<n3>{/C-A/t3/text()}</n3>", taskA);
        checkOutputMapping("n2", "<n2>{/C-A/t3/text()}</n2>", taskA);
        checkOutputMapping("n4", "<n4>{/C-A/t4/text()}</n4>", taskA);
    }


}
