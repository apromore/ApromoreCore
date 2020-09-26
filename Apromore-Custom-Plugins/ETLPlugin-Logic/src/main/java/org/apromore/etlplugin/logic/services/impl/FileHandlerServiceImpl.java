/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.etlplugin.logic.services.impl;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.io.input.ReaderInputStream;
import org.apromore.etlplugin.logic.services.FileHandlerService;
import org.zkoss.util.media.Media;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Implement the file handle service.
 */
public class FileHandlerServiceImpl implements FileHandlerService {
    private static final int BUFFER_SIZE = 1024;
    private static final String UPLOAD_FAILED = "Upload Failed";
    private static final String UPLOAD_SUCCESS = "Upload Success";
    private String tempDir = System.getProperty("java.io.tmpdir") +
        System.getenv("DATA_STORE");

    /**
     * Create a directory to save the output files to.
     *
     * @param path name of the file to create a directory for
     * @throws IOException if unable to change permissions
     */
    private void generateDirectory(String path) throws IOException {
        new File(path).mkdirs();
        changeFilePermission(path);
    }

    /**
     * Output a file to the user who request download.
     *
     * @return a file
     */
    public File outputFile() {
        File dir = new File(this.tempDir);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            // Select one file and return it. This is for demo purposes.
            for (File f : directoryListing) {
                return f;
            }
        }
        return null;
    }

    /**
     * Outputs all files.
     *
     * @return returns a list of files.
     */
    public ArrayList<File> outputFiles() {
        ArrayList<File> files = new ArrayList<File>();
        File dir = new File(this.tempDir);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File f : directoryListing) {
                files.add(f);
            }
            return files;
        }
        return null;
    }

    /**
     * Writes the input file to an output buffer.
     *
     * @param medias the input files.
     * @return return the message to show on client side.
     * @throws IllegalFileTypeException if file type is not supported
     */
    public String writeFiles(Media[] medias) throws IllegalFileTypeException {
        System.out.println("---> write files");
        for (int i = 0; i < medias.length; i++) {
            Media media = medias[i];
            String fileName = media.getName();
            String path;

            if (fileName.endsWith(".csv")) {
                path = this.tempDir + "/" +
                    FilenameUtils.removeExtension(fileName) + "_csv" + "/" +
                    fileName;
            } else {
                path = this.tempDir + "/" +
                    FilenameUtils.removeExtension(fileName) + "/" + fileName;
            }

            if (!(
                fileName.endsWith(".csv") ||
                fileName.endsWith(".dat") ||
                fileName.endsWith(".parq") ||
                fileName.endsWith(".parquet"))) {
                throw new
                    IllegalFileTypeException("File must be csv or parquet.");
            }

            try {
                if (fileName.endsWith(".csv")) {
                    generateDirectory(
                        this.tempDir + "/" +
                        FilenameUtils.removeExtension(fileName) + "_csv");
                }

                generateDirectory(
                    this.tempDir + "/" +
                    FilenameUtils.removeExtension(fileName));
            } catch (IOException e) {
                e.printStackTrace();
                return UPLOAD_FAILED;
            }

            File file = new File(path);

            try (
                InputStream fIn = (
                    media.isBinary() ?
                    media.getStreamData() :
                    new ReaderInputStream(
                        media.getReaderData(),
                        StandardCharsets.UTF_8));
                OutputStream fOut = new FileOutputStream(file, false);
                BufferedInputStream in = new BufferedInputStream(
                    new BOMInputStream(fIn));
                BufferedOutputStream out = new BufferedOutputStream(fOut)
            ) {
                byte buffer[] = new byte[BUFFER_SIZE];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                changeFilePermission(path);
            } catch (Exception e) {
                e.printStackTrace();
                return UPLOAD_FAILED;
            }
        }

        return UPLOAD_SUCCESS;
    }

    /**
     * Change the File permission so that impala can read and write the files in
     * the volume.
     *
     * @param filePath Path of the file in the volume.
     * @throws IOException if the file permissions were not changed
     */
    private void changeFilePermission(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        File file = path.toFile();

        if (file.exists()) {
            boolean brval = file.setReadable(true, false);
            boolean bwval = file.setWritable(true, false);
            if (file.isDirectory()) {
                boolean bxval = file.setExecutable(true, false);
            }
        }
    }

    /**
     * Find the newest parquet file in directory.
     *
     * @param directoryFilePath path to directory
     * @return returns a file
     */
    public File getLastParquet(String directoryFilePath) {
        File directory = new File(directoryFilePath);
        File[] files = directory.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;
        File chosenFile = null;

        if (files != null) {
            for (File file : files) {
                String extName = FilenameUtils.getExtension(file.getName());
                if (extName.equals("parq") &&
                    file.lastModified() > lastModifiedTime) {
                    chosenFile = file;
                    lastModifiedTime = file.lastModified();
                }
            }
        }

        return chosenFile;
    }
}
