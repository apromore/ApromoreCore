/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.pql;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import org.apromore.helper.Version;

/**
 * Tests for {@link ExternalId}.
 */
public class ExternalIdUnitTest {

    @Test
    public void testConstructFromComponents() {
        Version version = new Version(1, 0);
        ExternalId externalId = new ExternalId(32, "MAIN", version);
        assertEquals(32, externalId.getProcessId());
        assertEquals("MAIN", externalId.getBranch());
        assertEquals(version, externalId.getVersion());
        assertEquals(32, externalId.getProcessId());
    }

    @Test
    public void testConstructFromString() throws ParseException {
        ExternalId externalId = new ExternalId("32/MAIN/1.0");
        assertEquals(32, externalId.getProcessId());
        assertEquals("MAIN", externalId.getBranch());
        assertEquals(new Version(1, 0), externalId.getVersion());
        assertEquals("32/MAIN/1.0", externalId.toString());
    }

    @Test(expected = ParseException.class)
    public void testConstructFromInvalidString() throws ParseException {
        ExternalId externalId = new ExternalId("Not an external ID");
    }

    @Test
    public void testEquals() {
        ExternalId externalId = new ExternalId(32, "MAIN", new Version(1, 0));

        assertFalse(externalId.equals(null));
        assertTrue(externalId.equals(externalId));
        assertTrue(externalId.equals(new ExternalId(32, "MAIN", new Version(1, 0))));
        assertFalse(externalId.equals(new ExternalId(31, "MAIN", new Version(1, 0))));
        assertFalse(externalId.equals(new ExternalId(32, "BRANCH", new Version(1, 0))));
        assertFalse(externalId.equals(new ExternalId(32, "MAIN", new Version(1, 1))));
    }
}
