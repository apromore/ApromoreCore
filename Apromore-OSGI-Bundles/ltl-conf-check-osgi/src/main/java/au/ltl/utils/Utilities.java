package au.ltl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;

import au.ltl.domain.CombinationOfAcceptingStates;
import au.ltl.domain.CombinationOfRelevantTransitions;
import au.ltl.domain.Constants;
import au.ltl.domain.RelevantTransition;
import au.ltl.domain.Trace;

public class Utilities {

 		//
		// Method to generate k-combinations of a set of elements in an array. 
	    // You just need to pass the array and the "k" value,  which is an integer that represents the length of the unique subsets 
	    // you want to generate out of the original array. 
		// -- ATTENTION -- This method is customized for the plan-based declarative aligner.
		// It removes any combination that contains two transitions of the same automaton
	    //
	
	public static void findCombinationsOfTransitions(Object[] arr, String label, int len, int original_k_value, int startPosition, String[] result, Constants constant) {
	    if (len == 0){
	       	       
	    	//String str = "";
	    	 
	        Vector<String> automata_ID_of_relevant_transitions_involved_in_a_combination_vector = new Vector<String>();
	        Vector<String> combination_of_relevant_transitions_vector = new Vector<String>();
	        
	        for(String relevant_transition : result)
	        {
	        	int first_underscore = relevant_transition.indexOf("_");
				int last_underscore = relevant_transition.lastIndexOf("_");
				String automaton_id = relevant_transition.substring(first_underscore+1, last_underscore);
	        	
				//
				// If a combination under construction contains two relevant transitions of the same automaton, 
				// the combination is immediately discarded. Conversely, if the combination contains ONLY relevant transitions
				// taken from different automata, a new "RelevantTransition" object
				//
				if(automata_ID_of_relevant_transitions_involved_in_a_combination_vector.contains(automaton_id)) {
					//str += transition.toString() + ", " + "(" + automata_id + ")";
					return;
				}
				else  {
					//str += transition.toString() + ", ";					
					combination_of_relevant_transitions_vector.addElement(relevant_transition);
					automata_ID_of_relevant_transitions_involved_in_a_combination_vector.addElement(automaton_id);
				}
	        }
	        
	        // System.out.print(label + " -- ");
	        // System.out.println(str);
	        
	        Vector<String> original_transitions_associated_to_the_label_vector = new Vector<String>();
	        for(int hu=0;hu<arr.length;hu++) {
	        	original_transitions_associated_to_the_label_vector.addElement(arr[hu].toString());
	        }
	        	        
	        String cotID = "ct" + constant.getCombinationOfRelevantTransitions_vector().size();
	        
	        CombinationOfRelevantTransitions cot = new CombinationOfRelevantTransitions(cotID, label, original_k_value, combination_of_relevant_transitions_vector, original_transitions_associated_to_the_label_vector, constant);
	                
	        // System.out.println(cot.getId() + " -- " + cot.getLabel() + " --> " + cot.getCombination_of_transitions_vector() + " -- " + cot.getOriginal_transitions_associated_to_the_label_vector() + " -- " + cot.getPDDL_preconditions() + " -- " + cot.getPDDL_effects());
	        	        
	        constant.getCombinationOfRelevantTransitions_vector().addElement(cot);
	        
	        return;
	    }       
	    for (int i = startPosition; i <= arr.length-len; i++){
	        result[result.length - len] = arr[i].toString();
	        findCombinationsOfTransitions(arr, label, len-1, original_k_value, i+1, result, constant);
	    }
	}
		
	
	public static void findCombinationsOfAcceptingStates(Object[] arr, int len, int startPosition, String[] result, Constants constant) {
	    
		if (len == 0){
	       	       
	    	//String str = "";
	    	 
	        Vector<String> automata_ID_of_accepting_states_involved_in_a_combination_vector = new Vector<String>();
	        Vector<String> combination_of_accepting_states_vector = new Vector<String>();
	        
	        for(String accepting_state : result)
	        {
	        	int first_underscore = accepting_state.indexOf("_");
				int last_underscore = accepting_state.lastIndexOf("_");
				String automaton_id = accepting_state.substring(first_underscore+1, last_underscore);
	        	
				//
				// If a combination under construction contains two accepting states of the same automaton, 
				// the combination is immediately discarded. Conversely, if the combination contains ONLY accepting states
				// taken from different automata, a new "CombinationofAcceptingState" object is generated. 
				//
				if(automata_ID_of_accepting_states_involved_in_a_combination_vector.contains(automaton_id)) {
					//str += transition.toString() + ", " + "(" + automata_id + ")";
					return;
				}
				else  {
					//str += transition.toString() + ", ";					
					combination_of_accepting_states_vector.addElement(accepting_state);
					automata_ID_of_accepting_states_involved_in_a_combination_vector.addElement(automaton_id);
				}
	        }
	        
	        // System.out.print(label + " -- ");
	        // System.out.println(str);
	        	        	        
	        String cosID = "cs" + constant.getCombinationOfAcceptingStates_vector().size();
	        
	        CombinationOfAcceptingStates coas = new CombinationOfAcceptingStates(cosID, combination_of_accepting_states_vector);
	                
	        //System.out.println(coas.getId() + " --> " + coas.getCombinationOfAcceptingStates_vector());
	        	        
	        constant.getCombinationOfAcceptingStates_vector().addElement(coas);
	        return;
	    }       
	    for (int i = startPosition; i <= arr.length-len; i++){
	        result[result.length - len] = arr[i].toString();
	        findCombinationsOfAcceptingStates(arr, len-1, i+1, result, constant);
	    }
	}
	
	
	
