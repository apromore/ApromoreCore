package org.apromore.logman.stats.collector;

import java.time.Instant;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.utils.LogUtils;
import org.apromore.logman.utils.MathUtils;
import org.deckfour.xes.model.XLog;

/**
 * Represent all stats that have to do calculation over time
 * The calculation is discretized by using a time moving window
 * As the number of windows over time could be extremely large if the window size
 * is too small compared to the total time range, it is not recommended
 * to store all windows in the range. This is instead dealt with by using a running 
 * window index to keep memory lightweight without affecting the calculation speed.
 * 
 * For simplicity, this class provides a generic computeWindowSize() method.
 * 
 * @author Bruce Nguyen
 *
 */
public class TimeAwareStatsCollector extends StatsCollector {
	protected XLog xlog;
	protected long logStartTime = Long.MAX_VALUE;
	protected long logEndTime = Long.MIN_VALUE;
	protected long logDuration; //days
	protected long logTotalValue; // total number of value (duration, count, etc).
	
	protected long windowSize = 3600; // seconds
	protected long maxWindows = 1; // number of windows
	
	public TimeAwareStatsCollector(XLog xlog) {
		this.xlog = xlog;
	}
	
    @Override
    public void visitTrace(AXTrace trace) {
        if (LogUtils.getTimeMilliseconds(trace.get(0)) < logStartTime) {
        	logStartTime = LogUtils.getTimeMilliseconds(trace.get(0));
        }
        if (LogUtils.getTimeMilliseconds(trace.get(trace.size()-1)) > logEndTime) {
        	logEndTime = LogUtils.getTimeMilliseconds(trace.get(trace.size()-1));
        }
    }	
    
    @Override
    public void finishVisit() {
//    	logDuration = Instant.ofEpochMilli(logEndTime) 
//    	if (logDuration )
    }
    
//    /**
//     * Compute window size based on a total duration and a measure duration.
//     * The measureDuration should not be the average of all measureDuration, but
//     * only the lower quantile of the population. This is to ensure it can cover 
//     * the small duration but not too much skewed by the smallest ones.
//     * totalDuration and measureDurations must be the same unit
//     */
//    protected long computeWindowSize(long totalDuration, long[] measureDurations) {
//    	long quartile1st = MathUtils.quartile(measureDurations, 0.25);
//    	return (int)(totalDuration/quartile1st) + 1;
//    }
    

}
