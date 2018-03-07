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

public class OpenPSPSkipTest {

	@Test
	public void test() throws Exception {
		PESSemantics<Integer> pes1 = getLogPES_simpleSeq();
		UnfoldingPESSemantics<Integer> pes2 = getUnfoldingPESExample();
		
		OpenPartialSynchronizedProduct<Integer> psp = new OpenPartialSynchronizedProduct<Integer>(pes1, pes2);
		psp.perform()
			.prune()
		;
		
		IOUtils.toFile("psp.dot", psp.toDot());
	}
	
	public UnfoldingPESSemantics<Integer> getUnfoldingPESExample() throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("E:/JavaProjects/workspace/eventstr-confcheck/eventstr-confchecking/models/AtomicTest/simpleskip.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		Set<String> labels = new HashSet<>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		IOUtils.toFile("bpmnpes.dot", pes.getPES().toDot());
		return new UnfoldingPESSemantics<Integer>(pes.getPES(), pes);
	}

	public PESSemantics<Integer> getLogPES_simpleSeq() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(5, 6);
		adj.put(6, 7);

		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(7), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "end", "_1_"), "BASE");

		IOUtils.toFile("simplesequencepes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_simpleSkip() throws Exception {
		// skip B and C
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(2, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(7, 10), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "end", "_1_", "D", "end", "_1_"), "SimpleSkip");

		IOUtils.toFile("simpleskippes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_shortSkip() throws Exception {
		// skip C
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(3, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(7, 10), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "end", "_1_", "D", "end", "_1_"), "ShortSkip");

		IOUtils.toFile("shortskippes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}

	public PESSemantics<Integer> getLogPES_simpleLoop() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(4, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		adj.put(10, 11);
		adj.put(11, 12);

		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(7, 12), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "end", "_1_", "B", "C", "D", "end", "_1_"), "SimpleLoop");

		IOUtils.toFile("simplelooppes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_extendedLoop() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(4, 8);
		adj.put(8, 9);
		adj.put(9, 10);
		adj.put(10, 11);
		adj.put(11, 12);
		adj.put(9, 13);
		adj.put(13, 14);
		adj.put(14, 15);
		adj.put(15, 16);
		adj.put(16, 17);

		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(7, 12, 17), 
					Arrays.asList("_0_", "start", "A", "B", "C", "D", "end", "_1_", "B", "C", "D", "end", "_1_", "B", "C", "D", "end", "_1_"), "ExtendedLoop");

		IOUtils.toFile("extendedlooppes.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
}
