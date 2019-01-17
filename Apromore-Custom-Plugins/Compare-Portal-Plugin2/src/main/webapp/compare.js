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

// This global object is used to store some current common attributes
// currentCompare: is the serialized XML of the current model being compared
// selected_greys: is the array of elements to be greyed out for the selected diff. This is stored here as
// the serialized XML from the editor does not support some visual attributes, e.g. opacity
var ORYX_Editor_Compare_Store = {current_compare:'', selected_greys:[]};
var ORYX_Editor_Compare_Repair_History = [];

ORYX.Editor.prototype.displayMLDifference = function(buttonIndex, type, start, a, b, newTasks, end, start2, end2, greys, annotations) {
    console.log('buttonIndex: ' + buttonIndex);
    console.log('type: ' + type);
    console.log('start: ' + start);
    console.log('end: ' + end);
    console.log('a: ' + a);
    console.log('b: ' + b);
    console.log('newTasks: ' + newTasks);
    console.log('start2: ' + start2);
    console.log('end2: ' + end2);
    console.log('greys: ' + greys);
    console.log('annotations: ' + annotations);
    // Find the shapes corresponding to the function arguments resource IDs
    // function indexJSON(json) {
    //     var shapes = {};
    //     json.childShapes.each(function (shape) {
    //         shapes[shape.resourceId] = shape;
    //     }.bind(this));
    //     return shapes;
    // };
    //
    // var json = jQuery.extend(true, {}, editorConfig);
    // var shapes = indexJSON(json);

    // Reload the original diagarm
    editor.getCanvas().clear();
    editor.importXML(ORYX_Editor_Compare_Store.current_compare);
    ORYX_Editor_Compare_Store.selected_greys = greys;

    // function uuid() {
    //     return "uuid-" + Math.floor((Math.random() * 1000000) + 1).toString();
    // };

    function addFlowNode(id, type, x, y, w, h) {
      var shapeId = editor.getCanvas().createShape(type, x, y, w, h);
      editor.getCanvas().highlight(shapeId);
      //shapes[shape.id] = shape;
      return shapeId;
    };

    function addAssociation(id, source, target, waypoints) {
        var flowId = editor.getCanvas().createAssociation(source, target, {waypoints:waypoints});
        editor.getCanvas().highlight(flowId);
        return flowId;
        //shapes[source].outgoing.push({ resourceId: flow.id });
    };

    function addSequenceFlow(id, source, target, waypoints) {
        var flowId = editor.getCanvas().createSequenceFlow(source, target, {waypoints:waypoints});
        editor.getCanvas().highlight(flowId);
        //shapes[source].outgoing.push({ resourceId: flow.id });
        return flowId;
    };

    function highlight(shapeId) {
        editor.getCanvas().highlight(shapeId);
    };

    function shapeCenter(shapeId) {
        var position = editor.getCanvas().shapeCenter(shapeId);
        //console.log(position);
        return position;
    };

    function distance(a, b) { return Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y)); };

    // Find a point at least minSpacing away from points a and b, forming at least a right angle.  Sign +1 or -1 selects the two solutions.
    function lagrangePoint(a, b, minSpacing, sign) {
        // console.log('lagrangePoint');
        // console.log(a, b);
        // console.log(b.y);
        var dx = (b.y - a.y);
        var dy = (a.x - b.x);
        var scale = sign * Math.max(minSpacing / Math.sqrt(dx * dx + dy * dy), 0.5);
        // console.log(dx, dy, scale);
        return {x: (a.x + b.x)/2 + (b.y - a.y)*scale, y: (a.y + b.y)/2 + (a.x - b.x)*scale};
    };

    function center(shapeId) {
        return editor.getCanvas().getCenter(shapeId);
    };

    // Create a gateway from the Start element, splitting between elements A and B and rejoining to the End element
    function createSplitAndJoin(gatewayStencilId) {
        if (message = validate(["start.length == 1", "a.length == 1", "b.length == 1", "end.length == 1"])) { alert(message); }
        var cs = shapeCenter(start[0]);
        var ca = shapeCenter(a[0]);
        var cb = shapeCenter(b[0]);
        var ce = shapeCenter(end[0]);
        //console.log('shapeCenter');
        //console.log(ca);
        //console.log(cb);
        var gateway1 = lagrangePoint(ca, cb, 90, +1);
        var gateway2 = lagrangePoint(ca, cb, 90, -1);
        //console.log(gateway1, gateway2);

        if (distance(cs,gateway1) + distance(gateway2,ce) > distance(cs,gateway2) + distance(gateway1,ce)) {
            var tmp = gateway1;
            gateway1 = gateway2;
            gateway2 = tmp;
        }
        var startplus = addFlowNode("sid-startplus", gatewayStencilId, gateway1.x, gateway1.y, 40, 40);
        var endplus = addFlowNode("sid-endplus", gatewayStencilId, gateway2.x, gateway2.y, 40, 40);

        addSequenceFlow("sid-start-startplus", start[0], startplus, [center(start[0]), center(startplus)]);
        addSequenceFlow("sid-startplus-a", startplus, a[0], [center(startplus), center(a[0])]);
        addSequenceFlow("sid-startplus-b", startplus, b[0], [center(startplus), center(b[0])]);
        highlight(a[0]);
        highlight(b[0]);
        addSequenceFlow("sid-a-endplus", a[0], endplus, [center(a[0]), center(endplus)]);
        addSequenceFlow("sid-b-endplus", b[0], endplus, [center(b[0]), center(endplus)]);
        addSequenceFlow("sid-endplus-end", endplus, end[0], [center(endplus), center(end[0])]);
    }

    function validate(constraints) {
        var violations = constraints
            .map(function(constraint, index, constraints) { return eval(constraint) ? null : constraint; })
            .filter(function(x) { return x });
        console.info("Violations: " + violations);
        console.dir(violations);
        return violations.length == 0 ? null : "Assertions violated by Compare-Logic for pattern " + type + ": " + violations;
    }

    window.setTimeout(function() {
        switch (type) {
            case "CAUSCONC1":  // In the log Task A occurs before Task B, while in the model they are concurrent
            case "CONFLICT3":
                if (message = validate(["start.length == 1", "a.length == 1", "b.length == 1", "end.length == 1"])) {
                    alert(message);
                }
                addSequenceFlow("sid-start-a", start[0], a[0]);
                highlight(a[0]);
                addSequenceFlow("sid-a-b", a[0], b[0]);
                highlight(b[0]);
                addSequenceFlow("sid-b-end", b[0], end[0]);
                break;
            case "CAUSCONC2":
            case "CONFLICT1":
                createSplitAndJoin("bpmn:ParallelGateway");
                break;

            case "CONFLICT2":
            case "CONFLICT4":
                createSplitAndJoin("bpmn:ExclusiveGateway");
                break;

            case "TASKABS":
            case "TASKABS3":
            case "TASKABSModel":
            case "UNOBSACYCLICINTER":
                if (message = validate(["start.length == 1", "end.length == 1"])) {
                    alert(message);
                }
                addSequenceFlow("sid-start-end", start[0], end[0]);
                break;

            case "TASKABS4":
            case "TASKABSLog":
            case "TASKSUB":
                if (message = validate(["start.length == 1", "newTasks.length >= 1", "end.length == 1"])) {
                    alert(message);
                }
                var cs = shapeCenter(start[0]);
                var ce = shapeCenter(end[0]);
                var cm = lagrangePoint(cs, ce, 130, +1);

                var newTasksBPMN = {};
                var loc = cm.x
                for (var i = 0; i < newTasks.length; i++) {
                    var middleId = addFlowNode("sid-a", "bpmn:Task", loc, cm.y, 100, 80);
                    //console.log(middleId);
                    editor.getCanvas().updateProperties(middleId, {name:newTasks[i]});
                    // middle.properties.tasktype = "None";
                    // middle["properties"]["name"] = newTasks[i];
                    // middle["properties"]["bgcolor"] = "white";

                    newTasksBPMN[i] = middleId;
                    loc = loc + 130;
                    if (i > 0)
                        addSequenceFlow("sid-start-ab", newTasksBPMN[i - 1], newTasksBPMN[i], [center(newTasksBPMN[i - 1]), center(newTasksBPMN[i])]);
                }

                addSequenceFlow("sid-start-a", start[0], newTasksBPMN[0], [center(start[0]), center(newTasksBPMN[0])]);
                addSequenceFlow("sid-a-end", newTasksBPMN[newTasks.length - 1], end[0], [center(newTasksBPMN[newTasks.length - 1]), center(end[0])]);

                //var middle = addFlowNode("sid-a", "Task", cm.x, cm.y, 100, 80);
                //middle.properties.tasktype = "None";
                //middle["properties"]["name"] = newTasks[0];
                //middle["properties"]["bgcolor"] = "white";

                //addSequenceFlow("sid-start-a", start[0], middle.resourceId);
                //addSequenceFlow("sid-a-end", middle.resourceId, end[0]);
                break;

            case "TASKRELOC":
                if (message = validate(["start.length == 1", "newTasks.length >= 1", "end.length == 1"])) {
                    alert(message);
                }
                var cs = shapeCenter(start[0]);
                var ce = shapeCenter(end[0]);
                var cm = lagrangePoint(cs, ce, 130, +1);

                var newTasksBPMN = {};
                var loc = cm.x
                for (var i = 0; i < newTasks.length; i++) {
                    var middleId = addFlowNode("sid-a", "bpmn:Task", loc, cm.y, 100, 80);
                    editor.getCanvas().updateProperties(middleId, {name:newTasks[i]});
                    // middle.properties.tasktype = "None";
                    // middle["properties"]["name"] = newTasks[i];
                    // middle["properties"]["bgcolor"] = "white";

                    newTasksBPMN[i] = middleId;
                    loc = loc + 130;
                    if (i > 0)
                        addSequenceFlow("sid-start-ab", newTasksBPMN[i - 1], newTasksBPMN[i]);
                }

                addSequenceFlow("sid-start-a", start[0], newTasksBPMN[0]);
                addSequenceFlow("sid-a-end", newTasksBPMN[newTasks.length - 1], end[0]);

                addSequenceFlow("sid-start-se", start2[0], end2[0]);
                break;

            case "TASKSKIP1":
                if (message = validate(["start.length == 1", "a.length >= 1", "end.length == 1"])) {
                    alert(message);
                }
                var cs = shapeCenter(start[0]);
                var cm1 = shapeCenter(a[0]);
                var cm2 = shapeCenter(a[a.length - 1]);
                var ce = shapeCenter(end[0]);
                var g1 = lagrangePoint(cs, cm1, 90, +1);
                var g2 = lagrangePoint(cm2, ce, 90, +1);

                var startplus = addFlowNode("sid-startplus", "bpmn:ExclusiveGateway", g1.x, g1.y, 40, 40);
                var endplus = addFlowNode("sid-endplus", "bpmn:ExclusiveGateway", g2.x, g2.y, 40, 40);
                addSequenceFlow("sid-startplus-endplus", startplus, endplus, [center(startplus), center(endplus)]);
                addSequenceFlow("sid-start-startplus", start[0], startplus, [center(start[0]), center(startplus)]);
                addSequenceFlow("sid-startplus-a", startplus, a[0], [center(startplus), center(a[0])]);
                for (i = 0; i < a.length; i++) {
                    highlight(a[i]);
                }
                addSequenceFlow("sid-a-endplus", a[a.length - 1], endplus, [center(a[a.length - 1]), center(endplus)]);
                addSequenceFlow("sid-endplus-end", endplus, end[0], [center(endplus), center(end[0])]);
                break;

            case "TASKSKIP2":
            case "UNOBSCYCLICINTER":
                //if (message = validate(["start.length == 1", "a.length >= 1", "end.length == 1"])) { alert(message); }
                if (start.length > 0)
                    addSequenceFlow("sid-start-a", start[0], a[0]);
                highlight(a[0]);
                for (i = 1; i < a.length; i++) {
                    // addSequenceFlow("sid-link-" + 1, a[i-1], a[i]);
                    highlight(a[i]);
                }

                if (end.length > 0)
                    addSequenceFlow("sid-a-end", a[a.length - 1], end[0]);
                break;

            case "UNMREPETITION2":
                if (message = validate(["start.length == 1", "a.length >= 1", "end.length == 1"])) {
                    alert(message);
                }
                var cs = shapeCenter(start[0]);
                var cm1 = shapeCenter(a[0]);
                var cm2 = shapeCenter(a[a.length - 1]);
                var ce = shapeCenter(end[0]);
                var g1 = lagrangePoint(cs, cm1, 90, +1);
                var g2 = lagrangePoint(cm2, ce, 90, +1);

                var startplus = addFlowNode("sid-startplus", "bpmn:ExclusiveGateway", g1.x, g1.y, 40, 40);
                var endplus = addFlowNode("sid-endplus", "bpmn:ExclusiveGateway", g2.x, g2.y, 40, 40);
                addSequenceFlow("sid-startplus-endplus", endplus, startplus, [center(endplus), center(startplus)]);
                addSequenceFlow("sid-start-startplus", start[0], startplus, [center(start[0]), center(startplus)]);
                addSequenceFlow("sid-startplus-a", startplus, a[0], [center(startplus), center(a[0])]);
                for (i = 0; i < a.length; i++) {
                    highlight(a[i]);
                }
                addSequenceFlow("sid-a-endplus", a[a.length - 1], endplus, [center(a[a.length - 1]), center(endplus)]);
                addSequenceFlow("sid-endplus-end", endplus, end[0], [center(endplus), center(end[0])]);
                break;

            case "UNMREPETITION":
                if (message = validate(["start.length == 1", "newTasks.length == 1", "end.length == 1"])) {
                    alert(message);
                }
                var s = start[0];
                var e = end[0];
                var cs = shapeCenter(s);
                var ce = shapeCenter(e);
                var cm = lagrangePoint(cs, ce, 0, +1);
                var cr = lagrangePoint(lagrangePoint(cs, cm, 100, +1), lagrangePoint(cm, ce, 100, +1), 0, +1);
                var middle = addFlowNode("sid-a", "bpmn:ExclusiveGateway", cm.x, cm.y, 40, 40);
                var repeating = addFlowNode("sid-b", "bpmn:Task", cr.x, cr.y, 100, 80);
                editor.getCanvas().updateProperties(repeating, {name:newTasks[0]});
                // repeating.properties.tasktype = "None";
                // repeating["properties"]["name"] = newTasks[0];
                // repeating["properties"]["bgcolor"] = "white";
                addSequenceFlow("sid-start-xor", s, middle, [center(s), center(middle)]);
                addSequenceFlow("sid-xor-end", middle, e, [center(middle), center(e)]);
                var mr = { x: (cr.x + cm.x) / 2 - (cr.y - cm.y) / 2, y: (cr.y + cm.y) / 2 + (cr.x - cm.x) / 2 };
                var rm = { x: (cr.x + cm.x) / 2 + (cr.y - cm.y) / 2, y: (cr.y + cm.y) / 2 - (cr.x - cm.x) / 2 };
                addSequenceFlow("sid-xor-repeating", middle, repeating, [lagrangePoint(cm, mr, 0, -1), lagrangePoint(mr, cr, 0, -1)]);
                addSequenceFlow("sid-repeating-xor", repeating, middle, [lagrangePoint(cr, rm, 0, -1), lagrangePoint(rm, cm, 0, -1)]);
                break;

            case "UNMREPETITIONINTERVAL":
                if (message = validate(["start.length == 1", "end.length == 1"])) {
                    alert(message);
                }
                var cs = shapeCenter(start[0]);
                var ce = shapeCenter(end[0]);
                var cm = lagrangePoint(cs, ce, 130, +1);

                var newTasksBPMN = {};
                var loc = cm.x
                for (var i = 0; i < newTasks.length; i++) {
                    var middle = addFlowNode("sid-a", "bpmn:Task", loc, cm.y, 100, 80);
                    editor.getCanvas().updateProperties(middle, {name:newTasks[i]});
                    // middle.properties.tasktype = "None";
                    // middle["properties"]["name"] = newTasks[i];
                    // middle["properties"]["bgcolor"] = "white";

                    newTasksBPMN[i] = middle;
                    loc = loc + 130;
                    if (i > 0)
                        addSequenceFlow("sid-start-ab", newTasksBPMN[i - 1], newTasksBPMN[i], [center(newTasksBPMN[i - 1]), center(newTasksBPMN[i])]);
                }

                addSequenceFlow("sid-start-a", start[0], newTasksBPMN[0], [center(start[0]), center(newTasksBPMN[0])]);
                addSequenceFlow("sid-a-end", newTasksBPMN[newTasks.length - 1], end[0], [center(newTasksBPMN[newTasks.length - 1]), center(end[0])]);
                break;

            case "TASKSWAP":
                if (message = validate(["start.length == 1", "a.length == 1", "b.length == 1", "end.length == 1"])) {
                    alert(message);
                }
                var cs = shapeCenter(start[0]);
                var ca = shapeCenter(a[0]);
                var cb = shapeCenter(b[0]);
                var ce = shapeCenter(end[0]);
                var sb = lagrangePoint(cs, cb, 0, +1);
                var ae = lagrangePoint(ca, ce, 0, -1);
                highlight(a[0]);
                highlight(b[0]);
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
            var anchor = shapeCenter(annotations[0]);
            var balloon = addFlowNode("sid-balloon", "bpmn:TextAnnotation", anchor.x + 100, anchor.y - 100, 100, 50);
            balloon["properties"]["text"] = annotations[1];
            addAssociation("sid-anchor-balloon", annotations[0], balloon.resourceId);
        }

        //reloadJSON(json);
        // Reduce the opacity of greyed-out elements
        //shapes = indexJSON(this._canvas.toJSON());
        for (var i = greys.length; i--;) {
            //shapes[greys[i]].getShape().node.setAttributeNS(null, "style", "opacity: 0.25");
            editor.getCanvas().greyOut([greys[i]]);
        }
    },0);
}

