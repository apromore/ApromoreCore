package au.ltl.domain;

import java.util.HashSet;
import java.util.Vector;

import au.ltl.utils.Utilities;

public class CombinationOfRelevantTransitions {

	private String id;
	
	private String label; // Label related to the transitions involved in the combination
	
	private int length; // Length of the combination
	
	private Vector<String> combination_of_transitions_vector; // Vector containing a unique combinations of transitions related to a specific label 
	private Vector<String> original_transitions_associated_to_the_label_vector; // Vector with all the transitions associated to the specific label
	
	private StringBuffer PDDL_preconditions; //StringBuffer recording the preconditions required to perform the transition
	private StringBuffer PDDL_effects; //StringBuffer recording the effects provided after the performance of the transition
	
	private int number_of_conditions_in_the_PDDL_preconditions; // It indicates the number of conditions appearing in the preconditions. 
	
	private boolean contains_sink_states; 
	
	private HashSet<String> automaton_source_states;

	Constants constant;

	public CombinationOfRelevantTransitions(String cotID, String comb_label, int length_of_the_combination, Vector<String> vector_with_combination_of_transitions, Vector<String> original_transitions_associated_to_the_label, Constants constant) {
		id = cotID;
		label = comb_label;
		length = length_of_the_combination;
		number_of_conditions_in_the_PDDL_preconditions = length_of_the_combination;
		combination_of_transitions_vector = vector_with_combination_of_transitions;
		original_transitions_associated_to_the_label_vector = original_transitions_associated_to_the_label;
		contains_sink_states = false;
		automaton_source_states=new HashSet<>();
		this.constant = constant;
		setPreconditionsAndEffects();
	}
	
	public HashSet<String> getAutomaton_source_states() {
		return automaton_source_states;
	}



	public void setAutomaton_source_states(HashSet<String> automaton_source_states) {
		this.automaton_source_states = automaton_source_states;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Vector<String> getCombination_of_transitions_vector() {
		return combination_of_transitions_vector;
	}

	public void setCombination_of_transitions_vector(Vector<String> transitions_vector) {
		this.combination_of_transitions_vector = transitions_vector;
	}

	public Vector<String> getOriginal_transitions_associated_to_the_label_vector() {
		return original_transitions_associated_to_the_label_vector;
	}

	public void setOriginal_transitions_associated_to_the_label_array(Vector<String> original_transitions_associated_to_the_label) {
		this.original_transitions_associated_to_the_label_vector = original_transitions_associated_to_the_label;
	}

	public StringBuffer getPDDL_preconditions() {
		return PDDL_preconditions;
	}

	public StringBuffer getPDDL_effects() {
		return PDDL_effects;
	}

	public void setPDDL_preconditions(StringBuffer pDDL_preconditions) {
		PDDL_preconditions = pDDL_preconditions;
	}

	public void setPDDL_effects(StringBuffer pDDL_effects) {
		PDDL_effects = pDDL_effects;
	}
		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public int getNumberOfConditionsInThePDDLPreconditions() {
		return number_of_conditions_in_the_PDDL_preconditions;
	}

	public void setNumberOfConditionsInThePDDLPreconditions(int number) {
		number_of_conditions_in_the_PDDL_preconditions = number;
	}
	
	public boolean containsSinkstates() {
		return contains_sink_states;
	}

	public void setContainsSinkStates(boolean contains_sink_states) {
		this.contains_sink_states = contains_sink_states;
	}

	private void setPreconditionsAndEffects() {
		
		Vector<String> transitions_in_the_combination_vector = getCombination_of_transitions_vector();
		Vector<String> all_transitions_associated_to_the_label_vector  = getOriginal_transitions_associated_to_the_label_vector();
		Vector<String> automata_vector = new Vector<String>();
		
		StringBuffer precBuffer = new StringBuffer();
		StringBuffer effBuffer = new StringBuffer();
		
		for(int m=0;m<transitions_in_the_combination_vector.size();m++) {
			
			String rtID = transitions_in_the_combination_vector.elementAt(m);

			all_transitions_associated_to_the_label_vector.removeElement(rtID);
			
			RelevantTransition rt = Utilities.getRelevantTransition(rtID,constant);
			
			automaton_source_states.add(rt.getSource_state());

						
			if(constant.getAutomataSinkNonAcceptingStates_vector().contains(rt.getTarget_state()))
				contains_sink_states = true;
			
			precBuffer.append(rt.getPDDL_preconditions() + " ");
			effBuffer.append(rt.getPDDL_effects() + " ");
			
			// Identify the underlying automaton of the transition included in the combination and records it in a vector
			int first_underscore = rtID.indexOf("_");
			int last_underscore = rtID.lastIndexOf("_");
			String automaton_id = rtID.substring(first_underscore+1, last_underscore);
			automata_vector.addElement(automaton_id);
			//////////////////////////////
				
		}
		
		for(int n=0;n<all_transitions_associated_to_the_label_vector.size();n++) {
			
			String rt_not_in_the_combination_ID = all_transitions_associated_to_the_label_vector.elementAt(n);
			
			// Identify the underlying automaton of the transition not included the combination and check if it already exists 
			// in the vector. If so, it means that a precondition considering the same automaton has already been considered.
			// Also the length of the combination is increased.
			int first_underscore = rt_not_in_the_combination_ID.indexOf("_");
			int last_underscore = rt_not_in_the_combination_ID.lastIndexOf("_");
			String automaton_id = rt_not_in_the_combination_ID.substring(first_underscore+1, last_underscore);
			
			if(!automata_vector.contains(automaton_id)) {
				
				RelevantTransition rt2 = Utilities.getRelevantTransition(rt_not_in_the_combination_ID,constant);
				precBuffer.append("(not " + rt2.getPDDL_preconditions() + ") ");
				
				int new_number_of_conditions_in_the_PDDL_preconditions = number_of_conditions_in_the_PDDL_preconditions + 1;
				setNumberOfConditionsInThePDDLPreconditions(new_number_of_conditions_in_the_PDDL_preconditions);
				
			}
		}
		setPDDL_preconditions(precBuffer);
		setPDDL_effects(effBuffer);
		return;
	}


}
