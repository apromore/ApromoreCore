import PD from '../processdiscoverer';
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
    }

    initialize(elementIndexIDMap) {
        this._createElementCache(elementIndexIDMap);
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

        for (let elementIndex in elementIndexIDMap) {
            let elementId = elementIndexIDMap[elementIndex];
            let element = cy.getElementById(elementId);
            this._elementIndexToElement[elementIndex] = element;
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
            if (element.incomers().length > 0 && element.outgoers().length > 0) {
                this._elementIndexToPath[elementIndex] = graph._getNodeSkipPath(elementId);
            }
        }
    }

    getBoundingClientRect() {
        return this._cy.container().getBoundingClientRect();
    }

    getTransformMatrix() {
        return {};
    }

    isBPMNEditor() {
        return false;
    }

    getPointAtDistance(elementIndex, distance) {
        let p = this._element(elementIndex).isEdge() ? this._getPointAtDistanceOnEdge(elementIndex, distance)
                                            : this._getPointAtDistanceOnNode(elementIndex, distance);
        if (p) {
            let cy = this._cy;
            let zoom = cy.zoom();
            let pan = cy.pan();
            p.x = p.x * zoom + pan.x;
            p.y = p.y * zoom + pan.y;
        }
        else {
            console.log("Error getPointAtDistance for elementIndex=" + elementIndex + ', distance=' + distance );
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
}