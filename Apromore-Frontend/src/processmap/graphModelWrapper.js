import {AnimationEvent, AnimationEventType} from "../loganimation/animationEvents";
import * as Math from '../utils/math';

/**
 * GraphModelWrapper encapsulates Cytoscape and implementation needed by the token animation.
 * The token animation shows animation on the graph.
 *
 * @author Bruce Nguyen
 */
export default class GraphModelWrapper {
    /**
     * @param {Cytoscape} cy: cytoscape
     */
    constructor(cy) {
        this._listeners = [];
        this._cy = cy;

        // instance variables to implement automatic play after zooming/panning
        this._isModelMoving = false;
        this._zoomTimer = undefined;
        this._zoomLatency = 500; //latency to identify when zooming action stops
        this._panTimer = undefined;
        this._panLatency = 500; //latency to identify when panning action stops
    }

    initialize(elementIndexIDMap) {
        this._createElementCache(elementIndexIDMap);
        let me = this;

        const handleModelMoving = function() {
            let modelBox = me.getBoundingClientRect();
            let modelMatrix = me.getTransformMatrix();
            me._isModelMoving = true;
            me._notifyAll(new AnimationEvent(AnimationEventType.MODEL_CANVAS_MOVING,
                {viewbox: modelBox, transformMatrix: modelMatrix}));
        }

        const handleModelMovingStop = function() {
            if (!me._isModelMoving) return;
            let modelBox = me.getBoundingClientRect();
            let modelMatrix = me.getTransformMatrix();
            me._isModelMoving = false;
            me._notifyAll(new AnimationEvent(AnimationEventType.MODEL_CANVAS_MOVED,
                {viewbox: modelBox, transformMatrix: modelMatrix}));
        }

        this._cy.on('pan', function (event) {
            handleModelMoving();
            me.updateModelMovingTimer(me._panTimer, me._panLatency, handleModelMovingStop);
        });

        this._cy.on('zoom', function (event) {
            handleModelMoving();
            me.updateModelMovingTimer(me._zoomTimer, me._zoomLatency, handleModelMovingStop);
        });
    }

    /**
     * Update the timer used to check model moving progress
     * @param timer: the timer to update
     * @param latency: the latency setting to identify when moving has stopped
     * @param stopHandlingFunc: the function to be executed when the latency has passed
     */
    updateModelMovingTimer(timer, latency, stopHandlingFunc) {
        if (timer) {
            clearTimeout(timer);
            timer = null;
        }
        timer = setTimeout(function () {
            timer = null;
            stopHandlingFunc();
        }, latency);
    }

    /**
     * @param elementMapping: mapping from element index to element id
     * @private
     */
    _createElementCache(elementMapping) {
        let graph = this;
        let cy = this._cy;
        let elementIndexIDMap = elementMapping[0];
        let skipElementIndexIDMap = elementMapping[1];
        this._elementIndexToElement = {};
        this._elementIndexToPath = {}; // mapping from element index to array of points on the element
        this._elementIndexToPoint = new Map();

        for (let elementIndex in elementIndexIDMap) {
            let elementId = elementIndexIDMap[elementIndex];
            let element = cy.getElementById(elementId);
            this._elementIndexToElement[elementIndex] = element;
            this._elementIndexToPoint.set(elementIndex, []);
            if (element.isEdge()) {
                this._elementIndexToPath[elementIndex] = element._private.rscratch.allpts;
            }
            else if (element.incomers().length > 0 && element.outgoers().length > 0) {
                this._elementIndexToPath[elementIndex] = graph._getNodeCrossPath(elementId);
            }
        }

        for (let elementIndex in skipElementIndexIDMap) {
            let elementId = skipElementIndexIDMap[elementIndex];
            let element = cy.getElementById(elementId);
            this._elementIndexToElement[elementIndex] = element;
            this._elementIndexToPoint.set(elementIndex, []);
            if (element.incomers().length > 0 && element.outgoers().length > 0) {
                this._elementIndexToPath[elementIndex] = graph._getNodeSkipPath(elementId);
            }
        }

        this._ELEMENT_SLOTS = 1000; // number of slots on an element, the element has (number of slots + 1) points
        let elementDistanceStep = 1/this._ELEMENT_SLOTS;
        for (let [eleIndex, elePoints] of this._elementIndexToPoint) {
            for (let i=0; i<= this._ELEMENT_SLOTS; i++) {
                elePoints.push(this._getPointAtDistance(eleIndex, i/this._ELEMENT_SLOTS));
            }
        }
    }

