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

package org.apromore.service.logimporter.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to work with files.
 *
 * @author frankma
 */
public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static void deleteFile(File file) {

        // Delete file after pipe close, else delete the file when the application is terminated
        try {
            Files.delete(file.toPath());
            LOGGER.debug("Temp file \"{}\" deleted.", file);
        } catch (IOException e) {
            file.deleteOnExit();
            LOGGER.error("Temp file \"{}\" is scheduled for deletion as previous attempt was failed: {}", file,
                e.getMessage());
        }
    }

    public static String sha256Hashing(String originalString) {
        return DigestUtils.sha256Hex(originalString.trim());
    }
}
