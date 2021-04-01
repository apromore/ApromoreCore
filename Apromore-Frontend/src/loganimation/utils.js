/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

'use strict';

window.SVG_NS = "http://www.w3.org/2000/svg";
window.XLINK_NS = "http://www.w3.org/1999/xlink";

export function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

/* ******************************************************************
 * Return an object with four points corresponding to four corners of the input rect element
 * These are coordinates within the SVG document viewport
 * Input:
 * rect: the rectangle element
 * container: the containing SVGElement that contains the transformation affecting the rectangle
 * Return object has four points: nw, ne, se, sw, cc (center) (each with x,y attribute)
 * ******************************************************************/
export function getViewportPoints(svg, rect, container) {
    let matrix = container.transform.baseVal.getItem(0).matrix;
    let corners = {
        nw: svg.createSVGPoint().matrixTransform(matrix),
        ne: svg.createSVGPoint().matrixTransform(matrix),
        sw: svg.createSVGPoint().matrixTransform(matrix),
        se: svg.createSVGPoint().matrixTransform(matrix),
        cc: svg.createSVGPoint().matrixTransform(matrix)
    };

    let bbox = rect.getBBox();
    corners.ne.x += bbox.width;
    corners.se.x += bbox.width;
    corners.se.y += bbox.height;
    corners.sw.y += bbox.height;
    corners.cc.x += bbox.width / 2;
    corners.cc.y += bbox.height / 2;

    return corners;
}

/*
 * input: group element <g>
 * output: SVGPoint
 */
export function toViewportCoords(svg, groupE) {
    let pt = svg.createSVGPoint();
    let matrix = groupE.getScreenCTM();
    let rect = groupE.getBBox();
    pt.x = rect.x;
    pt.y = rect.y;
    return pt.matrixTransform(matrix);
}

export function drawCoordinateOrigin(svg) {
    const SVG_NS = "http://www.w3.org/2000/svg";
    let pt = svg.createSVGPoint();
    //let matrix  = groupE.getCTM();
    //let rect = groupE.getBBox();
    pt.x = svg.x.animVal.value;
    pt.y = svg.y.animVal.value;
    //console.log("SVG Document Origin: x="+ pt.x + " y=" + pt.y);
    //pt = pt.matrixTransform(matrix);

    let lineX = document.createElementNS(SVG_NS, "line");
    lineX.setAttributeNS(null, "x1", pt.x);
    lineX.setAttributeNS(null, "y1", pt.y);
    lineX.setAttributeNS(null, "x2", pt.x + 50);
    lineX.setAttributeNS(null, "y2", pt.y);
    lineX.setAttributeNS(null, "stroke", "red");
    lineX.setAttributeNS(null, "stroke-width", "5");

    let lineY = document.createElementNS(SVG_NS, "line");
    lineY.setAttributeNS(null, "x1", pt.x);
    lineY.setAttributeNS(null, "y1", pt.y);
    lineY.setAttributeNS(null, "x2", pt.x);
    lineY.setAttributeNS(null, "y2", pt.y + 50);
    lineY.setAttributeNS(null, "stroke", "red");
    lineY.setAttributeNS(null, "stroke-width", "5");

    //alert(rect.x + " " + rect.y);

    svg.appendChild(lineX);
    svg.appendChild(lineY);
}

/**
 * @deprecated
 * @param svg
 */
export function drawProcessModelOrigin(svg) {
    const SVG_NS = "http://www.w3.org/2000/svg";
    let pt = svg.createSVGPoint();
    let matrix = groupE.getCTM();
    let rect = groupE.getBBox();
    pt.x = rect.x;
    pt.y = rect.y;
    //alert(pt.x + " " + pt.y);
    pt = pt.matrixTransform(matrix);
    //console.log("Process Model Origin: x="+ pt.x + " y=" + pt.y);

    let lineX = document.createElementNS(SVG_NS, "line");
    lineX.setAttributeNS(null, "x1", pt.x);
    lineX.setAttributeNS(null, "y1", pt.y);
    lineX.setAttributeNS(null, "x2", pt.x + 50);
    lineX.setAttributeNS(null, "y2", pt.y);
    lineX.setAttributeNS(null, "stroke", "blue");
    lineX.setAttributeNS(null, "stroke-width", "5");

    let lineY = document.createElementNS(SVG_NS, "line");
    lineY.setAttributeNS(null, "x1", pt.x);
    lineY.setAttributeNS(null, "y1", pt.y);
    lineY.setAttributeNS(null, "x2", pt.x);
    lineY.setAttributeNS(null, "y2", pt.y + 50);
    lineY.setAttributeNS(null, "stroke", "blue");
    lineY.setAttributeNS(null, "stroke-width", "5");

    //alert(rect.x + " " + rect.y);

    groupE.appendChild(lineX);
    groupE.appendChild(lineY);
}

/* ********************************************************************
 * Find task in the list of nodes (object).
 * This list is an array from the JSON representation of Signavio
 * Use jsonModel global variable
 * A node has these attributes:
 *  - resourceId: uniquely identify the node
 *  - outgoing: array of outgoing sequence flows
 * Note: a task node can have association flow as part of its outgoing flows
 * After the search, the return node has these key attributes:
 *  - id: node id
 *  - ougoingFlow: id of outgoing flow
 *  - incomingFlow: id of incoming flow
 * ********************************************************************/
export function findModelNode(jsonModel, id) {
    let nodes = jsonModel.childShapes;

    //Find the node (with outgoing already)
    let node = null;
    for (let i = 0; i < nodes.length; ++i) {
        if (nodes[i].resourceId === id) {
            node = nodes[i];
            break;
        }
    }

    //Check and select the sequence flow (task can have association flow as outgoing)
    if (node != null) {
        if (node.outgoing.length > 2) {
            for (let i = 0; i < nodes.outgoing.length; ++i) {
                for (let j = 0; j < nodes.length; ++j) {
                    if (
                        nodes[j].resourceId === node.outgoing[i].resourceId &&
                        nodes[i].stencil.id === "SequenceFlow"
                    ) {
                        node.outgoingFlow = nodes[j].resourceId;
                        break;
                    }
                }
            }
        } else {
            node.outgoingFlow = node.outgoing[0].resourceId;
        }
    }

    //Find and assign the incoming flow
    for (let i = 0; i < nodes.length; ++i) {
        if (nodes[i].stencil.id === "SequenceFlow") {
            if (nodes[i].target.resourceId === id) {
                if (node != null) {
                    node.incomingFlow = nodes[i].resourceId;
                    break;
                }
            }
        }
    }

    return node;
}

/* *******************************************************
 * Calculate y function value from x value (pi.x, pi.y)
 * The straighline y = ax + b connects two points: p1, p2
 * Return value of y.
 *********************************************************/
export function getStraighLineFunctionValue(p1, p2, pi) {
    let a = (p1.y - p2.y) / (p1.x - p2.x);
    let b = p1.y - (p1.x * (p1.y - p2.y)) / (p1.x - p2.x);
    return a * pi.x + b;
}
