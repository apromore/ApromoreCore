package ee.ut.eventstr.comparison;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.RBFSOpenPartialSynchronizedProduct.Operation.Op;
import ee.ut.org.processmining.framework.util.Pair;

public class RBFSOpenPartialSynchronizedProduct<T> {
	private SinglePORunPESSemantics<T> pes1;
	private NewUnfoldingPESSemantics<T> pes2;
	private State root;
		
	public static class State {
		BitSet c1;
		Multiset<Integer> c2;
		Multiset<String> labels;
		Operation succ;

		State(BitSet c1, Multiset<String> labels, Multiset<Integer> c2) {
			this.c1 = c1; this.c2 = c2; this.labels = labels;
		}
		
		public String toString() {
			return String.format("<%s, %s, %s>", c1, labels, c2);
		}
	}
	
	static class Operation {
		enum Op {MATCH, LHIDE, RHIDE, MATCHNSHIFT, RHIDENSHIFT};
		Op op;
		String label;
		State nextState;
		Object target;
		
		private Operation(State state, Op op, Object target, String label) {
			this.nextState = state; this.target = target;
			this.op = op; this.label = label;
		}
		static Operation match(State state, Pair<Integer, Integer> target, String label) {
			return new Operation(state, Op.MATCH, target, label);
		}
		static Operation lhide(State state, Integer target, String label) {
			return new Operation(state, Op.LHIDE, target, label);
		}
		static Operation rhide(State state, Integer target, String label) {
			return new Operation(state, Op.RHIDE, target, label);
		}
		static Operation rhidenshift(State state, Integer target, String label) {
			return new Operation(state, Op.RHIDENSHIFT, target, label);
		}
		static Operation matchnshift(State state, Pair<Integer, Integer> target, String label) {
			return new Operation(state, Op.MATCHNSHIFT, target, label);
		}
		
		public String toString() {
			return String.format("%s(%s[%s])", op.toString().toLowerCase(), label, target);
		}
	}

		
	public RBFSOpenPartialSynchronizedProduct(SinglePORunPESSemantics<T> logpessem, NewUnfoldingPESSemantics<T> pes2) {
		this.pes1 = logpessem;
		this.pes2 = pes2;
	}

	public RBFSOpenPartialSynchronizedProduct<T> perform() {
		root = new State(new BitSet(), HashMultiset.<String> create(), HashMultiset.<Integer> create());
		
		Pair<State, Float> pair = rbfs(root, eta(root), Float.POSITIVE_INFINITY, new LinkedList<Operation>(), null);
		
		System.out.println("================");
		State node = pair.getFirst();
		System.out.println(node);
		while (node.succ != null) {
			System.out.println(node.succ);
			node = node.succ.nextState;
			System.out.println(node);
		}
		return this;
	}
	
	
	
	
	private Pair<State, Float> rbfs(State node, float nodeF, float fLimit, LinkedList<Operation> stack, Operation prev) {
//		System.out.println(stack);

		BitSet lpe = pes1.getPossibleExtensions(node.c1);				
		Set<Integer> rpe = pes2.getPossibleExtensions(node.c2);
		
		if (lpe.isEmpty() && rpe.isEmpty())
			return new Pair<>(node, fLimit);
		
		List<Operation> successors = expandNode(node, stack, prev);
		float[] f = new float[successors.size()];
		for (int s = 0; s < f.length; s++)
			f[s] = Math.max(eta(successors.get(s).nextState), nodeF);
		
		while (true) {
			int bestIdx = findMinIdx(f);
			if (f[bestIdx] > fLimit)
				return new Pair<>(null, f[bestIdx]);
			int altIndex = findSecondMinIdx(f, bestIdx);
			
			
//			System.out.println("Trying: " + successors.get(bestIdx));
			
			Operation pprev = successors.get(bestIdx).op.equals(Op.MATCH) || successors.get(bestIdx).op.equals(Op.MATCHNSHIFT) ?
					successors.get(bestIdx) : prev;
			
			stack.push(successors.get(bestIdx));
			Pair<State, Float> localResult = 
					rbfs(successors.get(bestIdx).nextState, f[bestIdx], Math.min(fLimit, f[altIndex]), stack, pprev);
			stack.pop();
			if (localResult.getFirst() != null) {
				node.succ = successors.get(bestIdx);
				return new Pair<>(node, localResult.getSecond());
			}
//			System.out.print("...");
			f[bestIdx] = localResult.getSecond();
		}
	}

