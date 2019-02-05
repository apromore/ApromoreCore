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

/**
 * The main feature of this plugin is using the Undo/Redo buttons on the editor toolbar
 * to control the execution of actions relating to not only the editor but also the difference list outside the editor.
 *
 * Therefore, a number of actions are designed to synchronize the editor and the difference list
 * The synchronization is done via Ajax calls from these actions to ZK that manages the whole plugin interface
 *
 * These actions must adhere to the rules defined in the Command Stack of bpmn.io to make Undo/Redo viable
 * Some key rules include:
 *  - The Undo of a group of actions is performed via composite actions playing the role of base action. All actions
 *  performed within a composite action will be undoed together
 *  - The execute method does not allow nested actions (must be atomic execution), thus composite actions must implement
 *  either preExecute or postExecute methods.
 *  - Code placed in the pre or postExecute methods will not be executed in redo. Therefore, individual actions must
 *  implement the execute method, not pre or postExecute.
 *
 * Composite actions:
 *  - ORYX_Compare_HighlightAction: click a difference item in the list
 *  - ORYX_Compare_ApplyAction: click the Apply button
 *  - ORYX_Compare_RecompareAction: click the Re-compare button
 * Individual actions:
 *  - ORYX_Compare_CompareActionHandler
 *  - ORYX_Compare_NoUndoCompareActionHandler
 *  - ORYX_Compare_DiffSelectionActionHandler
 *  - ORYX_Compare_DisableApplyButtonActionHandler
 *  - ORYX_Compare_EnableApplyButtonActionHandler
 *
 * A global ORYX.ComparePlugin object is used to manage actions for the whole plugin,
 * including changes to the editor and the difference list
 * Changes to the model are made via the global editor object
 * Changes to the difference list are made via ZK Ajax and ZK javascript.
 */

if (!ORYX) {
  ORYX = {};
}

ORYX.ComparePlugin = Clazz.extend({

  //_currentContext: undefined, // keep track of the current context for highlighting and applying

  _highlightedDifferences: {},

  construct: function() {    // this._currentContext = {
    //     diffXML: editor.getCanvas().getXML()
    // };

    // this._currentXML = editor.getCanvas().getXML();
  },

  _getDifference: function(buttonIndex, type, start, a, b, newTasks, end, start2, end2, greys, annotations) {
    return {
      type: type,
      start: start,
      a: a,
      b: b,
      newTasks: newTasks,
      end: end,
      start2: start2,
      end2: end2,
      greys: greys,
      annotations: annotations,
      diffIndex: buttonIndex
    };
  },

  highlightDifference: function(buttonIndex, type, start, a, b, newTasks, end, start2, end2, greys, annotations) {
    // Undo highlights of the previous difference
    if (editor.getCanvas().checkLatestAction('ORYX.Compare.highlightDifference')) {
      editor.getCanvas().undo();
    }

    //var context = this._currentContext;
    var diff = this._getDifference(buttonIndex, type, start, a, b, newTasks, end, start2, end2, greys, annotations);
    diff.xml = editor.getCanvas().getXML();
    this._highlightedDifferences['d' + buttonIndex] = diff;
    var context = Object.assign({}, diff);
    editor.getCanvas().executeActionHandler('ORYX.Compare.highlightDifference', context);
  },

  // Press the Apply button
  applyDifference: function() {
    var buttonIndex = -1;
    var buttons = zk.Widget.$(jq("$buttons"));
    for (var i=0;i<buttons.nChildren;i++) {
      if (buttons.getChildAt(i).isChecked()) {
        buttonIndex = i;
        break;
      }
    }
    if (buttonIndex < 0) {
      alert("You must select a difference.");
      return;
    }
    else {
      if (this._highlightedDifferences['d' + buttonIndex]) {
        var context = Object.assign({}, this._highlightedDifferences['d' + buttonIndex]);
        editor.getCanvas().executeActionHandler('ORYX.Compare.applyDifference', context);
        this._highlightedDifferences.length = 0; //clear the current difference list
        //context.xml = editor.getCanvas().getXML();
      }
      else {
        window.alert('Something wrong: could not find the difference data associated with the clicked button.');
        return;
      }

    }
  },

  reCompare: function() {
    var context = {
        xml: editor.getCanvas().getXML(),
        diffIndex: -1,
        recompared: true
    };
    editor.getCanvas().executeActionHandler('ORYX.Compare.reCompare', context);
    this._highlightedDifferences.length = 0; //clear the current difference list
    //this._currentContext = context;
  }
});



