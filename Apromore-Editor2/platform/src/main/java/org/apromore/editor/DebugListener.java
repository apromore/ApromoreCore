package org.apromore.editor;

// Java 2 Enterprise Edition
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Logs the configuration passed to the web application bundle.
 *
 * @author @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class DebugListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugListener.class.getCanonicalName());

    // Methods implementing ServletContext Listener

    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("Editor starting");

        // Obtain configuration
        final ConfigBean config = (ConfigBean) WebApplicationContextUtils.getWebApplicationContext(event.getServletContext())
                                                                         .getAutowireCapableBeanFactory()
                                                                         .getBean("editorConfig");
    }
    
    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.info("Editor destroyed");
    }
}
