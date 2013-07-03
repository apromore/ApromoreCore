package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Third party packages
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test suite for {@link CpfTaskType}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfTaskTypeUnitTest {

    /** Test instance. */
    CpfTaskType task;

    /** Populate {@link #task} with a freshly-constructed {@link CpfTaskType}. */
    @Before
    public void initializeTask() {
        task = (CpfTaskType) new ObjectFactory().createTaskType();
    }
     
    /**
     * Test {@link CpfTaskType#getCalledElement) and {@link CpfTaskType#setCalledElement).
     */
    @Test
    public void testCalledElement() throws Exception {
        assertNull(task.getCalledElement());

        task.setCalledElement(new QName("http://example.com", "test"));
        assertEquals(new QName("http://example.com", "test"), task.getCalledElement());

        task.setCalledElement(null);
        assertNull(task.getCalledElement());
    }

    /**
     * Test {@link CpfTaskType#isTriggeredByEvent) and {@link CpfTaskType#setTriggeredByEvent).
     */
    @Test
    public void testTriggeredByEvent() throws Exception {
        assertFalse(task.isTriggeredByEvent());

        task.setTriggeredByEvent(true);
        assertTrue(task.isTriggeredByEvent());

        task.setTriggeredByEvent(false);
        assertFalse(task.isTriggeredByEvent());
    }
}
