/*
 * Note browser compatibility
 * Chome: does not support reference variable to point to DOM elements, must use selectors (getElementsBy...)
 *          otherwise the innerHTML is not updated, element attribute is not updated
 * Chome: svg.setCurrentTime is not processed properly, must call svg to reload via innerHTML
 */

jQuery.browser = {};
jQuery.browser.mozilla = /mozilla/.test(navigator.userAgent.toLowerCase()) && !/webkit    /.test(navigator.userAgent.toLowerCase());
jQuery.browser.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
jQuery.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());
jQuery.browser.msie = /msie/.test(navigator.userAgent.toLowerCase());


var svgNS = "http://www.w3.org/2000/svg";
var xlinkNS = "http://www.w3.org/1999/xlink";
var jsonModel; //contains parsed objects of the process model

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
        console.log("SVG Document Origin: x="+ pt.x + " y=" + pt.y); 
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
    timeCoefficient: 1000,
    timeOffset: 0,

    pauseAnimations: function() {
        this.svgDocuments.forEach(function(s) {
                s.pauseAnimations();
        });

        if (this.clockTimer) {
                clearTimeout(this.clockTimer);
        }
        //this.updateClock();
    },

    unpauseAnimations: function() {
        this.svgDocuments.forEach(function(s) {
                s.unpauseAnimations();
        });

        if (this.clockTimer) {
                clearTimeout(controller.clockTimer);
        }
        this.clockTimer = setInterval(updateClock_global, 100);
    },

    reset: function(jsonRaw) {
        var svg3 = $j("div#progress_display > svg")[1];
        this.svgDocuments.clear();
        this.svgDocuments.push($j("div#svgLoc > svg")[0]);
        this.svgDocuments.push($j("div#progress_display > svg")[0]);
        this.svgDocuments.push(svg3);

        this.clear();

        var json = JSON.parse(jsonRaw);
        var logs = json.logs;

	// Add log intervals to timeline
	var timelineElement = $j("#timeline")[0];
        var startTopX = 10;
        var startTopY = 60;
	for (var j=0; j<json.timeline.logs.length; j++) {
	    var log = json.timeline.logs[j];
	    var logInterval = document.createElementNS(svgNS,"line");
	    logInterval.setAttributeNS(null,"x1",startTopX + 10 * log.startDatePos);  // magic number 10 is gapWidth / gapValue
	    logInterval.setAttributeNS(null,"y1",startTopY + 5 + 7 * j);
	    logInterval.setAttributeNS(null,"x2",startTopX + 10 * log.endDatePos);
	    logInterval.setAttributeNS(null,"y2",startTopY + 5 + 7 * j);
	    logInterval.setAttributeNS(null,"style","stroke: "+log.color +"; stroke-width: 5");
	    timelineElement.insertBefore(logInterval, timelineElement.lastChild);
	}

        //Recreate progress indicators
        var progressIndicatorE = controller.createProgressIndicators(logs, json.timeline);
        svg3.appendChild(progressIndicatorE);

        for (var i=0;i<logs.length;i++) {
            var animationE = this.animateTokens(logs[i]);
            this.svgDocuments[0].appendChild(animationE);
        }

        this.startPos = json.timeline.startDateSlot;
        this.endPos = json.timeline.endDateSlot;
        this.timeOffset = (new Date(json.timeline.startDateLabel)).getTime();
        this.timeCoefficient = ((new Date(json.timeline.endDateLabel)).getTime() - (new Date(json.timeline.startDateLabel)).getTime()) / (json.timeline.endDateSlot - json.timeline.startDateSlot);

        this.start();
    },

    setCurrentTime: function(time) {
        this.svgDocuments.forEach(function(s) {
                s.setCurrentTime(time);
        });
        this.updateClock();
    },

    getCurrentTime: function() {
        return this.svgDocuments[0].getCurrentTime();
    },

    updateClock: function() {
	if (this.getCurrentTime() > this.endPos) {
		this.end();
	} else {
            var date = new Date();
            date.setTime(this.getCurrentTime() * this.timeCoefficient + this.timeOffset);
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
        }
    },
    
    start: function() {
        this.pause();
        this.setCurrentTime(this.startPos);
    },
   
    end: function() {
        this.pause();
        this.setCurrentTime(this.endPos);
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
                    tokenPathE = this.createTokenPathElement(id, begin, dur, color);
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
                    tokenPathE = this.createTokenNodeElement(id, begin, dur, color, isVirtual);
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
    createTokenPathElement: function (edgeId, begin, duration, color) {
        //---------------------------------------------
        // Create animation for edge
        //---------------------------------------------
        var pathId = $j("#svg-"+edgeId).find("g").find("g").find("g").find("path").get(0).getAttribute("id");
        console.log("edgeId=" + pathId + ", begin=" + begin + ", duration=" + duration);
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
    
        return tokenPathE;
    },
    
     /*
     * <circle class="tokenPath">
     *      <animateMotion class="tokenPathAnimation">
     *          <mpath>
     */
    createTokenNodeElement: function (nodeId, begin, duration, color, isVirtual) {
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
        /*
        if (isVirtual == "false") {
            pathE.setAttributeNS(null,"visibility","hidden");
        }
        */
        //pathE.setAttributeNS(null,"visibility","hidden");
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
    
        return tokenPathE;
    },

    // show / hide the path of the animated object
    togglePath: function (){
        $j('#svgPath').toggleClass('show');
    },
              
    changeSpeed: function (speedRatio){
        //alert("new " + ui.value);
        
        var currentTime = this.getCurrentTime();
        var newTime = currentTime/speedRatio;
        
        //svgDoc.removeChild(animationE);
        //animationE.parentNode.removeChild(animationE);      
        
       /*
        * ---------------------------------------------
        * Update for every token
        * ---------------------------------------------
        */
        var curSpeed;
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
                   
                   curSpeed = pathAnimationE.getAttribute("dur");
                   curSpeed = curSpeed.substr(0,curSpeed.length - 1);
                   
                   curBegin = pathAnimationE.getAttribute("begin");
                   curBegin = curBegin.substr(0,curBegin.length - 1);
                    
                   pathAnimationE.setAttributeNS(null,"dur", curSpeed/speedRatio + "s");
                   pathAnimationE.setAttributeNS(null,"begin", curBegin/speedRatio + "s");
                   
                   //console.log("preDur:" + curSpeed + " " + "preBegin:" + curBegin + " " + "newDur:" + curSpeed/speedRatio + " " + "newBegin:" + newBegin);
               }
            }
        } 
        
        var animations = $j(".progressAnimation");
        for (var i=0; i<animations.length; i++) {
            animateE = animations[i];
            
            curSpeed = animateE.getAttribute("dur");
            curSpeed = curSpeed.substr(0,curSpeed.length - 1);
            
            curBegin = animateE.getAttribute("begin");
            curBegin = curBegin.substr(0,curBegin.length - 1);
   
            animateE.setAttributeNS(null,"dur", curSpeed/speedRatio + "s");
            animateE.setAttributeNS(null,"begin", curBegin/speedRatio + "s");              
        }               
       
        //svgDoc.appendChild(animationE);
        //svgDoc.appendChild(progressAnimE);
        
        
        // reload svg document
        if ( jQuery.browser.webkit ) {
            var content = $j("#svgLoc > svg").html();
            svgDocument().innerHTML = content;
        }
        
        this.setCurrentTime(newTime);
        //svgDoc.unsuspendRedraw(suspendId);
    },
    
    fastforward: function () {
	this.setCurrentTime(this.getCurrentTime() + 4);
    },
    
    fastBackward: function () {
	this.setCurrentTime(this.getCurrentTime() - 4);
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
        this.animateEdgeWidth("sid-CDC54AAE-5FA1-4CAC-8088-BD35F57D0560_1");  
        this.animateEdgeBlink("sid-CDC54AAE-5FA1-4CAC-8088-BD35F57D0560_1");
        this.animateEdgeColor("sid-CDC54AAE-5FA1-4CAC-8088-BD35F57D0560_1");
        
        this.animateEdgeWidth("sid-795CC5A0-EA40-42CB-A86E-38BDAD7067C0_1"); 
        this.animateEdgeColor("sid-3347E1CA-C190-492F-A838-67F16D38FBF6_1");
    },
    
    animateEdgeWidth: function(edgeElementId) {
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
    },
    
    animateEdgeColor: function(edgeElementId) {
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
    },    
    
    animateEdgeBlink: function(edgeElementId) {
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
    },     
    
    animateTasks: function() {
        svgDoc = svgDocument();
        
        svgDoc.appendChild(this.createTaskAnimation(svgDoc.getElementById("sid-9C2F52CE-0A69-49CA-9592-76D7A2496BA5bg_frame"), getRandomInt(10,50)));
        svgDoc.appendChild(this.createTaskAnimation(svgDoc.getElementById("sid-18FE9750-D3EE-47DE-BB31-0EFE64526364bg_frame"), getRandomInt(10,50)));
        svgDoc.appendChild(this.createTaskAnimation(svgDoc.getElementById("sid-F4B62085-B143-487A-9B60-9CB2FF6225BEbg_frame"), getRandomInt(10,50)));
        svgDoc.appendChild(this.createTaskAnimation(svgDoc.getElementById("sid-6C32A869-174A-4EB0-918C-4BF689C870DCbg_frame"), getRandomInt(10,50)));
        //console.log(tokenIndicatorE.innerHTML);
        //alert(tokenIndicatorE.innerHTML);
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
        
        //var clientRect = taskRectE.getBoundingClientRect();
        //x = clientRect.left;
        //y = clientRect.top;
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
        console.log("values:" + log.progress.values);
        console.log("keyTimes:" + log.progress.keyTimes);
        animateE.setAttributeNS(null,"begin","0s");
        animateE.setAttributeNS(null,"dur",timeline.timelineSlots + "s");
        animateE.setAttributeNS(null,"fill","freeze");
        animateE.setAttributeNS(null,"repeatCount", "1");
        
        pathE.appendChild(animateE);
        this.progressAnimationElements.push(animateE);
        
        var textE = document.createElementNS(svgNS,"text");
        textE.setAttributeNS(null,"x", x);
        textE.setAttributeNS(null,"y", y - 10);
        textE.setAttributeNS(null,"text-anchor","middle");
        var textNode = document.createTextNode(log.name);
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
     */ 
    createTimeline: function() {
        
        function addTimelineBar(lineX, lineY, lineLen, textX, textY, text, parent) {
            var lineElement = document.createElementNS(svgNS,"line");
            lineElement.setAttributeNS(null,"x1", lineX);
            lineElement.setAttributeNS(null,"y1", lineY);
            lineElement.setAttributeNS(null,"x2", lineX);
            lineElement.setAttributeNS(null,"y2", lineY+lineLen);
            lineElement.setAttributeNS(null,"stroke","grey");
            lineElement.setAttributeNS(null,"stroke-width",".5");
            
            var textElement = document.createElementNS(svgNS,"text");
            textElement.setAttributeNS(null,"x", textX);
            textElement.setAttributeNS(null,"y", textY);
            textElement.setAttributeNS(null,"text-anchor", "middle");
            textElement.setAttributeNS(null,"font-size", "11");
            textElement.innerHTML = text;
            
            parent.appendChild(lineElement);  
            parent.appendChild(textElement);
        }
        
        var timelineElement = document.createElementNS(svgNS,"g");
        timelineElement.setAttributeNS(null,"id","timeline");
        
        startTopX = 10;
        startTopY = 60;
        gapWidth = 20;       
        gapValue = 2; //2s
        lineLen = 30;
        textToLineGap = 5; 
        startValue = 0;
        textValue = -gapValue;
        lineTopX = -gapWidth + startTopX;
        gapNum = 60;
       
        /*---------------------------
        Add text and line for the bar
        ---------------------------*/       
       
        for (var i=0;i<=gapNum;i++) {
            lineTopX += gapWidth;
            textValue += gapValue;
            addTimelineBar(lineTopX, startTopY, lineLen, lineTopX, startTopY-textToLineGap, textValue, timelineElement);
        }

        /*---------------------------
        Add timeline tick
        ---------------------------*/
        var indicatorE = document.createElementNS(svgNS,"rect");
        indicatorE.setAttributeNS(null,"fill","red");
        indicatorE.setAttributeNS(null,"height",lineLen-10);
        indicatorE.setAttributeNS(null,"width","8");
        
        var indicatorAnimation = document.createElementNS(svgNS,"animateMotion");
        indicatorAnimation.setAttributeNS(null,"begin","0s");
        indicatorAnimation.setAttributeNS(null,"dur",gapNum*gapValue);
        indicatorAnimation.setAttributeNS(null,"by",gapValue);
        indicatorAnimation.setAttributeNS(null,"from", startTopX + "," + (startTopY+5));
        indicatorAnimation.setAttributeNS(null,"to",lineTopX + "," + (startTopY+5));
        indicatorAnimation.setAttributeNS(null,"fill","freeze");
        indicatorE.appendChild(indicatorAnimation);       
       
        timelineElement.appendChild(indicatorE); 
       
        return timelineElement;
       
    }
    
};

