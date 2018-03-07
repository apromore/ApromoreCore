package au.ltl.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import org.processmining.ltl2automaton.plugins.automaton.Automaton;

public class Constants {

	//
	// Vector that records the complete alphabet of activities that appear both in the log traces and in the Declare/LTL constraints. 
	// Notice that the repository may contain activities that are never used in any trace or in any constraints. 
	// Conversely, the union of activities appearing in the traces and in the constraints is ALWAYS included in this repository.
	//
	// -> This vector is re-initialized and populated during the transition from the AlphabetPerspective panel to the TracesPerspective Panel (after pressing the button "Next Step"). 
	// -> This vector is re-initialized when a new log is created from scratch (i.e., when the 'New' Item of the menu is pressed).
	// -> This vector is re-initialized and populated when an existing XES file is imported into the software (i.e., when the 'Open' Item of the menu is pressed).
	// -> This vector can be potentially augmented when a new Declare model is imported, if its constraints refer to activities not contained in the vector.
	//
	private Vector<String> activities_repository_vector = new Vector<String>();
		
	//
	// Vector that records the alphabet of the activities appearing just in the log traces. 
	// Notice that such an alphabet may include activities NOT used in any Declare/LTL constraint.
	//
	// -> This vector is re-initialized and populated during the transition from the TracesPerspective panel to the ConstraintsPerspective Panel (after pressing the button "Next Step"). 
	//	
	private Vector<String> alphabet_of_the_traces_vector = new Vector<String>();
	
	//
	// Vector that records the alphabet of activities that appear in the Declare/LTL constraints.
	// Notice that such an alphabet may include activities that DO NOT appear in any trace.
	//
	// -> This vector is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private Vector<String> alphabet_of_the_constraints_vector = new Vector<String>();
		
	//
	// Vector that records all the traces (represented as java objects of kind "Trace") of the log. 
	// -> This vector is re-initialized when a new log is created from scratch (i.e., when the 'New' Item of the menu is pressed) and when 
	//    an existing event log from a XES file is imported (i.e., when the 'Open' Item of the menu is pressed). 
	// -> This vector is populated on-the-fly during the usage of the TracesPerspective panel, each time a new trace is created/removed or when 
	//    an activity is inserted/moved or removed in/from a trace. The pressing of the button "Next Step" DOES NOT modify this vector, which is 
	//    instead updated each time a modification on an existing trace is performed. 
	// -> This vector is re-initialized and populated when an existing XES file is imported into the software (i.e., when the 'Open' Item of the menu is pressed).
	//
	private Vector<Trace> all_traces_vector = new Vector<Trace>();
	
	//
	// Vector that records all the LTL/Declare constraints, represented as String objects. 
	//
	// -> This vector is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	// 
	private Vector<String> all_constraints_vector = new Vector<String>();
		
	//
	// Vector that records the cost of adding/removing activities in/from a trace
	// It is a Vector of Vectors, where each Vector is built in the following way:
	// - the first element is the name of the activity, 
	// - the second element is the cost of adding the activity into the trace
	// - the third element is the cost of removing the activity from the trace
	//	
	// -> This vector is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private Vector<Vector<String>> activities_cost_vector = new Vector<Vector<String>>();
	
	private int addActionCost;
	private int deleteActionCost;
	
	//
	// Variables used to record the minimum length and the maximum length of a log trace.
	//	
	// -> This variables are re-initialized during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private int minimum_length_of_a_trace = 0;
	private int maximum_length_of_a_trace = 0;
	
	//
	// This Hashtable records the content of any trace (the KEY is the name of the trace, the VALUE is the original content of the trace).
	// Notice that if a trace TY has the SAME content of an already included trace TX (i.e., if two traces have the same content), TY will be discarded.
	//	
	// -> This variables are re-initialized during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	
	
	
	private  Hashtable<String, String> content_of_any_different_trace_Hashtable;
	
	//########################################################################
	/////////////////////// **** AAAI-17 encoding **** ///////////////////////
	//########################################################################
	
	public  int getAddActionCost() {
		return addActionCost;
	}
	public  void setAddActionCost(int addActionCost) {
		this.addActionCost = addActionCost;
	}
	public  int getDeleteActionCost() {
		return deleteActionCost;
	}
	public  void setDeleteActionCost(int deleteActionCost) {
		this.deleteActionCost = deleteActionCost;
	}
	//
	// Vector that records all the automata (objects of kind Automaton) associated to the Declare constraints (for AAAI17 encoding).
	//	
	// -> This vector is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private  Vector<Automaton> automata_vector = new Vector<Automaton>();
	
	//
	// Vector that records all the relevant transitions objects (connecting two different states of an automaton) of the automata associated to the Declare/LTL constraints (for AAAI17 encoding). 
	//	
	// -> This vector is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private  Vector<RelevantTransition> relevant_transitions_vector = new Vector<RelevantTransition>();
		
