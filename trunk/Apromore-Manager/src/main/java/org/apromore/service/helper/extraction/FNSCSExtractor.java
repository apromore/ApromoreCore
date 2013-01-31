package org.apromore.service.helper.extraction;

import java.util.Collection;
import java.util.List;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFEdge;
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
public class FNSCSExtractor {

    private static final Logger log = LoggerFactory.getLogger(FNSCSExtractor.class);

    @SuppressWarnings("unchecked")
    public static CPFNode extract(FragmentNode parent, FragmentNode child, Canonical g) {
        CPFNode originalChildB1 = child.getEntry();
        CPFNode originalChildB2 = child.getExit();

        preProcessFragmentV2(child, parent, g);

        CPFNode childB1 = child.getEntry();
        if (childB1 != null && childB1.getId().equals(originalChildB1.getId())) {
            CPFNode newChildB1 = FragmentUtil.duplicateNode(childB1, g);
            FragmentUtil.reconnectBoundary1(child, childB1, newChildB1);
        }

        CPFNode childB2 = child.getExit();
        if (childB2 != null && childB2.getId().equals(originalChildB2.getId())) {
            CPFNode newChildB2 = FragmentUtil.duplicateNode(childB2, g);
            FragmentUtil.reconnectBoundary2(child, childB2, newChildB2);
        }

        parent.removeNodes(child.getNodes());

        CPFNode fragmentB1 = parent.getEntry();
        CPFNode fragmentB2 = parent.getExit();

        CPFNode pocket = new CPFNode();
        pocket.setGraph(g);
        pocket.setName("Pocket");
        pocket.setNodeType(NodeTypeEnum.POCKET);
        g.setNodeProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
        parent.addNode(pocket);

        if (parent.getNodes().contains(originalChildB1)) {
            parent.addEdge(originalChildB1, pocket);
        } else {
            parent.addEdge(fragmentB1, pocket);
        }

        if (parent.getNodes().contains(originalChildB2)) {
            parent.addEdge(pocket, originalChildB2);
        } else {
            parent.addEdge(pocket, fragmentB2);
        }

        if (childB1 != null && Constants.CONNECTOR.equals(g.getNodeProperty(childB1.getId(), Constants.TYPE))) {
            if (g.getDirectSuccessors(childB1).size() == 1) {
                log.info("NEW CHILD BOUNDARY CONNECTOR B1 - " + FragmentUtil.fragmentToString(child));
            }
        }

        if (childB2 != null && Constants.CONNECTOR.equals(g.getNodeProperty(childB2.getId(), Constants.TYPE))) {
            if (g.getDirectPredecessors(childB2).size() == 1) {
                log.info("NEW CHILD BOUNDARY CONNECTOR B2 - " + FragmentUtil.fragmentToString(child));
            }
        }

        return pocket;
    }


    private static void preProcessFragmentV2(FragmentNode child, FragmentNode parent, Canonical g) {
        CPFNode b1 = child.getEntry();
        CPFNode b2 = child.getExit();

        if (b1 != null && Constants.CONNECTOR.equals(g.getNodeProperty(b1.getId(), Constants.TYPE))) {
            List<CPFNode> postset = FragmentUtil.getPostset(b1, child.getEdges());
            if (postset.size() == 1 && g.getPreset(b1).size() <= 1) {
                Collection<CPFEdge> postsetEdges = FragmentUtil.getOutgoingEdges(b1, child.getEdges());
                child.setEntry(postset.get(0));
                child.removeEdges(postsetEdges);
                parent.removeEdges(postsetEdges);
                child.removeNode(b1);
            }
        }

        if (b2 != null && Constants.CONNECTOR.equals(g.getNodeProperty(b2.getId(), Constants.TYPE))) {
            List<CPFNode> preset = FragmentUtil.getPreset(b2, child.getEdges());
            if (preset.size() == 1 && g.getPreset(b2).size() <= 1) {
                Collection<CPFEdge> presetEdges = FragmentUtil.getIncomingEdges(b2, child.getEdges());
                child.setExit(preset.get(0));
                child.removeEdges(presetEdges);
                parent.removeEdges(presetEdges);
                child.removeNode(b2);
            }
        }
    }

}
