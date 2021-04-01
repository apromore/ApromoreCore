import * as SVG from "@svgdotjs/svg.js";
import * as moment from "moment";
import {AnimationEvent, AnimationEventType} from "./animationEvents";

/**
 * TimelineAnimation shows a timeline and a running tick when the animation is going on.
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

        // Parameters
        this.totalEngineS = this.animationContext.getLogicalTimelineMax();
        this.slotNum = this.animationContext.getTimelineSlots();
        this.endPos = this.slotNum;
        this.slotEngineS = this.animationContext.getLogicalSlotTime(); // in seconds
        this.logMillis = animation.getAnimationContext().getLogEndTime() - animation.getAnimationContext().getLogStartTime();
        this.slotDataMs = this.logMillis / this.slotNum;
        this.timeCoef = this.animationContext.getTimelineRatio();
        this.currentSpeedLevel = 1.0;

        // Visual settings
        this.slotWidth = 9;
        this.timelineWidth = this.slotNum * this.slotWidth;
        this.timelineOffset = { x: 20, y: 20,};
        this.startX = this.timelineOffset.x;
        this.endX = this.startX + this.timelineWidth;
        this.cursorY = this.timelineOffset.y + 5;
        this.logIntervalSize = 5;
        this.logIntervalHeight = 7;
        this.logIntervalMargin = 8;
        this.SHOW_OTHER_LOGS_TIMESPAN = false;
        this.textFont = {size: '11', anchor: 'middle'};

        // Create the main timeline
        this.containerId = uiContainerId;
        this.svgTimeline = $j('#' + uiContainerId)[0];
        this.timelineEl = this._createTimelineElement()
        this.timelineCenterLine = this._createTimelineCenterLine()
        this.timelineCenterLineY = this.timelineOffset.y + this.logIntervalMargin;
        this.timelineEl.appendChild(this.timelineCenterLine);
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
     * @param logicalTime: in seconds
     * @returns {Number}: in milliseconds
     */
    getLogTimeFromLogicalTime(logicalTime) {
        return this.animationContext.getLogStartTime() + logicalTime * this.animationContext.getTimelineRatio() * 1000;
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

    _createTimelineCenterLine() {
        let timelinePathY = this.timelineOffset.y + this.logIntervalMargin;
        let timelinePath = 'm' + this.startX + ',' + timelinePathY + ' L' + this.endX + ',' + timelinePathY;
        let timelineCenterLine = new SVG.Path().plot(timelinePath).attr({fill: 'transparent', stroke: 'none'}).node;
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
        let x = this.timelineOffset.x;
        let y = this.timelineOffset.y;
        let time = this.animationContext.getLogStartTime();
        let color;
        let date, dateTxt, timeTxt;
        let skip;

        for (let i = 0; i <= this.slotNum; i++) {
            if (i % 10 === 0) {
                date = moment(time);
                dateTxt = date.format('D MMM YY');
                timeTxt = date.format('H:mm:ss');
                color = 'grey';
                skip = false;
            } else {
                dateTxt = '';
                timeTxt = '';
                color = '#e0e0e0';
                skip = true;
            }
            if (!skip) {
                this._addTick(x, y, tickSize, color, textToTickGap, dateTxt, timeTxt, this.timelineEl);
            }
            x += this.slotWidth;
            time += this.slotDataMs;
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

        let x = this.timelineOffset.x;
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
                if (curX >= me.startX && curX <= me.endX) {
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
            let dx = curX - me.timelineOffset.x;
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
        let ox = this.timelineOffset.x, y = this.timelineOffset.y + this.logIntervalMargin; // Start offset
        let logSummaries = this.animation.getLogSummaries();
        for (let logIndex = 0; logIndex < logSummaries.length; logIndex++) {
            let log = logSummaries[logIndex];
            let x1 = ox + this.slotWidth * log.startDatePos;
            let x2 = ox + this.slotWidth * log.endDatePos;
            let id = `ap-la-timeline-${logIndex}`;
            let style = 'stroke: ' + this.animation.getLogColor(logIndex) + '; stroke-width: ' + this.logIntervalSize;
            let opacity = 0.8;
            new SVG.Line().plot(x1, y, x2, y).attr({id, style, opacity}).addTo(this.timelineEl);

            // Display date label at the two ends
            if (this.SHOW_OTHER_LOGS_TIMESPAN && log.startDatePos % 10 !== 0) {
                let txt = log.startDateLabel.substr(0, 19);
                let x = ox + this.slotWidth * log.startDatePos - 50;
                y += 5;
                new SVG.Text().plain(txt).font(this.textFont).attr({x, y}).addTo(this.timelineEl);
            }
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
            if (this.SHOW_OTHER_LOGS_TIMESPAN && log.startDatePos % 10 !== 0) {
                y += 5;
            }
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