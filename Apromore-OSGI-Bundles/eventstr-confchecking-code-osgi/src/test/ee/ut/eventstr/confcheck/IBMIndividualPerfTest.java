package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jdom.JDOMException;
import org.junit.Test;

import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.DiffVerbalizer;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.pnml.PNMLReader;

public class IBMIndividualPerfTest {
	private SinglePORunPESSemantics<Integer> logpessem;
	private PrunedOpenPartialSynchronizedProduct<Integer> psp;
	private PrimeEventStructure<Integer> logpes;
	private NewUnfoldingPESSemantics<Integer> pnmlpes;
	
	private PESSemantics<Integer> fullLogPesSem;
	private DiffVerbalizer<Integer> verbalizer;
	
	private boolean createNewDot;
	private long totalStartTime;
	
	private final String basefolder = "E:/Documents/NICTA/IBMlogsFixed/";
	
	@Test
	public void test() throws Exception {
		String pnmlfolder = basefolder + "fixedLogs/";
		String logfiletemplate = pnmlfolder + "%s.xes";
		
		String filename = 
//				"a.s00000045__s00001886"	// 17 statements
//				"a.s00000045__s00001904"	// 11 statements
//				"a.s00000035__s00001435"	// 0 statements
//				"b3.s00000413__s00005846";	// 35000 traces
				"b3.s00000185__s00002121";	// 1 statement
				;
		createNewDot = true;
		
		String pnmlfilename = filename + ".pnml";
		String logfilename = filename + "_log";
		
		long totaltime = runTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename);
		
		System.out.println("Total time: " + (totaltime) / 1000 / 1000 + "ms");
	}
	
	public long runTest(String pnmlfolder, String logfiletemplate, String pnmlfilename, String logfilename) throws Exception {
		analyzeDifferences(logfilename, logfiletemplate, pnmlfilename, pnmlfolder);
		verbalizer.verbalize();
		
	    return System.nanoTime() - totalStartTime;
	}
	
	public long getAverageTest(String pnmlfolder, String logfiletemplate, String pnmlfilename, String logfilename) throws Exception {
		long totaltime = 0;
		long mintime = Integer.MAX_VALUE;
		long maxtime = 0;
		long curtime;
		
		for (int i = 1; i <= 5; i++) {
			curtime = runTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename);
			totaltime += curtime;
			if (curtime < mintime) mintime = curtime;
			if (curtime > maxtime) maxtime = curtime;
		}
		return (totaltime - mintime - maxtime) / 3;
	}
	
	public void analyzeDifferences(String logfilename, String logfiletemplate, String pnmlfilename, String pnmlfolder) throws Exception {
		logpes = getLogPESExample(logfilename, logfiletemplate);
		pnmlpes = getUnfoldingPESExample(pnmlfilename, pnmlfolder);
		fullLogPesSem = new PESSemantics<Integer>(logpes);
		verbalizer = new DiffVerbalizer<Integer>(fullLogPesSem, pnmlpes);
		
		for (int sink: logpes.getSinks()) {
	       	logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);			
			psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, pnmlpes);
				
			psp.perform()
				.prune()
			;
			verbalizer.addPSP(psp.getOperationSequence());				
		}
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		PetriNet net = PNMLReader.parse(new File(folder + filename));
		String dotfile = net.toDot().replace("fixedsize width", "width").replace("\".", "\"0.");	// repairing syntax errors in dotfile
	
		if (createNewDot) {
			String debugfolder = basefolder + "debugging/";
			String debugfile = debugfolder + filename.replace(".pnml", "") + ".dot";
			new File(debugfolder).mkdir();
			new File(debugfile).createNewFile();
			
			PrintWriter smr = new PrintWriter(debugfile);
			smr.println(dotfile);
			smr.close();
		}
		
		Set<String> labels = new HashSet<>();
		for (Transition t: net.getTransitions()) {
			if ((!t.getName().startsWith("decision")) && (!t.getName().startsWith("merge")) &&
					(!t.getName().startsWith("fork")) && (!t.getName().startsWith("join"))) {
				labels.add(t.getName());
			}
		}
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		
		String debugfolder = basefolder + "debugging/";
		String debugfile = debugfolder + filename + "_pnmlpes.dot";
		new File(debugfolder).mkdir();
		new File(debugfile).createNewFile();
		
		PrintWriter smr = new PrintWriter(debugfile);
		smr.println(pessem.toDot());
		smr.close();
		
		
		return pessem;
	}
	
	public PrimeEventStructure<Integer> getLogPESExample(String logfilename, String logfiletemplate) throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));
		
		totalStartTime = System.nanoTime();
		
		AlphaRelations alphaRelations = new AlphaRelations(log);
		
		PORuns runs = new PORuns();

		Set<Integer> eventlength = new HashSet<Integer>();
		Set<Integer> succ;
		
		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace);
			
			runs.add(porun);
			
			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());
				
			runs.add(porun);
		}
		runs.mergePrefix();
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
		
		String debugfolder = basefolder + "debugging/";
		String debugfile = debugfolder + logfilename + "_pes.dot";
		new File(debugfolder).mkdir();
		new File(debugfile).createNewFile();
		
		PrintWriter smr = new PrintWriter(debugfile);
		smr.println(pes.toDot());
		smr.close();
		
		return pes;
	}

}
