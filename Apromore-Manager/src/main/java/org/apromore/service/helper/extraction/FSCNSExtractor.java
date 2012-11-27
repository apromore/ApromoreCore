package org.apromore.service.helper.extraction;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.model.fragmentNode;
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
    public static CPFNode extract(fragmentNode f, fragmentNode cf, Canonical g) {
        FragmentUtil.removeEdges(f, cf.getEdges());

        CPFNode childB1 = cf.getEntry();
        Collection<CPFEdge> fragmentLink1 = FragmentUtil.getIncomingEdges(childB1, f.getEdges());
        FragmentUtil.removeEdges(fragmentLink1, cf.getEdges());
        FragmentUtil.removeEdges(f, fragmentLink1);

        CPFNode childB2 = cf.getExit();
        Collection<CPFEdge> fragmentLink2 = FragmentUtil.getOutgoingEdges(childB2, f.getEdges());
        FragmentUtil.removeEdges(fragmentLink2, cf.getEdges());
        FragmentUtil.removeEdges(f, fragmentLink2);

        FragmentUtil.removeNodes(f, cf);

        CPFNode pocket = new CPFNode();
        pocket.setName("Pocket");
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
