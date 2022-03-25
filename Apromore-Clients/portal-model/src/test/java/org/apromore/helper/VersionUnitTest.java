/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apromore.portal.helper.Version;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test the Version Number Code for Apromore.
 */
public class VersionUnitTest {

    @Test
    public void testOneNumberVersionFromString() {
        Version version = new Version("1");
        assertEquals(version.toString(), "1", "Numbers don't match");
    }

    @Test
    public void testTwoNumberVersionFromString() {
        Version version = new Version("1.0");
        assertEquals(version.toString(), "1.0", "Numbers don't match");
    }

    @Test
    public void testTwoNumberVersionFromNumber() {
        Version version = new Version(1,0);
        assertEquals(version.toString(), "1.0", "Numbers don't match");
    }

    @Test
    public void testThreeNumberVersionFromString() {
        Version version = new Version("1.0.0");
        assertEquals(version.toString(), "1.0.0", "Numbers don't match");
    }

    @Test
    public void testThreeNumberVersionFromNumber() {
        Version version = new Version(1,0,3);
        assertEquals(version.toString(), "1.0.3", "Numbers don't match");
    }

    @Test
    public void testVersionWithQualifierFromString() {
        Version version = new Version("1.0.0.test");
        assertEquals(version.toString(), "1.0.0.test", "Numbers don't match");
    }

    @Test
    public void testVersionWithQualifierFromConstruct() {
        Version version = new Version(1,0,3,"test");
        assertEquals(version.toString(), "1.0.3.test", "Numbers don't match");
    }

    @Test
    public void testVersionCompareToA() {
        Version versionA = new Version(1,0);
        Version versionB = new Version(1,0);
        assertTrue(versionA.compareTo(versionB) == 0, "Numbers don't match");
    }

    @Test
    public void testVersionCompareToB() {
        Version versionA = new Version(1,0,3,"2");
        Version versionB = new Version(1,0,3,"2");
        assertTrue(versionA.compareTo(versionB) == 0, "Numbers don't match");
    }

    @Test
    public void testVersionCompareToC() {
        Version versionA = new Version("23.2.1");
        Version versionB = new Version("23.2.1");
        assertTrue(versionA.compareTo(versionB) == 0, "Numbers don't match");
    }

    @Test
    public void testVersionCompareToD() {
        Version versionA = new Version(2,3,4);
        Version versionB = new Version(1,2,3);
        assertFalse(versionA.compareTo(versionB) == 0, "Numbers don't match");
    }
}
