/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2017 Queensland University of Technology.
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
 */

window.SVG_NS = "http://www.w3.org/2000/svg";
window.XLINK_NS = "http://www.w3.org/1999/xlink";

function getRandomInt(min, max) {
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
function getViewportPoints(svg, rect, container) {
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
function toViewportCoords(svg, groupE) {
  let pt = svg.createSVGPoint();
  let matrix = groupE.getScreenCTM();
  rect = groupE.getBBox();
  pt.x = rect.x;
  pt.y = rect.y;
  return pt.matrixTransform(matrix);
}

function drawCoordinateOrigin(svg) {
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

function drawProcessModelOrigin(svg) {
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
function findModelNode(jsonModel, id) {
  let nodes = jsonModel.childShapes;

  //Find the node (with outgoing already)
  let node = null;
  for (let i = 0; i < nodes.length; ++i) {
    if (nodes[i].resourceId == id) {
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
            nodes[j].resourceId == node.outgoing[i].resourceId &&
            nodes[i].stencil.id == "SequenceFlow"
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
    if (nodes[i].stencil.id == "SequenceFlow") {
      if (nodes[i].target.resourceId == id) {
        node.incomingFlow = nodes[i].resourceId;
        break;
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
function getStraighLineFunctionValue(p1, p2, pi) {
  let a = (p1.y - p2.y) / (p1.x - p2.x);
  let b = p1.y - (p1.x * (p1.y - p2.y)) / (p1.x - p2.x);
  return a * pi.x + b;
}

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
 * The super class for all classes in ORYX. Adds some OOP feeling to javascript.
 * See article "Object Oriented Super Class Method Calling with JavaScript" on
 * http://truecode.blogspot.com/2006/08/object-oriented-super-class-method.html
 * for a documentation on this. Fairly good article that points out errors in
 * Douglas Crockford's inheritance and super method calling approach.
 * Worth reading.
 * @class Clazz
 */
let Clazz = function() {};

/**
 * Empty constructor.
 * @methodOf Clazz.prototype
 */
Clazz.prototype.construct = function() {};

/**
 * Can be used to build up inheritances of classes.
 * @example
 * var MyClass = Clazz.extend({
 *   construct: function(myParam){
 *     // Do sth.
 *   }
 * });
 * var MySubClass = MyClass.extend({
 *   construct: function(myParam){
 *     // Use this to call constructor of super class
 *     arguments.callee.$.construct.apply(this, arguments);
 *     // Do sth.
 *   }
 * });
 * @param {Object} def The definition of the new class.
 */
Clazz.extend = function(def) {
  var classDef = function() {
    if (arguments[0] !== Clazz) {
      this.construct.apply(this, arguments);
    }
  };

  var proto = new this(Clazz);
  var superClass = this.prototype;

  for (var n in def) {
    var item = def[n];
    if (item instanceof Function) item.$ = superClass;
    proto[n] = item;
  }

  classDef.prototype = proto;

  //Give this new class the same static extend method
  classDef.extend = this.extend;
  return classDef;
};
