package ee.ut.bpmn.elements;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class Subprocess extends Node {
	protected Set<Node> startEvents = new HashSet<>();
	protected Set<SimpleEndEvent> endEvents = new HashSet<>();
	protected Set<BoundaryEvent> boundaryEvents = new HashSet<>();
	protected Set<TerminateEvent> terminateEvents = new HashSet<>();
	
	protected Transition processEnablement;
	protected Transition processPositiveCompletion;
	
	protected Place okPlace = null;
	protected Place nokPlace = null;
	protected Place sinkPlace = null;
	protected Place completionPlace;
	public Subprocess(Element element, PetriNet net) {
		super(element, net);
		String name = element.getAttributeValue("name");
		processEnablement = new Transition(name + "_enable");
		processPositiveCompletion = new Transition(name + "_positive");
		
		completionPlace = new Place();
		net.addFlow(completionPlace, processPositiveCompletion);
		
		net.addFlow(inputPlace, processEnablement);
	}
	
	public void connectTo(Place place) {
		net.addFlow(processPositiveCompletion, place);
	}
	
	public boolean isRootProcess() {
		return parent == null;
	}

	public Place getOk() {
		return okPlace;
	}
	
	public Place getNOk() {
		return nokPlace;
	}

	public void addStartEvent(Node node) {
		startEvents.add(node);		
		net.addFlow(processEnablement, node.getInputPlace());
	}
	
	public void addEndEvent(SimpleEndEvent node) {
		endEvents.add(node);		
		node.connectTo(completionPlace);
	}

	public void addTerminateEvent(TerminateEvent node) {
		terminateEvents.add(node);		
	}

	public void wireTerminateEvents() {
		for (TerminateEvent node: terminateEvents) {
			node.connectToNOk(nokPlace);
			node.connectToOk(okPlace);
			node.synchroniseWith(completionPlace);
		}
		
		if (!terminateEvents.isEmpty() && endEvents.isEmpty() && boundaryEvents.isEmpty()) {
			net.removePlace(completionPlace);
			net.removeTransition(processPositiveCompletion);
		}
	}

	public void attachBoundaryEvent(BoundaryEvent node) {
		boundaryEvents.add(node);
		if (okPlace == null) {
			okPlace = new Place("ok");
			nokPlace = new Place("nok");
			net.addFlow(processEnablement, okPlace);
			net.addFlow(okPlace, processPositiveCompletion);
		}
		node.setOkPlace(okPlace);
		node.setNOkPlace(nokPlace);
		node.synchroniseWith(completionPlace);
	}
	
	private Place singletonSinkPlace() {
		if (sinkPlace == null)
			sinkPlace = new Place();
		return sinkPlace;
	}
	
	public void addFinalPlace() {
		if (parent == null) {
			net.addFlow(processPositiveCompletion, singletonSinkPlace());
		}
	}

	public Place getSinkPlace() {
		if (parent == null)
			return singletonSinkPlace();
		else
			return parent.getSinkPlace();
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		// TODO Auto-generated method stub
		return null;
	}
}
