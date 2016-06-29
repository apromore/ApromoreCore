package ee.ut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class EndEvent extends SimpleEndEvent {
	private Transition negativeCase;
		
	public EndEvent(Element element, PetriNet net) {
		super(element, net);
		this.negativeCase = new Transition();
				
		net.addFlow(inputPlace, negativeCase);
	}
	
	public void connectTo(Place place) {
		net.addFlow(positiveCase, place);
		net.addFlow(negativeCase, place);
	}
		
	public void connectToOk(Place ok) {
		net.addFlow(ok, positiveCase);
		net.addFlow(positiveCase, ok);
	}
	
	public void connectToNOk(Place nok) {
		net.addFlow(negativeCase, nok);
		net.addFlow(nok, negativeCase);
	}
}