	//
	// Vector that records all the valid combinations of relevant transitions (connecting two different states; combinations containing 
	// transitions of the same automaton are discarded) of automata associated to the LTL/Declare constraints (for AAAI17 encoding).
	// Each instance of the vector is a unique combination of transitions related to a specific label.
	//	
	// -> This vector is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private  Vector<CombinationOfRelevantTransitions> combination_of_transitions_vector = new Vector<CombinationOfRelevantTransitions>();
	
	//
	// The Multimap "relevant_transitions_map" will contain the list of relevant transitions taken from any automaton and associate them 
	// to their specific label (e.g., a=[tr_0_0, tr_1_0], b=[tr_1_2], etc.) (for AAAI17 encoding).        				
	//	
	// -> This map is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private  Multimap<String, String> relevant_transitions_map = HashMultimap.create();
		
	//
	// Vectors used to record all the states/the accepting states/the initial states of the automata associated to 
	// the LTL/Declare constraints (for AAAI17 encoding).
	//	
	// -> These vectors are re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private  Vector<String> automata_all_states_vector = new Vector<String>();
	private  Vector<String> automata_accepting_states_vector = new Vector<String>();
	private  Vector<String> automata_initial_states_vector = new Vector<String>();
	
	//
	// Auxiliary StringBuffers used to record in PDDL (for the initial state) all the states/the accepting states/the initial states 
	// of the automata associated to the Declare/LTL constraints in the PDDL format (for AAAI17 encoding).
	//	
	// -> These StringBuffers are re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private  StringBuffer PDDL_automata_all_states_sb = new StringBuffer();
	private  StringBuffer PDDL_automata_accepting_states_sb = new StringBuffer();
	private  StringBuffer PDDL_automata_initial_states_sb = new StringBuffer();
	
	
	//
	// Vector that records all the valid combinations of accepting states (combinations containing 
	// states from the same automaton are discarded) of automata associated to the LTL/Declare constraints (for AAAI17 encoding).
	// Each instance of the vector is a unique combination of accepting states.
	//	
	// -> This vector is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private  Vector<CombinationOfAcceptingStates> combination_of_accepting_states_vector = new Vector<CombinationOfAcceptingStates>();
		
	//
	// Vector used to record all the non accepting sink states of the automata associated to 
	// the Declare/LTL constraints (for AAAI17 encoding).
	//	
	// -> This vector is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private  Vector<String> automata_sink_non_accepting_states_vector = new Vector<String>();
	
	//
	// Vector used to record the name of the "abstract" accepting states required for all those automata associated to the Declare/LTL constraints 
	// having more than one regular accepting state. In such a case, instead of having several accepting states for one automaton, 
	// a further abstract accepting state is generated: at this point any regular accepting state of the automaton will have a transition 
	// ending in such an abstract state (for AAAI17 encoding, to avoid the presence of OR conditions in the goal of the planning problem).
	//	
	// -> This vector is re-initialized and populated during the transition from the ConstraintsPerspective panel to the PlannerPerspective panel (after pressing the button "Next Step"). 
	//
	private  Vector<String> automata_abstract_accepting_states_vector = new Vector<String>();
	
	private  HashMultimap<String,String> labels_to_sink_states =HashMultimap.create();

	private  HashMap<String,Integer> task_numberOfTraces_map= new HashMap();

	
	private  Vector<String> PDDL_activities_vector = new Vector<String>();
	

	public  String LTL_NOT = "\u00AC";
	public  String LTL_OR = "\u2228";
	public  String LTL_AND = "\u2227";
	public  String LTL_IMPLIES = "\u2192";
	public  String LTL_eventually = "\u25C7";
	public  String LTL_globally = "\u25A1";
	public  String LTL_next = "\u25CB";
	public  String LTL_weak_until = "W".toUpperCase();
	public  String LTL_until = "U".toUpperCase();
	
	public  String PDDL_encoding = "AAAI17"; //It can be equal to "AAAI17" or to "ICAPS16".
	

	private  int numberOfTraces=1;
	private  HashSet<Trace> traces = new HashSet<Trace>();
	//
	// Boolean variable used to record the decision to discard or not the duplicated traces of a log during the alignment.
	//
	private  boolean discard_duplicated_traces = false;
	
	///////////////////////////////////////
	// -- LIST OF GETTERS AND SETTERS -- //
	///////////////////////////////////////

    public  void setNumberOfTraces(int i){
      numberOfTraces = i;
    }

