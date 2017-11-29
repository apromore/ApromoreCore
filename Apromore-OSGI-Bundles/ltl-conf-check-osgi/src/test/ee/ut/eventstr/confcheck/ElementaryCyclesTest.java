package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.UnfoldingPESSemantics;
import ee.ut.graph.ccomp.ElementaryCyclesFinder;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class ElementaryCyclesTest {

	@Test
	public void test() throws Exception {		
		String bpmnfilename = 
//				"noloop"
//				"inner"
//				"outer"
//				"nested"
//				"overlapping"
//				"CP_LGB"
//				"nested2"
//				"cycle10"
//				"crossed"
//				"concurrentcycle"
//				"concurrentcycle2"
//				"concurrentcycle3"
//				"concurrentcycle4"
//				"acyclic15p"
				"multicycle"
//				"multicycle2"
				;
				
		String bpmnfolder = 
//				"models/RunningExample/"
//				"models/simple/"
				"models/"
				;
		
		UnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample(bpmnfilename, bpmnfolder);

		Set<Integer> vertices = new HashSet<Integer>();
		Multimap<Integer, Integer> succs = HashMultimap.create(bpmnpes.getDirectSuccessors());
		Multimap<Integer, Integer> preds = HashMultimap.create(bpmnpes.getDirectPredecessors());
		vertices.addAll(succs.keySet());
		vertices.addAll(succs.values());
		
		Multimap<Integer, Integer> ccpairs = HashMultimap.create();
		for (Integer cutoff: bpmnpes.getCutoffEvents()) {
			Integer corr = bpmnpes.getCorrespondingEvent(cutoff);
			ccpairs.put(corr, cutoff);
			succs.put(cutoff, corr);
			preds.put(corr, cutoff);
			
//			BitSet cutoffLC = bpmnpes.getLocalConfiguration(cutoff);
//			BitSet corrLC = bpmnpes.getLocalConfiguration(corr);
//			if (isSubset(corrLC, cutoffLC)) {
//				BitSet interval = (BitSet) cutoffLC.clone();
//				interval.andNot(corrLC);
////				System.out.println("Interval: " + interval);
//			}
		}
		
		Set<Set<Integer>> cycles = new ElementaryCyclesFinder<Integer>(vertices, succs, preds).getElementaryCycles();
		Multimap<Integer, BitSet> correctedCycles = HashMultimap.create();
		for (Set<Integer> _cycle: cycles) {
			BitSet cycle = new BitSet();
			for (Integer ev: _cycle) cycle.set(ev);
			for (Integer entry: ccpairs.keySet()) {
				if (_cycle.contains(entry)) {
					BitSet intersection = (BitSet)bpmnpes.getLocalConfiguration(entry).clone();
					intersection.and(cycle);
					if (intersection.cardinality() == 1) {
//						System.out.println("This is an entry point: " + intersection + " to cycle " + cycle);
						BitSet correctedCycle = new BitSet();
						for (Entry<Integer, Integer> pair: ccpairs.entries()) {
							Integer corr = pair.getKey();
							Integer cutoff = pair.getValue();
							if (cycle.get(corr) && cycle.get(cutoff)) {
//								System.out.println("\tShould shift: " + pair);								
								correctedCycle.or(bpmnpes.getLocalConfiguration(cutoff));
								correctedCycle.andNot(bpmnpes.getLocalConfiguration(corr));
							}
						}
//						correctedCycle.set(entry);
//						System.out.println(correctedCycle);
						correctedCycles.put(entry, correctedCycle);
					}
				}
			}
		}
		
		System.out.println("===================== SET OF ELEMENTARY CYCLES");
		for (Entry<Integer, BitSet> pair: correctedCycles.entries()) {
			BitSet cycle = pair.getValue();
			System.out.println("Entry event: " + bpmnpes.getLabel(pair.getKey()) + ", " + cycle);
			for (int e = cycle.nextSetBit(0); e >= 0; e = cycle.nextSetBit(e + 1)) {
				System.out.print(bpmnpes.getLabel(e) + "  ");
			}
			System.out.println();
		}
	}
	
	public boolean isSubset(BitSet a, BitSet b) {
		BitSet set = (BitSet)a.clone();
		set.and(b);
		return set.cardinality() == a.cardinality();		
	}

	
	public UnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(folder + filename + ".bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		UnfoldingPESSemantics<Integer> pessem = new UnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("bpmnpes.dot", pessem.toDot());
		return pessem;
	}
}
