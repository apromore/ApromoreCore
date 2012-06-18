package org.apromore.service.helper.extraction;

import org.apromore.graph.JBPT.CPF;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.graph.algo.tctree.TCType;
import org.jbpt.pm.FlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class Extractor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Extractor.class);

	/**
	 *  Removes the content of the f c from f considering the f combinations.
	 *  e.g. (f = S, c = P), (f = P, c = P), etc.
	 *  Replaces the content of c in f by a pocket, and returns the ID of the newly inserted
	 *  pocket. Both fragments f and c will be modified according the f types.
	 *  
	 * @param f Parent f
	 * @param c Child f
	 * @return ID of the pocket inserted by replacing the child f
	 */
	public static FlowNode extractChildFragment(RPSTNode f, RPSTNode c, RPST rpst, CPF g) {
        FlowNode pocket = null;
		if (f.getType().equals(TCType.P)) {
			if (c.getType() != TCType.P) {
                LOGGER.debug("Processing FS CNS");
				pocket = FSCNSExtractor.extract(f, c, g);
                LOGGER.debug("Pocket Id: " + pocket.getId());
			}
		} else {
			if (c.getType().equals(TCType.P)) {
                LOGGER.debug("Processing FNS CS");
				pocket = FNSCSExtractor.extract(f, c, rpst, g);
                LOGGER.debug("Pocket Id: " + pocket.getId());
			} else {
                LOGGER.debug("Processing FNS CNS");
				pocket = FNSCNSExtractor.extract(f, c, rpst, g);
                LOGGER.debug("Pocket Id: " + pocket.getId());
			}
		}
		return pocket;
	}
}
