/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
/**
 * Browser compatibility notes
 *
 * Chrome:
 * - Does not support reference variable to point DOM elements, must use selectors (getElementsBy)
 *   otherwise the innerHTML and element attributes are not updated
 * - svg.setCurrentTime is not processed properly, must call svg to reload via innerHTML
 *
 * Dependencies:
 * utils.js (for Clazz)
 *
 * The animation page has four animation components:
 *
 * 1. The process model with tokens moving along the nodes and edges
 * 2. The timeline bar with a cursor moving along
 * 3. The circular progress bar showing the completion percentage for the log
 * 4. The digital clock running and showing the passing time
 *
 * These four components belong to four separate SVG document (<svg> tags).
 * Each SVG document has an internal SVG engine time
 *
 * The process model has nodes and edges which are SVG shapes. The animation shows tokens moving along these shapes.
 * Each token (or marker) belongs to a case in the log. A case is kept track in a LogCase object.
 * Each LogCase has multiple markers created and animated along certain nodes and edges
 * on the model in a continuous manner. Each marker is an SVG animateMotion element with a path attribute pointing
 * to the node or edge it has to move along. Two key attributes for animations are begin and dur (duration),
 * respectively when it begins and for how long. These attribute values are based on the time of the containing SVG document.
 *
 * The timeline bar has a number of equal slots configured in the configuration file, e.g. TimelineSlots = 120.
 * Each slot represents a duration of time in the event log, called SlotDataUnit, i.e. how many seconds per slot
 * Each slot also represents a duration of time in the animation engine, called SlotEngineUnit
 * For example, if the log spans a long period of time, SlotDataUnit will have a large value.
 * SlotEngineUnit is used to calculate the speed of the cursor movement on the timeline bar
 * SlotDataUnit is used to calculate the location of a specific event date on the timeline bar
 * timeCoef: the ratio of SlotDataUnit to SlotEngineUnit, i.e. 1 second in the engine = how many seconds in the data.
 * The starting point of time in all logs is set in json data sent from the server: startMs.
 *
 * The digital clock must keep running to show the clock jumping. It is governed by a timer property of
 * the controller. This timer is set to execute a function every interval of 100ms.
 * Starting from 0, it counts 100, 200, 300,...ms.
 * Call getCurrentTime() to the SVG document returns the current clock intervals = 100x (x = count)
 * The actual current time is: getCurrentTime()*timeCoef + startMs.
 */

/**
 * Animation controller
 *
 * ID of the timer used by the digital clock on the replay control panel
 * The timer is set whenever the replay is started or restarted, and cleared whenevenr it is paused.
 * The synchronization between the digital clock and internal timer of SVG documents is done vie this timer
 * because the timer will read the internal time of every SVG documents at every internal instant
 *
 */
'use strict';

