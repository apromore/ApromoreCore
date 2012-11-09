package org.apromore.service.helper.extraction;

import java.util.Collection;
import java.util.List;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
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

        preprocessFragmentV2(cf, f, g);

        Node childB1 = cf.getEntry();
        if (childB1.getId().equals(originalChildB1.getId())) {
            Node newChildB1 = FragmentUtil.duplicateNode(childB1, g);
            FragmentUtil.reconnectBoundary1(cf, childB1, newChildB1);
        }

        Node childB2 = cf.getExit();
        if (childB2.getId().equals(originalChildB2.getId())) {
            Node newChildB2 = FragmentUtil.duplicateNode(childB2, g);
            FragmentUtil.reconnectBoundary2(cf, childB2, newChildB2);
        }

        f.removeNodes(cf.getNodes());

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


    private static void preprocessFragmentV2(RFragment2 f, RFragment2 parentFragment, Canonical g) {
        Node b1 =  f.getEntry();
        Node b2 = f.getExit();

        if (Constants.CONNECTOR.equals(g.getNodeProperty(b1.getId(), Constants.TYPE))) {
            List<Node> postset = FragmentUtil.getPostset(b1, f.getEdges());
            if (postset.size() == 1 && g.getPreset(b1).size() <= 1) {
                Collection<Edge> postsetEdges = FragmentUtil.getOutgoingEdges(b1, f.getEdges());
                f.setEntry(postset.get(0));
                f.removeEdges(postsetEdges);
                parentFragment.removeEdges(postsetEdges);
                f.removeNode(b1);
            }
        }

        if (Constants.CONNECTOR.equals(g.getNodeProperty(b2.getId(), Constants.TYPE))) {
            List<Node> preset = FragmentUtil.getPreset(b2, f.getEdges());
            if (preset.size() == 1 && g.getPreset(b2).size() <= 1) {
                Collection<Edge> presetEdges = FragmentUtil.getIncomingEdges(b2, f.getEdges());
                f.setExit(preset.get(0));
                f.removeEdges(presetEdges);
                parentFragment.removeEdges(presetEdges);
                f.removeNode(b2);
            }
        }
    }

}
