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
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModelCheckerTest {

    @Test
    public void testValidModel() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("d1_valid.bpmn");
        ModelCheckResult modelCheckResult = ModelChecker.MODEL_CHECKER_NO_POOLS.checkModel(diagram);
        assertTrue(modelCheckResult.isValid());
        assertTrue(modelCheckResult.getErrorCodes().isEmpty());
        assertEquals("", modelCheckResult.invalidMessage());
    }

    @Test
    public void testEmptyModel() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("empty_model.bpmn");
        ModelCheckResult modelCheckResult = ModelChecker.MODEL_CHECKER_NO_POOLS.checkModel(diagram);
        assertFalse(modelCheckResult.isValid());
        assertTrue(modelCheckResult.getErrorCodes().contains(ModelCheckResult.EMPTY_MODEL_CODE));
    }

    @Test
    public void testModelWithPool() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("model_with_pool.bpmn");
        ModelCheckResult modelCheckResult = ModelChecker.MODEL_CHECKER_ALLOW_POOLS.checkModel(diagram);
        assertTrue(modelCheckResult.isValid());
        assertTrue(modelCheckResult.getErrorCodes().isEmpty());
        assertEquals("", modelCheckResult.invalidMessage());

        modelCheckResult = ModelChecker.MODEL_CHECKER_NO_POOLS.checkModel(diagram);
        assertFalse(modelCheckResult.isValid());
        assertTrue(modelCheckResult.getErrorCodes().contains(ModelCheckResult.POOLS_NOT_SUPPORTED_CODE));
    }

    @Test
    public void testModelWithSelfLoopAndMultipleEventArcs() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("self_loop.bpmn");

        ModelCheckResult modelCheckResult = ModelChecker.MODEL_CHECKER_NO_POOLS.checkModel(diagram);
        assertFalse(modelCheckResult.isValid());

        Set<Integer> errorCodes = modelCheckResult.getErrorCodes();
        assertTrue(errorCodes.contains(ModelCheckResult.SELF_LOOP_CODE));
        assertTrue(errorCodes.contains(ModelCheckResult.TASK_MULTIPLE_OUTGOING_ARCS_CODE));
        assertTrue(errorCodes.contains(ModelCheckResult.TASK_MULTIPLE_INCOMING_ARCS_CODE));
        assertTrue(errorCodes.contains(ModelCheckResult.START_MULTIPLE_OUTGOING_ARCS_CODE));
        assertTrue(errorCodes.contains(ModelCheckResult.END_MULTIPLE_INCOMING_ARCS_CODE));
    }

    @Test
    public void testModelDisjointedNodes() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("disjointed.bpmn");
        ModelCheckResult modelCheckResult = ModelChecker.MODEL_CHECKER_NO_POOLS.checkModel(diagram);

        assertFalse(modelCheckResult.isValid());

        Set<Integer> errorCodes = modelCheckResult.getErrorCodes();
        assertTrue(errorCodes.contains(ModelCheckResult.TASK_MISSING_ARCS_CODE));
        assertTrue(errorCodes.contains(ModelCheckResult.START_NO_OUTGOING_ARC_CODE));
        assertTrue(errorCodes.contains(ModelCheckResult.END_NO_INCOMING_ARC_CODE));
    }

    @Test
    public void testModelReverseSequenceFlow() throws Exception {
        BPMNDiagram diagram = getDiagramFromFile("start_end_reversed.bpmn");
        ModelCheckResult modelCheckResult = ModelChecker.MODEL_CHECKER_NO_POOLS.checkModel(diagram);

        assertFalse(modelCheckResult.isValid());

        Set<Integer> errorCodes = modelCheckResult.getErrorCodes();
        assertTrue(errorCodes.contains(ModelCheckResult.START_INCOMING_ARCS_CODE));
        assertTrue(errorCodes.contains(ModelCheckResult.START_NO_OUTGOING_ARC_CODE));
        assertTrue(errorCodes.contains(ModelCheckResult.END_OUTGOING_ARCS_CODE));
        assertTrue(errorCodes.contains(ModelCheckResult.END_NO_INCOMING_ARC_CODE));
    }

    /**
     * Get the contents of a file as string in the following directory: src/test/data
     * @param filename the name of a file in the src/test/data directory.
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
