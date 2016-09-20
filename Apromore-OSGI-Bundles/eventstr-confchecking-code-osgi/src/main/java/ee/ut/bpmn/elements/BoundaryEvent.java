package ee.ut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class BoundaryEvent extends Node {
	private Transition eventOccurrence;
	private Transition subprocessCompletionSynchronisation;
	@SuppressWarnings("unused")
	private Place internalPlace;
	
	public BoundaryEvent(Element element, PetriNet net) {
		super(element, net);
		this.eventOccurrence = new Transition(element.getAttributeValue("name"));
		this.subprocessCompletionSynchronisation = new Transition();
		this.internalPlace = new Place();
		net.addFlow(eventOccurrence, inputPlace);
		net.addFlow(inputPlace, subprocessCompletionSynchronisation);
	}
	
	public void setOkPlace(Place ok) {
		inputPlace = ok;
		net.addFlow(inputPlace, eventOccurrence);
	}

	public void setNOkPlace(Place nok) {
		net.addFlow(eventOccurrence, nok);
		net.addFlow(nok, subprocessCompletionSynchronisation);
	}

	public void connectTo(Place place) {
		net.addFlow(subprocessCompletionSynchronisation, place);
	}
	
	public void synchroniseWith(Place place) {
		net.addFlow(place, subprocessCompletionSynchronisation);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		// TODO Auto-generated method stub
		return null;
	}
}
