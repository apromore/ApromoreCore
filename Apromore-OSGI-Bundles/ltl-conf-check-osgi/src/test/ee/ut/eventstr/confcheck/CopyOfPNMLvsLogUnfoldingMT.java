package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Operation;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.ConcurrencyRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.pnml.PNMLReader;

public class CopyOfPNMLvsLogUnfoldingMT {
	Lock lock = new ReentrantLock();
	Condition condition = lock.newCondition();
	ConcurrentHashMap<Integer, List<List<Operation>>> opMap = new ConcurrentHashMap<Integer, List<List<Operation>>>();
	
	AtomicLong traceStartTime = new AtomicLong(0);
	
	AtomicInteger currentSink = new AtomicInteger();
	
	AtomicInteger finishedThreadCount = new AtomicInteger(0);
	
	PESSemantics<Integer> fullLogPesSem;
	DiffVerbalizer<Integer> verbalizer;
	
	final int totalThreadCount = 1;
	
	@Test
	public void test() throws Exception {
		String logfilename = 
				"RoadFines_real"
//				"a.s00000029__s00001170_log"
				;
		
		String pnmlfilename = 
				"MarcelloTrafficFines_loop"
//				"RoadFines_Base"
//				"a.s00000029__s00001170"
				;
		
		String logfiletemplate = 
				"models/pnml/%s.xes.gz"
//				"E:/Documents/NICTA/IBMlogs/%s.xes"
				;
		
		String pnmlfolder = 
//				"logs/fines/"
				"models/pnml/"
//				"E:/Documents/NICTA/IBMlogs/"
				;
		
		long totalStartTime = System.nanoTime();
		
		new Thread(new PSPgenerator(0, logfilename, logfiletemplate, pnmlfilename, pnmlfolder, true)).start();
		for (int i = 1; i < totalThreadCount; i++) {
			new Thread(new PSPgenerator(i, logfilename, logfiletemplate, pnmlfilename, pnmlfolder, false)).start();
		}
		
		// wait for the threads to finish
	    lock.lock();
	    while (finishedThreadCount.get() < totalThreadCount) {
	    	condition.await();
	    }
	    lock.unlock();

	    for (int i = 0; i < totalThreadCount; i++) {
	    	for (List<Operation> op: opMap.get(i)) {
	    		verbalizer.addPSP(op);
	    	}
	    }
	    
//	    PrintStream sout = System.out;
//	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	    PrintStream ps = new PrintStream(baos);
//	    System.setOut(ps);
	    verbalizer.verbalize();
//	    System.out.flush();
//	    System.setOut(sout);
//	    
//	    TreeSet<String> uset = new TreeSet<String>();
//	    
//	    StringTokenizer tokenizer = new StringTokenizer(baos.toString(), "\n");
//	    while (tokenizer.hasMoreElements()) {
//	    	String str = tokenizer.nextToken().replaceAll("\\(\\d+\\)", "");
//	    	uset.add(str);
//	    }
//	    
//	    for (String stmt: uset)
//	    	System.out.println(stmt);
	    
	    System.out.println("Total time: " + (System.nanoTime() - totalStartTime) / 1000 / 1000 + "ms");
	    System.out.println("Trace analysis time: " + (System.nanoTime() - traceStartTime.get()) / 1000 / 1000 + "ms");
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
		
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("pes.dot", pessem.toDot());
		return pessem;
	}
	
	public PrimeEventStructure<Integer> getLogPESExample(String logfilename, String logfiletemplate) throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));
		
		Multimap<String, String> concurrency = HashMultimap.create();
//		concurrency.put("B", "D"); concurrency.put("D", "B");
//		concurrency.put("C", "D"); concurrency.put("D", "C");
//		concurrency.put("E", "D"); concurrency.put("D", "E");
//		concurrency.put("F", "D"); concurrency.put("D", "F");
//		concurrency.put("G", "D"); concurrency.put("D", "G");
//		concurrency.put("H", "D"); concurrency.put("D", "H");
		
//		ConcurrencyRelations alphaRelations = new ConcurrencyRelations() {
//			public boolean areConcurrent(String label1, String label2) {
//				return concurrency.containsEntry(label1, label2);
//			}
//		};
		
		ConcurrencyRelations alphaRelations = new AlphaRelations(log);
		
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		System.out.println("Alpha relations created");
		
		PORuns runs = new PORuns();
		System.out.println("PO runs initialized");
		
		Set<Integer> eventlength = new HashSet<Integer>();
		Set<Integer> succ;
		
		int ntraces = 0;
		
		for (XTrace trace: log) {
//			PORun porun = new AbstractingShort12LoopsPORun(alphaRelations, trace);
			PORun porun = new PORun(alphaRelations, trace);
			
			runs.add(porun);
			
			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());
				
			runs.add(porun);
			
			ntraces++;
//			if (ntraces > 10) break;
		}
		System.out.println("PO runs created");
				
		IOUtils.toFile(logfilename + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(logfilename + "_merged.dot", runs.toDot());
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
		
		return pes;
	}
	
	class PSPgenerator implements Runnable {
		private SinglePORunPESSemantics<Integer> logpessem;
		private PrunedOpenPartialSynchronizedProduct<Integer> psp;
		private PrimeEventStructure<Integer> logpes;
		private NewUnfoldingPESSemantics<Integer> pnmlpes;
		private List<Integer> allSinks;
		private List<List<Operation>> operations = new ArrayList<List<Operation>>();
		
		private int index;
		
		public PSPgenerator(int index, String logfilename, String logfiletemplate, String pnmlfilename, String pnmlfolder, Boolean initialize) throws Exception {
			this.logpes = getLogPESExample(logfilename, logfiletemplate);
			this.pnmlpes = getUnfoldingPESExample(pnmlfilename, pnmlfolder);
			this.index = index;
			
			allSinks = new ArrayList<Integer>(logpes.getSinks());
			if (initialize) {
				currentSink.set(allSinks.size() - 1);
				fullLogPesSem = new PESSemantics<Integer>(logpes);
				verbalizer = new DiffVerbalizer<Integer>(fullLogPesSem, pnmlpes);
			}
		}
		
		@Override
		public void run() {
			int sink;
			int sinkindex;
			double startingTime;
			Boolean finishedSinks = false;
			
			if (traceStartTime.get() == 0) traceStartTime.getAndSet(System.nanoTime());
			while (!finishedSinks) {
				sinkindex = currentSink.getAndDecrement();
				
				if (sinkindex >= 0) {
					sink = allSinks.get(sinkindex);
	
					startingTime = System.nanoTime();
		        	logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);			
					psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, pnmlpes);
					
					psp.perform()
						.prune()
						;
					operations.add(psp.getOperationSequence());
					
					System.out.println(">>> Trace " + sinkindex + ", Sink " + sink + ", Time: " + (System.nanoTime() - startingTime) / 1000000 + ", " + psp.matchings.cost);
				}
				else {
					finishedSinks = true;
				}
			}		
			
			opMap.put(index, operations);
			
			lock.lock();
			condition.signalAll();
			lock.unlock();
			
			finishedThreadCount.incrementAndGet();
		}
	}
}
