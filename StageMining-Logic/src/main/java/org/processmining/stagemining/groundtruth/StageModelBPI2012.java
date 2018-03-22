package org.processmining.stagemining.groundtruth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.hypergraph.abs.IVertex;
import org.processmining.stagemining.utils.LogUtilites;

public class StageModelBPI2012 extends ExampleClass {
	public List<Set<String>> getGroundTruth(XLog log) throws Exception {
		
		Map<String,Set<String>> mapMilestonePhase = new HashMap<String, Set<String>>();
		mapMilestonePhase.put("S1", new HashSet<String>());
		mapMilestonePhase.put("S2", new HashSet<String>());
		mapMilestonePhase.put("S3", new HashSet<String>());
		mapMilestonePhase.put("S4", new HashSet<String>());
		
		List<Set<String>> phaseModel = new ArrayList<Set<String>>();
		phaseModel.add(mapMilestonePhase.get("S1"));
		phaseModel.add(mapMilestonePhase.get("S2"));
		phaseModel.add(mapMilestonePhase.get("S3"));
		phaseModel.add(mapMilestonePhase.get("S4"));
		
		Set<String> eventSet = new HashSet<String>();
		for (XTrace trace : log) {
			eventSet.clear();
			for (XEvent event : trace) {
				String eventName = LogUtilites.getConceptName(event).toLowerCase();
				String stageName = LogUtilites.getConceptName(event).substring(0,2);
				if (eventName.equals("start") || eventName.equals("end")) continue;
				if (mapMilestonePhase.keySet().contains(stageName)) {
					mapMilestonePhase.get(stageName).add(eventName);
				}
				else {
					throw new Exception("Cannot find stage name: '" + stageName + "'");
				}
			}
		}
		
		return phaseModel;
	}
}
