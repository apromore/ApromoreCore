/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apromore.portal.helper.Version;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test the Version Number Code for Apromore.
 */
class VersionUnitTest {

    @Test
    void testOneNumberVersionFromString() {
        Version version = new Version("1");
        assertEquals("1", version.toString(),  "Numbers don't match");
    }

    @Test
    void testTwoNumberVersionFromString() {
        Version version = new Version("1.0");
        assertEquals("1.0", version.toString(),  "Numbers don't match");
    }

    @Test
    void testTwoNumberVersionFromNumber() {
        Version version = new Version(1,0);
        assertEquals("1.0", version.toString(),  "Numbers don't match");
    }

    @Test
    void testThreeNumberVersionFromString() {
        Version version = new Version("1.0.0");
        assertEquals("1.0.0", version.toString(),  "Numbers don't match");
    }

    @Test
    void testThreeNumberVersionFromNumber() {
        Version version = new Version(1,0,3);
        assertEquals("1.0.3", version.toString(),  "Numbers don't match");
    }

    @Test
    void testVersionWithQualifierFromString() {
        Version version = new Version("1.0.0.test");
        assertEquals("1.0.0.test", version.toString(),  "Numbers don't match");
    }

    @Test
    void testVersionWithQualifierFromConstruct() {
        Version version = new Version(1,0,3,"test");
        assertEquals("1.0.3.test", version.toString(),  "Numbers don't match");
    }

    @Test
    void testVersionCompareToA() {
        Version versionA = new Version(1,0);
        Version versionB = new Version(1,0);
        assertEquals(versionA, versionB, "Numbers don't match");
    }

    @Test
    void testVersionCompareToB() {
        Version versionA = new Version(1,0,3,"2");
        Version versionB = new Version(1,0,3,"2");
        assertEquals(versionA, versionB, "Numbers don't match");
    }

    @Test
    void testVersionCompareToC() {
        Version versionA = new Version("23.2.1");
        Version versionB = new Version("23.2.1");
        assertEquals(versionA, versionB, "Numbers don't match");
    }

    @Test
    void testVersionCompareToD() {
        Version versionA = new Version(2,3,4);
        Version versionB = new Version(1,2,3);
        assertNotEquals(versionA, versionB, "Numbers don't match");
    }
}
