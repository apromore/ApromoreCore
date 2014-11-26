/*
 * Note browser compatibility
 * Chome: does not support reference variable to point to DOM elements, must use selectors (getElementsBy...)
 *          otherwise the innerHTML is not updated, element attribute is not updated
 * Chome: svg.setCurrentTime is not processed properly, must call svg to reload via innerHTML
 */

jQuery.browser = {};
jQuery.browser.mozilla = /mozilla/.test(navigator.userAgent.toLowerCase()) && !/webkit/.test(navigator.userAgent.toLowerCase());
jQuery.browser.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
jQuery.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());
jQuery.browser.msie = /msie/.test(navigator.userAgent.toLowerCase());


var svgNS = "http://www.w3.org/2000/svg";
var xlinkNS = "http://www.w3.org/1999/xlink";
var jsonModel; //contains parsed objects of the process model
var jsonServer; //contains parsed objects returned from the server

function svgDocument() {
    //return svgDocument();
    //return $j("#svgPath").get(0);
    //return $j("svg").get(0);
    return $j("div#svgLoc > svg")[0];
}

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

/* ******************************************************************
 * Return an object with four points coressponding to four corners of the input rect element
 * These are coordinates within the SVG document viewport
 * Return object has four points: nw, ne, se, sw, cc (center) (each with x,y attribute)
* ******************************************************************/
function getViewportPoints(rect){
    var svg = svgDocument();
    var pt  = svg.createSVGPoint();
    var corners = {};
    
    var matrix  = rect.getCTM();
    pt.x = rect.x.animVal.value;
    pt.y = rect.y.animVal.value;
    corners.nw = pt.matrixTransform(matrix);
    
    pt.x += rect.width.animVal.value;
    corners.ne = pt.matrixTransform(matrix);
    
    pt.y += rect.height.animVal.value;
    corners.se = pt.matrixTransform(matrix);
    
    pt.x -= rect.width.animVal.value;
    corners.sw = pt.matrixTransform(matrix);
    
    pt.x += rect.width.animVal.value/2;
    pt.y -= rect.height.animVal.value/2;
    corners.cc = pt.matrixTransform(matrix);
    
    return corners;
}

/*
 * input: group element <g>
 * output: SVGPoint
 */
function toViewportCoords(groupE) {
    var svg = svgDocument();
    var pt  = svg.createSVGPoint();
    var matrix  = groupE.getScreenCTM();
    rect = groupE.getBBox();
    pt.x = rect.x;
    pt.y = rect.y;
    return pt.matrixTransform(matrix);
}

/* *****************************************************
 * Get new point after applying transformation matrix
 * on an input point
 * point: the input point to be converted, with x,y coordinate attribute
 * ctm: transformation matrix, obtained from getCTM method of any SVG element
 * Return: a new point with x,y coordinate attribute
 * ***************************************************/
function getPointFromCTM(point, transformMatrix) {
    var newPoint  = svgDocument().createSVGPoint();
    newPoint.x = point.x;
    newPoint.y = point.y;
    newPoint = newPoint.matrixTransform(transformMatrix);
    return newPoint;
}



