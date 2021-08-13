package org.apromore.service.logimporter.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static void deleteFile (File file) {

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
}
