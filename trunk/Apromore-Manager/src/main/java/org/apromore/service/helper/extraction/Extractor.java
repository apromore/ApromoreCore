package org.apromore.service.helper.extraction;

import org.apromore.exception.RepositoryException;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.model.FragmentNode;
import org.apromore.util.FragmentUtil;
import org.jbpt.algo.tree.tctree.TCType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class Extractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Extractor.class);

    /**
     * Removes the content of the f c from f considering the f combinations.
     * e.g. (f = S, c = P), (f = P, c = P), etc.
     * Replaces the content of c in f by a pocket, and returns the ID of the newly inserted
     * pocket. Both fragments f and c will be modified according the f types.
     *
     * @param parent Parent f
     * @param child Child f
     * @return ID of the pocket inserted by replacing the child f
     */
    public static CPFNode extractChildFragment(final FragmentNode parent, final FragmentNode child, final Canonical g) throws RepositoryException {
        CPFNode pocket;
        if (parent.getType().equals(TCType.POLYGON)) {
            if (child.getType() != TCType.POLYGON) {
                LOGGER.info("Processing FS CNS");
                pocket = FSCNSExtractor.extract(parent, child, g);
                LOGGER.info("Pocket Id: " + pocket.getId());
            } else {
                LOGGER.info("Processing FS CS");
                pocket = FSCSExtractor.extract(parent, child, g);
                LOGGER.info("Pocket Id: " + pocket.getId());
            }
        } else {
            if (child.getType().equals(TCType.POLYGON)) {
                LOGGER.info("Processing FNS CS");
                pocket = FNSCSExtractor.extract(parent, child, g);
                LOGGER.info("Pocket Id: " + pocket.getId());
            } else {
                LOGGER.info("Processing FNS CNS");
                pocket = FNSCNSExtractor.extract(parent, child, g);
                LOGGER.info("Pocket Id: " + pocket.getId());
            }
        }
        return pocket;
    }
}
