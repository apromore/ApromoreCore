package nl.rug.ds.bpm.eventStructure;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.nets.unfolding.Unfolding2PES;

public class CombinedEventStructure {
	private List<String> totalLabels;
	private Set<Integer> silents;
	private int source;
	private int sink;
	private int pesCount;
	
	// Integer = label index in totalLabels 
	// BitSet  = PESs that contain this label
	private Map<Integer, BitSet> labelmap;
	
	// all relations: 
	// BitSet1 = behavioral relation with fromEvent and toEvent 
	// BitSet2 = set of relation types that hold for this combination of events
	private Map<BitSet, BitSet> combinedPES;
		
	// mutual relations: 
	// BitSet = fromEvent, toEvent
	private Set<BitSet> directcausals;
	private Set<BitSet> invdirectcausals;
	private Set<BitSet> transcausals;
	private Set<BitSet> invtranscausals;
	private Set<BitSet> existcausals;
	private Set<BitSet> invexistcausals;
	private Set<BitSet> conflict;
	private Set<BitSet> concurrency;
	private Set<BitSet> loops;
	private Set<BitSet> directloops;
	private Set<BitSet> invdirectloops;
	
	private BitSet mslevents; // mutual self loop events
	
	private BitSet syncevents; 
	
	// int selfloop event, bitset PESs that have that selfloop
	private Map<Integer, BitSet> sleventmap; // self loop event map	
	
	// relations and their originating PES: 
	// BitSet1 = behavioral relation with fromEvent and toEvent
    // BitSet2 = PESs that have this relation
	private Map<BitSet, BitSet> dcmap;
	private Map<BitSet, BitSet> idcmap;
	private Map<BitSet, BitSet> tcmap;
	private Map<BitSet, BitSet> itcmap;
	private Map<BitSet, BitSet> cfmap;
	private Map<BitSet, BitSet> ccmap;
	private Map<BitSet, BitSet> lpmap;
	private Map<BitSet, BitSet> dlpmap;
	private Map<BitSet, BitSet> idlpmap;
	
	private Map<BitSet, BitSet> relmap;
	
	public CombinedEventStructure() {
		combinedPES = new HashMap<BitSet, BitSet>();
		totalLabels = new ArrayList<String>();
		silents = new HashSet<Integer>();
		labelmap = new HashMap<Integer, BitSet>();
		pesCount = 0;
		
		directcausals = new HashSet<BitSet>();
		invdirectcausals = new HashSet<BitSet>();
		transcausals = new HashSet<BitSet>();
		invtranscausals = new HashSet<BitSet>();
		existcausals = new HashSet<BitSet>();
		invexistcausals = new HashSet<BitSet>();
		conflict = new HashSet<BitSet>();
		concurrency = new HashSet<BitSet>();
		loops = new HashSet<BitSet>();
		directloops = new HashSet<BitSet>();
		invdirectloops = new HashSet<BitSet>();
		
		syncevents = new BitSet();
		
		mslevents = new BitSet();
		
		sleventmap = new HashMap<Integer, BitSet>();
		
		dcmap = new HashMap<BitSet, BitSet>();
		idcmap = new HashMap<BitSet, BitSet>();
		tcmap = new HashMap<BitSet, BitSet>();
		itcmap = new HashMap<BitSet, BitSet>();
		cfmap = new HashMap<BitSet, BitSet>();
		ccmap = new HashMap<BitSet, BitSet>();
		lpmap = new HashMap<BitSet, BitSet>();
		dlpmap = new HashMap<BitSet, BitSet>();
		idlpmap = new HashMap<BitSet, BitSet>();
		
		relmap = new HashMap<BitSet, BitSet>();
	}
	
