import * as SVG from "@svgdotjs/svg.js";
//import * as moment from "moment";
const moment = require('moment-timezone');
import {AnimationEvent, AnimationEventType} from "./animationEvents";

/**
 * TimelineAnimation shows a timeline and a running tick when the animation is going on.
 * There are two intervals on the timeline:
 *  [TimelineStart, TimelineEnd]: this is the whole timeline and animation movies, including the Start and End events.
 *  [LogStart, LogEnd]: this is the log data interval, excluding the Start and End events.
 *  The timeline only shows dates of log intervals although it plays the whole timeline interval.
 */
export default class TimelineAnimation {
    /**
     * @param {LogAnimation} animation
     * @param {String} uiContainerId: id of the SVG element
     * @param {Array} caseCountsByFrames
     */
    constructor(animation, uiContainerId, caseCountsByFrames) {
        this.animation = animation;
        this.animationContext = animation.getAnimationContext();

        // Animation params
        this.totalEngineS = this.animationContext.getLogicalTimelineMax();
        this.slotNum = this.animationContext.getTimelineSlots();
        this.endPos = this.slotNum;
        this.currentSpeedLevel = 1.0;

        // Timeline settings: all is based on timelineWidth and timelineOffset
        this.timelineWidth = $j(window).width() - 490; // use hard-coded number here because sizes are set fixed in CSS and HTML
        this.startGap = this.animationContext.getStartGapRatio()*this.timelineWidth;
        this.endGap = this.animationContext.getEndGapRatio()*this.timelineWidth;
        this.timelineOffset = { x: 20, y: 40,};
        this.timelineStartX = this.timelineOffset.x;
        this.timelineEndX = this.timelineStartX + this.timelineWidth;
        this.logStartX = this.timelineStartX + this.startGap;
        this.logEndX = this.timelineEndX - this.endGap;
        this.cursorY = this.timelineOffset.y + 5;
        this.slotWidth = (this.timelineWidth - this.startGap - this.endGap)/this.slotNum;

        // Log Interval settings
        this.logIntervalSize = 5;
        this.logIntervalHeight = 7;
        this.logIntervalMargin = 8;
        this.textFont = {size: '11', anchor: 'middle'};

        // Create the main timeline
        this.containerId = uiContainerId;
        this.svgTimeline = $j('#' + uiContainerId)[0];
        this.timelineEl = this._createTimelineElement();
        this.timelineCenterLine = this._createTimelineCenterLine(this.timelineStartX, this.timelineEndX, 'solid', 'black');
        this.timelineCenterLogLine = this._createTimelineCenterLine(this.logStartX, this.logEndX,'transparent', 'none');
        this.timelineEl.appendChild(this.timelineCenterLine);
        this.timelineEl.appendChild(this.timelineCenterLogLine);
        this.svgTimeline.append(this.timelineEl);

        this._listeners = [];
        let canvas = this._createCanvasElement(uiContainerId);
        this.canvasContext = canvas[0].getContext('2d');
        this._initializeComponents(caseCountsByFrames);
    }

    destroy() {
        $j('#' + this.containerId).empty();
    }

    _initializeComponents(caseCountsByFrames) {
        this._addTimelineDistribution(caseCountsByFrames);
        this._addLogIntervals();
        this._addTicks();
        this._addCursor();
    }

    /**
     * @param {Number} frameIndex
     */
    updateCursor(frameIndex) {
        let point = this._getTimelinePointAtFrameIndex(frameIndex);
        let x = point.x;
        let y = this.cursorY;
        this.cursorEl.setAttribute('transform', `translate(${x},${y})`);
    }

    freezeControls() {
        this.cursorEl.style.pointerEvents = "none";
    }

    unFreezeControls() {
        this.cursorEl.style.pointerEvents = "auto";
    }

    /**
     * <g id="timeline">
     *   <-- timeline bar -->
     *   <line>
     *     <text>
     *     ...
     *   <line>

     *   <text>
     *     <!-- timeline cursor -->
     *     <rect>
     *       <animationMotion>
     *
     * Use: this.slotNum, this.slotEngineMs
     * @return HTMLElement
     */
    _createTimelineElement() {
        // Create the main timeline container group
        let timelineEl = new SVG.G().attr({
            id: 'timeline',
            style: '-webkit-touch-callout: none; -webkit-user-select: none; -khtml-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none',
        }).node;
        return timelineEl;
    }

    _createTimelineCenterLine(startX, endX, fillSetting, lineColor) {
        let timelinePathY = this.timelineOffset.y + this.logIntervalMargin;
        let timelinePath = 'm' + startX + ',' + timelinePathY + ' L' + endX + ',' + timelinePathY;
        let timelineCenterLine = new SVG.Path().plot(timelinePath).attr({fill: fillSetting, stroke: lineColor}).node;
        return timelineCenterLine;
    }

