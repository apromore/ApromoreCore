package org.apromore.tools.cpfimporter;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.FolderType;
import org.apromore.model.ImportProcessResultType;
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

    private ApplicationContext ctx;
    private AutowireCapableBeanFactory fac;

    /* The Canonical Process Importer Starting point. */
    public static void main(String[] args) throws Exception {
        new Import(args[0]);
    }


    /**
     * Default Constructor.
     */
    public Import(final String arg0) throws Exception {
        ctx = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-managerClient.xml");
        fac = ctx.getAutowireCapableBeanFactory();

        UploadProcess(new File(arg0));
    }

    /**
     * Finds a Spring bean with the passed in name.
     * @param name the Bean name we want.
     * @return the Spring Bean or null.
     */
    public Object getBean(final String name) {
        return fac.getBean(name);
    }


    /* Test method to show we connect to the server. */
    /*
    private void ShowAllUsers() {
        ManagerService manager = (ManagerService) getBean("managerClient");

        UsernamesType users = manager.readAllUsers();
        for (String user : users.getUsername()) {
            System.out.println("User Found: " + user);
        }
    }
    */

    private void CreateFolder(final File file) throws Exception {
        final ManagerService manager = (ManagerService) getBean("managerClient");
        final String user = "ad1f7b60-1143-4399-b331-b887585a0f30";

        File parentFile = file.getParentFile();
        int parentId = 0;
        if (parentFile != null) {
            CreateFolder(parentFile);
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
        final ManagerService manager = (ManagerService) getBean("managerClient");
        final String user = "ad1f7b60-1143-4399-b331-b887585a0f30";

        List<FolderType> tree = manager.getWorkspaceFolderTree(user);
        Path path = file.toPath();

        FolderType folder = null;
        int id = 0;
        for (int i=0; i < path.getNameCount(); i++) {
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

    private void UploadProcess(final File file) throws Exception {
        final ManagerService manager = (ManagerService) getBean("managerClient");
        final String userName = "admin";
        final Set<RequestParameterType<?>> noCanoniserParameters = Collections.<RequestParameterType<?>>emptySet();

        File parentFile = file.getParentFile();
        if (parentFile != null) {
            CreateFolder(parentFile);
            int parentId = getFolderId(parentFile);
            assert parentId != -1;

            ImportProcessResultType result = manager.importProcess(
            userName, parentId,
            "EPML 2.0",
            file.getName(),
            1.0D,
            new FileInputStream(file),
            "domain",
            "documentation",
            "created",
            "lastUpdate",
            noCanoniserParameters);
        }

//        // If the process was in a directory on the filesystem, move it to a corresponding one in Apromore
//        File parentFile = file.getParentFile();
//        if (parentFile != null) {
//            CreateFolder(parentFile);
//            int parentId = getFolderId(parentFile);
//            assert parentId != -1;
//            manager.addProcessToFolder(result.getProcessSummary().getId(), parentId);
//        }
    }

}
