/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.yawl.cpf2yawl.patterns.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.ResourcingInitiatorType;

public class RoleBasedDistributionUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPR2RoleBasedDistribution.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPR2RoleBasedDistribution.yawl.anf");
    }

    @Test
    public void testDirectDistribution() {
        NetFactsType net = findRootNet();

        final ExternalTaskFactsType a = findTaskByName("A", net);
        assertNotNull(a.getResourcing());
        assertTrue(a.getResourcing().getAllocate().getInitiator().equals(ResourcingInitiatorType.USER));
        assertTrue(a.getResourcing().getStart().getInitiator().equals(ResourcingInitiatorType.USER));
        assertNotNull(a.getResourcing().getOffer().getDistributionSet());
        assertTrue(a.getResourcing().getOffer().getInitiator().equals(ResourcingInitiatorType.SYSTEM));
        assertNotNull(a.getResourcing().getOffer().getDistributionSet().getInitialSet());
        assertTrue(a.getResourcing().getOffer().getDistributionSet().getInitialSet().getParticipant().isEmpty());
        assertFalse(a.getResourcing().getOffer().getDistributionSet().getInitialSet().getRole().isEmpty());
        assertEquals("RO-2e5d9358-f443-4b24-9a5c-c46a8ef139b7", a.getResourcing().getOffer().getDistributionSet().getInitialSet().getRole().get(0));
    }



}