package org.apromore.service.helper.extraction;

import org.apromore.common.Constants;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.util.FragmentUtil;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.pm.FlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class FNSCNSExtractor {

	private static final Logger log = LoggerFactory.getLogger(FNSCNSExtractor.class);

	public static FlowNode extract(RPSTNode f, RPSTNode cf, RPST rpst, CPF g) {
        FlowNode childB1 = (FlowNode) cf.getEntry();
        FlowNode newChildB1 = FragmentUtil.duplicateVertex(childB1, g);
		FragmentUtil.reconnectBoundary1(cf, childB1, newChildB1, rpst);

        FlowNode childB2 = (FlowNode) cf.getExit();
        FlowNode newChildB2 = FragmentUtil.duplicateVertex(childB2, g);
		FragmentUtil.reconnectBoundary2(cf, childB2, newChildB2, rpst);

		f.getFragment().removeVertices(cf.getFragment().getVertices());

        FlowNode fragmentB1 = (FlowNode) f.getEntry();
        FlowNode fragmentB2 = (FlowNode) f.getExit();

        FlowNode pocket = new CpfNode("Pocket");
		g.addVertex(pocket);
		g.setVertexProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
		f.getFragment().addVertex(pocket);

		if (f.getFragment().getVertices().contains(childB1))
			f.getFragment().addEdge(childB1, pocket);
		else {
			f.getFragment().addEdge(fragmentB1, pocket);
			log.error("CHILD B1 IS NOT IN FRAGMENT! Fragment: " + FragmentUtil.getFragmentType(f) + " Child fragment: " + FragmentUtil.getFragmentType(cf));
		}

		if (f.getFragment().getVertices().contains(childB2))
			f.getFragment().addEdge(pocket, childB2);
		else {
			f.getFragment().addEdge(pocket, fragmentB2);
			log.error("CHILD B2 IS NOT IN FRAGMENT! Fragment: " + FragmentUtil.getFragmentType(f) + " Child fragment: " + FragmentUtil.getFragmentType(cf));
		}

		return pocket;
	}
}
