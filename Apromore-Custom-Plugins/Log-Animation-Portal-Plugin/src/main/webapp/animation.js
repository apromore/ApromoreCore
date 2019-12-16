/*
 * Note browser compatibility
 * Chome: does not support reference variable to point to DOM elements, must use selectors (getElementsBy...)
 *          otherwise the innerHTML is not updated, element attribute is not updated
 * Chome: svg.setCurrentTime is not processed properly, must call svg to reload via innerHTML
 */

var $j = jQuery.noConflict();
$j.browser = {};
$j.browser.mozilla = /mozilla/.test(navigator.userAgent.toLowerCase()) && !/webkit/.test(navigator.userAgent.toLowerCase());
$j.browser.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
$j.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());
$j.browser.msie = /msie/.test(navigator.userAgent.toLowerCase());

var svgNS = "http://www.w3.org/2000/svg";
var xlinkNS = "http://www.w3.org/1999/xlink";
var jsonModel; //contains parsed objects of the process model
var jsonServer; //contains parsed objects returned from the server
var caseLabelsVisible = false;

var svgDocument;  // initialized in Controller.reset
var svgDocumentG;  // initialized in Controller.reset

var lastSliderValue = 11;
var speedRatio;

var timelineOffset = 5

var apPalette = [
    "#84c7e3",
    "#bb3a50",
    "#3ac16d",
    "#f96100"
]

function switchPlayPause(e) {
    controller.switchPlayPause(e);
}

function fastForward() {
    controller.fastforward();
}

function fastBackward() {
    controller.fastBackward();
}

function nextTrace() {
    controller.nextTrace();
}

function previousTrace() {
    controller.previousTrace();
}

function start(e) {
    //lastSliderValue = 11;
    //$j("#slider2").slider({ value: 11 });
    //lastSliderValue = $j("#slider2").slider("value");
    controller.start();
}

function end(e) {
    //lastSliderValue = 1;
    //$j("#rate_slider").slider({ value: 1 });
    //lastSliderValue = j("#rate_slider").slider("value");
    controller.end();
}

function toggleCaseLabelVisibility() {
    var input = $j("input#toggleCaseLabelVisibility")[0];
    controller.setCaseLabelsVisible(input.checked);
}

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

/* ******************************************************************
 * Return an object with four points corresponding to four corners of the input rect element
 * These are coordinates within the SVG document viewport
 * Input:
 * rect: the rectangle element
 * container: the containing SVGElement that contains the transformation affecting the rectangle
 * Return object has four points: nw, ne, se, sw, cc (center) (each with x,y attribute)
 * ******************************************************************/
function getViewportPoints(rect, container){
    var svg = svgDocument;
    var matrix = container.transform.baseVal.getItem(0).matrix;
    var corners = {
        nw: svg.createSVGPoint().matrixTransform(matrix),
        ne: svg.createSVGPoint().matrixTransform(matrix),
        sw: svg.createSVGPoint().matrixTransform(matrix),
        se: svg.createSVGPoint().matrixTransform(matrix),
        cc: svg.createSVGPoint().matrixTransform(matrix) };

    var bbox = rect.getBBox();
    corners.ne.x += bbox.width;
    corners.se.x += bbox.width;
    corners.se.y += bbox.height;
    corners.sw.y += bbox.height;
    corners.cc.x += bbox.width/2;
    corners.cc.y += bbox.height/2;

    return corners;
}

/*
 * input: group element <g>
 * output: SVGPoint
 */
function toViewportCoords(groupE) {
    var svg = svgDocument;
    var pt  = svg.createSVGPoint();
    var matrix  = groupE.getScreenCTM();
    rect = groupE.getBBox();
    pt.x = rect.x;
    pt.y = rect.y;
    return pt.matrixTransform(matrix);
}

function drawCoordinateOrigin() {
        var svg = svgDocument;
        var pt  = svg.createSVGPoint();
        //var matrix  = groupE.getCTM();
        //var rect = groupE.getBBox();
        pt.x = svg.x.animVal.value;
        pt.y = svg.y.animVal.value;
        //console.log("SVG Document Origin: x="+ pt.x + " y=" + pt.y);
        //pt = pt.matrixTransform(matrix);

        var lineX = document.createElementNS(svgNS,"line");
        lineX.setAttributeNS(null,"x1",pt.x);
        lineX.setAttributeNS(null,"y1",pt.y);
        lineX.setAttributeNS(null,"x2",pt.x+50);
        lineX.setAttributeNS(null,"y2",pt.y);
        lineX.setAttributeNS(null,"stroke","red");
        lineX.setAttributeNS(null,"stroke-width","5");

        var lineY = document.createElementNS(svgNS,"line");
        lineY.setAttributeNS(null,"x1",pt.x);
        lineY.setAttributeNS(null,"y1",pt.y);
        lineY.setAttributeNS(null,"x2",pt.x);
        lineY.setAttributeNS(null,"y2",pt.y+50);
        lineY.setAttributeNS(null,"stroke","red");
        lineY.setAttributeNS(null,"stroke-width","5");

        //alert(rect.x + " " + rect.y);

        svg.appendChild(lineX);
        svg.appendChild(lineY);
}

 function drawProcessModelOrigin() {
        var svg = svgDocument;
        var pt  = svg.createSVGPoint();
        var matrix  = groupE.getCTM();
        var rect = groupE.getBBox();
        pt.x = rect.x;
        pt.y = rect.y;
        //alert(pt.x + " " + pt.y);
        pt = pt.matrixTransform(matrix);
        //console.log("Process Model Origin: x="+ pt.x + " y=" + pt.y);

        var lineX = document.createElementNS(svgNS,"line");
        lineX.setAttributeNS(null,"x1",pt.x);
        lineX.setAttributeNS(null,"y1",pt.y);
        lineX.setAttributeNS(null,"x2",pt.x+50);
        lineX.setAttributeNS(null,"y2",pt.y);
        lineX.setAttributeNS(null,"stroke","blue");
        lineX.setAttributeNS(null,"stroke-width","5");

        var lineY = document.createElementNS(svgNS,"line");
        lineY.setAttributeNS(null,"x1",pt.x);
        lineY.setAttributeNS(null,"y1",pt.y);
        lineY.setAttributeNS(null,"x2",pt.x);
        lineY.setAttributeNS(null,"y2",pt.y+50);
        lineY.setAttributeNS(null,"stroke","blue");
        lineY.setAttributeNS(null,"stroke-width","5");

        //alert(rect.x + " " + rect.y);

        groupE.appendChild(lineX);
        groupE.appendChild(lineY);
}