	public void addPES(Unfolding2PES unfpes) {
		int relation, e1, e2;
		BitSet br, causes, predecessors;
		Set<BitSet> visitedBr = new HashSet<BitSet>();
		
		NewUnfoldingPESSemantics<Integer> pessem = getPESSemantics(unfpes);
		
		Map<Integer, BitSet> correspondings = getCorrespondings(pessem);
		
//		System.out.println(getAllCausesOf(pessem, correspondings, pessem.getLabels().size() - 1, new BitSet()));
		
		// first add all labels
		int lbl;
		for (int i = 0; i < pessem.getLabels().size(); i++) {
			if (!totalLabels.contains(pessem.getLabel(i))) {
				totalLabels.add(pessem.getLabel(i));
				if (pessem.getLabel(i).equals("_0_")) source = totalLabels.size() - 1;
				if (pessem.getLabel(i).equals("_1_")) sink = totalLabels.size() - 1;
			}
			
			lbl = totalLabels.indexOf(pessem.getLabel(i));
			if (pessem.getInvisibleEvents().contains(i)) silents.add(lbl);
			if (!labelmap.containsKey(lbl)) labelmap.put(lbl, new BitSet());
			
			labelmap.get(lbl).set(pesCount);
		}

		// traverse cutoff traces and replace them with corresponding relations
		int corr;
		BitSet loopsucc;
		BitSet looppred;
		
		for (int cutoff: pessem.getCutoffEvents()) {
			corr = pessem.getCorresponding(cutoff);
						
			// if the corresponding event is before the cutoff, then we're dealing with a loop
			if (pessem.getCausesOf(cutoff).get(corr)) {
				loopsucc = getRealSuccessors(pessem, corr, new BitSet());
				if (pessem.getInvisibleEvents().contains(cutoff)) {
					looppred = getRealPredecessors(pessem, cutoff, new BitSet());
				}
				else {
					looppred = new BitSet();
					looppred.set(cutoff);
				}
				
				for (int p = looppred.nextSetBit(0); p >= 0; p = looppred.nextSetBit(p + 1)) {
					for (int s = loopsucc.nextSetBit(0); s >= 0; s = loopsucc.nextSetBit(s + 1)) {
						// check for selfloops
						if (p == s) {
							if (!sleventmap.containsKey(p)) sleventmap.put(p, new BitSet());
							sleventmap.get(p).set(p);
						}
						else {
							if (pessem.getCausesOf(p).get(s)) addLoop(p, s);
						}
					}
				}
			}
			
			// fix direct causality of cutoff event
			pessem.getDirectSuccessors(cutoff).addAll(pessem.getDirectSuccessors(corr));
			
			// fix causality relations of cutoff event
			for (int i = 0; i < pessem.getLabels().size(); i++) {
				if ((pessem.getBRelation(corr, i) == BehaviorRelation.CAUSALITY) && (!pessem.getDirectSuccessors(cutoff).contains(i))) {
					e1 = totalLabels.indexOf(pessem.getLabel(cutoff));
					e2 = totalLabels.indexOf(pessem.getLabel(i));
					if (e1 != e2) {
						br = hash(e1, e2);
						if (!combinedPES.containsKey(br))
							combinedPES.put(br, new BitSet(6));
						
						if (e1 < e2) {
							addRelation(br, 2);
						}
						else {
							addRelation(br, 3);
						}
						visitedBr.add(br);
					}
				}
			}
		}
		
		// fix causality relations of cutoff-preceding events
		for (int i = 0; i < pessem.getLabels().size(); i++) {
			causes = getAllCausesOf(pessem, correspondings, i, new BitSet());
			predecessors = getRealPredecessors(pessem, i, new BitSet());
			causes.andNot(predecessors);
			for (int p = predecessors.nextSetBit(0); p >= 0; p = predecessors.nextSetBit(p + 1)) {
				if (correspondings.containsKey(p))
					causes.andNot(correspondings.get(p));
			}
			
			for (int cause = causes.nextSetBit(0); cause >= 0; cause = causes.nextSetBit(cause + 1)) {
				e1 = totalLabels.indexOf(pessem.getLabel(cause));
				e2 = totalLabels.indexOf(pessem.getLabel(i));
				if (e1 != e2) {
					br = hash(e1, e2);
					if (!combinedPES.containsKey(br))
						combinedPES.put(br, new BitSet(6));
					
					if (e1 < e2) {
						addRelation(br, 2);
					}
					else {
						addRelation(br, 3);
					}
					visitedBr.add(br);
				}
			}
		}
		
		// fill out all sets with behavioral relations 
		for (int x = 0; x < pessem.getLabels().size(); x++) {
			for (int y = x + 1; y < pessem.getLabels().size(); y++) {
				e1 = totalLabels.indexOf(pessem.getLabel(x));
				e2 = totalLabels.indexOf(pessem.getLabel(y));
				
				if (e1 != e2) {
					br = hash(e1, e2);

					if (!combinedPES.containsKey(br))
						combinedPES.put(br, new BitSet(6));
									
					if (pessem.getDirectSuccessors(x).contains(y)) {
						if (pessem.getInvisibleEvents().contains(y)) {
							BitSet realsucc = getRealSuccessors(pessem, y, new BitSet());
							
							for (int yn = realsucc.nextSetBit(0); yn >= 0; yn = realsucc.nextSetBit(yn + 1)) {
								e2 = totalLabels.indexOf(pessem.getLabel(yn));
								if (e2 != e1) {
									br = hash(e1, e2);
									if (!combinedPES.containsKey(br))
										combinedPES.put(br, new BitSet(6));
									
									fillInDirectCausals(e1, e2, br);
									visitedBr.add(br);
								}
							}
						}
						else {
							fillInDirectCausals(e1, e2, br);
							visitedBr.add(br);
						}					
					}
					else if (pessem.getDirectSuccessors(y).contains(x)) {
						if (pessem.getInvisibleEvents().contains(x)) {
							BitSet realsucc = getRealSuccessors(pessem, x, new BitSet());
							for (int yn = realsucc.nextSetBit(0); yn >= 0; yn = realsucc.nextSetBit(yn + 1)) {
								e1 = totalLabels.indexOf(pessem.getLabel(yn));
								if (e2 != e1) {
									br = hash(e1, e2);
									if (!combinedPES.containsKey(br))
										combinedPES.put(br, new BitSet(6));
									
									fillInInvDirectCausals(e1, e2, br);
									visitedBr.add(br);
								}
							}
						}
						else {
							fillInInvDirectCausals(e1, e2, br);
							visitedBr.add(br);
						}
	
					}
					else {
						// CAUSALITY, INV_CAUSALITY, CONFLICT, CONCURRENCY
						if (!visitedBr.contains(br)) {
							if (e1 < e2) {
								relation = pessem.getBRelation(x, y).ordinal() + 2;
							}
							else {
								relation = pessem.getBRelation(y, x).ordinal() + 2;
							}
							
							if (relation < 6) {
								addRelation(br, relation);
							}
						}
					}
				}
			}
		}
		
		// fill loop map		
		for (int e = 0; e < totalLabels.size() - 1; e++) {
			for (int f = e + 1; f < totalLabels.size(); f++) {
				br = hash(e, f);
				if (((dcmap.containsKey(br) && dcmap.get(br).get(pesCount)) || (tcmap.containsKey(br) && tcmap.get(br).get(pesCount))) &&
					((idcmap.containsKey(br) && idcmap.get(br).get(pesCount)) || (itcmap.containsKey(br) && itcmap.get(br).get(pesCount)))) {
					if (!lpmap.containsKey(br)) lpmap.put(br, new BitSet());
					lpmap.get(br).set(pesCount);
				}
			}
		}
				
		pesCount++;
	}
	
