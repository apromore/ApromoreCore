package ee.ut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class ParallelGateway extends Node {
	private Transition positiveCase;

	public ParallelGateway(Element element, PetriNet net) {
		super(element, net);
		
		positiveCase = new Transition();
	}
	
	public Place getInputPlace() {
		Place place;
		if (net.getPreset(positiveCase).isEmpty())
			place = inputPlace;
		else
			place = new Place();
		
		net.addFlow(place, positiveCase);
		
		return place;
	}

	public void connectTo(Place place) {
		net.addFlow(positiveCase, place);
	}
	
	public Transition getTransition(){
		return positiveCase;
	}
}
