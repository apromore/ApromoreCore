package org.apromore.pql.indexer;

// Java 2 Standard Edition
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Third party packages
import org.pql.api.PQLAPI;
import org.pql.bot.IPQLBotPersistenceLayer;
import org.pql.bot.PQLBotPersistenceLayerMySQL;
import org.pql.logic.ThreeValuedLogicType;
import org.pql.index.IndexType;
import org.pql.label.LabelManagerType;

/**
 * Bean for access to <code>site.properties</code>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class PQLIndexerConfigurationBean {

    /** Maximum number of indexing threads; <code>null</code> means limited only by the JVM's maximum concurrent processes */
    private Integer threadLimit;

    /** Whether PQL indexing is enabled at all. */
    private boolean isIndexingEnabled;

    /** Time between checks for unindexed models, in seconds. */
    private int defaultBotSleepTime;;

    /** Time between checks for unindexed models, in seconds. */
    private int defaultBotMaxIndexTime;

    /** PQL library object. */
    private PQLAPI pqlAPI = null;

    /** PQL library object. */
    private IPQLBotPersistenceLayer pqlBotPersistenceLayer = null;

    /**
     * This constructor is invoked by a <code>bean</code> element in <code>pqlIndexerContext.xml</code>.
     *
     * @param threadLimit
     * @param isIndexingEnabled
     * @param labelSimilaritySearch
     * @param defaultLabelSimilarity    number within the interval 0 to 1
     * @param indexedLabelSimilarities  comma-delimited list of numbers within the interval 0 to 1
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
    public PQLIndexerConfigurationBean(Integer threadLimit,
                                       boolean isIndexingEnabled,
                                       String  labelSimilaritySearch,
                                       double  defaultLabelSimilarity,
                                       String  indexedLabelSimilarities,
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

        this.threadLimit            = threadLimit;
        this.isIndexingEnabled      = isIndexingEnabled;
        this.defaultBotSleepTime    = defaultBotSleepTime;
        this.defaultBotMaxIndexTime = defaultBotMaxIndexTime;

        if (isIndexingEnabled) {

            // Initialize indexedLabelSimilarities
            final Set<Double> indexedLabelSimilaritiesSet = new HashSet<>();
            for (String indexedLabelSimilarity: Arrays.asList(indexedLabelSimilarities.split(","))) {
                try {
                    indexedLabelSimilaritiesSet.add(Double.parseDouble(indexedLabelSimilarity));
                } catch (NumberFormatException e) {
                    throw new PQLIndexerConfigurationException("Misconfigured pql.indexedLabelSimilarities " + indexedLabelSimilarities, e);
                }
            }

            try {
                this.pqlAPI = new PQLAPI(
                    mysqlURL, mysqlUser, mysqlPassword,
                    postgresHost, postgresName, postgresUser, postgresPassword,
                    lolaDir,
                    ThreeValuedLogicType.KLEENE,
                    IndexType.PREDICATES,
                    Enum.valueOf(LabelManagerType.class, labelSimilaritySearch),
                    defaultLabelSimilarity,
                    indexedLabelSimilaritiesSet
                );

                this.pqlBotPersistenceLayer = new PQLBotPersistenceLayerMySQL( mysqlURL, mysqlUser, mysqlPassword) {};

            } catch (ClassNotFoundException|SQLException e) {
                throw new PQLIndexerConfigurationException("Unable to create PQL API", e);
            }
        }
    }

    public Integer                 getThreadLimit()            { return threadLimit;            }
    public boolean                 isIndexingEnabled()         { return isIndexingEnabled;      }
    public int                     getDefaultBotSleepTime()    { return defaultBotSleepTime;    }
    public int                     getDefaultBotMaxIndexTime() { return defaultBotMaxIndexTime; }
    public PQLAPI                  getPQLAPI()                 { return pqlAPI;                 }
    public IPQLBotPersistenceLayer getPQLBotPersistenceLayer() { return pqlBotPersistenceLayer; }
}
