package ee.ut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class ExclusiveGateway extends Node {
	Transition transition;
	
	public ExclusiveGateway(Element element, PetriNet net) {
		super(element, net);
	}
	
	public void connectTo(Place place) {
		transition = new Transition();
		net.addFlow(inputPlace, transition);
		net.addFlow(transition, place);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		// TODO Auto-generated method stub
		return (org.jbpt.petri.Node) transition;
	}
}
