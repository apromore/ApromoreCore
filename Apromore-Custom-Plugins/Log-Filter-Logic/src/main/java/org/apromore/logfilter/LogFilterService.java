package org.apromore.logfilter;

import java.util.List;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.deckfour.xes.model.XLog;

public interface LogFilterService {
	public XLog filter(XLog log, List<LogFilterCriterion> criteria);
}
