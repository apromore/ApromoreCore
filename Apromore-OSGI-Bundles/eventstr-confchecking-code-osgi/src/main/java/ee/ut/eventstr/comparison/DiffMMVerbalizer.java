package ee.ut.eventstr.comparison;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct.Operation;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct.Operation.Op;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct.State;
import ee.ut.org.processmining.framework.util.Pair;

public class DiffMMVerbalizer <T>{
	private PESSemantics<T> pes1;
	private PESSemantics<T> pes2;
	
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
	
	public DiffMMVerbalizer(PESSemantics<T> pes1, PESSemantics<T> pes2, HashSet<String> commonLabels, HashSet<String> obsLabel1, HashSet<String> obsLabel2) {
		this.pes1 = pes1;
		this.pes2 = pes2;
		this.unobservedEvents = new HashSet<>();
		
		for(int i = 0; i < pes2.getLabels().size(); i++)
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
		for (List<Operation> opSeq: opSeqs){
			Operation finalSt = opSeq.get(opSeq.size()-1);
			BiMap<Integer, Integer> mappings = finalSt.nextState.mappings;
			BiMap<Integer, Integer> extendedMap = HashBiMap. <Integer, Integer> create(mappings);
			
			BitSet c1 = finalSt.nextState.c1;
			BitSet c2 = finalSt.nextState.c2;
			
			for (int i = c1.nextSetBit(0); i >= 0; i = c1.nextSetBit(i+1)) {
			     if(!extendedMap.containsKey(i) && this.commonLabels.contains(pes1.getLabel(i))){
			    	 boolean found = false;
			    	 
			    	 for (int j = c2.nextSetBit(0); j >= 0; j = c2.nextSetBit(j+1)) 
			    		 if(!extendedMap.containsValue(j) && pes1.getLabel(i).equals(pes2.getLabel(j))){
			    			 extendedMap.put(i, j);
			    			 found = true;
			    			 break;
			    		 }
			    	 
			    	 if(!found)
			    		 for(int j = 0; j < pes2.getLabels().size(); j++)
			    			 if(!extendedMap.containsValue(j) && pes2.getLabel(j).equals(pes1.getLabel(i))){
			    				 extendedMap.put(i, j);
			    				 found = true;
			    				 break;
			    			 }
			    	 
			    	 if(!found)
			    		 verbalizeNotFound(pes1.getLabel(i), getContext(i, mappings, 1));
			     }
			}
			
			for (int i = c2.nextSetBit(0); i >= 0; i = c2.nextSetBit(i+1)) 
				if(!extendedMap.containsValue(i) && this.commonLabels.contains(pes2.getLabel(i))){
			    	 boolean found = false;
			    	 
			    	 for(int j = 0; j < pes1.getLabels().size(); j++)
			    		 if(!extendedMap.containsKey(j) && pes1.getLabel(j).equals(pes2.getLabel(i))){
			    			extendedMap.put(j, i);
			    		 	found = true;
			    		 	break;
			    		 }
			    	 
			    	 if(!found)
			    		 verbalizeNotFound(pes2.getLabel(i), getContext(i, mappings, 2));
				}
			
			LinkedList<Entry<Integer, Integer>> list = new LinkedList<>(extendedMap.entrySet());
			
			for(int i = 0; i < list.size() - 1; i++){
				Entry<Integer, Integer> entry1 = list.get(i);
				for(int j = i; j < list.size(); j++){
					Entry<Integer, Integer> entry2 = list.get(j);
					if(i != j && this.commonLabels.contains(pes1.getLabel(entry1.getKey()))&& this.commonLabels.contains(pes1.getLabel(entry2.getKey()))){
						BehaviorRelation rel1 = pes1.getBRelation(entry1.getKey(), entry2.getKey());
						BehaviorRelation rel2 = pes2.getBRelation(entry1.getValue(), entry2.getValue());
						
						if(!rel1.equals(rel2))
							 verbalize(pes1.getLabel(entry1.getKey()), pes1.getLabel(entry2.getKey()), rel1, rel2, getContext(entry1, entry2, mappings));
					}
				}
			}
		}
		
		flushNonCommonTasks();
		compareCyclicBehavior();
	}

