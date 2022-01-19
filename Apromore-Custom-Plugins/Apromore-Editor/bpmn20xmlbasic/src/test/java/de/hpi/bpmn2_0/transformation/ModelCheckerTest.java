package de.hpi.bpmn2_0.transformation;


import org.junit.Test;

import javax.xml.bind.JAXBException;
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
    public void testValidModel() throws IOException, JAXBException {
        String fileContents = getFileContents("Case 1.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, false);
        assertTrue(modelCheckResult.isValid());
        assertEquals("", modelCheckResult.invalidMessage());
    }

    @Test
    public void testEmptyModel() throws IOException, JAXBException {
        String fileContents = getFileContents("empty_model.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, false);
        assertFalse(modelCheckResult.isValid());
        assertTrue(modelCheckResult.invalidMessage().contains("The model is empty"));
    }

    @Test
    public void testModelWithPool() throws IOException, JAXBException {
        String fileContents = getFileContents("model_with_pool.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, true);
        assertTrue(modelCheckResult.isValid());
        assertEquals("", modelCheckResult.invalidMessage());

        modelCheckResult = modelChecker.checkModel(fileContents, false);
        assertFalse(modelCheckResult.isValid());
        assertTrue(modelCheckResult.invalidMessage()
                .contains("There are pools in the model. Pools are not yet supported"));
    }

    @Test
    public void testModelWithSelfLoopAndMultipleEventArcs() throws IOException, JAXBException {
        String fileContents = getFileContents("self_loop.bpmn");

        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, false);
        assertFalse(modelCheckResult.isValid());

        String invalidMsg = modelCheckResult.invalidMessage();
        assertTrue(invalidMsg.contains("The element A has a self-loop"));
        assertTrue(invalidMsg.contains("The task A has more than one outgoing arc"));
        assertTrue(invalidMsg.contains("The task A has more than one incoming arc"));
        assertTrue(invalidMsg.contains("The Start Event has more than one outgoing arc"));
        assertTrue(invalidMsg.contains("The End Event has more than one incoming arc"));
    }

    @Test
    public void testModelWithMultipleStartEvents() throws IOException, JAXBException {
        String fileContents = getFileContents("multiple_start_events.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, false);
        assertFalse(modelCheckResult.isValid());
        assertTrue(modelCheckResult.invalidMessage().contains("The model must contain exactly 1 start event"));
    }

    @Test
    public void testModelWithMultipleEndEvents() throws IOException, JAXBException {
        String fileContents = getFileContents("multiple_end_events.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, false);
        assertFalse(modelCheckResult.isValid());
        assertTrue(modelCheckResult.invalidMessage().contains("The model must contain exactly 1 end event"));
    }

    @Test
    public void testModelDisjointedNodes() throws IOException, JAXBException {
        String fileContents = getFileContents("disjointed.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, false);

        assertFalse(modelCheckResult.isValid());

        String invalidMsg = modelCheckResult.invalidMessage();
        assertTrue(invalidMsg.contains("The task A has missing incoming or outgoing arcs"));
        assertTrue(invalidMsg.contains("The Start Event has a missing outgoing arc"));
        assertTrue(invalidMsg.contains("The End Event has a missing incoming arc"));
        assertTrue(invalidMsg.contains("The gateway Gateway_0gczbo1 has missing incoming or outgoing arcs"));
        assertTrue(invalidMsg.contains("The model has disconnected arcs"));
    }

    @Test
    public void testModelReverseSequenceFlow() throws IOException, JAXBException {
        String fileContents = getFileContents("start_end_backwards.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, false);

        assertFalse(modelCheckResult.isValid());

        String invalidMsg = modelCheckResult.invalidMessage();
        assertTrue(invalidMsg.contains("The Start Event has incoming arc(s)"));
        assertTrue(invalidMsg.contains("The End Event has outgoing arc(s)"));
    }

    @Test
    public void testModelNoProcesses() throws IOException, JAXBException {
        String fileContents = getFileContents("no_process.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, false);

        assertFalse(modelCheckResult.isValid());

        String invalidMsg = modelCheckResult.invalidMessage();
        assertTrue(invalidMsg.contains("There is no process diagram in the model"));
    }

    @Test
    public void testModelMultipleProcesses() throws IOException, JAXBException {
        String fileContents = getFileContents("multiple_processes.bpmn");
        ModelCheckResult modelCheckResult = modelChecker.checkModel(fileContents, false);

        assertFalse(modelCheckResult.isValid());

        String invalidMsg = modelCheckResult.invalidMessage();
        assertTrue(invalidMsg.contains("There is more than one process diagram in the model"));
    }

    /**
     * Get the contents of a file as string in the following directory: src/test/resources/data
     * @param filename the name of a file in the src/test/resources/data directory.
     * @return the contents of the file as a string.
     * @throws IOException
     */
    private String getFileContents(String filename) throws IOException {
        return Files.readString(Paths.get("src", "test", "resources", "data", "./" + filename),
                StandardCharsets.UTF_8);
    }

}
