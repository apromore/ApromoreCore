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

import Log from './logger';
import Utils from './utils';

/**
 * Editor is actually a wrapper around the true editor (e.g. BPMN.io)
 * It provides BPMN editing features while hiding the actual editor implementation provider.
 * The aim is to minimize the impact of the implementation changes with changes minimized to this
 * class only while the editor used in Apromore codebase is unchanged as they only access this Editor class.
 */
export default class Editor {
    constructor(options) {
        this.actualEditor = undefined;
        this.preventFitDelay = options.preventFitDelay;

        if (!(options && options.width && options.height)) {
            Log.fatal("The editor is missing mandatory parameters options.width and options.height.");
            return;
        }

        this.className = "Apromore_Editor";
        this.rootNode = Utils.graft("http://www.w3.org/1999/xhtml", options.parentNode,
            ['div', {id: options.id, width: options.width, height: options.height}
            ]);
        //this.rootNode.addClassName(this.className);
        this.rootNode.classList.add(this.className);
    }

    getScrollNode() {
        "use strict";
        return Ext.get(this.rootNode).parent("div{overflow=auto}", true);
    }

    updateUndoRedo() {
        try {
            var undo = jq('#ap-id-editor-undo-btn');
            var redo = jq('#ap-id-editor-redo-btn');
            if (this.canUndo()) {
                undo.removeClass('disabled');
            } else {
                undo.addClass('disabled');
            }
            if (this.canRedo()) {
                redo.removeClass('disabled');
            } else {
                redo.addClass('disabled');
            }
        } catch(e) {
            console.log('Unexpected error occurred when update button status');
        }
    }

    attachEditor(editor) {
        var me = this;
        this.actualEditor = editor; //This is a BPMNJS object
        this.updateUndoRedo();
        this.addCommandStackChangeListener(function () {
            me.updateUndoRedo();
        });
    }

    getSVGContainer() {
        return $("div.Apromore_Editor div.bjs-container div.djs-container svg")[0];
    }

    getSVGViewport() {
        return $("div.Apromore_Editor div.bjs-container div.djs-container svg g.viewport")[0];
    }