	private void addRelation(BitSet br, int relation) {
		combinedPES.get(br).set(relation);
		
		switch (relation) {
		case 2:
			if (!tcmap.containsKey(br)) tcmap.put(br, new BitSet());
			tcmap.get(br).set(pesCount);
			break;
		case 3:
			if (!itcmap.containsKey(br)) itcmap.put(br, new BitSet());
			itcmap.get(br).set(pesCount);
			break;
		case 4:
			if (!cfmap.containsKey(br)) cfmap.put(br, new BitSet());
			cfmap.get(br).set(pesCount);
			break;
		case 5:
			if (!ccmap.containsKey(br)) ccmap.put(br, new BitSet());
			ccmap.get(br).set(pesCount);
			break;
		}
		
		addRelMap(br);
	}
	
	private void addLoop(int e1, int e2) {
		BitSet br = hash(e1, e2);
		
		if (e1 < e2) {
			if (!dlpmap.containsKey(br)) dlpmap.put(br, new BitSet());
			dlpmap.get(br).set(pesCount);
		}
		else {
			if (!idlpmap.containsKey(br)) idlpmap.put(br, new BitSet());
			idlpmap.get(br).set(pesCount);
		}
	}
	
	private void fillInDirectCausals(int e1, int e2, BitSet br) {
		Map<BitSet, BitSet> curmap;
		
		if (e1 < e2) {
			combinedPES.get(br).set(0);
			curmap = dcmap;
		}
		else {
			combinedPES.get(br).set(1);
			curmap = idcmap;
		}
		
		if (!curmap.containsKey(br)) curmap.put(br, new BitSet());
		curmap.get(br).set(pesCount);
		
		addRelMap(br);
	}
	
