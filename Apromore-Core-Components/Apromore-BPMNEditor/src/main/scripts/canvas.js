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
 * Init namespaces
 */
if (!ORYX) {
    var ORYX = {};
}

ORYX.Canvas = {
    /** @lends ORYX.Core.Canvas.prototype */

    /**
     * Constructor
     */
    construct: function(options) {
        this.zoomLevel = 1;
        this._editor = undefined;

        if (!(options && options.width && options.height)) {
            ORYX.Log.fatal("Canvas is missing mandatory parameters options.width and options.height.");
            return;
        }

        this.className = "ORYX_Editor";
        this.resourceId = options.id;
        this.nodes = [];
        this.edges = [];

        this.rootNode = ORYX.Utils.graft("http://www.w3.org/1999/xhtml", options.parentNode,
            ['div', {id: options.id, width: options.width, height: options.height}
            ]);
        this.rootNode.addClassName(this.className);

    },

    getScrollNode: function () {
        "use strict";
        return Ext.get(this.rootNode).parent("div{overflow=auto}", true);
    },


    attachEditor: function (editor) {
        this._editor = editor;
    },

    getSVGContainer: function() {
        return $$("div.ORYX_Editor div.bjs-container div.djs-container svg")[0];
    },

    getSVGViewport: function() {
        return $$("div.ORYX_Editor div.bjs-container div.djs-container svg g.viewport")[0];
    },

    getSourceNodeId: function (sequenceFlowId) {
        var foundId;
        var elements = this._editor.getDefinitions().rootElements[0].flowElements;
        elements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.id == sequenceFlowId) {
                foundId = element.sourceRef.id;
            }
        });
        return foundId;
    },

    getTargetNodeId: function (sequenceFlowId) {
        var foundId;
        var flowElements = this._editor.getDefinitions().rootElements[0].flowElements;
        flowElements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.id == sequenceFlowId) {
                foundId = element.targetRef.id;
            }
        });
        return foundId;
    },

    getIncomingFlowId: function (nodeId) {
        var foundId;
        var flowElements = this._editor.getDefinitions().rootElements[0].flowElements;
        flowElements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.targetRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    },

    getOutgoingFlowId: function (nodeId) {
        var foundId;
        var elements = this._editor.getDefinitions().rootElements[0].flowElements;
        elements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.sourceRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    },

    getLanguage: function () {
        //return (this.language || (this.languages || []).first()) || "de_de";
        return ORYX.I18N.Language;
    },

    toString: function () {
        return "Canvas " + this.id;
    },

    importXML: function(xml) {
        this._editor.importXML(xml, function(err) {
            if (err) {
                return console.error('could not import BPMN 2.0 diagram', err);
            }
            this.zoomFitToModel();
        }.bind(this));
    },

    getXML: function() {
        var bpmnXML;
        this._editor.saveXML({ format: true }, function(err, xml) {
            bpmnXML = xml;
        });
        return bpmnXML;
    },

    getSVG: function() {
        var bpmnSVG;
        this._editor.saveSVG(function(err, svg) {
            bpmnSVG = svg;
        });
        return bpmnSVG;
    },

    zoomFitToModel: function() {
        if (this._editor) {
            var canvas = this._editor.get('canvas');
            // zoom to fit full viewport
            canvas.zoom('fit-viewport');
            var viewbox = canvas.viewbox();
            canvas.viewbox({
                x: viewbox.x - 200,
                y: viewbox.y,
                width: viewbox.outer.width * 1.5,
                height: viewbox.outer.height * 1.5
            });
        }
    },

    zoomIn: function() {
        this._editor.get('editorActions').trigger('stepZoom', { value: 1 });
    },


    zoomOut: function() {
        this._editor.get('editorActions').trigger('stepZoom', { value: -1 });
    },

    zoomDefault: function() {
        editorActions.trigger('zoom', { value: 1 });
    },

    createShape: function(type, x, y, w, h) {
        var modelling = this._editor.get('modeling');
        var parent = this._editor.get('canvas').getRootElement();
        //console.log('parent', parent);
        var shape = modelling.createShape({type:type, width:w, height:h}, {x:x, y:y}, parent);
        return shape.id;
    },

    updateProperties: function(elementId, properties) {
        var modelling = this._editor.get('modeling');
        var registry = this._editor.get('elementRegistry');
        modelling.updateProperties(registry.get(elementId), properties);
    },


    createSequenceFlow: function (source, target, attrs) {
        var attrs2 = {};
        Object.assign(attrs2,{type:'bpmn:SequenceFlow'});
        if (attrs.waypoints) {
            Object.assign(attrs2,{waypoints: attrs.waypoints});
        }
        var modelling = this._editor.get('modeling');
        var registry = this._editor.get('elementRegistry');
        var flow = modelling.connect(registry.get(source), registry.get(target), attrs2);
        //console.log(flow);
        return flow.id;
    },

    createAssociation: function (source, target, attrs) {
        var attrs2 = {};
        Object.assign(attrs2,{type:'bpmn:Association'});
        if (attrs.waypoints) {
            Object.assign(attrs2,{waypoints: attrs.waypoints});
        }
        var modelling = this._editor.get('modeling');
        var registry = this._editor.get('elementRegistry');
        var assoc = Object.assign(assoc, modelling.connect(registry.get(source), registry.get(target), attrs2));
        return assoc.id;
    },

    highlight: function (elementId) {
        //console.log("Highlighting elementId: " + elementId);
        var self = this;
        var element = self._editor.get('elementRegistry').get(elementId);
        var modelling = self._editor.get('modeling');
        //console.log(element);
        modelling.setColor([element],{stroke:'red'});
    },

    colorElements: function (elementIds, color) {
        var elements = [];
        var registry = this._editor.get('elementRegistry');
        elementIds.forEach(function(elementId) {
            elements.push(registry.get(elementId));
        });
        var modelling = this._editor.get('modeling');
        modelling.setColor(elements, {stroke:color});
    },

    colorElement: function (elementId, color) {
        var modelling = this._editor.get('modeling');
        var element = this._editor.get('elementRegistry').get(elementId);
        modelling.setColor([element],{stroke:color});
    },

    fillColor: function (elementId, color) {
        var modelling = this._editor.get('modeling');
        var element = this._editor.get('elementRegistry').get(elementId);
        modelling.setColor([element],{fill:color});
    },

    greyOut: function(elementIds) {
        var elementRegistry = this._editor.get('elementRegistry');
        var self = this;
        elementIds.forEach(function(id) {
            console.log('_elements', elementRegistry._elements);
            var gfx = elementRegistry.getGraphics(id);
            var visual = gfx.children[0];
            visual.setAttributeNS(null, "style", "opacity: 0.25");
        });

    },

    normalizeAll: function() {
        var registry = this._editor.get('elementRegistry');
        var modelling = this._editor.get('modeling');
        modelling.setColor(registry.getAll(), {stroke:'black'});
    },

    removeShapes: function(shapeIds) {
        var registry = this._editor.get('elementRegistry');
        var modelling = this._editor.get('modeling');
        console.log(shapeIds);
        var shapes = [];
        shapeIds.forEach(function(shapeId) {
            shapes.push(registry.get(shapeId));
        });
        modelling.removeElements(shapes);
    },

    getAllElementIds: function() {
        var ids = [];
        var elementRegistry = this._editor.get('elementRegistry');
        elementRegistry.getAll().forEach(function(element) {
            ids.push(element.id);
        });
        return ids;
    },

    shapeCenter: function (shapeId) {
        var position = {};
        var registry = this._editor.get('elementRegistry');
        var shape = registry.get(shapeId);
        //console.log('Shape of ' + shapeId);
        //console.log(shape);
        //console.log(shape.x);
        position.x = (shape.x + shape.width/2);
        position.y = (shape.y + shape.height/2);
        return position;
    },

    clear: function() {
        this._editor.clear();
    },

    registerActionHandler: function(handlerName, handler) {
        var commandStack = this._editor.get('commandStack');
        commandStack.registerHandler(handlerName, handler);
    },

    executeActionHandler: function(handlerName, context) {
        var commandStack = this._editor.get('commandStack');
        commandStack.execute(handlerName, context);
    },

    getCenter: function (shapeId) {
        var shape = this._editor.get('elementRegistry').get(shapeId);
        return {
            x: shape.x + (shape.width || 0) / 2,
            y: shape.y + (shape.height || 0) / 2
        }
    },

    // Center viewbox to an element
    // From https://forum.bpmn.io/t/centering-zooming-view-to-a-specific-element/1536/6
    centerElement: function(elementId) {
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
    },

    undo: function() {
        this._editor.get('commandStack').undo();
    },

    canUndo: function() {
        if (!this._editor) {
            return false;
        }
        else {
            return this._editor.get('commandStack').canUndo();
        }
    },

    redo: function() {
        this._editor.get('commandStack').redo();
    },

    canRedo: function() {
        if (!this._editor) {
            return false;
        }
        else {
            return this._editor.get('commandStack').canRedo();
        }
    },

    // NOTE: this is a hack on bpmn.io by calling to private methods/variables
    checkLatestAction: function(checkActionName) {
        var actions = this._editor.get('commandStack')._stack;
        var stackIndex = this._editor.get('commandStack')._stackIdx;
        var latestID = (stackIndex >= 0) ? actions[stackIndex].id: -1;
        for (var i=stackIndex; i>=0; i--) {
          if (actions[i].id == latestID && actions[i].command == checkActionName) {
            return true;
          }
        }
        return false;
    },

    addCommandStackChangeListener: function(callback) {
      this._editor.on('commandStack.changed', callback);
    }

};

ORYX.Canvas = Clazz.extend(ORYX.Canvas);

