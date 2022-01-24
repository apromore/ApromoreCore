/**
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.processmining.models.graphbased.directed.bpmn;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModelCheckerTest {
    private ModelChecker modelChecker = new ModelChecker();

    @Test
    public void testValidModel() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("d1_valid.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(diagram, false);
        assertTrue(modelCheckResult.isValid());
        assertEquals("", modelCheckResult.invalidMessage());
    }

    @Test
    public void testEmptyModel() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("empty_model.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(diagram, false);
        assertFalse(modelCheckResult.isValid());
        assertTrue(modelCheckResult.invalidMessage().contains("The model is empty"));
    }

    @Test
    public void testModelWithPool() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("model_with_pool.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(diagram, true);
        assertTrue(modelCheckResult.isValid());
        assertEquals("", modelCheckResult.invalidMessage());

        modelCheckResult = modelChecker.checkModel(diagram, false);
        assertFalse(modelCheckResult.isValid());
        assertTrue(modelCheckResult.invalidMessage()
                .contains("There are pools in the model. Pools are not yet supported"));
    }

    @Test
    public void testModelWithSelfLoopAndMultipleEventArcs() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("self_loop.bpmn");

        ModelCheckResult modelCheckResult = modelChecker.checkModel(diagram, false);
        assertFalse(modelCheckResult.isValid());

        String invalidMsg = modelCheckResult.invalidMessage();
        assertTrue(invalidMsg.contains("The element A has a self-loop"));
        assertTrue(invalidMsg.contains("The task A has more than one outgoing arc"));
        assertTrue(invalidMsg.contains("The task A has more than one incoming arc"));
        assertTrue(invalidMsg.contains("The Start Event has more than one outgoing arc"));
        assertTrue(invalidMsg.contains("The End Event has more than one incoming arc"));
    }

    @Test
    public void testModelDisjointedNodes() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("disjointed.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(diagram, false);

        assertFalse(modelCheckResult.isValid());

        String invalidMsg = modelCheckResult.invalidMessage();
        assertTrue(invalidMsg.contains("The task A has missing incoming or outgoing arcs"));
        assertTrue(invalidMsg.contains("The Start Event has a missing outgoing arc"));
        assertTrue(invalidMsg.contains("The End Event has a missing incoming arc"));
        assertTrue(invalidMsg.contains("The gateway Gateway_0gczbo1 has missing incoming or outgoing arcs"));
    }

    @Test
    public void testModelReverseSequenceFlow() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("start_end_reversed.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(diagram, false);

        assertFalse(modelCheckResult.isValid());

        String invalidMsg = modelCheckResult.invalidMessage();
        assertTrue(invalidMsg.contains("The Start Event has incoming arc(s)"));
        assertTrue(invalidMsg.contains("The Start Event has a missing outgoing arc"));
        assertTrue(invalidMsg.contains("The End Event has outgoing arc(s)"));
        assertTrue(invalidMsg.contains("The End Event has a missing incoming arc"));
    }

    /**
     * Get the contents of a file as string in the following directory: src/test/resources/data
     * @param filename the name of a file in the src/test/resources/data directory.
     * @return the contents of the file as a string.
     * @throws IOException
     */
    private String getFileContents(String filename) throws IOException {
        return Files.readString(Paths.get("src", "test", "data", "./" + filename),
                StandardCharsets.UTF_8);
    }

    private BPMNDiagram getDiagramFromFile(String filename) throws Exception {
        return BPMNDiagramFactory.newDiagramFromProcessText(getFileContents(filename));
    }

}
