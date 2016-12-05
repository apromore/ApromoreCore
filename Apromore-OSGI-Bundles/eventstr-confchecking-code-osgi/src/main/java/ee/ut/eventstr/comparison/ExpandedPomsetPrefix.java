package ee.ut.eventstr.comparison;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Op;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Operation;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.State;
import ee.ut.org.processmining.framework.util.Pair;

public class ExpandedPomsetPrefix<T> {
	private NewUnfoldingPESSemantics<T> pes;
	private Multimap<BitSet, BitSet> adjList;
	private Multimap<BitSet, BitSet> invAdjList;
	private Map<BitSet, Pair<Multiset<Integer>, Multiset<Integer>>> cycles;
	private Set<Multiset<Integer>> runs;
	private Set<BitSet> states;
	
	private HashMap<BitSet, State> stateMap;
	private HashMap<BitSet, Operation> opMap;
	
	public ExpandedPomsetPrefix(NewUnfoldingPESSemantics<T> pes) {
		this.pes = pes;
		this.adjList = HashMultimap.create();
		this.invAdjList = HashMultimap.create();
		this.stateMap = new HashMap<>();
		this.opMap = new HashMap<>();
		
		this.cycles = new HashMap<>();
		this.runs = new HashSet<>();
		
		buildPrefix(HashMultiset.<Integer>create(), new BitSet(), new LinkedHashMap<BitSet, Multiset<Integer>>());
		
		this.states = new HashSet<>(adjList.values());
		this.states.add(new BitSet());
	}
	
	private void buildPrefix(Multiset<Integer> conf, BitSet sconf, 
			LinkedHashMap<BitSet, Multiset<Integer>> visited) {
		visited.put(sconf, conf);
		
		for (Integer e: pes.getPossibleExtensions(conf)) {
			Pair<Multiset<Integer>, Boolean> pair = pes.extend(conf, e);
			Multiset<Integer> n_conf = pair.getFirst();
			BitSet n_sconf = pes.getShifted(n_conf);
			
			BitSet intersection = (BitSet)n_sconf.clone();
			intersection.and(pes.getConcurrencySet(e));
			
			if (visited.containsKey(n_sconf) && intersection.isEmpty()) {
				Multiset<Integer> entryConf = visited.get(n_sconf);
				Multiset<Integer> cycle = HashMultiset.create(n_conf);
				cycle.removeAll(entryConf);
				BitSet index = pack(n_conf);
				if (!cycles.containsKey(index)) {
					cycles.put(index, new Pair<>(entryConf, cycle));
					BitSet fake = pack(n_conf);
					visited.put(fake, n_conf);
					addBranch(visited);
					visited.remove(fake);
				}
			} 
			else {
				buildPrefix(n_conf, n_sconf, visited);
			}
		}
		
		// Is maximal?
		if (pes.getMaxConf().contains(sconf)) {
			if (!runs.contains(conf)) {
				runs.add(conf);
				addBranch(visited);
			}
		}
		visited.remove(sconf);
	}
	
	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {");
		
		out.println("\tnode[shape=box];");
		for (BitSet state: states) {
			out.printf("\tn%d [label=\"%s\\n%s\"];\n", state.hashCode(), state, opMap.get(state));
		}
		
		for (Entry<BitSet,BitSet> entry: adjList.entries()) {
			out.printf("\tn%d -> n%d;\n", entry.getKey().hashCode(), entry.getValue().hashCode());
		}
		
		out.println("}");
		
		return str.toString();
	}


	private void addBranch(LinkedHashMap<BitSet, Multiset<Integer>> visited) {
		BitSet curr = null;
		for (Entry<BitSet, Multiset<Integer>> entry: visited.entrySet()) {
			BitSet succ = pack(entry.getValue());
			if (curr != null) {
				adjList.put(curr, succ);
				invAdjList.put(succ, curr);
			}
			curr = succ;
		}
	}

	private BitSet pack(Multiset<Integer> conf) {
		BitSet set = new BitSet();
		for (int e: conf) {
			set.set(e);
		}
		return set;
	}

	public void mark(State state, Operation op) {
		if (op.op == Op.LHIDE) {
			return;
		}
		
		BitSet bset = pack(state.c2);
		if (bset.cardinality() == state.c2.size()) {			
			Operation currOp = opMap.get(bset);
			if (currOp == null) {
				opMap.put(bset, op);
				stateMap.put(bset, state);
			} 
			else if (!(currOp.op == Op.MATCH || currOp.op == Op.MATCHNSHIFT)) {
				if (op.op == Op.MATCH || op.op == Op.MATCHNSHIFT) {
					opMap.put(bset, op);
					stateMap.put(bset, state);					
				}
			}
		}
	}
	
//	public Multimap<State, List<Integer>> getAdditionalAcyclicIntervals_test() {
//		Multimap<State, List<Integer>> additional = HashMultimap.create();
//		for (Multiset<Integer> _run: runs) {
//			BitSet run = pack(_run);
//			System.out.println(run + " run");
//			System.out.println(opMap + " opMap");
//			
//			if (!opMap.containsKey(run)) {
//				LinkedList<Integer> tasks = new LinkedList<Integer>();
//				Stack<BitSet> open = new Stack<BitSet>();
//				Set<BitSet> visited = new HashSet<BitSet>();
//				open.push(run);
//				boolean stateFound = false;
//				
//				BitSet interval = new BitSet();
//				for (BitSet mc: opMap.keySet()) {
//					if (opMap.get(mc).label.equals("_1_")) {
//						interval.or(mc);
//						break;
//					}
//				}
//				System.out.println(interval + " interval");
//				interval.andNot(run);
//				for (int event = interval.nextSetBit(0); event >= 0; event = interval.nextSetBit(event + 1)) {
//					if (!pes.getInvisibleEvents().contains(event)) {
//						tasks.addFirst(event);
//					}
//				}
//				
//				while (!open.isEmpty()) {
//					BitSet curr = open.pop();
//					
//					Operation op = opMap.get(curr);
//					if (op == null || op.op == Op.RHIDE || op.op == Op.RHIDENSHIFT) {
//						for (BitSet pred: invAdjList.get(curr)) {
//							if ((!visited.contains(pred)) && (!open.contains(pred))) {
//								open.push(pred);
//							}
//						}
//					} 
//					else if (!stateFound) {
//						additional.put(stateMap.get(curr), tasks);
//						stateFound = true;
//					}
//				}
//			}
//		}
//		return additional;
//	}
	
