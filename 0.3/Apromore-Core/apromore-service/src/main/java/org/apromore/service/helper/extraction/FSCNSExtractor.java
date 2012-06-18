package org.apromore.service.helper.extraction;

import org.apromore.common.Constants;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.util.FragmentUtil;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.IFlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Processors take a fragment and its child fragment and takes care of
 * processing the child fragment within the parent fragment. Processor extracts
 * the child content and configure mappings to the child fragment.
 *
 * @author Chathura Ekanayake
 */
public class FSCNSExtractor {

	private static Logger log = LoggerFactory.getLogger(FSCNSExtractor.class);

	public static FlowNode extract(RPSTNode f, RPSTNode cf, CPF g) {
		FragmentUtil.removeEdges(f, cf);

        IFlowNode childB1 = (IFlowNode) cf.getEntry();
		Collection<AbstractDirectedEdge> fragmentLink1 = FragmentUtil.getIncomingEdges(childB1, f.getFragmentEdges());
		fragmentLink1.removeAll(cf.getFragmentEdges());
		f.getFragment().removeEdges(fragmentLink1);

        IFlowNode childB2 = (IFlowNode) cf.getExit();
		Collection<AbstractDirectedEdge> fragmentLink2 = FragmentUtil.getOutgoingEdges(childB2, f.getFragmentEdges());
		fragmentLink2.removeAll(cf.getFragmentEdges()); // remove internal edges to prevent the effect of loops
		f.getFragment().removeEdges(fragmentLink2);
		f.getFragment().removeVertices(cf.getFragment().getVertices());

        FlowNode pocket = new CpfNode("Pocket");
		g.setVertexProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
		f.getFragment().addVertex(pocket);
		if (!fragmentLink1.isEmpty()) {
			f.getFragment().addEdge(FragmentUtil.getFirstEdge(fragmentLink1).getSource(), pocket);
		} else {
			f.setEntry(pocket);
		}
		
		if (!fragmentLink2.isEmpty()) {
			f.getFragment().addEdge(pocket, FragmentUtil.getFirstEdge(fragmentLink2).getTarget());
		} else {
			f.setExit(pocket);
		}
		
		return pocket;
	}
}
