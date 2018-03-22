package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.jdom.JDOMException;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.DiffVerbalizer;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct;
import ee.ut.mining.log.ConcurrencyRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.AbstractingShort12LoopsPORun;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.pnml.PNMLReader;

public class PNMLvsLogUnfolding {
	
	final boolean VERBALIZING = true;
	
	@Test
	public void test() throws Exception {
		String logfilename = 
				"RoadFines_real"
				;
		
		String pnmlfilename = 
				"RoadFines_Base"
				;
		
		String logfiletemplate = 
				"models/pnml/%s.xes.gz"
				;
		
		String bpmnfolder = 
				"models/pnml/"
				;
		
		long startingTime = System.nanoTime();
		
		SinglePORunPESSemantics<Integer> logpessem;
		PrunedOpenPartialSynchronizedProduct<Integer> psp;
		PrimeEventStructure<Integer> logpes = getLogPESExample(logfilename, logfiletemplate);
		NewUnfoldingPESSemantics<Integer> pnmlpes = getUnfoldingPESExample(pnmlfilename, bpmnfolder);
		
		PESSemantics<Integer> fullLogPesSem = new PESSemantics<Integer>(logpes);
		DiffVerbalizer<Integer> verbalizer = new DiffVerbalizer<Integer>(fullLogPesSem, pnmlpes);
		
		List<Integer> allSinks = new ArrayList<Integer>(logpes.getSinks());
		int sink;
		int tracecount = 0;
		System.out.println(allSinks.size());
		
		for (int i = 0; i < allSinks.size(); i++) {
			sink = allSinks.get(i);

			startingTime = System.nanoTime();
        	logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);			
			psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, pnmlpes);
			tracecount++;
			psp.perform().prune();
			
			if (VERBALIZING)
				verbalizer.addPSP(psp.getOperationSequence());
			
			System.out.println(">>> Trace " + tracecount + ", Sink " + sink + ", Time: " + (System.nanoTime() - startingTime) / 1000000 + ", " + psp.matchings.cost);
		}
		
		if (VERBALIZING)
			verbalizer.verbalize();
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		PetriNet net = PNMLReader.parse(new File(folder + filename + ".pnml"));
		IOUtils.toFile("net.dot", net.toDot());
		
		Set<String> labels = new HashSet<>();
		for (Transition t: net.getTransitions()) {
			if (!t.getName().startsWith("Inv"))
				labels.add(t.getName());
		}
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("pes.dot", pessem.toDot());
		return pessem;
	}
	
	public PrimeEventStructure<Integer> getLogPESExample(String logfilename, String logfiletemplate) throws Exception {
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
		
		Set<Integer> eventlength = new HashSet<Integer>();
		Set<Integer> succ;
		for (XTrace trace: log) {
//		int initial = 355, num = 8;
//		int initial = 363, num = 4;
//		int initial = 0, num = 10;
//		for (int i = initial; i < initial + num; i++) { XTrace trace = log.get(i);			
			PORun porun =
					new AbstractingShort12LoopsPORun(alphaRelations, trace);
//					new PORun(alphaRelations, trace);
			runs.add(porun);
			
			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());
			
			runs.add(porun);
		}
		System.out.println("PO runs created");
				
		IOUtils.toFile(logfilename + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(logfilename + "_merged.dot", runs.toDot());
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
		
		return pes;
	}
}
