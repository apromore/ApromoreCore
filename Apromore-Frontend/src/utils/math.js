const getStraighLineFunctionValue = function(p1, p2, pi) {
    let a = (p1.y - p2.y) / (p1.x - p2.x);
    let b = p1.y - (p1.x * (p1.y - p2.y)) / (p1.x - p2.x);
    return a * pi.x + b;
}

/**
 *
 * @param {x1,x2,y1,y2,w,h} boundingBox
 * @returns {{cc: {}, se: {}, sw: {}, ne: {}, nw: {}}}
 */
export const getBoxPoints = function(boundingBox) {
    let corners = {nw: {}, ne: {}, sw: {}, se: {}, cc: {}};

    corners.nw.x = boundingBox.x1;
    corners.nw.y = boundingBox.y1;

    corners.se.x = boundingBox.x2;
    corners.se.y = boundingBox.y2;

    corners.ne.x = boundingBox.x2;
    corners.ne.y = boundingBox.y1;

    corners.sw.x = boundingBox.x1;
    corners.sw.y = boundingBox.y2;

    corners.cc.x = boundingBox.x1 + boundingBox.w / 2;
    corners.cc.y = boundingBox.y1 + boundingBox.h / 2;
    return corners;
}

/**
 *
 * @param {x,y} startPoint: source point of the node
 * @param {x,y} endPoint: end point of the node
 * @param {Object} taskRectPoints: points on the bounding box of the node
 * @return {Array} array of points.
 */
export const getBoxCrossPath = function(startPoint, endPoint, taskRectPoints) {
    let crossPath = [];
    crossPath.push(startPoint.x, startPoint.y, taskRectPoints.cc.x, taskRectPoints.cc.y, endPoint.x, endPoint.y);
    return crossPath;
}

/**
 *
 * @param {x,y} startPoint: source point of the node
 * @param {x,y} endPoint: end point of the node
 * @param {Object} taskRectPoints: points on the bounding box of the node
 */
export const getBoxSkipPath = function(startPoint, endPoint, taskRectPoints) {
    let skipPath = [];
    let arrayAbove, arrayBelow;

    // Both points are approximately on the same line
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
        skipPath.push(startPoint.x, endPoint);
    } else {
        arrayAbove = []
        arrayBelow = []

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

        if (arrayAbove.length === 1) {
            skipPath.push(startPoint.x, startPoint.y,
                arrayAbove[0].x, arrayAbove[0].y,
                endPoint.x, endPoint.y);
        } else if (arrayBelow.length === 1) {
            skipPath.push(startPoint.x, startPoint.y,
                arrayBelow[0].x, arrayBelow[0].y,
                endPoint.x, endPoint.y);
        } else {
            if (Math.abs(startPoint.x - taskRectPoints.sw.x) < 10) {
                skipPath.push(startPoint.x, startPoint.y,
                    taskRectPoints.sw.x, taskRectPoints.sw.y,
                    taskRectPoints.se.x, taskRectPoints.se.y,
                    endPoint.x, endPoint.y);
            } else if (Math.abs(startPoint.x - taskRectPoints.se.x) < 10) {
                skipPath.push(startPoint.x, startPoint.y,
                    taskRectPoints.se.x, taskRectPoints.se.y,
                    taskRectPoints.sw.x, taskRectPoints.sw.y,
                    endPoint.x, endPoint.y);
            } else if (Math.abs(startPoint.y - taskRectPoints.sw.y) < 10) {
                skipPath.push(startPoint.x, startPoint.y,
                    taskRectPoints.sw.x, taskRectPoints.sw.y,
                    taskRectPoints.nw.x, taskRectPoints.nw.y,
                    endPoint.x, endPoint.y);
            } else if (Math.abs(startPoint.y - taskRectPoints.nw.y) < 10) {
                skipPath.push(startPoint.x, startPoint.y,
                    taskRectPoints.nw.x, taskRectPoints.nw.y,
                    taskRectPoints.sw.x, taskRectPoints.sw.y,
                    endPoint.x, endPoint.y);
            }
        }
    }
    return skipPath;
}

/**
 * @param {Array} pts: array of bezier control points
 * @returns {number}
 */
export const getTotalLengthBezier = function(pts) {
    let totalLength = 0;
    for (let i = 0; i + 5 < pts.length; i += 4) {
        let x1 = pts[i];
        let y1 = pts[i + 1];
        let x2 = pts[i + 2];
        let y2 = pts[i + 3];
        let x3 = pts[i + 4];
        let y3 = pts[i + 5];
        totalLength += quadraticBezierLength(x1, y1, x2, y2, x3, y3);
    }
    return totalLength;
}

