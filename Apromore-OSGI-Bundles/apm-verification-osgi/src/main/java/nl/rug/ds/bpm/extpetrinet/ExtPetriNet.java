package nl.rug.ds.bpm.extpetrinet;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

public class ExtPetriNet extends PetriNet {
	
	public Transition addTransition(String uniqueId, String label) {
		Transition trans = new ExtTransition(this, label, uniqueId);
		
		this.getTransitions().add(trans);
		
		return trans;
	}
}
