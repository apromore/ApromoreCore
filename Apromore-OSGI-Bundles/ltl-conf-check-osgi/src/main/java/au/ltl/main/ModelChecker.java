package au.ltl.main;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import au.ConfigBean;
import au.ltl.extendedReader.XMLBrokerFactory2;
import au.qut.org.processmining.framework.util.Pair;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import org.apache.commons.io.FileUtils;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.processmining.ltl2automaton.plugins.automaton.Automaton;
import org.processmining.ltl2automaton.plugins.formula.DefaultParser;
import org.processmining.ltl2automaton.plugins.formula.Formula;
import org.processmining.ltl2automaton.plugins.formula.conjunction.ConjunctionFactory;
import org.processmining.ltl2automaton.plugins.formula.conjunction.ConjunctionTreeLeaf;
import org.processmining.ltl2automaton.plugins.formula.conjunction.ConjunctionTreeNode;
import org.processmining.ltl2automaton.plugins.formula.conjunction.DefaultTreeFactory;
import org.processmining.ltl2automaton.plugins.formula.conjunction.GroupedTreeConjunction;
import org.processmining.ltl2automaton.plugins.formula.conjunction.TreeFactory;
import org.processmining.ltl2automaton.plugins.ltl.SyntaxParserException;
import org.processmining.plugins.declare.visualizing.*;

import au.ltl.domain.Action;
import au.ltl.domain.Actions;
import au.ltl.domain.Constants;
import au.ltl.domain.Constraint;
import au.ltl.domain.MyKeyCheckerResultMap;
import au.ltl.domain.Repair;
import au.ltl.domain.Subnet;
import au.ltl.domain.Trace;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;
import au.ltl.utils.Checker;
import au.ltl.utils.DeclareTemplate;
import au.ltl.utils.LTLFormula;
import au.ltl.utils.ModelAbstractions;
import au.ltl.utils.NetReplayer;
import au.ltl.utils.UnfoldingDecomposer;

import javax.inject.Inject;
import javax.inject.Named;


public class ModelChecker {
	private PetriNet net;
	private Constants constant;
	private ModelAbstractions model;
	private InputStream XMLrulesFile;
	private LinkedList<Constraint> LTLConstraintUserList;
	private String FAST_DOWNWARD_DIR = ConfigBean.getDownwardPath();;
	private String RESULTS_DIR = FAST_DOWNWARD_DIR+File.separator+"result";

    private HashMap<String, FlowNode> mapIdNode;

