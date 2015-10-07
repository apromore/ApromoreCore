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
            bot.run();
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
