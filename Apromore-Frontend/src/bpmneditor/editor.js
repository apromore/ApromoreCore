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
import {reject} from "ramda";

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

    attachEditor(editor) {
        let me = this;
        this.actualEditor = editor; //This is a BPMNJS object
        // updateUndoRedo();
        // this.addCommandStackChangeListener(function () {
        //     updateUndoRedo();
        // });
        // function updateUndoRedo() {
        //     try {
        //         var undo = $('#ap-id-editor-undo-btn');
        //         var redo = $('#ap-id-editor-redo-btn');
        //         if (!undo || !redo) return;
        //         if (me.canUndo()) {
        //             undo.removeClass('disabled');
        //         } else {
        //             undo.addClass('disabled');
        //         }
        //         if (me.canRedo()) {
        //             redo.removeClass('disabled');
        //         } else {
        //             redo.addClass('disabled');
        //         }
        //     } catch(e) {
        //         console.log('Unexpected error occurred when update button status.', e.message);
        //     }
        // }
    }

    getSVGContainer() {
        return $("div.Apromore_Editor div.bjs-container div.djs-container svg")[0];
    }

    getSVGViewport() {
        return $("div.Apromore_Editor div.bjs-container div.djs-container svg g.viewport")[0];
    }

    getIncomingFlowId(nodeId) {
        if (!this.actualEditor || !this.actualEditor.getDefinitions()) return false;
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
        if (!this.actualEditor || !this.actualEditor.getDefinitions()) return false;
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
     * Import XML into the editor.
     * This method takes time depending on the complexity of the model
     * @param {String} xml: the BPMN XML
     * @param {Function} callback: callback function to call after the import finishes
     *
     * @todo: Avoid seperate conditional for loganimation and bpmneditor
     */
    async importXML(xml, callback) {
        if (!this.actualEditor) return false;
        if (!(typeof xml === 'string')) throw new Error("Invalid XML input");
        xml = sanitizeXML(xml);

        //EXPERIMENTING WITH THE BELOW TO FIX ARROWS NOT SNAP TO EDGES WHEN OPENING MODELS
        //Some BPMN files are not compatible with bpmn.io
        var editor = this.actualEditor;

        const result = await editor.importXML(xml).catch(err => {throw err;});
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
            Log.error('Skip connectionDocking error');
            throw e;
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


        /**
         * Any clean up needs to be done on the raw XML
         *
         * @todo: There might be a structured way to do this (e.g. XML parser),
         * but this should be the fastest fix for now
         */
        function sanitizeXML(xml) {
            if (!(typeof xml === 'string')) return;
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
    }

    async getXML() {
        if (!this.actualEditor) return '';
        const result = await this.actualEditor.saveXML({ format: true }).catch(err => {throw err;});
        const {xml} = result;
        return xml;
    }

    async getSVG() {
        if (!this.actualEditor) return '';
        const result = await this.actualEditor.saveSVG({ format: true }).catch(err => {throw err;});
        const {svg} = result;
        return svg;
    }

    zoomFitToModel() {
        if (!this.actualEditor) return false;
        var canvas = this.actualEditor.get('canvas');
        canvas.viewbox(false); // trigger recalculate the viewbox
        canvas.zoom('fit-viewport', 'auto'); // zoom to fit full viewport
        return true;
    }

    zoomIn() {
        if (!this.actualEditor) return false;
        var canvas = this.actualEditor.get('canvas');
        canvas.zoom(canvas.zoom() * 1.1);
        return true;
    }

    zoomOut() {
        if (!this.actualEditor) return false;
        var canvas = this.actualEditor.get('canvas');
        canvas.zoom(canvas.zoom() * 0.9);
        return true;
    }

    zoomDefault() {
        if (!this.actualEditor) return false;
        var canvas = this.actualEditor.get('canvas');
        canvas.zoom(1);
        return true;
    }

    undo() {
        if (!this.actualEditor) return false;
        this.actualEditor.get('commandStack').undo();
        return true;
    }

    canUndo() {
        if (!this.actualEditor) {
            return false;
        } else {
            return this.actualEditor.get('commandStack').canUndo();
        }
    }

    redo() {
        if (!this.actualEditor) return false;
        this.actualEditor.get('commandStack').redo();
        return true;
    }

    canRedo() {
        if (!this.actualEditor) {
            return false;
        } else {
            return this.actualEditor.get('commandStack').canRedo();
        }
    }

    addCommandStackChangeListener(callback) {
        if (!this.actualEditor) return false;
        this.actualEditor.on('commandStack.changed', callback);
        return true;
    }

    addEventBusListener(eventCode, callback) {
        if (!this.actualEditor) return false;
        this.actualEditor.get('eventBus').on(eventCode, callback);
        return true;
    }

};
