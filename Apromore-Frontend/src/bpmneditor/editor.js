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
        // this.preventFitDelay = options.preventFitDelay;

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
    }

    getSVGContainer() {
        //return $("div.Apromore_Editor div.bjs-container div.djs-container svg")[0];
        return $('#' + this.rootNode.id + " div.bjs-container div.djs-container svg")[0];
    }

    getSVGViewport() {
        //return $("div.Apromore_Editor div.bjs-container div.djs-container svg g.viewport")[0];
        return $('#' + this.rootNode.id + " div.bjs-container div.djs-container svg g.viewport")[0];
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
     */
    async importXML(xml) {
        if (!this.actualEditor) return false;
        if (!(typeof xml === 'string')) throw new Error("Invalid XML input");
        xml = sanitizeXML(xml);

        await this.actualEditor.importXML(xml);

        //EXPERIMENTING WITH THE BELOW TO FIX ARROWS NOT SNAP TO EDGES WHEN OPENING MODELS
        //Some BPMN files are not compatible with bpmn.io
        var editor = this.actualEditor;
        var eventBus = editor.get('eventBus');
        var elementRegistry = editor.get('elementRegistry');
        var connections = elementRegistry.filter(function(e) {return e.waypoints;});
        var connectionDocking = editor.get('connectionDocking');
        connections.forEach(function(connection) {
            connection.waypoints = connectionDocking.getCroppedWaypoints(connection);
        });
        eventBus.fire('elements.changed', { elements: connections });

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
        let canvas = this.actualEditor.get('canvas');
        canvas.viewbox(false); // trigger recalculate the viewbox
        canvas.zoom('fit-viewport', 'auto'); // zoom to fit full viewport
        if (!this.originViewbox) this.originViewbox = canvas.viewbox();
        return true;
    }

    zoomFitOriginal() {
        if (!this.actualEditor || !this.originViewbox) return false;
        this.actualEditor.get('canvas').viewbox(this.originViewbox);
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


    ////////////////////// Modelling Elements Editing Methods /////////////////////////////////

    createShape(type, x, y, w, h) {
        var modelling = this.actualEditor.get('modeling');
        var parent = this.actualEditor.get('canvas').getRootElement();
        var shape = modelling.createShape({type:type, width:w, height:h}, {x:x, y:y}, parent);
        return shape.id;
    }

    createSequenceFlow(source, target, attrs) {
        var attrs2 = {};
        Object.assign(attrs2,{type:'bpmn:SequenceFlow'});
        if (attrs.waypoints) {
            Object.assign(attrs2,{waypoints: attrs.waypoints});
        }
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        var flow = modelling.connect(registry.get(source), registry.get(target), attrs2);
        return flow.id;
    }

    removeShapes(shapeIds) {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        var shapes = [];
        shapeIds.forEach(function(shapeId) {
            shapes.push(registry.get(shapeId));
        });
        modelling.removeElements(shapes);
    }

    updateProperties(elementId, properties) {
        var modelling = this.actualEditor.get('modeling');
        var registry = this.actualEditor.get('elementRegistry');
        modelling.updateProperties(registry.get(elementId), properties);
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

    normalizeAll() {
        var registry = this.actualEditor.get('elementRegistry');
        var modelling = this.actualEditor.get('modeling');
        modelling.setColor(registry.getAll(), {stroke:'black'});
    }

    getCenter(shapeId) {
        var shape = this.actualEditor.get('elementRegistry').get(shapeId);
        return {
            x: shape.x + (shape.width || 0) / 2,
            y: shape.y + (shape.height || 0) / 2
        }
    }
};
