package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

// Third party packages
import org.apache.commons.io.output.NullOutputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// Local packages
import org.apromore.canoniser.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.BpmnObjectFactory;
import org.apromore.canoniser.bpmn.TestConstants;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfEventType;

/**
 * Test suite for {@link CpfCanonicalProcessType}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfCanonicalProcessTypeTest implements TestConstants {

    // Tests

    /**
     * Common canonisation test code.
     *
     * Parses <code>filename</code> and validates the resulting CPF.
     *
     * @param filename  the source filename within the {@link #TEST_MODELS} directory
     * @return a validated CPF that's been written out for inspection
     * @throws Exception if anything goes amiss setting up the test
     */
    private CpfCanonicalProcessType testCanonise(String filename) throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(MODELS_DIR, filename)), true);

        // Validate and serialize the canonised documents to be inspected offline
        CpfCanonicalProcessType cpf = new CpfCanonicalProcessType(definitions);
        cpf.setUri("dummy");

        // Output the CPF
        cpf.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf")), false);

        // Validate the CPF
        cpf.marshal(new NullOutputStream(), true);

        // Round-trip the CPF back into BPMN
        BpmnDefinitions definitions2 = new BpmnDefinitions(cpf, null);
        definitions2 = BpmnDefinitions.correctFlowNodeRefs(definitions2, new BpmnObjectFactory());
        definitions2.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf.bpmn")), false);
        definitions2.marshal(new NullOutputStream(), true);

        return cpf;
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch9_loan5.bpmn">chapter 9 loan example</a>.
     */
    @Test
    public void testCh9Loan5() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch9_loan5.bpmn");

        // Inspect the CPF
        CpfEdgeType edge = (CpfEdgeType) cpf.getElement("sid-5C7AEE8B-C506-49B1-B8B1-A36DAC925D7B");
        assertNotNull(edge);
        assertEquals("sid-5CEDFABE-7E6F-450C-B84E-11C9917AB563", edge.getSourceId());
        assertEquals("sid-F48A9B5E-671A-42C9-82FB-7A3F231E7876", edge.getTargetId());

        CpfEventType event = (CpfEventType) cpf.getElement("sid-F48A9B5E-671A-42C9-82FB-7A3F231E7876");
        assertNotNull(event);
        assertEquals(0, event.getOutgoingEdges().size());
        assertEquals(1, event.getIncomingEdges().size());

    }
}
