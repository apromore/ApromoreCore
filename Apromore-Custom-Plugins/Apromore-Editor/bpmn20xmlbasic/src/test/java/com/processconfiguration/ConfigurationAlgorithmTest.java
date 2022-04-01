/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package com.processconfiguration;

/**
 * Copyright (c) 2006
 * <p>
 * Philipp Berger, Martin Czuchra, Gero Decker, Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

import com.processconfiguration.common.Constants;
import java.io.File;
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
import org.xml.sax.SAXException;

import com.sun.xml.bind.IDResolver;

import static com.processconfiguration.common.Constants.test1File;
import static com.processconfiguration.common.Constants.testsDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.artifacts.TextAnnotation;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;

import static com.processconfiguration.DefinitionsIDResolverTest.assertValidBPMN;

/**
 * Test suite for the {@link ConfigurationAlgorithm}.
 *
 * This must be configured using the <code>tests.dir</code> property to indicate the
 * project <code>tests/</code> directory where the test data files can be found.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
class ConfigurationAlgorithmTest {

    /** Logger.  Named after the class. */
    private static final Logger logger = Logger.getLogger(ConfigurationAlgorithmTest.class.getCanonicalName());

    /**
     * A <a href="{@docRoot}/../tests/data/TrivialGateway.bpmn20.xml">test document</a> used in the test suite.
     *
     * <div align="center"><img src="{@docRoot}/svg/TrivialGateway.signavio.svg"/></div>
     */
    public static final File trivialGatewayFile =
        new File(new File(testsDirectory, "data"), "TrivialGateway.bpmn20.xml");

    /** Factory for JAXB marshallers and unmarshallers. */
    private final JAXBContext context;

    /**
     * @throws JAXBException if {@link #context} cannot be initialized
     */
    public ConfigurationAlgorithmTest() throws JAXBException {

        this.context = JAXBContext.newInstance(Definitions.class,
            ConfigurationAnnotationAssociation.class,
            ConfigurationAnnotationShape.class);
    }

    //
    // Tests
    //

    /**
     * Test the {@link ConfigurationAlgorithm#configure} method on the {@link Constants#test1File}.
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
    @Test
    final void testConfigure1() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(test1File);

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
    @Test
    final void testConfigure2() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(trivialGatewayFile);

        // Exercise the method
        ConfigurationAlgorithm.configure(definitions);

        // Validate the configured BPMN document
        assertValidBPMN(definitions, "test-configure2.bpmn.xml");
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findConfiguredGateways} method.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test
    final void testFindAbsentSequenceFlows1() throws JAXBException {

        // Obtain the test set
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(test1File);

        Set<Gateway> configuredGatewaySet = ConfigurationAlgorithm.findConfiguredGateways(definitions);
        assertEquals(1, configuredGatewaySet.size());

        // Exercise the method
        Set<SequenceFlow> absentFlowSet = ConfigurationAlgorithm.findAbsentSequenceFlows(configuredGatewaySet);

        // Examine the output
        assertEquals(1, absentFlowSet.size());
        Iterator<SequenceFlow> i = absentFlowSet.iterator();

        assertTrue(i.hasNext());
        SequenceFlow absentFlow = i.next();
        assertEquals("sid-6654E6A6-8036-4866-B8A8-FEA9658DB28B", absentFlow.getId());

        assertFalse(i.hasNext());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findConfiguredGateways} method.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test
    final void testFindConfiguredGateways1() throws JAXBException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(test1File);

        // Exercise the method
        Set<Gateway> configuredGateways = ConfigurationAlgorithm.findConfiguredGateways(definitions);

        // Examine the output
        assertEquals(configuredGateways.size(), 1);
        Iterator<Gateway> i = configuredGateways.iterator();

        assertTrue(i.hasNext());
        Gateway gateway = i.next();
        assertEquals("sid-B044A443-736E-495B-9DD8-90EB4860F9AC", gateway.getId());

        assertFalse(i.hasNext());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#findOrphans} method.
     *
     * On the unmodified {@link Constants#test1File}, no orphans should be found.
     *
     * @throws JAXBException if the test document can't be parsed
     */
    @Test
    final void testFindOrphans1() throws JAXBException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(test1File);

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
    @Test
    final void testFindOrphans2() throws JAXBException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(trivialGatewayFile);

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
    @Test
    final void testFindOrphans3() throws JAXBException {

        // Sequence position of the sequence flow that is deleted by this test
        final int deletedFlowIndex = 7;

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(test1File);

        // Pluck one of the sequence flows out of the test document
        SequenceFlow removedFlow =
            (SequenceFlow) ((Process) definitions.getRootElement().get(0)).getFlowElement().get(deletedFlowIndex);
        assertEquals("sid-B6C60809-2232-4F2B-B290-A4639AD05BCD", removedFlow.getId());
        ConfigurationAlgorithm.prune(definitions, Collections.singleton((BaseElement) removedFlow));

        // Exercise the method
        Set<FlowElement> orphans = ConfigurationAlgorithm.findOrphans(definitions);

        // Examine the output, expecting two orphans
        assertNotNull(orphans);
        assertEquals(2, orphans.size());
    }

    /**
     * Test the {@link ConfigurationAlgorithm#prune} method.
     *
     * @throws IOException if the schema can't be read, or the document can't be written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test
    final void testPrune1() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(test1File);

        ConfigurationAlgorithm.prune(definitions, java.util.Collections.EMPTY_SET);
        assertValidBPMN(definitions, "test-prune1.bpmn.xml");
    }

    /**
     * Test the {@link ConfigurationAlgorithm#prune} method.
     *
     * @throws IOException if the schema can't be read, or the document can't be written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test
    final void testPrune2() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(test1File);

        // Get the elements of the test document that we expect to see modified
        Process process = (Process) definitions.getRootElement().get(0);
        Task airbus = (Task) process.getFlowElement().get(0);

        // Exercise the method
        Set<BaseElement> pruningSet = new HashSet<BaseElement>();
        pruningSet.add(airbus);
        ConfigurationAlgorithm.prune(definitions, pruningSet);

        // Inspect the mutated definitions
        //assertValidBPMN(definitions, "test-prune2.bpmn.xml");  // pruning introduces unsourced/untargeted flows
        assertTrue(airbus != process.getFlowElement().get(0));
    }

    /**
     * Meta-test that the {@link #trivialGatewayFile} has the expected structure.
     *
     * @throws IOException if the test document can't be read or the output document written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test
    final void testTrivialGatewayFile1() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(trivialGatewayFile);

        // Inspect the test document
        assertValidBPMN(definitions, "test-TrivialGatewayFile1.bpmn.xml");
        assertEquals(1, definitions.getRootElement().size());
        Process process = (Process) definitions.getRootElement().get(0);

        SequenceFlow tuesday = (SequenceFlow) process.getFlowElement().get(4);
        assertEquals("sid-C354ED3F-6C0A-4A04-B25B-AA5A4B63227B", tuesday.getId());

        TextAnnotation tuesdayText = (TextAnnotation) process.getFlowElement().get(12);
        assertEquals("sid-C9BB116E-3513-42B8-B53D-6DE8FF1F597B", tuesdayText.getId());

        Association tuesdayAssoc = (Association) process.getFlowElement().get(5);
        assertEquals("sid-DF727F05-4C45-40C3-B4C8-9EECBFAE95AB", tuesdayAssoc.getId());
        assertEquals(tuesdayText, tuesdayAssoc.getSourceRef());
        assertEquals(tuesday, tuesdayAssoc.getTargetRef());

        assertEquals(1, tuesday.getIncoming().size());
        assertEquals(tuesdayAssoc, tuesday.getIncoming().get(0));
        assertEquals(0, tuesday.getOutgoing().size());

        assertEquals(0, tuesdayText.getIncoming().size());
        assertEquals(1, tuesdayText.getOutgoing().size());
        assertEquals(tuesdayAssoc, tuesdayText.getOutgoing().get(0));
    }

    /**
     * Test the {@link ConfigurationAlgorithm#removeTrivialGateway} method.
     *
     * @throws IOException if the test document can't be read or the output document written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test
    final void testRemoveTrivialGateway1() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(trivialGatewayFile);

        // Get references to elements within the test document
        Process process = (Process) definitions.getRootElement().get(0);
        StartEvent monday = (StartEvent) process.getFlowElement().get(0);
        assertEquals("sid-3A72224F-859B-4BA6-BAF8-8BE8F038EDCA", monday.getId());
        SequenceFlow tuesday = (SequenceFlow) process.getFlowElement().get(4);
        assertEquals("sid-C354ED3F-6C0A-4A04-B25B-AA5A4B63227B", tuesday.getId());
        Gateway wednesday = (Gateway) process.getFlowElement().get(1);
        assertEquals("sid-6A5EAA84-D4F8-4E3B-B5A0-EE93A65D0F10", wednesday.getId());
        SequenceFlow thursday = (SequenceFlow) process.getFlowElement().get(3);
        assertEquals("sid-F73F742A-DF4B-41D6-B0EA-10740E5718ED", thursday.getId());
        EndEvent friday = (EndEvent) process.getFlowElement().get(2);
        assertEquals("sid-1FA55E81-D95D-4288-B8AE-B9739C47766B", friday.getId());

        Association tuesdayAssoc = (Association) process.getFlowElement().get(5);
        assertEquals("sid-DF727F05-4C45-40C3-B4C8-9EECBFAE95AB", tuesdayAssoc.getId());
        assertEquals(tuesday, tuesdayAssoc.getTargetRef());

        Association thursdayAssoc = (Association) process.getFlowElement().get(6);
        assertEquals("sid-E2325258-5152-43D4-BBF5-1F58BF541336", thursdayAssoc.getId());
        assertEquals(thursday, thursdayAssoc.getTargetRef());

        assertEquals(1, tuesday.getIncoming().size());
        assertTrue(tuesday.getIncoming().contains(tuesdayAssoc));

        BPMNShape wednesdayGui = (BPMNShape) definitions.getDiagram().get(0).getBPMNPlane().getDiagramElement().get(1);
        assertEquals("sid-6A5EAA84-D4F8-4E3B-B5A0-EE93A65D0F10_gui", wednesdayGui.getId());
        assertSame(wednesday, wednesdayGui.getBpmnElement());

        // Exercise the method
        ConfigurationAlgorithm.removeTrivialGateway(definitions, wednesday);

        // Inspect mutated BPMN XML document
        assertSame(process, definitions.getRootElement().get(0));
        assertSame(monday, process.getFlowElement().get(0));
        assertSame(tuesday, process.getFlowElement().get(2));
        assertSame(friday, process.getFlowElement().get(1));

        assertEquals(monday, tuesday.getSourceRef());
        assertEquals(friday, tuesday.getTargetRef());
        assertEquals(tuesday, tuesdayAssoc.getTargetRef());
        assertEquals(tuesday, thursdayAssoc.getTargetRef());

        assertEquals(2, tuesday.getIncoming().size());
        assertTrue(tuesday.getIncoming().contains(tuesdayAssoc));
        assertTrue(tuesday.getIncoming().contains(thursdayAssoc));

        assertNotSame(wednesdayGui, definitions.getDiagram().get(0).getBPMNPlane().getDiagramElement().get(1));

        //assertValidBPMN(definitions, "test-removeTrivialGateway1.bpmn.xml");  // orphaned associations aren't valid
    }

    /**
     * Test the {@link ConfigurationAlgorithm#replaceConfiguredGateway} method.
     *
     * @throws IOException if the test document can't be read or the output document written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test
    final void testReplaceConfiguredGateway1() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(test1File);

        // Test gateway happens to be the second child of the first process
        Gateway gateway = (Gateway) ((Process) definitions.getRootElement().get(0)).getFlowElement().get(1);
        assertEquals("sid-B044A443-736E-495B-9DD8-90EB4860F9AC", gateway.getId());
        List<Edge> oldIncoming = gateway.getIncoming(),
            oldOutgoing = gateway.getOutgoing();

        BPMNShape gatewayGui = (BPMNShape) definitions.getDiagram().get(0).getBPMNPlane().getDiagramElement().get(1);
        assertEquals("sid-B044A443-736E-495B-9DD8-90EB4860F9AC_gui", gatewayGui.getId());
        assertSame(gateway, gatewayGui.getBpmnElement());

        // Exercise the method
        ConfigurationAlgorithm.replaceConfiguredGateway(definitions, gateway);

        // Expecting a new, non-configurable inclusive gateway
        Gateway newGateway = (Gateway) ((Process) definitions.getRootElement().get(0)).getFlowElement().get(1);
        assertTrue(newGateway instanceof InclusiveGateway);
        assertNotSame(gateway, newGateway);
        assertEquals(gateway.getId(), newGateway.getId());
        assertNull(newGateway.getExtensionElements().getFirstExtensionElementOfType(Configurable.class),
            "Reconfigured gateway still has a <pc:configurable> extension element");

        // Test that the graph connectivity has been preserved
        for (Edge edge : oldIncoming) {
            assertEquals(newGateway, edge.getTargetRef(), edge.getId() + " does not target configured gateway");
        }

        for (Edge edge : oldOutgoing) {
            assertEquals(newGateway, edge.getSourceRef(), edge.getId() + " is not sourced from configured gateway");
        }

        // Test that the diagram reference has also been updated
        assertSame(gatewayGui, definitions.getDiagram().get(0).getBPMNPlane().getDiagramElement().get(1));
        assertSame(newGateway, gatewayGui.getBpmnElement());

        assertValidBPMN(definitions, "test-replaceConfiguredGateway1.bpmn.xml");
    }
}