function drawCoordinateOrigin() {
        var svg = svgDocument();
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
        var svg = svgDocument();
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

Controller = function(){
    //var tokens = [];
    this.clockTimer = null;
    this.taskAnimationElements = [];
    this.edgeAnimationElements = [];
    this.progressAnimationElements = [];
};         

Controller.prototype = {

    svgDocuments: [],
    slotDataUnit: 1000,
    timelineSlots: 120,
    timelineEngineSeconds: 120,
    startDateMillis: 0,

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
        this.svgDocuments.clear();
        this.svgDocuments.push($j("div#svgLoc > svg")[0]);
        this.svgDocuments.push($j("div#playback_controls > svg")[0]);
        var svg3 = $j("div#progress_display > svg")[0];
        this.svgDocuments.push(svg3);

        this.clear();

        jsonServer = JSON.parse(jsonRaw);
        var logs = jsonServer.logs;
        
        this.startPos = jsonServer.timeline.startDateSlot; //start slot 
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
        var progressIndicatorE = controller.createProgressIndicators(logs, jsonServer.timeline);
        svg3.appendChild(progressIndicatorE);

        for (var i=0;i<logs.length;i++) {
            var animationE = this.animateTokens(logs[i]);
            this.svgDocuments[0].appendChild(animationE);
        }
        
        //Recreate timeline to update date labels
        $j("#timeline").remove();
        var timelineE = controller.createTimeline();
        $j("div#playback_controls > svg")[0].appendChild(timelineE);
        
        // Add log intervals to timeline: must be after the timeline creation
        var timelineElement = $j("#timeline")[0];
        var startTopX = 20;
        var startTopY = 18;
        for (var j=0; j<jsonServer.timeline.logs.length; j++) {
            var log = jsonServer.timeline.logs[j];
            var logInterval = document.createElementNS(svgNS,"line");
            logInterval.setAttributeNS(null,"x1",startTopX + 9 * log.startDatePos);  // magic number 10 is gapWidth / gapValue
            logInterval.setAttributeNS(null,"y1",startTopY + 8 + 7 * j);
            logInterval.setAttributeNS(null,"x2",startTopX + 9 * log.endDatePos);
            logInterval.setAttributeNS(null,"y2",startTopY + 8 + 7 * j);
            logInterval.setAttributeNS(null,"style","stroke: "+log.color +"; stroke-width: 5");
            timelineElement.insertBefore(logInterval, timelineElement.lastChild);
            
            //display date label at the two ends
            if (log.startDatePos % 10 != 0) {
                var logDateTextE = document.createElementNS(svgNS,"text");
                logDateTextE.setAttributeNS(null,"x", startTopX + 9 * log.startDatePos - 50);
                logDateTextE.setAttributeNS(null,"y", startTopY + 8 + 7 * j + 5);
                logDateTextE.setAttributeNS(null,"text-anchor", "middle");
                logDateTextE.setAttributeNS(null,"font-size", "11");
                logDateTextE.innerHTML = log.startDateLabel.substr(0,19);  
                timelineElement.insertBefore(logDateTextE, timelineElement.lastChild);              
            }
       }    
       
       // Show metrics for every log
       var metricsTable = $j("#metrics_table")[0];
       for (var i=0; i<logs.length; i++) {
           var row = metricsTable.insertRow(i+1);
           var cellLogName = row.insertCell(0); 
           var cellPlayCount = row.insertCell(1); 
           var cellUnplayCount = row.insertCell(2);
           var cellTraceFitness = row.insertCell(3); 
           var cellApproxFitness = row.insertCell(4); 
           var cellAlgoTime = row.insertCell(5); 
           var cellTotalTime = row.insertCell(6); 
           
           cellLogName.innerHTML = logs[i].name.substr(0,5) + "...";
           cellLogName.style.backgroundColor = logs[i].color;
           
           cellPlayCount.innerHTML = logs[i].playCount;
           
           cellUnplayCount.innerHTML = logs[i].unplayCount;
           cellUnplayCount.title = logs[i].unplayTraces;
           
           cellTraceFitness.innerHTML = logs[i].traceFitness;
           
           cellApproxFitness.innerHTML = logs[i].approxTraceFitness;
           
           cellAlgoTime.innerHTML = logs[i].algoTime/1000;
           
           cellTotalTime.innerHTML = logs[i].totalTime/1000;
       }    

       this.start();
    },

    setCurrentTime: function(time) {
        this.svgDocuments.forEach(function(s) {
                s.setCurrentTime(time);
        });
        this.updateClockOnce(time*this.timeCoefficient*1000 + this.startDateMillis);
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
    	if (this.getCurrentTime() > this.endPos*this.slotEngineUnit/1000) {
    		this.end();
    	} else {
            this.updateClockOnce(this.getCurrentTime()*this.timeCoefficient*1000 + this.startDateMillis);
        }
    },
    
    /*
     * This method is used to call to update the digital clock display.
     * This update is one-off only. 
     * It is safer to call this method than calling updateClock() method which is for timer.
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
        this.setCurrentTime(this.startPos);
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
    
    clear: function() {
        var tokenElements = svgDocument().getElementsByClassName("tokenAnimation");
        if (tokenElements != null) {
            while (tokenElements.length > 0) { //tokenElements array will be updated immediately, so only remove first element until no more elements
                if (tokenElements[0] != null) {
                    svgDocument().removeChild(tokenElements[0]); 
                } 
            }
        }
        
        var tokenE = svgDocument().getElementById("progressAnimation");
        if (tokenE != null) {
            svgDocument().removeChild(tokenE);
            //tokenE.outerHTML = "";  
        }
        
        
        
        for (var i=0;i<=(this.taskAnimationElements.length-1);i++) {
             //parentE = this.taskAnimationElements[i].parentElement;
             //parentE.removeChild(this.taskAnimationElements[i]);
             if (typeof this.taskAnimationElements[i] != 'undefined') {
                this.taskAnimationElements[i].outerHTML = "";
             }
        }
    
        for (var i=0;i<=(this.edgeAnimationElements.length-1);i++) {
             //parentE = this.edgeAnimationElements[i].parentElement;
             //parentE.removeChild(this.taskAnimationElements[i]);
             if (typeof this.edgeAnimationElements[i] != 'undefined') {
                this.edgeAnimationElements[i].outerHTML = "";
             }
        }                      
    },
    
    
    /*
     * <g class="tokenAnimation" id="tokenAnimation">
     *      <g id="caseId" class="token">
     *          <circle class="tokenPath">
     *          <circle class="tokenPath">
     *          <circle class="tokenPath">
     * log: log object
     */ 
    animateTokens: function (log){
        var name = log.name;
        var color = log.color;
        var cases = log.tokenAnimations;
        
        var animationE = document.createElementNS(svgNS,"g");
        animationE.setAttributeNS(null,"id","tokenAnimation_" + name);
        animationE.setAttributeNS(null,"class","tokenAnimation");
        animationE.setAttributeNS(null,"style","visibility: visible;");
        
        for (var i=0;i<=(cases.length-1);i++) {
            var tokenE = this.createTokenElement(cases[i].caseId);
            
            //Add tokens on sequence flows
            var paths = cases[i].paths;
            for (var j=0;j<=(paths.length-1);j++) {
                var id = paths[j].id;
                var begin = paths[j].begin;
                var dur = paths[j].dur;
                if (dur > 0) {
                    tokenPathE = this.createTokenPathElement(cases[i].caseId, id, begin, dur, color);
                    tokenE.appendChild(tokenPathE);
                }
            }
            
            //Add tokens on nodes (across)
            var nodes = cases[i].nodes;
            var incomingE;
            var incomingPoint;
            var outgoingE;
            var ougoingPoint;
            for (var j=0;j<=(nodes.length-1);j++) {
                var id = nodes[j].id;
                var begin = nodes[j].begin;
                var dur = nodes[j].dur;
                var isVirtual = nodes[j].isVirtual;

                if (dur > 0) {
                    tokenPathE = this.createTokenNodeElement(cases[i].caseId, id, begin, dur, color, isVirtual);
                    tokenE.appendChild(tokenPathE);
                }
            }
            
            animationE.appendChild(tokenE);
        }        
        
        return animationE;
    },
    
    /*
     * <g id="caseId" class="token">
     */
    createTokenElement: function (caseId) {
        var tokenE = document.createElementNS(svgNS,"g");
    
        tokenE.setAttributeNS(null,"id",caseId);
        tokenE.setAttributeNS(null,"class","token");

        return tokenE;
    },

    /*
     * <circle class="tokenPath">
     *      <animateMotion class="tokenPathAnimation">
     *          <mpath>
     */
    createTokenPathElement: function (caseId, edgeId, begin, duration, color) {
        //---------------------------------------------
        // Create animation for edge
        //---------------------------------------------
        var pathId = $j("#svg-"+edgeId).find("g").find("g").find("g").find("path").get(0).getAttribute("id");
        //console.log("edgeId=" + pathId + ", begin=" + begin + ", duration=" + duration);
        var tokenPathE = document.createElementNS(svgNS,"circle");

        // small random offset so that traces don't occlude one another so much
        var rand = 2 * Math.PI * Math.random();
        var cx = 3 * Math.sin(rand);
        var cy = 3 * Math.cos(rand);

        var svg = svgDocument();
        var pt  = svg.createSVGPoint();
        
        var matrix  = $j("#"+pathId).get(0).getCTM();
        pt.x = cx;
        pt.y = cy;
        pt = pt.matrixTransform(matrix);         
        
        tokenPathE.setAttributeNS(null,"cx",pt.x);
        tokenPathE.setAttributeNS(null,"cy",pt.y);
        tokenPathE.setAttributeNS(null,"r",4);
        
        
        /*
        var tokenPathE = document.createElementNS(svgNS,"path"); //use path as a token dot.
        
        if (color == "red") {
            dPath = "m 0,0 a 3 3 0 1 0 0.00001 0"; 
        } else {
            dPath = "m 0,0 a 3 3 0 1 1 0.00001 0";
        };       
        tokenPathE.setAttributeNS(null,"d",dPath);
        */
        
        tokenPathE.setAttributeNS(null,"fill",color);
        tokenPathE.setAttributeNS(null,"class","tokenPath");
    
        var animateMotion = document.createElementNS(svgNS,"animateMotion");
        animateMotion.setAttributeNS(null,"class","tokenPathAnimation");
        animateMotion.setAttributeNS(null,"begin",begin+"s");
        animateMotion.setAttributeNS(null,"dur",duration+"s");
    
        
        var mPath = document.createElementNS(svgNS,"mpath");
        mPath.setAttributeNS(xlinkNS,"href","#"+pathId);
        
        animateMotion.appendChild(mPath);
        tokenPathE.appendChild(animateMotion);
        
        //Add tooltip to display caseId
        var titleE = document.createElementNS(svgNS,"title");
        var titleTextE = document.createTextNode(caseId);
        titleE.appendChild(titleTextE);
        tokenPathE.appendChild(titleE);
    
        return tokenPathE;
    },
    
     /*
     * <circle class="tokenPath">
     *      <animateMotion class="tokenPathAnimation">
     *          <mpath>
     */
    createTokenNodeElement: function (caseId, nodeId, begin, duration, color, isVirtual) {
        var modelNode = findModelNode(nodeId);
        var incomingPathE = $j("#svg-"+modelNode.incomingFlow).find("g").find("g").find("g").find("path").get(0);
        var incomingEndPoint = incomingPathE.getPointAtLength(incomingPathE.getTotalLength());
        //console.log ("incoming: " + incomingEndPoint.x + "," + incomingEndPoint.y);
        
        var outgoingPathE = $j("#svg-"+modelNode.outgoingFlow).find("g").find("g").find("g").find("path").get(0);
        var outgoingStartPoint = outgoingPathE.getPointAtLength(0);
        //console.log ("outgoing: " + outgoingStartPoint.x + "," + outgoingStartPoint.y);
        
        var startPoint = getPointFromCTM(incomingEndPoint, incomingPathE.getCTM());
        var endPoint = getPointFromCTM(outgoingStartPoint, outgoingPathE.getCTM());
        
        var nodeRectE = $j("#svg-" + nodeId).find("g").find("g").find("g").find("rect").get(0);
        var taskRectPoints = getViewportPoints(nodeRectE); //only for tokens running on the edge of task shape (not used now)
               
        //---------------------------------------------------------
        // Create path element
        //---------------------------------------------------------
        
        var rectWidth = nodeRectE.getBBox().width;
        var rectHeight = nodeRectE.getBBox().height;
        var radius;
        if (rectWidth < rectHeight) {
            radius = (rectHeight)/2;
        } else {
            radius = (rectWidth)/2;
        }
              
        if (isVirtual == "false") { //go through center
            var path =  "m" + startPoint.x + "," + startPoint.y + " L" + taskRectPoints.cc.x + "," + taskRectPoints.cc.y + 
                        " L" + endPoint.x + "," + endPoint.y; 
            var pathId = nodeId +"_path";
        } else {
            var pathId = nodeId +"_virtualpath";
            
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
                }
                else {
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
                }
                else {
                    
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
        
        var pathE = document.createElementNS(svgNS,"path");
        pathE.setAttributeNS(null,"id",pathId);  
        pathE.setAttributeNS(null,"d",path);
        pathE.setAttributeNS(null,"stroke","red");
        pathE.setAttributeNS(null,"stroke-width","1");
        pathE.setAttributeNS(null,"fill","none");
        pathE.setAttributeNS(null,"visibility","hidden");
        svgDocument().appendChild(pathE);

        //---------------------------------------------------------
        // Create animation element
        //---------------------------------------------------------
        var tokenPathE = document.createElementNS(svgNS,"circle");
            
        if (color=="orange") {
            var cx = 3;
            var cy = 3;
         } else {
            var cx = -3;
            var cy = -3;
        }
        
        var svg = svgDocument();
        var pt  = svg.createSVGPoint();
        
        var matrix  = $j("#"+pathId).get(0).getCTM();
        pt.x = cx;
        pt.y = cy;
        pt = pt.matrixTransform(matrix);         
        
        tokenPathE.setAttributeNS(null,"cx",pt.x);
        tokenPathE.setAttributeNS(null,"cy",pt.y);
        tokenPathE.setAttributeNS(null,"r",4);
        
        tokenPathE.setAttributeNS(null,"fill",color);
        tokenPathE.setAttributeNS(null,"class","tokenPath");
    
        var animateMotion = document.createElementNS(svgNS,"animateMotion");
        animateMotion.setAttributeNS(null,"class","tokenPathAnimation");
        animateMotion.setAttributeNS(null,"begin",begin+"s");
        animateMotion.setAttributeNS(null,"dur",duration+"s");
    
        
        var mPath = document.createElementNS(svgNS,"mpath");
        mPath.setAttributeNS(xlinkNS,"href","#"+pathId);
        
        animateMotion.appendChild(mPath);
        tokenPathE.appendChild(animateMotion);
        
        //Add tooltip to display caseId
        var titleE = document.createElementNS(svgNS,"title");
        var titleTextE = document.createTextNode(caseId);
        titleE.appendChild(titleTextE);
        tokenPathE.appendChild(titleE);        
    
        return tokenPathE;
    },
              
    changeSpeed: function (speedRatio){
      
        var currentTime = this.getCurrentTime();
        var newTime = currentTime/speedRatio;  
        
       /*
        * ---------------------------------------------
        * Update for every token
        * ---------------------------------------------
        */
        var curDur;
        var curBegin;
        var animateE;
        var pathAnimationE;
        var tokens;
        var tokenPaths;
        var animations = $j(".tokenAnimation"); //svgDoc.getElementsByClassName("tokenAnimation");
        for (var i=0;i<animations.length;i++) {
            animateE = animations[i];
            tokens = animateE.getElementsByClassName("token");      
            
            for (var j=0; j<tokens.length; j++) {
               tokenPaths = tokens[j].getElementsByClassName("tokenPath");
               for (k=0; k<tokenPaths.length; k++) {
                   pathAnimationE = tokenPaths[k].firstChild; 
                   
                   curDur = pathAnimationE.getAttribute("dur");
                   curDur = curDur.substr(0,curDur.length - 1);
                   
                   curBegin = pathAnimationE.getAttribute("begin");
                   curBegin = curBegin.substr(0,curBegin.length - 1);
                    
                   pathAnimationE.setAttributeNS(null,"dur", curDur/speedRatio + "s");
                   pathAnimationE.setAttributeNS(null,"begin", curBegin/speedRatio + "s");
                   
                   //console.log("preDur:" + curSpeed + " " + "preBegin:" + curBegin + " " + "newDur:" + curSpeed/speedRatio + " " + "newBegin:" + newBegin);
               }
            }
        } 
        
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
       
        //-------------------------------------------------
        // Update SVG document
        // In case of Chrome since setCurrentTime on SVG document does not 
        // apply updated attributes (begin, duration) dynamically for SVG elements,
        // must call reload document to make it effective 
        //-------------------------------------------------
        if ( jQuery.browser.webkit ) {
            var content = $j("#svgLoc > svg").html();
            svgDocument().innerHTML = content;
        }
        this.setCurrentTime(newTime);
        
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
    },
    
    fastforward: function () {
       if (this.getCurrentTime() >= this.endPos*this.slotEngineUnit/1000) {
           return;
       } else {
	       this.setCurrentTime(this.getCurrentTime() + 1*this.slotEngineUnit/1000); //move forward 5 slots
	   }
    },
    
    fastBackward: function () {
       if (this.getCurrentTime() <= this.startPos*this.slotEngineUnit/1000) {
           return;
       } else {
           this.setCurrentTime(this.getCurrentTime() - 1*this.slotEngineUnit/1000); //move backward 5 slots
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

    pause: function() {
        var img = document.getElementById("pause").getElementsByTagName("img")[0];
        this.pauseAnimations();
        img.alt = "Play";
        img.src = "/editor/libs/animation/images/control_play.png";
    },

    play: function() {
        var img = document.getElementById("pause").getElementsByTagName("img")[0];
        this.unpauseAnimations();
        img.alt = "Pause";
        img.src = "/editor/libs/animation/images/control_pause.png";
    },
    
    switchPlayPause: function () {
	var img = document.getElementById("pause").getElementsByTagName("img")[0];
        if (img.alt == "Pause") {
            this.pause();
        } else {
            this.play();
        }
    },
    
    /*
     * Animation elements are attached as childs of every edge SVG element.
     */
    animateEdges: function() {
        /*
        this.animateEdgeWidth("sid-CDC54AAE-5FA1-4CAC-8088-BD35F57D0560_1");  
        this.animateEdgeBlink("sid-CDC54AAE-5FA1-4CAC-8088-BD35F57D0560_1");
        this.animateEdgeColor("sid-CDC54AAE-5FA1-4CAC-8088-BD35F57D0560_1");
        
        this.animateEdgeWidth("sid-795CC5A0-EA40-42CB-A86E-38BDAD7067C0_1"); 
        this.animateEdgeColor("sid-3347E1CA-C190-492F-A838-67F16D38FBF6_1");
        */
    },
    
    animateEdgeWidth: function(edgeElementId) {
        /*
        var animateE = document.createElementNS(svgNS, "animate");
        animateE.setAttributeNS(null,"class","edgeAnimation");
        animateE.setAttributeNS(null,"attributeName", "stroke-width");
        animateE.setAttributeNS(null,"values", "2;5;10;5;2");
        animateE.setAttributeNS(null,"keyTimes", "0;0.2;0.5;0.8;1");
        animateE.setAttributeNS(null,"begin","0s");
        animateE.setAttributeNS(null,"dur", "40s");
        animateE.setAttributeNS(null,"fill","freeze");
        
        var edgeE = svgDocument().getElementById(edgeElementId);
        edgeE.appendChild(animateE);
        
        this.edgeAnimationElements.push(animateE);
        */
    },
    
    animateEdgeColor: function(edgeElementId) {
        /*
        var animateE = document.createElementNS(svgNS, "animate");
        animateE.setAttributeNS(null,"class","edgeAnimation");
        animateE.setAttributeNS(null,"attributeName", "stroke");
        animateE.setAttributeNS(null,"from", "red");
        animateE.setAttributeNS(null,"to", "red");
        animateE.setAttributeNS(null,"begin", begin);
        animateE.setAttributeNS(null,"dur", dur);
        animateE.setAttributeNS(null,"fill","remove");
        
        var edgeE = svgDocument().getElementById(edgeElementId);
        edgeE.appendChild(animateE);
        
        this.edgeAnimationElements.push(animateE);
        */
    },    
    
    animateEdgeBlink: function(edgeElementId) {
        /*
        var animateE = document.createElementNS(svgNS, "animate");
        animateE.setAttributeNS(null,"class","edgeAnimation");
        animateE.setAttributeNS(null,"attributeName", "visibility");
        animateE.setAttributeNS(null,"from", "hidden");
        animateE.setAttributeNS(null,"to","visible");
        animateE.setAttributeNS(null,"begin","12s");
        animateE.setAttributeNS(null,"dur", "0.5s");
        animateE.setAttributeNS(null,"fill","freeze");
        animateE.setAttributeNS(null,"repeatCount","20");
        
        var edgeE = svgDocument().getElementById(edgeElementId);
        edgeE.appendChild(animateE);
        
        this.edgeAnimationElements.push(animateE);
        */
    },     
    
    animateTasks: function() {
        /*
        svgDoc = svgDocument();
        
        svgDoc.appendChild(this.createTaskAnimation(svgDoc.getElementById("sid-9C2F52CE-0A69-49CA-9592-76D7A2496BA5bg_frame"), getRandomInt(10,50)));
        svgDoc.appendChild(this.createTaskAnimation(svgDoc.getElementById("sid-18FE9750-D3EE-47DE-BB31-0EFE64526364bg_frame"), getRandomInt(10,50)));
        svgDoc.appendChild(this.createTaskAnimation(svgDoc.getElementById("sid-F4B62085-B143-487A-9B60-9CB2FF6225BEbg_frame"), getRandomInt(10,50)));
        svgDoc.appendChild(this.createTaskAnimation(svgDoc.getElementById("sid-6C32A869-174A-4EB0-918C-4BF689C870DCbg_frame"), getRandomInt(10,50)));
        //console.log(tokenIndicatorE.innerHTML);
        //alert(tokenIndicatorE.innerHTML);
        */
    },
    
     /*
     * <g id="taskAnimation">
     *  <g class='taskAnimationGroup'>
     *      <rect> //bounding rect
     *      <rect>
     *          <animate class='taskAnimation'>
     */ 
    createTaskAnimation: function(taskRectE, duration) {
        var indicatorE = document.createElementNS(svgNS,"g");
        indicatorE.setAttributeNS(null,"id","taskAnimation");

        var taskRectCoord = getViewportPoints(taskRectE).nw;
        x = taskRectCoord.x;
        y = taskRectCoord.y;
        
        var indicator1 = this.createTaskAnimationRect(x, y+5, "red", duration+getRandomInt(5,70)+"s", "0;10;20;50;90;70;90;50;30;0", "0;0.1;0.2;0.25;0.5;0.6;0.7;0.8;0.9;1");               
        var indicator2 = this.createTaskAnimationRect(x, y+15, "blue", duration+getRandomInt(5,70)+"s", "0;10;20;50;90;70;90;50;30;0", "0;0.1;0.2;0.25;0.5;0.6;0.7;0.8;0.9;1");        
        indicatorE.appendChild(indicator1);
        indicatorE.appendChild(indicator2);
        
        return indicatorE;
    },
    
    createTaskAnimationRect: function(x, y, color, duration, values, keyTimes) {
        var indicatorE = document.createElementNS(svgNS,"g");
        indicatorE.setAttributeNS(null,"class","taskAnimationGroup");
        
        /*
         * ------------------------------------------
         * Bounding rectangle
         * ------------------------------------------
         */
        var boundingRectE = document.createElementNS(svgNS,"rect");               
        boundingRectE.setAttributeNS(null,"x", x);
        boundingRectE.setAttributeNS(null,"y", y);
        boundingRectE.setAttributeNS(null,"width", "100");
        boundingRectE.setAttributeNS(null,"height", "7");
        boundingRectE.setAttributeNS(null,"stroke", "blue");
        boundingRectE.setAttributeNS(null,"fill", "white");
        
        /*
         * ------------------------------------------
         * Animated rectangle
         * ------------------------------------------
         */
        var mainRectE = document.createElementNS(svgNS,"rect");               
        mainRectE.setAttributeNS(null,"x", x);
        mainRectE.setAttributeNS(null,"y", y);
        mainRectE.setAttributeNS(null,"width", "1");
        mainRectE.setAttributeNS(null,"height", "7");
        mainRectE.setAttributeNS(null,"fill", color);
        
        var animateE = document.createElementNS(svgNS, "animate");
        animateE.setAttributeNS(null,"class","taskAnimation");
        animateE.setAttributeNS(null,"attributeName", "width");
        animateE.setAttributeNS(null,"repeatCount", "1");
        animateE.setAttributeNS(null,"values", values);
        animateE.setAttributeNS(null,"keyTimes", keyTimes);
        animateE.setAttributeNS(null,"begin","0s");
        animateE.setAttributeNS(null,"dur",duration);
        animateE.setAttributeNS(null,"fill","freeze");
        animateE.setAttributeNS(null,"calcMode","linear");
        
        mainRectE.appendChild(animateE);
        this.taskAnimationElements.push(animateE);
        
        indicatorE.appendChild(boundingRectE);
        indicatorE.appendChild(mainRectE);
        
        return indicatorE;     
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
            progressE.appendChild(controller.createProgressIndicatorsForLog(logs[i], timeline, x, y));
            x += 60;
        }
        return progressE;
    },
    
    //Create cirle progress bar shapes in initial screen only
    createProgressIndicatorsInitial: function() {
        var progressE = document.createElementNS(svgNS,"g");
        progressE.setAttributeNS(null,"id","progressAnimation");

        //Log 1
        var log = { 
                name : "Log 1",
                color : "orange",
                progress : {
                        values : "0",
                        keyTimes : "0"}};
        var timeline = {timelineSlots : 120};
        progressE.appendChild(controller.createProgressIndicatorsForLog(log, timeline, 30, 90));
        
        //Log 2
        //Log 1
        var log = { 
                name : "Log 2",
                color : "blue",
                progress : {
                        values : "0",
                        keyTimes : "0"}};
        var timeline = {timelineSlots : 120};
        progressE.appendChild(controller.createProgressIndicatorsForLog(log, timeline, 90, 90));
        
        return progressE;
    },
    
    /*
     * Create progress indicator for one log
     * log: the log object (name, color, traceCount, progress, tokenAnimations)
     * x,y: the coordinates to draw the progress bar
     */
    createProgressIndicatorsForLog: function(log, timeline, x, y) {
        var pieE = document.createElementNS(svgNS,"g");
        pieE.setAttributeNS(null,"class","progress");
        
        var pathE = document.createElementNS(svgNS,"path");               
        pathE.setAttributeNS(null,"d","M " + x + "," + y + " m 0, 0 a 20,20 0 1,0 0.00001,0");
        pathE.setAttributeNS(null,"fill","#CCCCCC");
        pathE.setAttributeNS(null,"stroke",log.color);
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
        animateE.setAttributeNS(null,"begin","0s");
        animateE.setAttributeNS(null,"dur",timeline.timelineSlots*this.slotEngineUnit/1000 + "s");
        animateE.setAttributeNS(null,"fill","freeze");
        animateE.setAttributeNS(null,"repeatCount", "1");
        
        pathE.appendChild(animateE);
        this.progressAnimationElements.push(animateE);
        
        var textE = document.createElementNS(svgNS,"text");
        textE.setAttributeNS(null,"x", x);
        textE.setAttributeNS(null,"y", y - 10);
        textE.setAttributeNS(null,"text-anchor","middle");
        var textNode = document.createTextNode(log.name.substr(0,5) + "...");
        textE.appendChild(textNode);
        
        pieE.appendChild(pathE);
        pieE.appendChild(textE); 
        
        //alert(pieE.innerHTML); 
        
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
    createTimeline: function() {
        
        function addTimelineBar(lineX, lineY, lineLen, lineColor, textX, textY, text1, text2, parent) {
            var lineElement = document.createElementNS(svgNS,"line");
            lineElement.setAttributeNS(null,"x1", lineX);
            lineElement.setAttributeNS(null,"y1", lineY);
            lineElement.setAttributeNS(null,"x2", lineX);
            lineElement.setAttributeNS(null,"y2", lineY+lineLen);
            lineElement.setAttributeNS(null,"stroke", lineColor);
            if (lineColor == "red") {
                lineElement.setAttributeNS(null,"stroke-width","1");    
            } else {
                lineElement.setAttributeNS(null,"stroke-width",".5");
            }
            
            
            var textElement1 = document.createElementNS(svgNS,"text");
            textElement1.setAttributeNS(null,"x", textX);
            textElement1.setAttributeNS(null,"y", textY);
            textElement1.setAttributeNS(null,"text-anchor", "middle");
            textElement1.setAttributeNS(null,"font-size", "11");
            textElement1.innerHTML = text1;
            
            var textElement2 = document.createElementNS(svgNS,"text");
            textElement2.setAttributeNS(null,"x", textX);
            textElement2.setAttributeNS(null,"y", textY + 10);
            textElement2.setAttributeNS(null,"text-anchor", "middle");
            textElement2.setAttributeNS(null,"font-size", "11");
            textElement2.innerHTML = text2;            
            
            parent.appendChild(lineElement);  
            parent.appendChild(textElement1);
            parent.appendChild(textElement2);
        }
        
        var timelineElement = document.createElementNS(svgNS,"g");
        timelineElement.setAttributeNS(null,"id","timeline");
        
        var startTopX = 20;
        var startTopY = 15;
        var gapWidth = 9;       
        var gapValue = this.slotEngineUnit/1000;
        var lineLen = 30;
        var textToLineGap = 5; 
        var startValue = 0;
        var textValue = -gapValue;
        var lineTopX = -gapWidth + startTopX;
        var gapNum = this.timelineSlots;
       
        /*---------------------------
        Add text and line for the bar
        ---------------------------*/       
       
        for (var i=0;i<=gapNum;i++) {
            lineTopX += gapWidth;
            //textValue += gapValue;
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
        indicatorE.setAttributeNS(null,"fill","red");
        indicatorE.setAttributeNS(null,"height",lineLen-10);
        indicatorE.setAttributeNS(null,"width","4");
        
        var indicatorAnimation = document.createElementNS(svgNS,"animateMotion");
        indicatorAnimation.setAttributeNS(null,"id","timelineTick");
        indicatorAnimation.setAttributeNS(null,"begin","0s");
        indicatorAnimation.setAttributeNS(null,"dur",gapNum*gapValue + "s");
        indicatorAnimation.setAttributeNS(null,"by",gapValue);
        indicatorAnimation.setAttributeNS(null,"from", startTopX + "," + (startTopY+5));
        indicatorAnimation.setAttributeNS(null,"to",lineTopX + "," + (startTopY+5));
        indicatorAnimation.setAttributeNS(null,"fill","freeze");
        indicatorE.appendChild(indicatorAnimation);       
       
        timelineElement.appendChild(indicatorE); 
       
        return timelineElement;
       
    }
    
};