    /**
     * @param {Number} percentage: percentage (0..1) from the start of the center line
     * @returns {SVGPoint | DOMPoint}
     * @private
     */
    _getTimelinePointAtPercentage(percentage) {
        if (!this.timelineCenterLine) return {x:0, y:0};
        let point = this.timelineCenterLine.getPointAtLength(this.timelineCenterLine.getTotalLength()*percentage);
        return point;
    }

    /**
     * @param {Number} frameIndex
     * @returns {SVGPoint | DOMPoint}
     * @private
     */
    _getTimelinePointAtFrameIndex(frameIndex) {
        if (!this.timelineCenterLine) return {x:0, y:0};
        return this._getTimelinePointAtPercentage(frameIndex/this.animationContext.getTotalNumberOfFrames());
    }

    _addTick(x, y, tickSize, color, textToTickGap, dateTxt, timeTxt) {
        if (!this.timelineEl) return;
        new SVG.Line().plot(x, y, x, y + tickSize).stroke({color, width: 0.5}).addTo(this.timelineEl);
        y -= textToTickGap;
        new SVG.Text().plain(timeTxt).font(this.textFont).attr({x, y}).addTo(this.timelineEl);
        y -= this.textFont.size * 1.5; // lineHeight
        new SVG.Text().plain(dateTxt).font(this.textFont).attr({x, y}).addTo(this.timelineEl);
    }

    _addTicks() {
        // Add text and line for the bar
        let tickSize = this.logIntervalHeight * (this.animation.getNumberOfLogs() - 1) + 2 * this.logIntervalMargin;
        let textToTickGap = 5;
        let x = this.logStartX;
        let y = this.timelineOffset.y;
        let time = this.animationContext.getLogStart();
        let color, date, dateTxt, timeTxt;

        for (let i = 0; i <= this.slotNum; i++) {
            if (i % 10 === 0) {
                date = moment.tz(time, this.animationContext.getTimezone());
                dateTxt = date.format('D MMM YY');
                timeTxt = date.format('HH:mm:ss');
                color = 'grey';
                this._addTick(x, y, tickSize, color, textToTickGap, dateTxt, timeTxt, this.timelineEl);
            }
            x += this.slotWidth;
            time += this.animationContext.getLogSlotTime();
            if (i == this.slotNum) time = this.animationContext.getLogEnd();
        }
    }

    _createCanvasElement(containerId) {
        let div = $j('<div style="position:absolute; background:none; width:100%"></div>');
        let canvas = $j('<canvas style="background:none; pointer-events:none; width:100%"></canvas>');
        div.append(canvas);
        $j('#' + containerId).parent().parent().prepend(div);
        return canvas;
    }

    /**
     * Draw a case distribution on the timeline
     * @param {Array} caseCountsByFrames: data with case count for every frame.
     * @private
     */
    _addTimelineDistribution(caseCountsByFrames) {
        // Set up canvas
        let timelineBox = this.svgTimeline.getBoundingClientRect();
        let ctx = this.canvasContext;
        ctx.canvas.width = timelineBox.width;
        ctx.canvas.height = timelineBox.height;
        ctx.canvas.x = timelineBox.x;
        ctx.canvas.y = timelineBox.y;
        ctx.strokeStyle = '#D3D3D3';
        ctx.lineWidth = 2;
        let matrix = this.timelineCenterLine.getCTM();
        ctx.setTransform(matrix.a, matrix.b, matrix.c, matrix.d, matrix.e, matrix.f);

        // Draw distribution
        if (caseCountsByFrames) {
            const MAX_HEIGHT = ctx.canvas.height/4;
            let maxCount = 0;
            for (let count of Object.values(caseCountsByFrames)) {
                if (typeof(count) != 'function' && maxCount < count) {
                    maxCount = count;
                }
            }
            let totalFrames = caseCountsByFrames.length;
            for (let i=0;i<totalFrames;i++) {
                let distancePercent = i/totalFrames;
                let point = this._getTimelinePointAtPercentage(distancePercent);
                let height = (caseCountsByFrames[i]/maxCount)*MAX_HEIGHT;
                ctx.beginPath();
                ctx.moveTo(point.x, point.y);
                ctx.lineTo(point.x, point.y - height);
                ctx.stroke();
            }
        }
    }

