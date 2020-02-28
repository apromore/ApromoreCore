/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.canoniser.bpmn.cpf;

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
 * Test suite for {@link CpfObjectType}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfObjectTypeUnitTest {

    /** Test instance. */
    CpfObjectTypeImpl object;

    /** Populate {@link #object} with a freshly-constructed {@link CpfObjectType}. */
    @Before
    public void initializeObject() {
        object = (CpfObjectTypeImpl) new ObjectFactory().createObjectType();
    }

    /**
     * Test {@link CpfTaskType#isIsCollection) and {@link CpfTaskType#setIsCollection).
     */
    @Test
    public void testIsCollection() throws Exception {
        assertFalse(object.isIsCollection());

        object.setIsCollection(true);
        assertTrue(object.isIsCollection());

        object.setIsCollection(false);
        assertFalse(object.isIsCollection());
    }
     
    /**
     * Test {@link CpfTaskType#getOriginalName) and {@link CpfTaskType#setOriginalName).
     */
    @Test
    public void testOriginalName() throws Exception {
        assertNull(object.getOriginalName());

        object.setOriginalName("test");
        assertEquals("test", object.getOriginalName());

        object.setOriginalName(null);
        assertNull(object.getOriginalName());
    }
}
