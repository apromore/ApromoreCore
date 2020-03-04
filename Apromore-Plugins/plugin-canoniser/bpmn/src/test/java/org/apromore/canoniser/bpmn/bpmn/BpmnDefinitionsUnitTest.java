/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2018, 2020 The University of Melbourne.
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

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.TestConstants;
import org.apromore.canoniser.bpmn.anf.AnfAnnotationsType;
import org.apromore.canoniser.bpmn.cpf.CpfIDResolver;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfUnmarshallerListener;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TSubProcess;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.dd._20100524.di.Plane;

/**
 * Test suite for {@link BpmnDefinitions}.
 * These are decanonization tests, exercising the constructor {@link BpmnDefinitions#(CanonicalProcessType, AnnotationsType)}.
 * Canonization is tested instead by {@link CpfCanonicalProcessTypeTest}.
 *
 * A number of these tests are from <cite>Canonization Service for AProMoRe</cite>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.3
 * @see <a href="http://apromore.org/wp-content/uploads/2010/12/AProMoReCanonization_v1.0.pdf">Canonization
 *     Service for AProMoRe</a>, page 24-25
 */
public class BpmnDefinitionsUnitTest implements TestConstants {

    /**
     * Common code for decanonisation tests.
     *
     * This method validates the CPF and ANF source files, decanonises them to produce a BPMN file, writes it out and validates it.
     *
     * @param filename  the filename for the input CPF and ANF files and for the output BPMN file,
     *     minus the respective <code>.cpf</code>, <code>.anf</code> and <code>.bpmn.xml</code> file extensions
     * @return the decanonised BPMN model
     */
    private BpmnDefinitions testDecanonise(final String filename) throws CanoniserException, FileNotFoundException, JAXBException, SAXException, TransformerException {

        // Read the CPF source file
        CpfCanonicalProcessType cpf =
            CpfCanonicalProcessType.newInstance(new FileInputStream(new File(CANONICAL_MODELS_DIR, filename + ".cpf")), true);

        AnnotationsType anf = null;
        File anfFile = new File(CANONICAL_MODELS_DIR, filename + ".anf");
        if (anfFile.exists()) {
            // Read the ANF source file
            anf = AnfAnnotationsType.newInstance(new FileInputStream(new File(CANONICAL_MODELS_DIR, filename + ".anf")), true);
        }

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(cpf, anf);

        // Serialize the test instance for offline inspection
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf+anf.bpmn")), false);

        // Validate the test instance
        definitions.marshal(new NullOutputStream(), true);

        // Round-trip the test instance from BPMN back to the original CPF
        CpfCanonicalProcessType cpf2 = new CpfCanonicalProcessType(definitions);
        cpf2.setUri("dummy");
        cpf2.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf+anf.bpmn.cpf")), false);
        cpf2.marshal(new NullOutputStream(), true);

        if (anfFile.exists()) {
            assertEquals(1, definitions.getBPMNDiagram().size());

            // Confirm constraints that can't be expressed in the CPF or ANF schemas
            assertEquals(cpf.getUri(), anf.getUri());

            AnfAnnotationsType anf2 = new AnfAnnotationsType(definitions, definitions.getBPMNDiagram().get(0));
            anf2.setUri("dummy");
            anf2.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf+anf.bpmn.anf")), false);
            anf2.marshal(new NullOutputStream(), true);
        }

        return definitions;
    }

