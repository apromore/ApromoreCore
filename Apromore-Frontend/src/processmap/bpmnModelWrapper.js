import bpmnEditor from "../bpmneditor/index";
import * as utils from "../loganimation/utils";
import {AnimationEvent, AnimationEventType} from "../loganimation/animationEvents";

/**
 * BPMNModelWrapper encapsulates the BPMN.io model editor and provides
 * interfaces into the editor needed by the token animation. The token animation
 * shows animation on the editor.
 *
 * This object is responsible for:
 *      - Access to the editor and the model
 *      - Access to modelling elements
 *      - Calculate points on the model
 *      - Handle events occuring on the underlying editor.
 *
 * @author Bruce Nguyen
 */
export default class BPMNModelWrapper {
    /**
     */
    constructor() {
        this._listeners = [];
    }

    /**
     * Note that loadEditor takes time for the XML to be loaded and BPMN data model to be created.
     * Caller of this method must take this loading time into account, otherwise the other methods
     * will fail because the BPMN data model has not been created in the memory yet.
     * @param {String} editorContainerId: id of the div element hosting the editor
     * @param {String} xmlString: XML content of the BPMN map/model
     */
    async loadProcessModel(editorContainerId, bpmnXML, callBack) {
        if (!Apromore.BPMNEditor) {
            Apromore.BPMNEditor = bpmnEditor;
        }
        this._editorApp = new Apromore.BPMNEditor.EditorApp({
            id: editorContainerId,
            fullscreen: true,
            disabledButtons: [
                window.Apromore.I18N.Save.save,
                window.Apromore.I18N.Save.saveAs,
                window.Apromore.I18N.File.svg,
                window.Apromore.I18N.File.bpmn,
                window.Apromore.I18N.File.pdf,
                window.Apromore.I18N.Undo.undo,
                window.Apromore.I18N.Undo.redo,
                window.Apromore.I18N.Share.share,
                window.Apromore.I18N.SimulationPanel.simulateModel
            ]
        });
        await this._editorApp.init({
            xml: bpmnXML
        });
        this._editor = this._editorApp.getEditor();
    }

    /**
     * The model is a large collection of elements. It needs to be prepared
     * with element data and events handling.
     * @param {Object} elementIndexIDMap: map from element index to element id
     */
    initialize(elementIndexIDMap) {
        if (!this._editorApp) {
            console.error('Stop. The editor has not loaded data yet');
            return;
        }

        this._svgMain = this._editor.getSVGContainer();
        this._svgViewport = this._editor.getSVGViewport();

        let me = this;
        this._editor.addEventBusListener("canvas.viewbox.changing", function() {
            let modelBox = me.getBoundingClientRect();
            let modelMatrix = me.getTransformMatrix();
            me._notifyAll(new AnimationEvent(AnimationEventType.MODEL_CANVAS_MOVING,
                {viewbox: modelBox, transformMatrix: modelMatrix}));
        });
        this._editor.addEventBusListener("canvas.viewbox.changed", function() {
            let modelBox = me.getBoundingClientRect();
            let modelMatrix = me.getTransformMatrix();
            me._notifyAll(new AnimationEvent(AnimationEventType.MODEL_CANVAS_MOVED,
                {viewbox: modelBox, transformMatrix: modelMatrix}));
        });

        this._createIndexToElementCache(elementIndexIDMap);
    }

    /**
     * @param {Array} elementMapping:
     *      - 1st element: mapping from element index to element id (one index for sequence flow, one index for one node)
     *      - 2nd element: mapping from skip element index to element id (one skip index for one node)
     * @return {Object} mapping from element index to SVG Path Element having the element id
     * @private
     */
    _createIndexToElementCache(elementMapping) {
        this._indexToElement = {};
        let elementIndexIDMap = elementMapping[0];
        let skipElementIndexIDMap = elementMapping[1];

        for (let elementIndex in elementIndexIDMap) {
            let elementId = elementIndexIDMap[elementIndex];
            let pathElement = $j('[data-element-id=' + elementId + ']').find('g').find('path').get(0);
            if (!pathElement) { // create cross and skip paths as they are not present
                this._createNodePathElements(elementId);
                pathElement = $j('[data-element-id=' + elementId + ']').find('g').find('path').get(0);
            }
            this._indexToElement[elementIndex] = pathElement;
        }

        for (let elementIndex in skipElementIndexIDMap) {
            let elementId = skipElementIndexIDMap[elementIndex];
            let pathElement = $j('[data-element-id=' + elementId + ']').find('g').find('path').get(1);
            this._indexToElement[elementIndex] = pathElement;
        }
    }

