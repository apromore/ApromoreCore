package org.apromore.tools.cpfimporter;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.UsernamesType;
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
    public static void main(String[] args) {
        new Import();
    }


    /**
     * Default Constructor.
     */
    public Import() {
        ctx = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-managerClient.xml");
        fac = ctx.getAutowireCapableBeanFactory();

        ShowAllUsers();
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
    private void ShowAllUsers() {
        ManagerService manager = (ManagerService) getBean("managerClient");

        UsernamesType users = manager.readAllUsers();
        for (String user : users.getUsername()) {
            System.out.println("User Found: " + user);
        }
    }

}
