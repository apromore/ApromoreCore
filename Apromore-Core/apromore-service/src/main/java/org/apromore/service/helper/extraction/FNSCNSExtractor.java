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
public class FNSCNSExtractor {

    private static final Logger log = LoggerFactory.getLogger(FNSCNSExtractor.class);

    @SuppressWarnings("unchecked")
    public static Node extract(RFragment2 f, RFragment2 cf, Canonical g) {
        Node childB1 = cf.getEntry();
        Node newChildB1 = FragmentUtil.duplicateVertex(childB1, g);
        FragmentUtil.reconnectBoundary1(cf, childB1, newChildB1);

        Node childB2 = cf.getExit();
        Node newChildB2 = FragmentUtil.duplicateVertex(childB2, g);
        FragmentUtil.reconnectBoundary2(cf, childB2, newChildB2);

        f.removeNodes(cf.getNodes());

        Node fragmentB1 = f.getEntry();
        Node fragmentB2 = f.getExit();

        Node pocket = new Node("Pocket");
        g.addNode(pocket);
        g.setNodeProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
        f.addNode(pocket);

        if (f.getNodes().contains(childB1))
            f.addEdge(childB1, pocket);
        else {
            f.addEdge(fragmentB1, pocket);
            log.error("CHILD B1 IS NOT IN FRAGMENT! Fragment: " + FragmentUtil.getFragmentType(f) + " Child fragment: " + FragmentUtil.getFragmentType(cf));
        }

        if (f.getNodes().contains(childB2))
            f.addEdge(pocket, childB2);
        else {
            f.addEdge(pocket, fragmentB2);
            log.error("CHILD B2 IS NOT IN FRAGMENT! Fragment: " + FragmentUtil.getFragmentType(f) + " Child fragment: " + FragmentUtil.getFragmentType(cf));
        }

        return pocket;
    }
}