    /**
     * @returns {x, y, width, height, top, left}
     */
    getBoundingClientRect() {
        let box = this._svgMain.getBoundingClientRect();
        return {x: box.x,
                y: box.y,
                width: box.width,
                height: box.height,
                top: box.top,
                left: box.left};
    }

    /**
     * @returns {DOMMatrix}
     */
    getTransformMatrix() {
        let viewBox = this._svgViewport.transform.baseVal.consolidate();
        return viewBox ? viewBox.matrix : undefined;
    }

    isBPMNEditor() {
        return true;
    }

    /**
     * Get the on-screen point at a length distance on an element
     * @param elementIndex
     * @param distance
     */
    getPointAtDistance(elementIndex, distance) {
        let pathElement = this._indexToElement[elementIndex];
        return pathElement.getPointAtLength(pathElement.getTotalLength() * distance);
    }

    registerListener(listener) {
        this._listeners.push(listener);
    }

    /**
     * @param {AnimationEvent} event
     */
    _notifyAll(event) {
        this._listeners.forEach(function(listener){
            listener.handleEvent(event);
        })
    }

    /**
     * Create two paths: one crossing and one skipping a node
     * The paths are added as children to the node element
     * @param {String} nodeId
     */
    _createNodePathElements (nodeId) {
        let incomingEndPoint = $j(
            '[data-element-id=' + this._editor.getIncomingFlowId(nodeId) +
            ']',
        )
        let incomingPathE = incomingEndPoint.find('g').find('path').get(0)
        incomingEndPoint = incomingPathE.getPointAtLength(
            incomingPathE.getTotalLength(),
        )
        let crossPath, skipPath;
        let arrayAbove, arrayBelow;

        let outgoingStartPoint = $j(
            '[data-element-id=' + this._editor.getOutgoingFlowId(nodeId) +
            ']',
        )
        let outgoingPathE = outgoingStartPoint.find('g').find('path').get(0)
        outgoingStartPoint = outgoingPathE.getPointAtLength(0)

        let startPoint = incomingEndPoint
        let endPoint = outgoingStartPoint

        let nodeTransformE = $j('[data-element-id=' + nodeId + ']').get(0) //this <g> element contains the translate function
        let nodeRectE = $j('[data-element-id=' + nodeId + ']')
                                    .find('g')
                                    .find('rect')
                                    .get(0)
        let taskRectPoints = utils.getViewportPoints(
            this._svgMain,
            nodeRectE,
            nodeTransformE,
        )

        crossPath =
            'm' + startPoint.x + ',' + startPoint.y +
            ' L' + taskRectPoints.cc.x + ',' + taskRectPoints.cc.y +
            ' L' + endPoint.x + ',' + endPoint.y

        // Both points are on a same edge
        if (
            (Math.abs(startPoint.x - endPoint.x) < 10 &&
                Math.abs(endPoint.x - taskRectPoints.se.x) < 10) ||
            (Math.abs(startPoint.x - endPoint.x) < 10 &&
                Math.abs(endPoint.x - taskRectPoints.sw.x) < 10) ||
            (Math.abs(startPoint.y - endPoint.y) < 10 &&
                Math.abs(endPoint.y - taskRectPoints.nw.y) < 10) ||
            (Math.abs(startPoint.y - endPoint.y) < 10 &&
                Math.abs(endPoint.y - taskRectPoints.sw.y) < 10)
        ) {
            skipPath =
                'm' + startPoint.x + ',' + startPoint.y +
                ' L' + endPoint.x + ',' + endPoint.y
        } else {
            arrayAbove = []
            arrayBelow = []

            if (
                taskRectPoints.se.y <
                utils.getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.se)
            ) {
                arrayAbove.push(taskRectPoints.se)
            } else {
                arrayBelow.push(taskRectPoints.se)
            }

            if (
                taskRectPoints.sw.y <
                utils.getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.sw)
            ) {
                arrayAbove.push(taskRectPoints.sw)
            } else {
                arrayBelow.push(taskRectPoints.sw)
            }