	private void compareCyclicBehavior() {
		for(String s : pes1.getCyclicTasks())
			if(!pes2.getCyclicTasks().contains(s) && commonLabels.contains(s))
				verbalizeRepeated(s ,"model 1");
		
		for(String s : pes2.getCyclicTasks())
			if(!pes1.getCyclicTasks().contains(s) && commonLabels.contains(s))
				verbalizeRepeated(s ,"model 2");
	}

	private void verbalizeRepeated(String task, String model) {
		String statement = String.format("Task %s can be repeated in %s, but not in the other model.", task, model);
//		System.out.println(statement);
		statements.add(statement);
	}

	private void flushNonCommonTasks() {
		for(String str : obsLabel1)
			if(!commonLabels.contains(str) && !str.equals("_0_") && !str.equals("_1_"))
				verbalizeNotCommon(str, "model 1");
		
		for(String str : obsLabel2)
			if(!commonLabels.contains(str) && !str.equals("_0_") && !str.equals("_1_"))
				verbalizeNotCommon(str, "model 2");
	}

	private void verbalizeNotCommon(String task, String model) {
		String statement = String.format("Task %s only occurs in %s.", task, model);
//		System.out.println(statement);
		statements.add(statement);
	}

	private void verbalizeNotFound(String task1, String context) {
		String statement = String.format("In model 1, there is a state after %s where %s can occur, whereas it cannot occur in the matching state in model 2", context, task1);
//		System.out.println(statement);
		statements.add(statement);
	}

	private String getContext(int i, BiMap<Integer, Integer> mappings, int mode) {
		BitSet lc1;
		
		if(mode == 1)
			lc1 = (BitSet) pes1.getLocalConfiguration(i).clone();
		else
			lc1 = (BitSet) pes2.getLocalConfiguration(i).clone();
		
//		BitSet map1 = new BitSet();
//		
//		
//		for(Entry<Integer, Integer> entry : mappings.entrySet()){
//			if(mode == 1)
//				map1.set(entry.getKey());
//			else
//				map1.set(entry.getValue());
//		}
//		
//		lc1.and(map1);
		
		if(mode == 1){
			HashSet<String> filt = new HashSet<>(pes1.getConfigurationLabels(lc1));
			filt.retainAll(commonLabels);
			return filt.toString();
		}
		
		HashSet<String> filt = new HashSet<>(pes2.getConfigurationLabels(lc1));
		filt.retainAll(commonLabels);
		return filt.toString();
	}

	private void verbalize(String task1, String task2,BehaviorRelation rel1, BehaviorRelation rel2, String context) {
		String verbR1 = "";
		
		if(rel1.equals(BehaviorRelation.CAUSALITY))
			verbR1 = String.format("task %s occurs before %s",task1, task2);
		else if(rel1.equals(BehaviorRelation.INV_CAUSALITY))
			verbR1 = String.format("task %s occurs before %s",task2, task1);
		else if(rel1.equals(BehaviorRelation.CONFLICT))
			verbR1 = String.format("either task %s occurs or %s",task1, task2);
		else if(rel1.equals(BehaviorRelation.CONCURRENCY))
			verbR1 = String.format("tasks %s and %s can occur in parallel",task1, task2);
		
		String verbR2 = "";
		
		if(rel2.equals(BehaviorRelation.CAUSALITY))
			verbR2 = String.format("task %s occurs before %s",task1, task2);
		else if(rel2.equals(BehaviorRelation.INV_CAUSALITY))
			verbR2 = String.format("task %s occurs before %s",task2, task1);
		else if(rel2.equals(BehaviorRelation.CONFLICT))
			verbR2 = String.format("either task %s occurs or %s",task1, task2);
		else if(rel2.equals(BehaviorRelation.CONCURRENCY))
			verbR2 = String.format("tasks %s and %s can occur in parallel",task1, task2);
		
		
		String statement = String.format("In model 1, there is a state after %s where %s, whereas in the matching state in model 2, %s.", context, verbR1, verbR2);
//		System.out.println(statement);
		statements.add(statement);
	}

