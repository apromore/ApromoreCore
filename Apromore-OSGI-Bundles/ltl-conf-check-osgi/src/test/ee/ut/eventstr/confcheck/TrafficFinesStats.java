package ee.ut.eventstr.confcheck;

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

public class TrafficFinesStats {
	String logfilename = 
			"RoadFines_real"
			;
		
	String logfiletemplate = 
			"models/pnml/%s.xes.gz"
			;

	@Test
	public void computeStats() throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));

		System.out.println(getDistinctTraceCount(log));
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

}
