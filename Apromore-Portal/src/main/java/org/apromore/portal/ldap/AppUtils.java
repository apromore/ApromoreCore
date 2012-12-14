package org.apromore.portal.ldap;

import org.springframework.context.ApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 19/06/12
 * Time: 9:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppUtils {

    private static ApplicationContext applicationContext;

    /**
     * Return the application context.
     *
     * @return the application context
     */
    public static ApplicationContext getApplicationContext() {
        return (applicationContext);
    }

    /**
     * Set the application context.
     *
     * @param context the application context to set.
     */
    public static void setApplicationContext(final ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * Returns the Bean given the bean name
     * @param name bean name
     * @return bean instance
     */
    public static Object getBean(final String name) {
        if(applicationContext == null) {
            throw new IllegalArgumentException("ApplicationContext is not initialized");
        }
        return applicationContext.getBean(name);
    }
}

