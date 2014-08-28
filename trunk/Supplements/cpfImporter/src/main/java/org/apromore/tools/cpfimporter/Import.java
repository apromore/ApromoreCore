/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.tools.cpfimporter;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apromore.manager.client.ManagerService;
import org.apromore.model.FolderType;
import org.apromore.plugin.property.RequestParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple application to import CPF and ANF files into Apromore so testing can be performed.
 */
public final class Import {

    private static final Logger LOGGER = LoggerFactory.getLogger(Import.class.getName());

    private final static String PWD = "./";

    private ManagerService manager;
    private String importRootFolder;

    /* The Canonical Process Importer Starting point. */
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            new Import(args[0]);
        } else {
            new Import("");
        }
    }


    /**
     * Default Constructor.
     */
    public Import(final String arg0) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/managerClientContext.xml");
        manager = (ManagerService) ctx.getAutowireCapableBeanFactory().getBean("managerClient");

        importRootFolder = arg0;
        File fileArg = new File(arg0);
        if (fileArg.isFile()) {
            uploadProcess(new File(arg0));
        } else {
            processDirectory(fileArg);
        }
    }


    private void processDirectory(File directory) {
        try {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        processDirectory(file);
                    } else {
                        uploadProcess(file);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("failed to process files or directories. ", e);
        }
    }


    /* upload a single process into apromore. */
    private void uploadProcess(final File file) {
        try {
            final String userName = "admin";
            final Set<RequestParameterType<?>> noCanoniserParameters = Collections.emptySet();

            File parentFile = file.getParentFile();
            String ext = FilenameUtils.getExtension(file.getName());
            if (getNativeFormat(ext) != null && parentFile != null) {
                createFolder(parentFile);
                int parentId = getFolderId(parentFile);
                assert parentId != -1;

                String now = DateFormat.getInstance().format(new Date());

                manager.importProcess(
                    userName,                                   // user name
                    parentId,                                   // folder ID
                    getNativeFormat(ext),                       // native type
                    FilenameUtils.getBaseName(file.getName()),  // process name
                    "1.0",                                      // version number
                    new FileInputStream(file),                  // XML serialization of the process
                    "domain",
                    "documentation",
                    now,                                        // creation timestamp
                    now,                                        // last modification timestamp
                    true,                                       // make public?
                    noCanoniserParameters);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load file {} due to {}", file.getName(), e.getMessage());
        }
    }

    /* creates a folder in apromore. */
    private void createFolder(final File file) throws Exception {
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
            manager.createFolder(user, file.getName(), parentId);
            LOGGER.info(file + " created");
        } else {
            LOGGER.info(file + " already exists");
        }
    }

    private int getFolderId(final File file) {
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

    private String getNativeFormat(String ext) {
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
