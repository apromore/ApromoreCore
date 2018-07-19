/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package ee.ut.eventstr.comparison;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.IDataNode;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import org.jbpt.pm.Activity;
import org.jbpt.pm.AlternativGateway;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.DataNode;
import org.jbpt.pm.Event;

import ee.ut.bpmn.BPMNReader;
import ee.ut.bpmn.replayer.BPMNReplayer;
import ee.ut.bpmn.replayer.Trace;
import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct.Operation;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct.Operation.Op;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct.State;
import ee.ut.eventstr.comparison.differences.Difference;
import ee.ut.eventstr.comparison.differences.Differences;
import ee.ut.eventstr.comparison.differences.ModelAbstractions;
import ee.ut.eventstr.comparison.differences.Run;
import ee.ut.eventstr.comparison.differences.Runs;
import ee.ut.org.processmining.framework.util.Pair;
import hub.top.petrinet.PetriNet;

public class DiffMMGraphicalVerbalizer {
	private Differences differences;

	private ModelAbstractions model1;
	private ModelAbstractions model2;

	private PESSemantics<Integer> pes1;
	private PESSemantics<Integer> pes2;

	private PetriNet net1;
	private PetriNet net2;

	private List<List<Operation>> opSeqs;

	private Set<Integer> unobservedEvents;
	private Set<Integer> eventsConsideredByConflictRelation;

	private HashSet<String> commonLabels;

	private Table<BitSet, BitSet, Map<Multiset<String>, State>> stateSpace;
	private Multimap<State, Operation> descendants;
	private State root;

	private Table<BitSet, BitSet, Map<Integer, int[]>> globalDiffs;

	private HashSet<String> statements = new HashSet<>();

	HashSet<String> obsLabel1;
	HashSet<String> obsLabel2;

	private BPMNReplayer replayerBPMN1;
	private BPMNReplayer replayerBPMN2;

	private BPMNReader loader1;
	private BPMNReader loader2;

	public DiffMMGraphicalVerbalizer(ModelAbstractions model1, ModelAbstractions model2, HashSet<String> commonLabels,
			HashSet<String> obsLabel1, HashSet<String> obsLabel2) {
		this.differences = new Differences();

		this.model1 = model1;
		this.model2 = model2;

		this.pes1 = model1.getPES();
		this.pes2 = model2.getPES();

		this.net1 = model1.getNet();
		this.net2 = model2.getNet();

		this.loader1 = model1.getReader();
		this.loader2 = model2.getReader();

		replayerBPMN1 = new BPMNReplayer((Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) loader1.getModel(), commonLabels,
				pes1);
		replayerBPMN2 = new BPMNReplayer((Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) loader2.getModel(), commonLabels,
				pes2);

		this.unobservedEvents = new HashSet<>();

		for (int i = 0; i < pes2.getLabels().size(); i++)
			unobservedEvents.add(i);

		this.commonLabels = new HashSet<>(commonLabels);
		this.obsLabel1 = obsLabel1;
		this.obsLabel2 = obsLabel2;

		this.eventsConsideredByConflictRelation = new HashSet<>();
		this.opSeqs = new ArrayList<>();
		this.stateSpace = HashBasedTable.create();
		this.descendants = HashMultimap.create();
		this.root = new State(new BitSet(), HashMultiset.<String> create(), HashBiMap.<Integer, Integer> create(), new BitSet());
		this.globalDiffs = HashBasedTable.create();
	}

	public void addPSP(List<Operation> opSeq) {
		opSeqs.add(opSeq);
	}

	public void verbalize() {
		for (List<Operation> opSeq : opSeqs) {
			Operation finalSt = opSeq.get(opSeq.size() - 1);
			BiMap<Integer, Integer> mappings = finalSt.nextState.mappings;
			BiMap<Integer, Integer> extendedMap = HashBiMap.<Integer, Integer> create(mappings);

			BitSet c1 = finalSt.nextState.c1;
			BitSet c2 = finalSt.nextState.c2;

			for (int i = c1.nextSetBit(0); i >= 0; i = c1.nextSetBit(i + 1)) {
				if (!extendedMap.containsKey(i) && this.commonLabels.contains(pes1.getLabel(i))) {
					boolean found = false;

					for (int j = c2.nextSetBit(0); j >= 0; j = c2.nextSetBit(j + 1))
						if (!extendedMap.containsValue(j) && pes1.getLabel(i).equals(pes2.getLabel(j))) {
							extendedMap.put(i, j);
							found = true;
							break;
						}

					if (!found)
						for (int j = 0; j < pes2.getLabels().size(); j++)
							if (!extendedMap.containsValue(j) && pes2.getLabel(j).equals(pes1.getLabel(i))) {
								extendedMap.put(i, j);
								found = true;
								break;
							}

					if (!found)
						verbalizeNotFound("model 1", pes1.getLabel(i), getContext(i, mappings, 1), i, pes1, replayerBPMN1, loader1);
				}
			}

			for (int i = c2.nextSetBit(0); i >= 0; i = c2.nextSetBit(i + 1))
				if (!extendedMap.containsValue(i) && this.commonLabels.contains(pes2.getLabel(i))) {
					boolean found = false;

					for (int j = 0; j < pes1.getLabels().size(); j++)
						if (!extendedMap.containsKey(j) && pes1.getLabel(j).equals(pes2.getLabel(i))) {
							extendedMap.put(j, i);
							found = true;
							break;
						}

					if (!found)
						verbalizeNotFound("model2", pes2.getLabel(i), getContext(i, mappings, 2), i, pes2, replayerBPMN2, loader2);
				}

			LinkedList<Entry<Integer, Integer>> list = new LinkedList<>(extendedMap.entrySet());

			for (int i = 0; i < list.size() - 1; i++) {
				Entry<Integer, Integer> entry1 = list.get(i);
				for (int j = i; j < list.size(); j++) {
					Entry<Integer, Integer> entry2 = list.get(j);
					if (i != j && this.commonLabels.contains(pes1.getLabel(entry1.getKey()))
							&& this.commonLabels.contains(pes1.getLabel(entry2.getKey()))) {
						BehaviorRelation rel1 = pes1.getBRelation(entry1.getKey(), entry2.getKey());
						BehaviorRelation rel2 = pes2.getBRelation(entry1.getValue(), entry2.getValue());

						if (!rel1.equals(rel2))
							verbalize(entry1, entry2, getContext(entry1, entry2, mappings));
					}
				}
			}
		}

		flushNonCommonTasks();
		compareCyclicBehavior();
	}

	private String verbalizeRepeated(String task, String model) {
		String statement = String.format("Task %s can be repeated in %s, but not in the other model.", task, model);
		
		return statement;
	}

	private String verbalizeNotCommon(String task, String model) {
		String statement = String.format("Task %s only occurs in %s.", task, model);
		
		return statement;
	}

	private void verbalizeNotFound(String model, String task, String context, int i, PESSemantics<Integer> pes, BPMNReplayer replayer, BPMNReader loader) {
		String statement = String.format(
				"In " + model + ", there is a state after %s where %s can occur, whereas it cannot occur in the matching state in the other model", context, task);

		Runs runs1 = null;
		Runs runs2 = null;
		
		if(model.equals("model 1"))
			runs1 = printTask(i, pes, replayer, net1, loader, statement);
		
		if(model.equals("model 2"))
			runs2 = printTask(i, pes, replayer, net2, loader, statement);
		
		Difference diff = new Difference(runs1, runs2);

		if (statement != null) {
			diff.setSentence(statement);
			differences.add(diff);
		}
		
		statements.add(statement);
	}
	
