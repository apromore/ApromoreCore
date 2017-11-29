package ee.ut.eventstr.confcheck.test;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.NewDiffVerbalizer;
import ee.ut.eventstr.comparison.ExpandedPomsetPrefix;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.ConcurrencyRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;

public class NewVerbalizerTest {
	@Test
	public void test() throws Exception {
		String logfilename = 
//				"nolooplog"
//				"innerlog"
//				"outerlog"
//				"nestedlog"
//				"overlappinglog"
				"cp"
				;
		
		String bpmnfilename = 
//				"noloop"
//				"inner"
//				"outer"
//				"nested"
//				"overlapping"
//				"concurrency"
//				"withcutoff"
//				"skipandconc"
//				"Fig17b"
//				"sequence"
//				"Fig1"
//				"nonsynchronized"
				"doublecutoff"
				;
		
		String logfiletemplate = 
				"models/RunningExample/%s.mxml"
				;
		
		String bpmnfolder = 
//				"models/RunningExample/"
//				"models/simple/"
				"models/elementary/"
				;
		
		PrimeEventStructure<Integer> logpes = getPES_DoubleCutoffPair(); //Fig21(); //_Concurrency2();
		NewUnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample(bpmnfilename, bpmnfolder);
		ExpandedPomsetPrefix<Integer> expprefix = new ExpandedPomsetPrefix<Integer>(bpmnpes);
		NewDiffVerbalizer<Integer> verbalizer = new NewDiffVerbalizer<Integer>(new PESSemantics<>(logpes), bpmnpes, expprefix);

		for (Integer sink: logpes.getSinks()) {
			SinglePORunPESSemantics<Integer> logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);			
			PrunedOpenPartialSynchronizedProduct<Integer> psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, bpmnpes);
			psp.perform()
				.prune()
			;
//			IOUtils.toFile("psp.dot", psp.toDot());
//			psp.prune();
			verbalizer.addPSP(psp.getOperationSequence());
		}
		
		verbalizer.verbalize();
		
		IOUtils.toFile("expprefix.dot", expprefix.toDot());
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(folder + filename + ".bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		IOUtils.toFile("net.dot", net.toDot());
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
				
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("bpmnpes.dot", pessem.toDot());
		
		System.out.println("Isormorphisms: " + pes.getIsomorphism());
		return pessem;
	}	
	
	public PESSemantics<Integer> getLogPESExample(String logfilename, String logfiletemplate) throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));
		
//		Multimap<String, String> concurrency = HashMultimap.create();
//		concurrency.put("B", "D"); concurrency.put("D", "B");
//		concurrency.put("C", "D"); concurrency.put("D", "C");
//		concurrency.put("E", "D"); concurrency.put("D", "E");
//		concurrency.put("F", "D"); concurrency.put("D", "F");
//		concurrency.put("G", "D"); concurrency.put("D", "G");
//		concurrency.put("H", "D"); concurrency.put("D", "H");
		
		ConcurrencyRelations alphaRelations
			= new AlphaRelations(log);
//			= new ConcurrencyRelations() {
//			public boolean areConcurrent(String label1, String label2) {
//				return concurrency.containsEntry(label1, label2);
//			}
//		};
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		PORuns runs = new PORuns();

		for (XTrace trace: log) {
//		for (int i = 0; i < 2; i++) { XTrace trace = log.get(i);
			PORun porun = new PORun(alphaRelations, trace);
			runs.add(porun);
		}
		
