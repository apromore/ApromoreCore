/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
