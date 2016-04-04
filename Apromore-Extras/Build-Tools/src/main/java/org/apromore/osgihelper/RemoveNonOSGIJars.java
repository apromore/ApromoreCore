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
