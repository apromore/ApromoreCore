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
 @namespace Global Oryx name space
 @name ORYX
 */
if (!ORYX) {
    var ORYX = {};
}

/**
 * The Editor class.
 */
ORYX.Editor = {
    construct: function (config) {
        "use strict";

        this._canvas = undefined;
        this.zoomLevel = 1.0;
        this.availablePlugins = [];
        this.activatedPlugins = [];
        this.pluginsData = [];
        this.modelMetaData = config;
        this.layout_regions = undefined;
        this.layout = undefined;

        var model = config;
        if (config.model) {
            model = config.model;
        }

        this.id = model.resourceId;
        if (!this.id) {
            this.id = model.id;
            if (!this.id) {
                this.id = ORYX.Utils.provideId();
            }
        }

        var langs = (config.languages || []).sort(function (k, h) {
            return config.position - config.position;
        });

        // Defines if the editor should be fullscreen or not
        this.fullscreen = config.fullscreen !== false;

        // CREATES the canvas
        this._createCanvas(model.stencil ? model.stencil.id : null, model.properties, langs);

        // GENERATES the whole EXT.VIEWPORT
        this._generateGUI();

        // LOAD the plugins
        window.setTimeout(function () {
            this.loadPlugins();
            this.activatePlugins();
        }.bind(this), 100);

        // LOAD the content of the current editor instance
        window.setTimeout(function () {
            // Attach the editor must be the LAST THING AFTER ALL HAS BEEN LOADED
            this.getCanvas().attachEditor(new BpmnJS({
                container: '#' + this.getCanvas().rootNode.id,
                keyboard: {
                    bindTo: window
                },
                propertiesPanel: {
                    parent: '#js-properties-panel'
                }
            }));

            if (config && config.xml) {
                this.importXML(config.xml);
            }

            // Fixed the problem that the viewport can not
            // start with collapsed panels correctly
            if (ORYX.CONFIG.PANEL_RIGHT_COLLAPSED === true) {
                this.layout_regions.east.collapse();
            }
            if (ORYX.CONFIG.PANEL_LEFT_COLLAPSED === true) {
                this.layout_regions.west.collapse();
            }
        }.bind(this), 200);


    },

    zoomFitToModel: function () {
        this.getCanvas().zoomFitToModel();
    },

    /**
     * Generate the whole viewport of the
     * Editor and initialized the Ext-Framework
     */
    _generateGUI: function () {
        "use strict";

        // Defines the layout height if it's NOT fullscreen
        var layoutHeight = ORYX.CONFIG.WINDOW_HEIGHT;

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
                var svg = ORYX.Utils.graft("http://www.w3.org/2000/svg", this.collapsedEl.dom,
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
                collapseTitle: ORYX.I18N.View.East,
                titleCollapse: true,
                border: false,
                cmargins: {left: 0, right: 0},
                floatable: false,
                expandTriggerAll: true,
                collapsible: true,
                width: 450,
                split: true,
                title: "Simulation Parameters",
                items: {
                    layout: "fit",
                    autoHeight: true,
                    el: document.getElementById("js-properties-panel")
                }
            }),

            // DEFINES BOTTOM-AREA
            south: new Ext.Panel({
                region: 'south',
                cls: 'x-panel-editor-south',
                autoEl: 'div',
                border: false
            }),

            //DEFINES LEFT-AREA
            west: new Ext.Panel({
                region: 'west',
                layout: 'anchor',
                autoEl: 'div',
                cls: 'x-panel-editor-west',
                collapsible: true,
                titleCollapse: true,
                collapseTitle: ORYX.I18N.View.West,
                width: ORYX.CONFIG.PANEL_LEFT_WIDTH || 10,
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
                autoScroll: false,
                items: {
                    layout: "fit",
                    autoHeight: true,
                    el: this.getCanvas().rootNode
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
            //layout_config.height = layoutHeight;
            layout_config.height = this.getEditorNode().clientHeight; // the panel and the containing div should be of the same height
            this.layout = new Ext.Panel(layout_config)
        }

        this.layout_regions.west.hide();
        this.layout_regions.info.hide();
        if (Ext.isIPad && "undefined" != typeof iScroll) {
            this.getCanvas().iscroll = new iScroll(this.layout_regions.center.body.dom.firstChild, {
                touchCount: 2
            })
        }

        // Set the editor to the center, and refresh the size
        this.getEditorNode().setAttributeNS(null, 'align', 'left');
        this.getCanvas().rootNode.setAttributeNS(null, 'align', 'left');
        // this.getCanvas().setSize({
        //     width: ORYX.CONFIG.CANVAS_WIDTH,
        //     height: ORYX.CONFIG.CANVAS_HEIGHT
        // });

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
                this.resizeFix();
            }

            return current_region;
        }

        return null;
    },

    getAvailablePlugins: function () {
        var curAvailablePlugins = this.availablePlugins.clone();
        curAvailablePlugins.each(function (plugin) {
            if (this.activatedPlugins.find(function (loadedPlugin) {
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
            var facade = this._getPluginFacade();
            var me = this;
            ORYX.Log.debug("Initializing plugin '%0'", match.name);

            try {

                var className = eval(match.name);
                var newPlugin = new className(facade, match);
                newPlugin.type = match.name;

                // If there is an GUI-Plugin, they get all Plugins-Offer-Meta-Data
                if (newPlugin.registryChanged)
                    newPlugin.registryChanged(me.pluginsData);

                // If there have an onSelection-Method it will pushed to the Editor Event-Handler
                // if (newPlugin.onSelectionChanged)
                //     me.registerOnEvent(ORYX.CONFIG.EVENT_SELECTION_CHANGED, newPlugin.onSelectionChanged.bind(newPlugin));
                this.activatedPlugins.push(newPlugin);
                this.activatedPlugins.each(function (loaded) {
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
            callback(false, match ? "NOTFOUND" : "YETACTIVATED");
            //TODO error handling
        }
    },

    /**
     *  Laden der Plugins
     */
    activatePlugins: function () {

        // if there should be plugins but still are none, try again.
        // TODO this should wait for every plugin respectively.
        /*if (!ORYX.Plugins && ORYX.availablePlugins.length > 0) {
         window.setTimeout(this.loadPlugins.bind(this), 100);
         return;
         }*/

        var me = this;
        var newPlugins = [];
        var facade = this._getPluginFacade();

        this.availablePlugins.each(function (value) {
            ORYX.Log.debug("Initializing plugin '%0'", value.name);
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
                ORYX.Log.error("Error: " + e.message);
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

        this.activatedPlugins = newPlugins;

        // Hack for the Scrollbars
        if (Ext.isMac) {
            ORYX.Editor.resizeFix();
        }
    },

    getEditorNode: function () {
        return document.getElementById(this.id);
    },

    /**
     * Creates the Canvas
     * @param {String} [stencilType] The stencil type used for creating the canvas. If not given, a stencil with myBeRoot = true from current stencil set is taken.
     * @param {Object} [canvasConfig] Any canvas properties (like language).
     */
    _createCanvas: function (stencilType, canvasConfig, lang) {
        this._canvas = new ORYX.Canvas({
            width: ORYX.CONFIG.CANVAS_WIDTH,
            height: ORYX.CONFIG.CANVAS_HEIGHT,
            id: ORYX.Utils.provideId(),
            parentNode: this.getEditorNode(),
            language: lang
        });
    },

    getCanvas: function() {
        return this._canvas;
    },

    getSimulationDrawer: function() {
        return this.layout_regions.east;
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
                    getAvailablePlugins: this.getAvailablePlugins.bind(this),
                    offer: this.offer.bind(this),
                    getStencilSets: function() {return {}},
                    getStencilSetExtensionDefinition: function () {return {}},
                    getRules: function() {return {}},
                    loadStencilSet: function() {},
                    createShape: function() {},
                    deleteShape: function() {},
                    getSelection: function() {},
                    setSelection: function() {},
                    updateSelection: function() {},
                    getCanvas: this.getCanvas.bind(this),
                    getSimulationDrawer: this.getSimulationDrawer.bind(this),
                    importJSON: function() {},
                    importERDF: function() {},
                    getERDF: function() {},
                    getJSON: function() {},
                    getXML: this.getXML.bind(this),
                    getSVG: this.getSVG.bind(this),
                    getSerializedJSON: function() {},
                    executeCommands: function() {},
                    isExecutingCommands: function() {},
                    registerOnEvent: function() {},
                    unregisterOnEvent: function() {},
                    raiseEvent: function() {},
                    enableEvent: function() {},
                    disableEvent: function() {},
                    eventCoordinates: function() {},
                    addToRegion: this.addToRegion.bind(this),
                    getAllLanguages: function() {return {}}
                }
            }.bind(this)())
        }
        return this._pluginFacade;
    },

    getXML: function() {
        return this.getCanvas().getXML();
    },

    getSVG: function() {
        return this.getCanvas().getSVG();
    },

    importXML: function(xml) {
        this.getCanvas().importXML(xml);
    },

    offer: function (pluginData) {
        if (!this.pluginsData.member(pluginData)) {
            this.pluginsData.push(pluginData);
        }
    },

    /**
     * When working with Ext, conditionally the window needs to be resized. To do
     * so, use this class method. Resize is deferred until 100ms, and all subsequent
     * resizeBugFix calls are ignored until the initially requested resize is
     * performed.
     */
    resizeFix: function () {
        if (!this._resizeFixTimeout) {
            this._resizeFixTimeout = window.setTimeout(function () {
                window.resizeBy(1, 1);
                window.resizeBy(-1, -1);
                this._resizefixTimeout = null;
            }, 100);
        }
    },

    /**
     * First bootstrapping layer. The Oryx loading procedure begins. In this
     * step, all preliminaries that are not in the responsibility of Oryx to be
     * met have to be checked here, such as the existance of the prototpe
     * library in the current execution environment. After that, the second
     * bootstrapping layer is being invoked. Failing to ensure that any
     * preliminary condition is not met has to fail with an error.
     */
    load: function() {

        if (ORYX.CONFIG.PREVENT_LOADINGMASK_AT_READY !== true) {
            var waitingpanel = new Ext.Window({renderTo:Ext.getBody(),id:'oryx-loading-panel',bodyStyle:'padding: 8px;background:white',title:ORYX.I18N.Oryx.title,width:'auto',height:'auto',modal:true,resizable:false,closable:false,html:'<span style="font-size:11px;">' + ORYX.I18N.Oryx.pleaseWait + '</span>'})
            waitingpanel.show()
        }

        ORYX.Log.debug("Oryx begins loading procedure.");

        // check for prototype
        if( (typeof Prototype=='undefined') ||
            (typeof Element == 'undefined') ||
            (typeof Element.Methods=='undefined') ||
            parseFloat(Prototype.Version.split(".")[0] + "." +
                Prototype.Version.split(".")[1]) < 1.5)

            throw("Application requires the Prototype JavaScript framework >= 1.5.3");

        ORYX.Log.debug("Prototype > 1.5 found.");

        // continue loading.
        this.loadPlugins();
    },

    /**
     * Load a list of predefined plugins from the server
     */
    loadPlugins: function() {

        // load plugins if enabled.
        if(ORYX.CONFIG.PLUGINS_ENABLED)
            this._loadPlugins()
        else
            ORYX.Log.warn("Ignoring plugins, loading Core only.");
    },

    _loadPlugins: function() {
        var me = this;
        var source = ORYX.CONFIG.PLUGINS_CONFIG;

        ORYX.Log.debug("Loading plugin configuration from '%0'.", source);
        new Ajax.Request(source, {
            asynchronous: false,
            method: 'get',
            onSuccess: function(result) {

                /*
                 * This is the method that is being called when the plugin
                 * configuration was successfully loaded from the server. The
                 * file has to be processed and the contents need to be
                 * considered for further plugin requireation.
                 */

                ORYX.Log.info("Plugin configuration file loaded.");

                // get plugins.xml content
                var resultXml = result.responseXML;
                console.log('Plugin list:', resultXml);

                // TODO: Describe how properties are handled.
                // Get the globale Properties
                var globalProperties = [];
                var preferences = $A(resultXml.getElementsByTagName("properties"));
                preferences.each( function(p) {

                    var props = $A(p.childNodes);
                    props.each( function(prop) {
                        var property = new Hash();

                        // get all attributes from the node and set to global properties
                        var attributes = $A(prop.attributes)
                        attributes.each(function(attr){property[attr.nodeName] = attr.nodeValue});
                        if(attributes.length > 0) { globalProperties.push(property) };
                    });
                });


                // TODO Why are we using XML if we don't respect structure anyway?
                // for each plugin element in the configuration..
                var plugin = resultXml.getElementsByTagName("plugin");
                $A(plugin).each( function(node) {

                    // get all element's attributes.
                    // TODO: What about: var pluginData = $H(node.attributes) !?
                    var pluginData = new Hash();

                    //pluginData: for one plugin
                    //.properties: contain all properties in the plugins.xml
                    //.requires: contains the requires property for the plugin
                    //.source: source javascript
                    //.name: name
                    //.notUseIn:

                    $A(node.attributes).each( function(attr){
                        pluginData[attr.nodeName] = attr.nodeValue});

                    // ensure there's a name attribute.
                    if(!pluginData['name']) {
                        ORYX.Log.error("A plugin is not providing a name. Ingnoring this plugin.");
                        return;
                    }

                    // ensure there's a source attribute.
                    if(!pluginData['source']) {
                        ORYX.Log.error("Plugin with name '%0' doesn't provide a source attribute.", pluginData['name']);
                        return;
                    }

                    // Get all private Properties
                    var propertyNodes = node.getElementsByTagName("property");
                    var properties = [];
                    $A(propertyNodes).each(function(prop) {
                        var property = new Hash();

                        // Get all Attributes from the Node
                        var attributes = $A(prop.attributes)
                        attributes.each(function(attr){property[attr.nodeName] = attr.nodeValue});
                        if(attributes.length > 0) { properties.push(property) };

                    });

                    // Set all Global-Properties to the Properties
                    properties = properties.concat(globalProperties);

                    // Set Properties to Plugin-Data
                    pluginData['properties'] = properties;

                    // Get the RequieredNodes
                    var requireNodes = node.getElementsByTagName("requires");
                    var requires;
                    $A(requireNodes).each(function(req) {
                        var namespace = $A(req.attributes).find(function(attr){ return attr.name == "namespace"})
                        if( namespace && namespace.nodeValue ){
                            if( !requires ){
                                requires = {namespaces:[]}
                            }

                            requires.namespaces.push(namespace.nodeValue)
                        }
                    });

                    // Set Requires to the Plugin-Data, if there is one
                    if( requires ){
                        pluginData['requires'] = requires;
                    }


                    // Get the RequieredNodes
                    var notUsesInNodes = node.getElementsByTagName("notUsesIn");
                    var notUsesIn;
                    $A(notUsesInNodes).each(function(not) {
                        var namespace = $A(not.attributes).find(function(attr){ return attr.name == "namespace"})
                        if( namespace && namespace.nodeValue ){
                            if( !notUsesIn ){
                                notUsesIn = {namespaces:[]}
                            }

                            notUsesIn.namespaces.push(namespace.nodeValue)
                        }
                    });

                    // Set Requires to the Plugin-Data, if there is one
                    if( notUsesIn ){
                        pluginData['notUsesIn'] = notUsesIn;
                    }


                    var url = ORYX.PATH + ORYX.CONFIG.PLUGINS_FOLDER + pluginData['source'];

                    ORYX.Log.debug("Requireing '%0'", url);

                    // Add the Script-Tag to the Site
                    //Kickstart.require(url);

                    ORYX.Log.info("Plugin '%0' successfully loaded.", pluginData['name']);

                    // Add the Plugin-Data to all available Plugins
                    me.availablePlugins.push(pluginData);

                });

            },
            onFailure: me._loadPluginsOnFails
        });

    },

    _loadPluginsOnFails: function(result) {
        ORYX.Log.error("Plugin configuration file not available.");
    },

    toggleFullScreen: function () {
        if (!document.fullscreenElement) {
            document.documentElement.requestFullscreen();
        } else {
            if (document.exitFullscreen) {
                document.exitFullscreen();
            }
        }
    }
};

ORYX.Editor = Clazz.extend(ORYX.Editor);




