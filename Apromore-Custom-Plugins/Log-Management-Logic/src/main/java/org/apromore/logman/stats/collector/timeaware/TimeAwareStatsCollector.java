package org.apromore.logman.stats.collector.timeaware;

import java.util.Arrays;

import org.apromore.logman.LogManager;
import org.apromore.logman.stats.collector.StatsCollector;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.joda.time.Interval;

/**
 * Represent all stats that have to do calculation over time
 * The calculation is discretized by using a time moving window (or even interval)
 * As the number of windows over time could be extremely large if the window size
 * is too small compared to the total time range, it is not recommended
 * to store all windows in the range. This is instead dealt with by using a running 
 * window index to keep memory lightweight without affecting the calculation speed.
 * 
 * A window here is understood as an Interval in joda-time library, meaning when
 * comparing with an instant or other windows, it is inclusive of the start instant 
 * and exclusive of the end. This is to avoid the situation that a timestamp may
 * fall into two different windows.
 * 
 * There are four attributes to determine the whole window-based period of analysis
 * - startTime: the starting timestamp of the whole period (milliseconds from UTC start time)
 * - endTime: the ending timestamp of the whole period (millisedonds from UTC start time)
 * - windowSize: the unit used is hours
 * - numberOfWindows: the number of windows in the whole period 
 * - values: the mapping from a window index to a measure value
 * 
 * 
 * 
 * Dependent classes will scan the period based on window index running from 0 to (numberOfWindows-1).
 * 
 * For simplicity, this class provides a default setting: startTime and endTime are the starting
 * and ending time of the whole log, windowSize can be 1, 6, 12, or 24 hours for the period of 
 * analysis from less than 3 months, 3-6 months, 6-12 months, and more than 1 year. This is given 
 * that few event logs have the log period less than 3 months as it will be too small. 
 * Extended class can override this setting and set different values for these attributes.
 * 
 * @author Bruce Nguyen
 *
 */
public class TimeAwareStatsCollector extends StatsCollector {
	protected XLog log;
	protected long startTime = Long.MAX_VALUE; // the start timestamp of the whole period
	protected long endTime = Long.MIN_VALUE; // the end timstamp of the whole period
	protected int windowSize = 24; // hours
	protected int numberOfWindows = 1; // number of windows
	protected double[] values; // index: window index, value: measure value
	
	@Override 
	public void startVisit(LogManager logManager) {
		log = null;
		startTime = Long.MAX_VALUE;
		endTime = Long.MIN_VALUE;
		windowSize = 24;
		numberOfWindows = 1;
		values = new double[] {};
	}
	
    @Override
    public void visitLog(XLog log) {
		this.log = log;
		
		// This traversal is required to set up the data structures
		// to be used by other steps
		for (XTrace trace: log) {
			if (LogUtils.getTimestamp(trace.get(0)) < startTime) {
	        	startTime = LogUtils.getTimestamp(trace.get(0));
	        }
	        if (LogUtils.getTimestamp(trace.get(trace.size()-1)) > endTime) {
	        	endTime = LogUtils.getTimestamp(trace.get(trace.size()-1));
	        }			
		}
		
    	long periodHours = (endTime - startTime)/1000/3600; //hours
    	if (periodHours > 365*24) { // about 1 year
    		windowSize = 24; //24 hours
    	}
    	else if (periodHours > 180*24) { // from 6 months to 1 year
    		windowSize = 12; //12 hours
    	}
    	else if (periodHours > 90*24) { // from 3 months to 6 months
    		windowSize = 6; //6 hours
    	}
    	else { // less than 3 months
    		windowSize = 1; //1 hour
    	}
    	numberOfWindows = (int)periodHours/windowSize + 1;
    	values = new double[numberOfWindows];
    	Arrays.fill(values, 0);
        
    }
	
    public long getStartTimestamp() {
    	return this.startTime;
    }
    
    public long getEndTimestamp() {
    	return this.endTime;
    }
    
    public int getWindowSize() {
    	return this.windowSize; 
    }
    
    public int getNumberOfWindows() {
    	return this.numberOfWindows;
    }	
    
    // index: window index, value: the value in the window 
    public double[] getValues() {
    	return values;
    }
    
    public long getWindowStart(int windowIndex) throws OutOfPeriodException {
    	if (windowIndex < 0 || windowIndex >= numberOfWindows) {
    		throw new OutOfPeriodException("Window index = " + windowIndex + 
    								" is out of bounds from 0 to " + (numberOfWindows - 1));
    	}
    	else {
    		return (startTime+windowSize*windowIndex);
    	}
    }
    
    public long getWindowEnd(int windowIndex) throws OutOfPeriodException {
    	if (windowIndex < 0 || windowIndex >= numberOfWindows) {
    		throw new OutOfPeriodException("Window index = " + windowIndex + 
    								" is out of bounds from 0 to " + (numberOfWindows - 1));
    	}
    	else {
    		return (startTime+windowSize*(windowIndex+1));
    	}
    }
    
    //window index runs from 0 to (numberOfWindows - 1)
    public Interval getWindow(int windowIndex) throws OutOfPeriodException {
    	if (windowIndex < 0 || windowIndex >= numberOfWindows) {
    		throw new OutOfPeriodException("Window index = " + windowIndex + 
    								" is out of bounds from 0 to " + (numberOfWindows - 1));
    	}
    	else {
    		return new Interval(getWindowStart(windowIndex), getWindowEnd(windowIndex));
    	}
    }
    
    protected int getContainingWindow(long timestamp) {
    	try {
	    	for (int i=0;i<numberOfWindows;i++) {
	    		if (getWindowStart(i) <= timestamp && timestamp < getWindowEnd(i)) {
	    			return i;
	    		}
	    	}
	    	return -1;
    	}
    	catch (OutOfPeriodException ex) {
    		return -1;
    	}
    }
    
    // Get the starting and ending window indexes that overlap the given [start,end] interval
    protected int[] getOverlappingWindows(long start, long end) {
    	try {
	    	if (getWindowStart(0) > end || getWindowEnd(numberOfWindows-1) < start) {
	    		return new int[] {};
	    	}
	    	else if (getWindowStart(0) == end) {
	    		return new int[] {0,0};
	    	}
	    	else if (getWindowEnd(numberOfWindows-1) == start) { //window is exlusive of the endpoint
	    		//return new int[] {numberOfWindows-1,numberOfWindows-1};
	    		return new int[] {};
	    	}
	    	else {
		    	int startIndex=-1;
		    	for (int i=0;i<numberOfWindows;i++) {
		    		if (getWindowEnd(i) < start && i<(numberOfWindows-1)) {
		    			startIndex = i+1;
		    			break;
		    		}
		    	}
		    	
		    	int endIndex=-1;
		    	for (int i=0;i<numberOfWindows;i++) {
		    		if (getWindowStart(i) > end && i>0) {
		    			endIndex = i-1;
		    			break;
		    		}
		    	}
		    	return new int[] {startIndex, endIndex};
	    	}
    	}
    	catch (OutOfPeriodException ex) {
    		return new int[] {};
    	}
    }
    
}