	public Multimap<State, List<Integer>> getOptionalAcyclicIntervals() {
		Multimap<State, List<Integer>> optional = HashMultimap.create();
		
		Set<BitSet> maxconfs = new HashSet<BitSet>(opMap.keySet());
		maxconfs.removeAll(adjList.keySet());
		
		BitSet diff = new BitSet();
		BitSet startconf = new BitSet();
		
		Set<BitSet> prevconfs = new HashSet<BitSet>();
		Set<BitSet> bitruns = new HashSet<BitSet>();
		
		List<Integer> elem;
		Boolean found;
		
		for (Multiset<Integer> _run: runs) {
			bitruns.add(pack(_run));
		}

		for (BitSet run: bitruns) {
			if (!opMap.containsKey(run)) {
				for (BitSet max: maxconfs) {
					diff.or(max);
					diff.andNot(run);

//					for (BitSet fm: opMap.keySet()) {
//						if (fm.intersects(diff)) {
//							for (BitSet prev: invAdjList.get(fm)) {
//								if (!prev.intersects(diff)) {
//									firstmismatchconfs.add(fm);
//								}
//							}
//						}
//					}

					for (int d = diff.nextSetBit(0); d >= 0; d = diff.nextSetBit(d + 1)) {
						BitSet conflict = pes.getDirectConflictSet(d);
						BitSet runcopy;
						found = false;
						
						if (!pes.getInvisibleEvents().contains(d)) {
							for (int d2 = conflict.nextSetBit(0); d2 >= 0; d2 = conflict.nextSetBit(d2 + 1)) {
								runcopy = (BitSet)run.clone();
								runcopy.set(d2);
								runcopy.clear(d);
								if ((bitruns.contains(runcopy)) && (!pes.getInvisibleEvents().contains(d2))) {
									found = true;
								}
							}
							
							if ((!found) && (!pes.getDirectConflictSet(d).intersects(diff))) {
								for (BitSet prev: opMap.keySet()) {
									if (!prev.get(d)) {
										for (BitSet next: adjList.get(prev)) {
											if (next.get(d)) {
												elem = new ArrayList<Integer>();
												if (!pes.getInvisibleEvents().contains(d)) {
													elem.add(d);
													optional.put(stateMap.get(prev), elem);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		
		return optional;
	}
	
	public Multimap<State, List<Integer>> getAdditionalAcyclicIntervals() {
		Multimap<State, List<Integer>> additional = HashMultimap.create();
		for (Multiset<Integer> _run: runs) {
			BitSet run = pack(_run);
			if (!opMap.containsKey(run)) {
				LinkedList<Integer> tasks = new LinkedList<Integer>();
				Stack<BitSet> open = new Stack<BitSet>();
				Set<BitSet> visited = new HashSet<BitSet>();
				open.push(run);
				BitSet prev = null;
				boolean stateFound = false;
				while (!open.isEmpty()) {
					BitSet curr = open.pop();
					
					if (prev != null) {
						BitSet diff = (BitSet) prev.clone();
						diff.andNot(curr);
						int event = diff.nextSetBit(0);
						if (event >= 0) {
							if (!pes.getInvisibleEvents().contains(event)) {
								tasks.addFirst(event);
							}
						}
					}
					
					Operation op = opMap.get(curr);
					if (op == null || op.op == Op.RHIDE || op.op == Op.RHIDENSHIFT) {
						for (BitSet pred: invAdjList.get(curr)) {
							if ((!visited.contains(pred)) && (!open.contains(pred))) {
								open.push(pred);
							}
						}
					} 
					else if (!stateFound) {
						additional.put(stateMap.get(curr), tasks);
						stateFound = true;
					}
//					prev = (BitSet)curr.clone();
				}
			}
		}
		return additional;
	}
	
	public Multimap<State, Multiset<Integer>> getAdditionalCyclicIntervals() {
		Multimap<State, Multiset<Integer>> additional = HashMultimap.create();
		for (BitSet cycleMarker: cycles.keySet()) {			
			if (!opMap.containsKey(cycleMarker)) {
				Pair<Multiset<Integer>, Multiset<Integer>> pair = cycles.get(cycleMarker);
				Stack<BitSet> open = new Stack<>();
				Set<BitSet> visited = new HashSet<>();
				open.push(cycleMarker);
				while (!open.isEmpty()) {
					BitSet curr = open.pop();
					
					Operation op = opMap.get(curr);
					
					if (op == null || op.op == Op.RHIDE || op.op == Op.RHIDENSHIFT) {
						for (BitSet pred: invAdjList.get(curr)) {
							if (!visited.contains(pred) && !open.contains(pred)) {
								open.push(pred);
							}
						}
					} 
					else {
						additional.put(stateMap.get(curr), pair.getSecond());
						break;
					}
				}
			}
		}
		return additional;
	}	
}
