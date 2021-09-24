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

import CONFIG from './config';
import Editor from './editor';
import Log from './logger';
import Utils from './utils';
import BpmnJS from './editor/bpmnio/bpmn-modeler.development';
import Plugins from './plugins/plugins';

/**
 * The EditorApp class represents the BPMN Editor. It calls to a BPMN.io editor internally while provides
 * other UI layout components (east, west, north, south) for property panel, navigation pane, etc.
 * and access to pluggable functions via buttons (called plugins). These editor features will call
 * to the wrapped BPMN.io editor.
 * Currently, the EditorApp can only open one BPMN model after it is initialized. It is not possible to
 * import new BPMN model after it has been created because other features like Simulation which depend on the EditorApp
 * may install listeners to changes on bpmn.io, likely these modules will be broken if the EditorApp imports a new BPMN
 * model after creation.
 * @todo: window.setTimeout here has time sensitivity that should be avoided in the future
 */
export default class EditorApp {
    constructor (config) {
        "use strict";
        this.editor = undefined;

        /*
        Plugin config data read from the plugin configuration file
        [
            {   source: 'source file',
                name: 'plugin name',
                properties: [{group: 'groupName', index: 'groupOrderNumber'}, {...}]
             }
            ...
        ]
        */
        this.availablePlugins = [];

        /*
        Plugin data after instantiating each plugin from its class
        [
            {(Toolbar) object
             type: 'plugin name',
             engage: true
             },

            {(Undo) object
             type: 'plugin name',
             engage: true
             },
            ...
        ]
        */
        this.activatedPlugins = [];

        /*
        Button data registered by a plugin with EditorApp, one plugin can regiter multiple buttons.
        [
            {
                'name': window.Apromore.I18N.View.zoomFitToModel,
                'btnId': 'ap-id-editor-zoomFit-btn',
                'functionality': this.zoomFitToModel.bind(this),
                'group': window.Apromore.I18N.View.group, // this value must be one of the group names under properties in availablePlugins.
                'icon': CONFIG.PATH + "images/ap/zoom-to-fit.svg",
                'description': window.Apromore.I18N.View.zoomFitToModelDesc,
                'index': 4,
                'minShape': 0,
                'maxShape': 0,
                isEnabled: function()
             }
             ...
          ]
         */
        this.buttonsData = [];

        this.layout_regions = undefined;

        this.layout = undefined;

        this.id = config.id;

        if (!this.id) {
            Log.fatal('Missing the container HTML element for the editor');
            return;
        }
        this.fullscreen = config.fullscreen !== false;
        this.useSimulationPanel = config.useSimulationPanel || false;
        this.enabledPlugins = config.enabledPlugins; // undefined means all plugins are enabled

        this.editorCommandStackListeners = [];
    }

    /**
     * Initialize the Editor App
     * @param config: config and model data to be initialized
     * @returns {Promise<void>}
     */
    async init(config) {
        this._createEditor(config.preventFitDelay || false);

        this._generateGUI();

        await this._loadData(config).catch(err => {throw err});
    }

    _initUI() {
        // Fixed the problem that the viewport can not
        // start with collapsed panels correctly
        if (CONFIG.PANEL_RIGHT_COLLAPSED === true) {
            this.layout_regions.east.collapse();
        }
        if (CONFIG.PANEL_LEFT_COLLAPSED === true) {
            this.layout_regions.west.collapse();
        }
    }

    zoomFitToModel() {
        if (this.editor) this.editor.zoomFitToModel();
    }

    /**
     * Generate the whole UI components for the editor
     * It is based on Ext Sencha regions: east, west, south and north
     */
    _generateGUI() {
        "use strict";

        // Defines the layout height if it's NOT fullscreen
        var layoutHeight = CONFIG.WINDOW_HEIGHT;

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
                title: "Simulation parameters",
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
                width: CONFIG.PANEL_LEFT_WIDTH || 10,
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
                    el: this.editor.rootNode
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

        // Set the editor to the center, and refresh the size
        this._getContainer().setAttributeNS(null, 'align', 'left');
        this.editor.rootNode.setAttributeNS(null, 'align', 'left');

    }

