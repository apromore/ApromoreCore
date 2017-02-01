package ee.ut.bpmn.elements;

import java.util.HashMap;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class SimpleTask extends Node {
	protected Transition positiveCase;
		
	public SimpleTask(Element element, PetriNet net){// HashMap<String, String> tasks), HashMap<String, Integer> labelCounter) {
		super(element, net);
		String name = element.getAttributeValue("name");
		
//		if(tasks.containsKey(name))
//			name += labelCounter.get(name);
		
		this.positiveCase = new Transition(name);
				
		net.addFlow(inputPlace, positiveCase);
	}
	
	public void connectTo(Place place) {
		net.addFlow(positiveCase, place);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		return positiveCase;
	}	
	
}
