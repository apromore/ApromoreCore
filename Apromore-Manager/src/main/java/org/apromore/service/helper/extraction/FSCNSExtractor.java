package org.apromore.service.helper.extraction;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.service.model.FragmentNode;
import org.apromore.util.FragmentUtil;

import java.util.Collection;

/**
 * Processors take a fragment and its child fragment and takes care of
 * processing the child fragment within the parent fragment. Processor extracts
 * the child content and configure mappings to the child fragment.
 *
 * @author Chathura Ekanayake
 */
public class FSCNSExtractor {

    @SuppressWarnings("unchecked")
    public static CPFNode extract(FragmentNode parent, FragmentNode child, Canonical g) {
        FragmentUtil.removeEdges(parent, child.getEdges());

        CPFNode childB1 = child.getEntry();
        Collection<CPFEdge> fragmentLink1 = FragmentUtil.getIncomingEdges(childB1, parent.getEdges());
        FragmentUtil.removeEdges(fragmentLink1, child.getEdges());
        FragmentUtil.removeEdges(parent, fragmentLink1);

        CPFNode childB2 = child.getExit();
        Collection<CPFEdge> fragmentLink2 = FragmentUtil.getOutgoingEdges(childB2, parent.getEdges());
        FragmentUtil.removeEdges(fragmentLink2, child.getEdges());
        FragmentUtil.removeEdges(parent, fragmentLink2);

        FragmentUtil.removeNodes(parent, child);

        CPFNode pocket = new CPFNode();
        pocket.setGraph(g);
        pocket.setName("Pocket");
        pocket.setNodeType(NodeTypeEnum.POCKET);
        g.setNodeProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
        parent.addNode(pocket);
        if (!fragmentLink1.isEmpty()) {
            parent.addEdge(FragmentUtil.getFirstEdge(fragmentLink1).getSource(), pocket);
        } else {
            parent.setEntry(pocket);
        }

        if (!fragmentLink2.isEmpty()) {
            parent.addEdge(pocket, FragmentUtil.getFirstEdge(fragmentLink2).getTarget());
        } else {
            parent.setExit(pocket);
        }

        return pocket;
    }
}
