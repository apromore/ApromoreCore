package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

public class IBMscalPerfTest {
	private SinglePORunPESSemantics<Integer> logpessem;
	private PrunedOpenPartialSynchronizedProduct<Integer> psp;
	private PrimeEventStructure<Integer> logpes;
	private NewUnfoldingPESSemantics<Integer> pnmlpes;
	
	private PESSemantics<Integer> fullLogPesSem;
	private DiffVerbalizer<Integer> verbalizer;
	
	private String summary = "";
	
	private long totalStartTime;
	
	@Test
	public void test() throws Exception {
		String pnmlfolder = 
//				"E:/Documents/NICTA/IBMlogsFixed/fixedLogs/"
//				"E:/Documents/NICTA/IBMlogsFixed/cleanLogs/"
				"E:/Documents/NICTA/IBMlogsFixed/doubleLogs/"
//				"E:/Documents/NICTA/IBMlogsFixed/proportionalLogs/"
				;
		String logfiletemplate = pnmlfolder + "%s.xes";
		
		Set<String> skipFiles = new HashSet<String>();
		skipFiles.add("c.s00000044__s00001066");
		skipFiles.add("b3.s00000413__s00005846");
		skipFiles.add("a.s00000108__s00003025");
		
		skipFiles.add("a.s00000163__s00002857");
		skipFiles.add("a.s00000163__s00002817");
		skipFiles.add("b3.s00000125__s00001623");
		skipFiles.add("b3.s00000167__s00002019");
		skipFiles.add("b3.s00000245__s00005905");
		skipFiles.add("a.s00000197__s00002170");
		skipFiles.add("b3.s00000433__s00004148");
		skipFiles.add("a.s00000163__s00002319");
		skipFiles.add("a.s00000029__s00001116");
		skipFiles.add("b3.s00000175__s00002042");
		skipFiles.add("a.s00000069__s00002359");
		skipFiles.add("a.s00000163__s00002890");
		skipFiles.add("b3.s00000815__s00006174");
		skipFiles.add("b3.s00000215__s00002639");
		skipFiles.add("a.s00000041__s00001526");

		skipFiles.add("a.s00000183__s00003704");
		skipFiles.add("a.s00000055__s00001665");
		skipFiles.add("a.s00000183__s00002123");
		skipFiles.add("b3.s00000901__s00006709");
		skipFiles.add("a.s00000049__s00000950");
		skipFiles.add("a.s00000063__s00002019");
		skipFiles.add("a.s00000045__s00001978");
		skipFiles.add("a.s00000183__s00002519");

		runAllTestsInFolder(pnmlfolder, logfiletemplate, skipFiles);
	}
	
	public void runAllTestsInFolder(String pnmlfolder, String logfiletemplate, Set<String> skipFiles) throws Exception {
		Set<String> files = getFileList(pnmlfolder);
		files.removeAll(skipFiles);
		
		String logfilename = "";
		String pnmlfilename = "";
		
		long averagetime;
		int filecount = 0;
		
		for (String f: files) {
			pnmlfilename = f + ".pnml";
			logfilename = f + "_log";
			
			System.out.print(f + ": ");
			
			averagetime = getAverageTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename);
			summary += "Average total time: " + (averagetime) / 1000 / 1000 + "ms\n";
			System.out.print("normal ");
			
			String suffix;
//			for (int i = 2; i <= 10; i += 2) {	// run from 2 to 10
			for (int i = 5; i <= 20; i += 5) {	// run from 10 to 50
				suffix = "0" + i;
				suffix = suffix.substring(suffix.length() - 2, suffix.length());
				averagetime = getAverageTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename + "_noise" + suffix);
				summary += "Average total time: " + (averagetime) / 1000 / 1000 + "ms\n";
				System.out.print("noise" + suffix + " ");
			}
			
			filecount++;
			System.out.println("- File " + filecount + " out of " + files.size() + " completed.");
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
	
	public long runTest(String pnmlfolder, String logfiletemplate, String pnmlfilename, String logfilename, int iteration) throws Exception {
		PrintStream stdout = System.out;
	    ByteArrayOutputStream verbalization = new ByteArrayOutputStream();
	    PrintStream verbout = new PrintStream(verbalization);
	    System.setOut(verbout);

	    analyzeDifferences(logfilename, logfiletemplate, pnmlfilename, pnmlfolder);

	    verbalizer.verbalize();
		
	    long totaltime = System.nanoTime() - totalStartTime;
	    
	    stdout.print(".");
	    
	    if (iteration == 1) {
//		    int statementcount = verbalization.toString().replace("In the ", "Task ").split("Task ").length - 1;
	    	int statementcount = removeDuplicateStatements(verbalization.toString()).size() - 1;
		    summary += pnmlfilename + " vs " + logfilename + ": " + statementcount + " statements, ";
	    }
	    
	    System.setOut(stdout);	 
	    return totaltime;
	}
	
	public long getAverageTest(String pnmlfolder, String logfiletemplate, String pnmlfilename, String logfilename) throws Exception {
		long totaltime = 0;
		long mintime = Long.MAX_VALUE;
		long maxtime = 0;
		long curtime;
		
		for (int i = 1; i <= 5; i++) {
			curtime = runTest(pnmlfolder, logfiletemplate, pnmlfilename, logfilename, i);
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
		
		System.out.println("Current logfile under investigation: " + logfilename);
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
	
	public Set<String> removeDuplicateStatements(String verbalization) {
		List<String> tempstat = new ArrayList<String>(Arrays.asList(verbalization.replace("In the ", "Task ").split("Task ")));
		Set<String> newstat = new HashSet<String>();
		
	    int parpos, nextpos;
	    String curstat;
	    
	    for (int i = 0; i < tempstat.size(); i++) {
	    	curstat = tempstat.get(i);
	        parpos = curstat.indexOf("(", 1);
	        
	        while (parpos > 0) {
	            nextpos = curstat.indexOf(")", parpos);
	            curstat = curstat.substring(0, parpos - 1) + curstat.substring(nextpos);
	            
	            parpos = curstat.indexOf("(", parpos);
	        }
	        tempstat.set(i, curstat);
	    }
	
		newstat.addAll(tempstat);
		return newstat;
	}
	
	public Set<String> getFileList(String filefolder) {
		File folder = new File(filefolder);
		File[] listOfFiles = folder.listFiles();

		String curfile;
		Set<String> fileset = new HashSet<String>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				curfile = listOfFiles[i].getName();
				if (curfile.endsWith(".pnml")) fileset.add(curfile.substring(0, curfile.indexOf(".", 4)));
		    }
		}
		
		return fileset;
	}

}
