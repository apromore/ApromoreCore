package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.jdom.JDOMException;
import org.junit.Test;

import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.DiffVerbalizer;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Operation;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.pnml.PNMLReader;

public class IBMscalTestMT {
	Lock lock = new ReentrantLock();
	Condition condition = lock.newCondition();
	ConcurrentHashMap<Integer, List<List<Operation>>> opMap;
	
	AtomicInteger currentSink = new AtomicInteger();
	
	AtomicInteger finishedThreadCount = new AtomicInteger(0);
	
	PESSemantics<Integer> fullLogPesSem;
	DiffVerbalizer<Integer> verbalizer;
	
	String summary = "";
	
	final int totalThreadCount = 1;
	
	@Test
	public void test() throws Exception {
		String pnmlfolder = 
//				"E:/Documents/NICTA/IBMlogs/"
				"E:/Documents/NICTA/IBMlogsFixed/fixedLogs/"
				;
		String logfiletemplate = pnmlfolder + "%s.xes";
		
		Set<String> skipFiles = new HashSet<String>();
		skipFiles.add("c.s00000044__s00001066");
//		skipFiles.add("a.s00000163__s00002857");
//		skipFiles.add("a.s00000029__s00001116");
//		skipFiles.add("a.s00000035__s00001435"); //this one works except for the noise files 
		
		runAllTestsInFolder(pnmlfolder, logfiletemplate, skipFiles);
	}
	
	public void runAllTestsInFolder(String pnmlfolder, String logfiletemplate, Set<String> skipFiles) throws Exception {
		Set<String> files = getFileList(pnmlfolder);
		files.removeAll(skipFiles);
		
		String logfilename = "";
		String pnmlfilename = "";
		String newfolder = "";
		
		for (String f: files) {
			pnmlfilename = f + ".pnml";
			logfilename = f + "_log";
			
			newfolder = pnmlfolder + f + "/";
			System.out.println(newfolder);
			new File(newfolder).mkdir();
			String newfile = newfolder + logfilename + ".txt";
			System.out.println(newfile);
			File file = new File(newfile);
			file.createNewFile();
			
			runTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename, newfile);
			runTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename + "_noise05", newfile);
			runTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename + "_noise10", newfile);
			runTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename + "_noise15", newfile);
		}
		
		String summaryfolder = pnmlfolder + "summary/"; 
		String summaryfile = summaryfolder + "summary.txt";
		new File(summaryfolder).mkdir();
		File file = new File(summaryfile);
		file.createNewFile();
		
		PrintWriter smr = new PrintWriter(summaryfile);
		smr.println(summary);
		smr.close();
		
		System.out.println("\nAll tests completed.");
	}
	
	public void runTest(String pnmlfolder, String logfiletemplate, String pnmlfilename, String logfilename, String newfile) throws Exception {
		long totalStartTime = System.nanoTime();
		
		opMap = new ConcurrentHashMap<Integer, List<List<Operation>>>();
		finishedThreadCount.getAndSet(0);
		
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
	    	System.out.println("index: " + i + " " + opMap.size());
	    	for (List<Operation> op: opMap.get(i)) {
	    		verbalizer.addPSP(op);
	    	}
	    }

	    PrintStream stdout = System.out;
	    PrintStream fileout = new PrintStream(new FileOutputStream(newfile, true));
	    System.setOut(fileout);

	    System.out.println(logfilename + ":");
	    verbalizer.verbalize();
		
	    System.out.println("Total time: " + (System.nanoTime() - totalStartTime) / 1000 / 1000 + "ms");
	    System.out.println();
	    
	    summary += pnmlfilename + " vs " + logfilename + ": Total time: " + (System.nanoTime() - totalStartTime) / 1000 / 1000 + "ms\n";
	    
	    System.setOut(stdout);
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		PetriNet net = PNMLReader.parse(new File(folder + filename));
		IOUtils.toFile("net.dot", net.toDot());
		
		Set<String> labels = new HashSet<>();
		for (Transition t: net.getTransitions()) {
			if ((!t.getName().startsWith("decision")) && (!t.getName().startsWith("merge")) &&
					(!t.getName().startsWith("fork")) && (!t.getName().startsWith("join"))) {
				labels.add(t.getName());
			}
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
		System.out.println(logfilename);
		
		AlphaRelations alphaRelations = new AlphaRelations(log);
		
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
			PORun porun = 
					new PORun(alphaRelations, trace)
//					new AbstractingShort12LoopsPORun(alphaRelations, trace);
			;
			
			runs.add(porun);
			
			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());
				
			runs.add(porun);
			
			ntraces++;
			if (ntraces > 10) break;
		}
		System.out.println("PO runs created");
				
		IOUtils.toFile(logfilename + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(logfilename + "_merged.dot", runs.toDot());
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
		
		return pes;
	}
	
	public Set<String> getFileList(String filefolder) {
		File folder = new File(filefolder);
		File[] listOfFiles = folder.listFiles();

		String curfile;
		Set<String> fileset = new HashSet<String>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				curfile = listOfFiles[i].getName();
				curfile = curfile.substring(0, curfile.indexOf(".", 4));
				curfile = curfile.replace("_log", "")
								 .replace("_noise05", "")
								 .replace("_noise10", "")
								 .replace("_noise15", "");
				fileset.add(curfile);
		    }
		}
		
		return fileset;
	}
	
	class PSPgenerator implements Runnable {
		private SinglePORunPESSemantics<Integer> logpessem;
		private PrunedOpenPartialSynchronizedProduct<Integer> psp;
		private PrimeEventStructure<Integer> logpes;
		private NewUnfoldingPESSemantics<Integer> pnmlpes;
		private List<Integer> allSinks;
		private List<List<Operation>> operations = new ArrayList<List<Operation>>();
		
		private String logfilename;
		
		private int index;
		
		public PSPgenerator(int index, String logfilename, String logfiletemplate, String pnmlfilename, String pnmlfolder, Boolean initialize) throws Exception {
			this.logpes = getLogPESExample(logfilename, logfiletemplate);
			this.pnmlpes = getUnfoldingPESExample(pnmlfilename, pnmlfolder);
			this.index = index;
			this.logfilename = logfilename;
			
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
			
			System.out.println("Current logfile under investigation: " + logfilename);
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
