package org.apromore.tools.cpfimporter;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Set;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.ImportProcessResultType;
import org.apromore.model.UsernamesType;
import org.apromore.plugin.property.RequestParameterType;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple application to import CPF and ANF files into Apromore so testing can be performed.
 */
public final class Import {

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

        //ShowAllUsers();
        //CreateFolder(arg0);
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

    private void CreateFolder(final String name) throws Exception {
        final ManagerService manager = (ManagerService) getBean("managerClient");
        final String user = "raboczi";

        manager.createFolder(user, name, 0);
    }

    private void UploadProcess(final File file) throws Exception {
        final ManagerService manager = (ManagerService) getBean("managerClient");
        final String user = "admin";
        final Set<RequestParameterType<?>> noCanoniserParameters = Collections.<RequestParameterType<?>>emptySet();

        ImportProcessResultType result = manager.importProcess(
            user,
            "AML fragment",
            file.getName(),
            Double.valueOf("1.0"),  // versionNumber,
            new FileInputStream(file),
            "domain",
            "documentation",
            "created",
            "lastUpdate",
            noCanoniserParameters
        );
    }
}