	private void flushNonCommonTasks() {
		for (String str : obsLabel1)
			if (!commonLabels.contains(str) && !str.equals("_0_") && !str.equals("_1_")){
				String statement = verbalizeNotCommon(str, "model 1");
				Runs runs1 = spotTask(str, replayerBPMN1, loader1, statement);
				Difference diff = new Difference(runs1, null);

				if (statement != null) {
					diff.setSentence(statement);
					differences.add(diff);
					statements.add(statement);
				}
			}

		for (String str : obsLabel2)
			if (!commonLabels.contains(str) && !str.equals("_0_") && !str.equals("_1_")){
				String statement = verbalizeNotCommon(str, "model 2");
				Runs runs2 = spotTask(str, replayerBPMN2, loader2, statement);
				Difference diff = new Difference(null, runs2);

				if (statement != null) {
					diff.setSentence(statement);
					differences.add(diff);
					statements.add(statement);
				}
			}
	}

	private void compareCyclicBehavior() {
		for (String s : pes1.getCyclicTasks())
			if (!pes2.getCyclicTasks().contains(s) && commonLabels.contains(s)){
				String statement = verbalizeRepeated(s, "model 1");
				Runs runs1 = spotTask(s, replayerBPMN1, loader1, statement);
				Runs runs2 = spotTask(s, replayerBPMN2, loader2, statement);
				
				Difference diff = new Difference(runs1, runs2);

				if (statement != null) {
					diff.setSentence(statement);
					differences.add(diff);
					this.statements.add(statement);
				}
			}

		for (String s : pes2.getCyclicTasks())
			if (!pes1.getCyclicTasks().contains(s) && commonLabels.contains(s)){
				String statement =  verbalizeRepeated(s, "model 2");
				Runs runs1 = spotTask(s, replayerBPMN1, loader1, statement);
				Runs runs2 = spotTask(s, replayerBPMN2, loader2, statement);
				
				Difference diff = new Difference(runs1, runs2);

				if (statement != null) {
					diff.setSentence(statement);
					differences.add(diff);
					this.statements.add(statement);
				}
			}
	}
	
	public void verboseNotCommonTasks(String taskLabel, String m1) {
		String statement = verbalizeNotCommon(taskLabel, m1);
		Runs runs1 = null;
		Runs runs2 = null;
		
		if(m1.equals("model 1"))
			runs1 = spotTask(taskLabel, replayerBPMN1, loader1, statement);
		
		if(m1.equals("model 2"))
			runs2 = spotTask(taskLabel, replayerBPMN2, loader2, statement);
		
		Difference diff = new Difference(runs1, runs2);

		if (statement != null) {
			diff.setSentence(statement);
			differences.add(diff);
		}
	}
	

	private String getContext(int i, BiMap<Integer, Integer> mappings, int mode) {
		BitSet lc1;

		if (mode == 1)
			lc1 = (BitSet) pes1.getLocalConfiguration(i).clone();
		else
			lc1 = (BitSet) pes2.getLocalConfiguration(i).clone();

		// BitSet map1 = new BitSet();
		//
		//
		// for(Entry<Integer, Integer> entry : mappings.entrySet()){
		// if(mode == 1)
		// map1.set(entry.getKey());
		// else
		// map1.set(entry.getValue());
		// }
		//
		// lc1.and(map1);

		if (mode == 1) {
			HashSet<String> filt = new HashSet<>(pes1.getConfigurationLabels(lc1));
			filt.retainAll(commonLabels);
			return filt.toString();
		}

		HashSet<String> filt = new HashSet<>(pes2.getConfigurationLabels(lc1));
		filt.retainAll(commonLabels);
		return filt.toString();
	}

	// BehaviorRelation rel1 = pes1.getBRelation(entry1.getKey(),
	// entry2.getKey());
	// BehaviorRelation rel2 = pes2.getBRelation(entry1.getValue(),
	// entry2.getValue());
	//
	// if(!rel1.equals(rel2))
	// verbalize(pes1.getLabel(entry1.getKey()), pes1.getLabel(entry2.getKey()),
	// rel1, rel2, getContext(entry1, entry2, mappings));

	private void verbalize(Entry<Integer, Integer> entry1, Entry<Integer, Integer> entry2, String context) {
		BehaviorRelation r1 = pes1.getBRelation(entry1.getKey(), entry2.getKey());
		Integer event1 = entry1.getKey();
		Integer event1a = entry2.getKey();

		if(r1.equals(BehaviorRelation.INV_CAUSALITY)){
			r1 = BehaviorRelation.CAUSALITY;
			Integer event1b = event1;
			event1 = event1a;
			event1a = event1b;
		}
		
		BehaviorRelation r2 = pes2.getBRelation(entry1.getValue(), entry2.getValue());
		Integer event2 = entry1.getValue();
		Integer event2a = entry2.getValue();
		
		if(r2.equals(BehaviorRelation.INV_CAUSALITY)){
			r2 = BehaviorRelation.CAUSALITY;
			Integer event2b = event2;
			event2 = event2a;
			event2a = event2b;
		}
		
		String statement = getSentence(pes1.getLabel(event1), pes1.getLabel(event1a), pes2.getLabel(event2), pes2.getLabel(event2a), r1, r2, context);

		Runs runs1;
		Runs runs2;

		if (r1.equals(BehaviorRelation.CAUSALITY)) {
			runs1 = printDifferenceCausality(event1, event1a, pes1, replayerBPMN1, "m1", net1, loader1, statement);
		} else if (r1.equals(BehaviorRelation.CONFLICT)) {
			runs1 = printDifferenceConflict(event1, event1a, pes1, replayerBPMN1, "m1", net1, loader1, statement);
		} else {
			runs1 = printDifferenceConcurrency(event1, event1a, pes1, replayerBPMN1, "m1", net1, loader1, statement);
		}

		if (r2.equals(BehaviorRelation.CAUSALITY)) {
			runs2 = printDifferenceCausality(event2, event2a, pes2, replayerBPMN2, "m2", net2, loader2, statement);
		} else if (r2.equals(BehaviorRelation.CONFLICT)) {
			runs2 = printDifferenceConflict(event2, event2a, pes2, replayerBPMN2, "m2", net2, loader2, statement);
		} else {
			runs2 = printDifferenceConcurrency(event2, event2a, pes2, replayerBPMN2, "m2", net2, loader2, statement);
		}

		Difference diff = new Difference(runs1, runs2);

		if (statement != null) {
			diff.setSentence(statement);
			differences.add(diff);
			statements.add(statement);
		}
	}
	
	private Runs spotTask(String label, BPMNReplayer replayerBPMN, BPMNReader loader, String sentence) {
		Runs runs = new Runs();
		
		HashMap<String, String> colorsBP = replayerBPMN.spotTask(label);
		runs.addRun(new Run(colorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>(), loader, sentence, null));
		
		return runs;
	}

	private Runs printDifferenceConflict(Integer event1, Integer event1a, PESSemantics<Integer> pes,
			BPMNReplayer replayerBPMN, String suffix, PetriNet net, BPMNReader loader, String sentence) {
		Runs runs = new Runs();

		// All configurations
		BitSet conf1 = pes.getLocalConfiguration(event1);
		BitSet conf1Minus = (BitSet) conf1.clone();
		conf1Minus.set(event1, false);

		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
		HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event1), pes.getPomset(conf1), repetitions, null);

//		HashMap<String, Integer> repetitionsPre = new HashMap<String, Integer>();
//		HashMap<String, String> colorsBPMNPre = replayerBPMN.execute("87668757645756454", pes.getPomset(conf1Minus), repetitionsPre, null);
//		HashMap<String, String> colorsBPMNFinal = getDifferenceColor(colorsBPMNPre, colorsBPMN, repetitions, repetitionsPre);

		BitSet conf1a = pes.getLocalConfiguration(event1a);
		BitSet conf1aMinus = (BitSet) conf1a.clone();
		conf1aMinus.set(event1a, false);

		HashMap<String, Integer> repetitions2 = new HashMap<String, Integer>();
		HashMap<String, String> colorsBPMN2 = replayerBPMN.execute(pes.getLabel(event1a), pes.getPomset(conf1a), repetitions2, null);
//		HashMap<String, Integer> repetitions2Pre = new HashMap<String, Integer>();
//		HashMap<String, String> colorsBPMN2Pre = replayerBPMN.execute("87668757645756454", pes.getPomset(conf1aMinus), repetitions2Pre, null);

