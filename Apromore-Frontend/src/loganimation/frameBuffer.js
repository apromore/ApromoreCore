
/**
 * Buffer contains array of frames like a stock (or store) containing frames as items.
 * The buffer keeps a currentIndex pointer to the first frame in the next chunk available for reading from the buffer
 * The frames from the first one to currentIndex-1 is the used stock
 * The frames from currentIndex to the last one is the current stock
 * Note that the buffer index is not the frame index.
 *
 * House keeping operations:
 * The buffer adds and removes frames via two endless loops: _requestLoop and _cleanLoop.
 *  - Request Loop: connects to the server to get new frames whenever the current stock drops below a safety threshold
 *  and keeps filling up the buffer until the current stock reaches a safety threshold.
 *  - Clean Loop:  the used stock is kept in the buffer as long as it is under a history threshold in case it will be
 *  read again for read efficiency. If the used stock is over a history threshold, old frames are removed out of the buffer
 *  to avoid over-filled buffer.
 *
 * Frames operation:
 * - Read: read the next chunk of frames from the buffer
 * - Write: write a chunk of frames to the end of the buffer
 * - Moveto: move the current pointer to a new position in the buffer
 *
 * Each frame in the buffer has this format:
 * {
 *      index:10,
 *      elements: [
 *          {elementId1: [{caseId1:[0.1, 1, “#abcd”]}, {caseId2:[0.1, 1, “#abcd”]},…]},
 *          {elementId2: [{caseId1:[0.1, 1, “#abcd”]}, {caseId2:[0.1, 1, “#abcd”]},…]},
 *          ...
 *          {elementIdN: [{caseId1:[0.1, 1, “#abcd”]}, {caseId2:[0.1, 1, “#abcd”]},…]},
 *      ]
 * }
 *
 * @author Bruce Nguyen
 */

'use strict';

import {AnimationContext} from "./animationContextState";
import DataRequester from "./dataRequester";

export default class FrameBuffer {
    /**
     * @param {AnimationContext} animationContext
     */
    constructor(animationContext) {
        this._dataRequester = new DataRequester(animationContext.getPluginExecutionId());
        this._chunkSize = FrameBuffer.DEFAULT_CHUNK_SIZE; //numbr of frames in every read
        this._safetyThreshold = FrameBuffer.DEFAULT_SAFETY_THRES;
        this._historyThreshold = FrameBuffer.DEFAULT_HISTORY_THRES;

        this._frames = [];
        this._currentIndex = -1;
        this._nextRequestFrameIndex = 0;
        this._lastRequestedFrameIndex = -1;
        this._frameSkip = 0;
        this._serverOutOfFrames = false;
        this._requestToken = 0; // token to control server responses
        this._sequentialMode = true;
        this._backgroundJobsAllowed = false;
    }

    // An explicit method to separate this operation stage from the initializing stage.
    startOps() {
        this._backgroundJobsAllowed = true;
        this._loopRequestData();
        this._loopCleanup();
    }

    stopOps() {
        this._backgroundJobsAllowed = false;
    }

    static get DEFAULT_CHUNK_SIZE() {
        return 300;
    }

    static get DEFAULT_SAFETY_THRES() {
        return 900;
    }

    static get DEFAULT_HISTORY_THRES() {
        return 600;
    }

    //In case the buffer wants to dynamically change to a different DataRequester (e.g. a custom data channel)
    setDataRequester(dataRequester) {
        this._dataRequester = dataRequester;
    }

    /**
     * Reset may not reset _nextRequestFrameIndex because it depends on the use case, e.g. to jump
     * to a new index or remain at the current one.
     */
    reset() {
        this._frames = [];
        this._currentIndex = -1;
        this._lastRequestedFrameIndex = -1;
        this._serverOutOfFrames = false;
        this._sequentialMode = true;
        this._requestToken++;
    }

    /**
     * @param {Number} nextFrameIndex
     * @param {Number} newFrameSkip
     */
    resetWithFrameSkip(nextFrameIndex, newFrameSkip) {
        this.reset();
        this._nextRequestFrameIndex = nextFrameIndex;
        this._frameSkip = newFrameSkip;
    }

