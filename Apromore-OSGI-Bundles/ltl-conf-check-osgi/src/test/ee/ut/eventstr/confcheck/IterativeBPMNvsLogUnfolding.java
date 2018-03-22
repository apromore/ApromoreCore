package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.NOpenPartialSynchronizedProduct;
import ee.ut.mining.log.ConcurrencyRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.AbstractingShortLoopsPORun;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;

public class IterativeBPMNvsLogUnfolding {

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
//				"rulesBPMN_unstructured"
				;
		
		String logfiletemplate = 
				"models/RunningExample/%s.mxml"
				;
		
		String bpmnfolder = 
				"models/RunningExample/"
//				"models/simple/"
//				"models/"
				;
		
		PrimeEventStructure<Integer> logpes = getLogPESExample(logfilename, logfiletemplate);
		NewUnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample(bpmnfilename, bpmnfolder);
		
		int tracecount = 0;
		Boolean problem = false;
		Set<Integer> problematicSinks = new HashSet<Integer>();
		for (Integer sink: logpes.getSinks()) {
			long startingTime = System.nanoTime();
		    
//			problem = createSinglePSPTimed(logpes, bpmnpes, sink);
			createSinglePSP(logpes, bpmnpes, sink);
			
			if (problem) {
				problematicSinks.add(sink);
				System.out.println(">>> Trace " + tracecount + " timed out: added to problematic sinks");
			}
			else {
				System.out.println(">>> Trace " + tracecount + ", Time: " + (System.nanoTime() - startingTime) / 1000 / 1000 + " ms");
			}
			tracecount++;
//			IOUtils.toFile(String.format("psp_%d.dot", sink), psp.toDot());
		}	
		
		IOUtils.toFile("problematicsinks.txt", problematicSinks.toString());
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
		
		System.out.println(pes.getPES().getLabels());
		pes.getPES().printBRelMatrix(System.out);
		
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

		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		System.out.println("Alpha relations created");
		
		PORuns runs = new PORuns();
		System.out.println("PO runs initialized");

		int maxsize = 15;
		int tracecount = 0;
		for (XTrace trace: log) {
//		int initial = 355, num = 3;
//		int initial = 363, num = 4;
//		int initial = 2, num = 10;
//		for (int i = initial; i < initial + num; i++) { XTrace trace = log.get(i);			
			PORun porun = 
//					new PORun(alphaRelations, trace);
					new AbstractingShortLoopsPORun(alphaRelations, trace);
			if(new HashSet<Integer>(porun.asSuccessorsList().values()).size() < maxsize) { 
				runs.add(porun);
				tracecount++;
			}	
		}
		
		System.out.println("PO runs created: " + tracecount + " traces");
				
		IOUtils.toFile(logfilename + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(logfilename + "_merged.dot", runs.toDot());

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
		
		return pes;
	}
	
	public void createSinglePSP(PrimeEventStructure<Integer> logpes, NewUnfoldingPESSemantics<Integer> bpmnpes, int sink) {
		SinglePORunPESSemantics<Integer> logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);
		NOpenPartialSynchronizedProduct<Integer> psp =
			new NOpenPartialSynchronizedProduct<Integer>(logpessem, bpmnpes);
		psp.perform()
			.prune()
		;	
	}
	
	public boolean createSinglePSPTimed(PrimeEventStructure<Integer> logpes, NewUnfoldingPESSemantics<Integer> bpmnpes, int sink) {
		Boolean problem = false;
		ExecutorService executor = Executors.newSingleThreadExecutor();
	    Future<?> future = executor.submit(new Runnable() {
	        @Override
	        public void run() {
	        	SinglePORunPESSemantics<Integer> logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);
				
				NOpenPartialSynchronizedProduct<Integer> psp =
						new NOpenPartialSynchronizedProduct<Integer>(logpessem, bpmnpes);
				psp.perform()
					.prune()
				;	
	        }
	    });

	    try {
	       future.get(10, TimeUnit.SECONDS);
	    }
	    catch(ExecutionException e) {
	    	System.out.println(e.getMessage());
	    }
	    catch(InterruptedException e) {
	    	System.out.println("Error: interrupted");
	    }
	    catch(TimeoutException e) {
	        problem = true;
	    }
	    
	    executor.shutdownNow();
	    return problem;
	}
	
}