//		IOUtils.toFile(logfilename + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(logfilename + "_merged.dot", runs.toDot());

		return new PESSemantics<Integer>(PORuns2PES.getPrimeEventStructure(runs, logfilename));
	}
	
	public PrimeEventStructure<Integer> getPES_Sequence() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);

		Multimap<Integer, Integer> conc = HashMultimap.create();

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(5), Arrays.asList("start", "A", "B", "C", "D", "end"), "PES2");
		return pes;
	}
	public PrimeEventStructure<Integer> getPES_Concurrency() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(1, 3);
		adj.put(2, 4);
		adj.put(3, 4);
		adj.put(4, 5);

		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(2, 3); conc.put(3, 2);

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(5), Arrays.asList("start", "A", "B", "C", "D", "end"), "PES2");
		return pes;
	}

	public PrimeEventStructure<Integer> getPES_Concurrency2() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(1, 3);
		adj.put(2, 4);
		adj.put(3, 5);
		adj.put(4, 5);

		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(2, 3); conc.put(3, 2);

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(5), Arrays.asList("start", "A", "B", "D", "C", "end"), "PES2");
		return pes;
	}

	public PrimeEventStructure<Integer> getPES_Fig17a() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(1, 3);
		adj.put(1, 4);
		adj.put(2, 5);
		adj.put(4, 5);
		adj.put(3, 6);
		adj.put(4, 6);
		adj.put(5, 7);
		adj.put(6, 8);


		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(2, 4); conc.put(4, 2);
		conc.put(3, 4); conc.put(4, 3);

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(7,8), Arrays.asList("start", "A", "B", "C", "D", "E", "E", "end", "end"), "PES2");
		return pes;
	}
	
	public PrimeEventStructure<Integer> getPES_Fig18a() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(1, 3);
		adj.put(2, 4);
		adj.put(3, 5);
		adj.put(4, 6);
		adj.put(5, 7);
		adj.put(6, 8);


		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(2, 4); conc.put(4, 2);
		conc.put(3, 4); conc.put(4, 3);

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(7,8), Arrays.asList("start", "A", "B", "C", "C", "D", "D", "end", "end"), "PES2");
		return pes;
	}

	public PrimeEventStructure<Integer> getPES_Fig2b() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(0, 2);
		adj.put(1, 3);
		adj.put(1, 4);
		adj.put(2, 4);
		adj.put(3, 5);
		adj.put(3, 6);
		adj.put(4, 7);
		adj.put(4, 8);
		adj.put(5, 9);
		adj.put(6, 10);
		adj.put(7, 11);
		adj.put(8, 12);


		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(1, 2); conc.put(2, 1);

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(9,10,11,12), Arrays.asList("A", "B", "C", "D", "D", "E", "F", "E", "F", "I", "I", "I", "I"), "PES2");
		return pes;
	}
	
	public PrimeEventStructure<Integer> getPES_Fig21() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);

		Multimap<Integer, Integer> conc = HashMultimap.create();

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(5), Arrays.asList("start", "A", "C", "D", "B", "end"), "PES2");
		return pes;
	}
	
	public PrimeEventStructure<Integer> getPES_NewFig() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(1, 3);
		adj.put(1, 4);
		adj.put(2, 5);
		adj.put(2, 6);
		adj.put(3, 6);
		adj.put(3, 7);
		adj.put(4, 7);
		adj.put(4, 8);
		adj.put(5, 9);
		adj.put(5, 12);
		adj.put(6, 11);
		adj.put(6, 12);
		adj.put(7, 13);
		adj.put(7, 14);
		adj.put(8, 13);
		adj.put(8, 10);
		adj.put(9, 11);
		adj.put(10, 14);
		adj.put(11, 15);
		adj.put(12, 16);
		adj.put(13, 17);
		adj.put(14, 18);


		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(2, 3); conc.put(3, 2);
		conc.put(3, 4); conc.put(4, 3);
		conc.put(5, 3); conc.put(3, 5);
		conc.put(5, 6); conc.put(6, 5);
		
		conc.put(8, 3); conc.put(3, 8);
		conc.put(8, 7); conc.put(7, 8);
		conc.put(6, 9); conc.put(9, 6);
		conc.put(7, 10); conc.put(10, 7);
		

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(15,16,17, 18), Arrays.asList("_0_", "A", "B", "F", "C", "D", "G", "G", "D", "E", "E", "H", "H", "H", "H", "_1_", "_1_", "_1_", "_1_"), "PES2");
		
		pes.printBRelMatrix(System.out);
		return pes;
	}
	
	public PrimeEventStructure<Integer> getPES_DoubleCutoffPair() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(1, 4);
		adj.put(1, 6);
		adj.put(2, 3);
		adj.put(3, 10);
		adj.put(3, 11);
		adj.put(4, 5);
		adj.put(5, 9);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 10);
		adj.put(9, 11);
		adj.put(10, 12);
		adj.put(11, 13);


		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(2, 6); conc.put(6, 2);
		conc.put(2, 4); conc.put(4, 2);
		conc.put(2, 7); conc.put(7, 2);
		conc.put(2, 5); conc.put(5, 2);
		conc.put(2, 8); conc.put(8, 2);
		conc.put(2, 9); conc.put(9, 2);
		
		conc.put(3, 6); conc.put(6, 3);
		conc.put(3, 4); conc.put(4, 3);
		conc.put(3, 7); conc.put(7, 3);
		conc.put(3, 5); conc.put(5, 3);
		conc.put(3, 8); conc.put(8, 3);
		conc.put(3, 9); conc.put(9, 3);

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(12,13), Arrays.asList("_0_", "A", "B", "C", "D", "E", "F", "G", "H", "H", "I", "I", "_1_", "_1_"), "PES2");
		IOUtils.toFile("logpes.dot", pes.toDot());
		return pes;
	}
}