	public static StringBuffer createPropositionalDomain(Trace trace, Constants constant) {
		
		StringBuffer PDDL_domain_buffer = new StringBuffer();
		
		PDDL_domain_buffer.append("(define (domain Mining)\n");
		PDDL_domain_buffer.append("(:requirements :typing :equality)\n");
		PDDL_domain_buffer.append("(:types state)\n\n");
		
		PDDL_domain_buffer.append("(:predicates\n");	
		PDDL_domain_buffer.append("(currstate ?s - state)\n");			
		PDDL_domain_buffer.append(")\n\n");			
		
		/*if(constant.getPlannerPerspective().getCostCheckBox().isSelected()) {
			PDDL_domain_buffer.append("(:functions\n");	
			PDDL_domain_buffer.append("(total-cost)\n");			
			PDDL_domain_buffer.append(")\n\n");		
		}*/
		
		for(int i=0;i<constant.getCombinationOfRelevantTransitions_vector().size();i++) {
			
			CombinationOfRelevantTransitions cot = constant.getCombinationOfRelevantTransitions_vector().elementAt(i);
			String label_of_the_cot =  cot.getLabel();
			
			//
			// If a combination of transitions is related to a label (i.e., to a symbol of the alphabet) not contained in the 
			// alphabet of the trace or in the alphabet of the automata, no action will be generated for this transition.
			//
			if(trace.getTraceAlphabet_vector().contains(label_of_the_cot) || constant.getAlphabetOfTheConstraints_vector().contains(label_of_the_cot)) {
				
				//
				// Generate an ADD action for any combination of transitions
				//
				PDDL_domain_buffer.append("(:action add" + "-" + label_of_the_cot + "-" + cot.getId() + "\n");
				PDDL_domain_buffer.append(":precondition ");
				
				if(cot.getNumberOfConditionsInThePDDLPreconditions()>1) PDDL_domain_buffer.append("(and ");
				
				PDDL_domain_buffer.append(cot.getPDDL_preconditions());
				
				if(cot.getNumberOfConditionsInThePDDLPreconditions()>1) 
					PDDL_domain_buffer.append(")\n");
				else 
					PDDL_domain_buffer.append("\n");
				
				PDDL_domain_buffer.append(":effect (and ");
				PDDL_domain_buffer.append(cot.getPDDL_effects());
				
				/*if(constant.getPlannerPerspective().getCostCheckBox().isSelected()) {
					PDDL_domain_buffer.append(" (increase (total-cost) ");	
				
						for(int yu=0;yu<constant.getActivitiesCost_vector().size();yu++) {
							Vector<String> specificTraceCostVector = constant.getActivitiesCost_vector().elementAt(yu);					
							if(specificTraceCostVector.elementAt(0).equalsIgnoreCase(label_of_the_cot)) {
								PDDL_domain_buffer.append(specificTraceCostVector.elementAt(1) + "))\n");
								break;
						}
					}
				}
				else {PDDL_domain_buffer.append(")\n");}*/
				
				PDDL_domain_buffer.append(")\n\n");
								
				for(int k=0;k<trace.getOriginalTraceContent_vector().size();k++) {
				
					//
					// Generate a Move Sync for any combination of transitions.
					//
					if(trace.getOriginalTraceContent_vector().elementAt(k).equalsIgnoreCase(label_of_the_cot)) {
						
						PDDL_domain_buffer.append("(:action sync" + "-" + label_of_the_cot + "-" + cot.getId() + "\n");
						PDDL_domain_buffer.append(":precondition (and ");
						PDDL_domain_buffer.append("(currstate t" + k + ") ");
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

			Object[] values_array = values.toArray();
			
			for(int l=0;l<values_array.length;l++) {
				String transition_id = values_array[l].toString();
				RelevantTransition rt = Utilities.getRelevantTransition(transition_id,constant);
				preconditionsSB.append("(not " + rt.getPDDL_preconditions() + ") ");
			}
			
			PDDL_domain_buffer.append("(:action sync" + "-" + act + "-t" + gk + "t" + j + "\n");
			PDDL_domain_buffer.append(":precondition ");
			
			if(values_array.length>0) PDDL_domain_buffer.append("(and ");
			
			PDDL_domain_buffer.append("(currstate t" + gk + ") ");
			
			if(values_array.length>0) PDDL_domain_buffer.append(preconditionsSB + ")\n");
			else PDDL_domain_buffer.append("\n");
			
			PDDL_domain_buffer.append(":effect ");
			PDDL_domain_buffer.append("(and (not (currstate t" + gk + ")) " + "(currstate t" + j + "))" );
			PDDL_domain_buffer.append(")\n\n");
			
						
			// Generate a DEL ACTION
			
			PDDL_domain_buffer.append("(:action del" + "-" + trace.getOriginalTraceContent_vector().elementAt(gk) + "-t" + gk + "-t" + j + "\n");
			PDDL_domain_buffer.append(":precondition ");
			PDDL_domain_buffer.append("(currstate t" + gk + ")\n");
			PDDL_domain_buffer.append(":effect (and ");
			PDDL_domain_buffer.append("(not (currstate t" + gk + ")) " + "(currstate t" + j + ") " );
		
			/*if(constant.getPlannerPerspective().getCostCheckBox().isSelected()) {
				PDDL_domain_buffer.append(" (increase (total-cost) ");	
			
					for(int yu=0;yu<constant.getActivitiesCost_vector().size();yu++) {
						Vector<String> specificTraceCostVector = constant.getActivitiesCost_vector().elementAt(yu);					
						if(specificTraceCostVector.elementAt(0).equalsIgnoreCase(trace.getOriginalTraceContent_vector().elementAt(gk))) {
							PDDL_domain_buffer.append(specificTraceCostVector.elementAt(2) + "))\n");
							break;
					}
				}
			}
			else {PDDL_domain_buffer.append(")\n");}*/
		
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
			
		PDDL_domain_buffer.append(")");
		
		return PDDL_domain_buffer;
	}
	
	public static StringBuffer createPropositionalProblem(Trace trace, Constants constant) {

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
		
		/*
		if(constant.getPlannerPerspective().getCostCheckBox().isSelected()) {
			PDDL_cost_buffer.append("(= (total-cost) 0)\n");
			PDDL_init_buffer.append(PDDL_cost_buffer);
		}*/
		PDDL_init_buffer.append(")\n");	
		
		//
		// Definition of the GOAL STATE
		//
		
		PDDL_goal_buffer.append("(:goal\n");
		PDDL_goal_buffer.append("(and\n");
				
		PDDL_goal_buffer.append("(currstate t" + trace.getOriginalTraceContent_vector().size() + ")\n");
		
		PDDL_goal_buffer.append(constant.getPDDLAutomataAcceptingStates_sb());

		PDDL_goal_buffer.append("))\n");
		/*
		if(constant.getPlannerPerspective().getCostCheckBox().isSelected()) 
			PDDL_goal_buffer.append("(:metric minimize (total-cost))\n");	 */
		
		PDDL_problem_buffer.append(PDDL_objects_buffer);
		PDDL_problem_buffer.append(PDDL_init_buffer);
		PDDL_problem_buffer.append(PDDL_goal_buffer);	
		PDDL_problem_buffer.append(")");	

		return PDDL_problem_buffer;
	}
	
	public static RelevantTransition getRelevantTransition(String tr_id, Constants constant) {
		
		RelevantTransition rt = null;
				
		for(int l=0;l<constant.getRelevantTransitions_vector().size();l++) {
			rt = constant.getRelevantTransitions_vector().elementAt(l);
			
			if(rt.getId().equalsIgnoreCase(tr_id))
				return rt;
		}
		return rt;
	}
	
	//
	// Method used to empty the content of a folder whose name is passed as argument.
	//
	public static void emptyFolder(String folderName) {
	 	File index = new File(folderName);            	
    	String[]entries = index.list();
    	for(String s: entries){
    	    File currentFile = new File(index.getPath(),s);
    	    currentFile.delete();
    	}
	}
		
	//
	// Method used to create a new file with a specific content.
	//
	public static void createFile(String nomeFile, StringBuffer buffer) {
		 
		File file = null;
	    FileWriter fw = null;
		   
		   try {
			file = new File(nomeFile);
			file.setExecutable(true);
			
			fw = new FileWriter(file);
			fw.write(buffer.toString());
			fw.close();
			
		   //fw.flush();
		   //fw.close();
		   }
		   catch(IOException e) {
		   e.printStackTrace();
		   }
	}


	//
	// Method that returns TRUE if the string passed as input is in the UPPER CASE format.
	//
	public static boolean isUpperCase(String str){
		
		for(int i=0; i<str.length(); i++){
			char c = str.charAt(i);
			
			if(Character.isUpperCase(c))
				return true;
			}
		return false;
	}
	
	
	//
	// Method that returns the current Timestamp.
	//
	public static Timestamp getCurrentTimestamp() {
		 java.util.Date date = new java.util.Date();
		 return new Timestamp(date.getTime());
	    }
	
	//
	// Method that creates a XES file starting from a XLog passed as input.
	//	
	public static File createXESFile(XLog eventLog, File outFile) throws IOException {
		  OutputStream outStream = new FileOutputStream(outFile);
		  new XesXmlSerializer().serialize(eventLog,outStream);
		  return outFile;
	}
	
	 public static void createXLog() throws IOException {
		
		 Process pr = Runtime.getRuntime().exec("gnome-terminal -e ./run_FD_all");                 		
 	     try {
			pr.waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		 
		//---------------------------------------------------------------//
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XLog log = factory.createLog();
		//---------------------------------------------------------------//
		 
		File folder = new File("fast-downward/src/plans_found");
		File[] listOfFiles = folder.listFiles();
		
		Arrays.sort(listOfFiles);
		int traces_noised_int = 0;

		for (int i = 0; i < listOfFiles.length; i++) {
			
		  File file = listOfFiles[i];
		  
		  //---------------------------------------------------------------//
		  XTrace trace = factory.createTrace();
		  XConceptExtension.instance().assignName(trace, "id"+i);
		  //---------------------------------------------------------------//		
		  
		  
		  boolean trace_with_noise = false;
		  
		  if (file.isFile()) {
			  
			  System.out.println("FILE NAME : " + file.getName());
		      BufferedReader br = new BufferedReader(new FileReader(file));           
		           
		      try {
		               StringBuilder sb = new StringBuilder();
		               String line = br.readLine();

		               trace_with_noise = false;
		               
		               while (line != null) {

		            	   boolean event_found = false;
		            	   
		            	   
		                   System.out.println(line);
		                   
		            	   if(line.contains("(sync-")) {
		            		   line = line.replace("(sync-", "");
		            		   int index = line.lastIndexOf("-");
		            		   line = line.substring(0,index);
		            		   event_found = true;
		            	   }
		            	   
		            	   if(line.contains("(add-")) {
		            		   line = line.replace("(add-", "");
		            		   int index = line.lastIndexOf("-");
		            		   line = line.substring(0,index);	
		            		   event_found = true;
		            		   trace_with_noise = true;
		            	   }
		            	   
		            	   if(line.contains("(del-")) {
		            		   trace_with_noise = true;
		            	   }
		            	   
		     			  //System.out.println("Event " + line);
		            	   		                 
		     			  if(event_found) {
			     			  XEvent event = factory.createEvent();
					 		  XConceptExtension.instance().assignName(event, line);
	
					 		  Timestamp tm = Utilities.getCurrentTimestamp();
	
					 		  XLifecycleExtension.instance().assignTransition(event, "complete");
					 		  XTimeExtension.instance().assignTimestamp(event, tm.getTime());
	
					 		  trace.add(event);
		     			  }
				 		
		                  line = br.readLine();
		               
		               }
		               
		           } finally {
		               br.close();
		           }
		           
		           log.add(trace);
		           
		         if(trace_with_noise)
		           traces_noised_int++;
		           
		          } 
		  		
		  System.out.println("Number of traces with noise : " + traces_noised_int);
		  
		        }
		
		
		File file_for_log = new File("aligned_logs/" + Utilities.getCurrentTimestamp().getTime() + ".xes");
	 	try {
	 		Utilities.createXESFile(log,file_for_log);
	 		} 
	 	catch (IOException e) {
	 	e.printStackTrace();
	 	}	
		 		 
		 	}

}