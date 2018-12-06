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
            // access modeler components
            var canvas = this._editor.get('canvas');
            // zoom to fit full viewport
            canvas.zoom('fit-viewport');
            var viewbox = canvas.viewbox();
            canvas.viewbox({
                x: viewbox.x - 200,
                y: viewbox.y,
                width: viewbox.outer.width*1.5,
                height: viewbox.outer.height*1.5
            });
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

    },
};

ORYX.Canvas = Clazz.extend(ORYX.Canvas);

