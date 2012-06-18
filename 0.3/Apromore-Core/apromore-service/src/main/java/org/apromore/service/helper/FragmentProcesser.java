package org.apromore.service.helper;

import org.apromore.common.Constants;
import org.apromore.graph.JBPT.CPF;
import org.apromore.util.FragmentUtil;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.pm.IFlowNode;

import java.util.Collection;
import java.util.List;

/**
 * @author Chathura Ekanayake
 */
public class FragmentProcesser {

    @SuppressWarnings("unchecked")
	public static IFlowNode[] preprocessFragmentV2(RPSTNode f, RPSTNode parentFragment, CPF g) {
        IFlowNode[] originalBoundary = new IFlowNode[2];
		originalBoundary[0] = (IFlowNode) f.getEntry();
		originalBoundary[1] = (IFlowNode) f.getExit();

        IFlowNode b1 = (IFlowNode) f.getEntry();
        IFlowNode b2 = (IFlowNode) f.getExit();

		if (Constants.CONNECTOR.equals(g.getVertexProperty(b1.getId(), Constants.TYPE))) {
			List<IFlowNode> postset = FragmentUtil.getPostset(b1, f.getFragmentEdges());
            if (postset.size() == 1 && f.getFragment().getDirectPredecessors(b1).size() <= 1) {
				Collection<AbstractDirectedEdge> postsetEdges = FragmentUtil.getOutgoingEdges(b1, f.getFragmentEdges());
                f.setEntry(postset.get(0));
				f.getFragment().removeEdges(postsetEdges);
				parentFragment.getFragment().removeEdges(postsetEdges);
				f.getFragment().removeVertex(b1);
			}
		} 

		if (Constants.CONNECTOR.equals(g.getVertexProperty(b2.getId(), Constants.TYPE))) {
			List<IFlowNode> preset = FragmentUtil.getPreset(b2, f.getFragmentEdges());
			if (preset.size() == 1 && f.getFragment().getDirectSuccessors(b2).size() <= 1) {
				Collection<AbstractDirectedEdge> presetEdges = FragmentUtil.getIncomingEdges(b2, f.getFragmentEdges());
				f.setExit(preset.get(0));
				f.getFragment().removeEdges(presetEdges);
				parentFragment.getFragment().removeEdges(presetEdges);
				f.getFragment().removeVertex(b2);
			}
		}

		return originalBoundary;
	}

}
