package org.apromore.tools.cpfimporter;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apromore.manager.client.ManagerService;
import org.apromore.model.FolderType;
import org.apromore.plugin.property.RequestParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple application to import CPF and ANF files into Apromore so testing can be performed.
 */
public final class Import {

    private static final Logger LOGGER = LoggerFactory.getLogger(Import.class.getName());

    private AutowireCapableBeanFactory fac;
    private ManagerService manager;

    /* The Canonical Process Importer Starting point. */
    public static void main(String[] args) throws Exception {
        new Import(args[0]);
    }


    /**
     * Default Constructor.
     */
    public Import(final String arg0) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-managerClient.xml");
        fac = ctx.getAutowireCapableBeanFactory();
        manager = (ManagerService) getBean("managerClient");

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

                manager.importProcess(userName, parentId, getNativeFormat(ext), FilenameUtils.getBaseName(file.getName()),
                        1.0D, new FileInputStream(file),
                        "domain",
                        "documentation",
                        "created",
                        "lastUpdate", noCanoniserParameters);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load file {} due to {}" , file.getName(), e.getMessage());
        }
    }

    /* creates a folder in apromore. */
    private void createFolder(final File file) throws Exception {
        final String user = "ad1f7b60-1143-4399-b331-b887585a0f30";

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
                return -1;  // folder has a nonexistent parent
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
            return "XPDL 2.1";
        } else if (ext.equalsIgnoreCase("yawl")) {
            return "YAWL 2.2";
        } else if (ext.equalsIgnoreCase("pnml")) {
            return "PNML 1.3.2";
        } else if (ext.equalsIgnoreCase("aml")) {
            return "AML fragment";
        }
        return null;
    }



    /* Finds a Spring bean with the passed in name. */
    private Object getBean(final String name) {
        return fac.getBean(name);
    }

}
