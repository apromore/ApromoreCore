package org.apromore.service.helper.extraction;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.model.fragmentNode;
import org.apromore.util.FragmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class FNSCNSExtractor {

    private static final Logger log = LoggerFactory.getLogger(FNSCNSExtractor.class);

    @SuppressWarnings("unchecked")
    public static CPFNode extract(fragmentNode f, fragmentNode cf, Canonical g) {
        CPFNode childB1 = cf.getEntry();
        CPFNode newChildB1 = FragmentUtil.duplicateNode(childB1, g);
        FragmentUtil.reconnectBoundary1(cf, childB1, newChildB1);

        CPFNode childB2 = cf.getExit();
        CPFNode newChildB2 = FragmentUtil.duplicateNode(childB2, g);
        FragmentUtil.reconnectBoundary2(cf, childB2, newChildB2);

        f.removeNodes(cf.getNodes());

        CPFNode fragmentB1 = f.getEntry();
        CPFNode fragmentB2 = f.getExit();

        CPFNode pocket = new CPFNode();
        pocket.setName("Pocket");
        g.addNode(pocket);
        g.setNodeProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
        f.addNode(pocket);

        if (f.getNodes().contains(childB1)) {
            f.addEdge(childB1, pocket);
        } else {
            f.addEdge(fragmentB1, pocket);
            log.error("CHILD B1 IS NOT IN FRAGMENT! Fragment: " + FragmentUtil.getFragmentType(f) + " Child fragment: " + FragmentUtil.getFragmentType(cf));
        }

        if (f.getNodes().contains(childB2)) {
            f.addEdge(pocket, childB2);
        } else {
            f.addEdge(pocket, fragmentB2);
            log.error("CHILD B2 IS NOT IN FRAGMENT! Fragment: " + FragmentUtil.getFragmentType(f) + " Child fragment: " + FragmentUtil.getFragmentType(cf));
        }

        return pocket;
    }
}
