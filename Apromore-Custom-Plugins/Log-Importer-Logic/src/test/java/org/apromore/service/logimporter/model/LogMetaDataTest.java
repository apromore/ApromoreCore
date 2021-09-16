/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2021 Apromore Pty Ltd.
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
package org.apromore.service.logimporter.model;

import org.apromore.service.logimporter.exception.InvalidLogMetadataException;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LogMetaDataTest {

    private final String perspective_1 = "Perspective_1";
    private final String perspective_2 = "Perspective_2";
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetPerspectives() throws InvalidLogMetadataException {

        // Given
        LogMetaData logMetaData = createTestLogMetaData();
        logMetaData.setCaseIdPos(0);
        logMetaData.setActivityPos(1);
        logMetaData.setResourcePos(2);
        logMetaData.setEndTimestampPos(3);
        logMetaData.setPerspectivePos(Arrays.asList(4, 5));
        logMetaData.setEventAttributesPos(Arrays.asList(4, 5));

        // When
        List<String> actual = logMetaData.getPerspectives();
        List<String> expected = Arrays.asList(XConceptExtension.KEY_NAME, XOrganizationalExtension.KEY_RESOURCE,
                perspective_1, perspective_2);

        // Then
        // Test equal
        assertThat(actual, is(expected));
    }

    @Test
    public void testGetPerspectives_NoPerspectivePos() throws InvalidLogMetadataException {

        // Given
        LogMetaData logMetaData = createTestLogMetaData();
        logMetaData.setCaseIdPos(0);
        logMetaData.setActivityPos(1);
        logMetaData.setResourcePos(2);
        logMetaData.setEndTimestampPos(3);

        // When
        List<String> actual = logMetaData.getPerspectives();
        List<String> expected = Arrays.asList(XConceptExtension.KEY_NAME, XOrganizationalExtension.KEY_RESOURCE);

        // Then
        // Test equal
        assertThat(actual, is(expected));
    }

    @Test
    public void testGetPerspectives_NoResourcePos() throws InvalidLogMetadataException {

        // Given
        LogMetaData logMetaData = createTestLogMetaData();
        logMetaData.setCaseIdPos(0);
        logMetaData.setActivityPos(1);
        logMetaData.setEndTimestampPos(3);
        logMetaData.setPerspectivePos(Arrays.asList(4, 5));
        logMetaData.setEventAttributesPos(Arrays.asList(4, 5));

        // When
        List<String> actual = logMetaData.getPerspectives();
        List<String> expected = Arrays.asList(XConceptExtension.KEY_NAME, perspective_1, perspective_2);

        // Then
        // Test equal
        assertThat(actual, is(expected));
    }

    @Test(expected = InvalidLogMetadataException.class)
    public void testGetPerspectives_eventAttributesPosNotMatchPerspectivePos() throws InvalidLogMetadataException {

        // Given
        LogMetaData logMetaData = createTestLogMetaData();
        logMetaData.setActivityPos(1);
        logMetaData.setEndTimestampPos(3);
        logMetaData.setPerspectivePos(Arrays.asList(4, 5));

        // When
        List<String> actual = logMetaData.getPerspectives();

        // Then
        // Throw InvalidLogMetadataException
    }

    @Test(expected = InvalidLogMetadataException.class)
    public void testGetPerspectives_missingActivityPos() throws InvalidLogMetadataException {

        // Given
        LogMetaData logMetaData = createTestLogMetaData();
        logMetaData.setCaseIdPos(0);
        logMetaData.setEndTimestampPos(3);
        logMetaData.setPerspectivePos(Arrays.asList(4, 5));
        logMetaData.setEventAttributesPos(Arrays.asList(4, 5));

        // When
        List<String> actual = logMetaData.getPerspectives();

        // Then
        // Throw InvalidLogMetadataException
    }

    private LogMetaData createTestLogMetaData() {
        return new LogMetaData(Arrays.asList("case id", "activity", "resource", "2011/02/16 14:31:00.000",
                perspective_1, perspective_2));
    }
}