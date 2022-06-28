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
                name: 'plugin name'
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
                'icon': CONFIG.PATH + "images/ap/zoom-to-fit.svg",
                'description': window.Apromore.I18N.View.zoomFitToModelDesc,
                'index': 4,
                'groupOrder': the index of the group this button belongs to
                isDisabled: function()
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
        this.processName = config.processName;
        this.useSimulationPanel = config.useSimulationPanel || false;
        this.isPublished = config.isPublished || false;
        this.disabledButtons = config.disabledButtons; // undefined means all plugins are enabled

        this.editorCommandStackListeners = [];
        this.publishStateListeners = [];
    }

    /**
     * Initialize the Editor App
     * @param config: config and model data to be initialized
     * @returns {Promise<void>}
     */
    async init(config) {
        this._createEditor();

        await this._generateGUI();

        await this._loadData(config);

        await this._initUI();

        return this.editor && this.editor.actualEditor
    }

    /**
     * Initialize starting UI
     * Zoom to fit model can only be called after all UI panels have been set up.
     * Use Promise to make this asynchronous code to be synchronous (force the caller to wait)
     * This is because other code (like LogAnimation) can asynchronously respond to viewbox changing events
     * (caused by zoom actions) and they could get null transform matrix if they don't wait for zoomFitToModel to finish.
     *
     * @returns {Promise<void>}
     * @private
     */

    async _initUI() {
        await this._collapsePanels();
        let me = this;
        return new Promise(async (resolve, reject) => {
            await Utils.delay(200);
            me.zoomFitToModel();
            resolve('initUI - zoomFitToModel');
        })
    }

    async _collapsePanels() {
        let me = this;
        return new Promise(async (resolve, reject) => {
            await Utils.delay(300);
            this.useSimulationPanel ? me.layout_regions.east.collapse() : me.layout_regions.east.hide();
            me.layout_regions.west.hide();
            resolve('PanelCollapsedCompleted');
        });

    }

    zoomFitToModel() {
        if (this.editor) this.editor.zoomFitToModel();
    }

    /**
     * Generate the whole UI components for the editor
     * It is based on Ext Sencha regions: east, west, south and north
     */
    async _generateGUI() {
        "use strict";

        // Set the editor to the center, and refresh the size
        this._getContainer().setAttributeNS(null, 'align', 'left');
        this.editor.rootNode.setAttributeNS(null, 'align', 'left');

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
                width: this.useSimulationPanel ? 450 : 0,
                split: false,
                title: "Properties",
                id: 'ap-editor-props-container',
                items: [
                    {   html: '<div id="ap-editor-props-bar">' +
                          '<div id="ap-editor-props-extension">Metadata</div>' +
                          '<div id="ap-editor-props-attachment">Attachments</div>' +
                          '<div id="ap-editor-props-simulation">Simulation</div>' +
                          '</div>',
                        region:'north',
                        border: false,
                        style: "z-index: 300",
                        height: 30
                    },
                    {
                        // layout: "fit",
                        region: 'center',
                        border: false,
                        autoHeight: true,
                        el: document.getElementById("js-properties-panel")
                    }
                ]
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
                collapsible: false,
                titleCollapse: false,
                collapseTitle: window.Apromore.I18N.View.West,
                width: 0,
                autoScroll: false,
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
                    autoWidth: true,
                    width: 'auto',
                    el: this.editor.rootNode
                }
            })
        };

        let me = this;

        if (this.useSimulationPanel) {
            this.layout_regions.center.addListener('resize', function (event) {
            try{
                const listerTypes=me.editorCommandStackListeners;
                if(listerTypes){
                for (const listerType of listerTypes) {
                          if(listerType && listerType.type ==='Toolbar'){
                            return;
                        }
                    }
                }
                }catch(e){}

            me.zoomFitToModel();
            });
        }

        var tabs = ['extension', 'attachment', 'simulation'];
        function selectTab(tab) {
          var container = $('#ap-editor-props-container');
          tabs.forEach((t) => {
            if (t === tab){
              container.addClass(tab);
            } else {
              container.removeClass(t);
            }
          })
        }

        return new Promise(function (resolve, reject) {
            // Config for the Ext.Viewport
            let layout_config = {
                layout: "border",
                items: [
                    me.layout_regions.north,
                    me.layout_regions.east,
                    me.layout_regions.south,
                    me.layout_regions.west,
                    me.layout_regions.center
                ],
                listeners: {
                    render: function() {
                        console.log('UI Viewport finished');
                        resolve('UI Viewport finished');
                        setTimeout(() => {
                            selectTab('extension');
                            $('#ap-editor-props-extension').on('click', () => {
                              selectTab('extension');
                              $('#ap-editor-props-container .bpp-properties-tabs-links > li a[data-tab-target=customTab]')[0].click();
                            })
                            $('#ap-editor-props-attachment').on('click', () => {
                              selectTab('attachment');
                              $('#ap-editor-props-container .bpp-properties-tabs-links > li a[data-tab-target=attachmentTab]')[0].click();
                            })
                            $('#ap-editor-props-simulation').on('click', () => {
                              selectTab('simulation');
                              var tabLink = $('#ap-editor-props-container .bpp-properties-tabs-links > li:not(.bpp-hidden):not(:first-child):not(:nth-child(2)) a');
                              if (tabLink && tabLink[0]) {
                                tabLink[0].click();
                              }
                            })
                        }, 1000);
                    }
                }
            };

            // IF Fullscreen, use a viewport, or use a panel and render it to the given id
            if (me.fullscreen) {
                me.layout = new Ext.Viewport(layout_config);
            } else {
                layout_config.renderTo = me.id;
                //layout_config.height = layoutHeight;
                layout_config.height = me._getContainer().clientHeight; // the panel and the containing div should be of the same height
                me.layout = new Ext.Panel(layout_config);
            }
        });
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

            //Log.debug("original dimensions of region %0: %1 x %2", current_region.region, current_region.width, current_region.height);

            // update dimensions of region if required.
            if (!current_region.width && component.initialConfig && component.initialConfig.width) {
                // Log.debug("resizing width of region %0: %1", current_region.region, component.initialConfig.width);
                // current_region.setWidth(component.initialConfig.width)
            }
            if (component.initialConfig && component.initialConfig.height) {
                // Log.debug("resizing height of region %0: %1", current_region.region, component.initialConfig.height);
                // var current_height = current_region.height || 0;
                // current_region.height = component.initialConfig.height + current_height;
                // current_region.setHeight(component.initialConfig.height + current_height)
            }

            // set title if provided as parameter.
            if (typeof title == "string") {
                current_region.setTitle(title);
            }

            // trigger doLayout() and show the pane
            current_region.ownerCt.doLayout();
            current_region.show();

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
        //      name: 'plugin name'
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

                    if (plugin.onPublishStateUpdate) {
                        me.publishStateListeners.push(plugin);
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
    }

    getActivatedPlugins() {
        return this.activatedPlugins;
    }

    getProcessName() {
        return this.processName || 'untitled';
    }

    _getContainer() {
        return document.getElementById(this.id);
    }

    _createEditor() {
        this.editor = new Editor({
            width: CONFIG.CANVAS_WIDTH,
            height: CONFIG.CANVAS_HEIGHT,
            id: Utils.provideId(),
            parentNode: this._getContainer()
        });
    }

    getEastPanel() {
        if (this.layout_regions && this.layout_regions.east) return this.layout_regions.east;
    }

    getWestPanel() {
        if (this.layout_regions && this.layout_regions.west) return this.layout_regions.west;
    }

    getSouthPanel() {
        if (this.layout_regions && this.layout_regions.south) return this.layout_regions.south;
    }

    getNorthPanel() {
        if (this.layout_regions && this.layout_regions.north) return this.layout_regions.north;
    }

    getCenterPanel() {
        if (this.layout_regions && this.layout_regions.center) return this.layout_regions.center;
    }

    getInfoPanel() {
        if (this.layout_regions && this.layout_regions.info) return this.layout_regions.info;
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
                    app: me,
                    getEastPanel: this.getEastPanel.bind(this),
                    useSimulationPanel: this.useSimulationPanel,
                    isPublished: this.isPublished,
                    getXML: this.getXML.bind(this),
                    getSVG: this.getSVG.bind(this),
                    getSVG2: this.getSVG2.bind(this),
                    addToRegion: this.addToRegion.bind(this),
                    undo: () => me.editor.undo(),
                    redo: () => me.editor.redo(),
                    zoomIn: () => me.editor.zoomIn(),
                    zoomOut: () => me.editor.zoomOut(),
                    zoomFitToModel: () => me.useSimulationPanel ? me.editor.zoomFitToModel() : me.editor.zoomFitOriginal()
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

    async getSVG2() {
        if (!this.editor) return Promise.reject(new Error('The Editor was not created (EditorApp.getSVG)'));
        return await this.editor.getSVG2();
    }

    /**
     * A door for plugin to register buttons with the EditorApp
     * buttonData {
        //     'btnId': 'ap-id-editor-export-pdf-btn',
        //     'name': window.Apromore.I18N.File.pdf,
        //     'functionality': this.exportPDF.bind(this),
        //     'icon': CONFIG.PATH + "images/ap/export-pdf.svg",
        //     'description': window.Apromore.I18N.File.pdfDesc,
        //     'index': 5,
        //     'groupOrder': 0
        // }
     * @param buttonData: button data
     */
    offer(buttonData) {
        //Do not add buttons in the disabledButtons list to the toolbar
        if (!this.disabledButtons || !this.disabledButtons.includes(buttonData.name)) {
            this.buttonsData.push(buttonData);
        }
    }

    getEditor() {
        return this.editor;
    }

    /**
     * Load proces model and a list of predefined plugins from the server
     * @param config
     * @returns {Promise<void>}
     * @private
     */
    async _loadData(config) {
        await this._loadModel(config);
        // Loading plugins must proceed in case of errors after logging the error.
        if (CONFIG.PLUGINS_ENABLED) await this._loadAvailablePluginData().catch(err => Log.warn(err.message));
    }

    /**
     * Create the real editor with a process model
     * @param config: the editor config and process model to be loaded
     * @returns {Promise<void>}
     * @private
     */
    async _loadModel(config) {
        if (!this.editor) throw new Error('The Editor was not created (EditorApp._loadEditor)');

        let me = this;
        let options = {
          container: '#' + me.editor.rootNode.id,
          langTag: config.langTag,
          username: '',
          processName: '',
		  textRenderer: {
		    defaultStyle: {
		      fontSize: 16
		    },
		    externalStyle: {
		      fontSize: 16
		    }
  		}	
        }
        if (!config.viewOnly) {
          options.keyboard = { bindTo: window };
          options.propertiesPanel = me.useSimulationPanel ? { parent: '#js-properties-panel' } : undefined
        }
        options.username = config.username || '';
        options.processName = config.processName || 'untitled';
        await me.editor.attachEditor(new BpmnJS(options));

        if (config && config.xml) {
            await this.editor.importXML(config.xml);
            this.editor.addCommandStackChangeListener(this._handleEditorCommandStackChanges.bind(this));
        }
        else {
            throw new Error('Missing XML for the BPMN model in the editor loading (EditorApp._loadEditor)');
        }

    }

    _handleEditorCommandStackChanges() {
        let me = this;
        this.editorCommandStackListeners.forEach(listener =>
            listener.editorCommandStackChanged(me.editor.canUndo(), me.editor.canRedo()))
    }

    _onPublishStateUpdate(isPublished) {
        this.publishStateListeners.forEach(listener => listener.onPublishStateUpdate(isPublished))
    }

    /**
     * Load plugins with an Ajax request for the plugin configuration file
     *     Available plugins structure: array of plugin structures
     * [
     *      {
     *        name: plugin name,
     *        source: plugin javascript source filename
     *    }
     *    ...
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
                        let resultXml = result;

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

                            // pluginData
                            // {source: 'source file', name: 'plugin name'}

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
