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

public class MXMLgeneratorAtomic {
	
	private Date curdate;
	
	private final int MIN = 60000;
	Multimap<Integer, Integer> adj;
	Multimap<Integer, Integer> conc;
	Map<String, Double> partialprob;
	Map<String, Double> totalprob;
	Multimap<String, String> sourcemap;
	List<String> startevents;
	List<String> endevents;
	
	public MXMLgeneratorAtomic() {
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
	
	private String getTimeStamp(int minutes) {		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		curdate = new Date(curdate.getTime() + minutes * MIN + Math.round(new Random().nextDouble() * 1000));
		
		String datestr = dateFormat.format(curdate);
		
		datestr = datestr.replace(" ", "T") + ".000+00:00";
		
		return datestr;
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
		String path = "eventstr-confchecking/models/AtomicTest/";
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
	
	public List<String> createBase(Boolean startend) {
		clearAll();
		
		List<String> interleavings = new ArrayList<String>();
		List<String> totaltraces = new ArrayList<String>();
		
		interleavings.add("BCD");
		interleavings.add("BDC");
		interleavings.add("DBC");
		
		for (int i = 0; i < interleavings.size(); i++)
			totaltraces.add("A" + interleavings.get(i) + "EFGH");

		if (startend) {
			startevents.add("start");
			endevents.add("end");
		}
		
		for (String t: totaltraces) {
			totalprob.put(t, 1.0 / totaltraces.size());
		}
		
		return totaltraces;
	}
	
	public List<String> createI1(Boolean startend) {
		clearAll();
		
		List<String> interleavings = new ArrayList<String>();
		List<String> totaltraces = new ArrayList<String>();
		
		interleavings.add("BCD");
		interleavings.add("BDC");
		interleavings.add("DBC");
		
		for (int i = 0; i < interleavings.size(); i++)
			totaltraces.add("A" + interleavings.get(i) + "EGH");

		if (startend) {
			startevents.add("start");
			endevents.add("end");
		}
		
		for (String t: totaltraces) {
			totalprob.put(t, 1.0 / totaltraces.size());
		}
		
		return totaltraces;
	}
	
	public List<String> createI2(Boolean startend) {
		clearAll();
		
		List<String> interleavings = new ArrayList<String>();
		List<String> totaltraces = new ArrayList<String>();
		
		interleavings.add("BCD");
		interleavings.add("BDC");
		interleavings.add("DBC");
		
		for (int i = 0; i < interleavings.size(); i++)
			totaltraces.add("A" + interleavings.get(i) + "EAFGH");

		if (startend) {
			startevents.add("start");
			endevents.add("end");
		}
		
		for (String t: totaltraces) {
			totalprob.put(t, 1.0 / totaltraces.size());
		}
		
		return totaltraces;
	}
	
	public List<String> createI3(Boolean startend) {
		clearAll();
		
		List<String> interleavings = new ArrayList<String>();
		List<String> totaltraces = new ArrayList<String>();
		
		interleavings.add("BCD");
		interleavings.add("BDC");
		interleavings.add("DBC");
		
		for (int i = 0; i < interleavings.size(); i++)
			totaltraces.add("A" + interleavings.get(i) + "EXGH");

		if (startend) {
			startevents.add("start");
			endevents.add("end");
		}
		
		for (String t: totaltraces) {
			totalprob.put(t, 1.0 / totaltraces.size());
		}
		
		return totaltraces;
	}
	
	public List<String> createR1(Boolean startend) {
		clearAll();
		
		List<String> interleavings = new ArrayList<String>();
		List<String> totaltraces = new ArrayList<String>();
		
		interleavings.add("BCD");
		interleavings.add("BDC");
		interleavings.add("DBC");
		
		for (int i = 0; i < interleavings.size(); i++)
			totaltraces.add("A" + interleavings.get(i) + "EFGH");
		
		for (int i = 0; i < interleavings.size(); i++)
			for (int j = 0; j < interleavings.size(); j++)
				totaltraces.add("A" + interleavings.get(i) + interleavings.get(j) + "EFGH");
		
		for (int i = 0; i < interleavings.size(); i++)
			for (int j = 0; j < interleavings.size(); j++)
				for (int k = 0; k < interleavings.size(); k++)
					totaltraces.add("A" + interleavings.get(i) + interleavings.get(j) + interleavings.get(k) + "EFGH");
		
		if (startend) {
			startevents.add("start");
			endevents.add("end");
		}
		
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
	
	public List<String> createR2(Boolean startend) {
		clearAll();
		
		List<String> interleavings = new ArrayList<String>();
		List<String> totaltraces = new ArrayList<String>();
		
		interleavings.add("BCD");
		interleavings.add("BDC");
		interleavings.add("DBC");
		
		for (int i = 0; i < interleavings.size(); i++) {
			totaltraces.add("A" + interleavings.get(i) + "EFGH");
			totaltraces.add("A" + interleavings.get(i) + "EH");
		}
		
		if (startend) {
			startevents.add("start");
			endevents.add("end");
		}
		
		for (String t: totaltraces) {
			totalprob.put(t, 1.0 / totaltraces.size());
		}
		
		return totaltraces;
	}
	
	public List<String> createO1(Boolean startend) {
		clearAll();
		
		List<String> totaltraces = new ArrayList<String>();
		
		totaltraces.add("ABCDEFGH");
		
		if (startend) {
			startevents.add("start");
			endevents.add("end");
		}
		
		for (String t: totaltraces) {
			totalprob.put(t, 1.0 / totaltraces.size());
		}
		
		return totaltraces;
	}
	
	public List<String> createO2(Boolean startend) {
		clearAll();
		
		List<String> interleavings = new ArrayList<String>();
		List<String> totaltraces = new ArrayList<String>();
		
		interleavings.add("BCD");
		interleavings.add("BDC");
		interleavings.add("DBC");
		
		for (int i = 0; i < interleavings.size(); i++) {
			totaltraces.add("A" + interleavings.get(i) + "EFH");
			totaltraces.add("A" + interleavings.get(i) + "EGH");
		}
		
		if (startend) {
			startevents.add("start");
			endevents.add("end");
		}
		
		for (String t: totaltraces) {
			totalprob.put(t, 1.0 / totaltraces.size());
		}
		
		return totaltraces;
	}
	
	public List<String> createO3(Boolean startend) {
		clearAll();
		
		List<String> interleavings = new ArrayList<String>();
		List<String> totaltraces = new ArrayList<String>();
		
		interleavings.add("BD");
		interleavings.add("DB");
		
		for (int i = 0; i < interleavings.size(); i++)
			totaltraces.add("A" + interleavings.get(i) + "CEFGH");
		
		if (startend) {
			startevents.add("start");
			endevents.add("end");
		}
		
		for (String t: totaltraces) {
			totalprob.put(t, 1.0 / totaltraces.size());
		}
		
		return totaltraces;
	}
	
	public static void main(String[] args) {
		MXMLgeneratorAtomic gen = new MXMLgeneratorAtomic();
		gen.createMXML(gen.createI1(true), 1000, "I1logse");
		gen.createMXML(gen.createI2(true), 1000, "I2logse");
		gen.createMXML(gen.createI3(true), 1000, "I3logse");
		gen.createMXML(gen.createO1(true), 1000, "O1logse");
		gen.createMXML(gen.createO2(true), 1000, "O2logse");
		gen.createMXML(gen.createO3(true), 1000, "O3logse");
		gen.createMXML(gen.createR2(true), 1000, "R2logse");
	}	
}
