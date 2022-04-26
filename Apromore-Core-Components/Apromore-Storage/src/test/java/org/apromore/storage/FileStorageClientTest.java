/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
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