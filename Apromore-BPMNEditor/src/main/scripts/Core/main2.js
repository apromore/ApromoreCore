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

var idCounter = 0;
var ID_PREFIX = "resource";

/**
 * Main initialization method. To be called when loading
 * of the document, including all scripts, is completed.
 */
function initEditor() {
    "use strict";

    /* When the blank image url is not set programatically to a local
     * representation, a spacer gif on the site of ext is loaded from the
     * internet. This causes problems when internet or the ext site are not
     * available. */
    Ext.BLANK_IMAGE_URL = (ORYX.CONFIG.BLANK_IMAGE) || (ORYX.PATH + 'libs/ext-2.0.2/resources/images/default/s.gif');

    var lang = ORYX.I18N.Language;
    document.documentElement.className += " " + lang.split("_").concat(lang).uniq().join(" ");
    var browser = Ext.isIE7 ? "x-ie x-ie7" : (Ext.isIE ? "x-ie" : (Ext.isGecko ? "x-gecko" : (ORYX.Utils.isIPad() ? "x-ipad" : "x-other")));
    var version = ORYX.Utils.getBrowserVersion();
    document.documentElement.className += " " + browser + " " + browser + "-" + version.replace(/\./g, "-");

    ORYX.Log.debug("Querying editor instances");
    ORYX.Editor.setMissingClasses();

    // If someone wants to create the editor instance himself
    if (window.onOryxResourcesLoaded) {
        window.onOryxResourcesLoaded();
    } else if (window.location.pathname.include(ORYX.CONFIG.ORYX_NEW_URL)) {
        new ORYX.Editor({
            id: 'editorcanvas',
            fullscreen: true,
            stencilset: {
                url: "/oryx" + ORYX.Utils.getParamFromUrl("stencilset")
            }
        });
    } else {
        //HACK for distinguishing between different backends
        // Backend of 2008 uses /self URL ending
        var modelUrl = window.location.href.replace(/#.*/g, "");
        if (modelUrl.endsWith("/self")) {
            modelUrl = modelUrl.replace("/self", "/json");
        } else {
            modelUrl += "&data";
        }

        ORYX.Editor.createByUrl(modelUrl, {
            id: modelUrl
        });
    }
}

/**
 @namespace Global Oryx name space
 @name ORYX
 */
if (!ORYX) {
    var ORYX = {};
}

/**
 * The Editor class.
 * @class ORYX.Editor
 * @extends Clazz
 * @param {Object} config An editor object, passed to {@link ORYX.Editor#loadSerialized}
 * @param {String} config.id Any ID that can be used inside the editor. If fullscreen=false, any HTML node with this id must be present to render the editor to this node.
 * @param {boolean} [config.fullscreen=true] Render editor in fullscreen mode or not.
 * @param {String} config.stencilset.url Stencil set URL.
 * @param {String} [config.stencil.id] Stencil type used for creating the canvas.
 * @param {Object} config.properties Any properties applied to the canvas.
 */
ORYX.Editor = {
    /** @lends ORYX.Editor.prototype */

    construct: function (config) {
        "use strict";
        // initialization.

        this.loadedPlugins = [];
        this.pluginsData = [];

        var model = config;
        if (config.model) {
            model = config.model;
        }

        this.id = model.resourceId;
        if (!this.id) {
            this.id = model.id;
            if (!this.id) {
                this.id = ORYX.Editor.provideId();
            }
        }

        /*
        this.bpmnJS = new BpmnJS({
            container: '#' + this.id
        });
        */

        var langs = (config.languages || []).sort(function (k, h) {
            return config.position - config.position;
        });
        this._initNumberFormat(config.numberFormat, config.currency);

        // Defines if the editor should be fullscreen or not
        this.fullscreen = config.fullscreen !== false;

        if (Signavio && Signavio.Helper && Signavio.Helper.ShowMask instanceof Function) {
            Signavio.Helper.ShowMask(true, !this.fullscreen ? this.id : Ext.getBody());
        }

        // CREATES the canvas
       this._createCanvas(model.stencil ? model.stencil.id : null, model.properties, langs);

        // GENERATES the whole EXT.VIEWPORT
        this._generateGUI();

        // Initializing of a callback to check loading ends
        var loadPluginFinished = false;
        var loadContentFinished = false;
        var initFinished = function () {
            if (!loadPluginFinished || !loadContentFinished) {
                return;
            }
            this._finishedLoading();
        }.bind(this);

        // disable key events when Ext modal window is active
        ORYX.Editor.makeExtModalWindowKeysave(this._getPluginFacade());

        // LOAD the plugins
        window.setTimeout(function () {
            this.loadPlugins();
            loadPluginFinished = true;
            initFinished();
        }.bind(this), 100);

        // LOAD the content of the current editor instance
        window.setTimeout(function () {
            this.loadSerialized(model, langs, true); // Request the meta data as well
            loadContentFinished = true;
            initFinished();
        }.bind(this), 200);
    },

    _finishedLoading: function () {
        "use strict";
        if (Ext.getCmp('oryx-loading-panel')) {
            Ext.getCmp('oryx-loading-panel').hide();
        }

        Ext.Component.prototype.stateful = false;
        Ext.Component.prototype.saveState = Ext.Component.prototype.saveState.createInterceptor(function () {
            return this.stateful !== false;
        });

        // Fixed the problem that the viewport can not
        // start with collapsed panels correctly
        if (ORYX.CONFIG.PANEL_RIGHT_COLLAPSED === true) {
            this.layout_regions.east.collapse();
        }
        if (ORYX.CONFIG.PANEL_LEFT_COLLAPSED === true) {
            this.layout_regions.west.collapse();
        }

        if (String(window.navigator.userAgent).include("Firefox/3.5 Prism") && "function" == typeof this.layout.fireResize) {
            window.setTimeout(function (element) {
                this.layout.fireResize(element.getWidth(), element.getHeight());
            }.bind(this, Ext.getBody()), 100);
        }
    },

    _initNumberFormat: function (numFormat, currencyFormat) {
        "use strict";
        var format = new Signavio.Utils.NumberFormatter(numFormat, currencyFormat);
        Signavio.Utils.registerNumberFormatterFunctions(format);
    },


    /**
     * Generate the whole viewport of the
     * Editor and initialized the Ext-Framework
     */
    _generateGUI: function () {
        "use strict";

        // Defines the layout height if it's NOT fullscreen
        /*
        var layoutHeight = ORYX.CONFIG.WINDOW_HEIGHT,
            canvasParent = this.getCanvas().rootNode.parentNode,
        */

        /**
         * Extend the Region implementation so that,
         * the clicking area can be extend to the whole collapse area and
         * an title can now be shown.
         */
        var oldGetCollapsedEl = Ext.layout.BorderLayout.Region.prototype.getCollapsedEl;
        Ext.layout.BorderLayout.Region.prototype.getCollapsedEl = function () {
            oldGetCollapsedEl.apply(this, arguments);

            if (this.collapseMode !== 'mini' && this.floatable === false && this.expandTriggerAll === true) {
                this.collapsedEl.addClassOnOver("x-layout-collapsed-over");
                this.collapsedEl.on("mouseover", this.collapsedEl.addClass.bind(this.collapsedEl, "x-layout-collapsed-over"));
                this.collapsedEl.on("click", this.onExpandClick, this);
            }

            if (this.collapseTitle) {
                // Use SVG to rotate text
                var svg = ORYX.Editor.graft("http://www.w3.org/2000/svg", this.collapsedEl.dom,
                    ['svg', {style: "position:relative;left:" + (this.position === "west" ? 4 : 6) + "px;top:" + (this.position === "west" ? 2 : 5) + "px;"},
                        ['text', {transform: "rotate(90)", x: 0, y: 0, "stroke-width": "0px", fill: "#EEEEEE", style: "font-weight:bold;", "font-size": "11"}, this.collapseTitle]
                    ]),
                    text = svg.childNodes[0];
                svg.setAttribute("xmlns:svg", "http://www.w3.org/2000/svg");

                // Rotate the west into the other side
                if (this.position === "west" && text.getComputedTextLength instanceof Function) {
                    // Wait till rendered
                    window.setTimeout(function () {
                        var length = text.getComputedTextLength();
                        text.setAttributeNS(null, "transform", "rotate(-90, " + ((length / 2) + 7) + ", " + ((length / 2) - 3) + ")");
                    }, 1)
                }
                delete this.collapseTitle;
            }
            return this.collapsedEl;
        };

        // DEFINITION OF THE VIEWPORT AREAS
        this.layout_regions = {

            // DEFINES TOP-AREA
            north: new Ext.Panel({ //TOOO make a composite of the oryx header and addable elements (for toolbar), second one should contain margins
                region: 'north',
                cls: 'x-panel-editor-north',
                autoEl: 'div',
                border: false
            }),

            // DEFINES RIGHT-AREA
            east: new Ext.Panel({
                region: 'east',
                layout: 'fit',
                cls: 'x-panel-editor-east',
                autoEl: 'div',
                collapseTitle: ORYX.I18N.View.East,
                titleCollapse: true,
                border: false,
                cmargins: {left: 0, right: 0},
                floatable: false,
                expandTriggerAll: true,
                collapsible: true,
                width: ORYX.CONFIG.PANEL_RIGHT_WIDTH || 200,
                split: true,
                title: "East"
            }),

            // DEFINES BOTTOM-AREA
            south: new Ext.Panel({
                region: 'south',
                cls: 'x-panel-editor-south',
                autoEl: 'div',
                border: false
            }),

            // DEFINES LEFT-AREA
            west: new Ext.Panel({
                region: 'west',
                layout: 'anchor',
                autoEl: 'div',
                cls: 'x-panel-editor-west',
                collapsible: true,
                titleCollapse: true,
                collapseTitle: ORYX.I18N.View.West,
                width: ORYX.CONFIG.PANEL_LEFT_WIDTH || 200,
                autoScroll: Ext.isIPad ? false : true,
                cmargins: {left: 0, right: 0},
                floatable: false,
                expandTriggerAll: true,
                split: true,
                title: "West"
            }),

            // DEFINES CENTER-AREA (FOR THE EDITOR)
            center: new Ext.Panel({
                region: 'center',
                cls: 'x-panel-editor-center',
                autoScroll: true,
                items: {
                    layout: "fit",
                    autoHeight: true
                    //el: canvasParent
                }
            }),

            info: new Ext.Panel({
                region: "south",
                cls: "x-panel-editor-info",
                autoEl: "div",
                border: false,
                layout: "fit",
                cmargins: {
                    top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0
                },
                collapseTitle: "Information",
                floatable: false,
                titleCollapse: false,
                expandTriggerAll: true,
                collapsible: true,
                split: true,
                title: "Information",
                height: 100,
                tools: [
                    {
                        id: "close",
                        handler: function (g, f, e) {
                            e.hide();
                            e.ownerCt.layout.layout()
                        }
                    }
                ]
            })
        };

        // Config for the Ext.Viewport
        var layout_config = {
            layout: "border",
            items: [this.layout_regions.north, this.layout_regions.east, this.layout_regions.south, this.layout_regions.west, new Ext.Panel({
                layout: "border",
                region: "center",
                border: false,
                items: [this.layout_regions.center, this.layout_regions.info]
            })]
        };

        // IF Fullscreen, use a viewport
        if (this.fullscreen) {
            this.layout = new Ext.Viewport(layout_config);

            // IF NOT, use a panel and render it to the given id
        } else {
            layout_config.renderTo = this.id;
            layout_config.height = layoutHeight;
            this.layout = new Ext.Panel(layout_config)
        }

        this.layout_regions.info.hide();
        if (Ext.isIPad && "undefined" != typeof iScroll) {
            this.getCanvas().iscroll = new iScroll(this.layout_regions.center.body.dom.firstChild, {
                touchCount: 2
            })
        }

    },

    getCanvas: function () {
        return this._canvas;
    },


    /**
     * adds a component to the specified region
     *
     * @param {String} region
     * @param {Ext.Component} component
     * @param {String} title, optional
     * @return {Ext.Component} dom reference to the current region or null if specified region is unknown
     */
    addToRegion: function (region, component, title) {
        if (region.toLowerCase && this.layout_regions[region.toLowerCase()]) {
            var current_region = this.layout_regions[region.toLowerCase()];

            current_region.add(component);

            ORYX.Log.debug("original dimensions of region %0: %1 x %2", current_region.region, current_region.width, current_region.height);

            // update dimensions of region if required.
            if (!current_region.width && component.initialConfig && component.initialConfig.width) {
                ORYX.Log.debug("resizing width of region %0: %1", current_region.region, component.initialConfig.width);
                current_region.setWidth(component.initialConfig.width)
            }
            if (component.initialConfig && component.initialConfig.height) {
                ORYX.Log.debug("resizing height of region %0: %1", current_region.region, component.initialConfig.height);
                var current_height = current_region.height || 0;
                current_region.height = component.initialConfig.height + current_height;
                current_region.setHeight(component.initialConfig.height + current_height)
            }

            // set title if provided as parameter.
            if (typeof title == "string") {
                current_region.setTitle(title);
            }

            // trigger doLayout() and show the pane
            current_region.ownerCt.doLayout();
            current_region.show();

            if (Ext.isMac) {
                ORYX.Editor.resizeFix();
            }

            return current_region;
        }

        return null;
    },

    getAvailablePlugins: function () {
        var curAvailablePlugins = ORYX.availablePlugins.clone();
        curAvailablePlugins.each(function (plugin) {
            if (this.loadedPlugins.find(function (loadedPlugin) {
                return loadedPlugin.type == this.name;
            }.bind(plugin))) {
                plugin.engaged = true;
            } else {
                plugin.engaged = false;
            }
        }.bind(this));
        return curAvailablePlugins;
    },

    loadScript: function (url, callback) {
        var script = document.createElement("script");
        script.type = "text/javascript";
        if (script.readyState) {  //IE
            script.onreadystatechange = function () {
                if (script.readyState == "loaded" || script.readyState == "complete") {
                    script.onreadystatechange = null;
                    callback();
                }
            };
        } else {  //Others
            script.onload = function () {
                callback();
            };
        }
        script.src = url;
        document.getElementsByTagName("head")[0].appendChild(script);
    },
    /**
     * activate Plugin
     *
     * @param {String} name
     * @param {Function} callback
     *        callback(sucess, [errorCode])
     *            errorCodes: NOTUSEINSTENCILSET, REQUIRESTENCILSET, NOTFOUND, YETACTIVATED
     */
    activatePluginByName: function (name, callback, loadTry) {

        var match = this.getAvailablePlugins().find(function (value) {
            return value.name == name
        });
        if (match && (!match.engaged || (match.engaged === 'false'))) {
            var loadedStencilSetsNamespaces = this.getStencilSets().keys();
            var facade = this._getPluginFacade();
            var me = this;
            ORYX.Log.debug("Initializing plugin '%0'", match.name);

            if (!match.requires || !match.requires.namespaces || match.requires.namespaces.any(function (req) {
                return loadedStencilSetsNamespaces.indexOf(req) >= 0
            })) {
                if (!match.notUsesIn || !match.notUsesIn.namespaces || !match.notUsesIn.namespaces.any(function (req) {
                    return loadedStencilSetsNamespaces.indexOf(req) >= 0
                })) {

                    try {

                        var className = eval(match.name);
                        var newPlugin = new className(facade, match);
                        newPlugin.type = match.name;

                        // If there is an GUI-Plugin, they get all Plugins-Offer-Meta-Data
                        if (newPlugin.registryChanged)
                            newPlugin.registryChanged(me.pluginsData);

                        // If there have an onSelection-Method it will pushed to the Editor Event-Handler
                        if (newPlugin.onSelectionChanged)
                            //me.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, newPlugin.onSelectionChanged.bind(newPlugin));
                        this.loadedPlugins.push(newPlugin);
                        this.loadedPlugins.each(function (loaded) {
                            if (loaded.registryChanged)
                                loaded.registryChanged(this.pluginsData);
                        }.bind(me));
                        callback(true);

                    } catch (e) {
                        ORYX.Log.warn("Plugin %0 is not available", match.name);
                        if (!!loadTry) {
                            callback(false, "INITFAILED");
                            return;
                        }
                        this.loadScript("plugins/scripts/" + match.source, this.activatePluginByName.bind(this, match.name, callback, true));
                    }
                } else {
                    callback(false, "NOTUSEINSTENCILSET");
                    ORYX.Log.info("Plugin need a stencilset which is not loaded'", match.name);
                }

            } else {
                callback(false, "REQUIRESTENCILSET");
                ORYX.Log.info("Plugin need a stencilset which is not loaded'", match.name);
            }


        } else {
            callback(false, match ? "NOTFOUND" : "YETACTIVATED");
            //TODO error handling
        }
    },

    /**
     *  Laden der Plugins
     */
    loadPlugins: function () {

        // if there should be plugins but still are none, try again.
        // TODO this should wait for every plugin respectively.
        /*if (!ORYX.Plugins && ORYX.availablePlugins.length > 0) {
         window.setTimeout(this.loadPlugins.bind(this), 100);
         return;
         }*/

        var me = this;
        var newPlugins = [];

        // Available Plugins will be initalize
        var facade = this._getPluginFacade();

        ORYX.availablePlugins.each(function (value) {
            ORYX.Log.debug("Initializing plugin '%0'", value.name);
            if ((!value.requires || !value.requires.namespaces || value.requires.namespaces.any(function (req) {
                // return loadedStencilSetsNamespaces.indexOf(req) >= 0
            }) ) &&
                (!value.notUsesIn || !value.notUsesIn.namespaces || !value.notUsesIn.namespaces.any(function (req) {
                    // return loadedStencilSetsNamespaces.indexOf(req) >= 0
                }) ) &&
                /*only load activated plugins or undefined */
                (value.engaged || (value.engaged === undefined))) {

                try {
                    var className = eval(value.name);
                    if (className) {
                        var plugin = new className(facade, value);
                        plugin.type = value.name;
                        newPlugins.push(plugin);
                        plugin.engaged = true;
                    }
                } catch (e) {
                    ORYX.Log.warn("Plugin %0 is not available", value.name);
                }

            } else {
                ORYX.Log.info("Plugin need a stencilset which is not loaded'", value.name);
            }

        });

        newPlugins.each(function (value) {
            // If there is an GUI-Plugin, they get all Plugins-Offer-Meta-Data
            if (value.registryChanged)
                value.registryChanged(me.pluginsData);

            // If there have an onSelection-Method it will pushed to the Editor Event-Handler
            // if (value.onSelectionChanged)
            //     me.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, value.onSelectionChanged.bind(value));
        });

        this.loadedPlugins = newPlugins;

        // Hack for the Scrollbars
        if (Ext.isMac) {
            ORYX.Editor.resizeFix();
        }


    },

    /**
     * Creates the Canvas
     * @param {String} [stencilType] The stencil type used for creating the canvas. If not given, a stencil with myBeRoot = true from current stencil set is taken.
     * @param {Object} [canvasConfig] Any canvas properties (like language).
     */
    _createCanvas: function (stencilType, canvasConfig, lang) {
        // if (stencilType) {
        //     // Add namespace to stencilType
        //     if (stencilType.search(/^http/) === -1) {
        //         stencilType = this.getStencilSets().values()[0].namespace() + stencilType;
        //     }
        // } else {
        //     // Get any root stencil type
        //     stencilType = this.getStencilSets().values()[0].findRootStencilName();
        // }
        //
        // // get the stencil associated with the type
        // var canvasStencil = ORYX.Core.StencilSet.stencil(stencilType);
        //
        // if (!canvasStencil) {
        //     ORYX.Log.fatal("Initialisation failed, because the stencil with the type %0 is not part of one of the loaded stencil sets.", stencilType);
        // }

        // create all dom
        var div = ORYX.Editor.graft("http://www.w3.org/1999/xhtml", null, ['div']);
        // set class for custom styling
        div.addClassName("ORYX_Editor");

        // create the canvas
        this._canvas = new ORYX.Core.Canvas({
            width: ORYX.CONFIG.CANVAS_WIDTH,
            height: ORYX.CONFIG.CANVAS_HEIGHT,
            eventHandlerCallback: this.handleEvents.bind(this),
            id: this.id,
            parentNode: div,
            language: lang
        }, null);

        if (canvasConfig && canvasConfig.properties) {
            // Migrate canvasConfig to an RDF-like structure
            var properties = [];
            for (field in canvasConfig.properties) {
                properties.push({
                    prefix: 'oryx',
                    name: field,
                    value: canvasConfig.properties[field],
                    format: (canvasConfig.formats ? canvasConfig.formats[field] : [])
                });
            }

            this._canvas.deserialize(properties);
            if (this.modelMetaData.model && this.modelMetaData.model.orientation) {
                this._canvas.setOrientation(this.modelMetaData.model.orientation)
            }
            if (this.modelMetaData.imageMaxWidth && this.modelMetaData.imageMaxWidth > 0) {
                this._canvas.imageMaxWidth = this.modelMetaData.imageMaxWidth
            } else {
                this._canvas.imageMaxWidth = undefined
            }
            if (this.modelMetaData.imageMaxHeight && this.modelMetaData.imageMaxHeight > 0) {
                this._canvas.imageMaxHeight = this.modelMetaData.imageMaxHeight
            } else {
                this._canvas.imageMaxHeight = undefined
            }
        }

    },

    /**
     * Returns a per-editor singleton plugin facade.
     * To be used in plugin initialization.
     */
    _getPluginFacade: function () {
        if (!(this._pluginFacade)) {
            this._pluginFacade = (function () {
                return {
                    activatePluginByName: this.activatePluginByName.bind(this),
                    //deactivatePluginByName:		this.deactivatePluginByName.bind(this),
                    getAvailablePlugins: this.getAvailablePlugins.bind(this),
                    offer: this.offer.bind(this),
                    importJSON: this.importJSON.bind(this),
                    getJSON: this.getJSON.bind(this),
                    getSerializedJSON: this.getSerializedJSON.bind(this),
                    getCanvas: this.getCanvas.bind(this),
                    executeCommands: this.executeCommands.bind(this),
                    isExecutingCommands: this.isExecutingCommands.bind(this),
                    registerOnEvent: this.registerOnEvent.bind(this),
                    unregisterOnEvent: this.unregisterOnEvent.bind(this),
                    raiseEvent: this.handleEvents.bind(this),
                    enableEvent: this.enableEvent.bind(this),
                    disableEvent: this.disableEvent.bind(this),
                    eventCoordinates: this.eventCoordinates.bind(this),
                    addToRegion: this.addToRegion.bind(this)
                }
            }.bind(this)())
        }
        return this._pluginFacade;
    },

    /**
     * Returns JSON of underlying canvas (calls ORYX.Canvas#toJSON()).
     * @return {Object} Returns JSON representation as JSON object.
     */
    getJSON: function () {

    },

    /**
     * Serializes a call to toJSON().
     * @return {String} Returns JSON representation as string.
     */
    getSerializedJSON: function () {
        return Ext.encode(this.getJSON());
    },

    /**
     * Imports shapes in JSON as expected by {@link ORYX.Editor#loadSerialized}
     * @param {Object|String} jsonObject The (serialized) json object to be imported
     * @param {boolean } [noSelectionAfterImport=false] Set to true if no shapes should be selected after import
     * @throws {SyntaxError} If the serialized json object contains syntax errors
     */
    importJSON: function (jsonObject, noSelectionAfterImport) {
        // check, if the imported json model can be loaded in this editor (stencil set has to fit)
    },

    /**
     * Loads serialized model to the oryx.
     * @example
     * editor.loadSerialized({
     *    resourceId: "mymodel1",
     *    childShapes: [
     *       {
     *          stencil:{ id:"Subprocess" },
     *          outgoing:[{resourceId: 'aShape'}],
     *          target: {resourceId: 'aShape'},
     *          bounds:{ lowerRight:{ y:510, x:633 }, upperLeft:{ y:146, x:210 } },
     *          resourceId: "myshape1",
     *          childShapes:[],
     *          properties:{},
     *       }
     *    ],
     *    properties:{
     *       language: "English"
     *    },
     *    stencilset:{
     *       url:"http://localhost:80/oryx/stencilsets/bpmn1.1/bpmn1.1.json"
     *    },
     *    stencil:{
     *       id:"BPMNDiagram"
     *    }
     * });
     * @param {Object} model Description of the model to load.
     * @param {Array} [model.ssextensions] List of stenctil set extensions.
     * @param {String} model.stencilset.url
     * @param {String} model.stencil.id
     * @param {Array} model.childShapes
     * @param {Array} [model.properties]
     * @param {String} model.resourceId
     * @return {ORYX.Core.Shape[]} List of created shapes
     * @methodOf ORYX.Editor.prototype
     */
    loadSerialized: function (model, langs, requestMeta) {

    },

    disableEvent: function (eventType) {
        if (eventType == ORYX.CONFIG.EVENT_KEYDOWN) {
            this._keydownEnabled = false;
        }
        if (eventType == ORYX.CONFIG.EVENT_KEYUP) {
            this._keyupEnabled = false;
        }
        if (this.DOMEventListeners.keys().member(eventType)) {
            var value = this.DOMEventListeners.remove(eventType);
            this.DOMEventListeners['disable_' + eventType] = value;
        }
    },

    enableEvent: function (eventType) {
        if (eventType == ORYX.CONFIG.EVENT_KEYDOWN) {
            this._keydownEnabled = true;
        }

        if (eventType == ORYX.CONFIG.EVENT_KEYUP) {
            this._keyupEnabled = true;
        }

        if (this.DOMEventListeners.keys().member("disable_" + eventType)) {
            var value = this.DOMEventListeners.remove("disable_" + eventType);
            this.DOMEventListeners[eventType] = value;
        }
    },

    /**
     *  Methods for the PluginFacade
     */
    registerOnEvent: function (eventType, callback) {
        if (!(this.DOMEventListeners.keys().member(eventType))) {
            this.DOMEventListeners[eventType] = [];
        }

        this.DOMEventListeners[eventType].push(callback);
    },

    unregisterOnEvent: function (eventType, callback) {
        if (this.DOMEventListeners.keys().member(eventType)) {
            this.DOMEventListeners[eventType] = this.DOMEventListeners[eventType].without(callback);
        } else {
            // Event is not supported
            // TODO: Error Handling
        }
    },

    /**
     * Leitet die Events an die Editor-Spezifischen Event-Methoden weiter
     * @param {Object} event Event , welches gefeuert wurde
     * @param {Object} uiObj Target-UiObj
     */
    handleEvents: function (event, uiObj) {

        ORYX.Log.trace("Dispatching event type %0 on %1", event.type, uiObj);

        switch (event.type) {
            case ORYX.CONFIG.EVENT_MOUSEDOWN:
                // this._handleMouseDown(event, uiObj);
                break;
            case ORYX.CONFIG.EVENT_MOUSEMOVE:
                // this._handleMouseMove(event, uiObj);
                break;
            case ORYX.CONFIG.EVENT_MOUSEUP:
                // this._handleMouseUp(event, uiObj);
                break;
            case ORYX.CONFIG.EVENT_MOUSEOVER:
                // this._handleMouseHover(event, uiObj);
                break;
            case ORYX.CONFIG.EVENT_MOUSEOUT:
                // this._handleMouseOut(event, uiObj);
                break;
        }
        /* Force execution if necessary. Used while handle Layout-Callbacks. */
        if (event.forceExecution) {
            // this._executeEventImmediately({event: event, arg: uiObj});
        } else {
            // this._eventsQueue.push({event: event, arg: uiObj});
        }

        if (!this._queueRunning) {
            // this._executeEvents();
        }

        // TODO: Make this return whether no listener returned false.
        // So that, when one considers bubbling undesireable, it won't happen.
        return false;
    },

    /**
     * Calculates the event coordinates to SVG document coordinates.
     * @param {Event} event
     * @return {SVGPoint} The event coordinates in the SVG document
     */
    eventCoordinates: function (event) {

        // var canvas = this.getCanvas();
        //
        // var svgPoint = canvas.node.ownerSVGElement.createSVGPoint();
        // svgPoint.x = event.clientX;
        // svgPoint.y = event.clientY;
        // var matrix = window.document.getScreenCTM();
        // return svgPoint.matrixTransform(matrix.inverse());
        return;
    },

    offer: function (pluginData) {
        if (!this.pluginsData.member(pluginData)) {
            this.pluginsData.push(pluginData);
        }
    }
};
ORYX.Editor = Clazz.extend(ORYX.Editor);

/**
 * Creates a new ORYX.Editor instance by fetching a model from given url and passing it to the constructur
 * @param {String} modelUrl The JSON URL of a model.
 * @param {Object} config Editor config passed to the constructur, merged with the response of the request to modelUrl
 */
ORYX.Editor.createByUrl = function (modelUrl, config) {
    if (!config) config = {};

    new Ajax.Request(modelUrl, {
        method: 'GET',
        onSuccess: function (transport) {
            var editorConfig = Ext.decode(transport.responseText);
            editorConfig = Ext.applyIf(editorConfig, config);
            new ORYX.Editor(editorConfig);
        }.bind(this)
    });
};

// TODO Implement namespace awareness on attribute level.
/**
 * graft() function
 * Originally by Sean M. Burke from interglacial.com, altered for usage with
 * SVG and namespace (xmlns) support. Be sure you understand xmlns before
 * using this funtion, as it creates all grafted elements in the xmlns
 * provided by you and all element's attribures in default xmlns. If you
 * need to graft elements in a certain xmlns and wish to assign attributes
 * in both that and another xmlns, you will need to do stepwise grafting,
 * adding non-default attributes yourself or you'll have to enhance this
 * function. Latter, I would appreciate: martin�apfelfabrik.de
 * @param {Object} namespace The namespace in which
 *                    elements should be grafted.
 * @param {Object} parent The element that should contain the grafted
 *                    structure after the function returned.
 * @param {Object} t the crafting structure.
 * @param {Object} doc the document in which grafting is performed.
 */
ORYX.Editor.graft = function (namespace, parent, t, doc) {

    doc = (doc || (parent && parent.ownerDocument) || document);
    var e;
    if (t === undefined) {
        throw "Can't graft an undefined value";
    } else if (t.constructor == String) {
        e = doc.createTextNode(t);
    } else {
        for (var i = 0; i < t.length; i++) {
            if (i === 0 && t[i].constructor == String) {
                var snared;
                snared = t[i].match(/^([a-z][a-z0-9]*)\.([^\s\.]+)$/i);
                if (snared) {
                    e = doc.createElementNS(namespace, snared[1]);
                    e.setAttributeNS(null, 'class', snared[2]);
                    continue;
                }
                snared = t[i].match(/^([a-z][a-z0-9]*)$/i);
                if (snared) {
                    e = doc.createElementNS(namespace, snared[1]);  // but no class
                    continue;
                }

                // Otherwise:
                e = doc.createElementNS(namespace, "span");
                e.setAttribute(null, "class", "namelessFromLOL");
            }

            if (t[i] === undefined) {
                throw "Can't graft an undefined value in a list!";
            } else if (t[i].constructor == String || t[i].constructor == Array) {
                this.graft(namespace, e, t[i], doc);
            } else if (t[i].constructor == Number) {
                this.graft(namespace, e, t[i].toString(), doc);
            } else if (t[i].constructor == Object) {
                // hash's properties => element's attributes
                for (var k in t[i]) {
                    e.setAttributeNS(null, k, t[i][k]);
                }
            } else {

            }
        }
    }
    if (parent) {
        parent.appendChild(e);
    } else {

    }
    return e; // return the topmost created node
};

ORYX.Editor.provideId = function () {
    var res = [], hex = '0123456789ABCDEF';

    for (var i = 0; i < 36; i++) res[i] = Math.floor(Math.random() * 0x10);

    res[14] = 4;
    res[19] = (res[19] & 0x3) | 0x8;

    for (var i = 0; i < 36; i++) res[i] = hex[res[i]];

    res[8] = res[13] = res[18] = res[23] = '-';

    return "oryx_" + res.join('');
};

/**
 * When working with Ext, conditionally the window needs to be resized. To do
 * so, use this class method. Resize is deferred until 100ms, and all subsequent
 * resizeBugFix calls are ignored until the initially requested resize is
 * performed.
 */
ORYX.Editor.resizeFix = function () {
    if (!ORYX.Editor._resizeFixTimeout) {
        ORYX.Editor._resizeFixTimeout = window.setTimeout(function () {
            window.resizeBy(1, 1);
            window.resizeBy(-1, -1);
            ORYX.Editor._resizefixTimeout = null;
        }, 100);
    }
};

ORYX.Editor.Cookie = {

    callbacks: [],

    onChange: function (callback, interval) {

        this.callbacks.push(callback);
        this.start(interval)

    },

    start: function (interval) {

        if (this.pe) {
            return;
        }

        var currentString = document.cookie;

        this.pe = new PeriodicalExecuter(function () {

            if (currentString != document.cookie) {
                currentString = document.cookie;
                this.callbacks.each(function (callback) {
                    callback(this.getParams())
                }.bind(this));
            }

        }.bind(this), ( interval || 10000 ) / 1000);
    },

    stop: function () {

        if (this.pe) {
            this.pe.stop();
            this.pe = null;
        }
    },

    getParams: function () {
        var res = {};

        var p = document.cookie;
        p.split("; ").each(function (param) {
            res[param.split("=")[0]] = param.split("=")[1];
        });

        return res;
    },

    toString: function () {
        return document.cookie;
    }
};

/**
 * Workaround for SAFARI/Webkit, because
 * when trying to check SVGSVGElement of instanceof there is
 * raising an error
 *
 */
ORYX.Editor.SVGClassElementsAreAvailable = true;
ORYX.Editor.setMissingClasses = function () {

    try {
        SVGElement;
    } catch (e) {
        ORYX.Editor.SVGClassElementsAreAvailable = false;
        SVGSVGElement = document.createElementNS('http://www.w3.org/2000/svg', 'svg').toString();
        SVGGElement = document.createElementNS('http://www.w3.org/2000/svg', 'g').toString();
        SVGPathElement = document.createElementNS('http://www.w3.org/2000/svg', 'path').toString();
        SVGTextElement = document.createElementNS('http://www.w3.org/2000/svg', 'text').toString();
        //SVGMarkerElement 	= document.createElementNS('http://www.w3.org/2000/svg', 'marker').toString();
        SVGRectElement = document.createElementNS('http://www.w3.org/2000/svg', 'rect').toString();
        SVGImageElement = document.createElementNS('http://www.w3.org/2000/svg', 'image').toString();
        SVGCircleElement = document.createElementNS('http://www.w3.org/2000/svg', 'circle').toString();
        SVGEllipseElement = document.createElementNS('http://www.w3.org/2000/svg', 'ellipse').toString();
        SVGLineElement = document.createElementNS('http://www.w3.org/2000/svg', 'line').toString();
        SVGPolylineElement = document.createElementNS('http://www.w3.org/2000/svg', 'polyline').toString();
        SVGPolygonElement = document.createElementNS('http://www.w3.org/2000/svg', 'polygon').toString();

    }

};

ORYX.Editor.checkClassType = function (classInst, classType) {

    if (ORYX.Editor.SVGClassElementsAreAvailable) {
        return classInst instanceof classType
    } else {
        return classInst == classType
    }
};

ORYX.Editor.makeExtModalWindowKeysave = function (facade) {
    Ext.override(Ext.Window, {
        beforeShow: function () {
            delete this.el.lastXY;
            delete this.el.lastLT;
            if (this.x === undefined || this.y === undefined) {
                var xy = this.el.getAlignToXY(this.container, 'c-c');
                var pos = this.el.translatePoints(xy[0], xy[1]);
                this.x = this.x === undefined ? pos.left : this.x;
                this.y = this.y === undefined ? pos.top : this.y;
            }
            this.el.setLeftTop(this.x, this.y);

            if (this.expandOnShow) {
                this.expand(false);
            }

            if (this.modal) {
                //facade.disableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
                Ext.getBody().addClass("x-body-masked");
                this.mask.setSize(Ext.lib.Dom.getViewWidth(true), Ext.lib.Dom.getViewHeight(true));
                this.mask.show();
            }
        },
        afterHide: function () {
            this.proxy.hide();
            if (this.monitorResize || this.modal || this.constrain || this.constrainHeader) {
                Ext.EventManager.removeResizeListener(this.onWindowResize, this);
            }
            if (this.modal) {
                this.mask.hide();
                //facade.enableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
                Ext.getBody().removeClass("x-body-masked");
            }
            if (this.keyMap) {
                this.keyMap.disable();
            }
            this.fireEvent("hide", this);
        },
        beforeDestroy: function () {
            if (this.modal)
                facade.enableEvent(ORYX.CONFIG.EVENT_KEYDOWN);
            Ext.destroy(
                this.resizer,
                this.dd,
                this.proxy,
                this.mask
            );
            Ext.Window.superclass.beforeDestroy.call(this);
        }
    });
};
