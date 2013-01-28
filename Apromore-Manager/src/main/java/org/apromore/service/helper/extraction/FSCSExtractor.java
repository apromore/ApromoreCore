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
    public static CPFNode extract(FragmentNode f, FragmentNode cf, Canonical g) {
        FragmentUtil.removeEdges(f, cf.getEdges());

        // We have to identify the edges in the parent fragment (f) that connect to the boundary nodes of the child
        // fragment (cf). In this special case, child fragment has only one boundary node (i.e. childB1 = childB2).

        CPFNode childB1 = cf.getEntry();
        Collection<CPFEdge> fragmentLink1 = FragmentUtil.getIncomingEdges(childB1, f.getEdges());
        FragmentUtil.removeEdges(fragmentLink1, cf.getEdges());
        FragmentUtil.removeEdges(f, fragmentLink1);

        CPFNode childB2 = cf.getExit();
        Collection<CPFEdge> fragmentLink2 = FragmentUtil.getOutgoingEdges(childB2, f.getEdges());
        FragmentUtil.removeEdges(fragmentLink2, cf.getEdges());
        FragmentUtil.removeEdges(f, fragmentLink2);

        FragmentUtil.removeNodes(f, cf);

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
