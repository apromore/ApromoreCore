package org.apromore.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apromore.graph.JBPT.CPF;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;

/**
 * @author Chathura Ekanayake
 */
public class PetriNetUtil {

	public static PetriNet transform(CPF g) {
		Map<FlowNode, Transition> vtMap = new HashMap<FlowNode, Transition>();
		
		PetriNet petriNet = new PetriNet();
		Collection<FlowNode> vertices = g.getVertices();
		for (FlowNode v : vertices) {
			Transition t = new Transition(v.getName());
			vtMap.put(v, t);
			petriNet.addNode(t);
		}
		
		FlowNode source = g.getSourceVertices().get(0);
		Transition sourceT = vtMap.get(source);
		Place p1 = new Place();
		//p1.setTokens(1);
		petriNet.addFlow(p1, sourceT);
		
		FlowNode sink = g.getSinkVertices().get(0);
		Transition sinkT = vtMap.get(sink);
		Place p2 = new Place();
		petriNet.addFlow(sinkT, p2);
		
		Collection<ControlFlow<FlowNode>> edges = g.getEdges();
		for (ControlFlow<FlowNode> e : edges) {
			Transition sourceTransition = vtMap.get(e.getSource());
			Transition targetTransition = vtMap.get(e.getTarget());
			Place place = new Place();
			petriNet.addFlow(sourceTransition, place);
			petriNet.addFlow(place, targetTransition);
		}
		
		return petriNet;
	}
}
