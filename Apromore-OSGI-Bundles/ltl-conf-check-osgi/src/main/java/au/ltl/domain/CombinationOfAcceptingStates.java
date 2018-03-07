package au.ltl.domain;

import java.util.Vector;

public class CombinationOfAcceptingStates {

	private String id;
			
	private Vector<String> combination_of_accepting_states_vector; // Vector containing a unique combinations of accepting states related to the automata representing LTL/Declare constraints. 
		
	public CombinationOfAcceptingStates(String cosID, Vector<String> vector_with_combination_of_states) {		
		id = cosID;
		combination_of_accepting_states_vector = vector_with_combination_of_states;
	}

	public Vector<String> getCombinationOfAcceptingStates_vector() {
		return combination_of_accepting_states_vector;
	}

	public void setCombinationOfAcceptingStates_vector(Vector<String> coas_vector) {
		this.combination_of_accepting_states_vector = coas_vector;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
