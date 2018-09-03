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

public class IBMstatementCount {
	private SinglePORunPESSemantics<Integer> logpessem;
	private PrunedOpenPartialSynchronizedProduct<Integer> psp;
	private PrimeEventStructure<Integer> logpes;
	private NewUnfoldingPESSemantics<Integer> pnmlpes;
	
	private PESSemantics<Integer> fullLogPesSem;
	private DiffVerbalizer<Integer> verbalizer;
	
	private String summary = "";
	
	@Test
	public void test() throws Exception {
		String pnmlfolder = "E:/Documents/NICTA/IBMlogsFixed/fixedLogs/";
		String logfiletemplate = pnmlfolder + "%s.xes";
		
		Set<String> skipFiles = new HashSet<String>();
		skipFiles.add("c.s00000044__s00001066");
		skipFiles.add("b3.s00000413__s00005846");
		skipFiles.add("a.s00000108__s00003025");
		
		runAllTestsInFolder(pnmlfolder, logfiletemplate, skipFiles);
	}
	
	public void runAllTestsInFolder(String pnmlfolder, String logfiletemplate, Set<String> skipFiles) throws Exception {
		Set<String> files = getFileList(pnmlfolder);
		files.removeAll(skipFiles);
		
		String logfilename, pnmlfilename;
		
		int filecount = 0;
		
		for (String f: files) {
			pnmlfilename = f + ".pnml";
			logfilename = f + "_log";
			
			System.out.print(pnmlfilename + " vs " + logfilename);
			runTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename);
			
			filecount++;
			System.out.println(" - File " + filecount + " out of " + files.size() + " completed.");
		}
		System.out.println();
		System.out.println(summary);
	}
	
	public void runTest(String pnmlfolder, String logfiletemplate, String pnmlfilename, String logfilename) throws Exception {
		PrintStream stdout = System.out;
	    ByteArrayOutputStream verbalization = new ByteArrayOutputStream();
	    PrintStream verbout = new PrintStream(verbalization);
	    System.setOut(verbout);

	    analyzeDifferences(logfilename, logfiletemplate, pnmlfilename, pnmlfolder);

	    verbalizer.verbalize();
		
	    int statementcount = verbalization.toString().replace("In the ", "Task ").split("Task ").length - 1;
	    
	    System.setOut(stdout);	 
	    
	    if (statementcount > 0) {
	    	summary += pnmlfilename + ": " + statementcount + " statements";
	    }
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
			if ((!t.getName().startsWith("decision")) && (!t.getName().startsWith("merge")) &&
					(!t.getName().startsWith("fork")) && (!t.getName().startsWith("join"))) {
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
								 .replace("_noise02", "")
								 .replace("_noise04", "")
								 .replace("_noise06", "")
								 .replace("_noise08", "")
								 .replace("_noise10", "");
				fileset.add(curfile);
		    }
		}
		
		return fileset;
	}

}
