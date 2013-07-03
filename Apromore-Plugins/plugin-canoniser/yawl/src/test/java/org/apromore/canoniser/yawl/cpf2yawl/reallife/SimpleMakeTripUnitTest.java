package org.apromore.canoniser.yawl.cpf2yawl.reallife;

import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.bind.JAXBElement;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ExternalNetElementFactsType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.FlowsIntoType;
import org.yawlfoundation.yawlschema.LayoutFlowFactsType;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class SimpleMakeTripUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/SimpleMakeTripProcess.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/SimpleMakeTripProcess.yawl.anf");
    }

    @Test
    public void testEdges() {
        assertTrue(checkEdge(findTaskByName("register", findRootNet()), findTaskByName("book flight", findRootNet()), findRootNet()));
    }

    @Test
    public void testEdgeLayout() {
        checkEdgeLayout("register", "book flight", findRootNet());
    }

    private LayoutFlowFactsType checkEdgeLayout(final String sourceName, final String targetName, final NetFactsType net) {
        String sourceId = findTaskByName(sourceName, net).getId();
        String targetId = findTaskByName(targetName, net).getId();

        LayoutNetFactsType netLayout = canonical2Yawl.getYAWL().getLayout().getSpecification().get(0).getNet().get(0);

        for (JAXBElement<?> element : netLayout.getBoundsOrFrameOrViewport()) {
            if (element.getValue() instanceof LayoutFlowFactsType) {
                LayoutFlowFactsType flowLayout = (LayoutFlowFactsType) element.getValue();
                if (flowLayout.getSource().equals(sourceId) && flowLayout.getTarget().equals(targetId)) {
                    return flowLayout;
                }
            }
        }

        return null;
    }

    private boolean checkEdge(final ExternalTaskFactsType sourceTask, final ExternalTaskFactsType targetTask, final NetFactsType net) {
        for (ExternalNetElementFactsType element: net.getProcessControlElements().getTaskOrCondition()) {
            if (element.getId().equals(sourceTask.getId())) {
                for (FlowsIntoType flow: element.getFlowsInto()) {
                    if (flow.getNextElementRef().getId().equals(targetTask.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Test
    public void testRoutingConditions() {
        NetFactsType net = findRootNet();
        ExternalTaskFactsType register = checkTask(net, "register", ControlTypeCodeType.XOR, ControlTypeCodeType.OR, 3);
        checkAtLeastOneDefaultFlow(register);
        checkOnlyOneDefaultFlow(register);
        checkNoMissingPredicate(register);

        checkTask(net, "pay", ControlTypeCodeType.OR, ControlTypeCodeType.AND, 1);
    }

}
