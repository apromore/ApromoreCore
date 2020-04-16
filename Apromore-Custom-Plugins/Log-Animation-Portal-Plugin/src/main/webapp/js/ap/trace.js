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
/**
 * A log trace
 *
 * - tokenAnimation is one of this.jsonServer.logs[].tokenAnimations
 * - color is the color the log trace marker
 * - label is the text annotation of the log trace marker
 * - offset
 *
 * Dependencies:
 * utils.js (for Clazz)
 */
'use strict'

let LogCase = {

  construct: function (controller, tokenAnimation, color, label, offset) {

    this.controller = controller
    this.tokenAnimation = tokenAnimation
    this.color = color
    this.label = label
    this.offset = offset

    this.svgMain = controller.svgMain
    this.svgViewport = controller.svgViewport;

    // a random angle to offset the marker
    // prevents markers from occluding one another totally
    // this.offsetAngle = 2 * Math.PI * Math.random()

    this.pathElementCache = {}
    this.nodeMarkers = []
    this.pathMarkers = []

  },

  /**
   * Create a marker (i.e. a token, which is an SVG animation element) and insert
   * it into the existing SVG document. SVG engine will start animating
   * them automatically.
   *
   * @param t number of engine slots
   * @param dt
   */
  updateMarker: function (t, dt) {

    // Delete any path markers which have moved beyond their interval
    let newMarkers = []
    let alreadyMarkedIndices = []
    while (this.pathMarkers.length > 0) {
      let marker = this.pathMarkers.pop()
      if (marker.begin <= t && t <= marker.end) {
        alreadyMarkedIndices[marker.index] = true
        newMarkers.push(marker)
      } else {
        marker.element.remove()
      }
    }
    this.pathMarkers = newMarkers

    // Insert any new path markers
    for (let i = 0; i < this.tokenAnimation.paths.length; i++) {
      let path = this.tokenAnimation.paths[i]
      let begin = parseFloat(path.begin)
      let dur = parseFloat(path.dur)
      let end = begin + dur

      if (begin <= t && t <= end && !alreadyMarkedIndices[i]) {
        let pathElement = this.getPathElement(path)
        if (!pathElement) {
          console.log('Unable to create marker for path.id=' + path.id)
        } else {
          let marker = {
            begin: begin,
            end: end,
            index: i,
            element: this.createPathMarker(
              t,
              dt,
              this.getPathElement(path),
              begin,
              dur,
              this.offset,
            ),
          }
          this.svgViewport.appendChild(marker.element)
          this.pathMarkers.push(marker)
        }
      }
    }

    // Delete any node markers which have moved beyond their interval
    newMarkers = []
    alreadyMarkedIndices = []
    while (this.nodeMarkers.length > 0) {
      let marker = this.nodeMarkers.pop()
      if (marker.begin <= t && t <= marker.end) {
        alreadyMarkedIndices[marker.index] = true
        newMarkers.push(marker)
      } else {
        marker.element.remove()
      }
    }
    this.nodeMarkers = newMarkers

    // Insert any new node markers
    for (let i = 0; i < this.tokenAnimation.nodes.length; i++) {
      let node = this.tokenAnimation.nodes[i]
      let begin = parseFloat(node.begin)
      let dur = parseFloat(node.dur)
      let end = begin + dur

      if (begin <= t && t <= end && !alreadyMarkedIndices[i]) {
        let marker = {
          begin: begin,
          end: end,
          index: i,
          element: this.createNodeMarker(t, dt, node, begin, dur, this.offset),
        }
        this.nodeMarkers.push(marker)
        this.svgViewport.appendChild(marker.element)
      }
    }
  },

  getPathElement: function (path) {
    let pathElement = this.pathElementCache[path.id]
    if (!pathElement) {
      // pathElement = this.pathElementCache[path.id] = $j("#svg-"+path.id).find("g").find("g").find("g").find("path").get(0);
      pathElement
        = this.pathElementCache[path.id]
        = $j('[data-element-id=' + path.id + ']').find('g').find('path').get(0)
    }
    return pathElement
  },

  createNodeMarker: function (t, dt, node, begin, dur, offset) {
    // let modelNode = findModelNode(this.jsonModel, node.id);
    // let incomingPathE = $j("#svg-"+modelNode.incomingFlow).find("g").find("g").find("g").find("path").get(0);
    // let incomingEndPoint = incomingPathE.getPointAtLength(incomingPathE.getTotalLength());
    let incomingEndPoint = $j(
      '[data-element-id=' + this.controller.canvas.getIncomingFlowId(node.id) +
      ']',
    )
    let incomingPathE = incomingEndPoint.find('g').find('path').get(0)
    incomingEndPoint = incomingPathE.getPointAtLength(
      incomingPathE.getTotalLength(),
    )
    let path, pathId
    let arrayAbove, arrayBelow

    // let outgoingPathE = $j("#svg-"+modelNode.outgoingFlow).find("g").find("g").find("g").find("path").get(0);
    // let outgoingStartPoint = outgoingPathE.getPointAtLength(0);
    let outgoingStartPoint = $j(
      '[data-element-id=' + this.controller.canvas.getOutgoingFlowId(node.id) +
      ']',
    )
    let outgoingPathE = outgoingStartPoint.find('g').find('path').get(0)
    outgoingStartPoint = outgoingPathE.getPointAtLength(0)

    let startPoint = incomingEndPoint
    let endPoint = outgoingStartPoint

    // let nodeRectE = $j("#svg-" + node.id).find("g").get(0); //this <g> element contains the translate function
    let nodeTransformE = $j('[data-element-id=' + node.id + ']').get(0) //this <g> element contains the translate function
    let nodeRectE = $j('[data-element-id=' + node.id + ']').
      find('g').
      find('rect').
      get(0)
    let taskRectPoints = getViewportPoints(
      this.svgMain,
      nodeRectE,
      nodeTransformE,
    )

    // Create path element
    if (node.isVirtual == 'false') {
      //go through center
      path =
        'm' + startPoint.x + ',' + startPoint.y +
        ' L' + taskRectPoints.cc.x + ',' + taskRectPoints.cc.y +
        ' L' + endPoint.x + ',' + endPoint.y
      // pathId = node.id +"_path";
    } else {
      // pathId = node.id +"_virtualpath";

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
        path =
          'm' + startPoint.x + ',' + startPoint.y +
          ' L' + endPoint.x + ',' + endPoint.y
      } else {
        arrayAbove = new Array()
        arrayBelow = new Array()

        if (
          taskRectPoints.se.y <
          getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.se)
        ) {
          arrayAbove.push(taskRectPoints.se)
        } else {
          arrayBelow.push(taskRectPoints.se)
        }

        if (
          taskRectPoints.sw.y <
          getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.sw)
        ) {
          arrayAbove.push(taskRectPoints.sw)
        } else {
          arrayBelow.push(taskRectPoints.sw)
        }

        if (
          taskRectPoints.ne.y <
          getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.ne)
        ) {
          arrayAbove.push(taskRectPoints.ne)
        } else {
          arrayBelow.push(taskRectPoints.ne)
        }

        if (
          taskRectPoints.nw.y <
          getStraighLineFunctionValue(startPoint, endPoint, taskRectPoints.nw)
        ) {
          arrayAbove.push(taskRectPoints.nw)
        } else {
          arrayBelow.push(taskRectPoints.nw)
        }

        if (arrayAbove.length == 1) {
          path =
            'm' + startPoint.x + ',' + startPoint.y + ' ' +
            'L' + arrayAbove[0].x + ',' + arrayAbove[0].y + ' ' +
            'L' + endPoint.x + ',' + endPoint.y
        } else if (arrayBelow.length == 1) {
          path =
            'm' + startPoint.x + ',' + startPoint.y + ' ' +
            'L' + arrayBelow[0].x + ',' + arrayBelow[0].y + ' ' +
            'L' + endPoint.x + ',' + endPoint.y
        } else {
          if (Math.abs(startPoint.x - taskRectPoints.sw.x) < 10) {
            path =
              'm' + startPoint.x + ',' + startPoint.y + ' ' +
              'L' + taskRectPoints.sw.x + ',' + taskRectPoints.sw.y + ' ' +
              'L' + taskRectPoints.se.x + ',' + taskRectPoints.se.y + ' ' +
              'L' + endPoint.x + ',' + endPoint.y
          } else if (Math.abs(startPoint.x - taskRectPoints.se.x) < 10) {
            path =
              'm' + startPoint.x + ',' + startPoint.y + ' ' +
              'L' + taskRectPoints.se.x + ',' + taskRectPoints.se.y + ' ' +
              'L' + taskRectPoints.sw.x + ',' + taskRectPoints.sw.y + ' ' +
              'L' + endPoint.x + ',' + endPoint.y
          } else if (Math.abs(startPoint.y - taskRectPoints.sw.y) < 10) {
            path =
              'm' + startPoint.x + ',' + startPoint.y + ' ' +
              'L' + taskRectPoints.sw.x + ',' + taskRectPoints.sw.y + ' ' +
              'L' + taskRectPoints.nw.x + ',' + taskRectPoints.nw.y + ' ' +
              'L' + endPoint.x + ',' + endPoint.y
          } else if (Math.abs(startPoint.y - taskRectPoints.nw.y) < 10) {
            path =
              'm' + startPoint.x + ',' + startPoint.y + ' ' +
              'L' + taskRectPoints.nw.x + ',' + taskRectPoints.nw.y + ' ' +
              'L' + taskRectPoints.sw.x + ',' + taskRectPoints.sw.y + ' ' +
              'L' + endPoint.x + ',' + endPoint.y
          }
        }
      }
    }
    return this.createMarker(t, dt, path, begin, dur, offset)
  },

  createPathMarker: function (t, dt, pathElement, begin, dur, offset) {
    let  path = pathElement.getAttribute('d')
    return this.createMarker(t, dt, path, begin, dur, offset)
  },

  /* A marker is an SVG g element:
   * <g>
   *   <animateMotion begin dur fill="freeze" path rotate="auto" />: the path to animate the token
   *   <circle cx cy r fill></circle>: the shape of the token
   *   <text x y style fill text-anchor visibility></text>: the label on top of the token
   * </g>
   */
  createMarker: function (t, dt, path, begin, dur, offset) {
    let marker = document.createElementNS(SVG_NS, 'g')
    marker.setAttributeNS(null, 'stroke', 'none')

    let animateMotion = document.createElementNS(SVG_NS, 'animateMotion')
    animateMotion.setAttributeNS(null, 'begin', begin / dt)
    animateMotion.setAttributeNS(null, 'dur', dur / dt)
    animateMotion.setAttributeNS(null, 'fill', 'freeze')
    animateMotion.setAttributeNS(null, 'path', path)
    animateMotion.setAttributeNS(null, 'rotate', 'auto')
    marker.appendChild(animateMotion)

    let circle = document.createElementNS(SVG_NS, 'circle')
    // Bruce 15/6/2015: add offset as a parameter, add 'rotate' attribute, put markers of different logs on separate lines.
    // let offset = 2;
    // circle.setAttributeNS(null, "cx", offset * Math.sin(this.offsetAngle));
    // circle.setAttributeNS(null, "cy", offset * Math.cos(this.offsetAngle));
    circle.setAttributeNS(null, 'cx', 0)
    circle.setAttributeNS(null, 'cy', offset)
    circle.setAttributeNS(null, 'r', 5)
    circle.setAttributeNS(null, 'fill', this.color)
    marker.appendChild(circle)

    let text = document.createElementNS(SVG_NS, 'text')
    // text.setAttributeNS(null,"x",offset * Math.sin(this.offsetAngle));
    // text.setAttributeNS(null,"y",offset * Math.cos(this.offsetAngle) - 10);
    text.setAttributeNS(null, 'x', 0)
    text.setAttributeNS(null, 'y', offset - 10)
    text.setAttributeNS(
      null,
      'style',
      'fill: black; text-anchor: middle' +
      (this.controller.caseLabelsVisible ? '' : '; visibility: hidden'),
    )
    text.appendChild(document.createTextNode(this.label))
    marker.appendChild(text)

    return marker
  },
}

Ap.la.LogCase = Clazz.extend(LogCase)
