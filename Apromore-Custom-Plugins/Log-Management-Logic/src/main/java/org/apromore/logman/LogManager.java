package org.apromore.logman;

import java.util.List;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logman.log.durationaware.DurationAwareLog;
import org.apromore.logman.stats.LogStatistics;
import org.deckfour.xes.model.XLog;


public class LogManager {
	private DurationAwareLog log;

	//Should make in LogStatistics
	//private Map<String, Map<String, Float>> originalLogAttMap; //attributename => (attributevalue => NumberOFOccurrence)

	private List<LogFilterCriterion> filterCriteria;
	private XLog filteredLog;
	
	//Should make in LogStatistics
	//private Map<String, Map<String, Float>> filteredLogAttMap; //attributename => (attributevalue => NumberOFOccurrence)
	
	private String classifierAttribute;
	private boolean classifierAggregate = false;
	
	public LogManager(DurationAwareLog log) {
		this.log = log;
	}
	
	public DurationAwareLog getlLog() {
		return this.log;
	}
	
	public LogStatistics getOriginalLogStats() {
		return null;
	}
	
	public String getClassifierAttribute() {
		return this.classifierAttribute;
	}
	
	public void setClassifierAttribute(String attribute, boolean classifierAggregate) {
		//Change the activity log
		//Change the case variants statistics
		this.classifierAttribute = attribute;
		this.classifierAggregate = classifierAggregate;
	}
	
	public void filter(List<LogFilterCriterion> filterCriteria) {
//		this.filterCriteria = filterCriteria;
//		LogFilterService logFilter = new LogFilterImpl();
//		XLog filteredLog = logFilter.filter(this.activityLog, filterCriteria);
	}
	
	public XLog getFilteredLog() {
		return this.filteredLog;
	}
	
	public LogStatistics getFilteredLogStats() {
		return null;
	}
	
	public List<LogFilterCriterion> getLogFilterCriteria() {
		return this.filterCriteria;
	}
	

}
