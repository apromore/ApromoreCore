/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.osgihelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class RemoveNonOSGIJars {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new RuntimeException("Must be called with exactly one argument!");
        }
        String path = args[0];
        Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.jar");
                if (pathMatcher.matches(file)) {
                    boolean deleteFile = false;
                    try (JarInputStream inputStream = new JarInputStream(new FileInputStream(file.toFile()))) {
                        Manifest manifest = inputStream.getManifest();
                        if (manifest == null) {
                            JarEntry jarEntry;
                            while((jarEntry = inputStream.getNextJarEntry()) != null) {
                                if (JarFile.MANIFEST_NAME.equalsIgnoreCase(jarEntry.getName())) {
                                    manifest = new Manifest(inputStream);
                                    break;
                                }
                            }
                        }
                        if (manifest != null && manifest.getMainAttributes().getValue("Bundle-Name") == null) {
                            deleteFile = true;
                        }
                    }
                    if (deleteFile) {
                        Files.delete(file);
                        System.out.println("Removed non-OSGI JAR " + file.getFileName());
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
