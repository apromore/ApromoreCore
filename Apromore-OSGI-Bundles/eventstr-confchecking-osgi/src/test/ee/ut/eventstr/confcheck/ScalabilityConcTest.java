package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
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
import ee.ut.eventstr.comparison.DiffVerbalizer;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Operation;
import ee.ut.mining.log.ConcurrencyRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;

public class ScalabilityConcTest {
	String logfilename;
	String bpmnfilename;
	String logfiletemplate;
	String bpmnfolder;
		
	SinglePORunPESSemantics<Integer> logpessem;
	PrunedOpenPartialSynchronizedProduct<Integer> psp;
	PrimeEventStructure<Integer> logpes; 
	NewUnfoldingPESSemantics<Integer> bpmnpes;
		
	PESSemantics<Integer> fullLogPesSem;
	DiffVerbalizer<Integer> verbalizer;
		
	List<List<Operation>> operations;
	
	@Test
	public void test() throws Exception {
		logfilename ="exclTestLog";
		bpmnfilename = "exclTest";
		logfiletemplate	= "E:/JavaProjects/workspace/FancyJavaUtilities/logs/exclusive/%s.mxml";
		bpmnfolder = "E:/JavaProjects/workspace/FancyJavaUtilities/bpmn/exclusive/";
		
		testSingle(21);
	}
	
	public void testSingle(int combination) throws Exception {
		List<Integer> allSinks; 

		long startingTime;
		
		int sink;
		long pspgenstartingtime;

		startingTime = System.nanoTime();
		logpes = getLogPESExample(logfilename + combination, logfiletemplate);
		bpmnpes = getUnfoldingPESExample(bpmnfilename, bpmnfolder);
		
		fullLogPesSem = new PESSemantics<Integer>(logpes);
		verbalizer = new DiffVerbalizer<Integer>(fullLogPesSem, bpmnpes);
		operations = new ArrayList<List<Operation>>();
			
		allSinks = new ArrayList<Integer>(logpes.getSinks());
			
		pspgenstartingtime = System.nanoTime();
		for (int i = 0; i < allSinks.size(); i++) {
			sink = allSinks.get(i);
	
			logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);			
			psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, bpmnpes);
			psp.perform().prune();
				
			operations.add(psp.getOperationSequence());
		}
			
		for (List<Operation> op: operations) {
			verbalizer.addPSP(op);
		}
		try {
			verbalizer.verbalize();
		}
		catch (Exception e){}
			
		System.out.println("Total time: " + (System.nanoTime() - startingTime) / 1000000);
		System.out.println(combination + " PSP generation time: " + (System.nanoTime() - pspgenstartingtime) / 1000000);
	}
	
	public void testAll() throws Exception {
		for (int log = 21; log < 35; log++) {
			if (log == 25) log = 31;
			testSingle(log);
		}
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(folder + filename + ".bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes()) {
			labels.add(model.getName(node));
		}
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		return pessem;
	}
	
	public PrimeEventStructure<Integer> getLogPESExample(String logfilename, String logfiletemplate) throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));
		
		Multimap<String, String> concurrency = HashMultimap.create();
		
		ConcurrencyRelations alphaRelations = new ConcurrencyRelations() {
			public boolean areConcurrent(String label1, String label2) {
				return concurrency.containsEntry(label1, label2);
			}
		};
		
		PORuns runs = new PORuns();
		System.out.println("PO runs initialized");
		
		Set<Integer> eventlength = new HashSet<Integer>();
		Set<Integer> succ;
		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace);
			
			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());
			
			runs.add(porun);
		}
		System.out.println("PO runs created");
				
		runs.mergePrefix();
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
		
		return pes;
	}
}
