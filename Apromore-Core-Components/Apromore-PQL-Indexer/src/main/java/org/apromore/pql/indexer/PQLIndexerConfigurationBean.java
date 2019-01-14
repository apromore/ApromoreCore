/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.pql.indexer;

// Java 2 Standard Edition
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Third party packages
import org.jbpt.persist.MySQLConnection;
import org.pql.bot.AbstractPQLBot.NameInUseException;
import org.pql.bot.PQLBot;
import org.pql.core.PQLBasicPredicatesMC;
import org.pql.index.IndexType;
import org.pql.index.PQLIndexMySQL;
import org.pql.label.ILabelManager;
import org.pql.label.LabelManagerLevenshtein;
import org.pql.label.LabelManagerLuceneVSM;
import org.pql.label.LabelManagerThemisVSM;
import org.pql.label.LabelManagerType;
import org.pql.mc.LoLA2ModelChecker;
import org.slf4j.LoggerFactory;

/**
 * Bean for access to <code>site.properties</code>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class PQLIndexerConfigurationBean {

    /** The number of PQL indexer bots to launch; if zero, indexing is disabled. */
    private int numberOfIndexerThreads;

    /** Time between checks for unindexed models, in seconds. */
    private int defaultBotSleepTime;

    /** Time between checks for unindexed models, in seconds. */
    private int defaultBotMaxIndexTime;

    private MySQLConnection mysql;
    private IndexType indexType;
    private LoLA2ModelChecker mc;
    private PQLIndexMySQL index;

    /** This is a list of process arguments suitable as input to the {@link ProcessBuilder} class. */
    private List<String> args = new ArrayList();

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
    public PQLIndexerConfigurationBean(int     numberOfIndexerThreads,
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
            " pql.numberOfIndexerThreads=" + numberOfIndexerThreads +
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

        this.numberOfIndexerThreads = numberOfIndexerThreads;
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

        PQLBasicPredicatesMC bp = new PQLBasicPredicatesMC(mc);
        
        
        try {
            this.mysql = new MySQLConnection(mysqlURL, mysqlUser, mysqlPassword);
        } catch(ClassNotFoundException | SQLException e) {
            if (this.numberOfIndexerThreads > 0) {
                throw new PQLIndexerConfigurationException("MySQL connection could not be created", e);
            } else {
                LoggerFactory.getLogger(getClass()).info("MySQL connection could not be created for PQL indexer, but this doesn't matter since indexing is disabled.");
                return;
            }
        }

        // Initialize labelManager
        ILabelManager labelManager;
        try {

            switch (LabelManagerType.valueOf(labelSimilaritySearch)) {
            case LEVENSHTEIN:
                labelManager = new LabelManagerLevenshtein(mysql.getConnection(),
                                                           defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet);
                break;
            case LUCENE:
                labelManager = new LabelManagerLuceneVSM(mysql.getConnection(),
                                                         defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet,
                                                         labelSimilarityConfig);
                break;
            case THEMIS_VSM:
                labelManager = new LabelManagerThemisVSM(mysql.getConnection(),
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
            index = new PQLIndexMySQL(mysql.getConnection(),
                                      bp, labelManager, mc,
                                      defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholdsSet, indexType,
                                      defaultBotMaxIndexTime, defaultBotSleepTime);

        } catch (ClassNotFoundException | SQLException e) {
            throw new PQLIndexerConfigurationException("Unable to create index", e);
        }
        assert index != null;


        // Process argument list

        // MySQL
        args.add("--database");    args.add(mysqlURL);
        args.add("--user");        args.add(mysqlUser);
        args.add("--password");    args.add(mysqlPassword);

        // LoLA2
        args.add("--lola");        args.add(lolaDir);

        // Similarity
        args.add("--labeltype");   args.add(labelSimilaritySearch);
        args.add("--labelrepo");   args.add(labelSimilarityConfig);
        args.add("--threshold");   args.add(Double.toString(defaultLabelSimilarityThreshold));
        args.add("--thresholds");  args.add(indexedLabelSimilarityThresholds);

        // Bot timers
        args.add("--index");       args.add(Integer.toString(defaultBotMaxIndexTime));
        args.add("--sleep");       args.add(Integer.toString(defaultBotSleepTime));
    }

    public int getNumberOfIndexerThreads() { return numberOfIndexerThreads; }

    /**
     * @param name  the name for the created bot, which should be unique; pass <code>null</code> to use a random UUID
     */
    public PQLBot createBot(String name) throws PQLIndexerConfigurationException {
        try {
            return new PQLBot(mysql.getConnection(),
                              name,
                              index,
                              mc,
                              indexType,
                              defaultBotMaxIndexTime,
                              defaultBotSleepTime);
        } catch (ClassNotFoundException | NameInUseException | SQLException e) {
            throw new PQLIndexerConfigurationException("Unable to create bot", e);
        }
    }

    /**
     * @return a list of process arguments suitable as input to the {@link ProcessBuilder} class
     */
    public List<String> getBotProcessArguments() {
        return args;
    }
}