	//
	// Getters and Setters to retrieve and manipulate the vectors containing the complete repository of activities, the alphabet of the traces and of the constraints.
	//	

	
	public  Vector<String> getActivitiesRepository_vector() {
		return activities_repository_vector;
	}
	public  HashMap<String, Integer> getTask_numberOfTraces_map() {
		return task_numberOfTraces_map;
	}
	public  void setTask_numberOfTraces_map(HashMap<String, Integer> task_numberOfTraces_map) {
		this.task_numberOfTraces_map = task_numberOfTraces_map;
		this.task_numberOfTraces_map = task_numberOfTraces_map;
	}
	public  HashSet<Trace> getTraces() {
		return traces;
	}
	public  void setTraces(HashSet<Trace> traces) {
		this.traces = traces;
	}
	public  int getNumberOfTraces() {
		int i = numberOfTraces;
		numberOfTraces++;
		return i;
	}
	public  int getNumberOfTracesFixed() {
		return numberOfTraces-1;
	}

	public  HashMultimap<String, String> getLabels_to_sink_states() {
		return labels_to_sink_states;
	}
	public  void setLabels_to_sink_states(HashMultimap<String, String> labels_to_sink_states) {
		this.labels_to_sink_states = labels_to_sink_states;
	}
	public  void setActivitiesRepository_vector(Vector<String> v) {
		activities_repository_vector = v;
	}
	public  Vector<String> getAlphabetOfTheConstraints_vector() {
		return alphabet_of_the_constraints_vector;
	}
	public  void setAlphabetOfTheConstraints_vector(Vector<String> constraints_alphabet) {
		alphabet_of_the_constraints_vector = constraints_alphabet;
	}
	public  Vector<String> getAlphabetOfTheTraces_vector() {
		return alphabet_of_the_traces_vector;
	}
	public  void setAlphabetOfTheTraces_vector(Vector<String> traces_alphabet) {
		this.alphabet_of_the_traces_vector = traces_alphabet;
	}
	
	//
	// Getters and Setters to retrieve and manipulate the vectors containing all the traces and all the LTL/Declare constraints.
	//		
	public  Vector<Trace> getAllTraces_vector() {
		return all_traces_vector;
	}
	public  void setAllTraces_vector(Vector<Trace> all_traces_vector) {
		this.all_traces_vector = all_traces_vector;
	}
	public  Vector<String> getAllConstraints_vector() {
		return all_constraints_vector;
	}
	public  void setAllConstraints_vector(Vector<String> cnt_vector) {
		this.all_constraints_vector = cnt_vector;
	}
	
	//
	// Getters and Setters to retrieve and manipulate the vector containing the cost of adding/removing activities into/from the trace.
	//			
	public  Vector<Vector<String>> getActivitiesCost_vector() {
		return activities_cost_vector;
	}
	public  void setActivitiesCost_vector(Vector<Vector<String>> cost_vector) {
		this.activities_cost_vector = cost_vector;
	}

	/////////////////////// **** AAAI-17 encoding
	
	//
	// Getters and Setters to retrieve and manipulate the vectors containing all the automata and their relevant transitions (for AAAI17 encoding).
	//		
	public  Vector<Automaton> getAutomata_vector() {
		return automata_vector;
	}
	public  void setAutomata_vector(Vector<Automaton> automata_vector) {
		this.automata_vector = automata_vector;
	}
	public  Vector<RelevantTransition> getRelevantTransitions_vector() {
		return relevant_transitions_vector;
	}
	public  void setRelevantTransitions_vector(Vector<RelevantTransition> transitions_vector) {
		this.relevant_transitions_vector = transitions_vector;
	}
	
	//
	// Getters and Setters to retrieve and manipulate the vectors containing all the automata and their relevant transitions (for AAAI17 encoding).
	//
	
	public  Vector<CombinationOfRelevantTransitions> getCombinationOfRelevantTransitions_vector() {
		return combination_of_transitions_vector;
	}
	public  void setCombinationOfRelevantTransitions_vector(Vector<CombinationOfRelevantTransitions> combination_of_transitions_vector) {
		this.combination_of_transitions_vector = combination_of_transitions_vector;
	}
	public  Multimap<String, String> getRelevantTransitions_map() {
		return relevant_transitions_map;
	}
	public  void setRelevantTransitions_map(Multimap<String, String> relevant_transitions_map) {
		this.relevant_transitions_map = relevant_transitions_map;
	}
	
	//
	// Getters and Setters to retrieve and manipulate the vector containing all the combination of accepting states (for AAAI17 encoding).
	//
	public  Vector<CombinationOfAcceptingStates> getCombinationOfAcceptingStates_vector() {
		return combination_of_accepting_states_vector;
	}
	public  void setCombinationOfAcceptingStates_vector(Vector<CombinationOfAcceptingStates> combination_vector) {
		this.combination_of_accepting_states_vector = combination_vector;
	}
	
