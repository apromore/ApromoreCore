/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013, 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

/**
 * Test the Version Util.
 * Extraction of version numbers from a string and increments that version.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class VersionUtilUnitTest {

    @Test
    public void extractVersionNumbers() {
        String versionNumber = "1.2";
        String[] version = VersionUtil.extractVersionNumber(versionNumber);
        assertThat(version.length, equalTo(2));
        assertThat(version[0], equalTo("1"));
        assertThat(version[1], equalTo("2"));

        versionNumber = "1.2.3";
        version = VersionUtil.extractVersionNumber(versionNumber);
        assertThat(version.length, equalTo(3));
        assertThat(version[0], equalTo("1"));
        assertThat(version[1], equalTo("2"));
        assertThat(version[2], equalTo("3"));

        versionNumber = "1.2-beta";
        version = VersionUtil.extractVersionNumber(versionNumber);
        assertThat(version.length, equalTo(2));
        assertThat(version[0], equalTo("1"));
        assertThat(version[1], equalTo("2"));

        versionNumber = "1.2.3 beta";
        version = VersionUtil.extractVersionNumber(versionNumber);
        assertThat(version.length, equalTo(3));
        assertThat(version[0], equalTo("1"));
        assertThat(version[1], equalTo("2"));
        assertThat(version[2], equalTo("3"));

        versionNumber = "1.5.93 beta";
        version = VersionUtil.extractVersionNumber(versionNumber);
        assertThat(version.length, equalTo(3));
        assertThat(version[0], equalTo("1"));
        assertThat(version[1], equalTo("5"));
        assertThat(version[2], equalTo("93"));

        versionNumber = "1.5.93beta4";
        version = VersionUtil.extractVersionNumber(versionNumber);
        assertThat(version.length, equalTo(4));
        assertThat(version[0], equalTo("1"));
        assertThat(version[1], equalTo("5"));
        assertThat(version[2], equalTo("93"));
        assertThat(version[3], equalTo("4"));

        versionNumber = "abcded";
        version = VersionUtil.extractVersionNumber(versionNumber);
        assertThat(version.length, equalTo(0));
    }


    @Test
    public void incrementVersionNumbers() {
        String versionNumber = "1.2";
        String[] version = VersionUtil.extractVersionNumber(versionNumber);
        String newVersionNumber = VersionUtil.incrementVersionNumber(version);
        assertThat(newVersionNumber, equalTo("1.3"));

        versionNumber = "1.2.5";
        version = VersionUtil.extractVersionNumber(versionNumber);
        newVersionNumber = VersionUtil.incrementVersionNumber(version);
        assertThat(newVersionNumber, equalTo("1.2.6"));

        versionNumber = "1.2.94Beta";
        version = VersionUtil.extractVersionNumber(versionNumber);
        newVersionNumber = VersionUtil.incrementVersionNumber(version);
        assertThat(newVersionNumber, equalTo("1.2.95"));

        versionNumber = "1.2 Beta4";
        version = VersionUtil.extractVersionNumber(versionNumber);
        newVersionNumber = VersionUtil.incrementVersionNumber(version);
        assertThat(newVersionNumber, equalTo("1.2.5"));
    }
}
