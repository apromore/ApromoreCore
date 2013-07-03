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
public class CpfEventTypeUnitTest {

    /** Test instance. */
    CpfEventType event;

    /** Populate {@link #event} with a freshly-constructed {@link CpfEventType}. */
    @Before
    public void initializeEvent() {
        event = (CpfEventType) new ObjectFactory().createEventType();
    }
     
    /** Test {@link CpfEventType#isSignalCatcher) and {@link CpfEventType#setSignalCaughtRef). */
    @Test
    public void testIsSignalCatcher() throws Exception {
        QName signal = new QName("http://example.com", "value");

        assertFalse(event.isSignalCatcher());
        assertFalse(event.isSignalThrower());

        event.setSignalCaughtRef(signal);
        assertTrue(event.isSignalCatcher());
        assertEquals(signal, event.getSignalCaughtRef());
        assertFalse(event.isSignalThrower());

        // Make sure that we can represent a signal without a signal reference
        event.setSignalCaughtRef(null);
        assertTrue(event.isSignalCatcher());
        assertNull(event.getSignalCaughtRef());
        assertFalse(event.isSignalThrower());
    }
}
