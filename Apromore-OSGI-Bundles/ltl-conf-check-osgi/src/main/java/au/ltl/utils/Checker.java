package au.ltl.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import org.processmining.ltl2automaton.plugins.automaton.Automaton;
import org.processmining.ltl2automaton.plugins.automaton.State;
import org.processmining.ltl2automaton.plugins.automaton.Transition;
import org.processmining.ltl2automaton.plugins.formula.DefaultParser;
import org.processmining.ltl2automaton.plugins.formula.Formula;
import org.processmining.ltl2automaton.plugins.formula.conjunction.ConjunctionFactory;
import org.processmining.ltl2automaton.plugins.formula.conjunction.ConjunctionTreeLeaf;
import org.processmining.ltl2automaton.plugins.formula.conjunction.ConjunctionTreeNode;
import org.processmining.ltl2automaton.plugins.formula.conjunction.DefaultTreeFactory;
import org.processmining.ltl2automaton.plugins.formula.conjunction.GroupedTreeConjunction;
import org.processmining.ltl2automaton.plugins.formula.conjunction.TreeFactory;
import org.processmining.ltl2automaton.plugins.ltl.SyntaxParserException;

import au.ltl.domain.Trace;
import au.ltl.domain.CombinationOfRelevantTransitions;
import au.ltl.domain.CombinationOfAcceptingStates;
import au.ltl.domain.Constants;
import au.ltl.domain.RelevantTransition;

public class Checker {

	private Trace trace;
	private HashSet<String> constraints;
	private String constraint_name;
	
	protected static final String PLAN_FOUND_DIR_PREFIX = "plan_found_";
	protected static final String root = "/Users/armascer/Work/ApromoreCode/ApromoreCode/Apromore-OSGI-Bundles/ltl-conf-check-osgi/";
	protected static final String FAST_DOWNWARD_DIR_MAC = root+"fast-downward-mac"+File.separator;
	protected static final String FAST_DOWNWARD_DIR_WIN_LIN = root+"fast-downward-win-lin"+File.separator;
	protected static final String FAST_DOWNWARD_SCRIPT ="fast-downward.py";
	protected static final String FAST_DOWNWARD_DOMAIN ="domain.pddl";
	protected static final String FAST_DOWNWARD_PROBLEM ="problem.pddl";
	protected static final String RESULT_FOLDER = "result"+File.separator;
	protected Process plannerManagerProcess;
	Constants constant;

	public Checker(Trace trace, HashSet<String> constraints, String con_name, Constants constant) {
		super();
		this.trace = trace;
		this.constraints = constraints;
		constraint_name=con_name;
		this.constant = constant;
	}

