package org.apromore.pql.indexer;

// Java 2 Standard Edition
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Third party packages
import org.pql.bot.PQLBot;
import org.pql.core.PQLBasicPredicatesMC;
import org.pql.index.IndexType;
import org.pql.index.PQLIndexMySQL;
import org.pql.label.ILabelManager;
import org.pql.label.LabelManagerLevenshtein;
import org.pql.label.LabelManagerLuceneVSM;
import org.pql.label.LabelManagerThemisVSM;
import org.pql.label.LabelManagerType;
import org.pql.logic.IThreeValuedLogic;
import org.pql.logic.KleeneLogic;
import org.pql.mc.LoLA2ModelChecker;
import org.slf4j.LoggerFactory;

/**
 * Bean for access to <code>site.properties</code>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class PQLIndexerConfigurationBean {

    private String mysqlURL;
    private String mysqlUser;
    private String mysqlPassword;

    /** Whether PQL indexing is enabled at all. */
    private boolean isIndexingEnabled;

    /** Time between checks for unindexed models, in seconds. */
    private int defaultBotSleepTime;

    /** Time between checks for unindexed models, in seconds. */
    private int defaultBotMaxIndexTime;

    private IndexType indexType;
    private LoLA2ModelChecker mc;
    private PQLIndexMySQL index;

    /**
     * This constructor is invoked by a <code>bean</code> element in <code>pqlIndexerContext.xml</code>.
     *
     * @param isIndexingEnabled
     * @param labelSimilaritySearch
     * @param defaultLabelSimilarityThreshold   number within the interval 0 to 1
     * @param indexedLabelSimilarityThresholds  comma-delimited list of numbers within the interval 0 to 1
     * @param defaultBotSleepTime       seconds
     * @param defaultBotMaxIndexTime    seconds
     * @param lolaDir                   file in the local filesystem
     * @param mysqlURL                  JDBC URL
     * @param mysqlUser
     * @param mysqlPassword
     * @param postgresHost              DNS address
     * @param postgresName
     * @param postgresUser
     * @param postgresPassword
     * @throws PQLIndexerConfigurationException  if the bean can't be created due to problems with <var>site.properties</var>
     */
    public PQLIndexerConfigurationBean(boolean isIndexingEnabled,
                                       String  labelSimilaritySearch,
                                       String  labelSimilarityConfig,
                                       double  defaultLabelSimilarityThreshold,
                                       String  indexedLabelSimilarityThresholds,
                                       int     defaultBotSleepTime,
                                       int     defaultBotMaxIndexTime,
                                       String  lolaDir,
                                       String  mysqlURL,
                                       String  mysqlUser,
                                       String  mysqlPassword,
                                       String  postgresHost,
                                       String  postgresName,
                                       String  postgresUser,
                                       String  postgresPassword) throws PQLIndexerConfigurationException {

        LoggerFactory.getLogger(getClass()).info("PQL Indexer configured with:" + 
            " pql.enableIndexing=" + isIndexingEnabled +
            " pql.labelSimilaritySearch=" + labelSimilaritySearch +
            " pql.labelSimilarityConfig=" + labelSimilarityConfig +
            " pql.defaultLabelSimilarityThreshold=" + defaultLabelSimilarityThreshold +
            " pql.indexedLabelSimilarityThresholds=" + indexedLabelSimilarityThresholds +
            " pql.defaultBotSleepTime=" + defaultBotSleepTime +
            " pql.defaultBotMaxIndexTime=" + defaultBotMaxIndexTime +
            " pql.lola.dir=" + lolaDir +
            " pql.mysql.url=" + mysqlURL +
            " pql.mysql.user=" + mysqlUser +
            " pql.postgres.host=" + postgresHost +
            " pql.postgres.name=" + postgresName +
            " pql.postgres.user=" + postgresUser);

        this.mysqlURL               = mysqlURL;
        this.mysqlUser              = mysqlUser;
        this.mysqlPassword          = mysqlPassword;

        this.isIndexingEnabled      = isIndexingEnabled;
        this.defaultBotSleepTime    = defaultBotSleepTime;
        this.defaultBotMaxIndexTime = defaultBotMaxIndexTime;

        this.indexType = IndexType.PREDICATES;
        this.mc        = new LoLA2ModelChecker(lolaDir);

        // Initialize indexedLabelSimilarities
        final Set<Double> indexedLabelSimilarityThresholdsSet = new HashSet<>();
        for (String indexedLabelSimilarityThreshold: Arrays.asList(indexedLabelSimilarityThresholds.split(","))) {
            try {
                indexedLabelSimilarityThresholdsSet.add(Double.parseDouble(indexedLabelSimilarityThreshold));
            } catch (NumberFormatException e) {
                throw new PQLIndexerConfigurationException("Misconfigured pql.indexedLabelSimilarityThresholds " + indexedLabelSimilarityThresholds, e);
            }
        }

        IThreeValuedLogic    logic     = new KleeneLogic();
        PQLBasicPredicatesMC bp        = new PQLBasicPredicatesMC(mc, logic);
        
        // Initialize labelManager
        ILabelManager labelManager;
        try {
            switch (LabelManagerType.valueOf(labelSimilaritySearch)) {
            case LEVENSHTEIN:
                labelManager = new LabelManagerLevenshtein(mysqlURL, mysqlUser, mysqlPassword,
                                                           defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet);
                break;
            case LUCENE:
                labelManager = new LabelManagerLuceneVSM(mysqlURL, mysqlUser, mysqlPassword,
                                                         defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet,
                                                         labelSimilarityConfig);
                break;
            case THEMIS_VSM:
                labelManager = new LabelManagerThemisVSM(mysqlURL, mysqlUser, mysqlPassword,
                                                         postgresHost, postgresName, postgresUser, postgresPassword,
                                                         defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet);
                break;
            default:
                throw new PQLIndexerConfigurationException("Misconfigured pql.labelSimilaritySearch " + labelSimilaritySearch);
            }
        } catch (ClassNotFoundException | IOException | SQLException e) {
            throw new PQLIndexerConfigurationException("Unable to create label manager", e);
        }
        assert labelManager != null;

        // Initialize index
        try {
            index = new PQLIndexMySQL(mysqlURL, mysqlUser, mysqlPassword,
                                      bp, labelManager, mc, logic,
                                      defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet, indexType,
                                      defaultBotMaxIndexTime, defaultBotSleepTime);

        } catch (ClassNotFoundException | SQLException e) {
            throw new PQLIndexerConfigurationException("Unable to create index", e);
        }
        assert index != null;
    }

    public boolean isIndexingEnabled() { return isIndexingEnabled; }

    public PQLBot createBot() throws PQLIndexerConfigurationException {
        try {
            return new PQLBot(mysqlURL, mysqlUser, mysqlPassword,
                              null,  // bot name will be a random UUID
                              index,
                              mc,
                              indexType,
                              defaultBotMaxIndexTime,
                              defaultBotSleepTime,
                              true  /* verbose */);
        } catch (ClassNotFoundException | SQLException e) {
            throw new PQLIndexerConfigurationException("Unable to create bot", e);
        }
    }
}
