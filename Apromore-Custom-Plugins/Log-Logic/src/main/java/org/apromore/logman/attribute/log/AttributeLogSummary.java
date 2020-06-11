/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.logman.attribute.log;

public class AttributeLogSummary  {
    private long eventCount = 0;
    private long actCount = 0;
    private long caseCount = 0;
    private long variantCount = 0;
    private long startTime = Long.MAX_VALUE;
    private long endTime = Long.MIN_VALUE;
    private double traceDurationMin, traceDurationMax, traceDurationMean, traceDurationMedian;
    
    public AttributeLogSummary(long eventCount, long actCount, long caseCount, long variantCount,
                              long startTime, long endTime,
                              double traceDurationMin, double traceDurationMax, 
                              double traceDurationMean, double traceDurationMedian) {
        this.eventCount = eventCount;
        this.actCount = actCount;
        this.caseCount = caseCount;
        this.variantCount = variantCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.traceDurationMin = traceDurationMin;
        this.traceDurationMax = traceDurationMax;
        this.traceDurationMean = traceDurationMean;
        this.traceDurationMedian = traceDurationMedian;
    }
    
    public long getEventCount() {
        return eventCount;
    }
    
    public long getActivityCount() {
        return actCount;
    }
    
    public long getCaseCount() {
        return caseCount;
    }
    
    public long getVariantCount() {
        return variantCount;
    }
    
    public long getLogMinTime() {
        return startTime;
    }
    
    public long getLogMaxTime() {
        return endTime;
    }
    
    public double getTraceDurationMin() {
    	return traceDurationMin;
    }
    
    public double getTraceDurationMax() {
    	return traceDurationMax;
    }
    
    public double getTraceDurationMean() {
    	return traceDurationMean;
    }
    
    public double getTraceDurationMedian() {
    	return traceDurationMedian;
    }


}