//		HashMap<String, String> colorsBPMN2Final = getDifferenceColor(colorsBPMN2Pre, colorsBPMN2, repetitions2, repetitions2Pre);
		HashMap<String, String> newColorsBP = unifyColorsBPConflict(colorsBPMN, colorsBPMN2);

//		if(loader.equals(loader1))
//			printModels("m", "1", net, loader, null, newColorsBP, repetitions, repetitions2);
//		else
//			printModels("m", "2", net, loader, null, newColorsBP, repetitions, repetitions2);
		
		runs.addRun(new Run(newColorsBP, repetitions, repetitions2, loader, sentence, pes.getPomset(conf1, conf1a)));

		return runs;
	}
	
	private Runs printDifferenceConcurrency(Integer event1, Integer event1a, PESSemantics<Integer> pes,
			BPMNReplayer replayerBPMN, String suffix, PetriNet net, BPMNReader loader, String sentence) {
		Runs runs = new Runs();

		BitSet conf1 = pes.getLocalConfiguration(event1);
		BitSet conf1a = pes.getLocalConfiguration(event1a);
		conf1.or(conf1a);
		
		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
		HashMap<String, String> colorsBPMN = replayerBPMN.executeC(pes.getLabel(event1), pes.getLabel(event1a), pes.getPomset(conf1), repetitions, null);
		
//		HashMap<String, Integer> repetitions2 = new HashMap<String, Integer>();
//		HashMap<String, String> colorsBPMN2 = replayerBPMN.execute(pes.getLabel(event1a), pes.getPomset(conf1a), repetitions2, null);
//		HashMap<String, String> newColorsBP = unifyColorsBPConflict(colorsBPMN, colorsBPMN2);
		
		runs.addRun(new Run(colorsBPMN, repetitions, repetitions, loader, sentence, pes.getPomset(conf1, conf1a)));

		return runs;
	}

	private HashMap<String, String> unifyColorsBPConflict(HashMap<String, String> colors1,
			HashMap<String, String> colors1a) {
		HashMap<String, String> unifiedColors = new HashMap<String, String>();
		for (String key : colors1.keySet())
			if (colors1a.containsKey(key) && colors1a.get(key).equals("green") && colors1.get(key).equals("green"))
				unifiedColors.put(key, "green");
			else if (colors1.get(key).equals("red"))
				unifiedColors.put(key, "red");
			else if (!colors1a.containsKey(key))
				unifiedColors.put(key, "yellow");
			else
				unifiedColors.put(key, colors1.get(key));

		for (String key : colors1a.keySet())
			if (colors1a.get(key).equals("red"))
				unifiedColors.put(key, "red");
			else if (!colors1.containsKey(key))
				unifiedColors.put(key, "yellow");
			else if (!unifiedColors.containsKey(key))
				unifiedColors.put(key, colors1a.get(key));

		return unifiedColors;
	}
	
	public void printModels(String prefix, String suffix, PetriNet net, BPMNReader loader, HashMap<Object, String> colorsPN,
			HashMap<String, String> colorsBPMN1, HashMap<String, Integer> repetitions1, HashMap<String, Integer> repetitions2) {
		Random r = new Random();
		int rand = r.nextInt();

		try {
			PrintStream out = new PrintStream("target/tex/difference" + prefix
					+ "-" + rand + "-" + suffix + ".dot");

			if (colorsPN != null) {
				out.print(net.toDot(colorsPN));
				out.close();
			}

			out = new PrintStream("target/tex/difference" + prefix + "-" + rand
					+ "-" + suffix + "BPMN.dot");
			@SuppressWarnings("unchecked")
			String modelColor = printBPMN2DOT(colorsBPMN1, (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>)loader.getModel(), loader, repetitions1, repetitions2);
			out.print(modelColor);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String printBPMN2DOT(HashMap<String, String> colorsUnf,
			Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model,
			BPMNReader loader, HashMap<String, Integer> repetitions1,
			HashMap<String, Integer> repetitions2) {
		String result = "";

		if (repetitions2 == null)
			repetitions2 = new HashMap<String, Integer>();

		result += "digraph G {\n";
		result += "rankdir=LR \n"; 

		for (Event e : model.getEvents()) {
			if (colorsUnf.containsKey(e.getId())) {
				result += String
						.format("  n%s[shape=ellipse,label=\"%s(x %s)(x%s)\", color=\"%s\"];\n",
								e.getId().replace("-", ""), e.getName(),
								getLabel(repetitions1, e),
								getLabel(repetitions2, e),
								colorsUnf.get(e.getId()));
			} else
				result += String.format("  n%s[shape=ellipse,label=\"%s\"];\n",
						e.getId().replace("-", ""), e.getName());
		}
		result += "\n";

		for (Activity a : model.getActivities()) {
			if (colorsUnf.containsKey(a.getId()))
				result += String
						.format("  n%s[shape=box,label=\"%s(x%s)(x%s)\",color=\"%s\"];\n",
								a.getId().replace("-", ""), a.getName(),
								getLabel(repetitions1, a),
								getLabel(repetitions2, a),
								colorsUnf.get(a.getId()));
			else
				result += String.format("  n%s[shape=box,label=\"%s\"];\n", a
						.getId().replace("-", ""), a.getName());
		}
		result += "\n";

		for (Gateway g : model.getGateways(AndGateway.class)) {
			if (colorsUnf.containsKey(g.getId()))
				result += String
						.format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
								g.getId().replace("-", ""), "AND",
								getLabel(repetitions1, g),
								getLabel(repetitions2, g),
								colorsUnf.get(g.getId()));
			else
				result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
						g.getId().replace("-", ""), "AND");
		}
		for (Gateway g : model.getGateways(XorGateway.class)) {
			if (colorsUnf.containsKey(g.getId()))
				result += String
						.format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
								g.getId().replace("-", ""), "XOR",
								getLabel(repetitions1, g),
								getLabel(repetitions2, g),
								colorsUnf.get(g.getId()));
			else
				result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
						g.getId().replace("-", ""), "XOR");
		}
		for (Gateway g : model.getGateways(OrGateway.class)) {
			if (colorsUnf.containsKey(g.getId()))
				result += String
						.format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
								g.getId().replace("-", ""), "OR",
								getLabel(repetitions1, g),
								getLabel(repetitions2, g),
								colorsUnf.get(g.getId()));
			else
				result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
						g.getId().replace("-", ""), "OR");
		}
		for (Gateway g : model.getGateways(AlternativGateway.class))
			result += String.format("  n%s[shape=diamond,label=\"%s\"];\n", g
					.getId().replace("-", ""), "?");
		result += "\n";

		for (DataNode d : model.getDataNodes()) {
			result += String.format("  n%s[shape=note,label=\"%s\"];\n", d
					.getId().replace("-", ""),
					d.getName().concat(" [" + d.getState() + "]"));
		}
		result += "\n";

		for (ControlFlow<FlowNode> cf : model.getControlFlow()) {
			if (cf.getLabel() != null && cf.getLabel() != "")
				result += String.format("  n%s->n%s[label=\"%s\"];\n", cf
						.getSource().getId().replace("-", ""), cf.getTarget()
						.getId().replace("-", ""), cf.getLabel());
			else
				result += String.format("  n%s->n%s;\n", cf.getSource().getId()
						.replace("-", ""),
						cf.getTarget().getId().replace("-", ""));
		}
		result += "\n";

		for (Activity a : model.getActivities()) {
			for (IDataNode d : a.getReadDocuments()) {
				result += String.format("  n%s->n%s;\n",
						d.getId().replace("-", ""), a.getId().replace("-", ""));
			}
			for (IDataNode d : a.getWriteDocuments()) {
				result += String.format("  n%s->n%s;\n",
						a.getId().replace("-", ""), d.getId().replace("-", ""));
			}
		}
		result += "}";

		return result;
	}

	private String getLabel(HashMap<String, Integer> repetitions, FlowNode e) {
		if (!repetitions.containsKey(e.getId()))
			return "0";

		return repetitions.get(e.getId()) + "";
	}

	private HashMap<String, String> getDifferenceColor(HashMap<String, String> colorsBPMNPre,
			HashMap<String, String> colorsBPMN, HashMap<String, Integer> repetitions,
			HashMap<String, Integer> repetitionsPre) {
		HashMap<String, String> colors = new HashMap<String, String>();

		for (String key : colorsBPMN.keySet())
			if (colorsBPMN.get(key).equals("red"))
				colors.put(key, "red");
			else if (colorsBPMNPre.containsKey(key))
				colors.put(key, "green");
			else
				colors.put(key, "yellow");
		return colors;
	}
