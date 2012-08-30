package org.apromore.service.helper.extraction;

import org.apromore.common.Constants;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.service.helper.FragmentProcesser;
import org.apromore.util.FragmentUtil;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.IFlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class FNSCSExtractor {

    private static final Logger log = LoggerFactory.getLogger(FNSCSExtractor.class);

    @SuppressWarnings("unchecked")
    public static FlowNode extract(RPSTNode f, RPSTNode cf, RPST rpst, CPF g) {
        FlowNode originalChildB1 = (FlowNode) cf.getEntry();
        FlowNode originalChildB2 = (FlowNode) cf.getExit();

        // Do we need this call ???
        FragmentProcesser.preprocessFragmentV2(cf, f, g);

        FlowNode childB1 = (FlowNode) cf.getEntry();
        if (childB1.getId().equals(originalChildB1.getId())) {
            FlowNode newChildB1 = FragmentUtil.duplicateVertex(childB1, g);
            FragmentUtil.reconnectBoundary1(cf, childB1, newChildB1, rpst);
        }

        FlowNode childB2 = (FlowNode) cf.getExit();
        if (childB2.getId().equals(originalChildB2.getId())) {
            FlowNode newChildB2 = FragmentUtil.duplicateVertex(childB2, g);
            FragmentUtil.reconnectBoundary2(cf, childB2, newChildB2, rpst);
        }

        f.getFragment().removeVertices(cf.getFragment().getVertices());

        FlowNode fragmentB1 = (FlowNode) f.getEntry();
        FlowNode fragmentB2 = (FlowNode) f.getExit();

        FlowNode pocket = new CpfNode("Pocket");
        g.setVertexProperty(pocket.getId(), Constants.TYPE, Constants.POCKET);
        f.getFragment().addVertex(pocket);

        if (f.getFragment().getVertices().contains(originalChildB1)) {
            f.getFragment().addEdge(originalChildB1, pocket);
        } else {
            f.getFragment().addEdge(fragmentB1, pocket);
        }

        if (f.getFragment().getVertices().contains(originalChildB2)) {
            f.getFragment().addEdge(pocket, originalChildB2);
        } else {
            f.getFragment().addEdge(pocket, fragmentB2);
        }

        if (Constants.CONNECTOR.equals(g.getVertexProperty(childB1.getId(), Constants.TYPE))) {
            if (g.getDirectSuccessors(childB1).size() == 1) {
                log.debug("NEW CHILD BOUNDARY CONNECTOR B1");
                log.debug(FragmentUtil.fragmentToString(cf, g));
            }
        }

        if (Constants.CONNECTOR.equals(g.getVertexProperty(childB2.getId(), Constants.TYPE))) {
            if (g.getDirectPredecessors(childB2).size() == 1) {
                log.debug("NEW CHILD BOUNDARY CONNECTOR B2");
                log.debug(FragmentUtil.fragmentToString(cf, g));
            }
        }

        return pocket;
    }

}
