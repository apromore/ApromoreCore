package org.apromore.plugin.portal.logfilter;

import java.util.List;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.deckfour.xes.model.XLog;

public interface LogFilterResultListener {
	public void filterFinished(List<LogFilterCriterion> criteria, XLog filteredLog);
}