    _addCursor() {
        if (!this.timelineEl) return;
        if (this.cursorEl) this.timelineEl.removeChild(this.cursorEl);

        let x = this.timelineStartX;
        let y = this.cursorY;
        let cursorEl;
        let me = this;

        let path = 'M0 0 L8 8 L8 25 L-8 25 L-8 8 Z';
        cursorEl = new SVG.Path().plot(path).attr({
            fill: '#FAF0E6',
            stroke: 'grey',
            style: 'cursor: move',
            transform: `translate(${x},${y})`,
        }).node;

        this.timelineEl.appendChild(cursorEl);
        this.cursorEl = cursorEl;

        // Control dragging of the timeline cursor
        let dragging = false;
        let isPlayingBeforeDrag = false;
        let curX;
        let curY = this.cursorY;

        cursorEl.addEventListener('mousedown', startDragging.bind(this));
        this.svgTimeline.addEventListener('mousemove', onDragging.bind(this));
        this.svgTimeline.addEventListener('mouseup', stopDragging.bind(this));
        this.svgTimeline.addEventListener('mouseleave', stopDragging.bind(this));

        function startDragging(evt) {
            let selectedElement = evt.target;
            evt.preventDefault();
            isPlayingBeforeDrag = me.animation.isPlaying();
            dragging = true;
            me.animation.pause();
        }

        function onDragging(evt) {
            if (dragging) {
                curX = getSVGMousePosition(evt).x;
                if (curX >= me.timelineStartX && curX <= me.timelineEndX) {
                    let logicalTime = getLogicalTimeFromMouseX(curX);
                    this.cursorEl.setAttribute('transform', `translate(${curX},${curY})`);
                    this._notifyAll(new AnimationEvent(AnimationEventType.TIMELINE_CURSOR_MOVING,
                                            {logicalTime: logicalTime}));
                }
            }
        }

        function stopDragging(evt) {
            evt.preventDefault();
            if (!dragging) return; // Avoid doing the below two times
            if (evt.type === 'mouseleave' && dragging) {
                return;
            }
            dragging = false;
            let logicalTime = getLogicalTimeFromMouseX(curX);
            console.log('stopDragging goto', curX, logicalTime);
            me.animation.goto(logicalTime);
            if (isPlayingBeforeDrag) {
                me.animation.unPause();
            }
        }

        function getLogicalTimeFromMouseX(curX) {
            let dx = curX - me.timelineStartX;
            return (dx / me.timelineWidth) * me.animationContext.getLogicalTimelineMax();
        }

        // Convert from screen coordinates to SVG document coordinates
        function getSVGMousePosition(evt) {
            let svg = me.svgTimeline;
            let matrix = svg.getScreenCTM().inverse();
            let point = svg.createSVGPoint();
            point.x = evt.clientX;
            point.y = evt.clientY;
            return point.matrixTransform(matrix);
        }
    }

    _addLogIntervals() {
        if (!this.timelineEl) return;
        let ctx = this.animationContext;
        let ox = this.logStartX, y = this.timelineOffset.y + this.logIntervalMargin + 5; // Start offset
        let logSummaries = this.animation.getLogSummaries();
        for (let logIndex = 0; logIndex < logSummaries.length; logIndex++) {
            let log = logSummaries[logIndex];
            let logStartTime = new Date(log.startLogDateLabel).getTime();
            let logEndTime = new Date(log.endLogDateLabel).getTime();
            let x1 = ox + this.slotWidth * (logStartTime - ctx.getLogStart())/ctx.getLogSlotTime();
            let x2 = ox + this.slotWidth * (logEndTime - ctx.getLogStart())/ctx.getLogSlotTime();;
            let id = `ap-la-timeline-${logIndex}`;
            let style = 'stroke: ' + this.animation.getLogColor(logIndex) + '; stroke-width: ' + this.logIntervalSize;
            let opacity = 0.8;
            new SVG.Line().plot(x1, y, x2, y).attr({id, style, opacity}).addTo(this.timelineEl);
            y += this.logIntervalHeight;
        }
    }

    /**
     * Reorder the logs in the timeline
     *
     * @param {array} logOrder - A new log order (trigger by sortable)
     */
    arrangeLogTimelines(logOrder) {
        let logSummaries = this.animation.getLogSummaries();
        let y = this.timelineOffset.y + this.logIntervalMargin
        for (let i = 0; i < logSummaries.length; i++) {
            let logIndex = logOrder[i];
            let id = `ap-la-timeline-${logIndex}`;
            let timeline = document.getElementById(id);
            if (timeline) {
                timeline.setAttribute('y1', y);
                timeline.setAttribute('y2', y);
            }
            let log = logSummaries[logIndex];
            y += this.logIntervalHeight;
        }
    }

    registerListener(listener) {
        this._listeners.push(listener);
    }

    /**
     * @param {AnimationEvent} event
     */
    _notifyAll(event) {
        this._listeners.forEach(function(listener){
            listener.handleEvent(event);
        })
    }
}