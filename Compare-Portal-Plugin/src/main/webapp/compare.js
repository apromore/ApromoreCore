/*
  ~ Copyright © 2009-2018 The Apromore Initiative.
  ~
  ~ This file is part of "Apromore".
  ~
  ~ "Apromore" is free software; you can redistribute it and/or modify
  ~ it under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ "Apromore" is distributed in the hope that it will be useful, but
  ~ WITHOUT ANY WARRANTY; without even the implied warranty
  ~ of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  ~ See the GNU Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this program.
  ~ If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
*/

                    // Replace any current model in oryxEditor1 with 
                    function reloadJSON(json) {
                        // Remove the existing model from the canvas
                        oryxEditor1.getCanvas().getChildShapes().each(function (shape) {
                            oryxEditor1.deleteShape(shape);
                        }.bind(this));

                        // Reload the modified model
                        oryxEditor1.importJSON(json, true);
                    };

                    ORYX.Editor.prototype.displayMLDifference = function(buttonIndex, type, start, a, b, newTasks, end, start2, end2, greys, annotations) {

                        // Find the shapes corresponding to the function arguments resource IDs
                        function indexJSON(json) {
                            var shapes = {};
                            json.childShapes.each(function (shape) {
                                shapes[shape.resourceId] = shape;
                            }.bind(this));
                            return shapes;
                        };

                        var json = jQuery.extend(true, {}, editorConfig);
                        var shapes = indexJSON(json);

                        function uuid() {
                            return "uuid-" + Math.floor((Math.random() * 1000000) + 1).toString();
                        }

                        function addFlowNode(id, stencilId, x, y, w, h) {
                            id = uuid();
                            var addition = {
                                "bounds": {
                                    "lowerRight": { "x": x + w/2, "y": y + h/2 },
                                    "upperLeft": { "x": x - w/2, "y": y - h/2 }
                                },
                                "childShapes": [],
                                "incoming": [],
                                "labels": [],
                                "outgoing": [],
                                "properties": {
                                    "bordercolor": "#FF0000"
                                },
                                "resourceId": id,
                                "stencil": { "id": stencilId }
                            };
                            json.childShapes.push(addition);
                            shapes[id] = addition;
                            return addition;
                        };

                        function addAssociation(id, source, target, waypoints) {
                            id = uuid();

                            function findCenter(shape) {
                                return { x: (shape.bounds.lowerRight.x - shape.bounds.upperLeft.x)/2, y: (shape.bounds.lowerRight.y - shape.bounds.upperLeft.y)/2 }; 
                            };

                            function findLeftEdge(shape) {
                                return { x: 0, y: (shape.bounds.lowerRight.y - shape.bounds.upperLeft.y)/2 }; 
                            };

                            var addition = {
                                "childShapes": [],
                                "dockers": [
                                    findCenter(shapes[source]),
                                    findLeftEdge(shapes[target])
                                ],
                                "incoming": [],
                                "labels": [],
                                "outgoing": [
                                    { "resourceId": target }
                                ],
                                "properties": {
                                    "bordercolor": "#FF0000"
                                },
                                "resourceId": id,
                                "stencil": { "id": "Association_Undirected" },
                                "source": { "resourceId": source },
                                "target": { "resourceId": target }
                            };
                            if (waypoints) {
                                var endpoint = addition.dockers.pop();
                                addition.dockers = addition.dockers.concat(waypoints);
                                addition.dockers.push(endpoint);
                            }
                            json.childShapes.push(addition);
                            shapes[source].outgoing.push({ resourceId: id });
                        };

                        function addSequenceFlow(id, source, target, waypoints) {
                            id = uuid();

                            function findCenter(shape) {
                                return { x: (shape.bounds.lowerRight.x - shape.bounds.upperLeft.x)/2, y: (shape.bounds.lowerRight.y - shape.bounds.upperLeft.y)/2 }; 
                            };

                            var addition = {
                                "childShapes": [],
                                "dockers": [
                                    findCenter(shapes[source]),
                                    findCenter(shapes[target])
                                ],
                                "incoming": [],
                                "labels": [],
                                "outgoing": [
                                    { "resourceId": target }
                                ],
                                "properties": {
                                    "bordercolor": "#FF0000"
                                },
                                "resourceId": id,
                                "stencil": { "id": "SequenceFlow" },
                                "source": { "resourceId": source },
                                "target": { "resourceId": target }
                            };
                            if (waypoints) {
                                var endpoint = addition.dockers.pop();
                                addition.dockers = addition.dockers.concat(waypoints);
                                addition.dockers.push(endpoint);
                            }
                            json.childShapes.push(addition);
                            shapes[source].outgoing.push({ resourceId: id });
                        };

                        function highlight(shape) {
                            shape.properties["selected"] = true;
                            shape.properties["selectioncolor"] = "#FF0000";
                        };

                        function shapeCenter(shape) {
                            return {x: (shape.bounds.lowerRight.x + shape.bounds.upperLeft.x) / 2,
                                    y: (shape.bounds.lowerRight.y + shape.bounds.upperLeft.y) / 2};
                        }

                        function distance(a, b) { return Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y)); };

                        // Find a point at least minSpacing away from points a and b, forming at least a right angle.  Sign +1 or -1 selects the two solutions.
                        function lagrangePoint(a, b, minSpacing, sign) {
                            var dx = (b.y - a.y);
                            var dy = (a.x - b.x);
                            var scale = sign * Math.max(minSpacing / Math.sqrt(dx * dx + dy * dy), 0.5);
                            return {x: (a.x + b.x)/2 + (b.y - a.y)*scale, y: (a.y + b.y)/2 + (a.x - b.x)*scale};
                        };

                        // Create a gateway from the Start element, splitting between elements A and B and rejoining to the End element
                        function createSplitAndJoin(gatewayStencilId) {
                            if (message = validate(["start.length == 1", "a.length == 1", "b.length == 1", "end.length == 1"])) { alert(message); }
                            var cs = shapeCenter(shapes[start[0]]);
                            var ca = shapeCenter(shapes[a[0]]);
                            var cb = shapeCenter(shapes[b[0]]);
                            var ce = shapeCenter(shapes[end[0]]);
                            var gateway1 = lagrangePoint(ca, cb, 90, +1);
                            var gateway2 = lagrangePoint(ca, cb, 90, -1);
                            if (distance(cs,gateway1) + distance(gateway2,ce) > distance(cs,gateway2) + distance(gateway1,ce)) {
                                var tmp = gateway1;
                                gateway1 = gateway2;
                                gateway2 = tmp;
                            }
                            var startplus = addFlowNode("sid-startplus", gatewayStencilId, gateway1.x, gateway1.y, 40, 40).resourceId;
                            var endplus = addFlowNode("sid-endplus", gatewayStencilId, gateway2.x, gateway2.y, 40, 40).resourceId;

                            addSequenceFlow("sid-start-startplus", start[0], startplus);
                            addSequenceFlow("sid-startplus-a", startplus, a[0]);
                            addSequenceFlow("sid-startplus-b", startplus, b[0]);
                            highlight(shapes[a[0]]);
                            highlight(shapes[b[0]]);
                            addSequenceFlow("sid-a-endplus", a[0], endplus);
                            addSequenceFlow("sid-b-endplus", b[0], endplus);
                            addSequenceFlow("sid-endplus-end", endplus, end[0]);
                        }

                        function validate(constraints) {
                            var violations = constraints
                                .map(function(constraint, index, constraints) { return eval(constraint) ? null : constraint; })
                                .filter(function(x) { return x });
                            console.info("Violations: " + violations);
                            console.dir(violations);
                            return violations.length == 0 ? null : "Assertions violated by Compare-Logic for pattern " + type + ": " + violations;
                        }

                        switch (type) {
                        case "CAUSCONC1":  // In the log Task A occurs before Task B, while in the model they are concurrent
                        case "CONFLICT3":
                            if (message = validate(["start.length == 1", "a.length == 1", "b.length == 1", "end.length == 1"])) { alert(message); }
                            addSequenceFlow("sid-start-a", start[0], a[0]);
                            highlight(shapes[a]);
                            addSequenceFlow("sid-a-b", a[0], b[0]);
                            highlight(shapes[b]);
                            addSequenceFlow("sid-b-end", b[0], end[0]);
                            break;

                        case "CAUSCONC2":
                        case "CONFLICT1":
                            createSplitAndJoin("ParallelGateway");
                            break;

                        case "CONFLICT2":
                        case "CONFLICT4":
                            createSplitAndJoin("Exclusive_Databased_Gateway");
                            break;

                        case "TASKABS":
                        case "TASKABS3":
                        case "TASKABSModel":
                        case "UNOBSACYCLICINTER":
                            if (message = validate(["start.length == 1", "end.length == 1"])) { alert(message); }
                            addSequenceFlow("sid-start-end", start[0], end[0]);
                            break;

                        case "TASKABS4":
                        case "TASKABSLog":
                        case "TASKSUB":
                            if (message = validate(["start.length == 1", "newTasks.length >= 1", "end.length == 1"])) { alert(message); }
                            var cs = shapeCenter(shapes[start[0]]);
                            var ce = shapeCenter(shapes[end[0]]);
                            var cm = lagrangePoint(cs, ce, 130, +1);
                            
                            var newTasksBPMN = {};
                            var loc = cm.x
                            for(var i = 0; i < newTasks.length; i++){
                                var middle = addFlowNode("sid-a", "Task", loc, cm.y, 100, 80);
                                middle.properties.tasktype = "None";
                                middle["properties"]["name"] = newTasks[i];
                                middle["properties"]["bgcolor"] = "white";

                                newTasksBPMN[i] = middle;
                                loc = loc + 130;
                                if(i > 0)
                                    addSequenceFlow("sid-start-ab", newTasksBPMN[i-1].resourceId, newTasksBPMN[i].resourceId);
                            }

                            addSequenceFlow("sid-start-a", start[0], newTasksBPMN[0].resourceId);
                            addSequenceFlow("sid-a-end", newTasksBPMN[newTasks.length-1].resourceId, end[0]);

                            //var middle = addFlowNode("sid-a", "Task", cm.x, cm.y, 100, 80);
                            //middle.properties.tasktype = "None";
                            //middle["properties"]["name"] = newTasks[0];
                            //middle["properties"]["bgcolor"] = "white";

                            //addSequenceFlow("sid-start-a", start[0], middle.resourceId);
                            //addSequenceFlow("sid-a-end", middle.resourceId, end[0]);
                            break;

                        case "TASKRELOC":
                         if (message = validate(["start.length == 1", "newTasks.length >= 1", "end.length == 1"])) { alert(message); }
                            var cs = shapeCenter(shapes[start[0]]);
                            var ce = shapeCenter(shapes[end[0]]);
                            var cm = lagrangePoint(cs, ce, 130, +1);

                            var newTasksBPMN = {};
                            var loc = cm.x
                            for(var i = 0; i < newTasks.length; i++){
                                var middle = addFlowNode("sid-a", "Task", loc, cm.y, 100, 80);
                                middle.properties.tasktype = "None";
                                middle["properties"]["name"] = newTasks[i];
                                middle["properties"]["bgcolor"] = "white";

                                newTasksBPMN[i] = middle;
                                loc = loc + 130;
                                if(i > 0)
                                    addSequenceFlow("sid-start-ab", newTasksBPMN[i-1].resourceId, newTasksBPMN[i].resourceId);
                            }

                            addSequenceFlow("sid-start-a", start[0], newTasksBPMN[0].resourceId);
                            addSequenceFlow("sid-a-end", newTasksBPMN[newTasks.length-1].resourceId, end[0]);

                            addSequenceFlow("sid-start-se", start2[0], end2[0]);
                            break;

                        case "TASKSKIP1":
                            if (message = validate(["start.length == 1", "a.length >= 1", "end.length == 1"])) { alert(message); }
                            var cs = shapeCenter(shapes[start[0]]);
                            var cm1 = shapeCenter(shapes[a[0]]);
                            var cm2 = shapeCenter(shapes[a[a.length - 1]]);
                            var ce = shapeCenter(shapes[end[0]]);
                            var g1 = lagrangePoint(cs, cm1, 90, +1);
                            var g2 = lagrangePoint(cm2, ce, 90, +1);

                            var startplus = addFlowNode("sid-startplus", "Exclusive_Databased_Gateway", g1.x, g1.y, 40, 40).resourceId;
                            var endplus = addFlowNode("sid-endplus", "Exclusive_Databased_Gateway", g2.x, g2.y, 40, 40).resourceId;
                            addSequenceFlow("sid-startplus-endplus", startplus, endplus);
                            addSequenceFlow("sid-start-startplus", start[0], startplus);
                            addSequenceFlow("sid-startplus-a", startplus, a[0]);
                            for (i=0; i < a.length; i++) {
                                highlight(shapes[a[i]]);
                            }
                            addSequenceFlow("sid-a-endplus", a[a.length - 1], endplus);
                            addSequenceFlow("sid-endplus-end", endplus, end[0]);
                            break;

                        case "TASKSKIP2":
                        case "UNOBSCYCLICINTER":
                            //if (message = validate(["start.length == 1", "a.length >= 1", "end.length == 1"])) { alert(message); }
                            if(start.length > 0)
                                addSequenceFlow("sid-start-a", start[0], a[0]);
                            highlight(shapes[a[0]]);
                            for (i=1; i < a.length; i++) {
                                // addSequenceFlow("sid-link-" + 1, a[i-1], a[i]);
                                highlight(shapes[a[i]]);
                            }

                            if(end.length > 0)
                                addSequenceFlow("sid-a-end", a[a.length - 1], end[0]);
                            break;
                            
                        case "UNMREPETITION2":
                            if (message = validate(["start.length == 1", "a.length >= 1", "end.length == 1"])) { alert(message); }
                            var cs = shapeCenter(shapes[start[0]]);
                            var cm1 = shapeCenter(shapes[a[0]]);
                            var cm2 = shapeCenter(shapes[a[a.length - 1]]);
                            var ce = shapeCenter(shapes[end[0]]);
                            var g1 = lagrangePoint(cs, cm1, 90, +1);
                            var g2 = lagrangePoint(cm2, ce, 90, +1);

                            var startplus = addFlowNode("sid-startplus", "Exclusive_Databased_Gateway", g1.x, g1.y, 40, 40).resourceId;
                            var endplus = addFlowNode("sid-endplus", "Exclusive_Databased_Gateway", g2.x, g2.y, 40, 40).resourceId;
                            addSequenceFlow("sid-startplus-endplus", endplus, startplus);
                            addSequenceFlow("sid-start-startplus", start[0], startplus);
                            addSequenceFlow("sid-startplus-a", startplus, a[0]);
                            for (i=0; i < a.length; i++) {
                                highlight(shapes[a[i]]);
                            }
                            addSequenceFlow("sid-a-endplus", a[a.length - 1], endplus);
                            addSequenceFlow("sid-endplus-end", endplus, end[0]);
                            break;

                        case "UNMREPETITION":
                            if (message = validate(["start.length == 1", "newTasks.length == 1", "end.length == 1"])) { alert(message); }
                            var s = start[0];
                            var e = end[0];
                            var cs = shapeCenter(shapes[s]);
                            var ce = shapeCenter(shapes[e]);
                            var cm = lagrangePoint(cs, ce, 0, +1);
                            var cr = lagrangePoint(lagrangePoint(cs, cm, 100, +1), lagrangePoint(cm, ce, 100, +1), 0, +1);
                            var middle = addFlowNode("sid-a", "Exclusive_Databased_Gateway", cm.x, cm.y, 40, 40);
                            var repeating = addFlowNode("sid-b", "Task", cr.x, cr.y, 100, 80);
                            repeating.properties.tasktype = "None";
                            repeating["properties"]["name"] = newTasks[0];
                            repeating["properties"]["bgcolor"] = "white";
                            addSequenceFlow("sid-start-xor", s, middle.resourceId);
                            addSequenceFlow("sid-xor-end", middle.resourceId, e);
                            var mr = {x: (cr.x + cm.x)/2 - (cr.y - cm.y)/2, y: (cr.y + cm.y)/2 + (cr.x - cm.x)/2};
                            var rm = {x: (cr.x + cm.x)/2 + (cr.y - cm.y)/2, y: (cr.y + cm.y)/2 - (cr.x - cm.x)/2};
                            addSequenceFlow("sid-xor-repeating", middle.resourceId, repeating.resourceId, [lagrangePoint(cm, mr, 0, -1), lagrangePoint(mr, cr, 0, -1)]);
                            addSequenceFlow("sid-repeating-xor", repeating.resourceId, middle.resourceId, [lagrangePoint(cr, rm, 0, -1), lagrangePoint(rm, cm, 0, -1)]);
                            break;

                        case "UNMREPETITIONINTERVAL":
                            if (message = validate(["start.length == 1", "end.length == 1"])) { alert(message); }
                            var cs = shapeCenter(shapes[start[0]]);
                            var ce = shapeCenter(shapes[end[0]]);
                            var cm = lagrangePoint(cs, ce, 130, +1);

                            var newTasksBPMN = {};
                            var loc = cm.x
                            for(var i = 0; i < newTasks.length; i++){
                                var middle = addFlowNode("sid-a", "Task", loc, cm.y, 100, 80);
                                middle.properties.tasktype = "None";
                                middle["properties"]["name"] = newTasks[i];
                                middle["properties"]["bgcolor"] = "white";

                                newTasksBPMN[i] = middle;
                                loc = loc + 130;
                                if(i > 0)
                                    addSequenceFlow("sid-start-ab", newTasksBPMN[i-1].resourceId, newTasksBPMN[i].resourceId);
                            }

                            addSequenceFlow("sid-start-a", start[0], newTasksBPMN[0].resourceId);
                            addSequenceFlow("sid-a-end", newTasksBPMN[newTasks.length-1].resourceId, end[0]);
                            break;

                        case "TASKSWAP":
                            if (message = validate(["start.length == 1", "a.length == 1", "b.length == 1", "end.length == 1"])) { alert(message); }
                            var cs = shapeCenter(shapes[start[0]]);
                            var ca = shapeCenter(shapes[a[0]]);
                            var cb = shapeCenter(shapes[b[0]]);
                            var ce = shapeCenter(shapes[end[0]]);
                            var sb = lagrangePoint(cs, cb, 0, +1);
                            var ae = lagrangePoint(ca, ce, 0, -1);
                            highlight(shapes[a[0]]);
                            highlight(shapes[b[0]]);
                            addSequenceFlow("sid-start-b", start[0], b[0], [sb]);
                            addSequenceFlow("sid-b-a", b[0], a[0]);
                            addSequenceFlow("sid-a-end", a[0], end[0], [ae]);
                            break;

			            default:
                            alert("Unsupported difference type \"" + type + "\"");
                            return;
                        }

                        // Add annotation text
                        if (annotations) {
                            var anchor = shapeCenter(shapes[annotations[0]]);
                            var balloon = addFlowNode("sid-balloon", "TextAnnotation", anchor.x + 100, anchor.y - 100, 100, 50);
                            balloon["properties"]["text"] = annotations[1];
                            addAssociation("sid-anchor-balloon", annotations[0], balloon.resourceId);
                        }

                        reloadJSON(json);
                        // Reduce the opacity of greyed-out elements
                        shapes = indexJSON(this._canvas.toJSON());
                        for (var i = greys.length; i--;) {
                            shapes[greys[i]].getShape().node.setAttributeNS(null, "style", "opacity: 0.25");
                        }

                        console.dir(json);

                        ORYX.Editor.prototype.repairMLDifference = function() {

                            // Remove the greyed-out elements
                            for (var i = greys.length; i--;) {
                                function removeElement(shape) {
                                    json.childShapes.each(function (childShape) {
                                        if (childShape && childShape.resourceId == shape.resourceId) {
                                            console.log("Removing edge " + shape.resourceId);
                                            json.childShapes.remove(childShape);
                                        }
                                    }.bind(this));
                                }
                                removeElement(shapes[greys[i]].getShape());
                            }

                            // Remove red highlighting from added elements
                            json.childShapes.each(function (childShape) {
                                if (childShape && childShape.properties.bordercolor == "#FF0000") {
                                    childShape.properties.bordercolor = "#000000";
                                }
                                if (childShape && childShape.stencil.id == "Task" && childShape.properties.bgcolor == "white") {
                                    childShape.properties.bgcolor = "#FFFFCC";
                                }
                                if (childShape && childShape.properties.selected) {
                                    childShape.properties.selected = false;
                                }
                            }.bind(this));

                            reloadJSON(json);

                            // Inverse function to JSON.parse, because JSON.stringify isn't
                            function stringify(obj) {

                                var t = typeof (obj);
	                            if (t != "object" || obj === null) {

                                    // simple data type
                                    if (t == "string") obj = '"'+obj+'"';
                                    return String(obj);

	                            }
                                else {
                                    // recurse array or object
                                    var n, v, json = [], arr = (obj && obj.constructor == Array);

                                    for (n in obj) {
                                        v = obj[n]; t = typeof(v);

                                        if (t == "string") v = '"'+v+'"';
                                        else if (t == "object" && v !== null) v = stringify(v);

                                        if (t != "function") json.push((arr ? "" : '"' + n + '":') + String(v));
                                    }

                                    return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
                                }
                            };

                            // Convert the repaired model from JSON to BPMN and re-invoke the comparison logic
                            new Ajax.Request(exportPath, {
                                parameters: {'data': stringify(json)},
                                method: 'POST',

                                onSuccess: function(transport) {
                                    zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onRepair', transport.responseText));
                                    editorConfig = json;
                                }.bind(this),

                                onFailure: function(transport) {
                                    Ext.Msg.show({
                                        title: "Error",
                                        msg: "Failed to recalculate differences!",
                                        buttons: Ext.Msg.OK,
                                        icon: Ext.Msg.ERROR
                                    }).getDialog().syncSize()
                                }.bind(this)
                            });
                        }
                    }

                    ORYX.Editor.prototype.repairMLDifference = function() {
                        alert("You must select a difference to repair.");
                    }