	private void fillInInvDirectCausals(int e1, int e2, BitSet br) {
		Map<BitSet, BitSet> curmap;

		if (e1 < e2) {
			combinedPES.get(br).set(1);
			curmap = idcmap;
		}
		else {
			combinedPES.get(br).set(0);
			curmap = dcmap;
		}
		
		if (!curmap.containsKey(br)) curmap.put(br, new BitSet());
		curmap.get(br).set(pesCount);
		
		addRelMap(br);
	}
	
	private void fillInSyncEvents() {
		BitSet findcaus = new BitSet();
		
		for (BitSet conc: ccmap.keySet()) {
			for (BitSet caus: directcausals) {
				findcaus.clear();

				if (caus.nextSetBit(0) == conc.nextSetBit(0)) {
					findcaus.set(conc.previousSetBit(conc.length()));
					findcaus.set(caus.previousSetBit(caus.length()));

					if (caus.previousSetBit(caus.length()) > conc.previousSetBit(conc.length())) {
						if (directcausals.contains(findcaus)) syncevents.set(caus.previousSetBit(caus.length()));
					}
					else {
						if (invdirectcausals.contains(findcaus)) syncevents.set(caus.previousSetBit(caus.length()));
					}
				}
				else if (caus.nextSetBit(0) == conc.previousSetBit(conc.length())) {
					findcaus.set(conc.nextSetBit(0));
					findcaus.set(caus.previousSetBit(caus.length()));

					if (caus.previousSetBit(caus.length()) > conc.nextSetBit(conc.length())) {
						if (directcausals.contains(findcaus)) syncevents.set(caus.previousSetBit(caus.length()));
					}
					else {
						if (invdirectcausals.contains(findcaus)) syncevents.set(caus.previousSetBit(caus.length()));
					}
				}
				
			}
			for (BitSet invcaus: invdirectcausals) {
				findcaus.clear();
				
				if (invcaus.previousSetBit(invcaus.length()) == conc.nextSetBit(0)) {
					findcaus.set(conc.previousSetBit(conc.length()));
					findcaus.set(invcaus.nextSetBit(0));

					if (invcaus.nextSetBit(0) > conc.previousSetBit(conc.length())) {
						if (directcausals.contains(findcaus)) syncevents.set(invcaus.nextSetBit(0));
					}
					else {
						if (invdirectcausals.contains(findcaus)) syncevents.set(invcaus.nextSetBit(0));
					}
				}
				else if (invcaus.previousSetBit(invcaus.length()) == conc.previousSetBit(conc.length())) {
					findcaus.set(conc.nextSetBit(0));
					findcaus.set(invcaus.nextSetBit(0));

					if (invcaus.nextSetBit(0) > conc.previousSetBit(conc.length())) {
						if (directcausals.contains(findcaus)) syncevents.set(invcaus.nextSetBit(0));
					}
					else {
						if (invdirectcausals.contains(findcaus)) syncevents.set(invcaus.nextSetBit(0));
					}
				}
			}
		}
		
//		System.out.println("Sync events: " + syncevents);
	}
		
