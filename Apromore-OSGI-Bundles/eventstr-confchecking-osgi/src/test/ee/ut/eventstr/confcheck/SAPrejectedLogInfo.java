package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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

public class SAPrejectedLogInfo {
	private String filedetails = "";
	
	@Test
	public void test() throws Exception {
		String pnmlfolder = 
//				"E:/Documents/NICTA/SAPlogs/sap_rm_sound/"
				"E:/Documents/NICTA/SAPlogs/sap_tauless/"
				;
		String logfiletemplate = pnmlfolder + "%s.xes";

		Set<String> problemfiles = new HashSet<String>();
		problemfiles.add("1Ex_e58p");
//		problemfiles.add("1An_kr8w_1");	// redundant
//		problemfiles.add("1Ku_acul");	// redundant
		problemfiles.add("1Un_j7jq");
		
		Set<String> slowfiles = new HashSet<String>();
		slowfiles.add("1Ex_ea41");	// very slow after 5% noise
		slowfiles.add("1An_l51v");	// still very slow
		slowfiles.add("1Ex_e43l_2");// still very slow
		slowfiles.add("1Un_k54y");	// could be reasonable
		slowfiles.add("1Un_k9yk");	// very slow on 20% noise
		slowfiles.add("1Un_jyvy");	// might be doable overnight, stuck after 10%
		slowfiles.add("1Er_j5sl");	// still very slow
		
		Set<String> emptyfiles = new HashSet<String>();
		emptyfiles.add("1Er_ixso_1");
		
		Set<String> skipfiles = new HashSet<String>();
		skipfiles.addAll(problemfiles);
		skipfiles.addAll(slowfiles);
//		skipfiles.addAll(emptyfiles);
		
		Set<String> files = getFileList(pnmlfolder);
		limitSize(logfiletemplate, files, 2561, Integer.MAX_VALUE);
		
		getAllLogInfo(pnmlfolder, logfiletemplate, files, "rejectedfiledetailslarge.txt");
		getAllLogInfo(pnmlfolder, logfiletemplate, skipfiles, "rejectedfiledetails.txt");
	}
	
	public void limitSize(String logfiletemplate, Set<String> files, int minsize, int maxsize) {
		Set<String> wrongsizedfiles = new HashSet<String>();
		long fsize;
		for (String f: files) {
			fsize = new File(String.format(logfiletemplate, f + "_log")).length() / 1024;
			if ((fsize > maxsize) || (fsize < minsize)) {
				wrongsizedfiles.add(f);
			}
		}
		files.removeAll(wrongsizedfiles);
	}
	
	public void showFileSizes(String logfiletemplate, Set<String> files) {
		long fsize;
		for (String f: files) {
			fsize = new File(String.format(logfiletemplate, f + "_log")).length() / 1024;
			System.out.println(f + "_log: " + fsize + " kb");
		}
	}
	
	public void getAllLogInfo(String pnmlfolder, String logfiletemplate, Set<String> files, String summaryfile) throws Exception {
		String logfilename = "";
		String pnmlfilename = "";
		
		int filecount = 0;
		for (String f: files) {
			pnmlfilename = f + ".pnml";
			logfilename = f + "_log";
			
			getLogInfo(pnmlfolder, logfiletemplate, pnmlfilename, logfilename);
			String suffix;
			for (int i = 5; i <= 20; i += 5) {
				suffix = "0" + i;
				suffix = suffix.substring(suffix.length() - 2, suffix.length());
				getLogInfo(pnmlfolder, logfiletemplate, pnmlfilename, logfilename + "_noise" + suffix);
			}
			
			filecount++;
			System.out.println("File: " + filecount);
		}
		
		String summaryfolder = pnmlfolder + "summary/"; 
		summaryfile = summaryfolder + summaryfile;
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
		
	    filedetails += "pnml: " + pnmlfilename + "\t" 
	    						+ transitions + " transitions\t" 
	    						+ places + " places\t" 
	    						+ getSplitJoinInfo(net) + "\t"
	    						+ "log: " + logfilename + "\t" 
	    						+ logsize + " traces\t" 
	    						+ distinctsize + " distinct traces\t "
	    						+ totaleventcount + " events\t"
	    						+ getFileSize(String.format(logfiletemplate, logfilename))
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
		
		splitJoinInfo = "XOR-splits: " + XORsplitCount + "\t" + 
						"XOR-outdegree: " + XORoutdegree + "\t" + 
						"XOR-joins: " + XORjoinCount + "\t" + 
						"XOR-indegree: " + XORindegree + "\t" + 
						"AND-splits: " + ANDsplitCount + "\t" + 
						"AND-outdegree: " + ANDoutdegree + "\t" +
						"AND-joins: " + ANDjoinCount + "\t" +
						"AND-indegree: " + ANDindegree
						;
		
		return splitJoinInfo;
	}
	
	public String getFileSize(String filename) {
		try {
			double filesize = Files.size(new File(filename).toPath()) / 1024.0; 
			if (filesize > 1024) {
				return String.format("%.5g", filesize / 1024.0) + " MB";
			}
			else {
				return String.format("%.5g", filesize) + " KB";
			}
		} catch (IOException e) {}
		
		return "0 KB";
	}
	
	public Set<String> getFileList(String filefolder) {
		File folder = new File(filefolder);
		List<String> fileList = new ArrayList<String>(Arrays.asList(folder.list()));
		
		String curfile;
		Set<String> fileset = new HashSet<String>();
		
		for (int i = 0; i < fileList.size(); i++) {
			if ((fileList.get(i).endsWith(".pnml")) && (fileList.contains(fileList.get(i).substring(0, fileList.get(i).length() - 5) + "_log.xes"))) {
				curfile = fileList.get(i);
				fileset.add(curfile.substring(0, curfile.indexOf(".", 4)));
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
