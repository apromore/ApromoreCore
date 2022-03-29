/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
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

import org.apromore.service.loganimation.replay.AnimationLog;

public class LogAnimationContext {
    private long logStartTimestamp;
    private long logEndTimestamp;
    private double logToRecordingTimeRatio = 1; //a second on the animation timeline is converted to actual seconds
    private double logTimeFrameInterval; // frame interval in terms of log time (milliseconds)
    
    public LogAnimationContext(AnimationLog log) {
        this.logStartTimestamp = log.getStartDate().getMillis();
        this.logEndTimestamp = log.getEndDate().getMillis();
    }
    
    public LogAnimationContext(AnimationLog log, int recordingFrameRate, int recordingDuration) {
        this(log);
    }
    
    public long getLogStartTimestamp() {
        return this.logStartTimestamp;
    }
    
    public long getLogEndTimestamp() {
        return this.logEndTimestamp;
    }
    
    public double getTimelineRatio() {
        return this.logToRecordingTimeRatio;
    }
    
}
