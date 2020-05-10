/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

package org.apromore.helper;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Version Number Code for Apromore.
 */
public class VersionUnitTest {

    @Test
    public void testOneNumberVersionFromString() {
        Version version = new Version("1");
        Assert.assertEquals("Numbers don't match", version.toString(), "1");
    }

    @Test
    public void testTwoNumberVersionFromString() {
        Version version = new Version("1.0");
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0");
    }

    @Test
    public void testTwoNumberVersionFromNumber() {
        Version version = new Version(1,0);
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0");
    }

    @Test
    public void testThreeNumberVersionFromString() {
        Version version = new Version("1.0.0");
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0.0");
    }

    @Test
    public void testThreeNumberVersionFromNumber() {
        Version version = new Version(1,0,3);
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0.3");
    }

    @Test
    public void testVersionWithQualifierFromString() {
        Version version = new Version("1.0.0.test");
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0.0.test");
    }

    @Test
    public void testVersionWithQualifierFromConstruct() {
        Version version = new Version(1,0,3,"test");
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0.3.test");
    }

    @Test
    public void testVersionCompareToA() {
        Version versionA = new Version(1,0);
        Version versionB = new Version(1,0);
        Assert.assertTrue("Numbers don't match", versionA.compareTo(versionB) == 0);
    }

    @Test
    public void testVersionCompareToB() {
        Version versionA = new Version(1,0,3,"2");
        Version versionB = new Version(1,0,3,"2");
        Assert.assertTrue("Numbers don't match", versionA.compareTo(versionB) == 0);
    }

    @Test
    public void testVersionCompareToC() {
        Version versionA = new Version("23.2.1");
        Version versionB = new Version("23.2.1");
        Assert.assertTrue("Numbers don't match", versionA.compareTo(versionB) == 0);
    }

    @Test
    public void testVersionCompareToD() {
        Version versionA = new Version(2,3,4);
        Version versionB = new Version(1,2,3);
        Assert.assertFalse("Numbers don't match", versionA.compareTo(versionB) == 0);
    }
}
