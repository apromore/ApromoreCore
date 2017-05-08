/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package org.apromore.tools.cpfimporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apromore.manager.client.ManagerService;
import org.apromore.manager.client.ManagerServiceClient;
import org.apromore.model.FolderType;
import org.apromore.plugin.property.RequestParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple application to import CPF and ANF files into Apromore so testing can be performed.
 */
public final class Import {

    private static final Logger LOGGER = LoggerFactory.getLogger(Import.class.getName());

    private final static String PWD = "./";

    private static ManagerService manager = null;

    /* The Canonical Process Importer Starting point. */
    public static void main(String[] args) throws Exception {

        File fromDir = new File(PWD);  // default to reading files from the current directory
        File toDir = null;          // default to keeping the existing subdirectory path form inside the fromDir

        if (args.length < 1) {
            args = new String[] {""};
        }

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
            case "-h":
            case "-?":
            case "-help":
            case "--help":
                displayHelp(System.out);
                System.exit(0);
                break;
            case "-manager":
                manager = new ManagerServiceClient(new URI(args[++i]));
                break;
            case "-from":
                fromDir = new File(args[++i]);
                if (!fromDir.isDirectory()) {
                    throw new Exception("-from " + fromDir + " is not a directory");
                }
                break;
            case "-to":
                toDir = new File(args[++i]);
                break;
            default:
                File fileArg = new File(fromDir, args[i]);
                File toArg = (toDir == null) ? new File(args[i]) : new File(toDir, args[i]);
                if (fileArg.isFile()) {
                    uploadFile(fileArg, toArg);
                }
                else if (fileArg.isDirectory()) {
                    processDirectory(fileArg, toArg);
                } else {
                    System.err.println("Argument \"" + fileArg + "\" is neither a file nor a directory");
                    System.exit(-1);
                }
            }
        }
    }


    private static void  displayHelp(PrintStream out) {
        out.println("cpfImporter takes a list of the following arguments:\n" +
                    "  <file>                 add a single process model\n" +
                    "  <directory>            recursively add all process models in the directory\n" +
                    "  -manager <url>         use the specified manager, e.g. http://localhost:9000/manager/services\n" +
                    "  -from <directory>      use the specified base directory\n" +
                    "  -to <directory>        use the specified directory as the base folder within Apromore\n" +
                    "  -h, -help, --help, -?  show this message");
    }


    private static void processDirectory(File directory, final File toDir) {
        try {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        processDirectory(file, toDir);
                    } else {
                        uploadFile(file, toDir);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("failed to process files or directories. ", e);
        }
    }


    /* upload a single process or log into apromore. */
    private static void uploadFile(final File file, final File toFile) {
        LOGGER.info("Upload from " + file + " to " + toFile);
        try {
            final String userName = "admin";
            final Set<RequestParameterType<?>> noCanoniserParameters = Collections.emptySet();

            File parentFile = toFile.getParentFile();
            String ext = FilenameUtils.getExtension(toFile.getName());
            if (parentFile != null) {
                createFolder(parentFile);
                int parentId = getFolderId(parentFile);
                assert parentId != -1;

                String now = DateFormat.getInstance().format(new Date());

                if (manager == null) {
                    LOGGER.error("Failed to load file {} because no -manager parameter was specified", file);
                } else if ("mxml".equals(ext) || "xes".equals(ext)) {
                    manager.importLog(
                        userName,
                        parentId,
                        FilenameUtils.getBaseName(toFile.getName()),  // log name
                        new FileInputStream(file),                    // XML serialization of the log
                        ext,                                          // filename extension
                        "domain",
                        now,                                          // creation timestamp
                        true);                                        // make public?
                } else if (getNativeFormat(ext) != null) {
                    manager.importProcess(
                        userName,                                     // user name
                        parentId,                                     // folder ID
                        getNativeFormat(ext),                         // native type
                        FilenameUtils.getBaseName(toFile.getName()),  // process name
                        "1.0",                                        // version number
                        new FileInputStream(file),                    // XML serialization of the process
                        "domain",
                        "documentation",
                        now,                                          // creation timestamp
                        now,                                          // last modification timestamp
                        true,                                         // make public?
                        noCanoniserParameters);
                } else {
                    LOGGER.error("Failed to load file {}; unrecognized file extension", file.getName());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load file {} due to {}", file.getName(), e.getMessage());
        }
    }

    /* creates a folder in apromore. */
    private static void createFolder(final File file) throws Exception {
        final String user = "ad1f7b60-1143-4399-b331-b887585a0f30";

//        String filename = file.getPath().replaceFirst("./", "");
//        Path p1 = Paths.get(filename);

        File parentFile = file.getParentFile();
        int parentId = 0;
        if (parentFile != null) {
            createFolder(parentFile);
            parentId = getFolderId(parentFile);
        }
        if (getFolderId(file) == -1) {
            manager.createFolder(user, file.getName(), parentId, false);
            LOGGER.info(file + " created");
        } else {
            LOGGER.debug(file + " already exists");
        }
    }

    private static int getFolderId(final File file) {
        final String user = "ad1f7b60-1143-4399-b331-b887585a0f30";

        List<FolderType> tree = manager.getWorkspaceFolderTree(user);
        Path path = file.toPath();

        FolderType folder;
        int id = 0;
        for (int i = 0; i < path.getNameCount(); i++) {
            folder = findFolderByName(path.getName(i).toString(), tree);
            if (folder == null) {
                return -1;
            }
            tree = folder.getFolders();
            id = folder.getId();
        }
        return id;
    }

    private static FolderType findFolderByName(String name, List<FolderType> folders) {
        for (FolderType folder : folders) {
            if (folder.getFolderName().equals(name)) {
                return folder;
            }
        }
        return null;
    }

    private static String getNativeFormat(String ext) {
        if (ext.equalsIgnoreCase("epml")) {
            return "EPML 2.0";
        } else if (ext.equalsIgnoreCase("bpmn")) {
            return "BPMN 2.0";
        } else if (ext.equalsIgnoreCase("xpdl")) {
            return "XPDL 2.2";
        } else if (ext.equalsIgnoreCase("yawl")) {
            return "YAWL 2.2";
        } else if (ext.equalsIgnoreCase("pnml")) {
            return "PNML 1.3.2";
        } else if (ext.equalsIgnoreCase("aml")) {
            return "AML fragment";
        }
        return null;
    }

}
