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

if (!Apromore) {
    var Apromore = {};
}

/**
 * The EditorApp class represents the BPMN Editor. It calls to a BPMN.io editor internally while provides
 * other UI layout components (east, west, north, south) for property panel, navigation pane, etc.
 * and access to pluggable functions via buttons (called plugins). These editor features will call
 * to the wrapped BPMN.io editor.
 * Its behavior is divided into public and private methods (starting with underscore).
 * @todo: the namespace Apromore should be changed to Apromore throughout in one pass
 */
Apromore.EditorApp = {
    construct: function (config) {
        "use strict";
        this.editor = undefined;
        this.availablePlugins = []; // plugin config data read from plugin configuration files
        this.activatedPlugins = []; // available plugin objects created from plugin javascript source files
        this.pluginsData = [];      // plugin dynamic properties provided by a plugin object when it is created
        this.layout_regions = undefined;
        this.layout = undefined;

        this.id = config.id;
        if (!this.id) {
            Apromore.Log.fatal('Missing the container HTML element for the editor');
            return;
        }
        this.fullscreen = config.fullscreen !== false;
        this.useSimulationPanel = config.useSimulationPanel || false;
        this.enabledPlugins = config.enabledPlugins; // undefined means all plugins are enabled

        // CREATES the editor
        this._createEditor();

        // GENERATES the main UI regions
        this._generateGUI();

        // LOAD the plugins
        this._loadPlugins();

        // Attach the editor must be the LAST THING AFTER ALL HAS BEEN LOADED
        this.getEditor().attachEditor(new BpmnJS({
            container: '#' + this.getEditor().rootNode.id,
            keyboard: {
                bindTo: window
            },
            propertiesPanel: this.useSimulationPanel ? {
                parent: '#js-properties-panel'
            } : undefined
        }));

        // Wait until the editor is fully loaded to start XML import and then UI init
        var me = this;
        window.setTimeout(function() {
            if (config && config.xml) {
                me.importXML(config.xml, me._initUI.bind(me));
            }
            else {
                me._initUI();
            }
        }, 100);
    },

    _initUI: function () {
        // Fixed the problem that the viewport can not
        // start with collapsed panels correctly
        if (Apromore.CONFIG.PANEL_RIGHT_COLLAPSED === true) {
            this.layout_regions.east.collapse();
        }
        if (Apromore.CONFIG.PANEL_LEFT_COLLAPSED === true) {
            this.layout_regions.west.collapse();
        }
    },

    zoomFitToModel: function () {
        this.getEditor().zoomFitToModel();
    },

    /**
     * Generate the whole UI components for the editor
     * It is based on Ext Sencha regions: east, west, south and north
     */
    _generateGUI: function () {
        "use strict";

        // Defines the layout height if it's NOT fullscreen
        var layoutHeight = Apromore.CONFIG.WINDOW_HEIGHT;

        // DEFINITION OF THE VIEWPORT AREAS
        this.layout_regions = {

            // DEFINES TOP-AREA
            north: new Ext.Panel({ //TOOO make a composite of the Apromore header and addable elements (for toolbar), second one should contain margins
                region: 'north',
                cls: 'x-panel-editor-north',
                autoEl: 'div',
                border: false
            }),

            // DEFINES RIGHT-AREA
            east: new Ext.Panel({
                region: 'east',
                layout: 'anchor',
                cls: 'x-panel-editor-east',
                collapseTitle: window.Apromore.I18N.View.East,
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
                collapseTitle: window.Apromore.I18N.View.West,
                width: Apromore.CONFIG.PANEL_LEFT_WIDTH || 10,
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
                    el: this.getEditor().rootNode
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
            items: [
                this.layout_regions.north,
                this.layout_regions.east,
                this.layout_regions.south,
                this.layout_regions.west,
                new Ext.Panel({ //combine center and info into one panel
                    layout: "border",
                    region: "center",
                    height: 500,
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
            layout_config.height = this._getContainer().clientHeight; // the panel and the containing div should be of the same height
            this.layout = new Ext.Panel(layout_config)
        }

        if (!this.useSimulationPanel) {
            this.layout_regions.east.hide();
        }

        this.layout_regions.west.hide();
        this.layout_regions.info.hide();
        if (Ext.isIPad && "undefined" != typeof iScroll) {
            this.getEditor().iscroll = new iScroll(this.layout_regions.center.body.dom.firstChild, {
                touchCount: 2
            })
        }

        // Set the editor to the center, and refresh the size
        this._getContainer().setAttributeNS(null, 'align', 'left');
        this.getEditor().rootNode.setAttributeNS(null, 'align', 'left');

    },

    /**
     * Adds a component to the specified UI region on the editor
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

            Apromore.Log.debug("original dimensions of region %0: %1 x %2", current_region.region, current_region.width, current_region.height);

            // update dimensions of region if required.
            if (!current_region.width && component.initialConfig && component.initialConfig.width) {
                Apromore.Log.debug("resizing width of region %0: %1", current_region.region, component.initialConfig.width);
                current_region.setWidth(component.initialConfig.width)
            }
            if (component.initialConfig && component.initialConfig.height) {
                Apromore.Log.debug("resizing height of region %0: %1", current_region.region, component.initialConfig.height);
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

    // Get plugins that have been activated successfully (i.e. those plugin objects)
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

    /**
     *  Make plugin object
     *  Activated plugins: array of plugin objects
     *  [
     *      {
     *          <plugin attributes>,
     *          type: name of the plugin,
     *          engaged: true
     *      }
     *  ]
     */
    _activatePlugins: function () {
        var me = this;
        var newPlugins = [];
        var facade = this._getPluginFacade();

        // Instantiate plugin class
        // this.pluginData is filled in
        this.availablePlugins.each(function (value) {
            Apromore.Log.debug("Initializing plugin '%0'", value.name);
            try {
                var className = eval(value.name);
                if (className) {
                    var plugin = new className(facade, value);
                    plugin.type = value.name;
                    plugin.engaged = true;
                    newPlugins.push(plugin);
                }
            } catch (e) {
                Apromore.Log.warn("Plugin %0 is not available", value.name);
                Apromore.Log.error("Error: " + e.message);
            }
        });

        newPlugins.each(function (value) {
            // For plugins that need to work on other plugins such as the toolbar
            if (value.registryChanged)
                value.registryChanged(me.pluginsData);
        });

        this.activatedPlugins = newPlugins;

        // Hack for the Scrollbars
        if (Ext.isMac) {
            this.resizeFix();
        }
    },

    _getContainer: function () {
        return document.getElementById(this.id);
    },

    _createEditor: function () {
        this.editor = new Apromore.Editor({
            width: Apromore.CONFIG.CANVAS_WIDTH,
            height: Apromore.CONFIG.CANVAS_HEIGHT,
            id: Apromore.Utils.provideId(),
            parentNode: this._getContainer()
        });
    },

    getEditor: function() {
        return this.editor;
    },

    getSimulationDrawer: function() {
        return this.layout_regions.east;
    },

    /**
     * Facade object representing the editor to be used by plugins instead of
     * passing the whole editor object
     */
    _getPluginFacade: function () {
        if (!(this._pluginFacade)) {
            this._pluginFacade = (function () {
                return {
                    getAvailablePlugins: this.getAvailablePlugins.bind(this),
                    offer: this.offer.bind(this),
                    getEditor: this.getEditor.bind(this),
                    getSimulationDrawer: this.getSimulationDrawer.bind(this),
                    useSimulationPanel: this.useSimulationPanel,
                    getXML: this.getXML.bind(this),
                    getSVG: this.getSVG.bind(this),
                    addToRegion: this.addToRegion.bind(this)
                }
            }.bind(this)())
        }
        return this._pluginFacade;
    },

    getXML: function() {
        return this.getEditor().getXML();
    },

    getSVG: function() {
        return this.getEditor().getSVG();
    },

    importXML: function(xml, callback) {
        this.getEditor().importXML(xml, callback);
    },

    offer: function (pluginData) {
        if (!(this.pluginsData.findIndex(function(plugin) {return plugin.name === pluginData.name;}) >=0)) {
            if (this.enabledPlugins && !this.enabledPlugins.includes(pluginData.name)) {
                pluginData.isEnabled = function(){ return false};
            }
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
     * Load a list of predefined plugins from the server
     */
    _loadPlugins: function() {
        if(Apromore.CONFIG.PLUGINS_ENABLED) {
            this._loadPluginData();
            this._activatePlugins();
        }
        else {
            Apromore.Log.warn("Ignoring plugins, loading Core only.");
        }
    },


    // Available plugins structure: array of plugin structures
    // [
    //      {
    //          name: plugin name,
    //          source: plugin javascript source filename,
    //          properties (both plugin and global properties):
    //          [
    //              {attributeName1 -> attributeValue1, attributeName2 -> attributeValue2,...}
    //              {attributeName1 -> attributeValue1, attributeName2 -> attributeValue2,...}
    //              {attributeName1 -> attributeValue1, attributeName2 -> attributeValue2,...}
    //          ],
    //          requires: namespaces:[list of javascript libraries],
    //          notUsesIn: namespaces:[list of javascript libraries]
    //      }
    // ]
    _loadPluginData: function() {
        var me = this;
        var source = Apromore.CONFIG.PLUGINS_CONFIG;

        Apromore.Log.debug("Loading plugin configuration from '%0'.", source);
        new Ajax.Request(source, {
            asynchronous: false,
            method: 'get',
            onSuccess: function(result) {
                Apromore.Log.info("Plugin configuration file loaded.");

                // get plugins.xml content
                var resultXml = result.responseXML;
                console.log('Plugin list:', resultXml);

                // Global properties XML:
                // <properties>
                //      <property attributeName1="attributeValue1" attributeName2="attributeValue2" />
                //      <property attributeName1="attributeValue1" attributeName2="attributeValue2" />
                //      ...
                // </properties>
                var globalProperties = [];
                var preferences = $A(resultXml.getElementsByTagName("properties"));
                preferences.each( function(p) {
                    var props = $A(p.childNodes);
                    props.each( function(prop) {
                        var property = new Hash(); // Hash is provided by Prototype library
                        // get all attributes from the node and set to global properties
                        var attributes = $A(prop.attributes)
                        attributes.each(function(attr){property[attr.nodeName] = attr.nodeValue});
                        if(attributes.length > 0) { globalProperties.push(property) };
                    });
                });

                // Plugin XML:
                //  <plugins>
                //      <plugin source="javascript filename.js" name="Javascript class name" property="" requires="" notUsesIn="" />
                //      <plugin source="javascript filename.js" name="Javascript class name" property="" requires="" notUsesIn="" />
                //  </plugins>
                var plugin = resultXml.getElementsByTagName("plugin");
                $A(plugin).each( function(node) {
                    var pluginData = new Hash();

                    //pluginData: for one plugin
                    //.source: source javascript
                    //.name: name
                    $A(node.attributes).each( function(attr){
                        pluginData[attr.nodeName] = attr.nodeValue});

                    // ensure there's a name attribute.
                    if(!pluginData['name']) {
                        Apromore.Log.error("A plugin is not providing a name. Ignoring this plugin.");
                        return;
                    }

                    // ensure there's a source attribute.
                    if(!pluginData['source']) {
                        Apromore.Log.error("Plugin with name '%0' doesn't provide a source attribute.", pluginData['name']);
                        return;
                    }

                    // Get all plugin properties
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

                    var url = Apromore.PATH + Apromore.CONFIG.PLUGINS_FOLDER + pluginData['source'];
                    Apromore.Log.debug("Requiring '%0'", url);
                    Apromore.Log.info("Plugin '%0' successfully loaded.", pluginData['name']);
                    me.availablePlugins.push(pluginData);
                });

            },
            onFailure: function () {
                Apromore.Log.error("Plugin configuration file not available.");
            }
        });

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

Apromore.EditorApp = Clazz.extend(Apromore.EditorApp);