    /**
     * Test parsing of <code>Case 2.bpmn</code> via {@link BpmnDefinitions#newInstance}.
     *
     * Looking primarily to see that the lists of incoming and outgoing sequence flows on the flow nodes are correct, as
     * as the gateway direction on the XOR gateway.
     */
    @Test
    public final void testNewInstance() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(BPMN_MODELS_DIR, "Case 2.bpmn")), true);

        // TODO - Inspect the test instance

        // Serialize the test instance
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "Case 2.bpmn")), true);
    }

    /**
     * Test parsing of <code>Test1.bpmn</code>.
     */
    @Test
    public final void testNewInstance2() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(BPMN_MODELS_DIR, "Test1.bpmn")), false);

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());
        assertTrue(definitions.getRootElement().get(0).getValue() instanceof TProcess);
        assertEquals(10, ((TProcess) definitions.getRootElement().get(0).getValue()).getFlowElement().size());

        assertNotNull(definitions.getBPMNDiagram());
        assertEquals(1, definitions.getBPMNDiagram().size());
        assertEquals("sid-db4fcdfb-67a0-4ef0-9a45-3167bfd77e4f", definitions.getBPMNDiagram().get(0).getId());
        assertNotNull(definitions.getBPMNDiagram().get(0).getBPMNPlane());
        assertEquals("sid-69a9f6ba-9421-44ee-a6fb-f50fc5e881e4", definitions.getBPMNDiagram().get(0).getBPMNPlane().getId());
        assertEquals(new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", "sid-68aefed9-f32a-4503-895c-b26b0ee8dded"),
                     definitions.getBPMNDiagram().get(0).getBPMNPlane().getBpmnElement());
        assertNotNull(definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement());
        assertEquals(10, definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().size());
    }

    /**
     * Test parsing of <code>Request_For_Advance_Payment.bpmn</code>.
     */
    @Test
    public final void testNewInstance3() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions =
            BpmnDefinitions.newInstance(new FileInputStream(new File(BPMN_MODELS_DIR, "Request_For_Advance_Payment.bpmn")), true);

        // TODO - Inspect the test instance

        // Serialize the test instance
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "Request_For_Advance_Payment.bpmn")), true);
    }

    @Test
    public final void testNewInstance4() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(BPMN_MODELS_DIR, "ch9_loan5.bpmn")), true);

        // TODO - Inspect the test instance
        TDataOutputAssociation doa = (TDataOutputAssociation) definitions.findElementById("sid-9413B494-364E-4F60-9C85-F313479C96F0");
        assertNotNull(doa);
        assertNotNull(doa.getSourceRef());
        //assertNotNull(doa.getTargetRef());

        // Serialize the test instance
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "ch9_loan5.bpmn")), true);
    }

    @Test
    public final void testNewInstance5() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions =
            BpmnDefinitions.newInstance(new FileInputStream(new File(BPMN_MODELS_DIR, "ch9_PurchaseOrder4Complete.bpmn")), true);

        // TODO - Inspect the test instance

        // Serialize the test instance
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "ch9_PurchaseOrder4Complete.bpmn")), true);
    }

    /**
     * Test decanonisation of <code>Basic.cpf</code> and <code>Basic.anf</code>.
     */
    @Test
    public final void testDecanoniseBasic() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("Basic");

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());

        // Process c6
        assertEquals(BpmnProcess.class, definitions.getRootElement().get(0).getValue().getClass());
        TProcess c6 = (TProcess) definitions.getRootElement().get(0).getValue();
        assertEquals("c6", c6.getId());

        // Expect 5 flow elements
        assertEquals(5, c6.getFlowElement().size());

        // Start event c1
        TStartEvent c1 = (TStartEvent) c6.getFlowElement().get(2).getValue();
        assertEquals("c1", c1.getId());

        // Task c2
        TTask c2 = (TTask) c6.getFlowElement().get(3).getValue();
        assertEquals("c2", c2.getId());

        // End event c1
        TEndEvent c3 = (TEndEvent) c6.getFlowElement().get(4).getValue();
        assertEquals("c3", c3.getId());

        // Sequence flow c4
        TSequenceFlow c4 = (TSequenceFlow) c6.getFlowElement().get(0).getValue();
        assertEquals("c4", c4.getId());
        assertEquals(c1, c4.getSourceRef());
        assertEquals(c2, c4.getTargetRef());

        // Sequence flow c5
        TSequenceFlow c5 = (TSequenceFlow) c6.getFlowElement().get(1).getValue();
        assertEquals("c5", c5.getId());
        assertEquals(c2, c5.getSourceRef());
        assertEquals(c3, c5.getTargetRef());

        // A single diagram, since 1 ANF file was passed in
        assertEquals(1, definitions.getBPMNDiagram().size());
    }

    /**
     * Test decanonisation of <code>Basic-without-anf.cpf</code>.
     */
    @Test
    public final void testDecanoniseBasicWithoutANF() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("Basic-without-anf");

        // No diagram, since no ANF file was passed in
        assertEquals(0, definitions.getBPMNDiagram().size());
    }

    /**
     * Test decanonisation of <code>Extension.cpf</code> and <code>Extension.anf</code>.
     */
    @Test
    public final void testDecanoniseExtension() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("Extension");

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());

        // Process c6
        assertEquals(BpmnProcess.class, definitions.getRootElement().get(0).getValue().getClass());
        TProcess c6 = (TProcess) definitions.getRootElement().get(0).getValue();
        assertEquals("c6", c6.getId());

        // Expect 5 flow elements
        assertEquals(5, c6.getFlowElement().size());

        // Start event c1
        TStartEvent c1 = (TStartEvent) c6.getFlowElement().get(2).getValue();
        assertEquals("c1", c1.getId());
        assertNull(c1.getExtensionElements());

        // Task c2
        TTask c2 = (TTask) c6.getFlowElement().get(3).getValue();
        assertEquals("c2", c2.getId());
        assertNotNull(c2.getExtensionElements());
        List anys = (List) c2.getExtensionElements().getAny();
        assertEquals(1, anys.size());
        Element element = (Element) anys.get(0);

        // End event c1
        TEndEvent c3 = (TEndEvent) c6.getFlowElement().get(4).getValue();
        assertEquals("c3", c3.getId());
        assertNull(c3.getExtensionElements());

        // Sequence flow c4
        TSequenceFlow c4 = (TSequenceFlow) c6.getFlowElement().get(0).getValue();
        assertEquals("c4", c4.getId());
        assertEquals(c1, c4.getSourceRef());
        assertEquals(c2, c4.getTargetRef());

        // Sequence flow c5
        TSequenceFlow c5 = (TSequenceFlow) c6.getFlowElement().get(1).getValue();
        assertEquals("c5", c5.getId());
        assertEquals(c2, c5.getSourceRef());
        assertEquals(c3, c5.getTargetRef());
    }

    /**
     * Test decanonization to <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Expected 1.bpmn">expectation #1</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Expected 1.svg"/></div>
     */
    @Test
    public final void testDecanonise1() {
        // not yet implemented
    }

    /**
     * Test decanonisation of <code>Pool.cpf</code> and <code>Pool.anf</code>.
     */
    @Test
    public final void testDecanonisePool() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("Pool");
    }

    /**
     * Test decanonisation of <code>TwoLanes.cpf</code> and <code>TwoLanes.anf</code>.
     */
    @Test
    public final void testDecanoniseTwoLanes() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("TwoLanes");
    }

    /**
     * Test decanonisation of <code>TwoPools.cpf</code> and <code>TwoPools.anf</code>.
     */
    @Test
    public final void testDecanoniseTwoPools() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("TwoPools");
    }

    /**
     * Test decanonisation of <code>Subprocess.cpf</code> and <code>Subprocess.and</code>.
     */
    @Test
    public final void testDecanoniseSubprocess() throws Exception {
        
        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("Subprocess");

        // Process c6
        assertEquals(BpmnProcess.class, definitions.getRootElement().get(0).getValue().getClass());
        TProcess c6 = (TProcess) definitions.getRootElement().get(0).getValue();
        assertEquals("c6", c6.getId());

        // Expect 5 flow elements
        assertEquals(5, c6.getFlowElement().size());

        // Start event c1
        TStartEvent c1 = (TStartEvent) c6.getFlowElement().get(2).getValue();
        assertEquals("c1", c1.getId());

        // SubProcess c2
        TSubProcess c2 = (TSubProcess) c6.getFlowElement().get(3).getValue();
        assertEquals("c2", c2.getId());
        assertEquals(5, c2.getFlowElement().size());
    }

    /**
     * Test decanonisation of <code>PaymentSubnet.cpf</code>.
     */
    @Test
    public final void testDecanonisePaymentSubnet() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("PaymentSubnet.yawl");
    }

	    /**
     * Test decanonisation of <code>PaymentSubnet.cpf</code>.
     */
    @Test
    public final void testDecanonisePaymentSubnetWithoutLabel() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("PaymentSubnetWithoutLayout.yawl");
    }
	
    /**
     * Test decanonisation of <code>OrderFulfillment.cpf</code>.
     */
    @Test
    public final void testDecanoniseOrderFulfillment() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("OrderFulfillment");
    }

    /**
     * Test decanonisation of <code>DataInput.cpf</code>.
     */
    @Test
    public final void testDecanoniseDataInput() throws Exception {
        
        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("DataInput");

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());

        // Process
        assertEquals(BpmnProcess.class, definitions.getRootElement().get(0).getValue().getClass());
        TProcess process = (TProcess) definitions.getRootElement().get(0).getValue();
        assertEquals("process", process.getId());
        assertEquals(6, process.getFlowElement().size());
        TTask task = ((JAXBElement<TTask>) process.getFlowElement().get(3)).getValue();

        // Find the ioSpecification/dataInput
        assertNotNull(task.getIoSpecification());
        assertNotNull(task.getIoSpecification().getDataInput());
        assertEquals(1, task.getIoSpecification().getDataInput().size());

        // Find the dataInputAssociation/targetRef
        assertNotNull(task.getDataInputAssociation());
        assertEquals(1, task.getDataInputAssociation().size());
        assertNotNull(task.getDataInputAssociation().get(0).getTargetRef());

        // Show that they match
        assertEquals(task.getIoSpecification().getDataInput().get(0), task.getDataInputAssociation().get(0).getTargetRef());
    }

    /**
     * Test decanonisation of <code>DataOutput.cpf</code>.
     */
    @Test
    public final void testDecanoniseDataOutput() throws Exception {
        
        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("DataOutput");

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());

        // Process
        assertEquals(BpmnProcess.class, definitions.getRootElement().get(0).getValue().getClass());
        TProcess process = (TProcess) definitions.getRootElement().get(0).getValue();
        assertEquals("process", process.getId());
        assertEquals(6, process.getFlowElement().size());
        TTask task = ((JAXBElement<TTask>) process.getFlowElement().get(3)).getValue();

        // Find the ioSpecification/dataOutput
        assertNotNull(task.getIoSpecification());
        assertNotNull(task.getIoSpecification().getDataOutput());
        assertEquals(1, task.getIoSpecification().getDataOutput().size());

        // Find the dataOutputAssociation/sourceRef
        assertNotNull(task.getDataOutputAssociation());
        assertEquals(1, task.getDataOutputAssociation().size());
        assertNotNull(task.getDataOutputAssociation().get(0).getSourceRef());
        assertEquals(1, task.getDataOutputAssociation().get(0).getSourceRef().size());

        // Show that they match
        assertEquals(task.getIoSpecification().getDataOutput().get(0), task.getDataOutputAssociation().get(0).getSourceRef().get(0).getValue());
    }

    /**
     * Test {@link BpmnDefinitions#rewriteImplicitGatewaysExplicitly} against
     * <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ImplicitJoin.bpmn">ImplicitJoin.bpmn</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/ImplicitJoin.svg"/></div>
     */
    @Test
    public final void testRewriteImplicitJoin() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(BPMN_MODELS_DIR, "ImplicitJoin.bpmn")), true);

        BpmnProcess process = (BpmnProcess) definitions.findElement(new QName(definitions.getTargetNamespace(), "sid-8f7af173-d650-4513-9abf-c5874363bff5"));
        assertEquals(7, process.getFlowElement().size());

        BpmnTask task = (BpmnTask) definitions.findElement(new QName(definitions.getTargetNamespace(), "sid-DD6A3F22-DDB8-4395-ACF3-FB933393BA7A"));
        assertEquals(2, task.getIncoming().size());

        // Exercise the rewriting method
        definitions.rewriteImplicitGatewaysExplicitly();

        // Validate the rewritten model, and write it out for inspection
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "ImplicitJoin-rewritten.bpmn")), false);
        definitions.marshal(new NullOutputStream(), true);

        // Confirm that the rewriting took place
        assertEquals(9, process.getFlowElement().size());
        assertEquals(1, task.getIncoming().size());
    }

    /**
     * Test {@link BpmnDefinitions#rewriteImplicitGatewaysExplicitly} against
     * <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ImplicitSplit.bpmn">ImplicitSplit.bpmn</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/ImplicitSplit.svg"/></div>
     */
    @Test
    public final void testRewriteImplicitSplit() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(BPMN_MODELS_DIR, "ImplicitSplit.bpmn")), true);

        BpmnProcess process = (BpmnProcess) definitions.findElement(new QName(definitions.getTargetNamespace(), "sid-ebf93c68-5e38-44d0-a9be-10befb2f9160"));
        assertEquals(7, process.getFlowElement().size());

        BpmnTask task = (BpmnTask) definitions.findElement(new QName(definitions.getTargetNamespace(), "sid-B8464973-138F-4E6A-8880-AC5664D2E417"));
        assertEquals(2, task.getOutgoing().size());

        // Exercise the rewriting method
        definitions.rewriteImplicitGatewaysExplicitly();

        // Validate the rewritten model, and write it out for inspection
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "ImplicitSplit-rewritten.bpmn")), false);
        definitions.marshal(new NullOutputStream(), true);

        // Confirm that the rewriting took place
        assertEquals(9, process.getFlowElement().size());
        assertEquals(1, task.getOutgoing().size());
    }
}