	public ModelChecker(ModelAbstractions model, InputStream rulesFile, LinkedList<Constraint> lTLConstraintList, int addActionCost, int deleteActionCost) {
		this.model=model;
        indexBPMNModel();
		XMLrulesFile=rulesFile;
		net=model.getNet();
		LTLConstraintUserList=lTLConstraintList;
		this.constant = new Constants();
		constant.setAddActionCost(addActionCost);
		constant.setDeleteActionCost(deleteActionCost);
		constant.setNumberOfTraces(1);

		Iterator<Activity> it = model.getBpmnModel().getActivities().iterator();
		HashMap<String,Integer> task_numberOfTraces_map= new HashMap();
		while(it.hasNext()){
			Activity a=it.next();
			task_numberOfTraces_map.put(a.getId(), 0);			
		}
		constant.setTask_numberOfTraces_map(task_numberOfTraces_map);
		
        File resultFolder = new File(RESULTS_DIR);
		try {
			FileUtils.deleteDirectory(resultFolder);
			resultFolder.mkdirs();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private void indexBPMNModel() {
        this.mapIdNode = new HashMap<>();

        for(FlowNode node : model.getBpmnModel().getActivities())
            mapIdNode.put(node.getId(), node);

        for(FlowNode node : model.getBpmnModel().getGateways())
            mapIdNode.put(node.getId(), node);

        mapIdNode.put(model.getStart().getId(), model.getStart());
        mapIdNode.put(model.getEnd().getId(), model.getEnd());
    }

    public HashMap<String, List<RuleVisualization>> checkNet() {
		System.out.println("Check started");
		HashSet<String> labels= new HashSet<String>();
		Collection<Activity> a =model.getBpmnModel().getActivities();	
		Iterator<Activity> it= a.iterator();
		while(it.hasNext()){
			Activity t=it.next();
			labels.add(t.getName());
		}
		Vector<String> activities_repository_vector = new Vector <String>();
		Iterator<String> it_alphabet=labels.iterator();
		while(it_alphabet.hasNext()){
			activities_repository_vector.add(it_alphabet.next());
		}
		//st(activities_repository_vector);
		
		HashSet<String> commonLabels = new HashSet<String>(labels);
        HashSet<String> silent = new HashSet<String>(labels);
        for (Transition t : net.getTransitions())
            silent.add(t.getName());
        silent.removeAll(commonLabels);
		        
        UnfoldingDecomposer decomposer = new UnfoldingDecomposer(model);
		HashSet<Subnet> subNets = decomposer.getSubNets();
        
		Iterator <Subnet> it_sub=subNets.iterator();
		while(it_sub.hasNext()){
			Subnet net= it_sub.next();
			NetReplayer replayer = new NetReplayer(net,model,silent, constant);
			replayer.getTraces();
		}
		
		HashSet<Trace> trace_set= constant.getTraces();
					
		HashSet<Constraint> LTL_constraints_set =new HashSet<Constraint>(); // Set with all the constraints as Constraint class
		HashSet<String> LTL_formulas_set =new HashSet<String>(); // Set with all the constraints as strings
		
		if(LTLConstraintUserList!=null){
			Iterator<Constraint> it_c=LTLConstraintUserList.iterator();
			while(it_c.hasNext()){
				Constraint c= it_c.next();
				LTL_constraints_set.add(c);
				LTL_formulas_set.add(c.getLtlFormula());
			}
		}
		
		if(XMLrulesFile!=null){
			extractConstraintsFromXml(LTL_constraints_set,LTL_formulas_set);
		}
		
			
		// Add the label in the constraints in the activity repository vector
		Iterator <String> it_constr= LTL_formulas_set.iterator();
		while(it_constr.hasNext()){
			Automaton automaton = generateAutomatonByLTLFormula2(it_constr.next());
			Iterator <org.processmining.ltl2automaton.plugins.automaton.Transition> it_trans = automaton.transitions().iterator();
			while (it_trans.hasNext()){
				org.processmining.ltl2automaton.plugins.automaton.Transition transition= it_trans.next();
				if(transition.isPositive()){
					String label = transition.getPositiveLabel();
					if(!activities_repository_vector.contains(label)){
						activities_repository_vector.add(label);
					}
				}
			}
		}

		constant.setActivitiesRepository_vector(activities_repository_vector);
		
		Iterator<Trace> it_traces = trace_set.iterator();
		while(it_traces.hasNext()){
			Trace trace= it_traces.next();
			Iterator <Constraint> it_costraints= LTL_constraints_set.iterator();
			int i=1;
			while(it_costraints.hasNext()){
				Constraint constraint= it_costraints.next();
				HashSet<String> local_constraint_set = new HashSet<>();
				local_constraint_set.add(constraint.getLtlFormula());
				Checker checker = new Checker(trace,local_constraint_set,constraint.getConstraintName(),constant);
				checker.check();
				i++;
				
			}
			Checker checker = new Checker(trace,LTL_formulas_set,"all constraints", constant);
			checker.check();
		}
		
		
		HashMap<String,Integer> task_numberOfTraces_map= constant.getTask_numberOfTraces_map();
		it_traces = trace_set.iterator();
		while(it_traces.hasNext()){
			Trace t= it_traces.next();
			Vector<String> or_tr = t.getOriginal_transaction_id();
			for(int i =0;i<or_tr.size();i++){
				int number=task_numberOfTraces_map.get(or_tr.elementAt(i));
				number++;
				task_numberOfTraces_map.replace(or_tr.elementAt(i), number);
			}
		}
		constant.setTask_numberOfTraces_map(task_numberOfTraces_map);
		//st("");
		//st(task_numberOfTraces_map);
		//st("");
		
		
		// Create structure for Repairs list
		BufferedReader br = null;
		FileReader fr = null;
		
		HashMultimap<MyKeyCheckerResultMap,Action> checkerResultMap = HashMultimap.create();

		Iterator <Constraint> it_costraints= LTL_constraints_set.iterator();
		while(it_costraints.hasNext()){
			Constraint constraint= it_costraints.next();
			String constraint_name=constraint.getConstraintName();
			it_traces = trace_set.iterator();
			while(it_traces.hasNext()){
				Trace trace= it_traces.next();
				try {

					//br = new BufferedReader(new FileReader(FILENAME));
					fr = new FileReader(RESULTS_DIR+File.separator+trace.getTraceID()+"_"+constraint_name);
					br = new BufferedReader(fr);

					String sCurrentLine;
					Vector<String> transactionId=trace.getOriginal_transaction_id();
					Iterator<String> it_tr =transactionId.iterator();
					String actual_tr_id=it_tr.next();
					while ((sCurrentLine = br.readLine()) != null) {
						sCurrentLine=sCurrentLine.replaceAll("\\(","");
						String[] parts = sCurrentLine.split("-");
						String actionName=parts[0];
						if(actionName.equals("sync")){
							MyKeyCheckerResultMap key= new MyKeyCheckerResultMap(constraint_name,actual_tr_id);
							Action action= new Action("sync","");
							checkerResultMap.put(key, action);
							if(it_tr.hasNext()){
								actual_tr_id=it_tr.next();
							}else actual_tr_id=model.getEnd().getId();
						}else if(actionName.equals("del")){
							MyKeyCheckerResultMap key= new MyKeyCheckerResultMap(constraint_name,actual_tr_id);
							Action action= new Action("del","");
							checkerResultMap.put(key, action);
							if(it_tr.hasNext()){
								actual_tr_id=it_tr.next();
							}else actual_tr_id=model.getEnd().getId();
						}else if(actionName.equals("add")){
							String labelToAdd = parts[1].replaceAll("\\*\\*\\*"," ");
							MyKeyCheckerResultMap key= new MyKeyCheckerResultMap(constraint_name,actual_tr_id);
							Action action= new Action("add",labelToAdd);
							checkerResultMap.put(key, action);
						}
						
					}

				} catch (IOException e) {

					e.printStackTrace();

				} finally {

					try {

						if (br != null)
							br.close();

						if (fr != null)
							fr.close();

					} catch (IOException ex) {

						ex.printStackTrace();

					}

				}

			}

		}//while end
			
		it_traces = trace_set.iterator();
		while(it_traces.hasNext()){
			Trace trace= it_traces.next();
			try {

				fr = new FileReader(RESULTS_DIR+File.separator+trace.getTraceID()+"_all constraints");
				br = new BufferedReader(fr);

				String sCurrentLine;
				Vector<String> transactionId=trace.getOriginal_transaction_id();
				Iterator<String> it_tr =transactionId.iterator();
				String actual_tr_id=it_tr.next();
				while ((sCurrentLine = br.readLine()) != null) {
					sCurrentLine=sCurrentLine.replaceAll("\\(","");
					String[] parts = sCurrentLine.split("-");
					String actionName=parts[0];
					if(actionName.equals("sync")){
						MyKeyCheckerResultMap key= new MyKeyCheckerResultMap("All Constraints",actual_tr_id);
						Action action= new Action("sync","");
						checkerResultMap.put(key, action);
						if(it_tr.hasNext()){
							actual_tr_id=it_tr.next();
						}else actual_tr_id=model.getEnd().getId();
					}else if(actionName.equals("del")){
						MyKeyCheckerResultMap key= new MyKeyCheckerResultMap("All Constraints",actual_tr_id);
						Action action= new Action("del","");
						checkerResultMap.put(key, action);
						if(it_tr.hasNext()){
							actual_tr_id=it_tr.next();
						}else actual_tr_id=model.getEnd().getId();
					}else if(actionName.equals("add")){
						String labelToAdd = parts[1].replaceAll("\\*\\*\\*"," ");
						MyKeyCheckerResultMap key= new MyKeyCheckerResultMap("All Constraints",actual_tr_id);
						Action action= new Action("add",labelToAdd);
						checkerResultMap.put(key, action);
					}

				}

			} catch (IOException e) {

				e.printStackTrace();

			} finally {

				try {

					if (br != null)
						br.close();

					if (fr != null)
						fr.close();

				} catch (IOException ex) {

					ex.printStackTrace();

				}

			}

		}		
		
		LinkedList<Actions> actionsList=new LinkedList<>();
		Set<MyKeyCheckerResultMap> keySet=checkerResultMap.keySet();
		Iterator<MyKeyCheckerResultMap> it_keys=keySet.iterator();
		while(it_keys.hasNext()){
			MyKeyCheckerResultMap key=it_keys.next();
			String ruleName=key.getRuleName();
			String taskId=key.getTaskId();
			int numberOfTraces=0;
			if(!taskId.equals(model.getEnd().getId())){
				numberOfTraces= task_numberOfTraces_map.get(taskId);
			}else numberOfTraces=constant.getNumberOfTracesFixed();
			Set<Action> action_set=checkerResultMap.get(key);
			Iterator<Action> it_action_set=action_set.iterator();
			LinkedList<Action> action_list=new LinkedList<>();
			LinkedList<Repair> repair_list=new LinkedList<>();
			HashMap<String,Integer> labelToAdd_numberOfTraces_map= new HashMap();
			int deleteNumber=0;
			while(it_action_set.hasNext()){
				Action action=it_action_set.next();
				if(!ruleName.equals("All Constraints")){
					action_list.add(action);
				}else{
					action_list.add(action);
					if(action.getActionType().equals("del")){
						deleteNumber++;
					}else if(action.getActionType().equals("add")){
						String labelToAdd=action.getLabelToAdd();
						if(!labelToAdd_numberOfTraces_map.containsKey(labelToAdd)){
							labelToAdd_numberOfTraces_map.put(labelToAdd, 1);
						}else{
							int numb=labelToAdd_numberOfTraces_map.get(labelToAdd);
							labelToAdd_numberOfTraces_map.replace(labelToAdd, numb+1);
						}
					}
				}
			
			}
			
			Actions actions= new Actions(ruleName,taskId,numberOfTraces,action_list);
			if(ruleName.equals("All Constraints")){
				if(deleteNumber==numberOfTraces){
					Repair repair= new Repair ("del","");
					repair_list.add(repair);
				}
				Iterator<String>it_label_toAdd= labelToAdd_numberOfTraces_map.keySet().iterator();
				while(it_label_toAdd.hasNext()){
					String label=it_label_toAdd.next();
					int numb=labelToAdd_numberOfTraces_map.get(label);
					if(numb==numberOfTraces){
						Repair repair= new Repair ("add",label);
						repair_list.add(repair);					
					}
				}
				actions.setRepairList(repair_list);
				
			}
			actionsList.add(actions);
					
		}

//		try {
//			ObjectMapper mapper = new ObjectMapper();
//
//			FileOutputStream os = new FileOutputStream("result"+File.separator+"result.json");
//			PrintStream ps = new PrintStream(os);
//				ps.println(mapper.writeValueAsString(actionsList));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//
//		it_traces = trace_set.iterator();
//		while(it_traces.hasNext()){
//			Trace o=it_traces.next();
//			st(o.getTraceID());
//			st(o.getOriginalTraceContent_vector());
//		}

		return analyzeMisconformances(actionsList);
	}

	private HashMap<String, List<RuleVisualization>> analyzeMisconformances(LinkedList<Actions> results) {
		HashMap<String, List<RuleVisualization>> ruleMap = new HashMap<>();

		for(Actions action : results){
			if(!ruleMap.containsKey(action.getRuleName()))
				ruleMap.put(action.getRuleName(), new LinkedList<>());

			createRule(action,ruleMap);
		}

		return ruleMap;
	}

	public void createRule(Actions action, HashMap<String, List<RuleVisualization>> ruleMap) {
		// Check for operation and create legend
		int sync = 0;
		int del = 0;
		int add = 0;

		int total = action.getNumberOfTraces();
		LinkedList<String> toAdd = new LinkedList<>();

		for (Action a : action.getActionList()) {
			switch (a.getActionType()) {
				case "sync":
					sync++;
					break;
				case "del":
					del++;
					break;
				case "add":
                    add++;
                    if(!toAdd.contains(a.getLabelToAdd()))
                        toAdd.add(a.getLabelToAdd());

					break;
			}
		}

		String sentence = "";
        String sentenceAdd = "";
		String color = "green";
		boolean isAddition = false;

		if (sync == total)
			sentence = sync + "/" + total + " traces do not require any modification of this task";
		else if (del > 0){
			sentence = del + "/" + total + " traces require the deletion of this task";
			if(del == total)
				color = "grey";
		}

        if(add > 0) {
			sentenceAdd = add + "/" + total + " traces require the addition of task \"" + toAdd.toString() + "\" at this position";
			isAddition = true;
		}

		// Create rule
		RuleVisualization rule = new RuleVisualization(action.getTaskId(),sentence, color);

        if(rule.getColor().equals("grey")){
            for(ControlFlow<FlowNode> edge : model.getBpmnModel().getIncomingEdges(mapIdNode.get(rule.getElementId()))) {
                rule.addToRemove(edge.getId());
                rule.setStart(edge.getSource().getId());
                break;
            }

            for(ControlFlow<FlowNode> edge : model.getBpmnModel().getOutgoingEdges(mapIdNode.get(rule.getElementId()))) {
                rule.addToRemove(edge.getId());
                rule.setEnd(edge.getTarget().getId());
                break;
            }
        }

        if(sentence.length() > 0)
            ruleMap.get(action.getRuleName()).add(rule);

		if(isAddition){
			ControlFlow<FlowNode> inputFlow = model.getBpmnModel().getIncomingEdges(mapIdNode.get(action.getTaskId())).iterator().next();
            RuleVisualization ruleAdd = new RuleVisualization(inputFlow.getId(),sentenceAdd, "grey");
			if(total != add) {
				ruleAdd.setColor("green");
			}else {
				ruleAdd.setType("addTask");
				for (String newTask : toAdd) {
					ruleAdd.addNewTask(newTask);
					ruleAdd.addPrePost(inputFlow.getSource().getId(), inputFlow.getTarget().getId());
				}
			}
            ruleMap.get(action.getRuleName()).add(ruleAdd);
		}
	}

	private void extractConstraintsFromXml( HashSet<Constraint> lTL_constraints_set, HashSet<String> lTL_formulas_set) {
		try {

			AssignmentViewBroker broker = XMLBrokerFactory2.newAssignmentBroker(XMLrulesFile);

			AssignmentModel assmod = broker.readAssignment();

			for(ConstraintDefinition cd : assmod.getConstraintDefinitions()){

				boolean is_valid_constraint = true;  
				Vector<String> activities_not_in_the_repo_vector = new Vector<String>();

				String constraint = cd.getName() + "(";	

				int index = 0;

				for(Parameter p : cd.getParameters()){

					if(cd.getBranches(p).iterator().hasNext()){

						//String activityName = cd.getBranches(p).iterator().next().toString().toLowerCase();
						String activityName = cd.getBranches(p).iterator().next().toString();

						if(activityName.contains(" "))
							//activityName = activityName.replaceAll(" ", "");

						if(activityName.contains("/"))
							activityName = activityName.replaceAll("\\/", "");

						if(activityName.contains("("))
							activityName = activityName.replaceAll("\\(", "");

						if(activityName.contains(")"))
							activityName = activityName.replaceAll("\\)", "");

						if(activityName.contains("<"))
							activityName = activityName.replaceAll("\\<", "");

						if(activityName.contains(">"))
							activityName = activityName.replaceAll("\\>", "");

						if(activityName.contains("."))
							activityName = activityName.replaceAll("\\.", "");

						if(activityName.contains(","))
							activityName = activityName.replaceAll("\\,", "_");

						if(activityName.contains("+"))
							activityName = activityName.replaceAll("\\+", "_");

						if(activityName.contains("-"))
							activityName = activityName.replaceAll("\\-", "_");

						
						cd.getBranches(p).iterator().next();

						constraint = constraint + activityName;

						if(index<cd.getParameters().size()-1)
							constraint = constraint + ",";
						index++;
					}
				}

				constraint = constraint + ")";
				
				Constraint complete_constraint = getConstraintFromTemplate(constraint);
				
				lTL_formulas_set.add(complete_constraint.getLtlFormula() );
				lTL_constraints_set.add(complete_constraint);
							  		      

			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		
	}


	private static Constraint getConstraintFromTemplate(String temporal_constraint) {
		String[] constraint_splitted = temporal_constraint.split("\\(");

		//
		// Extract the name of the constraint (existence, response, etc.).
		//
		String constraint_name = constraint_splitted[0];
		String constraint_name_final = constraint_splitted[0];
		String ltl_formula="";

		String[] constraint_splitted_2 = constraint_splitted[1].split("\\)");

		//
		// FIRST CASE: the constraint involves two activities (e.g., response(A,B)).
		//	            		
		if(constraint_splitted_2[0].contains(",")) {

			String[] constraint_splitted_3 = constraint_splitted_2[0].split(",");	    

			//
			// Extract the name of the first activity (e.g., if the constraint is response(A,B), the first activity is "A").
			//
			String activity1 = constraint_splitted_3[0];

			//
			// Extract the name of the second activity (e.g., if the constraint is response(A,B), the second activity is "B").
			//
			String activity2 = constraint_splitted_3[1];

			constraint_name_final = constraint_name_final + "(" + activity1 + "," + activity2+")";

			//
			// Infer the LTL constraint associated to any Declare template.
			//	
			if(constraint_name.equalsIgnoreCase("choice"))
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Choice,activity1,activity2);	
			else if(constraint_name.equalsIgnoreCase("exclusive choice"))
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Exclusive_Choice,activity1,activity2);
			else if(constraint_name.equalsIgnoreCase("responded existence"))
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Responded_Existence,activity1,activity2);
			else if(constraint_name.equalsIgnoreCase("not responded existence")) 
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Not_Responded_Existence,activity1,activity2);	        	         			
			else if(constraint_name.equalsIgnoreCase("co-existence"))	        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.CoExistence,activity1,activity2);	 
			else if(constraint_name.equalsIgnoreCase("not co-existence"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Not_CoExistence,activity1,activity2);	 	        	         			
			else if(constraint_name.equalsIgnoreCase("response"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Response,activity1,activity2);	 	 	        	         			
			else if(constraint_name.equalsIgnoreCase("precedence"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Precedence,activity1,activity2);		   
			else if(constraint_name.equalsIgnoreCase("succession"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Succession,activity1,activity2);	
			else if(constraint_name.equalsIgnoreCase("chain response"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Chain_Response,activity1,activity2);		
			else if(constraint_name.equalsIgnoreCase("chain precedence"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Chain_Precedence,activity1,activity2);	
			else if(constraint_name.equalsIgnoreCase("chain succession"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Chain_Succession,activity1,activity2);
			else if(constraint_name.equalsIgnoreCase("alternate response"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Alternate_Response,activity1,activity2);
			else if(constraint_name.equalsIgnoreCase("alternate precedence"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Alternate_Precedence,activity1,activity2);	        	         			
			else if(constraint_name.equalsIgnoreCase("alternate succession"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Alternate_Succession,activity1,activity2);	  
			else if(constraint_name.equalsIgnoreCase("not response"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Not_Response,activity1,activity2);	        	         			
			else if(constraint_name.equalsIgnoreCase("not precedence"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Not_Precedence,activity1,activity2);		
			else if(constraint_name.equalsIgnoreCase("not succession"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Not_Succession,activity1,activity2);	
			else if(constraint_name.equalsIgnoreCase("not chain response"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Not_Chain_Response,activity1,activity2);	 	        	         			
			else if(constraint_name.equalsIgnoreCase("not chain precedence"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Not_Chain_Precedence,activity1,activity2);	 	  
			else if(constraint_name.equalsIgnoreCase("not chain succession"))	        	         		        	         			
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Not_Chain_Succession,activity1,activity2);	 	  


		}
		//
		// SECOND CASE: the constraint involves one activity (e.g., existence(A))
		//
		else {

			String activity = constraint_splitted_2[0];
			constraint_name_final = constraint_name_final + "(" + activity+")";
	//
				// Infer the LTL constraint associated to any Declare template.
				//
			if(constraint_name.equalsIgnoreCase("existence"))
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Existence,activity,null);
			else if(constraint_name.equalsIgnoreCase("absence"))
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Absence,activity,null);	  
			else if(constraint_name.equalsIgnoreCase("init"))
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Init,activity,null);	    
			else if(constraint_name.equalsIgnoreCase("absence2"))
				ltl_formula = LTLFormula.getFormulaByTemplate(DeclareTemplate.Absence2,activity,null);	     

		}
		
		st(constraint_name_final);
		st(ltl_formula);
		Constraint c = new Constraint(ltl_formula,constraint_name_final);
		return c;
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
	public static Automaton generateAutomatonByLTLFormula2(String formula) {
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
		Automaton aut =conjunction.getAutomaton().op.determinize();

		return aut;
	}
	private static void st(Object x){
		System.out.println(x.toString());
	}

}
