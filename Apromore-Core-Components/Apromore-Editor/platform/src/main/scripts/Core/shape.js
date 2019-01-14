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
if(!ORYX) {var ORYX = {};}
if(!ORYX.Core) {ORYX.Core = {};}

/**
 * @classDescription Base class for Shapes.
 * @extends ORYX.Core.AbstractShape
 */
ORYX.Core.Shape = {

    /**
     * Constructor
     */
    construct: function(options, stencil) {
        // call base class constructor
        arguments.callee.$.construct.apply(this, arguments);

        this.dockers = [];
        this.magnets = [];

        this._defaultMagnet;

        this.incoming = [];
        this.outgoing = [];

        this.nodes = [];

        this._dockerChangedCallback = this._dockerChanged.bind(this);

        //Hash map for all labels. Labels are not treated as children of shapes.
        this._labels = new Hash();

        // create SVG node
        this.node = ORYX.Editor.graft("http://www.w3.org/2000/svg",
            null,
            ['g', {id:"svg-" + this.resourceId},
                ['g', {"class": "stencils"},
                    ['g', {"class": "me"}],
                    ['g', {"class": "children"}],
                    ['g', {"class": "edge"}]
                ],
                ['g', {"class": "controls"},
                    ['g', {"class": "dockers"}],
                    ['g', {"class": "magnets"}]
                ]
            ]);

        this.fillButStroke = {
            "http://b3mn.org/stencilset/bpmn2.0#Task": ["sendTaskpath", "sendTaskpath2"],
            "http://b3mn.org/stencilset/epc#Mail": ["atText"],
            "http://b3mn.org/stencilset/bpmn2.0#IntermediateMessageEventThrowing": ["path1", "path2"],
            "http://b3mn.org/stencilset/bpmn2.0#EndMessageEvent": ["path1", "path2"]
        };

        this.strokeAndFill = {
            "http://b3mn.org/stencilset/bpmn2.0#Task": ["userTaskcircle"],
            "http://b3mn.org/stencilset/bpmn2.0#Subprocess": ["adhocpath"],
            "http://b3mn.org/stencilset/bpmn2.0#EventSubprocess": ["adhocpath"],
            "http://b3mn.org/stencilset/bpmn2.0#CollapsedSubprocess": ["adhocpath"],
            "http://b3mn.org/stencilset/bpmn2.0#CollapsedEventSubprocess": ["adhocpath"],
            "http://b3mn.org/stencilset/bpmn2.0#IntermediateSignalEventThrowing": ["signalThrowing"],
            "http://b3mn.org/stencilset/bpmn2.0#IntermediateCompensationEventThrowing": ["poly1", "poly2"],
            "http://b3mn.org/stencilset/bpmn2.0#IntermediateEscalationEventThrowing": ["path9"],
            "http://b3mn.org/stencilset/bpmn2.0#IntermediateMultipleEventThrowing": ["middlePolygonThrowing"],
            "http://b3mn.org/stencilset/bpmn2.0#IntermediateLinkEventThrowing": ["poly1"],
            "http://b3mn.org/stencilset/bpmn2.0#EndCompensationEvent": ["poly1", "poly2"],
            "http://b3mn.org/stencilset/bpmn2.0#EndCancelEvent": ["path1"],
            "http://b3mn.org/stencilset/bpmn2.0#EndMultipleEvent": ["middlepolygon"],
            "http://b3mn.org/stencilset/bpmn2.0#EndErrorEvent": ["errorPolygon"],
            "http://b3mn.org/stencilset/bpmn2.0#EndEscalationEvent": ["path9"],
            "http://b3mn.org/stencilset/bpmn2.0#EndSignalEvent": ["signalThrowing"],
            "http://b3mn.org/stencilset/bpmn2.0#EndTerminateEvent": ["circle1"],
            "http://b3mn.org/stencilset/bpmn2.0#processparticipant": ["path1", "path2", "circle1"],
            "http://b3mn.org/stencilset/bpmn2.0#Exclusive_Databased_Gateway": ["crosspath", "crosspath2"],
            "http://b3mn.org/stencilset/bpmn2.0#SequenceFlow": ["arrowhead"],
            "http://b3mn.org/stencilset/bpmn2.0#DataObject": ["output"],
            "http://b3mn.org/stencilset/bpmn2.0conversation#SequenceFlow": ["arrowhead"],
            "http://b3mn.org/stencilset/bpmn2.0choreography#IntermediateEscalationEventThrowing": ["path9"],
            "http://b3mn.org/stencilset/bpmn2.0choreography#IntermediateLinkEventThrowing": ["poly1"],
            "http://b3mn.org/stencilset/bpmn2.0choreography#EndTerminateEvent": ["circle1"],
            "http://b3mn.org/stencilset/bpmn2.0choreography#Exclusive_Databased_Gateway": ["crosspath", "crosspath2"],
            "http://b3mn.org/stencilset/bpmn2.0choreography#SequenceFlow": ["arrowhead"],
            "http://b3mn.org/stencilset/epc#Fax": ["opening"]
        }
    },

    getMeContainer: function () {
        return this.node.childNodes[0].childNodes[0];
    },

    getChildContainer: function () {
        return this.node.childNodes[0].childNodes[1];
    },

    getEdgeContainer: function () {
        return this.node.childNodes[0].childNodes[2];
    },

    /**
     * If changed flag is set, refresh method is called.
     */
    update: function() {
    },

    /**
     * !!!Not called from any sub class!!!
     */
    _update: function() {
    },

    /**
     * Calls the super class refresh method
     *  and updates the svg elements that are referenced by a property.
     */
    refresh: function() {
        arguments.callee.$.refresh.apply(this, arguments);

        // BEGIN ADDITION FOR CONFIGURABLE TASK LABELS

                // Populate the <table> within a configuration annotation, based on the "variants" property of an annotated shape
                var refreshconfigurationannotation = function(annotated, annotation) {
                        try {
                                var svgElem = annotation.node.ownerDocument.getElementById(annotation.id + "text");
                                var variants = annotated.properties["oryx-variants"];
                                if (!variants || variants == "") {
                                        svgElem.getElementsByTagName("body")[0].innerHTML="Unset";
                                }
                                else {
                                        var a = annotated.properties["oryx-variants"].evalJSON();
                                        var rows = "";
                                        for (i=0; i<a.totalCount; i++) {
                                                rows += "<tr><td>" + a.items[i].id;
                                                if (a.items[i].name) { rows += "</td><td>" + a.items[i].name; }
                                                if (a.items[i].type) { rows += "</td><td>" + ORYX.I18N.TGatewayType[a.items[i].type]; }
                                                rows += "</td></tr>";
                                        }
                                        svgElem.getElementsByTagName("body")[0].innerHTML="<table>"+rows+"</table>";
                                }
                        }
                        catch(err) {
                                console.error("Unable to render ConfigurationAnnotation " + annotation.resourceId + ": " + err.message);
                        }
                };

                // Traverse from configuration annotation to the annotated shape
                if (this.getStencil().id() == "http://b3mn.org/stencilset/bpmn2.0#ConfigurationAnnotation") {
                        var connected = false;
                        var f = (function(method) {
                                this[method]().each((function(association,n) {
                                        if (association.getStencil().id() == "http://b3mn.org/stencilset/bpmn2.0#Association_Undirected") {
                                                // Look for an element with a "variants" property at the other end of the association
                                                association[method]().each((function(annotated,m) {
                                                        connected = true;
                                                        refreshconfigurationannotation(annotated, this);
                                                }).bind(this));
                                        }
                                }).bind(this));
                        }).bind(this);;
                        f("getIncomingShapes");
                        f("getOutgoingShapes");

                        // Blank if not connected to a configured shape
                        if (!connected) {
                                this.node.ownerDocument.getElementById(this.id + "text").getElementsByTagName("body")[0].innerHTML="Unconnected";
                        }
                }

                // Traverse outwards from an association between an annotated shape and the configuration annotation
                else if (this.getStencil().id() == "http://b3mn.org/stencilset/bpmn2.0#Association_Undirected") {
                        var isAttachedToConfigurationAnnotation = false;
                        var f = (function(method, method2) {
                                this[method]().each((function(annotation) {
                                        if (annotation.getStencil().id() == "http://b3mn.org/stencilset/bpmn2.0#ConfigurationAnnotation") {
                                                isAttachedToConfigurationAnnotation = true;
                                                this[method2]().each((function(annotated) {
                                                        refreshconfigurationannotation(annotated, annotation);
                                                }).bind(this));
                                        }
                                }).bind(this));
                        }).bind(this);
                        f("getIncomingShapes", "getOutgoingShapes");
                        f("getOutgoingShapes", "getIncomingShapes");

                        if (isAttachedToConfigurationAnnotation) {
                                this.node.setAttributeNS(null, "class", "configuration-extension");
                        }
                        else {
                                this.node.removeAttributeNS(null, "class");
                        }
                }

                // Traverse from annotated shape to the configuration annotation
                if (this.properties["oryx-variants"]) {
                        var hasAnnotation = false;
                        var f = (function(method) {
                                this[method]().each((function(association, n) {
                                        if (association.getStencil().id() == "http://b3mn.org/stencilset/bpmn2.0#Association_Undirected") {
                                                // Look for a configuration annotation at the source of the association
                                                association[method]().each((function(annotation, m) {
                                                        if (annotation.getStencil().id() == "http://b3mn.org/stencilset/bpmn2.0#ConfigurationAnnotation") {
                                                                hasAnnotation = true;
                                                                refreshconfigurationannotation(this, annotation);
                                                        }
                                                }).bind(this));
                                        }
                                }).bind(this));
                        }).bind(this);
                        f("getIncomingShapes");
                        f("getOutgoingShapes");

                        if (!hasAnnotation) {
                                //console.warn(this.resourceId + " lacks an annotation")
                        }
                }

        // END ADDITION FOR CONFIGURABLE TASK LABELS

        if (this.node.ownerDocument) {
            var c = function (e) {
                return this.node.ownerDocument.getElementById(e) || Ext.fly(this.node).child("[id=" + e + "]", true)
            }.bind(this);
            var b = function (e) {
                return [].concat($A(this.node.ownerDocument.getElementsByTagNameNS("http://www.w3.org/2000/svg", e)), $A(this.node.getElementsByTagNameNS("http://www.w3.org/2000/svg", e))).compact()
            }.bind(this);
            var a = this.getStencil().id();
            var d = this;
            this.propertiesChanged.each((function (e) {
                if (e.value) {
                    var h = this.properties[e.key];
                    var g = this.getStencil().property(e.key);
                    this.propertiesChanged[e.key] = false;
                    if (g.type() == ORYX.CONFIG.TYPE_CHOICE) {
                        g.refToView().each((function (m) {
                            if (m !== "") {
                                var k = this._labels[this.id + m];
                                if (k && g.item(h)) {
                                    k.text(d.wrapValueInPrefixAndSuffix(g, g.item(h).title()))
                                }
                            }
                        }).bind(this));
                        var f = new Hash();
                        g.items().each((function (k) {
                            k.refToView().each((function (m) {
                                if (m === "") {
                                    return
                                }
                                var n;
                                n = c(this.id + m);
                                if (!n) {
                                    return
                                }
                                if (!f[n.id] || h == k.value()) {
                                    if (Ext.isOpera) {
                                        n.setAttributeNS(null, "visibility", ((h == k.value()) ? "visible" : "hidden"))
                                    }
                                    n.setAttributeNS(null, "display", ((h == k.value()) ? "inherit" : "none"));
                                    f[n.id] = n
                                }
                                if (ORYX.Editor.checkClassType(n, SVGImageElement)) {
                                    n.setAttributeNS("http://www.w3.org/1999/xlink", "href", n.getAttributeNS("http://www.w3.org/1999/xlink", "href"))
                                }
                            }).bind(this))
                        }).bind(this))
                    } else {
                        g.refToView().each((function (p) {
                            if (p === "") {
                                return
                            }
                            var o = this.id + p;
                            var r = c(o);
                            var m = false;
                            try {
                                containsInDOM = this.node.parentNode && !(r.ownerSVGElement)
                            } catch (u) {
                                containsInDOM = false
                            }
                            if (!r || containsInDOM) {
                                if (g.type() === ORYX.CONFIG.TYPE_URL || g.type() === ORYX.CONFIG.TYPE_DIAGRAM_LINK) {
                                    var y = b("a");
                                    r = $A(y).find(function (C) {
                                        return C.getAttributeNS(null, "id") === o
                                    });
                                    if (!r) {
                                       return
                                    }
                                } else {
                                    return
                                }
                            }
                            if (g.complexAttributeToView()) {
                                var x = this._labels[o];
                                if (x) {
                                    try {
                                        propJson = h.evalJSON();
                                        var z = propJson[g.complexAttributeToView()];
                                        x.text(("undefined" === typeof z) ? h : z)
                                    } catch (u) {
                                        x.text(h)
                                    }
                                }
                            } else {
                                switch (g.type()) {
                                    case ORYX.CONFIG.TYPE_BOOLEAN:
                                        if (typeof h == "string") {
                                            h = h === "true"
                                        }
                                        if (Ext.isOpera) {
                                            r.setAttributeNS(null, "visibility", (!(h === g.inverseBoolean()) ? "visible" : "hidden"))
                                        }
                                        r.setAttributeNS(null, "display", (!(h === g.inverseBoolean())) ? "inherit" : "none");
                                        break;
                                    case ORYX.CONFIG.TYPE_COLOR:
                                        if (g.fill()) {
                                            if (r.tagName.toLowerCase() === "stop") {
                                                if (h) {
                                                    if (g.lightness() && g.lightness() !== 1) {
                                                        h = ORYX.Utils.adjustLightness(h, g.lightness())
                                                    }
                                                    r.setAttributeNS(null, "stop-color", h);
                                                    if (r.parentNode.tagName.toLowerCase() === "radialgradient") {
                                                        ORYX.Utils.adjustGradient(r.parentNode, r)
                                                    }
                                                }
                                                if (r.parentNode.tagName.toLowerCase() === "radialgradient") {
                                                    $A(r.parentNode.getElementsByTagName("stop")).each(function (C) {
                                                        C.setAttributeNS(null, "stop-opacity", h ? C.getAttributeNS(ORYX.CONFIG.NAMESPACE_ORYX, "default-stop-opacity") || 1 : 0)
                                                    }.bind(this))
                                                }
                                                var B = this.getMeContainer().childNodes[0];
                                                if (B && B.nodeType == 1) {
                                                    if (h && String(B.getAttributeNS(null, "pointer-events")).toLowerCase() == "stroke") {
                                                        B.setAttributeNS(null, "pointer-events", "all")
                                                    } else {
                                                        if (!h && String(B.getAttributeNS(null, "pointer-events")).toLowerCase() == "all") {
                                                            B.setAttributeNS(null, "pointer-events", "stroke")
                                                        }
                                                    }
                                                }
                                            } else {
                                                r.setAttributeNS(null, "fill", h || "none")
                                            }
                                        }
                                        if (g.stroke()) {
                                            if (this.fillButStroke[a] && this.fillButStroke[a].include(p)) {
                                                r.setAttributeNS(null, "fill", h || "none")
                                            } else {
                                                r.setAttributeNS(null, "stroke", h);
                                                if (this.strokeAndFill[a] && this.strokeAndFill[a].include(p)) {
                                                    r.setAttributeNS(null, "fill", h || "none")
                                                }
                                            }
                                        }
                                        break;
                                    case ORYX.CONFIG.TYPE_STRING:
                                        var x = this._labels[o];
                                        if (x) {
                                            x.text(d.wrapValueInPrefixAndSuffix(g, h));
                                            x.css = ""
                                        }
                                        break;
                                    case ORYX.CONFIG.TYPE_INTEGER:
                                        var x = this._labels[o];
                                        if (x) {
                                            x.text(d.wrapValueInPrefixAndSuffix(g, h))
                                        }
                                        break;
                                    case ORYX.CONFIG.TYPE_FLOAT:
                                        if (g.fillOpacity()) {
                                            r.setAttributeNS(null, "fill-opacity", h)
                                        }
                                        if (g.strokeOpacity()) {
                                            r.setAttributeNS(null, "stroke-opacity", h)
                                        }
                                        if (!g.fillOpacity() && !g.strokeOpacity()) {
                                            var x = this._labels[o];
                                            if (x) {
                                                x.text(d.wrapValueInPrefixAndSuffix(g, h))
                                            }
                                        }
                                        break;
                                    case ORYX.CONFIG.TYPE_URL:
                                    case ORYX.CONFIG.TYPE_DIAGRAM_LINK:
                                        var k = r.getAttributeNodeNS("http://www.w3.org/1999/xlink", "xlink:href");
                                        if (k) {
                                            k.textContent = h
                                        } else {
                                            r.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", h)
                                        }
                                        break;
                                    case ORYX.CONFIG.TYPE_COMPLEX:
                                      // BEGIN ADDITION FOR CONFIGURATION VARIANTS
                                      if (p == "text_variants") {
                                            var label = this._labels[o];
                                            if (label) {
                                                // Label with comma-separated list of configuration IDs
                                                try {
                                                    var variants = this.properties["oryx-variants"].evalJSON();
                                                    var formatted = "";
                                                    for (i = 0; i < variants.totalCount; i++) {
                                                        formatted += variants.items[i].id;
                                                        if (i < variants.totalCount - 1) {
                                                            formatted += ",";
                                                        }
                                                    }
                                                    label.text(formatted);
                                                }
                                                catch (err) {
                                                    // variants probably wasn't a populated list
                                                    label.text("");
                                                }
                                            }
                                      }

                                      else {
                                      // END ADDITION FOR CONFIGURATION VARIANTS

                                        if (x) {
                                            var n = function (J, I) {
                                                var C = "";
                                                var H = [];
                                                var D = typeof J.rowDelimiter() === "undefined" ? "\n" : J.rowDelimiter();
                                                var E = typeof J.itemDelimiter() === "undefined" ? " " : J.itemDelimiter();
                                                J.complexItems().each(function (K) {
                                                    if (K.includeInView()) {
                                                        H.push(K)
                                                    }
                                                });
                                                var G;
                                                if (I) {
                                                    G = typeof I === "object" ? I : I.evalJSON(true)
                                                }
                                                if (typeof G !== "undefined" && G.items) {
                                                    var F = [];
                                                    G.items.each(function (L) {
                                                        var K = [];
                                                        H.each(function (N) {
                                                            var U = false;
                                                            var O = "";
                                                            var P = N.viewPrefix();
                                                            if (typeof P !== "undefined") {
                                                                O = O.concat(P)
                                                            }
                                                            var Q = L[N.id()];
                                                            switch (N.type()) {
                                                                case ORYX.CONFIG.TYPE_COMPLEX:
                                                                    if (typeof Q !== "undefined" && Q.totalCount > 0) {
                                                                        O = O.concat(n(N, Q))
                                                                    }
                                                                    U = true;
                                                                    break;
                                                                case ORYX.CONFIG.TYPE_CHOICE:
                                                                    var R = N.items().find(function (V) {
                                                                        return V.value() === Q
                                                                    });
                                                                    if (R) {
                                                                        O = O.concat(R.title());
                                                                        U = true
                                                                    }
                                                                    break;
                                                                case ORYX.CONFIG.TYPE_BOOLEAN:
                                                                    if (((typeof Q === "boolean" && Q) || (typeof Q === "string" && Q.toLowerCase() === "true")) && typeof N.representation() !== "undefined") {
                                                                        O = O.concat(N.representation());
                                                                        U = true
                                                                    }
                                                                    break;
                                                                default:
                                                                    if (N.isList()) {
                                                                        var S;
                                                                        if (Q instanceof Array) {
                                                                            S = Q
                                                                        } else {
                                                                            if (typeof Q === "string") {
                                                                                S = Q.evalJSON(true)
                                                                            }
                                                                        }
                                                                        if (typeof S !== "undefined") {
                                                                            var M = typeof N.itemDelimiter() !== "undefined" ? N.itemDelimiter() : ", ";
                                                                            O = O.concat(S.join(M));
                                                                            U = true
                                                                        }
                                                                    } else {
                                                                        if (typeof Q !== "undefined" && Q !== "") {
                                                                            O = O.concat(Q);
                                                                            U = true
                                                                        }
                                                                    }
                                                                    break
                                                            }
                                                            var T = N.viewSuffix();
                                                            if (typeof T !== "undefined") {
                                                                O = O.concat(T)
                                                            }
                                                            if (U) {
                                                                K.push(O)
                                                            }
                                                        });
                                                        F.push(K.join(E))
                                                    });
                                                    C = F.join(D)
                                                }
                                                return C
                                            };
                                            x.text(n(g, h))
                                        }
                                      }
                                        break
                                }
                            }
                        }).bind(this))
                    }
                }
            }).bind(this));
            this._labels.values().each(function (e) {
                e.update()
            })
        }
    },

    wrapValueInPrefixAndSuffix: function (d, c) {
        if (typeof c === "undefined" || c === "") {
            return c
        }
        var a = c;
        var b = d.viewPrefix();
        if (typeof b !== "undefined") {
            a = b + a
        }
        var e = d.viewSuffix();
        if (typeof e !== "undefined") {
            a += e
        }
        return a
    },

    layout: function() {
        //this.getStencil().layout(this)
        var layoutEvents = this.getStencil().layout()
        if (layoutEvents) {
            layoutEvents.each(function(event) {

                // setup additional attributes
                event.shape = this;
                event.forceExecution = true;

                // do layouting
                this._delegateEvent(event);
            }.bind(this))

        }
    },

    /**
     * Returns an array of Label objects.
     */
    getLabels: function() {
        return this._labels.values();
    },

    /**
     * Returns the label for a given ref
     * @return {ORYX.Core.Label} Returns null if there is no label
     */
    getLabel: function(ref){
        if (!ref){
            return null;
        }
        return (this._labels.find(function(o){
            return o.key.endsWith(ref);
        })||{}).value || null;
    },

    /**
     * Hides all related labels
     *
     */
    hideLabels: function(){
        this.getLabels().invoke("hide");
    },

    /**
     * Shows all related labels
     *
     */
    showLabels: function(){
        var labels = this.getLabels();
        labels.invoke("show");
        labels.each(function(label) {
            label.update();
        });
    },

    setOpacity: function(value, animate){
        // 0.0 <= value <= 1.0
        value = Math.max(Math.min((typeof value == "number" ? value : 1.0), 1.0), 0.0);

        if (value !== 1.0){
            value = String(value);
            this.node.setAttributeNS(null, "fill-opacity", value)
            this.node.setAttributeNS(null, "stroke-opacity", value)
        } else {
            this.node.removeAttributeNS(null, "fill-opacity");
            this.node.removeAttributeNS(null, "stroke-opacity");
        }
    },

    /**
     * Returns an array of dockers of this object.
     */
    getDockers: function() {
        return this.dockers;
    },

    getMagnets: function() {
        return this.magnets;
    },

    getDefaultMagnet: function() {
        if(this._defaultMagnet) {
            return this._defaultMagnet;
        } else if (this.magnets.length > 0) {
            return this.magnets[0];
        } else {
            return undefined;
        }
    },

    getParentShape: function() {
        return this.parent;
    },

    getIncomingShapes: function(iterator) {
        if(iterator) {
            this.incoming.each(iterator);
        }
        return this.incoming;
    },

    getIncomingNodes: function(iterator) {
        return this.incoming.select(function(incoming){
            var isNode = (incoming instanceof ORYX.Core.Node);
            if(isNode && iterator) iterator(incoming);
            return isNode;
        });
    },

    getOutgoingShapes: function(iterator) {
        if(iterator) {
            this.outgoing.each(iterator);
        }
        return this.outgoing;
    },

    getOutgoingNodes: function(iterator) {
        return this.outgoing.select(function(out){
            var isNode = (out instanceof ORYX.Core.Node);
            if(isNode && iterator) iterator(out);
            return isNode;
        });
    },

    getAllDockedShapes: function(iterator) {
        var result = this.incoming.concat(this.outgoing);
        if(iterator) {
            result.each(iterator);
        }
        return result
    },

    getCanvas: function() {
        if ("undefined" != typeof this.canvas) {
            return this.canvas
        }
        if (this.parent instanceof ORYX.Core.Canvas) {
            this.canvas = this.parent
        } else {
            if (this.parent instanceof ORYX.Core.Shape) {
                this.canvas = this.parent.getCanvas()
            } else {
                this.canvas = null
            }
        }
        return this.canvas
    },

    /**
     *
     * @param {Object} deep
     * @param {Object} iterator
     */
    getChildNodes: function(deep, iterator) {
        if(!deep && !iterator) {
            return this.nodes.clone();
        } else {
            var result = [];
            this.nodes.each(function(uiObject) {
                if(!uiObject.isVisible){return}
                if(iterator) {
                    iterator(uiObject);
                }
                result.push(uiObject);

                if(deep && uiObject instanceof ORYX.Core.Shape) {
                    result = result.concat(uiObject.getChildNodes(deep, iterator));
                }
            });

            return result;
        }
    },

    getParentWithStencilId: function (id) {
        var shape = this;
        b = [].concat(id);
        while (shape && !(shape instanceof ORYX.Core.Canvas)) {
            var stencilId = shape.getStencil().id();
            for (var c = 0; c < id.length; c++) {
                if (shape instanceof ORYX.Core.Node && stencilId.endsWith(id[c])) {
                    return shape
                }
            }
            shape = shape.parent
        }
        return undefined
    },

    is: function (a, c) {
        var b = this.getStencil().id();
        a = [].concat(a);
        if (typeof c !== "undefined") {
            a = a.map(function (d) {
                if (!d.startsWith(c)) {
                    return c + d
                }
                return d
            })
        }
        return a.any(function (d) {
            return (d && b.endsWith(d))
        })
    },

    /**
     * Overrides the UIObject.add method. Adds uiObject to the correct sub node.
     * @param {UIObject} uiObject
     * @param {Number} index
     */
    add: function(uiObject, index, silent) {
        //parameter has to be an UIObject, but
        // must not be an Edge.
        if(uiObject instanceof ORYX.Core.UIObject && !(uiObject instanceof ORYX.Core.Edge)) {

            if (!(this.children.member(uiObject))) {
                //if uiObject is child of another parent, remove it from that parent.
                if (uiObject.parent) {
                    uiObject.parent.remove(uiObject, true);
                }

                //add uiObject to this Shape
                if (index != undefined) {
                    this.children.splice(index, 0, uiObject);
                } else {
                    this.children.push(uiObject);
                }

                //set parent reference
                uiObject.parent = this;

                //add uiObject.node to this.node depending on the type of uiObject
                var parent;
                if (uiObject instanceof ORYX.Core.Node) {
                    parent = this.node.childNodes[0].childNodes[1];
                    if (index != undefined) {
                        this.nodes.splice(index, 0, uiObject);
                    } else {
                        uiObject.node = this.node.childNodes[0].childNodes[1].appendChild(uiObject.node);
                        this.nodes.push(uiObject);
                    }
                } else if (uiObject instanceof ORYX.Core.Controls.Control) {
                    var ctrls = this.node.childNodes[1];
                    if (uiObject instanceof ORYX.Core.Controls.Docker) {
                        parent = ctrls.childNodes[0];
                        if (this.dockers.length >= 2){
                            this.dockers.splice(index !== undefined ? Math.min(index, this.dockers.length-1) : this.dockers.length-1, 0, uiObject);
                        } else {
                            this.dockers.push(uiObject);
                        }
                    } else if (uiObject instanceof ORYX.Core.Controls.Magnet) {
                        parent = ctrls.childNodes[1];
                        this.magnets.push(uiObject);
                    } else {
                        parent = ctrls;
                    }
                } else {
                    parent = this.node;
                }

                if (!(uiObject instanceof ORYX.Core.Shape)) {
                    if(index != undefined && index < parent.childNodes.length) {
                        uiObject.node = parent.insertBefore(uiObject.node, parent.childNodes[index]);
                    } else {
                        uiObject.node = parent.appendChild(uiObject.node);
                    }
                }
                this._changed();

                if (uiObject instanceof ORYX.Core.Shape && this.eventHandlerCallback && silent !== true) {
                    this.eventHandlerCallback({
                        type:ORYX.CONFIG.EVENT_SHAPEADDED,
                        shape: uiObject
                    })
                }
            } else {
                ORYX.Log.warn("add: ORYX.Core.UIObject is already a child of this object.");
            }
        } else {
            ORYX.Log.warn("add: Parameter is not of type ORYX.Core.UIObject.");
        }
    },

    /**
     * Overrides the UIObject.remove method. Removes uiObject.
     * @param {UIObject} uiObject
     */
    remove: function(uiObject, silent) {
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
                        uiObject.node = this.node.childNodes[0].childNodes[2].removeChild(uiObject.node)
                    }
                } else {
                    if ($A(this.node.childNodes[0].childNodes[1].childNodes).include(uiObject.node)) {
                        uiObject.node = this.node.childNodes[0].childNodes[1].removeChild(uiObject.node)
                    }
                    this.nodes = this.nodes.without(uiObject);
                }
            } else if (uiObject instanceof ORYX.Core.Controls.Control) {
                if (uiObject instanceof ORYX.Core.Controls.Docker) {
                    if ($A(this.node.childNodes[1].childNodes[0].childNodes).include(uiObject.node)) {
                        uiObject.node = this.node.childNodes[1].childNodes[0].removeChild(uiObject.node)
                    }
                    this.dockers = this.dockers.without(uiObject);
                } else if (uiObject instanceof ORYX.Core.Controls.Magnet) {
                    if ($A(this.node.childNodes[1].childNodes[1].childNodes).include(uiObject.node)) {
                        uiObject.node = this.node.childNodes[1].childNodes[1].removeChild(uiObject.node)
                    }
                    this.magnets = this.magnets.without(uiObject);
                } else {
                    if ($A(this.node.childNodes[1].childNodes).include(uiObject.node)) {
                        uiObject.node = this.node.childNodes[1].removeChild(uiObject.node)
                    }
                }
            }

            if(this.eventHandlerCallback && silent !== true) {
                this.eventHandlerCallback({
                    type: ORYX.CONFIG.EVENT_SHAPEREMOVED,
                    shape: uiObject,
                    parent: parent
                });
            }

            this._changed();
        } else {
            ORYX.Log.warn("remove: ORYX.Core.UIObject is not a child of this object.");
        }
    },

    /**
     * Calculate the Border Intersection Point between two points
     * @param {PointA}
     * @param {PointB}
     */
    getIntersectionPoint: function() {
        var pointAX, pointAY, pointBX, pointBY;

        // Get the the two Points
        switch(arguments.length) {
            case 2:
                pointAX = arguments[0].x;
                pointAY = arguments[0].y;
                pointBX = arguments[1].x;
                pointBY = arguments[1].y;
                break;
            case 4:
                pointAX = arguments[0];
                pointAY = arguments[1];
                pointBX = arguments[2];
                pointBY = arguments[3];
                break;
            default:
                throw "getIntersectionPoints needs two or four arguments";
        }

        // Defined an include and exclude point
        var includePointX, includePointY, excludePointX, excludePointY;
        var bounds = this.absoluteBounds();

        if (this.isPointIncluded(pointAX, pointAY, bounds)) {
            includePointX = pointAX;
            includePointY = pointAY;
        } else {
            excludePointX = pointAX;
            excludePointY = pointAY;
        }

        if (this.isPointIncluded(pointBX, pointBY, bounds)) {
            includePointX = pointBX;
            includePointY = pointBY;
        } else {
            excludePointX = pointBX;
            excludePointY = pointBY;
        }

        // If there is no inclue or exclude Shape, than return
        if(!includePointX || !includePointY || !excludePointX || !excludePointY) {
            return undefined;
        }

        var midPointX = 0;
        var midPointY = 0;
        var refPointX, refPointY;
        var minDifferent = 1;
        var centre = ORYX.Core.Math.getIntersectionPointOfRect(excludePointX, excludePointY, includePointX, includePointY, bounds.upperLeft().x, bounds.upperLeft().y, bounds.lowerRight().x, bounds.lowerRight().y);
        if (centre && this.isPointIncluded(centre.x, centre.y, bounds)) {
            return centre
        }

        var i = 0;
        while (true) {
            // Calculate the midpoint of the current to points
            var midPointX = Math.min(includePointX, excludePointX) + ((Math.max(includePointX, excludePointX) - Math.min(includePointX, excludePointX)) / 2.0);
            var midPointY = Math.min(includePointY, excludePointY) + ((Math.max(includePointY, excludePointY) - Math.min(includePointY, excludePointY)) / 2.0);

            // Set the new midpoint by the means of the include of the bounds
            if (this.isPointIncluded(midPointX, midPointY, bounds)){
                includePointX = midPointX;
                includePointY = midPointY;
            } else {
                excludePointX = midPointX;
                excludePointY = midPointY;
            }

            // Calc the length of the line
            var length = Math.sqrt(Math.pow(includePointX - excludePointX, 2) + Math.pow(includePointY - excludePointY, 2))
            // Calc a point one step from the include point
            refPointX = includePointX + ((excludePointX - includePointX) / length),
                refPointY = includePointY + ((excludePointY - includePointY) / length)


            // If the reference point not in the bounds, break
            if (!this.isPointIncluded(refPointX, refPointY, bounds)) {
                break
            }

            if (Math.abs(length) < 1) {
                break
            }
        }

        // Return the last includepoint
        return {
            x:refPointX,
            y:refPointY
        };
    },


    /**
     * Calculate if the point is inside the Shape
     * @param {PointX}
     * @param {PointY}
     */
    isPointIncluded: function(){
        return  false
    },

    /**
     * Returns TRUE if the given node
     * is a child node of the shapes node
     * @param {Element} node
     * @return {Boolean}
     *
     */
    containsNode: function(node){
        var me = this.node.firstChild.firstChild;
        while(node){
            if (node == me){
                return true;
            }
            node = node.parentNode;
        }
        return false
    },

    /**
     * Calculate if the point is over an special offset area
     * @param {Point}
     */
    isPointOverOffset: function(){
        return  this.isPointIncluded.apply( this , arguments )
    },

    getOverlap: function (node) {
        var tmpNode = this;
        var aul = tmpNode.absoluteBounds().upperLeft();
        var alr = tmpNode.absoluteBounds().lowerRight();
        var bul = node.absoluteBounds().upperLeft();
        var blr = node.absoluteBounds().lowerRight();
        var m = Math.min(alr.x, blr.x) - Math.max(aul.x, bul.x);
        var k = Math.min(alr.y, blr.y) - Math.max(aul.y, bul.y);
        if (m > 0 || k > 0) {
            var f = {
                x: Math.max(aul.x, bul.x),
                y: Math.min(alr.y, blr.y)
            };
            var d = {
                x: Math.min(alr.x, blr.x),
                y: Math.max(aul.y, bul.y)
            };
            return new ORYX.Core.Bounds(f, d)
        } else {
            return false
        }
    },

    _dockerChanged: function() {
    },

    /**
     * Create a Docker for this Edge
     */
    createDocker: function(index, position) {
        var docker = new ORYX.Core.Controls.Docker({eventHandlerCallback: this.eventHandlerCallback});
        docker.bounds.registerCallback(this._dockerChangedCallback);
        if(position) {
            docker.bounds.centerMoveTo(position);
        }
        this.add(docker, index);

        return docker
    },

    /**
     * Get the serialized object
     * return Array with hash-entrees (prefix, name, value)
     * Following values will given:
     * 		Bounds
     * 		Outgoing Shapes
     * 		Parent
     */
    serialize: function() {
        var serializedObject = arguments.callee.$.serialize.apply(this);

        // Add the bounds
        serializedObject.push({name: 'bounds', prefix:'oryx', value: this.bounds.serializeForERDF(), type: 'literal'});

        // Add the outgoing shapes
        this.getOutgoingShapes().each((function(followingShape){
            serializedObject.push({name: 'outgoing', prefix:'raziel', value: '#'+ERDF.__stripHashes(followingShape.resourceId), type: 'resource'});
        }).bind(this));

        // Add the parent shape, if the parent not the canvas
        serializedObject.push({name: 'parent', prefix:'raziel', value: '#'+ERDF.__stripHashes(this.parent.resourceId), type: 'resource'});

        return serializedObject;
    },


    deserialize: function(serialize, json){
        arguments.callee.$.deserialize.apply(this, arguments);

        // Set the Bounds
        var bounds = serialize.find(function(ser) {
            return 'oryx-bounds' === (ser.prefix+"-"+ser.name)
        });
        if (bounds) {
            var b = bounds.value.replace(/,/g, " ").split(" ").without("");
            if (this instanceof ORYX.Core.Edge) {
                if (!this.dockers.first().isChanged) {
                    this.dockers.first().bounds.centerMoveTo(parseFloat(b[0]), parseFloat(b[1]));
                }
                if (!this.dockers.last().isChanged) {
                    this.dockers.last().bounds.centerMoveTo(parseFloat(b[2]), parseFloat(b[3]));
                }
            } else {
                this.bounds.set(parseFloat(b[0]), parseFloat(b[1]), parseFloat(b[2]), parseFloat(b[3]));
            }
        }

        if (json && json.labels instanceof Array) {
            json.labels.each(function(slabel) {
                var label = this.getLabel(slabel.ref);
                if (label){
                    label.deserialize(slabel, this);
                }
            }.bind(this))
        }
    },

    toJSON: function(){
        var json = arguments.callee.$.toJSON.apply(this, arguments);

        var labels = [], id = this.id;
        this._labels.each(function(obj){
            var slabel = obj.value.serialize();
            if (slabel){
                slabel.ref = obj.key.replace(id, '');
                labels.push(slabel);
            }
        });

        if (labels.length > 0){
            json.labels = labels;
        }
        return json;
    },


    /**
     * Private methods.
     */

    /**
     * Child classes have to overwrite this method for initializing a loaded
     * SVG representation.
     * @param {SVGDocument} svgDocument
     */
    _init: function(svgDocument) {
        //adjust ids
        this._adjustIds(svgDocument, 0);
    },

    _adjustIds: function(element, idIndex) {
        if(element instanceof Element) {
            var eid = element.getAttributeNS(null, 'id');
            if(eid && eid !== "") {
                element.setAttributeNS(null, 'id', this.id + eid);
            } else {
                element.setAttributeNS(null, 'id', this.id + "_" + this.id + "_" + idIndex);
                idIndex++;
            }

            // Replace URL in fill attribute
            var fill = element.getAttributeNS(null, 'fill');
            if (fill && fill.include("url(#")) {
                fill = fill.replace(/url\(#/g, 'url(#' + this.id);
                element.setAttributeNS(null, 'fill', fill);
            }

            if (element.hasChildNodes()) {
                for (var i = 0; i < element.childNodes.length; i++) {
                    idIndex = this._adjustIds(element.childNodes[i], idIndex);
                }
            }
        }
        return idIndex;
    },

    toString: function() {
        return "ORYX.Core.Shape " + this.getId()
    }
};
ORYX.Core.Shape = ORYX.Core.AbstractShape.extend(ORYX.Core.Shape);
