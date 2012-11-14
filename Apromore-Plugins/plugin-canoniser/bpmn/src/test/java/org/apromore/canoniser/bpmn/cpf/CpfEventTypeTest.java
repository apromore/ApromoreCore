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
 * Test suite for {@link CpfEventType}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfEventTypeTest {

    /** Test instance. */
    CpfEventType event;

    /** Populate {@link #event} with a freshly-constructed {@link CpfEventType}. */
    @Before
    public void initializeEvent() {
        event = (CpfEventType) new ObjectFactory().createEventType();
    }
     
    /**
     * Test {@link CpfEventType#isSignal) and {@link CpfEventType#setIsSignal).
     */
    @Test
    public void testIsSignal() throws Exception {
        QName signal = new QName("http://example.com", "value");

        assertFalse(event.isSignal());

        event.setSignalRef(signal);
        assertTrue(event.isSignal());
        assertEquals(signal, event.getSignalRef());

        // Make sure that we can represent a signal without a signal reference
        event.setSignalRef(null);
        assertTrue(event.isSignal());
        assertNull(event.getSignalRef());
    }
}
