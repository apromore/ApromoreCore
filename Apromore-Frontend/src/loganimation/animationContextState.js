'use strict';

/**
 * AnimationContext represents the global animation settings on the front-end
 */
export class AnimationContext {
    /**
     *
     * @param {String} pluginExecutionId: id of the plugin execution
     * @param {String} pluginContextName: context name of the plugin
     * @param {Number} minLogStartTime: the start timestamp in the log
     * @param {Number} maxLogEndTime: the end timestamp in the log
     * @param {Number} timelineSlots: the number of slots on the timeline
     * @param {Number} logicalTimelineMax: the maximum logical time on the timeline in seconds
     * @param {Number} logicalSlotTime: the logical time in seconds of one timeline slot
     * @param {Number} recordingFrameRate: the frame rate used to record frames
     */
    constructor(pluginExecutionId, minLogStartTime, maxLogEndTime,
                timelineSlots, logicalTimelineMax, logicalSlotTime,
                recordingFrameRate) {
        this._pluginExecutionId = pluginExecutionId;
        this._minLogStartTime = minLogStartTime;
        this._maxLogEndTime = maxLogEndTime;
        this._timelineSlots = timelineSlots;
        this._logicalTimelineMax = logicalTimelineMax;
        this._logicalSlotTime = logicalSlotTime;
        this._recordingFrameRate = recordingFrameRate;
    }

    getPluginExecutionId() {
        return this._pluginExecutionId;
    }

    // in milliseconds
    getLogStartTime() {
        return this._minLogStartTime;
    }

    // in milliseconds
    getLogEndTime() {
        return this._maxLogEndTime;
    }

    // in milliseconds
    getTotalLogTime() {
        return this._maxLogEndTime - this._minLogStartTime;
    }

    // the ratio between log time and logical time: 1 logical time unit = x log time unit
    getTimelineRatio() {
        return this.getTotalLogTime()/(this._logicalTimelineMax*1000);
    }

    // Number of slots
    getTimelineSlots() {
        return this._timelineSlots;
    }

    // in seconds
    getLogicalTimelineMax() {
        return this._logicalTimelineMax;
    }

    // in seconds
    getLogicalSlotTime() {
        return this._logicalSlotTime;
    }

    getRecordingFrameRate() {
        return this._recordingFrameRate;
    }

    getTotalNumberOfFrames() {
        return this._recordingFrameRate*this._logicalTimelineMax;
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

