package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Test;

import ee.ut.mining.log.XLogReader;
import ee.ut.pnml.PNMLReader;

public class IBMscalLogInfo {
	private String filedetails = "";
	
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
		
		getAllLogInfo(pnmlfolder, logfiletemplate, skipFiles);
	}
	
	public void getAllLogInfo(String pnmlfolder, String logfiletemplate, Set<String> skipFiles) throws Exception {
		Set<String> files = getFileList(pnmlfolder);
		files.removeAll(skipFiles);
		
		String logfilename = "";
		String pnmlfilename = "";
		
		int filecount = 0;
		for (String f: files) {
			pnmlfilename = f + ".pnml";
			logfilename = f + "_log";
			
			getLogInfo(pnmlfolder, logfiletemplate, pnmlfilename, logfilename);
			String suffix;
//			for (int i = 2; i <= 10; i += 2) {
			for (int i = 5; i <= 20; i += 5) {
				suffix = "0" + i;
				suffix = suffix.substring(suffix.length() - 2, suffix.length());
				getLogInfo(pnmlfolder, logfiletemplate, pnmlfilename, logfilename + "_noise" + suffix);
			}
			
			filecount++;
			System.out.println("File: " + filecount);
		}
		
		String summaryfolder = pnmlfolder + "summary/"; 
		String summaryfile = summaryfolder + "filedetailsfull.txt";
		new File(summaryfolder).mkdir();
		File file = new File(summaryfile);
		file.createNewFile();
		
		PrintWriter smr = new PrintWriter(summaryfile);
		smr.println(filedetails);
		smr.close();
		
		System.out.println("\nInfo of all log files obtained.");
	}
	
	public void getLogInfo(String pnmlfolder, String logfiletemplate, String pnmlfilename, String logfilename) throws Exception {
		PetriNet net = PNMLReader.parse(new File(pnmlfolder + pnmlfilename));
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));
	    
		int transitions = net.getTransitions().size();
		int places = net.getPlaces().size();
		int logsize = log.size();
		int distinctsize = getDistinctTraceCount(log);
		int totaleventcount = getTotalEventCount(log);
		
	    filedetails += "pnml: " + pnmlfilename + ", " 
	    						+ transitions + " transitions, " 
	    						+ places + " places; " 
	    						+ getSplitJoinInfo(net) + ", "
	    						+ "log: " + logfilename + ", " 
	    						+ logsize + " traces, " 
	    						+ distinctsize + " distinct traces, "
	    						+ totaleventcount + " events"
	    						+ "\n";
	}
	
	public String getSplitJoinInfo(PetriNet net) {
		String splitJoinInfo;
		int XORsplitCount = 0;
		int XORjoinCount = 0;
		int ANDsplitCount = 0;
		int ANDjoinCount = 0;
		int XORindegree = 0;
		int XORoutdegree = 0;
		int ANDindegree = 0;
		int ANDoutdegree = 0;
		
		for (Place p: net.getPlaces()) {
			if (p.getIncoming().size() > 1) {
				XORjoinCount++;
				XORindegree += p.getIncoming().size();
			}
			if (p.getOutgoing().size() > 1) {
				XORsplitCount++;
				XORoutdegree += p.getOutgoing().size();
			}
		}
		
		for (Transition t: net.getTransitions()) {
			if (t.getIncoming().size() > 1) {
				ANDjoinCount++;
				ANDindegree += t.getIncoming().size();
			}
			if (t.getOutgoing().size() > 1) {
				ANDsplitCount++;
				ANDoutdegree += t.getOutgoing().size();
			}
		}
		
		if (XORjoinCount > 0) XORindegree /= XORjoinCount;
		if (XORsplitCount > 0) XORoutdegree /= XORsplitCount;
		if (ANDjoinCount > 0) ANDindegree /= ANDjoinCount;
		if (ANDsplitCount > 0) ANDoutdegree /= ANDsplitCount;
		
		splitJoinInfo = "XOR-splits: " + XORsplitCount + ", " + 
						"XOR-outdegree: " + XORoutdegree + ", " + 
						"XOR-joins: " + XORjoinCount + ", " + 
						"XOR-indegree: " + XORindegree + ", " + 
						"AND-splits: " + ANDsplitCount + ", " + 
						"AND-outdegree: " + ANDoutdegree + ", " +
						"AND-joins: " + ANDjoinCount + ", " +
						"AND-indegree: " + ANDindegree
						;
		
		return splitJoinInfo;
	}
	
	private Set<String> getFileList(String filefolder) {
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
	
	private int getDistinctTraceCount(XLog log) {
		Set<List<String>> traces = new HashSet<List<String>>();
		
		for (XTrace trace: log) {
			traces.add(getActivities(trace));
		}
		
		return traces.size();
	}
	
	private List<String> getActivities(XTrace trace) { 
 		List<String> traceActivities = new ArrayList<String>(); 
 		XConceptExtension conceptExt = XConceptExtension.instance();
 		XLifecycleExtension lifecycleExt = XLifecycleExtension.instance();
 		
 		for (XEvent event : trace) { 
 			String actName = ""; 
 			actName += conceptExt.extractName(event); 
 			String trans = lifecycleExt.extractTransition(event); 
 			if (trans != null) { 
 				actName += " " + trans; 
 			} 
 			traceActivities.add(actName); 
 		} 
 		return traceActivities; 
 	} 

	private int getTotalEventCount(XLog log) {
		int eventcount = 0;
		for (XTrace trace: log) {
			eventcount += trace.size();
		}
		return eventcount;
	}
}
