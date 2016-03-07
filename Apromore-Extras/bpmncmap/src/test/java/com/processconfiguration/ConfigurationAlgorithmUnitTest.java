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

package com.processconfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

import com.sun.xml.bind.IDResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/*
import de.hpi.bpmn2_0.dto.BaseElement;
import de.hpi.bpmn2_0.dto.Definitions;
import de.hpi.bpmn2_0.dto.FlowElement;
import de.hpi.bpmn2_0.dto.Process;
import de.hpi.bpmn2_0.dto.activity.Task;
import de.hpi.bpmn2_0.dto.artifacts.TextAnnotation;
import de.hpi.bpmn2_0.dto.connector.Association;
import de.hpi.bpmn2_0.dto.connector.Edge;
import de.hpi.bpmn2_0.dto.connector.SequenceFlow;
import de.hpi.bpmn2_0.dto.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.dto.event.EndEvent;
import de.hpi.bpmn2_0.dto.event.StartEvent;
import de.hpi.bpmn2_0.dto.extension.synergia.Configurable;
import de.hpi.bpmn2_0.dto.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.dto.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.dto.gateway.Gateway;
import de.hpi.bpmn2_0.dto.gateway.InclusiveGateway;
*/
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.bpmn.BpmnObjectFactory;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.di.*;
import org.omg.spec.bpmn._20100524.model.*;