	public int findMinIdx(float[] numbers) {
	    float minVal = Float.MAX_VALUE;
	    int minIdx = 0;
	    for(int idx = 0; idx < numbers.length; idx++) {
	        if(numbers[idx] < minVal) {
	            minVal = numbers[idx];
	            minIdx = idx;
	        }
	    }
	    return minIdx;
	}

	public int findSecondMinIdx(float[] numbers, int bestIndex) {
	    float minVal = Float.MAX_VALUE;
	    int minIdx = bestIndex;
	    
	    for(int idx = 0; idx < numbers.length; idx++) {
	        if (idx != bestIndex && numbers[idx] < minVal) {
	            minVal = numbers[idx];
	            minIdx = idx;
	        }
	    }
	    return minIdx;
	}

	
	private List<Operation> expandNode(State node, LinkedList<Operation> stack, Operation prev) {
		List<Operation> successors = new ArrayList<>();
		BitSet lpe = pes1.getPossibleExtensions(node.c1);				
		Set<Integer> rpe = pes2.getPossibleExtensions(node.c2);
		
		Pair<Integer, Integer> prevPair = prev != null ? (Pair)prev.target : null;
		
		
		for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
			String label1 = pes1.getLabel(e1);
			BitSet c1p = (BitSet)node.c1.clone();
			c1p.set(e1);
			
			for (Integer e2: rpe) {
				if (label1.equals(pes2.getLabel(e2)) 
						&& isOrderPreserving(node, e1, e2, stack)
					) {
					
					if (prev != null && 
							pes1.getBRelation(e1, prevPair.getFirst()) == BehaviorRelation.CONCURRENCY &&
							pes2.getBRelation(e2, prevPair.getSecond()) == BehaviorRelation.CONCURRENCY &&
							label1.compareTo(prev.label) > 0)
						continue;
					
					Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(node.c2, e2);
					Multiset<String> labels = HashMultiset.create(node.labels);
					labels.add(label1);
					State nstate = new State(c1p, labels, extPair.getFirst());
					if (extPair.getSecond())
						successors.add(Operation.matchnshift(nstate, new Pair<>(e1, e2), label1));
					else
						successors.add(Operation.match(nstate, new Pair<>(e1, e2), label1));
				}
			}
		}
		
		List<Operation> delayedSuccessors = new ArrayList<>();
		
		for (Integer e2: rpe) {
			Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(node.c2, e2);
			State nstate = new State(node.c1, node.labels, extPair.getFirst());
			if (extPair.getSecond()) {
				if (pes2.getLocalConfiguration(e2).get(pes2.getCorresponding(e2)))
					delayedSuccessors.add(Operation.rhidenshift(nstate, e2, pes2.getLabel(e2)));
				else
					successors.add(Operation.rhidenshift(nstate, e2, pes2.getLabel(e2)));
			} else
				successors.add(Operation.rhide(nstate, e2, pes2.getLabel(e2)));
		}
		