/* ********************************************************************
 * Find task in the list of nodes (object).
 * This list is an array from the JSON representation of Signavio
 * Use jsonModel global variable
 * A node has these attributes:
 *  - resourceId: uniquely identify the node
 *  - outgoing: array of outgoing sequence flows
 * Note: a task node can have association flow as part of its outgoing flows
 * After the search, the return node has these key attributes:
 *  - id: node id
 *  - ougoingFlow: id of outgoing flow
 *  - incomingFlow: id of incoming flow
 * ********************************************************************/
function findModelNode(id) {
    var nodes = jsonModel.childShapes;

    //Find the node (with outgoing already)
    var node = null;
    for(var i = 0; i < nodes.length; ++i) {
        if(nodes[i].resourceId == id) {
            node = nodes[i];
            break;
        }
    }

    //Check and select the sequence flow (task can have association flow as outgoing)
    if (node != null) {
        if (node.outgoing.length > 2) {
            for(var i = 0; i < nodes.outgoing.length; ++i) {
                for(var j = 0; j < nodes.length; ++j) {
                    if (nodes[j].resourceId == node.outgoing[i].resourceId && nodes[i].stencil.id == "SequenceFlow") {
                        node.outgoingFlow = nodes[j].resourceId;
                        break;
                    }
                }
            }
        }
        else {
            node.outgoingFlow = node.outgoing[0].resourceId;
        }
    }

    //Find and assign the incoming flow
    for(var i = 0; i < nodes.length; ++i) {
        if (nodes[i].stencil.id == "SequenceFlow") {
            if(nodes[i].target.resourceId == id) {
                node.incomingFlow = nodes[i].resourceId;
                break;
            }
        }
    }

    return node;
}

/* *******************************************************
 * Calculate y function value from x value (pi.x, pi.y)
 * The straighline y = ax + b connects two points: p1, p2
 * Return value of y.
 *********************************************************/
function getStraighLineFunctionValue(p1, p2, pi) {
    var a = (p1.y - p2.y)/(p1.x - p2.x);
    var b = p1.y - p1.x*(p1.y - p2.y)/(p1.x - p2.x);
    return a*pi.x + b;
}

function updateClock_global() {
        controller.updateClock();
}

