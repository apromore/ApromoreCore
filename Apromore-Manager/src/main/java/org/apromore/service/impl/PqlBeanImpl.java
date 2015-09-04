package org.apromore.service.impl;

import org.apromore.service.LolaDirBean;
import org.apromore.service.PqlBean;
import org.pql.api.IPQLAPI;
import org.pql.api.PQLAPI;
import org.pql.core.IPQLBasicPredicatesOnTasks;
import org.pql.core.PQLBasicPredicatesMC;
import org.pql.core.PQLBasicPredicatesMySQL;
import org.pql.index.PQLIndexMySQL;
import org.pql.label.ILabelManager;
import org.pql.label.LabelManagerLevenshtein;
import org.pql.label.LabelManagerVSM;
import org.pql.logic.IThreeValuedLogic;
import org.pql.logic.KleeneLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

/**
 * Created by corno on 23/08/2014.
 */
public class PqlBeanImpl implements PqlBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PqlBeanImpl.class);

    private final Set<Double> indexedLabelSimilarities = new HashSet<Double>();
    private final double defaultLabelSimilarity = 0.75;

    private LolaDirBean lolaDir;
    private MySqlBeanImpl mySqlBean;
    private PGBeanImpl pgBean;
    private boolean indexingEnabled;
    private String labelSimilaritySearch;

    /**
    * @throws IllegalArgumentException if <var>labelSimilaritySearch</var> isn't one of the values <code>LEVENSHTEIN</code> or <code>VSM</code>.
    */
    @Inject
    public PqlBeanImpl(LolaDirImpl lolaDir, MySqlBeanImpl mySqlBean, PGBeanImpl pgBean, boolean indexingEnabled, String labelSimilaritySearch){

        File lolaPath = new File(lolaDir.getLolaDir());
        if (indexingEnabled && !lolaPath.isFile()) {
            LOGGER.error("LoLA 2.0 executable not found at " + lolaDir.getLolaDir());
        }

        this.lolaDir               = lolaDir;
        this.mySqlBean             = mySqlBean;
        this.pgBean                = pgBean;
        this.indexingEnabled       = indexingEnabled;
        this.labelSimilaritySearch = labelSimilaritySearch;

        switch (labelSimilaritySearch) {
        case "LEVENSHTEIN":
        case "VSM":
            break;
        default:
            throw new IllegalArgumentException("Label similarity search algorithm was \"" + labelSimilaritySearch + "\"; valid options are \"LEVENSHTEIN\" or \"VSM\"");
        }

        indexedLabelSimilarities.add(new Double(0.5));
        indexedLabelSimilarities.add(new Double(0.75));
        indexedLabelSimilarities.add(new Double(1.0));
    }

    @Override
    public PQLAPI getApi() {
        try {
            IThreeValuedLogic logic = new KleeneLogic();
            ILabelManager     labelMngr;

            switch (labelSimilaritySearch) {
            case "LEVENSHTEIN":
                labelMngr = new LabelManagerLevenshtein(mySqlBean.getURL(), mySqlBean.getUser(), mySqlBean.getPassword(), defaultLabelSimilarity, indexedLabelSimilarities);
                break;
            case "VSM":
                LOGGER.info("Creating VSM label manager host=" + pgBean.getHost() + " name=" + pgBean.getName() + " user=" + pgBean.getUser() + " password=" + pgBean.getPassword());
                labelMngr = new LabelManagerVSM(
                    mySqlBean.getURL(), mySqlBean.getUser(), mySqlBean.getPassword(),
                    pgBean.getHost(), pgBean.getName(), pgBean.getUser(), pgBean.getPassword(),
                    defaultLabelSimilarity, indexedLabelSimilarities
                );
                break;
            default:
                throw new RuntimeException("Label similiarity search property was \"" + labelSimilaritySearch + "\"; valid options are \"LEVENSHTEIN\" or \"VSM\"");
            }

            return new PQLAPI(mySqlBean.getURL(),
                              mySqlBean.getUser(),
                              mySqlBean.getPassword(),

                              pgBean.getHost(),
                              pgBean.getName(),
                              pgBean.getUser(),
                              pgBean.getPassword(),

                              (String) lolaDir.getLolaDir(),
                              org.pql.logic.ThreeValuedLogicType.KLEENE,

                              org.pql.index.IndexType.PREDICATES,
                              org.pql.label.LabelManagerType.VSM,
                              defaultLabelSimilarity,
                              indexedLabelSimilarities);

        } catch(ClassNotFoundException | java.sql.SQLException e){
            //LOGGER.error("------------------" + ex.toString());
            throw new RuntimeException("Failed to initialize PQL API", e);
        }
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