/**
 * Test suite for the {@link ConfigurationAlgorithm}.
 *
 * This must be configured using the <code>tests.dir</code> property to indicate the
 * project <code>tests/</code> directory where the test data files can be found.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class ConfigurationAlgorithmUnitTest {

    /** Logger.  Named after the class. */
    private static final Logger logger = Logger.getLogger(ConfigurationAlgorithmUnitTest.class.getCanonicalName());

    /**
     * Test data directory.
     *
     * Initialized from the <code>tests.dir</code> system property.
     */
    public static final File testsDirectory = new File("src/test/resources/");

    /**
     * Where to dump logging output from tests.
     */
    public static final File OUTPUT_DIRECTORY = new File("target/");

    /**
     * A <a href="{@docRoot}/../tests/data/Test1.bpmn20.xml">test document</a> used in the test suite.
     *
     * <div align="center"><img src="{@docRoot}/svg/Test1.signavio.svg"/></div>
     */
    public static final File test1File = new File(testsDirectory, "Test1.bpmn20.xml");

    /**
     * A <a href="{@docRoot}/../tests/data/TrivialGateway.bpmn20.xml">test document</a> used in the test suite.
     *
     * <div align="center"><img src="{@docRoot}/svg/TrivialGateway.signavio.svg"/></div>
     */
    public static final File trivialGatewayFile = new File(testsDirectory, "TrivialGateway.bpmn20.xml");

    /**
     * A <a href="{@docRoot}/../tests/data/1 Terminal Entry.bpmn">test document</a> used in the test suite.
     */
    public static final File terminalEntryFile = new File(testsDirectory, "1 Terminal entry.bpmn");

    /**
     * A <a href="{@docRoot}/../tests/data/Case 12.bpmn">test document</a> used in the test suite.
     */
    public static final File case12File = new File(testsDirectory, "Case 12.bpmn");

    /** Factory for JAXB marshallers and unmarshallers. */
    private final JAXBContext context;

    /**
     * @throws JAXBException if {@link #context} cannot be initialized
     */
    public ConfigurationAlgorithmUnitTest() throws JAXBException {

        this.context = JAXBContext.newInstance(TDefinitions.class,
                                               ConfigurationAnnotationAssociation.class,
                                               ConfigurationAnnotationShape.class);
    }

    private void assertValidBPMN(BpmnDefinitions definitions, String filename) throws FileNotFoundException, JAXBException {
	definitions.marshal(new FileOutputStream(new File(OUTPUT_DIRECTORY, filename)), true);
    }

    //
    // Tests
    //

    /**
     * Test the {@link ConfigurationAlgorithm#configure} method on the {@link #test1File}.
     *
     * The expected behavior is for the gateway to be trivialized by the loss of its southbound sequence flow,
     * and the <q>Boeing</q> task to be removed, as follows:
     *
     * <div align="center"><img src="{@docRoot}/svg/Test1-configured.signavio.svg"/></div>
     *
     * @throws IOException if the test document can't be read
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test public final void testConfigure1() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(test1File), true);

        // Exercise the method
        ConfigurationAlgorithm.configure(definitions);

        // Validate the configured BPMN document
        assertValidBPMN(definitions, "test-configure1.bpmn.xml");
    }

    /**
     * Test the {@link ConfigurationAlgorithm#configure} method on the {@link #trivialGatewayFile}.
     *
     * The expected behavior is for the <q>Tuesday</q> and <q>Thursday</q> text annotations to both
     * end up associated with the combined sequence flow, once the gateway has been removed, as follows:
     *
     * <div align="center"><img src="{@docRoot}/svg/TrivialGateway-configured.signavio.svg"/></div>
     *
     * @throws IOException if the test document can't be read
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test public final void testConfigure2() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(trivialGatewayFile), true);

        // Exercise the method
        ConfigurationAlgorithm.configure(definitions);

        // Validate the configured BPMN document
        assertValidBPMN(definitions, "test-configure2.bpmn.xml");
    }

    /**
     * Test the {@link ConfigurationAlgorithm#configure} method on the {@link #terminalEntryFile}.
     *
     * @throws IOException if the test document can't be read
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test public final void testConfigure3() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(terminalEntryFile), true);

        // Exercise the method
        ConfigurationAlgorithm.configure(definitions);

        // Validate the configured BPMN document
        assertValidBPMN(definitions, "test-configure3.bpmn.xml");
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findConfiguredGateways} method.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test public final void testFindAbsentSequenceFlows1() throws FileNotFoundException, JAXBException {

        // Obtain the test set
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(test1File), true);

        Set<TGateway> configuredGatewaySet = ConfigurationAlgorithm.findConfiguredGateways(definitions);
        assertEquals(1, configuredGatewaySet.size());

        // Exercise the method
        Set<TSequenceFlow> absentFlowSet = ConfigurationAlgorithm.findAbsentSequenceFlows(definitions, configuredGatewaySet);

        // Examine the output
        assertEquals(1, absentFlowSet.size());
        Iterator<TSequenceFlow> i = absentFlowSet.iterator();

        assertTrue(i.hasNext());
        TSequenceFlow absentFlow = i.next();
        assertEquals("sid-6654E6A6-8036-4866-B8A8-FEA9658DB28B", absentFlow.getId());

        assertFalse(i.hasNext());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findConfiguredGateways} method.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test public final void testFindConfiguredGateways1() throws FileNotFoundException, JAXBException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(test1File), true);

        // Exercise the method
        Set<TGateway> configuredGateways = ConfigurationAlgorithm.findConfiguredGateways(definitions);

        // Examine the output
        assertEquals(configuredGateways.size(), 1);
        Iterator<TGateway> i = configuredGateways.iterator();

        assertTrue(i.hasNext());
        TGateway gateway = i.next();
        assertEquals("sid-B044A443-736E-495B-9DD8-90EB4860F9AC", gateway.getId());

        assertFalse(i.hasNext());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findConfiguredGateways} method.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test public final void testFindConfiguredGateways2() throws FileNotFoundException, JAXBException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(trivialGatewayFile), true);

        // Exercise the method
        Set<TGateway> configuredGateways = ConfigurationAlgorithm.findConfiguredGateways(definitions);

        // Examine the output
        assertTrue(configuredGateways.isEmpty());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findOrphans} method.
     *
     * On the unmodified {@link #test1File}, no orphans should be found.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test public final void testFindOrphans1() throws FileNotFoundException, JAXBException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(test1File), true);

        // Exercise the method and examine the output -- the test document oughtn't to have any
        assertTrue(ConfigurationAlgorithm.findOrphans(definitions).isEmpty());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findOrphans} method.
     *
     * On the unmodified {@link #trivialGatewayFile}, no orphans should be found.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test public final void testFindOrphans2() throws FileNotFoundException, JAXBException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(trivialGatewayFile), true);

        // Exercise the method and examine the output -- the test document oughtn't to have any
        assertTrue(ConfigurationAlgorithm.findOrphans(definitions).isEmpty());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findOrphans} method.
     *
     * Removes the incoming flow to the Airbus task, which should leave it and its outgoing flow as orphans.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test public final void testFindOrphans3() throws FileNotFoundException, JAXBException {

        // Sequence position of the sequence flow that is deleted by this test
        final int deletedFlowIndex = 7;

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(test1File), true);

        // Pluck one of the sequence flows out of the test document
        TSequenceFlow removedFlow = (TSequenceFlow) ((TProcess) definitions.getRootElement().get(0).getValue()).getFlowElement().get(deletedFlowIndex).getValue();
        assertEquals("sid-B6C60809-2232-4F2B-B290-A4639AD05BCD", removedFlow.getId());
        ConfigurationAlgorithm.prune(definitions, Collections.singleton((TBaseElement) removedFlow));

        // Exercise the method
        Set<TBaseElement> orphans = ConfigurationAlgorithm.findOrphans(definitions);

        // Examine the output, expecting two orphans
        assertNotNull(orphans);
        assertEquals(2, orphans.size());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findOrphans} method.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test public final void testFindOrphans4() throws FileNotFoundException, JAXBException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(terminalEntryFile), true);

        // Exercise the method and examine the output -- the test document oughtn't to have any
        Set<TBaseElement> orphans = ConfigurationAlgorithm.findOrphans(definitions);
        for (TBaseElement orphan: orphans) {
            System.err.println( "  " + orphan.getId() + " " + orphan);
        }
        assertTrue("Unexpected orphan elements: " + orphans, orphans.isEmpty());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findOrphans} method.
     *
     * This particularly tests that activities are traversable through their boundary events.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test public final void testFindOrphans5() throws FileNotFoundException, JAXBException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(case12File), true);

        // Exercise the method and examine the output -- the test document oughtn't to have any
        Set<TBaseElement> orphans = ConfigurationAlgorithm.findOrphans(definitions);
        for (TBaseElement orphan: orphans) {
            System.err.println( "  " + orphan.getId() + " " + orphan);
        }
        assertTrue("Unexpected orphan elements: " + orphans, orphans.isEmpty());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#prune} method.
     *
     * @throws IOException if the schema can't be read, or the document can't be written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test public final void testPrune1() throws IOException, JAXBException, SAXException, TransformerException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(test1File), true);

        ConfigurationAlgorithm.prune(definitions, java.util.Collections.EMPTY_SET);
        definitions = definitions.correctFlowNodeRefs(definitions, new BpmnObjectFactory());
        assertValidBPMN(definitions, "test-prune1.bpmn.xml");
    }

    /**
     * Test the {@link ConfigurationAlgorithm#prune} method.
     *
     * @throws IOException if the schema can't be read, or the document can't be written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test public final void testPrune2() throws IOException, JAXBException, SAXException, TransformerException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(test1File), true);

        // Get the elements of the test document that we expect to see modified
        TProcess process = (TProcess) definitions.getRootElement().get(0).getValue();
        TTask airbus = (TTask) process.getFlowElement().get(0).getValue();

        // Exercise the method
        Set<TBaseElement> pruningSet = new HashSet<TBaseElement>();
        pruningSet.add(airbus);
        ConfigurationAlgorithm.prune(definitions, pruningSet);
        definitions = definitions.correctFlowNodeRefs(definitions, new BpmnObjectFactory());
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIRECTORY, "prune2.bpmn")), false);

        // Inspect the mutated definitions
        //assertValidBPMN(definitions, "test-prune2.bpmn.xml");  // pruning introduces unsourced/untargeted flows
        assertTrue(airbus != process.getFlowElement().get(0).getValue());
    }

    /**
     * Meta-test that the {@link #trivialGatewayFile} has the expected structure.
     *
     * @throws IOException if the test document can't be read or the output document written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test public final void testTrivialGatewayFile1() throws CanoniserException, IOException, JAXBException, SAXException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(trivialGatewayFile), true);

        // Inspect the test document
        assertValidBPMN(definitions, "test-TrivialGatewayFile1.bpmn.xml");
        assertEquals(1, definitions.getRootElement().size());
        TProcess process = (TProcess) definitions.getRootElement().get(0).getValue();

        TSequenceFlow tuesday = (TSequenceFlow) process.getFlowElement().get(4).getValue();
        assertEquals("sid-C354ED3F-6C0A-4A04-B25B-AA5A4B63227B", tuesday.getId());

        TTextAnnotation tuesdayText = (TTextAnnotation) process.getArtifact().get(7).getValue();
        assertEquals("sid-C9BB116E-3513-42B8-B53D-6DE8FF1F597B", tuesdayText.getId());

        TAssociation tuesdayAssoc = (TAssociation) process.getArtifact().get(0).getValue();
        assertEquals("sid-DF727F05-4C45-40C3-B4C8-9EECBFAE95AB", tuesdayAssoc.getId());
        assertEquals(tuesdayText, definitions.findElement(tuesdayAssoc.getSourceRef()));
        assertEquals(tuesday, definitions.findElement(tuesdayAssoc.getTargetRef()));

	/* TODO - reinstate
        assertEquals(1, tuesday.getIncoming().size());
        assertEquals(tuesdayAssoc, tuesday.getIncoming().get(0));
        assertEquals(0, tuesday.getOutgoing().size());

        assertEquals(0, tuesdayText.getIncoming().size());
        assertEquals(1, tuesdayText.getOutgoing().size());
        assertEquals(tuesdayAssoc, tuesdayText.getOutgoing().get(0));
	*/
    }

    /**
     * Test the {@link ConfigurationAlgorithm#removeTrivialGateway} method.
     *
     * @throws IOException if the test document can't be read or the output document written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test public final void testRemoveTrivialGateway1() throws CanoniserException, IOException, JAXBException, SAXException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(trivialGatewayFile), true);

        // Get references to elements within the test document
        TProcess process = (TProcess) definitions.getRootElement().get(0).getValue();
        TStartEvent monday = (TStartEvent) process.getFlowElement().get(0).getValue();
        assertEquals("sid-3A72224F-859B-4BA6-BAF8-8BE8F038EDCA", monday.getId());
        TSequenceFlow tuesday = (TSequenceFlow) process.getFlowElement().get(4).getValue();
        assertEquals("sid-C354ED3F-6C0A-4A04-B25B-AA5A4B63227B", tuesday.getId());
        TGateway wednesday = (TGateway) process.getFlowElement().get(1).getValue();
        assertEquals("sid-6A5EAA84-D4F8-4E3B-B5A0-EE93A65D0F10", wednesday.getId());
        TSequenceFlow thursday = (TSequenceFlow) process.getFlowElement().get(3).getValue();
        assertEquals("sid-F73F742A-DF4B-41D6-B0EA-10740E5718ED", thursday.getId());
        TEndEvent friday = (TEndEvent) process.getFlowElement().get(2).getValue();
        assertEquals("sid-1FA55E81-D95D-4288-B8AE-B9739C47766B", friday.getId());

        TAssociation tuesdayAssoc = (TAssociation) process.getArtifact().get(0).getValue();
        assertEquals("sid-DF727F05-4C45-40C3-B4C8-9EECBFAE95AB", tuesdayAssoc.getId());
        assertEquals(tuesday, definitions.findElement(tuesdayAssoc.getTargetRef()));

        TAssociation thursdayAssoc = (TAssociation) process.getArtifact().get(1).getValue();
        assertEquals("sid-E2325258-5152-43D4-BBF5-1F58BF541336", thursdayAssoc.getId());
        assertEquals(thursday, definitions.findElement(thursdayAssoc.getTargetRef()));

	/* TODO - reinstate
        assertEquals(1, tuesday.getIncoming().size());
        assertTrue(tuesday.getIncoming().contains(tuesdayAssoc));
	*/

        BPMNShape wednesdayGui = (BPMNShape) definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().get(1).getValue();
        assertEquals("sid-6A5EAA84-D4F8-4E3B-B5A0-EE93A65D0F10_gui", wednesdayGui.getId());
        assertSame(wednesday, definitions.findElement(wednesdayGui.getBpmnElement()));

        // Exercise the method
        ConfigurationAlgorithm.removeTrivialGateway(definitions, wednesday);

        // Inspect mutated BPMN XML document
        assertSame(process, definitions.getRootElement().get(0).getValue());
        assertSame(monday, process.getFlowElement().get(0).getValue());
        assertSame(tuesday, process.getFlowElement().get(2).getValue());
        assertSame(friday, process.getFlowElement().get(1).getValue());

        assertEquals(monday, tuesday.getSourceRef());
        assertEquals(friday, tuesday.getTargetRef());
        assertEquals(tuesday, definitions.findElement(tuesdayAssoc.getTargetRef()));
        assertEquals(tuesday, definitions.findElement(thursdayAssoc.getTargetRef()));

	/* TODO - reinstate
        assertEquals(2, tuesday.getIncoming().size());
        assertTrue(tuesday.getIncoming().contains(tuesdayAssoc));
        assertTrue(tuesday.getIncoming().contains(thursdayAssoc));
	*/

        assertNotSame(wednesdayGui, definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().get(1).getValue());

        //assertValidBPMN(definitions, "test-removeTrivialGateway1.bpmn.xml");  // orphaned associations aren't valid
    }

    /**
     * Test the {@link ConfigurationAlgorithm#replaceConfiguredGateway} method.
     *
     * @throws IOException if the test document can't be read or the output document written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test public final void testReplaceConfiguredGateway1() throws CanoniserException, IOException, JAXBException, SAXException {

        // Obtain the test document
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(test1File), true);

        // Test gateway happens to be the second child of the first process
        TGateway gateway = (TGateway) ((TProcess) definitions.getRootElement().get(0).getValue()).getFlowElement().get(1).getValue();
        assertEquals("sid-B044A443-736E-495B-9DD8-90EB4860F9AC", gateway.getId());
        List<QName> oldIncoming = gateway.getIncoming(),
                    oldOutgoing = gateway.getOutgoing();

        BPMNShape gatewayGui = (BPMNShape) definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().get(1).getValue();
        assertEquals("sid-B044A443-736E-495B-9DD8-90EB4860F9AC_gui", gatewayGui.getId());
        assertSame(gateway, definitions.findElement(gatewayGui.getBpmnElement()));

        // Exercise the method
        ConfigurationAlgorithm.replaceConfiguredGateway(definitions, gateway);
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIRECTORY, "test-replaceConfiguredGateway1.bpmn")), false);

        // Expecting a new, non-configurable inclusive gateway
        TGateway newGateway = (TGateway) ((TProcess) definitions.getRootElement().get(0).getValue()).getFlowElement().get(1).getValue();
        assertTrue(newGateway instanceof TInclusiveGateway);
        assertNotSame(gateway, newGateway);
        assertEquals(gateway.getId(), newGateway.getId());
        assertNull("Reconfigured gateway still has a <pc:configurable> extension element",
                   ConfigurationAlgorithm.getFirstExtensionElementOfType(newGateway.getExtensionElements()));

        // Test that the graph connectivity has been preserved
	/* TODO - reinstate these tests
        for (Edge edge : oldIncoming) {
            assertEquals(edge.getId() + " does not target configured gateway", newGateway, edge.getTargetRef());
        }

        for (Edge edge : oldOutgoing) {
            assertEquals(edge.getId() + " is not sourced from configured gateway", newGateway, edge.getSourceRef());
        }
	*/

        // Test that the diagram reference has also been updated
        assertSame(gatewayGui, definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().get(1).getValue());
        assertSame(newGateway, definitions.findElement(gatewayGui.getBpmnElement()));

        assertValidBPMN(definitions, "test-replaceConfiguredGateway1.bpmn.xml");
    }
}