            if (
                taskRectPoints.ne.y <
                utils.getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.ne)
            ) {
                arrayAbove.push(taskRectPoints.ne)
            } else {
                arrayBelow.push(taskRectPoints.ne)
            }

            if (
                taskRectPoints.nw.y <
                utils.getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.nw)
            ) {
                arrayAbove.push(taskRectPoints.nw)
            } else {
                arrayBelow.push(taskRectPoints.nw)
            }

            if (arrayAbove.length === 1) {
                skipPath =
                    'm' + startPoint.x + ',' + startPoint.y + ' ' +
                    'L' + arrayAbove[0].x + ',' + arrayAbove[0].y + ' ' +
                    'L' + endPoint.x + ',' + endPoint.y
            } else if (arrayBelow.length === 1) {
                skipPath =
                    'm' + startPoint.x + ',' + startPoint.y + ' ' +
                    'L' + arrayBelow[0].x + ',' + arrayBelow[0].y + ' ' +
                    'L' + endPoint.x + ',' + endPoint.y
            } else {
                if (Math.abs(startPoint.x - taskRectPoints.sw.x) < 10) {
                    skipPath =
                        'm' + startPoint.x + ',' + startPoint.y + ' ' +
                        'L' + taskRectPoints.sw.x + ',' + taskRectPoints.sw.y + ' ' +
                        'L' + taskRectPoints.se.x + ',' + taskRectPoints.se.y + ' ' +
                        'L' + endPoint.x + ',' + endPoint.y
                } else if (Math.abs(startPoint.x - taskRectPoints.se.x) < 10) {
                    skipPath =
                        'm' + startPoint.x + ',' + startPoint.y + ' ' +
                        'L' + taskRectPoints.se.x + ',' + taskRectPoints.se.y + ' ' +
                        'L' + taskRectPoints.sw.x + ',' + taskRectPoints.sw.y + ' ' +
                        'L' + endPoint.x + ',' + endPoint.y
                } else if (Math.abs(startPoint.y - taskRectPoints.sw.y) < 10) {
                    skipPath =
                        'm' + startPoint.x + ',' + startPoint.y + ' ' +
                        'L' + taskRectPoints.sw.x + ',' + taskRectPoints.sw.y + ' ' +
                        'L' + taskRectPoints.nw.x + ',' + taskRectPoints.nw.y + ' ' +
                        'L' + endPoint.x + ',' + endPoint.y
                } else if (Math.abs(startPoint.y - taskRectPoints.nw.y) < 10) {
                    skipPath =
                        'm' + startPoint.x + ',' + startPoint.y + ' ' +
                        'L' + taskRectPoints.nw.x + ',' + taskRectPoints.nw.y + ' ' +
                        'L' + taskRectPoints.sw.x + ',' + taskRectPoints.sw.y + ' ' +
                        'L' + endPoint.x + ',' + endPoint.y
                }
            }
        }

        let crossPathE = document.createElementNS(SVG_NS, 'path');
        crossPathE.setAttributeNS(null, 'd', crossPath);
        crossPathE.setAttributeNS(null, 'fill', 'transparent');
        crossPathE.setAttributeNS(null, 'stroke', 'none');

        let skipPathE = document.createElementNS(SVG_NS, 'path');
        skipPathE.setAttributeNS(null, 'd', skipPath);
        skipPathE.setAttributeNS(null, 'fill', 'transparent');
        skipPathE.setAttributeNS(null, 'stroke', 'none');

        let nodeGroupE = $j('[data-element-id=' + nodeId + ']').find('g').get(0);
        nodeGroupE.appendChild(crossPathE);
        nodeGroupE.appendChild(skipPathE);
    }


}