		for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
			BitSet c1p = (BitSet)node.c1.clone();
			c1p.set(e1);
			State nstate = new State(c1p, node.labels, node.c2);
			successors.add(Operation.lhide(nstate, e1, pes1.getLabel(e1)));
		}
		
		successors.addAll(delayedSuccessors);
		
		return successors;
	}
	
	private boolean isOrderPreserving(State s, int e1, Integer e2, LinkedList<Operation> stack) {
		BitSet e1dpred = (BitSet)pes1.getDirectPredecessors(e1).clone();
		Set<Integer> e2dpred = new HashSet<>(pes2.getDirectPredecessors(e2));
				
		BitSet e1causes = pes1.getLocalConfiguration(e1);
		BitSet e2causes = pes2.getLocalConfiguration(e2);
		
		
		for (Operation op: stack) {
//			System.out.println(">> " + op);
			if (op.op == Op.MATCH) {
				@SuppressWarnings("unchecked")
				Pair<Integer, Integer> matchedEvents = (Pair<Integer,Integer>)op.target;
				e1dpred.clear(matchedEvents.getFirst());
				e2dpred.remove(matchedEvents.getSecond());
				
				if (!(e1causes.get(matchedEvents.getFirst()) == e2causes.get(matchedEvents.getSecond()))) {
//					System.out.println("====== It is not order preserving!");
					return false;
				}

			} else if (op.op == Op.MATCHNSHIFT) {
				@SuppressWarnings("unchecked")
				Pair<Integer, Integer> matchedEvents = (Pair<Integer,Integer>)op.target;
				e1dpred.clear(matchedEvents.getFirst());
				e2dpred.remove(matchedEvents.getSecond());
				
//				System.out.println("Performed inverse shift (+match): " + matchedEvents.getSecond());
				if (pes2.getBRelation(e2, matchedEvents.getSecond()) != BehaviorRelation.CONCURRENCY) {
					e2causes = pes2.unshift(e2causes, matchedEvents.getSecond());
//					e2causes = pes2.getLocalConfiguration(matchedEvents.getSecond());
				}
				
				if (!(e1causes.get(matchedEvents.getFirst()) == e2causes.get(matchedEvents.getSecond()))) {
//					System.out.println("====== It is not order preserving! (after inverse shift)");
					return false;
				}
			} else if (op.op == Op.RHIDENSHIFT || op.op == Op.RHIDE) {
				Integer hiddenEvent = (Integer)op.target;
//				if (e2dpred.contains(hiddenEvent)) {
					e2dpred.remove(hiddenEvent);
					e2dpred.addAll(pes2.getDirectPredecessors(hiddenEvent));
					if (op.op == Op.RHIDENSHIFT && pes2.getBRelation(e2, hiddenEvent) != BehaviorRelation.CONCURRENCY) {
//						System.out.println("Performed inverse shift: " + hiddenEvent);
						e2causes = pes2.unshift(e2causes, hiddenEvent);
//						e2causes.clear(hiddenEvent);
//								e2causes = pes2.getLocalConfiguration(hiddenEvent);
					}
//				}
			} else {
				Integer hiddenEvent = (Integer)op.target;
				e1dpred.clear(hiddenEvent);
				e1dpred.or(pes1.getDirectPredecessors(hiddenEvent));
			}
		}
		return true;
	}

	public float eta(State s) {
		Multiset<Integer> c2copy = HashMultiset.create(s.c2);
		c2copy.removeAll(pes2.getInvisibleEvents());
		return g(s.c1, c2copy, s.labels)
				+ h(s.c1, s.c2)
				;
	}
		
	public float g(BitSet c1, Multiset<Integer> c2, Multiset<String> labels) {
		return c1.cardinality() + c2.size() - labels.size() * 2.0f;
	}
	
	public float h(BitSet c1, Multiset<Integer> c2) {
		Set<String> pf1 = pes1.getPossibleFutureAsLabels(c1);
		Set<String> pf2 = pes2.getPossibleFutureAsLabels(c2);
		
		pf1.removeAll(pf2);
		return pf1.size();
	}

//	private boolean contains(BitSet superset, BitSet subset) {
//		int cardinality = superset.cardinality();
//		superset = (BitSet)superset.clone();
//		superset.or(subset);
//		return cardinality == superset.cardinality();
//	}
}