package org.apromore.service.helper.extraction;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Node;
import org.apromore.service.model.RFragment2;
import org.apromore.util.FragmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class FNSCSExtractor {

    private static final Logger log = LoggerFactory.getLogger(FNSCSExtractor.class);

    @SuppressWarnings("unchecked")
    public static Node extract(RFragment2 f, RFragment2 cf, Canonical g) {
        Node originalChildB1 = cf.getEntry();
        Node originalChildB2 = cf.getExit();

        //Node[] originalChildBoundary = FragmentProcesser.preprocessFragmentV2(cf, f, g);

        Node childB1 = cf.getEntry();
        if (childB1.getId().equals(originalChildB1.getId())) {
            Node newChildB1 = FragmentUtil.duplicateVertex(childB1, g);
            FragmentUtil.reconnectBoundary1(cf, childB1, newChildB1);
        }

        Node childB2 = cf.getExit();
        if (childB2.getId().equals(originalChildB2.getId())) {
            Node newChildB2 = FragmentUtil.duplicateVertex(childB2, g);
            FragmentUtil.reconnectBoundary2(cf, childB2, newChildB2);
        }

        f.removeNodes(cf.getVertices());

        Node fragmentB1 = f.getEntry();
        Node fragmentB2 = f.getExit();

        Node pocket = new Node("Pocket");
        g.setNodeProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
        f.addNode(pocket);

        if (f.getNodes().contains(originalChildB1)) {
            f.addEdge(originalChildB1, pocket);
        } else {
            f.addEdge(fragmentB1, pocket);
        }

        if (f.getNodes().contains(originalChildB2)) {
            f.addEdge(pocket, originalChildB2);
        } else {
            f.addEdge(pocket, fragmentB2);
        }

        if (Constants.CONNECTOR.equals(g.getNodeProperty(childB1.getId(), Constants.TYPE))) {
            if (g.getDirectSuccessors(childB1).size() == 1) {
                log.debug("NEW CHILD BOUNDARY CONNECTOR B1");
                log.debug(FragmentUtil.fragmentToString(cf, g));
            }
        }

        if (Constants.CONNECTOR.equals(g.getNodeProperty(childB2.getId(), Constants.TYPE))) {
            if (g.getDirectPredecessors(childB2).size() == 1) {
                log.debug("NEW CHILD BOUNDARY CONNECTOR B2");
                log.debug(FragmentUtil.fragmentToString(cf, g));
            }
        }

        return pocket;
    }

}