/*
* The animation page has four animation components:
* 1. The process model with tokens moving along the nodes and edges.
* 2. The timeline bar with a tick moving along
* 3. The circular progress bar showing the completion percentage for the log
* 4. The digital clock running and showing the passing time
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
* SlotEngineUnit is used to calculate the speed of the tick movement on the timeline bar
* SlotDataUnit is used to calculate the location of a specific event date on the timeline bar
* timeCoefficient: the ratio of SlotDataUnit to SlotEngineUnit, i.e. 1 second in the engine = how many seconds in the data.
* The starting point of time in all logs is set in json data sent from the server: startDateMillis.
*
* The digital clock must keep running to show the clock jumping. It is governed by a timer property of
* the controller. This timer is set to execute a function every interval of 100ms.
* Starting from 0, it counts 100, 200, 300,...ms.
* Call getCurrentTime() to the SVG document returns the current clock intervals = 100x (x = count)
* The actual current time is: getCurrentTime()*timeCoefficient + startDateMillis.
*

*/
//
// Animation controller
//
AnimationController = {
    // ID of the timer used by the digital clock on the replay control panel
    // The timer is set whenever the replay is started or restarted, and cleared whenevenr it is paused.
    // The synchronization between the digital clock and internal timer of SVG documents is done vie this timer
    // because the timer will read the internal time of every SVG documents at every internal instant

    construct: function (editorCanvas) {
        "use strict";
        this.clockTimer = null;
        this.svgDocuments =  [];
        this.timelineSVG = undefined;
        this.slotDataUnit = 1000;
        this.timelineSlots = 120;
        this.timelineEngineSeconds = 120;
        this.startDateMillis = 0;
        this.canvas = editorCanvas; // the editor canvas
        this.timelineTick = undefined; //keep track of the timeline tick for dragging action

        this.PLAY_CLS = 'ap-mc-icon-play'
        this.PAUSE_CLS = 'ap-mc-icon-pause'
    },

    pauseAnimations: function() {
        this.svgDocuments.forEach(function(s) {
                s.pauseAnimations();
        });

        if (this.clockTimer) {
                clearInterval(this.clockTimer);
        }
    },

    /*
     * Only this method creates a timer.
     * This timer is used to update the digital clock.
     * The mechanism is the digital clock reads SVG document current time every 100ms via updateClock() method.
     * This is pulling way.
     * In case of updating the clock once, it is safer to call updateClockOnce() method than updateClock(), to avoid endless loop.
     */
    unpauseAnimations: function() {
        this.svgDocuments.forEach(function(s) {
            s.unpauseAnimations();
        });

        if (this.clockTimer) {
            clearInterval(this.clockTimer);
        }
        this.clockTimer = setInterval(updateClock_global, 100);
    },

    reset: function(jsonRaw) {
        svgDocument = this.canvas.getSVGContainer();
        svgDocumentG = this.canvas.getSVGViewport();
        // this.timelineSVG = $j("div#playback_controls > svg")[0];
        this.timelineSVG = $j("div.ap-animator-timeline > svg")[0];
        this.svgDocuments.clear();
        this.svgDocuments.push(svgDocument);
        this.svgDocuments.push(this.timelineSVG);
        this.svgDocuments.push($j("div#progress_display svg")[0]);

        var tokenE = svgDocument.getElementById("progressAnimation");
        if (tokenE != null) {
            svgDocument.removeChild(tokenE);
            //tokenE.outerHTML = "";
        }

        jsonServer = JSON.parse(jsonRaw);
        var logs = jsonServer.logs;

        // Reconstruct this.logCases to correspond to the changed jsonServer value
        this.logCases = [];

        var offsets = [3,-3,9,-9,12,-12,15,-15];

        for (var log_index = 0; log_index < jsonServer.logs.length; log_index++) {
            var log = jsonServer.logs[log_index];
            this.logCases[log_index] = [];
            for (var tokenAnimation_index = 0; tokenAnimation_index < log.tokenAnimations.length; tokenAnimation_index++) {
                var tokenAnimation = log.tokenAnimations[tokenAnimation_index];
                var color = apPalette[log_index] || log.color;
                this.logCases[log_index][tokenAnimation_index] = new LogCase(tokenAnimation, color, tokenAnimation.caseId, offsets[log_index]);
            }
        }

        this.startPos = jsonServer.timeline.startDateSlot; //start slot, starting from 0
        this.endPos = jsonServer.timeline.endDateSlot; // end slot
        this.timelineSlots = jsonServer.timeline.timelineSlots;
        this.timelineEngineSeconds = jsonServer.timeline.totalEngineSeconds; //total engine seconds
        this.slotEngineUnit = jsonServer.timeline.slotEngineUnit*1000; // number of engine milliseconds per slot
        this.startDateMillis = (new Date(jsonServer.timeline.startDateLabel)).getTime(); // start date in milliseconds
        // slotDataUnit: number of data milliseconds per slot
        this.slotDataUnit = ((new Date(jsonServer.timeline.endDateLabel)).getTime() - (new Date(jsonServer.timeline.startDateLabel)).getTime()) / (jsonServer.timeline.endDateSlot - jsonServer.timeline.startDateSlot);
        // timeCoefficient: number of data seconds (millis) per one engine second (millis)
        this.timeCoefficient = this.slotDataUnit/this.slotEngineUnit;

        //Recreate progress indicators
        var progressIndicatorE = this.createProgressIndicators(logs, jsonServer.timeline);
        $j("div#progress_display svg")[0].append(progressIndicatorE);

        // IVO: Add individual logs
        function renderIndicators () {
            var progParent = $j("body").first()
            for(var i=0;i<logs.length;i++) {
                let progSVG = $j(`<svg id="progressbar-${i}" xmlns="http://www.w3.org/2000/svg"></n:svg>`)
                let progIndicator = this.createProgressIndicatorsForLog(
                    i+1, logs[i], jsonServer.timeline, 0, 0)
                progSVG.append(progIndicator)
                progParent.append(progSVG);
            }
        }

        let props = [
            "info-no",
            "info-log",
            "info-traces",
            "info-relayed",
            "info-reliable",
            "info-fitness"
        ]

        function getProps(logIdx) {
            let source = $j('#metrics_table tr:nth-child(' + (logIdx + 1) + ') td')
            props.forEach(function(prop, idx) {
                $j('#' + prop).text($j(source[idx]).text() || '')
            })
        }

        $j([0]).text()
        
        for(var i=0;i<logs.length;i++) {
            let pId = '#ap-la-progress-' + (i + 1);
            $j(pId).hover(
                (function (i) {
                    return function () {
                        console.log('over' + i);
                        getProps(i)
                        let { top, left } = $j(pId).offset()
                        let bottom = `calc(100vh - ${top - 10}px)`
                        left += 20
                        console.log(bottom, $j(pId))
                        $j('#ap-animator-info-tip').css({ bottom, left })
                        $j('#ap-animator-info-tip').show();
                    }
                })(i + 1),
                (function (i) {
                    return function () {
                        console.log('out' + i);
                        $j('#ap-animator-info-tip').hide();
                    }
                })(i + 1)
            )
        }
        // IVO

        //Recreate timeline to update date labels
        //$j("#timeline").remove();
        var timelineE = this.createTimeline(jsonServer.timeline.logs.length);
        this.timelineSVG.append(timelineE);

        // Add log intervals to timeline: must be after the timeline creation
        //var timelineElement = $j("#timeline")[0];
        var startTopX = 20;
        var startTopY = 18;
        for (var j=0; j<jsonServer.timeline.logs.length; j++) {
            var log = jsonServer.timeline.logs[j];
            var logInterval = document.createElementNS(svgNS,"line");
            logInterval.setAttributeNS(null,"x1",startTopX + 9 * log.startDatePos);  // magic number 10 is slotWidth / slotEngineDur
            logInterval.setAttributeNS(null,"y1",startTopY + 8 + 7 * j + timelineOffset);
            logInterval.setAttributeNS(null,"x2",startTopX + 9 * log.endDatePos);
            logInterval.setAttributeNS(null,"y2",startTopY + 8 + 7 * j  + timelineOffset);
            logInterval.setAttributeNS(null,"style","stroke: "+ (apPalette[j] || log.color) +"; stroke-width: 5");
            timelineE.insertBefore(logInterval, timelineE.lastChild);

            //display date label at the two ends
            if (log.startDatePos % 10 != 0) {
                var logDateTextE = document.createElementNS(svgNS,"text");
                logDateTextE.setAttributeNS(null,"x", startTopX + 9 * log.startDatePos - 50);
                logDateTextE.setAttributeNS(null,"y", startTopY + 8 + 7 * j + 5  + timelineOffset);
                logDateTextE.setAttributeNS(null,"text-anchor", "middle");
                logDateTextE.setAttributeNS(null,"font-size", "11");
                logDateTextE.innerHTML = log.startDateLabel.substr(0,19);
                timelineE.insertBefore(logDateTextE, timelineE.lastChild);
            }
       }

       // Show metrics for every log
       var metricsTable = $j("#metrics_table")[0];
       for (var i=0; i<logs.length; i++) {
           var row = metricsTable.insertRow(i+1);
           var cellLogNo = row.insertCell(0);;
           var cellLogName = row.insertCell(1);
           var cellTotalCount = row.insertCell(2);
           var cellPlayCount = row.insertCell(3);
           var cellReliableCount = row.insertCell(4);
           var cellExactFitness = row.insertCell(5);
           //var cellExactFitnessFormulaTime = row.insertCell(5);
           //var cellApproxFitness = row.insertCell(6);
           //var cellApproxFitnessFormulaTime = row.insertCell(7);
           //var cellAlgoTime = row.insertCell(8);


            cellLogNo.innerHTML = i+1;
            cellLogNo.style.backgroundColor = logs[i].color;
            cellLogNo.style.textAlign = 'center';

           if (logs[i].filename.length > 50) {
                   cellLogName.innerHTML = logs[i].filename.substr(0,50) + "...";
           }
           else {
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

       this.start();
    },

    // Note: the engine time is changed in two situations:
    // 1. Change the token speed: go slower/faster
    // 2. Change the engine time to move the token forward/backward
    // In changing speed situation: the tokens (or markers) and clock are not updated. Their settings must be the same
    // after the engine time is changed, e.g. tokens must stay in the same position, clock must show the same datetime
    setCurrentTime: function(time, changeSpeed) {
        this.svgDocuments.forEach(function(s) {
            s.setCurrentTime(time);
        });

        if (!changeSpeed) {
          this.updateMarkersOnce();
          this.updateClockOnce(time * this.timeCoefficient * 1000 + this.startDateMillis);
        }

    },

    getCurrentTime: function() {
        return this.svgDocuments[0].getCurrentTime();
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
        if (this.getCurrentTime() > this.endPos*this.slotEngineUnit/1000) {
            this.end();
        } else {
            this.updateClockOnce(this.getCurrentTime()*this.timeCoefficient*1000 + this.startDateMillis);
        }
    },

    /*
     * Update all tokens (LogCase objects) with the new current time
     */
    updateMarkersOnce: function() {
        var t = this.getCurrentTime();
        var dt = this.timeCoefficient * 1000 / this.slotDataUnit; // 1/this.SlotEngineUnit
        t *= dt; //number of engine slots: t = t/this.SlotEngineUnit

        // Display all the log trace markers
        for (var log_index = 0; log_index < jsonServer.logs.length; log_index++) {
            for (var tokenAnimation_index = 0; tokenAnimation_index < jsonServer.logs[log_index].tokenAnimations.length; tokenAnimation_index++) {
                this.logCases[log_index][tokenAnimation_index].updateMarker(t, dt);
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
        var date = new Date();
        date.setTime(time);
        if (window.Intl) {
            document.getElementById("date").innerHTML = new Intl.DateTimeFormat([], {
                    year: "numeric", month: "short", day: "numeric"
            }).format(date);
            document.getElementById("time").innerHTML = new Intl.DateTimeFormat([], {
                    hour12: false, hour: "numeric", minute: "numeric", second: "numeric"
            }).format(date);
            //document.getElementById("subtitle").innerHTML = new Intl.NumberFormat([], {
            //        minimumIntegerDigits: 3
            //}).format(date.getMilliseconds();
        } else {  // Fallback for browsers that don't support Intl (e.g. Safari 8.0)
            document.getElementById("date").innerHTML = date.toDateString();
            document.getElementById("time").innerHTML = date.toTimeString();
        }
    },

    start: function() {
        this.pause();
        this.setCurrentTime(this.startPos); // The startPos timing could be a little less than the first event timing in the log to accomodate the start event of BPMN

        var date = new Date();
        date.setTime(this.startPos);
        var dayString = new Intl.DateTimeFormat([], {
                    year: "numeric", month: "short", day: "numeric"
            }).format(date);
        var timeString = new Intl.DateTimeFormat([], {
                    hour12: false, hour: "numeric", minute: "numeric", second: "numeric"
            }).format(date);
        //console.log(dayString + " " + timeString);
    },

    /*
     * This method is used to process tasks when replay reaches the end of the timeline
     */
    end: function() {
        this.pause();
        this.setCurrentTime(this.endPos*this.slotEngineUnit/1000);
        this.updateClockOnce(this.endPos*this.slotEngineUnit*this.timeCoefficient + this.startDateMillis);
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
    changeSpeed: function (speedRatio){

        //------------------------------------------
        // Update the speed of circle progress bar
        //------------------------------------------
        var animations = $j(".progressAnimation");
        for (var i=0; i<animations.length; i++) {
            animateE = animations[i];

            curDur = animateE.getAttribute("dur");
            curDur = curDur.substr(0,curDur.length - 1);

            curBegin = animateE.getAttribute("begin");
            curBegin = curBegin.substr(0,curBegin.length - 1);

            animateE.setAttributeNS(null,"dur", curDur/speedRatio + "s");
            animateE.setAttributeNS(null,"begin", curBegin/speedRatio + "s");
        }

        //-----------------------------------------
        // Update timeline tick with the new speed
        //-----------------------------------------
        var timelineTickE = $j("#timelineTick").get(0);
        curDur = timelineTickE.getAttribute("dur");
        curDur = curDur.substr(0,curDur.length - 1);
        curBegin = timelineTickE.getAttribute("begin");
        curBegin = curBegin.substr(0,curBegin.length - 1);

        timelineTickE.setAttributeNS(null,"dur", curDur/speedRatio + "s");
        timelineTickE.setAttributeNS(null,"begin", curBegin/speedRatio + "s");

        //----------------------------------------
        // Update Coefficients and units to ensure consistency
        // between the clock, timeline and SVG documents
        //----------------------------------------
        if (this.slotEngineUnit) {
            this.slotEngineUnit = this.slotEngineUnit/speedRatio;
            if (this.timeCoefficient) {
                this.timeCoefficient = this.slotDataUnit/this.slotEngineUnit;
            }
        }

        // Update markers: this is needed the moment before
        // changing the engine time to update existing and new markers?
        this.updateMarkersOnce();

        // Now, change the engine time to auto ajust the tokens faster/slower
        // Note: update engine time without updating markers and the clock
        var currentTime = this.getCurrentTime();
        var newTime = currentTime/speedRatio;
        this.setCurrentTime(newTime, true);
    },

    fastforward: function () {
       if (this.getCurrentTime() >= this.endPos*this.slotEngineUnit/1000) {
           return;
       } else {
           this.setCurrentTime(this.getCurrentTime() + 1*this.slotEngineUnit/1000); //move forward 1 slots
       }
    },

    fastBackward: function () {
       if (this.getCurrentTime() <= this.startPos*this.slotEngineUnit/1000) {
           return;
       } else {
           this.setCurrentTime(this.getCurrentTime() - 1*this.slotEngineUnit/1000); //move backward 1 slots
       }
    },

    nextTrace: function () {
        if (this.getCurrentTime() >= this.endPos*this.slotEngineUnit/1000) {
            return;
        } else {
            var tracedates = jsonServer.tracedates; //assume that jsonServer.tracedates has been sorted in ascending order
            var currentTimeMillis = this.getCurrentTime()*this.timeCoefficient*1000 + this.startDateMillis;
            //search for the next trace date/time immediately after the current time
            for (var i=0; i<tracedates.length; i++) {
                if (currentTimeMillis < tracedates[i]) {
                    this.setCurrentTime((tracedates[i]-this.startDateMillis)/(1000*this.timeCoefficient));
                    return;
                }
            }
        }
    },

    previousTrace: function () {
        if (this.getCurrentTime() <= this.startPos*this.slotEngineUnit/1000) {
            return;
        } else {
            var tracedates = jsonServer.tracedates; //assume that jsonServer.tracedates has been sorted in ascending order
            var currentTimeMillis = this.getCurrentTime()*this.timeCoefficient*1000 + this.startDateMillis;
            //search for the previous trace date/time immediately before the current time
            for (var i=tracedates.length-1; i>=0; i--) {
                if (currentTimeMillis > tracedates[i]) {
                    this.setCurrentTime((tracedates[i]-this.startDateMillis)/(1000*this.timeCoefficient));
                    return;
                }
            }
        }
    },

    isPaused: function () {
        return $j("#pause").hasClass(this.PAUSE_CLS)
    },

    setPlayPauseBtn: function (state) {
        const { PAUSE_CLS, PLAY_CLS } = this;
        const btn = $j("#pause")

        if (typeof state === 'undefined') {
            state = this.isPaused() // do toggle
        }
        if (state) {
            btn.removeClass(PAUSE_CLS).addClass(PLAY_CLS)
        } else {
            btn.removeClass(PLAY_CLS).addClass(PAUSE_CLS)
        }
    },

    pause: function() {
        // var img = document.getElementById("pause").getElementsByTagName("img")[0];
        this.pauseAnimations();
        this.setPlayPauseBtn(true);
        // img.alt = "Play";
        // img.src = "images/control_play.png";
    },

    play: function() {
        // var img = document.getElementById("pause").getElementsByTagName("img")[0];
        this.unpauseAnimations();
        this.setPlayPauseBtn(false);
        // img.alt = "Pause";
        // img.src = "images/control_pause.png";
    },

    switchPlayPause: function () {
        // var img = document.getElementById("pause").getElementsByTagName("img")[0];
        if (this.isPaused()) {
            this.pause();
        } else {
            this.play();
        }
    },

    /*
     * <g id="progressAnimation"><g class='progress'><path><animate class='progressanimation'>
     * logs: array of log object
     * timeline: object containing timeline information
     */
    createProgressIndicators: function(logs, timeline) {
        var progressE = document.createElementNS(svgNS,"g");
        progressE.setAttributeNS(null,"id","progressAnimation");

        var x = 30;
        var y = 20;
        for(var i=0;i<logs.length;i++) {
            progressE.appendChild(this.createProgressIndicatorsForLog(i+1, logs[i], timeline, x, y));
            x += 150;
        }
        return progressE;
    },

    /*
     * Create progress indicator for one log
     * log: the log object (name, color, traceCount, progress, tokenAnimations)
     * x,y: the coordinates to draw the progress bar
     */
    createProgressIndicatorsForLog: function(logNo, log, timeline, x, y) {
        var pieE = document.createElementNS(svgNS,"g");
        pieE.setAttributeNS(null,"id","ap-la-progress-" + logNo);
        pieE.setAttributeNS(null,"class","progress");

        var color = apPalette[logNo - 1] || log.color
        var pathE = document.createElementNS(svgNS,"path");
        pathE.setAttributeNS(null,"d","M " + x + "," + y + " m 0, 0 a 20,20 0 1,0 0.00001,0");
        // pathE.setAttributeNS(null,"fill","#CCCCCC");
        pathE.setAttributeNS(null,"fill",color);
        pathE.setAttributeNS(null,"fill-opacity", 0.5);
        pathE.setAttributeNS(null,"stroke", color);
        pathE.setAttributeNS(null,"stroke-width","5");
        pathE.setAttributeNS(null,"stroke-dasharray","0 126 126 0");
        pathE.setAttributeNS(null,"stroke-dashoffset","1");

        var animateE = document.createElementNS(svgNS,"animate");
        animateE.setAttributeNS(null,"class","progressAnimation");
        animateE.setAttributeNS(null,"attributeName","stroke-dashoffset");
        animateE.setAttributeNS(null,"values", log.progress.values);
        animateE.setAttributeNS(null,"keyTimes", log.progress.keyTimes);
        //console.log("values:" + log.progress.values);
        //console.log("keyTimes:" + log.progress.keyTimes);
        animateE.setAttributeNS(null,"begin", log.progress.begin + "s");
        //animateE.setAttributeNS(null,"dur",timeline.timelineSlots*this.slotEngineUnit/1000 + "s");
        animateE.setAttributeNS(null,"dur", log.progress.dur + "s");
        animateE.setAttributeNS(null,"fill","freeze");
        animateE.setAttributeNS(null,"repeatCount", "1");

        pathE.appendChild(animateE);

        var textE = document.createElementNS(svgNS,"text");
        textE.setAttributeNS(null,"x", x);
        textE.setAttributeNS(null,"y", y - 10);
        textE.setAttributeNS(null,"text-anchor","middle");
        var textNode = document.createTextNode(log.name);
        textE.appendChild(textNode);

        var tooltip = document.createElementNS(svgNS,"title");
        tooltip.appendChild(document.createTextNode(log.name));
        textE.appendChild(tooltip);

        pieE.appendChild(pathE);
        pieE.appendChild(textE);

        return pieE;
    },

    /*
     * <g id="timeline">
     *      ---- timeline bar
     *      <line>
     *      <text>
     *      ...
     *      <line>
     *      <text>
     *      ----- timeline tick
     *      <rect>
     *          <animationMotion>
     * Use: this.timelineSlots, this.slotEngineUnit.
     */
    createTimeline: function(logNum) {

        function addTimelineBar(lineX, lineY, lineLen, lineColor, textX, textY, text1, text2, parent) {
            var lineElement = document.createElementNS(svgNS,"line");
            lineElement.setAttributeNS(null,"x1", lineX);
            lineElement.setAttributeNS(null,"y1", lineY + 6);
            lineElement.setAttributeNS(null,"x2", lineX);
            // lineElement.setAttributeNS(null,"y2", lineY+lineLen);
            lineElement.setAttributeNS(null,"y2", lineY + 15);

            if (lineColor === "red") {
                lineElement.setAttributeNS(null,"stroke", 'black');
                lineElement.setAttributeNS(null,"stroke-width","0.5");
                parent.appendChild(lineElement);
            } else {
                lineElement.setAttributeNS(null,"stroke-width",".5");
            }


            var textElement1 = document.createElementNS(svgNS,"text");
            textElement1.setAttributeNS(null,"x", textX);
            textElement1.setAttributeNS(null,"y", textY - 10);
            textElement1.setAttributeNS(null,"text-anchor", "middle");
            textElement1.setAttributeNS(null,"font-size", "11");
            textElement1.innerHTML = text1;

            var textElement2 = document.createElementNS(svgNS,"text");
            textElement2.setAttributeNS(null,"x", textX);
            textElement2.setAttributeNS(null,"y", textY + 5);
            textElement2.setAttributeNS(null,"text-anchor", "middle");
            textElement2.setAttributeNS(null,"font-size", "11");
            textElement2.innerHTML = text2;


            parent.appendChild(textElement1);
            parent.appendChild(textElement2);
        }

        var timelineElement = document.createElementNS(svgNS,"g");
        timelineElement.setAttributeNS(null,"id","timeline");
        timelineElement.setAttributeNS(null,"style","-webkit-touch-callout: none; -webkit-user-select: none; -khtml-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none");

        var startTopX = 20;
        var startTopY = 15;
        var slotWidth = 9;
        var slotEngineDur = this.slotEngineUnit/1000; //number of SVG engine seconds for one slot
        var lineLen = 30;
        var textToLineGap = 5;
        var startValue = 0;
        var textValue = -slotEngineDur;
        var lineTopX = -slotWidth + startTopX;
        var slotNum = this.timelineSlots; // the number of timeline vertical bars



        /*---------------------------
        Add text and line for the bar
        ---------------------------*/

        for (var i=0;i<=slotNum;i++) {
            lineTopX += slotWidth;
            //textValue += slotEngineDur;
            if (i%10 == 0) {
                var date = new Date();
                date.setTime(this.startDateMillis + i*this.slotDataUnit);
                textValue1 = date.getDate() + "/" + (date.getMonth()+1) + "/" + (date.getFullYear()+"").substr(2,2);
                textValue2 = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
                var lineColor = "red";
            } else {
                textValue1 = "";
                textValue2 = "";
                var lineColor = "black";
            }
            addTimelineBar(lineTopX, startTopY, lineLen, lineColor, lineTopX, startTopY-textToLineGap, textValue1, textValue2, timelineElement);
        }

        /*---------------------------
        Add timeline tick
        ---------------------------*/
        var indicatorE = document.createElementNS(svgNS,"rect");
        indicatorE.setAttributeNS(null,"fill","#FAF0E6");
        indicatorE.setAttributeNS(null,"height", 8 * logNum + 10);
        indicatorE.setAttributeNS(null,"width","12");
        indicatorE.setAttributeNS(null,"stroke","grey");
        indicatorE.setAttributeNS(null,"rx","2");
        indicatorE.setAttributeNS(null,"ry","2");
        // indicatorE.setAttributeNS(null,"x", -3); // note: the Y-axis of the tick is controlled by the animateMotion path it attaches to
        indicatorE.setAttributeNS(null,"x", -6);
        indicatorE.setAttributeNS(null, "style", "cursor: move");

        var indicatorAnimation = document.createElementNS(svgNS,"animateMotion");
        indicatorAnimation.setAttributeNS(null,"id","timelineTick");
        indicatorAnimation.setAttributeNS(null,"begin","0s");
        indicatorAnimation.setAttributeNS(null,"dur",slotNum*slotEngineDur + "s");
        indicatorAnimation.setAttributeNS(null,"by",slotEngineDur);
        indicatorAnimation.setAttributeNS(null,"from", startTopX + "," + (startTopY+5));
        indicatorAnimation.setAttributeNS(null,"to",lineTopX + "," + (startTopY+5));
        indicatorAnimation.setAttributeNS(null,"fill","freeze");
        indicatorE.appendChild(indicatorAnimation);
        timelineElement.appendChild(indicatorE);

        /*---------------------------
        Control dragging of the timeline stick
        ---------------------------*/
        indicatorE.addEventListener('mousedown', startDragging.bind(this));
        this.timelineSVG.addEventListener('mousemove', dragging.bind(this));
        this.timelineSVG.addEventListener('mouseup', stopDragging.bind(this));
        this.timelineSVG.addEventListener('mouseleave', stopDragging.bind(this));

        var dragging= false;
        var offset;
        var lastPos=-3;
        var svg = this.timelineSVG;

        var elementWithFocus = null;
        var clickX, clickY;
        var lastMoveX=0, lastMoveY=0;

        function startDragging(evt) {
          evt.preventDefault();
          dragging = true;
          elementWithFocus = evt.target;
          // clickX = evt.clientX;
          offset = getMousePosition(evt);
          offset.x -= parseFloat(indicatorE.getAttributeNS(null, "x"));
          clickX = offset.x;
          this.pause();
        }

        function dragging(evt) {
          evt.preventDefault();
          if (dragging) {
            moveX = lastMoveX + ( evt.clientX - clickX );
            //var coord = getMousePosition(evt);
            //lastPos = coord.x - offset.x;
            //elementWithFocus.setAttributeNS(null, "x",  lastPos);
            elementWithFocus.setAttributeNS(null, "x",  moveX);
          }
        }

        // Only update SVG current time when the dragging finishes to avoid heavy on-the-fly updates
        // After every call setCurrentTime, the SVG coordinate is moved to the new position of the tick
        // As calling setCurrentTime will also update the tick's position, we have to move the tick
        // back to its original position before the call to setCurrentTime, otherwise it is moved two times
        // The dragging flag is checked to avoid doing two times for mouseup and mouseleave events
        function stopDragging(evt) {
          if (!dragging) return; //avoid doing the below two times
          if(evt.type == 'mouseleave' && dragging) {
            return;
          }
          dragging = false;
          elementWithFocus = null;
          var slotCount = (moveX) / slotWidth;
          lastMoveX = 0;
          lastMoveY = 0;
          //alert("moveX: " + moveX);
          //alert("slotCount: " + slotCount);
          if (slotCount != 0) {
            indicatorE.setAttributeNS(null,"x", 0);
            this.setCurrentTime(this.getCurrentTime() + 1.0*slotCount*this.slotEngineUnit / 1000.0);
            //this.play();
          }

        }

        function updateTimeline() {
          var slotCount = moveX / slotWidth;
          //alert("moveX: " + moveX);
          //alert("slotCount: " + slotCount);
          if (slotCount != 0) {
            this.setCurrentTime(this.getCurrentTime() + 1.0*slotCount*this.slotEngineUnit / 1000.0);
          }
        }

        //Convert from screen coordinates to SVG document coordinates
        function getMousePosition(evt) {
          var matrix = svg.getScreenCTM();
          var pt = svg.createSVGPoint();
          pt.x = evt.clientX;
          pt.y = evt.clientY;
          pt.matrixTransform(matrix);
          return {x: pt.x, y: pt.y};
          // return {
          //   x: (evt.clientX - CTM.e) / CTM.a,
          //   y: (evt.clientY - CTM.f) / CTM.d
          // };
        }

        return timelineElement;

    },

    setCaseLabelsVisible: function(visible) {
      if (caseLabelsVisible != visible) {
        caseLabelsVisible = visible;
        this.updateMarkersOnce();
      }
    }


};

AnimationController = Clazz.extend(AnimationController);

//
// A log trace
//

// tokenAnimation is expected to one of the elements of jsonServer.logs[].tokenAnimations
// color is the desired color the log trace marker
// label is the desired text annotation of the log trace marker

LogCase = {

    construct: function (tokenAnimation, color, label, offset) {
        "use strict";
        this.tokenAnimation = tokenAnimation;
        this.color          = color;
        this.label          = label;

        this.offsetAngle      = 2 * Math.PI * Math.random();  // a random angle to offset the marker; prevents markers from occluding one another totally
        this.pathElementCache = {};
        this.nodeMarkers      = [];
        this.pathMarkers      = [];
        this.offset = offset;
    },

    /*
    /* Create a marker (i.e. a token, which is an SVG animation element) and insert
     * it into the existing SVG document. SVG engine will start animating
     * them automatically.
     * Params
     * t: number of engine slots
     * dt:
     */
    updateMarker: function(t, dt) {

        // Delete any path markers which have moved beyond their interval
        var newMarkers = []
        var alreadyMarkedIndices = [];
        while (this.pathMarkers.length > 0) {
            var marker = this.pathMarkers.pop();
            if (marker.begin <= t && t <= marker.end) {
                alreadyMarkedIndices[marker.index] = true;
                newMarkers.push(marker);
            } else {
                marker.element.remove();
            }
        }
        this.pathMarkers = newMarkers;

        // Insert any new path markers
        for (var i = 0; i < this.tokenAnimation.paths.length; i++) {
            var path  = this.tokenAnimation.paths[i];
            var begin = parseFloat(path.begin);
            var dur   = parseFloat(path.dur);
            var end   = begin + dur;


            if (begin <= t && t <= end && !alreadyMarkedIndices[i]) {
                var pathElement = this.getPathElement(path);
                if (!pathElement) {
                    console.log("Unable to create marker for path.id=" + path.id);
                } else {
                    var marker = {
                        begin:   begin,
                        end:     end,
                        index:   i,
                        element: this.createPathMarker(t, dt, this.getPathElement(path), begin, dur, this.offset)
                    };
                    svgDocumentG.appendChild(marker.element);
                    this.pathMarkers.push(marker);
                }
            }
        }

        // Delete any node markers which have moved beyond their interval
        newMarkers = [];
        alreadyMarkedIndices = [];
        while (this.nodeMarkers.length > 0) {
            var marker = this.nodeMarkers.pop();
            if (marker.begin <= t && t <= marker.end) {
                alreadyMarkedIndices[marker.index] = true;
                newMarkers.push(marker);
            } else {
                marker.element.remove();
            }
        }
        this.nodeMarkers = newMarkers;

        // Insert any new node markers
        for (var i = 0; i < this.tokenAnimation.nodes.length; i++) {
            var node  = this.tokenAnimation.nodes[i];
            var begin = parseFloat(node.begin);
            var dur   = parseFloat(node.dur);
            var end   = begin + dur;

            if (begin <= t && t <= end && !alreadyMarkedIndices[i]) {
            var marker = {
                begin:   begin,
                end:     end,
                index:   i,
                element: this.createNodeMarker(t, dt, node, begin, dur, this.offset)
            };
            this.nodeMarkers.push(marker);
                svgDocumentG.appendChild(marker.element);
            }
        }
    },

    getPathElement: function(path) {
        var pathElement = this.pathElementCache[path.id];
        if (!pathElement) {
            //pathElement = this.pathElementCache[path.id] = $j("#svg-"+path.id).find("g").find("g").find("g").find("path").get(0);
            pathElement = this.pathElementCache[path.id] = $j("[data-element-id="+path.id+"]").find("g").find("path").get(0);
        }
        return pathElement;
    },

    createNodeMarker: function(t, dt, node, begin, dur, offset) {
        //var modelNode = findModelNode(node.id);
        //var incomingPathE = $j("#svg-"+modelNode.incomingFlow).find("g").find("g").find("g").find("path").get(0);
        //var incomingEndPoint = incomingPathE.getPointAtLength(incomingPathE.getTotalLength());
        var incomingEndPoint = $j("[data-element-id="+controller.canvas.getIncomingFlowId(node.id)+"]");
        //console.log(incomingEndPoint);
        var incomingPathE = incomingEndPoint.find("g").find("path").get(0);
        var incomingEndPoint = incomingPathE.getPointAtLength(incomingPathE.getTotalLength());

        // var outgoingPathE = $j("#svg-"+modelNode.outgoingFlow).find("g").find("g").find("g").find("path").get(0);
        // var outgoingStartPoint = outgoingPathE.getPointAtLength(0);
        var outgoingStartPoint = $j("[data-element-id="+controller.canvas.getOutgoingFlowId(node.id)+"]");
        var outgoingPathE = outgoingStartPoint.find("g").find("path").get(0);
        var outgoingStartPoint = outgoingPathE.getPointAtLength(0);

        var startPoint = incomingEndPoint;
        var endPoint = outgoingStartPoint;

        //var nodeRectE = $j("#svg-" + node.id).find("g").get(0); //this <g> element contains the translate function
        var nodeTransformE = $j("[data-element-id="+node.id+"]").get(0); //this <g> element contains the translate function
        var nodeRectE = $j("[data-element-id="+node.id+"]").find("g").find("rect").get(0);
        var taskRectPoints = getViewportPoints(nodeRectE, nodeTransformE);

        //---------------------------------------------------------
        // Create path element
        //---------------------------------------------------------

        if (node.isVirtual == "false") { //go through center
            var path =  "m" + startPoint.x + "," + startPoint.y + " L" + taskRectPoints.cc.x + "," + taskRectPoints.cc.y +
                        " L" + endPoint.x + "," + endPoint.y;
            //var pathId = node.id +"_path";
        } else {
            //var pathId = node.id +"_virtualpath";

            // Both points are on a same edge
            if ((Math.abs(startPoint.x - endPoint.x) < 10 && Math.abs(endPoint.x - taskRectPoints.se.x) < 10) ||
                (Math.abs(startPoint.x - endPoint.x) < 10 && Math.abs(endPoint.x - taskRectPoints.sw.x) < 10) ||
                (Math.abs(startPoint.y - endPoint.y) < 10 && Math.abs(endPoint.y - taskRectPoints.nw.y) < 10) ||
                (Math.abs(startPoint.y - endPoint.y) < 10 && Math.abs(endPoint.y - taskRectPoints.sw.y) < 10)) {
                var path = "m" + startPoint.x + "," + startPoint.y + " L" + endPoint.x + "," + endPoint.y;
            }
            else {
                var arrayAbove = new Array();
                var arrayBelow = new Array();

                if (taskRectPoints.se.y < getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.se)) {
                    arrayAbove.push(taskRectPoints.se);
                } else {
                    arrayBelow.push(taskRectPoints.se);
                }

                if (taskRectPoints.sw.y < getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.sw)) {
                    arrayAbove.push(taskRectPoints.sw);
                } else {
                    arrayBelow.push(taskRectPoints.sw);
                }

                if (taskRectPoints.ne.y < getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.ne)) {
                    arrayAbove.push(taskRectPoints.ne);
                } else {
                    arrayBelow.push(taskRectPoints.ne);
                }

                if (taskRectPoints.nw.y < getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.nw)) {
                    arrayAbove.push(taskRectPoints.nw);
                } else {
                    arrayBelow.push(taskRectPoints.nw);
                }

                if (arrayAbove.length == 1) {
                    var path =  "m" + startPoint.x + "," + startPoint.y + " " +
                                "L" + arrayAbove[0].x + "," + arrayAbove[0].y + " " +
                                "L" + endPoint.x + "," + endPoint.y;
                }
                else if (arrayBelow.length == 1) {
                    var path =  "m" + startPoint.x + "," + startPoint.y + " " +
                                "L" + arrayBelow[0].x + "," + arrayBelow[0].y + " " +
                                "L" + endPoint.x + "," + endPoint.y;
                } else {

                    if (Math.abs(startPoint.x - taskRectPoints.sw.x) < 10) {
                        var path =  "m" + startPoint.x + "," + startPoint.y + " " +
                                    "L" + taskRectPoints.sw.x + "," + taskRectPoints.sw.y + " " +
                                    "L" + taskRectPoints.se.x + "," + taskRectPoints.se.y + " " +
                                    "L" + endPoint.x + "," + endPoint.y;
                    }
                    else if (Math.abs(startPoint.x - taskRectPoints.se.x) < 10) {
                        var path =  "m" + startPoint.x + "," + startPoint.y + " " +
                                    "L" + taskRectPoints.se.x + "," + taskRectPoints.se.y + " " +
                                    "L" + taskRectPoints.sw.x + "," + taskRectPoints.sw.y + " " +
                                    "L" + endPoint.x + "," + endPoint.y;
                    }
                    else if (Math.abs(startPoint.y - taskRectPoints.sw.y) < 10) {
                        var path =  "m" + startPoint.x + "," + startPoint.y + " " +
                                    "L" + taskRectPoints.sw.x + "," + taskRectPoints.sw.y + " " +
                                    "L" + taskRectPoints.nw.x + "," + taskRectPoints.nw.y + " " +
                                    "L" + endPoint.x + "," + endPoint.y;
                    }
                    else if (Math.abs(startPoint.y - taskRectPoints.nw.y) < 10) {
                        var path =  "m" + startPoint.x + "," + startPoint.y + " " +
                                    "L" + taskRectPoints.nw.x + "," + taskRectPoints.nw.y + " " +
                                    "L" + taskRectPoints.sw.x + "," + taskRectPoints.sw.y + " " +
                                    "L" + endPoint.x + "," + endPoint.y;
                    }
                }
            }
        }

        return this.createMarker(t, dt, path, begin, dur, offset);
    },

    createPathMarker: function(t, dt, pathElement, begin, dur, offset) {
        return this.createMarker(t, dt, pathElement.getAttribute("d"), begin, dur, offset);
    },

    /* A marker is an SVG g element:
     * <g>
     *      <animateMotion begin dur fill="freeze" path rotate="auto" />: the path to animate the token
     *      <circle cx cy r fill></circle>: the shape of the token
     *      <text x y style fill text-anchor visibility></text>: the label on top of the token
     * </g>
     */
    createMarker: function(t, dt, d, begin, dur, offset) {
        var marker = document.createElementNS(svgNS,"g");
        marker.setAttributeNS(null,"stroke","none");

        var animateMotion = document.createElementNS(svgNS,"animateMotion");
        animateMotion.setAttributeNS(null,"begin",begin/dt);
        animateMotion.setAttributeNS(null,"dur",dur/dt);
        animateMotion.setAttributeNS(null,"fill","freeze");
        animateMotion.setAttributeNS(null,"path",d);
        animateMotion.setAttributeNS(null,"rotate","auto");
        marker.appendChild(animateMotion);

        var circle = document.createElementNS(svgNS,"circle");

        //Bruce 15/6/2015: add offset as a parameter, add 'rotate' attribute, put markers of different logs on separate lines.
        //var offset = 2;
        //circle.setAttributeNS(null,"cx",offset * Math.sin(this.offsetAngle));
        //circle.setAttributeNS(null,"cy",offset * Math.cos(this.offsetAngle));
        circle.setAttributeNS(null,"cx", 0);
        circle.setAttributeNS(null,"cy", offset);
        circle.setAttributeNS(null,"r",5);
        circle.setAttributeNS(null,"fill",this.color);
        marker.appendChild(circle);


        var text = document.createElementNS(svgNS,"text");
        //text.setAttributeNS(null,"x",offset * Math.sin(this.offsetAngle));
        //text.setAttributeNS(null,"y",offset * Math.cos(this.offsetAngle) - 10);
        text.setAttributeNS(null,"x",0);
        text.setAttributeNS(null,"y",offset - 10);
        text.setAttributeNS(null,"style","fill: black; text-anchor: middle" + (caseLabelsVisible ? "" : "; visibility: hidden"));
        text.appendChild(document.createTextNode(this.label));
        marker.appendChild(text);


        return marker;
    }
};

LogCase = Clazz.extend(LogCase);


/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

/**
 * The super class for all classes in ORYX. Adds some OOP feeling to javascript.
 * See article "Object Oriented Super Class Method Calling with JavaScript" on
 * http://truecode.blogspot.com/2006/08/object-oriented-super-class-method.html
 * for a documentation on this. Fairly good article that points out errors in
 * Douglas Crockford's inheritance and super method calling approach.
 * Worth reading.
 * @class Clazz
 */
var Clazz = function() {};

/**
 * Empty constructor.
 * @methodOf Clazz.prototype
 */
Clazz.prototype.construct = function() {};

/**
 * Can be used to build up inheritances of classes.
 * @example
 * var MyClass = Clazz.extend({
 *   construct: function(myParam){
 *     // Do sth.
 *   }
 * });
 * var MySubClass = MyClass.extend({
 *   construct: function(myParam){
 *     // Use this to call constructor of super class
 *     arguments.callee.$.construct.apply(this, arguments);
 *     // Do sth.
 *   }
 * });
 * @param {Object} def The definition of the new class.
 */
Clazz.extend = function(def) {
    var classDef = function() {
        if (arguments[0] !== Clazz) { this.construct.apply(this, arguments); }
    };

    var proto = new this(Clazz);
    var superClass = this.prototype;

    for (var n in def) {
        var item = def[n];
        if (item instanceof Function) item.$ = superClass;
        proto[n] = item;
    }

    classDef.prototype = proto;

    //Give this new class the same static extend method
    classDef.extend = this.extend;
    return classDef;
};




