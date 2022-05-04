/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileStorageClientTest {

    private static final String BASE_PATH = "src/test/resources";

    private FileStorageClient fileStorageClient;

    @BeforeEach
    void setUp() {
        fileStorageClient = new FileStorageClient(BASE_PATH);
    }

    @Test
    void should_return_true_if_file_exists() {
        assertTrue(fileStorageClient.isFileExists("baseFolder", "test.csv"));
    }

    @Test
    void should_return_false_if_file_does_not_exist() {
        assertFalse(fileStorageClient.isFileExists("baseFolder", "not_exists.csv"));
    }
}