package ee.ut.bpmn.elements;

import java.util.HashMap;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class Task extends Node {
	private Transition positiveCase;
	private Transition negativeCase;
		
	public Task(Element element, PetriNet net, HashMap<String, String> tasks) {
		super(element, net);
		String name = element.getAttributeValue("name");
		
		if(tasks.containsKey(element.getAttributeValue("name")))
			name += tasks.size();
		
		this.positiveCase = new Transition(name);
		this.negativeCase = new Transition();
				
		net.addFlow(inputPlace, positiveCase);
		net.addFlow(inputPlace, negativeCase);
	}
	
	public void connectTo(Place place) {
		net.addFlow(positiveCase, place);
		net.addFlow(negativeCase, place);
	}
	
	public void connectToOk(Place ok) {
		net.addFlow(positiveCase, ok);
		net.addFlow(ok, positiveCase);
	}
	
	public void connectToNOk(Place nok) {
		net.addFlow(negativeCase, nok);
		net.addFlow(nok, negativeCase);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		return positiveCase;
	}
}
