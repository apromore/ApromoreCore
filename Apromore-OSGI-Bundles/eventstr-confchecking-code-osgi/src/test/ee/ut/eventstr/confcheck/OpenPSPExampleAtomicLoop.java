package ee.ut.eventstr.confcheck;

//--
//OpenPSP test using base BPMN vs atomic changes in the log
//all changes take place in a loop
//--

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.UnfoldingPESSemantics;
import ee.ut.eventstr.comparison.OpenPartialSynchronizedProduct;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;

public class OpenPSPExampleAtomicLoop {
	
	@Test
	public void test() throws Exception {
		PESSemantics<Integer> pes1 = getLogPES(); // rename this void with _I2, _I3 etc.
		UnfoldingPESSemantics<Integer> pes2 = getUnfoldingPESExample("inner");
		
		OpenPartialSynchronizedProduct<Integer> psp = new OpenPartialSynchronizedProduct<Integer>(pes1, pes2);
		psp.perform()
			.prune()
		;
		
		IOUtils.toFile("psp.dot", psp.toDot());

		psp.analyze();
	}
	
	public UnfoldingPESSemantics<Integer> getUnfoldingPESExample(String name) throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(String.format("models/simple/%s.bpmn", name)));
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
		UnfoldingPESSemantics<Integer> pessem = new UnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile(name+".dot", pessem.toDot());
		return pessem;
	}
	
	public PESSemantics<Integer> getLogPES() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);

		adj.put(3, 4);
		adj.put(3, 7);
		adj.put(3, 16);

		adj.put(4, 5);
		adj.put(5, 6);

		adj.put(7, 8);
		adj.put(7, 11);
		
		adj.put(8, 9);
		adj.put(9, 10);

		adj.put(11, 12);
		adj.put(12, 13);
		adj.put(13, 14);
		adj.put(14, 15);
		
		adj.put(16, 17);
		
		adj.put(17, 18);
		adj.put(17, 21);
		
		adj.put(18, 19);
		adj.put(19, 20);
		
		adj.put(21, 22);
		adj.put(22, 23);
		adj.put(23, 24);
		adj.put(24, 25);
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		List<Integer> sinks = Arrays.asList(6,10,15,20,25);
		
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), sinks, 
					Arrays.asList("_0_", "start", "A", "B", "C", "end", "_1_", "B", "C", "end", "_1_",
							"A", "B", "C", "end", "_1_",
							"A", "B", "C", "end", "_1_", "A", "B", "C", "end", "_1_"
							), "nested");

		IOUtils.toFile("nested2.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
	public PESSemantics<Integer> getLogPES_Outer() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);

		adj.put(3, 4);
		adj.put(3, 7);

		adj.put(4, 5);
		adj.put(5, 6);

		adj.put(7, 8);		
		adj.put(8, 9);
		adj.put(9, 10);
		adj.put(10, 11);

		adj.put(8, 12);
		adj.put(12, 13);
		adj.put(13, 14);
		adj.put(14, 15);
		adj.put(15, 16);
		
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		List<Integer> sinks = Arrays.asList(6,11,16);
		
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), sinks, 
					Arrays.asList("_0_", "start", "A", "B", "C", "end", "_1_", "A", "B", "C", "end", "_1_",
							"A", "B", "C", "end", "_1_"
							), "outer");

		IOUtils.toFile("outer2.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}

	public PESSemantics<Integer> getLogPES_Overlappingloop() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(3, 55);
		
		adj.put(4, 5); 
		adj.put(4, 52);
		
		adj.put(5, 6);
		adj.put(5, 27);
		
		adj.put(6, 7);
		adj.put(6, 24);
		
		adj.put(7, 8);
		adj.put(7, 20);
		
		adj.put(8, 9);
		adj.put(9, 10);
		adj.put(9, 16);
		
		adj.put(10, 11);
		adj.put(11, 12);
		adj.put(12, 13);
		adj.put(13, 14);
		adj.put(14, 15);
		
		adj.put(16, 17);
		adj.put(17, 18);
		adj.put(18, 19);
		
		adj.put(20, 21);
		adj.put(21, 22);
		adj.put(22, 23);
		
		adj.put(24, 25);
		adj.put(25, 26);
		
		adj.put(27, 28);
		adj.put(28, 29);
		adj.put(28, 40);

		adj.put(29, 30);
		adj.put(30, 31);
		adj.put(31, 32);
		adj.put(31, 37);
		
		adj.put(32, 33);
		adj.put(33, 34);
		adj.put(34, 35);
		adj.put(35, 36);
		
		adj.put(37, 38);
		adj.put(38, 39);
		
		adj.put(40, 41);
		adj.put(41, 42);
		adj.put(41, 48);
		
		adj.put(42, 43);
		adj.put(43, 44);
		adj.put(44, 45);
		adj.put(45, 46);
		adj.put(46, 47);
		
		adj.put(48, 49);
		adj.put(49, 50);
		adj.put(50, 51);
		
		adj.put(52, 53);
		adj.put(53, 54);
		
		adj.put(55, 56);
		adj.put(56, 57);
		adj.put(56, 85);
		
		adj.put(57, 58);
		adj.put(57, 82);
		
		adj.put(58, 59);
		adj.put(58, 70);
		
		adj.put(59, 60);
		adj.put(60, 61);
		adj.put(61, 62);
		adj.put(61, 67);
		
		adj.put(62, 63);
		adj.put(63, 64);
		adj.put(64, 65);
		adj.put(65, 66);
		
		adj.put(67, 68);
		adj.put(68, 69);
		
		adj.put(70, 71);
		adj.put(71, 72);
		adj.put(71, 78);
		
		adj.put(72, 73);
		adj.put(73, 74);
		adj.put(74, 75);
		adj.put(75, 76);
		adj.put(76, 77);
		
		adj.put(78, 79);
		adj.put(79, 80);
		adj.put(80, 81);
		
		adj.put(82, 83);
		adj.put(83, 84);
		
		adj.put(85, 86);
		adj.put(86, 87);
		adj.put(87, 88);
		adj.put(87, 98);
		
		adj.put(88, 89);
		adj.put(89, 90);
		adj.put(89, 95);
		
		adj.put(90, 91);
		adj.put(91, 92);
		adj.put(92, 93);
		adj.put(93, 94);
		
		adj.put(95, 96);
		adj.put(96, 97);
		
		adj.put(98, 99);
		adj.put(99, 100);	

		Multimap<Integer, Integer> conc = HashMultimap.create();
		
		List<Integer> sinks = Arrays.asList(15, 19, 23, 26, 36, 39,
											47, 51, 54, 66, 69, 77,
											81, 84, 94, 97, 100);
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), sinks, 
					Arrays.asList("_0_", "start", "A", "B", "C", "B", "C", "B", "A", "B", "A", "B", "C", "D", "end", "_1_"
										, "C", "D", "end", "_1_"
										, "C", "D", "end", "_1_"
										, "D", "end", "_1_"
										, "A", "B", "A", "B", "C", "B", "C", "D", "end", "_1_"
											, "D", "end", "_1_"
											, "C", "B", "A", "B", "C", "D", "end", "_1_"
											, "C", "D", "end", "_1_"
										, "D", "end", "_1_"
										, "A", "B", "C", "B", "A", "B", "C", "B", "C", "D", "end", "_1_"
											, "D", "end", "_1_"
											, "C", "B", "A", "B", "C", "D", "end", "_1_"
											, "C", "D", "end", "_1_"
											, "D", "end", "_1_"
											, "A", "B", "C", "B", "C", "B", "C", "D", "end", "_1_"
												, "D", "end", "_1_"
												, "D", "end", "_1_"

							), "overlappingloop");

		IOUtils.toFile("overlappingloop.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
	
}
