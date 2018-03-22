package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import utilities.PESViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.comparison.NewOpenPartialSynchronizedProduct;
import ee.ut.mining.log.ConcurrencyRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;

public class NewBPMNvsLogUnfolding {

	@Test
	public void test() throws Exception {
		String logfilename = 
//				"nolooplog"
//				"innerlog"
//				"outerlog"
//				"nestedlog"
//				"overlappinglog"
				"cpreal"
				;
		
		String bpmnfilename = 
//				"noloop"
//				"inner"
//				"outer"
//				"nested"
//				"overlapping"
				"cp_lgb"
				;
		
		String logfiletemplate = 
				"models/RunningExample/%s.mxml"
				;
		
		String bpmnfolder = 
				"models/RunningExample/"
//				"models/simple/"
				;
		
		long startingTime = System.nanoTime();
		
		PESSemantics<Integer> logpes = getLogPESExample(logfilename, logfiletemplate);
		NewUnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample(bpmnfilename, bpmnfolder);
		
		NewOpenPartialSynchronizedProduct<Integer> psp = new NewOpenPartialSynchronizedProduct<Integer>(logpes, bpmnpes);
		psp.perform()
			.prune()
			;

		System.out.println(">>> Time: " + (System.nanoTime() - startingTime) / 1000 / 1000 + " ms");

		IOUtils.toFile("psp.dot", psp.toDot());
//		psp.verbalize(new HashMap<>());

	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(folder + filename + ".bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		labels.remove("start");
//		labels.remove("AND2");
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("bpmnpes.dot", pessem.toDot());
//		IOUtils.toFile("bpmnpes.txt", PESViewer.getStringMatrix(pes.getPES()));
		Set<String> relevantLabels = new HashSet<String>();
		relevantLabels.add("B");
		relevantLabels.add("F");
		IOUtils.toFile("bpmnpes.txt", PESViewer.getRelevantStringMatrix(pes.getPES(), relevantLabels, pes.getPES().getDirectCausalRelations(), null, true, false, false));
		return pessem;
	}	
	
	public PESSemantics<Integer> getLogPESExample(String logfilename, String logfiletemplate) throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));
		
		Multimap<String, String> concurrency = HashMultimap.create();
		concurrency.put("B", "D"); concurrency.put("D", "B");
		concurrency.put("C", "D"); concurrency.put("D", "C");
		concurrency.put("E", "D"); concurrency.put("D", "E");
		concurrency.put("F", "D"); concurrency.put("D", "F");
		concurrency.put("G", "D"); concurrency.put("D", "G");
		concurrency.put("H", "D"); concurrency.put("D", "H");
		
		ConcurrencyRelations alphaRelations = new ConcurrencyRelations() {
			public boolean areConcurrent(String label1, String label2) {
				return concurrency.containsEntry(label1, label2);
			}
		};

		
//		AlphaRelations alphaRelations = new AlphaRelations(log);
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		System.out.println("Alpha relations created");
		
		PORuns runs = new PORuns();
		System.out.println("PO runs initialized");

//		for (XTrace trace: log) {
//		int initial = 355, num = 5;
		int initial = 648, num = 1;
//		int initial = 0, num = 2;
		for (int i = initial; i < initial + num; i++) { XTrace trace = log.get(i);			
			PORun porun = new PORun(alphaRelations, trace);
			runs.add(porun);
		}
		System.out.println("PO runs created");
		
		System.out.println("Sources: " + runs.getSources().size());	
		System.out.println("Sinks: " + runs.getSinks().size());
		
		IOUtils.toFile(logfilename + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(logfilename + "_merged.dot", runs.toDot());
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
//		IOUtils.toFile(logfilename + "_pes.txt", PESViewer.getStringMatrix(pes));
		Set<String> relevantLabels = new HashSet<String>();
		relevantLabels.add("B");
		relevantLabels.add("F");
		IOUtils.toFile(logfilename + "_pes.txt", PESViewer.getRelevantStringMatrix(pes, relevantLabels, pes.getDirectCausalRelations(), null, true, false, false));
		
		return new PESSemantics<Integer>(pes);
	}
}
