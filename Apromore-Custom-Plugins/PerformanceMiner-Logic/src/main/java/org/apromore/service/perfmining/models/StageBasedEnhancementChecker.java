package org.apromore.service.perfmining.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.apromore.service.perfmining.parameters.SPFConfig;
import org.apromore.service.perfmining.util.LogUtilites;
import org.apromore.service.perfmining.util.Logger;

public class StageBasedEnhancementChecker {
	private XLog log = null;
	private final List<List<String>> alltraceStageList = new ArrayList<List<String>>();
	private List<String> modelStageList = null;
	private int[][] stageTable;
	private final String logfilename = "stage-based-enhancement-checker.log";
	private final String logfolder = System.getProperty("user.dir");
	private final SPFConfig config;

	public StageBasedEnhancementChecker(XLog log, SPFConfig config) {
		this.log = log;
		this.config = config;
		Logger.startLog(logfolder, logfilename);
		Logger.println("Log information: " + LogUtilites.getValue(log.getAttributes().get("name")));
		Logger.println("Checking Time: " + Calendar.getInstance().getTime());
		selectModelStageList(log);
		buildStageTable();
	}

	public String getOutputFileName() {
		return logfolder + "\\" + logfilename;
	}

	public boolean check() {
		boolean result = true;

		if (!checkAttributes(log)) {
			result = false;
		}

		if (!checkStageBasedEnhancementConditions()) {
			result = false;
		}

		if (config.getCheckStartCompleteEvents() && !checkStartCompleteEvents(log)) {
			result = false;
		}

		return result;
	}

	/**
	 * Select the longest and most frequent stage list
	 * 
	 * @param log
	 * @return
	 */
	private void selectModelStageList(XLog log) {
		List<List<String>> caseStageList = new ArrayList<List<String>>();

		//---------------------------------
		// Select longest distinct stage list from traces
		// Assume that events in trace are sorted by timestamp
		//---------------------------------
		int maxSize = 0;
		for (XTrace trace : log) {
			List<String> distTraceStages = new ArrayList<String>();
			List<String> nonDistTraceStages = new ArrayList<String>(); //non-distinct stage list
			String previousStage = "";
			for (XEvent event : trace) {
				String stage = LogUtilites.getValue(event.getAttributes().get("stage")).toString().toLowerCase();

				if (!distTraceStages.contains(stage)) { //distinct stage list 
					distTraceStages.add(stage);
				}

				if (!stage.equals(previousStage)) { // non-distinct stage list
					previousStage = stage;
					nonDistTraceStages.add(stage);
				}
			}
			alltraceStageList.add(nonDistTraceStages);

			if (distTraceStages.size() == maxSize) {
				caseStageList.add(distTraceStages);
			} else if (distTraceStages.size() > maxSize) {
				caseStageList.clear();
				caseStageList.add(distTraceStages);
				maxSize = distTraceStages.size();
			}
		}

		Map<String, List<String>> stringToStageListMap = new HashMap<String, List<String>>();
		Map<String, Integer> stringToCountMap = new HashMap<String, Integer>();

		//-------------------------------------
		// Count the frequency of distinct stage list
		//-------------------------------------
		for (List<String> stages : caseStageList) {
			String stageString = listToString(stages);
			if (!stringToStageListMap.containsKey(stageString)) {
				stringToStageListMap.put(stageString, stages);
			}

			if (!stringToCountMap.containsKey(stageString)) {
				stringToCountMap.put(stageString, 1);
			} else {
				stringToCountMap.put(stageString, stringToCountMap.get(stageString) + 1);
			}
		}

		String mostFreqStageString = "";
		int maxCount = 0;

		//-------------------------------------
		// Select the most frequent stage list
		//-------------------------------------		
		for (String stageString : stringToCountMap.keySet()) {
			if (stringToCountMap.get(stageString) > maxCount) {
				maxCount = stringToCountMap.get(stageString);
				mostFreqStageString = stageString;
			}
		}

		modelStageList = stringToStageListMap.get(mostFreqStageString);
	}

	private String listToString(List<String> list) {
		String itemString = "";
		for (String item : list) {
			itemString = itemString + "-" + item;
		}
		return itemString;
	}

	// Call after selectModelStageList() method
	private void buildStageTable() {
		stageTable = new int[alltraceStageList.size()][modelStageList.size()];

		for (int i = 0; i < alltraceStageList.size(); i++) {
			List<String> traceStages = alltraceStageList.get(i);
			for (int j = 0; j < traceStages.size(); j++) {
				String stage = traceStages.get(j);
				int m_i = modelStageList.indexOf(stage);
				if (m_i == j) { // stage is at the right order
					stageTable[i][j] = 1;
				} else if (m_i == -1) { // not found stage
					stageTable[i][j] = 0;
				} else { //stage is at wrong order
					stageTable[i][j] = -1;
				}
			}
		}

	}

