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

package org.apromore.service.impl;

import org.apromore.service.LolaDirBean;
import org.apromore.service.PqlBean;
import org.jbpt.persist.MySQLConnection;
import org.pql.api.IPQLAPI;
import org.pql.api.PQLAPI;
import org.pql.core.IPQLBasicPredicatesOnTasks;
import org.pql.core.PQLBasicPredicatesMC;
import org.pql.core.PQLBasicPredicatesMySQL;
import org.pql.index.IndexType;
import org.pql.index.PQLIndexMySQL;
import org.pql.label.ILabelManager;
import org.pql.label.LabelManagerLevenshtein;
import org.pql.label.LabelManagerLuceneVSM;
import org.pql.label.LabelManagerThemisVSM;
import org.pql.label.LabelManagerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

/**
 * Created by corno on 23/08/2014.
 */
public class PqlBeanImpl implements PqlBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PqlBeanImpl.class);


    private LolaDirBean lolaDir;
    private MySqlBeanImpl mySqlBean;
    private PGBeanImpl pgBean;
    private boolean indexingEnabled;
    private int numberOfQueryThreads;
    private String labelSimilaritySearch;
    private String labelSimilarityConfig;
    private LabelManagerType labelManagerType;
    private final double defaultLabelSimilarityThreshold;
    private final Set<Double> indexedLabelSimilarityThresholds = new HashSet<Double>();
    private long defaultBotSleepTime;
    private long defaultBotMaxIndexTime;

    private PQLAPI api = null;

    /**
    * @throws IllegalArgumentException if <var>labelSimilaritySearch</var> isn't one of the
    *   values <code>LEVENSHTEIN</code>, <code>LUCENE</code> or <code>THEMIS_VSM</code>.
    * @throws IllegalArgumentException if <var>indexedLabelSimilarityThresholds</var> isn't
    *   a comma-delimited list of floating-point numbers.
    */
    @Inject
    public PqlBeanImpl(LolaDirImpl lolaDir,
                       MySqlBeanImpl mySqlBean,
                       PGBeanImpl pgBean,
                       boolean indexingEnabled,
                       int numberOfQueryThreads,
                       String labelSimilaritySearch,
                       String labelSimilarityConfig,
                       double defaultLabelSimilarityThreshold,
                       String indexedLabelSimilarityThresholds,
                       long defaultBotSleepTime,
                       long defaultBotMaxIndexTime){

        LOGGER.info("Manager configured with:" +
            " pql.lola.dir=" + lolaDir.getLolaDir() +
            " pql.mysql.url=" + mySqlBean.getURL() +
            " pql.mysql.user=" + mySqlBean.getUser() +
            " pql.postgres.host=" + pgBean.getHost() +
            " pql.postgres.name=" + pgBean.getName() +
            " pql.postgres.user=" + pgBean.getUser() +
            " pql.enableIndexing=" + indexingEnabled +
            " pql.numberOfQueryThreads=" + numberOfQueryThreads +
            " pql.labelSimilaritySearch=" + labelSimilaritySearch +
            " pql.labelSimilarityConfig=" + labelSimilarityConfig +
            " pql.defaultLabelSimilarityThreshold=" + defaultLabelSimilarityThreshold +
            " pql.indexedLabelSimilarityThresholds=" + indexedLabelSimilarityThresholds +
            " pql.defaultBotSleepTime=" + defaultBotSleepTime +
            " pql.defaultBotMaxIndexTime=" + defaultBotMaxIndexTime);

        File lolaPath = new File(lolaDir.getLolaDir());
        if (indexingEnabled && !lolaPath.isFile()) {
            LOGGER.error("LoLA 2.0 executable not found at " + lolaDir.getLolaDir());
        }

        this.lolaDir               = lolaDir;
        this.mySqlBean             = mySqlBean;
        this.pgBean                = pgBean;
        this.indexingEnabled       = indexingEnabled;
        this.numberOfQueryThreads  = numberOfQueryThreads;
        this.labelSimilaritySearch = labelSimilaritySearch;
        this.labelSimilarityConfig = labelSimilarityConfig;
        this.labelManagerType      = Enum.valueOf(LabelManagerType.class, labelSimilaritySearch);
        this.defaultLabelSimilarityThreshold = defaultLabelSimilarityThreshold;

        this.indexedLabelSimilarityThresholds.clear();  // Redundant, but makes the code easier to understand
        for (String indexedLabelSimilarityThreshold: Arrays.asList(indexedLabelSimilarityThresholds.split(","))) {
            try {
                this.indexedLabelSimilarityThresholds.add(Double.parseDouble(indexedLabelSimilarityThreshold));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Misconfigured pql.indexedLabelSimilarityThresholds " + indexedLabelSimilarityThresholds, e);
            }
        }

        this.defaultBotSleepTime    = defaultBotSleepTime;
        this.defaultBotMaxIndexTime = defaultBotMaxIndexTime;
    }

    @Override
    public PQLAPI getApi() {

        if (api == null) {
            try {
                ILabelManager     labelMngr;

                MySQLConnection mysql = new MySQLConnection(mySqlBean.getURL(), mySqlBean.getUser(), mySqlBean.getPassword());
                LOGGER.info("Created a PQL database connection");

                switch (labelManagerType) {
                case LEVENSHTEIN:
                    labelMngr = new LabelManagerLevenshtein(
                        mysql.getConnection(),
                        defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholds
                    );
                    break;
                case LUCENE:
                    labelMngr = new LabelManagerLuceneVSM(
                        mysql.getConnection(),
                        defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholds, labelSimilarityConfig
                    );
                    break;
                case THEMIS_VSM:
                    labelMngr = new LabelManagerThemisVSM(
                        mysql.getConnection(),
                        pgBean.getHost(), pgBean.getName(), pgBean.getUser(), pgBean.getPassword(),
                        defaultLabelSimilarityThreshold, indexedLabelSimilarityThresholds
                    );
                    break;
                default:
                    throw new RuntimeException("Label similiarity search property was \"" + labelSimilaritySearch + "\"; valid options are \"LEVENSHTEIN\", \"LUCENE\" or \"THEMIS_VSM\"");
                }

                api = new PQLAPI(mySqlBean.getURL(),
                              mySqlBean.getUser(),
                              mySqlBean.getPassword(),

                              pgBean.getHost(),
                              pgBean.getName(),
                              pgBean.getUser(),
                              pgBean.getPassword(),

                              (String) lolaDir.getLolaDir(),
                              labelSimilarityConfig,
                              IndexType.PREDICATES,
                              labelManagerType,
                              defaultLabelSimilarityThreshold,
                              indexedLabelSimilarityThresholds,
                              numberOfQueryThreads,
                              defaultBotMaxIndexTime,
                              defaultBotSleepTime);

            } catch(ClassNotFoundException | IOException | SQLException e){
                LOGGER.error("Failed to initialize PQL API", e);
                throw new RuntimeException("Failed to initialize PQL API", e);
            }
        }
        return api;
    }

    @Override
    public boolean isIndexingEnabled() {
        return indexingEnabled;
    }

    @Override
    public String getLabelSimilaritySearch() {
        return labelSimilaritySearch;
    }
}
