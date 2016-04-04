package org.apromore.manager.client;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Helper class to retrieve an instance of the ManagerService
 */
public final class ManagerServiceLoader {

    private ManagerServiceLoader() {
    }

    public static ManagerService getInstance(ServletContext servletContext) {
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        return (ManagerService) applicationContext.getAutowireCapableBeanFactory().getBean("managerClient");
    }

}