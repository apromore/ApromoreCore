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
if (!Apromore) {
    var Apromore = {};
}

/**
 * Editor is actually a wrapper around the true editor (e.g. BPMN.io)
 * It provides BPMN editing features while hiding the actual editor implementation provider.
 * The aim is to minimize the impact of the implementation changes with changes minimized to this
 * class only while the editor used in Apromore codebase is unchanged as they only access this Editor class.
 */
Apromore.Editor = {
    construct: function(options) {
        this.actualEditor = undefined;

        if (!(options && options.width && options.height)) {
            Apromore.Log.fatal("The editor is missing mandatory parameters options.width and options.height.");
            return;
        }

        this.className = "Apromore_Editor";
        this.rootNode = Apromore.Utils.graft("http://www.w3.org/1999/xhtml", options.parentNode,
            ['div', {id: options.id, width: options.width, height: options.height}
            ]);
        //this.rootNode.addClassName(this.className);
        this.rootNode.classList.add(this.className);
    },

    getScrollNode: function () {
        "use strict";
        return Ext.get(this.rootNode).parent("div{overflow=auto}", true);
    },

    attachEditor: function (editor) {
        this.actualEditor = editor;
    },

    getSVGContainer: function() {
        return $("div.Apromore_Editor div.bjs-container div.djs-container svg")[0];
    },

    getSVGViewport: function() {
        return $("div.Apromore_Editor div.bjs-container div.djs-container svg g.viewport")[0];
    },

    getSourceNodeId: function (sequenceFlowId) {
        var foundId;
        var elements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        elements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.id == sequenceFlowId) {
                foundId = element.sourceRef.id;
            }
        });
        return foundId;
    },

    getTargetNodeId: function (sequenceFlowId) {
        var foundId;
        var flowElements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        flowElements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.id == sequenceFlowId) {
                foundId = element.targetRef.id;
            }
        });
        return foundId;
    },

    getIncomingFlowId: function (nodeId) {
        var foundId;
        var flowElements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        flowElements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.targetRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    },

    getOutgoingFlowId: function (nodeId) {
        var foundId;
        var elements = this.actualEditor.getDefinitions().rootElements[0].flowElements;
        elements.forEach(function(element) {
            if (!foundId && element.$type == "bpmn:SequenceFlow" && element.sourceRef.id == nodeId) {
                foundId = element.id;
            }
        });
        return foundId;
    },

    toString: function () {
        return "EditorWrapper " + this.id;
    },

    /**
     * Import XML into the editor.
     * This method takes time depending on the complexity of the model
     * @param {String} xml: the BPMN XML
     * @param {Function} callback: callback function to call after the import finishes
     */
    importXML: function(xml, callback) {
      // this.editor.importXML(xml, function(err) {
      //   if (err) {
      //     return console.error('could not import BPMN 2.0 diagram', err);
      //   }
      //   this.zoomFitToModel();
      // }.bind(this));

      //EXPERIMENTING WITH THE BELOW TO FIX ARROWS NOT SNAP TO EDGES WHEN OPENING MODELS
      //Some BPMN files are not compatible with bpmn.io
      var editor = this.actualEditor;
      this.actualEditor.importXML(xml, function(err) {
        if (err) {
          return console.error('could not import BPMN 2.0 diagram', err);
        }

        var eventBus = editor.get('eventBus');
        var connectionDocking = editor.get('connectionDocking');
        var elementRegistry = editor.get('elementRegistry');
        var connections = elementRegistry.filter(function(e) {
          return e.waypoints;
        });
        connections.forEach(function(connection) {
          connection.waypoints = connectionDocking.getCroppedWaypoints(connection);
        });
        eventBus.fire('elements.changed', { elements: connections });
        callback();
        var me = this; // delay the fit until the properties panel fully collapsed
        setTimeout(function () {
            me.zoomFitToModel();
        }, 500)
      }.bind(this));
    },

    getXML: function() {
        var bpmnXML;
        this.actualEditor.saveXML({ format: true }, function(err, xml) {
            bpmnXML = xml;
        });
        return bpmnXML;
    },

    getSVG: function() {
        var bpmnSVG;
        this.actualEditor.saveSVG(function(err, svg) {
            bpmnSVG = svg;
        });
        return bpmnSVG;
    },

    zoomFitToModel: function() {
        if (this.actualEditor) {
            var canvas = this.actualEditor.get('canvas');
            canvas.viewbox(false); // trigger recalculate the viewbox
            // zoom to fit full viewport
            canvas.zoom('fit-viewport', 'auto');
        }
    },

    zoomIn: function() {
        this.actualEditor.get('editorActions').trigger('stepZoom', { value: 1 });
    },


    zoomOut: function() {
        this.actualEditor.get('editorActions').trigger('stepZoom', { value: -1 });
    },

    zoomDefault: function() {
        editorActions.trigger('zoom', { value: 1 });
    },

    createShape: function(type, x, y, w, h) {
        var modelling = this.actualEditor.get('modeling');
        var parent = this.actualEditor.get('canvas').getRootElement();
        //console.log('parent', parent);
        var shape = modelling.createShape({type:type, width:w, height:h}, {x:x, y:y}, parent);
        return shape.id;
    },

    updateProperties: function(elementId, properties) {
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        modelling.updateProperties(registry.get(elementId), properties);
    },


    createSequenceFlow: function (source, target, attrs) {
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
    },

    createAssociation: function (source, target, attrs) {
        var attrs2 = {};
        Object.assign(attrs2,{type:'bpmn:Association'});
        if (attrs.waypoints) {
            Object.assign(attrs2,{waypoints: attrs.waypoints});
        }
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        var assoc = Object.assign(assoc, modelling.connect(registry.get(source), registry.get(target), attrs2));
        return assoc.id;
    },

    highlight: function (elementId) {
        //console.log("Highlighting elementId: " + elementId);
        var self = this;
        var element = self.actualEditor.get('elementRegistry').get(elementId);
        var modelling = self.actualEditor.get('modeling');
        //console.log(element);
        modelling.setColor([element],{stroke:'red'});
    },

    colorElements: function (elementIds, color) {
        var elements = [];
        var registry = this.actualEditor.get('elementRegistry');
        elementIds.forEach(function(elementId) {
            elements.push(registry.get(elementId));
        });
        var modelling = this.actualEditor.get('modeling');
        modelling.setColor(elements, {stroke:color});
    },

    colorElement: function (elementId, color) {
        var modelling = this.actualEditor.get('modeling');
        var element = this.actualEditor.get('elementRegistry').get(elementId);
        modelling.setColor([element],{stroke:color});
    },

    fillColor: function (elementId, color) {
        var modelling = this.actualEditor.get('modeling');
        var element = this.actualEditor.get('elementRegistry').get(elementId);
        modelling.setColor([element],{fill:color});
    },

    greyOut: function(elementIds) {
        var elementRegistry = this.actualEditor.get('elementRegistry');
        var self = this;
        elementIds.forEach(function(id) {
            console.log('_elements', elementRegistry._elements);
            var gfx = elementRegistry.getGraphics(id);
            var visual = gfx.children[0];
            visual.setAttributeNS(null, "style", "opacity: 0.25");
        });

    },

    normalizeAll: function() {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        modelling.setColor(registry.getAll(), {stroke:'black'});
    },

    removeShapes: function(shapeIds) {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        console.log(shapeIds);
        var shapes = [];
        shapeIds.forEach(function(shapeId) {
            shapes.push(registry.get(shapeId));
        });
        modelling.removeElements(shapes);
    },

    getAllElementIds: function() {
        var ids = [];
        var elementRegistry = this.actualEditor.get('elementRegistry');
        elementRegistry.getAll().forEach(function(element) {
            ids.push(element.id);
        });
        return ids;
    },

    shapeCenter: function (shapeId) {
        var position = {};
        var registry = this.actualEditor.get('elementRegistry');
        var shape = registry.get(shapeId);
        //console.log('Shape of ' + shapeId);
        //console.log(shape);
        //console.log(shape.x);
        position.x = (shape.x + shape.width/2);
        position.y = (shape.y + shape.height/2);
        return position;
    },

    clear: function() {
        this.actualEditor.clear();
    },

    registerActionHandler: function(handlerName, handler) {
        var commandStack = this.actualEditor.get('commandStack');
        commandStack.registerHandler(handlerName, handler);
    },

    executeActionHandler: function(handlerName, context) {
        var commandStack = this.actualEditor.get('commandStack');
        commandStack.execute(handlerName, context);
    },

    getCenter: function (shapeId) {
        var shape = this.actualEditor.get('elementRegistry').get(shapeId);
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

  _getActionStack: function() {
    return this.actualEditor.get('commandStack')._stack;
  },

  _getCurrentStackIndex: function() {
    return this.actualEditor.get('commandStack')._stackIdx;
  },

  // Get all base action indexes backward from the current command stack index
  // The first element in the result is the earliest base action and so on
  _getBaseActions: function() {
    var actions = this._getActionStack();
    var stackIndex = this._getCurrentStackIndex();
    var baseActionIndexes = [];
    for (var i=0; i<=stackIndex; i++) {
      if (i==0 || (actions[i].id != actions[i-1].id)) {
        baseActionIndexes.push(i);
      }
    }
    return baseActionIndexes;
  },

  undo: function() {
    this.actualEditor.get('commandStack').undo();
  },

  // Undo to the point before an action (actionName is the input)
  // Nothing happens if the action is not found
  // The number of undo times is the number of base actions from the current stack index
  undoSeriesUntil: function(actionName) {
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
  },

  canUndo: function() {
    if (!this.actualEditor) {
      return false;
    }
    else {
      return this.actualEditor.get('commandStack').canUndo();
    }
  },

  redo: function() {
    this.actualEditor.get('commandStack').redo();
  },

  canRedo: function() {
    if (!this.actualEditor) {
      return false;
    }
    else {
      return this.actualEditor.get('commandStack').canRedo();
    }
  },

  getLastBaseAction: function() {
    var actions = this._getActionStack();
    var baseActions = this._getBaseActions();
    if (baseActions.length > 0) {
      return actions[baseActions[baseActions.length-1]].command;
    }
    else {
      return '';
    }
  },

  // Get the next latest base action in the command stack
  // that is not in the excluding list
  getNextBaseActionExcluding: function(excludingActions) {
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
  },

  addCommandStackChangeListener: function(callback) {
    this.actualEditor.on('commandStack.changed', callback);
  },

  addEventBusListener: function(eventCode, callback) {
    this.actualEditor.get('eventBus').on(eventCode, callback);
  }

};

Apromore.Editor = Clazz.extend(Apromore.Editor);