//
//	private Runs printDifferenceCausality(Integer event1, Integer event1a, PESSemantics<Integer> pes,
//			BPMNReplayer replayerBPMN, String suffix, PetriNet net, BPMNReader loader, String sentence) {
//		Runs runs = new Runs();
//
//		// All configurations
//		BitSet conf1 = pes.getLocalConfiguration(event1);
//		Trace<Integer> trace = new Trace<>();
//		trace.addAllStrongCauses(pes.getEvents(conf1));
//
//		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
//		HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event1), pes.getPomset(conf1),
//				repetitions, null);
//
//		BitSet conf1a = pes.getLocalConfiguration(event1a);
//		Trace<Integer> trace2 = new Trace<>();
//		trace2.addAllStrongCauses(pes.getEvents(conf1a));
//
//		BitSet exts = (BitSet) conf1a.clone();
//		BitSet intersects = (BitSet) conf1a.clone();
//		for (int i = conf1.nextSetBit(0); i >= 0; i = conf1.nextSetBit(i + 1))
//			exts.set(i, false);
//
//		intersects.and(conf1);
//
//		if (intersects.equals(conf1) && pes.arePossibleExtensions(conf1, exts)) {
//			HashMap<String, Integer> repetitions2 = new HashMap<String, Integer>();
//			HashMap<String, String> colorsBPMN2 = replayerBPMN.execute(pes.getLabel(event1a), pes.getPomset(conf1a),
//					repetitions2, null);
//
//			HashMap<String, String> newColorsBP = unifyColorsBP(colorsBPMN, colorsBPMN2);
//			HashMap<String, Integer> newRep1 = repetitions;
//			HashMap<String, Integer> newRep2 = repetitions2;
//
//			runs.addRun(new Run(newColorsBP, newRep1, newRep2, loader, sentence, pes.getPomset(conf1, conf1a)));
//		}
//
//		return runs;
//	}

	private HashMap<String, String> unifyColorsBP(HashMap<String, String> colors1, HashMap<String, String> colors1a) {
		HashMap<String, String> unifiedColors = new HashMap<String, String>();
		for (String key : colors1.keySet())
			if (colors1.get(key).equals("red"))
				unifiedColors.put(key, "red");
			else if (colors1a.containsKey(key))
				unifiedColors.put(key, "green");
			else
				unifiedColors.put(key, "yellow");

		for (String key : colors1a.keySet())
			if (colors1a.get(key).equals("red"))
				unifiedColors.put(key, "red");
			else if (!colors1.containsKey(key))
				unifiedColors.put(key, "yellow");

		return unifiedColors;
	}

	private Runs printDifferenceCausality(Integer event1, Integer event1a, PESSemantics<Integer> pes,
			BPMNReplayer replayerBPMN, String suffix, PetriNet net, BPMNReader loader, String sentence) {
		Runs runs = new Runs();

		// All configurations
		BitSet conf1 = pes.getLocalConfiguration(event1);
		Trace<Integer> trace = new Trace<>();
		trace.addAllStrongCauses(pes.getEvents(conf1));

		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
		HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event1), pes.getPomset(conf1),
				repetitions, null);

		BitSet conf1a = pes.getLocalConfiguration(event1a);
		Trace<Integer> trace2 = new Trace<>();
		trace2.addAllStrongCauses(pes.getEvents(conf1a));

		BitSet exts = (BitSet) conf1a.clone();
		BitSet intersects = (BitSet) conf1a.clone();
		for (int i = conf1.nextSetBit(0); i >= 0; i = conf1.nextSetBit(i + 1))
			exts.set(i, false);

		intersects.and(conf1);

		if (intersects.equals(conf1) && pes.arePossibleExtensions(conf1, exts)) {
			HashMap<String, Integer> repetitions2 = new HashMap<String, Integer>();
			HashMap<String, String> colorsBPMN2 = replayerBPMN.execute(pes.getLabel(event1a), pes.getPomset(conf1a),
					repetitions2, null);

			HashMap<String, String> newColorsBP = unifyColorsBP(colorsBPMN, colorsBPMN2);
			HashMap<String, Integer> newRep1 = repetitions;
			HashMap<String, Integer> newRep2 = repetitions2;

			runs.addRun(new Run(newColorsBP, newRep1, newRep2, loader, sentence, pes.getPomset(conf1, conf1a)));
			
//			printModels("m", "1", net, loader, null, newColorsBP, newRep1, newRep2);
		}

		return runs;
	}
	
	private Runs printTask(Integer event1, PESSemantics<Integer> pes,
			BPMNReplayer replayerBPMN, PetriNet net, BPMNReader loader, String sentence) {
		Runs runs = new Runs();

		// All configurations
		BitSet conf1 = pes.getLocalConfiguration(event1);
		Trace<Integer> trace = new Trace<>();
		trace.addAllStrongCauses(pes.getEvents(conf1));

		HashMap<String, Integer> repetitions = new HashMap<String, Integer>();
		HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event1), pes.getPomset(conf1),
				repetitions, null);

		runs.addRun(new Run(colorsBPMN, repetitions, new HashMap<String, Integer>(), loader, sentence, pes.getPomset(conf1)));

		return runs;
	}

	private String getSentence(String task1, String task2, String task3, String task4, BehaviorRelation rel1, BehaviorRelation rel2, String context) {
		String verbR1 = "";

		if (rel1.equals(BehaviorRelation.CAUSALITY))
			verbR1 = String.format("task %s occurs before %s", task1, task2);
		else if (rel1.equals(BehaviorRelation.INV_CAUSALITY))
			verbR1 = String.format("task %s occurs before %s", task2, task1);
		else if (rel1.equals(BehaviorRelation.CONFLICT))
			verbR1 = String.format("either task %s occurs or %s", task1, task2);
		else if (rel1.equals(BehaviorRelation.CONCURRENCY))
			verbR1 = String.format("tasks %s and %s can occur in parallel", task1, task2);

		String verbR2 = "";

		if (rel2.equals(BehaviorRelation.CAUSALITY))
			verbR2 = String.format("task %s occurs before %s", task3, task4);
		else if (rel2.equals(BehaviorRelation.INV_CAUSALITY))
			verbR2 = String.format("task %s occurs before %s", task4, task3);
		else if (rel2.equals(BehaviorRelation.CONFLICT))
			verbR2 = String.format("either task %s occurs or %s", task3, task4);
		else if (rel2.equals(BehaviorRelation.CONCURRENCY))
			verbR2 = String.format("tasks %s and %s can occur in parallel", task3, task4);

		String statement = String.format("In model 1, there is a state after %s where %s, whereas in the matching state in model 2, %s.",
				context, verbR1, verbR2);
		// System.out.println(statement);
		return statement;
	}

	private void verbalize(String task1, String task2, BehaviorRelation rel1, BehaviorRelation rel2, String context) {
		String verbR1 = "";

		if (rel1.equals(BehaviorRelation.CAUSALITY))
			verbR1 = String.format("task %s occurs before %s", task1, task2);
		else if (rel1.equals(BehaviorRelation.INV_CAUSALITY))
			verbR1 = String.format("task %s occurs before %s", task2, task1);
		else if (rel1.equals(BehaviorRelation.CONFLICT))
			verbR1 = String.format("either task %s occurs or %s", task1, task2);
		else if (rel1.equals(BehaviorRelation.CONCURRENCY))
			verbR1 = String.format("tasks %s and %s can occur in parallel", task1, task2);

		String verbR2 = "";

		if (rel2.equals(BehaviorRelation.CAUSALITY))
			verbR2 = String.format("task %s occurs before %s", task1, task2);
		else if (rel2.equals(BehaviorRelation.INV_CAUSALITY))
			verbR2 = String.format("task %s occurs before %s", task2, task1);
		else if (rel2.equals(BehaviorRelation.CONFLICT))
			verbR2 = String.format("either task %s occurs or %s", task1, task2);
		else if (rel2.equals(BehaviorRelation.CONCURRENCY))
			verbR2 = String.format("tasks %s and %s can occur in parallel", task1, task2);

		String statement = String.format(
				"In model 1, there is a state after %s where %s, whereas in the matching state in model 2, %s.",
				context, verbR1, verbR2);
		// System.out.println(statement);
		statements.add(statement);
	}

	private String getContext(Entry<Integer, Integer> entry1, Entry<Integer, Integer> entry2, BiMap<Integer, Integer> mappings) {
		Integer evt1 = entry1.getKey();
		Integer evt1a = entry2.getKey();

		Integer evt2 = entry1.getValue();
		Integer evt2a = entry2.getValue();

		BitSet lc1 = (BitSet) pes1.getLocalConfiguration(evt1).clone();
		lc1.and(pes1.getLocalConfiguration(evt1a));
		lc1.set(evt1, false);
		lc1.set(evt1a, false);
		
		BitSet lc2 = (BitSet) pes2.getLocalConfiguration(evt2).clone();
		lc2.and(pes2.getLocalConfiguration(evt2a));
		lc2.set(evt2, false);
		lc2.set(evt2a, false);

		BitSet map1 = new BitSet();
		BitSet map2 = new BitSet();
		for (Entry<Integer, Integer> entry : mappings.entrySet()) {
			map1.set(entry.getKey());
			map2.set(entry.getValue());
		}

		lc1.and(map1);
		lc2.and(map2);

		HashSet<String> filt = new HashSet<>(pes1.getConfigurationLabels(lc1));
		filt.retainAll(commonLabels);
		return filt.toString();
	}

	private void verbalizeAcyclicDifferences(List<Operation> opSeq, List<int[]> diffIndexesList, int index) {
		for (int[] diffIndexes : diffIndexesList) {
			Operation firstMatching = opSeq.get(diffIndexes[0]);
			Operation secondMatching = opSeq.get(diffIndexes[2]);
			Operation firstHiding = opSeq.get(diffIndexes[1]);
			Pair<Integer, Integer> firstMatchingEventPair = (Pair) firstMatching.target;
			Pair<Integer, Integer> secondMatchingEventPair = (Pair) secondMatching.target;

			BitSet context1 = (BitSet) secondMatching.nextState.c1.clone();
			context1.andNot(firstMatching.nextState.c1);
			context1.clear(secondMatchingEventPair.getFirst());

			BitSet context2 = (BitSet) secondMatching.nextState.c2.clone();
			context2.andNot(firstMatching.nextState.c2);
			context2.set(secondMatchingEventPair.getSecond(), false);

			String firstHidingLabel = firstHiding.label;

			if (firstHiding.op == Op.LHIDE) {
				Pair<Operation, Boolean> pair = findRHide(opSeq, diffIndexes, firstHidingLabel);
				Operation secondHiding = pair.getFirst();

				// Found a corresponding RHIDE
				if (pair.getSecond()) {
					context1.set(firstMatchingEventPair.getFirst());
					context2.set(firstMatchingEventPair.getSecond());

					if (!globalDiffs.contains(context1, context2)) {
						// System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
						// firstMatching.label,
						// firstMatchingEventPair.getFirst(),
						// pes1.getBRelation(firstMatchingEventPair.getFirst(),
						// (Integer)firstHiding.target),
						// firstHidingLabel, (Integer)firstHiding.target);
						// System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
						// firstMatching.label,
						// firstMatchingEventPair.getSecond(),
						// pes2.getBRelation(firstMatchingEventPair.getSecond(),
						// (Integer)secondHiding.target),
						// secondHiding.label, (Integer)secondHiding.target);
						verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(), firstMatching.label,
								(Integer) firstHiding.target, firstHidingLabel, firstMatchingEventPair.getSecond(),
								firstMatching.label, (Integer) secondHiding.target, secondHiding.label);
					}
				} else if (secondHiding != null) {
					// ========= Symmetric <<==
					if (!globalDiffs.contains(context1, context2)) {
						String statement = String.format(
								"In the log, after the occurrence of %s(%d), %s(%d) is substituted by %s(%d)\n",
								firstMatching.label, firstMatchingEventPair.getFirst(), firstHiding.label,
								(Integer) firstHiding.target, secondHiding.label, (Integer) secondHiding.target);

						statements.add(statement);
						System.out.println(statement);
					}
				} else {
					// No RHIDE found within difference context
					if (firstMatching.nextState.labels.contains(firstHidingLabel)) {
						if (!globalDiffs.contains(context1, context2)) {
							String statement = String.format(
									"In the log, after the occurrence of %s(%d), %s(%d) is duplicated, while in the model it is not\n",
									firstMatching.label, firstMatchingEventPair.getFirst(), firstHidingLabel,
									(Integer) firstHiding.target);

							statements.add(statement);
							System.out.println(statement);
						}
					} else {
						int e2 = firstMatchingEventPair.getSecond();
						BitSet dconflict = pes2.getDirectConflictSet(e2);
						boolean found = false;
						Integer e2p = null;

						for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1))
							if (!pe.equals(e2) && pes2.getBRelation(e2, pe) == BehaviorRelation.CONFLICT
									&& firstHidingLabel.equals(pes2.getLabel(pe))) {
								eventsConsideredByConflictRelation.add(pe);
								found = true;
								e2p = pe;
								break;
							}

						if (found) {
							context1.set(firstMatchingEventPair.getFirst());
							context2.set(firstMatchingEventPair.getSecond());
							context2.set(e2p);

							if (!globalDiffs.contains(context1, context2)) {
								// System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
								// firstMatching.label,
								// firstMatchingEventPair.getFirst(),
								// pes1.getBRelation(firstMatchingEventPair.getFirst(),
								// (Integer)firstHiding.target),
								// firstHidingLabel,
								// (Integer)firstHiding.target);
								// System.out.printf("RIGHT: %s(%d) %s
								// %s(%d)\n",
								// firstMatching.label,
								// firstMatchingEventPair.getSecond(),
								// pes2.getBRelation(firstMatchingEventPair.getSecond(),
								// e2p),
								// pes2.getLabel(e2p), e2p);
								verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(),
										firstMatching.label, (Integer) firstHiding.target, firstHiding.label,
										firstMatchingEventPair.getSecond(), firstMatching.label, e2p,
										pes2.getLabel(e2p));
							}
						} else {
							e2 = firstMatchingEventPair.getSecond();
							BitSet succs2 = pes2.getDirectSuccessors(e2);

							succs2.andNot(context2);

							found = false;
							e2p = null;

							for (int ev = succs2.nextSetBit(0); ev >= 0; ev = succs2.nextSetBit(ev + 1))
								if (firstHidingLabel.equals(pes2.getLabel(ev))) {
									found = true;
									e2p = ev;
									break;
								}

							if (found) {
								context1.set(secondMatchingEventPair.getFirst());
								context2.set(e2p);
								context2.set(secondMatchingEventPair.getSecond());

								if (!globalDiffs.contains(context1, context2)) {
									// System.out.printf("LEFT: %s(%d) %s
									// %s(%d)\n",
									// secondMatching.label,
									// secondMatchingEventPair.getFirst(),
									// pes1.getBRelation(secondMatchingEventPair.getFirst(),
									// (Integer)firstHiding.target),
									// firstHidingLabel,
									// (Integer)firstHiding.target);
									// System.out.printf("RIGHT: %s(%d) %s
									// %s(%d)\n",
									// secondMatching.label,
									// secondMatchingEventPair.getSecond(),
									// pes2.getBRelation(secondMatchingEventPair.getSecond(),
									// e2p),
									// pes2.getLabel(e2p), e2p);
									verbalizeBehDiffFromModelPerspective(secondMatchingEventPair.getFirst(),
											secondMatching.label, (Integer) firstHiding.target, firstHiding.label,
											secondMatchingEventPair.getSecond(), secondMatching.label, e2p,
											pes2.getLabel(e2p));
								}
							} else {

								found = false;
								e2p = null;
								BitSet directS = pes2.getDirectSuccessors(secondMatchingEventPair.getSecond());

								for (int ev = directS.nextSetBit(0); ev >= 0; ev = directS.nextSetBit(ev + 1))
									if (firstHidingLabel.equals(pes2.getLabel(ev))) {
										found = true;
										e2p = ev;
										break;
									}

								if (found) {
									context1.set(secondMatchingEventPair.getFirst());
									context2.set(e2p);
									context2.set(secondMatchingEventPair.getSecond());

									if (!globalDiffs.contains(context1, context2)) {
										// System.out.printf("LEFT: %s(%d) %s
										// %s(%d)\n",
										// secondMatching.label,
										// secondMatchingEventPair.getFirst(),
										// pes1.getBRelation(secondMatchingEventPair.getFirst(),
										// (Integer)firstHiding.target),
										// firstHidingLabel,
										// (Integer)firstHiding.target);
										// System.out.printf("RIGHT: %s(%d) %s
										// %s(%d)\n",
										// secondMatching.label,
										// secondMatchingEventPair.getSecond(),
										// pes2.getBRelation(secondMatchingEventPair.getSecond(),
										// e2p),
										// pes2.getLabel(e2p), e2p);
										verbalizeBehDiffFromModelPerspective(secondMatchingEventPair.getFirst(),
												secondMatching.label, (Integer) firstHiding.target, firstHiding.label,
												secondMatchingEventPair.getSecond(), secondMatching.label, e2p,
												pes2.getLabel(e2p));
									}
								} else {
									found = false;
									e2p = null;
									for (int i = diffIndexes[0]; i > 0; i--) {
										if (opSeq.get(i).op == Op.RHIDE) {
											Integer hiddenEvent = (Integer) opSeq.get(i).target;
											if (firstHidingLabel.equals(pes2.getLabel(hiddenEvent))) {
												found = true;
												e2p = hiddenEvent;
												break;
											}
										}
									}
									if (found) {
										context1.set(firstMatchingEventPair.getFirst());
										context2.set(firstMatchingEventPair.getSecond());
										context2.set(e2p);

										if (!globalDiffs.contains(context1, context2)) {
											// System.out.printf("LEFT: %s(%d)
											// %s %s(%d)\n",
											// firstMatching.label,
											// firstMatchingEventPair.getFirst(),
											// pes1.getBRelation(firstMatchingEventPair.getFirst(),
											// (Integer)firstHiding.target),
											// firstHidingLabel,
											// (Integer)firstHiding.target);
											// System.out.printf("RIGHT: %s(%d)
											// %s %s(%d)\n",
											// firstMatching.label,
											// firstMatchingEventPair.getSecond(),
											// pes2.getBRelation(firstMatchingEventPair.getSecond(),
											// e2p),
											// pes2.getLabel(e2p), e2p);

											verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(),
													firstMatching.label, (Integer) firstHiding.target,
													firstHiding.label, firstMatchingEventPair.getSecond(),
													firstMatching.label, e2p, pes2.getLabel(e2p));
										}
									} else {
										context1.set(firstMatchingEventPair.getFirst());

										if (!globalDiffs.contains(context1, context2)) {
											String statement = String.format(
													"In the log, %s(%d) occurs after %s(%d), while in the model it does not\n",
													firstHidingLabel, (Integer) firstHiding.target, firstMatching.label,
													firstMatchingEventPair.getFirst());

											statements.add(statement);
											// System.out.println(statement);
										}
									}
								}
							}
						}
					}
				}

			} else {
				Pair<Operation, Boolean> pair = findLHide(opSeq, diffIndexes, firstHidingLabel);
				Operation secondHiding = pair.getFirst();

				// Found an LHIDE on an event with the same label
				if (pair.getSecond()) {
					context1.set(firstMatchingEventPair.getFirst());
					context2.set(firstMatchingEventPair.getSecond());

					if (!globalDiffs.contains(context1, context2)) {
						// System.out.printf("**LEFT: %s(%d) %s %s(%d)\n",
						// firstMatching.label,
						// firstMatchingEventPair.getFirst(),
						// pes1.getBRelation(firstMatchingEventPair.getFirst(),
						// (Integer)secondHiding.target),
						// secondHiding.label, (Integer)secondHiding.target);
						// System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
						// firstMatching.label,
						// firstMatchingEventPair.getSecond(),
						// pes2.getBRelation(firstMatchingEventPair.getSecond(),
						// (Integer)firstHiding.target),
						// firstHidingLabel, (Integer)firstHiding.target);

						verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(), firstMatching.label,
								(Integer) secondHiding.target, secondHiding.label, firstMatchingEventPair.getSecond(),
								firstMatching.label, (Integer) firstHiding.target, firstHiding.label);
					}
				} else if (secondHiding != null) {
					// ========= Symmetric <<==
					if (!globalDiffs.contains(context1, context2)) {
						String statement = String.format(
								"In the log, after the occurrence of %s(%d), %s(%d) is substituted by %s(%d)\n",
								firstMatching.label, firstMatchingEventPair.getFirst(), firstHiding.label,
								(Integer) firstHiding.target, secondHiding.label, (Integer) secondHiding.target);

						statements.add(statement);
						System.out.println(statement);
					}
				} else {
					// No LHIDE found within this Difference Context
					if (firstMatching.nextState.labels.contains(firstHidingLabel)) {
						if (!globalDiffs.contains(context1, context2)) {
							String statement = String.format(
									"In the model, after the occurrence of %s(%d), %s(%d) is duplicated, while in the log it is not\n",
									firstMatching.label, firstMatchingEventPair.getFirst(), firstHidingLabel,
									(Integer) firstHiding.target);

							statements.add(statement);
							System.out.println(statement);
						}
					} else {
						Integer e1 = firstMatchingEventPair.getFirst();
						boolean found = false;
						Integer e1p = null;

						BitSet dconflict = pes1.getDirectConflictSet(e1);

						for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1))
							if (!pe.equals(e1) && pes1.getBRelation(e1, pe) == BehaviorRelation.CONFLICT
									&& firstHidingLabel.equals(pes1.getLabel(pe))) {
								eventsConsideredByConflictRelation.add(pe);
								found = true;
								e1p = pe;
								break;
							}

						if (found) {
							context1.or(dconflict);
							context1.set(e1);
							context2.set(firstMatchingEventPair.getSecond());

							if (!globalDiffs.contains(context1, context2)) {
								// System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
								// firstMatching.label,
								// firstMatchingEventPair.getFirst(),
								// pes1.getBRelation(firstMatchingEventPair.getFirst(),
								// e1p),
								// pes1.getLabel(e1p), e1p);
								// System.out.printf("RIGHT: %s(%d) %s
								// %s(%d)\n",
								// firstMatching.label,
								// firstMatchingEventPair.getSecond(),
								// pes2.getBRelation(firstMatchingEventPair.getSecond(),
								// (Integer)firstHiding.target),
								// firstHidingLabel,
								// (Integer)firstHiding.target);

								verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(),
										firstMatching.label, (Integer) e1p, pes1.getLabel(e1p),
										firstMatchingEventPair.getSecond(), firstMatching.label,
										(Integer) firstHiding.target, firstHiding.label);

							}
						} else {
							found = false;
							e1p = null;
							Pair<Integer, Integer> secondMatchingPair = (Pair) secondMatching.target;

							e1 = secondMatchingPair.getFirst();
							dconflict = pes1.getDirectConflictSet(e1);

							for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1))
								if (!pe.equals(e1) && pes1.getBRelation(e1, pe) == BehaviorRelation.CONFLICT
										&& firstHidingLabel.equals(pes1.getLabel(pe))) {
									eventsConsideredByConflictRelation.add(pe);
									found = true;
									e1p = pe;
									break;
								}
							if (found) {
								found = false;

								for (Operation succ : descendants.get(firstMatching.nextState))
									if (succ.op == Op.MATCH) {
										found = matchSecond(succ.nextState, context2, secondMatchingPair.getSecond());
										if (found)
											break;
									}

								if (found) {
									if (!globalDiffs.contains(context1, context2)) {
										String statement = String.format(
												"In the log, %s(%s) can be skipped, while in the model it cannot\n",
												translate(context2), context2);

										statements.add(statement);
										System.out.println(statement);
									}
								} else {
									context1.set(e1p);
									context1.set(secondMatchingEventPair.getFirst());
									context2.set(secondMatchingEventPair.getSecond());

									if (!globalDiffs.contains(context1, context2)) {
										// System.out.printf("LEFT: %s(%d) %s
										// %s(%d)\n",
										// pes1.getLabel(e1p), e1p,
										// pes1.getBRelation(e1p,
										// secondMatchingEventPair.getFirst()),
										// secondMatching.label,
										// secondMatchingEventPair.getFirst());
										// System.out.printf("RIGHT: %s(%d) %s
										// %s(%d)\n",
										// firstHidingLabel,
										// (Integer)firstHiding.target,
										// pes2.getBRelation((Integer)firstHiding.target,
										// secondMatchingEventPair.getSecond()),
										// secondMatching.label,
										// secondMatchingEventPair.getSecond());
										verbalizeBehDiffFromModelPerspective((Integer) e1p, pes1.getLabel(e1p),
												secondMatchingEventPair.getFirst(), secondMatching.label,
												(Integer) firstHiding.target, firstHiding.label,
												secondMatchingEventPair.getSecond(), secondMatching.label);
									}
								}
							} else {
								e1 = secondMatchingEventPair.getFirst();
								BitSet preds1 = (BitSet) pes1.getDirectPredecessors(e1).clone();

								preds1.andNot(context1);

								found = false;
								e1p = null;
								for (Integer ev = preds1.nextSetBit(0); ev >= 0; ev = preds1.nextSetBit(ev + 1))
									if (firstHidingLabel.equals(pes1.getLabel(ev))) {
										found = true;
										e1p = ev;
										break;
									}

								if (found) {
									context1.set(e1p);
									context1.set(firstMatchingEventPair.getFirst());
									context2.set(firstMatchingEventPair.getSecond());

									if (!globalDiffs.contains(context1, context2)) {
										// System.out.printf("LEFT: %s(%d) %s
										// %s(%d)\n",
										// firstMatching.label,
										// firstMatchingEventPair.getFirst(),
										// pes1.getBRelation(firstMatchingEventPair.getFirst(),
										// e1p),
										// firstHidingLabel, e1p);
										// System.out.printf("RIGHT: %s(%d) %s
										// %s(%d)\n",
										// firstMatching.label,
										// firstMatchingEventPair.getSecond(),
										// pes2.getBRelation(firstMatchingEventPair.getSecond(),
										// (Integer)firstHiding.target),
										// firstHidingLabel,
										// (Integer)firstHiding.target);
										verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(),
												firstMatching.label, (Integer) e1p, pes1.getLabel(e1p),
												firstMatchingEventPair.getSecond(), firstMatching.label,
												(Integer) firstHiding.target, firstHiding.label);
									}
								} else {

									found = false;
									e1p = null;

									BitSet succs = pes1.getDirectSuccessors(firstMatchingEventPair.getFirst());
									for (int ev = succs.nextSetBit(0); ev >= 0; ev = succs.nextSetBit(ev + 1))
										if (firstHidingLabel.equals(pes1.getLabel(ev))) {
											found = true;
											e1p = ev;
											break;
										}

									if (found) {
										context1.set(e1p);
										context1.set(secondMatchingEventPair.getFirst());
										context2.set(secondMatchingEventPair.getSecond());

										if (!globalDiffs.contains(context1, context2)) {
											// System.out.printf("LEFT: %s(%d)
											// %s %s(%d)\n",
											// firstHidingLabel, e1p,
											// pes1.getBRelation(e1p,
											// secondMatchingEventPair.getFirst()),
											// secondMatching.label,
											// secondMatchingEventPair.getFirst());
											// System.out.printf("RIGHT: %s(%d)
											// %s %s(%d)\n",
											// firstHidingLabel,
											// (Integer)firstHiding.target,
											// pes2.getBRelation((Integer)firstHiding.target,
											// secondMatchingEventPair.getSecond()),
											// secondMatching.label,
											// secondMatchingEventPair.getSecond());
											verbalizeBehDiffFromModelPerspective((Integer) e1p, pes1.getLabel(e1p),
													secondMatchingEventPair.getFirst(), secondMatching.label,
													(Integer) firstHiding.target, firstHiding.label,
													secondMatchingEventPair.getSecond(), secondMatching.label);
										}

									} else {
										context2.set((Integer) firstHiding.target);
										if (!globalDiffs.contains(context1, context2)) {
											String statement = String.format(
													"In the model, %s(%d) occurs after %s(%d), while in the log it does not\n",
													firstHidingLabel, (Integer) firstHiding.target, firstMatching.label,
													firstMatchingEventPair.getSecond());

											statements.add(statement);
											System.out.println(statement);
										}
									}
								}
							}
						}
					}
				}
			}

			// System.out.printf("Context '%s, %s'\n", context1, context2);
			String statement = String.format("\tFirst match [%s: (%d),(%d)], Second match [%s: (%d),(%d)]\n",
					firstMatching.label, firstMatchingEventPair.getFirst(), firstMatchingEventPair.getSecond(),
					secondMatching.label, secondMatchingEventPair.getFirst(), secondMatchingEventPair.getSecond());

			statements.add(statement);
			// System.out.println(statements);

			Map<Integer, int[]> diffs = globalDiffs.get(context1, context2);
			if (diffs == null)
				globalDiffs.put(context1, context2, diffs = new HashMap<>());
			if (!diffs.containsKey(index))
				diffs.put(index, diffIndexes);
		}
	}

	// private void verbalizeBehDiffFromLogPerspective(Integer e1,
	// String e1l, Integer e1p, String e1pl,
	// Integer e2, String e2l, Integer e2p, String e2pl) {
	// System.out.printf("In the log, %s(%d) %s %s(%d), while in the model
	// %s(%d) %s %s(%d)\n",
	// e1l, e1, verbalizeBRel(pes1.getBRelation(e1, e1p)), e1pl, e1p,
	// e2l, e2, verbalizeBRel(pes2.getBRelation(e2, e2p)), e2pl, e2p);
	// }

	private void verbalizeBehDiffFromModelPerspective(Integer e1, String e1l, Integer e1p, String e1pl, Integer e2,
			String e2l, Integer e2p, String e2pl) {
		String statement = String.format("In the model, %s(%d) %s %s(%d), while in the event log %s(%d) %s %s(%d)\n",
				e2l, e2, verbalizeBRel(pes2.getBRelation(e2, e2p)), e2pl, e2p, e1l, e1,
				verbalizeBRel(pes1.getBRelation(e1, e1p)), e1pl, e1p);

		statements.add(statement);
		System.out.println(statement);
	}

	private String verbalizeBRel(BehaviorRelation bRelation) {
		switch (bRelation) {
		case CAUSALITY:
			return "occurs before";
		case INV_CAUSALITY:
			return "occurs after";
		case CONCURRENCY:
			return "occurs concurrently to";
		case CONFLICT:
			return "cannot occur in the same run with";
		default:
			break;
		}
		return null;
	}

	private boolean matchSecond(State curr, BitSet context2, Integer ev2) {
		boolean result = false;
		for (Operation op : descendants.get(curr)) {
			switch (op.op) {
			case MATCH:
				// case MATCHNSHIFT:
				Pair<Integer, Integer> pair = (Pair) op.target;
				if (context2.get(pair.getSecond()))
					result = matchSecond(op.nextState, context2, ev2);
				else if (pair.getSecond().equals(ev2))
					return true;
				break;
			case RHIDE:
				// case RHIDENSHIFT:
				if (context2.get(((Pair<Integer, Integer>) op.target).getSecond()))
					result = matchSecond(op.nextState, context2, ev2);
				break;
			default:
				break;
			}
		}
		return result;
	}

	private Pair<Operation, Boolean> findLHide(List<Operation> opSeq, int[] diffIndexes, String firstHidingLabel) {
		Operation firstRHiding = null;
		for (int i = diffIndexes[1] + 1; i < diffIndexes[2]; i++) {
			Operation secondHidingOperation = opSeq.get(i);
			if (secondHidingOperation.op == Op.LHIDE) {
				if (firstHidingLabel.equals(secondHidingOperation.label)) {
					// System.out.println("Found a matching for hidden event: "
					// + secondHidingOperation.target);
					return new Pair<>(secondHidingOperation, true);
				} else if (firstRHiding == null)
					firstRHiding = secondHidingOperation;
			}
		}
		return new Pair<>(firstRHiding, false);
	}

	private Pair<Operation, Boolean> findRHide(List<Operation> opSeq, int[] diffIndexes, String firstHidingLabel) {
		Operation firstRHiding = null;
		for (int i = diffIndexes[1] + 1; i < diffIndexes[2]; i++) {
			Operation secondHidingOperation = opSeq.get(i);
			if (secondHidingOperation.op == Op.RHIDE) {
				if (firstHidingLabel.equals(secondHidingOperation.label)) {
					// System.out.println("Found a matching for hidden event: "
					// + secondHidingOperation.target);
					return new Pair<>(secondHidingOperation, true);
				} else if (firstRHiding == null)
					firstRHiding = secondHidingOperation;
			}
		}
		return new Pair<>(firstRHiding, false);
	}

	private List<int[]> getADiffContexts(List<Operation> opSeq) {
		List<int[]> differences = new ArrayList<>();
		int[] diffIndexes = null;
		boolean visibleEventHasBeenHidden = false;

		State pred = root;

		for (int i = 0; i < opSeq.size(); i++) {
			Operation curr = opSeq.get(i);

			State state = curr.nextState;
			Map<Multiset<String>, State> map = stateSpace.get(state.c1, state.c2);
			if (map == null)
				stateSpace.put(state.c1, state.c2, map = new HashMap<>());
			if (map.containsKey(state.labels)) {
				state = map.get(state.labels);
				curr.nextState = state;
			} else
				map.put(state.labels, state);

			boolean found = false;
			for (Operation desc : descendants.get(pred))
				if (desc.op == curr.op) {
					if (curr.op == Op.MATCH) {
						Pair<Integer, Integer> pair1 = (Pair) curr.target;
						Pair<Integer, Integer> pair2 = (Pair) desc.target;
						if (pair1.equals(pair2)) {
							found = true;
							break;
						}
					} else {
						Integer ev1 = (Integer) curr.target;
						Integer ev2 = (Integer) desc.target;
						if (ev1.equals(ev2)) {
							found = true;
							break;
						}
					}
				}
			if (!found)
				descendants.put(pred, curr);
			pred = state;

			if (curr.op == Op.MATCH)
				unobservedEvents.remove((Integer) ((Pair) curr.target).getSecond());
			else // if (curr.op != Op.LHIDE)
				unobservedEvents.remove((Integer) curr.target);

			if (diffIndexes == null) {
				if (curr.op == Op.LHIDE) {
					// System.out.println("Found earliest discrepancy (LHIDE): "
					// + curr);
					diffIndexes = new int[3];
					diffIndexes[0] = i - 1;
					diffIndexes[1] = i;
					visibleEventHasBeenHidden = true;
				} else if (curr.op == Op.RHIDE) {
					Integer hiddenEvent = (Integer) curr.target;
					// System.out.println("Found earliest discrepancy (RHIDE): "
					// + curr);
					diffIndexes = new int[3];
					diffIndexes[0] = i - 1;

					visibleEventHasBeenHidden = true; // !pes2.getInvisibleEvents().contains(hiddenEvent);
					if (visibleEventHasBeenHidden)
						diffIndexes[1] = i;
				}
			} else {
				if (curr.op == Op.MATCH) {
					if (visibleEventHasBeenHidden) {
						// System.out.println("==> Context: " +
						// opSeq.subList(diffIndexes[0], i+1));

						diffIndexes[2] = i;
						differences.add(diffIndexes);
					} else {
						// System.out.println("==> Context discarded: No visible
						// event has been hidden");
					}
					diffIndexes = null;
				} else {
					if (!visibleEventHasBeenHidden) {
						if (curr.op == Op.LHIDE) {
							diffIndexes[1] = i;
							visibleEventHasBeenHidden = true;
						} else {
							visibleEventHasBeenHidden = true; // !pes2.getInvisibleEvents().contains((Integer)curr.target);
							if (visibleEventHasBeenHidden)
								diffIndexes[1] = i;
						}
					}
				}
			}
		}
		return differences;
	}

	private void processUnobservedEvents() {

		System.out.println("Unobserved: " + unobservedEvents);

		// Map<Multiset<Integer>, Multiset<Integer>> footprints =
		// pes2.getFootprints();
		Set<Integer> toRemove = new HashSet<>();
		// for (Multiset<Integer> footprint: footprints.keySet()) {
		// if (unobservedEvents.containsAll(footprint)) {
		// BitSet cycle = footprints.get(footprint);
		// System.out.printf("In the model, the interval %s is repeated multiple
		// times, while in the log it is not",
		// translate(cycle));
		// toRemove.addAll(footprints.get(footprint));
		// }
		// }

		unobservedEvents.removeAll(toRemove);

		Set<Integer> primeUnobservedEvents = new HashSet<>();

		for (Integer ev : unobservedEvents) {
			boolean found = false;
			BitSet predecessors = pes2.getDirectPredecessors(ev);
			for (int pred = predecessors.nextSetBit(0); pred >= 0; pred = predecessors.nextSetBit(pred + 1))
				if (unobservedEvents.contains(pred)) {
					found = true;
					break;
				}
			if (!found)
				primeUnobservedEvents.add(ev);
		}

		// We discard all the events that, in spite of not being explicitly
		// represented in the PSPs, have already been considered for
		// verbalization
		primeUnobservedEvents.removeAll(eventsConsideredByConflictRelation);

		for (Integer ev : primeUnobservedEvents) {
			// if (pes2.getInvisibleEvents().contains(ev)) {
			// BitSet _causes = pes2.getLocalConfiguration(ev);
			// _causes.clear(ev);
			// BitSet causes = new BitSet();
			// for (int event = _causes.nextSetBit(0); event >=0; event =
			// _causes.nextSetBit(event + 1))
			// causes.set(event);
			// BitSet pe2 = pes2.getPossibleExtensions(causes);
			//
			// for (int event = pe2.nextSetBit(0); event >= 0; event =
			// pe2.nextSetBit(event+1))
			// if (!pes2.getInvisibleEvents().contains(event) &&
			// pes2.getBRelation(ev, event) == BehaviorRelation.CONFLICT)
			// System.out.printf(" In the model, '%s' can be skipped, while in
			// the event log it cannot\n", pes2.getLabel(event));
			// } else
			{
				System.out.printf("    Task '%s' appears in PES2 and not in PES1\n", pes2.getLabel(ev));
			}
		}
	}

	private Object translate(BitSet bs) {
		Set<String> set = new HashSet<>();

		for (int ev = bs.nextSetBit(0); ev >= 0; ev = bs.nextSetBit(ev + 1))
			set.add(pes2.getLabel(ev));
		return set;
	}

	public Set<String> getStatements() {
		return statements;
	}

	public List<List<Operation>> getOpSeqs() {
		return this.opSeqs;
	}

	public Differences getDifferences() {
		return differences;
	}
}
