/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