    /**
     * Adds a component to the specified UI region on the editor
     *
     * @param {String} region
     * @param {Ext.Component} component
     * @param {String} title, optional
     * @return {Ext.Component} dom reference to the current region or null if specified region is unknown
     */
    addToRegion(region, component, title) {
        if (region.toLowerCase && this.layout_regions[region.toLowerCase()]) {
            var current_region = this.layout_regions[region.toLowerCase()];

            current_region.add(component);

            Log.debug("original dimensions of region %0: %1 x %2", current_region.region, current_region.width, current_region.height);

            // update dimensions of region if required.
            if (!current_region.width && component.initialConfig && component.initialConfig.width) {
                Log.debug("resizing width of region %0: %1", current_region.region, component.initialConfig.width);
                current_region.setWidth(component.initialConfig.width)
            }
            if (component.initialConfig && component.initialConfig.height) {
                Log.debug("resizing height of region %0: %1", current_region.region, component.initialConfig.height);
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

            // if (Ext.isMac) {
            //     this.resizeFix();
            // }

            return current_region;
        }

        return null;
    }

    /**
     *  Instantiate plugin object from plugin class
     */
    _activatePlugins() {
        var me = this;
        var newPlugins = [];
        var facade = this._getPluginFacade();

        // Instantiate plugin class
        // this.pluginData is filled in
        console.log('Plugins', Plugins);
        let plugins = Plugins;

        // Available plugins:
        // [{   source: 'source file',
        //      name: 'plugin name',
        //      properties: [{group: 'groupName', index: 'groupOrderNumber'}, {...}] // this is all groups from the configuration file
        //   }
        //  {...}
        // ]
        this.availablePlugins.each(function (value) {
            Log.debug("Initializing plugin '%0'", value.name);
            try {
                var className = eval('plugins.' + value.name);
                if (className) {
                    var plugin = new className(facade, value);
                    plugin.type = value.name;
                    plugin.engaged = true;
                    newPlugins.push(plugin);

                    if (plugin.editorCommandStackChanged) {
                        me.editorCommandStackListeners.push(plugin);
                    }
                }
            } catch (e) {
                Log.warn("Plugin %0 is not available", value.name);
                Log.error(`EditorApp._activatePlugins's error : ${e.message}`);
            }
        });

        // newPlugins
        /*
        [
            {   (Toolbar) object
                 type: 'plugin name',
                 engage: true
                 },

             {  (Undo) object
                 type: 'plugin name',
                 engage: true
                 },
            ...
        ]
         */
        newPlugins.each(function (value) {
            // For plugins that need to work on other plugins such as the toolbar
            if (value.registryChanged) {
                value.registryChanged(me.buttonsData);
            }
        });

        this.activatedPlugins = newPlugins;

        // Hack for the Scrollbars
        // if (Ext.isMac) {
        //     this.resizeFix();
        // }
    }

    getActivatedPlugins() {
        return this.activatedPlugins;
    }

    _getContainer() {
        return document.getElementById(this.id);
    }

    _createEditor(preventFitDelay) {
        this.editor = new Editor({
            width: CONFIG.CANVAS_WIDTH,
            height: CONFIG.CANVAS_HEIGHT,
            id: Utils.provideId(),
            parentNode: this._getContainer(),
            preventFitDelay: preventFitDelay
        });
    }

    getEastRegion() {
        if (this.layout_regions && this.layout_regions.east) return this.layout_regions.east;
    }

    /**
     * Facade object representing the editor to be used by plugins instead of
     * passing the whole editor object
     */
    _getPluginFacade() {
        if (!(this._pluginFacade)) {
            let me = this;
            this._pluginFacade = (function () {
                return {
                    offer: this.offer.bind(this),
                    getEastRegion: this.getEastRegion.bind(this),
                    useSimulationPanel: this.useSimulationPanel,
                    getXML: this.getXML.bind(this),
                    getSVG: this.getSVG.bind(this),
                    addToRegion: this.addToRegion.bind(this),
                    undo: () => me.editor.undo(),
                    redo: () => me.editor.redo(),
                    zoomIn: () => me.editor.zoomIn(),
                    zoomOut: () => me.editor.zoomOut(),
                    zoomFitToModel: () => me.editor.zoomFitToModel()
                }
            }.bind(this)())
        }
        return this._pluginFacade;
    }

    async getXML() {
        if (!this.editor) return Promise.reject(new Error('The Editor was not created (EditorApp.getXML)'));
        return await this.editor.getXML();
    }

    async getSVG() {
        if (!this.editor) return Promise.reject(new Error('The Editor was not created (EditorApp.getSVG)'));
        return await this.editor.getSVG();
    }

    // Remove this method because it is not able to handle dependencies
    // from the Simulatio module that listens to changes to model elements
    // async importXML(xml, callback) {
    //     await this.editor.importXML(xml, callback);
    // }

    /**
     * A door for plugin to register buttons with the EditorApp
     * buttonData {
        //     'btnId': 'ap-id-editor-export-pdf-btn',
        //     'name': window.Apromore.I18N.File.pdf,
        //     'functionality': this.exportPDF.bind(this),
        //     'group': window.Apromore.I18N.File.group,
        //     'icon': CONFIG.PATH + "images/ap/export-pdf.svg",
        //     'description': window.Apromore.I18N.File.pdfDesc,
        //     'index': 5,
        //     'minShape': 0,
        //     'maxShape': 0
        // }
     * @param buttonData: button data
     */
    offer(buttonData) {
        if (!(this.buttonsData.findIndex(function(plugin) {return plugin.name === buttonData.name;}) >=0)) {
            if (this.enabledPlugins && !this.enabledPlugins.includes(buttonData.name)) {
                buttonData.isEnabled = function(){ return false};
            }
            this.buttonsData.push(buttonData);
        }
    }

    getEditor() {
        return this.editor;
    }

    /**
     * When working with Ext, conditionally the window needs to be resized. To do
     * so, use this class method. Resize is deferred until 100ms, and all subsequent
     * resizeBugFix calls are ignored until the initially requested resize is
     * performed.
     */
    // resizeFix() {
    //     if (!this._resizeFixTimeout) {
    //         window.resizeBy(1, 1);
    //         window.resizeBy(-1, -1);
    //         this._resizefixTimeout = null;
    //     }
    // }

    /**
     * Load the editor and a list of predefined plugins from the server
     * @param config
     * @returns {Promise<void>}
     * @private
     */
    async _loadData(config) {
        await this._loadEditor(config).catch(err => {throw err});
        if(CONFIG.PLUGINS_ENABLED) {
            await this._loadAvailablePluginData().catch(err => Log.warn("Error in loading plugins. Error: " + err.message));
        }
    }

    /**
     * Load the real editor with a process model
     * @param config: the editor config and process model to be loaded
     * @returns {Promise<void>}
     * @private
     */
    async _loadEditor(config) {
        if (!this.editor) return Promise.reject(new Error('The Editor was not created (EditorApp._loadEditor)'));

        let me = this;
        let options = {
          container: '#' + me.editor.rootNode.id,
          langTag: config.langTag
        }
        if (!config.viewOnly) {
          options.keyboard = { bindTo: window };
          options.propertiesPanel = me.useSimulationPanel ? { parent: '#js-properties-panel' } : undefined
        }

        await me.editor.attachEditor(new BpmnJS(options));

        // Wait until the editor is fully loaded to start XML import and then UI init
        if (config && config.xml) {
            await this.editor.importXML(config.xml, me._initUI.bind(me))
                .catch(error => {
                    throw err
                });
            this.editor.addCommandStackChangeListener(this._handleEditorCommandStackChanges);
        }
        else {
            throw new Error('Missing XML for the BPMN model in the editor loading (EditorApp._loadEditor)');
        }

    }

    _handleEditorCommandStackChanges() {
        this.editorCommandStackListeners.forEach(listener =>
            listener.editorCommandStackChanged(this.editor.canUndo(), this.editor.canRedo()))
    }

    /**
     * Load plugins with an Ajax request for the plugin configuration file
     *     Available plugins structure: array of plugin structures
     * [
     * {
     *        name: plugin name,
     *        source: plugin javascript source filename,
     *        properties (both plugin and global properties):
     *        [
     *            {attributeName1 -> attributeValue1, attributeName2 -> attributeValue2,...}
     *            {attributeName1 -> attributeValue1, attributeName2 -> attributeValue2,...}
     *            {attributeName1 -> attributeValue1, attributeName2 -> attributeValue2,...}
     *        ],
     *        requires: namespaces:[list of javascript libraries],
     *        notUsesIn: namespaces:[list of javascript libraries]
     *    }
     * ]
     * @returns {Promise<unknown>}: return a custom Promise to control this async action.
     * @private
     */
    _loadAvailablePluginData() {
        let me = this;
        let source = CONFIG.PLUGINS_CONFIG;
        //TODO: await Ajax response
        Log.debug("Loading plugin configuration from '%0'.", source);
        return new Promise((resolve, reject) => {
            $.ajax({
                url: source,
                type: 'get',
                async: true,
                success: function (result, status, xhr) {
                    try {
                        Log.info("Plugin configuration file loaded.");

                        // get plugins.xml content
                        let resultXml = result;
                        console.log(resultXml);

                        // Read properties tag
                        // <properties>
                        //     <property group="File" index="1" />
                        //     <property group="View" index="2" />
                        // </properties>
                        let properties = [];
                        let preferences = $A(resultXml.getElementsByTagName("properties"));
                        preferences.each(function (p) {
                            let props = $A(p.childNodes); // props = [<property group='File' index='1' />, <property />]
                            props.each(function (prop) {
                                let property = new Hash(); // Hash is provided by Prototype library
                                // get all attributes from the node and set to global properties
                                let attributes = $A(prop.attributes) // attributes = [{nodeName: 'group', nodeValue: 'File'}, {nodeName: 'index', nodeValue: '1'}]
                                attributes.each(function (attr) {
                                    property[attr.nodeName] = attr.nodeValue // each property is a set of key-value pairs: {'group' => 'File', 'index' => '1'}
                                });
                                if (attributes.length > 0) {
                                    properties.push(property) // array [{'group' => 'File', 'index' => '1'}, ...]
                                }
                            });
                        });

                        // Plugin XML:
                        //  <plugins>
                        //      <plugin source="javascript filename.js" name="Javascript class name" />
                        //      <plugin source="javascript filename.js" name="Javascript class name" />
                        //  </plugins>
                        let plugin = resultXml.getElementsByTagName("plugin");
                        $A(plugin).each(function (node) {
                            let pluginData = new Hash();

                            //pluginData: for one plugin
                            //.source: source javascript
                            //.name: name
                            $A(node.attributes).each(function (attr) {
                                pluginData[attr.nodeName] = attr.nodeValue
                            });

                            // ensure there's a name attribute.
                            if (!pluginData['name']) {
                                Log.error("A plugin is not providing a name. Ignoring this plugin.");
                                return;
                            }

                            // ensure there's a source attribute.
                            if (!pluginData['source']) {
                                Log.error("Plugin with name '%0' doesn't provide a source attribute.", pluginData['name']);
                                return;
                            }

                            pluginData['properties'] = properties;

                            // pluginData
                            // {source: 'source file', name: 'plugin name', properties: [{group: 'groupName', index: 'groupOrderNumber'}, {...}]}

                            console.log('pluginData', pluginData);
                            let url = CONFIG.PATH + CONFIG.PLUGINS_FOLDER + pluginData['source'];
                            Log.debug("Requiring '%0'", url);
                            Log.info("Plugin '%0' successfully loaded.", pluginData['name']);
                            me.availablePlugins.push(pluginData);
                        });

                        me._activatePlugins();
                        //editor must be attached after the plugins are loaded
                        //await me._loadEditor(config);

                        resolve();
                    }
                    catch (err) {
                        Log.error(`(EditorApp._loadPluginData). Error message: ${err.message}`);
                        reject(err);
                    }
                },
                error: function (xhr, status, error) {
                    Log.error("Plugin configuration file not available.");
                    //await me._loadEditor(config);
                    reject(new Error(`EditorApp._loadPluginData: an error returned from the Ajax request. Error code: ${status}`));
                }
            });
        })

    }
};
