package org.apromore.logman;

import java.util.List;
import java.util.Map;

import org.apromore.logfilter.LogFilterService;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.impl.LogFilterImpl;
import org.apromore.logman.stats.LogStatistics;
import org.apromore.logman.stats.LogSummary;
import org.deckfour.xes.model.XLog;

import au.edu.qut.processmining.log.SimpleLog;

public class LogManager {
	private XLog originalLog;
	private LogSummary originalLogSummary;
	private LogStatistics originalLogStats;
	
	//Should make in LogStatistics
	//private Map<String, Map<String, Float>> originalLogAttMap; //attributename => (attributevalue => NumberOFOccurrence)

	private List<LogFilterCriterion> filterCriteria;
	private XLog filteredLog;
	private LogSummary filteredLogSummary;
	private LogStatistics filteredLogStats;
	
	//Should make in LogStatistics
	//private Map<String, Map<String, Float>> filteredLogAttMap; //attributename => (attributevalue => NumberOFOccurrence)
	
	private String classifierAttribute;
	private final String CONCEPT_NAME = "concept:name";	
	private XLog activityLog;	
	private SimpleLog simpleActivityLog;
	
	public LogManager(XLog originaLog) {
		this.originalLog = originaLog;
	}
	
	public XLog getOriginalLog() {
		return this.originalLog;
	}
	
	public LogStatistics getOriginalLogStats() {
		
	}
	
	public String getClassifierAttribute() {
		return this.classifierAttribute;
	}
	
	public void setClassifierAttribute(String attribute) {
		//Change the activity log
		//Change the case variants statistics
		this.classifierAttribute = attribute;
		if (this.classifierAttribute.equals(CONCEPT_NAME)) {
			this.activityLog = this.originalLog;
		}
		else {
			this.activityLog = this.createActivityLog(this.classifierAttribute);
		}
	}
	
	public XLog getActivityLog() {
		return this.activityLog;
	}
	
	public SimpleLog getSimpleActivityLog() {
		return this.simpleActivityLog;
	}
	
	private XLog createActivityLog(String classifierAttribute) {
		if (classifierAttribute.equals(CONCEPT_NAME)) {
			return this.eagerMatchStartEndEvents()
		}
	}
	
	private XLog eagerMatchStartEndEvents(XLog log) {
		
	}
	
	public Set<String> getDistinctActivities() {
		
	}
	
	public void filter(List<LogFilterCriterion> filterCriteria) {
		this.filterCriteria = filterCriteria;
		LogFilterService logFilter = new LogFilterImpl();
		XLog filteredLog = logFilter.filter(this.activityLog, filterCriteria);
	}
	
	public XLog getFilteredLog() {
		return this.filteredLog;
	}
	
	public LogStatistics getFilteredLogStats() {
		
	}
	
	public List<LogFilterCriterion> getLogFilterCriteria() {
		return this.filterCriteria;
	}
	

}
