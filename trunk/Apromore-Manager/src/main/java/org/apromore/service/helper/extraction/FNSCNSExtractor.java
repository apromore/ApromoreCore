package org.apromore.service.helper.extraction;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.service.model.FragmentNode;
import org.apromore.util.FragmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class FNSCNSExtractor {

    private static final Logger log = LoggerFactory.getLogger(FNSCNSExtractor.class);

    @SuppressWarnings("unchecked")
    public static CPFNode extract(FragmentNode parent, FragmentNode child, Canonical g) {
        CPFNode childB1 = child.getEntry();
        CPFNode newChildB1 = FragmentUtil.duplicateNode(childB1, g);
        FragmentUtil.reconnectBoundary1(child, childB1, newChildB1);

        CPFNode childB2 = child.getExit();
        CPFNode newChildB2 = FragmentUtil.duplicateNode(childB2, g);
        FragmentUtil.reconnectBoundary2(child, childB2, newChildB2);

        parent.removeNodes(child.getNodes());

        CPFNode fragmentB1 = parent.getEntry();
        CPFNode fragmentB2 = parent.getExit();

        CPFNode pocket = new CPFNode();
        pocket.setGraph(g);
        pocket.setName("Pocket");
        pocket.setNodeType(NodeTypeEnum.POCKET);
        g.addNode(pocket);
        g.setNodeProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
        parent.addNode(pocket);

        if (parent.getNodes().contains(childB1)) {
            parent.addEdge(childB1, pocket);
        } else {
            parent.addEdge(fragmentB1, pocket);
            log.error("CHILD B1 IS NOT IN FRAGMENT! Fragment: " + FragmentUtil.getFragmentType(parent) + " Child fragment: " +
                    FragmentUtil.getFragmentType(child));
        }

        if (parent.getNodes().contains(childB2)) {
            parent.addEdge(pocket, childB2);
        } else {
            parent.addEdge(pocket, fragmentB2);
            log.error("CHILD B2 IS NOT IN FRAGMENT! Fragment: " + FragmentUtil.getFragmentType(parent) + " Child fragment: " +
                    FragmentUtil.getFragmentType(child));
        }

        return pocket;
    }
}