	public void check(){

		//
		// Reset the global vector containing the list of Declare/LTL constraints.
		//	
		constant.setAllConstraints_vector(new Vector<String>()); 

		//
		// Reset the vector containing the alphabet of activities involved ONLY in the Declare/LTL constraints.
		//
		constant.setAlphabetOfTheConstraints_vector(new Vector<String>());

		//
		// Create a local vector containing an automaton for any Declare/LTL constraint.
		//	
		Vector<Automaton> automata_vector = new Vector<Automaton>();

		//
		// Create a local vector containing the relevant transitions (a transition is said to be "relevant" if the source and the target state 
		// are different) of any automaton representing a Declare/LTL constraint.
		//	
		Vector<RelevantTransition> relevant_transitions_vector = new Vector<RelevantTransition>();

		//
		// Reset the global vectors used to record all the states/the accepting states/the initial states 
		// of the automata associated to the Declare/LTL constraints.
		//
		constant.setAutomataInitialStates_vector(new Vector<String>());
		constant.setAutomataAcceptingStates_vector(new Vector<String>());
		constant.setAutomataAllStates_vector(new Vector<String>());

		//
		// Reset the global auxiliar stringbuffers used to record all the states/the accepting states/the initial states 
		// of the automata associated to the Declare/LTL constraints in the PDDL format.
		//
		constant.setPDDLAutomataInitialStates_sb(new StringBuffer());
		constant.setPDDLAutomataAcceptingStates_sb(new StringBuffer());
		constant.setPDDLAutomataAllStates_sb(new StringBuffer());

		//
		// Reset the global vector used to record the abstract accepting states of the automata associated to the Declare/LTL constraints.
		//
		constant.setAutomataAbstractAcceptingStates_vector(new Vector<String>());

		//
		// Reset the global auxiliar StringBuffer used to record all the PDDL actions required to connect the regular accepting states 
		// of one automaton to the abstract states stored in the vector "Constant.automata_abstract_accepting_states".
		//
		//constant.setPDDLActionsForAbstractAcceptingStates_sb(new StringBuffer());

		//
		// Reset the global vector used to record the non-accepting sink states of the automata associated to the Declare/LTL constraints.
		//
		constant.setAutomataSinkNonAcceptingStates_vector(new Vector<String>());
		
		HashMultimap<String,String> labels_to_sink_states = HashMultimap.create();
		constant.setLabels_to_sink_states(labels_to_sink_states);

		//
		// Define the prefix and the index of the states of the automata and of their relevant transitions.
		// For example, if the first automaton (i.e., with "automaton_index" equal to 0) has two states and three relevant transitions, 
		// we would have: s_0_0, s_0_1 (states) and tr_0_1, tr_0_2, tr_0_3 (relevant transitions).
		// A second automaton will "automaton_index" equal to 1, a third automaton will have "automaton_index" equal to 2, and so on.
		//
		String st_prefix = "s";
		String tr_prefix = "tr";
		int automaton_index = 0;
		int single_tr_index = 0;	

		//
		// Reset the local Multimap "transitions_map", which will contain the list of relevant transitions taken from 
		// any automaton with the associations to their specific label (e.g., a=[tr_0_0,tr_1_0], b=[tr_1_2], etc.).
		//
		Multimap<String, String> transitions_map = HashMultimap.create();


		//
		// For any Declare/LTL constraint, generate the supporting structures required to synthesize correct planning domains and problems.
		//	         		
		Iterator <String> it_constraints=constraints.iterator();
		while(it_constraints.hasNext()){
			String constraint = it_constraints.next();
			//
			// Reset the local LTL formula that records the Declare/LTL constraint under consideration.
			//


			//
			// Reset the local vector used to record the accepting states of an automaton.
			//
			Vector<String> automaton_accepting_states_vector = new Vector<String>();

			//
			// Reset for any Declare/LTL constraint - i.e., for any corresponding automaton - the index of its relevant transitions.
			//	
			single_tr_index = 0;

			Automaton automaton = generateAutomatonByLTLFormula(constraint);

			//Since we are working with minimum automata, we need to detect labels that would bring to non-accepting sink
			//states in non minimum automata. 
			//The multimap "labels_to_sink_states" will contain for each label, a set of automaton states from which 
			// was present a transition with that label leading to a non-accepting sink state

			labels_to_sink_states = constant.getLabels_to_sink_states();
			


			Iterator <State> it_states_automaton=automaton.iterator();

			while(it_states_automaton.hasNext()){
				State state=it_states_automaton.next();
				HashSet<String> labels_with_transition=new HashSet<String>();
				Iterator<Transition> it_tran_state= state.getOutput().iterator();
				while(it_tran_state.hasNext()){
					Transition transition=it_tran_state.next();
					if(transition.isPositive()){
						labels_with_transition.add(transition.getPositiveLabel());
					}
					else{
						Collection<String> coll = transition.getNegativeLabels();  
						for(int ix=0;ix<constant.getActivitiesRepository_vector().size();ix++) {
							String symbol = constant.getActivitiesRepository_vector().elementAt(ix);
							symbol = symbol.replaceAll("\\[", "").replaceAll("\\]","");
							if(transition.isAll()){
								labels_with_transition.add(symbol);
							}
							else if(coll!=null){
								Iterator<String> it= coll.iterator();
								boolean contained=false;
								while(it.hasNext()){
									if(it.next().equals(symbol)){
										contained=true;
									}
								}
								if(!contained){
									labels_with_transition.add(symbol);
								}
							}


						}

					}

				}
				String tr_source_state = st_prefix + "_" + automaton_index + "_" + state.getId();
				for(int ix=0;ix<constant.getActivitiesRepository_vector().size();ix++) {
					String symbol = constant.getActivitiesRepository_vector().elementAt(ix).replaceAll("\\[", "").replaceAll("\\]","");
					if(!labels_with_transition.contains(symbol)){
						labels_to_sink_states.put(symbol,tr_source_state);
					}
				}
			}
			
			constant.setLabels_to_sink_states(labels_to_sink_states);

			State initial_state_of_the_automaton = automaton.getInit();

			if(automaton.getStateCount()==1){
				String tr_source_state = st_prefix + "_" + automaton_index + "_" + initial_state_of_the_automaton.getId();
				if(!constant.getAutomataAllStates_vector().contains(tr_source_state))  {
					constant.getAutomataAllStates_vector().addElement(tr_source_state);
					constant.getPDDLAutomataAllStates_sb().append(tr_source_state + " - state\n");        	            					 
				} 
				//
				// Keep track of all the accepting states of the automaton under consideration and records it in a local vector.
				if(initial_state_of_the_automaton.isAccepting() && !automaton_accepting_states_vector.contains(tr_source_state))  {
					automaton_accepting_states_vector.addElement(tr_source_state);
				}
			}

			//
			// Identify the initial state of the specific automaton under consideration and records it in the global vector/stringbuffer of the initial states.
			//
			if(!constant.getAutomataInitialStates_vector().contains(st_prefix + "_" + automaton_index + "_" + initial_state_of_the_automaton.getId())) {
				constant.getAutomataInitialStates_vector().addElement(st_prefix + "_" + automaton_index + "_" + initial_state_of_the_automaton.getId());
				constant.getPDDLAutomataInitialStates_sb().append("(currstate " + st_prefix + "_" + automaton_index + "_" + initial_state_of_the_automaton.getId() + ")\n");
			}

			//
			// For any transition of the automaton under consideration, we check if such transition is relevant 
			// (i.e., if it connects a target state different from the source state).
			//
			Iterator<Transition> it = automaton.transitions().iterator();

			while(it.hasNext()) {

				Transition transition = (Transition) it.next();
				int tr_source_state_id = transition.getSource().getId();
				int tr_target_state_id = transition.getTarget().getId();

				if(tr_source_state_id != tr_target_state_id) {

					//	        	            	  			
					// If the transition is relevant, we identify its source state, its target state and its label.
					//
					String tr_source_state = st_prefix + "_" + automaton_index + "_" + tr_source_state_id;
					String tr_target_state = st_prefix + "_" + automaton_index + "_" + tr_target_state_id;
					String tr_id = null;
					String tr_label = null;

					//
					// Simple case: the label is positive (e.g., A).
					//
					if(!transition.isNegative())  {
						tr_id = tr_prefix + "_" + automaton_index + "_" + single_tr_index;
						tr_label = transition.getPositiveLabel();
						if(!constant.getAlphabetOfTheConstraints_vector().contains(tr_label)){
							constant.getAlphabetOfTheConstraints_vector().addElement(transition.getPositiveLabel());
						}


						//
						// Create a new RelevantTransition object and records it in the global vector of relevant transitions.
						//	
						RelevantTransition relevant_transition = new RelevantTransition(tr_id, tr_source_state, tr_target_state, tr_label, transition.getPositiveLabel());
						relevant_transitions_vector.addElement(relevant_transition);

						//
						// Associate in the "transition_map" object the label of the transition just created to its ID. Remember that "transitions_map" 
						// will contain the list of relevant transitions taken from any automaton with the associations to their specific label 
						// (e.g., a=[tr_0_0,tr_1_0], b=[tr_1_2], etc.).
						//	
						transitions_map.put(tr_label, tr_id);

						single_tr_index++;
					}
					else { // If the label is negative (e.g., !A) there are several possible concrete positive labels (...B,C,D,E,...etc.), 
						// i.e., several possible valid relevant transitions to be recorded. Starting from a negative label, the positive 
						// ones are inferred from the repository of activities involved in the log and in the Declare constraints.

						Collection<String> coll = transition.getNegativeLabels();

						for(int ix=0;ix<constant.getActivitiesRepository_vector().size();ix++) {
							tr_id = tr_prefix + "_" + automaton_index + "_" + single_tr_index;
							String symbol = constant.getActivitiesRepository_vector().elementAt(ix);
							if(!coll.contains(symbol)) {
								tr_label = symbol;
								RelevantTransition relevant_transition = new RelevantTransition(tr_id, tr_source_state, tr_target_state, tr_label, transition.getPositiveLabel());
								relevant_transitions_vector.addElement(relevant_transition);
								if(!constant.getAlphabetOfTheConstraints_vector().contains(tr_label)){
									constant.getAlphabetOfTheConstraints_vector().addElement(transition.getPositiveLabel());
								}

								//
								// Associate in the "transition_map" object the label of the transition just created to its ID
								//
								transitions_map.put(tr_label, tr_id);

								single_tr_index++;
							}
						}
					}

					//
					// Keep track of all the states of the automaton under consideration and records it in the corresponding global stringbuffer/vector.
					//
					if(!constant.getAutomataAllStates_vector().contains(tr_source_state))  {
						constant.getAutomataAllStates_vector().addElement(tr_source_state);
						constant.getPDDLAutomataAllStates_sb().append(tr_source_state + " - state\n");        	            					 
					} 
					if(!constant.getAutomataAllStates_vector().contains(tr_target_state))  {
						constant.getAutomataAllStates_vector().addElement(tr_target_state);
						constant.getPDDLAutomataAllStates_sb().append(tr_target_state + " - state\n");        
					}

					//
					// Keep track of all the accepting states of the automaton under consideration and records it in a local vector.
					//
					if(transition.getSource().isAccepting() && !automaton_accepting_states_vector.contains(tr_source_state))  {
						automaton_accepting_states_vector.addElement(tr_source_state);
					}
					if(transition.getTarget().isAccepting() && !automaton_accepting_states_vector.contains(tr_target_state))  {
						automaton_accepting_states_vector.addElement(tr_target_state);
					}	        	         

				}
			}

			// Record the accepting states of the automaton under consideration in the corresponding global vector and in the 
			// global StringBuffer used to take trace of the goal condition. 
			//
			// FIRST CASE: The automaton has several accepting states.
			//
			// If an automaton has more than one accepting state, such accepting states must be nested in an OR.
			// However, if disjunctive conditions are not allowed, an abstract state for the automaton must be generated, 
			// together with as many planning actions as are the regular accepting states. Such actions represent the transitions 
			// between the regular accepting states and the abstract accepting state generated.
			//
			if(automaton_accepting_states_vector.size() > 1) {

				String aut_abstract_state = st_prefix + "_" + automaton_index + "_" + "abstract";

				constant.getAutomataAbstractAcceptingStates_vector().addElement(aut_abstract_state);

				constant.getPDDLAutomataAcceptingStates_sb().append("(currstate " + aut_abstract_state + ")\n");  

				constant.getPDDLAutomataAllStates_sb().append(aut_abstract_state + " - state\n"); 

				for(int yu=0;yu<automaton_accepting_states_vector.size();yu++) {	         					
					if(!constant.getAutomataAcceptingStates_vector().contains(automaton_accepting_states_vector.elementAt(yu)))
						constant.getAutomataAcceptingStates_vector().addElement(automaton_accepting_states_vector.elementAt(yu));	         					
					if(!constant.getAutomataAllStates_vector().contains(automaton_accepting_states_vector.elementAt(yu)))
						constant.getAutomataAllStates_vector().addElement(automaton_accepting_states_vector.elementAt(yu));	         					
				}

			}
			//
			// SECOND CASE: The automaton has just one accepting state.
			//
			else {
				constant.getAutomataAcceptingStates_vector().addElement(automaton_accepting_states_vector.elementAt(0));
				constant.getPDDLAutomataAcceptingStates_sb().append("(currstate " + automaton_accepting_states_vector.elementAt(0) + ")\n"); 
			}

			//
			// Update the local vector containing an automaton for any Declare/LTL constraint.
			//	
			automata_vector.addElement(automaton);	        	         			


			//
			// The index is increased after having analyzed any automaton, in order to have unique IDs identifying uniquely the automata.
			//
			automaton_index++;
		}

		//
		// Update the global vectors containing the automata and the relevant transitions.
		//
		constant.setAutomata_vector(automata_vector);
		constant.setRelevantTransitions_vector(relevant_transitions_vector);

		//
		// Reset the global vector containing the combinations of relevant transitions.
		//
		constant.setCombinationOfRelevantTransitions_vector(new Vector<CombinationOfRelevantTransitions>());

		// Reset the global vector containing the combinations of relevant transitions
		constant.setRelevantTransitions_map(transitions_map);

		
		constant.setCombinationOfAcceptingStates_vector(new Vector<CombinationOfAcceptingStates>());

		Vector<String> automata_id_of_accepting_states_vector = new Vector<String>();
		for(int q=0;q<constant.getAutomataAcceptingStates_vector().size();q++) {
			String state_id = constant.getAutomataAcceptingStates_vector().elementAt(q);
			//System.out.println(state_id);
			int first_underscore = state_id.indexOf("_");
			int last_underscore = state_id.lastIndexOf("_");
			String automaton_id = state_id.substring(first_underscore+1, last_underscore);
			//System.out.println(automata_id);
			if(!automata_id_of_accepting_states_vector.contains(automaton_id))
				automata_id_of_accepting_states_vector.addElement(automaton_id);
		}
		int k_value = automata_id_of_accepting_states_vector.size();
		Object[] arr = constant.getAutomataAcceptingStates_vector().toArray();

		Utilities.findCombinationsOfAcceptingStates(arr, k_value, 0, new String[k_value],constant);


		Set<String> set_of_keys = constant.getRelevantTransitions_map().keySet();

		//
		// For any key of the "transition_map" object, i.e., for any label, identify the relevant transitions associated 
		// to that label.
		//
		Iterator<String> it = set_of_keys.iterator();
		while(it.hasNext())  {

			String key = (String) it.next();
			Collection<String> values = constant.getRelevantTransitions_map().get(key);

			/*
				System.out.print(key + " --> ");
				System.out.println(values);
			 */

			Object[] values_array = values.toArray();

			//
			// Given a specific label (e.g., A), which groups several transitions of different automata 
			// (e.g., tr_0_0, tr_1_1, tr_1_2), it is important to discard those combinations that contain 
			// transitions of the same automaton (for example, any combination that includes at the same time 
			// tr_1_1 and tr_1_2 must be discarded).
			//
			// FIRST OF ALL, we identify the underlying automata of the relevant transitions associated to the 
			// specific label. In the above example, two different automata having ID "0" and "1" are considered. 
			//
			Vector<String> automata_id_of_relevant_transitions_vector = new Vector<String>();
			for(int l=0;l<values_array.length;l++) {
				String transition_id = values_array[l].toString();
				//System.out.println(transition_id);
				int first_underscore = transition_id.indexOf("_");
				int last_underscore = transition_id.lastIndexOf("_");
				String automaton_id = transition_id.substring(first_underscore+1, last_underscore);
				//System.out.println(automata_id);
				if(!automata_id_of_relevant_transitions_vector.contains(automaton_id))
					automata_id_of_relevant_transitions_vector.addElement(automaton_id);
			}

			//
			// To identify the number of different automata involved in the relevant transitions helps to set the 
			// maximum "k" value to calculate the combination of relevant transitions (e.g., in our example, we 
			// calculate combinations with k=1 and k=2 at maximum).
			// The method invoked removes automatically any combination that contains two transitions of the same automaton.
			//
			for(int kl=1;kl<=automata_id_of_relevant_transitions_vector.size();kl++) {
				Utilities.findCombinationsOfTransitions(values_array, key, kl, kl, 0, new String[kl],constant);
			}
		}
		
		generatePDDLfiles();
		
		invokePlanner();
		

	}
	

