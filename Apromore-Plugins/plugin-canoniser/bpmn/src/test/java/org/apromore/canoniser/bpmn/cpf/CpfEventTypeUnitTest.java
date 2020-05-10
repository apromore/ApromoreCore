/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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
