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

/**
 @namespace Namespace for the Oryx core elements.
 @name ORYX.Core
 */
if (!ORYX.Core) {
    ORYX.Core = {};
}

/**
 * @class Oryx canvas.
 * @extends ORYX.Core.AbstractShape
 *
 */
ORYX.Core.Canvas = ORYX.Core.AbstractShape.extend({
    /** @lends ORYX.Core.Canvas.prototype */

    /**
     * Defines the current zoom level
     */
    zoomLevel: 1,

    /**
     * Constructor
     */
    construct: function (options) {
        arguments.callee.$.construct.apply(this, arguments);

        if (!(options && options.width && options.height)) {

            ORYX.Log.fatal("Canvas is missing mandatory parameters options.width and options.height.");
            return;
        }

        this.resourceId = options.id;
        this.nodes = [];
        this.edges = [];

        //init svg document
        this.rootNode = ORYX.Editor.graft("http://www.w3.org/2000/svg", options.parentNode,
            ['svg', {id: this.id, width: options.width, height: options.height},
                ['defs', {}]
            ]);

        this.rootNode.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        this.rootNode.setAttribute("xmlns:svg", "http://www.w3.org/2000/svg");

        this._htmlContainer = ORYX.Editor.graft("http://www.w3.org/1999/xhtml", options.parentNode,
            ['div', {id: "oryx_canvas_htmlContainer", style: "position:absolute; top:5px"}]);

        this.node = ORYX.Editor.graft("http://www.w3.org/2000/svg", this.rootNode,
            ['g', {},
                ['g', {"class": "stencils"},
                    ['g', {"class": "me"}],
                    ['g', {"class": "children"}],
                    ['g', {"class": "edge"}]
                ],
                ['g', {"class": "svgcontainer"}]
            ]);

        this.node.setAttributeNS(null, 'stroke', 'black');
        this.node.setAttributeNS(null, 'font-family', 'Verdana, sans-serif');
        this.node.setAttributeNS(null, 'font-size-adjust', 'none');
        this.node.setAttributeNS(null, 'font-style', 'normal');
        this.node.setAttributeNS(null, 'font-variant', 'normal');
        this.node.setAttributeNS(null, 'font-weight', 'normal');
        this.node.setAttributeNS(null, 'line-heigth', 'normal');

        this.node.setAttributeNS(null, 'font-size', ORYX.CONFIG.LABEL_DEFAULT_LINE_HEIGHT);

        this.bounds.set(0, 0, options.width, options.height);

        this.addEventHandlers(this.rootNode.parentNode);

        //disable context menu
        this.rootNode.oncontextmenu = function () {
            return false;
        };
    },

    getScrollNode: function () {
        "use strict";
        return Ext.get(this.rootNode).parent("div{overflow=auto}", true);
    },

    focus: function () {
        "use strict";
        try {
            // Get a href
            if (!this.focusEl) {
                this.focusEl = Ext.getBody().createChild({
                    tag: "a",
                    href: "#",
                    cls: "x-grid3-focus x-grid3-focus-canvas",
                    tabIndex: "-1"
                });
                this.focusEl.swallowEvent("click", true);
            }

            // Focus it
            if (Ext.isGecko) {
                this.focusEl.focus();
            }
            else {
                this.focusEl.focus.defer(1, this.focusEl);
            }
            this.focusEl.blur.defer(3, this.focusEl);

        } catch (e) {
        }
    },

    update: function () {
        "use strict";
        this.nodes.each(function (node) {
            this._traverseForUpdate(node);
        }.bind(this));

        var layoutEvents = this.getStencil().layout();

        if (layoutEvents) {
            layoutEvents.each(function (event) {

                // setup additional attributes
                event.shape = this;
                event.forceExecution = true;
                event.target = this.rootNode;

                // do layouting

                this._delegateEvent(event);
            }.bind(this));
        }

        this.nodes.invoke("_update");
        this.edges.invoke("_update", true);
    },

    _traverseForUpdate: function (shape) {
        "use strict";
        var childRet = shape.isChanged;
        shape.getChildNodes(false, function (child) {
            if (this._traverseForUpdate(child)) {
                childRet = true;
            }
        }.bind(this));

        if (childRet) {
            shape.layout();
            return true;
        } else {
            return false;
        }
    },

    layout: function () {
        "use strict";
    },

    /**
     *
     * @param {Object} deep
     * @param {Object} iterator
     */
    getChildNodes: function (deep, iterator) {
        "use strict";
        if (!deep && !iterator) {
            return this.nodes.clone();
        } else {
            var result = [];
            this.nodes.each(function (uiObject) {
                if (iterator) {
                    iterator(uiObject);
                }
                result.push(uiObject);

                if (deep && uiObject instanceof ORYX.Core.Shape) {
                    result = result.concat(uiObject.getChildNodes(deep, iterator));
                }
            });

            return result;
        }
    },


    /**
     * Overrides the UIObject.add method. Adds uiObject to the correct sub node.
     * @param {UIObject} uiObject
     */
    add: function (uiObject, index, silent) {
        //if uiObject is child of another UIObject, remove it.
        if (uiObject instanceof ORYX.Core.UIObject) {
            if (!(this.children.member(uiObject))) {
                //if uiObject is child of another parent, remove it from that parent.
                if (uiObject.parent) {
                    uiObject.parent.remove(uiObject, true);
                }

                //add uiObject to the Canvas
                //add uiObject to this Shape
                if (index !== undefined) {
                    this.children.splice(index, 0, uiObject);
                } else {
                    this.children.push(uiObject);
                }

                //set parent reference
                uiObject.parent = this;

                //add uiObject.node to this.node depending on the type of uiObject
                if (uiObject instanceof ORYX.Core.Shape) {
                    if (uiObject instanceof ORYX.Core.Edge) {
                        uiObject.addMarkers(this.rootNode.getElementsByTagNameNS(NAMESPACE_SVG, "defs")[0]);
                        uiObject.node = this.node.childNodes[0].childNodes[2].appendChild(uiObject.node);
                        this.edges.push(uiObject);
                    } else {
                        uiObject.node = this.node.childNodes[0].childNodes[1].appendChild(uiObject.node);
                        this.nodes.push(uiObject);
                    }
                } else {
                    uiObject.node = this.node.appendChild(uiObject.node);
                }

                uiObject.bounds.registerCallback(this._changedCallback);

                if (this.eventHandlerCallback && silent !== true) {
                    this.eventHandlerCallback({type: ORYX.CONFIG.EVENT_SHAPEADDED, shape: uiObject});
                }
            } else {
                ORYX.Log.warn("add: ORYX.Core.UIObject is already a child of this object.");
            }
        } else {
            ORYX.Log.fatal("add: Parameter is not of type ORYX.Core.UIObject.");
        }
    },

    /**
     * Overrides the UIObject.remove method. Removes uiObject.
     * @param {UIObject} uiObject
     */
    remove: function (uiObject, silent) {
        "use strict";
        //if uiObject is a child of this object, remove it.
        if (this.children.member(uiObject)) {
            //remove uiObject from children
            var parent = uiObject.parent;

            this.children = this.children.without(uiObject);

            //delete parent reference of uiObject
            uiObject.parent = undefined;

            //delete uiObject.node from this.node
            if (uiObject instanceof ORYX.Core.Shape) {
                if (uiObject instanceof ORYX.Core.Edge) {
                    uiObject.removeMarkers();
                    if ($A(this.node.childNodes[0].childNodes[2].childNodes).include(uiObject.node)) {
                        uiObject.node = this.node.childNodes[0].childNodes[2].removeChild(uiObject.node);
                    }
                    this.edges = this.edges.without(uiObject);
                } else {
                    if ($A(this.node.childNodes[0].childNodes[1].childNodes).include(uiObject.node)) {
                        uiObject.node = this.node.childNodes[0].childNodes[1].removeChild(uiObject.node)
                    }
                    this.nodes = this.nodes.without(uiObject);
                }
            } else {
                if ($A(this.node.childNodes).include(uiObject.node)) {
                    uiObject.node = this.node.removeChild(uiObject.node)
                }
            }

            if (this.eventHandlerCallback && silent !== true) {
                this.eventHandlerCallback({
                    type: ORYX.CONFIG.EVENT_SHAPEREMOVED,
                    shape: uiObject,
                    parent: parent
                });
            }

            uiObject.bounds.unregisterCallback(this._changedCallback);
        } else {
            ORYX.Log.warn("remove: ORYX.Core.UIObject is not a child of this object.");
        }
    },

    /**
     * Creates shapes out of the given collection of shape objects and adds them to the canvas.
     * @example
     * canvas.addShapeObjects({
         bounds:{ lowerRight:{ y:510, x:633 }, upperLeft:{ y:146, x:210 } },
         resourceId:"oryx_F0715955-50F2-403D-9851-C08CFE70F8BD",
         childShapes:[],
         properties:{},
         stencil:{
           id:"Subprocess"
         },
         outgoing:[{resourceId: 'aShape'}],
         target: {resourceId: 'aShape'}
       });
     * @param {Object} shapeObjects
     * @param {Function} [eventHandler] An event handler passed to each newly created shape (as eventHandlerCallback)
     * @return {Array} A collection of ORYX.Core.Shape
     * @methodOf ORYX.Core.Canvas.prototype
     */
    addShapeObjects: function (shapeObjects, langs, eventHandler) {
        "use strict";
        try {
            if (!shapeObjects) {
                return;
            }

            this.initializingShapes = true;
            var addShape = function (shape, parent) {
                // Create a new Stencil
                var stencil = ORYX.Core.StencilSet.stencil(this.getStencil().namespace() + shape.stencil.id, langs);
                if (!stencil) {
                    try {
                        throw new Error(this.getStencil().namespace() + shape.stencil.id + " is undefined.");
                    } catch (exception) {
                        if (window.console && console.log) {
                            console.log(exception);
                        }
                    }
                    return;
                }

                // Create a new Shape
                var ShapeClass = (stencil.type() === "node") ? ORYX.Core.Node : ORYX.Core.Edge;
                var newShape = new ShapeClass({
                    'eventHandlerCallback': eventHandler
                }, stencil);

                // Set the resource id
                newShape.setResourceId(shape.resourceId);

                // Set parent to json object to be used later
                // Due to the nested json structure, normally shape.parent is not set/ must not be set.
                // In special cases, it can be easier to set this directly instead of a nested structure.
                shape.parent = "#" + ((shape.parent && shape.parent.resourceId) || parent.resourceId);

                // Add the shape to the canvas
                this.add(newShape);

                return {
                    json: shape,
                    object: newShape
                };
            }.bind(this);

            /** Builds up recursively a flatted array of shapes, including a javascript object and json representation
             * @param {Object} shape Any object that has Object#childShapes
             */
            var addChildShapesRecursively = function (shape) {
                var addedShapes = [];

                shape.childShapes.each(function (childShape) {
                    addedShapes.push(addShape(childShape, shape));
                    addedShapes = addedShapes.concat(addChildShapesRecursively(childShape));
                });

                return addedShapes;
            }.bind(this);

            var shapes = addChildShapesRecursively({
                childShapes: shapeObjects,
                resourceId: this.resourceId
            });


            // prepare deserialisation parameter
            shapes.each(
                function (shape) {
                    var properties = [];
                    for (var field in shape.json.properties) {
                        properties.push({
                            prefix: 'oryx',
                            name: field,
                            value: shape.json.properties[field]
                        });
                    }

                    // Outgoings
                    shape.json.outgoing.each(function (out) {
                        if (out) {
                            properties.push({
                                prefix: 'raziel',
                                name: 'outgoing',
                                value: "#" + out.resourceId
                            });
                        }
                    });

                    // Target
                    // (because of a bug, the first outgoing is taken when there is no target,
                    // can be removed after some time)
                    if (shape.object instanceof ORYX.Core.Edge) {
                        var target = shape.json.target;
                        if (target) {
                            properties.push({
                                prefix: 'raziel',
                                name: 'target',
                                value: "#" + target.resourceId
                            });
                        }
                    }

                    // Bounds
                    if (shape.json.bounds && shape.json.bounds.upperLeft && shape.json.bounds.lowerRight &&
                        [shape.json.bounds.upperLeft.x, shape.json.bounds.upperLeft.y, shape.json.bounds.lowerRight.x, shape.json.bounds.lowerRight.y].all(function (bound) {
                            return "number" == typeof bound;
                        })) {
                        properties.push({
                            prefix: "oryx",
                            name: "bounds",
                            value: shape.json.bounds.upperLeft.x + "," + shape.json.bounds.upperLeft.y + "," + shape.json.bounds.lowerRight.x + "," + shape.json.bounds.lowerRight.y
                        });
                    }

                    // Dockers [{x:40, y:50}, {x:30, y:60}] => "40 50 30 60  #"
                    if (shape.json.dockers) {
                        properties.push({
                            prefix: 'oryx',
                            name: 'dockers',
                            value: shape.json.dockers.inject("", function (dockersStr, docker) {
                                return dockersStr + docker.x + " " + docker.y + " ";
                            }) + " #"
                        });
                    }

                    // Parent
                    properties.push({
                        prefix: 'raziel',
                        name: 'parent',
                        value: shape.json.parent
                    });

                    shape.__properties = properties;
                }.bind(this)
            );

            // Deserialize the properties from the shapes
            // This can't be done earlier because Shape#deserialize expects that all referenced nodes are already there

            // first, deserialize all nodes
            shapes.each(function (shape) {
                if (shape.object instanceof ORYX.Core.Node) {
                    shape.object.deserialize(shape.__properties, shape.json);
                }
            });

            // second, deserialize all edges
            shapes.each(function (shape) {
                if (shape.object instanceof ORYX.Core.Edge) {
                    shape.object.deserialize(shape.__properties, shape.json);
                    shape.object._oldBounds = shape.object.bounds.clone();
                    shape.object._update();
                }
            });

            delete this.initializingShapes;
            return shapes.pluck("object");
        } catch (exception) {
            if (window.console && console.log) {
                console.log(exception)
            }
        }
    },

    /**
     * Updates the size of the canvas, regarding to the containg shapes.
     */
    updateSize: function () {
        // Check the size for the canvas
        var maxWidth = 0;
        var maxHeight = 0;
        var offset = 100;
        this.getChildShapes(true, function (shape) {
            var b = shape.bounds;
            maxWidth = Math.max(maxWidth, b.lowerRight().x + offset);
            maxHeight = Math.max(maxHeight, b.lowerRight().y + offset);
        });

        if (this.bounds.width() < maxWidth || this.bounds.height() < maxHeight) {
            this.setSize({
                width: Math.max(this.bounds.width(), maxWidth),
                height: Math.max(this.bounds.height(), maxHeight)
            })
        }
    },

    getRootNode: function () {
        return this.rootNode;
    },

    getSvgContainer: function () {
        return this.node.childNodes[1];
    },

    getChildContainer: function () {
        return this.node.childNodes[0].childNodes[1]
    },

    getEdgeContainer: function () {
        return this.node.childNodes[0].childNodes[2]
    },

    getHTMLContainer: function () {
        return this._htmlContainer;
    },

    /**
     * Return all elements of the same highest level
     * @param {Object} elements
     */
    getShapesWithSharedParent: function (elements) {

        // If there is no elements, return []
        if (!elements || elements.length < 1) {
            return []
        }
        // If there is one element, return this element
        if (elements.length == 1) {
            return elements
        }

        return elements.findAll(function (value) {
            var parentShape = value.parent;
            while (parentShape) {
                if (elements.member(parentShape)) {
                    return false;
                }
                parentShape = parentShape.parent
            }
            return true;
        });

    },

    setSize: function (size, dontSetBounds) {
        if (!size || !size.width || !size.height) {
            return
        }

        if (this.rootNode.parentNode) {
            this.rootNode.parentNode.style.width = size.width + 'px';
            this.rootNode.parentNode.style.height = size.height + 'px';
        }

        this.rootNode.setAttributeNS(null, 'width', size.width);
        this.rootNode.setAttributeNS(null, 'height', size.height);
        this.updateScrollArea();

        //this._htmlContainer.style.top = "-" + (size.height + 4) + 'px';
        if (!dontSetBounds) {
            this.bounds.set({
                a: {
                    x: 0,
                    y: 0
                },
                b: {
                    x: size.width / this.zoomLevel,
                    y: size.height / this.zoomLevel
                }
            })
        }
    },

    updateScrollArea: function () {
        if (Ext.isIPad && "undefined" !== window.iScroll && this.iscroll) {
            this.iscroll.destroy();
            this.iscroll = new iScroll(this.rootNode.parentNode, {
                touchCount: 2
            })
        }
    },

    getShapeSize: function () {
        var c, n, b, m;
        var d = false;
        if (Ext.isChrome) {
            try {
                var q = this.getRootNode().childNodes[1].childNodes[0].childNodes[1].getBBox();
                if (q.x === 0 || q.y === 0) {
                    var a;
                    this.getChildShapes(true).each(function (e) {
                        var r = e.absoluteBounds();
                        if (!a) {
                            a = r.clone()
                        } else {
                            a.include(r)
                        }
                        e.getLabels().each(function (s) {
                            if (s.node.textContent) {
                                if (Ext.isChrome) {
                                    if (s.node.getAttributeNS(null, "display") === "none") {
                                        return
                                    }
                                }
                                var t = ORYX.Core.Math.getTranslatedBoundingBox(s.node);
                                a.include(new ORYX.Core.Bounds(t.x, t.y, t.x + t.width, t.y + t.height))
                            }
                        })
                    });
                    return a || new ORYX.Core.Bounds(0, 0, 0, 0)
                }
            } catch (g) {
            }
        }
        if (Ext.isIE9) {
            d = true
        }
        if (d) {
            var k = this.getRootNode().childNodes[1].childNodes[0].childNodes[1].getBBox();
            c = k.x;
            n = k.y;
            b = k.x + k.width;
            m = k.y + k.height;
            var p = function (r, t, e, s) {
                if (!r && !t && !e && !s) {
                    return
                }
                c = Math.min(c, r);
                n = Math.min(n, t);
                b = Math.max(b, e);
                m = Math.max(m, s)
            };
            this.getChildShapes(false).each(function (e) {
                if (e instanceof ORYX.Core.Edge) {
                    p(e.bounds.a.x, e.bounds.a.y, e.bounds.b.x, e.bounds.b.y);
                    e.getLabels().each(function (r) {
                        var s = r.node.getBBox();
                        p(s.x, s.y, s.x + s.width, s.y + s.height)
                    })
                }
            })
        } else {
            try {
                var h = 9;
                var f = this.getRootNode().childNodes[1].childNodes[0].childNodes[1].getBBox();
                var o = this.getRootNode().childNodes[1].childNodes[0].childNodes[2].getBBox();
                if (!f.x && !f.y && !f.width && !f.height) {
                    f = o
                }
                if (!o.x && !o.y && !o.width && !o.height) {
                    o = f
                }
                c = Math.min(f.x, o.x + h);
                n = Math.min(f.y, o.y + h);
                b = Math.max(f.x + f.width, o.x + o.width - h);
                m = Math.max(f.y + f.height, o.y + o.height - h)
            } catch (g) {
                return this.getShapeBounds()
            }
        }
        return new ORYX.Core.Bounds(c, n, b, m)
    },

    getShapeBounds: function () {
        var pos;
        this.getChildShapes().each(function (bounds) {
            var coordinates = bounds.absoluteBounds();
            if (!pos) {
                pos = coordinates.clone()
            } else {
                pos.include(coordinates)
            }
        });
        return pos || new ORYX.Core.Bounds(0, 0, 0, 0)
    },

    /**
     * Returns an SVG document of the current process.
     * @param {Boolean} escapeText Use true, if you want to parse it with an XmlParser,
     *                    false, if you want to use the SVG document in browser on client side.
     */
    getSVGRepresentation: function (escapeText) {
        // Get the serialized svg image source
        var svgClone = this.getRootNode().cloneNode(true);
        this._removeInvisibleElements(svgClone);
        var shapeSize = this.getShapeSize();

        var margin = 50;
        var width, height, tx, ty;
        width = shapeSize.width();
        height = shapeSize.height();
        tx = -shapeSize.upperLeft().x + (this.children.length ? (margin / 2) : 0);
        ty = -shapeSize.upperLeft().y + (this.children.length ? (margin / 2) : 0);

        svgClone.setAttributeNS(null, "width", Math.min.apply(Math, [width + margin, this.imageMaxWidth || undefined].compact()));
        svgClone.setAttributeNS(null, "height", Math.min.apply(Math, [height + margin, this.imageMaxHeight || undefined].compact()));
        svgClone.childNodes[1].firstChild.setAttributeNS(null, "transform", "translate(" + tx + ", " + ty + ")");
        svgClone.childNodes[1].setAttributeNS(null, "transform", "scale(1)");
        svgClone.childNodes[1].setAttributeNS(null, "transform", "");
        svgClone.childNodes[1].removeAttributeNS(null, "transform");

        try {
            var svgCont = svgClone.childNodes[1].childNodes[1];
            svgCont.parentNode.removeChild(svgCont);
        } catch (e) {
        }

        if (escapeText) {
            $A(svgClone.getElementsByTagNameNS(ORYX.CONFIG.NAMESPACE_SVG, 'tspan')).each(function (elem) {
                elem.textContent = elem.textContent.escapeHTML();
            });

            $A(svgClone.getElementsByTagNameNS(ORYX.CONFIG.NAMESPACE_SVG, 'text')).each(function (elem) {
                if (elem.childNodes.length == 0)
                    elem.textContent = elem.textContent.escapeHTML();
            });
        }

        // generating absolute urls for the pdf-exporter
        $A(svgClone.getElementsByTagNameNS(ORYX.CONFIG.NAMESPACE_SVG, 'image')).each(function (elem) {
            elem.parentNode.removeChild(elem);
        });

        // escape all links
        $A(svgClone.getElementsByTagNameNS(ORYX.CONFIG.NAMESPACE_SVG, 'a')).each(function (elem) {
            elem.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", (elem.getAttributeNS("http://www.w3.org/1999/xlink", "href") || "").escapeHTML());
        });

        return svgClone;
    },

    /**
     * Removes all nodes (and its children) that has the
     * attribute visibility set to "hidden"
     */
    _removeInvisibleElements: function (element) {
        var index = 0;
        var nodes = element.childNodes;
        var count = nodes.length;
        while (index < count) {
            var child = nodes[index];
            if (child.getAttributeNS && (child.getAttributeNS(null, "visibility") === "hidden" ||
                child.getAttributeNS(null, "display") === "none" || (child.getAttribute("stroke") === "none" &&
                child.getAttribute("fill") === "none"))) {
                element.removeChild(child);
                --count;
            } else {
                this._removeInvisibleElements(child);
                index++;
            }
        }
    },

    setLanguage: function (c, a) {
        if (c == this.getLanguage() && !(a instanceof Function)) {
            this.language = c;
            return false
        }
        this.language = c;
        var b = [];
        this.getChildShapes(true, function (d) {
            if (d.getStencil().properties().findAll(function (e) {
                if (e.language() === c && e.refToView().length > 0) {
                    d.propertiesChanged[e.prefix() + "-" + e.id()] = true;
                    return true
                }
                return false
            }).length > 0) {
                d._changed();
                b.push(d)
            }
        });
        if (a instanceof Function) {
            a(b)
        }
        this.update()
    },

    getLanguage: function () {
        return (this.language || (this.languages || []).first()) || "de_de"
    },

    migrateLanguage: function (a) {
        if (this.languages && this.languages.length > 0 && a && this.languages.first() !== a) {
            var c = this.languages.first();
            var b = function (d, g) {
                var e = g.prefix() + "-" + g.id(),
                    f = d.hiddenProperties[e + "_" + c];
                if ("undefined" == typeof f) {
                    f = d.properties[e + "_" + c]
                }
                if (d.getStencil().property(e + "_" + a)) {
                    d.properties[e + "_" + a] = d.properties[e]
                } else {
                    d.hiddenProperties[e + "_" + a] = d.properties[e]
                }
                if (d.formats && d.formats[e]) {
                    d.formats[e + "_" + a] = d.formats[e]
                }
                d.properties[e] = "undefined" == typeof f ? "" : f;
                if (d.formats && d.formats[e + "_" + c]) {
                    d.formats[e] = d.formats[e + "_" + c];
                    delete d.formats[e + "_" + c]
                } else {
                    delete d.formats[e]
                }
                delete d.hiddenProperties[e + "_" + c];
                delete d.properties[e + "_" + c]
            };
            this.getChildShapes(true, function (d) {
                d.getStencil().properties().each(function (e) {
                    if (e.multilanguage() && e == e.origin()) {
                        b(d, e)
                    }
                })
            });
            this.getStencil().properties().each(function (d) {
                if (d.multilanguage() && d == d.origin()) {
                    b(this, d)
                }
            }.bind(this))
        }
    },

    _delegateEvent: function (event) {
        if (this.eventHandlerCallback && ( event.target == this.rootNode || event.target == this.rootNode.parentNode )) {
            this.eventHandlerCallback(event, this);
        }
    },

    setOrientation: function (a) {
        if (a !== "horizontal" && a !== "vertical") {
            return
        }
        this.orientation = undefined;
        this.setProperty("oryx-orientation", a)
    },

    getOrientation: function () {
        return this.orientation || this.properties["oryx-orientation"]
    },

    toString: function () {
        return "Canvas " + this.id
    },

    deserialize: function (b, a) {
        if (a.language) {
            this.language = a.language
        }
        arguments.callee.$.deserialize.apply(this, arguments)
    },

    toJSON: function () {
        var json = arguments.callee.$.toJSON.apply(this, arguments);
        if (this.languages && this.languages.length > 0) {
            json.language = this.languages.first()
        }
        json.stencilset = {
            url: this.getStencil().stencilSet().source(),
            namespace: this.getStencil().stencilSet().namespace()
        };
        return json
    }
});