/**
 * This command represents the highlight
 */
function ORYX_Compare_HighlightActionHandler() {}
ORYX_Compare_HighlightActionHandler.prototype.postExecute = function(context) {
  //console.log('commandStack.currentExecution.actions', editor.getCanvas()._editor.get('commandStack')._currentExecution.actions.slice());
  ORYX.CanvasUtilForCompare.highlightDifference(context);

  editor.getCanvas().executeActionHandler('ORYX.Compare.selectDifference', {diffIndex: context.diffIndex, oldDiffIndex:-1});

  editor.getCanvas().executeActionHandler('ORYX.Compare.enableApply', {});
  //console.log('commandStack.stack', editor.getCanvas()._editor.get('commandStack')._stack.slice());
}

//ORYX_Compare_HighlightActionHandler.prototype.revert = function(context) {
  //zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onRecompare', {bpmnXML: context.xml, diffIndex: context.oldDiffIndex}));
//}

/**
 * This command represents the apply action
 */
function ORYX_Compare_ApplyActionHandler() {}
ORYX_Compare_ApplyActionHandler.prototype.postExecute = function(context) {
    var canvas = editor.getCanvas();
    canvas.removeShapes(context.greys);
    canvas.normalizeAll();

    var afterApplyXML = canvas.getXML();
    context.oldXML = context.xml;
    context.oldDiffIndex = context.diffIndex;
    context.xml = afterApplyXML;
    context.diffIndex = -1; //reset the difference list
    editor.getCanvas().executeActionHandler('ORYX.Compare.compare', context);

    editor.getCanvas().executeActionHandler('ORYX.Compare.disableApply', {});
}


/**
 * This command represents the Compare action
 */
function ORYX_Compare_CompareActionHandler() {}
ORYX_Compare_CompareActionHandler.prototype.execute = function(context) {
  zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onRecompare', {bpmnXML: context.xml, diffIndex: -1}));
}
ORYX_Compare_CompareActionHandler.prototype.revert = function(context) {
  zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onRecompare', {bpmnXML: context.oldXML, diffIndex: context.oldDiffIndex}));
}

/**
 * This command represents the Compare action that is not undo-able
 */
function ORYX_Compare_NoUndoCompareActionHandler() {}
ORYX_Compare_NoUndoCompareActionHandler.prototype.execute = function(context) {
  zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onRecompare', {bpmnXML: context.xml, diffIndex: -1}));
}

/**
 * This command represents the Re-compare action
 */
function ORYX_Compare_ReCompareActionHandler() {}
ORYX_Compare_ReCompareActionHandler.prototype.postExecute = function(context) {
  editor.getCanvas().executeActionHandler('ORYX.Compare.compareNoUndo', context);
  editor.getCanvas().executeActionHandler('ORYX.Compare.disableApply', {});
}
ORYX_Compare_ReCompareActionHandler.prototype.revert = function(context) {
  zk.Widget.$(jq("$buttons")).clear(); //clear the difference list
  zk.Widget.$(jq("$apply")).setDisabled(true);
}

/**
 * This command is to select/clear an item in the difference list
 */
function ORYX_Compare_DiffSelectionActionHandler() {}
ORYX_Compare_DiffSelectionActionHandler.prototype.execute = function(context) {
  //editor.getCanvas().normalizeAll();
  zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onDiffSelection', {diffIndex: context.diffIndex}));
}
ORYX_Compare_DiffSelectionActionHandler.prototype.revert = function(context) {
  zAu.send(new zk.Event(zk.Widget.$(jq("$win")), 'onDiffSelection', {diffIndex: context.oldDiffIndex}));
}

/**
 * This command is to disable the Apply button
 */
