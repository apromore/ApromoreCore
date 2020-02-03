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

package de.hpi.bpmn2_0.transformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

import org.json.JSONException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import static com.processconfiguration.ConfigurationAlgorithmTest.testsDirectory;
import static com.processconfiguration.DefinitionsIDResolverTest.assertValidBPMN;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Task;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;

/**
 * Test suite for {@link Diagram2BpmnConverter}.
 * 
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @see de.hpi.bpmn2_0.BPMNSerializationTest
 */
public class Diagram2BpmnConverterTest {

    /** Logger.  Named after the class. */
    final static private Logger logger = Logger.getLogger(Diagram2BpmnConverterTest.class.getCanonicalName());

    /**
     * Test the {@link Diagram2BpmnConverter#getDefinitionsFromDiagram} method.
     */
    @Test public void testGetDefinitionsFromDiagram1() throws BpmnConverterException, IOException, JAXBException, JSONException, SAXException {

        // Read the test JSON
        BufferedReader br = new BufferedReader(new FileReader(new File(new File(testsDirectory, "data"), "GetDiagramFromBpmn20_1-expected.json")));
        String bpmnJson = "";
        String line;
        while ((line = br.readLine()) != null) {
           bpmnJson += line;
        }
        BasicDiagram diagram = BasicDiagramBuilder.parseJson(bpmnJson);

        // Execute the conversion
        Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions definitions = converter.getDefinitionsFromDiagram();

        // Examine the resultant BPMN XML
        assertValidBPMN(definitions, "test-GetDefinitionsFromDiagram.bpmn.xml");
        Task airbus = (Task) ((Process) definitions.getRootElement().get(0)).getFlowElement().get(0);
        assertEquals("sid-B8EA9D11-3DF2-46E9-8498-9351EEB1C3B4", airbus.getId());
        assertEquals(1, airbus.getIncoming().size());
        assertEquals("sid-B6C60809-2232-4F2B-B290-A4639AD05BCD", airbus.getIncoming().get(0).getId());
        assertEquals(1, ((FlowNode) airbus).getIncomingSequenceFlows().size());
        assertEquals("sid-B6C60809-2232-4F2B-B290-A4639AD05BCD", ((FlowNode) airbus).getIncomingSequenceFlows().get(0).getId());
        assertEquals(1, airbus.getOutgoing().size());
        assertEquals("sid-F44D88A1-2E18-43E6-93C7-7F038AB2C2A1", airbus.getOutgoing().get(0).getId());
        assertEquals(1, ((FlowNode) airbus).getOutgoingSequenceFlows().size());
        assertEquals("sid-F44D88A1-2E18-43E6-93C7-7F038AB2C2A1", ((FlowNode) airbus).getOutgoingSequenceFlows().get(0).getId());
    }
}
