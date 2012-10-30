package org.apromore.service.helper.extraction;

import java.util.Collection;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.Node;
import org.apromore.service.model.RFragment2;
import org.apromore.util.FragmentUtil;

/**
 * Processors take a fragment and its child fragment and takes care of
 * processing the child fragment within the parent fragment. Processor extracts
 * the child content and configure mappings to the child fragment.
 *
 * @author Chathura Ekanayake
 */
public class FSCNSExtractor {

    @SuppressWarnings("unchecked")
    public static Node extract(RFragment2 f, RFragment2 cf, Canonical g) {
        FragmentUtil.removeEdges(f, cf.getEdges());

        Node childB1 = cf.getEntry();
        Collection<Edge> fragmentLink1 = FragmentUtil.getIncomingEdges(childB1, f.getEdges());
        FragmentUtil.removeEdges(fragmentLink1, cf.getEdges());
        FragmentUtil.removeEdges(f, fragmentLink1);

        Node childB2 = cf.getExit();
        Collection<Edge> fragmentLink2 = FragmentUtil.getOutgoingEdges(childB2, f.getEdges());
        FragmentUtil.removeEdges(fragmentLink2, cf.getEdges());
        FragmentUtil.removeEdges(f, fragmentLink2);

        FragmentUtil.removeNodes(f, cf);

        Node pocket = new Node("Pocket");
        g.setNodeProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
        f.addNode(pocket);
        if (!fragmentLink1.isEmpty()) {
            f.addEdge(FragmentUtil.getFirstEdge(fragmentLink1).getSource(), pocket);
        } else {
            f.setEntry(pocket);
        }

        if (!fragmentLink2.isEmpty()) {
            f.addEdge(pocket, FragmentUtil.getFirstEdge(fragmentLink2).getTarget());
        } else {
            f.setExit(pocket);
        }

        return pocket;
    }
}