function ORYX_Compare_DisableApplyButtonActionHandler() {}
ORYX_Compare_DisableApplyButtonActionHandler.prototype.execute = function(context) {
  zk.Widget.$(jq("$apply")).setDisabled(true);
}
ORYX_Compare_DisableApplyButtonActionHandler.prototype.revert = function(context) {
  zk.Widget.$(jq("$apply")).setDisabled(false);
}

/**
 * This command is to enable the Apply button
 */
function ORYX_Compare_EnableApplyButtonActionHandler() {}
ORYX_Compare_EnableApplyButtonActionHandler.prototype.execute = function(context) {
  zk.Widget.$(jq("$apply")).setDisabled(false);
}
ORYX_Compare_EnableApplyButtonActionHandler.prototype.revert = function(context) {
  zk.Widget.$(jq("$apply")).setDisabled(true);
}

/**
 * This command is to disable the Re-compare button
 */
function ORYX_Compare_DisableReCompareButtonActionHandler() {}
ORYX_Compare_DisableReCompareButtonActionHandler.prototype.execute = function(context) {
  zk.Widget.$(jq("$recompare")).setDisabled(true);
}
ORYX_Compare_DisableReCompareButtonActionHandler.prototype.revert = function(context) {
  zk.Widget.$(jq("$recompare")).setDisabled(false);
}


