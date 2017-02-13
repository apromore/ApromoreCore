package org.apromore.service.perfmining.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;

/*
 * This class manages all user-defined settings
 */
public class SPFConfig {

	private List<String> stageList = new ArrayList<String>();
	private Map<String, String> eventStageMap = new HashMap<String, String>();
	private final List<String> caseStatusList = new ArrayList<String>();
	private List<String> exitTypeList = new ArrayList<String>();
	private UIPluginContext context = null;
	private XLog log;
	private int timeStep = 3600; //seconds
	private TimeZone timezone = null;
	private boolean checkStartCompleteEvents = false;

	public SPFConfig() {
	}

	public XLog getXLog() {
		return log;
	}

	public void setXLog(XLog newLog) {
		log = newLog;
	}

	public UIPluginContext getContext() {
		return context;
	}

	public void setContext(UIPluginContext context) {
		this.context = context;
	}

	public void setStageList(List<String> stageList) {
		this.stageList = stageList;
	}

	public List<String> getStageList() {
		return stageList;
	}	

	public List<String> getCaseStatusList() {
		return caseStatusList;
	}

	public List<String> getExitTypeList() {
		return exitTypeList;
	}

	public void setExitTypeList(List<String> newList) {
		exitTypeList = newList;
	}

	public void setEventStageMap(Map<String, String> newEventStageMap) {
		eventStageMap = newEventStageMap;
	}

	public Map<String, String> getEventStageMap() {
		return eventStageMap;
	}

	public int getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(int timeStep) throws Exception {
		if (timeStep < 0) {
			throw new Exception("Invalid time step");
		} else {
			this.timeStep = timeStep;
		}
	}

	public TimeZone getTimeZone() {
		return timezone;
	}

	public void setTimeZone(TimeZone timezone) {
		this.timezone = timezone;
	}

	public boolean getCheckStartCompleteEvents() {
		return checkStartCompleteEvents;
	}

	public void setCheckStartCompleteEvents(boolean check) {
		checkStartCompleteEvents = check;
	}

}