	private boolean checkAttributes(XLog log) {
		Logger.println("CHECK TRACE AND EVENT ATTRIBUTES");

		boolean error = false;
		for (XTrace trace : log) {

			if (!trace.getAttributes().containsKey("status")) {
				Logger.println("Trace " + LogUtilites.getConceptName(trace) + ": missing 'status' attribute.");
				error = true;
			}

			for (XEvent event : trace) {
				if (!event.getAttributes().containsKey("stage")) {
					Logger.println("Trace " + LogUtilites.getConceptName(trace) + ". Event "
							+ LogUtilites.getConceptName(event) + ": missing 'stage' attribute.");
					error = true;
				}
			}

			if (error) {
				return false;
			}
		}

		return true;
	}

	private boolean checkStageBasedEnhancementConditions() {
		boolean result = true;

		Logger.println("CHECK STAGE-BASED ENHANCEMENT CONDITION");
		Logger.println("Model stage list: " + modelStageList.toString());

		for (int i = 0; i < alltraceStageList.size(); i++) {
			String status = LogUtilites.getValue(log.get(i).getAttributes().get("status")).toString().toLowerCase();
			for (int j = 0; j < modelStageList.size(); j++) {
				if ((stageTable[i][j] == 0) && (j < modelStageList.size() - 1) && (stageTable[i][j + 1] != 0)) {
					Logger.println("Trace " + LogUtilites.getConceptName(log.get(i)) + ": unfound stage + '"
							+ modelStageList.get(j) + "'");
					result = false;
				}
				if ((stageTable[i][j] == 0) && (status.equals("complete") || status.equals("completed"))) {
					Logger.println("Trace " + LogUtilites.getConceptName(log.get(i))
							+ ": complete case with missing stage + '" + modelStageList.get(j) + "'");
					result = false;
				}
				if (stageTable[i][j] == -1) {
					Logger.println("Trace " + LogUtilites.getConceptName(log.get(i)) + ": wrong order at stage + '"
							+ modelStageList.get(j) + "'");
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * Check the consistency between start and complete event Only used for a
	 * log containing both start and complete event
	 * 
	 * @param log
	 * @return false: inconsistency detected
	 */
	private boolean checkStartCompleteEvents(XLog log) {
		boolean result = true;
		Map<String, Integer> startEventCountMap = new HashMap<String, Integer>();
		Map<String, Integer> completeEventCountMap = new HashMap<String, Integer>();

		for (XTrace trace : log) {
			String previousStage = "";
			for (XEvent event : trace) {
				String stage = LogUtilites.getValue(event.getAttributes().get("stage")).toString().toLowerCase();
				String actName = LogUtilites.getConceptName(event);

				if (!stage.equals(previousStage)) {
					if (!previousStage.equals("")) {
						for (String eventName : startEventCountMap.keySet()) {
							if (!completeEventCountMap.containsKey(eventName)
									|| (completeEventCountMap.get(eventName) < startEventCountMap.get(eventName))) {

								Logger.println("Trace " + LogUtilites.getConceptName(trace)
										+ ": unequal number of 'start' and 'complete' events " + " at event '"
										+ eventName + "' at stage '" + previousStage + "'");
								result = false;
							}
						}
					}
					previousStage = stage;
					startEventCountMap.clear();
					completeEventCountMap.clear();
				}

				String transition = LogUtilites.getLifecycleTransition(event).toLowerCase();
				if (transition.equals("start")) {
					if (!startEventCountMap.containsKey(actName)) {
						startEventCountMap.put(actName, 1);
					} else {
						startEventCountMap.put(actName, startEventCountMap.get(actName) + 1);
					}
				}

				if (transition.equals("complete")) {
					if (!completeEventCountMap.containsKey(actName)) {
						completeEventCountMap.put(actName, 1);
					} else {
						completeEventCountMap.put(actName, completeEventCountMap.get(actName) + 1);
					}

					//					if (completeCount > startCount) {
					//						Logger.println("Trace " + LogUtilites.getConceptName(trace) + ": unmatched 'complete' events with 'start' event " + 
					//										" at event '" + LogUtilites.getConceptName(event) + "at position " + trace.indexOf(event)); 
					//						result = false;
					//					}
				}

			}
		}
		return result;
	}
}
