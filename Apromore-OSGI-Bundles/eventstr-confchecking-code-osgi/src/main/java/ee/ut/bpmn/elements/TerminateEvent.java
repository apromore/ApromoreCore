package ee.ut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class TerminateEvent extends Node {
	private Transition positiveCase;
	private Transition negativeCase;
	
	private Transition subprocessCompletionSynchronisation;
	private Place internalPlace;

	public TerminateEvent(Element element, PetriNet net) {
		super(element, net);
		
		this.positiveCase = new Transition(element.getAttributeValue("name"));
		this.negativeCase = new Transition();
		
		net.addFlow(inputPlace, positiveCase);
		net.addFlow(inputPlace, negativeCase);
		
		this.subprocessCompletionSynchronisation = new Transition();
		this.internalPlace = new Place();
		net.addFlow(positiveCase, internalPlace);
		net.addFlow(internalPlace, subprocessCompletionSynchronisation);
	}
	
	public void connectTo(Place place) {
		throw new RuntimeException("Malformed BPMN model: There is a terminate event with a successor");
	}
	
	public void connectToOk(Place ok) {
		net.addFlow(ok, positiveCase);
	}
	
	public void setParent(Subprocess parent) {
		net.addFlow(subprocessCompletionSynchronisation, parent.getSinkPlace());
	}
	
	public void connectToNOk(Place nok) {
		net.addFlow(negativeCase, nok);
		net.addFlow(nok, negativeCase);
		net.addFlow(positiveCase, nok);
		net.addFlow(nok, subprocessCompletionSynchronisation);
	}
	
	public void synchroniseWith(Place place) {
//		net.addFlow(place, subprocessCompletionSynchronisation);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
	return positiveCase;
	}
}
