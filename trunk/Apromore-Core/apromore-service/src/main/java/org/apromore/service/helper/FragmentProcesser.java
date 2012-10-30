package org.apromore.service.helper;

import java.util.Collection;
import java.util.List;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.Node;
import org.apromore.service.model.RFragment2;
import org.apromore.util.FragmentUtil;

/**
 * @author Chathura Ekanayake
 */
public class FragmentProcesser {

    @SuppressWarnings("unchecked")
    public static INode[] preprocessFragmentV2(RFragment2 f, RFragment2 parentFragment, Canonical g) {
        Node[] originalBoundary = new Node[2];
        originalBoundary[0] = f.getEntry();
        originalBoundary[1] = f.getExit();

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

        return originalBoundary;
    }

}
