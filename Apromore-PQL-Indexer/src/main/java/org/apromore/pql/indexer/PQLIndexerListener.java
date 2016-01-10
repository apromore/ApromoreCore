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

package org.apromore.pql.indexer;

// Java 2 Standard Edition
import java.sql.SQLException;

// Java 2 Enterprise Edition
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// Third party packages
import org.pql.bot.PQLBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Manages a group of PQL indexer threads as long as the servlet context exists.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Controller
public class PQLIndexerListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PQLIndexerListener.class.getCanonicalName());

    private PQLBot bot = null;

    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("PQL Indexer starting");

        // Obtain configuration
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        final PQLIndexerConfigurationBean config = (PQLIndexerConfigurationBean) applicationContext.getAutowireCapableBeanFactory().getBean("pqlIndexerConfig");

        if (!config.isIndexingEnabled()) {
            LOGGER.info("PQL Indexer disabled: pql.enableIndexing is configured to be false.  No indexer threads will be created.");
            return;
        }
        assert config.isIndexingEnabled();

        // Create PQL bot
        try {
            bot = config.createBot();
            bot.setDaemon(true);
            bot.start();
            LOGGER.info("PQL Indexer created bot");

        } catch (PQLIndexerConfigurationException e) {
            LOGGER.error("PQL Indexer unable to create bot", e);
        }

        LOGGER.info("PQL Indexer started");
    }

    public void contextDestroyed(ServletContextEvent event) {
        bot.terminate();
        LOGGER.info("PQL Indexer destroyed");
    }
}
