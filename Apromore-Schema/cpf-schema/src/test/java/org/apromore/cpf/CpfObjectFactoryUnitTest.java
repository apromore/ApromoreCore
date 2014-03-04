package org.apromore.cpf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 * Test suite for {@link CpfObjectFactory}.
 */
public class CpfObjectFactoryUnitTest {

    /**
     * Test the equality and hashcode methods of {@link CancellationRefType}s.
     */
    @Test
    public void testCancellationRef() {
        CancellationRefType a = CpfObjectFactory.getInstance().createCancellationRefType();
        CancellationRefType b = CpfObjectFactory.getInstance().createCancellationRefType();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        a.setRefId("a");
        assertFalse(a.equals(b));
        assertFalse(a.hashCode() == b.hashCode());

        b.setRefId("b");
        assertFalse(a.equals(b));
        assertFalse(a.hashCode() == b.hashCode());

        b.setRefId("a");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
