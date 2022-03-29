/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.service.loganimation.recording;

import java.util.List;

import org.apromore.service.loganimation.replay.AnimationLog;

/**
 * An <b>AnimationContext</b> captures the global setting for the animation.
 * 
 * @author Bruce Nguyen
 *
 */
public class AnimationContext {
	private int recordingFrameRate = 60; //frames per second
    private int recordingDuration = 600; //seconds
    private double recordingFrameInterval = 0; //milliseconds between two consecutive frames
    
    private long minLogStartTimestamp = Long.MAX_VALUE;
    private long maxLogEndTimestamp = Long.MIN_VALUE;
    private double logToRecordingTimeRatio = 1; //a second on the animation timeline is converted to actual seconds
    private double logTimeFrameInterval; // frame interval in terms of log time (milliseconds)
    
    private int frameSkip = 0;
    
    public AnimationContext(List<AnimationLog> logs) {
        for (AnimationLog log: logs) {
            if (minLogStartTimestamp > log.getStartDate().getMillis()) minLogStartTimestamp = log.getStartDate().getMillis();
            if (maxLogEndTimestamp < log.getEndDate().getMillis()) maxLogEndTimestamp = log.getEndDate().getMillis();
        }
        this.setRecordingFrameRate(this.recordingFrameRate);
        this.setRecordingDuration(this.recordingDuration);
    }
    
    public AnimationContext(List<AnimationLog> logs, int recordingFrameRate, int recordingDuration) {
        this(logs);
        this.setRecordingFrameRate(recordingFrameRate);
        this.setRecordingDuration(recordingDuration);
    }
    
    public int getRecordingFrameRate() {
        return this.recordingFrameRate;
    }
    
    public void setRecordingFrameRate(int fps) {
        if (fps > 0) {
            this.recordingFrameRate = fps;
            this.recordingFrameInterval = 1.0/fps*1000;
            this.logTimeFrameInterval = logToRecordingTimeRatio*recordingFrameInterval;
        }
    }
    
    public int getMaxNumberOfFrames() {
    	return this.recordingFrameRate*this.recordingDuration;
    }
    
    public double getRecordingFrameInterval() {
        return this.recordingFrameInterval;
    }
    
    public double getRecordingLogFrameInterval() {
        return this.logTimeFrameInterval;
    }
    
    public long getMinLogStartTimestamp() {
        return this.minLogStartTimestamp;
    }
    
    public long getMaxLogEndTimestamp() {
        return this.maxLogEndTimestamp;
    }
    
    public int getRecordingDuration() {
        return this.recordingDuration;
    }
    
    //Unit: seconds
    public void setRecordingDuration(int recordingDuration) {
        if (recordingDuration > 0) {
            this.recordingDuration = recordingDuration;
            this.logToRecordingTimeRatio = (double)(maxLogEndTimestamp - minLogStartTimestamp)/(recordingDuration*1000);
            this.logTimeFrameInterval = logToRecordingTimeRatio*recordingFrameInterval;
        }
    }
    
    public double getTimelineRatio() {
        return this.logToRecordingTimeRatio;
    }
    
    /**
     * 
     * @param timestamp: milliseconds since 1/1/1970
     */
    public int getFrameIndexFromLogTimestamp(long timestamp) {
        if (timestamp <= minLogStartTimestamp) {
            return 0;
        }
        else if (timestamp >= maxLogEndTimestamp) {
            return getMaxNumberOfFrames() - 1;
        }
        else {
            return (int)Math.floor(1.0*(timestamp - minLogStartTimestamp)/logTimeFrameInterval);
        }
    }
    
    public void setFrameSkip(int frameSkip) {
        this.frameSkip = frameSkip;
    }
    
    public int getFrameSkip() {
        return this.frameSkip;
    }
    
}
