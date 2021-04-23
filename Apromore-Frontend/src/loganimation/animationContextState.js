'use strict';

/**
 * AnimationContext represents the global animation settings on the front-end.
 * The period of the timeline is [TimelineStart, TimelineEnd] which is also the timeline of the whole animation movie.
 * [LogStart, LogEnd] is the timeline in the log which is shorter than [TimelineStart, TimelineEnd] because of the artificial
 * Start and End Events.
 */
export class AnimationContext {
    /**
     *
     * @param {String} pluginExecutionId: id of the plugin execution
     * @param {Number} timelineStart: the start timestamp on the timeline
     * @param {Number} timelineEnd: the end timestamp on the timeline
     * @param {Number} logStart: the start timestamp in the log(s)
     * @param {Number} logEnd: the end timestamp in the log(s)
     * @param {Number} timelineSlots: the number of slots on the timeline
     * @param {Number} logicalTimelineMax: the maximum logical time on the timeline in seconds
     * @param {Number} recordingFrameRate: the frame rate used to record frames
     * @param {String} timezone: time zone of the server running log animation
     */
    constructor(pluginExecutionId,
                timelineStart, timelineEnd,
                logStart, logEnd,
                timelineSlots, logicalTimelineMax,
                recordingFrameRate,
                timezone) {
        this._pluginExecutionId = pluginExecutionId;
        this._timelineStart = timelineStart;
        this._timelineEnd = timelineEnd;
        this._logStart = logStart;
        this._logEnd = logEnd;
        this._timelineSlots = timelineSlots;
        this._logicalTimelineMax = logicalTimelineMax;
        this._recordingFrameRate = recordingFrameRate;
        this._timezone = timezone;
    }

    getPluginExecutionId() {
        return this._pluginExecutionId;
    }

    // in milliseconds
    getTimelineStart() {
        return this._timelineStart;
    }

    // in milliseconds
    getTimelineEnd() {
        return this._timelineEnd;
    }

    // in milliseconds
    getLogStart() {
        return this._logStart;
    }

    // in milliseconds
    getLogEnd() {
        return this._logEnd;
    }

    // in milliseconds
    getTimelineDuration() {
        return this._timelineEnd - this._timelineStart;
    }

    // the ratio between log time and logical time: 1 logical time unit = x log time unit
    getTimelineRatio() {
        return this.getTimelineDuration()/(this._logicalTimelineMax*1000);
    }

    getStartGapRatio() {
        return (this.getLogStart() - this.getTimelineStart())/this.getTimelineDuration();
    }

    getEndGapRatio() {
        return (this.getTimelineEnd() - this.getLogEnd())/this.getTimelineDuration();
    }

    // Number of slots
    getTimelineSlots() {
        return this._timelineSlots;
    }

    // Number of millis in log time for each timeline slot
    getLogSlotTime() {
        return (this.getLogEnd() - this.getLogStart())/this.getTimelineSlots();
    }

    // in seconds
    getLogicalTimelineMax() {
        return this._logicalTimelineMax;
    }

    // in seconds
    getLogicalSlotTime() {
        return this.getLogSlotTime()/(this.getTimelineRatio()*1000);
    }

    getRecordingFrameRate() {
        return this._recordingFrameRate;
    }

    getTotalNumberOfFrames() {
        return this._recordingFrameRate*this._logicalTimelineMax;
    }

    getTimezone() {
        return this._timezone;
    }
}

/**
 * Represent different states of TokenAnimation
 */
export class AnimationState {
    static get INITIALIZED() {
        return 0;
    }
    static get PLAYING() { // playing animation frame by frame
        return 1;
    }
    static get JUMPING() { // jumping backward or forward to a new frame
        return 2;
    }
    static get PAUSING() { // pausing
        return 3;
    }
}