ORYX.CanvasUtilForCompare = {
  construct: function() {
    //
  },

  addFlowNode: function(id, type, x, y, w, h) {
    //console.log ('Element position: ' + 'x=' + x + ',' + 'y=' + y + '. isFinite(x)=' + isFinite(x) + '. isFinite(x)=' + isFinite(y));
    var shapeId = editor.getCanvas().createShape(type, x, y, w, h);
    editor.getCanvas().highlight(shapeId);
    return shapeId;
  },

  addAssociation: function(id, source, target, waypoints) {
    var flowId = editor.getCanvas().createAssociation(source, target, {waypoints:waypoints});
    editor.getCanvas().highlight(flowId);
    return flowId;
  },

  addSequenceFlow: function(id, source, target, waypoints) {
    var flowId = editor.getCanvas().createSequenceFlow(source, target, {waypoints:waypoints});
    editor.getCanvas().highlight(flowId);
    return flowId;
  },

  highlight: function(shapeId) {
    editor.getCanvas().highlight(shapeId);
  },

  shapeCenter: function (shapeId) {
    var position = editor.getCanvas().shapeCenter(shapeId);
    //console.log(position);
    return position;
  },

  distance: function (a, b) {
    return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
  },

// Find a point at least minSpacing away from points a and b, forming at least a right angle.  Sign +1 or -1 selects the two solutions.
// Bruce: fix Infinity error when a and b are the same element
  lagrangePoint: function (a, b, minSpacing, sign) {
    console.log('this.lagrangePoint');
    console.log(a, b);
    var dx = (b.y - a.y);
    var dy = (a.x - b.x);
    if (dx == 0 && dy == 0) {
      dx = a.y/2;
      dy = a.x/2;
    }
    var scale = sign*Math.max(minSpacing / Math.sqrt(dx * dx + dy * dy), 0.5);
    console.log(dx, dy, scale);

    return {x: (a.x + b.x)/2 + (b.y - a.y)*scale, y: (a.y + b.y)/2 + (a.x - b.x)*scale};
  },

  center: function (shapeId) {
    return editor.getCanvas().getCenter(shapeId);
  },

// Create a gateway from the Start element, splitting between elements A and B and rejoining to the End element
  createSplitAndJoin: function (gatewayStencilId, context) {
    if (message = this.validate(context, ["start.length == 1", "a.length == 1", "b.length == 1", "end.length == 1"])) { alert(message); }

    var buttonIndex = context.buttonIndex,
      type = context.type,
      start = context.start,
      a = context.a,
      b = context.b,
      newTasks = context.newTasks,
      end = context.end,
      start2 = context.start2,
      end2 = context.end2,
      greys = context.greys,
      annotations = context.annotations;

    var cs = this.shapeCenter(start[0]);
    var ca = this.shapeCenter(a[0]);
    var cb = this.shapeCenter(b[0]);
    var ce = this.shapeCenter(end[0]);
    //console.log('this.shapeCenter');
    //console.log(ca);
    //console.log(cb);
    var gateway1 = this.lagrangePoint(ca, cb, 90, +1);
    var gateway2 = this.lagrangePoint(ca, cb, 90, -1);
    //console.log(gateway1, gateway2);

    if (this.distance(cs,gateway1) + this.distance(gateway2,ce) > this.distance(cs,gateway2) + this.distance(gateway1,ce)) {
      var tmp = gateway1;
      gateway1 = gateway2;
      gateway2 = tmp;
    }

    var startplus = this.addFlowNode("sid-startplus", gatewayStencilId, gateway1.x, gateway1.y, 40, 40);
    var endplus = this.addFlowNode("sid-endplus", gatewayStencilId, gateway2.x, gateway2.y, 40, 40);

    this.addSequenceFlow("sid-start-startplus", start[0], startplus, [this.center(start[0]), this.center(startplus)]);
    this.addSequenceFlow("sid-startplus-a", startplus, a[0], [this.center(startplus), this.center(a[0])]);
    this.addSequenceFlow("sid-startplus-b", startplus, b[0], [this.center(startplus), this.center(b[0])]);

    this.highlight(a[0]);
    this.highlight(b[0]);

    this.addSequenceFlow("sid-a-endplus", a[0], endplus, [this.center(a[0]), this.center(endplus)]);
    this.addSequenceFlow("sid-b-endplus", b[0], endplus, [this.center(b[0]), this.center(endplus)]);
    this.addSequenceFlow("sid-endplus-end", endplus, end[0], [this.center(endplus), this.center(end[0])]);


  },

  validate: function (context, constraints) {
    var buttonIndex = context.buttonIndex,
      type = context.type,
      start = context.start,
      a = context.a,
      b = context.b,
      newTasks = context.newTasks,
      end = context.end,
      start2 = context.start2,
      end2 = context.end2,
      greys = context.greys,
      annotations = context.annotations;

    var violations = constraints
      .map(function(constraint, index, constraints) { return eval(constraint) ? null : constraint; })
      .filter(function(x) { return x });
    console.info("Violations: " + violations);
    console.dir(violations);
    return violations.length == 0 ? null : "Assertions violated by Compare-Logic for pattern " + type + ": " + violations;
  },

  highlightDifference: function(context) {

    var buttonIndex = context.buttonIndex,
      type = context.type,
      start = context.start,
      a = context.a,
      b = context.b,
      newTasks = context.newTasks,
      end = context.end,
      start2 = context.start2,
      end2 = context.end2,
      greys = context.greys,
      annotations = context.annotations;

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

    //window.setTimeout((function() {
      switch (type) {
        case "CAUSCONC1":  // In the log Task A occurs before Task B, while in the model they are concurrent
        case "CONFLICT3":
          if (message = this.validate(context, ["start.length == 1", "a.length == 1", "b.length == 1", "end.length == 1"])) {
            alert(message);
          }
          this.addSequenceFlow("sid-start-a", start[0], a[0]);
          this.highlight(a[0]);
          this.addSequenceFlow("sid-a-b", a[0], b[0]);
          this.highlight(b[0]);
          this.addSequenceFlow("sid-b-end", b[0], end[0]);
          break;
        case "CAUSCONC2":
        case "CONFLICT1":
          this.createSplitAndJoin("bpmn:ParallelGateway", context);
          break;

        case "CONFLICT2":
        case "CONFLICT4":
          this.createSplitAndJoin("bpmn:ExclusiveGateway", context);
          break;

        case "TASKABS":
        case "TASKABS3":
        case "TASKABSModel":
        case "UNOBSACYCLICINTER":
          if (message = this.validate(context, ["start.length == 1", "end.length == 1"])) {
            alert(message);
          }
          this.addSequenceFlow("sid-start-end", start[0], end[0]);
          break;

        case "TASKABS4":
        case "TASKABSLog":
        case "TASKSUB":
          //console.log('commandStack.currentExecution.actions before TASKSUB', editor.getCanvas()._editor.get('commandStack')._currentExecution.actions.slice());
          if (message = this.validate(context, ["start.length == 1", "newTasks.length >= 1", "end.length == 1"])) {
            alert(message);
          }
          var cs = this.shapeCenter(start[0]);
          var ce = this.shapeCenter(end[0]);
          var cm = this.lagrangePoint(cs, ce, 130, +1);

          console.log('lagrangePoint', cm);
          var factor = (cm.y > 0) ? 1.2 : 1/5;

          var newTasksBPMN = {};
          var loc = cm.x
          for (var i = 0; i < newTasks.length; i++) {
            var middleId = this.addFlowNode("sid-a", "bpmn:Task", loc, cm.y*factor, 100, 80);
            //console.log(middleId);
            editor.getCanvas().updateProperties(middleId, {name:newTasks[i]});
            // middle.properties.tasktype = "None";
            // middle["properties"]["name"] = newTasks[i];
            // middle["properties"]["bgcolor"] = "white";

            newTasksBPMN[i] = middleId;
            loc = loc + 130;
            if (i > 0)
              this.addSequenceFlow("sid-start-ab", newTasksBPMN[i - 1], newTasksBPMN[i], [this.center(newTasksBPMN[i - 1]), this.center(newTasksBPMN[i])]);
          }

          this.addSequenceFlow("sid-start-a", start[0], newTasksBPMN[0], [this.center(start[0]), this.center(newTasksBPMN[0])]);
          this.addSequenceFlow("sid-a-end", newTasksBPMN[newTasks.length - 1], end[0], [this.center(newTasksBPMN[newTasks.length - 1]), this.center(end[0])]);

          //var middle = this.addFlowNode("sid-a", "Task", cm.x, cm.y, 100, 80);
          //middle.properties.tasktype = "None";
          //middle["properties"]["name"] = newTasks[0];
          //middle["properties"]["bgcolor"] = "white";

          //addSequenceFlow("sid-start-a", start[0], middle.resourceId);
          //addSequenceFlow("sid-a-end", middle.resourceId, end[0]);
          break;

        case "TASKRELOC":
          if (message = this.validate(context, ["start.length == 1", "newTasks.length >= 1", "end.length == 1"])) {
            alert(message);
          }
          var cs = this.shapeCenter(start[0]);
          var ce = this.shapeCenter(end[0]);
          var cm = this.lagrangePoint(cs, ce, 130, +1);

          var newTasksBPMN = {};
          var loc = cm.x
          for (var i = 0; i < newTasks.length; i++) {
            var middleId = this.addFlowNode("sid-a", "bpmn:Task", loc, cm.y, 100, 80);
            editor.getCanvas().updateProperties(middleId, {name:newTasks[i]});
            // middle.properties.tasktype = "None";
            // middle["properties"]["name"] = newTasks[i];
            // middle["properties"]["bgcolor"] = "white";

            newTasksBPMN[i] = middleId;
            loc = loc + 130;
            if (i > 0)
              this.addSequenceFlow("sid-start-ab", newTasksBPMN[i - 1], newTasksBPMN[i]);
          }

          this.addSequenceFlow("sid-start-a", start[0], newTasksBPMN[0]);
          this.addSequenceFlow("sid-a-end", newTasksBPMN[newTasks.length - 1], end[0]);
          this.addSequenceFlow("sid-start-se", start2[0], end2[0]);

          break;

        case "TASKSKIP1":
          if (message = this.validate(context, ["start.length == 1", "a.length >= 1", "end.length == 1"])) {
            alert(message);
          }
          var cs = this.shapeCenter(start[0]);
          var cm1 = this.shapeCenter(a[0]);
          var cm2 = this.shapeCenter(a[a.length - 1]);
          var ce = this.shapeCenter(end[0]);
          var g1 = this.lagrangePoint(cs, cm1, 90, +1);
          var g2 = this.lagrangePoint(cm2, ce, 90, +1);

          var startplus = this.addFlowNode("sid-startplus", "bpmn:ExclusiveGateway", g1.x, g1.y, 40, 40);
          var endplus = this.addFlowNode("sid-endplus", "bpmn:ExclusiveGateway", g2.x, g2.y, 40, 40);
          this.addSequenceFlow("sid-startplus-endplus", startplus, endplus, [this.center(startplus), this.center(endplus)]);
          this.addSequenceFlow("sid-start-startplus", start[0], startplus, [this.center(start[0]), this.center(startplus)]);
          this.addSequenceFlow("sid-startplus-a", startplus, a[0], [this.center(startplus), this.center(a[0])]);
          for (i = 0; i < a.length; i++) {
            this.highlight(a[i]);
          }
          this.addSequenceFlow("sid-a-endplus", a[a.length - 1], endplus, [this.center(a[a.length - 1]), this.center(endplus)]);
          this.addSequenceFlow("sid-endplus-end", endplus, end[0], [this.center(endplus), this.center(end[0])]);
          break;

        case "TASKSKIP2":
        case "UNOBSCYCLICINTER":
          //if (message = this.validate(["start.length == 1", "a.length >= 1", "end.length == 1"])) { alert(message); }
          if (start.length > 0)
            this.addSequenceFlow("sid-start-a", start[0], a[0]);
          this.highlight(a[0]);
          for (i = 1; i < a.length; i++) {
            // this.addSequenceFlow("sid-link-" + 1, a[i-1], a[i]);
            this.highlight(a[i]);
          }

          if (end.length > 0)
            this.addSequenceFlow("sid-a-end", a[a.length - 1], end[0]);
          break;

        case "UNMREPETITION2":
          if (message = this.validate(context, ["start.length == 1", "a.length >= 1", "end.length == 1"])) {
            alert(message);
          }
          var cs = this.shapeCenter(start[0]);
          var cm1 = this.shapeCenter(a[0]);
          var cm2 = this.shapeCenter(a[a.length - 1]);
          var ce = this.shapeCenter(end[0]);
          var g1 = this.lagrangePoint(cs, cm1, 90, +1);
          var g2 = this.lagrangePoint(cm2, ce, 90, +1);

          var startplus = this.addFlowNode("sid-startplus", "bpmn:ExclusiveGateway", g1.x, g1.y, 40, 40);
          var endplus = this.addFlowNode("sid-endplus", "bpmn:ExclusiveGateway", g2.x, g2.y, 40, 40);
          this.addSequenceFlow("sid-startplus-endplus", endplus, startplus, [this.center(endplus), this.center(startplus)]);
          this.addSequenceFlow("sid-start-startplus", start[0], startplus, [this.center(start[0]), this.center(startplus)]);
          this.addSequenceFlow("sid-startplus-a", startplus, a[0], [this.center(startplus), this.center(a[0])]);
          for (i = 0; i < a.length; i++) {
            this.highlight(a[i]);
          }
          this.addSequenceFlow("sid-a-endplus", a[a.length - 1], endplus, [this.center(a[a.length - 1]), this.center(endplus)]);
          this.addSequenceFlow("sid-endplus-end", endplus, end[0], [this.center(endplus), this.center(end[0])]);
          break;

        case "UNMREPETITION":
          if (message = this.validate(context, ["start.length == 1", "newTasks.length == 1", "end.length == 1"])) {
            alert(message);
          }
          var s = start[0];
          var e = end[0];
          var cs = this.shapeCenter(s);
          var ce = this.shapeCenter(e);
          var cm = this.lagrangePoint(cs, ce, 0, +1);
          var cr = this.lagrangePoint(this.lagrangePoint(cs, cm, 100, +1), this.lagrangePoint(cm, ce, 100, +1), 0, +1);
          var middle = this.addFlowNode("sid-a", "bpmn:ExclusiveGateway", cm.x, cm.y, 40, 40);
          var repeating = this.addFlowNode("sid-b", "bpmn:Task", cr.x, cr.y, 100, 80);
          editor.getCanvas().updateProperties(repeating, {name:newTasks[0]});
          // repeating.properties.tasktype = "None";
          // repeating["properties"]["name"] = newTasks[0];
          // repeating["properties"]["bgcolor"] = "white";
          this.addSequenceFlow("sid-start-xor", s, middle, [this.center(s), this.center(middle)]);
          this.addSequenceFlow("sid-xor-end", middle, e, [this.center(middle), this.center(e)]);
          var mr = { x: (cr.x + cm.x) / 2 - (cr.y - cm.y) / 2, y: (cr.y + cm.y) / 2 + (cr.x - cm.x) / 2 };
          var rm = { x: (cr.x + cm.x) / 2 + (cr.y - cm.y) / 2, y: (cr.y + cm.y) / 2 - (cr.x - cm.x) / 2 };
          this.addSequenceFlow("sid-xor-repeating", middle, repeating, [this.lagrangePoint(cm, mr, 0, -1), this.lagrangePoint(mr, cr, 0, -1)]);
          this.addSequenceFlow("sid-repeating-xor", repeating, middle, [this.lagrangePoint(cr, rm, 0, -1), this.lagrangePoint(rm, cm, 0, -1)]);
          break;

        case "UNMREPETITIONINTERVAL":
          if (message = this.validate(context, ["start.length == 1", "end.length == 1"])) {
            alert(message);
          }
          var cs = this.shapeCenter(start[0]);
          var ce = this.shapeCenter(end[0]);
          var cm = this.lagrangePoint(cs, ce, 130, +1);

          var newTasksBPMN = {};
          var loc = cm.x
          for (var i = 0; i < newTasks.length; i++) {
            var middle = this.addFlowNode("sid-a", "bpmn:Task", loc, cm.y, 100, 80);
            editor.getCanvas().updateProperties(middle, {name:newTasks[i]});
            // middle.properties.tasktype = "None";
            // middle["properties"]["name"] = newTasks[i];
            // middle["properties"]["bgcolor"] = "white";

            newTasksBPMN[i] = middle;
            loc = loc + 130;
            if (i > 0)
              this.addSequenceFlow("sid-start-ab", newTasksBPMN[i - 1], newTasksBPMN[i], [this.center(newTasksBPMN[i - 1]), this.center(newTasksBPMN[i])]);
          }

          this.addSequenceFlow("sid-start-a", start[0], newTasksBPMN[0], [this.center(start[0]), this.center(newTasksBPMN[0])]);
          this.addSequenceFlow("sid-a-end", newTasksBPMN[newTasks.length - 1], end[0], [this.center(newTasksBPMN[newTasks.length - 1]), this.center(end[0])]);
          break;

        case "TASKSWAP":
          if (message = this.validate(context, ["start.length == 1", "a.length == 1", "b.length == 1", "end.length == 1"])) {
            alert(message);
          }
          var cs = this.shapeCenter(start[0]);
          var ca = this.shapeCenter(a[0]);
          var cb = this.shapeCenter(b[0]);
          var ce = this.shapeCenter(end[0]);
          var sb = this.lagrangePoint(cs, cb, 0, +1);
          var ae = this.lagrangePoint(ca, ce, 0, -1);
          this.highlight(a[0]);
          this.highlight(b[0]);
          this.addSequenceFlow("sid-start-b", start[0], b[0], [sb]);
          this.addSequenceFlow("sid-b-a", b[0], a[0]);
          this.addSequenceFlow("sid-a-end", a[0], end[0], [ae]);
          break;

        default:
          alert("Unsupported difference type \"" + type + "\"");
          return;
      }

      // Add annotation text
      if (annotations) {
        var anchor = this.shapeCenter(annotations[0]);
        var balloon = this.addFlowNode("sid-balloon", "bpmn:TextAnnotation", anchor.x + 100, anchor.y - 100, 100, 50);
        //balloon["properties"]["text"] = annotations[1];
        editor.getCanvas().updateProperties(balloon, {name:annotations[1]});
        this.addAssociation("sid-anchor-balloon", annotations[0], balloon);
      }

      //reloadJSON(json);
      // Reduce the opacity of greyed-out elements
      //shapes = indexJSON(this._canvas.toJSON());
      // for (var i = greys.length; i--;) {
      //   //shapes[greys[i]].getShape().node.setAttributeNS(null, "style", "opacity: 0.25");
      //   editor.getCanvas().greyOut([greys[i]]);
      // }
      editor.getCanvas().colorElements(greys, 'silver');

    //}).bind(this),0);
  }

};
