package ee.ut.bpmn.utils;

//import static org.hamcrest.MatcherAssert.assertThat;

//--
//OpenPSP test using base BPMN vs atomic changes in the log
//--

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.UnfoldingPESSemantics;
import ee.ut.eventstr.comparison.OpenPartialSynchronizedProduct;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class OpenPSPExampleAtomic {

	@Test
	public void test() throws Exception {
		PESSemantics<Integer> pes1 = getLogPES_O3(); // rename this void with _I2, _I3 etc.
		UnfoldingPESSemantics<Integer> pes2 = getUnfoldingPESExample();
		
		OpenPartialSynchronizedProduct<Integer> psp = new OpenPartialSynchronizedProduct<Integer>(pes1, pes2);
		psp.perform()
			.prune()
		;
		
		IOUtils.toFile("psp.dot", psp.toDot());
	}
	
	public UnfoldingPESSemantics<Integer> getUnfoldingPESExample() throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("eventstr-confchecking/models/AtomicTest/base.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		Set<String> labels = new HashSet<>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ONEUNFOLDING);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		IOUtils.toFile("bpmnpes.dot", pes.getPES().toDot());
		
		return new UnfoldingPESSemantics<Integer>(pes.getPES(), pes);
	}
	
	public void printBR(Unfolding2PES pes) {
		PrimeEventStructure<Integer> p = pes.getPES();
		
		System.out.println(p.getLabels());
		for (int i = 0; i < p.getBRelMatrix().length; i++) {
			for (int j = 0; j < p.getBRelMatrix().length; j++) {
				System.out.print(p.getBRelMatrix()[i][j] + " ");
			}
			System.out.println();
		}
	}

	public PESSemantics<Integer> getLogPES_base() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(2, 5);
		adj.put(4, 6);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(3, 5);
		conc.put(4, 5);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(10), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "E", "F", "G", "end", "_1_"), "BASE");

		IOUtils.toFile("basepes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_I1() throws Exception {
		//remove F
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(2, 5);
		adj.put(4, 6);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(3, 5);
		conc.put(4, 5);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(9), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "E", "G", "end", "_1_"), "I1");

		IOUtils.toFile("I1pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_I2() throws Exception {
		// duplicate A after E
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(2, 5);
		adj.put(4, 6);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		adj.put(10, 11);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(3, 5);
		conc.put(4, 5);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(11), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "E", "A", "F", "G", "end", "_1_"), "I2");

		IOUtils.toFile("I2pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_I3() throws Exception {
		// substitute F with X
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(2, 5);
		adj.put(4, 6);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(3, 5);
		conc.put(4, 5);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(10), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "E", "X", "G", "end", "_1_"), "I3");

		IOUtils.toFile("I3pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_R1() throws Exception {
		// loop B, C and D
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(2, 5);
		adj.put(4, 6);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(6, 9);
		adj.put(8, 10);
		adj.put(9, 10);
		adj.put(10, 11);
		adj.put(11, 12);
		adj.put(12, 13);
		adj.put(13, 14);
		adj.put(4, 15);
		adj.put(5, 15);
		adj.put(15, 16);
		adj.put(16, 17);
		adj.put(17, 18);
		adj.put(18, 19);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(3, 5);
		conc.put(4, 5);
		conc.put(7, 9);
		conc.put(8, 9);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(14, 19), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "A", "B", "C", "D", "E", "F", "G", "end", "_1_", "E", "F", "G", "end", "_1_"), "R1");

		IOUtils.toFile("R1pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_R2() throws Exception {
		// skip F and G
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(2, 5);
		adj.put(4, 6);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		adj.put(6, 11);
		adj.put(11, 12);

		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(3, 5);
		conc.put(4, 5);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(10, 12), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "E", "F", "G", "end", "_1_", "end", "_1_"), "R2");

		IOUtils.toFile("R2pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_O1() throws Exception {
		// sequentialize B, C and D
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(10), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "E", "F", "G", "end", "_1_"), "O1");

		IOUtils.toFile("O1pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_O2() throws Exception {
		// F and G conditional
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(2, 5);
		adj.put(4, 6);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(6, 10);
		adj.put(10, 11);
		adj.put(11, 12);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(3, 5);
		conc.put(4, 5);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(9, 12), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "E", "F", "end", "_1_", "G", "end", "_1_"), "O2");

		IOUtils.toFile("O2pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_O3() throws Exception {
		// sync B with D
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(2, 5);
		adj.put(3, 4);
		adj.put(5, 4);
		adj.put(4, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(3, 5);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(10), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "E", "F", "G", "end", "_1_"), "O3");

		IOUtils.toFile("O3pes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
}
