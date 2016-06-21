package ee.ut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jdom2.Element;

public abstract class Node {
	protected Subprocess parent;
	protected PetriNet net;
	
	protected Place inputPlace;
	
	public Node(Element element, PetriNet net) {
		this.parent = null;
		this.net = net;
		this.inputPlace = new Place("p"+net.getPlaces().size());
	}

	public Subprocess getParent() {
		return parent;
	}

	public void setParent(Subprocess parent) {
		this.parent = parent;
	}
	
	public Place getInputPlace() {
		return inputPlace;
	}
	
	public abstract void connectTo(Place place);

	public abstract org.jbpt.petri.Node getTransition();
}