	private void addRelMap(BitSet br) {
		if (!relmap.containsKey(br)) relmap.put(br, new BitSet());
		relmap.get(br).set(pesCount);
	}
	
	private BitSet getRealPredecessors(NewUnfoldingPESSemantics<Integer> pessem, int event, BitSet visited) {
		BitSet pred = toBitSet(pessem.getDirectPredecessors(event));
		BitSet cleanpred = new BitSet();
		BitSet silents = toBitSet(pessem.getInvisibleEvents());
		
		BitSet nvisited = (BitSet)visited.clone();
		nvisited.set(event);
		
		cleanpred.or(pred);
		cleanpred.andNot(silents); // remove all silents
		pred.andNot(cleanpred); // remove all clean predecessors
		pred.andNot(nvisited); // remove all predecessors that already have been visited, in order to prevent endless loops with silents
		
		for (int e = pred.nextSetBit(0); e >= 0; e = pred.nextSetBit(e + 1)) {
			cleanpred.or(getRealPredecessors(pessem, e, nvisited));
		}
		
		return cleanpred;
	}
	
	private BitSet getRealSuccessors(NewUnfoldingPESSemantics<Integer> pessem, int event, BitSet visited) {
		BitSet succ;
		
		if (pessem.getCutoffEvents().contains(event)) {
			succ = toBitSet(pessem.getDirectSuccessors(getRealCorresponding(pessem, event)));
		}
		else {
			succ = toBitSet(pessem.getDirectSuccessors(event));
		}
		
		BitSet cleansucc = new BitSet();
		BitSet silents = toBitSet(pessem.getInvisibleEvents());
		
		BitSet nvisited = (BitSet)visited.clone();
		nvisited.set(event);
		
		cleansucc.or(succ);
		cleansucc.andNot(silents); // remove all silents
		succ.andNot(cleansucc); // remove all clean successors
		succ.andNot(nvisited); // remove all successors that already have been visited, in order to prevent endless loops with silents
		
		for (int e = succ.nextSetBit(0); e >= 0; e = succ.nextSetBit(e + 1)) {
			cleansucc.or(getRealSuccessors(pessem, e, nvisited));
		}
		
		return cleansucc;
	}
	
	private BitSet getAllCausesOf(NewUnfoldingPESSemantics<Integer> pessem, Map<Integer, BitSet> correspondings, int event, BitSet visited) {
		BitSet pred = new BitSet();
		BitSet cutoffs = new BitSet();
		BitSet realpred = new BitSet();
		
		pred.or(pessem.getCausesOf(event));
					
		realpred.or(pred);
		
		BitSet nvisited = (BitSet)visited.clone();
		nvisited.set(event);
		
		pred.andNot(nvisited);
		if (pred.cardinality() == 0) return realpred;
		
		for (int corr: correspondings.keySet()) {
			if (pred.get(corr)) cutoffs.or(correspondings.get(corr));
		}
		realpred.or(cutoffs);

		nvisited.or(pred);
		
		for (int e = cutoffs.nextSetBit(0); e >= 0; e = cutoffs.nextSetBit(e + 1)) {
			realpred.or(getAllCausesOf(pessem, correspondings, e, nvisited));
		}
		
		return realpred;
	}
	
	private int getRealCorresponding(NewUnfoldingPESSemantics<Integer> pessem, int event) {
		int corr = event;
		
		while (pessem.getCutoffEvents().contains(corr)) {
			corr = pessem.getCorresponding(corr);
		}
		return corr;
	}
	
	private Map<Integer, BitSet> getCorrespondings(NewUnfoldingPESSemantics<Integer> pessem) {
		Map<Integer, BitSet> corresponding = new HashMap<Integer, BitSet>();
		
		for (int c: pessem.getCutoffEvents()) {
			if (!corresponding.containsKey(pessem.getCorresponding(c)))
				corresponding.put(pessem.getCorresponding(c), new BitSet());
			
			corresponding.get(pessem.getCorresponding(c)).set(c);
		}
		
		return corresponding;
	}
	