    getSafefyThreshold() {
        return this._safetyThreshold;
    }

    setSafetyThreshold(safetyThreshold) {
        this._safetyThreshold = safetyThreshold;
    }

    getHistoryThreshold() {
        return this._historyThreshold;
    }

    setHistoryThreshold(historyThreshold) {
        this._historyThreshold = historyThreshold;
    }

    getChunkSize() {
        return this._chunkSize;
    }

    setChunkSize(chunkSize) {
        this._chunkSize = chunkSize;
    }

    isEmpty() {
        return (this._frames.length === 0);
    }

    size() {
        return this._frames.length;
    }

    getFirstIndex() {
        return (this.isEmpty() ? -1 : 0);
    }

    getLastIndex() {
        return (this._frames.length-1);
    }

    getCurrentIndex() {
        return this._currentIndex;
    }

    getNextRequestFrameIndex() {
        return this.isEmpty() ? 0 : (this._frames[this.getLastIndex()].index + this._frameSkip + 1);
    }

    /**
     * The number of frames from currentIndex to lastIndex
     * @returns {number}
     */
    getUnusedStockLevel() {
        if (this.isEmpty()) return 0;
        return (this.getLastIndex() - this._currentIndex + 1);
    }

    /**
     * The number of frames from firstIndex to (currentIndex-1)
     * @returns {number}
     */
    getUsedStockLevel() {
        if (this.isEmpty()) return 0;
        return (this._currentIndex - this.getFirstIndex());
    }

    isSafetyStock() {
        return (this.getUnusedStockLevel() >= this._safetyThreshold);
    }

    isObsoleteStock() {
        return (this.getUsedStockLevel() > this._historyThreshold);
    }

    // Has unused frames in the buffer
    isStockAvailable() {
        return (this.getUnusedStockLevel() > 0);
    }

    // Has no unused frames in the buffer and cannot provide any more
    isOutOfSupply() {
        return this._serverOutOfFrames && !this.isStockAvailable();
    }

    /**
     * Sequential reading the next chunk from the buffer starting from currentIndex
     * @returns {Array} array of frames, empty if running out of frames.
     */
    readNextChunk() {
        console.log('Buffer - readNext');
        let frames = [];
        if (this.isStockAvailable()) {
            let lastIndex = this._currentIndex + this._chunkSize - 1;
            lastIndex = (lastIndex <= this.getLastIndex() ? lastIndex : this.getLastIndex());
            for (let i = this._currentIndex; i <= lastIndex; i++) {
                frames.push(this._frames[i]);
            }
            this._currentIndex += frames.length;
        }
        this._logStockLevel();
        return frames;
    }

    /**
     * Move the buffer currentIndex to a frame, e.g when the tick is dragged randomly on the timeline.
     * The frame index corresponds to a buffer index which can be less or greater than the currentIndex, or can
     * be outside the current frames in the buffer. In the latter case, it is too far reaching, the buffer will be
     * cleared and new frames must be read into the buffer starting from the input frame.
     *
     * After the buffer has been cleared, results returned from previous communication with the server may not be used.
     * This is controlled via a request token sent and received in the communication.
     *
     * @param {Number} frameIndex: the frame index
     */
    moveTo(frameIndex) {
        console.log('Buffer - moveTo: frameIndex=' + frameIndex);
        let bufferIndex = this._getBufferIndexFromFrameIndex(frameIndex);
        if (bufferIndex >= 0 && bufferIndex < this.size()) {
            console.log('Buffer - moveTo: moveTo point is within buffer with found index=' + bufferIndex);
            this._currentIndex = bufferIndex;
        }
        else { // the new requested frames are too far outside this buffer: reset buffer
            console.log('Buffer - moveTo: moveTo point is out of buffer, buffer cleared to read new frames');
            this._nextRequestFrameIndex = frameIndex;
            this.setSequentialMode(false);
            this.reset();
        }
        this._logStockLevel();
    }

