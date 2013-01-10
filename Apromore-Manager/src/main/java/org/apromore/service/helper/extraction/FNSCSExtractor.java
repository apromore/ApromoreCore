package org.apromore.service.helper.extraction;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.service.model.FragmentNode;
import org.apromore.util.FragmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * @author Chathura Ekanayake
 */
public class FNSCSExtractor {

    private static final Logger log = LoggerFactory.getLogger(FNSCSExtractor.class);

    @SuppressWarnings("unchecked")
    public static CPFNode extract(FragmentNode f, FragmentNode cf, Canonical g) {
        CPFNode originalChildB1 = cf.getEntry();
        CPFNode originalChildB2 = cf.getExit();

        preprocessFragmentV2(cf, f, g);

        CPFNode childB1 = cf.getEntry();
        if (childB1 != null && childB1.getId().equals(originalChildB1.getId())) {
            CPFNode newChildB1 = FragmentUtil.duplicateNode(childB1, g);
            FragmentUtil.reconnectBoundary1(cf, childB1, newChildB1);
        }

        CPFNode childB2 = cf.getExit();
        if (childB2 != null && childB2.getId().equals(originalChildB2.getId())) {
            CPFNode newChildB2 = FragmentUtil.duplicateNode(childB2, g);
            FragmentUtil.reconnectBoundary2(cf, childB2, newChildB2);
        }

        f.removeNodes(cf.getNodes());

        CPFNode fragmentB1 = f.getEntry();
        CPFNode fragmentB2 = f.getExit();

        CPFNode pocket = new CPFNode();
        pocket.setName("Pocket");
        pocket.setNodeType(NodeTypeEnum.POCKET);
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

        if (childB1 != null && Constants.CONNECTOR.equals(g.getNodeProperty(childB1.getId(), Constants.TYPE))) {
            if (g.getDirectSuccessors(childB1).size() == 1) {
                log.debug("NEW CHILD BOUNDARY CONNECTOR B1");
                //log.debug(FragmentUtil.fragmentToString(cf, g));
            }
        }

        if (childB2 != null && Constants.CONNECTOR.equals(g.getNodeProperty(childB2.getId(), Constants.TYPE))) {
            if (g.getDirectPredecessors(childB2).size() == 1) {
                log.debug("NEW CHILD BOUNDARY CONNECTOR B2");
                //log.debug(FragmentUtil.fragmentToString(cf, g));
            }
        }

        return pocket;
    }


    private static void preprocessFragmentV2(FragmentNode f, FragmentNode parentFragment, Canonical g) {
        CPFNode b1 =  f.getEntry();
        CPFNode b2 = f.getExit();

        if (b1 != null && Constants.CONNECTOR.equals(g.getNodeProperty(b1.getId(), Constants.TYPE))) {
            List<CPFNode> postset = FragmentUtil.getPostset(b1, f.getEdges());
            if (postset.size() == 1 && g.getPreset(b1).size() <= 1) {
                Collection<CPFEdge> postsetEdges = FragmentUtil.getOutgoingEdges(b1, f.getEdges());
                f.setEntry(postset.get(0));
                f.removeEdges(postsetEdges);
                parentFragment.removeEdges(postsetEdges);
                f.removeNode(b1);
            }
        }

        if (b2 != null && Constants.CONNECTOR.equals(g.getNodeProperty(b2.getId(), Constants.TYPE))) {
            List<CPFNode> preset = FragmentUtil.getPreset(b2, f.getEdges());
            if (preset.size() == 1 && g.getPreset(b2).size() <= 1) {
                Collection<CPFEdge> presetEdges = FragmentUtil.getIncomingEdges(b2, f.getEdges());
                f.setExit(preset.get(0));
                f.removeEdges(presetEdges);
                parentFragment.removeEdges(presetEdges);
                f.removeNode(b2);
            }
        }
    }

}
