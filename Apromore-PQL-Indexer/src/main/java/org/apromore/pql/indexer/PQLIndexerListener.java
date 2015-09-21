package org.apromore.pql.indexer;

// Java 2 Standard Edition
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// Java 2 Enterprise Edition
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// Third party packages
import org.pql.api.PQLAPI;
import org.pql.bot.IPQLBotPersistenceLayer;
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

    private final ThreadGroup threadGroup = new ThreadGroup("PQL indexers");

    public void contextInitialized(ServletContextEvent event) {

        // Obtain configuration
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        final PQLIndexerConfigurationBean config = (PQLIndexerConfigurationBean) applicationContext.getAutowireCapableBeanFactory().getBean("pqlIndexerConfig");

        if (!config.isIndexingEnabled()) {
            LOGGER.info("PQL Indexer disabled: pql.enableIndexing is configured to be false.  No indexer threads will be created.");
            return;
        }
        assert config.isIndexingEnabled();

        // Determine now many threads to spawn
        int maxThreadCount = Runtime.getRuntime().availableProcessors();
        if (maxThreadCount < 1) {
            maxThreadCount = 1;
        }
        else if (maxThreadCount > config.getThreadLimit()) {
            maxThreadCount = config.getThreadLimit();
        }
        assert maxThreadCount >= 1;
        assert maxThreadCount <= config.getThreadLimit();

        LOGGER.info("PQL Indexer initialized with " + maxThreadCount + " threads, " +
                    "polling every " + config.getDefaultBotSleepTime() + "s, " +
                    "timing out after " + config.getDefaultBotMaxIndexTime() + "s");

        for (int i = 1; i <= maxThreadCount; i++) {
            Thread indexerThread = new Thread(threadGroup, "pql-indexer-" + i) {
                public void run() {
                    PQLAPI                  pqlAPI     = config.getPQLAPI();
                    IPQLBotPersistenceLayer botPersist = config.getPQLBotPersistenceLayer();

                    // Ensure that this bot name doesn't already exist in the PQL database
                    try {
                        if (botPersist.isAlive(getName())) {
                            LOGGER.error(getName() + " still exists in the PQL database; terminating prematurely");
                            return;
                        }
                    } catch (SQLException e) {
                        LOGGER.error("Unable to check whether " + getName() + " still exists in the PQL database; terminating prematurely", e);
                        return;
                    }

                    // Continuously poll for unindexed models
                    while (!interrupted()) {
                        try {
                            LOGGER.info("Check for pending jobs");

                            int jobID = botPersist.getNextIndexJobID();
                            if (jobID<=0) {
                                LOGGER.info("There are no pending jobs");
                            } else {
                                LOGGER.info("Fetched new job with ID " + jobID);

                                // claim job
                                botPersist.claimIndexJob(jobID, getName());
                                LOGGER.info("Claim job with ID " + jobID);

                                // start job
                                if (!botPersist.startIndexJob(jobID, getName())) {
                                    LOGGER.warn("Failed to claim job with ID " + jobID);
                                } else {
                                    LOGGER.info("Claimed job with ID " + jobID);

                                    // check if model can be indexed
                                    if (pqlAPI.checkNetSystem(jobID)) {
                                        LOGGER.info("The model for job with ID " + jobID + " can be indexed");

                                        // index
                                        LOGGER.info("Start indexing job with ID " + jobID);
                                        try {
                                            if (pqlAPI.index(jobID)) {
                                                botPersist.finishIndexJob(jobID, getName());
                                                LOGGER.info("Finished indexing job with ID " + jobID);
                                            }

                                        } catch (SQLException e) {
                                            LOGGER.warn("Unable to finish job with ID " + jobID, e);
                                            pqlAPI.deleteIndex(jobID);
                                        }
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            LOGGER.warn("Unable to check for jobs", e);
                        }

                        // Sleep until next poll
                        try {
                            LOGGER.info("Sleeping for " + config.getDefaultBotSleepTime() + " seconds");
                            sleep(config.getDefaultBotSleepTime() * 1000);
                        } catch (InterruptedException e) {
                            interrupt();
                        }
                    }
                    assert interrupted();

                    LOGGER.info("Terminated");
                }
            };
            indexerThread.setPriority(Thread.MIN_PRIORITY);
            indexerThread.start();
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.info("PQL Indexer destroyed");
        threadGroup.interrupt();
    }
}