    getSourceNodeId(sequenceFlowId) {
        var foundId;
        var elements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        elements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.id == sequenceFlowId) {
                foundId = element.sourceRef.id;
            }
        });
        return foundId;
    }

    getTargetNodeId(sequenceFlowId) {
        var foundId;
        var flowElements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        flowElements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.id == sequenceFlowId) {
                foundId = element.targetRef.id;
            }
        });
        return foundId;
    }

    getIncomingFlowId(nodeId) {
        var foundId;
        var flowElements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        flowElements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.targetRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    }

    getOutgoingFlowId(nodeId) {
        var foundId;
        var elements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        elements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.sourceRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    }

    toString() {
        return "EditorWrapper " + this.id;
    }

    /**
     * Any clean up needs to be done on the raw XML
     *
     * @todo: There might be a structured way to do this (e.g. XML parser),
     * but this should be the fastest fix for now
     */
    sanitizeXML(xml) {
        var REMOVE_LIST = [
            // Empty label element breaks label editing in bpmn.io
            /<bpmndi:BPMNLabel\s*\/>/ig,
            /<bpmndi:BPMNLabel\s*>\s*<\/bpmndi:BPMNLabel\s*>/ig
        ];

        REMOVE_LIST.forEach(function(regex) {
            xml = xml.replaceAll(regex, '');
        })
        return xml;
    }

    /**
     * Import XML into the editor.
     * This method takes time depending on the complexity of the model
     * @param {String} xml: the BPMN XML
     * @param {Function} callback: callback function to call after the import finishes
     *
     * @todo: Avoid seperate conditional for loganimation and bpmneditor
     */
    async importXML(xml, callback) {
        try {
            xml = this.sanitizeXML(xml);
        } catch(e) {
            console.log('Failed to sanitize');
            // pass
        }

        //EXPERIMENTING WITH THE BELOW TO FIX ARROWS NOT SNAP TO EDGES WHEN OPENING MODELS
        //Some BPMN files are not compatible with bpmn.io
        var editor = this.actualEditor;
        //Converting to promise
        try {
            const result = await editor.importXML(xml);
            var eventBus = editor.get('eventBus');
            var elementRegistry = editor.get('elementRegistry');
            var connections = elementRegistry.filter(function(e) {
                return e.waypoints;
            });
            try {
                var connectionDocking = editor.get('connectionDocking');
                connections.forEach(function(connection) {
                    connection.waypoints = connectionDocking.getCroppedWaypoints(connection);
                });
            } catch (e) {
                console.log('skip connectionDocking error');
                // pass
            }
            eventBus.fire('elements.changed', { elements: connections });
            // @todo: Avoid this conditional
            if (this.preventFitDelay) { // this is for loganimation
                this.zoomFitToModel();
                callback();
            } else { // this is for BPMN editor
                callback();
                var me = this; // delay the fit until the properties panel fully collapsed
                setTimeout(function () {
                    me.zoomFitToModel();
                }, 500);
            }

        } catch (err) {
            window.alert("Failed to import BPMN diagram. Please make sure it's a valid BPMN 2.0 diagram.");
            return;
        }
    }

    async getXML() {
        var bpmnXML;

        try {
          const result = await this.actualEditor.saveXML({ format: true });
          const { xml } = result;
          bpmnXML = xml;
        } catch (err) {
          console.log(err);
        }
        return bpmnXML;
    }

    async getSVG() {
        var bpmnSVG;

        try {
          const result = await this.actualEditor.saveSVG();
          const { svg } = result;
          bpmnSVG = svg;
        } catch (err) {
          console.log(err);
        }
        return bpmnSVG;
    }

    zoomFitToModel() {
        if (this.actualEditor) {
            var canvas = this.actualEditor.get('canvas');
            canvas.viewbox(false); // trigger recalculate the viewbox
            canvas.zoom('fit-viewport', 'auto'); // zoom to fit full viewport
        }
    }

    zoomIn() {
        // this.actualEditor.get('editorActions').trigger('stepZoom', { value: 1 });
        if (this.actualEditor) {
            var canvas = this.actualEditor.get('canvas');
            canvas.zoom(canvas.zoom() * 1.1);
        }
    }

    zoomOut() {
        // this.actualEditor.get('editorActions').trigger('stepZoom', { value: -1 });
        if (this.actualEditor) {
            var canvas = this.actualEditor.get('canvas');
            canvas.zoom(canvas.zoom() * 0.9);
        }
    }

    zoomDefault() {
        // editorActions.trigger('zoom', { value: 1 });
        if (this.actualEditor) {
            var canvas = this.actualEditor.get('canvas');
            canvas.zoom(1);
        }
    }

    createShape(type, x, y, w, h) {
        var modelling = this.actualEditor.get('modeling');
        var parent = this.actualEditor.get('canvas').getRootElement();
        //console.log('parent', parent);
        var shape = modelling.createShape({type:type, width:w, height:h}, {x:x, y:y}, parent);
        return shape.id;
    }

    updateProperties(elementId, properties) {
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        modelling.updateProperties(registry.get(elementId), properties);
    }


    createSequenceFlow (source, target, attrs) {
        var attrs2 = {};
        Object.assign(attrs2,{type:'bpmn:SequenceFlow'});
        if (attrs.waypoints) {
            Object.assign(attrs2,{waypoints: attrs.waypoints});
        }
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        var flow = modelling.connect(registry.get(source), registry.get(target), attrs2);
        //console.log(flow);
        return flow.id;
    }

    createAssociation(source, target, attrs) {
        var attrs2 = {};
        Object.assign(attrs2,{type:'bpmn:Association'});
        if (attrs.waypoints) {
            Object.assign(attrs2,{waypoints: attrs.waypoints});
        }
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        var assoc = Object.assign(assoc, modelling.connect(registry.get(source), registry.get(target), attrs2));
        return assoc.id;
    }

    highlight(elementId) {
        //console.log("Highlighting elementId: " + elementId);
        var self = this;
        var element = self.actualEditor.get('elementRegistry').get(elementId);
        var modelling = self.actualEditor.get('modeling');
        //console.log(element);
        modelling.setColor([element],{stroke:'red'});
    }

    colorElements(elementIds, color) {
        var elements = [];
        var registry = this.actualEditor.get('elementRegistry');
        elementIds.forEach(function(elementId) {
            elements.push(registry.get(elementId));
        });
        var modelling = this.actualEditor.get('modeling');
        modelling.setColor(elements, {stroke:color});
    }

    colorElement(elementId, color) {
        var modelling = this.actualEditor.get('modeling');
        var element = this.actualEditor.get('elementRegistry').get(elementId);
        modelling.setColor([element],{stroke:color});
    }

    fillColor(elementId, color) {
        var modelling = this.actualEditor.get('modeling');
        var element = this.actualEditor.get('elementRegistry').get(elementId);
        modelling.setColor([element],{fill:color});
    }

    greyOut(elementIds) {
        var elementRegistry = this.actualEditor.get('elementRegistry');
        var self = this;
        elementIds.forEach(function(id) {
            console.log('_elements', elementRegistry._elements);
            var gfx = elementRegistry.getGraphics(id);
            var visual = gfx.children[0];
            visual.setAttributeNS(null, "style", "opacity: 0.25");
        });

    }

    normalizeAll() {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        modelling.setColor(registry.getAll(), {stroke:'black'});
    }

    removeShapes(shapeIds) {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        console.log(shapeIds);
        var shapes = [];
        shapeIds.forEach(function(shapeId) {
            shapes.push(registry.get(shapeId));
        });
        modelling.removeElements(shapes);
    }

    getAllElementIds() {
        var ids = [];
        var elementRegistry = this.actualEditor.get('elementRegistry');
        elementRegistry.getAll().forEach(function(element) {
            ids.push(element.id);
        });
        return ids;
    }

    shapeCenter(shapeId) {
        var position = {};
        var registry = this.actualEditor.get('elementRegistry');
        var shape = registry.get(shapeId);
        //console.log('Shape of ' + shapeId);
        //console.log(shape);
        //console.log(shape.x);
        position.x = (shape.x + shape.width/2);
        position.y = (shape.y + shape.height/2);
        return position;
    }

    clear() {
        this.actualEditor.clear();
    }

    registerActionHandler(handlerName, handler) {
        var commandStack = this.actualEditor.get('commandStack');
        commandStack.registerHandler(handlerName, handler);
    }

    executeActionHandler(handlerName, context) {
        var commandStack = this.actualEditor.get('commandStack');
        commandStack.execute(handlerName, context);
    }

    getCenter(shapeId) {
        var shape = this.actualEditor.get('elementRegistry').get(shapeId);
        return {
            x: shape.x + (shape.width || 0) / 2,
            y: shape.y + (shape.height || 0) / 2
        }
    }

    // Center viewbox to an element
    // From https://forum.bpmn.io/t/centering-zooming-view-to-a-specific-element/1536/6
    centerElement(elementId) {
        // assuming we center on a shape.
        // for connections we must compute the bounding box
        // based on the connection's waypoints
        var bbox = elementRegistry.get(elementId);

        var currentViewbox = canvas.viewbox();

        var elementMid = {
          x: bbox.x + bbox.width / 2,
          y: bbox.y + bbox.height / 2
        };

        canvas.viewbox({
          x: elementMid.x - currentViewbox.width / 2,
          y: elementMid.y - currentViewbox.height / 2,
          width: currentViewbox.width,
          height: currentViewbox.height
        });
    }

  _getActionStack() {
    return this.actualEditor.get('commandStack')._stack;
  }

  _getCurrentStackIndex() {
    return this.actualEditor.get('commandStack')._stackIdx;
  }

  // Get all base action indexes backward from the current command stack index
  // The first element in the result is the earliest base action and so on
  _getBaseActions() {
    var actions = this._getActionStack();
    var stackIndex = this._getCurrentStackIndex();
    var baseActionIndexes = [];
    for (var i=0; i<=stackIndex; i++) {
      if (i==0 || (actions[i].id != actions[i-1].id)) {
        baseActionIndexes.push(i);
      }
    }
    return baseActionIndexes;
  }

  undo() {
    this.actualEditor.get('commandStack').undo();
  }

  // Undo to the point before an action (actionName is the input)
  // Nothing happens if the action is not found
  // The number of undo times is the number of base actions from the current stack index
  undoSeriesUntil(actionName) {
    var actions = this._getActionStack();
    var baseActions = this._getBaseActions();
    var baseActionNum = 0;
    for (var i=baseActions.length-1; i>=0; i--) {
      if (actions[baseActions[i]].command == actionName) {
        baseActionNum = baseActions.length - i;
        break;
      }
    }

    console.log('baseActionNum', baseActionNum);

    while (baseActionNum > 0) {
      this.undo();
      baseActionNum--;
    }
  }

  canUndo() {
    if (!this.actualEditor) {
      return false;
    }
    else {
      return this.actualEditor.get('commandStack').canUndo();
    }
  }

  redo() {
    this.actualEditor.get('commandStack').redo();
  }

  canRedo() {
    if (!this.actualEditor) {
      return false;
    }
    else {
      return this.actualEditor.get('commandStack').canRedo();
    }
  }

  getLastBaseAction() {
    var actions = this._getActionStack();
    var baseActions = this._getBaseActions();
    if (baseActions.length > 0) {
      return actions[baseActions[baseActions.length-1]].command;
    }
    else {
      return '';
    }
  }

  // Get the next latest base action in the command stack
  // that is not in the excluding list
  getNextBaseActionExcluding(excludingActions) {
    var actions = this._getActionStack();
    var baseActionIndexes = this._getBaseActions();
    if (baseActionIndexes.length >= 2) {
      for (var i = baseActionIndexes.length-2; i>=0; i--) {
        if (excludingActions.indexOf(actions[baseActionIndexes[i]].command) < 0) {
          return actions[baseActionIndexes[i]].command;
        }
      }
    }
    return '';
  }

  addCommandStackChangeListener(callback) {
    this.actualEditor.on('commandStack.changed', callback);
  }

  addEventBusListener(eventCode, callback) {
    this.actualEditor.get('eventBus').on(eventCode, callback);
  }

};
