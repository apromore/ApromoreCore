package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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

public class FinesTest {
	private SinglePORunPESSemantics<Integer> logpessem;
	private PrunedOpenPartialSynchronizedProduct<Integer> psp;
	private PrimeEventStructure<Integer> logpes;
	private NewUnfoldingPESSemantics<Integer> pnmlpes;
	
	private PESSemantics<Integer> fullLogPesSem;
	private DiffVerbalizer<Integer> verbalizer;
	
	private long totalStartTime;
		
	private PrintStream stdOut = System.out;
	private PrintStream alternateOut; 
	
	@Test
	public void test() throws Exception {
		String pnmlfolder = "models/pnml/";
		String logfiletemplate = pnmlfolder + "%s.xes";
		
        String logfilename ="RoadFines_real";
		String pnmlfilename = "RoadFines_Base.pnml";
		
		ByteArrayOutputStream conversionOutput = new ByteArrayOutputStream();
	    alternateOut = new PrintStream(conversionOutput);
	    System.setOut(alternateOut);
	    
		runTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename);
	}
	
	public void runTest(String pnmlfolder, String logfiletemplate, String pnmlfilename, String logfilename) throws Exception {
		analyzeDifferences(logfilename, logfiletemplate, pnmlfilename, pnmlfolder);
		
		System.setOut(stdOut);
		System.out.println(pnmlfilename + " vs " + logfilename + ".xes:");
		verbalizer.verbalize();
		System.out.println("Total time: " + (System.nanoTime() - totalStartTime) / 1000 / 1000 + "ms");
		System.out.println();
	    System.setOut(alternateOut);
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
		
		Set<String> labels = new HashSet<>();
		for (Transition t: net.getTransitions()) {
			if (!(t.getName().equals("")) && !(t.getName().startsWith("Inv"))) { // get rid of silent transitions
				labels.add(t.getName());
			}
		}
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		
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
		
		return pes;
	}

}