// Press the Apply button
ORYX.Editor.prototype.applyMLDifference = function() {
    var buttonIndex = -1;
    var buttons = zk.Widget.$(jq("$buttons"));
    for (var i=0;i<buttons.nChildren;i++) {
        if (buttons.getChildAt(i).isChecked()) {
            buttonIndex = i;
            break;
        }
    }
    if (buttonIndex < 0) {
        alert("You must select a difference to repair.");
        return;
    }
    else {
        window.setTimeout(function () {
            // Store the highlighted model into history
            var highlightDiffXML = editor.getCanvas().getXML();
            ORYX_Editor_Compare_Repair_History.push({
                compare: ORYX_Editor_Compare_Store.current_compare,
                highlight: highlightDiffXML,
                greys: ORYX_Editor_Compare_Store.selected_greys.slice(),
                diffIndex: buttonIndex
            });

            // Apply repair
            editor.getCanvas().removeShapes(ORYX_Editor_Compare_Store.selected_greys);
            editor.getCanvas().normalizeAll();

            // Update the repaired model and greys as the current one
            var afterApplyXML = editor.getCanvas().getXML();
            ORYX_Editor_Compare_Store.current_compare = afterApplyXML;
            ORYX_Editor_Compare_Store.selected_greys = [];

            // Compare the repaired model with the log: update the difference list
            zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onRepair', {bpmnXML: afterApplyXML, diffIndex: -1}));
        }, 0);
    }
};

