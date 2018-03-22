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

public class ProblematicTest {
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
				"model"
				;
		
		String logfiletemplate = 
				"models/RunningExample/%s.mxml"
				;
		
		String bpmnfolder = 
//				"models/RunningExample/"
//				"models/simple/"
//				"models/elementary/"
				"current/"
				;
		
		PrimeEventStructure<Integer> logpes = getPES_Log();//Fig21(); //_Concurrency2();
		NewUnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample(bpmnfilename, bpmnfolder);
		ExpandedPomsetPrefix<Integer> expprefix = new ExpandedPomsetPrefix<Integer>(bpmnpes);
		NewDiffVerbalizer<Integer> verbalizer = new NewDiffVerbalizer<Integer>(new PESSemantics<>(logpes), bpmnpes, expprefix);

		for (Integer sink: logpes.getSinks()) {
			SinglePORunPESSemantics<Integer> logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);			
			PrunedOpenPartialSynchronizedProduct<Integer> psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, bpmnpes);
			psp.perform()
//				.prune()
			;
			IOUtils.toFile("psp.dot", psp.toDot());
			psp.prune();
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
	
	public PrimeEventStructure<Integer> getPES_Log() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(0, 2);
		adj.put(1, 3);
		adj.put(1, 4);
		adj.put(2, 3);
		adj.put(2, 4);
		adj.put(3, 5);
		adj.put(4, 5);
		adj.put(4, 6);
		adj.put(5, 7);
		adj.put(6, 9);
		adj.put(7, 8);
		adj.put(8, 9);


		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(1, 2); conc.put(2, 1);
		conc.put(3, 4); conc.put(4, 3);
		conc.put(3, 6); conc.put(6, 3);
		conc.put(5, 6); conc.put(6, 5);
		conc.put(6, 7); conc.put(7, 6);
		conc.put(6, 8); conc.put(8, 6);

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(9), Arrays.asList("_0_", "X", "A", "B", "D", "Y", "E", "C", "Z", "_1_"), "PES2");
		IOUtils.toFile("logpes.dot", pes.toDot());
		return pes;
	}
}
