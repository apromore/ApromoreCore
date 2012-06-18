package org.apromore.util;

import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.TreeVisitor;
import org.apromore.service.helper.OperationContext;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.graph.algo.tctree.TCType;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.IFlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Chathura Ekanayake
 */
public class HashUtil {
	
	private static Logger log = LoggerFactory.getLogger(HashUtil.class);

	public static String computeHash(RPSTNode fragment, OperationContext op) {
		CPF graph = op.getGraph();
		TreeVisitor visitor = op.getTreeVisitor();
		int fragmentSize = fragment.getFragment().getVertices().size();

		log.debug("Computing hash of a fragment with " + fragmentSize + " vertices...");
		
		Collection<AbstractDirectedEdge> fEdges = fragment.getFragmentEdges();
		Collection<FlowNode> fVertices = fragment.getFragment().getVertices();
		Set<FlowNode> vertices = new HashSet<FlowNode>(fVertices);
		Set<AbstractDirectedEdge> edges = new HashSet<AbstractDirectedEdge>(fEdges);

		String hash = null;
		String type = "None";
		
		try {
			TCType nodeType = fragment.getType();
			if (nodeType == TCType.P) {
				type = "P";
				log.debug("Fragment type: " + type + " | Fragment size: " + fragmentSize);
				hash = visitor.visitSNode(graph, edges, (IFlowNode) fragment.getEntry());
			} else if (nodeType == TCType.B) {
				type = "B";
				log.debug("Fragment type: " + type + " | Fragment size: " + fragmentSize);
				hash = computeBondHash(fragment, op);
			} else if (nodeType == TCType.R) {
				type = "R";
				log.debug("Fragment type: " + type + " | Fragment size: " + fragmentSize);
				if (fragmentSize <= 10) {
					hash = visitor.visitRNode(graph, edges, vertices, (IFlowNode) fragment.getEntry(), (IFlowNode) fragment.getExit());
				} else {
					hash = null;
					log.debug("Large fragment. Skipped the hash computation.");
				}
			}
		} catch (StringIndexOutOfBoundsException se) {
			String msg = "Unable to compute hash. " + se.getMessage();
			log.error(msg);
			hash = null;
		}
		
		log.debug("Hash: " + hash);
		
		return hash;
	}
	
	private static String computeBondHash(RPSTNode fragment, OperationContext op) {
		int oPockets = 0;
		int iPockets = 0;
		int oDirectConnections = 0;
		int iDirectConnections = 0;
		
		IVertex entry = fragment.getEntry();
		IVertex exit = fragment.getExit();
		Collection<IVertex> successors = fragment.getFragment().getDirectSuccessors(entry);
		for (IVertex successor : successors) {
			if (exit.equals(successor)) {
				oDirectConnections++;
			} else {
				oPockets++;
			}
		}
		
		// this is required if the fragment contains loops
		Collection<IVertex> predecessors = fragment.getFragment().getDirectPredecessors(entry);
		for (IVertex predecessor : predecessors) {
			if (exit.equals(predecessor)) {
				iDirectConnections++;
			} else {
				iPockets++;
			}
		}

        return "OP" + oPockets + "ODC" + oDirectConnections + "IP" + iPockets + "IDC" + iDirectConnections;
	}
}
