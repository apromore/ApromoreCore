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

package org.apromore.canoniser.yawl.cpf2yawl.patterns.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class RoundRobinAllocationUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPR16RoundRobinAllocation.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPR16RoundRobinAllocation.yawl.anf");
    }

    @Test
    public void testAllocationStrategy() {
        NetFactsType net = findRootNet();

        final ExternalTaskFactsType a = findTaskByName("A", net);
        final ExternalTaskFactsType b = findTaskByName("B", net);
        final ExternalTaskFactsType c = findTaskByName("C", net);

        assertNotNull(a.getResourcing());
        assertNotNull(b.getResourcing());
        assertNotNull(c.getResourcing());

        assertNotNull(a.getResourcing().getAllocate());
        assertNotNull(b.getResourcing().getAllocate());
        assertNotNull(c.getResourcing().getAllocate());

        assertNotNull(a.getResourcing().getAllocate().getAllocator());
        assertNotNull(b.getResourcing().getAllocate().getAllocator());
        assertNotNull(c.getResourcing().getAllocate().getAllocator());

        assertEquals("RoundRobinByTime", a.getResourcing().getAllocate().getAllocator().getName());
        assertEquals("RoundRobinByLeastFrequency", b.getResourcing().getAllocate().getAllocator().getName());
        assertEquals("RoundRobinByExperience", c.getResourcing().getAllocate().getAllocator().getName());
    }
}