let AnimationController = {

  construct: function(canvas) {

    this.jsonModel = null; // Parsed objects of the process model
    this.jsonServer = null; // Parsed objects returned from the server
    this.timeline = null;
    this.tracedates = null;
    this.logs = null;
    this.logNum = 0;

    this.canvas = canvas; // the editor canvas
    this.svgViewport = null; // initialized in Controller.reset
    this.svgDocs = [];
    this.svgMain = null; // initialized in Controller.reset
    this.svgTimeline = undefined;
    this.svgProgresses = [];

    this.clockTimer = null;
    this.startMs = 0;
    this.endMs = 120;
    this.slotNum = 120;
    this.slotDataMs = 1000;
    this.caseLabelsVisible = false;

    this.textFont = {size: '11', anchor: 'middle'};
    this.PLAY_CLS = 'ap-mc-icon-play';
    this.PAUSE_CLS = 'ap-mc-icon-pause';
    this.SHOW_OTHER_LOGS_TIMESPAN = false;
    this.apPalette = ['#84c7e3', '#bb3a50', '#3ac16d', '#f96100', '#FBA525'];
    this.timelineOffset = {
      x: 20, y: 20,
    };
  },

  pauseAnimations: function() {
    this.svgDocs.forEach(function(svgDoc) {
      svgDoc.pauseAnimations();
    });

    if (this.clockTimer) {
      clearInterval(this.clockTimer);
    }
  },

  /*
   * Only this method creates a timer.
   *
   * This timer is used to update the digital clock.
   * The mechanism is the digital clock reads SVG document current time every 100ms via updateClock() method.
   * This is pulling way.
   * In case of updating the clock once, it is safer to call updateClockOnce() method than updateClock(),
   * to avoid endless loop.
   */
  unpauseAnimations: function() {
    let me = this;

    this.svgDocs.forEach(function(svgDoc) {
      svgDoc.unpauseAnimations();
    });

    if (this.clockTimer) {
      clearInterval(this.clockTimer);
    }

    this.clockTimer = setInterval(function() {
      me.updateClock();
    }, 100);
  },

  reset: function(jsonRaw) {
    this.jsonServer = JSON.parse(jsonRaw);
    let {logs, timeline, tracedates} = this.jsonServer;
    this.logs = logs;
    this.logNum = logs.length;
    this.timeline = timeline;
    this.tracedates = tracedates;

    this.svgMain = this.canvas.getSVGContainer();
    this.svgViewport = this.canvas.getSVGViewport();
    this.svgTimeline = $j('div.ap-la-timeline > svg')[0];
    this.timelineEl = null;

    this.svgProgresses = [];
    this.svgDocs.clear();
    this.svgDocs.push(this.svgMain);
    this.svgDocs.push(this.svgTimeline);

    this.startMs = new Date(timeline.startDateLabel).getTime(); // Start date in milliseconds
    this.endMs = new Date(timeline.endDateLabel).getTime(); // End date in milliseconds
    this.totalMs = this.endMs - this.startMs;
    this.currentMs = this.startMs;
    this.totalEngineS = timeline.totalEngineSeconds; // Total engine seconds
    this.oriTotalEngineS = timeline.totalEngineSeconds;
    this.startPos = timeline.startDateSlot; // Start slot, starting from 0
    this.endPos = timeline.endDateSlot; // End slot, currently set at 120
    this.slotNum = timeline.timelineSlots; // The number of timeline vertical bars or (timeline.endDateSlot - timeline.startDateSlot)
    this.slotEngineMs = timeline.slotEngineUnit * 1000; // Animation milliseconds per slot
    this.slotEngineS = timeline.slotEngineUnit; // in seconds
    // Data milliseconds per slot
    this.slotDataMs = this.totalMs / this.slotNum;
    // Ratio for data ms / animation ms
    this.timeCoef = this.slotDataMs / this.slotEngineMs;

    this.slotWidth = 9;
    this.timelineWidth = this.slotNum * this.slotWidth;
    this.logIntervalSize = 5;
    this.logIntervalHeight = 7;
    this.logIntervalMargin = 8;

    // Reconstruct this.logCases
    this.logCases = [];
    let offsets = [3, -3, 9, -9, 12, -12, 15, -15];

    for (let i = 0; i < this.logNum; i++) {
      let log = logs[i];
      log.index = (i + 1); // set index
      this.logCases[i] = [];
      for (let j = 0; j < log.tokenAnimations.length; j++) {
        let tokenAnimation = log.tokenAnimations[j];
        let color = this.apPalette[i] || log.color;
        this.logCases[i][j] = new Ap.la.LogCase(
            this,
            tokenAnimation,
            color,
            tokenAnimation.caseId,
            offsets[i],
        );
      }
    }

    // Recreate progress indicators (deprecated)
    // let tokenE = this.svgMain.getElementById('progressAnimation');
    // if (tokenE != null) {
    //   this.svgMain.removeChild(tokenE);
    // }
    // let progressIndicatorE = this.createProgressIndicatorsDeprecated(logs, timeline);
    // $j("div#ap-la-progress svg")[0].append(progressIndicatorE);

    this.createProgressIndicators();
    this.createLogInfoPopups();
    this.createTimeline();
    this.createLogIntervals();
    this.createTicks();
    this.createCursor();
    this.createMetricTables();
    let me = this;
    window.onkeydown = function(event){
      if(event.keyCode === 32 || event.key === " ") {
        event.preventDefault();
        // event.stopPropagation();
        me.playPause();
      }
    };
    this.start();
  },

  // Add log intervals to timeline
  createLogIntervals: function() {
    let {
      timeline, logNum, slotWidth, timelineOffset, timelineEl, apPalette,
      logIntervalHeight, logIntervalMargin, logIntervalSize,
    } = this;
    let ox = timelineOffset.x, y = timelineOffset.y + logIntervalMargin; // Start offset

    for (let i = 0; i < logNum; i++) {
      let log = timeline.logs[i];
      let x1 = ox + slotWidth * log.startDatePos; // Magic number 10 is slotWidth / slotEngineS
      let x2 = ox + slotWidth * log.endDatePos;
      let style = 'stroke: ' + (apPalette[i] || log.color) + '; stroke-width: ' + logIntervalSize;
      let opacity = 0.8;
      new SVG.Line().plot(x1, y, x2, y).attr({style, opacity}).addTo(timelineEl);

      // Display date label at the two ends
      if (this.SHOW_OTHER_LOGS_TIMESPAN && log.startDatePos % 10 != 0) {
        let txt = log.startDateLabel.substr(0, 19);
        x = ox + slotWidth * log.startDatePos - 50;
        y += 5;
        new SVG.Text().plain(txt).font(this.textFont).attr({x, y}).addTo(timelineEl);
      }
      y += logIntervalHeight;
    }
  },

  createProgressIndicators: function(speedRatio) {
    let {logs, logNum} = this;
    let log, progressContainer, svgProgressEl, label;
    let svgProgress, svgProgresses = [];
    let progressWrapper = $j('#ap-la-progress');

    progressWrapper.empty();
    for (let i = 0; i < logNum; i++) {
      log = logs[i];
      svgProgress = $j(`<svg id="progressbar-${i}  xmlns="${SVG_NS}" viewBox="-10 0 20 40" ></svg>`);
      progressWrapper.append(
          $j(`<div id="progress-c-${i}"></div>`).append(
              svgProgress.append(this.createProgressIndicatorsForLog(i + 1, log, this.timeline, 0, 0, speedRatio)),
          ).append($j(`<div class="label">${log.filename}</div>`)),
      );
      svgProgress = svgProgress[0];
      svgProgresses.push(svgProgress);
      this.svgDocs.push(svgProgress);
    }

    this.svgProgresses = svgProgresses;
  },

  createLogInfoPopups: function() {
    let {logs, logNum} = this;
    let logInfo = $j('#ap-la-info-tip');
    let props = [
      {
        id: 'info-no',
        key: 'index',
      },
      {
        id: 'info-log',
        key: 'filename',
      },
      {
        id: 'info-traces',
        key: 'total',
      },
      {
        id: 'info-replayed',
        key: 'play',
        title: 'unplayTraces',
      },
      {
        id: 'info-reliable',
        key: 'reliable',
        title: 'unreliableTraces',
      },
      {
        id: 'info-fitness',
        key: 'exactTraceFitness',
      },
    ];

    function getProps(log) {
      props.forEach(function(prop) {
        $j('#' + prop.id).text(log[prop.key]).attr('title', log[prop.title || prop.key]);
      });
    }

    for (let i = 0; i < logNum; i++) {
      let pId = '#ap-la-progress-' + (i + 1);
      $j(pId).hover(
          (function(idx) {
            let log = logs[idx - 1];
            return function() {
              getProps(log);
              let {top, left} = $j(pId).offset();
              let bottom = `calc(100vh - ${top - 10}px)`;
              left += 20;
              logInfo.attr('data-log-idx', idx);
              logInfo.css({bottom, left});
              logInfo.show();
            };
          })(i + 1),
          function() {
            logInfo.hide();
          },
      );
    }
  },

  // Note: the engine time is changed in two situations:
  // 1. Change the token speed: go slower/faster
  // 2. Change the engine time to move the token forward/backward
  // In changing speed situation: the tokens (or markers) and clock are not updated. Their settings must be the same
  // after the engine time is changed, e.g. tokens must stay in the same position, clock must show the same datetime
  setCurrentTime: function(time, timeMs, changeSpeed, forController) {
    if (time < 0) { time = 0; }
    if (time > this.totalEngineS) { time = this.totalEngineS; }
    if (timeMs < this.startMs) { timeMs = this.startMs; }
    if (timeMs > this.endMs) { timeMs = this.endMs; }

    this.currentMs = timeMs

    this.svgDocs.forEach(function(svgDoc) {
      svgDoc.setCurrentTime(time);
    });

    if (!changeSpeed && !forController) {
      this.updateMarkersOnce();
    }

    if (!changeSpeed || forController) {
      this.updateClockOnce(timeMs);
    }
  },

  getCurrentTime: function() {
    return this.svgDocs[0].getCurrentTime();
  },

  /*
   * This method is used to read SVG document current time at every interval based on timer mechanism
   * It stops reading when SVG document time reaches the end of the timeline
   * The end() method is used for ending tasks for the replay completion scenario
   * Thus, the end() method should NOT create a loopback to this method.
   */
  updateClock: function() {
    this.updateMarkersOnce();

    // Original implementation -- checks for termination, updates clock view
    if (this.getCurrentTime() > (this.endPos * this.slotEngineMs) / 1000) {
      this.end();
    } else {
      this.updateClockOnce(this.getCurrentTime() * this.timeCoef * 1000 + this.startMs);
    }
  },

  /*
   * Update all tokens (LogCase objects) with the new current time
   */
  updateMarkersOnce: function() {
    let t = this.getCurrentTime();
    let dt = (this.timeCoef * 1000) / this.slotDataMs; // 1/this.SlotEngineUnit
    t *= dt; // Number of engine slots: t = t/this.SlotEngineUnit

    // Display all the log trace markers
    for (let logIdx = 0; logIdx < this.logs.length; logIdx++) {
      for (
          let tokenAnimIdx = 0;
          tokenAnimIdx < this.logs[logIdx].tokenAnimations.length;
          tokenAnimIdx++
      ) {
        this.logCases[logIdx][tokenAnimIdx].updateMarker(t, dt);
      }
    }
  },

  /*
   * This method is used to call to update the digital clock display.
   * This update is one-off only.
   * It is safer to call this method than calling updateClock() method which is for timer.
   * param - time: is the
   */
  updateClockOnce: function(time) {
    let dateEl = document.getElementById('date');
    let timeEl = document.getElementById('time');
    let locales = 'en-GB';
    let date = new Date();
    date.setTime(time);

    if (window.Intl) {
      dateEl.innerHTML = new Intl.DateTimeFormat(locales, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      }).format(date);
      timeEl.innerHTML = new Intl.DateTimeFormat(locales, {
        hour12: false,
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
      }).format(date);
    } else {
      // Fallback for browsers that don't support Intl (e.g. Safari 8.0)
      dateEl.innerHTML = date.toDateString();
      timeEl.innerHTML = date.toTimeString();
    }
  },

  start: function() {
    this.pause();
    this.setCurrentTime(this.startPos, this.startMs); // The startPos timing could be a little less than the first event timing in the log to accomodate the start event of BPMN
  },

  /*
   * This method is used to process tasks when replay reaches the end of the timeline
   */
  end: function() {
    this.pause();
    // console.log((this.endPos * this.slotEngineMs) / 1000, this.endPos * this.slotEngineMs * this.timeCoef + this.startMs);

    this.setCurrentTime((this.endPos * this.slotEngineMs) / 1000, this.endMs);
    this.updateClockOnce(this.endPos * this.slotEngineMs * this.timeCoef + this.startMs);
    if (this.clockTimer) {
      clearInterval(this.clockTimer);
    }
  },

  /**
   * Let L be the total length of an element where tokens are moved along (e.g. a sequence flow)
   * Let X be the current time duration set for the token to finish the length L (X is the value of dur attribute)
   * Let D be the distance that the token has done right before the speed is changed
   * Let Cx be the current engine time right before the speed is changed, e.g. Cx = svgDoc.getCurrentTime().
   * Let Y be the NEW time duration set for the token to travel through the length L.
   * Let Cy be the current engine time assuming that Y has been set and the token has finished the D distance.
   * Thus, the token can move faster or lower if Y < X or Y > X, respectively (Y is the new value of the dur attribute)
   * A requirement when changing the animation speed is all tokens must keep running from
   * the last position they were right before the speed change.
   * We have: D = Cy*L/Y = Cx*L/X => Cy = (Y/X)*Cx
   * Thus, for the token to start from the same position it was before the speed changes (i.e. dur changes from X to Y),
   * the engine time must be set to (Y/X)*Cx, where Cx = svgDoc.getCurrentTime().
   * Y/X is called the TimeRatio.
   * Instead of making changes to the distances, the user sets the speed through a speed slider control.
   * Each level represents a speed rate of the tokens
   * The SpeedRatio Sy/Sx is the inverse of the TimeRatio Y/X.
   * In the formula above: Cy = Cx/SpeedRatio, i.e. if the more the speed increases, the shorter the time
   * In summary, by setting the engine current time (svgDoc.setCurrentTime) and keeping the begin and dur
   * attributes of tokens unchangeed, the engine will automatically adjust the tokens to go faster or slower
   * @param speedRatio
   */
  changeSpeed: function(speedRatio) {
    if (!this.slotEngineMs) {
      return;
    }
    let isCurrentlyPlaying = this.isPlaying();
    this.pause();
    let currentTime = this.getCurrentTime();

    // Update Coefficients and units to ensure consistency
    // between the clock, timeline and SVG documents
    this.totalEngineS = this.totalEngineS / speedRatio;
    this.slotEngineMs = this.slotEngineMs / speedRatio;
    this.slotEngineS = this.slotEngineS / speedRatio;
    if (this.timeCoef) {
      this.timeCoef = this.slotDataMs / this.slotEngineMs;
    }
    let slotNum = this.slotNum;
    let slotEngineS = this.slotEngineS;

    // Update the speed of circle progress bar
    let curDur, curBegin, animateEl;
    let animations = $j('.progress-animation');

    let newTime = currentTime / speedRatio;
    let timeMs = this.slotSecondstoRealMs(newTime);

    for (let i = 0; i < animations.length; i++) {
      animateEl = animations[i];
      curDur = animateEl.getAttribute('dur');
      curDur = curDur.substr(0, curDur.length - 1);
      curBegin = animateEl.getAttribute('begin');
      curBegin = curBegin.substr(0, curBegin.length - 1);
      animateEl.setAttributeNS(null, 'dur', curDur / speedRatio + 's');
      animateEl.setAttributeNS(null, 'begin', curBegin / speedRatio + 's');
    }
    // this.createProgressIndicators(speedRatio);

    /*
    let cursorAnim = $j('#cursor-animation').get(0);
    curDur = cursorAnim.getAttribute('dur');
    curDur = curDur.substr(0, curDur.length - 1);
    curBegin = cursorAnim.getAttribute('begin');
    curBegin = curBegin.substr(0, curBegin.length - 1);
    cursorAnim.setAttributeNS(null, 'begin', '0s');
    cursorAnim.setAttributeNS(null, 'dur', slotNum * slotEngineS + 's');
    */
    // Update timeline cursor with the new speed
    if (this.cursorEl) {
      this.timelineEl.removeChild(this.cursorEl)
    }
    this.createCursor();

    // Update markers: this is needed the moment before
    // changing the engine time to update existing and new markers?
    this.updateMarkersOnce();

    // Now, change the engine time to auto ajust the tokens faster/slower
    // Note: update engine time without updating markers and the clock
    this.setCurrentTime(newTime, timeMs, true);

    if (isCurrentlyPlaying) {
      this.play();
    }
  },

  slotSecondstoRealMs: function (seconds) {
    return seconds * this.timeCoef * 1000 + this.startMs
  },

  // Move forward 1 slot
  fastforward: function() {
    if (this.getCurrentTime() >= (this.endPos * this.slotEngineMs) / 1000) {
      return;
    } else {
      let s = this.getCurrentTime() + (1 * this.slotEngineMs) / 1000
      this.setCurrentTime(s, this.slotSecondstoRealMs(s));
    }
  },

  // Move backward 1 slot
  fastBackward: function() {
    if (this.getCurrentTime() <= (this.startPos * this.slotEngineMs) / 1000) {
      return;
    } else {
      let s = this.getCurrentTime() - (1 * this.slotEngineMs) / 1000
      this.setCurrentTime(s, this.slotSecondstoRealMs(s));
    }
  },

  nextTrace: function() {
    if (this.getCurrentTime() >= (this.endPos * this.slotEngineMs) / 1000) {
      return;
    } else {
      let tracedates = this.tracedates; // assume that this.jsonServer.tracedates has been sorted in ascending order
      // search for the next trace date/time immediately after the current time
      let currentS = this.getCurrentTime();
      this.currentMs = this.slotSecondstoRealMs(currentS);
      for (let i = 0; i < tracedates.length; i++) {
        let traceS = (tracedates[i] - this.startMs) / (1000 * this.timeCoef);
        if (Math.round(currentS * 1000) < Math.round(traceS * 1000)) {
          this.setCurrentTime((tracedates[i] - this.startMs) / (1000 * this.timeCoef), tracedates[i]);
          return;
        }
      }
    }
  },

  previousTrace: function() {
    if (this.getCurrentTime() <= (this.startPos * this.slotEngineMs) / 1000) {
      return;
    } else {
      let tracedates = this.tracedates; //assume that this.jsonServer.tracedates has been sorted in ascending order
      // search for the previous trace date/time immediately before the current time
      let currentS = this.getCurrentTime();
      this.currentMs = this.slotSecondstoRealMs(currentS);
      for (let i = tracedates.length - 1; i >= 0; i--) {
        let traceS = (tracedates[i] - this.startMs) / (1000 * this.timeCoef);
        if (Math.round(currentS * 1000) > Math.round(traceS * 1000)) {
          this.setCurrentTime((tracedates[i] - this.startMs) / (1000 * this.timeCoef), tracedates[i]);
          return;
        }
      }
    }
  },

  canPause: function() {
    return $j('#pause').hasClass(this.PAUSE_CLS);
  },

  isPlaying: function() {
    return $j('#pause').hasClass(this.PAUSE_CLS);
  },

  setPlayPauseBtn: function(state) {
    const {PAUSE_CLS, PLAY_CLS} = this;
    const btn = $j('#pause');

    if (typeof state === 'undefined') {
      state = this.canPause(); // do toggle
    }
    if (state) {
      btn.removeClass(PAUSE_CLS).addClass(PLAY_CLS);
    } else {
      btn.removeClass(PLAY_CLS).addClass(PAUSE_CLS);
    }
  },

  pause: function() {
    this.pauseAnimations();
    this.setPlayPauseBtn(true);
  },

  play: function() {
    this.unpauseAnimations();
    this.setPlayPauseBtn(false);
  },

  playPause: function() {
    if (this.canPause()) {
      this.pause();
    } else {
      this.play();
    }
  },

  /*
   * Create progress indicator for one log
   * log: the log object (name, color, traceCount, progress, tokenAnimations)
   * x,y: the coordinates to draw the progress bar
   */
  createProgressIndicatorsForLog: function(logNo, log, timeline, x, y, speedRatio) {
    speedRatio = speedRatio || 1;
    let {values, keyTimes, begin, dur} = log.progress;
    let color = this.apPalette[logNo - 1] || log.color;
    let progress = new SVG.G().attr({
      id: 'ap-la-progress-' + logNo,
    }).node;

    let path = 'M ' + x + ',' + y + ' m 0, 0 a 20,20 0 1,0 0.00001,0';
    let pie = new SVG.Path().plot(path).attr({
      fill: color,
      'fill-opacity': 0.5,
      stroke: color,
      'stroke-width': '5',
      'stroke-dasharray': '0 126 126 0',
      'stroke-dashoffset': '1',
    }).node;

    let pieAnim = document.createElementNS(SVG_NS, 'animate');
    pieAnim.setAttributeNS(null, 'class', 'progress-animation');
    pieAnim.setAttributeNS(null, 'attributeName', 'stroke-dashoffset');
    pieAnim.setAttributeNS(null, 'values', values);
    pieAnim.setAttributeNS(null, 'keyTimes', keyTimes);
    pieAnim.setAttributeNS(null, 'begin', begin / speedRatio + 's');
    pieAnim.setAttributeNS(null, 'dur', dur / speedRatio + 's');
    pieAnim.setAttributeNS(null, 'fill', 'freeze');
    pieAnim.setAttributeNS(null, 'repeatCount', '1');
    pie.appendChild(pieAnim);
    progress.appendChild(pie);
    return progress;
  },

  createTick: function(x, y, tickSize, color, textToTickGap, dateTxt, timeTxt, timelineEl) {
    new SVG.Line().plot(x, y, x, y + tickSize).stroke({color, width: 0.5}).addTo(timelineEl);
    y -= textToTickGap;
    new SVG.Text().plain(timeTxt).font(this.textFont).attr({x, y}).addTo(timelineEl);
    y -= this.textFont.size * 1.5; // lineHeight
    new SVG.Text().plain(dateTxt).font(this.textFont).attr({x, y}).addTo(timelineEl);
  },

  createTicks: function() {
    // Add text and line for the bar

    let {
      slotNum, logNum, slotEngineS, slotWidth, slotDataMs, timelineEl, timelineOffset,
      logIntervalHeight, logIntervalMargin,
    } = this;
    let tickSize = logIntervalHeight * (logNum - 1) + 2 * logIntervalMargin;
    let textToTickGap = 5;
    let x = timelineOffset.x;
    let y = timelineOffset.y;
    let time = this.startMs;
    let color;
    let date, dateTxt, timeTxt;
    let skip;

    for (let i = 0; i <= slotNum; i++) {
      if (i % 10 == 0) {
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
        this.createTick(x, y, tickSize, color, textToTickGap, dateTxt, timeTxt, timelineEl);
      }
      x += slotWidth;
      time += slotDataMs;
    }
  },

  createCursor: function() {
    let {
      logNum,
      totalEngineS,
      svgTimeline,
      slotNum,
      slotWidth,
      slotEngineS,
      timelineWidth,
      timelineEl,
      timelineOffset,
    } = this;
    let x = timelineOffset.x;
    let y = timelineOffset.y + 5;
    let cursorEl;
    let me = this;

    let path = 'M0 0 L8 8 L8 25 L-8 25 L-8 8 Z';
    cursorEl = new SVG.Path().plot(path).attr({
      fill: '#FAF0E6',
      stroke: 'grey',
      style: 'cursor: move',
      transform: `translate(${x},${y})`,
    }).node;

    let cursorAnim = document.createElementNS(SVG_NS, 'animateTransform');
    cursorAnim.setAttributeNS(null, 'attributeName', 'transform');
    cursorAnim.setAttributeNS(null, 'type', 'translate');
    cursorAnim.setAttributeNS(null, 'id', 'cursor-animation');
    cursorAnim.setAttributeNS(null, 'begin', '0s');
    cursorAnim.setAttributeNS(null, 'dur', slotNum * slotEngineS + 's');
    cursorAnim.setAttributeNS(null, 'by', 1);
    cursorAnim.setAttributeNS(null, 'from', x + ',' + y);
    cursorAnim.setAttributeNS(null, 'to', x + slotNum * slotWidth + ',' + y);
    cursorAnim.setAttributeNS(null, 'fill', 'freeze');

    cursorEl.appendChild(cursorAnim);
    timelineEl.appendChild(cursorEl);

    this.cursorEl = cursorEl;

    // Control dragging of the timeline cursor
    let dragging = false;
    let isPlayingBeforeDrag = null;

    cursorEl.addEventListener('mousedown', startDragging.bind(this));
    svgTimeline.addEventListener('mousemove', onDragging.bind(this));
    svgTimeline.addEventListener('mouseup', stopDragging.bind(this));
    svgTimeline.addEventListener('mouseleave', stopDragging.bind(this));

    function startDragging(evt) {
      isPlayingBeforeDrag = me.isPlaying();
      evt.preventDefault();
      dragging = true;
      me.pause();
    }

    function onDragging(evt) {
      evt.preventDefault();
      if (dragging) {
        let time = getTimeFromMouseX(evt);
        this.setCurrentTime(time, this.slotSecondstoRealMs(time), null, true); // for controller only
      }
    }

    // Only update SVG current time when the dragging finishes to avoid heavy on-the-fly updates
    // After every call to setCurrentTime, the SVG coordinate is moved to the new position of the cursor
    // As calling setCurrentTime will also update the cursor's position, we have to move the cursor
    // back to its original position before the call to setCurrentTime, otherwise it is moved two times
    // The dragging flag is checked to avoid doing two times for mouseup and mouseleave events
    function stopDragging(evt) {
      if (!dragging) return; // Avoid doing the below two times
      if (evt.type == 'mouseleave' && dragging) {
        return;
      }
      dragging = false;
      let time = getTimeFromMouseX(evt);
      this.setCurrentTime(time, this.slotSecondstoRealMs(time));
      if (isPlayingBeforeDrag) {
        me.play();
      }
    }

    function getTimeFromMouseX(evt) {
      let x = getSVGMousePosition(evt).x;
      let dx = x - me.timelineOffset.x;
      return (dx / me.timelineWidth) * me.totalEngineS;
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

  },
  /*
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
   */
  createTimeline: function() {
    // Create the main timeline container group
    let timelineEl = new SVG.G().attr({
      id: 'timeline',
      style: '-webkit-touch-callout: none; -webkit-user-select: none; -khtml-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none',
    }).node;
    this.timelineEl = timelineEl;
    this.svgTimeline.append(timelineEl);
    return timelineEl;
  },

  setCaseLabelsVisible: function(visible) {
    if (this.caseLabelsVisible != visible) {
      this.caseLabelsVisible = visible;
      this.updateMarkersOnce();
    }
  },

  // Deprecated section

  /*
   * <g id="progressAnimation"><g class='progress'><path><animate class='progressanimation'>
   * logs: array of log object
   * timeline: object containing timeline information
   */
  createProgressIndicatorsDeprecated: function(logs, timeline) {
    let progressE = document.createElementNS(SVG_NS, 'g');
    progressE.setAttributeNS(null, 'id', 'progressAnimation');

    let x = 30;
    let y = 20;
    for (let i = 0; i < logs.length; i++) {
      progressE.appendChild(
          this.createProgressIndicatorsForLog(i + 1, logs[i], timeline, x, y),
      );
      x += 150;
    }
    return progressE;
  },

  /*
   * Create progress indicator for one log
   * log: the log object (name, color, traceCount, progress, tokenAnimations)
   * x,y: the coordinates to draw the progress bar
   */
  createProgressIndicatorsForLogDeprecated: function(logNo, log, timeline, x, y) {
    let pieE = document.createElementNS(SVG_NS, 'g');
    pieE.setAttributeNS(null, 'id', 'ap-la-progress-' + logNo);
    pieE.setAttributeNS(null, 'class', 'progress');

    let color = this.apPalette[logNo - 1] || log.color;
    let pathEl = document.createElementNS(SVG_NS, 'path');
    pathEl.setAttributeNS(
        null,
        'd',
        'M ' + x + ',' + y + ' m 0, 0 a 20,20 0 1,0 0.00001,0',
    );
    // pathEl.setAttributeNS(null,"fill","#CCCCCC");
    pathEl.setAttributeNS(null, 'fill', color);
    pathEl.setAttributeNS(null, 'fill-opacity', 0.5);
    pathEl.setAttributeNS(null, 'stroke', color);
    pathEl.setAttributeNS(null, 'stroke-width', '5');
    pathEl.setAttributeNS(null, 'stroke-dasharray', '0 126 126 0');
    pathEl.setAttributeNS(null, 'stroke-dashoffset', '1');

    let animateEl = document.createElementNS(SVG_NS, 'animate');
    animateEl.setAttributeNS(null, 'class', 'progress-animation');
    animateEl.setAttributeNS(null, 'attributeName', 'stroke-dashoffset');
    animateEl.setAttributeNS(null, 'values', log.progress.values);
    animateEl.setAttributeNS(null, 'keyTimes', log.progress.keyTimes);
    //console.log("values:" + log.progress.values);
    //console.log("keyTimes:" + log.progress.keyTimes);
    animateEl.setAttributeNS(null, 'begin', log.progress.begin + 's');
    //animateEl.setAttributeNS(null,"dur",timeline.slotNum*this.slotEngineMs/1000 + "s");
    animateEl.setAttributeNS(null, 'dur', log.progress.dur + 's');
    animateEl.setAttributeNS(null, 'fill', 'freeze');
    animateEl.setAttributeNS(null, 'repeatCount', '1');

    pathEl.appendChild(animateEl);

    // let textE = document.createElementNS(SVG_NS,"text");
    // textE.setAttributeNS(null,"x", x);
    // textE.setAttributeNS(null,"y", y - 10);
    // textE.setAttributeNS(null,"text-anchor","middle");
    // let textNode = document.createTextNode(log.name);
    // textE.appendChild(textNode);

    // let tooltip = document.createElementNS(SVG_NS,"title");
    // tooltip.appendChild(document.createTextNode(log.name));
    // textE.appendChild(tooltip);

    pieE.appendChild(pathEl);
    // pieE.appendChild(textE);

    return pieE;
  },

  createMetricTables: function() {
    let logs = this.logs;
    // Show metrics for every log
    let metricsTable = $j('#metrics_table')[0];
    for (let i = 0; i < logs.length; i++) {
      let row = metricsTable.insertRow(i + 1);
      let cellLogNo = row.insertCell(0);
      let cellLogName = row.insertCell(1);
      let cellTotalCount = row.insertCell(2);
      let cellPlayCount = row.insertCell(3);
      let cellReliableCount = row.insertCell(4);
      let cellExactFitness = row.insertCell(5);
      //let cellExactFitnessFormulaTime = row.insertCell(5);
      //let cellApproxFitness = row.insertCell(6);
      //let cellApproxFitnessFormulaTime = row.insertCell(7);
      //let cellAlgoTime = row.insertCell(8);

      cellLogNo.innerHTML = i + 1;
      cellLogNo.style.backgroundColor = logs[i].color;
      cellLogNo.style.textAlign = 'center';

      if (logs[i].filename.length > 50) {
        cellLogName.innerHTML = logs[i].filename.substr(0, 50) + '...';
      } else {
        cellLogName.innerHTML = logs[i].filename;
      }
      cellLogName.title = logs[i].filename;
      cellLogName.style.font = '1em monospace';
      //cellLogName.style.backgroundColor = logs[i].color;

      cellTotalCount.innerHTML = logs[i].total;
      cellTotalCount.style.textAlign = 'center';
      cellTotalCount.style.font = '1em monospace';

      cellPlayCount.innerHTML = logs[i].play;
      cellPlayCount.title = logs[i].unplayTraces;
      cellPlayCount.style.textAlign = 'center';
      cellPlayCount.style.font = '1em monospace';

      cellReliableCount.innerHTML = logs[i].reliable;
      cellReliableCount.title = logs[i].unreliableTraces;
      cellReliableCount.style.textAlign = 'center';
      cellReliableCount.style.font = '1em monospace';

      cellExactFitness.innerHTML = logs[i].exactTraceFitness;
      cellExactFitness.style.textAlign = 'center';
      cellExactFitness.style.font = '1em monospace';

      //cellExactFitnessFormulaTime.innerHTML = logs[i].exactFitnessFormulaTime;
      //cellApproxFitness.innerHTML = logs[i].approxTraceFitness;
      //cellApproxFitnessFormulaTime.innerHTML = logs[i].approxFitnessFormulaTime;
      //cellAlgoTime.innerHTML = logs[i].algoTime;
    }
  },

};

Ap.la.AnimationController = Clazz.extend(AnimationController);
