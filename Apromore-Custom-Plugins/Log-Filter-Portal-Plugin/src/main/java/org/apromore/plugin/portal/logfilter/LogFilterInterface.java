package org.apromore.plugin.portal.logfilter;

import java.io.IOException;
import java.util.List;

import org.apromore.logfilter.LogFilterService;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.criteria.factory.LogFilterCriterionFactory;
import org.apromore.plugin.portal.PortalContext;
import org.deckfour.xes.model.XLog;

/**
 * This interface is used to register this plugin as an OSGi service for
 * other plugins to call to 
 * @author Bruce Nguyen
 *
 */
public interface LogFilterInterface {
	/**
	 * Open the log filter UI
	 * @param portalContext
	 * @param log
	 * @param label: the name assigned to this list of criteria
	 * @param originalCriteria: the input criteria which can be edited in the log filter UI 
	 * @param logStats: log statistics
	 * @return: an array, 1st element: filtered XLog, 2nd element: List<LogFilterCriterion>
	 * @throws IOException
	 */
	public void execute(PortalContext portalContext, XLog log, String label, 
			List<LogFilterCriterion> originalCriteria, LogStatistics logStats,
			LogFilterService logFilterService,
			LogFilterCriterionFactory logFilterCriterionFactory,
			LogFilterResultListener resultListener) throws IOException;
}
