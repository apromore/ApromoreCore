package org.apromore.logfilter;

import java.util.List;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.deckfour.xes.model.XLog;

/**
 * @author Bruce Hoang Nguyen (11/07/2019)
 */
public interface LogFilterService {
	public XLog filter(XLog log, List<LogFilterCriterion> criteria);
}