	private String getContext(Entry<Integer, Integer> entry1, Entry<Integer, Integer> entry2, BiMap<Integer, Integer> mappings) {
		Integer evt1 =  entry1.getKey();
		Integer evt1a = entry2.getKey();
		
		Integer evt2 = entry1.getValue();
		Integer evt2a = entry2.getValue();
		
		BitSet lc1 = (BitSet) pes1.getLocalConfiguration(evt1).clone();
		lc1.and(pes1.getLocalConfiguration(evt1a));
		
		BitSet lc2 = (BitSet) pes2.getLocalConfiguration(evt2).clone();
		lc2.and(pes2.getLocalConfiguration(evt2a));
		
		BitSet map1 = new BitSet();
		BitSet map2 = new BitSet();
		for(Entry<Integer, Integer> entry : mappings.entrySet()){
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
		for (int[] diffIndexes: diffIndexesList) {
			Operation firstMatching = opSeq.get(diffIndexes[0]);
			Operation secondMatching = opSeq.get(diffIndexes[2]);
			Operation firstHiding = opSeq.get(diffIndexes[1]);
			Pair<Integer, Integer> firstMatchingEventPair = (Pair)firstMatching.target;
			Pair<Integer, Integer> secondMatchingEventPair = (Pair)secondMatching.target;

			BitSet context1 = (BitSet)secondMatching.nextState.c1.clone();
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
//						System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//								firstMatching.label, firstMatchingEventPair.getFirst(),
//								pes1.getBRelation(firstMatchingEventPair.getFirst(), (Integer)firstHiding.target),
//								firstHidingLabel, (Integer)firstHiding.target);
//						System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//								firstMatching.label, firstMatchingEventPair.getSecond(),
//								pes2.getBRelation(firstMatchingEventPair.getSecond(), (Integer)secondHiding.target),
//								secondHiding.label, (Integer)secondHiding.target);
						verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(), firstMatching.label, (Integer)firstHiding.target, firstHidingLabel,
								firstMatchingEventPair.getSecond(), firstMatching.label, (Integer)secondHiding.target, secondHiding.label);
					}
				} else if (secondHiding != null) {
					// ========= Symmetric  <<==
					if (!globalDiffs.contains(context1, context2)) {
						String statement = String.format("In the log, after the occurrence of %s(%d), %s(%d) is substituted by %s(%d)\n",
								firstMatching.label, firstMatchingEventPair.getFirst(),
								firstHiding.label, (Integer)firstHiding.target,
								secondHiding.label, (Integer)secondHiding.target);
						
						statements.add(statement);
						System.out.println(statement);
					}
				} else {
					// No RHIDE found within difference context
					if (firstMatching.nextState.labels.contains(firstHidingLabel)) {
						if (!globalDiffs.contains(context1, context2)) {
							String statement = String.format("In the log, after the occurrence of %s(%d), %s(%d) is duplicated, while in the model it is not\n",
									firstMatching.label, firstMatchingEventPair.getFirst(),
									firstHidingLabel, (Integer)firstHiding.target);
							
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
//								System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//										firstMatching.label, firstMatchingEventPair.getFirst(),
//										pes1.getBRelation(firstMatchingEventPair.getFirst(), (Integer)firstHiding.target),
//										firstHidingLabel, (Integer)firstHiding.target);
//								System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//										firstMatching.label, firstMatchingEventPair.getSecond(),
//										pes2.getBRelation(firstMatchingEventPair.getSecond(), e2p),
//										pes2.getLabel(e2p), e2p);
								verbalizeBehDiffFromModelPerspective(
										firstMatchingEventPair.getFirst(), firstMatching.label, (Integer)firstHiding.target, firstHiding.label, 
										firstMatchingEventPair.getSecond(), firstMatching.label, e2p, pes2.getLabel(e2p));
							}							
						} else {
							e2 = firstMatchingEventPair.getSecond();
							BitSet succs2 = pes2.getDirectSuccessors(e2);

							succs2.andNot(context2);
							
							found = false;
							e2p = null;
							
							for (int ev = succs2.nextSetBit(0); ev >= 0; ev = succs2.nextSetBit(ev+1))
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
//									System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//											secondMatching.label, secondMatchingEventPair.getFirst(),
//											pes1.getBRelation(secondMatchingEventPair.getFirst(), (Integer)firstHiding.target),
//											firstHidingLabel, (Integer)firstHiding.target);
//									System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//											secondMatching.label, secondMatchingEventPair.getSecond(),
//											pes2.getBRelation(secondMatchingEventPair.getSecond(), e2p),
//											pes2.getLabel(e2p), e2p);
									verbalizeBehDiffFromModelPerspective(
											secondMatchingEventPair.getFirst(), secondMatching.label, (Integer)firstHiding.target, firstHiding.label, 
											secondMatchingEventPair.getSecond(), secondMatching.label, e2p, pes2.getLabel(e2p));
								}
							} else {
								
								found = false;
								e2p = null;
								BitSet directS = pes2.getDirectSuccessors(secondMatchingEventPair.getSecond());
								
								for (int ev = directS.nextSetBit(0); ev >= 0; ev = directS.nextSetBit(ev+1)) 
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
//										System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//												secondMatching.label, secondMatchingEventPair.getFirst(),
//												pes1.getBRelation(secondMatchingEventPair.getFirst(), (Integer)firstHiding.target),
//												firstHidingLabel, (Integer)firstHiding.target);
//										System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//												secondMatching.label, secondMatchingEventPair.getSecond(),
//												pes2.getBRelation(secondMatchingEventPair.getSecond(), e2p),
//												pes2.getLabel(e2p), e2p);
										verbalizeBehDiffFromModelPerspective(
												secondMatchingEventPair.getFirst(), secondMatching.label, (Integer)firstHiding.target, firstHiding.label, 
												secondMatchingEventPair.getSecond(), secondMatching.label, e2p, pes2.getLabel(e2p));
									}
								} else {
									found = false;
									e2p = null;
									for (int i = diffIndexes[0]; i > 0; i--) {
										if (opSeq.get(i).op == Op.RHIDE) {
											Integer hiddenEvent = (Integer)opSeq.get(i).target;
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
//											System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//													firstMatching.label, firstMatchingEventPair.getFirst(),
//													pes1.getBRelation(firstMatchingEventPair.getFirst(), (Integer)firstHiding.target),
//													firstHidingLabel, (Integer)firstHiding.target);
//											System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//													firstMatching.label, firstMatchingEventPair.getSecond(),
//													pes2.getBRelation(firstMatchingEventPair.getSecond(), e2p),
//													pes2.getLabel(e2p), e2p);
											
											verbalizeBehDiffFromModelPerspective(
													firstMatchingEventPair.getFirst(), firstMatching.label, (Integer)firstHiding.target, firstHiding.label, 
													firstMatchingEventPair.getSecond(), firstMatching.label, e2p, pes2.getLabel(e2p));
										}
									} else {
										context1.set(firstMatchingEventPair.getFirst());
										
										if (!globalDiffs.contains(context1, context2)) {
											String statement = String.format("In the log, %s(%d) occurs after %s(%d), while in the model it does not\n",
													firstHidingLabel, (Integer)firstHiding.target,
													firstMatching.label, firstMatchingEventPair.getFirst());
											
											statements.add(statement);
//											System.out.println(statement);
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
//						System.out.printf("**LEFT: %s(%d) %s %s(%d)\n",
//								firstMatching.label, firstMatchingEventPair.getFirst(),
//								pes1.getBRelation(firstMatchingEventPair.getFirst(), (Integer)secondHiding.target),
//								secondHiding.label, (Integer)secondHiding.target);
//						System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//								firstMatching.label, firstMatchingEventPair.getSecond(),
//								pes2.getBRelation(firstMatchingEventPair.getSecond(), (Integer)firstHiding.target),
//								firstHidingLabel, (Integer)firstHiding.target);
						
						verbalizeBehDiffFromModelPerspective(
								firstMatchingEventPair.getFirst(), firstMatching.label, (Integer)secondHiding.target, secondHiding.label, 
								firstMatchingEventPair.getSecond(), firstMatching.label, (Integer)firstHiding.target, firstHiding.label);
					}					
				} else if (secondHiding != null) {
					// ========= Symmetric <<==
					if (!globalDiffs.contains(context1, context2)) {
						String statement =String.format("In the log, after the occurrence of %s(%d), %s(%d) is substituted by %s(%d)\n",
								firstMatching.label, firstMatchingEventPair.getFirst(),
								firstHiding.label, (Integer)firstHiding.target,
								secondHiding.label, (Integer)secondHiding.target);
						
						statements.add(statement);
						System.out.println(statement);
					}
				} else {
					// No LHIDE found within this Difference Context
					if (firstMatching.nextState.labels.contains(firstHidingLabel)) {
						if (!globalDiffs.contains(context1, context2)) {
							String statement = String.format("In the model, after the occurrence of %s(%d), %s(%d) is duplicated, while in the log it is not\n",
									firstMatching.label, firstMatchingEventPair.getFirst(),
									firstHidingLabel, (Integer)firstHiding.target);
							
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
//								System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//										firstMatching.label, firstMatchingEventPair.getFirst(),
//										pes1.getBRelation(firstMatchingEventPair.getFirst(), e1p),
//										pes1.getLabel(e1p), e1p);
//								System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//										firstMatching.label, firstMatchingEventPair.getSecond(),
//										pes2.getBRelation(firstMatchingEventPair.getSecond(), (Integer)firstHiding.target),
//										firstHidingLabel, (Integer)firstHiding.target);
								
								verbalizeBehDiffFromModelPerspective(
										firstMatchingEventPair.getFirst(), firstMatching.label, (Integer)e1p, pes1.getLabel(e1p), 
										firstMatchingEventPair.getSecond(), firstMatching.label, (Integer)firstHiding.target, firstHiding.label);

							}
						} else {
							found = false;
							e1p = null;
							Pair<Integer, Integer> secondMatchingPair = (Pair)secondMatching.target;
							
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
								
								for (Operation succ: descendants.get(firstMatching.nextState))
									if (succ.op == Op.MATCH) {
										found = matchSecond(succ.nextState, context2, secondMatchingPair.getSecond());
										if (found)
											break;
									}
																
								if (found) {
									if (!globalDiffs.contains(context1, context2)) {
										String statement = String.format("In the log, %s(%s) can be skipped, while in the model it cannot\n",
												translate(context2), context2);
										
										statements.add(statement);
										System.out.println(statement);
									}
								} else {
									context1.set(e1p);
									context1.set(secondMatchingEventPair.getFirst());									
									context2.set(secondMatchingEventPair.getSecond());

									if (!globalDiffs.contains(context1, context2)) {
//										System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//												pes1.getLabel(e1p), e1p,
//												pes1.getBRelation(e1p, secondMatchingEventPair.getFirst()),
//												secondMatching.label, secondMatchingEventPair.getFirst());
//										System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//												firstHidingLabel, (Integer)firstHiding.target,
//												pes2.getBRelation((Integer)firstHiding.target, secondMatchingEventPair.getSecond()),
//												secondMatching.label, secondMatchingEventPair.getSecond());
										verbalizeBehDiffFromModelPerspective(
												(Integer)e1p, pes1.getLabel(e1p), secondMatchingEventPair.getFirst(), secondMatching.label, 
												(Integer)firstHiding.target, firstHiding.label, secondMatchingEventPair.getSecond(), secondMatching.label);
									}
								}
							} else {
								e1 = secondMatchingEventPair.getFirst();
								BitSet preds1 = (BitSet)pes1.getDirectPredecessors(e1).clone();
								
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
//										System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//												firstMatching.label, firstMatchingEventPair.getFirst(),
//												pes1.getBRelation(firstMatchingEventPair.getFirst(), e1p),
//												firstHidingLabel, e1p);
//										System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//												firstMatching.label, firstMatchingEventPair.getSecond(),
//												pes2.getBRelation(firstMatchingEventPair.getSecond(), (Integer)firstHiding.target),
//												firstHidingLabel, (Integer)firstHiding.target);
										verbalizeBehDiffFromModelPerspective(
												firstMatchingEventPair.getFirst(), firstMatching.label, (Integer)e1p, pes1.getLabel(e1p), 
												firstMatchingEventPair.getSecond(), firstMatching.label, (Integer)firstHiding.target, firstHiding.label);
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
//											System.out.printf("LEFT: %s(%d) %s %s(%d)\n",
//													firstHidingLabel, e1p,
//													pes1.getBRelation(e1p, secondMatchingEventPair.getFirst()),
//													secondMatching.label, secondMatchingEventPair.getFirst());
//											System.out.printf("RIGHT: %s(%d) %s %s(%d)\n",
//													firstHidingLabel, (Integer)firstHiding.target,
//													pes2.getBRelation((Integer)firstHiding.target, secondMatchingEventPair.getSecond()),
//													secondMatching.label, secondMatchingEventPair.getSecond());
											verbalizeBehDiffFromModelPerspective(
													(Integer)e1p, pes1.getLabel(e1p), secondMatchingEventPair.getFirst(), secondMatching.label,
													(Integer)firstHiding.target, firstHiding.label, secondMatchingEventPair.getSecond(), secondMatching.label);
										}									

									} else { 
										context2.set((Integer)firstHiding.target);
										if (!globalDiffs.contains(context1, context2)) {								
											String statement = String.format("In the model, %s(%d) occurs after %s(%d), while in the log it does not\n",
													firstHidingLabel, (Integer)firstHiding.target,
													firstMatching.label, firstMatchingEventPair.getSecond());
											
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
			
//			System.out.printf("Context '%s, %s'\n", context1, context2);
			String statement = String.format("\tFirst match [%s: (%d),(%d)], Second match [%s: (%d),(%d)]\n",
					firstMatching.label, firstMatchingEventPair.getFirst(), firstMatchingEventPair.getSecond(),
					secondMatching.label, secondMatchingEventPair.getFirst(), secondMatchingEventPair.getSecond()
					);
			
			statements.add(statement);
//			System.out.println(statements);

			Map<Integer, int[]> diffs = globalDiffs.get(context1, context2);
			if (diffs == null)
				globalDiffs.put(context1, context2, diffs = new HashMap<>());
			if (!diffs.containsKey(index))
				diffs.put(index, diffIndexes);
		}
	}
	
//	private void verbalizeBehDiffFromLogPerspective(Integer e1,
//			String e1l, Integer e1p, String e1pl,
//			Integer e2, String e2l, Integer e2p, String e2pl) {
//		System.out.printf("In the log, %s(%d) %s %s(%d), while in the model %s(%d) %s %s(%d)\n", 
//				e1l, e1, verbalizeBRel(pes1.getBRelation(e1, e1p)), e1pl, e1p,
//				e2l, e2, verbalizeBRel(pes2.getBRelation(e2, e2p)), e2pl, e2p);		
//	}
	
	private void verbalizeBehDiffFromModelPerspective(Integer e1,
			String e1l, Integer e1p, String e1pl,
			Integer e2, String e2l, Integer e2p, String e2pl) {
		String statement = String.format("In the model, %s(%d) %s %s(%d), while in the event log %s(%d) %s %s(%d)\n",
				e2l, e2, verbalizeBRel(pes2.getBRelation(e2, e2p)), e2pl, e2p, 
				e1l, e1, verbalizeBRel(pes1.getBRelation(e1, e1p)), e1pl, e1p);
		
		statements.add(statement);
		System.out.println(statement);
	}

	private String verbalizeBRel(BehaviorRelation bRelation) {
		switch (bRelation) {
		case CAUSALITY: return "occurs before";
		case INV_CAUSALITY: return "occurs after";
		case CONCURRENCY: return "occurs concurrently to";
		case CONFLICT: return "cannot occur in the same run with";
		default:
			break;
		}
		return null;
	}

	private boolean matchSecond(State curr, BitSet context2, Integer ev2) {
		boolean result = false;
		for (Operation op: descendants.get(curr)) {
			switch (op.op) {
			case MATCH:
//			case MATCHNSHIFT:
				Pair<Integer, Integer> pair = (Pair)op.target;
				if (context2.get(pair.getSecond()))
					result = matchSecond(op.nextState, context2, ev2);
				else if (pair.getSecond().equals(ev2))
					return true;
				break;
			case RHIDE:
//			case RHIDENSHIFT:
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
//					System.out.println("Found a matching for hidden event: " + secondHidingOperation.target);
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
//					System.out.println("Found a matching for hidden event: " + secondHidingOperation.target);
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
			for (Operation desc: descendants.get(pred))
				if (desc.op == curr.op) {
					if (curr.op == Op.MATCH) {
						Pair<Integer, Integer> pair1 = (Pair)curr.target;
						Pair<Integer, Integer> pair2 = (Pair)desc.target;
						if (pair1.equals(pair2)) {
							found = true;
							break;
						}
					} else {
						Integer ev1 = (Integer)curr.target;
						Integer ev2 = (Integer)desc.target;
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
				unobservedEvents.remove((Integer)((Pair)curr.target).getSecond());
			else //if (curr.op != Op.LHIDE)
				unobservedEvents.remove((Integer)curr.target);
			
			if (diffIndexes == null) {
				if (curr.op == Op.LHIDE) {
//					System.out.println("Found earliest discrepancy (LHIDE): " + curr);
					diffIndexes = new int[3];
					diffIndexes[0] = i - 1;
					diffIndexes[1] = i;
					visibleEventHasBeenHidden = true;
				} else if (curr.op == Op.RHIDE) {
					Integer hiddenEvent = (Integer)curr.target;
//					System.out.println("Found earliest discrepancy (RHIDE): " + curr);
					diffIndexes = new int[3];
					diffIndexes[0] = i - 1;

					visibleEventHasBeenHidden = true; //!pes2.getInvisibleEvents().contains(hiddenEvent);
					if (visibleEventHasBeenHidden)
						diffIndexes[1] = i;
				}
			} else {
				if (curr.op == Op.MATCH) {
					if (visibleEventHasBeenHidden) {
//						System.out.println("==> Context: " + opSeq.subList(diffIndexes[0], i+1));

						diffIndexes[2] = i;
						differences.add(diffIndexes);
					} else {
//						System.out.println("==> Context discarded: No visible event has been hidden");
					}
					diffIndexes = null;
				} else {
					if (!visibleEventHasBeenHidden) {
						if (curr.op == Op.LHIDE) {
							diffIndexes[1] = i;
							visibleEventHasBeenHidden = true;
						} else {
							visibleEventHasBeenHidden = true; //!pes2.getInvisibleEvents().contains((Integer)curr.target);
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
		
//		Map<Multiset<Integer>, Multiset<Integer>> footprints = pes2.getFootprints();
		Set<Integer> toRemove = new HashSet<>();
//		for (Multiset<Integer> footprint: footprints.keySet()) {
//			if (unobservedEvents.containsAll(footprint)) {
//				BitSet cycle = footprints.get(footprint);
//				System.out.printf("In the model, the interval %s is repeated multiple times, while in the log it is not",
//						translate(cycle));
//				toRemove.addAll(footprints.get(footprint));
//			}
//		}
		
		unobservedEvents.removeAll(toRemove);
		
		Set<Integer> primeUnobservedEvents = new HashSet<>();
		
		for (Integer ev: unobservedEvents) {
			boolean found = false;
			BitSet predecessors = pes2.getDirectPredecessors(ev);
			for (int pred = predecessors.nextSetBit(0); pred >= 0; pred = predecessors.nextSetBit(pred+1)) 
				if (unobservedEvents.contains(pred)) {
					found = true;
					break;
				}
			if (!found) primeUnobservedEvents.add(ev);
		}
		
		// We discard all the events that, in spite of not being explicitly represented in the PSPs, have already been considered for verbalization
		primeUnobservedEvents.removeAll(eventsConsideredByConflictRelation);
		
		for (Integer ev: primeUnobservedEvents) {
//			if (pes2.getInvisibleEvents().contains(ev)) {
//				BitSet _causes = pes2.getLocalConfiguration(ev); _causes.clear(ev);
//				BitSet causes = new BitSet();
//				for (int event = _causes.nextSetBit(0); event >=0; event = _causes.nextSetBit(event + 1))
//					causes.set(event);
//				BitSet pe2 = pes2.getPossibleExtensions(causes);
//				
//				for (int event = pe2.nextSetBit(0); event >= 0; event = pe2.nextSetBit(event+1)) 
//					if (!pes2.getInvisibleEvents().contains(event) && pes2.getBRelation(ev, event) == BehaviorRelation.CONFLICT)
//						System.out.printf("    In the model, '%s' can be skipped, while in the event log it cannot\n", pes2.getLabel(event));
//			} else 
			{
				System.out.printf("    Task '%s' appears in PES2 and not in PES1\n", pes2.getLabel(ev));
			}
		}
	}
	
	private Object translate(BitSet bs) {
		Set<String> set = new HashSet<>();
		
		for (int ev = bs.nextSetBit(0); ev >= 0; ev = bs.nextSetBit(ev+1))
			set.add(pes2.getLabel(ev));
		return set;
	}

	public Set<String> getStatements() {
		return statements;
	}

	public List<List<Operation>> getOpSeqs() {
		return this.opSeqs;
	}
}