/**
 *
 * @param {Array} pts: array of points, e.g. [x1,y1,x2,y2,x3,y3].
 * @param {Number} length of the point on the curve
 * @returns {{x: *, y: *}}
 */
export const getPointAtLengthBezier = function(pts, length) {
    let totalLength = 0;
    for (let i = 0; i + 5 < pts.length; i += 4) {
        let x1 = pts[i];
        let y1 = pts[i + 1];
        let x2 = pts[i + 2];
        let y2 = pts[i + 3];
        let x3 = pts[i + 4];
        let y3 = pts[i + 5];
        let localLength = quadraticBezierLength(x1, y1, x2, y2, x3, y3);
        if ((totalLength + localLength) >= length) {
            let localDistance = length - totalLength;
            let px = qbezierAt(x1, x2, x3, localDistance / localLength);
            let py = qbezierAt(y1, y2, y3, localDistance / localLength);
            return {x: px, y: py};
        } else {
            totalLength += localLength;
        }
    }
    return {x: pts[pts.length - 2], y: pts[pts.length - 1]};
}

//https://gist.github.com/tunght13488/6744e77c242cc7a94859
export const quadraticBezierLength = function(x1, y1, x2, y2, x3, y3) {
    let a, b, e, c, d, u, a1, e1, c1, d1, u1, v1x, v1y;

    v1x = x2 * 2;
    v1y = y2 * 2;
    d = x1 - v1x + x3;
    d1 = y1 - v1y + y3;
    e = v1x - 2 * x1;
    e1 = v1y - 2 * y1;
    c1 = (a = 4 * (d * d + d1 * d1));
    c1 += (b = 4 * (d * e + d1 * e1));
    c1 += (c = e * e + e1 * e1);
    c1 = 2 * Math.sqrt(c1);
    a1 = 2 * a * (u = Math.sqrt(a));
    u1 = b / u;
    a = 4 * c * a - b * b;
    c = 2 * Math.sqrt(c);

    let Y = (u1 + c) > 0 ? Math.log((2 * u + u1 + c1) / (u1 + c)) : 0;
    return a1===0 ? 0 : (a1 * c1 + u * b * (c1 - c) + a * Y) / (4 * a1);
}

// from http://en.wikipedia.org/wiki/Bézier_curve#Quadratic_curves
export const qbezierAt = function (p0, p1, p2, t) {
    return (1 - t) * (1 - t) * p0 + 2 * (1 - t) * t * p1 + t * t * p2;
}

/**
 *
 * @param {Array} pts: array, each element is either x or y coordinate, each consecutive x,y pair is a point
 * @returns {Number}
 */
export const getTotalLengthSegments = function (pts) {
    //let pts = edge._private.rscratch.allpts;
    let totalLength = 0;
    for (let i = 0; i + 3 < pts.length; i += 2) {
        let x1 = pts[i];
        let y1 = pts[i + 1];
        let x2 = pts[i + 2];
        let y2 = pts[i + 3];
        totalLength += segmentLength(x1, y1, x2, y2);
    }
    return totalLength;
}

/**
 *
 * @param {Array} pts: array, each element is either x or y coordinate, each consecutive x,y pair is a point
 * @param {Number} length: the distance of the point on the segments
 * @returns {Number}
 */
export const getPointAtLengthSegments = function (pts, length) {
    //let pts = edge._private.rscratch.allpts;
    let totalLength = 0;
    for (let i = 0; i + 3 < pts.length; i += 2) {
        let x1 = pts[i];
        let y1 = pts[i + 1];
        let x2 = pts[i + 2];
        let y2 = pts[i + 3];
        let localLength = segmentLength(x1, y1, x2, y2);
        if ((totalLength + localLength) >= length) {
            let localDistance = length - totalLength;
            let px = segmentsAt(x1, x2, localDistance / localLength);
            let py = segmentsAt(y1, y2, localDistance / localLength);
            return {x: px, y: py};
        } else {
            totalLength += localLength;
        }
    }
    return {x: pts[pts.length - 2], y: pts[pts.length - 1]};
};

export const segmentLength = function (x1, y1, x2, y2) {
    return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
}

export const segmentsAt = function (p0, p1, t) {
    return (1 - t) * p0 + t * p1;
};