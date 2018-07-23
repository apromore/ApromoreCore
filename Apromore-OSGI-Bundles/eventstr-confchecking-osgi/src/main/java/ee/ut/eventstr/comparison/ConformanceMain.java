/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package ee.ut.eventstr.comparison;

import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.pnml.PNMLReader;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.*;
import java.util.*;

public class ConformanceMain {
	private SinglePORunPESSemantics<Integer> logpessem;
	private PrunedOpenPartialSynchronizedProduct<Integer> psp;
	private PrimeEventStructure<Integer> logpes;
	private NewUnfoldingPESSemantics<Integer> pnmlpes;
	private ExpandedPomsetPrefix<Integer> expprefix;

	private PESSemantics<Integer> fullLogPesSem;
	private NewDiffVerbalizer<Integer> verbalizer;
	
	private long totalStartTime;
	
	private static final String version = "0.1";
	
	public static void main(String[] args) {
		// parameters: 
		// folder, model, log, outputfile, [silentlistfile]
		
		ConformanceMain main = new ConformanceMain();
		
		String folder;
		int errorArg;
		
		try {			
			if (args.length == 0) {
				System.out.println("ProConformance");
				System.out.println();
				System.out.println("Version: " + version);
				System.out.println("Build date: " + "22-12-2015");
				System.out.println();

				System.out.println("Developed by:");
				System.out.println("Dr L. Garcia-Banuelos");
				System.out.println("Dr N.R.T.P. van Beest");
			}
			else if (args.length == 4) {
				errorArg = main.checkAllArgs(args);
				if (errorArg == -1) {
					folder = args[0];
					if (!folder.endsWith("\\")) folder += "\\";
					main.runTest(folder, args[1], args[2], args[3], new HashSet<String>());
				}
				else {
					main.displayError(errorArg, args);
				}
			}
			else if (args.length == 5) {
				errorArg = main.checkAllArgs(args);
				if (errorArg == -1) {
					folder = args[0];
					if (!folder.endsWith("\\")) folder += "\\";
					main.runTest(folder, args[1], args[2], args[3], main.getSilents(folder, args[4]));
				}
				else {
					main.displayError(errorArg, args);
				}
			}
			else {
				System.out.println("Wrong number of arguments: ");
				System.out.println("foldername pnmlfile logfile outputfile [silentslistfile]");
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void displayError(int errorArg, String[] args) {
		if (errorArg == 0) {
			System.out.println("Error: path " + args[errorArg] + " does not exist");
		}
		else {
			System.out.println("Error: file " + args[errorArg] + " does not exist");
		}
	}
	
	private int checkAllArgs(String[] args) {
		String folder = args[0];
		if (!folder.endsWith("\\")) folder += "\\";
		
		if (!checkFileExist(folder)) return 0;
		
		if (!checkFileExist(folder + args[1])) return 1;
		if (!checkFileExist(folder + args[2])) return 2;
		
		if (args.length == 5) {
			if (!checkFileExist(folder + args[4])) return 4;	
		}
		
		return -1;
	}
	
	private boolean checkFileExist(String file) {
		return new File(file).exists();
	}
		
	private void runTest(String folder, String pnmlfilename, String logfilename, String outputfile, Set<String> silents) throws Exception {
		PrintStream stdout = System.out;
		ByteArrayOutputStream verbalization = new ByteArrayOutputStream();
	    PrintStream verbout = new PrintStream(verbalization);
	    System.setOut(verbout);

	    analyzeDifferences(folder, pnmlfilename, logfilename, silents);

		verbalizer.verbalize();
			
		long totaltime = System.nanoTime() - totalStartTime;
		    
		String statements = verbalization.toString();
		statements = statements.substring(statements.indexOf("================") + 17);
		Set<String> statementSet = removeDuplicateStatements(statements);
		statementSet.remove("");
		
		System.setOut(stdout);	 
		
		if (statementSet.size() == 1) {
			System.out.println("1 difference found");
		}
		else {
			System.out.println(statementSet.size() + " differences found");
		}
		
		PrintWriter writer = new PrintWriter(folder + outputfile, "UTF-8");
		writer.println("Differences between " + pnmlfilename + " and " + logfilename + ":");
		writer.println(statements);
		writer.close();
		
		System.out.println("Model - Log comparison finished!");
		System.out.println("Total time: " + totaltime / 1000 / 1000 + "ms");
	}
		
	private void analyzeDifferences(String folder, String pnmlfilename, String logfilename, Set<String> silents) throws Exception {
		logpes = getLogPESExample(folder, logfilename);
		pnmlpes = getUnfoldingPESExample(folder, pnmlfilename, silents);
		expprefix = new ExpandedPomsetPrefix<Integer>(pnmlpes);
		fullLogPesSem = new PESSemantics<Integer>(logpes);
		verbalizer = new NewDiffVerbalizer<Integer>(fullLogPesSem, pnmlpes, expprefix);
			
		for (int sink: logpes.getSinks()) {
	       	logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);			
			psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, pnmlpes);
			
			psp.perform().prune()
			;
			verbalizer.addPSP(psp.getOperationSequence());				
		}
	}
		
	private NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String folder, String filename, Set<String> silents) throws Exception {
		PetriNet net = PNMLReader.parse(new File(folder + filename));
			
		Set<String> labels = new HashSet<>();
		for (Transition t: net.getTransitions()) {
			if (!silents.contains(t.getName())) {
				labels.add(t.getName());
			}
		}
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA, new HashSet<String>());
		unfolder.computeUnfolding();

		HashMap<String, String> map = new HashMap<>();
		for(Transition t : unfolder.getUnfoldingAsPetriNet().getTransitions())
			map.put(t.getName(), t.getName());
		
		Unfolding2PES pes = new Unfolding2PES(unfolder, labels, map);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		return pessem;
	}
		
	private PrimeEventStructure<Integer> getLogPESExample(String folder, String logfilename) throws Exception {
		XLog log = XLogReader.openLog(folder + logfilename);
			
		totalStartTime = System.nanoTime();
		
		AlphaRelations alphaRelations = new AlphaRelations(log);
			
		PORuns runs = new PORuns();

		Set<Integer> eventlength = new HashSet<Integer>();
		Set<Integer> succ;
		int i = 0;

		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace, (i++) + "");
			
			runs.add(porun);
			
			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());
					
			runs.add(porun);
		}
			
		runs.mergePrefix();
			
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
			
		return pes;
	}
	
	private Set<String> removeDuplicateStatements(String verbalization) {
		List<String> tempstat = new ArrayList<String>(Arrays.asList(verbalization.split("\n")));
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
	
	private Set<String> getSilents(String folder, String filename) throws Exception {
		Set<String> silents = new HashSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(folder + filename));
		String line;
		while ((line = br.readLine()) != null) {
			silents.add(line);
		}
		br.close();
		
		return silents;
	}

//	public void printSilents() throws Exception {
//		String folder = "C:/Users/Nick/Dropbox/icse2016/tool/";
//		String filename = "example.pnml";
//		
//		PetriNet net = PNMLReader.parse(new File(folder + filename));
//		
//		for (Transition t: net.getTransitions()) {
//			if ((t.getName().startsWith("decision")) || (t.getName().startsWith("merge")) ||
//					(t.getName().startsWith("fork")) || (t.getName().startsWith("join"))) {
//				System.out.println(t.getName());
//			}
//		}
//	}
}
