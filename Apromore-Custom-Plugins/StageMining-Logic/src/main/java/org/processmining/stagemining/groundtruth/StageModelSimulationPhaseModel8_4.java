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

public class StageModelSimulationPhaseModel8_4 extends ExampleClass {
	public List<Set<String>> getGroundTruth(XLog log) throws Exception {
		
		Map<String,Set<String>> mapMilestonePhase = new HashMap<String, Set<String>>();
		
		mapMilestonePhase.put("S1", new HashSet<String>());
		mapMilestonePhase.put("S2", new HashSet<String>());
		mapMilestonePhase.put("S3", new HashSet<String>());
		mapMilestonePhase.put("S4", new HashSet<String>());
		mapMilestonePhase.put("S5", new HashSet<String>());
		mapMilestonePhase.put("S6", new HashSet<String>());
		
		mapMilestonePhase.get("S1").add("a");
		mapMilestonePhase.get("S1").add("b");
		mapMilestonePhase.get("S1").add("c");
		mapMilestonePhase.get("S1").add("s1");
		
		mapMilestonePhase.get("S2").add("d");
		mapMilestonePhase.get("S2").add("e");
		mapMilestonePhase.get("S2").add("f");
		mapMilestonePhase.get("S2").add("s2");
		
		mapMilestonePhase.get("S3").add("g");
		mapMilestonePhase.get("S3").add("h");
		mapMilestonePhase.get("S3").add("i");
		mapMilestonePhase.get("S3").add("s3");
		
		mapMilestonePhase.get("S4").add("j");
		mapMilestonePhase.get("S4").add("k");
		mapMilestonePhase.get("S4").add("l");
		mapMilestonePhase.get("S4").add("s4");
		
		mapMilestonePhase.get("S5").add("m");
		mapMilestonePhase.get("S5").add("n");
		mapMilestonePhase.get("S5").add("o");
		mapMilestonePhase.get("S5").add("s5");
		
		mapMilestonePhase.get("S6").add("p");
		mapMilestonePhase.get("S6").add("q");
		mapMilestonePhase.get("S6").add("r");
		mapMilestonePhase.get("S6").add("s6");
		
		List<Set<String>> phaseModel = new ArrayList<Set<String>>();
		phaseModel.add(mapMilestonePhase.get("S1"));
		phaseModel.add(mapMilestonePhase.get("S2"));
		phaseModel.add(mapMilestonePhase.get("S3"));
		phaseModel.add(mapMilestonePhase.get("S4"));
		phaseModel.add(mapMilestonePhase.get("S5"));
		phaseModel.add(mapMilestonePhase.get("S6"));
		
		return phaseModel;
	}
}