	//
	// Getters and Setters to retrieve and manipulate the vectors containing the states/the accepting states/the initial states/the non-accepting 
	// sink states/ the abstract accepting sink states of the automata associated to the LTL/Declare constraints (for AAAI17 encoding).
	//	
	public  Vector<String> getAutomataAllStates_vector() {
		return automata_all_states_vector;
	}
	public  Vector<String> getAutomataAcceptingStates_vector() {
		return automata_accepting_states_vector;
	}
	public  Vector<String> getAutomataInitialStates_vector() {
		return automata_initial_states_vector;
	}
	public  void setAutomataAllStates_vector(Vector<String> automata_all_states) {
		this.automata_all_states_vector = automata_all_states;
	}
	public  void setAutomataAcceptingStates_vector(Vector<String> automata_accepting_states) {
		this.automata_accepting_states_vector = automata_accepting_states;
	}
	public  void setAutomataInitialStates_vector(Vector<String> automata_initial_states) {
		this.automata_initial_states_vector = automata_initial_states;
	}
	public  Vector<String> getAutomataSinkNonAcceptingStates_vector() {
		return automata_sink_non_accepting_states_vector;
	}
	public  Vector<String> getAutomataAbstractAcceptingStates_vector() {
		return automata_abstract_accepting_states_vector;
	}
	public  void setAutomataSinkNonAcceptingStates_vector(Vector<String> automata_sink_non_accepting_states) {
		this.automata_sink_non_accepting_states_vector = automata_sink_non_accepting_states;
	}
	public  void setAutomataAbstractAcceptingStates_vector(Vector<String> automata_abstract_goal_states) {
		this.automata_abstract_accepting_states_vector = automata_abstract_goal_states;
	}
	
	//
	// Getters and Setters to retrieve and manipulate the auxiliary StringBuffers used to record in PDDL all the states/the accepting states/the initial states 
	// of the automata associated to the Declare/LTL constraints in the PDDL format (for AAAI17 encoding). 
	//		
	public  StringBuffer getPDDLAutomataAllStates_sb() {
		return PDDL_automata_all_states_sb;
	}
	public  StringBuffer getPDDLAutomataAcceptingStates_sb() {
		return PDDL_automata_accepting_states_sb;
	}
	public  StringBuffer getPDDLAutomataInitialStates_sb() {
		return PDDL_automata_initial_states_sb;
	}
	public  void setPDDLAutomataAllStates_sb(StringBuffer pDDL_automata_all_states_sb) {
		PDDL_automata_all_states_sb = pDDL_automata_all_states_sb;
	}
	public  void setPDDLAutomataAcceptingStates_sb(StringBuffer pDDL_automata_accepting_states_sb) {
		PDDL_automata_accepting_states_sb = pDDL_automata_accepting_states_sb;
	}
	public  void setPDDLAutomataInitialStates_sb(StringBuffer pDDL_automata_initial_states_sb) {
		PDDL_automata_initial_states_sb = pDDL_automata_initial_states_sb;
	}
	
	/////////////////////// **** END of AAAI-17 encoding
	
	public  Vector<String> getPDDLActivitiesVector() {
		return PDDL_activities_vector;
	}
	public  void setPDDLActivitiesVector(Vector<String> PDDL_activities_vector) {
		this.PDDL_activities_vector = PDDL_activities_vector;
	}
	public  String getPDDL_encoding() {
		return PDDL_encoding;
	}
	public  void setPDDL_encoding(String pDDL_encoding) {
		PDDL_encoding = pDDL_encoding;
	}

	
	//
	// Getters and Setters to retrieve and manipulate: 
	// - the variables used to record the minimum length and the maximum length of a log trace.
	// - the boolean variable used to record the decision to discard or not the duplicated traces of a log during the alignment.
	//
	public  int getMinimumLengthOfATrace() {
		return minimum_length_of_a_trace;
	}
	public  int getMaximumLengthOfATrace() {
		return maximum_length_of_a_trace;
	}
	public  void setMinimumLengthOfATrace(int minimum_length_of_a_trace) {
		this.minimum_length_of_a_trace = minimum_length_of_a_trace;
	}
	public  void setMaximumLengthOfATrace(int maximum_length_of_a_trace) {
		this.maximum_length_of_a_trace = maximum_length_of_a_trace;
	}
	public  boolean isDiscard_duplicated_traces() {
		return discard_duplicated_traces;
	}
	public  void setDiscard_duplicated_traces(boolean discard_duplicated_traces) {
		this.discard_duplicated_traces = discard_duplicated_traces;
	}
	
	
	
	
	public  Hashtable<String, String> getContentOfAnyDifferentTrace_Hashtable() {
		return content_of_any_different_trace_Hashtable;
	}
	public  void setContentOfAnyDifferentTrace_Hashtable(Hashtable<String, String> content_of_any_trace_Hashtable) {
		this.content_of_any_different_trace_Hashtable = content_of_any_trace_Hashtable;
	}
	
	
	
}
