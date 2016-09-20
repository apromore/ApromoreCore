package ee.ut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class SimpleEndEvent extends Node {
	protected Transition positiveCase;
		
	public SimpleEndEvent(Element element, PetriNet net) {
		super(element, net);
		
		this.positiveCase = new Transition(element.getAttributeValue("name"));
				
		net.addFlow(inputPlace, positiveCase);
	}
	
	public void connectTo(Place place) {
		net.addFlow(positiveCase, place);
	}
		
	public void connectToOk(Place ok) {
		net.addFlow(ok, positiveCase);
	}
	
	public void connectToNOk(Place nok) {
		net.addFlow(positiveCase, nok);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		return positiveCase;
	}
}
