package ee.ut.bpmn.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class MXMLgenerator {
	
	private Date curdate;
	
	private final int MIN = 60000;
	Multimap<Integer, Integer> adj;
	Multimap<Integer, Integer> conc;
	Map<String, Double> partialprob;
	Map<String, Double> totalprob;
	Multimap<String, String> sourcemap;
	List<String> startevents;
	List<String> endevents;
	
	public MXMLgenerator() {
		curdate = new Date(System.currentTimeMillis());
		clearAll();
	}
	
	private void clearAll() {
		adj = HashMultimap.create();
		conc = HashMultimap.create();
		partialprob = new HashMap<String, Double>();
		totalprob = new HashMap<String, Double>();
		sourcemap = HashMultimap.create();
		startevents = new ArrayList<String>();
		endevents = new ArrayList<String>();
	}
	
	private int asc(String s) {
		return (s.substring(0, 1).toCharArray()[0]);
	}
	
	private String chr(int c) {
		return Character.toChars(c)[0] + "";
	}
	
	private String getTimeStamp(int minutes) {		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		curdate = new Date(curdate.getTime() + minutes * MIN + Math.round(new Random().nextDouble() * 1000));
		
		String datestr = dateFormat.format(curdate);
		
		datestr = datestr.replace(" ", "T") + ".000+00:00";
		
		return datestr;
	}
	
	private Boolean occursMoreThan(String trace, String act, int loopcount) {
		int pos = 0;
		int currentcount = 0;
		
		while (pos >= 0) {
			pos = trace.indexOf(act, pos);
			if (pos >= 0) {
				currentcount++;
				pos++;
			}
		}
		
		return (currentcount > loopcount);
	}
	
	private Boolean isConcWith(String act1, String act2) {
		int a1 = asc(act1) - 64;
		int a2 = asc(act2) - 64;
		
		return (conc.get(a1).contains(a2) || conc.get(a2).contains(a1));
	}
	
	private Boolean isConc(String act) {
		int a = asc(act) - 64;
		
		return (conc.keySet().contains(a) || conc.values().contains(a));
	}
	
	private Boolean bothConc(String act1, String act2) {
		return (isConc(act1) && isConc(act2));
	}
	
	private List<String> generatePartialTraces(String curtrace, String finalact, int looplimit) {
		List<String> gentraces = new ArrayList<String>();
		
		int lastact = asc(curtrace.substring(curtrace.length() - 1, curtrace.length())) - 64;
		
		if ((adj.get(lastact).size() > 0) && (!chr(lastact + 64).equals(finalact))) {
			for (int c: adj.get(lastact)) {
				if ((curtrace.length() < 2) || (!occursMoreThan(curtrace, chr(c+64), looplimit))) { // this is to limit the loop length
					gentraces.addAll(generatePartialTraces(curtrace + chr(c + 64), finalact, looplimit));
				}
			}
		} 
		else {
			gentraces.add(curtrace);
		}
		
		return gentraces;
	}
	
	private void addTotalProbability(String curtrace, String ptrace1, String ptrace2) {
		double cumulativeprob = 1;
		
		for (String s: partialprob.keySet()) {
			if (ptrace1.contains(s) || ptrace2.contains(s)) cumulativeprob = cumulativeprob * partialprob.get(s);
		}
		totalprob.put(curtrace, cumulativeprob);
	}
	
	private void addTotalProbabilityIterative(String curtrace, String ptrace1, String ptrace2) {
		double cumulativeprob = 1;
		String seq;
		
		for (int i = 0; i < ptrace1.length() - 1; i++) {
			seq = ptrace1.substring(i, i + 2);
			if (partialprob.get(seq) != null) {
				cumulativeprob *= partialprob.get(seq);
			}
		}
		
		for (int i = 0; i < ptrace2.length() - 1; i++) {
			seq = ptrace2.substring(i, i + 2);
			if (partialprob.get(seq) != null) {
				cumulativeprob *= partialprob.get(seq);
			}
		}
		
		totalprob.put(curtrace, cumulativeprob);
	}
	
	private void correctTotalProbablity() {
		double tempvalue;
		for (String k: sourcemap.keySet()) {
			for (String trace: sourcemap.get(k)) {
				tempvalue = totalprob.get(trace);
				totalprob.remove(trace);
				totalprob.put(trace, tempvalue / sourcemap.get(k).size());
			}
		}
	}
	
	private List<String> generateFullTraces(String ptrace1, String ptrace2, int pos1, int pos2, String curtrace) {
		List<String> fulltraces = new ArrayList<String>();
		
		String prevact1 = "";
		String prevact2 = "";
		String act1 = "";
		String act2 = "";
		
		if ((pos1 >= ptrace1.length()) && (pos2 >= ptrace2.length())) { // if the end of both partial traces is reached, return the current trace
			fulltraces.add(curtrace);
			addTotalProbabilityIterative(curtrace, ptrace1, ptrace2);
			sourcemap.put(ptrace1 + ptrace2, curtrace);
		}
		else if (pos1 >= ptrace1.length()) {	// if the end of partial trace 1 is reached, complete the current trace with the remainder of partial trace 2
			curtrace += ptrace2.substring(pos2);
			fulltraces.add(curtrace);
			addTotalProbabilityIterative(curtrace, ptrace1, ptrace2);
			sourcemap.put(ptrace1 + ptrace2, curtrace);
		}
		else if (pos2 >= ptrace2.length()) {	// if the end of partial trace 2 is reached, complete the current trace with the remainder of partial trace 1
			curtrace += ptrace1.substring(pos1);
			fulltraces.add(curtrace);
			addTotalProbabilityIterative(curtrace, ptrace1, ptrace2);
			sourcemap.put(ptrace1 + ptrace2, curtrace);
		}
		else {
			act1 = ptrace1.substring(pos1, pos1 + 1);
			act2 = ptrace2.substring(pos2, pos2 + 1);
			if (pos1 > 0)
				prevact1 = ptrace1.substring(pos1 - 1, pos1);
			if (pos2 > 0)
				prevact2 = ptrace2.substring(pos2 - 1, pos2);
			
			if (act1.equals(act2)) { // if activities are equal, use it in the current trace and advance both partial traces
				fulltraces.addAll(generateFullTraces(ptrace1, ptrace2, pos1 + 1, pos2 + 1, curtrace + act1));
			}
			else {
				if (bothConc(act1, act2)) {
					if (isConcWith(act1, act2)) { // activities in both partial traces are concurrent, so advance both
						fulltraces.addAll(generateFullTraces(ptrace1, ptrace2, pos1 + 1, pos2, curtrace + act1));
						fulltraces.addAll(generateFullTraces(ptrace1, ptrace2, pos1, pos2 + 1, curtrace + act2));
					}
					else {
						if (isConcWith(act1, prevact2)) {
							fulltraces.addAll(generateFullTraces(ptrace1, ptrace2, pos1 + 1, pos2, curtrace + act1));
						}
						else if (isConcWith(prevact1, act2)) {
							fulltraces.addAll(generateFullTraces(ptrace1, ptrace2, pos1, pos2 + 1, curtrace + act2));
						}
					}
				}
				else if (isConc(act1)) {
					fulltraces.addAll(generateFullTraces(ptrace1, ptrace2, pos1 + 1, pos2, curtrace + act1));
				}
				else if (isConc(act2)) {
					fulltraces.addAll(generateFullTraces(ptrace1, ptrace2, pos1, pos2 + 1, curtrace + act2));					
				}				
			}
		}

		return fulltraces;
	}
	
	private String getActivity(String actname) {
		String activity = "<AuditTrailEntry>\n";
		activity += "<WorkflowModelElement>" + actname + "</WorkflowModelElement>\n";
		activity += "<EventType>complete</EventType>\n";
		activity += "<Timestamp>" +  getTimeStamp(5) + "</Timestamp>\n";
		activity += "<Originator>Default Resource</Originator>\n";
		activity += "</AuditTrailEntry>\n";

		return activity;
	}
	
	private String getData() {
		return "<Data>\n<Attribute name=\"LogType\">MXML.EnactmentLog</Attribute>\n</Data>\n";
	}
	
	private String getTrace(List<String> curtrace, int id) {
		String trace = "<ProcessInstance id=\"" + id + "\">\n";
		
		trace += getData();
		
		for (int i = 0; i < curtrace.size(); i++) {
			trace += getActivity(curtrace.get(i));
		}
		
		trace += "</ProcessInstance>\n";
		
		return trace;
	}
	
	private String getHeader(String path, String filename) {
		String headerfile = "";

		try {
			headerfile = new String(Files.readAllBytes(Paths.get(path + filename)));
		}
		catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
		
		return headerfile;
	}
	
	private static String getFooter() {
		return "</Process></WorkflowLog>";
	}
	
	public void createMXML(List<String> totaltraces, int tracecount, String modelname) {
		String path = "eventstr-confchecking/models/RunningExample/";
		String headerfile = "header.txt";
		String mxmlfile = ".mxml";
		
		String mxml = getHeader(path, headerfile);
		List<String> curtrace;
		int id = 1;
		
		for (String trace: totaltraces) {
			curtrace = new ArrayList<String>();
			curtrace.addAll(startevents);
			for (int i = 0; i < trace.length(); i++) {
				curtrace.add(trace.substring(i, i + 1));
			}
			curtrace.addAll(endevents);
			
			for (int c = 0; c < Math.round(tracecount * totalprob.get(trace)); c++) {
			//for (int c = 0; c < tracecount * totalprob.get(trace); c++) {
				mxml += getTrace(curtrace, id);
				id++;
			}
		}

		mxml += getFooter();
		
		mxmlfile = modelname + mxmlfile;

		try {
			File newTextFile = new File(path + mxmlfile);
            FileWriter fileWriter = new FileWriter(newTextFile);
            fileWriter.write(mxml);
            fileWriter.close();
		}
		catch(Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
	
	public List<String> createGeneratedTraces() {
		clearAll();
		
		adj.put(1, 2); //A=1, B=2, etc.
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 5);
		adj.put(2, 5);
		adj.put(5, 9);
		
		adj.put(1, 6);
		adj.put(6, 7);
		adj.put(7, 9);
		adj.put(7, 8);
		adj.put(8, 7);
		adj.put(8, 9);
		
		//adj.put(2, 7);
		//adj.put(4, 7);
		
		conc.put(2, 6);
		conc.put(3, 6);
		conc.put(4, 6);
		conc.put(5, 7);
		conc.put(5, 8);
		
		partialprob.put("BC", 0.6);
		partialprob.put("BE", 0.4);
		partialprob.put("FGI", 0.7);
		partialprob.put("FGHI", 0.3 * 0.8);
		partialprob.put("GHGI", 0.3 * 0.2 * 0.7);
		partialprob.put("GHGHI", 0.3 * 0.2 * 0.3);
		
		startevents.add("start");
		endevents.add("end");
		
		System.out.println(adj);

		List<String> partialtraces = generatePartialTraces("A", "", 1); 
		System.out.println(partialtraces);
		
		List<String> totaltraces = new ArrayList<String>();
		
		for (int i = 0; i < 2; i ++) {
			for (int j = 2; j < 6; j++) {
				totaltraces.addAll(generateFullTraces(partialtraces.get(i), partialtraces.get(j), 0, 0, ""));
			}
		}
		System.out.println(totaltraces);
		System.out.println(totaltraces.size());
		
		System.out.println(partialprob);
		System.out.println(totalprob);
		correctTotalProbablity();
		System.out.println(totalprob);
		
		return totaltraces;
	}
	
	public List<String> createGeneratedTraces2() {
		clearAll();
		
		adj.put(1, 2); //A=1, B=2, etc.
		adj.put(2, 3);
		adj.put(3, 4);
		adj.put(4, 15);
		adj.put(2, 15);
		adj.put(15, 9);
		
		adj.put(1, 6);
		adj.put(6, 7);
		adj.put(7, 9);
		adj.put(7, 8);
		adj.put(8, 7);
		adj.put(8, 9);
		
		//adj.put(2, 7);
		//adj.put(4, 7);
		
		conc.put(2, 6);
		conc.put(3, 6);
		conc.put(4, 6);
		conc.put(15, 7);
		conc.put(15, 8);
		
		partialprob.put("BC", 0.6);
		partialprob.put("BO", 0.4);
		partialprob.put("FGI", 0.7);
		partialprob.put("FGHI", 0.3 * 0.8);
		partialprob.put("GHGI", 0.3 * 0.2 * 0.7);
		partialprob.put("GHGHI", 0.3 * 0.2 * 0.3);
		
		startevents.add("start");
		endevents.add("end");
		
		System.out.println(adj);

		List<String> partialtraces = generatePartialTraces("A", "", 1); 
		System.out.println(partialtraces);
		
		List<String> totaltraces = new ArrayList<String>();
		
		for (int i = 0; i < 2; i ++) {
			for (int j = 2; j < 6; j++) {
				totaltraces.addAll(generateFullTraces(partialtraces.get(i), partialtraces.get(j), 0, 0, ""));
			}
		}
		System.out.println(totaltraces);
		System.out.println(totaltraces.size());
		
		System.out.println(partialprob);
		System.out.println(totalprob);
		correctTotalProbablity();
		System.out.println(totalprob);
		
		return totaltraces;
	}
	
	public List<String> createGeneratedTracesPar() {
		clearAll();
		
		adj.put(1, 2); //A=1, B=2, etc.
		adj.put(1, 3);
		adj.put(2, 4);
		adj.put(3, 4);
		
		conc.put(2, 3);
		
		startevents.add("start");
		endevents.add("end");
		
		System.out.println(adj);

		List<String> partialtraces = generatePartialTraces("A", "", 1); 
		System.out.println(partialtraces);
		
		List<String> totaltraces = new ArrayList<String>();
		
		totaltraces.addAll(generateFullTraces(partialtraces.get(0), partialtraces.get(1), 0, 0, ""));
		
		System.out.println(totaltraces);
		System.out.println(totaltraces.size());
		
		System.out.println(partialprob);
		System.out.println(totalprob);
		
		return totaltraces;
	}
	
	public List<String> createGeneratedTracesOuter() {
		clearAll();
		
		adj.put(1, 2); //A=1, B=2, etc.
		adj.put(2, 3);
		adj.put(2, 1);
		
		startevents.add("start");
		endevents.add("end");
		
		System.out.println(adj);

		List<String> partialtraces = generatePartialTraces("A", "", 2); 
		
		List<String> totaltraces = new ArrayList<String>();
		
		for (String t: partialtraces) {
			totaltraces.add(t);
			if (t.length() < 4) {
				totalprob.put(t, 0.5);
			}
			else {
				totalprob.put(t, 0.25);
			}
		}
		
		System.out.println(totaltraces);
		System.out.println(totaltraces.size());
		
		System.out.println(totalprob);
		
		return totaltraces;
	}
	
	public List<String> createGeneratedTracesInner() {
		clearAll();
		
		adj.put(1, 2); //A=1, B=2, etc.
		adj.put(2, 3);
		adj.put(2, 2);
		
		startevents.add("start");
		endevents.add("end");
		
		System.out.println(adj);

		List<String> partialtraces = generatePartialTraces("A", "", 2); 
		
		List<String> totaltraces = new ArrayList<String>();
		
		for (String t: partialtraces) {
			totaltraces.add(t);
			if (t.length() < 4) {
				totalprob.put(t, 0.5);
			}
			else {
				totalprob.put(t, 0.25);
			}
		}
		
		System.out.println(totaltraces);
		System.out.println(totaltraces.size());
		
		System.out.println(totalprob);
		
		return totaltraces;
	}
	
	public List<String> createGeneratedTracesNoloop() {
		clearAll();
		
		adj.put(1, 2); //A=1, B=2, etc.
		adj.put(2, 3);
		
		startevents.add("start");
		endevents.add("end");
		
		System.out.println(adj);

		List<String> partialtraces = generatePartialTraces("A", "", 2); 
		
		List<String> totaltraces = new ArrayList<String>();
		
		for (String t: partialtraces) {
			totaltraces.add(t);
			totalprob.put(t, 1.0);
		}
		
		System.out.println(totaltraces);
		System.out.println(totaltraces.size());
		
		System.out.println(partialprob);
		System.out.println(totalprob);
		
		return totaltraces;
	}
	
	public List<String> createGeneratedTracesNested() {
		clearAll();
		
		adj.put(1, 2); //A=1, B=2, etc.
		adj.put(2, 3);
		adj.put(2, 1);
		adj.put(2, 2);
		
		startevents.add("start");
		endevents.add("end");
		
		System.out.println(adj);

		List<String> partialtraces = generatePartialTraces("A", "", 2); 
		
		List<String> totaltraces = new ArrayList<String>();
		
		// loop AB = 40%, loop B = 30%, no loop 30%
		for (String t: partialtraces) {
			String t1 = t.replace("AB", "");
			String t2 = t1.replace("B", "");
			int outercount = (t.length() - t1.length()) / 2 - 1;
			int innercount = (t1.length() - t2.length());
			
			totaltraces.add(t);
			
			double prob = Math.pow(0.4, outercount) * Math.pow(0.3, innercount);
			if (innercount + outercount < 2) {
				prob *= 0.3;
			}
			totalprob.put(t, prob);
			
		}
		
		System.out.println(totaltraces);
		System.out.println(totaltraces.size());
		
		System.out.println(totalprob);
		
		return totaltraces;
	}
	
	public List<String> createGeneratedTracesOverlapping() {
		clearAll();
		
		adj.put(1, 2); //A=1, B=2, etc.
		adj.put(2, 3);
		adj.put(2, 1);
		adj.put(3, 2);
		adj.put(3, 4);
		
		startevents.add("start");
		endevents.add("end");
		
		System.out.println(adj);

		List<String> partialtraces = generatePartialTraces("A", "", 2); 
		
		List<String> totaltraces = new ArrayList<String>();
		
		// loop AB = 40%, loop BC = 50%
		for (String t: partialtraces) {
			t = t.replace("D", "");
			
			String t1 = t.replace("BA", "");
			String t2 = t.replace("CB", "");
			int leftcount = (t.length() - t1.length()) / 2;
			int rightcount = (t.length() - t2.length()) / 2;
			
			totaltraces.add(t);
			
			double prob = Math.pow(0.4, leftcount) * Math.pow(0.6 * 0.5, rightcount);
			if (leftcount + rightcount < 2) {
				prob *= 0.5 * 0.6;
			}
			totalprob.put(t, prob);
		}
		
		System.out.println(totaltraces);
		System.out.println(totaltraces.size());
		
		System.out.println(totalprob);
		
		return totaltraces;
	}
	
	public List<String> createGeneratedCP() {
		clearAll();
		
		adj.put(1, 2); //A=1, B=2, etc.
		adj.put(1, 3);
		adj.put(1, 4);
		
		adj.put(2, 2);
		adj.put(2, 5);
		adj.put(2, 6);
		adj.put(2, 7);
		adj.put(3, 3);
		adj.put(3, 5);
		adj.put(3, 7);
		
		adj.put(5, 2);
		adj.put(5, 3);
		adj.put(5, 7);
		adj.put(6, 2);
		adj.put(6, 3);
		adj.put(6, 7);
		
		adj.put(7, 8);
		adj.put(8, 9);
		
		adj.put(4, 4);
		adj.put(4, 9);

		conc.put(2, 4);
		conc.put(3, 4);
		conc.put(5, 4);
		conc.put(6, 4);
		conc.put(7, 4);
		conc.put(8, 4);
		
		partialprob.put("AB", 0.4);
		partialprob.put("AC", 0.6);
		partialprob.put("BB", 0.3);
		partialprob.put("CC", 0.3);
		partialprob.put("DD", 0.5);
		
		partialprob.put("BE", 0.7 * 0.5);
		partialprob.put("BF", 0.7 * 0.3);
		partialprob.put("BG", 0.7 * 0.2);
		
		partialprob.put("CE", 0.7 * 0.5);
		partialprob.put("CF", 0.7 * 0.3);
		partialprob.put("CG", 0.7 * 0.2);
		
		partialprob.put("EG", 0.6);
		partialprob.put("EC", 0.2);
		partialprob.put("EB", 0.2);
		partialprob.put("FG", 0.6);
		partialprob.put("FC", 0.2);
		partialprob.put("FB", 0.2);
		
		startevents.add("start");
		endevents.add("end");
		
		//System.out.println(adj);

		List<String> partialtraces = generatePartialTraces("A", "", 1); 
		System.out.println(partialtraces);
		
		List<String> totaltraces = new ArrayList<String>();
		
		for (int i = 0; i < 2; i ++) {
			for (int j = 2; j < partialtraces.size(); j++) {
				totaltraces.addAll(generateFullTraces(partialtraces.get(i), partialtraces.get(j), 0, 0, ""));
			}
		}
		//System.out.println(totaltraces);
		System.out.println(totaltraces.size());
		
		System.out.println(partialprob);
		System.out.println(totalprob);
		correctTotalProbablity();
		System.out.println(totalprob);
		
		return totaltraces;
	}
	
	public List<String> createGeneratedR1() {
		clearAll();
		
		List<String> interleavings = new ArrayList<String>();
		List<String> totaltraces = new ArrayList<String>();
		
		interleavings.add("BCD");
		interleavings.add("BDC");
		interleavings.add("DBC");
		
		for (int i = 0; i < interleavings.size(); i++)
			totaltraces.add("A" + interleavings.get(i) + "FGH");
		
		for (int i = 0; i < interleavings.size(); i++)
			for (int j = 0; j < interleavings.size(); j++)
				totaltraces.add("A" + interleavings.get(i) + interleavings.get(j) + "FGH");
		
		for (int i = 0; i < interleavings.size(); i++)
			for (int j = 0; j < interleavings.size(); j++)
				for (int k = 0; k < interleavings.size(); k++)
					totaltraces.add("A" + interleavings.get(i) + interleavings.get(j) + interleavings.get(k) + "FGH");
		
//		startevents.add("start");
//		endevents.add("end");
		
		for (String t: totaltraces) {
			if (t.length() == 7) {
				totalprob.put(t, 0.5 / 3);
			}
			else if (t.length() == 10) {
				totalprob.put(t, 0.25 / 9);
			}
			else {
				totalprob.put(t, 0.25 / 27);
			}
		}
		
		return totaltraces;
	}
	
	public static void main(String[] args) {
		MXMLgenerator gen = new MXMLgenerator();
//		gen.createMXML(gen.createGeneratedTracesOuter(), 1000, "Outerlog");
//		gen.createMXML(gen.createGeneratedTracesInner(), 1000, "Innerlog");
//		gen.createMXML(gen.createGeneratedTracesNoloop(), 1000, "Nolooplog");
//		gen.createMXML(gen.createGeneratedTracesNested(), 1000, "Nestedlog");
//		gen.createMXML(gen.createGeneratedTracesOverlapping(), 1000, "Overlappinglog");
		
//		gen.createMXML(gen.createGeneratedCP(), 10000, "CP");
		gen.createMXML(gen.createGeneratedR1(), 1000, "R1log");
		
//		gen.createMXML(gen.createGeneratedTraces(), 1000,  "RealExampleGenerated");
//		gen.createMXML(gen.createGeneratedTraces2(), 1000,  "RealExampleGenerated2");
	}
	
}