	private void invokePlanner() {
		
		File filePlan = new File( System.getProperty("user.dir")+File.separator+"sas_plan");

		if(filePlan.exists() && !filePlan.isDirectory()) { 
			filePlan.delete();
		}


		// creating string for planner invocation
				ArrayList<String> commandComponents = new ArrayList<>();

				// Python is assumed to be installed as default version on the user machine
				String pythonInterpreter = "python";
				commandComponents.add(pythonInterpreter);


				//DECOMMENTARE QUANDO AGGIUNGI SPACCHETTAMENTO
				/*	if (resourcesUnpacker != null) {
					context.log("Waiting for planner resources to be unpacked.");
					resourcesUnpacker.join();
				}*/



				// the path to the fast-downward launcher script
				File fdScript;

				String os=OSUtils.getOs();
				if(os.equals("mac")){
					fdScript = new File(FAST_DOWNWARD_DIR_MAC+FAST_DOWNWARD_SCRIPT);
				}else fdScript = new File(FAST_DOWNWARD_DIR_WIN_LIN+FAST_DOWNWARD_SCRIPT);
				
				try {
					commandComponents.add(fdScript.getCanonicalPath());

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				

				// Fast-Downward is assumed to be built in advance both for 32 and 64 bits OS (both Windows and Unix-like).
				commandComponents.add("--build");
				if (OSUtils.is64bitsOS())
					commandComponents.add("release64");
				else
					commandComponents.add("release32");

				//redirect the found plan to the proper folder NO PERCHE' è BUGGATO SU LINUX
				/*	commandComponents.add("--plan-file");
				commandComponents.add(fileOutput.getCanonicalPath());*/
				
				commandComponents.add("--plan-file");
				commandComponents.add("result"+File.separator+trace.getTraceID()+"_"+constraint_name);
				
				//File resultFolder = new File( System.getProperty("user.dir")+File.separator+"result"+File.separator+trace.getTraceID()+"_"+constraint_name);
				/*try {
					commandComponents.add("--plan-file");
					commandComponents.add(resultFolder.getCanonicalPath());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				if(os.equals("mac")){
					commandComponents.add(FAST_DOWNWARD_DIR_MAC+FAST_DOWNWARD_PROBLEM);  // problem file
				}else 	commandComponents.add(FAST_DOWNWARD_DIR_WIN_LIN+FAST_DOWNWARD_PROBLEM);  // problem file


				//QUI SI POTREBBE INSERIRE L OPZIONE PER CAMBIARE EURISTICA
				commandComponents.add("--search");
				commandComponents.add("astar(blind())");

				//convert the arraylist to an array for process builder
				String[] commandArgs = commandComponents.toArray(new String[0]);

				//context.log("Invoking planner..."); DA RIMETTERE QUANDO HO IL CONTEXT


				ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);
				try {
					plannerManagerProcess = processBuilder.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// read std out & err in separated thread
				StreamAsyncReader errorGobbler = new StreamAsyncReader(plannerManagerProcess.getErrorStream(), "ERROR");
				StreamAsyncReader outputGobbler = new StreamAsyncReader(plannerManagerProcess.getInputStream(), "OUTPUT");
				//errorGobbler.start();
				//outputGobbler.start();

				// start progress checker (ignoring empty trace related files)
				// RIMETTERE IL PROGRESS CHECKER QUANDO PUOI
				// wait for the process to return to read the generated outputs
				try {
					plannerManagerProcess.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		
	}

	public  void generatePDDLfiles() {

		File domainFile;
		File problemFile;

		String os=OSUtils.getOs();
		if(os.equals("mac")){
			domainFile = new File(FAST_DOWNWARD_DIR_MAC+FAST_DOWNWARD_DOMAIN);
			problemFile = new File(FAST_DOWNWARD_DIR_MAC+FAST_DOWNWARD_PROBLEM);
		}else {
			domainFile = new File(FAST_DOWNWARD_DIR_WIN_LIN+FAST_DOWNWARD_DOMAIN);
			problemFile = new File(FAST_DOWNWARD_DIR_WIN_LIN+FAST_DOWNWARD_PROBLEM);
		}


		if(domainFile.exists() && !domainFile.isDirectory()) { 
			domainFile.delete();
		}
		if(problemFile.exists() && !problemFile.isDirectory()) { 
			problemFile.delete();
		}



		StringBuffer sb_domain =createPropositionalDomain();
		StringBuffer sb_problem =createPropositionalProblem();


		try {
			FileWriter fw = new FileWriter(domainFile);
			fw.write(sb_domain.toString());
			fw.close();

			fw = new FileWriter(problemFile);
			fw.write(sb_problem.toString());
			fw.close();



			//fw.flush();
			//fw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}



	
	private StringBuffer createPropositionalProblem() {
		StringBuffer PDDL_objects_buffer = new StringBuffer();	
		StringBuffer PDDL_init_buffer = new StringBuffer();
		StringBuffer PDDL_cost_buffer = new StringBuffer();
		StringBuffer PDDL_goal_buffer = new StringBuffer();
		StringBuffer PDDL_problem_buffer = new StringBuffer();
		
		PDDL_objects_buffer.append("(define (problem Align) (:domain Mining)\n");
		PDDL_objects_buffer.append("(:objects\n");	
		
		for(int l=0;l<=trace.getOriginalTraceContent_vector().size();l++) {
			PDDL_objects_buffer.append("t" + l + " - state\n");
		}

		PDDL_objects_buffer.append(constant.getPDDLAutomataAllStates_sb());
		PDDL_objects_buffer.append(")\n");	
		
		//
		// Definition of the INITIAL STATE
		//
		PDDL_init_buffer = new StringBuffer("(:init\n");
		PDDL_init_buffer.append("(currstate t0)\n");
		
		PDDL_init_buffer.append(constant.getPDDLAutomataInitialStates_sb());

		PDDL_cost_buffer.append("(= (total-cost) 0)\n");
		PDDL_init_buffer.append(PDDL_cost_buffer);

		PDDL_init_buffer.append(")\n");	
		
		//
		// Definition of the GOAL STATE
		//
		
		PDDL_goal_buffer.append("(:goal\n");
		PDDL_goal_buffer.append("(and\n");
				
		PDDL_goal_buffer.append("(currstate t" + trace.getOriginalTraceContent_vector().size() + ")\n");
		
		PDDL_goal_buffer.append(constant.getPDDLAutomataAcceptingStates_sb());

		PDDL_goal_buffer.append("))\n");

		PDDL_goal_buffer.append("(:metric minimize (total-cost))\n");	

		PDDL_problem_buffer.append(PDDL_objects_buffer);
		PDDL_problem_buffer.append(PDDL_init_buffer);
		PDDL_problem_buffer.append(PDDL_goal_buffer);	
		PDDL_problem_buffer.append(")");	

		return PDDL_problem_buffer;
	}

	private StringBuffer createPropositionalDomain() {
		
		StringBuffer PDDL_domain_buffer = new StringBuffer();
		
		PDDL_domain_buffer.append("(define (domain Mining)\n");
		PDDL_domain_buffer.append("(:requirements :typing :equality)\n");
		PDDL_domain_buffer.append("(:types state)\n\n");
		
		PDDL_domain_buffer.append("(:predicates\n");	
		PDDL_domain_buffer.append("(currstate ?s - state)\n");			
		PDDL_domain_buffer.append(")\n\n");			
		PDDL_domain_buffer.append("(:functions\n");	
		PDDL_domain_buffer.append("(total-cost)\n");			
		PDDL_domain_buffer.append(")\n\n");		
		
		for(int i=0;i<constant.getCombinationOfRelevantTransitions_vector().size();i++) {
			
			CombinationOfRelevantTransitions cot = constant.getCombinationOfRelevantTransitions_vector().elementAt(i);
			String label_of_the_cot =  cot.getLabel();
			
			
			//no token linked to the traces involved should be in a state where it's not admitted
			//the actual label
			
			StringBuffer sink_state_precondition=new StringBuffer();
			int counter_prec_bad_states=0;
			Set<String> bad_states=constant.getLabels_to_sink_states().get(label_of_the_cot);
			Iterator<String> it_bad_states=bad_states.iterator();
			while(it_bad_states.hasNext()){
				String bad_state=it_bad_states.next();
				sink_state_precondition.append("(not (currstate "+bad_state+") )");
				counter_prec_bad_states++;
			}
			
			// check if is possible to perform an action with this label that effects the automaton.
			//This check is mandatory because of the usage of minimum automata
			boolean admitted=true;
			HashSet<String> automaton_states_involved=cot.getAutomaton_source_states();
			Iterator<String> it_automaton_states_involved=automaton_states_involved.iterator();
			while(it_automaton_states_involved.hasNext()){
				String aut_state=it_automaton_states_involved.next();
				if(constant.getLabels_to_sink_states().containsEntry(label_of_the_cot, aut_state)){
					admitted=false;
					break;
				}
			}
			
			
			//
			// If a combination of transitions is related to a label (i.e., to a symbol of the alphabet) not contained in the 
			// alphabet of the trace or in the alphabet of the automata, no action will be generated for this transition.
			//	
			
			if(admitted){
				if(trace.getTraceAlphabet_vector().contains(label_of_the_cot) || constant.getAlphabetOfTheConstraints_vector().contains(label_of_the_cot)) {

					//
					// Generate an ADD action for any combination of transitions
					//
					PDDL_domain_buffer.append("(:action add" + "-" + label_of_the_cot.replaceAll(" ", "***") + "-" + "\n");
					PDDL_domain_buffer.append(":precondition ");

					if(cot.getNumberOfConditionsInThePDDLPreconditions()+counter_prec_bad_states>1) PDDL_domain_buffer.append("(and ");

					PDDL_domain_buffer.append(cot.getPDDL_preconditions());
					PDDL_domain_buffer.append(sink_state_precondition);

					if(cot.getNumberOfConditionsInThePDDLPreconditions()+counter_prec_bad_states>1) 
						PDDL_domain_buffer.append(")\n");
					else 
						PDDL_domain_buffer.append("\n");

					PDDL_domain_buffer.append(":effect (and ");
					PDDL_domain_buffer.append(cot.getPDDL_effects());
					PDDL_domain_buffer.append(" (increase (total-cost) "+constant.getAddActionCost()+")");	
					PDDL_domain_buffer.append(")\n");

					PDDL_domain_buffer.append(")\n\n");

					for(int k=0;k<trace.getOriginalTraceContent_vector().size();k++) {

						//
						// Generate a Move Sync for any combination of transitions.
						//
						if(trace.getOriginalTraceContent_vector().elementAt(k).equalsIgnoreCase(label_of_the_cot)) {

							PDDL_domain_buffer.append("(:action sync" + "-" + label_of_the_cot.replaceAll(" ", "_") + "-" + cot.getId() + "\n");
							PDDL_domain_buffer.append(":precondition (and ");
							PDDL_domain_buffer.append("(currstate t" + k + ") ");
							PDDL_domain_buffer.append(sink_state_precondition);
							PDDL_domain_buffer.append(cot.getPDDL_preconditions() + ")\n");
							PDDL_domain_buffer.append(":effect (and ");
							int j = k+1;
							PDDL_domain_buffer.append("(not (currstate t" + k + ")) " + "(currstate t" + j + ") " );
							PDDL_domain_buffer.append(cot.getPDDL_effects() + ")\n");
							PDDL_domain_buffer.append(")\n\n");
						}
					}
				}
			}
		}
		
		//
		// For any activity of the trace, generate:
		// -- a DEL action (representing a move in the log) 
		// -- a SYNC action, representing a further syncronous move, which can be performed in any state different from the ones in the preconditions of the combinations.
		//
		for(int gk=0;gk<trace.getOriginalTraceContent_vector().size();gk++) {

			int j = gk+1;
			
			//Generate a MOVE SYNC
						
			StringBuffer preconditionsSB = new StringBuffer();
						
			String act = trace.getOriginalTraceContent_vector().elementAt(gk);
			Collection<String> values =  constant.getRelevantTransitions_map().get(act);
			//System.out.print(act + " --> ");
			//System.out.println(values);
			
			//no token linked to the traces involved should be in a state where it's not admitted
			//the actual label
			
			StringBuffer sink_state_precondition=new StringBuffer();
			int counter_prec_bad_states=0;
			Set<String> bad_states=constant.getLabels_to_sink_states().get(act);
			Iterator<String> it_bad_states=bad_states.iterator();
			while(it_bad_states.hasNext()){
				String bad_state=it_bad_states.next();
				sink_state_precondition.append("(not (currstate "+bad_state+") )");
				counter_prec_bad_states++;
			}		
			Object[] values_array = values.toArray();
			
			for(int l=0;l<values_array.length;l++) {
				String transition_id = values_array[l].toString();
				RelevantTransition rt = Utilities.getRelevantTransition(transition_id,constant);
				preconditionsSB.append("(not " + rt.getPDDL_preconditions() + ") ");
			}
			preconditionsSB.append(sink_state_precondition);
			
			PDDL_domain_buffer.append("(:action sync" + "-" + act.replaceAll(" ", "_") + "-t" + gk + "t" + j + "\n");
			PDDL_domain_buffer.append(":precondition ");
			
			if(values_array.length+counter_prec_bad_states>0) PDDL_domain_buffer.append("(and ");
			
			PDDL_domain_buffer.append("(currstate t" + gk + ") ");
			
			if(values_array.length+counter_prec_bad_states>0) PDDL_domain_buffer.append(preconditionsSB + ")\n");
			else PDDL_domain_buffer.append("\n");
			
			PDDL_domain_buffer.append(":effect ");
			PDDL_domain_buffer.append("(and (not (currstate t" + gk + ")) " + "(currstate t" + j + "))" );
			PDDL_domain_buffer.append(")\n\n");
			
						
			// Generate a DEL ACTION
			
			PDDL_domain_buffer.append("(:action del" + "-" + trace.getOriginalTraceContent_vector().elementAt(gk).replaceAll(" ", "_") + "-t" + gk + "-t" + j + "\n");
			PDDL_domain_buffer.append(":precondition ");
			PDDL_domain_buffer.append("(currstate t" + gk + ")\n");
			PDDL_domain_buffer.append(":effect (and ");
			PDDL_domain_buffer.append("(not (currstate t" + gk + ")) " + "(currstate t" + j + ") " );
			PDDL_domain_buffer.append(" (increase (total-cost) "+constant.getDeleteActionCost()+")");	
			PDDL_domain_buffer.append(")\n");
		
			PDDL_domain_buffer.append(")\n\n");
		}
		
		//
		// If the planner used to synhesize the alignment IS NOT ABLE to manage disjunctive goal conditions, 
		// we need to generate PDDL actions to reach the ABSTRACT accepting state of any automaton, that are used as target states 
		// for any regular accepting state.
		//

		StringBuffer PDDL_temp_effects_sb = new StringBuffer(":effect (and ");

		//
		// Vector used to record the ID of the automata related to the abstract states.
		//
		Vector<String> automata_id_of_abstract_states_vector = new Vector<String>(); 

		if(constant.getAutomataAbstractAcceptingStates_vector().size()>0){
			for(int op=0;op<constant.getAutomataAbstractAcceptingStates_vector().size();op++) {
				String abstract_state_id = constant.getAutomataAbstractAcceptingStates_vector().elementAt(op);
				PDDL_temp_effects_sb.append("(currstate " + abstract_state_id + ") ");

				//System.out.println(state_id);
				int first_underscore = abstract_state_id.indexOf("_");
				int last_underscore = abstract_state_id.lastIndexOf("_");
				String automaton_id = abstract_state_id.substring(first_underscore+1, last_underscore);
				automata_id_of_abstract_states_vector.addElement(automaton_id);
			}


			//
			// For any combination of accepting states, we generate a PDDL action to reach all the abstract states of the automata. 
			// 
			//

			for(int jk=0;jk<constant.getCombinationOfAcceptingStates_vector().size();jk++) {

				StringBuffer PDDL_temp_effects_2_sb = new StringBuffer();

				CombinationOfAcceptingStates coas = constant.getCombinationOfAcceptingStates_vector().elementAt(jk);

				PDDL_domain_buffer.append("(:action goto" + "-abstract_states-" + coas.getId() +  "\n");
				PDDL_domain_buffer.append(":precondition (and (currstate t" + trace.getOriginalTraceContent_vector().size()+ ") ");

				for(int hgf=0;hgf<coas.getCombinationOfAcceptingStates_vector().size();hgf++) {
					String specific_accepting_state_ID_in_the_combination = coas.getCombinationOfAcceptingStates_vector().elementAt(hgf);
					PDDL_domain_buffer.append("(currstate " + specific_accepting_state_ID_in_the_combination + ") ");

					int first_underscore_2 = specific_accepting_state_ID_in_the_combination.indexOf("_");
					int last_underscore_2 = specific_accepting_state_ID_in_the_combination.lastIndexOf("_");
					String automaton_id_2 = specific_accepting_state_ID_in_the_combination.substring(first_underscore_2+1, last_underscore_2);
					if(automata_id_of_abstract_states_vector.contains(automaton_id_2)) {
						PDDL_temp_effects_2_sb.append("(not (currstate " + specific_accepting_state_ID_in_the_combination + ")) ");
					}
				}
				PDDL_domain_buffer.append(")\n" + PDDL_temp_effects_sb + PDDL_temp_effects_2_sb + ")\n)\n\n");

			}
		}



		PDDL_domain_buffer.append(")");
		
		return PDDL_domain_buffer;
	}


	public static Automaton generateAutomatonByLTLFormula(String formula) {
		List<Formula> formulaeParsed = new ArrayList<Formula>();

		try {
			formulaeParsed.add(new DefaultParser(formula).parse());
		} catch (SyntaxParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TreeFactory<ConjunctionTreeNode, ConjunctionTreeLeaf> treeFactory = DefaultTreeFactory.getInstance();
		ConjunctionFactory<? extends GroupedTreeConjunction> conjunctionFactory = GroupedTreeConjunction
				.getFactory(treeFactory);
		GroupedTreeConjunction conjunction = conjunctionFactory.instance(formulaeParsed);
		Automaton aut =conjunction.getAutomaton().op.reduce();

		return aut;
	}

	private static void st(Object x){
		System.out.println(x.toString());
	}

}
