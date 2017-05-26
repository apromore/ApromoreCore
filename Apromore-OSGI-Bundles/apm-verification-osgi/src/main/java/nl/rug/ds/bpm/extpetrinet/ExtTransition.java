package nl.rug.ds.bpm.extpetrinet;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

public class ExtTransition extends Transition {

	private String uniqueId;
	
	public ExtTransition(PetriNet net, String name) {
		this(net, name, "");
	}
		
	public ExtTransition(PetriNet net, String name, String uniqueId) {
		super(net, name);
		this.uniqueId = uniqueId;
	}
	
	public void setUniqueIdentifier(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	@Override
	public String getUniqueIdentifier() {
		return uniqueId;
	}

}
