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