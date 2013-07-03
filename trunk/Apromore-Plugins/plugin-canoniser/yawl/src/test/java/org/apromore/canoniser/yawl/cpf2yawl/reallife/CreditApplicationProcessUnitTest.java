package org.apromore.canoniser.yawl.cpf2yawl.reallife;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class CreditApplicationProcessUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/CreditApplicationProcess.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/CreditApplicationProcess.yawl.anf");
    }


    @Test
    public void testLayoutLocale() {
        assertEquals("AU", canonical2Yawl.getYAWL().getLayout().getLocale().getCountry());
        assertEquals("en", canonical2Yawl.getYAWL().getLayout().getLocale().getLanguage());
    }

    @Test
    public void testStructure() {
        NetFactsType rootNet = findRootNet();
        assertNotNull(rootNet);

        ExternalTaskFactsType receiveApplication = findTaskByName("receive application", rootNet);
        assertNotNull(receiveApplication);
        assertNotNull(findTaskByName("get more info", rootNet));
        assertNotNull(findTaskByName("check for completeness", rootNet));

        ExternalTaskFactsType completeApproval = checkTask(rootNet, "complete approval", ControlTypeCodeType.AND, ControlTypeCodeType.AND, 1);
        ExternalTaskFactsType notifyRejection = checkTask(rootNet, "notify rejection", ControlTypeCodeType.XOR, ControlTypeCodeType.AND, 1);

        ExternalNetElementType outputCondition = completeApproval.getFlowsInto().get(0).getNextElementRef();
        ExternalNetElementType outputCondition2 = notifyRejection.getFlowsInto().get(0).getNextElementRef();
        assertEquals("completeApproval and notifyRejection should flow both into the same condition", outputCondition.getId(), outputCondition2.getId());

        checkIsOutputCondition(rootNet, outputCondition.getId());
    }


    @Test
    public void testRoutingConditions() {
        NetFactsType net = findRootNet();
        ExternalTaskFactsType makeDecision = checkTask(net, "make decision", ControlTypeCodeType.XOR, ControlTypeCodeType.XOR, 2);
        checkAtLeastOneDefaultFlow(makeDecision);
        checkOnlyOneDefaultFlow(makeDecision);
        checkNoMissingPredicate(makeDecision);

        ExternalTaskFactsType checkLoanAmount = checkTask(net, "check loan amount", ControlTypeCodeType.XOR, ControlTypeCodeType.XOR, 2);
        checkAtLeastOneDefaultFlow(checkLoanAmount);
        checkOnlyOneDefaultFlow(checkLoanAmount);
        checkNoMissingPredicate(checkLoanAmount);

        ExternalTaskFactsType startApproval = checkTask(net, "start approval", ControlTypeCodeType.XOR, ControlTypeCodeType.AND, 2);
        //No default flow for AND splits
        checkNoDefaultFlow(startApproval);
    }

}