// Press the Back button
ORYX.Editor.prototype.beforeApplyMLDifference = function() {
    var context = (ORYX_Editor_Compare_Repair_History.length == 1) ? ORYX_Editor_Compare_Repair_History[0] : ORYX_Editor_Compare_Repair_History.pop();

    if (context) {
        // Highlight the differences
        editor.getCanvas().clear();
        editor.importXML(context.highlight);
        // the below code complements the editor to grey out some elements
        // this is needed as some grey attributes are not contained in the serialized model XML
        window.setTimeout(function() {
            //console.log('context.highlight', context.highlight);
            //console.log('greys', context.selected_greys);
            var greys = context.greys;
            if (greys) {
                for (var i = greys.length; i--;) {
                    //shapes[greys[i]].getShape().node.setAttributeNS(null, "style", "opacity: 0.25");
                    editor.getCanvas().greyOut([greys[i]]);
                }
            }
        }, 0);

        // Update the old model and greys as the current one
        ORYX_Editor_Compare_Store.current_compare = context.compare;
        ORYX_Editor_Compare_Store.selected_greys = context.greys;

        // Compare the old model with the log: update the difference list
        zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onRepair', { bpmnXML:context.compare, diffIndex:context.diffIndex }));
    }
}

// function ApplyDiffHandler() {
//     //
// }
//
// ApplyDiffHandler.prototype.execute = function(context) {
//     zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onRepair', context.newXML));
// };
//
// ApplyDiffHandler.prototype.revert = function(context) {
//     bpmnXML = context.oldXML;
//     editor.importXML(bpmnXML);
//     zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onRepair', bpmnXML));
// };