	private NewUnfoldingPESSemantics<Integer> getPESSemantics(Unfolding2PES pes) {
		PrintStream blackhole = new PrintStream(new ByteArrayOutputStream());
		PrintStream stdout = System.out;
		System.setOut(blackhole);
		
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);

		System.setOut(stdout);
		
		return pessem;
	}
	
	public void findMutualRelations() {
		BitSet relation;
		
		directcausals.clear();
		invdirectcausals.clear();
		transcausals.clear();
		invtranscausals.clear();
		existcausals.clear();
		invexistcausals.clear();
		conflict.clear();
		concurrency.clear();
		loops.clear();
				
		for (BitSet key: combinedPES.keySet()) {
			relation = combinedPES.get(key);
			if (relation.cardinality() == 1) {
				switch (relation.nextSetBit(0)) {
					case 0: 
//						if (dcmap.get(key).cardinality() == pesCount) {
							directcausals.add(key); 
//						}
						break;
					case 1: 
//						if (idcmap.get(key).cardinality() == pesCount) {
							invdirectcausals.add(key); 
//						}
						break;
					case 2: 
//						if (tcmap.get(key).cardinality() == pesCount) {
							transcausals.add(key); 
//						}
						break;
					case 3: 
//						if (itcmap.get(key).cardinality() == pesCount) {
							invtranscausals.add(key); 
//						}
						break;
					case 4: 
						if (cfmap.get(key).cardinality() == pesCount) {
							conflict.add(key); 
						}
						break;
					case 5: 
						if (ccmap.get(key).cardinality() == pesCount) {
							concurrency.add(key); 
						}
						break;
				}
			}
else if (relation.cardinality() > 1) {
				
				// if there is a concurrency relation, check whether there are other relations that come from the same PES. If so: remove that other relation
				if (relation.get(5)) {					
					if (dcmap.containsKey(key) && (dcmap.get(key).equals(ccmap.get(key)))) {
						relation.clear(0);
						dcmap.remove(key);
					}
					if (idcmap.containsKey(key) && (idcmap.get(key).equals(ccmap.get(key)))) {
						relation.clear(1);
						idcmap.remove(key);
					}
					if (tcmap.containsKey(key) && (tcmap.get(key).equals(ccmap.get(key)))) {
						relation.clear(2);
						tcmap.remove(key);
					}
					if (itcmap.containsKey(key) && (itcmap.get(key).equals(ccmap.get(key)))) {
						relation.clear(3);
						itcmap.remove(key);
					}
					if (cfmap.containsKey(key) && (cfmap.get(key).equals(ccmap.get(key)))) {
						relation.clear(4);
						cfmap.remove(key);
					}
					
					// if after removing the other relations the concurrency relation is the only relation, it can be safely added to the concurrency set
					if (relation.cardinality() == 1) {
						concurrency.add(key);
					}
				}
				
				// if there is no PES for which the conflict relation is the only relation for [key], then the conflict relation can be removed
				if (relation.get(4)) {					
					if ((!(dcmap.containsKey(key) && (!dcmap.get(key).intersects(cfmap.get(key))))) && 
							(!(idcmap.containsKey(key) && (!idcmap.get(key).intersects(cfmap.get(key))))) &&
							(!(tcmap.containsKey(key) && (!tcmap.get(key).intersects(cfmap.get(key))))) &&
							(!(itcmap.containsKey(key) && (!itcmap.get(key).intersects(cfmap.get(key))))) &&
							(!(ccmap.containsKey(key) && (!ccmap.get(key).intersects(cfmap.get(key)))))) { 
					
						relation.clear(4);
						cfmap.remove(key);
						conflict.remove(key);
					}
				}
				
				if (relation.get(0)) {
					if (dcmap.get(key).equals(relmap.get(key))) directcausals.add(key);
				}
				else if (relation.get(1)) {
					if (idcmap.get(key).equals(relmap.get(key))) invdirectcausals.add(key);
				}
				else if (relation.get(2)) {
					if (tcmap.get(key).equals(relmap.get(key))) transcausals.add(key);
				}
				else if (relation.get(3)) {
					if (itcmap.get(key).equals(relmap.get(key))) invtranscausals.add(key);
				}
				
				if (!(relation.get(4)) && !(relation.get(5))) { // no conflict and no concurrency, so causality 
					if (relation.get(2)) {
						existcausals.add(key);
					}
					if (relation.get(3)) {
						invexistcausals.add(key);
					}
				}
			}
			
			if (lpmap.containsKey(key) && lpmap.get(key).cardinality() == pesCount) {
				loops.add(key);
			}
		}
		
		for (BitSet dl: dlpmap.keySet()) {
			if (dlpmap.get(dl).cardinality() == relmap.get(dl).cardinality()) {
				directloops.add(dl);
			}
		}
		
		for (BitSet idl: idlpmap.keySet()) {
			if (idlpmap.get(idl).cardinality() == relmap.get(idl).cardinality()) {
				invdirectloops.add(idl);
			}
		}

		for (int sl: sleventmap.keySet()) {
			if (sleventmap.get(sl).cardinality() == pesCount) {
				mslevents.set(sl);
			}
		}
		
		existcausals.addAll(transcausals);
		invexistcausals.addAll(invtranscausals);
	}
	
	public BitSet getBRrel(String e1, String e2) {
		return getBRrel(totalLabels.indexOf(e1), totalLabels.indexOf(e2));
	}
	
	public BitSet getBRrel(int e1, int e2) {
		return combinedPES.get(hash(e1, e2));
	}
	
	public String getLabel(int e) {
		return totalLabels.get(e);
	}
	
	public List<String> getAllLabels() {
		return totalLabels;
	}
	
	public Set<Integer> getSilents() {
		return silents;
	}
	
	public int getSource() {
		return source;
	}
	
	public int getSink() {
		return sink;
	}
	
	public Boolean occursInAllPESs(int label) {
		return (labelmap.get(label).cardinality() == pesCount);
	}
	
	public BitSet getSyncEvents() {
		if (syncevents.cardinality() == 0) fillInSyncEvents();
		return syncevents;
	}
	
	public Set<BitSet> getDirectCausals()    {return dcmap.keySet();}
	public Set<BitSet> getInvDirectCausals() {return idcmap.keySet();}
	public Set<BitSet> getTransCausals()     {return tcmap.keySet();}
	public Set<BitSet> getInvTransCausals()  {return itcmap.keySet();}
	public Set<BitSet> getConflicts()        {return cfmap.keySet();}
	public Set<BitSet> getConcurrents()      {return ccmap.keySet();}
	public Set<BitSet> getLoops()			 {return lpmap.keySet();}
	public Set<BitSet> getDirectLoops()		 {return dlpmap.keySet();}
	public Set<BitSet> getInvDirectLoops()	 {return idlpmap.keySet();}
		
	public Set<BitSet> getMutualDirectCausals()    {return directcausals;}
	public Set<BitSet> getMutualInvDirectCausals() {return invdirectcausals;}
	public Set<BitSet> getMutualTransCausals() 	   {return transcausals;}
	public Set<BitSet> getMutualInvTransCausals()  {return invtranscausals;}
	public Set<BitSet> getMutualExistCausals()	   {return existcausals;}
	public Set<BitSet> getMutualInvExistCausals()  {return invexistcausals;}
	public Set<BitSet> getMutualConflicts() 	   {return conflict;}
	public Set<BitSet> getMutualConcurrents() 	   {return concurrency;}
	public Set<BitSet> getMutualLoops()			   {return loops;}
	public Set<BitSet> getMutualDirectLoops()	   {return directloops;}
	public Set<BitSet> getMutualInvDirectLoops()   {return invdirectloops;}
	
	public BitSet getMutualSelfLoopEvents() {return mslevents;}

	public Set<BitSet> getImmediateResponses() {
		Set<BitSet> immresp = new HashSet<BitSet>();

		Set<BitSet> allrel = relmap.keySet();
		BitSet notpes = new BitSet();
		BitSet relpes = new BitSet();
		boolean exists;
		
		for (BitSet eir: allrel) {
			relpes.clear();
			if (dcmap.containsKey(eir)) relpes.or(dcmap.get(eir));
			if (dlpmap.containsKey(eir)) relpes.or(dlpmap.get(eir));
			
			if (relpes.cardinality() == pesCount) {
				immresp.add(eir);
			}
			else {
				notpes.set(0, pesCount);
				notpes.andNot(relpes);
				exists = false;
				for (int pes = notpes.nextSetBit(0); pes >= 0; pes = notpes.nextSetBit(pes + 1)) {
					if (labelmap.get(eir.nextSetBit(0)).get(pes)) exists = true;
				}
				
				if ((!exists) && (notpes.cardinality() > 0)) immresp.add(eir);
			}
		}
		
		return immresp;
	}
	
	public Set<BitSet> getInvImmediateResponses() {
		Set<BitSet> invimmresp = new HashSet<BitSet>();

		Set<BitSet> allrel = relmap.keySet();
		BitSet notpes = new BitSet();
		BitSet relpes = new BitSet();
		boolean exists;
		
		for (BitSet eir: allrel) {
			relpes.clear();
			if (idcmap.containsKey(eir)) relpes.or(idcmap.get(eir));
			if (idlpmap.containsKey(eir)) relpes.or(idlpmap.get(eir));
			
			if (relpes.cardinality() == pesCount) {
				invimmresp.add(eir);
			}
			else {
				notpes.set(0, pesCount);
				notpes.andNot(relpes);
				exists = false;
				for (int pes = notpes.nextSetBit(0); pes >= 0; pes = notpes.nextSetBit(pes + 1)) {
					if (labelmap.get(eir.previousSetBit(eir.length())).get(pes)) exists = true;
				}
				
				if ((!exists) && (notpes.cardinality() > 0)) invimmresp.add(eir);
			}
		}
		
		return invimmresp;
	}
	
	public Boolean containsRelation(Set<BitSet> relations, int e1, int e2) {
		BitSet r = new BitSet();
		r.set(e1);
		r.set(e2);
		
		return relations.contains(r);
	}
	
	public String getBRmatrix() {
		return getBRmatrix(false);
	}
	
	public String getBRmatrix(Boolean viewLabels) {
		String matrix = "";
		String br = "";
		String spaces = "                  ";
		String lbl;
		
		for (int y = 0; y < totalLabels.size(); y++) {
			if (viewLabels) {
				lbl = totalLabels.get(y) + spaces;
				matrix += lbl.substring(0, spaces.length()) + ": ";
			}
			for (int x = 0; x < totalLabels.size(); x++) {
				if (combinedPES.containsKey(hash(x, y))) {
					br = combinedPES.get(hash(x, y)).toString();
					matrix += br + spaces.substring(0, spaces.length() - br.length()) + " ";
				}
				else {
					matrix += spaces + " ";
				}
			}
			matrix += "\n";
		}
		
		return matrix;
	}
	
	public String getLabelString() {
		return getLabelString(false);
	}
	
	public String getLabelString(Boolean viewFirstLabel) {
		String spaces = "                  ";
		String labels = "";
		String lbl;
		
		if (viewFirstLabel) {
			labels = spaces + "  ";
		}
		
		for (int i = 0; i < totalLabels.size(); i++) {
			lbl = totalLabels.get(i) + spaces;
			labels += lbl.substring(0, spaces.length()) + " "; 
		}
		
		return labels;
	}
	
	public int getPEScount() {
		return pesCount;
	}
	
	private BitSet toBitSet(Collection<Integer> is) {
		BitSet bts = new BitSet();
		
		for (int i: is) bts.set(i);
		
		return bts;
	}
	
	private BitSet hash(int x, int y) {
		BitSet relation = new BitSet();
		relation.set(x);
		relation.set(y);
		return relation;
	}
}
