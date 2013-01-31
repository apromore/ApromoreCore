package org.apromore.service.helper.extraction;

import java.util.Collection;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.service.model.FragmentNode;
import org.apromore.util.FragmentUtil;

/**
 * Extractor for extracting a polygon (i.e. sequence) from a polygon.
 * <p/>
 * This scenario occurs only if there is a loop within a polygon and that loop has a same node as the entry
 * and exit. e.g. Entry/exit node of the loop can be a state which has multiple entry and multiple exit edges.
 *
 * @author Chathura Ekanayake
 */
public class FSCSExtractor {

    @SuppressWarnings("unchecked")
    public static CPFNode extract(FragmentNode parent, FragmentNode child, Canonical g) {
        FragmentUtil.removeEdges(parent, child.getEdges());

        // We have to identify the edges in the parent fragment (f) that connect to the boundary nodes of the child
        // fragment (cf). In this special case, child fragment has only one boundary node (i.e. childB1 = childB2).

        CPFNode childB1 = child.getEntry();
        Collection<CPFEdge> fragmentLink1 = FragmentUtil.getIncomingEdges(childB1, parent.getEdges());
        FragmentUtil.removeEdges(fragmentLink1, child.getEdges());
        FragmentUtil.removeEdges(parent, fragmentLink1);

        CPFNode childB2 = child.getExit();
        Collection<CPFEdge> fragmentLink2 = FragmentUtil.getOutgoingEdges(childB2, parent.getEdges());
        FragmentUtil.removeEdges(fragmentLink2, child.getEdges());
        FragmentUtil.removeEdges(parent, fragmentLink2);

        FragmentUtil.removeNodes(parent, child);

        // fragmentLink1 is the incoming edge from f to cf. fragmentLink2 is the outgoing edge from cf to f.
        // We have replace cf with a pocket and use fragmentLink1 as the incoming edge to pocket and fragmentLink2
        // as the outgoing edge from the pocket. If any fragmentLink1 is empty, there is no incoming edge
        // from f to cf. That means, cf occurs in a boundary of f. Therefore, we add pocket as the new boundary (entry
        // in that case) of f.

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
