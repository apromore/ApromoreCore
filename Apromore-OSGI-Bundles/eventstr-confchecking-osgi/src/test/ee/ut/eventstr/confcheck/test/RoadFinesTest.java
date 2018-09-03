package ee.ut.eventstr.confcheck.test;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
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
import ee.ut.pnml.PNMLReader;

public class RoadFinesTest {
	@Test
	public void test() throws Exception {
		PrimeEventStructure<Integer> logpes = getLogPESExample("RoadFines_real", "models/pnml/%s.xes");
		NewUnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample("MarcelloTrafficFines_loop", "models/pnml/");
		ExpandedPomsetPrefix<Integer> expprefix = new ExpandedPomsetPrefix<Integer>(bpmnpes);
		NewDiffVerbalizer<Integer> verbalizer = new NewDiffVerbalizer<Integer>(new PESSemantics<>(logpes), bpmnpes, expprefix);

		
		for (Integer sink: logpes.getSinks()) {
			SinglePORunPESSemantics<Integer> logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);
			PrunedOpenPartialSynchronizedProduct<Integer> psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, bpmnpes);
			psp.perform().prune();
			verbalizer.addPSP(psp.getOperationSequence());
		}
		
		verbalizer.verbalize();		
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		PetriNet net = PNMLReader.parse(new File(folder + filename + ".pnml"));
		IOUtils.toFile("net.dot", net.toDot());
		
		Set<String> labels = new HashSet<>();
		for (Transition t: net.getTransitions()) {
			if (!t.getName().startsWith("Inv") && !t.getName().startsWith("tau") && !t.getName().startsWith("Tau"))
				labels.add(t.getName());
		}
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
				
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
//		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("bpmnpes.dot", pessem.toDot());
		return pessem;
	}	
	
	public PrimeEventStructure<Integer> getLogPESExample(String logfilename, String logfiletemplate) throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));
		
		ConcurrencyRelations alphaRelations = getIMConcurrencyRelations(log);//getAlphaRelations(log);
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		PORuns runs = new PORuns();

		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace);
						
			runs.add(porun);			
		}

//		IOUtils.toFile(logfilename + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(logfilename + "_merged.dot", runs.toDot());
		
		return PORuns2PES.getPrimeEventStructure(runs, logfilename);
	}
	
	public ConcurrencyRelations getAlphaRelations(XLog log) {
		return new AlphaRelations(log);
	}
	
	public ConcurrencyRelations getIMConcurrencyRelations(XLog log) {
		Multimap<String, String> concurrency = HashMultimap.create();
		concurrency.put("Receive Result Appeal from Prefecture", "Notify Result Appeal to Offender"); concurrency.put("Notify Result Appeal to Offender", "Receive Result Appeal from Prefecture");
		concurrency.put("Receive Result Appeal from Prefecture", "Add penalty"); concurrency.put("Add penalty", "Receive Result Appeal from Prefecture");
		concurrency.put("Add penalty", "Notify Result Appeal to Offender"); concurrency.put("Notify Result Appeal to Offender", "Add penalty");

		concurrency.put("Send Appeal to Prefecture", "Insert Fine Notification"); concurrency.put("Insert Fine Notification", "Send Appeal to Prefecture");
		concurrency.put("Send Appeal to Prefecture", "Receive Result Appeal from Prefecture"); concurrency.put("Receive Result Appeal from Prefecture", "Send Appeal to Prefecture");
		concurrency.put("Send Appeal to Prefecture", "Notify Result Appeal to Offender"); concurrency.put("Notify Result Appeal to Offender", "Send Appeal to Prefecture");
		concurrency.put("Send Appeal to Prefecture", "Add penalty"); concurrency.put("Add penalty", "Send Appeal to Prefecture");
		concurrency.put("Send Appeal to Prefecture", "Send for Credit Collection"); concurrency.put("Send for Credit Collection", "Send Appeal to Prefecture");

		ConcurrencyRelations alphaRelations = new ConcurrencyRelations() {
			public boolean areConcurrent(String label1, String label2) {
				return concurrency.containsEntry(label1, label2);
			}
		};

		return alphaRelations;
	}
}