    /**
     * @param {Boolean} newMode
     */
    setSequentialMode(newMode) {
        this._sequentialMode = newMode;
    }

    isSequentialMode() {
        return this._sequentialMode;
    }

    /**
     * Append a chunk of frames to the end of buffer
     * A request token is used to identify if the coming frames are no longer needed due to
     * local changes while waiting for the server response.
     * @param {Array} frames: chunk of frames
     * @param {Number} requestToken: the token id associated with this frame chunk
     */
    write(frames, requestToken) {
        if (requestToken === this._requestToken) { // don't get old results
            console.log('Buffer - write: valid requestToken, frames accepted, token=' + requestToken);
            if (frames && frames instanceof  Array && frames.length > 0) {
                this._frames = this._frames.concat(frames);
                this._serverOutOfFrames = false;
                if (this._currentIndex < 0) {
                    this._currentIndex = 0;
                }
                this._nextRequestFrameIndex = this.getNextRequestFrameIndex();
                this._logStockLevel();
            }
            else {
                this._serverOutOfFrames = true;
                console.log('Buffer - write: receive empty result. Server is out of frames.');
            }
        }
        else {
            console.log('Buffer - write: obsolete requestToken, frames rejected, token=' + requestToken);
        }
    }

    /**
     * Convert from frame index to the buffer index
     * This depends on the frame index attribute of the last frame in the buffer
     * @param {Number} frameIndex
     * @private
     */
    _getBufferIndexFromFrameIndex(frameIndex) {
        return !this.isEmpty() ? (frameIndex - this._frames[0].index) : -1;
    }

    /**
     * Keeps sending requests to the server for new chunks of frames if the current stock is below a safety threshold
     * It operates in two modes: sequential and random. In sequential requests, subsequent chunks are requested in
     * sequential ordering. The ordering must be checked and maintained between these chunks. In random mode, the buffer
     * can move to a certain frame out of the sequential order, but going forwards, it will come back to the sequential
     * mode.
     * @private
     */
    _loopRequestData() {
        if (!this._backgroundJobsAllowed) return;
        console.log('Buffer - loopRequestData');
        window.setTimeout(this._loopRequestData.bind(this),1000);

        // Avoid sending request for frames already requested in sequential mode
        if (this.isSequentialMode() && this._nextRequestFrameIndex <= this._lastRequestedFrameIndex) {
            return;
        }

        let frameIndex = this._nextRequestFrameIndex;
        if (!this.isSafetyStock() && !this._serverOutOfFrames) {
            this._dataRequester.requestData(this, this._requestToken, frameIndex, this._chunkSize);
            this._lastRequestedFrameIndex = frameIndex;
            if (!this.isSequentialMode()) this.setSequentialMode(true);
            console.log('Buffer - loopRequestData: safety stock not yet reached, send request to DataRequester, frameIndex = ' + frameIndex);
        }
    }

    _loopCleanup() {
        if (!this._backgroundJobsAllowed) return;
        console.log('Buffer - loopCleanup');
        window.setTimeout(this._loopCleanup.bind(this),1000);
        //console.log('Buffer - cleanLoop: historyThreshold=' + this._historyThreshold);
        //this._logStockLevel();
        let obsoleteSize = this.getUsedStockLevel() - this._historyThreshold;
        if (obsoleteSize > 0) {
            console.log('Buffer - loopCleanup: remove obsolete frames, number of frames removed: ' + obsoleteSize);
            this._frames.splice(0, obsoleteSize);
            this._currentIndex -= obsoleteSize;
        }
    }

    _logStockLevel() {
        console.log('Buffer - currentIndex=' + this._currentIndex);
        console.log('Buffer - lastIndex=' + this.getLastIndex());
        console.log('Buffer - current frameIndex', this._frames[this._currentIndex] ? this._frames[this._currentIndex].index : -1);
        console.log('Buffer - current stock level: ' + this.getUnusedStockLevel());
        console.log('Buffer - current used level: ' + this.getUsedStockLevel());
        console.log('Buffer - current token number: ' + this._requestToken);
    }
}