    /**
     * @returns {x, y, width, height, top, left}
     */
    getBoundingClientRect() {
        //let box = this._cy.container().getBoundingClientRect();
        //let box = this._cy.elements().boundingBox();
        let width = $j("canvas[data-id='layer2-node']").attr('width');
        let height = $j("canvas[data-id='layer2-node']").attr('height');
        let jContainer = $j(this._cy.container());
        let paddingTop = jContainer.css('padding-top');
        let paddingLeft = jContainer.css('padding-left');
        return {x: paddingLeft,
                y: paddingTop,
                top: paddingTop,
                left: paddingLeft,
                width: width,
                height: height};
    }

    getTransformMatrix() {
        return {};
    }

    isBPMNEditor() {
        return false;
    }

    /**
     * @param elementIndex
     * @param distance: from 0 to 1, two decimal points, e.g. 0.01, 0.02, 0.03...
     */
    getPointAtDistance(elementIndex, distance) {

        let p = this._elementIndexToPoint.get(elementIndex)[this._getIndexFromDistance(distance)];
        let zoom = this._cy.zoom();
        let pan = this._cy.pan();
        return {x: p.x * zoom + pan.x, y: p.y * zoom + pan.y};
    }

    /**
     * @param {Number} distance: 0 to 1
     * @returns {number|*}
     * @private
     */
    _getIndexFromDistance(distance) {
        if (!this._ELEMENT_SLOTS) return 0;
        let elementDistanceStep = 1/this._ELEMENT_SLOTS;
        let roundDistance = window.Math.round(distance*1000)/1000; // rounded to 3 decimal points
        let index = window.Math.floor(roundDistance/elementDistanceStep);
        if (index > this._ELEMENT_SLOTS) index = this._ELEMENT_SLOTS;
        return index;
    }

    _getPointAtDistance(elementIndex, distance) {
        let p = this._element(elementIndex).isEdge() ? this._getPointAtDistanceOnEdge(elementIndex, distance)
                                            : this._getPointAtDistanceOnNode(elementIndex, distance);
        if (!p) {
            console.log("Error getPointAtDistance for elementIndex=" + elementIndex + ', distance=' + distance );
            console.log(this._element(elementIndex).isEdge() ? 'This element is an edge' : 'This element is a node');
        }
        return p;
    }

    _element(elementIndex) {
        return this._elementIndexToElement[elementIndex];
    }

    _getPointAtDistanceOnEdge(elementIndex, distance) {
        let pts = this._elementIndexToPath[elementIndex];
        let edgeType = this._element(elementIndex)._private.rscratch.edgeType;
        switch (edgeType) {
            case 'bezier':
            case 'multibezier':
            case 'self':
                return this._getPointAtDistanceOnBezier(pts, distance);
                break;
            case 'haystack':
            case 'straight':
            case 'segments':
                return this._getPointAtDistanceOnSegments(pts, distance);
                break;
            default:
        }
    }

    _getPointAtDistanceOnNode(elementIndex, distance) {
        let pts = this._elementIndexToPath[elementIndex];
        return this._getPointAtDistanceOnSegments(pts, distance);
    }

    _getPointAtDistanceOnBezier(pts, distance) {
        if (!pts || !pts.length || pts.length <= 1) {
            return {x:0, y:0};
        }
        else if (pts.length === 2) {
            return {x: pts[0], y: pts[1]};
        }
        else {
            let totalLength = Math.getTotalLengthBezier(pts);
            let currentPoint = Math.getPointAtLengthBezier(pts, distance*totalLength);
            return {x: currentPoint.x, y: currentPoint.y};
        }
    }

    _getPointAtDistanceOnSegments(pts, distance) {
        let cy = this._cy;
        if (!pts || !pts.length || pts.length <= 1) {
            return {x:0, y:0};
        }
        else if (pts.length === 2) {
            return {x: pts[0], y: pts[1]};
        }
        else { // at least two points, each has x,y
            let totalLength = Math.getTotalLengthSegments(pts);
            let currentPoint = Math.getPointAtLengthSegments(pts, distance*totalLength);
            return {x: currentPoint.x, y: currentPoint.y};
        }
    }

    _getNodeCrossPath(nodeId) {
        let cy = this._cy;
        let startPoint = cy.$('#' + nodeId).incomers()[0].targetEndpoint();
        let endPoint = cy.$('#' + nodeId).outgoers()[0].sourceEndpoint();
        let boundingBox = cy.getElementById(nodeId).boundingBox();
        return Math.getBoxCrossPath(startPoint, endPoint, Math.getBoxPoints(boundingBox));
    }

    _getNodeSkipPath(nodeId) {
        let cy = this._cy;
        let startPoint = cy.$('#' + nodeId).incomers()[0].targetEndpoint();
        let endPoint = cy.$('#' + nodeId).outgoers()[0].sourceEndpoint();
        let boundingBox = cy.getElementById(nodeId).boundingBox();
        return Math.getBoxSkipPath(startPoint, endPoint, Math.getBoxPoints(boundingBox));
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
}